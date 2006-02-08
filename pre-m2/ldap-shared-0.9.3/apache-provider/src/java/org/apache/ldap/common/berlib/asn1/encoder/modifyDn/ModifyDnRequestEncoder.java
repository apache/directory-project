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
package org.apache.ldap.common.berlib.asn1.encoder.modifyDn;

import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.message.ModifyDnRequest;

/**
 * Encoder which generates a TupleNode tree from a ModifyDnRequest PDU stub.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ModifyDnRequestEncoder
{
    /** thread safe (flywieght) instance of this encoder */
    public static final ModifyDnRequestEncoder INSTANCE =
            new ModifyDnRequestEncoder();


    /**
     * Encodes a ModifyDnRequest into a TupleNode tree.
     *
     * @param req the ModifyDnRequest stub to encode
     * @return the encoded root TupleNode tree
     */
    public TupleNode encode( ModifyDnRequest req )
    {
        /// Create the top level TupleNode of the PDU
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to req PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( req.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Create the modify response sequence of TLV tuple
        DefaultMutableTupleNode modifyDnReq =
                new DefaultMutableTupleNode( new Tuple() );
        modifyDnReq.getTuple().setTag( LdapTag.MODIFYDN_REQUEST, false );
        modifyDnReq.getTuple().setLength( Length.INDEFINITE );

        // add the tuple for the LDAPDN entry name
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( req.getName() );
        modifyDnReq.addLast( child );
        child.setParent( modifyDnReq );

        // add the tuple for the RelativeLDAPDN newrdn
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( req.getNewRdn() );
        modifyDnReq.addLast( child );
        child.setParent( modifyDnReq );

        // add the tuple for the BOOLEAN deleteOldRdn field
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( req.getDeleteOldRdn() );
        modifyDnReq.addLast( child );
        child.setParent( modifyDnReq );

        // add the tuple for the OPTIONAL LDAPDN newSuperior
        if ( req.getNewSuperior() != null )
        {
            child = ( DefaultMutableTupleNode )
                    EncoderUtils.encode( LdapTag.CONTEXT_SPECIFIC_TAG_0,
                            req.getNewSuperior() );
            modifyDnReq.addLast( child );
            child.setParent( modifyDnReq );
        }

        top.addLast( modifyDnReq );
        modifyDnReq.setParent( top );
        return top;
    }
}
