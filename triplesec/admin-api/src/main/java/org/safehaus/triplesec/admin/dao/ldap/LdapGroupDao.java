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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
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
import org.safehaus.triplesec.admin.Group;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.admin.dao.GroupDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapGroupDao implements GroupDao, LdapDao, Constants
{
    private static final Logger log = LoggerFactory.getLogger( LdapGroupDao.class );
    private static final String[] ATTRIBUTES = {
        COMMON_NAME_ID, CREATORS_NAME_ID, CREATE_TIMESTAMP_ID, MODIFIERS_NAME_ID, MODIFY_TIMESTAMP_ID, UNIQUE_MEMBER_ID
    };
    private final DirContext ctx;
    private final String baseUrl;
    private final String principalName;

    
    public LdapGroupDao( DirContext ctx ) throws DataAccessException
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
    // GroupDao method implementations
    // -----------------------------------------------------------------------

    
    public Group add( String name, Set members ) throws DataAccessException
    {
        if ( members.size() == 0 )
        {
            throw new ConstraintViolationException( "At least one member must be present within a group." );
        }

        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, GROUP_OF_UNIQUE_NAMES_OC, true );
        attrs.put( COMMON_NAME_ID, name );
        attrs.put( convertToLdapDns( members ) );
        String rdn = getRelativeDn( name );
        try
        {
            ctx.createSubcontext( rdn, attrs );
            return new Group( principalName, new Date( System.currentTimeMillis() ), this, name, members );
        }
        catch ( NameAlreadyBoundException e )
        {
            log.error( "Cannot create group " + rdn, e );
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


    public Group rename( String newName, Group archetype ) throws DataAccessException
    {
        String oldRdn = getRelativeDn( archetype.getName() );
        String newRdn = getRelativeDn( newName );
        
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
            String msg = "Rename failed. Another group already exists at " + newRdn + " under " + baseUrl;
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
        
        return new Group( archetype.getCreatorsName(), archetype.getCreateTimestamp(), principalName, 
            new Date( System.currentTimeMillis() ), this, newName, archetype.getMembers() );
    }


    public Group modify( String creatorsName, Date createTimestamp, String name, Set members, ModificationItem[] mods )
        throws DataAccessException
    {
        if ( members.size() == 0 )
        {
            throw new ConstraintViolationException( "At least one member must be present within a group." );
        }

        String rdn = getRelativeDn( name );
        for ( int ii = 0; ii < mods.length; ii++ )
        {
            mods[ii] = convertToLdapDns( mods[ii] );
        }
        
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
        
        return new Group( creatorsName, createTimestamp, this.principalName, 
            new Date( System.currentTimeMillis() ), this, name, members );
    }


    public void delete( String name ) throws DataAccessException
    {
        String rdn = getRelativeDn( name );

        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + name;
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

    
    public Group load( String name ) throws DataAccessException
    {
        String rdn = getRelativeDn( name );
        Set members = null;
        Attributes attrs = null;
        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        
        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
            members = convertDnsToUsers( attrs.get( UNIQUE_MEMBER_ID ) );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
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
        
        return new Group( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, name, members );
    }


    public Iterator iterator() throws DataAccessException
    {
        String base = "ou=Groups";
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, ctx.search( base, 
                "(objectClass=groupOfUniqueNames)", controls ), null );
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

    
    private Set convertDnsToUsers( Attribute attribute ) throws NamingException
    {
        Set members = new HashSet();
        for ( int ii = 0; ii < attribute.size(); ii++ )
        {
            LdapDN userDn = new LdapDN( ( String ) attribute.get( ii ) );
            String member = ( String ) userDn.getRdn().getValue();
            members.add( member );
        }
        return members;
    }


    private String getRelativeDn( String name )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "cn=" ).append( name );
        buf.append( ",ou=Groups" );
        return buf.toString();
    }


    private ModificationItem convertToLdapDns( ModificationItem item )
    {
        Attribute users = item.getAttribute();
        BasicAttribute userDns = new BasicAttribute( users.getID() );
        for ( int ii = 0; ii < users.size(); ii++ )
        {
            StringBuffer buf = new StringBuffer();
            try
            {
                buf.append( "uid=" ).append( ( String ) users.get( ii ) );
            }
            catch ( NamingException e )
            {
                log.error( "Could not access attribute value.", e );
            }
            buf.append( ",ou=Users," ).append( baseUrl );
            userDns.add( buf.toString() );
        }
        
        return new ModificationItem( item.getModificationOp(), userDns );
    }
    
    
    private Attribute convertToLdapDns ( Set members )
    {
        BasicAttribute attr = new BasicAttribute( UNIQUE_MEMBER_ID );
        for ( Iterator ii = members.iterator(); ii.hasNext(); /**/ )
        {
            String userId = ( String ) ii.next();
            StringBuffer buf = new StringBuffer();
            buf.append( "uid=" ).append( userId );
            buf.append( ",ou=Users," ).append( baseUrl );
            attr.add( buf.toString() );
        }
        return attr;
    }

 
    // -----------------------------------------------------------------------
    // LdapDao method implementations
    // -----------------------------------------------------------------------

    
    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String name = null;
        Set members = null;
        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        
        try
        {
            name = ( String ) attrs.get( COMMON_NAME_ID ).get();
            members = convertDnsToUsers( attrs.get( UNIQUE_MEMBER_ID ) );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to produce object for attributes: " + attrs;
            log.error( msg, e );
        }
        
        return new Group( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, name, members );
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
