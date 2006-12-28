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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.safehaus.crypto.BlockCipherWrapper;
import org.safehaus.crypto.DESEngine;
import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;


public class SettingsUtils
{
    private static final String SETTINGS_FILE = ".tsecAdminToolSettings";
    private static final String SMTP_PASSWORD = "smtpPassword";
    private static final String SMTP_USERNAME = "smtpUsername";
    private static final String SMTP_SUBJECT = "smtpSubject";
    private static final String SMTP_FROM = "smtpFrom";
    private static final String SMTP_HOST = "smtpHost";
    private static final String SMS_TRANSPORT_URL = "smsTransportUrl";
    private static final String SMS_PASSWORD = "smsPassword";
    private static final String SMS_USERNAME = "smsUsername";
    private static final String SMS_ACCOUNT = "smsAccountName";
    private static final String ADMIN_TOOL_PASSWORD = "adminToolPassword";
    private static final String CONNECTION_USE_LDAPS = "connUseLdaps";
    private static final String CONNECTION_REALM = "connRealm";
    private static final String CONNECTION_PRINCIPAL = "connPrincipal";
    private static final String CONNECTION_LDAP_PORT = "connLdapPort";
    private static final String CONNECTION_KRB5_PORT = "connKrb5Port";
    private static final String CONNECTION_HOST = "connHost";
    private static final String CONNECTION_CREDENTIALS = "connCredentials";
    private static final String SMTP_AUTHENTICATE = "smtpAuthenticate";
    private static final String ENABLE_PASSCODE_PROMPT = "enablePasscodePrompt";
    private static final String PRESENTATION_BASE_URL = "presentationBaseUrl";

    
    public static boolean isAvailableSettings()
    {
        File userHome = new File( System.getProperty( "user.home" ) );
        File settingsFile = new File( userHome, SETTINGS_FILE );
        return settingsFile.exists();
    }


