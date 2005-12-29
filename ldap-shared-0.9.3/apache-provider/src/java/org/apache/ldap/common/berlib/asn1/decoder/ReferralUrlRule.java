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
package org.apache.ldap.common.berlib.asn1.decoder ;


import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.ldap.common.message.Referral;

import java.nio.ByteBuffer;


/**
 * Rule that collects an OCTET_STRING and adds it to the Referral object at
 * the top of the stack.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ReferralUrlRule extends PrimitiveOctetStringRule
{
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;

        // get the LDAP URL collected as an OCTET STRING
        ByteBuffer buf = ( ByteBuffer ) getDigester().pop() ;

        // peek at the Referral underneath who we add the LDAPURL to
        Referral ref = ( Referral ) getDigester().peek() ;

        byte[] url = null ;
        if ( buf.limit() == buf.capacity() && buf.hasArray() )
        {
            // use the backing store
            url = buf.array() ;
        }
        else
        {
            // copy because we don't have accessible array or data < array
            url = new byte[buf.remaining()] ;
            buf.get( url ) ;
        }

        ref.addLdapUrl( new String( url ) ) ;
    }
}
