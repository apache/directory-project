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
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;
import javax.naming.directory.SearchControls;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.shared.ldap.util.StringTools;

import org.safehaus.triplesec.admin.Constants;
import org.safehaus.triplesec.admin.ConstraintViolationException;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.EntryAlreadyExistsException;
import org.safehaus.triplesec.admin.LocalUser;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.admin.PermissionDeniedException;
import org.safehaus.triplesec.admin.dao.LocalUserDao;


public class LdapLocalUserDao extends AbstractLdapDao implements LocalUserDao, Constants
{
    private static final String[] ATTRIBUTES = { "*", "+" };


    public LdapLocalUserDao( DirContext ctx ) throws DataAccessException
    {
        super( ctx );
    }


    public LocalUser add( String id, String description, String firstName, 
        String lastName, String password, String address1, String address2, 
        String city, String stateProvRegion, String zipPostalCode, 
        String country, String company, String email, boolean disabled ) throws DataAccessException
    {
        StringBuffer buf = new StringBuffer();
        BasicAttributes attrs = new BasicAttributes( OBJECT_CLASS_ID, "top", true );
        attrs.get( OBJECT_CLASS_ID ).add( EXTENSIBLE_OBJECT_OC );
        attrs.get( OBJECT_CLASS_ID ).add( UID_OBJECT_OC );
        attrs.get( OBJECT_CLASS_ID ).add( KRB5KDCENTRY_OC );
        attrs.get( OBJECT_CLASS_ID ).add( KRB5PRINCIPAL_OC );
        attrs.get( OBJECT_CLASS_ID ).add( INET_ORG_PERSON_OC );
        attrs.get( OBJECT_CLASS_ID ).add( PERSON_OC );
        attrs.get( OBJECT_CLASS_ID ).add( ORGANIZATIONAL_PERSON_OC );
        attrs.put( UID_ID, id );
        String cn = buf.append( firstName ).append( " " ).append( lastName ).toString();
        buf.setLength( 0 );
        attrs.put( COMMON_NAME_ID, cn );
        attrs.put( GIVENNAME_ID, firstName );
        attrs.put( SURNAME_ID, lastName );
        attrs.put( PASSWORD_ID, StringTools.getBytesUtf8( password ) );

        if ( email != null )
        {
            attrs.put( EMAIL_ID, email );
        }
        
        if ( description != null )
        {
            attrs.put( DESCRIPTION_ID, description );
        }
        
        if ( address1 != null )
        {
            attrs.put( STREET_ID, address1 );
        }

        if ( address2 != null )
        {
            attrs.put( POSTAL_ADDRESS_ID, address2 );
        }
        
        if ( city != null )
        {
            attrs.put( LOCALITY_NAME_ID, city );
        }
        
        if ( stateProvRegion != null )
        {
            attrs.put( STATE_PROVINCE_ID, stateProvRegion );
        }
        
        if ( zipPostalCode != null )
        {
            attrs.put( ZIP_POSTAL_CODE_ID, zipPostalCode );
        }
        
        if ( country != null )
        {
            attrs.put( COUNTRY_ID, country );
        }
        
        if ( company != null )
        {
            attrs.put( ORGANIZATION_ID, company );
        }
        
        if ( disabled ) 
        {
            attrs.put( KRB5_DISABLED_ID, "TRUE" );
        }
        else
        {
            attrs.put( KRB5_DISABLED_ID, "FALSE" );
        }
        
        // -------------------------------------------------------------------
        // Handle Kerberos Attributes and Key Encryption
        // -------------------------------------------------------------------

        String krb5PrincipalName = buf.append( id ).append( "@" ).append( realm.toUpperCase() ).toString();
        buf.setLength( 0 );
        attrs.put( KRB5PRINCIPAL_NAME_ID, krb5PrincipalName );
        attrs.put( KRB5PRINCIPAL_REALM_ID, realm.toUpperCase() );

        KerberosPrincipal kerberosPrincipal = new KerberosPrincipal( krb5PrincipalName );
        KerberosKey key = new KerberosKey( kerberosPrincipal, password.toCharArray(), "DES" );
        byte[] encodedKey = key.getEncoded();
        attrs.put( KRB5KEY_ID, encodedKey );
        attrs.put( KRB5KEY_VERSION_NUMBER_ID, Integer.toString( key.getVersionNumber() ) );
        attrs.put( KRB5ENCRYPTION_TYPE_ID, Integer.toString( key.getKeyType() ) );
        
        String rdn = getRelativeDn( id );
        try
        {
            ctx.createSubcontext( rdn, attrs );
            return new LocalUser( principalName, new Date( System.currentTimeMillis() ), this, 
                id, description, firstName, lastName, password, address1, address2, city, 
                stateProvRegion, zipPostalCode, country, company, email, disabled );
        }
        catch ( NameAlreadyBoundException e )
        {
            log.error( "Cannot create local user " + rdn, e );
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


    public LocalUser rename( String newId, LocalUser archetype ) throws DataAccessException
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
            String msg = "Rename failed. Another user already exists at " + newRdn + " under " + baseUrl;
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
            String msg = "Rename failed. StringPermission denied.";
            log.error( msg, e );
            throw new PermissionDeniedException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Rename failed. " + oldRdn + " under " + baseUrl + " could not be renamed to " + newRdn;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
        
        // -------------------------------------------------------------------
        // Regenerate principal name and key since key depends on principal name
        // -------------------------------------------------------------------

        StringBuffer buf = new StringBuffer();
        BasicAttributes attrs = new BasicAttributes( true );
        String krb5PrincipalName = buf.append( newId ).append( "@" ).append( realm.toUpperCase() ).toString();
        buf.setLength( 0 );
        attrs.put( KRB5PRINCIPAL_NAME_ID, krb5PrincipalName );
        attrs.put( SAFEHAUS_ID, newId );
        
        KerberosPrincipal kerberosPrincipal = new KerberosPrincipal( krb5PrincipalName );
        KerberosKey key = new KerberosKey( kerberosPrincipal, archetype.getPassword().toCharArray(), "DES" );
        byte[] encodedKey = key.getEncoded();
        attrs.put( KRB5KEY_ID, encodedKey );

        try
        {
            ctx.modifyAttributes( newRdn, DirContext.REPLACE_ATTRIBUTE, attrs );
        }
        catch ( NamingException e )
        {
            String msg = "Rename partially failed. Could not update kerberos key and principal name for " + newRdn;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
        
        return new LocalUser( archetype.getCreatorsName(), archetype.getCreateTimestamp(), 
            principalName, new Date( System.currentTimeMillis() ), this, newId, 
            archetype.getDescription(), archetype.getFirstName(), archetype.getLastName(), archetype.getPassword(), 
            archetype.getAddress1(), archetype.getAddress2(), archetype.getCity(),
            archetype.getStateProvRegion(), archetype.getZipPostalCode(), archetype.getCountry(),
            archetype.getCompany(), archetype.getEmail(), archetype.isDisabled() );
    }


    public LocalUser modify( String creatorsName, Date createTimestamp, String id, String description, 
        String password, String firstName, String lastName, String address1, String address2, String city, 
        String stateProvRegion, String zipPostalCode, String country, String company, String email, 
        boolean disabled, ModificationItem[] mods ) throws DataAccessException
    {
        for ( int ii = 0; ii < mods.length; ii++ )
        {
            if ( mods[ii].getAttribute().getID().equalsIgnoreCase( "userPassword" ) )
            {
                StringBuffer buf = new StringBuffer();
                String krb5PrincipalName = buf.append( id ).append( "@" ).append( realm.toUpperCase() ).toString();
                KerberosPrincipal kerberosPrincipal = new KerberosPrincipal( krb5PrincipalName );
                KerberosKey key = new KerberosKey( kerberosPrincipal, password.toCharArray(), "DES" );
                byte[] encodedKey = key.getEncoded();
                Attribute attr = new BasicAttribute( KRB5KEY_ID, encodedKey );
                ModificationItem item = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr );
                ModificationItem[] temp = mods;
                mods = new ModificationItem[temp.length + 1];
                for ( int jj = 0; jj < temp.length; jj++ )
                {
                    mods[jj] = temp[jj];
                }
                mods[temp.length] = item;
            }
        }
        
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
            String msg = "Modify failed. StringPermission denied to " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new PermissionDeniedException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Could not modify " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        
        return new LocalUser( creatorsName, createTimestamp, this.principalName, 
            new Date( System.currentTimeMillis() ), this, id, description, 
            firstName, lastName, password, address1, address2, city, stateProvRegion, 
            zipPostalCode, country, company, email, disabled );
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
            String msg = "Delete failed. StringPermission denied to delete " + rdn + " under " + baseUrl;
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


    public LocalUser load( String id ) throws DataAccessException
    {
        String rdn = getRelativeDn( id );
        Attributes attrs = null;
        String description = null;
        String password = null;
        String firstName = null;
        String lastName = null;
        String creatorsName = null;
        String modifiersName = null;
        String address1 = null;
        String address2 = null;
        String city = null;
        String stateProvRegion = null;
        String zipPostalCode = null;
        String country = null;
        String company = null;
        String email = null;
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
            lastName = ( String ) attrs.get( SURNAME_ID ).get();
            firstName = LdapUtils.getSingleValued( GIVENNAME_ID, attrs );
            password = LdapUtils.getSingleValued( PASSWORD_ID, attrs );
            disabled = LdapUtils.getBoolean( KRB5_DISABLED_ID, attrs, false );
            
            address1 = LdapUtils.getSingleValued( STREET_ID, attrs );
            address2 = LdapUtils.getSingleValued( POSTAL_ADDRESS_ID, attrs );
            city = LdapUtils.getSingleValued( LOCALITY_NAME_ID, attrs );
            stateProvRegion = LdapUtils.getSingleValued( STATE_PROVINCE_ID, attrs );
            zipPostalCode = LdapUtils.getSingleValued( ZIP_POSTAL_CODE_ID, attrs );
            country = LdapUtils.getSingleValued( COUNTRY_ID, attrs );
            company = LdapUtils.getSingleValued( ORGANIZATION_ID, attrs );
            email = LdapUtils.getSingleValued( EMAIL_ID, attrs );
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
        
        return new LocalUser( creatorsName, createTimestamp, modifiersName, modifyTimestamp, 
            this, id, description, firstName, lastName, password, address1, address2, city, stateProvRegion, 
            zipPostalCode, country, company, email, disabled );
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
                "( & (! (apacheSamType=*) ) (objectClass=person) )", controls ), null );
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
        String firstName = null;
        String lastName = null;
        String password = null;
        String creatorsName = null;
        String modifiersName = null;
        String address1 = null;
        String address2 = null;
        String city = null;
        String stateProvRegion = null;
        String zipPostalCode = null;
        String country = null;
        String company = null;
        String email = null;
        Date createTimestamp = null;
        Date modifyTimestamp = null;
        boolean disabled = false;
        
        try
        {
            uid = ( String ) attrs.get( UID_ID ).get();
            lastName = ( String ) attrs.get( SURNAME_ID ).get();
            firstName = LdapUtils.getSingleValued( GIVENNAME_ID, attrs );
            description = LdapUtils.getSingleValued( DESCRIPTION_ID, attrs );
            password = LdapUtils.getSingleValued( PASSWORD_ID, attrs );
            disabled = LdapUtils.getBoolean( KRB5_DISABLED_ID, attrs, false );
            address1 = LdapUtils.getSingleValued( STREET_ID, attrs );
            address2 = LdapUtils.getSingleValued( POSTAL_ADDRESS_ID, attrs );
            city = LdapUtils.getSingleValued( LOCALITY_NAME_ID, attrs );
            stateProvRegion = LdapUtils.getSingleValued( STATE_PROVINCE_ID, attrs );
            zipPostalCode = LdapUtils.getSingleValued( ZIP_POSTAL_CODE_ID, attrs );
            country = LdapUtils.getSingleValued( COUNTRY_ID, attrs );
            company = LdapUtils.getSingleValued( ORGANIZATION_ID, attrs );
            email = LdapUtils.getSingleValued( EMAIL_ID, attrs );
            
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
        
        return new LocalUser( creatorsName, createTimestamp, modifiersName, modifyTimestamp, 
            this, uid, description, firstName, lastName, password, address1, address2, city, 
            stateProvRegion, zipPostalCode, country, company, email, disabled );
    }
}
