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


import org.apache.ldap.common.name.LdapName ;

import org.apache.ldap.common.message.LdapResult ;
import org.apache.ldap.common.message.LdapResultImpl ;
import org.apache.ldap.common.message.ResultCodeEnum ;
import org.apache.ldap.common.message.ExtendedResponseImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPResultEnum ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.ExtendedResponse ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Extended response transformation functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ExtendedResponseTransform
{
	static org.apache.ldap.common.message.ExtendedResponse
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
		ExtendedResponseImpl l_response =
            new ExtendedResponseImpl( a_snaccMessage.messageID.intValue() ) ;
        LDAPMessageChoice l_snaccOp = a_snaccMessage.protocolOp ;
        ExtendedResponse l_snaccResponse = l_snaccOp.extendedResp ;
        LdapResult l_result = new LdapResultImpl( l_response ) ;
        ControlTransform.transformFromSnacc( l_response,
            a_snaccMessage.controls ) ;
        l_response.setLdapResult( l_result ) ;

        // Set the extended response OID
        l_response.setResponseName(
            new String( l_snaccResponse.responseName ) ) ;

        // Set the extended response payload value
        l_response.setResponse( l_snaccResponse.response ) ;

        // Can't use the LdapResult utility since ExtendedResponse does not
        // extend the LDAPResult data type.  The extra OID and payload fields
        // in the PDU prevents the definition to derive from LDAPResult.

        // Set the error message if one was generated
        if( l_snaccResponse.errorMessage != null )
        {
         l_result.setErrorMessage( new String(
                l_snaccResponse.errorMessage ) ) ;
        }

        // Set the reverals if one was generated
        if( l_snaccResponse.referral != null )
        {
            ReferralTransform.transformAdd( l_result,
                l_snaccResponse.referral ) ;
        }

        // Set the last matched distinguished name if it is not null or the
        // empty string.  Anonymous authentic uses the empty string DN.
        byte [] l_dn = l_snaccResponse.matchedDN ;
        if( l_dn == null )
        {
         l_result.setMatchedDn( "" ) ;
        }
	    else
        {
            l_result.setMatchedDn( new String( l_dn ) ) ;
        }

        // Set the result code
        LDAPResultEnum l_snaccCode = l_snaccResponse.resultCode ;
        ResultCodeEnum l_resultCode =
            ResultCodeEnum.getResultCodeEnum( l_snaccCode.value ) ;
        l_result.setResultCode( l_resultCode ) ;

        return l_response ;
    }


    static LDAPMessage
        transformToSnacc( org.apache.ldap.common.message.ExtendedResponse a_resp )
    {
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_resp ) ;
        LDAPMessageChoice l_snaccOp = l_snaccMessage.protocolOp ;
		l_snaccOp.choiceId = LDAPMessageChoice.EXTENDEDRESP_CID ;
        LdapResult l_result = a_resp.getLdapResult() ;
        ExtendedResponse l_snaccResponse = new ExtendedResponse() ;
        l_snaccOp.extendedResp = l_snaccResponse ;

        // Set the extended response OID
        l_snaccResponse.responseName = a_resp.getResponseName().getBytes() ;

        // Set the extended response payload value
        l_snaccResponse.response = a_resp.getResponse() ;

        // Set the resultCode
	    LDAPResultEnum l_snaccResultEnum = new LDAPResultEnum() ;
        l_snaccResponse.resultCode = l_snaccResultEnum ;
	    l_snaccResultEnum.value = l_result.getResultCode().getValue() ;

        // Set the error message if one exists
        if( l_result.getErrorMessage() == null )
        {
            l_snaccResponse.errorMessage = "".getBytes() ;
        }
        else
	    {
            l_snaccResponse.errorMessage =
                l_result.getErrorMessage().getBytes() ;
        }

        // Set the matched DN parameter
        l_snaccResponse.matchedDN =
            l_result.getMatchedDn().toString().getBytes() ;

        // Set the referrals
        l_snaccResponse.referral = ReferralTransform.transform(
            l_result.getReferral() ) ;

        return l_snaccMessage ;
    }
}
