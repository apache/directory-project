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

import org.apache.ldap.common.message.UnbindRequestImpl ; 
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.UnbindRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Unbind request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class UnbindRequestTransform
{
    static org.apache.ldap.common.message.UnbindRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the unbind request message
        UnbindRequestImpl l_request =
            new UnbindRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls) ;

        return l_request ;
    }


    static LDAPMessage transformToSnacc(
        org.apache.ldap.common.message.UnbindRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        UnbindRequest l_snaccRequest = new UnbindRequest() ;
        l_protocolOp.unbindRequest = l_snaccRequest ;
        l_protocolOp.choiceId = LDAPMessageChoice.UNBINDREQUEST_CID ;

        return l_snaccMessage ;
    }
}
