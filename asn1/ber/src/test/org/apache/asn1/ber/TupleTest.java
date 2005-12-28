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
package org.apache.asn1.ber ;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.ArrayUtils ;
import org.apache.asn1.codec.binary.BinaryCodec;
import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.Tuple;

import junit.framework.TestCase ;


/**
 * Tests Tuple class.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TupleTest extends TestCase
{
    /** precalculated left shift of 1 by 14 places */
    private static final int BIT_13 = 1 << 14 ;
    /** precalculated left shift of 1 by 16 places */
    private static final int BIT_15 = 1 << 16 ;
    /** precalculated left shift of 1 by 21 places */
    private static final int BIT_20 = 1 << 21 ;
    /** precalculated left shift of 1 by 24 places */
    private static final int BIT_23 = 1 << 24 ;
    /** precalculated left shift of 1 by 28 places */
    private static final int BIT_27 = 1 << 28 ;

    /** for convenience in handling nulls */
    private static final ByteBuffer EMPTY_BUFFER =
        ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TupleTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for TupleTest.
     * @param arg0
     */
    public TupleTest(String arg0)
    {
        super(arg0);
    }

    /*
     * Class to test for void Tuple()
     */
    public void testTuple()
    {
        assertNotNull( new Tuple() ) ;
    }

    /*
     * Class to test for void Tuple(int)
     */
    public void testTupleint()
    {
        Tuple t0 = new Tuple( 0, 0 ) ;
        assertEquals( 0, t0.id ) ;
        Tuple t1 = new Tuple( 1, 0 ) ;
        assertEquals( 1, t1.id ) ;
        assertFalse( t0.equals(t1) ) ;
    }

    /*
     * Class to test for void Tuple(int, int)
     */
    public void testTupleintint()
    {
        Tuple t0 = new Tuple( 0, 0 ) ;
        assertEquals( 0, t0.id ) ;
        assertEquals( 0, t0.length ) ;
        Tuple t1 = new Tuple( 0, 1 ) ;
        assertEquals( 0, t1.id ) ;
        assertEquals( 1, t1.length ) ;
        assertFalse( t0.equals(t1) ) ;
    }

    /*
     * Class to test for void Tuple(int, int, TypeClass)
     */
    public void testTupleintintTypeClass()
    {
        Tuple t0 = new Tuple( 0, 0, TypeClass.PRIVATE ) ;
        assertEquals( 0, t0.id ) ;
        assertEquals( 0, t0.length ) ;
        assertEquals( TypeClass.PRIVATE, t0.getTypeClass() ) ;
        Tuple t1 = new Tuple( 0, 1, null ) ;
        assertEquals( 0, t1.id ) ;
        assertEquals( 1, t1.length ) ;
        assertFalse( t0.equals(t1) ) ;
        assertEquals( TypeClass.APPLICATION, t1.getTypeClass() ) ;
    }

    /*
     * Class to test for void Tuple(int, int, TypeClass, boolean, byte[])
     */
    public void testTupleintTypeClass()
    {
        Tuple t = new Tuple( 2, TypeClass.PRIVATE ) ;
        assertEquals( 2, t.getId() ) ;
        assertEquals( TypeClass.PRIVATE, t.getTypeClass() ) ;
        assertEquals( false, t.isPrimitive() ) ;
        assertEquals( Length.INDEFINITE, t.getLength() ) ;
        assertEquals( EMPTY_BUFFER, t.getLastValueChunk() ) ;

        t = new Tuple( 2, (TypeClass) null ) ;
        assertEquals( 2, t.getId() ) ;
        assertEquals( TypeClass.APPLICATION, t.getTypeClass() ) ;
        assertEquals( false, t.isPrimitive() ) ;
        assertEquals( Length.INDEFINITE, t.getLength() ) ;
        assertEquals( EMPTY_BUFFER, t.getLastValueChunk() ) ;
    }

    public void testGetId()
    {
        Tuple t = new Tuple() ;
        assertEquals( 0, t.getId() ) ;
        t = new Tuple( 2, 0 ) ;
        assertEquals( 2, t.getId() ) ;
        t.id = 21 ;
        assertEquals( 21, t.getId() ) ;
    }


    public void testSize()
    {
        Tuple t = new Tuple( 1, TypeClass.APPLICATION ) ;
        assertEquals( 2, t.size() ) ;
        t.id = 32 ;
        assertEquals( 3, t.size() ) ;
        t.id = 127 ;
        assertEquals( 4, t.size() ) ;
        t.id = 128 ;
        assertEquals( 4, t.size() ) ;
        t.id = 1 << 14 ;
        assertEquals( 5, t.size() ) ;
        t.id = 1 << 21 ;
        assertEquals( 6, t.size() ) ;

        t.length = 127 ;
        assertEquals( 6+127, t.size() ) ;
        t.length = 128 ;
        assertEquals( 7+128, t.size() ) ;
        t.length = 255 ;
        assertEquals( 7+255, t.size() ) ;
        t.length = 256 ;
        assertEquals( 8+256, t.size() ) ;
    }


    public void testIsIndefinite()
    {
        Tuple t = new Tuple() ;
        assertFalse( t.isIndefinite() ) ;
        t.length = Length.INDEFINITE ;
        assertTrue( t.isIndefinite() ) ;
    }


    public void testIsIndefiniteTerminator()
    {
        Tuple t = new Tuple() ;
        assertFalse( t.isIndefiniteTerminator() ) ;
        t.id = 0 ;
        t.length = 0 ;
        t.isPrimitive = true ;
        t.typeClass = TypeClass.UNIVERSAL ;
        assertTrue( t.isIndefiniteTerminator() ) ;
    }


    public void testIsPrimitive()
    {
        Tuple t = new Tuple() ;
        assertTrue( t.isPrimitive() ) ;
        t.isPrimitive = false ;
        assertFalse( t.isPrimitive() ) ;
    }

    public void testGetLength()
    {
        Tuple t = new Tuple() ;
        assertEquals( 0, t.getLength() ) ;
        t = new Tuple( 1, 2 ) ;
        assertEquals( 2, t.getLength() ) ;
        t.length = 21 ;
        assertEquals( 21, t.getLength() ) ;
    }

    public void testGetTypeClass()
    {
        Tuple t = new Tuple() ;
        assertEquals( t.typeClass, TypeClass.APPLICATION ) ;
        t = new Tuple( 0, 0 ) ;
        assertEquals( TypeClass.APPLICATION, t.getTypeClass() ) ;
        t.typeClass = TypeClass.PRIVATE ;
        assertEquals( TypeClass.PRIVATE, t.getTypeClass() ) ;
    }

    public void testGetValue()
    {
        Tuple t = new Tuple() ;
        assertEquals( EMPTY_BUFFER, t.getLastValueChunk() ) ;
        byte[] bites = {1, 2, 3, 45} ;
        t.valueChunk = ByteBuffer.wrap( bites ) ;
        assertEquals( ByteBuffer.wrap( bites ), t.getLastValueChunk() ) ;
        t.clear() ;
        assertEquals( EMPTY_BUFFER, t.getLastValueChunk() ) ;
    }

    public void testClear()
    {
        Tuple t = new Tuple() ;
        t.id = 12 ;
        assertEquals( 12, t.id ) ;
        t.clear() ;
        assertEquals( 0, t.id ) ;

        t.length = 12 ;
        assertEquals( 12, t.length ) ;
        t.clear() ;
        assertEquals( Length.UNDEFINED, t.length ) ;

        t.index = 12 ;
        assertEquals( 12, t.index ) ;
        t.clear() ;
        assertEquals( 0, t.index ) ;

        t.isPrimitive = false ;
        assertEquals( false, t.isPrimitive ) ;
        t.clear() ;
        assertEquals( true, t.isPrimitive ) ;

        t.typeClass = TypeClass.CONTEXT_SPECIFIC ;
        assertEquals( TypeClass.CONTEXT_SPECIFIC, t.typeClass ) ;
        t.clear() ;
        assertEquals( TypeClass.APPLICATION, t.typeClass ) ;

        t.valueChunk = ByteBuffer.allocate( 3 ) ;
        assertNotNull( t.valueChunk ) ;
        t.clear() ;
        assertEquals( EMPTY_BUFFER, t.valueChunk ) ;

        t.valueIndex = 12 ;
        assertEquals( 12, t.valueIndex ) ;
        t.clear() ;
        assertEquals( Length.UNDEFINED, t.valueIndex ) ;

    }

    /*
     * Class to test for boolean equals(Object)
     */
    public void testEqualsObject()
    {
        Tuple tnull0 = new Tuple() ;
        tnull0.valueChunk = null ;
        Tuple tnull1 = new Tuple() ;
        tnull1.valueChunk = null ;
        tnull0.equals( tnull1 ) ;

        tnull1.equals( tnull1 ) ;
        tnull0.equals( tnull0 ) ;

        Tuple t0 = new Tuple() ;
        Tuple t1 = ( Tuple ) t0.clone() ;

        assertTrue( t0.equals( t1 ) ) ;
        t0.id = 23 ;
        assertFalse( t0.equals( t1 ) ) ;
        t1 = ( Tuple ) t0.clone() ;
        assertTrue( t0.equals( t1 ) ) ;

        // indices are not taken into account in Tuple.equals(Object)
        t0.index = 23 ;
        t1.index = 33 ;
        assertTrue( t0.equals( t1 ) ) ;
        t1 = ( Tuple ) t0.clone() ;
        assertTrue( t0.equals( t1 ) ) ;

        t0.isPrimitive = false ;
        t1.isPrimitive = true ;
        assertFalse( t0.equals( t1 ) ) ;
        t1 = ( Tuple ) t0.clone() ;
        assertTrue( t0.equals( t1 ) ) ;

        t0.length = 23 ;
        assertFalse( t0.equals( t1 ) ) ;
        t1 = ( Tuple ) t0.clone() ;
        assertTrue( t0.equals( t1 ) ) ;

        t0.typeClass = TypeClass.PRIVATE ;
        t1.typeClass = TypeClass.UNIVERSAL ;
        assertFalse( t0.equals( t1 ) ) ;
        t1 = ( Tuple ) t0.clone() ;
        assertTrue( t0.equals( t1 ) ) ;

        // indices are not taken into account in Tuple.equals(Object)
        t0.valueIndex = 23 ;
        t1.valueIndex = 3 ;
        assertTrue( t0.equals( t1 ) ) ;
        t1 = ( Tuple ) t0.clone() ;
        assertTrue( t0.equals( t1 ) ) ;

        t0.valueChunk = ByteBuffer.allocate( 4 ) ;
        t1.valueChunk = null ;

        // The buffer does not factor into equality
        assertTrue( t0.equals( t1 ) ) ;

        t1 = ( Tuple ) t0.clone() ;
        assertTrue( t0.equals( t1 ) ) ;

        assertFalse( t0.equals( new Object() )) ;
     }

    /*
     * Class to test for Object clone()
     */
    public void testClone()
    {
        Tuple t = new Tuple() ;
        assertTrue( t.equals( t.clone() ) ) ;
    }

    public void testToEncodedBufferConstructed()
    {
        Tuple t = null ;
        ByteBuffer encoded ;

        t = new Tuple( 0, 0 ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "01100000"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 2, 0 ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "01100010"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 30, 0 ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "01111110"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 31, 0 ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "00011111" +
                "01111111"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 128, 0 ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "00000000" +
                "10000001" +
                "01111111"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 128, 127 ) ;
        ArrayList list = new ArrayList() ;
        list.add( ByteBuffer.allocate( 127 ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals(
                "01111111" +
                "00000000" +
                "10000001" +
                "01111111"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 128, 128 ) ;
        list.clear() ;
        list.add( ByteBuffer.allocate( 128 ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals(
                "10000000" +
                "10000001" +
                "00000000" +
                "10000001" +
                "01111111"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 128, 255 ) ;
        list.clear() ;
        list.add( ByteBuffer.allocate( 255 ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals(
                "11111111" +
                "10000001" +
                "00000000" +
                "10000001" +
                "01111111"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 128, 256 ) ;
        list.clear() ;
        list.add( ByteBuffer.allocate( 256 ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals(
                "00000000" +
                "00000001" +
                "10000010" +
                "00000000" +
                "10000001" +
                "01111111"
                , toAsciiString( encoded ) ) ;
    }

    public void testToEncodedBufferPrimitive()
    {
        Tuple t = null ;
        ByteBuffer encoded ;
        byte[] data ;

        t = new Tuple( 0, 0, true, TypeClass.APPLICATION ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "01000000"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 2, 0, true, TypeClass.APPLICATION ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "01000010"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 30, 0, true, TypeClass.APPLICATION ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "01011110"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 31, 0, true, TypeClass.APPLICATION ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "00011111" +
                "01011111"
                , toAsciiString( encoded ) ) ;

        t = new Tuple( 128, 0, true, TypeClass.APPLICATION ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        assertEquals(
                "00000000" +
                "00000000" +
                "10000001" +
                "01011111"
                , toAsciiString( encoded ) ) ;

        data = new byte[1] ;
        t = new Tuple( 128, 1, true, TypeClass.APPLICATION ) ;
        ArrayList list = new ArrayList() ;
        list.add( ByteBuffer.wrap( data ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals(
                "00000000" +
                "00000001" +
                "00000000" +
                "10000001" +
                "01011111"
                , toAsciiString( encoded ) ) ;

        data = new byte[127] ;
        t = new Tuple( 128, 127, true, TypeClass.APPLICATION ) ;
        list.clear() ;
        list.add( ByteBuffer.wrap( data ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals( BinaryCodec.toAsciiString( data ) +
                "01111111" +
                "00000000" +
                "10000001" +
                "01011111"
                , toAsciiString( encoded ) ) ;

        data = new byte[128] ;
        t = new Tuple( 128, 128, true, TypeClass.APPLICATION ) ;
        list.clear() ;
        list.add( ByteBuffer.wrap( data ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals( BinaryCodec.toAsciiString( data ) +
                "10000000" +
                "10000001" +
                "00000000" +
                "10000001" +
                "01011111"
                , toAsciiString( encoded ) ) ;

        data = new byte[255] ;
        t = new Tuple( 128, 255, true, TypeClass.APPLICATION ) ;
        list.clear() ;
        list.add( ByteBuffer.wrap( data ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals( BinaryCodec.toAsciiString( data ) +
                "11111111" +
                "10000001" +
                "00000000" +
                "10000001" +
                "01011111"
                , toAsciiString( encoded ) ) ;

        data = new byte[256] ;
        t = new Tuple( 128, 256, true, TypeClass.APPLICATION ) ;
        list.clear() ;
        list.add( ByteBuffer.wrap( data ) ) ;
        encoded = t.toEncodedBuffer( list ) ;
        assertEquals( BinaryCodec.toAsciiString( data ) +
                "00000000" +
                "00000001" +
                "10000010" +
                "00000000" +
                "10000001" +
                "01011111"
                , toAsciiString( encoded ) ) ;
    }


    public String toAsciiString( ByteBuffer buf )
    {
        return BinaryCodec.toAsciiString( buf.array() ) ;
    }


    public void testSetTagBufferint()
    {
        ByteBuffer bites = ByteBuffer.allocate( 1 ) ;
        Tuple t = new Tuple( 0, 0 ) ;
        t.setTag( bites, 1 ) ;
        String binary = toAsciiString( bites ) ;
        assertEquals( "01100000", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 0 ) ;
        t.setTag( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111110", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 0 ) ;
        t.isPrimitive = true ;
        t.setTag( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01011110", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 31, 0 ) ;
        t.setTag( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00011111" + "01111111", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 127, 0 ) ;
        t.setTag( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" + "01111111", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( 128, 0 ) ;
        t.setTag( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" + "10000001" + "01111111", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( BIT_13 - 1, 0 ) ;
        t.setTag( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" +
                      "11111111" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( BIT_13, 0 ) ;
        t.setTag( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( BIT_13 + 1, 0 ) ;
        t.setTag( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( BIT_20 - 1, 0 ) ;
        t.setTag( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" +
                      "11111111" +
                      "11111111" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( BIT_20, 0 ) ;
        t.setTag( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" +
                      "10000000" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( BIT_20 + 1, 0 ) ;
        t.setTag( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" +
                      "10000000" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( BIT_27 - 1, 0 ) ;
        t.setTag( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" +
                      "11111111" +
                      "11111111" +
                      "11111111" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 6 ) ;
        t = new Tuple( BIT_27, 0 ) ;

        try
        {
            t.setTag( bites, 6 ) ;
            fail( "should never reach this point due to thrown exception" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }

    public void testSetTagbyteArrayint()
    {
        ByteBuffer bites = ByteBuffer.allocate( 1 ) ;
        Tuple t = new Tuple( 0, 0 ) ;
        t.setTag( bites, 1 ) ;
        String binary = toAsciiString( bites ) ;
        assertEquals( "01100000", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 0 ) ;
        t.setTag( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111110", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 0 ) ;
        t.isPrimitive = true ;
        t.setTag( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01011110", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 31, 0 ) ;
        t.setTag( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00011111" + "01111111", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 127, 0 ) ;
        t.setTag( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" + "01111111", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( 128, 0 ) ;
        t.setTag( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" + "10000001" + "01111111", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( BIT_13 - 1, 0 ) ;
        t.setTag( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" +
                      "11111111" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( BIT_13, 0 ) ;
        t.setTag( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( BIT_13 + 1, 0 ) ;
        t.setTag( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( BIT_20 - 1, 0 ) ;
        t.setTag( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" +
                      "11111111" +
                      "11111111" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( BIT_20, 0 ) ;
        t.setTag( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" +
                      "10000000" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( BIT_20 + 1, 0 ) ;
        t.setTag( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" +
                      "10000000" +
                      "10000000" +
                      "10000001" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( BIT_27 - 1, 0 ) ;
        t.setTag( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111" +
                      "11111111" +
                      "11111111" +
                      "11111111" +
                      "01111111", binary ) ;

        bites = ByteBuffer.allocate( 6 ) ;
        t = new Tuple( BIT_27, 0 ) ;

        try
        {
            t.setTag( bites, 6 ) ;
            fail( "should never reach this point due to thrown exception" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }


    String toAsciiString( int raw )
    {
        byte[] intBytes = new byte[4] ;
        intBytes[0] = (byte) ( (int) 0x000000ff & raw ) ;
        intBytes[1] = (byte) ( (int) ( 0x0000ff00 & raw ) >> 8 ) ;
        intBytes[2] = (byte) ( (int) ( 0x00ff0000 & raw ) >> 16 ) ;
        intBytes[3] = (byte) ( (int) ( 0xff000000 & raw ) >> 24 ) ;

        return BinaryCodec.toAsciiString( intBytes ) ;
    }


    public void testSetLengthBuffer()
    {
        ByteBuffer bites = ByteBuffer.allocate( 1 ) ;
        Tuple t = new Tuple( 0, 0 ) ;
        t.setLength( bites, 1 ) ;
        String binary = toAsciiString( bites ) ;
        assertEquals( "00000000", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 15 ) ;
        t.setLength( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00001111", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 127 ) ;
        t.setLength( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 30, 128 ) ;
        t.setLength( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "10000000" + "10000001", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 30, 255 ) ;
        t.setLength( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" + "10000001", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( 30, 256 ) ;
        t.setLength( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" + "00000001" + "10000010", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( 30, BIT_15 - 1 ) ;
        t.setLength( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" +
                      "11111111" + "10000010", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( 30, BIT_15 ) ;
        t.setLength( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" + "00000000" + "00000001" + "10000011", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( 30, BIT_15 + 1 ) ;
        t.setLength( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" +
                      "00000000" +
                      "00000001" + "10000011", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( 30, BIT_23 - 1 ) ;
        t.setLength( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" +
                      "11111111" +
                      "11111111" + "10000011", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( 30, BIT_23 ) ;
        t.setLength( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" +
                      "00000000" +
                      "00000000" +
                      "00000001" +
                      "10000100", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( 30, BIT_23 + 1 ) ;
        t.setLength( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" +
                      "00000000" +
                      "00000000" +
                      "00000001" + "10000100", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( 30, Integer.MAX_VALUE ) ;
        t.setLength( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" +
                      "11111111" +
                      "11111111" +
                      "01111111" + "10000100", binary ) ;


        bites = ByteBuffer.allocate( 6 ) ;
        t = new Tuple( 30, Integer.MAX_VALUE + 1 ) ;

        try
        {
            t.setLength( bites, 6 ) ;
            fail( "should never get here due to thrown exception" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }

    public void testSetLengthbyteArrayintint()
    {
        ByteBuffer bites = ByteBuffer.allocate( 1 ) ;
        Tuple t = new Tuple( 0, 0 ) ;
        t.setLength( bites, 1 ) ;
        String binary = toAsciiString( bites ) ;
        assertEquals( "00000000", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 15 ) ;
        t.setLength( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00001111", binary ) ;

        bites = ByteBuffer.allocate( 1 ) ;
        t = new Tuple( 30, 127 ) ;
        t.setLength( bites, 1 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "01111111", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 30, 128 ) ;
        t.setLength( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "10000000" + "10000001", binary ) ;

        bites = ByteBuffer.allocate( 2 ) ;
        t = new Tuple( 30, 255 ) ;
        t.setLength( bites, 2 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" + "10000001", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( 30, 256 ) ;
        t.setLength( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" +
                      "00000001" + "10000010", binary ) ;

        bites = ByteBuffer.allocate( 3 ) ;
        t = new Tuple( 30, BIT_15 - 1 ) ;
        t.setLength( bites, 3 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" + "11111111" + "10000010", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( 30, BIT_15 ) ;
        t.setLength( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" + "00000000" +
                      "00000001" + "10000011", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( 30, BIT_15 + 1 ) ;
        t.setLength( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" + "00000000" +
                      "00000001" + "10000011", binary ) ;

        bites = ByteBuffer.allocate( 4 ) ;
        t = new Tuple( 30, BIT_23 - 1 ) ;
        t.setLength( bites, 4 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" + "11111111" +
                      "11111111" + "10000011", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( 30, BIT_23 ) ;
        t.setLength( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000000" +
                      "00000000" +
                      "00000000" +
                      "00000001" + "10000100", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( 30, BIT_23 + 1 ) ;
        t.setLength( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "00000001" +
                      "00000000" +
                      "00000000" +
                      "00000001" + "10000100", binary ) ;

        bites = ByteBuffer.allocate( 5 ) ;
        t = new Tuple( 30, Integer.MAX_VALUE ) ;
        t.setLength( bites, 5 ) ;
        binary = toAsciiString( bites ) ;
        assertEquals( "11111111" +
                      "11111111" +
                      "11111111" +
                      "01111111" + "10000100", binary ) ;


        bites = ByteBuffer.allocate( 6 ) ;
        t = new Tuple( 30, Integer.MAX_VALUE + 1 ) ;

        try
        {
            t.setLength( bites, 6 ) ;
            fail( "should never get here due to thrown exception" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }

    public void testGetTagLength()
    {
        Tuple t = new Tuple() ;
        assertEquals( 1, t.getTagLength() ) ;
        t.id = 30 ;
        assertEquals( 1, t.getTagLength() ) ;
        t.id = 31 ;
        assertEquals( 2, t.getTagLength() ) ;
        t.id = 100 ;
        assertEquals( 3, t.getTagLength() ) ;
        t.id = 127 ;
        assertEquals( 3, t.getTagLength() ) ;
        t.id = 128 ;
        assertEquals( 3, t.getTagLength() ) ;
        t.id = 129 ;
        assertEquals( 3, t.getTagLength() ) ;

        t.id = BIT_13 - 1 ;
        assertEquals( 3, t.getTagLength() ) ;
        t.id = BIT_13 ;
        assertEquals( 4, t.getTagLength() ) ;
        t.id = BIT_13 + 100 ;
        assertEquals( 4, t.getTagLength() ) ;

        t.id = BIT_20 - 1 ;
        assertEquals( 4, t.getTagLength() ) ;
        t.id = BIT_20 ;
        assertEquals( 5, t.getTagLength() ) ;
        t.id = BIT_20 + 100 ;
        assertEquals( 5, t.getTagLength() ) ;

        t.id = BIT_27 - 1 ;
        assertEquals( 5, t.getTagLength() ) ;

        t.id = BIT_27 ;

        try
        {
            assertEquals( 6, t.getTagLength() ) ;
            fail( "should throw an exception before getting here" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }

    public void testGetLengthLength()
    {
        Tuple t = new Tuple() ;
        assertEquals( 1, t.getLengthLength() ) ;
        t.length = 127 ;
        assertEquals( 1, t.getLengthLength() ) ;
        t.length = 128 ;
        assertEquals( 2, t.getLengthLength() ) ;
        t.length = 255 ;
        assertEquals( 2, t.getLengthLength() ) ;
        t.length = 256 ;
        assertEquals( 3, t.getLengthLength() ) ;

        t.length = BIT_15 - 1 ;
        assertEquals( 3, t.getLengthLength() ) ;
        t.length = BIT_15 ;
        assertEquals( 4, t.getLengthLength() ) ;
        t.length = BIT_15 + 100 ;
        assertEquals( 4, t.getLengthLength() ) ;

        t.length = BIT_23 - 1 ;
        assertEquals( 4, t.getLengthLength() ) ;
        t.length = BIT_23 ;
        assertEquals( 5, t.getLengthLength() ) ;
        t.length = BIT_23 + 100 ;
        assertEquals( 5, t.getLengthLength() ) ;

        t.length = Integer.MAX_VALUE ;
        assertEquals( 5, t.getLengthLength() ) ;


        t.length = Integer.MAX_VALUE + 1 ;
        try
        {
            assertEquals( 6, t.getLengthLength() ) ;
            fail( "should throw an exception before getting here" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }

    }
}
