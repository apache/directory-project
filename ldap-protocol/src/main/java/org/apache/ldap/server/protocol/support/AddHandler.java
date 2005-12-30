/*
 *   Copyright 2004 The Apache Software Foundation
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
package org.apache.ldap.server.protocol.support;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.ldap.common.exception.LdapException;
import org.apache.ldap.common.message.AddRequest;
import org.apache.ldap.common.message.AddResponse;
import org.apache.ldap.common.message.AddResponseImpl;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.util.ExceptionUtils;
import org.apache.ldap.server.protocol.SessionRegistry;
import org.apache.mina.common.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A single reply handler for {@link org.apache.ldap.common.message.AddRequest}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AddHandler implements MessageHandler
{
    private static final Logger log = LoggerFactory.getLogger( AddHandler.class );

    public void messageReceived( IoSession session, Object request )
    {
        AddRequest req = ( AddRequest ) request;
        
        if ( log.isDebugEnabled() )
        {
            log.debug( "Received a Add message : " + req.toString() );
        }

        AddResponse resp = new AddResponseImpl( req.getMessageId() );
        resp.setLdapResult( new LdapResultImpl( resp ) );

        try
        {
            LdapContext ctx = SessionRegistry.getSingleton().getLdapContext( session, null, true );
            ctx.createSubcontext( req.getEntry(), req.getAttributes() );
        }
        catch( NamingException e )
        {
            String msg = "failed to add entry " + req.getEntry();

            if ( log.isDebugEnabled() )
            {
                msg += ":\n" + ExceptionUtils.getStackTrace( e );
            }

            ResultCodeEnum code;

            if ( e instanceof LdapException )
            {
                code = ( ( LdapException ) e ).getResultCode();
            }
            else
            {
                code = ResultCodeEnum.getBestEstimate( e, req.getType() );
            }

            resp.getLdapResult().setResultCode( code );
            resp.getLdapResult().setErrorMessage( msg );
            
            if ( ( e.getResolvedName() != null ) &&
                    ( ( code == ResultCodeEnum.NOSUCHOBJECT ) ||
                      ( code == ResultCodeEnum.ALIASPROBLEM ) ||
                      ( code == ResultCodeEnum.INVALIDDNSYNTAX ) ||
                      ( code == ResultCodeEnum.ALIASDEREFERENCINGPROBLEM ) ) )
            {
                resp.getLdapResult().setMatchedDn( e.getResolvedName().toString() );
            }

            session.write( resp );
            return;
        }

        resp.getLdapResult().setResultCode( ResultCodeEnum.SUCCESS );
        session.write( resp );
    }
}
