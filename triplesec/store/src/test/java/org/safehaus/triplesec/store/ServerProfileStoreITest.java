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


import java.io.File;
import java.util.*;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.commons.io.FileUtils;
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
import org.safehaus.profile.BaseServerProfileModifier;
import org.safehaus.profile.ProfileTestData;
import org.safehaus.profile.ServerProfile;
import org.safehaus.triplesec.store.schema.SafehausSchema;
import junit.framework.TestCase;


/**
 * A set of testcases for a profile store implementation.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ServerProfileStoreITest extends TestCase
{
    /** the server side store for profiles */
    ServerProfileStore store = null;
    DirContext userContext = null;

    public ServerProfileStoreITest() throws Exception
    {
        super();
    }


    protected void setUp() throws Exception
    {
        File workingDirectory = File.createTempFile( "ServerProfileStoreITest", "test" );
        if ( ! workingDirectory.exists() ) 
        {
            workingDirectory.mkdirs();
        }
        FileUtils.forceDelete( workingDirectory );
        
        MutableStartupConfiguration config = new MutableStartupConfiguration();
        config.setWorkingDirectory( workingDirectory );
        MutablePartitionConfiguration partConfig = new MutablePartitionConfiguration();
        partConfig.setName( "example" );

        HashSet indices = new HashSet();
        indices.add( "dc" );
        indices.add( "ou" );
        indices.add( "objectClass" );
        indices.add( "cn" );
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
        config.setShutdownHookEnabled( false );
        
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

        assertNotNull( store );
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
     * Tests getting a profile.
     *
     * @throws Exception if it fails
     */
    public void testGetProfile() throws Exception
    {
        KerberosPrincipal principal = new KerberosPrincipal( "akarasulu@EXAMPLE.COM" );
        ServerProfile p = store.getProfile( principal );
        assertNotNull( p );
        assertEquals( "akarasulu", p.getUserId() );
        assertEquals( "EXAMPLE.COM", p.getRealm() );
        assertEquals( "example realm", p.getLabel() );
        assertEquals( 27304238, p.getFactor() );
        assertEquals( 0, p.getFailuresInEpoch() );
        assertEquals( -1, p.getResynchCount() );
        assertEquals( "test account", p.getInfo() );
    }


    /**
     * Tests updating a profile.
     *
     * @throws Exception if it fails
     */
    public void testUpdate() throws Exception
    {
        KerberosPrincipal principal = new KerberosPrincipal( "akarasulu@EXAMPLE.COM" );
        ServerProfile p = store.getProfile( principal );
        assertNotNull( p );
        assertEquals( "akarasulu", p.getUserId() );
        assertEquals( "EXAMPLE.COM", p.getRealm() );
        assertEquals( "example realm", p.getLabel() );
        assertEquals( 27304238, p.getFactor() );
        assertEquals( 0, p.getFailuresInEpoch() );
        assertEquals( -1, p.getResynchCount() );
        assertEquals( "test account", p.getInfo() );

        BaseServerProfileModifier modifier = new BaseServerProfileModifier( p );
        modifier.setFailuresInEpoch( 123 );
        modifier.setResynchCount( 0 );
        modifier.setFactor( 27304238 + 1 );
        store.update( principal, modifier.getServerProfile() );

        ServerProfile updated = store.getProfile( principal );
        assertEquals( 123, updated.getFailuresInEpoch() );
        assertEquals( 0, updated.getResynchCount() );
        assertEquals( 27304239, updated.getFactor() );
    }


    /**
     * Tests adding a profile.
     *
     * @throws Exception if it fails
     */
    public void testAddProfile() throws Exception
    {
        for ( int ii = 0; ii < ProfileTestData.PROFILES.length; ii++ )
        {
            ServerProfile profile = ProfileTestData.PROFILES[ii];
            String name = profile.getUserId() + "@" + profile.getRealm();
            KerberosPrincipal principal = new KerberosPrincipal( name );

            if ( ! store.hasProfile( principal ) )
            {
                store.add( profile );
            }

            profile = store.getProfile( principal );
            assertNotNull( profile );
            assertEquals( profile.getFactor(), profile.getFactor() );
            assertEquals( profile.getUserId(), profile.getUserId() );
            assertEquals( profile.getFailuresInEpoch(), profile.getFailuresInEpoch() );
            assertEquals( profile.getInfo(), profile.getInfo() );
            assertEquals( profile.getLabel(), profile.getLabel() );
            assertEquals( profile.getRealm(), profile.getRealm() );
            assertEquals( profile.getResynchCount(), profile.getResynchCount() );
            assertEquals( profile.getSecret(), profile.getSecret() );
        }
    }
}
