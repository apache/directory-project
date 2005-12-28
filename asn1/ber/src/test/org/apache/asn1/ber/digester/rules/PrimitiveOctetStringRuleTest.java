/*
 *   Copyright 2004-2005 The Apache Software Foundation
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
package org.apache.asn1.ber.digester.rules ;


import junit.framework.TestCase ;

import org.apache.asn1.ber.digester.BERDigester ;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;

import java.nio.ByteBuffer;


/**
 * Tests the operation of the PrimitiveOctetStringRule.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PrimitiveOctetStringRuleTest extends TestCase
{
    private BERDigester digester ;
    private PrimitiveOctetStringRule rule ;


    protected void setUp() throws Exception
    {
        super.setUp() ;

        rule = new PrimitiveOctetStringRule() ;
        digester = new BERDigester() ;
        rule.setDigester( digester ) ;
    }


    protected void tearDown() throws Exception
    {
        super.tearDown() ;

        rule = null ;
        digester = null ;
    }


    /**
     * Tests for correct behavior when the OCTET STRING is constructed.
     */
    public void testConstructedOctetString()
    {
        rule.tag( UniversalTag.OCTET_STRING.getTagId(), false, null ) ;
        rule.length( 2 ) ;
        byte[] bites = { 0x07, 0x1 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        rule.finish() ;

        // we should have nothing in the object stack
        assertEquals( 0, digester.getCount() ) ;
    }


    /**
     * Tests for correct behavior when the OCTET STRING is primitive.
     */
    public void testPrimitiveOctetString()
    {
        rule.tag( UniversalTag.OCTET_STRING.getTagId(), true, null ) ;
        rule.length( 2 ) ;
        byte[] bites = { 0x07, 0x1 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        rule.finish() ;

        // we should have nothing in the object stack
        assertEquals( 1, digester.getCount() ) ;
        ByteBuffer pushed = ( ByteBuffer ) digester.pop() ;
        assertEquals( bites[0], pushed.get() ) ;
        assertEquals( bites[1], pushed.get() ) ;
        assertEquals( 0, digester.getCount() ) ;
    }


    /**
     * Tests when the length is indefinite.
     */
    public void testIndefiniteLength()
    {
        rule.tag( UniversalTag.OCTET_STRING.getTagId(), true, null ) ;
        rule.length( Length.INDEFINITE ) ;
        rule.finish() ;
        ByteBuffer buf = ( ByteBuffer ) digester.pop() ;
        assertFalse( buf.hasRemaining() ) ;
    }


    /**
     * Tests when the length is indefinite.
     */
    public void testNullValue()
    {
        rule.tag( UniversalTag.OCTET_STRING.getTagId(), true, null ) ;
        rule.length( Length.INDEFINITE ) ;
        rule.value( null ) ;
        rule.value( ByteBuffer.allocate( 0 ) ) ;
        rule.finish() ;
        ByteBuffer buf = ( ByteBuffer ) digester.pop() ;
        assertFalse( buf.hasRemaining() ) ;
    }


    /**
     * Tests when the wrong tag is supplied.
     */
    public void testWrongTag()
    {
        try
        {
            rule.tag( UniversalTag.OBJECT_IDENTIFIER.getTagId(), true, null ) ;
            fail( "should never get here due to an exception" ) ;
        }
        catch ( IllegalArgumentException e )
        {
        }
    }
}