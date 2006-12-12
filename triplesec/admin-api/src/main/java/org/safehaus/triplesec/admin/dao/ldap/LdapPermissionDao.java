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


import java.util.Date;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
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
import org.safehaus.triplesec.admin.Permission;
import org.safehaus.triplesec.admin.dao.PermissionDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapPermissionDao implements PermissionDao, LdapDao, Constants
{
    public static final String[] ATTRIBUTES = new String[] { 
        DESCRIPTION_ID, PERM_NAME_ID, "creatorsName", "createTimestamp", "modifiersName", "modifyTimestamp" 
    };
    private static final Logger log = LoggerFactory.getLogger( LdapPermissionDao.class );
    private final DirContext ctx;
    private final String baseUrl;
    private final String principalName;
    
    
    public LdapPermissionDao( DirContext ctx ) throws DataAccessException
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
    
    
    // -----------------------------------------------------------------------
    // PermissionDao method implementations
    // -----------------------------------------------------------------------

    
    public Permission add( String appName, String permName, String description ) 
        throws DataAccessException
    {
        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, POLICY_PERMISSION_OC, true );
        attrs.put( PERM_NAME_ID, permName );
        if ( description != null )
        {
            attrs.put( DESCRIPTION_ID, description );
        }
        
        String rdn = getRelativeDn( appName, permName );
        try
        {
            ctx.createSubcontext( rdn, attrs );
            return new Permission( principalName, new Date( System.currentTimeMillis() ), 
                this, appName, permName, description );
        }
        catch ( NameAlreadyBoundException e )
        {
            log.error( "Cannot create permission " + rdn, e );
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
    
    
    public void delete( String appName, String permName ) 
        throws DataAccessException
    {
        String rdn = getRelativeDn( appName, permName );

        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + permName;
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

    
    public Permission modify( String creatorsName, Date createTimestamp, String appName, 
        String permName, String description, ModificationItem[] mods ) throws DataAccessException
    {
        String rdn = getRelativeDn( appName, permName );
        
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
        
        return new Permission( creatorsName, createTimestamp, this.principalName, 
            new Date( System.currentTimeMillis() ), this, appName, permName, description );
    }
    
    
    public Permission rename( String newPermName, Permission perm ) 
        throws DataAccessException
    {
        String oldRdn = getRelativeDn( perm.getApplicationName(), perm.getName() );
        String newRdn = getRelativeDn( perm.getApplicationName(), newPermName );
        
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
            String msg = "Rename failed. Another permission already exists at " + newRdn + " under " + baseUrl;
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
        
        return new Permission( perm.getCreatorsName(), perm.getCreateTimestamp(), principalName, 
            new Date( System.currentTimeMillis() ), 
            this, perm.getApplicationName(), newPermName, perm.getDescription() );
    }
    
    
    public Permission load( String appName, String permName )
        throws DataAccessException
    {
        String description = null;
        String creatorsName = null;
        Date createTimestamp = null;
        String modifiersName = null;
        Date modifyTimestamp = null;
        String rdn = getRelativeDn( appName, permName );
        Attributes attrs = null;
        
        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
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
        
        return new Permission( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, 
            appName, permName, description );
    }
    
    
    public boolean has( String appName, String permName )
        throws DataAccessException
    {
        String rdn = getRelativeDn( appName, permName );
        
        try
        {
            ctx.getAttributes( rdn );
            return true;
        }
        catch ( NameNotFoundException e )
        {
            return false;
        }
        catch ( NamingException e )
        {
            return false;
        }
    }
    
    
    public Iterator permissionNameIterator( String appName ) throws DataAccessException
    {
        String base = getRelativeDn( appName );
        SearchControls controls = new SearchControls();
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, PERM_NAME_ID, ctx.search( base, 
                "(& (permName=*) (objectClass=policyPermission) )", controls ), appName );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + base + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    public Iterator permissionIterator( String appName ) throws DataAccessException
    {
        String base = getRelativeDn( appName );
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, ctx.search( base, 
                "(& (permName=*) (objectClass=policyPermission) )", controls ), appName );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + base + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    // -----------------------------------------------------------------------
    // Private utility methods
    // -----------------------------------------------------------------------

    
    private String getRelativeDn( String appName, String permName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "permName=" ).append( permName );
        buf.append( ",ou=Permissions,appName=" ).append( appName );
        buf.append( ",ou=Applications" );
        return buf.toString();
    }
    
    
    private String getRelativeDn( String appName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "ou=Permissions,appName=" ).append( appName );
        buf.append( ",ou=Applications" );
        return buf.toString();
    }
    
    
    // -----------------------------------------------------------------------
    // LdapDao method implementations
    // -----------------------------------------------------------------------

    
    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String permName = null;
        String description = null;
        String creatorsName = null;
        Date createTimestamp = null;
        String modifiersName = null;
        Date modifyTimestamp = null;
        
        try
        {
            permName = ( String ) attrs.get( PERM_NAME_ID ).get();
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to produce object for attributes: " + attrs;
            log.error( msg, e );
        }
        
        return new Permission( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, 
            ( String ) extra, permName, description );
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
