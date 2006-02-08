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

import org.apache.ldap.common.message.DeleteRequestImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.DelRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Delete request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class DelRequestTransform
{
    static org.apache.ldap.common.message.DeleteRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the delete request message
        DeleteRequestImpl l_request =
            new DeleteRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls) ;

        // Set the distinguished name of the entry deleted by the request
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        DelRequest l_snaccRequest = l_protocolOp.delRequest ;
        l_request.setName( new String( l_snaccRequest.value ) ) ;

        return l_request ;
    }


    static LDAPMessage transformToSnacc(
        org.apache.ldap.common.message.DeleteRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        DelRequest l_snaccRequest = new DelRequest() ;
        l_protocolOp.delRequest = l_snaccRequest ;
        l_protocolOp.choiceId = LDAPMessageChoice.DELREQUEST_CID ;

        // Set the DN of the entry to add
        l_snaccRequest.value = a_request.getName().getBytes() ;

        return l_snaccMessage ;
    }
}
