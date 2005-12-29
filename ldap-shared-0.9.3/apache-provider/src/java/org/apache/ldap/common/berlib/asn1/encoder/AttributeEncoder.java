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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;


/**
 * An Attribute to a TupleNode tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class AttributeEncoder
{
    /** a thread safe instance of this encoder */
    public static final AttributeEncoder INSTANCE = new AttributeEncoder();


    /**
     * Encodes an attribute into a TupleNode tree.  The following ASN.1
     * structure is the result:
     * <code>
     * SEQUENCE
     * {
     *    type    AttributeDescription,
     *    vals    SET OF AttributeValue
     * }
     * </code>
     *
     * @param attr the Attribute to encode
     * @return the root TupleNode of the tlv tree
     */
    public TupleNode encode( Attribute attr )
    {
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                EncoderUtils.encode( attr.getID() );
        top.addLast( child );
        child.setParent( top );

        NamingEnumeration list = null;
        try
        {
            list = attr.getAll();
        }
        catch ( NamingException e )
        {
            e.printStackTrace();
        }

        DefaultMutableTupleNode setOf =
                new DefaultMutableTupleNode( new Tuple() );
        setOf.getTuple().setTag( UniversalTag.SET_SET_OF, false );
        setOf.getTuple().setLength( Length.INDEFINITE );

        while ( list.hasMoreElements() )
        {
            Object next = list.nextElement();

            if ( next instanceof String )
            {
                child = ( DefaultMutableTupleNode )
                    EncoderUtils.encode( ( String ) next );
            }
            else
            {
                child = ( DefaultMutableTupleNode )
                    EncoderUtils.encode( ( byte[] ) next );
            }
            setOf.addFront( child );
            child.setParent( setOf );
        }

        top.addLast( setOf );
        setOf.setParent( top );
        return top;
    }
}