    public static void store( AdminToolSettings settings ) throws IOException
    {
        File userHome = new File( System.getProperty( "user.home" ) );
        File settingsFile = new File( userHome, SETTINGS_FILE );
        store( settings, settingsFile );
    }
    
    
    public static void store( AdminToolSettings settings, File settingsFile ) throws IOException
    {
        if ( settingsFile.exists() )
        {
            settingsFile.delete();
        }
        
        // Set all the properties in a Properties object
        Properties props = new Properties();
        props.setProperty( ADMIN_TOOL_PASSWORD, settings.getAdminToolPassword() );
        props.setProperty( ENABLE_PASSCODE_PROMPT, String.valueOf( settings.isPasscodePromptEnabled() ) );
        props.setProperty( PRESENTATION_BASE_URL, String.valueOf( settings.getPresentationBaseUrl() ) );
        
        props.setProperty( CONNECTION_CREDENTIALS, settings.getDefaultConnectionInfo().getCredentials() );
        props.setProperty( CONNECTION_HOST, settings.getDefaultConnectionInfo().getHost() );
        props.setProperty( CONNECTION_KRB5_PORT, String.valueOf( settings.getDefaultConnectionInfo().getKrb5Port() ) );
        props.setProperty( CONNECTION_LDAP_PORT, String.valueOf( settings.getDefaultConnectionInfo().getLdapPort() ) );
        props.setProperty( CONNECTION_PRINCIPAL, settings.getDefaultConnectionInfo().getPrincipal() );
        props.setProperty( CONNECTION_REALM, settings.getDefaultConnectionInfo().getRealm() );
        props.setProperty( CONNECTION_USE_LDAPS, String.valueOf( settings.getDefaultConnectionInfo().isUseLdaps() ) );
        
        props.setProperty( SMS_ACCOUNT, settings.getDefaultSmsConfig().getSmsAccountName() );
        props.setProperty( SMS_PASSWORD, settings.getDefaultSmsConfig().getSmsPassword() );
        props.setProperty( SMS_TRANSPORT_URL, settings.getDefaultSmsConfig().getSmsTransportUrl() );
        props.setProperty( SMS_USERNAME, settings.getDefaultSmsConfig().getSmsUsername() );

        props.setProperty( SMTP_AUTHENTICATE, String.valueOf( settings.getDefaultSmtpConfig().isSmtpAuthenticate() ) );
        props.setProperty( SMTP_FROM, settings.getDefaultSmtpConfig().getSmtpFrom() );
        props.setProperty( SMTP_HOST, settings.getDefaultSmtpConfig().getSmtpHost() );
        props.setProperty( SMTP_SUBJECT, settings.getDefaultSmtpConfig().getSmtpSubject() );
        
        if ( settings.getDefaultSmtpConfig().isSmtpAuthenticate() )
        {
            props.setProperty( SMTP_PASSWORD, settings.getDefaultSmtpConfig().getSmtpPassword() );
            props.setProperty( SMTP_USERNAME, settings.getDefaultSmtpConfig().getSmtpUsername() );
        }
        
        // Write out the properties into memory
        ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
        props.store( decrypted, new Date().toString() );
        decrypted.flush();
        
        // Encrypt the in memory properties buffer
        BlockCipherWrapper engine = new BlockCipherWrapper( new DESEngine().getClass() );
        byte[] encrypted = engine.encrypt( settings.getSettingsPassphrase(), decrypted.toByteArray() );
        
        // Write out the encrypted buffer to disk
        FileOutputStream out = new FileOutputStream( settingsFile );
        out.write( encrypted );
        out.flush();
        out.close();
    }
    
    
    public static AdminToolSettings load( String passphrase ) throws IOException
    {
        File userHome = new File( System.getProperty( "user.home" ) );
        File settingsFile = new File( userHome, SETTINGS_FILE );
        return load( passphrase, settingsFile );
    }
    
    
    public static AdminToolSettings load( String passphrase, File settingsFile ) throws IOException
    {
        if ( ! settingsFile.exists() )
        {
            return null;
        }
        
        // Load the encrypted contents of the file into memory: it's small
        byte[] encrypted = new byte[ ( int ) settingsFile.length()];
        FileInputStream fin = new FileInputStream ( settingsFile );
        fin.read( encrypted );
        fin.close();

        // Initialize the cipher wrapper and decrypt
        BlockCipherWrapper engine = new BlockCipherWrapper( new DESEngine().getClass() );
        byte[] decrypted = engine.decrypt( passphrase, encrypted );
        
        // Load decrypted properties
        ByteArrayInputStream in = new ByteArrayInputStream( decrypted );
        Properties props = new Properties();
        props.load( in );
        
        // Build properties bean
        AdminToolSettings settings = new AdminToolSettings();
        settings.setSettingsPassphrase( passphrase );
        settings.setAdminToolPassword( props.getProperty( ADMIN_TOOL_PASSWORD ) );
        settings.setPresentationBaseUrl( props.getProperty( PRESENTATION_BASE_URL ) );
        
        if ( props.getProperty( ENABLE_PASSCODE_PROMPT ) != null )
        {
            settings.setPasscodePromptEnabled( parseBoolean( props.getProperty( ENABLE_PASSCODE_PROMPT ) ) );
        }
        
        ConnectionInfoModifier modifier = new ConnectionInfoModifier();
        modifier.setCredentials( props.getProperty( CONNECTION_CREDENTIALS ) );
        modifier.setHost( props.getProperty( CONNECTION_HOST ) );
        modifier.setKrb5Port( Integer.parseInt( props.getProperty( CONNECTION_KRB5_PORT ) ) );
        modifier.setLdapPort( Integer.parseInt( props.getProperty( CONNECTION_LDAP_PORT ) ) );
        modifier.setPrincipal( props.getProperty( CONNECTION_PRINCIPAL ) );
        modifier.setRealm( props.getProperty( CONNECTION_REALM ) );
        modifier.setUseLdaps( parseBoolean( props.getProperty( CONNECTION_USE_LDAPS ) ) );
        settings.setDefaultConnectionInfo( modifier.getConnectionInfo() );
        
        SmsConfiguration smsConfiguration = new SmsConfiguration();
        smsConfiguration.setSmsAccountName( props.getProperty( SMS_ACCOUNT ) );
        smsConfiguration.setSmsPassword( props.getProperty( SMS_PASSWORD ) );
        smsConfiguration.setSmsTransportUrl( props.getProperty( SMS_TRANSPORT_URL ) );
        smsConfiguration.setSmsUsername( props.getProperty( SMS_USERNAME ) );
        settings.setDefaultSmsConfig( smsConfiguration );
        
        SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setSmtpHost( props.getProperty( SMTP_HOST ) );
        smtpConfiguration.setSmtpFrom( props.getProperty( SMTP_FROM ) );
        smtpConfiguration.setSmtpSubject( props.getProperty( SMTP_SUBJECT ) );
        
        if ( props.getProperty( SMTP_AUTHENTICATE ) != null && parseBoolean( props.getProperty( SMTP_AUTHENTICATE ) ) )
        {
            smtpConfiguration.setSmtpPassword( props.getProperty( SMTP_PASSWORD ) );
            smtpConfiguration.setSmtpUsername( props.getProperty( SMTP_USERNAME ) );
            smtpConfiguration.setSmtpAuthenticate( true );
        }
        else
        {
            smtpConfiguration.setSmtpAuthenticate( false );
        }
        settings.setDefaultSmtpConfig( smtpConfiguration );
        
        return settings;
    }
    
    
    private static boolean parseBoolean( String bool )
    {
        if ( bool.toLowerCase().equals( "true" ) )
        {
            return true;
        }
        
        return false;
    }
}
