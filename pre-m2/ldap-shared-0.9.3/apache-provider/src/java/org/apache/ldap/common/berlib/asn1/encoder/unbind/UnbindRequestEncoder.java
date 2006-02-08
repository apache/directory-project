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
package org.apache.ldap.common.berlib.asn1.encoder.unbind;

import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.message.UnbindRequest;


/**
 * An UnbindRequest stub to TupleNode tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class UnbindRequestEncoder
{
    /** thread safe instance of this encoder */
    public static final UnbindRequestEncoder INSTANCE =
            new UnbindRequestEncoder();


    /**
     * Encodes a UnbindRequest stub into a tree of TupleNodes.
     *
     * @param resp the UnbindRequest stub to encode
     * @return the root TupleNode of the tlv tree
     */
    public TupleNode encode( UnbindRequest resp )
    {
        /// Create the top level TupleNode of the PDU
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to response PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( resp.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Create the unbind resp sequence of TLV tuple
        DefaultMutableTupleNode unbindResp =
                new DefaultMutableTupleNode( new Tuple() );
        unbindResp.getTuple().setTag( LdapTag.UNBIND_REQUEST, true );
        unbindResp.getTuple().setLength( 0 );

        top.addLast( unbindResp );
        unbindResp.setParent( top );
        return top;

    }
}
