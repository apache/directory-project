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


import org.apache.ldap.common.message.LdapResult ;
import org.apache.ldap.common.message.LdapResultImpl ;
import org.apache.ldap.common.message.ResultCodeEnum ;
import org.apache.ldap.common.message.BindResponseImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.BindResponse ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPResultEnum ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Transforms Snacc4J BindResponse message stubs and their contained stubs into
 * LDAPd CMA based BindResponse objects and vice versa.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class BindResponseTransform
{

    /**
     * Transforms a Snacc4J BindResponse message stub and its contained stubs
     * into a CMA BindResponse object instance.
     *
     * @param a_snaccMessage the Snacc4J BindResponse message stub to transform
     * @return the transformed LDAPd CMA BindResponse
     */
	static org.apache.ldap.common.message.BindResponse
        transform( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
		BindResponseImpl l_response =
            new BindResponseImpl( a_snaccMessage.messageID.intValue() ) ;
        LdapResultImpl l_result = new LdapResultImpl( l_response ) ;
        LDAPMessageChoice l_snaccOp = a_snaccMessage.protocolOp ;
        BindResponse l_snaccResponse = l_snaccOp.bindResponse ;
        l_response.setLdapResult( l_result ) ;

        // Can't use the LdapResult utility since BindResponse does not extend
        // the LDAPResult data type.  The extra SASL field in the PDU prevents
        // the definition to derive from LDAPResult.

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


    /**
     * Transforms an LDAPd CMA BindResponse instance and its contained objects
     * into a Snacc4J LDAPMessage envelope.
     *
     * @param a_resp the LDAPd CMA BindResponse to transform into a Snacc4J
     * message envelope stub.
     * @return the transformed Snacc4J message envelope stub representing a
     * BindResponse.
     */
    static LDAPMessage transform( 
            org.apache.ldap.common.message.BindResponse a_resp )
    {
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_resp ) ;

        LdapResult l_result = a_resp.getLdapResult() ;
        LDAPMessageChoice l_snaccOp = l_snaccMessage.protocolOp ;
		l_snaccOp.choiceId = LDAPMessageChoice.BINDRESPONSE_CID ;
        BindResponse l_snaccResponse = new BindResponse() ;
        l_snaccOp.bindResponse = l_snaccResponse ;

        // Set the resultCode
		LDAPResultEnum l_snaccResultEnum = new LDAPResultEnum() ;
        l_snaccResponse.resultCode = l_snaccResultEnum ;
        ResultCodeEnum l_resultCode = l_result.getResultCode() ;
		l_snaccResultEnum.value = l_resultCode.getValue() ;

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

        // Freak if unimplemented SASL fields are used.
        if( a_resp.getServerSaslCreds() != null )
        {
            l_snaccResponse.serverSaslCreds = a_resp.getServerSaslCreds() ;
        }

        return l_snaccMessage ;
    }
}
