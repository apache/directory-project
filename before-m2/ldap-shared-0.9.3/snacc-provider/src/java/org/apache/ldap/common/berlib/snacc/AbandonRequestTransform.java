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


import java.math.BigInteger;

import org.apache.ldap.common.message.AbandonRequestImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AbandonRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Abandon request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class AbandonRequestTransform
{
    static org.apache.ldap.common.message.AbandonRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the abandon request message
        AbandonRequestImpl l_request =
            new AbandonRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls) ;

        // Set the id of the request to abandon.
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        AbandonRequest l_snaccRequest = l_protocolOp.abandonRequest ;
        l_request.setAbandoned( l_snaccRequest.value.intValue() ) ;

        return l_request ;
    }


    static LDAPMessage transformToSnacc(
        org.apache.ldap.common.message.AbandonRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        AbandonRequest l_snaccRequest = new AbandonRequest() ;
        l_protocolOp.abandonRequest = l_snaccRequest ;
        l_protocolOp.choiceId = LDAPMessageChoice.ABANDONREQUEST_CID ;

        // Set the id of the request to abandon.
        l_snaccRequest.value = new BigInteger( 
                Integer.toString( a_request.getAbandoned() ) ) ;

        return l_snaccMessage ;
    }
}
