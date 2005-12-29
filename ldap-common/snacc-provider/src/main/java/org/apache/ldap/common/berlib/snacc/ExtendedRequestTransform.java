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


import java.util.Iterator ;

import org.apache.ldap.common.message.ExtendedRequestImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.ExtendedRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Extended request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ExtendedRequestTransform
{
    static org.apache.ldap.common.message.ExtendedRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the delete request message
        ExtendedRequestImpl l_request =
            new ExtendedRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls) ;

        // Set the oid of the extended request operation
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        ExtendedRequest l_snaccRequest = l_protocolOp.extendedReq ;
        l_request.setOid( new String( l_snaccRequest.requestName ) ) ;

        // Set the payload value of the extended request operation
        l_request.setPayload( l_snaccRequest.requestValue ) ;

        return l_request ;
    }


    static LDAPMessage transformToSnacc(
        org.apache.ldap.common.message.ExtendedRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        ExtendedRequest l_snaccRequest = new ExtendedRequest() ;
        l_protocolOp.extendedReq = l_snaccRequest ;
        l_protocolOp.choiceId = LDAPMessageChoice.EXTENDEDREQ_CID ;

        // Set the OID of the extended request
        l_snaccRequest.requestName = a_request.getOid().getBytes() ;

        // Set the payload value of the extended request
        l_snaccRequest.requestValue = a_request.getPayload() ;

        return l_snaccMessage ;
    }
}
