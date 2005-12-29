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
package org.apache.ldap.common.berlib.asn1.encoder.delete;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.message.DeleteRequest;


/**
 * A DeleteRequest stub to TupleNode encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class DeleteRequestEncoder
{
    /** thread safe instance of the DeleteRequest stub encoder */
    public static final DeleteRequestEncoder INSTANCE =
            new DeleteRequestEncoder();

    /**
     * Encodes a DeleteRequest stub into a TupleNode representing a tlv tree
     * for the PDU.
     *
     * @param request the DeleteRequest stub to encode
     * @return the encoded root TupleNode for the tlv tree
     */
    public TupleNode encode( DeleteRequest request )
    {
        /// Create the top level PDU node
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to response PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        DefaultMutableTupleNode delreq = ( DefaultMutableTupleNode )
                EncoderUtils.encode( LdapTag.DEL_REQUEST, request.getName() );
        top.addLast( delreq );
        delreq.setParent( top );

        return top;
    }
}
