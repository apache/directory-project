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
import org.apache.ldap.common.message.BindRequest;


/**
 * A BindRequest stub to tuple tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class BindRequestEncoder
{
    /** thread safe instance of this encoder */
    public static final BindRequestEncoder INSTANCE = new BindRequestEncoder();


    /**
     * Encodes a BindRequest stub into a tuple tree.
     *
     * @param request the BindRequest instance to be encoded
     * @return the root of the tuple tree node for the BindRequest
     */
    public TupleNode encode( BindRequest request )
    {
        /// Create the top level BindRequest PDU node
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to response PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Create the bind response sequence of TLV tuple
        DefaultMutableTupleNode bindreq =
                new DefaultMutableTupleNode( new Tuple() );
        bindreq.getTuple().setTag( LdapTag.BIND_REQUEST, false );
        bindreq.getTuple().setLength( Length.INDEFINITE );

        // add the version integer
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                request.getVersion3() ? 3 : 2 );
        bindreq.addLast( child );
        child.setParent( bindreq );

        // add the name LDAPDN
        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                request.getName() );
        bindreq.addLast( child );
        child.setParent( bindreq );

        // add the security information
        if ( request.isSimple() )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                    LdapTag.CONTEXT_SPECIFIC_TAG_0, request.getCredentials() );
            bindreq.addLast( child );
            child.setParent( bindreq );
        }
        else
        {
            throw new NotImplementedException( "SASL not yet implemented!" );
        }

        top.addLast( bindreq );
        bindreq.setParent( top );
        return top;
    }
}
