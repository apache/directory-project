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
package org.apache.ldap.common.berlib.asn1.encoder.extended;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.message.ExtendedRequest;


/**
 * An extended request stub to tlv tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ExtendedRequestEncoder
{
    /** thread safe (flyweight) instance of this encoder */
    public static final ExtendedRequestEncoder INSTANCE =
            new ExtendedRequestEncoder();


    /**
     * Encodes an ExtendedRequest stub into a TupleNode tree representing the
     * tlv nesting of an ExtendedRequest PDU.
     *
     * @param request the ExtendedRequest to encode
     * @return the root TupleNode of the tree
     */
    public TupleNode encode( ExtendedRequest request )
    {
        /// Create the top level TupleNode of the PDU
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Create the search request sequence of TLV tuple
        DefaultMutableTupleNode extendedReq =
                new DefaultMutableTupleNode( new Tuple() );
        extendedReq.getTuple().setTag( LdapTag.EXTENDED_REQUEST, false );
        extendedReq.getTuple().setLength( Length.INDEFINITE );

        // Add the tuple for the LDAPOID representing requestName
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                LdapTag.CONTEXT_SPECIFIC_TAG_0, request.getOid() );
        extendedReq.addLast( child );
        child.setParent( extendedReq );

        // If it exists add the OPTIONAL extended request's requestValue
        if ( request.getPayload() != null )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                    LdapTag.CONTEXT_SPECIFIC_TAG_1, request.getPayload() );
            extendedReq.addLast( child );
            child.setParent( extendedReq );
        }

        top.addLast( extendedReq );
        extendedReq.setParent( top );
        return top;
    }
}
