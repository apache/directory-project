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
package org.apache.ldap.common.berlib.asn1.encoder.compare;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.message.CompareRequest;


/**
 * Encoder for CompareRequest stubs which generates a tree of tlv TupleNodes.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class CompareRequestEncoder
{
    /** thread safe (flyweight) instance of this encoder */
    public static final CompareRequestEncoder INSTANCE =
            new CompareRequestEncoder();


    /**
     * Ecodes CompareRequest stubs into TupleNode trees.
     *
     * @param request the CompareRequest stub to encode
     * @return the root of the TupleNode tree
     */
    public TupleNode encode ( CompareRequest request )
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

        /// Create the top level TupleNode of the PDU
        DefaultMutableTupleNode compareReq =
                new DefaultMutableTupleNode( new Tuple() );
        compareReq.getTuple().setTag( LdapTag.COMPARE_REQUEST, false );
        compareReq.getTuple().setLength( Length.INDEFINITE );

        // Add the compare name LDAPDN to the compareRequest
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getName() );
        compareReq.addLast( child );
        child.setParent( compareReq );

        // Create the AttributeValueAssertion node
        DefaultMutableTupleNode ava =
                new DefaultMutableTupleNode( new Tuple() );
        ava.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        ava.getTuple().setLength( Length.INDEFINITE );

        // Start adding the AttributeValueAssertion pair
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getAttributeId() );
        ava.addLast( child );
        child.setParent( ava );

        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getAssertionValue() );
        ava.addLast( child );
        child.setParent( ava );

        // Add the ava request to the compareRequest
        compareReq.addLast( ava );
        ava.setParent( compareReq );

        // Add the compare Request to the top
        top.addLast( compareReq );
        compareReq.setParent( top );
        return top;
    }
}
