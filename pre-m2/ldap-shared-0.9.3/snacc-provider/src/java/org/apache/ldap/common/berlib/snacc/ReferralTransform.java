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

/*
 * $Id: ReferralTransform.java,v 1.3 2003/05/11 01:42:46 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.berlib.snacc ;


import java.util.Iterator ;
import javax.naming.NamingException ;

import org.apache.ldap.common.message.LdapResult ;
import org.apache.ldap.common.message.ReferralImpl ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Referral ;
import org.apache.ldap.common.message.spi.ProviderException ;


/**
 * Transforms Snacc4J Referral stub instances into LDAPd Common Message API
 * (CMA) Referral objects and vice versa.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class ReferralTransform
{
    /**
     * Transforms a LDAPd CMA Referral into a Snacc4J Referral Stub.
     *
     * @param a_referral the LDAPd CMA Referral
     * @return the Snacc4J Referral Stub
     */
    static Referral transform( org.apache.ldap.common.message.Referral a_referral )
    {
        // If the referral is null meaning the response is not a referral
        // we simply return null to indicate the same for the Snacc4J stub.
        if( a_referral == null )
        {
            return null ;
        }

        // Create the Snacc4J referral since the response is a referral
        Referral a_snaccReferral = new Referral() ;
		Iterator l_list = a_referral.getLdapUrls().iterator() ;

        // Iterate through the list of referral strings and add the string
        // to the Snacc4J referral stub as byte arrays.
        while( l_list.hasNext() )
        {
			a_snaccReferral.add( ( ( String ) l_list.next() ).getBytes() ) ;
        }

        return a_snaccReferral ;
    }


    /**
     * Transforms a Snacc4J Referral stub into a LDAPd CMA Referral if the stub
     * is not null and adds it to the LDAPd CMA LdapResult.  We need the LDAPd
     * CMA LdapResult to instantiate the Referral who parent lockable will be
     * the LdapResult.
     *
     * @param a_result the LDAPd CMA LdapResult to add the Referral to
     * @param a_snaccReferral the Snacc4J Referral Stub to transform and add
     */
    static void transformAdd( LdapResult a_result, Referral a_snaccReferral )
        throws ProviderException
    {
        // If the response is not a referral then the snacc referral stub will
        // be null so it need not be added to the LdapResult.
		if( a_snaccReferral == null )
        {
            return ;
        }

        // Response is a referral so we create the LDAPd CMA Referral using the
        // convenience implementation supplied with the CMA.  Then we loop
        // through the Snacc4J referral looking for urls which we convert to
        // strings and add to the LDAPd CMA Referral.
        ReferralImpl l_referral = new ReferralImpl( a_result ) ;
        for( int ii = 0; ii < a_snaccReferral.size(); ii++ )
        {
            l_referral.addLdapUrl( new String( (byte [])
                    a_snaccReferral.get( ii ) ) ) ;
        }

        // At the end we set the
        a_result.setReferral( l_referral ) ;
    }
}
