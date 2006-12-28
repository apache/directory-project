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
package org.safehaus.triplesec.admin.swing;


import java.io.File;
import java.io.IOException;

import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;

import junit.framework.TestCase;


public class SettingsUtilsTest extends TestCase
{
    public void testStoreAndLoad() throws IOException
    {
        File settingsFile = new File( System.getProperty( "settingsFile" ) );
        
        // -------------------------------------------------------------------
        // setup the settings to store
        // -------------------------------------------------------------------

        AdminToolSettings settings = new AdminToolSettings();
        settings.setAdminToolPassword( "secret" );
        settings.setSettingsPassphrase( "secret" );
        
        ConnectionInfoModifier modifier = new ConnectionInfoModifier();
        modifier.setCredentials( "secret" );
        modifier.setHost( "localhost" );
        modifier.setKrb5Port( 88 );
        modifier.setLdapPort( 10389 );
        modifier.setPrincipal( "admin" );
        modifier.setRealm( "SAFEHAUS.ORG" );
        modifier.setUseLdaps( false );
        settings.setDefaultConnectionInfo( modifier.getConnectionInfo() );
        
        SmsConfiguration smsConfiguration = new SmsConfiguration();
        smsConfiguration.setSmsAccountName( "test" );
        smsConfiguration.setSmsPassword( "secret" );
        smsConfiguration.setSmsTransportUrl( "http://some.server.com/some/path" );
        smsConfiguration.setSmsUsername( "smsuser" );
        settings.setDefaultSmsConfig( smsConfiguration );
        
        SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setSmtpAuthenticate( false );
        smtpConfiguration.setSmtpFrom( "admin@example.com" );
        smtpConfiguration.setSmtpHost( "hertz" );
        smtpConfiguration.setSmtpSubject( "New account available" );
        settings.setDefaultSmtpConfig( smtpConfiguration );
        
        // -------------------------------------------------------------------
        // Store the settings then load it and test for equality
        // -------------------------------------------------------------------

        SettingsUtils.store( settings, settingsFile );
        AdminToolSettings reloaded = SettingsUtils.load( "secret", settingsFile );

        assertEquals( settings.getAdminToolPassword(), reloaded.getAdminToolPassword() );
        assertEquals( settings.getSettingsPassphrase(), reloaded.getSettingsPassphrase() );

        assertEquals( settings.getDefaultConnectionInfo().getCredentials(), reloaded.getDefaultConnectionInfo().getCredentials() );
        assertEquals( settings.getDefaultConnectionInfo().getHost(), reloaded.getDefaultConnectionInfo().getHost() );
        assertEquals( settings.getDefaultConnectionInfo().getLdapRealmBase(), reloaded.getDefaultConnectionInfo().getLdapRealmBase() );
        assertEquals( settings.getDefaultConnectionInfo().getPrincipal(), reloaded.getDefaultConnectionInfo().getPrincipal() );
        assertEquals( settings.getDefaultConnectionInfo().getRealm(), reloaded.getDefaultConnectionInfo().getRealm() );
        assertEquals( settings.getDefaultConnectionInfo().getKrb5Port(), reloaded.getDefaultConnectionInfo().getKrb5Port() );
        assertEquals( settings.getDefaultConnectionInfo().getLdapPort(), reloaded.getDefaultConnectionInfo().getLdapPort() );
        assertEquals( settings.getDefaultConnectionInfo().isUseLdaps(), reloaded.getDefaultConnectionInfo().isUseLdaps() );

        assertEquals( settings.getDefaultSmsConfig().getSmsAccountName(), reloaded.getDefaultSmsConfig().getSmsAccountName() );
        assertEquals( settings.getDefaultSmsConfig().getSmsPassword(), reloaded.getDefaultSmsConfig().getSmsPassword() );
        assertEquals( settings.getDefaultSmsConfig().getSmsTransportUrl(), reloaded.getDefaultSmsConfig().getSmsTransportUrl() );
        assertEquals( settings.getDefaultSmsConfig().getSmsUsername(), reloaded.getDefaultSmsConfig().getSmsUsername() );

        assertEquals( settings.getDefaultSmtpConfig().getSmtpFrom(), reloaded.getDefaultSmtpConfig().getSmtpFrom() );
        assertEquals( settings.getDefaultSmtpConfig().isSmtpAuthenticate(), reloaded.getDefaultSmtpConfig().isSmtpAuthenticate() );
        assertEquals( settings.getDefaultSmtpConfig().getSmtpHost(), reloaded.getDefaultSmtpConfig().getSmtpHost() );
        assertEquals( settings.getDefaultSmtpConfig().getSmtpSubject(), reloaded.getDefaultSmtpConfig().getSmtpSubject() );
    }
}
