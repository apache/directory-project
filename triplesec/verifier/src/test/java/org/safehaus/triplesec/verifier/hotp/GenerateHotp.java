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
package org.safehaus.triplesec.verifier.hotp;


import java.io.File;
import java.util.*;

import javax.security.auth.kerberos.KerberosPrincipal;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import org.safehaus.triplesec.store.*;
import org.safehaus.triplesec.store.schema.SafehausSchema;
import org.safehaus.profile.ServerProfile;
import org.safehaus.profile.BaseServerProfileModifier;
import org.safehaus.otp.Hotp;
import org.safehaus.otp.Base64;
import org.apache.directory.server.core.schema.bootstrap.*;
import org.apache.directory.server.core.configuration.MutableStartupConfiguration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.Configuration;
import org.apache.directory.shared.ldap.message.LockableAttributesImpl;
import org.apache.directory.shared.ldap.message.LockableAttributeImpl;
import org.apache.directory.server.protocol.shared.store.Krb5KdcEntryFilter;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;


/**
 * Generates the next Hotp value and updates the store.  Running test cases
 * will effect the values stored within the store.  Deleting the store will
 * reinitialize the store.
 *
 * @author <a href="mailto:directory-dev@incubator.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class GenerateHotp
{
    public static final KerberosPrincipal DEFAULT_PRINCIPAL = new KerberosPrincipal( "akarasulu@EXAMPLE.COM" );


    public static void main( String[] args )
    {
        KerberosPrincipal principal = null;

        try
        {
            principal = DEFAULT_PRINCIPAL;

            MutableStartupConfiguration config = new MutableStartupConfiguration();

            MutablePartitionConfiguration partConfig = new MutablePartitionConfiguration();
            partConfig.setName( "example" );

            HashSet indices = new HashSet();
            indices.add( "dc" );
            indices.add( "ou" );
            indices.add( "objectClass" );
            indices.add( "krb5PrincipalName" );
            indices.add( "uid" );
            partConfig.setIndexedAttributes( indices );

            partConfig.setSuffix( "dc=example,dc=com" );

            LockableAttributesImpl attrs = new LockableAttributesImpl();
            LockableAttributeImpl attr = new LockableAttributeImpl( "objectClass" );
            attr.add( "top" );
            attr.add( "domain" );
            attrs.put( attr );
            attrs.put( "dc", "example" );
            partConfig.setContextEntry( attrs );

            Set schemas = new HashSet();
            schemas.add( new SystemSchema() );
            schemas.add( new SafehausSchema() );
            schemas.add( new ApacheSchema() );
            schemas.add( new CoreSchema() );
            schemas.add( new CosineSchema() );
            schemas.add( new InetorgpersonSchema() );
            schemas.add( new Krb5kdcSchema() );
            config.setBootstrapSchemas( schemas );
            config.setContextPartitionConfigurations( Collections.singleton( partConfig ) );

            partConfig.setSuffix( "dc=example,dc=com" );

            Hashtable env = new Hashtable();
            env.put( Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.core.jndi.CoreContextFactory" );
            env.put( Context.PROVIDER_URL, "dc=example,dc=com" );
            env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
            env.put( Context.SECURITY_AUTHENTICATION, "simple" );
            env.put( Context.SECURITY_CREDENTIALS, "secret" );
            env.put( Configuration.JNDI_KEY, config );
            env.put( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
            env.put( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );

            DirContext userContext = new InitialDirContext( env );
            try
            {
                userContext = ( DirContext ) userContext.lookup( "ou=users" );
            }
            catch ( NamingException e )
            {
                Attributes users = new BasicAttributes( "objectClass", "top", true );
                users.get( "objectClass" ).add( "organizationalUnit" );
                attrs.put( "ou", "users" );
                userContext = userContext.createSubcontext( "ou=users", attrs );
            }

            ServerProfileStore store = new DefaultServerProfileStore( userContext );
            store.init();

            List filters = Collections.singletonList( new Krb5KdcEntryFilter() );
            LdifFileLoader loader = new LdifFileLoader( userContext, new File( "safehaus.ldif" ), filters,
                    GenerateHotp.class.getClassLoader() );
            loader.execute();

            if ( args.length > 0 )
            {
                principal = new KerberosPrincipal( args[0] );
            }

            ServerProfile p = store.getProfile( principal );

            if ( p == null )
            {
                System.err.println( "Principal " + principal + " not found!" );

                System.exit( -1 );
            }

            BaseServerProfileModifier modifier = new BaseServerProfileModifier( p );
            System.out.println( "Secret hex = " + getHex( p.getSecret() ) );
            System.out.println( "Secret base64 = " + new String( Base64.encode( p.getSecret() ) ) );
            System.out.println( "Moving factor = " + p.getFactor() );
            String hotp = Hotp.generate( p.getSecret(), p.getFactor(), DefaultHotpSamVerifier.HOTP_SIZE );
            modifier.incrementFactor();
            store.update( principal, modifier.getServerProfile() );
            System.out.println( "The next HOTP value for principal " + principal + " is " + hotp );
        }
        catch ( NamingException e )
        {
            System.err.println( "Failed while accessing or updating principal " + principal + " in store!" );

            System.exit( -3 );
        }
    }


    public static final char[] HEXCHARS = { '0', '1', '2', '3', '4', '5', '6', '7',
                                            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    static String getHex( byte[] bytes )
    {
        StringBuffer buf = new StringBuffer();

        for ( int ii = 0; ii < bytes.length; ii++ )
        {
            buf.append( HEXCHARS[ ( bytes[ii] & 0x70 ) >> 4] );
            buf.append( HEXCHARS[bytes[ii] & 0x0f] );
        }

        return buf.toString();
    }
}
