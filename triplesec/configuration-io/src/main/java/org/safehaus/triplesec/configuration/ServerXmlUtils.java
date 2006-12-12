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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;

import org.apache.directory.server.core.configuration.AttributesPropertyEditor;
import org.apache.directory.server.core.configuration.InterceptorConfiguration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.MutableInterceptorConfiguration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * Utilities for reading and writing Spring configuration files for ApacheDS.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ServerXmlUtils
{
    private static final Logger log = LoggerFactory.getLogger( ServerXmlUtils.class );

    
    public static MutableTriplesecStartupConfiguration readConfiguration( URL url ) throws IOException
    {
        ApplicationContext factory = null;
        factory = new FileSystemXmlApplicationContext( url.toString() );
        return ( MutableTriplesecStartupConfiguration ) factory.getBean( "configuration" );
    }
    
    
    public static void writeConfiguration( File configurationFile, TriplesecStartupConfiguration configuration, 
        Properties environment ) throws Exception
    {
        Document document = DocumentHelper.createDocument();
        document.addDocType( "beans", "-//SPRING//DTD BEAN//EN", 
            "http://www.springframework.org/dtd/spring-beans.dtd" );
        Element beans = document.addElement( "beans" );
        
        if ( log.isDebugEnabled() )
        {
            log.debug( "adding environment properties: " + environment );
        }
        addEnvironmentBean( beans, environment );
        addConfigurationBean( beans, configuration );
        addCustomEditorsBean( beans );
        
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setTrimText( false );
        XmlUtils.writeDocument( document, configurationFile, format );
    }


    // -----------------------------------------------------------------------
    // Package Friendly Methods (non-exposed but PF for testing)
    // -----------------------------------------------------------------------

    
    private static void addCustomEditorsBean( Element beans )
    {
        Element bean = beans.addElement( "bean" ).addAttribute( "class", CustomEditorConfigurer.class.getName() );
        Element property = bean.addElement( "property" ).addAttribute( "name", "customEditors" );
        Element map = property.addElement( "map" );
        Element entry = map.addElement( "entry" ).addAttribute( "key", "javax.naming.directory.Attributes" );
        entry.addElement( "bean" ).addAttribute( "class", AttributesPropertyEditor.class.getName() );
    }


    static void addConfigurationBean( Element beansElement, TriplesecStartupConfiguration configuration )
        throws Exception
    {
        Element bean = beansElement.addElement( "bean" );
        bean.addAttribute( "id", "configuration" );
        bean.addAttribute( "class", 
            "org.safehaus.triplesec.configuration.MutableTriplesecStartupConfiguration" );
        
        if ( configuration.getPresentationBaseUrl() != null )
        {
            addProperty( bean, "presentationBaseUrl", configuration.getPresentationBaseUrl() );
        }
        
        addProperty( bean, "allowAnonymousAccess", configuration.isAllowAnonymousAccess() );
        addProperty( bean, "accessControlEnabled", configuration.isAccessControlEnabled() );
        addProperty( bean, "ldapPort", configuration.getLdapPort() );
        addProperty( bean, "ldapsCertificateFile", configuration.getLdapsCertificateFile() );
        addProperty( bean, "ldapsCertificatePassword", configuration.getLdapsCertificatePassword() );
        addProperty( bean, "ldapsPort", configuration.getLdapsPort() );
        addProperty( bean, "httpPort", configuration.getHttpPort() );
        
        if ( configuration.getLdifDirectory() != null )
        {
            addProperty( bean, "ldifDirectory", configuration.getLdifDirectory() );
        }
        
        addProperty( bean, "workingDirectory", configuration.getWorkingDirectory() );
        addProperty( bean, "enableChangePassword", configuration.isEnableChangePassword() );
        addProperty( bean, "enableKerberos", configuration.isEnableKerberos() );
        addProperty( bean, "enableLdaps", configuration.isEnableLdaps() );
        addProperty( bean, "enableNetworking", configuration.isEnableNetworking() );
        addProperty( bean, "enableNtp", configuration.isEnableNtp() );
        addProperty( bean, "enableHttp", configuration.isEnableHttp() );
        addProperty( bean, "exitVmOnShutdown", configuration.isExitVmOnShutdown() );
        addProperty( bean, "shutdownHookEnabled", configuration.isShutdownHookEnabled() );
        
        if ( configuration.getLdifFilters() != null && configuration.getLdifFilters().size() > 0 )
        {
            addBeans( "ldifFilters", bean, configuration.getLdifFilters() );
        }
        
        addBeans( "extendedOperationHandlers", bean, ( List ) configuration.getExtendedOperationHandlers() );
        addBeans( "bootstrapSchemas", bean, configuration.getBootstrapSchemas() );
        
        if ( configuration.getAuthenticatorConfigurations() != null && 
            configuration.getAuthenticatorConfigurations().size() > 0 )
        {
            // addBeans( "authenticatorConfigurations", bean, configuration.getAuthenticatorConfigurations() );
        }
        
        addInterceptorBeans( "interceptorConfigurations", bean, configuration.getInterceptorConfigurations() );
        addPartitionRefs( bean, configuration.getContextPartitionConfigurations() );
        
        for ( Iterator ii = configuration.getContextPartitionConfigurations().iterator(); ii.hasNext(); /**/ )
        {
            addPartitionConfiguration( beansElement, ( MutablePartitionConfiguration ) ii.next() );
        }
        
        addProperty( bean, configuration.getSmsConfiguration() );
        addProperty( bean, configuration.getSmtpConfiguration() );
    }
    
    
    private static void addProperty( Element bean, SmsConfiguration smsConfiguration )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", "smsConfiguration" );
        Element smsBean = property.addElement( "bean" );
        smsBean.addAttribute( "class", SmsConfiguration.class.getName() );
        addProperty( smsBean, "smsUsername", smsConfiguration.getSmsUsername() );
        addProperty( smsBean, "smsPassword", smsConfiguration.getSmsPassword() );
        addProperty( smsBean, "smsAccountName", smsConfiguration.getSmsAccountName() );
        addProperty( smsBean, "smsTransportUrl", smsConfiguration.getSmsTransportUrl() );
    }


    private static void addProperty( Element bean, SmtpConfiguration smtpConfiguration )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", "smtpConfiguration" );
        Element smsBean = property.addElement( "bean" );
        smsBean.addAttribute( "class", SmtpConfiguration.class.getName() );
        addProperty( smsBean, "smtpAuthenticate", smtpConfiguration.isSmtpAuthenticate() );
        if ( smtpConfiguration.isSmtpAuthenticate() )
        {
            addProperty( smsBean, "smtpUsername", smtpConfiguration.getSmtpUsername() );
            addProperty( smsBean, "smtpPassword", smtpConfiguration.getSmtpPassword() );
        }
        addProperty( smsBean, "smtpHost", smtpConfiguration.getSmtpHost() );
        addProperty( smsBean, "smtpFrom", smtpConfiguration.getSmtpFrom() );
        addProperty( smsBean, "smtpSubject", smtpConfiguration.getSmtpSubject() );
    }


    static void addPartitionConfiguration( Element beansElement, 
        MutablePartitionConfiguration configuration ) throws Exception
    {
        Element bean = beansElement.addElement( "bean" );
        bean.addAttribute( "id", configuration.getName() + "PartitionConfiguration" );
        bean.addAttribute( "class", MutablePartitionConfiguration.class.getName() );
        addProperty( bean, "name", configuration.getName() );
        addProperty( bean, "suffix", configuration.getSuffix() );
        
        // Add the contextEntry property by building the partial LDIF
        StringBuffer contextEntry = new StringBuffer();
        contextEntry.append( "\n" );
        NamingEnumeration enumeration = configuration.getContextEntry().getAll();
        while ( enumeration.hasMore() )
        {
            Attribute attr = ( Attribute ) enumeration.next();
            for ( int ii = 0; ii < attr.size(); ii++ )
            {
                contextEntry.append( attr.getID() ).append( ": " ).append( attr.get( ii ) ).append( "\n" );
            }
        }
        addProperty( bean, "contextEntry", contextEntry.toString() );
        
        // Build the indexedAttributes property 
        Element property = bean.addElement( "property" ).addAttribute( "name", "indexedAttributes" );
        Element indexSet = property.addElement( "set" );
        Set excludes = new HashSet();
        excludes.add( "1.2.6.1.4.1.18060.1.1.1.3.1" );
        excludes.add( "1.2.6.1.4.1.18060.1.1.1.3.2" );
        excludes.add( "1.2.6.1.4.1.18060.1.1.1.3.3" );
        excludes.add( "1.2.6.1.4.1.18060.1.1.1.3.4" );
        excludes.add( "1.2.6.1.4.1.18060.1.1.1.3.5" );
        excludes.add( "1.2.6.1.4.1.18060.1.1.1.3.6" );
        excludes.add( "1.2.6.1.4.1.18060.1.1.1.3.7" );
        for ( Iterator ii = configuration.getIndexedAttributes().iterator(); ii.hasNext(); /**/ )
        {
            String index = ( String ) ii.next();
            
            if ( excludes.contains( index ) )
            {
                continue;
            }
            
            indexSet.addElement( "value" ).addText( index );
        }
    }


    static void addInterceptorBeans( String propertyKey, Element bean, List interceptorConfigurations )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", propertyKey );
        Element list = property.addElement( "list" );
        
        for ( int ii = 0; ii < interceptorConfigurations.size(); ii++ )
        {
            InterceptorConfiguration configuration = ( InterceptorConfiguration ) interceptorConfigurations.get( ii );
            Element interceptorBean = list.addElement( "bean" ).addAttribute( "class", 
                MutableInterceptorConfiguration.class.getName() );
            addProperty( interceptorBean, "name", configuration.getName() );
            addBeanProperty( interceptorBean, "interceptor", configuration.getInterceptor().getClass() );
        }
    }


    static void addPartitionRefs( Element bean, Set partitions )
    {
        Element property = bean.addElement( "property" ).addAttribute( "name", "contextPartitionConfigurations" );
        Element set = property.addElement( "set" );
        
        for ( Iterator ii = partitions.iterator(); ii.hasNext(); /**/ )
        {
            MutablePartitionConfiguration configuration = ( MutablePartitionConfiguration ) ii.next();
            set.addElement( "ref" ).addAttribute( "bean", configuration.getName() + "PartitionConfiguration" );
        }
    }
    
    
    static void addBeanProperty( Element bean, String key, Class clazz )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", key );
        property.addElement( "bean" ).addAttribute( "class", clazz.getName() );
    }


    static void addBeans( String propertyKey, Element bean, List objects )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", propertyKey );
        Element list = property.addElement( "list" );
        
        for ( int ii = 0; ii < objects.size(); ii++ )
        {
            Object object = objects.get( ii );
            list.addElement( "bean" ).addAttribute( "class", object.getClass().getName() );
        }
    }


    static void addBeans( String propertyKey, Element bean, Set objects )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", propertyKey );
        Element set = property.addElement( "set" );
        
        for ( Iterator ii = objects.iterator(); ii.hasNext(); /**/ )
        {
            Object object = ii.next();
            set.addElement( "bean" ).addAttribute( "class", object.getClass().getName() );
        }
    }


    static void addProperty( Element bean, String key, File value )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", key );
        property.addElement( "value" ).addText( value.getPath() );
    }
    
    
    static void addProperty( Element bean, String key, boolean value )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", key );
        property.addElement( "value" ).addText( String.valueOf( value ) );
    }
    
    
    static void addProperty( Element bean, String key, int value )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", key );
        property.addElement( "value" ).addText( String.valueOf( value ) );
    }
    
    
    static void addProperty( Element bean, String key, String value )
    {
        Element property = bean.addElement( "property" );
        property.addAttribute( "name", key );
        property.addElement( "value" ).addText( value );
    }
    
    
    /**
     * Adds the properties bean to the configuration.  The section appears like so:
     * 
     * <pre>
     *     &lt;property name="properties"&gt;
     *       &lt;props&gt;
     *         &lt;prop key="java.naming.security.authentication"&gt;simple&lt;/prop&gt;
     *         &lt;prop key="java.naming.security.principal"&gt;uid=admin,ou=system&lt;/prop&gt;
     *         ...
     *       &lt;/props&gt;
     *     &lt;/property&gt;
     * </pre>
     * @param beansElement the top most root &lt;beans&gt; tag
     * @param environment the properties to add to this section
     */
    static void addEnvironmentBean( Element beansElement, Properties environment )
    {
        Element beanElement = beansElement.addElement( "bean" );
        beanElement.addAttribute( "id", "environment" );
        beanElement.addAttribute( "class", "org.springframework.beans.factory.config.PropertiesFactoryBean" );
        Element propertyElement = beanElement.addElement( "property" );
        propertyElement.addAttribute( "name", "properties" ) ;
        Element propsElement = propertyElement.addElement( "props" );
        
        List keys = new ArrayList( environment.keySet() );
        Collections.sort( keys );
        for ( int ii = 0; ii < keys.size(); ii++ )
        {
            String key = ( String ) keys.get( ii );
            String value = environment.getProperty( key );
            
            Element prop = propsElement.addElement( "prop" );
            prop.addAttribute( "key", key );
            prop.addText( value );
        }
    }
    
    
    static Document readDocument( URL url ) throws IOException, DocumentException
    {
        Document document = null;
        SAXReader reader = new SAXReader();
        document = reader.read( url );
        return document;
    }
}
