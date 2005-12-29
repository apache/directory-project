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
package org.apache.ldap.common.berlib.asn1.encoder.abandon;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.message.AbandonRequest;


/**
 * Encodes an AbandonRequest stub into a TupleNode tree representing the TLV
 * nesting pattern of the request.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class AbandonRequestEncoder
{
    /** thread safe (flyweight) instance of this encoder */
    public static final AbandonRequestEncoder INSTANCE =
            new AbandonRequestEncoder();


    /**
     * Encodes an AbandonRequest stub into a TupleNode tree.
     *
     * @param request the AbandonRequest stub to encode
     * @return the root TupleNode for the tlv tree
     */
    public TupleNode encode( AbandonRequest request )
    {
        /// Create the top level PDU node
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Add the node for the id that is abandoned
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                LdapTag.ABANDON_REQUEST, request.getAbandoned() );
        top.addLast( child );
        child.setParent( top );

        return top;
    }
}
