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
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.berlib.asn1.encoder.LdapResultEncoder;
import org.apache.ldap.common.message.AddResponse;


/**
 * A AddResponse stub to tuple tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 */
public class AddResponseEncoder
{
    /** An instance of this encoder */
    public static final AddResponseEncoder INSTANCE =
            new AddResponseEncoder();


    /**
     * Encodes a AddResponse stub corresponding to a AddResponse
     * PDU into a TupleTree representing the TLV nesting heirarchy.
     *
     * @param response the AddResponse to encode
     * @return the root TLV tuple for the encoded AddResponse PDU
     */
    public TupleNode encode( AddResponse response )
    {
        /// Create the top level TupleNode of the PDU
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to response PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( response.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Create the Add response sequence of TLV tuple
        DefaultMutableTupleNode addResult =
                new DefaultMutableTupleNode( new Tuple() );
        addResult.getTuple().setTag( LdapTag.ADD_RESPONSE, false );
        addResult.getTuple().setLength( Length.INDEFINITE );

        // Stuff sequence of TLV tuple with the Components of the LDAPResult
        LdapResultEncoder.INSTANCE.encode( addResult, response.getLdapResult() );

        top.addLast( addResult );
        addResult.setParent( top );
        return top;
    }
}
