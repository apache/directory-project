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


import java.io.UnsupportedEncodingException;
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
import org.safehaus.triplesec.admin.Profile;
import org.safehaus.triplesec.admin.PermissionClass;
import org.safehaus.triplesec.admin.dao.ProfileDao;
import org.safehaus.triplesec.admin.dao.PermissionClassDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapProfileDao implements ProfileDao, LdapDao, Constants
{
    private static final Logger log = LoggerFactory.getLogger( LdapProfileDao.class );
    private static final String[] ATTRIBUTES = new String[] {
        PROFILEID_ID, DESCRIPTION_ID, USER_ID, PERM_CLASS_NAME_ID, ROLES_ID, PASSWORD_ID,
        CREATORS_NAME_ID, CREATE_TIMESTAMP_ID, MODIFIERS_NAME_ID, MODIFY_TIMESTAMP_ID
    };
    private final DirContext ctx;
    private final String baseUrl;
    private final String principalName;
    private final PermissionClassDao permissionClassDao;
    
    
    public LdapProfileDao( DirContext ctx, PermissionClassDao permissionClassDao) throws DataAccessException
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
        this.permissionClassDao = permissionClassDao;
    }


    public Iterator profileIterator( String applicationName, String user ) throws DataAccessException
    {
        String base = getRelativeDn( applicationName );
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, ctx.search( base, 
                "(& (profileId=*) (user=" + user + ") (objectClass=policyProfile) )", controls ), applicationName );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + base + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }

    
    public Iterator profileIterator( String appName ) throws DataAccessException
    {
        String base = getRelativeDn( appName );
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, ctx.search( base, 
                "(& (profileId=*) (objectClass=policyProfile) )", controls ), appName );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + base + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }

    
    public Profile load( String appName, String profileId ) throws DataAccessException
    {
        String description;
        String user;
        Set<String> roles;
        Set<PermissionClass> permissionClasses = new HashSet<PermissionClass>();
        String rdn = getRelativeDn( appName, profileId );
        Attributes attrs;
        
        String creatorsName;
        String modifiersName;
        Date createTimestamp;
        Date modifyTimestamp;
        boolean disabled;
        
        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
            user = LdapUtils.getSingleValued( USER_ID, attrs );
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            roles = getMultiValued( ROLES_ID, attrs );
            disabled = LdapUtils.getBoolean( SAFEHAUS_DISABLED_ID, attrs, false );
            
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );

            for (Iterator iterator = permissionClassDao.permissionClassNameIterator(rdn); iterator.hasNext(); ) {
                permissionClasses.add((PermissionClass) iterator.next());
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
        
        return new Profile( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, 
            appName, profileId, user, description, permissionClasses, roles, disabled );
    }


    public Profile add( String appName, String profileId, String user, String description, Set<PermissionClass> permissionClasses,
        Set<String> roles ) throws DataAccessException
    {
        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, POLICY_PROFILE_OC, true );
        attrs.put( PROFILEID_ID, profileId );
        attrs.put( USER_ID, user );
        if ( description != null )
        {
            attrs.put( DESCRIPTION_ID, description );
        }
        addMultiValued( ROLES_ID, attrs, roles );

        String rdn = getRelativeDn( appName, profileId );

        for (PermissionClass permissionClass : permissionClasses) {
            permissionClassDao.add(rdn, permissionClass.getPermissionClassName(), permissionClass.getGrants(), permissionClass.getDenials());
        }

        try
        {
            ctx.createSubcontext( rdn, attrs );
            return new Profile( principalName, new Date( System.currentTimeMillis() ), this, appName, 
                profileId, user, description, permissionClasses, roles );
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


    public Profile rename( String newProfileId, Profile profile ) throws DataAccessException
    {
        String oldRdn = getRelativeDn( profile.getApplicationName(), profile.getId() );
        String newRdn = getRelativeDn( profile.getApplicationName(), newProfileId );
        
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
            String msg = "Rename failed. Another profile already exists at " + newRdn + " under " + baseUrl;
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
        
        return new Profile( profile.getCreatorsName(), profile.getCreateTimestamp(), principalName, 
            new Date( System.currentTimeMillis() ), this, profile.getApplicationName(), newProfileId, 
            profile.getUser(), profile.getDescription(), profile.getPermissionClasses(),
            profile.getRoles(), profile.isDisabled() );
    }


    public Profile modify( String creatorsName, Date createTimestamp, String appName, String profileId, 
        String user, String description, Set<PermissionClass> permissionClasses,
        Set<String> roles, boolean disabled, ModificationItem[] mods ) throws DataAccessException
    {
        String rdn = getRelativeDn( appName, profileId );
        
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
        
        return new Profile( creatorsName, createTimestamp, principalName, new Date( System.currentTimeMillis() ), 
            this, appName, profileId, user, description, permissionClasses, roles, disabled );
    }


    public void delete( String appName, String profileId ) throws DataAccessException
    {
        String rdn = getRelativeDn( appName, profileId );

        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + profileId;
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

    
    private String getRelativeDn( String appName, String profileId )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "profileId=" ).append( profileId );
        buf.append( ",ou=Profiles,appName=" ).append( appName );
        buf.append( ",ou=Applications" );
        return buf.toString();
    }
    
    
    private String getRelativeDn( String appName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "ou=Profiles,appName=" ).append( appName );
        buf.append( ",ou=Applications" );
        return buf.toString();
    }
    
    
    private Set<String> getMultiValued( String id, Attributes attrs ) throws NamingException
    {
        Set<String> values = Collections.EMPTY_SET;
        if ( attrs.get( id ) != null )
        {
            values = new HashSet<String>();
            for ( NamingEnumeration ii = attrs.get( id ).getAll(); ii.hasMore(); /**/ )
            {
                values.add( (String) ii.next() );
            }
            return Collections.unmodifiableSet( values );
        }
        return values;
    }
    
    
    private String getSingleValued( String id, Attributes attrs ) throws NamingException
    {
        String value = null;
        if ( attrs.get( id ) != null )
        {
            Object obj = attrs.get( id ).get();
            if ( obj instanceof String )
            {
                value = ( String ) attrs.get( id ).get();
            }
            else
            {
                try
                {
                    value = new String( ( byte[] ) attrs.get( id ).get(), "UTF-8" );
                }
                catch ( UnsupportedEncodingException e )
                {
                    log.error( "Failed to encode string", e );
                }
            }
        }
        return value;
    }
    

    private void addMultiValued( String id, Attributes attrs, Set<String> values )
    {
        if ( values == null )
        {
            return;
        }
        if ( ! values.isEmpty() )
        {
            BasicAttribute attr = new BasicAttribute( id );
            for (Object value : values) {
                attr.add(value);
            }
            attrs.put( attr );
        }
    }

    
    // -----------------------------------------------------------------------
    // LdapDao method implementations
    // -----------------------------------------------------------------------

    
    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String profileId = null;
        String user = null;
        String description = null;
        Set<String> roles = Collections.EMPTY_SET;

        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        boolean disabled = false;
        
        try
        {
            profileId = ( String ) attrs.get( PROFILEID_ID ).get();
            user =  ( String ) attrs.get( USER_ID ).get();
            description = getSingleValued( DESCRIPTION_ID, attrs );
            roles = getMultiValued( ROLES_ID, attrs );
            disabled = LdapUtils.getBoolean( SAFEHAUS_DISABLED_ID, attrs, false );
            
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
        
        return new Profile( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, 
            ( String ) extra, profileId, user, description, new HashSet<PermissionClass>(), roles, disabled );
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
