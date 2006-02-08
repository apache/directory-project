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

import org.apache.ldap.common.message.ReferralImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;
import org.apache.ldap.common.message.SearchResponseReferenceImpl ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SearchResultReference ;


/**
 * Search result reference transformation functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchResponseReferenceTransform
{
	static org.apache.ldap.common.message.SearchResponseReference
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
		SearchResponseReferenceImpl l_response =
            new SearchResponseReferenceImpl( a_snaccMessage.messageID.intValue() ) ;
        LDAPMessageChoice l_snaccOp = a_snaccMessage.protocolOp ;
        SearchResultReference l_snaccResponse = l_snaccOp.searchResRef ;

        ControlTransform.transformFromSnacc( l_response,
            a_snaccMessage.controls ) ;

        // SearchResultReference ldap urls here.
        Iterator l_list = l_snaccResponse.iterator() ;
        ReferralImpl l_referral = new ReferralImpl( l_response ) ;
        l_response.setReferral( l_referral ) ;
        while( l_list.hasNext() )
        {
            l_referral.addLdapUrl( new String( ( byte [] ) l_list.next() ) ) ;
        }

        return l_response ;
    }


    static LDAPMessage
        transformToSnacc(org.apache.ldap.common.message.SearchResponseReference a_resp )
        throws ProviderException
    {
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_resp ) ;
        LDAPMessageChoice l_snaccOp = l_snaccMessage.protocolOp ;
		l_snaccOp.choiceId = LDAPMessageChoice.SEARCHRESREF_CID ;

        SearchResultReference l_snaccResponse = new SearchResultReference() ;
        l_snaccOp.searchResRef = l_snaccResponse ;

        // Add the LDAPURLs to the response.
        Iterator l_list = a_resp.getReferral().getLdapUrls().iterator() ;
        while( l_list.hasNext() )
        {
            l_snaccResponse.add( ( ( String ) l_list.next() ).getBytes() ) ;
        }

        return l_snaccMessage ;
    }
}
