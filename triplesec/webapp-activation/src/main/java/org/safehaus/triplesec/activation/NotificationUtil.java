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
package org.safehaus.triplesec.activation;


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
import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.smtp.SMTPTransport;


/**
 * Utility methods for sending notifications via sms and email.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class NotificationUtil
{
    // Request parameters for NMSI SMS POST
    private static final String MSG_REQPARAM = "msg";
    private static final String MOBILE_REQPARAM = "CellNumber";
    private static final String ACCOUNTNAME_REQPARAM = "Campaign";
    private static final String PASSWORD_REQPARAM = "PWD";
    private static final String USERNAME_REQPARAM = "UID";
    private static final String CARRIERCODE_REQPARAM = "Carrier";
    
    /** logger for this class */
    private static final Logger log = LoggerFactory.getLogger( ActivationUtils.class );


    static void send( String realm, Notification msg, Object config ) throws Exception
    {
        if ( msg instanceof SmsNotification )
        {
            sendSms( realm, ( SmsNotification ) msg, ( SmsConfiguration ) config );
        }
        else
        {
            sendEmail( realm, ( EmailNotification ) msg, ( SmtpConfiguration ) config );
        }
    }
    
    
    static void sendSms( String realm, SmsNotification msg, SmsConfiguration smsConfig ) throws IOException
    {
        HttpClient client = new HttpClient();
        HttpMethod method = new PostMethod( smsConfig.getSmsTransportUrl() );
        int carrierCode = msg.getCarrier().getValue();
        String mobile = msg.getCellularNumber();
        
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
        
        NameValuePair[] params = new NameValuePair[] {
            new NameValuePair( CARRIERCODE_REQPARAM, String.valueOf( carrierCode ) ),
            new NameValuePair( USERNAME_REQPARAM, smsConfig.getSmsUsername() ),
            new NameValuePair( PASSWORD_REQPARAM, smsConfig.getSmsPassword() ),
            new NameValuePair( ACCOUNTNAME_REQPARAM, smsConfig.getSmsAccountName() ),
            new NameValuePair( MOBILE_REQPARAM, mobile ),
            new NameValuePair( MSG_REQPARAM, msg.getMessage() )
        };
        method.setQueryString( params );
        client.executeMethod( method );
    }


    static void sendEmail( String realm, EmailNotification msg, SmtpConfiguration smtpConfig ) 
        throws MessagingException
    {
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

        mimeMsg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( msg.getEmailAddress(), false ) );
        mimeMsg.setSubject( smtpConfig.getSmtpSubject() );
        mimeMsg.setText( msg.getMessage() );
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
        log.info( "mail server response: " + transport.getLastServerResponse() );
    }
}
