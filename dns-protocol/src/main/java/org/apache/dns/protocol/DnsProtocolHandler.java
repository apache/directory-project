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

package org.apache.dns.protocol;

import org.apache.dns.DnsConfiguration;
import org.apache.dns.messages.DnsMessage;
import org.apache.dns.service.DnsContext;
import org.apache.dns.service.DomainNameServiceChain;
import org.apache.dns.store.RecordStore;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.protocol.common.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DnsProtocolHandler implements IoHandler
{
    private static final Logger log = LoggerFactory.getLogger( DnsProtocolHandler.class );

    private DnsConfiguration config;
    private RecordStore store;

    private Command dnsService;

    public DnsProtocolHandler( DnsConfiguration config, RecordStore store )
    {
        this.config = config;
        this.store = store;

        dnsService = new DomainNameServiceChain();
    }

    public void sessionCreated( IoSession session ) throws Exception
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " CREATED" );
        }
        
        session.getFilterChain().addFirst(
                "codec",
                new ProtocolCodecFilter( DnsProtocolCodecFactory.getInstance() ) );
    }

    public void sessionOpened( IoSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " OPENED" );
        }
    }

    public void sessionClosed( IoSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " CLOSED" );
        }
    }

    public void sessionIdle( IoSession session, IdleStatus status )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " IDLE(" + status + ")" );
        }
    }

    public void exceptionCaught( IoSession session, Throwable cause )
    {
        log.error( session.getRemoteAddress() + " EXCEPTION", cause );
        session.close();
    }

    public void messageReceived( IoSession session, Object message )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " RCVD: " + message );
        }

        DnsMessage request = (DnsMessage) message;

        try
        {
            DnsContext dnsContext = new DnsContext();
            dnsContext.setConfig( config );
            dnsContext.setStore( store );
            dnsContext.setRequest( request );

            dnsService.execute( dnsContext );

            session.write( dnsContext.getReply() );
        }
        catch ( Exception e )
        {
            log.error( e.getMessage(), e );
        }
    }

    public void messageSent( IoSession session, Object message )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " SENT: " + message );
        }
    }
}
