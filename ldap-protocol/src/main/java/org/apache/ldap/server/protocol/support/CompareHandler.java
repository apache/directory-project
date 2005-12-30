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
import org.apache.ldap.common.message.CompareRequest;
import org.apache.ldap.common.message.CompareResponse;
import org.apache.ldap.common.message.CompareResponseImpl;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.name.LdapName;
import org.apache.ldap.common.util.ExceptionUtils;
import org.apache.ldap.server.jndi.ServerLdapContext;
import org.apache.ldap.server.protocol.SessionRegistry;
import org.apache.mina.common.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A single reply handler for {@link org.apache.ldap.common.message.CompareRequest}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class CompareHandler implements MessageHandler
{
    private static final Logger log = LoggerFactory.getLogger( CompareHandler.class );

    public void messageReceived( IoSession session, Object request )
    {
        CompareRequest req = ( CompareRequest ) request;
        CompareResponse resp = new CompareResponseImpl( req.getMessageId() );
        resp.setLdapResult( new LdapResultImpl( resp ) );

        try
        {
            LdapContext ctx = SessionRegistry.getSingleton().getLdapContext( session, null, true );
            ServerLdapContext newCtx = ( ServerLdapContext ) ctx.lookup( "" );
            LdapName name = new LdapName( req.getName() );
            
            if ( newCtx.compare( name, req.getAttributeId(), req.getAssertionValue() ) )
            {
                resp.getLdapResult().setResultCode( ResultCodeEnum.COMPARETRUE );
            }
            else
            {
                resp.getLdapResult().setResultCode( ResultCodeEnum.COMPAREFALSE );
            }
        }
        catch ( Exception e )
        {
            String msg = "failed to compare entry " + req.getName();

            if ( log.isDebugEnabled() )
            {
                msg += ":\n" + ExceptionUtils.getStackTrace( e );
            }

            ResultCodeEnum code;
            
            if ( e instanceof LdapException )
            {
                code = ( ( LdapException ) e ).getResultCode() ;
            }
            else
            {
                code = ResultCodeEnum.getBestEstimate( e, req.getType() );
            }

            resp.getLdapResult().setResultCode( code );
            resp.getLdapResult().setErrorMessage( msg );

            if ( e instanceof NamingException )
            {
            	NamingException ne = (NamingException)e;
            	
                if ( ( ne.getResolvedName() != null ) &&
                        ( ( code == ResultCodeEnum.NOSUCHOBJECT ) ||
                          ( code == ResultCodeEnum.ALIASPROBLEM ) ||
                          ( code == ResultCodeEnum.INVALIDDNSYNTAX ) ||
                          ( code == ResultCodeEnum.ALIASDEREFERENCINGPROBLEM ) ) )
                {
                    resp.getLdapResult().setMatchedDn( ne.getResolvedName().toString() );
                }
            }

            session.write( resp );
            return;
        }

        resp.getLdapResult().setMatchedDn( req.getName() );
        session.write( resp );
    }
}
