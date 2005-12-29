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
import org.apache.ldap.common.berlib.asn1.encoder.LdapResultEncoder;
import org.apache.ldap.common.message.ExtendedResponse;

/**
 * An encoder that transforms a stub into a TupleNode tree representing the
 * tlv nesting heirarchy.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ExtendedResponseEncoder
{
    /** thread safe (fly wieght) intance of this encoder */
    public static final ExtendedResponseEncoder INSTANCE =
            new ExtendedResponseEncoder();


    /**
     * Encodes an ExtendedResponse stub corresponding to a ExtendedResponse
     * PDU into a TupleTree representing the TLV nesting heirarchy.
     *
     * @param response the ExtendedResponse to encode
     * @return the root TLV tuple for the encoded SearchResultDone PDU
     */
    public TupleNode encode( ExtendedResponse response )
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

        // Create the extended response sequence of TLV tuple
        DefaultMutableTupleNode extResp =
                new DefaultMutableTupleNode( new Tuple() );
        extResp.getTuple().setTag( LdapTag.EXTENDED_RESPONSE, false );
        extResp.getTuple().setLength( Length.INDEFINITE );

        // Stuff sequence of TLV tuple with the Components of the LDAPResult
        LdapResultEncoder.INSTANCE.encode( extResp, response.getLdapResult() );

        // add the response name nodes conditionally since they are OPTIONAL
        if ( response.getResponseName() != null )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils
                    .encode( LdapTag.CONTEXT_SPECIFIC_TAG_10,
                            response.getResponseName() );
            extResp.addLast( child );
            child.setParent( extResp );
        }

        // add the response payload node conditionally since it is OPTIONAL
        if ( response.getResponse() != null )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils
                    .encode( LdapTag.CONTEXT_SPECIFIC_TAG_11,
                            response.getResponse() );
            extResp.addLast( child );
            child.setParent( extResp );
        }

        top.addLast( extResp );
        extResp.setParent( top );
        return top;
    }
}
