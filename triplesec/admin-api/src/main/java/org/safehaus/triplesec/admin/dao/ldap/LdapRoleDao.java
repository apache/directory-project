/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.triplesec.admin.dao.ldap;


import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.safehaus.triplesec.admin.Constants;
import org.safehaus.triplesec.admin.ConstraintViolationException;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.EntryAlreadyExistsException;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.admin.Role;
import org.safehaus.triplesec.admin.dao.RoleDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapRoleDao implements RoleDao, LdapDao, Constants
{
    private static final Logger log = LoggerFactory.getLogger( LdapRoleDao.class );
    private static final String[] ATTRIBUTES = new String[] {
        ROLE_NAME_ID, DESCRIPTION_ID, GRANTS_ID, CREATORS_NAME_ID, CREATE_TIMESTAMP_ID, 
        MODIFIERS_NAME_ID, MODIFY_TIMESTAMP_ID
    };
    private final DirContext ctx;
    private final String baseUrl;
    private final String principalName;
    
    
    public LdapRoleDao( DirContext ctx ) throws DataAccessException
    {
        this.ctx = ctx;

        String name = null;
        String principal = null;
        try
        {
            name = ctx.getNameInNamespace();
            String principalDn = ( String ) ctx.getEnvironment().get( Context.SECURITY_PRINCIPAL );
            if ( principalDn.equalsIgnoreCase( "uid=admin,ou=system" ) )
            {
                principal = "admin";
            }
            else 
            {
                principal = ( String ) new LdapDN( principalDn ).getRdn().getValue();
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed to get name in namespace for base context.";
            log.error( msg, e );
            throw new DataAccessException( msg );
            
        }
        finally
        {
            baseUrl = name;
            principalName = principal;
        }
    }

    
    public Iterator roleIterator( String appName ) throws DataAccessException
    {
        String base = getRelativeDn( appName );
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, ctx.search( base, 
                "(& (roleName=*) (objectClass=policyRole) )", controls ), appName );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + base + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    public Role load( String appName, String roleName ) throws DataAccessException
    {
        String description = null;
        Set grants = Collections.EMPTY_SET;
        String rdn = getRelativeDn( appName, roleName );
        Attributes attrs = null;
        
        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        
        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
            
            if ( attrs.get( GRANTS_ID ) != null )
            {
                grants = new HashSet();
                for ( NamingEnumeration ii = attrs.get( GRANTS_ID ).getAll(); ii.hasMore(); /**/ )
                {
                    grants.add( ii.next() );
                }
            }
        }
        catch ( NameNotFoundException e )
        {
            String msg = "Could not find " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to lookup " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
        
        return new Role( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, appName, roleName, 
            description, Collections.unmodifiableSet( grants ) );
    }


    public Role add( String appName, String roleName, String description, Set grants )
        throws DataAccessException
    {
        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, POLICY_ROLE_OC, true );
        attrs.put( ROLE_NAME_ID, roleName );
        if ( description != null )
        {
            attrs.put( DESCRIPTION_ID, description );
        }
        if ( ! grants.isEmpty() )
        {
            BasicAttribute attr = new BasicAttribute( GRANTS_ID );
            for ( Iterator ii = grants.iterator(); ii.hasNext(); /**/ )
            {
                attr.add( ii.next() );
            }
            attrs.put( attr );
        }
        
        String rdn = getRelativeDn( appName, roleName );
        try
        {
            ctx.createSubcontext( rdn, attrs );
            return new Role( principalName, new Date( System.currentTimeMillis() ), this, 
                appName, roleName, description, grants );
        }
        catch ( NameAlreadyBoundException e )
        {
            log.error( "Cannot create role " + rdn, e );
            EntryAlreadyExistsException eaee = new EntryAlreadyExistsException();
            eaee.initCause( e );
            throw eaee;
        }
        catch ( NamingException e )
        {
            log.error( "Unexpected failure", e );
            throw new DataAccessException( e.getMessage() );
        }
    }


    public Role rename( String newRoleName, Role role ) throws DataAccessException
    {
        String oldRdn = getRelativeDn( role.getApplicationName(), role.getName() );
        String newRdn = getRelativeDn( role.getApplicationName(), newRoleName );
        
        try
        {
            ctx.rename( oldRdn, newRdn );
        }
        catch ( NameNotFoundException e )
        {
            String msg = "Rename failed. Could not find " + oldRdn + " under " + baseUrl;
            log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        catch ( NameAlreadyBoundException e )
        {
            String msg = "Rename failed. Another role already exists at " + newRdn + " under " + baseUrl;
            log.error( msg, e );
            throw new EntryAlreadyExistsException( msg );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Rename failed. " + oldRdn + " under " + baseUrl + " is required by other entities";
            log.error( msg, e );
            throw new ConstraintViolationException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Rename failed. " + oldRdn + " under " + baseUrl + " could not be renamed to " + newRdn;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
        
        return new Role( role.getCreatorsName(), role.getCreateTimestamp(), principalName, 
            new Date( System.currentTimeMillis() ), this, role.getApplicationName(), newRoleName, 
            role.getDescription(), role.getGrants() );
    }


    public Role modify( String creatorsName, Date createTimestamp, String appName, String roleName, 
        String description, Set grants, ModificationItem[] mods )
        throws DataAccessException
    {
            String rdn = getRelativeDn( appName, roleName );
            
            try
            {
                ctx.modifyAttributes( rdn, mods );
            }
            catch ( SchemaViolationException e )
            {
                String msg = "Could not modify " + rdn + " under " + baseUrl;
                msg += " The modification violates constraints.";
                log.error( msg, e );
                throw new ConstraintViolationException( msg );
            }
            catch ( NameNotFoundException e )
            {
                String msg = "Entry " + rdn + " under " + baseUrl + " does not exist";
                log.error( msg, e );
                throw new NoSuchEntryException( msg );
            }
            catch ( NamingException e )
            {
                String msg = "Could not modify " + rdn + " under " + baseUrl;
                log.error( msg, e );
                throw new NoSuchEntryException( msg );
            }
            
            return new Role( creatorsName, createTimestamp, principalName, new Date( System.currentTimeMillis() ), 
                this, appName, roleName, description, grants );
    }


    public void delete( String appName, String roleName ) throws DataAccessException
    {
        String rdn = getRelativeDn( appName, roleName );

        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + roleName;
            log.error( msg, e );
            throw new ConstraintViolationException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    // -----------------------------------------------------------------------
    // Private utility methods
    // -----------------------------------------------------------------------

    
    private String getRelativeDn( String appName, String roleName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "roleName=" ).append( roleName );
        buf.append( ",ou=Roles,appName=" ).append( appName );
        buf.append( ",ou=Applications" );
        return buf.toString();
    }
    
    
    private String getRelativeDn( String appName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "ou=Roles,appName=" ).append( appName );
        buf.append( ",ou=Applications" );
        return buf.toString();
    }
    
    
    // -----------------------------------------------------------------------
    // LdapDao method implementations
    // -----------------------------------------------------------------------

    
    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String roleName = null;
        String description = null;
        Set grants = Collections.EMPTY_SET;
        
        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        
        try
        {
            roleName = ( String ) attrs.get( ROLE_NAME_ID ).get();
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
            if ( attrs.get( GRANTS_ID ) != null )
            {
                grants = new HashSet();
                for ( NamingEnumeration ii = attrs.get( GRANTS_ID ).getAll(); ii.hasMore(); /**/ )
                {
                    grants.add( ii.next() );
                }
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed to produce object for attributes: " + attrs;
            log.error( msg, e );
        }
        
        return new Role( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, 
            ( String ) extra, roleName, description, Collections.unmodifiableSet( grants ) );
    }


    public void deleteEntry( String rdn )
    {
        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( NamingException e )
        {
            log.error( "Failed to delete " + rdn + " under " + baseUrl, e );
        }
    }
}
