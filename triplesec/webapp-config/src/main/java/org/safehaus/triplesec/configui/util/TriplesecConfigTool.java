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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.configuration.ShutdownConfiguration;
import org.apache.directory.server.core.configuration.SyncConfiguration;
import org.apache.directory.shared.ldap.ldif.Entry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.util.Base64;
import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.apache.tools.ant.Project;
//import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.dom4j.io.OutputFormat;

import org.safehaus.triplesec.TriplesecInstallationLayout;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;
import org.safehaus.triplesec.configuration.ContextParameter;
import org.safehaus.triplesec.configuration.FilterConfiguration;
import org.safehaus.triplesec.configuration.MutableTriplesecStartupConfiguration;
import org.safehaus.triplesec.configuration.ServerXmlUtils;
import org.safehaus.triplesec.configuration.ServletConfiguration;
import org.safehaus.triplesec.configuration.WebappConfiguration;
import org.safehaus.triplesec.configuration.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to generate the server.xml file, update the web.xml files of installed 
 * webapps, and setup the ldif files loaded on startup.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecConfigTool
{
    private static final Logger log = LoggerFactory.getLogger( TriplesecConfigTool.class );
    
    public static void writeConfiguration( TriplesecInstallationLayout layout, TriplesecConfigSettings settings ) 
        throws Exception
    {
        // -------------------------------------------------------------------
        // save the new password and revert to the default one
        // -------------------------------------------------------------------

        String password = settings.getAdminPassword();
        settings.setAdminPassword( "secret" );
        settings.setAdminPassword2( "secret" );
        
        // -------------------------------------------------------------------
        // write out the server.xml file but use old password
        // -------------------------------------------------------------------

        TriplesecConfigBuilder builder = new TriplesecConfigBuilder();
        TriplesecPropBuilder propBuilder = new TriplesecPropBuilder();
        Properties props = propBuilder.build( settings );
        MutableTriplesecStartupConfiguration config = builder.build( settings );
        ServerXmlUtils.writeConfiguration( layout.getConfigurationFile(), config, props );
        
        // -------------------------------------------------------------------
        // start up the server to change the password within the database & shutdown
        // -------------------------------------------------------------------

        File wkDir = new File( layout.getVarDirectory(), "partitions" );
        wkDir.mkdirs();
        setAdminPassword( config, wkDir, password );

        // -------------------------------------------------------------------
        // rewrite configuration with the new password
        // -------------------------------------------------------------------

        settings.setAdminPassword( password );
        settings.setAdminPassword2( password );
        props = propBuilder.build( settings );
        config = builder.build( settings );
        ServerXmlUtils.writeConfiguration( layout.getConfigurationFile(), config, props );
        
        // -------------------------------------------------------------------
        // If demo is enabled and installed move it's LDIF files to conf else
        // delete the entire demo application from the webapps directory
        // -------------------------------------------------------------------

        // ApacheDS Bug in LDIF File Loading prevents us from using this code
        // the order of LDIF file loading is not predictable across systems and
        // this needs to be fixed before we can enable this code here.
        
//        File demo = new File( layout.getWebappsDirectory(), "demo" );
//        if ( settings.isEnableDemo() && demo.exists() )
//        {
//            File demoWebinfDir = new File( demo, "WEB-INF" );
//            File[] ldifFiles = demoWebinfDir.listFiles( new FileFilter() {
//                public boolean accept( File pathname )
//                {
//                    if ( pathname.getName().lastIndexOf( ".ldif" ) != -1 )
//                    {
//                        return true;
//                    }
//                    return false;
//                }
//            } );
//
//            // Copy all LDIF files to the conf directory to be loaded on startup
//            Project project = new Project();
//            for ( int ii = 0; ii < ldifFiles.length; ii++ )
//            {
//                Copy cp = new Copy();
//                cp.setProject( project );
//                cp.setFile( ldifFiles[ii] );
//                cp.setTodir( layout.getConfigurationDirectory() );
//                cp.execute();
//            }
//        }
//        else if ( ! settings.isEnableDemo() && demo.exists() )
//        {
//            Project project = new Project();
//            Delete del = new Delete();
//            del.setProject( project );
//            del.setDir( demo );
//            del.execute();
//        }

        // Here's the HACK: we append any LDIF files we find to the server.xml
        // remove this after fixing the apacheds bug.

        // note we open the file for appending
        FileOutputStream out = new FileOutputStream( 
            new File( layout.getConfigurationDirectory(), "00server.ldif" ), true );
        
        File demo = new File( layout.getWebappsDirectory(), "demo" );
        if ( settings.isEnableDemo() && demo.exists() )
        {
            File demoWebinfDir = new File( demo, "WEB-INF" );
            File[] ldifFiles = demoWebinfDir.listFiles( new FileFilter()
            {
                public boolean accept( File pathname )
                {
                    if ( pathname.getName().lastIndexOf( ".ldif" ) != -1 )
                    {
                        return true;
                    }
                    return false;
                }
            } );

            // open the file and read then write bytes to server.ldif
            for ( int ii = 0; ii < ldifFiles.length; ii++ )
            {
                FileInputStream in = new FileInputStream( ldifFiles[ii] );
                int ch = -1;
                out.write( '\n' );
                out.write( '\n' );
                while ( ( ch = in.read() ) != -1 )
                {
                    out.write( ch );
                }
                out.write( '\n' );
                out.write( '\n' );
                in.close();
            }
            
            out.close();

            // -------------------------------------------------------------------
            // Yet another hack! Here we slurp up the spring applicationContext 
            // for the spring based wicket demo application and replace macros
            // -------------------------------------------------------------------

            File appCtxFile = new File( demo, "WEB-INF" );
            appCtxFile = new File( appCtxFile, "classes" );
            appCtxFile = new File( appCtxFile, "applicationContext.xml" );
            String appCtxTemplate = FileUtils.readFileToString( appCtxFile, "UTF-8" );
            
            StringBuffer buf = new StringBuffer();
            String ldapBaseDn = NamespaceTools.inferLdapName( settings.getPrimaryRealmName() );
            buf.append( "appName=demo,ou=Applications," ).append( ldapBaseDn );
            appCtxTemplate = appCtxTemplate.replaceAll( "APPLICATION_PRINCIPAL_DN", buf.toString() );
            
            buf.setLength( 0 );
            buf.append( "ldap://localhost:" ).append( settings.getLdapPort() ).append( "/" ).append( ldapBaseDn );
            appCtxTemplate = appCtxTemplate.replaceAll( "CONNECTION_URL", buf.toString() );
            appCtxTemplate = appCtxTemplate.replaceAll( "REALM", settings.getPrimaryRealmName().toUpperCase() );
            appCtxTemplate = appCtxTemplate.replaceAll( "SMS_TRANSPORT_URL", settings.getSmsTransportUrl() );
            appCtxTemplate = appCtxTemplate.replaceAll( "SMS_PASSWORD", settings.getSmsPassword() );
            appCtxTemplate = appCtxTemplate.replaceAll( "SMS_USERNAME", settings.getSmsUsername() );
            appCtxTemplate = appCtxTemplate.replaceAll( "SMS_ACCOUNT_NAME", settings.getSmsAccountName() );
            FileUtils.writeByteArrayToFile( appCtxFile, appCtxTemplate.getBytes( "UTF-8" ) );
                    
        }
        else if ( !settings.isEnableDemo() && demo.exists() )
        {
            Project project = new Project();
            Delete del = new Delete();
            del.setProject( project );
            del.setDir( demo );
            del.execute();
        }
        
        // -------------------------------------------------------------------
        // read and alter all the ldif file contents
        // -------------------------------------------------------------------

        File confDir = layout.getConfigurationDirectory();
        File[] ldifFiles = confDir.listFiles( new FileFilter() {
            public boolean accept( File pathname )
            {
                if ( pathname.getName().lastIndexOf( ".ldif" ) != -1 )
                {
                    return true;
                }
                return false;
            }
        } );

        for ( int ii = 0; ii < ldifFiles.length; ii++ )
        {
            alterLdifFile( NamespaceTools.inferLdapName( settings.getPrimaryRealmName() ), 
                settings.getPrimaryRealmName(), ldifFiles[ii] );
        }
        
        // -------------------------------------------------------------------
        // scan for webapps other than this one to modify their web.xml
        // -------------------------------------------------------------------

        File[] webapps = layout.getWebappsDirectory().listFiles( new FileFilter() {
            public boolean accept( File pathname )
            {
                if ( pathname.isDirectory() && ! pathname.getName().endsWith( "config" ) )
                {
                    return true;
                }
                return false;
            }
        } );

        for ( int ii = 0; ii < webapps.length; ii++ )
        {
            File webXml = new File( new File( webapps[ii], "WEB-INF" ), "web.xml" );
            alterWebXmlFile( webXml, settings, props );
        }
    }
    
    
    private static void alterWebXmlFile( File webXml, TriplesecConfigSettings settings, Properties props ) 
        throws Exception
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "altering webapp configuration: " + webXml.getAbsolutePath() );
        }

        boolean hasChanged = false;
        String ldapHost = "localhost";
        WebappConfiguration webconfig = new WebappConfiguration( webXml );
        
        String ldapBaseDn = null;
        if ( settings.getPrimaryRealmName() != null )
        { 
            ldapBaseDn = NamespaceTools.inferLdapName( settings.getPrimaryRealmName().toLowerCase() );
        }
        else
        {
            ldapBaseDn = "example.com";
        }
        
        try 
        {
            InetAddress localMachine = InetAddress.getLocalHost();
            ldapHost = localMachine.getHostName();
        }
        catch ( UnknownHostException uhe ) 
        {
            uhe.printStackTrace();
        }
        
        for ( Iterator ii = webconfig.getContextParameters(); ii.hasNext(); /****/ )
        {
            ContextParameter parameter = ( ContextParameter ) ii.next();
            
            if ( parameter.getName().equals( "realm" ) && settings.getPrimaryRealmName() != null )
            {
                parameter.setValue( settings.getPrimaryRealmName().toUpperCase() );
                hasChanged = true;
            }

            else if ( parameter.getName().equals( "presentationBaseUrl" ) && settings.getPresentationBaseUrl() != null )
            {
                parameter.setValue( settings.getPresentationBaseUrl() );
                hasChanged = true;
            }
            
            else if ( parameter.getName().equals( "redirectUrl" ) && settings.getRegRedirectUrl() != null )
            {
                parameter.setValue( settings.getRegRedirectUrl() );
                hasChanged = true;
            }
            
            else if ( parameter.getName().equals( "ldapCredentials" ) && settings.getAdminPassword() != null )
            {
                parameter.setValue( settings.getAdminPassword() );
                hasChanged = true;
            }
            
            else if ( parameter.getName().equals( "ldapPort" ) )
            {
                parameter.setValue( String.valueOf( settings.getLdapPort() ) );
                hasChanged = true;
            }
            
            else if ( parameter.getName().equals( "ldapBaseDn" ) )
            {
                parameter.setValue( ldapBaseDn );
                hasChanged = true;
            }
            
            else if ( parameter.getName().equals( "ldapHost" ) )
            {
                parameter.setValue( ldapHost );
                hasChanged = true;
            }
            
            // ---------------------------------------------------------------
            // Handle setup of SMS configuration init parameters
            // ---------------------------------------------------------------

            else if ( parameter.getName().equals( "smsAccountName" ) && settings.getSmsAccountName() != null )
            {
                parameter.setValue( settings.getSmsAccountName() );
                hasChanged = true;
            }

            else if ( parameter.getName().equals( "smsPassword" ) && settings.getSmsPassword() != null )
            {
                parameter.setValue( settings.getSmsPassword() );
                hasChanged = true;
            }

            else if ( parameter.getName().equals( "smsTransportUrl" ) && settings.getSmsTransportUrl() != null )
            {
                parameter.setValue( settings.getSmsTransportUrl() );
                hasChanged = true;
            }

            else if ( parameter.getName().equals( "smsUsername" ) && settings.getSmsUsername() != null )
            {
                parameter.setValue( settings.getSmsUsername() );
                hasChanged = true;
            }

            // ---------------------------------------------------------------
            // Handle setup of SMTP configuration init parameters
            // ---------------------------------------------------------------

            else if ( parameter.getName().equals( "smtpFrom" ) && settings.getSmtpFrom() != null )
            {
                parameter.setValue( settings.getSmtpFrom() );
                hasChanged = true;
            }

            else if ( parameter.getName().equals( "smtpSubject" ) && settings.getSmtpSubject() != null )
            {
                parameter.setValue( settings.getSmtpSubject() );
                hasChanged = true;
            }
            
            else if ( parameter.getName().equals( "smtpHost" ) && settings.getSmtpHost() != null )
            {
                parameter.setValue( settings.getSmtpHost() );
                hasChanged = true;
            }
            
            // @todo some issues here: below will add these parameters even if they exist
            // we need a lookup to see if the parameter exists then need to create it
            if ( settings.isSmtpAuthenticate() &&
                 settings.getSmtpUsername() != null &&
                 settings.getSmtpPassword() != null )
            {
                if ( parameter.getName().equals( "smtpUsername" ) )
                {
                    parameter.setValue( settings.getSmtpUsername() );
                }
//                else
//                {
//                    webconfig.addContextParameter( "smtpUsername", settings.getSmtpUsername() );
//                }

                if ( parameter.getName().equals( "smtpPassword" ) )
                {
                    parameter.setValue( settings.getSmtpPassword() );
                }
//                else
//                {
//                    webconfig.addContextParameter( "smtpPassword", settings.getSmtpPassword() );
//                }
                
                hasChanged = true;
            }
            else
            {
                // check for the presence of init parameters here and if present delete them
            }
        }
        
        for ( Iterator ii = webconfig.getServletConfigurations(); ii.hasNext(); /****/ )
        {
            ServletConfiguration sconf = ( ServletConfiguration ) ii.next();
            
            if ( sconf.hasInitParameter( "realm" ) && settings.getPrimaryRealmName() != null )
            {
                sconf.setInitParameterValue( "realm", settings.getPrimaryRealmName().toUpperCase() );
                hasChanged = true;
            }
            
            if ( sconf.hasInitParameter( "presentationBaseUrl" ) && settings.getPresentationBaseUrl() != null )
            {
                sconf.setInitParameterValue( "presentationBaseUrl", settings.getPresentationBaseUrl() );
                hasChanged = true;
            }
            
            if ( sconf.hasInitParameter( "redirectUrl" ) && settings.getRegRedirectUrl() != null )
            {
                sconf.setInitParameterValue( "redirectUrl", settings.getRegRedirectUrl() );
                hasChanged = true;
            }
            
            if ( sconf.hasInitParameter( "ldapCredentials" ) && settings.getAdminPassword() != null )
            {
                sconf.setInitParameterValue( "ldapCredentials", settings.getAdminPassword() );
                hasChanged = true;
            }
            
            if ( sconf.hasInitParameter( "ldapPort" ) )
            {
                sconf.setInitParameterValue( "ldapPort", String.valueOf( settings.getLdapPort() ) );
                hasChanged = true;
            }
            
            if ( sconf.hasInitParameter( "ldapBaseDn" ) )
            {
                sconf.setInitParameterValue( "ldapBaseDn", ldapBaseDn );
                hasChanged = true;
            }
            
            if ( sconf.hasInitParameter( "ldapHost" ) )
            {
                sconf.setInitParameterValue( "ldapHost", ldapHost );
                hasChanged = true;
            }
            
            // ---------------------------------------------------------------
            // Handle setup of SMS configuration init parameters
            // ---------------------------------------------------------------

            if ( sconf.hasInitParameter( "smsAccountName" ) && settings.getSmsAccountName() != null )
            {
                sconf.setInitParameterValue( "smsAccountName", settings.getSmsAccountName() );
                hasChanged = true;
            }

            if ( sconf.hasInitParameter( "smsPassword" ) && settings.getSmsPassword() != null )
            {
                sconf.setInitParameterValue( "smsPassword", settings.getSmsPassword() );
                hasChanged = true;
            }

            if ( sconf.hasInitParameter( "smsTransportUrl" ) && settings.getSmsTransportUrl() != null )
            {
                sconf.setInitParameterValue( "smsTransportUrl", settings.getSmsTransportUrl() );
                hasChanged = true;
            }

            if ( sconf.hasInitParameter( "smsUsername" ) && settings.getSmsUsername() != null )
            {
                sconf.setInitParameterValue( "smsUsername", settings.getSmsUsername() );
                hasChanged = true;
            }

            // ---------------------------------------------------------------
            // Handle setup of SMTP configuration init parameters
            // ---------------------------------------------------------------

            if ( sconf.hasInitParameter( "smtpFrom" ) && settings.getSmtpFrom() != null )
            {
                sconf.setInitParameterValue( "smtpFrom", settings.getSmtpFrom() );
                hasChanged = true;
            }

            if ( sconf.hasInitParameter( "smtpSubject" ) && settings.getSmtpSubject() != null )
            {
                sconf.setInitParameterValue( "smtpSubject", settings.getSmtpSubject() );
                hasChanged = true;
            }
            
            if ( sconf.hasInitParameter( "smtpHost" ) && settings.getSmtpHost() != null )
            {
                sconf.setInitParameterValue( "smtpHost", settings.getSmtpHost() );
                hasChanged = true;
            }
            
            if ( settings.isSmtpAuthenticate() &&
                 settings.getSmtpUsername() != null &&
                 settings.getSmtpPassword() != null )
            {
                if ( sconf.hasInitParameter( "smtpUsername" ) )
                {
                    sconf.setInitParameterValue( "smtpUsername", settings.getSmtpUsername() );
                }
                else
                {
                    sconf.addInitParameter( "smtpUsername", settings.getSmtpUsername() );
                }

                if ( sconf.hasInitParameter( "smtpPassword" ) )
                {
                    sconf.setInitParameterValue( "smtpPassword", settings.getSmtpPassword() );
                }
                else
                {
                    sconf.addInitParameter( "smtpPassword", settings.getSmtpPassword() );
                }
                
                hasChanged = true;
            }
            else
            {
                // check for the presence of init parameters here and if present delete them
            }
        }
        
        for ( Iterator ii = webconfig.getFilterConfigurations(); ii.hasNext(); /****/ )
        {
            FilterConfiguration fconf = ( FilterConfiguration ) ii.next();

            if ( fconf.hasInitParameter( "presentationBaseUrl" ) && settings.getPresentationBaseUrl() != null )
            {
                fconf.setInitParameterValue( "presentationBaseUrl", settings.getPresentationBaseUrl() );
                hasChanged = true;
            }
            
            if ( fconf.hasInitParameter( "redirectUrl" ) && settings.getRegRedirectUrl() != null )
            {
                fconf.setInitParameterValue( "redirectUrl", settings.getRegRedirectUrl() );
                hasChanged = true;
            }
            
            if ( fconf.hasInitParameter( "ldapCredentials" ) && settings.getAdminPassword() != null )
            {
                fconf.setInitParameterValue( "ldapCredentials", settings.getAdminPassword() );
                hasChanged = true;
            }
            
            if ( fconf.hasInitParameter( "ldapPort" ) )
            {
                fconf.setInitParameterValue( "ldapPort", String.valueOf( settings.getLdapPort() ) );
                hasChanged = true;
            }
            
            if ( fconf.hasInitParameter( "ldapBaseDn" ) ) 
            {
                fconf.setInitParameterValue( "ldapBaseDn", ldapBaseDn );
                hasChanged = true;
            }
            
            if ( fconf.hasInitParameter( "ldapHost" ) )
            {
                fconf.setInitParameterValue( "ldapHost", ldapHost );
                hasChanged = true;
            }
            
            // ---------------------------------------------------------------
            // Handle setup of SMS configuration init parameters
            // ---------------------------------------------------------------

            if ( fconf.hasInitParameter( "smsAccountName" ) && settings.getSmsAccountName() != null )
            {
                fconf.setInitParameterValue( "smsAccountName", settings.getSmsAccountName() );
                hasChanged = true;
            }

            if ( fconf.hasInitParameter( "smsPassword" ) && settings.getSmsPassword() != null )
            {
                fconf.setInitParameterValue( "smsPassword", settings.getSmsPassword() );
                hasChanged = true;
            }

            if ( fconf.hasInitParameter( "smsTransportUrl" ) && settings.getSmsTransportUrl() != null )
            {
                fconf.setInitParameterValue( "smsTransportUrl", settings.getSmsTransportUrl() );
                hasChanged = true;
            }

            if ( fconf.hasInitParameter( "smsUsername" ) && settings.getSmsUsername() != null )
            {
                fconf.setInitParameterValue( "smsUsername", settings.getSmsUsername() );
                hasChanged = true;
            }

            // ---------------------------------------------------------------
            // Handle setup of SMTP configuration init parameters
            // ---------------------------------------------------------------

            if ( fconf.hasInitParameter( "smtpFrom" ) && settings.getSmtpFrom() != null )
            {
                fconf.setInitParameterValue( "smtpFrom", settings.getSmtpFrom() );
                hasChanged = true;
            }

            if ( fconf.hasInitParameter( "smtpSubject" ) && settings.getSmtpSubject() != null )
            {
                fconf.setInitParameterValue( "smtpSubject", settings.getSmtpSubject() );
                hasChanged = true;
            }
            
            if ( fconf.hasInitParameter( "smtpHost" ) && settings.getSmtpHost() != null )
            {
                fconf.setInitParameterValue( "smtpHost", settings.getSmtpHost() );
                hasChanged = true;
            }
            
            if ( settings.isSmtpAuthenticate() &&
                 settings.getSmtpUsername() != null &&
                 settings.getSmtpPassword() != null )
            {
                if ( fconf.hasInitParameter( "smtpUsername" ) )
                {
                    fconf.setInitParameterValue( "smtpUsername", settings.getSmtpUsername() );
                }
                else
                {
                    fconf.addInitParameter( "smtpUsername", settings.getSmtpUsername() );
                }

                if ( fconf.hasInitParameter( "smtpPassword" ) )
                {
                    fconf.setInitParameterValue( "smtpPassword", settings.getSmtpPassword() );
                }
                else
                {
                    fconf.addInitParameter( "smtpPassword", settings.getSmtpPassword() );
                }
                
                hasChanged = true;
            }
            else
            {
                // check for the presence of init parameters here and if present delete them
            }
        }
        
        if ( hasChanged )
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setTrimText( false );
            XmlUtils.writeDocument( webconfig.getDocument(), webXml, format );
        }
    }


    public static void alterLdifFile( String suffix, String realm, File ldifFile ) throws Exception
    {
        List entries = new ArrayList(); 
        LdifReader reader = new LdifReader();
        List entryList = reader.parseLdifFile( ldifFile.getAbsolutePath() );
        for ( int ii = 0; ii < entryList.size(); ii++ )
        {
            Entry entry = ( Entry ) entryList.get( ii );
            Attributes attrs = entry.getAttributes();
            
            // old legacy stuff expecting DN in attributes
            attrs.put( "dn", entry.getDn() );
            entries.add( alter( suffix, realm, attrs ) );
        }
        
        FileWriter out = new FileWriter( ldifFile );
        for ( int ii = 0; ii < entries.size(); ii++ )
        {
            Attributes attrs = ( Attributes ) entries.get( ii );
            String dn = ( String ) attrs.remove( "dn" ).get();
            String ldif = getEntryLdif( dn, attrs );
            out.write( ldif );
        }
        out.flush();
        out.close();
    }
    
    
    public static String getEntryLdif( String dn, Attributes attrs ) throws NamingException
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "dn: " ).append( dn ).append( "\n" );
        
        for ( NamingEnumeration ids = attrs.getIDs(); ids.hasMore(); /****/ )
        {
            String id = ( String ) ids.next();
            Attribute attr = attrs.get( id );
            
            for ( int ii = 0; ii < attr.size(); ii++ )
            {
                Object value = attr.get( ii );
                
                if ( value instanceof String )
                {
                    buf.append( id ).append( ": " ).append( value ).append( "\n" );
                }
                else
                {
                    value = new String ( Base64.encode( ( byte[] ) value ) );
                    buf.append( id ).append( ":: " ).append( value ).append( "\n" );
                }
            }
        }
        
        buf.append( "\n" );
        return buf.toString();
    }
    
    
    public static Attributes alter( String suffix, String realm, Attributes attrs ) throws NamingException
    {
        String realmLowerCase = realm.toLowerCase();
        String realmUpperCase = realm.toUpperCase();
        
        for ( NamingEnumeration ids = attrs.getIDs(); ids.hasMore(); /****/ )
        {
            String id = ( String ) ids.next();
            Attribute attr = attrs.remove( id );
            
            if ( id.equalsIgnoreCase( "dn" ) )
            {
                String dn = ( String ) attr.get();
                dn = dn.replaceAll( "dc=example,.*dc=com", suffix );
                attrs.put( "dn", dn );
            }
            else if ( id.equalsIgnoreCase( "krb5PrincipalName" ) )
            {
                String pname = ( String ) attr.get();
                pname = pname.replaceAll( "EXAMPLE\\.COM", realmUpperCase );
                pname = pname.replaceAll( "example\\.com", realmLowerCase );
                attrs.put( "krb5PrincipalName", pname );
            }
            else if ( id.equalsIgnoreCase( "mail" ) )
            {
                String mail = ( String ) attr.get();
                mail = mail.replaceAll( "example\\.com", realmLowerCase );
                attrs.put( "mail", mail );
            }
            else if ( id.equalsIgnoreCase( "prescriptiveACI" ) )
            {
                String prescriptiveACI = ( String ) attr.get();
                prescriptiveACI = prescriptiveACI.replaceAll( "dc=example,.*dc=com", suffix );
                attrs.put( "prescriptiveACI", prescriptiveACI );
            }
            else if ( id.equalsIgnoreCase( "safehausRealm" ) )
            {
                attrs.put( "safehausRealm", realm.toUpperCase() );
            }
            else
            {
                attrs.put( attr );
            }
        }
        return attrs;
    }
    
    
    public static void setAdminPassword( MutableTriplesecStartupConfiguration config, File wkDir, String newPassword ) 
        throws Exception
    {
        // Build the environment and set the working directory
        Hashtable env = new Hashtable();
        env.put( "java.naming.factory.initial", "org.apache.directory.server.core.jndi.CoreContextFactory" );
        env.put( "java.naming.provider.url", "ou=system" );
        env.put( "java.naming.security.principal", "uid=admin,ou=system" );
        env.put( "java.naming.security.credentials", "secret" );
        env.put( "java.naming.security.authentication", "simple" );
        config.setWorkingDirectory( wkDir );
        env.putAll( config.toJndiEnvironment() );
        
        // Fire up the server without services and set the admin user password
        InitialDirContext ctx = new InitialDirContext( env );
        ctx.modifyAttributes( "uid=admin", DirContext.REPLACE_ATTRIBUTE, new BasicAttributes( "userPassword", 
            newPassword, true ) );
        ctx.close();
        
        // Synch the database to persist changes 
        SyncConfiguration syncConfig = new SyncConfiguration();
        env.putAll( syncConfig.toJndiEnvironment() );
        env.put( "java.naming.security.credentials", newPassword );
        ctx = new InitialDirContext( env );
        ctx.close();
        
        // Shutdown the database
        env.putAll( new ShutdownConfiguration().toJndiEnvironment() );
        ctx = new InitialDirContext( env );
    }
}
