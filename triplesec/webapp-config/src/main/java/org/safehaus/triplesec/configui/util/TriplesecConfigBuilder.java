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
package org.safehaus.triplesec.configui.util;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

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
import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;
import org.safehaus.triplesec.configuration.MutableTriplesecStartupConfiguration;
import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;
import org.safehaus.triplesec.store.interceptor.PolicyProtectionInterceptor;
import org.safehaus.triplesec.store.schema.SafehausSchema;


/**
 * A tool used to build the server configuration. 
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecConfigBuilder
{
    public MutableTriplesecStartupConfiguration build( TriplesecConfigSettings settings ) throws NamingException
    {
        MutableTriplesecStartupConfiguration config = getDefault();
        
        /*
         * Alter the configuration here according to new config settings
         */
        String realm = settings.getPrimaryRealmName();
        String baseDn = NamespaceTools.inferLdapName( realm );
        baseDn = baseDn.toLowerCase();
        
        // NTP causes exceptions everytime and has little utility - disabling it 
        config.setEnableNtp( false );
        
        config.setLdapPort( ( int ) settings.getLdapPort() ); // @todo make getLdapPort() return an int
        config.setPresentationBaseUrl( settings.getPresentationBaseUrl() );
        
        if ( realm.toUpperCase().equals( "EXAMPLE.COM" ) )
        {
            return config;
        }
        
        // -------------------------------------------------------------------
        // Configure custom partition for realm
        // -------------------------------------------------------------------
        
        // create partition and set suffix and name
        MutablePartitionConfiguration partition = new MutablePartitionConfiguration();
        Set partitions = new HashSet( 1 );
        partitions.add( partition );
        partition.setSuffix( baseDn );
        if ( realm.indexOf( '.' ) == -1 )
        {
            partition.setName( realm.toLowerCase() );
        }
        else
        {
            String[] comps = realm.split( "\\." );
            partition.setName( comps[0].toLowerCase() );
        }
        
        // setup indices 
        Set indices = new HashSet();
        indices.add( "objectClass" );
        indices.add( "ou" );
        indices.add( "dc" );
        indices.add( "uid" );
        indices.add( "profileId" );
        indices.add( "roles" );
        indices.add( "grants" );
        indices.add( "denials" );
        indices.add( "krb5PrincipalName" );
        partition.setIndexedAttributes( indices );
        
        // setup partition's context entry (top entry)
        Attributes contextEntry = new BasicAttributes( "objectClass", "top", true );
        contextEntry.get( "objectClass" ).add( "domain" );
        contextEntry.get( "objectClass" ).add( "extensibleObject" );
        contextEntry.put( "dc", partition.getName() );
        contextEntry.put( "administrativeRole", "accessControlSpecificArea" );
        contextEntry.get( "administrativeRole" ).add( "collectiveAttributeSpecificArea" );
        partition.setContextEntry( contextEntry );
        
        config.setContextPartitionConfigurations( partitions );

        // -------------------------------------------------------------------
        // Configure http settings
        // -------------------------------------------------------------------
        
        if ( settings.isEnableHttp() )
        {
            config.setEnableHttp( true );
            config.setHttpPort( settings.getHttpPort() );
        }
        else
        {
            config.setEnableHttp( false );
        }
        
        // -------------------------------------------------------------------
        // Configure server sms settings
        // -------------------------------------------------------------------
        
        SmsConfiguration smsConfig = new SmsConfiguration();
        config.setSmsConfiguration( smsConfig );
        smsConfig.setSmsAccountName( settings.getSmsAccountName() );
        smsConfig.setSmsPassword( settings.getSmsPassword() );
        smsConfig.setSmsTransportUrl( settings.getSmsTransportUrl() );
        smsConfig.setSmsUsername( settings.getSmsUsername() );

        // -------------------------------------------------------------------
        // Configure server smtp settings
        // -------------------------------------------------------------------
        
        SmtpConfiguration smtpConfig = new SmtpConfiguration();
        config.setSmtpConfiguration( smtpConfig );
        if ( settings.isSmtpAuthenticate() )
        {
            smtpConfig.setSmtpAuthenticate( true );
            smtpConfig.setSmtpPassword( settings.getSmtpPassword() );
            smtpConfig.setSmtpUsername( settings.getSmtpUsername() );
        }
        smtpConfig.setSmtpFrom( settings.getSmtpFrom() );
        smtpConfig.setSmtpHost( settings.getSmtpHost() );
        smtpConfig.setSmtpSubject( settings.getSmtpSubject() );
        
        // -------------------------------------------------------------------
        // Configure LDAPS settings
        // -------------------------------------------------------------------
        
        if ( settings.isEnableLdaps() )
        {
            File certFile = new File( settings.getLdapCertFilePath() );
            config.setEnableLdaps( true );
            config.setLdapsPort( settings.getLdapsPort() );
            config.setLdapsCertificateFile( certFile );
            config.setLdapsCertificatePassword( settings.getLdapCertPassword() );
        }
        else
        {
            config.setEnableLdaps( false );
        }
        
        return config;
    }
    
    
    public MutableTriplesecStartupConfiguration getDefault() throws NamingException
    {
        MutableTriplesecStartupConfiguration config = new MutableTriplesecStartupConfiguration();
        config.setLdapPort( 10389 );
        config.setAccessControlEnabled( true );
        config.setShutdownHookEnabled( true );
        config.setAllowAnonymousAccess( false );
        config.setEnableChangePassword( true );
        config.setEnableKerberos( true );
        config.setEnableNetworking( true );
        config.setEnableLdaps( false );
        config.setEnableNtp( true );
        config.setExitVmOnShutdown( true );
        config.setLdifDirectory( new File( "conf" ) );
        config.setWorkingDirectory( new File( "var/partitions" ) );
        
        try
        {
            String hostname = InetAddress.getLocalHost().getHostName();
            config.setPresentationBaseUrl( "http://" + hostname + ":8383" );
        }
        catch ( UnknownHostException e ) 
        {
            e.printStackTrace();
        }
        
        List filters = new ArrayList();
        filters.add( new Krb5KdcEntryFilter() );
        config.setLdifFilters( filters );
        
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
        
        config.setInterceptorConfigurations( interceptors );
        
        List extendedHandlers = new ArrayList();
        extendedHandlers.add( new GracefulShutdownHandler() );
        extendedHandlers.add( new LaunchDiagnosticUiHandler() );
        config.setExtendedOperationHandlers( extendedHandlers );

        config.getSmsConfiguration().setSmsAccountName( "foo" );
        config.getSmsConfiguration().setSmsUsername( "bar" );
        config.getSmsConfiguration().setSmsTransportUrl( "http://google.com" );
        config.getSmsConfiguration().setSmsPassword( "secret" );
        
        config.getSmtpConfiguration().setSmtpAuthenticate( false );
        config.getSmtpConfiguration().setSmtpFrom( "dev@safehaus.org" );
        config.getSmtpConfiguration().setSmtpHost( "localhost" );
        config.getSmtpConfiguration().setSmtpSubject( "Triplesec account activated" );
        
        Set partitions = new HashSet( config.getContextPartitionConfigurations() );
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
        config.setContextPartitionConfigurations( partitions );
        
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
        config.setBootstrapSchemas( schemas );

        return config;
    }
}
