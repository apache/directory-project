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
package org.safehaus.triplesec.store;


import java.util.Hashtable;

import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.spi.DirStateFactory;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attribute;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosKey;

import org.safehaus.profile.ServerProfile;
import org.apache.directory.server.kerberos.shared.store.KerberosAttribute;
import org.apache.directory.shared.ldap.message.LockableAttributeImpl;


/**
 * A StateFactory for a server profile.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ProfileStateFactory implements DirStateFactory
{
    /** the krb5kdc schema key for a krb5KDCEntry */
    private static final String KEY_ATTR = "krb5Key";

    /** the krb5kdc schema key encryption type for a krb5KDCEntry */
    private static final String TYPE_ATTR = "krb5EncryptionType";

    /** the krb5kdc schema principal name for a krb5KDCEntry */
    private static final String PRINCIPAL_ATTR = "krb5PrincipalName";

    /** the krb5kdc schema key version identifier for a krb5KDCEntry */
    private static final String VERSION_ATTR   = "krb5KeyVersionNumber";


    public Result getStateToBind( Object obj, Name name, Context nameCtx, Hashtable environment, Attributes inAttrs ) throws NamingException
    {
        ServerProfile p = ( ServerProfile ) obj;

        Attributes outAttrs = new BasicAttributes( true );

        if ( inAttrs != null )
        {
            NamingEnumeration list = inAttrs.getIDs();

            while ( list.hasMore() )
            {
                String id = ( String ) list.next();

                outAttrs.put( ( Attribute ) inAttrs.get( id ).clone() );
            }
        }

        // process the objectClass attribute

        Attribute oc = outAttrs.get( "objectClass" );

        if ( oc == null )
        {
            oc = new LockableAttributeImpl( "objectClass" );

            outAttrs.put( oc );
        }

        if ( ! oc.contains( "top" ) )
        {
            oc.add( "top" );
        }

        if ( ! oc.contains( "uidObject" ) )
        {
            oc.add( "uidObject" );

            outAttrs.put( "uid", p.getUserId() );
        }

        if ( ! oc.contains( "extensibleObject" ) )
        {
            oc.add( "extensibleObject" );

            outAttrs.put( "apacheSamType", "7" );
        }

        if ( ! oc.contains( "person" ) )
        {
            oc.add( "person" );

            // @todo look into adding sn and cn to ServerProfiles
            // shoot I think we're going to need to have these properties to
            // enforce person usage - or we can blow chunks ????

            outAttrs.put( "sn", p.getUserId() );

            outAttrs.put( "cn", p.getUserId() );
        }

        if ( ! oc.contains( "organizationalPerson" ) )
        {
            oc.add( "organizationalPerson" );
        }

        if ( ! oc.contains( "inetOrgPerson" ) )
        {
            oc.add( "inetOrgPerson" );
        }

        if ( ! oc.contains( "krb5KDCEntry" ) )
        {
            oc.add( "krb5KDCEntry" );
            String pw = p.getUserId();

            if ( p.getPassword() == null )
            {
                outAttrs.put( "userpassword", p.getUserId() );
            }

            StringBuffer buf = new StringBuffer();
            buf.append( p.getUserId() );
            buf.append( "@" );
            buf.append( p.getRealm() );
            KerberosPrincipal principal = new KerberosPrincipal( buf.toString() );

            KerberosKey key = new KerberosKey( principal, pw.toCharArray(), "DES" );
            outAttrs.put( PRINCIPAL_ATTR, principal.getName() );
            byte[] encodedKey = key.getEncoded();
            outAttrs.put( KEY_ATTR, encodedKey );
            outAttrs.put( VERSION_ATTR, Integer.toString( key.getVersionNumber() ) );
            outAttrs.put( TYPE_ATTR, Integer.toString( key.getKeyType() ) );
        }

        if ( ! oc.contains( "safehausProfile" ) )
        {
            oc.add( "safehausProfile" );
        }

        // process the Profile specific attributes

        outAttrs.put( new LockableAttributeImpl( "safehausUid", p.getUserId() ) );
        outAttrs.put( new LockableAttributeImpl( "safehausRealm", p.getRealm() ) );
        outAttrs.put( new LockableAttributeImpl( "safehausFactor", String.valueOf( p.getFactor() ) ) );
        outAttrs.put( new LockableAttributeImpl( "safehausSecret", p.getSecret() ) );
        outAttrs.put( new LockableAttributeImpl( "safehausLabel", p.getLabel() ) );
        outAttrs.put( new LockableAttributeImpl( "safehausTokenPin", p.getTokenPin() ) );
        outAttrs.put( new LockableAttributeImpl( "safehausNotifyBy", p.getNotifyBy() ) );
        outAttrs.put( new LockableAttributeImpl( KerberosAttribute.ACCOUNT_DISABLED, String.valueOf( p.isDisabled() ).toUpperCase() ) );
        outAttrs.put( new LockableAttributeImpl( "userPassword", p.getPassword() ) );

        if ( p.getActivationKey() != null )
        {
        	outAttrs.put( new LockableAttributeImpl( "safehausActivationKey", p.getActivationKey() ) );
        }
        
        outAttrs.put( new LockableAttributeImpl( "safehausResynchCount", Integer.toString( p.getResynchCount() ) ) );
        outAttrs.put( new LockableAttributeImpl( "safehausFailuresInEpoch", Integer.toString( p.getFailuresInEpoch() ) ) );
        if ( p.getInfo() != null )
        {
            outAttrs.put( new BasicAttribute( "safehausInfo", p.getInfo() ) );
        }

        Result r = new Result( obj, outAttrs );
        return r;
    }


    public Object getStateToBind( Object obj, Name name, Context nameCtx, Hashtable environment ) throws NamingException
    {
        throw new UnsupportedOperationException( "Structural objectClass needed for safehausProfile within additional attributes!" );
    }
}
