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
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;

import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;


/**
 * Encodes a Modification item in a ModifyRequest into a TupleNode tree.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ModificationItemEncoder
{
    /** thread safe (flyweight) instance of this encoder */
    public static final ModificationItemEncoder INSTANCE =
            new ModificationItemEncoder();


    /**
     * Encodes a ModificationItem into a TupleNode tree using the following
     * ASN.1.
     * <code>
     * modification    SEQUENCE OF SEQUENCE {
     *        operation       ENUMERATED {
     *                                add     (0),
     *                                delete  (1),
     *                                replace (2) },
     *        modification    AttributeTypeAndValues } }
     * </code>
     *
     * @param item the mod item being encoded
     * @return the root TupleNode of the tlv tree for the mod item
     */
    public TupleNode encode( ModificationItem item )
    {
        DefaultMutableTupleNode child = null;
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        child = ( DefaultMutableTupleNode ) EncoderUtils.encode(
                UniversalTag.ENUMERATED,
                getLdapModOp( item.getModificationOp() ) );
        top.addLast( child );
        child.setParent( top );

        child = ( DefaultMutableTupleNode ) AttributeEncoder.
                INSTANCE.encode( item.getAttribute() );
        top.addLast( child );
        child.setParent( top );

        return top;
    }


    /**
     * Maps the JNDI ModificationItem operation to the LDAP modify operation
     * ENUMERATED value.
     *
     * @param jndiModOp
     * @return
     */
    private int getLdapModOp( int jndiModOp )
    {
        switch( jndiModOp )
        {
            case( DirContext.ADD_ATTRIBUTE ):
                return 0;
            case( DirContext.REMOVE_ATTRIBUTE ):
                return 1;
            case( DirContext.REPLACE_ATTRIBUTE ):
                return 2;
            default:
                throw new IllegalArgumentException( "Unrecognized JNDI " +
                        "ModificationItem operation" );
        }
    }
}
