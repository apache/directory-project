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
package org.safehaus.sms.clickatell;


import org.safehaus.sms.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;


/**
 * A session factory for the Clickatell gateway
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ClickatellSmsSessionFactory extends SmsSessionFactory
{
    /** The default wrt keep alives */
    private final static boolean KEEPALIVE_DEFAULT = true;
    /** The default SMS transport type used by this SmsSessionFactory */
    private final static SmsTransportType TRANSPORT_DEFAULT = SmsTransportType.HTTP;

    /** A hash of session identifier strings to SmsSession instances */
    private final HashMap map = new HashMap(3);


    public SmsSession getSmsSession( String sessionId )
    {
        return ( SmsSession ) map.get( sessionId );
    }


    public SmsSession getSmsSession( String user, String password, String applicationId ) throws IOException
    {
        ClickatellSmsSession session = new ClickatellSmsSession();
        session.setApplicationId( applicationId );
        session.setUser( user );
        session.setKeepAliveEnabled( KEEPALIVE_DEFAULT );
        session.setTransport( TRANSPORT_DEFAULT );
        session.setSessionId( authenticate( user, password, applicationId, TRANSPORT_DEFAULT ) );
        return session;
    }


    public SmsSession getSmsSession( String user, String password, String applicationId,
                                     SmsTransportType transport ) throws IOException
    {
        return getSmsSession( user, password, applicationId );
    }


    public SmsSession getSmsSession( String user, String password, String applicationId,
                                     SmsTransportType transport, boolean enableKeepAlive ) throws IOException
    {
        return getSmsSession( user, password, applicationId );
    }


    public SmsSession getSmsSession( String user, String password, String applicationId,
                                     boolean enableKeepAlive ) throws IOException
    {
        return getSmsSession( user, password, applicationId );
    }


    /**
     * Authenticates the user and recovers a session identifier for SmsSession creation.
     *
     * @param user the user account for authenticating
     * @param password the user credentials
     * @param applicationId the applicationId associated with the SmsSession to be created
     * @return the session identifier for the newly created session
     * @throws IOException if there are failures connecting or getting a response
     */
    private String authenticate( String user, String password, String applicationId, SmsTransportType transport ) throws IOException
    {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod( transport.getScheme() + "://api.clickatell.com/"
                + transport.getScheme() + "/auth" );
        NameValuePair[] data =
        {
            new NameValuePair( "api_id", applicationId ),
            new NameValuePair( "user", user ),
            new NameValuePair( "password", password )
        };
        post.setRequestBody( data );
        client.executeMethod( post );
        return post.getResponseBodyAsString();
    }


    /**
     * A Clickatell specific SmsSession.
     */
    class ClickatellSmsSession extends AbstractSmsSession
    {
        protected void setSessionId( String sessionId )
        {
            super.setSessionId( sessionId );
        }


        public boolean invalidate()
        {
            throw new RuntimeException( "Not implemented yet" );
        }


        protected void setTransport( SmsTransportType transport )
        {
            super.setTransport( transport );
        }


        protected void setUser( String user )
        {
            super.setUser( user );
        }


        protected void setApplicationId( String applicationId )
        {
            super.setApplicationId( applicationId );
        }


        protected void setKeepAliveEnabled( boolean isKeepAliveEnabled )
        {
            super.setKeepAliveEnabled( isKeepAliveEnabled );
        }


        public int sendMessage( SmsMessage msg ) throws SmsException, IOException
        {
            String scheme = getTransport().getScheme();
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod( scheme + "://api.clickatell.com/" + scheme + "/sendmsg" );

            if ( ! isKeepAliveEnabled() )
            {
                throw new SmsException( "Without keep alive enabled you must use sendMessage() overload that requires the password" );
            }
            NameValuePair[] data =
            {
                new NameValuePair( "session_id", getSessionId() ),
                new NameValuePair( "to", msg.getDestination() ),
                new NameValuePair( "text", msg.getText() )
            };
            post.setRequestBody( data );
            client.executeMethod( post );
            String response = post.getResponseBodyAsString();

            if ( response.indexOf( "ERR:" ) != -1 )
            {
                int errorCode = Integer.parseInt( response.split( "ERR: " )[1].split( "," )[0] );
                throw new SmsException( errorCode );
            }

            return Integer.parseInt( response.split( "ID: " )[1] );
        }
    }


    int ping( SmsSession session ) throws IOException
    {
        String scheme = session.getTransport().getScheme();
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod( scheme + "://api.clickatell.com/" + scheme + "/ping" );
        NameValuePair[] data = { new NameValuePair( "session_id", session.getSessionId() ) };
        post.setRequestBody( data );
        client.executeMethod( post );
        String response = post.getResponseBodyAsString();

        if ( response.indexOf( "OK:" ) != -1 )
        {
            return 0;
        }

        return Integer.parseInt( response.split( "ERR: " )[1].split( "," )[0] );
    }


    class SessionKeepAlive implements Runnable
    {
        public void run()
        {
            try
            {
                System.out.println( "Waiting 10 minutes before keep alive ping" );
                Thread.currentThread().wait( 600000 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }

            Iterator list = map.values().iterator();
            while ( list.hasNext() )
            {
                int errorCode = 0;
                SmsSession session = ( SmsSession ) list.next();
                try
                {
                    errorCode = ping( session );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }

                System.err.println( "Ping failed: encountered error code of " + errorCode );
            }
        }
    }
}
