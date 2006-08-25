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
package org.apache.directory.server.changepw.service;


import java.net.InetAddress;

import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.server.kerberos.shared.messages.ApplicationRequest;
import org.apache.directory.server.kerberos.shared.messages.components.Authenticator;
import org.apache.directory.server.kerberos.shared.messages.components.Ticket;
import org.apache.directory.server.kerberos.shared.messages.value.HostAddress;
import org.apache.directory.server.kerberos.shared.messages.value.HostAddresses;
import org.apache.directory.server.kerberos.shared.replay.ReplayCache;
import org.apache.directory.server.kerberos.shared.store.PrincipalStore;
import org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntry;
import org.apache.mina.common.IoSession;
import org.apache.mina.handler.chain.IoHandlerCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MonitorContext implements IoHandlerCommand
{
    /** the log for this class */
    private static final Logger log = LoggerFactory.getLogger( MonitorContext.class );

    private String contextKey = "context";

    public void execute( NextCommand next, IoSession session, Object message ) throws Exception
    {
        if ( log.isDebugEnabled() )
        {
            try
            {
                ChangePasswordContext changepwContext = ( ChangePasswordContext ) session.getAttribute( getContextKey() );

                PrincipalStore store = changepwContext.getStore();
                ApplicationRequest authHeader = changepwContext.getAuthHeader();
                Ticket ticket = changepwContext.getTicket();
                ReplayCache replayCache = changepwContext.getReplayCache();
                long clockSkew = changepwContext.getConfig().getClockSkew();

                Authenticator authenticator = changepwContext.getAuthenticator();
                KerberosPrincipal clientPrincipal = authenticator.getClientPrincipal();
                String desiredPassword = changepwContext.getPassword();

                InetAddress clientAddress = changepwContext.getClientAddress();
                HostAddresses clientAddresses = ticket.getClientAddresses();

                boolean caddrContainsSender = false;

                if ( ticket.getClientAddresses() != null )
                {
                    caddrContainsSender = ticket.getClientAddresses().contains( new HostAddress( clientAddress ) );
                }

                StringBuffer sb = new StringBuffer();
                sb.append( "Monitoring context:" );
                sb.append( "\n\t" + "store                  " + store );
                sb.append( "\n\t" + "authHeader             " + authHeader );
                sb.append( "\n\t" + "ticket                 " + ticket );
                sb.append( "\n\t" + "replayCache            " + replayCache );
                sb.append( "\n\t" + "clockSkew              " + clockSkew );
                sb.append( "\n\t" + "clientPrincipal        " + clientPrincipal );
                sb.append( "\n\t" + "desiredPassword        " + desiredPassword );
                sb.append( "\n\t" + "clientAddress          " + clientAddress );
                sb.append( "\n\t" + "clientAddresses        " + clientAddresses );
                sb.append( "\n\t" + "caddr contains sender  " + caddrContainsSender );

                KerberosPrincipal ticketServerPrincipal = ticket.getServerPrincipal();
                PrincipalStoreEntry ticketPrincipal = changepwContext.getServerEntry();

                sb.append( "\n\t" + "principal              " + ticketServerPrincipal );
                sb.append( "\n\t" + "cn                     " + ticketPrincipal.getCommonName() );
                sb.append( "\n\t" + "realm                  " + ticketPrincipal.getRealmName() );
                sb.append( "\n\t" + "principal              " + ticketPrincipal.getPrincipal() );
                sb.append( "\n\t" + "SAM type               " + ticketPrincipal.getSamType() );
                sb.append( "\n\t" + "Key type               " + ticketPrincipal.getEncryptionKey().getKeyType() );
                sb.append( "\n\t" + "Key version            " + ticketPrincipal.getEncryptionKey().getKeyVersion() );

                log.debug( sb.toString() );
            }
            catch ( Exception e )
            {
                // This is a monitor.  No exceptions should bubble up.
                log.error( "Error in context monitor", e );
            }
        }

        next.execute( session, message );
    }


    public String getContextKey()
    {
        return ( this.contextKey );
    }
}
