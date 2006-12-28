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


import java.io.IOException;
import java.io.File;
import java.util.*;

import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.kerberos.shared.crypto.encryption.EncryptionEngine;
import org.apache.directory.server.kerberos.shared.crypto.encryption.EncryptionEngineFactory;
import org.apache.directory.server.kerberos.shared.crypto.encryption.EncryptionType;
import org.apache.directory.server.kerberos.shared.exceptions.KerberosException;
import org.apache.directory.server.kerberos.shared.io.encoder.EncryptedDataEncoder;
import org.apache.directory.server.kerberos.shared.io.encoder.EncryptedTimestampEncoder;
import org.apache.directory.server.kerberos.shared.messages.value.EncryptedData;
import org.apache.directory.server.kerberos.shared.messages.value.EncryptedTimeStamp;
import org.apache.directory.server.kerberos.shared.messages.value.EncryptedTimeStampModifier;
import org.apache.directory.server.kerberos.shared.messages.value.EncryptionKey;
import org.apache.directory.server.kerberos.shared.messages.value.KerberosTime;
import org.apache.directory.server.kerberos.sam.SamException;
import org.apache.directory.server.kerberos.sam.TimestampChecker;
import org.apache.directory.shared.ldap.message.LockableAttributeImpl;
import org.apache.directory.shared.ldap.message.LockableAttributesImpl;
import org.apache.directory.server.core.configuration.Configuration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.MutableStartupConfiguration;
import org.apache.directory.server.core.configuration.ShutdownConfiguration;
import org.apache.directory.server.core.schema.bootstrap.ApacheSchema;
import org.apache.directory.server.core.schema.bootstrap.CoreSchema;
import org.apache.directory.server.core.schema.bootstrap.CosineSchema;
import org.apache.directory.server.core.schema.bootstrap.InetorgpersonSchema;
import org.apache.directory.server.core.schema.bootstrap.Krb5kdcSchema;
import org.apache.directory.server.core.schema.bootstrap.SystemSchema;
import org.apache.directory.server.protocol.shared.store.Krb5KdcEntryFilter;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.safehaus.otp.Hotp;
import org.safehaus.otp.HotpErrorConstants;
import org.safehaus.otp.ResynchParameters;
import org.safehaus.profile.ServerProfile;
import org.safehaus.triplesec.store.DefaultServerProfileStore;
import org.safehaus.triplesec.store.ServerProfileStore;
import org.safehaus.triplesec.store.ProfileStateFactory;
import org.safehaus.triplesec.store.ProfileObjectFactory;
import org.safehaus.triplesec.store.schema.SafehausSchema;


/**
 * Test cases for the HOTP SAM verifier class.
 *
 * @version $Rev$
 */
public class HotpSamVerifierITest extends TestCase
{
    DirContext userContext;
    DefaultServerProfileStore store;

    /**
     * Creates the hotp verifier test class.
     */
    public HotpSamVerifierITest()
    {
        super();
    }


    protected void setUp() throws Exception
    {
        File workingDirectory = new File ( System.getProperty( "workingDirectory" ) ); 
        if ( ! workingDirectory.exists() ) 
        {
            workingDirectory.mkdirs();
        }
        FileUtils.forceDelete( workingDirectory );
        
        MutableStartupConfiguration config = new MutableStartupConfiguration();
        config.setWorkingDirectory( workingDirectory );
        config.setShutdownHookEnabled( false );
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

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.core.jndi.CoreContextFactory" );
        env.put( Context.PROVIDER_URL, "dc=example,dc=com" );
        env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, "secret" );
        env.put( Configuration.JNDI_KEY, config );
        env.put( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
        env.put( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );

        userContext = new InitialDirContext( env );
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

        store = new DefaultServerProfileStore( userContext );
        store.init();

        List filters = Collections.singletonList( new Krb5KdcEntryFilter() );
        LdifFileLoader loader = new LdifFileLoader( userContext, new File( "safehaus.ldif" ), filters, getClass().getClassLoader() );
        loader.execute();
    }


    protected void tearDown() throws Exception
    {
        userContext.close();
        ShutdownConfiguration config = new ShutdownConfiguration();
        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.core.jndi.CoreContextFactory" );
        env.put( Context.PROVIDER_URL, "dc=example,dc=com" );
        env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, "secret" );
        env.put( Configuration.JNDI_KEY, config );
        env.put( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
        env.put( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );
        new InitialDirContext( env );
        
        userContext = null;
        store = null;
    }


