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
package org.apache.ldap.common.berlib.asn1.encoder.modify;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.encoder.EncoderUtils;
import org.apache.ldap.common.berlib.asn1.encoder.ModificationItemEncoder;
import org.apache.ldap.common.message.ModifyRequest;

import javax.naming.directory.ModificationItem;
import java.util.Iterator;


/**
 * A ModifyRequest stub to TupleNode tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ModifyRequestEncoder
{
    /** thread safe (flyweight) instance of this encoder */
    public static final ModifyRequestEncoder INSTANCE =
            new ModifyRequestEncoder();


    /**
     * Encodes a ModifyRequest stub into a TupleNode tree.
     *
     * @param request the ModifyRequest stub to encode
     * @return the root TupleNode of the tlv tree
     */
    public TupleNode encode( ModifyRequest request )
    {
        /// Create the top level TupleNode of the PDU
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        // Create and add the message id to request PDU
        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getMessageId() );
        top.addLast( child );
        child.setParent( top );

        // Add modifyReq envelope sequence of node
        DefaultMutableTupleNode modReq =
                new DefaultMutableTupleNode( new Tuple() );
        modReq.getTuple().setTag( LdapTag.MODIFY_REQUEST, false );
        modReq.getTuple().setLength( Length.INDEFINITE );

        // Add the LDAPDN name of the entry being modified
        child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( request.getName() );
        modReq.addLast( child );
        child.setParent( modReq );

        // create and add the node containing the sequence of modifications
        DefaultMutableTupleNode mods =
                new DefaultMutableTupleNode( new Tuple() );
        mods.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        mods.getTuple().setLength( Length.INDEFINITE );

        // Add all the modification items using the ModificationItemEncoder
        Iterator list = request.getModificationItems().iterator();
        while( list.hasNext() )
        {
            ModificationItem item = ( ModificationItem ) list.next();
            DefaultMutableTupleNode itemNode = ( DefaultMutableTupleNode )
                    ModificationItemEncoder.INSTANCE.encode( item );
            mods.addLast( itemNode );
            itemNode.setParent( mods );
        }

        modReq.addLast( mods );
        mods.setParent( modReq );

        top.addLast( modReq );
        modReq.setParent( top );
        return top;
    }
}
