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
package org.safehaus.triplesec.registration.view.panels;

 
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.directory.shared.ldap.util.StringTools;
import org.safehaus.sms.Carrier;
import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.HauskeysUser;
import org.safehaus.triplesec.admin.HauskeysUserModifier;
import org.safehaus.triplesec.admin.TriplesecAdmin;
import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;
import org.safehaus.triplesec.registration.model.RegistrationInfo;
import org.safehaus.triplesec.registration.view.TriplesecRegistrationApplication;

import com.sun.mail.smtp.SMTPTransport;

import wicket.model.IModel;
import wicket.model.Model;
import wicket.markup.html.form.Button;
import wicket.markup.html.link.ExternalLink;
import wicket.markup.html.basic.Label;
import wicket.Component;


/**
 *
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class WizardPanelFinish extends WizardPanel
{
    private static final long serialVersionUID = 1L;
    private String message = "Click the Done button to complete registration.";
    private ExternalLink link = null;

    public WizardPanelFinish( String id, IModel model )
    {
        super( id, model, "Finished!" );

        TriplesecRegistrationApplication app = ( TriplesecRegistrationApplication ) getApplication();
        StringBuffer url = new StringBuffer();
        
        if ( app.getRedirectUrl() != null && ! app.getRedirectUrl().equals( "undefined" ) )
        {
            url.append( app.getRedirectUrl() );
        }
        else
        {
            url.append( app.getPresentationBaseUrl() ).append( "/demo" );
        }
        
        link = new ExternalLink( "link", url.toString(), "here." );
        link.setVisible( false );
        getForm().add( link );
        
        getForm().add( new Label( "message", new Model()
        {
            private static final long serialVersionUID = 5029629236706738579L;

            public Object getObject(Component component)
            {
                return message;
            }
        }
        ) );
    }


    protected Button newNextButton( String id )
    {
        Button done = new Button( id )
        {
            private static final long serialVersionUID = -2684017282683778591L;


            protected void onSubmit()
            {
                // register the new user account here
                RegistrationInfo info = ( RegistrationInfo ) getForm().getModelObject();
                TriplesecRegistrationApplication app = ( TriplesecRegistrationApplication ) getApplication();
                TriplesecAdmin admin = app.getAdmin();
                
                // -----------------------------------------------------------
                // create the user entry
                // -----------------------------------------------------------

                String activationKey = null;
                
                try
                {
                    activationKey = admin.getRandomUniqueActivationKey();
                }
                catch ( DataAccessException e1 )
                {
                    message = "Registration for user " + info.getUsername() 
                    + " failed. Notifications will NOT be delivered.";
                    hideButtons();
                    error( "Registration failed!" );
                    return;
                }
                
                HauskeysUserModifier modifier = admin
                    .newHauskeysUser( info.getUsername(), info.getFirstName(), info.getLastName(), info.getPassword() )
                    .setEmail( info.getEmail() ).setLabel( info.getMidletName() )
                    .setMidletName( info.getMidletName() ).setMobile( info.getMobile() )
                    .setMobileCarrier( info.getMobileCarrier() ).setNotifyBy( info.getDeploymentMechanism() )
                    .setPassword( info.getPassword() ).setRealm( app.getRealm() ).setTokenPin( info.getTokenPin() )
                    .setActivationKey( activationKey ).setSecret( StringTools.utf8ToString( getRandomSecret() ) )
                    .setMovingFactor( getRandomFactor() ).setAddress1( info.getAddress1() )
                    .setAddress2( info.getAddress2() ).setCity( info.getCity() ).setCountry( info.getCountry() )
                    .setStateProvRegion( info.getStateProvRegion() ).setZipPostalCode( info.getZipPostalCode() );
                
                HauskeysUser user;
                try
                {
                    user = modifier.add();
                }
                catch ( DataAccessException e )
                {
                    message = "Registration for user " + info.getUsername() 
                        + " failed. Notifications will NOT be delivered.";
                    hideButtons();
                    error( "Registration failed!" );
                    return;
                }
                
                // -----------------------------------------------------------
                // Add a profile for the user to the demo app if it exists
                // -----------------------------------------------------------
                
                Application demoApp = null;
                try
                {
                    demoApp = admin.getApplication( "demo" );
                }
                catch ( DataAccessException e )
                {
                }
                
                if ( demoApp != null )
                {
                    try
                    {
                        demoApp.modifier().newProfile( info.getUsername(), info.getUsername() ).add();
                    }
                    catch ( DataAccessException e )
                    {
                        message = "Triplesec user " + info.getUsername() 
                            + " has been registered but could not create demo account profile. " 
                            + "Notifications will still be delivered.";
                        hideButtons();
                        warn( "Registration completed but with warnings." );
                        return;
                    }
                }

                // -----------------------------------------------------------
                // Send notifications
                // -----------------------------------------------------------
                
                try
                {
                    if ( info.getDeploymentMechanism().equals( "email" ) )
                    {
                        sendEmail( user );
                    }
                    else
                    {
                        sendSms( user );
                    }
                }
                catch ( Exception e )
                {
                    message = "Triplesec user " + info.getUsername() 
                        + " has been registered. However notifications may not " +
                                "delivered due to some messaging failures.";
                    hideButtons();
                    warn( "Registration completed with warnings." );
                    return;
                }
                
                message = "Triplesec user " + info.getUsername() 
                    + " has been registered. Notifications will be delivered.  " +
                            "To use/test your account follow this link ";
                link.setVisible( true );
                hideButtons();
                info( "Registration completed successfully." );
            }
        };
        done.setModel( new Model( "Done" ) );
        return done;
    }

    
    void sendSms( HauskeysUser user ) throws IOException
    {
        TriplesecRegistrationApplication app = ( TriplesecRegistrationApplication ) getApplication();
        SmsConfiguration smsConfig = app.getSmsConfig();
        HttpClient client = new HttpClient();
        HttpMethod method = new PostMethod( smsConfig.getSmsTransportUrl() );
        int carrierCode = Carrier.getCarrierCode( user.getMobileCarrier() );
        String mobile = user.getMobile();
        
        if ( mobile.startsWith( "+" ) )
        {
            mobile = mobile.substring( 1 );
        }
        
        if ( mobile.startsWith( "1" ) )
        {
            mobile = mobile.substring( 1 );
            if ( mobile.length() < 10 )
            {
                throw new RuntimeException( "mobile phone number was not 10 digits in length" );
            }
        }
        
        StringBuffer buf = new StringBuffer();
        buf.append( app.getPresentationBaseUrl() );
        buf.append( "/activation/" );
        buf.append( user.getActivationKey() );
        buf.append( "/HausKeys.jar" );
        
        NameValuePair[] params = new NameValuePair[] {
            new NameValuePair( "Carrier", String.valueOf( carrierCode ) ),
            new NameValuePair( "UID", smsConfig.getSmsUsername() ),
            new NameValuePair( "PWD", smsConfig.getSmsPassword() ),
            new NameValuePair( "Campaign", smsConfig.getSmsAccountName() ),
            new NameValuePair( "CellNumber", mobile ),
            new NameValuePair( "msg", buf.toString() )
        };
        method.setQueryString( params );
        client.executeMethod( method );
    }


    void sendEmail( HauskeysUser user ) throws MessagingException
    {
        TriplesecRegistrationApplication app = ( TriplesecRegistrationApplication ) getApplication();
        SmtpConfiguration smtpConfig = app.getSmtpConfig();
        Properties props = new Properties();
        props.setProperty( "mail.smtp.host", smtpConfig.getSmtpHost() );
        Session session = Session.getInstance( props, null );

        MimeMessage mimeMsg = new MimeMessage( session );
        if ( smtpConfig.getSmtpFrom() != null )
        {
            mimeMsg.setFrom( new InternetAddress( smtpConfig.getSmtpFrom() ) );
        }
        else
        {
            mimeMsg.setFrom();
        }

        StringBuffer buf = new StringBuffer();
        buf.append( app.getPresentationBaseUrl() );
        buf.append( "/activation/" );
        buf.append( user.getActivationKey() );
        buf.append( "/HausKeys.jar" );
        
        mimeMsg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( user.getEmail(), false ) );
        mimeMsg.setSubject( smtpConfig.getSmtpSubject() );
        mimeMsg.setText( buf.toString() );
        mimeMsg.setHeader( "X-Mailer", "triplesec-acivation" );
        mimeMsg.setSentDate( new Date() );
        SMTPTransport transport = ( SMTPTransport ) session.getTransport( "smtp" );
        if ( smtpConfig.isSmtpAuthenticate() )
        {
            transport.connect( smtpConfig.getSmtpHost(), smtpConfig.getSmtpUsername(), smtpConfig.getSmtpPassword() );
        }
        else
        {
            transport.connect();
        }
        transport.sendMessage( mimeMsg, mimeMsg.getAllRecipients() );
        app.getWicketServlet().log( "mail server response: " + transport.getLastServerResponse() );
    }
    

    private String getRandomFactor()
    {
        return String.valueOf( RandomUtils.nextLong() );
    }


    private byte[] getRandomSecret()
    {
        // max length 16, min length 8 bytes
        int length = 8 + RandomUtils.nextInt() % 8;
        return RandomStringUtils.random( length ).getBytes();
    }
}
