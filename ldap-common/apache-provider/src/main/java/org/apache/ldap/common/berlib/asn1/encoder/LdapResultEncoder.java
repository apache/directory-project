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
package org.apache.ldap.common.berlib.asn1.encoder;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.message.LdapResult;


/**
 * Encodes the elements of an LdapResult as TLV tuples into a top level tuple
 * node.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class LdapResultEncoder
{
    /**
     * An instance of this encoder.
     */
    public static final LdapResultEncoder INSTANCE = new LdapResultEncoder();

    /**
     * Encodes an LdapResult fields into TupleNodes adding them to the top
     * argument.  This method is thread safe.
     *
     * @param top the topmost node to add LDAP result fields to
     * @param result the ldap result to encode
     */
    public void encode( DefaultMutableTupleNode top, LdapResult result )
    {
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( UniversalTag.ENUMERATED,
                        result.getResultCode().getValue() );
        top.addLast( child );
        child.setParent( top );

        if ( result.getMatchedDn() == null )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode( "" );
        }
        else
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode( result.getMatchedDn() );
        }

        top.addLast( child );
        child.setParent( top );

        if ( result.getErrorMessage() != null )
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                    result.getErrorMessage() );
            top.addLast( child );
            child.setParent( top );
        }
        else
        {
            child = ( DefaultMutableTupleNode ) EncoderUtils.encode( "" );
            top.addLast( child );
            child.setParent( top );
        }

        if ( result.getReferral() != null &&
                result.getReferral().getLdapUrls().size() > 0 )
        {

            child = ( DefaultMutableTupleNode )
                    ReferralEncoder.INSTANCE.encode( result.getReferral() );
            top.addLast( child );
            child.setParent( top );
        }
    }
}
