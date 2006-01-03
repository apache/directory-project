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
import org.apache.ldap.common.message.LdapResult;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.ModifyDnRequest;
import org.apache.ldap.common.message.ModifyDnResponse;
import org.apache.ldap.common.message.ModifyDnResponseImpl;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.name.LdapName;
import org.apache.ldap.common.util.ExceptionUtils;
import org.apache.ldap.common.util.StringTools;
import org.apache.ldap.server.protocol.SessionRegistry;
import org.apache.mina.common.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A single reply handler for
 * {@link org.apache.ldap.common.message.ModifyDnRequest}s.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ModifyDnHandler implements MessageHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( ModifyDnHandler.class );

    public void messageReceived(IoSession session, Object request)
    {
        ModifyDnRequest req = (ModifyDnRequest) request;
        ModifyDnResponse resp = new ModifyDnResponseImpl( req.getMessageId() );
        resp.setLdapResult( new LdapResultImpl( resp ) );

        System.out.println("req.getName() == [" + req.getName() +"]");
        if (req.getName() == null || req.getName().length() == 0)
        {
            // it is not allowed to modify the name of the Root DSE
            
            String msg = "Modify DN is not allowed on Root DSE.";

            LdapResult result = resp.getLdapResult();
            result.setResultCode( ResultCodeEnum.PROTOCOLERROR );
            result.setErrorMessage( msg );
            session.write( resp );
        }
        else
        {
            try
            {
                LdapContext ctx = SessionRegistry.getSingleton().getLdapContext( session, null, true );
                String deleteRDN = String.valueOf( req.getDeleteOldRdn() );
                ctx.addToEnvironment( "java.naming.ldap.deleteRDN", deleteRDN );

                if (req.isMove())
                {
                    LdapName oldDn = new LdapName( req.getName() );
                    LdapName newDn = null;
                    
                    String newSuperior = req.getNewSuperior();
                    
                    if ( StringTools.isEmpty( newSuperior ) )
                    {
                    	newDn = (LdapName)oldDn.getPrefix( 1 );
                    }
                    else
                    {
                    	newDn = new LdapName( req.getNewSuperior() );
                    }
                    

                    if (req.getNewRdn() != null)
                    {
                        newDn.add( req.getNewRdn() );
                    }
                    else
                    {
                        newDn.add( oldDn.getRdn() );
                    }

                    ctx.rename( new LdapName( req.getName() ), newDn );
                }
                else
                {
                    LdapName newDn = new LdapName( req.getName() );
                    newDn.remove( newDn.size() - 1 );
                    newDn.add( req.getNewRdn() );
                    ctx.rename( new LdapName( req.getName() ), newDn );
                }
            }
            catch ( NamingException e )
            {
                String msg = "failed to modify DN of entry " + req.getName();

                if (LOG.isDebugEnabled())
                {
                    msg += ":\n" + ExceptionUtils.getStackTrace( e );
                }

                ResultCodeEnum code;

                if (e instanceof LdapException)
                {
                    code = ((LdapException) e).getResultCode();
                }
                else
                {
                    code = ResultCodeEnum.getBestEstimate( e, req.getType() );
                }

                resp.getLdapResult().setResultCode( code );
                resp.getLdapResult().setErrorMessage( msg );

                if ((e.getResolvedName() != null)
                        && ((code == ResultCodeEnum.NOSUCHOBJECT) || (code == ResultCodeEnum.ALIASPROBLEM)
                                || (code == ResultCodeEnum.INVALIDDNSYNTAX) || (code == ResultCodeEnum.ALIASDEREFERENCINGPROBLEM)))
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
}
