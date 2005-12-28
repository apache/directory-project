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
package org.apache.asn1.ber.digester.rules ;


import junit.framework.TestCase ;

import org.apache.asn1.ber.TypeClass ;
import org.apache.asn1.ber.digester.BERDigester ;
import org.apache.asn1.ber.primitives.UniversalTag ;
import org.apache.asn1.ber.digester.rules.PrimitiveIntDecodeRule;

import java.nio.ByteBuffer;
import java.math.BigInteger;


/**
 * Tests the PrimitiveIntDecodeRule.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PrimitiveIntDecodeRuleTest extends TestCase
{
    PrimitiveIntDecodeRule rule ;
    BERDigester digester ;


    protected void setUp() throws Exception
    {
        super.setUp() ;
        rule = new PrimitiveIntDecodeRule() ;
        digester = new BERDigester() ;
        rule.setDigester( digester ) ;
        int[] pattern = { 0x10000000, 0x02000000 } ;
        digester.addRule( pattern, rule ) ;
    }


    protected void tearDown() throws Exception
    {
        super.tearDown() ;
        rule.setDigester( null ) ;
        rule = null ;
        digester = null ;
    }


    public void testTag()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;

        try
        {
            rule.tag( 0, false, null ) ;
            fail( "should never get here" ) ;
        }
        catch ( IllegalArgumentException e )
        {
        }
    }


    public void testLength()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;

        rule.length( 0 ) ;
        rule.length( 1 ) ;
        rule.length( 2 ) ;
        rule.length( 3 ) ;
        rule.length( 4 ) ;

        try
        {
            rule.length( -1 ) ;
            fail( "should never get here due to exception" ) ;
        }
        catch ( IllegalArgumentException e )
        {
        }

        try
        {
            rule.length( 5 ) ;
            fail( "should never get here due to exception" ) ;
        }
        catch ( IllegalArgumentException e )
        {
        }
    }


    public void testValue0()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 0 ) ;
        rule.value( null ) ;
    }


    public void testValue1()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 1 ) ;
        byte[] bites = { 0x45 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
    }


    public void testValue2()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 2 ) ;
        byte[] bites = { 0x45, 0x23 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
    }


    public void testValue2Fragmented()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 2 ) ;
        byte[] bites0 = { 0x45 } ;
        byte[] bites1 = { 0x23 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites0 ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf = ByteBuffer.wrap( bites1 ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
    }


    public void testValue3()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 3 ) ;
        byte[] bites = { 0x45, 0x23, 0x12 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
    }


    public void testValue4()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 4 ) ;
        byte[] bites = { 0x45, 0x23, 0x12, 0x01 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
    }


    public void testValue5()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 4 ) ;
        byte[] bites = { 0x45, 0x23, 0x12, 0x01, 0x07 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
    }


    public void testFinishNullDigester()
    {
        rule.setDigester( null ) ;
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 0 ) ;
        rule.value( null ) ;
        rule.finish() ;

        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testFinish0()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 0 ) ;
        rule.value( null ) ;
        rule.finish() ;

        assertEquals( 1, digester.getIntCount() ) ;
        assertEquals( 0, digester.popInt() ) ;
        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testFinish1()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 1 ) ;
        byte[] bites = { 0x45 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;

        rule.finish() ;
        assertEquals( 1, digester.getIntCount() ) ;
        assertEquals( 0x45, digester.popInt() ) ;
        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testFinish2()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 2 ) ;
        byte[] bites = { 0x45, 0x23 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;

        rule.finish() ;
        BigInteger big = new BigInteger( bites ) ;
        assertEquals( 1, digester.getIntCount() ) ;
        assertEquals( big.intValue(), digester.popInt() ) ;
        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testFinish2Fragmented()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 2 ) ;
        byte[] bites0 = { 0x45 } ;
        byte[] bites1 = { 0x23 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites0 ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf = ByteBuffer.wrap( bites1 ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        rule.finish() ;

        assertEquals( 1, digester.getIntCount() ) ;
        byte[] bitesUsed = { 0x45, 0x23 } ;
        BigInteger big = new BigInteger( bitesUsed ) ;
        assertEquals( big.intValue(), digester.popInt() ) ;
        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testFinish3()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 3 ) ;
        byte[] bites = { 0x45, 0x23, 0x12 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x00, 3 ) ) ;
        rule.finish() ;

        assertEquals( 1, digester.getIntCount() ) ;
        BigInteger big = new BigInteger( bites ) ;
        assertEquals( big.intValue(), digester.popInt() ) ;
        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testFinish4()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 4 ) ;
        byte[] bites = { 0x45, 0x23, 0x12, 0x01 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
        rule.finish() ;

        assertEquals( 1, digester.getIntCount() ) ;
        BigInteger big = new BigInteger( bites ) ;
        assertEquals( big.intValue(), digester.popInt() ) ;
        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testFinish5()
    {
        rule.tag( UniversalTag.INTEGER.getTagId(), true,
                TypeClass.UNIVERSAL ) ;
        rule.length( 4 ) ;
        byte[] bites = { 0x45, 0x23, 0x12, 0x01, 0x07 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
        buf.clear() ;
        rule.value( buf ) ;
        assertTrue( rule.equals( (byte) 0x45, 0 ) ) ;
        assertTrue( rule.equals( (byte) 0x23, 1 ) ) ;
        assertTrue( rule.equals( (byte) 0x12, 2 ) ) ;
        assertTrue( rule.equals( (byte) 0x01, 3 ) ) ;
        rule.finish() ;

        assertEquals( 1, digester.getIntCount() ) ;
        byte[] bitesUsed = { 0x45, 0x23, 0x12, 0x01 } ;
        BigInteger big = new BigInteger( bitesUsed ) ;
        assertEquals( big.intValue(), digester.popInt() ) ;
        assertEquals( 0, digester.getIntCount() ) ;
    }


    public void testByDecoding() throws Exception
    {
        byte[] data = { 0x30, 0x03, 0x02, 0x01, 0x0f } ;
        ByteBuffer buf = ByteBuffer.wrap( data ) ;
        digester.decode( buf ) ;
        assertEquals( 0x0f, digester.peekInt() ) ;
    }
}