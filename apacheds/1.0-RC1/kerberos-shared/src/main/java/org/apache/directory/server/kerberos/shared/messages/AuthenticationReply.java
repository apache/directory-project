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
package org.apache.directory.server.kerberos.shared.messages;


import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.server.kerberos.shared.messages.components.Ticket;
import org.apache.directory.server.kerberos.shared.messages.value.EncryptedData;
import org.apache.directory.server.kerberos.shared.messages.value.PreAuthenticationData;


public class AuthenticationReply extends KdcReply
{
    /**
     * Class constructors
     */
    public AuthenticationReply()
    {
        super( MessageType.KRB_AS_REP );
    }


    public AuthenticationReply(PreAuthenticationData[] paData, KerberosPrincipal clientPrincipal, Ticket ticket,
        EncryptedData encPart)
    {
        super( paData, clientPrincipal, ticket, encPart, MessageType.KRB_AS_REP );
    }
}
