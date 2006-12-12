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

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;

import org.safehaus.triplesec.admin.Constants;
import org.safehaus.triplesec.admin.ConstraintViolationException;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.ExternalUser;
import org.safehaus.triplesec.admin.EntryAlreadyExistsException;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.admin.PermissionDeniedException;
import org.safehaus.triplesec.admin.dao.ExternalUserDao;


public class LdapExternalUserDao extends AbstractLdapDao implements ExternalUserDao, Constants
{
    private static final String[] ATTRIBUTES = { "*", "+" };

    
    public LdapExternalUserDao( DirContext ctx ) throws DataAccessException
    {
        super( ctx );
    }


    public ExternalUser add( String id, String description, String referral ) throws DataAccessException
    {
        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, "referral", true );
        attrs.get( OBJECT_CLASS_ID ).add( EXTENSIBLE_OBJECT_OC );
        attrs.get( OBJECT_CLASS_ID ).add( UID_OBJECT_OC );
        attrs.put( REF_ID, referral );
        attrs.put( UID_ID, id );
        
        if ( description != null )
        {
            attrs.put( DESCRIPTION_ID, description );
        }
        
        String rdn = getRelativeDn( id );
        try
        {
            ctx.createSubcontext( rdn, attrs );
            return new ExternalUser( principalName, new Date( System.currentTimeMillis() ), this, 
                id, description, referral );
        }
        catch ( NameAlreadyBoundException e )
        {
            log.error( "Cannot create external user " + rdn, e );
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


    public ExternalUser rename( String newId, ExternalUser archetype ) throws DataAccessException
    {
        String oldRdn = getRelativeDn( archetype.getId() );
        String newRdn = getRelativeDn( newId );
        
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
        catch ( NoPermissionException e )
        {
            String msg = "Rename failed. Permission denied.";
            log.error( msg, e );
            throw new PermissionDeniedException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Rename failed. " + oldRdn + " under " + baseUrl + " could not be renamed to " + newRdn;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
        
        return new ExternalUser( archetype.getCreatorsName(), archetype.getCreateTimestamp(), principalName, 
            new Date( System.currentTimeMillis() ), this, newId, archetype.getDescription(), 
            archetype.getReferral(), archetype.isDisabled() );
    }


    public ExternalUser modify( String creatorsName, Date createTimestamp, String id, String description, 
        String referral, boolean disabled, ModificationItem[] mods ) throws DataAccessException
    {
        String rdn = getRelativeDn( id );
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
        catch ( NoPermissionException e )
        {
            String msg = "Modify failed. Permission denied to " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new PermissionDeniedException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Could not modify " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        
        return new ExternalUser( creatorsName, createTimestamp, this.principalName, 
            new Date( System.currentTimeMillis() ), this, id, description, referral, disabled );
    }


    public void delete( String id ) throws DataAccessException
    {
        String rdn = getRelativeDn( id );

        try
        {
            ctx.destroySubcontext( rdn );
        }
        catch ( SchemaViolationException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            msg += ".  Other entities depend on " + id;
            log.error( msg, e );
            throw new ConstraintViolationException( msg );
        }
        catch ( NoPermissionException e )
        {
            String msg = "Delete failed. Permission denied to delete " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new PermissionDeniedException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Could not delete " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }


    public ExternalUser load( String id ) throws DataAccessException
    {
        String rdn = getRelativeDn( id );
        Attributes attrs = null;
        String description = null;
        String referral = null;
        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        boolean disabled = false;
        
        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
            creatorsName = LdapUtils.getPrincipal( CREATORS_NAME_ID, attrs );
            modifiersName = LdapUtils.getPrincipal( MODIFIERS_NAME_ID, attrs );
            createTimestamp = LdapUtils.getDate( CREATE_TIMESTAMP_ID, attrs );
            modifyTimestamp = LdapUtils.getDate( MODIFY_TIMESTAMP_ID, attrs );
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            referral = ( String ) attrs.get( REF_ID ).get();
            disabled = LdapUtils.getBoolean( KRB5_DISABLED_ID, attrs, false );
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
        
        return new ExternalUser( creatorsName, createTimestamp, modifiersName, modifyTimestamp, 
            this, id, description, referral, disabled );
    }


    public Iterator iterator() throws DataAccessException
    {
        String base = "ou=Users";
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes( ATTRIBUTES );
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        try
        {
            return new JndiIterator( this, ctx.search( base, 
                "(objectClass=referral)", controls ), null );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + base + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }

    
    // -----------------------------------------------------------------------
    // Private Utility Methods
    // -----------------------------------------------------------------------

    
    private String getRelativeDn( String uid )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "uid=" ).append( uid );
        buf.append( ",ou=Users" );
        return buf.toString();
    }


    // -----------------------------------------------------------------------
    // LdapDao method implementations
    // -----------------------------------------------------------------------

    
    public Object getEntryObject( Object extra, Attributes attrs )
    {
        String uid = null;
        String description = null;
        String referral = null;
        String creatorsName = null;
        String modifiersName = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        boolean disabled = false;
        
        try
        {
            uid = ( String ) attrs.get( UID_ID ).get();
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            referral = ( String ) attrs.get( REF_ID ).get();
            disabled = LdapUtils.getBoolean( KRB5_DISABLED_ID, attrs, false );

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
        
        return new ExternalUser( creatorsName, createTimestamp, modifiersName, modifyTimestamp, 
            this, uid, description, referral, disabled );
    }
}
