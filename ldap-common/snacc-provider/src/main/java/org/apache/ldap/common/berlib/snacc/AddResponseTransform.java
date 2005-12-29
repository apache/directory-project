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
package org.apache.ldap.common.berlib.snacc ;


import org.apache.ldap.common.message.AddResponseImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AddResponse ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Add response transformation functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class AddResponseTransform
{
	static org.apache.ldap.common.message.AddResponse
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
		AddResponseImpl l_response =
            new AddResponseImpl( a_snaccMessage.messageID.intValue() ) ;
        LDAPMessageChoice l_snaccOp = a_snaccMessage.protocolOp ;
        AddResponse l_snaccResponse = l_snaccOp.addResponse ;

        ControlTransform.transformFromSnacc( l_response,
            a_snaccMessage.controls ) ;
        l_response.setLdapResult( ResultTransform.transformFromSnacc(
            l_response, l_snaccResponse ) ) ;
        return l_response ;
    }


    static LDAPMessage
        transformToSnacc(org.apache.ldap.common.message.AddResponse a_resp )
    {
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_resp ) ;
        LDAPMessageChoice l_snaccOp = l_snaccMessage.protocolOp ;
		l_snaccOp.choiceId = LDAPMessageChoice.ADDRESPONSE_CID ;

        AddResponse l_snaccResponse = new AddResponse() ;
        l_snaccOp.addResponse = l_snaccResponse ;

        ResultTransform.transformToSnacc( l_snaccResponse,
            a_resp.getLdapResult() ) ;
        return l_snaccMessage ;
    }
}
