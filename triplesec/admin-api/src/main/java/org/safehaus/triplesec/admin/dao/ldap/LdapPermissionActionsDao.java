/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.safehaus.triplesec.admin.PermissionActions;
import org.safehaus.triplesec.admin.dao.PermissionActionsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapPermissionActionsDao implements LdapDao, Constants, PermissionActionsDao {
    private static final Logger log = LoggerFactory.getLogger( LdapPermissionActionsDao.class );
    private static final String[] ATTRIBUTES = new String[] {
        GRANT_ID, DENY_ID, ACTION_ID, CREATORS_NAME_ID, CREATE_TIMESTAMP_ID,
        MODIFIERS_NAME_ID, MODIFY_TIMESTAMP_ID
    };
    private final DirContext ctx;
    private final String baseUrl;
    private final String principalName;


    public LdapPermissionActionsDao( DirContext ctx ) throws DataAccessException
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
            LdapPermissionActionsDao.log.error( msg, e );
            throw new DataAccessException( msg );

        }
        finally
        {
            baseUrl = name;
            principalName = principal;
        }
    }


    public Iterator<PermissionActions> permissionActionsIterator( String contextDn, boolean isGrant ) throws DataAccessException
    {
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( LdapPermissionActionsDao.ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        String query = isGrant? "(& (grant=*) (objectClass=permGrant) )": "(& (deny=*) (objectClass=permDeny) )";
        try
        {
            return new JndiIterator( this, ctx.search( contextDn,
                query, controls ), contextDn );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + contextDn + " under " + baseUrl;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    public PermissionActions load( String contextDn, boolean isGrant, String permName ) throws DataAccessException
    {
        Set<String> actions = new HashSet<String>();
        String rdn = getRelativeDn( contextDn, isGrant, permName );
        Attributes attrs;

        String creatorsName;
        String modifiersName;
        Date createTimestamp;
        Date modifyTimestamp;

        try
        {
            attrs = ctx.getAttributes( rdn, LdapPermissionActionsDao.ATTRIBUTES );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );

            if ( attrs.get( ACTION_ID ) != null )
            {
                for ( NamingEnumeration ii = attrs.get( GRANT_ID ).getAll(); ii.hasMore(); /**/ )
                {
                    actions.add( (String)ii.next() );
                }
            }
        }
        catch ( NameNotFoundException e )
        {
            String msg = "Could not find " + rdn + " under " + baseUrl;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to lookup " + rdn + " under " + baseUrl;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new DataAccessException( msg );
        }

        return new PermissionActions( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this, permName, Collections.unmodifiableSet(actions) );
    }


    public PermissionActions add( String contextDn, boolean isGrant, String permName, Set<String> actions )
        throws DataAccessException
    {
        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, isGrant? PERM_GRANT_OC: PERM_DENY_OC, true );
        attrs.put( isGrant? GRANT_ID: DENY_ID, permName );
        if ( ! actions.isEmpty() )
        {
            BasicAttribute attr = new BasicAttribute( ACTION_ID );
            for (String action : actions) {
                attr.add(action);
            }
            attrs.put( attr );
        }

        String rdn = getRelativeDn( contextDn, isGrant, permName );
        try
        {
            ctx.createSubcontext( rdn, attrs );
            return new PermissionActions( principalName, new Date( System.currentTimeMillis() ), null, null,
                    this, permName, Collections.unmodifiableSet(actions) );
        }
        catch ( NameAlreadyBoundException e )
        {
            LdapPermissionActionsDao.log.error( "Cannot create role " + rdn, e );
            EntryAlreadyExistsException eaee = new EntryAlreadyExistsException();
            eaee.initCause( e );
            throw eaee;
        }
        catch ( NamingException e )
        {
            LdapPermissionActionsDao.log.error( "Unexpected failure", e );
            throw new DataAccessException( e.getMessage() );
        }
    }


    public PermissionActions rename( String contextDn, boolean isGrant, String newPermissionName, PermissionActions permissionActions ) throws DataAccessException
    {
        String oldRdn = getRelativeDn( contextDn, isGrant, permissionActions.getPermissionName() );
        String newRdn = getRelativeDn( contextDn, isGrant, newPermissionName );

        try
        {
            ctx.rename( oldRdn, newRdn );
        }
        catch ( NameNotFoundException e )
        {
            String msg = "Rename failed. Could not find " + oldRdn + " under " + baseUrl;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        catch ( NameAlreadyBoundException e )
        {
            String msg = "Rename failed. Another permissionActions already exists at " + newRdn + " under " + baseUrl;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new EntryAlreadyExistsException( msg );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Rename failed. " + oldRdn + " under " + baseUrl + " is required by other entities";
            LdapPermissionActionsDao.log.error( msg, e );
            throw new ConstraintViolationException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Rename failed. " + oldRdn + " under " + baseUrl + " could not be renamed to " + newRdn;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new DataAccessException( msg );
        }

        return new PermissionActions( permissionActions.getCreatorsName(), permissionActions.getCreateTimestamp(), principalName,
            new Date( System.currentTimeMillis() ), this, newPermissionName,
            permissionActions.getActions() );
    }


    public PermissionActions modify( String creatorsName, Date createTimestamp, String contextDn, boolean isGrant, String permissionName,
        Set<String> actions, ModificationItem[] mods )
        throws DataAccessException
    {
            String rdn = getRelativeDn( contextDn, isGrant, permissionName );

            try
            {
                ctx.modifyAttributes( rdn, mods );
            }
            catch ( SchemaViolationException e )
            {
                String msg = "Could not modify " + rdn + " under " + baseUrl;
                msg += " The modification violates constraints.";
                LdapPermissionActionsDao.log.error( msg, e );
                throw new ConstraintViolationException( msg );
            }
            catch ( NameNotFoundException e )
            {
                String msg = "Entry " + rdn + " under " + baseUrl + " does not exist";
                LdapPermissionActionsDao.log.error( msg, e );
                throw new NoSuchEntryException( msg );
            }
            catch ( NamingException e )
            {
                String msg = "Could not modify " + rdn + " under " + baseUrl;
                LdapPermissionActionsDao.log.error( msg, e );
                throw new NoSuchEntryException( msg );
            }

            return new PermissionActions( creatorsName, createTimestamp, principalName, new Date( System.currentTimeMillis() ),
                this, permissionName, actions );
    }


    public void delete( String contextDn, boolean isGrant,  String permissionName ) throws DataAccessException
    {
        String rdn = getRelativeDn( contextDn, isGrant, permissionName );

        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + permissionName;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new ConstraintViolationException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            LdapPermissionActionsDao.log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    // -----------------------------------------------------------------------
    // Private utility methods
    // -----------------------------------------------------------------------


    private String getRelativeDn( String contextDn, boolean isGrant, String permName )
    {
        StringBuffer buf = new StringBuffer();
        buf.append(isGrant? GRANT_ID: DENY_ID).append( "=" ).append( permName );
        buf.append( "," ).append( contextDn );
        return buf.toString();
    }

    // -----------------------------------------------------------------------
    // LdapDao method implementations
    // -----------------------------------------------------------------------


    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String permissionName = null;
        Set<String> actions = new HashSet<String>();

        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;

        try
        {
            permissionName = ( String ) attrs.get( GRANT_ID ).get();
            if (permissionName == null) {
                permissionName = (String) attrs.get(DENY_ID).get();
            }
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
            if ( attrs.get( ACTION_ID ) != null )
            {
                for ( NamingEnumeration ii = attrs.get( ACTION_ID ).getAll(); ii.hasMore(); /**/ )
                {
                    actions.add( (String) ii.next() );
                }
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed to produce object for attributes: " + attrs;
            LdapPermissionActionsDao.log.error( msg, e );
        }

        return new PermissionActions( creatorsName, createTimestamp, modifiersName, modifyTimestamp, this,
            permissionName, Collections.unmodifiableSet( actions ) );
    }


    public void deleteEntry( String rdn )
    {
        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( NamingException e )
        {
            LdapPermissionActionsDao.log.error( "Failed to delete " + rdn + " under " + baseUrl, e );
        }
    }
}
