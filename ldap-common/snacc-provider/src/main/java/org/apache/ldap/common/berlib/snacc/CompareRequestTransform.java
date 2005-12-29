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

import org.apache.ldap.common.message.CompareRequestImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.CompareRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeValueAssertion ;


/**
 * Compare request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class CompareRequestTransform
{
    static org.apache.ldap.common.message.CompareRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the compare request message
        CompareRequestImpl l_request =
            new CompareRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls) ;

        // Set the distinguished name of the entry compared by the request
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        CompareRequest l_snaccRequest = l_protocolOp.compareRequest ;
        l_request.setName( new String( l_snaccRequest.entry ) ) ;

        // Set the attribute's id and value used in the comparison
        AttributeValueAssertion l_ava = l_snaccRequest.ava ;
        l_request.setAssertionValue( new String( l_ava.assertionValue ) ) ;
        l_request.setAttributeId( new String( l_ava.attributeDesc ) ) ;

        return l_request ;
    }


    static LDAPMessage transformToSnacc(
        org.apache.ldap.common.message.CompareRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        CompareRequest l_snaccRequest = new CompareRequest() ;
        l_protocolOp.compareRequest = l_snaccRequest ;
        l_protocolOp.choiceId = LDAPMessageChoice.COMPAREREQUEST_CID ;

        // Set the DN of the entry to compare
        l_snaccRequest.entry = a_request.getName().getBytes() ;

        // Build the AttributeValueAssertion for the comparison
        AttributeValueAssertion l_ava = new AttributeValueAssertion() ;
        l_snaccRequest.ava = l_ava ;

        // Set the attribute to be used in the comparison
        l_ava.attributeDesc = a_request.getAttributeId().getBytes() ;

        // Set the attribute's value to be used in the comparison
        l_ava.assertionValue = a_request.getAssertionValue() ;

        return l_snaccMessage ;
    }
}
