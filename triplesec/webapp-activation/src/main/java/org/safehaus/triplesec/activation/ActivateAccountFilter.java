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


import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.safehaus.otp.HotpAttributes;
import org.safehaus.otp.HotpAttributesCipher;
import org.safehaus.triplesec.configuration.ActivationConfiguration;
import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The account activation filter activates user accounts.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ActivateAccountFilter implements Filter, Runnable
{
	private static final int MIN_NOTIFICATION_AGE = 15000;
    private static final String SMTP_PASSWORD_PARAM = "smtpPassword";
    private static final String SMTP_USERNAME_PARAM = "smtpUsername";
    private static final String SMTP_SUBJECT_PARAM = "smtpSubject";
    private static final String SMTP_FROM_PARAM = "smtpFrom";
    private static final String SMTP_HOST_PARAM = "smtpHost";
    private static final String SMS_TRANSPORT_URL_PARAM = "smsTransportUrl";
    private static final String SMS_PASSWORD_PARAM = "smsPassword";
    private static final String SMS_USERNAME_PARAM = "smsUsername";
    private static final String SMS_ACCOUNT_PARAM = "smsAccountName";

    private static final String MIDLET_NAME_ATTRIBUTE_PARAM = "midletNameAttribute";
    private static final String OTP_LENGTH_PARAM = "otpLength";
    private static final String ENABLE_DECOY_MIDLET_PARAM = "enableDecoyMidlet";
    private static final String PRESENTATION_BASE_URL_PARAM = "presentationBaseUrl";
    
    private static final Logger log = LoggerFactory.getLogger( ActivateAccountFilter.class );
	private static final String DEFAULT_MIDLETNAME = "HausKeys";
	
	private LdapContext ctx;
	private File baseDir;
    private SmsConfiguration smsConfig = null;
	private SmtpConfiguration smtpConfig = null;
	private ActivationConfiguration actConfig = null;
    private NotificationQueue msgQueue = new NotificationQueue();
    private Thread notifierThread;
    private boolean keepRunning = true;
    
    
    // -----------------------------------------------------------------------
    // Filter Initialization Methods
    // -----------------------------------------------------------------------

    
    /**
     * Initializes the LDAP context using security settings from web.xml.
     * 
     * @param config the filter's configuration
     */
    private void initLdapContext( FilterConfig config ) throws ServletException
    {
        String ldapHost = config.getInitParameter( "ldapHost" );
        String ldapPrincipalDn = config.getInitParameter( "ldapPrincipalDn" );
        String ldapCredentials = config.getInitParameter( "ldapCredentials" );
        String ldapBaseDn = config.getInitParameter( "ldapBaseDn" );
        int ldapPort = Integer.parseInt( config.getInitParameter( "ldapPort" ) );

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );

        // calculate the provider url from components
        StringBuffer buf = new StringBuffer();
        buf.append( "ldap://" ).append( ldapHost ).append( ":" ).append( ldapPort );
        buf.append( "/" ).append( ldapBaseDn );
        env.put( Context.PROVIDER_URL, buf.toString() );
        
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, ldapCredentials );
        env.put( Context.SECURITY_PRINCIPAL, ldapPrincipalDn );

        try
        {
            ctx = ( LdapContext ) new InitialLdapContext( env, null ).lookup( "" );
        }
        catch ( NamingException e )
        {
            log.error( "Failed to initialize DirContext for LDAP service.", e );
        }
    }
    
    
    /**
     * Intializes the SMS settings from web.xml needed to send messages.
     * 
     * @param config the filter's configuration
     */
    private void initSmsConfiguration( FilterConfig config )
    {
        smsConfig = new SmsConfiguration();
        smsConfig.setSmsAccountName( config.getInitParameter( SMS_ACCOUNT_PARAM ) );
        smsConfig.setSmsUsername( config.getInitParameter( SMS_USERNAME_PARAM ) );
        smsConfig.setSmsPassword( config.getInitParameter( SMS_PASSWORD_PARAM ) );
        smsConfig.setSmsTransportUrl( config.getInitParameter( SMS_TRANSPORT_URL_PARAM ) );
    }
    
    
    /**
     * Initializes the mail server settings from web.xml to send messages.
     * 
     * @param config the filter's configuration
     */
    private void initSmtpConfiguration( FilterConfig config )
    {
        smtpConfig = new SmtpConfiguration();
        smtpConfig.setSmtpHost( config.getInitParameter( SMTP_HOST_PARAM ) );
        smtpConfig.setSmtpFrom( config.getInitParameter( SMTP_FROM_PARAM ) );
        smtpConfig.setSmtpSubject( config.getInitParameter( SMTP_SUBJECT_PARAM ) );
        if ( config.getInitParameter( SMTP_USERNAME_PARAM ) == null )
        {
            smtpConfig.setSmtpAuthenticate( false );
        }
        else
        {
            smtpConfig.setSmtpAuthenticate( true );
            smtpConfig.setSmtpUsername( config.getInitParameter( SMTP_USERNAME_PARAM ) );
            smtpConfig.setSmtpPassword( config.getInitParameter( SMTP_PASSWORD_PARAM ) );
        }
    }
    
    
    /**
     * Initializes activation behavior settings from the web.xml to send messages.
     * 
     * @param config the filter's configuration
     */
    private void initActivationConfiguration( FilterConfig config )
    {
        actConfig = new ActivationConfiguration();
        
        String presentationBaseUrl = config.getInitParameter( PRESENTATION_BASE_URL_PARAM );
        if ( presentationBaseUrl != null && ! presentationBaseUrl.equals( "undefined" ) )
        {
            actConfig.setActivationBaseUrl( presentationBaseUrl + "/activation" );
        }
        
        if ( config.getInitParameter( ENABLE_DECOY_MIDLET_PARAM ) != null )
        {
            String val = config.getInitParameter( ENABLE_DECOY_MIDLET_PARAM );
            actConfig.setEnableDecoyMidlet( val.equals( "1" ) || val.equalsIgnoreCase( "true" ) );
        }
        
        if ( config.getInitParameter( OTP_LENGTH_PARAM ) != null )
        {
            actConfig.setOtpLength( Integer.parseInt( config.getInitParameter( OTP_LENGTH_PARAM ) ) );
        }
        
        if ( config.getInitParameter( MIDLET_NAME_ATTRIBUTE_PARAM ) != null )
        {
            actConfig.setMidletNameAttribute( config.getInitParameter( MIDLET_NAME_ATTRIBUTE_PARAM ) );
        }
    }
    
    
    /**
     * Filter's main init method called by the servlet container.
     * 
     * @param config the filter's configuration
     */
	public void init( FilterConfig config ) throws ServletException 
	{
        initLdapContext( config );
        initSmsConfiguration( config );
        initSmtpConfiguration( config );
        initActivationConfiguration( config );
        
        try 
        {
			baseDir = new File( config.getServletContext().getRealPath( "" ) ).getCanonicalFile();
		} 
        catch ( IOException e ) 
        {
        	log.error( "Could not get cannonical file for base path", e );
        	throw new ServletException( e );
		}
        
        notifierThread = new Thread( this, "notifier" );
        notifierThread.start();
	}

    
    // -----------------------------------------------------------------------
    // Main Filter Processing Method and Helpers
    // -----------------------------------------------------------------------

    
    /**
     * The following sequence of events are handed by this filter:
     * <ol>
     *   <li>Client receives SMS and follows link to a URL for Jad or Jar under this filter.</li>
     *   <li>Servlet filter constructs Jad or midlet Jar for client request.</li>
     *   <li>
     *      If a Jar was requested an SMS message is sent to the client with an a message 
     *      to activate the account and an activation URL.  The activation URL is the same
     *      as the first URL except the activation code is prefixed with an 'a' for activate.
     *   </li>
     *   <li>Client receives 2nd activation SMS and navigates to the embedded activation URL.</li>
     *   <li>Filter then destroys the activation directory and activates the account.</li>
     *   <li>A third final SMS is sent to notify the client that the account has been activated.</li>
     * </ol>
     * 
     */
	public void doFilter( ServletRequest sreq, ServletResponse sresp, FilterChain chain ) 
        throws IOException, ServletException 
	{
		HttpServletRequest req = ( HttpServletRequest ) sreq;
		HttpServletResponse resp = ( HttpServletResponse ) sresp;
		
		String activationKey = ActivationUtils.getActivationKey( req.getRequestURI() );
        
        if ( req.getRequestURI().endsWith( "activate.txt" ) || 
             req.getRequestURI().endsWith( "activate.wml" ) ||
             req.getRequestURI().endsWith( "activate.html" ) )
        {
            processActivation( activationKey, req, resp, chain );
        }
        else
        {
            processDownload( activationKey, req, resp, chain );
        }
    } 

    
    private void sendResponse( String msg, HttpServletRequest req, HttpServletResponse resp )
        throws IOException, ServletException 
    {
        String contentType = null;
        StringBuffer content = new StringBuffer();
        
        if ( req.getRequestURI().endsWith( ".txt" ) )
        {
            contentType = "text/plain";
            content.append( msg );
        }
        else if ( req.getRequestURI().endsWith( ".html" ) )
        {
            contentType = "text/html";
            content.append( "<html><body><p>" ).append( msg ).append( "</p></body></html>" );
        }
        else if ( req.getRequestURI().endsWith( ".wml" ) )
        {
            contentType = "text/vnd.wap.wml";
            content.append( "<wml> <card id=\"main\" title=\"Safehaus\"><p align=\"left\">" );
            content.append( msg ).append( "</p></card></wml>" );
        }
        else
        {
            String errmsg = "Unrecognized file extension in request URI: " + req.getRequestURI();
            log.error( errmsg );
            throw new IllegalStateException( errmsg );
        }
        
        resp.setContentType( contentType );
        resp.getWriter().write( content.toString() );
        resp.getWriter().flush();
        resp.getWriter().close();
        return;
    }
    
    
    private void processActivation( String activationKey, HttpServletRequest req, HttpServletResponse resp, FilterChain chain  )
        throws IOException, ServletException 
    {
        // -------------------------------------------------------------------
        // Lookup the account associated with this activation key
        // -------------------------------------------------------------------
        
        HotpAccount account = null;
        try 
        {
            account = ActivationUtils.getAccountHotpAccount( ( LdapContext ) ctx.lookup( "ou=users" ), activationKey );
        } 
        catch ( NamingException e ) 
        {
            String message = "Failed to access safehaus account information for activation key: " + activationKey;
            log.error( message, e );
            sendResponse( message, req, resp );
            return;
        }
        
        // -------------------------------------------------------------------
        // Delete the safehausActivationKey attribute for the user to activate the account
        // -------------------------------------------------------------------

        BasicAttributes mods = new BasicAttributes( "safehausActivationKey", null, true );
        try
        {
            ctx.modifyAttributes( account.getName() + ",ou=users", DirContext.REMOVE_ATTRIBUTE, mods );
        }
        catch ( NamingException e )
        {
            String errmsg = "Failed to activate account for " + account.getName();
            log.error( errmsg, e );
            sendResponse( errmsg, req, resp );
            return;
        }
        
        // -------------------------------------------------------------------
        // Delete the directory with jar that was generated for this key
        // -------------------------------------------------------------------

        File activationDirectory = new File( baseDir, activationKey );
        ActivationUtils.deleteActivationDirectory( activationDirectory );
        
        // -------------------------------------------------------------------
        // Send informational message back confirming activation using 
        // account's notification medium: sms or email.
        // -------------------------------------------------------------------

        Notification msg = null;
        if ( account.isDeliveryMethodSms() )
        {
            msg = new SmsNotification( account.getCellularNumber(), account.getCarrier(), activationKey, 
                    "Your account for realm " + account.getRealm() + " has been activated!" );
            try
            {
                NotificationUtil.send( account.getRealm(), msg, smsConfig );
            }
            catch ( Exception e )
            {
                log.error( "Failed to send sms message: " + msg, e );
            }
        }
        else
        {
            msg = new EmailNotification( account.getEmailAddress(), activationKey, 
                "Your account for realm " + account.getRealm() + " has been activated!" );
            try
            {
                NotificationUtil.send( account.getRealm(), msg, smtpConfig );
            }
            catch ( Exception e )
            {
                log.error( "Failed to send email message: " + msg, e );
            }
        }
        
        // -------------------------------------------------------------------
        // Return a text response back as well (should work for all clients)
        // -------------------------------------------------------------------

        sendResponse( "Your account for realm " + account.getRealm() + " has been activated!", req, resp );
        return;
    }
    

    private void processDownload( String activationKey, HttpServletRequest req, 
                                  HttpServletResponse resp, FilterChain chain  ) throws IOException, ServletException 
    {
        if ( req.getRequestURI().indexOf( ".jar" ) == -1 )
        {
            String message = "No such resource associated with the activation key: " + activationKey;
            log.info( message );
            resp.setContentType( "text/plain" );
            resp.getWriter().write( message );
            resp.getWriter().close();
            return;
        }

        // -------------------------------------------------------------------
        // Lookup the account associated with this activation key
        // -------------------------------------------------------------------
        
        HotpAccount account = null;
        try 
        {
            account = ActivationUtils.getAccountHotpAccount( ( LdapContext ) ctx.lookup( "ou=users" ), activationKey );
        } 
        catch ( NamingException e ) 
        {
            String message = "Failed to access safehaus account information for activation key: " + activationKey;
            log.error( message, e );
            resp.setContentType( "text/plain" );
            resp.getWriter().write( message );
            resp.getWriter().close();
            return;
        }
        
        // -------------------------------------------------------------------
        // If the activation directory exists that means the jar is there 
        // already and this is not the 1st request so far for the jar file 
        // so we want to expedite the delivery of the ActivationMessage for 
        // this activation.
        // -------------------------------------------------------------------
        
        File activationDirectory = new File( baseDir, activationKey );
        if ( activationDirectory.exists() )
        {
            chain.doFilter( req, resp );

            // Dequeue the message if it has not been delivered already and send
            // instead of waiting for the message to be delivered after the delay   
            Notification msg = null;
            synchronized ( msgQueue )
            {
                msg = msgQueue.dequeue( activationKey );
            }
            if ( msg != null )
            {
                try
                {
                    NotificationUtil.send( account.getRealm(), msg, smsConfig );
                }
                catch ( Exception e )
                {
                    log.error( "Failed to deliver message: " + msg, e );
                }
            }
            
            return;
        }
        
        // -------------------------------------------------------------------
        // First jar request builds the midlet
        // -------------------------------------------------------------------

        ActivationUtils.createActivationDirectory( activationDirectory );
		String hotpInfo = null;
		String midletName = DEFAULT_MIDLETNAME;
        if ( ! actConfig.isEnableDecoyMidlet() && account == null )
        {
            String message = "No such safehaus account with activation key: " + activationKey;
            log.info( message );
            resp.setContentType( "text/plain" );
            resp.getWriter().write( message );
            resp.getWriter().close();
            return;
        }

        if ( account == null )
		{
			hotpInfo = ActivationUtils.getBogusHotpInfo();
		}
		else
		{
            HotpAttributes hotpAttrs = new HotpAttributes( actConfig.getOtpLength(), 
                account.getFactor(), account.getSecret() );
            hotpInfo = HotpAttributesCipher.encrypt( account.getPin(), hotpAttrs );
		}
		
        midletName = account.getMidletName();
		File appSrc = new File( baseDir, "HausKeys.jar" );
		File appDest = new File( activationDirectory, "HausKeys.jar" );
		try 
		{
			ActivationUtils.buildMidlet( midletName, hotpInfo, appSrc, appDest );
		} 
		catch ( Exception e ) 
		{
			String message = "Failed to create midlet jar for activation key " + activationKey;
			log.error( message, e );
			resp.setContentType( "text/plain" );
			resp.getWriter().write( message );
			resp.getWriter().close();
			ActivationUtils.deleteActivationDirectory( activationDirectory );
			return;
		} 

		chain.doFilter( req, resp );

        Notification msg = null;
        StringBuffer content = new StringBuffer();
        if ( actConfig.getActivationBaseUrl() == null || actConfig.getActivationBaseUrl().equals( "undefined" ) )
        {
            content.append( "To activate your new account navigate to the following URL: http://" );
            content.append( req.getServerName() ).append( ":" ).append( req.getServerPort() );
            content.append( "/activation/" ).append( activationKey ).append( "/activate" );
        }
        else 
        {
            content.append( "To activate your new account navigate to the following URL: " );
            content.append( actConfig.getActivationBaseUrl() );
            content.append( "/" ).append( activationKey ).append( "/activate" );
        }
        
        if ( account.isDeliveryMethodSms() )
        {
            content.append( ".wml" );
            msg = new SmsNotification( account.getCellularNumber(), account.getCarrier(), 
                activationKey, content.toString() );
        }
        else
        {
            content.append( ".html" );
            msg = new EmailNotification( account.getEmailAddress(), activationKey, content.toString() );
        }
        
        synchronized ( msgQueue )
        {
            // enqueue the activation request message for the account
            msgQueue.enqueue( msg );
            msgQueue.notifyAll();
        }
	}
	
	
    public void destroy()
    {
        keepRunning = false;
        
        while ( notifierThread.isAlive() )
        {
            synchronized ( msgQueue )
            {
                msgQueue.notifyAll();
            }
            
            try
            {
                notifierThread.join( 250 );
            }
            catch ( InterruptedException e )
            {
                log.error( "encountered failure while waiting for notifier thread to die", e );
            }
        }
    }
    

    public void run()
    {
        while ( keepRunning )
        {
            synchronized ( msgQueue )
            {
                if ( msgQueue.available( MIN_NOTIFICATION_AGE ) )
                {
                    Notification[] msgs = msgQueue.dequeue( MIN_NOTIFICATION_AGE );
                    for ( int ii = 0; ii < msgs.length; ii++ )
                    {
                        if ( msgs[ii] instanceof SmsNotification )
                        {
                            try
                            {
                                NotificationUtil.send( "unknown", msgs[ii], smsConfig );
                            }
                            catch ( Exception e )
                            {
                                log.error( "Failed to send sms notification: " + msgs[ii], e );
                            }
                        }
                        else
                        {
                            try
                            {
                                NotificationUtil.send( "unknown", msgs[ii], smtpConfig );
                            }
                            catch ( Exception e )
                            {
                                log.error( "Failed to send email notification: " + msgs[ii], e );
                            }
                        }
                    }
                }

                long waitMillis = msgQueue.getWaitMillis();
                try
                {
                    if ( waitMillis == Long.MAX_VALUE )
                    {
                        msgQueue.wait();
                    }
                    else if ( waitMillis == 0 )
                    {
                        continue;
                    }
                    else
                    {
                        msgQueue.wait( waitMillis );
                    }
                }
                catch ( InterruptedException e )
                {
                    log.error( "got interrupted exception while attempting to wait for " 
                        + waitMillis + " milliseconds", e );
                }
            }
        }
    }
}
