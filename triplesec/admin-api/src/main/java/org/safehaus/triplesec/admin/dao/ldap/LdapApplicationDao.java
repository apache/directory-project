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


import org.apache.directory.shared.ldap.name.LdapDN;
import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.Constants;
import org.safehaus.triplesec.admin.ConstraintViolationException;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.EntryAlreadyExistsException;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.admin.dao.ApplicationDao;
import org.safehaus.triplesec.admin.dao.ProfileDao;
import org.safehaus.triplesec.admin.dao.RoleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;


public class LdapApplicationDao implements ApplicationDao, LdapDao, Constants, Serializable
{
    private static final long serialVersionUID = 280058609541343711L;
    private static final String[] ATTRIBUTES = new String[] { 
        DESCRIPTION_ID, APP_NAME_ID, PASSWORD_ID, CREATORS_NAME_ID , MODIFIERS_NAME_ID, 
        CREATE_TIMESTAMP_ID, MODIFY_TIMESTAMP_ID };
    private static final Logger log = LoggerFactory.getLogger( LdapPermissionClassDao.class );
    private final String principalName;
    private final DirContext ctx;
    private final String baseUrl;
    private final RoleDao roleDao;
    private final ProfileDao profileDao;
    
    
    public LdapApplicationDao( DirContext ctx,
        RoleDao roleDao, ProfileDao profileDao ) throws DataAccessException
    {
        this.ctx = ctx;
        this.roleDao = roleDao;
        this.profileDao = profileDao;

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


    public Application add( String appName, String description, String userPassword ) throws DataAccessException
    {
        BasicAttributes attrs = new BasicAttributes( "objectClass", "policyApplication", true );
        attrs.put( APP_NAME_ID, appName );
        if ( description != null )
        {
            attrs.put( "description", description );
        }
        if ( userPassword != null )
        {
            attrs.put( "userPassword", userPassword );
        }
        
        String rdn = getRelativeDn( appName );
        try
        {
            DirContext appCtx = ctx.createSubcontext( rdn, attrs );
            attrs = new BasicAttributes( "objectClass", "organizationalUnit", true );
            attrs.put( "ou", "Permissions" );
            appCtx.createSubcontext( "ou=Permissions", attrs );
            attrs = new BasicAttributes( "objectClass", "organizationalUnit", true );
            attrs.put( "ou", "Roles" );
            appCtx.createSubcontext( "ou=Roles", attrs );
            attrs = new BasicAttributes( "objectClass", "organizationalUnit", true );
            attrs.put( "ou", "Profiles" );
            appCtx.createSubcontext( "ou=Profiles", attrs );
            return new Application( principalName, new Date( System.currentTimeMillis() ), 
                this, appName, description, userPassword, 
                roleDao, profileDao );
        }
        catch ( NameAlreadyBoundException e )
        {
            log.error( "Cannot create application " + rdn, e );
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


    public Application modify( String appName, ModificationItem[] mods ) throws DataAccessException
    {
        String rdn = getRelativeDn( appName );
        
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
        
        return load( appName );
    }


    public Application load( String appName ) throws DataAccessException
    {
        String description;
        String userPassword;
        String creatorsName;
        Date createTimestamp;
        String modifiersName;
        Date modifyTimestamp;
        String rdn = getRelativeDn( appName );
        Attributes attrs;
        
        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
            description = LdapUtils.getSingleValued( "description", attrs );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
            userPassword = LdapUtils.getSingleValued( "userPassword", attrs );
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
        
        return new Application( creatorsName, createTimestamp, modifiersName, modifyTimestamp, 
            this, appName, description, userPassword, roleDao, profileDao );
    }


    public boolean has( String appName ) throws DataAccessException
    {
        String rdn = getRelativeDn( appName );
        Attributes attrs;
        
        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
        }
        catch ( NameNotFoundException e )
        {
            return false;
        }
        catch ( NamingException e )
        {
            String msg = "Failed to lookup " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }

        return attrs != null;
    }


    public Iterator applicationIterator() throws DataAccessException
    {
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, ctx.search( "ou=applications", 
                "(& (appName=*) (objectClass=policyApplication) )", controls ), null );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search ou=applications under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    public void delete( Application application ) throws DataAccessException
    {
        String rdn = getRelativeDn( application.getName() );

        try
        {
            DirContext appCtx = ( DirContext ) ctx.lookup( rdn );
            appCtx.destroySubcontext( "ou=Permissions" );
            appCtx.destroySubcontext( "ou=Roles" );
            appCtx.destroySubcontext( "ou=Profiles" );
            ctx.destroySubcontext( rdn );
        }
        catch ( ContextNotEmptyException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities exist below " + application.getName();
            msg += ".  Delete all permissions, roles and profiles before deleting the app.";
            log.error( msg, e );
            throw new ConstraintViolationException( msg );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + application.getName();
            log.error( msg, e );
            throw new ConstraintViolationException( msg );
        }
        catch ( NameNotFoundException e )
        {
            String msg = rdn + " under " + baseUrl + " does not exist!";
            log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    public Application rename( Application app, String newName ) throws DataAccessException
    {
        String oldRdn = getRelativeDn( app.getName() );
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
        
        return new Application( app.getCreatorsName(), app.getCreateTimestamp(), app.getModifiersName(), 
            app.getModifyTimestamp(), this, newName, app.getDescription(), app.getPassword(),
                roleDao, profileDao );
    }
    
    
    private String getRelativeDn( String appName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "appName=" ).append( appName );
        buf.append( ",ou=applications" );
        return buf.toString();
    }


    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String appName = null;
        String description = null;
        String userPassword = null;
        String creatorsName = null;
        Date createTimestamp = null;
        String modifiersName = null;
        Date modifyTimestamp = null;
        
        try
        {
            appName = ( String ) attrs.get( APP_NAME_ID ).get();
            description = LdapUtils.getSingleValued( "description", attrs );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
            userPassword = LdapUtils.getSingleValued( "userPassword", attrs );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to produce object for attributes: " + attrs;
            log.error( msg, e );
        }
        
        return new Application( creatorsName, createTimestamp, modifiersName, modifyTimestamp, 
            this, appName, description, userPassword,
                roleDao, profileDao );
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
