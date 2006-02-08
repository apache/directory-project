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
package org.apache.asn1.ber;


import junit.framework.TestCase;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.asn1.codec.stateful.EncoderCallback;
import org.apache.asn1.codec.stateful.StatefulEncoder;
import org.apache.asn1.codec.stateful.StatefulEncoder;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.asn1.ber.BEREncoder;
import org.apache.commons.lang.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Tests the BEREncoder for correct operation.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class BEREncoderTest extends TestCase implements EncoderCallback
{
    private BEREncoder encoder = null;
    private ByteBuffer collector = null;


    protected void setUp() throws Exception
    {
        super.setUp();
        encoder = new BEREncoder();
        encoder.setCallback( this );
        collector = ByteBuffer.wrap( new byte[32] );
    }


    protected void tearDown() throws Exception
    {
        super.tearDown();
        encoder.setCallback( null );
        encoder = null;
        collector = null;
    }


    public void encodeOccurred( StatefulEncoder encoder, Object encoded )
    {
        ByteBuffer buf = ( ByteBuffer ) encoded;
        collector.put( buf );
    }



    /**
     * Produces a primitive tuple and pumps it through the encoder while
     * listening to the output of the encoder collecting the output bytes into
     * one buffer.  Then the collected data is compared with the expected
     * encoded data.
     */
    public void testPrimitives()
    {
        // prepare tlv and generate an integer tag event
        Tuple tlv = new Tuple();
        tlv.setTag( UniversalTag.INTEGER );
        encoder.tag( tlv );

        // generate a length event
        tlv.setLength( 1 );
        encoder.length( tlv );

        // generate a value event
        byte[] value = new byte[] { (byte) 10 };
        ByteBuffer chunk = ByteBuffer.wrap( value );
        tlv.setLastValueChunk( chunk );
        encoder.chunkedValue( tlv, chunk );

        // not really necessary but here for completeness
        encoder.finish( tlv );

        // generate the expected encoded bytes
        ArrayList list = new ArrayList();
        list.add( ByteBuffer.wrap( value ) );
        ByteBuffer buf = tlv.toEncodedBuffer( list );
        byte[] correctBytes = new byte[buf.remaining()];
        buf.get( correctBytes );

        // gather the collected encoded bytes
        collector.flip();
        byte[] encodedBytes = new byte[collector.remaining()];
        collector.get( encodedBytes );

        // compare the two
        assertTrue( ArrayUtils.isEquals( correctBytes, encodedBytes ) );
    }


    /**
     * Produces the tlv events for constructed TLV of definate length.
     */
    public void testConstructedDefinateLength1()
    {
        // prepare top level TLV of sequence with length of 3
        Tuple top = new Tuple();
        top.setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        encoder.tag( top );
        top.setLength( 3 );
        encoder.length( top );

        // prepare single nested child tlv
        Tuple tlv = new Tuple();
        tlv.setTag( UniversalTag.INTEGER );
        encoder.tag( tlv );
        tlv.setLength( 1 );
        encoder.length( tlv );
        byte[] value = new byte[] { (byte) 10 };
        ByteBuffer chunk = ByteBuffer.wrap( value );
        tlv.setLastValueChunk( chunk );
        encoder.chunkedValue( tlv, chunk );
        encoder.finish( tlv );
        encoder.finish( top );

        // prepare the expected correct sequence of encoded bytes
        ArrayList list = new ArrayList();
        ByteBuffer all = ByteBuffer.wrap( new byte[64] ) ;
        all.put( top.toEncodedBuffer( list ) );
        list.add( ByteBuffer.wrap( value ) );
        all.put( tlv.toEncodedBuffer( list ) );
        all.flip();
        byte[] correctBytes = new byte[all.remaining()];
        all.get( correctBytes );

        // gather the collected encoded bytes
        collector.flip();
        byte[] encodedBytes = new byte[collector.remaining()];
        collector.get( encodedBytes );

        // compare correct with encoded
        assertTrue( ArrayUtils.isEquals( correctBytes, encodedBytes ) );
    }


    /**
     * Produces the tlv events for constructed TLV of definate length.
     */
    public void testConstructedDefinateLength2()
    {
        // prepare top level TLV of sequence with length of 3
        Tuple top = new Tuple();
        top.setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        encoder.tag( top );
        top.setLength( 8 );
        encoder.length( top );

        // prepare the expected correct sequence of encoded bytes
        ArrayList list = new ArrayList();
        ByteBuffer all = ByteBuffer.wrap( new byte[64] ) ;
        all.put( top.toEncodedBuffer( list ) );

        // prepare single nested child tlv
        Tuple tlv = new Tuple();
        tlv.setTag( UniversalTag.INTEGER );
        encoder.tag( tlv );
        tlv.setLength( 1 );
        encoder.length( tlv );
        byte[] value = new byte[] { (byte) 10 };
        ByteBuffer chunk = ByteBuffer.wrap( value );
        tlv.setLastValueChunk( chunk );
        encoder.chunkedValue( tlv, chunk );
        encoder.finish( tlv );
        list.add( ByteBuffer.wrap( value ) );
        all.put( tlv.toEncodedBuffer( list ) );

        tlv.setTag( UniversalTag.INTEGER );
        encoder.tag( tlv );
        tlv.setLength( 3 );
        encoder.length( tlv );
        value = new byte[] { (byte) 2, 7, 12 };
        chunk = ByteBuffer.wrap( value );
        tlv.setLastValueChunk( chunk );
        encoder.chunkedValue( tlv, chunk );
        encoder.finish( tlv );
        encoder.finish( top );
        list.add( ByteBuffer.wrap( value ) );
        all.put( tlv.toEncodedBuffer( list ) );

        // prepare the correct buffers
        all.flip();
        byte[] correctBytes = new byte[all.remaining()];
        all.get( correctBytes );

        // gather the collected encoded bytes
        collector.flip();
        byte[] encodedBytes = new byte[collector.remaining()];
        collector.get( encodedBytes );

        // compare correct with encoded
        assertTrue( ArrayUtils.isEquals( correctBytes, encodedBytes ) );
    }


    /**
     * Produces the tlv events for constructed TLV of definate length.
     */
    public void testConstructedIndefiniteLength()
    {
        // prepare top level TLV of sequence with length of 3
        Tuple top = new Tuple();
        top.setTag( UniversalTag.SEQUENCE_SEQUENCE_OF, false );
        encoder.tag( top );
        top.setLength( Length.INDEFINITE );
        encoder.length( top );

        // prepare the expected correct sequence of encoded bytes
        ArrayList list = new ArrayList();
        ByteBuffer all = ByteBuffer.wrap( new byte[64] ) ;
        all.put( top.toEncodedBuffer( list ) );

        // prepare single nested child tlv
        Tuple tlv = new Tuple();
        tlv.setTag( UniversalTag.INTEGER );
        encoder.tag( tlv );
        tlv.setLength( 1 );
        encoder.length( tlv );
        byte[] value = new byte[] { (byte) 10 };
        ByteBuffer chunk = ByteBuffer.wrap( value );
        tlv.setLastValueChunk( chunk );
        encoder.chunkedValue( tlv, chunk );
        encoder.finish( tlv );
        list.add( ByteBuffer.wrap( value ) );
        all.put( tlv.toEncodedBuffer( list ) );

        tlv.setTag( UniversalTag.INTEGER );
        encoder.tag( tlv );
        tlv.setLength( 3 );
        encoder.length( tlv );
        value = new byte[] { (byte) 2, 7, 12 };
        chunk = ByteBuffer.wrap( value );
        tlv.setLastValueChunk( chunk );
        encoder.chunkedValue( tlv, chunk );
        encoder.finish( tlv );
        encoder.finish( top );
        list.add( ByteBuffer.wrap( value ) );
        all.put( tlv.toEncodedBuffer( list ) );
        all.put( (byte) 0 ).put( (byte) 0 );

        // prepare the correct buffers
        all.flip();
        byte[] correctBytes = new byte[all.remaining()];
        all.get( correctBytes );

        // gather the collected encoded bytes
        collector.flip();
        byte[] encodedBytes = new byte[collector.remaining()];
        collector.get( encodedBytes );

        // compare correct with encoded
        assertTrue( ArrayUtils.isEquals( correctBytes, encodedBytes ) );
    }
}
