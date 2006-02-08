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
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;


/**
 * An Attributes to TupleNode tree encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class AttributesEncoder
{
    /** a thread safe instance of this encoder */
    public static final AttributesEncoder INSTANCE = new AttributesEncoder();


    /**
     * Encodes a set of Attributes into a TupleNode tree.
     *
     * @param attrs the Attributes to encode
     * @return the root TupleNode of the tlv tree
     */
    public TupleNode encode( Attributes attrs )
    {
        DefaultMutableTupleNode top =
                new DefaultMutableTupleNode( new Tuple() );
        top.getTuple().setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        top.getTuple().setLength( Length.INDEFINITE );

        NamingEnumeration list = attrs.getAll();
        while ( list.hasMoreElements() )
        {
            Attribute attr = ( Attribute ) list.nextElement();
            DefaultMutableTupleNode child = ( DefaultMutableTupleNode )
                    AttributeEncoder.INSTANCE.encode( attr );
            top.addLast( child );
            child.setParent( top );
        }

        return top;
    }
}
