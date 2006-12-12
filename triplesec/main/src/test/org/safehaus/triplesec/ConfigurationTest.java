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
package org.safehaus.triplesec;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.ldap.common.berlib.asn1.SnickersProvider;
import org.apache.ldap.server.configuration.DirectoryPartitionConfiguration;
import org.apache.ldap.server.configuration.ServerStartupConfiguration;
import org.apache.ldap.server.jndi.ServerContextFactory;
import org.apache.ldap.server.schema.bootstrap.ApacheSchema;
import org.apache.ldap.server.schema.bootstrap.CoreSchema;
import org.apache.ldap.server.schema.bootstrap.CosineSchema;
import org.apache.ldap.server.schema.bootstrap.InetorgpersonSchema;
import org.apache.ldap.server.schema.bootstrap.Krb5kdcSchema;
import org.apache.ldap.server.schema.bootstrap.SystemSchema;
import org.safehaus.triplesec.store.ProfileObjectFactory;
import org.safehaus.triplesec.store.ProfileStateFactory;
import org.safehaus.triplesec.verifier.hotp.DefaultHotpSamVerifier;
import org.safehaus.triplesec.store.schema.SafehausSchema;
import junit.framework.TestCase;

public class ConfigurationTest extends TestCase
{
    private ConfigurationFactory factory;

    private Hashtable env;

    public void setUp()
    {
        factory = new ConfigurationFactory();

        env = new Hashtable();
        
        // Put common properties
        env.put( Configuration.KDC_PRIMARY_REALM, "EXAMPLE.COM" );
        env.put( Configuration.KDC_PRINCIPAL, "krbtgt/EXAMPLE.COM@EXAMPLE.COM" );
        env.put( Configuration.CHANGEPW_PRINCIPAL, "kadmin/changepw@EXAMPLE.COM" );
    }
    
    public void testSufficientProperties()
    {
        factory.getInstance( env );
    }
    
    public void testInsufficientProperties()
    {
        env.clear();
        subTestInsufficientProperties();
        
        env.clear();
        env.put( Configuration.KDC_PRIMARY_REALM, "EXAMPLE.COM" );
        subTestInsufficientProperties();
        
        env.clear();
        env.put( Configuration.KDC_PRIMARY_REALM, "EXAMPLE.COM" );
        env.put( Configuration.KDC_PRINCIPAL, "krbtgt/EXAMPLE.COM@EXAMPLE.COM" );
        subTestInsufficientProperties();
    }

    private void subTestInsufficientProperties()
    {
        try
        {
            env = factory.getInstance( env );
            fail( "IllegalArgumentException must be thrown." );
        }
        catch( IllegalArgumentException e )
        {
            assertTrue( true );
        }
    }

    public void testPreserveOriginalProperties()
    {
        env.put( "keyX", "valueY" );
        env = factory.getInstance( env );
        assertEquals( "valueY", env.get( "keyX" ) );
    }
    
    public void testJndiProperties() throws Exception
    {
        env = factory.getInstance( env );
        assertEquals( "dc=example,dc=com", env.get( Context.PROVIDER_URL ) );
        assertEquals( "uid=admin,ou=system", env.get( Context.SECURITY_PRINCIPAL ) );
        assertEquals( "simple", env.get( Context.SECURITY_AUTHENTICATION ) );
        assertEquals( "secret", env.get( Context.SECURITY_CREDENTIALS ) );
        assertEquals( ServerContextFactory.class.getName(), env.get( Context.INITIAL_CONTEXT_FACTORY ) );
        assertEquals( ProfileStateFactory.class.getName(), env.get( Context.STATE_FACTORIES ) );
        assertEquals( ProfileObjectFactory.class.getName(), env.get( Context.OBJECT_FACTORIES ) );

        // Do additional tests
        tearDown();
        setUp();
        
        env.put( Configuration.KDC_PRIMARY_REALM, "GLEAMYNODE.NET" );
        env = factory.getInstance( env );
        assertEquals( "dc=gleamynode,dc=net", env.get( Context.PROVIDER_URL ) );
    }
    
    public void testApacheDsProperties() throws NamingException
    {
        Hashtable env = factory.getInstance( this.env );

        ServerStartupConfiguration cfg = ( ServerStartupConfiguration ) ServerStartupConfiguration.toConfiguration( env );

        assertTrue( cfg.isEnableKerberos() );

        Set partitions = cfg.getContextPartitionConfigurations();

        assertEquals( 1, partitions.size() );

        // let's test that all partition information is correct

        DirectoryPartitionConfiguration partition = ( DirectoryPartitionConfiguration ) partitions.iterator().next();

        assertEquals( "dc=example,dc=com", partition.getSuffix() );

        assertTrue( partition.getIndexedAttributes().contains( "ou" ) );
        assertTrue( partition.getIndexedAttributes().contains( "uid" ) );
        assertTrue( partition.getIndexedAttributes().contains( "objectClass" ) );
        assertTrue( partition.getIndexedAttributes().contains( "krb5PrincipalName" ) );
        assertTrue( partition.getIndexedAttributes().contains( "safehausUid" ) );
        assertTrue( partition.getIndexedAttributes().contains( "safehausRealm" ) );

        assertEquals( "example", partition.getContextEntry().get( "dc" ).get() );

        assertTrue( partition.getContextEntry().get( "objectClass" ).contains( "top" ) );
        assertTrue( partition.getContextEntry().get( "objectClass" ).contains( "domain" ) );

        assertEquals( env.get( Configuration.WORK_DIRECTORY ) +
                      File.separator + "realms" + 
                      File.separator + "example_com", cfg.getWorkingDirectory().getPath() );

        assertEquals( SnickersProvider.class.getName(), env.get( Configuration.ASN1_BERLIB_PROVIDER ) );

        assertTrue( cfg.isAllowAnonymousAccess() );
        assertTrue( cfg.isEnableNetworking() );
        
        Set schemaTypes = new HashSet();
        Iterator it = cfg.getBootstrapSchemas().iterator();
        while( it.hasNext() )
        {
            schemaTypes.add( it.next().getClass() );
        }
        
        assertTrue( schemaTypes.contains( SystemSchema.class ) );
        assertTrue( schemaTypes.contains( ApacheSchema.class ) );
        assertTrue( schemaTypes.contains( CoreSchema.class ) );
        assertTrue( schemaTypes.contains( CosineSchema.class ) );
        assertTrue( schemaTypes.contains( InetorgpersonSchema.class ) );
        assertTrue( schemaTypes.contains( Krb5kdcSchema.class ) );
        assertTrue( schemaTypes.contains( SafehausSchema.class ) );

        assertEquals( 389, cfg.getLdapPort() );
    }

