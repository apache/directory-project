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


import java.util.Iterator;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.safehaus.triplesec.admin.Constants;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.admin.PermissionDeniedException;
import org.safehaus.triplesec.admin.User;
import org.safehaus.triplesec.admin.dao.UserDao;


public class LdapUserDao extends AbstractLdapDao implements UserDao, Constants
{
    private static final String[] ATTRIBUTES = 
        {
            "*", "+"
        };

    private final LdapExternalUserDao externalUserDao;
    private final LdapLocalUserDao localUserDao;
    private final LdapHauskeysUserDao hauskeysUserDao;
    
    
    public LdapUserDao( DirContext ctx, LdapExternalUserDao externalUserDao, LdapLocalUserDao localUserDao, 
        LdapHauskeysUserDao hauskeysUserDao ) throws DataAccessException
    {
        super( ctx );
        this.externalUserDao = externalUserDao;
        this.localUserDao = localUserDao;
        this.hauskeysUserDao = hauskeysUserDao;
    }


    public User load( String id ) throws DataAccessException
    {
        String rdn = getRelativeDn( id );
        Attributes attrs = null;

        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
        }
        catch ( NameNotFoundException e )
        {
            String msg = "Could not find " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new NoSuchEntryException( msg );
        }
        catch ( NoPermissionException e )
        {
            String msg = "User load failed. Permission denied.";
            log.error( msg, e );
            throw new PermissionDeniedException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to lookup " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }

        return ( User ) getEntryObject( null, attrs );
    }


    public boolean hasUser( String id ) throws DataAccessException
    {
        String rdn = getRelativeDn( id );
        Attributes attrs = null;

        try
        {
            attrs = ctx.getAttributes( rdn, ATTRIBUTES );
        }
        catch ( NameNotFoundException e )
        {
            return false;
        }
        catch ( NoPermissionException e )
        {
            String msg = "User lookup failed. Permission denied.";
            log.error( msg, e );
            throw new PermissionDeniedException( msg );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to lookup " + rdn + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }

        return attrs != null;
    }


    public Object getEntryObject( Object extra, Attributes attrs )
    {
        try
        {
            if ( isExternalUser( attrs ) ) 
            {
                return externalUserDao.getEntryObject( extra, attrs );
            }
            else if ( isHauskeysUser( attrs ) )
            {
                return hauskeysUserDao.getEntryObject( extra, attrs );
            }
        }
        catch ( NamingException e )
        {
            log.error( "Failed to determine type of user", e );
        }
        
        return localUserDao.getEntryObject( extra, attrs );
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
                "(| (objectClass=referral) (objectClass=person) )", controls ), null );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to search " + base + " under " + baseUrl;
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }
    
    
    public String getRandomUniqueActivationKey() throws DataAccessException
    {
        String key = null;
        int count = 0;
        boolean isNotUnique = true;
        while( isNotUnique && count < 10 )
        {
            // max length 16, min length 8 bytes
            int length = 8 + RandomUtils.nextInt() % 8;
            key = RandomStringUtils.randomNumeric( length );

            NamingEnumeration list = null;
            try
            {
                list = ctx.search( "ou=users", new BasicAttributes( "safehausActivationKey", key, true ) );
                isNotUnique = list.hasMore();
            }
            catch ( NamingException e )
            {
                throw new DataAccessException( "Encountered failure while searching for activation keys." );
            }
            finally
            {
                if ( list != null ) { try { list.close(); }catch (Exception e){ log.error( "can't close naming enum" ); } };
                count++;
            }
        }

        return key;
    }

    
    // -----------------------------------------------------------------------
    // Private Utility Methods
    // -----------------------------------------------------------------------

    
    private boolean isExternalUser( Attributes attrs ) throws NamingException
    {
        Attribute oc = attrs.get( OBJECT_CLASS_ID );
        
        if ( oc == null )
        {
            return false;
        }
        
        if ( oc.contains( REFERRAL_OC ) )
        {
            return true;
        }
        
        for ( NamingEnumeration ii = oc.getAll(); ii.hasMore(); /**/ )
        {
            String val = ( String ) ii.next();
            if ( val.equalsIgnoreCase( REFERRAL_OC ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    private boolean isHauskeysUser( Attributes attrs ) throws NamingException
    {
        String samType = LdapUtils.getSingleValued( APACHE_SAM_TYPE_ID, attrs );
        
        if ( samType == null )
        {
            return false;
        }
        else if ( Integer.parseInt( samType ) == 7 )
        {
            return true;
        }
        else
        {
            throw new IllegalStateException( "Unrecognized sam type value: " + samType );
        }
    }

    
    private String getRelativeDn( String uid )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "uid=" ).append( uid );
        buf.append( ",ou=Users" );
        return buf.toString();
    }
}