    /**
     * Generates the encrypted time stamp using a KerberosKey to mimic clients
     *
     * @param kerberosKey the kerberos key (from hotp value)
     * @param time the kerberos time for timestamp
     * @return the encrypted time stamp
     */
    private byte[] generateSad( KerberosKey kerberosKey, KerberosTime time )
    {
        EncryptionType keyType = EncryptionType.getTypeByOrdinal( kerberosKey.getKeyType() );
        EncryptionKey key = new EncryptionKey( keyType, kerberosKey.getEncoded() );
        byte[] sad = null;

        try
        {
            // Create the Timestamp
            EncryptedTimeStampModifier modifier = new EncryptedTimeStampModifier();
            modifier.setKerberosTime( time );
            EncryptedTimeStamp timeStamp = modifier.getEncryptedTimestamp();

            // Encode the Timestamp into ASN.1
            EncryptedTimestampEncoder encoder = new EncryptedTimestampEncoder();
            byte[] timeBytes = encoder.encode( timeStamp );

            // Encrypt the Timestamp
            EncryptionEngine engine = EncryptionEngineFactory.getEncryptionEngineFor( key );
            EncryptedData encryptedData = engine.getEncryptedData( key, timeBytes );

            // Encode the EncryptedData
            sad = EncryptedDataEncoder.encode( encryptedData );
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch (KerberosException ke)
        {
            ke.printStackTrace();
        }

        return sad;
    }

    
    /**
     * Tests that accounts lock out and ones that are locked from start do not
     * succeed.
     */
    public void testLockedOut() throws SamException, IOException, NamingException
    {
        DefaultHotpSamVerifier samVerifierDefault = new DefaultHotpSamVerifier();
        samVerifierDefault.setUserContext( userContext );
        samVerifierDefault.setIntegrityChecker( new TimestampChecker() );
        samVerifierDefault.startup();

        assertNotNull( samVerifierDefault );
        KerberosPrincipal lockedout = new KerberosPrincipal( "lockedout@EXAMPLE.COM" );

        try
        {
            char[] hotp = "123456".toCharArray();
            KerberosKey key = new KerberosKey( lockedout, hotp, "DES" );
            byte[] sad = generateSad( key, new KerberosTime() );
            samVerifierDefault.verify( lockedout, sad );
            fail( "should not get here due to exception" );
        }
        catch ( SamException e )
        {
//            assertEquals( HotpErrorConstants.LOCKEDOUT_MSG, e.getMessage() );
        }

        // --------------------------------------------------------------------
        // Ok let's now try to lock out an unlocked existing account: akarauslu
        // --------------------------------------------------------------------

        KerberosPrincipal akarasulu = new KerberosPrincipal( "akarasulu@EXAMPLE.COM" );
        int ii = 0;
        final int limit = ResynchParameters.DEFAULTS.getLockoutCount();

        for (; ii < limit; ii++ )
        {
            char[] hotp = "123456".toCharArray();
            KerberosKey key = new KerberosKey( akarasulu, hotp, "DES" );
            byte[] sad = generateSad( key, new KerberosTime() );

            try
            {
                samVerifierDefault.verify( akarasulu, sad );
            }
            catch( SamException e )
            {
                assertEquals( HotpErrorConstants.HOTPAUTH_FAILURE_MSG, e.getMessage() );
            }
        }

        assertEquals( limit, ii );

        // this next attempt with a bad hotp value should take us over the limit

        try
        {
            char[] hotp = "123456".toCharArray();
            KerberosKey key = new KerberosKey( akarasulu, hotp, "DES" );
            byte[] sad = generateSad( key, new KerberosTime() );
            samVerifierDefault.verify( akarasulu, sad );
            fail( "should not get here due to lockout" );
        }
        catch ( SamException e )
        {
            assertEquals( limit, ii );
            assertEquals( HotpErrorConstants.LOCKEDOUT_MSG, e.getMessage() );
        }
    }


    public void testResynch() throws SamException, NamingException, IOException
    {
        DefaultHotpSamVerifier samVerifierDefault = new DefaultHotpSamVerifier();
        samVerifierDefault.setUserContext( userContext );
        samVerifierDefault.setIntegrityChecker( new TimestampChecker() );
        samVerifierDefault.startup();
        ServerProfileStore s = samVerifierDefault.getStore();
        KerberosPrincipal principal = new KerberosPrincipal( "akarasulu@EXAMPLE.COM" );
        ServerProfile p = s.getProfile( principal );
        long factor = p.getFactor() + 5;
        byte[] secret = p.getSecret();
        assertNotNull( samVerifierDefault );

        try
        {
            char[] hotp = Hotp.generate( secret, factor, DefaultHotpSamVerifier.HOTP_SIZE ).toCharArray();
            KerberosKey key = new KerberosKey( principal, hotp, "DES" );
            byte[] sad = generateSad( key, new KerberosTime() );
            samVerifierDefault.verify( principal, sad );
            fail( "should not get here due to resynch" );
        }
        catch ( SamException e )
        {
            assertEquals( HotpErrorConstants.RESYNCH_STARTING_MSG, e.getMessage() );
        }

        char[] hotp = Hotp.generate( secret, factor + 1, DefaultHotpSamVerifier.HOTP_SIZE ).toCharArray();
        KerberosKey key = new KerberosKey( principal, hotp, "DES" );
        byte[] sad = generateSad( key, new KerberosTime() );
        assertNotNull( samVerifierDefault.verify( principal, sad ) );
    }


    public void testNormal() throws SamException, NamingException, IOException
    {
        DefaultHotpSamVerifier samVerifierDefault = new DefaultHotpSamVerifier();
        samVerifierDefault.setUserContext( userContext );
        samVerifierDefault.setIntegrityChecker( new TimestampChecker() );
        samVerifierDefault.startup();
        assertNotNull( samVerifierDefault );

        ServerProfileStore s = samVerifierDefault.getStore();
        KerberosPrincipal principal = new KerberosPrincipal( "akarasulu@EXAMPLE.COM" );
        ServerProfile p = s.getProfile( principal );
        long factor = p.getFactor();
        byte[] secret = p.getSecret();
        for ( int ii = 0; ii < 100; ii++ )
        {
            char[] hotp = Hotp.generate( secret, factor + ii, DefaultHotpSamVerifier.HOTP_SIZE ).toCharArray();
            KerberosKey key = new KerberosKey( principal, hotp, "DES" );
            byte[] sad = generateSad( key, new KerberosTime() );
            assertNotNull( samVerifierDefault.verify( principal, sad ) );
        }
    }
}