    public void testSafeHausProperties()
    {
        env = factory.getInstance( env );
        assertEquals( ".." + File.separator + "var", env.get( Configuration.WORK_DIRECTORY ) );
        assertEquals( "389", env.get( Configuration.LDAP_PORT ) );
        assertEquals( LoggingHotpMonitor.class.getName(), env.get( Configuration.VERIFIER_MONITOR ) );
        assertEquals( LoggingStoreMonitor.class.getName(), env.get( Configuration.STORE_MONITOR ) );
        assertEquals( "true", env.get( Configuration.LOAD_TEST_DATA ) );
        assertEquals( "ou=Users,dc=example,dc=com", env.get( Configuration.ENTRY_BASEDN ) );
        assertEquals( ".." + File.separator + "conf" + File.separator + "server.ldif", env.get( Configuration.ENTRY_LDIF_FILE ) );
    }
    
    public void testKerberosProperties()
    {
        env = factory.getInstance( env );
        assertEquals( DefaultHotpSamVerifier.class.getName(), env.get( Configuration.KERBEROS_SAM_TYPE ) );
    }
    
    public void testKdcProperties()
    {
        env = factory.getInstance( env );
        assertEquals( "88", env.get( Configuration.KDC_DEFAULT_PORT ) );
        assertEquals( ".." + File.separator + "conf" + File.separator + "server.ldif", env.get( Configuration.KDC_ENTRY_LDIF_FILE ) );
        assertEquals( "ou=Users,dc=example,dc=com", env.get( Configuration.KDC_ENTRY_BASEDN ) );
        assertEquals( "5", env.get( Configuration.KDC_ALLOWABLE_CLOCKSKEW ) );
        assertEquals( "kerberoskeys.ser", env.get( Configuration.KDC_KEYS_LOCATION ) );
        assertEquals( "des-cbc-md5 des3-cbc-sha1 des3-cbc-md5 " +
                      "des-cbc-md4 des-cbc-crc",
                      env.get( Configuration.KDC_ENCRYPTION_TYPES ) );
        assertEquals( "true", env.get( Configuration.KDC_PA_ENC_TIMESTAMP_REQUIRED ) );
    }
    
    public void testTgsProperties()
    {
        env = factory.getInstance( env );
        assertEquals( "1440", env.get( Configuration.TGS_MAXIMUM_TICKET_LIFETIME ) );
        assertEquals( "10080", env.get( Configuration.TGS_MAXIMUM_RENEWABLE_LIFETIME ) );
        assertEquals( "true", env.get( Configuration.TGS_EMPTY_ADDRESSES_ALLOWED ) );
        assertEquals( "true", env.get( Configuration.TGS_FORWARDABLE_ALLOWED ) );
        assertEquals( "true", env.get( Configuration.TGS_PROXIABLE_ALLOWED ) );
        assertEquals( "true", env.get( Configuration.TGS_POSTDATE_ALLOWED ) );
        assertEquals( "true", env.get( Configuration.TGS_RENEWABLE_ALLOWED ) );
    }
    
    public void testInducedProperties() throws Exception
    {
        env.put( Configuration.KDC_PRIMARY_REALM, "GLEAMYNODE.NET" );

        env.put( Configuration.LDAP_PORT, "1024" );

        env = factory.getInstance( env );

        ServerStartupConfiguration cfg = ( ServerStartupConfiguration ) ServerStartupConfiguration.toConfiguration( env );

        assertEquals( 1024, cfg.getLdapPort() );

        assertEquals( env.get( Configuration.KDC_ENTRY_BASEDN ),
                      env.get( Configuration.ENTRY_BASEDN ) );

        assertEquals( env.get( Configuration.KDC_ENTRY_LDIF_FILE ),
                      env.get( Configuration.ENTRY_LDIF_FILE ) );

        assertTrue( cfg.isEnableNetworking() );

        tearDown();

        setUp();

        // We don't need to do this anymore do we ???

        env.put( Configuration.LDAP_PORT, "-1" );

        env = factory.getInstance( env );
        cfg = ( ServerStartupConfiguration ) ServerStartupConfiguration.toConfiguration( env );
        
        assertFalse( cfg.isEnableNetworking() );
    }
}
