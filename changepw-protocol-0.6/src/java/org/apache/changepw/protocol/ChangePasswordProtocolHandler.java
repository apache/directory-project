/*
 *   Copyright 2005 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.apache.changepw.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.changepw.ChangePasswordConfiguration;
import org.apache.changepw.messages.ChangePasswordRequest;
import org.apache.changepw.service.ChangePasswordChain;
import org.apache.changepw.service.ChangePasswordContext;
import org.apache.kerberos.store.PrincipalStore;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.protocol.ProtocolHandler;
import org.apache.mina.protocol.ProtocolSession;
import org.apache.protocol.common.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordProtocolHandler implements ProtocolHandler
{
    private static final Logger log = LoggerFactory.getLogger( ChangePasswordProtocolHandler.class );

    private ChangePasswordConfiguration config;
    private PrincipalStore store;

    private Command changepwService;

    public ChangePasswordProtocolHandler( ChangePasswordConfiguration config, PrincipalStore store )
    {
        this.config = config;
        this.store = store;

        changepwService = new ChangePasswordChain();
    }

    public void sessionCreated( ProtocolSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " CREATED" );
        }
    }

    public void sessionOpened( ProtocolSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " OPENED" );
        }
    }

    public void sessionClosed( ProtocolSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " CLOSED" );
        }
    }

    public void sessionIdle( ProtocolSession session, IdleStatus status )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " IDLE(" + status + ")" );
        }
    }

    public void exceptionCaught( ProtocolSession session, Throwable cause )
    {
        log.error( session.getRemoteAddress() + " EXCEPTION", cause );
        session.close();
    }

    public void messageReceived( ProtocolSession session, Object message )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " RCVD: " + message );
        }

        InetAddress clientAddress = ( (InetSocketAddress) session.getRemoteAddress() ).getAddress();
        ChangePasswordRequest request = (ChangePasswordRequest) message;

        try
        {
            ChangePasswordContext changepwContext = new ChangePasswordContext();
            changepwContext.setConfig( config );
            changepwContext.setStore( store );
            changepwContext.setClientAddress( clientAddress );
            changepwContext.setRequest( request );

            changepwService.execute( changepwContext );

            session.write( changepwContext.getReply() );
        }
        catch ( Exception e )
        {
            log.error( e.getMessage() );
        }
    }

    public void messageSent( ProtocolSession session, Object message )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " SENT: " + message );
        }
    }
}
