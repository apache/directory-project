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
package org.safehaus.triplesec.configuration;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.authn.AuthenticationService;
import org.apache.directory.server.core.authz.AuthorizationService;
import org.apache.directory.server.core.authz.DefaultAuthorizationService;
import org.apache.directory.server.core.collective.CollectiveAttributeService;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.MutableInterceptorConfiguration;
import org.apache.directory.server.core.event.EventService;
import org.apache.directory.server.core.exception.ExceptionService;
import org.apache.directory.server.core.normalization.NormalizationService;
import org.apache.directory.server.core.operational.OperationalAttributeService;
import org.apache.directory.server.core.referral.ReferralService;
import org.apache.directory.server.core.schema.SchemaService;
import org.apache.directory.server.core.schema.bootstrap.ApacheSchema;
import org.apache.directory.server.core.schema.bootstrap.CollectiveSchema;
import org.apache.directory.server.core.schema.bootstrap.CorbaSchema;
import org.apache.directory.server.core.schema.bootstrap.CoreSchema;
import org.apache.directory.server.core.schema.bootstrap.CosineSchema;
import org.apache.directory.server.core.schema.bootstrap.InetorgpersonSchema;
import org.apache.directory.server.core.schema.bootstrap.JavaSchema;
import org.apache.directory.server.core.schema.bootstrap.Krb5kdcSchema;
import org.apache.directory.server.core.schema.bootstrap.SystemSchema;
import org.apache.directory.server.core.subtree.SubentryService;
import org.apache.directory.server.ldap.support.extended.GracefulShutdownHandler;
import org.apache.directory.server.ldap.support.extended.LaunchDiagnosticUiHandler;
import org.apache.directory.server.protocol.shared.store.Krb5KdcEntryFilter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.safehaus.triplesec.store.interceptor.PolicyProtectionInterceptor;
import org.safehaus.triplesec.store.schema.SafehausSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;


