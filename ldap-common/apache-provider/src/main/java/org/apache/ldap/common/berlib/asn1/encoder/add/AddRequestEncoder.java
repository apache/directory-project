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
package org.apache.ldap.common.berlib.asn1.encoder.add;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.AttributesEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.message.AddRequest;


/**
 * AddRequest Encoder for transforming AddRequest stubs into a TupleNode
 * representing the root of a tlv tuple tree.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class AddRequestEncoder
{
    /** thread safe (flyweight) instance for this encoder */
    public static final AddRequestEncoder INSTANCE = new AddRequestEncoder();


    public TupleNode encode( AddRequest request )
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

        // Create the addReq nodes
        DefaultMutableTupleNode addReq =
                new DefaultMutableTupleNode( new Tuple() );
        addReq.getTuple().setTag( LdapTag.ADD_REQUEST, false );
        addReq.getTuple().setLength( Length.INDEFINITE );

        // Add the LDAPDN entry name to the addReq node
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getEntry() );
        addReq.addLast( child );
        child.setParent( addReq );

        // Add the AttributeList to the addReq node delegating generation
        // to the AttributesEncoder which is also used by the SearchRequest
        // for generating the PartialAttributesList with the same ASN.1 syntax
        child = ( DefaultMutableTupleNode )
                AttributesEncoder.INSTANCE.encode( request.getAttributes() );
        addReq.addLast( child );
        child.setParent( addReq );

        // Attach the addReq node to the top and return the top
        top.addLast( addReq );
        addReq.setParent( top );
        return top;
    }
}
