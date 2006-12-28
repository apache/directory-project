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
package org.safehaus.sms.smpp;


import org.safehaus.sms.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import ie.omk.smpp.Connection;
import ie.omk.smpp.Address;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.UnbindResp;


/**
 * A session factory that uses the Short Message Peer to Peer (SMPP) protocol
 * to connect to a gateway and exchange SMS messages.  These gateways are referred
 * to as a Short Message Service Center (SMSC).
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class SmppSmsSessionFactory extends SmsSessionFactory
{
    private static final Log log = LogFactory.getLog( SmppSmsSessionFactory.class );
    /** The default wrt keep alives */
    private final static boolean KEEPALIVE_DEFAULT = true;
    /** The default SMS transport type used by this SmsSessionFactory */
    private final static SmsTransportType TRANSPORT_DEFAULT = SmsTransportType.SMPP;

    /** A hash of session identifier strings to SmsSession instances */
    private final HashMap map = new HashMap(3);


    public SmsSession getSmsSession( String sessionId )
    {
        return ( SmsSession ) map.get( sessionId );
    }


    public SmsSession getSmsSession( String user, String password, String applicationId ) throws IOException
    {
        SmppSmsSession session = new SmppSmsSession( connect( user, password ) );
        session.setApplicationId( applicationId );
        session.setUser( user );
        session.setKeepAliveEnabled( KEEPALIVE_DEFAULT );
        session.setTransport( TRANSPORT_DEFAULT );
        session.setSessionId( session.getConnection().toString() );
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


    private boolean disconnect( SmppSmsSession session )
    {
        Connection conn = session.getConnection();

        if ( conn != null && conn.isBound() )
        {
            try
            {
                UnbindResp ubr = conn.unbind();

                if ( ubr != null && ubr.getCommandStatus() == 0 )
                {
                    log.info( "Successfully unbound from the SMSC" );
                }
                else if ( ubr != null )
                {
                    log.warn( "There was an error unbinding." );
                }
            }
            catch ( IOException e )
            {
                log.error( "failed to disconnect", e );
                return false;
            }
        }

        return true;
    }


    private Connection connect( String username, String password )
    {
        Connection conn = null;
        String host = System.getProperty( "org.safehaus.sms.smpp.host", "localhost" );
        int port = Integer.parseInt( System.getProperty( "org.safehaus.sms.smpp.port", "2775" ) );

        try
        {
            conn = new Connection( host, port, true );
        }
        catch ( UnknownHostException uhe )
        {
            log.fatal( "could not connect to host " + host + " on port " + port + " for SMPP connection", uhe );
            System.exit(0);
        }

        boolean retry = false;

        while ( ! retry )
        {
            try
            {
                conn.bind( Connection.TRANSMITTER, username, password, null );

                while ( ! conn.isBound() )
                {
                    try
                    {
                        Thread.sleep( 250 );
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                }

                retry = true;
            }
            catch ( IOException ioe )
            {
                try
                {
                    Thread.currentThread().wait( 60 * 1000 );
                }
                catch ( InterruptedException ie )
                {
                    log.error( "failed to wait on current thread", ie );
                }
            }
        }

        return conn;
    }


    /**
     * A SmppSmsSessionFactory specific SmsSession.
     */
    class SmppSmsSession extends AbstractSmsSession
    {
        public final Connection conn;


        SmppSmsSession( Connection conn )
        {
            this.conn = conn;
        }


        protected void setSessionId( String sessionId )
        {
            super.setSessionId( sessionId );
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
            // Submit a simple message
            SubmitSM sm = null;
            try
            {
                sm = ( SubmitSM ) conn.newInstance(SMPPPacket.SUBMIT_SM );
            }
            catch ( BadCommandIDException e )
            {
                log.error( "Submit command not recognized", e );
                throw new SmsException( e.getMessage() );
            }

            sm.setDestination( new Address(0, 0, msg.getDestination() ) );
            sm.setMessageText( msg.getText() );
            SubmitSMResp smr = ( SubmitSMResp ) conn.sendRequest(sm);

            if ( smr != null )
            {
                log.info( "Submitted message ID: " + smr.getMessageId() );
                return smr.getCommandStatus();
            }

            return 0;
        }


        public Connection getConnection()
        {
            return conn;
        }


        public boolean invalidate()
        {
            return disconnect( this );
        }
    }
}
