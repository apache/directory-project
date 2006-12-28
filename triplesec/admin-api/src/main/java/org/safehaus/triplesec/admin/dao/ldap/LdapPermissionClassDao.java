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
import java.util.Set;
import java.util.HashSet;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.safehaus.triplesec.admin.Constants;
import org.safehaus.triplesec.admin.ConstraintViolationException;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.EntryAlreadyExistsException;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.admin.PermissionClass;
import org.safehaus.triplesec.admin.PermissionActions;
import org.safehaus.triplesec.admin.dao.PermissionClassDao;
import org.safehaus.triplesec.admin.dao.PermissionActionsDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapPermissionClassDao implements PermissionClassDao, LdapDao, Constants
{
    public static final String[] ATTRIBUTES = new String[] { 
        PERM_CLASS_NAME_ID, "creatorsName", "createTimestamp", "modifiersName", "modifyTimestamp"
    };
    private static final Logger log = LoggerFactory.getLogger( LdapPermissionClassDao.class );
    private final DirContext ctx;
    private final String baseUrl;
    private final String principalName;
    private final PermissionActionsDao permissionActionsDao;
    
    
    public LdapPermissionClassDao( DirContext ctx, PermissionActionsDao permissionActionsDao) throws DataAccessException
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
        this.permissionActionsDao = permissionActionsDao;
    }
    
    
    // -----------------------------------------------------------------------
    // PermissionClassDao method implementations
    // -----------------------------------------------------------------------

    
    public PermissionClass add( String contextDn, String permClassName, Set<PermissionActions> grants, Set<PermissionActions> denials )
        throws DataAccessException
    {
        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, PERM_CLASS_OC, true );
        attrs.put( PERM_CLASS_NAME_ID, permClassName );

        if (grants == null) {
            grants = new HashSet<PermissionActions>();
        }
        if (denials == null) {
            denials = new HashSet<PermissionActions>();
        }

        String rdn = getRelativeDn( contextDn, permClassName );
        try
        {
            ctx.createSubcontext( rdn, attrs );
            for (PermissionActions permissionActions : grants) {
                permissionActionsDao.add(rdn, true, permissionActions.getPermissionName(), permissionActions.getActions());
            }
            for (PermissionActions permissionActions : denials) {
                permissionActionsDao.add(rdn, false, permissionActions.getPermissionName(), permissionActions.getActions());
            }
            return new PermissionClass( principalName, new Date( System.currentTimeMillis() ), null, null,
                this, permClassName, grants, denials );
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
    
    
    public void delete( String contextDn, String permClassName )
        throws DataAccessException
    {
        String rdn = getRelativeDn( contextDn, permClassName );

        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + permClassName;
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

    //TODO signature wrong and not implemented
    public PermissionClass modify( String creatorsName, Date createTimestamp, String contextDn,
        String permClassName  ) throws DataAccessException
    {
        String rdn = getRelativeDn( contextDn, permClassName );
        
//        try
//        {
//            ctx.modifyAttributes( rdn, mods );
//        }
//        catch ( SchemaViolationException e )
//        {
//            String msg = "Could not modify " + rdn + " under " + baseUrl;
//            msg += " The modification violates constraints.";
//            log.error( msg, e );
//            throw new ConstraintViolationException( msg );
//        }
//        catch ( NameNotFoundException e )
//        {
//            String msg = "Entry " + rdn + " under " + baseUrl + " does not exist";
//            log.error( msg, e );
//            throw new NoSuchEntryException( msg );
//        }
//        catch ( NamingException e )
//        {
//            String msg = "Could not modify " + rdn + " under " + baseUrl;
//            log.error( msg, e );
//            throw new NoSuchEntryException( msg );
//        }
        
        return new PermissionClass( creatorsName, createTimestamp, this.principalName,
            new Date( System.currentTimeMillis() ), this, permClassName, null, null  );
    }
    
    
    public PermissionClass rename( String contextDn, String newPermClassName, PermissionClass permClass )
        throws DataAccessException
    {
        String oldRdn = getRelativeDn( contextDn, permClass.getPermissionClassName() );
        String newRdn = getRelativeDn( contextDn, newPermClassName );
        
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
        
        return new PermissionClass( permClass.getCreatorsName(), permClass.getCreateTimestamp(), principalName,
            new Date( System.currentTimeMillis() ), 
            this, newPermClassName, permClass.getGrants(), permClass.getDenials() );
    }
    
    
    public PermissionClass load( String contextDn, String permClassName )
        throws DataAccessException
    {
        String creatorsName;
        Date createTimestamp;
        String modifiersName;
        Date modifyTimestamp;
        String rdn = getRelativeDn( contextDn, permClassName );
        Attributes attrs;
        Set<PermissionActions> grants = new HashSet<PermissionActions>();
        Set<PermissionActions> denials = new HashSet<PermissionActions>();

        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
            for (Iterator<PermissionActions> grantsIterator = permissionActionsDao.permissionActionsIterator(rdn, true); grantsIterator.hasNext(); ) {
                grants.add(grantsIterator.next());
            }
            for (Iterator<PermissionActions> denialsIterator = permissionActionsDao.permissionActionsIterator(rdn, false); denialsIterator.hasNext(); ) {
                grants.add(denialsIterator.next());
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


        return new PermissionClass( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this,
            permClassName, grants, denials );
    }
    
    
    public boolean has( String appName, String permClassName )
        throws DataAccessException
    {
        String rdn = getRelativeDn( appName, permClassName );
        
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
    
    
    public Iterator permissionClassNameIterator( String contextDn ) throws DataAccessException
    {
        SearchControls controls = new SearchControls();
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, PERM_CLASS_NAME_ID, ctx.search( contextDn,
                "(& (permClassName=*) (objectClass=permClass) )", controls ), contextDn );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + contextDn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }



    // -----------------------------------------------------------------------
    // Private utility methods
    // -----------------------------------------------------------------------

    
    private String getRelativeDn( String contextDn, String permName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "permClassName=" ).append( permName );
        buf.append(",").append( contextDn );
        return buf.toString();
    }
    

    
    // -----------------------------------------------------------------------
    // LdapDao method implementations
    // -----------------------------------------------------------------------

    //TODO load grants, denies.
    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String permName = null;
        String creatorsName = null;
        Date createTimestamp = null;
        String modifiersName = null;
        Date modifyTimestamp = null;
        
        try
        {
            permName = ( String ) attrs.get( PERM_CLASS_NAME_ID ).get();
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
        
        return new PermissionClass( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this,
            permName, null, null );
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
