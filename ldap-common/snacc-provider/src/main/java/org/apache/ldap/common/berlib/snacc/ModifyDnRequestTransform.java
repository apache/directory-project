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

import org.apache.ldap.common.message.ModifyDnRequest ;
import org.apache.ldap.common.message.ModifyDnRequestImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.ModifyDNRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * ModifyDn request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ModifyDnRequestTransform
{
    static ModifyDnRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the modifyDn request message
        ModifyDnRequestImpl l_request =
            new ModifyDnRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls) ;

        // Set the dn of the entry whose dn is being modified
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        ModifyDNRequest l_snaccRequest = l_protocolOp.modDNRequest ;
        l_request.setName( new String( l_snaccRequest.entry ) ) ;

        // Set delete rdn flag
        l_request.setDeleteOldRdn( l_snaccRequest.deleteoldrdn ) ;

        // Set the new rdn.
        l_request.setNewRdn( new String( l_snaccRequest.newrdn ) ) ;

        // Set the new superior dn if this is a move operation.
        if( l_snaccRequest.newSuperior != null )
        {
            l_request.setNewSuperior(
                new String( l_snaccRequest.newSuperior ) ) ;
        }

        return l_request ;
    }


    static LDAPMessage transformToSnacc(
        ModifyDnRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        ModifyDNRequest l_snaccRequest = new ModifyDNRequest() ;
        l_protocolOp.modDNRequest = l_snaccRequest ;
        l_protocolOp.choiceId = LDAPMessageChoice.MODDNREQUEST_CID ;

        // Set the dn of the entry whose dn is being modified
        l_snaccRequest.entry = a_request.getName().getBytes() ;

        // Set delete rdn flag
        l_snaccRequest.deleteoldrdn = a_request.getDeleteOldRdn() ;

        // Set the new rdn.
        l_snaccRequest.newrdn = a_request.getNewRdn().getBytes() ;

        // Set the new superior if it is not null and this is a move operation
        if( a_request.isMove() )
        {
            l_snaccRequest.newSuperior = a_request.getNewSuperior().getBytes() ;
        }

        return l_snaccMessage ;
    }
}
