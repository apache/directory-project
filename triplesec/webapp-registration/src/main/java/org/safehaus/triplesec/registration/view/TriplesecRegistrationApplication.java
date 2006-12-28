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
package org.safehaus.triplesec.registration.view;


import java.util.Properties;

import javax.naming.Context;

import wicket.protocol.http.WebApplication;

import org.safehaus.triplesec.admin.TriplesecAdmin;
import org.safehaus.triplesec.admin.dao.DaoFactory;
import org.safehaus.triplesec.admin.dao.ldap.LdapDaoFactory;
import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;
import org.safehaus.triplesec.registration.view.pages.WizardPage;


public class TriplesecRegistrationApplication extends WebApplication
{
    // Ldap init parameter names
    private static final String LDAP_PORT = "ldapPort";
    private static final String LDAP_BASE_DN = "ldapBaseDn";
    private static final String LDAP_CREDENTIALS = "ldapCredentials";
    private static final String LDAP_PRINCIPAL_DN = "ldapPrincipalDn";
    private static final String LDAP_HOST = "ldapHost";

    // Mail init parameters names
    private static final String SMTP_PASSWORD_PARAM = "smtpPassword";
    private static final String SMTP_USERNAME_PARAM = "smtpUsername";
    private static final String SMTP_SUBJECT_PARAM = "smtpSubject";
    private static final String SMTP_FROM_PARAM = "smtpFrom";
    private static final String SMTP_HOST_PARAM = "smtpHost";
    
    // SMS init parameters names
    private static final String SMS_TRANSPORT_URL_PARAM = "smsTransportUrl";
    private static final String SMS_PASSWORD_PARAM = "smsPassword";
    private static final String SMS_USERNAME_PARAM = "smsUsername";
    private static final String SMS_ACCOUNT_PARAM = "smsAccountName";
    
    // General init parameters names
    private static final String REDIRECT_URL_PARAM = "redirectUrl";
    
    private String realm;
    private String presentationBaseUrl;
    private String redirectUrl;
    private SmsConfiguration smsConfig;
    private SmtpConfiguration smtpConfig;
    private TriplesecAdmin admin;
    
    
    public Class getHomePage()
    {
        return WizardPage.class;
    }


    protected void init()
    {
        getMarkupSettings().setStripWicketTags( true );

        realm = getWicketServlet().getInitParameter( "realm" );
        presentationBaseUrl = getWicketServlet().getInitParameter( "presentationBaseUrl" );
        redirectUrl = getWicketServlet().getInitParameter( REDIRECT_URL_PARAM );
        
        initSmsConfiguration();
        initSmtpConfiguration();
        initAdminApi();
    }


    private void initAdminApi()
    {
        // -------------------------------------------------------------------
        // Get LDAP connection init parameters 
        // -------------------------------------------------------------------

        String ldapHost = getWicketServlet().getInitParameter( LDAP_HOST );
        String ldapPrincipalDn = getWicketServlet().getInitParameter( LDAP_PRINCIPAL_DN );
        String ldapCredentials = getWicketServlet().getInitParameter( LDAP_CREDENTIALS );
        String ldapBaseDn = getWicketServlet().getInitParameter( LDAP_BASE_DN );
        int ldapPort = Integer.parseInt( getWicketServlet().getInitParameter( LDAP_PORT ) );
        
        // -------------------------------------------------------------------
        // Assemble connection properties for Admin API
        // -------------------------------------------------------------------
        
        Properties env = new Properties();
        env.put( DaoFactory.IMPLEMENTATION_CLASS, LdapDaoFactory.class.getName() );
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        StringBuffer buf = new StringBuffer();
        
        buf.append( "ldap" );
        buf.append( "://" ).append( ldapHost ).append( ":" ).append( ldapPort );
        buf.append( "/" ).append( ldapBaseDn );
        env.put( Context.PROVIDER_URL, buf.toString() );
        env.put( Context.SECURITY_PRINCIPAL, ldapPrincipalDn );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, ldapCredentials );
        
        try
        {
            admin = new TriplesecAdmin( env );
        }
        catch ( Throwable t )
        {
            getWicketServlet().log( "Failed to connect to triplesec server", t );
        }
    }


    /**
     * Intializes the SMS settings from web.xml needed to send messages.
     */
    private void initSmsConfiguration()
    {
        smsConfig = new SmsConfiguration();
        smsConfig.setSmsAccountName( getWicketServlet().getInitParameter( SMS_ACCOUNT_PARAM ) );
        smsConfig.setSmsUsername( getWicketServlet().getInitParameter( SMS_USERNAME_PARAM ) );
        smsConfig.setSmsPassword( getWicketServlet().getInitParameter( SMS_PASSWORD_PARAM ) );
        smsConfig.setSmsTransportUrl( getWicketServlet().getInitParameter( SMS_TRANSPORT_URL_PARAM ) );
    }
    
    
    /**
     * Initializes the mail server settings from web.xml to send messages.
     */
    private void initSmtpConfiguration()
    {
        smtpConfig = new SmtpConfiguration();
        smtpConfig.setSmtpHost( getWicketServlet().getInitParameter( SMTP_HOST_PARAM ) );
        smtpConfig.setSmtpFrom( getWicketServlet().getInitParameter( SMTP_FROM_PARAM ) );
        smtpConfig.setSmtpSubject( getWicketServlet().getInitParameter( SMTP_SUBJECT_PARAM ) );
        if ( getWicketServlet().getInitParameter( SMTP_USERNAME_PARAM ) == null )
        {
            smtpConfig.setSmtpAuthenticate( false );
        }
        else
        {
            smtpConfig.setSmtpAuthenticate( true );
            smtpConfig.setSmtpUsername( getWicketServlet().getInitParameter( SMTP_USERNAME_PARAM ) );
            smtpConfig.setSmtpPassword( getWicketServlet().getInitParameter( SMTP_PASSWORD_PARAM ) );
        }
    }


    public void setRealm( String realm )
    {
        this.realm = realm;
    }


    public String getRealm()
    {
        return realm;
    }

    
    public String getRedirectUrl()
    {
        return redirectUrl;
    }
    

    public void setPresentationBaseUrl( String presentationBaseUrl )
    {
        this.presentationBaseUrl = presentationBaseUrl;
    }


    public String getPresentationBaseUrl()
    {
        return presentationBaseUrl;
    }


    public void setAdmin( TriplesecAdmin admin )
    {
        this.admin = admin;
    }


    public TriplesecAdmin getAdmin()
    {
        return admin;
    }


    public void setSmsConfig( SmsConfiguration smsConfig )
    {
        this.smsConfig = smsConfig;
    }


    public SmsConfiguration getSmsConfig()
    {
        return smsConfig;
    }


    public void setSmtpConfig( SmtpConfiguration smtpConfig )
    {
        this.smtpConfig = smtpConfig;
    }


    public SmtpConfiguration getSmtpConfig()
    {
        return smtpConfig;
    }
}
