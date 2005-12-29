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
package org.apache.ldap.common.berlib.asn1.encoder.bind;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.commons.lang.NotImplementedException;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.berlib.asn1.encoder.LdapResultEncoder;
import org.apache.ldap.common.message.BindResponse;


/**
 * A thread safe BindResponse PDU encoder that produces a TupleNode tree
 * representing the TLV nesting heirarchy.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class BindResponseEncoder
{
    /** An instance of this encoder */
    public static final BindResponseEncoder INSTANCE =
            new BindResponseEncoder();


    /**
     * Encodes a BindResponse into a TupleTree representing the TLV nesting
     * heirarchy.
     *
     * @param response the BindResponse to encode
     * @return the root TLV tuple for the encoded BindResponse PDU
     */
    public TupleNode encode( BindResponse response )
    {
        /// Create the top level BindResponse PDU node
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to response PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( response.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Create the bind response sequence of TLV tuple
        DefaultMutableTupleNode bindresp =
                new DefaultMutableTupleNode( new Tuple() );
        bindresp.getTuple().setTag( LdapTag.BIND_RESPONSE, false );
        bindresp.getTuple().setLength( Length.INDEFINITE );

        // Stuff sequence of TLV tuple with the Components of the LDAPResult
        LdapResultEncoder.INSTANCE.encode( bindresp, response.getLdapResult() );

        // Conditionally add the sasl credentials if this is a SASL bind
        if ( response.getServerSaslCreds() != null )
        {
            throw new NotImplementedException( "SASL binds not implemented" ) ;
        }

        top.addLast( bindresp );
        bindresp.setParent( top );
        return top;
    }
}
