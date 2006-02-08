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
package org.apache.ldap.common.berlib.asn1.encoder;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.Referral;

import java.util.Iterator;


/**
 * TupleNode tree encoder for Referrals.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class ReferralEncoder
{
    /**
     * An instance of this encoder.
     */
    public static final ReferralEncoder INSTANCE = new ReferralEncoder();


    /**
     * Encodes a Referral stub object into a TupleNode tree.  This method is
     * thread safe.
     *
     * @param ref an instance of the Referral stub class
     * @return the encoded TupleTree structure for the Referral object
     */
    public TupleNode encode( Referral ref )
    {
        Tuple tlv = new Tuple();
        tlv.setTag( LdapTag.REFERRAL_TAG, false );
        tlv.setLength( Length.INDEFINITE );
        DefaultMutableTupleNode refs = new DefaultMutableTupleNode( tlv );

        Iterator list = ref.getLdapUrls().iterator();
        while( list.hasNext() )
        {
            DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                    EncoderUtils.encode( ( String ) list.next() );
            refs.addLast( child );
            child.setParent( refs );
        }

        return refs;
    }
}
