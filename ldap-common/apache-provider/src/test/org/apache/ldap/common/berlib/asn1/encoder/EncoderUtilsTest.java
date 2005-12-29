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


import junit.framework.TestCase;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ldap.common.berlib.asn1.BufferUtils;
import org.apache.ldap.common.berlib.asn1.LdapTag;


/**
 * Tests the EncoderUtils class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class EncoderUtilsTest extends TestCase
{
    /**
     * Tests an overload of the EncoderUtils.encode() method for encoding
     * Strings.
     *
     * @see EncoderUtils#encode(org.apache.asn1.ber.TagEnum, String)
     */
    public void testEncodeTagEnumString()
    {
        final String str = "hello world";
        TupleNode node = EncoderUtils.encode( LdapTag.CONTEXT_SPECIFIC_TAG_7,
                 str );
        assertEquals( node.getTuple().getRawPrimitiveTag(),
                LdapTag.CONTEXT_SPECIFIC_TAG_7.getPrimitiveTag() );
        assertEquals( node.getTuple().getLength(), str.getBytes().length );
        assertTrue( node.getTuple().isPrimitive() );
        byte[] bites = BufferUtils.getArray(
                node.getTuple().getLastValueChunk() );
        assertTrue( ArrayUtils.isEquals( str.getBytes(), bites ) );
    }


    /**
     * Tests an overload of the EncoderUtils.encode() method for encoding
     * Strings.
     *
     * @see EncoderUtils#encode(String)
     */
    public void testEncodeString()
    {
        final String str = "hello world";
        TupleNode node = EncoderUtils.encode( str );
        assertEquals( node.getTuple().getRawTag(),
                UniversalTag.OCTET_STRING.getPrimitiveTag() );
        assertEquals( node.getTuple().getLength(), str.getBytes().length );
        assertTrue( node.getTuple().isPrimitive() );
        byte[] bites = BufferUtils.getArray(
                node.getTuple().getLastValueChunk() );
        assertTrue( ArrayUtils.isEquals( str.getBytes(), bites ) );
    }
}