/**
 * Tests the ServerXmlUtils class.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ServerXmlUtilsTest extends TestCase
{
    private static final Logger log = LoggerFactory.getLogger( ServerXmlUtilsTest.class );
    private File workingDirectory = null;

    
    public void setUp() throws Exception
    {
        String wkdirProp = System.getProperty( "workingDirectory" );
        log.debug( "workingDirectory system property = " + wkdirProp );
        if ( wkdirProp == null || wkdirProp.equals( "" ) )
        {
            wkdirProp = System.getProperty( "java.io.tmpdir" ) + File.separator + "target";
        }
        
        workingDirectory = new File( wkdirProp );
        if ( workingDirectory.exists() )
        {
            log.debug( "deleteing workingDirectory = " + workingDirectory );
            FileUtils.forceDelete( workingDirectory );
        }

        log.debug( "creating workingDirectory = " + workingDirectory );
        workingDirectory.mkdirs();
        super.setUp();
    }
    
    
    public void tearDown() throws Exception
    {
        workingDirectory = null;
        super.tearDown();
    }
    
    
    public void testAddEnvironment0() throws Exception
    {
        Document document = DocumentHelper.createDocument();
        document.addDocType( "beans", "-//SPRING//DTD BEAN//EN", 
            "http://www.springframework.org/dtd/spring-beans.dtd" );
        Element beans = document.addElement( "beans" );
        ServerXmlUtils.addEnvironmentBean( beans, new Properties() );
        checkDocument( document );
    }
    
    
    public void testAddEnvironment1() throws Exception
    {
        Document document = DocumentHelper.createDocument();
        document.addDocType( "beans", "-//SPRING//DTD BEAN//EN", 
            "http://www.springframework.org/dtd/spring-beans.dtd" );
        Element beans = document.addElement( "beans" );
        Properties props = new Properties();
        props.put( "property1key", "property1value" );
        ServerXmlUtils.addEnvironmentBean( beans, props );
        checkDocument( document );
    }
    
    
    public void testWriteConfiguration0() throws Exception
    {
        MutableTriplesecStartupConfiguration configuration = new MutableTriplesecStartupConfiguration();
        configuration.setLdapPort( 10389 );
        configuration.setAccessControlEnabled( true );
        configuration.setShutdownHookEnabled( true );
        configuration.setAllowAnonymousAccess( false );
        configuration.setEnableChangePassword( true );
        configuration.setEnableKerberos( true );
        configuration.setEnableNetworking( true );
        configuration.setEnableLdaps( false );
        configuration.setEnableNtp( true );
        configuration.setExitVmOnShutdown( true );
        configuration.setLdifDirectory( new File( "conf" ) );
        configuration.setWorkingDirectory( new File( "var/partitions" ) );
        
        List filters = new ArrayList();
        filters.add( new Krb5KdcEntryFilter() );
        configuration.setLdifFilters( filters );
        
        List interceptors = new ArrayList();
        MutableInterceptorConfiguration interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new NormalizationService() );
        interceptorConfiguration.setName( "normalizationService" );
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new AuthenticationService() );
        interceptorConfiguration.setName( "authenticationService" );
        interceptors.add( interceptorConfiguration );

        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new ReferralService() );
        interceptorConfiguration.setName( "referralService" );
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new AuthorizationService() );
        interceptorConfiguration.setName( "authorizationService" );
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new DefaultAuthorizationService() );
        interceptorConfiguration.setName( "defaultAuthorizationService" ); 
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration(); 
        interceptorConfiguration.setInterceptor( new ExceptionService() );
        interceptorConfiguration.setName( "exceptionService" );
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new SchemaService() );
        interceptorConfiguration.setName( "schemaService" ); 
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new SubentryService() );
        interceptorConfiguration.setName( "subentryService" );
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new OperationalAttributeService() );
        interceptorConfiguration.setName( "operationalAttributeService" );
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new CollectiveAttributeService() );
        interceptorConfiguration.setName( "collectiveAttributeService" );
        interceptors.add( interceptorConfiguration );

        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new EventService() );
        interceptorConfiguration.setName( "eventService" );
        interceptors.add( interceptorConfiguration );
        
        interceptorConfiguration = new MutableInterceptorConfiguration();
        interceptorConfiguration.setInterceptor( new PolicyProtectionInterceptor() );
        interceptorConfiguration.setName( "policyProtectionInterceptor" );
        interceptors.add( interceptorConfiguration );
        
        configuration.setInterceptorConfigurations( interceptors );
        
        List extendedHandlers = new ArrayList();
        extendedHandlers.add( new GracefulShutdownHandler() );
        extendedHandlers.add( new LaunchDiagnosticUiHandler() );
        configuration.setExtendedOperationHandlers( extendedHandlers );

        configuration.getSmsConfiguration().setSmsAccountName( "foo" );
        configuration.getSmsConfiguration().setSmsUsername( "bar" );
        configuration.getSmsConfiguration().setSmsTransportUrl( "http://google.com" );
        configuration.getSmsConfiguration().setSmsPassword( "secret" );
        
        configuration.getSmtpConfiguration().setSmtpAuthenticate( false );
        configuration.getSmtpConfiguration().setSmtpFrom( "dev@safehaus.org" );
        configuration.getSmtpConfiguration().setSmtpHost( "localhost" );
        configuration.getSmtpConfiguration().setSmtpSubject( "Triplesec account activated" );
        
        Set partitions = new HashSet( configuration.getContextPartitionConfigurations() );
        MutablePartitionConfiguration partitionConfiguration = new MutablePartitionConfiguration();
        partitionConfiguration.setName( "example" );
        partitionConfiguration.setSuffix( "dc=example,dc=com" );
        Set indices = new HashSet();
        indices.add( "objectClass" );
        indices.add( "ou" ) ;
        indices.add( "dc" ) ;
        indices.add( "uid" ) ;
        indices.add( "profileId" ) ;
        indices.add( "roles" ) ;
        indices.add( "grants" ) ;
        indices.add( "denials" ) ;
        indices.add( "krb5PrincipalName" ) ;
        partitionConfiguration.setIndexedAttributes( indices );
        Attributes contextEntry = new BasicAttributes( "objectClass", "top", true );
        contextEntry.get( "objectClass" ).add( "domain" );
        contextEntry.get( "objectClass" ).add( "extensibleObject" );
        contextEntry.put( "dc", "example" );
        contextEntry.put( "administrativeRole", "accessControlSpecificArea" );
        contextEntry.get( "administrativeRole" ).add( "collectiveAttributeSpecificArea" );
        partitionConfiguration.setContextEntry( contextEntry );
        partitions.add( partitionConfiguration );
        configuration.setContextPartitionConfigurations( partitions );
        
        Set schemas = new HashSet();
        schemas.add( new CorbaSchema() );
        schemas.add( new CoreSchema() );
        schemas.add( new CosineSchema() );
        schemas.add( new ApacheSchema() );
        schemas.add( new CollectiveSchema() );
        schemas.add( new InetorgpersonSchema() );
        schemas.add( new JavaSchema() );
        schemas.add( new Krb5kdcSchema() );
        schemas.add( new SystemSchema() );
        schemas.add( new SafehausSchema() );
        configuration.setBootstrapSchemas( schemas );
        
        File outputFile = new File( workingDirectory, getName() + ".xml" );
        log.debug( getName() + "(): outputing document to file: " + outputFile.getCanonicalPath() );
        Properties props = new Properties();
        props.put( "java.naming.security.authentication", "simple" );
        props.put( "java.naming.security.principal", "uid=admin,ou=system" );
        props.put( "java.naming.security.credentials", "secret" );
        props.put( "java.naming.provider.url", "dc=example,dc=com" );
        props.put( "java.naming.factory.state", "org.safehaus.triplesec.store.ProfileStateFactory" );
        props.put( "java.naming.factory.object", "org.safehaus.triplesec.store.ProfileObjectFactory" );
        props.put( "kdc.primary.realm", "EXAMPLE.COM" );
        props.put( "kdc.principal", "krbtgt/EXAMPLE.COM@EXAMPLE.COM" );
        props.put( "kdc.encryption.types", "des-cbc-md5 des3-cbc-sha1 des3-cbc-md5 des-cbc-md4 des-cbc-crc" );
        props.put( "kdc.entryBaseDn", "ou=users,dc=example,dc=com" );
        props.put( "kdc.java.naming.security.credentials", "secret" );
        props.put( "changepw.entryBaseDn", "ou=users,dc=example,dc=com" );
        props.put( "changepw.java.naming.security.credentials", "secret" );
        props.put( "changepw.principal", "kadmin/changepw@EXAMPLE.COM" );
        props.put( "kdc.allowable.clockskew", "5" );
        props.put( "kdc.tgs.maximum.ticket.lifetime", "1440" );
        props.put( "kdc.tgs.maximum.renewable.lifetime", "10080" );
        props.put( "kdc.pa.enc.timestamp.required", "true" );
        props.put( "kdc.tgs.empty.addresses.allowed", "true" );
        props.put( "kdc.tgs.forwardable.allowed", "true" );
        props.put( "kdc.tgs.proxiable.allowed", "true" );
        props.put( "kdc.tgs.postdate.allowed", "true" );
        props.put( "kdc.tgs.renewable.allowed", "true" );
        props.put( "safehaus.entry.basedn", "ou=Users,dc=example,dc=com" );
        props.put( "safehaus.load.testdata", "true" );
        props.put( "kerberos.sam.type.7", "org.safehaus.triplesec.verifier.hotp.DefaultHotpSamVerifier" );
        ServerXmlUtils.writeConfiguration( outputFile, configuration, props );
    }
    
    
    // -----------------------------------------------------------------------
    // Private Utility Methods 
    // -----------------------------------------------------------------------

    
    private void checkDocument( Document document ) throws IOException
    {
        File outputFile = new File( workingDirectory, getName() + ".xml" );
        log.debug( getName() + "(): outputing document to file: " + outputFile.getCanonicalPath() );
        XmlUtils.writeDocument( document, outputFile, OutputFormat.createPrettyPrint() );
        checkFile( getClass().getResource( getName() + ".xml" ), outputFile );
    }
    
    
    private void checkFile( URL expected, File generated ) throws IOException
    {
        InputStream generatedIn = new FileInputStream( generated );
        InputStream expectedIn = expected.openStream();
        while ( generatedIn.available() > 0 )
        {
            int generatedByte = generatedIn.read();
            int expectedByte = expectedIn.read();
            assertEquals( generatedByte, expectedByte );
        }
        expectedIn.close();
        generatedIn.close();
        log.debug( "generated output " + generated.getCanonicalPath() + " == expected output " + expected.toString() );
    }
}
