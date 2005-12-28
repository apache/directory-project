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


import java.nio.ByteBuffer ;
import java.util.ArrayList ;

import org.apache.commons.lang.ArrayUtils ;
import org.apache.commons.lang.RandomStringUtils ;

import org.apache.asn1.codec.stateful.StatefulDecoder ;
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitorAdapter;
import org.apache.asn1.codec.stateful.*;
import org.apache.asn1.ber.AbstractDecoderTestCase;
import org.apache.asn1.ber.BERDecoderMonitor;


/**
 * Tests the decoder using various complext TLV decoding scenarios and performs
 * round trip encode - decode functionality.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDecoderTest extends AbstractDecoderTestCase
{
    private static final ByteBuffer EMPTY_BUFFER =
        ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;


    public BERDecoderTest()
    {
        super( BERDecoderTest.class.getName() ) ;
    }


    public void testBasisCases() throws Exception
    {
        decoder.setDecoderMonitor( new DecoderMonitorAdapter() ) ;
        decoder.decode( null ) ;
        decoder.decode( EMPTY_BUFFER ) ;
    }


    public void testPrimitives() throws Exception
    {
        Tuple decoded = null ;
        Tuple t = new Tuple( 45, 0, true, TypeClass.APPLICATION ) ;
        assertTrue( decode( t, EMPTY_BUFFER ).equals( t ) ) ;

        t = new Tuple( 45, "Hello world!".length(), true,
                TypeClass.APPLICATION ) ;
        decoded = decode( t, ByteBuffer.wrap( "Hello world!".getBytes() ) ) ;
        assertTrue( decoded.equals( t ) ) ;
        assertEquals( "Hello world!", toString( decoded.getLastValueChunk() ) ) ;

        String mesg = RandomStringUtils.randomAlphanumeric(1000) ;
        t = new Tuple( 1234233, mesg.length(), true, TypeClass.APPLICATION ) ;
        decoded = decode( t, ByteBuffer.wrap( mesg.getBytes() ) ) ;
        assertTrue( decoded.equals( t ) ) ;
        assertEquals( mesg, toString( decoded.getLastValueChunk() ) ) ;
    }


    String toString(ByteBuffer buf)
    {
        buf = buf.slice() ;
        byte[] bites = new byte[buf.remaining()] ;
        buf.get( bites ) ;
        return new String( bites ) ;
    }


    public void testConstructedIndefinite() throws Exception
    {
        Tuple top = new Tuple( 1, TypeClass.APPLICATION ) ;
        Tuple t0 = new Tuple( 2, "Hello".length(),
                true, TypeClass.APPLICATION ) ;
        Tuple t1 = new Tuple( 3, "World".length(),
                true, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;

        Tuple decoded = decode( t0, ByteBuffer.wrap( "Hello".getBytes() ) ) ;
        assertTrue( decoded.equals( t0 ) ) ;
        assertEquals( "Hello", toString( decoded.getLastValueChunk() ) ) ;

        decoded = decode( t1, ByteBuffer.wrap( "World".getBytes() ) ) ;
        assertTrue( decoded.equals( t1 ) ) ;
        assertEquals( "World", toString( decoded.getLastValueChunk() ) ) ;

        decoded = decode( terminator, EMPTY_BUFFER ) ;
        assertTrue( decoded.equals( top ) ) ;
    }


    public void testConstructedLongLengthForm() throws Exception
    {
        String str0 = RandomStringUtils.randomAlphanumeric(128) ;
        Tuple t0 = new Tuple( 2, 128, true, TypeClass.APPLICATION ) ;
        String str1 = RandomStringUtils.randomAlphanumeric(128) ;
        Tuple t1 = new Tuple( 3, 128, true, TypeClass.APPLICATION ) ;
        Tuple top = new Tuple( 1, t0.size() + t1.size() ) ;
        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;

        Tuple decoded = decode( t0, ByteBuffer.wrap( str0.getBytes() ) ) ;
        assertTrue( decoded.equals( t0 ) ) ;
        assertEquals( str0, toString( decoded.getLastValueChunk() ) ) ;

        // automatically set to top because after t1 is delivered top is
        decoded = decode( t1, ByteBuffer.wrap( str1.getBytes() ) ) ;
        assertTrue( decoded.equals( top ) ) ;
    }


    public void testConstructedShortLengthForm() throws Exception
    {
        Tuple t0 = new Tuple( 2, "Hello".length(), true,
                TypeClass.APPLICATION ) ;
        Tuple t1 = new Tuple( 3, "World".length(), true,
                TypeClass.APPLICATION ) ;
        Tuple top = new Tuple( 1, t0.size() + t1.size() ) ;
        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;

        Tuple decoded = decode( t0, ByteBuffer.wrap( "Hello".getBytes() ) ) ;
        assertTrue( decoded.equals( t0 ) ) ;
        assertEquals( "Hello", toString( decoded.getLastValueChunk() ) ) ;

        // automatically set to top because after t1 is delivered top is
        decoded = decode( t1, ByteBuffer.wrap( "World".getBytes() ) ) ;
        assertTrue( decoded.equals( top ) ) ;
    }


    public void testFragmentedValue() throws Exception
    {
        String str0 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t0 = new Tuple( 2, str0.length(), true, TypeClass.APPLICATION ) ;
        String str1 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t1 = new Tuple( 3, str1.length(), true, TypeClass.APPLICATION ) ;
        Tuple top = new Tuple( 1, t0.size() + t1.size() ) ;
        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;

        ArrayList list = new ArrayList() ;
        list.add( ByteBuffer.wrap( str0.getBytes() ) ) ;
        ByteBuffer all = t0.toEncodedBuffer( list ) ;
        ByteBuffer[] fragments = fragment( all, 10 ) ;
        Tuple decoded = null ;

        for ( int ii = 0; ii < fragments.length; ii++ )
        {
            decoded = decode( fragments[ii] ) ;
        }

        assertTrue( decoded.equals( t0 ) ) ;
        assertEquals( str0, toString( buf ) ) ;

        // automatically set to top because after t1 is delivered top is
        decoded = decode( t1, ByteBuffer.wrap( str1.getBytes() ) ) ;
        assertTrue( decoded.equals( top ) ) ;
    }


    public void testDecodeOccurred()
    {
        try
        {
            decoder.decodeOccurred( null, null ) ;
            fail( "should never get here due to exception being thrown" ) ;
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }


    public void testFireTagDecoded() throws Exception
    {
        decoder.setDecoderMonitor( new BERMonitor() ) ;
        String str0 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t0 = new Tuple( 2, str0.length() ) ;
        String str1 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t1 = new Tuple( 3, str1.length() ) ;
        Tuple top = new Tuple( 1, t0.size() + t1.size() ) ;
        Tuple decoded = decode( t0, ByteBuffer.wrap( str0.getBytes() ) ) ;
        assertTrue( decoded.equals( t0 ) ) ;

        // automatically set to top because after t1 is delivered top is
        decoded = decode( t1, ByteBuffer.wrap( str1.getBytes() ) ) ;

        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;

        decoder.setDecoderMonitor(null) ;
        decoded = decode( t0, ByteBuffer.wrap( str0.getBytes() ) ) ;
        assertTrue( decoded.equals( t0 ) ) ;

        tlvList.clear() ;
        decoder.setDecoderMonitor(null) ;
        decoder.setCallback(null) ;
        decoded = decode( t0, ByteBuffer.wrap( str0.getBytes() ) ) ;
        assertTrue(tlvList.isEmpty()) ;
    }


    public void testFireTagDecoded2() throws Exception
    {
        decoder.setDecoderMonitor( null ) ;
        String str0 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t0 = new Tuple( 2, str0.length() ) ;
        String str1 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t1 = new Tuple( 3, str1.length() ) ;
        Tuple top = new Tuple( 1, t0.size() + t1.size() ) ;
        Tuple decoded = decode( t0, ByteBuffer.wrap( str0.getBytes() ) ) ;
        assertTrue( decoded.equals( t0 ) ) ;

        // automatically set to top because after t1 is delivered top is
        decoded = decode( t1, ByteBuffer.wrap( str1.getBytes() ) ) ;
        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;
    }


    public void testFireTagDecoded3() throws Exception
    {
        decoder.setDecoderMonitor( new BERMonitor() ) ;
        decoder.setCallback( null ) ;
        String str0 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t0 = new Tuple( 2, str0.length() ) ;
        String str1 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t1 = new Tuple( 3, str1.length() ) ;
        Tuple top = new Tuple( 1, t0.size() + t1.size() ) ;
        decode( t0, ByteBuffer.wrap( str0.getBytes() ) ) ;

        // automatically set to top because after t1 is delivered top is
        decode( t1, ByteBuffer.wrap( str1.getBytes() ) ) ;
        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;
    }


    public void testFireTagDecoded4() throws Exception
    {
        decoder.setDecoderMonitor( null ) ;
        decoder.setCallback( null ) ;
        String str0 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t0 = new Tuple( 2, str0.length() ) ;
        String str1 = RandomStringUtils.randomAlphanumeric(20) ;
        Tuple t1 = new Tuple( 3, str1.length() ) ;
        Tuple top = new Tuple( 1, t0.size() + t1.size() ) ;
        decode( t0, ByteBuffer.wrap( str0.getBytes() ) );

        // automatically set to top because after t1 is delivered top is
        decode( t1, ByteBuffer.wrap( str1.getBytes() ) ) ;
        assertTrue( decode( top, EMPTY_BUFFER ).equals( top ) ) ;
    }


    class BERMonitor implements BERDecoderMonitor
    {
        public void callbackOccured(StatefulDecoder decoder,
                DecoderCallback cb, Object decoded) { }

        public void error(StatefulDecoder decoder, Exception exception) { }

        public void callbackSet(StatefulDecoder decoder, DecoderCallback oldcb,
                DecoderCallback newcb) { }

        public void fatalError(StatefulDecoder decoder, Exception exception){}

        public void lengthDecoded( Tuple tlv ) { }

        public void tagDecoded( Tuple tlv ) { }

        public void warning( StatefulDecoder decoder, Exception exception ) { }
    }
}
