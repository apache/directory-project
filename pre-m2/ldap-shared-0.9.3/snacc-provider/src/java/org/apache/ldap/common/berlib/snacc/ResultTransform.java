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
import org.apache.ldap.common.message.spi.ProviderException ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPResult ;

import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPResultEnum;


/**
 * Provides transformation functions for LdapResult structures back and forth
 * from snacc PDUs to the Ldapd representation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ResultTransform
{
    static LdapResult transformFromSnacc( Lockable parent,
                                          LDAPResult snaccResult )
            throws ProviderException
    {
        LdapResult result = new LdapResultImpl( parent ) ;

        // Set the error message if one was generated
        if( snaccResult.errorMessage != null )
        {
            result.setErrorMessage( new String( snaccResult.errorMessage ) ) ;
        }

        // Set the reverals if one was generated
        if( snaccResult.referral != null )
        {
            ReferralTransform.transformAdd( result, snaccResult.referral ) ;
        }

        // Set the last matched distinguished name if it is not null or the
        // empty string.  Anonymous authentic uses the empty string DN.
        byte [] dn = snaccResult.matchedDN ;
        if( dn == null )
        {
            result.setMatchedDn( "" ) ;
        }
	    else
        {
            result.setMatchedDn( new String( dn ) ) ;
        }

        // Set the result code
        LDAPResultEnum snaccCode = snaccResult.resultCode ;
        ResultCodeEnum resultCode =
                ResultCodeEnum.getResultCodeEnum( snaccCode.value ) ;
        result.setResultCode( resultCode ) ;

        return result ;
    }


    static void transformToSnacc( LDAPResult snaccResult, LdapResult result )
            throws ProviderException
    {
        // Set the resultCode
	    LDAPResultEnum snaccResultEnum = new LDAPResultEnum() ;
        snaccResult.resultCode = snaccResultEnum ;
	    snaccResultEnum.value = result.getResultCode().getValue() ;

        // Set the error message if one exists
        if( result.getErrorMessage() == null )
        {
            snaccResult.errorMessage = "".getBytes() ;
        }
        else
	    {
            snaccResult.errorMessage =
                result.getErrorMessage().getBytes() ;
        }

        // Set the matched DN parameter
        snaccResult.matchedDN =
            result.getMatchedDn().getBytes() ;

        // Set the referrals
        snaccResult.referral = ReferralTransform.transform(
            result.getReferral() ) ;
    }
}
