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


import org.apache.ldap.common.message.AbandonRequest;
import org.apache.mina.common.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for {@link org.apache.ldap.common.message.AbandonRequest}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AbandonHandler implements MessageHandler
{
    private static final Logger log = LoggerFactory.getLogger( AbandonHandler.class );

    public void messageReceived( IoSession session, Object request )
    {
        AbandonRequest req = ( AbandonRequest ) request;
        int abandonedId = req.getAbandoned();

        if ( abandonedId < 0 )
        {
            return;
        }
        
        log.warn( "Got abandon request from client but I don't know how to do this just yet" );
    }
}
