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
package org.apache.asn1.ber ;


import junit.framework.TestCase ;

import java.nio.ByteBuffer ;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.ber.Length;


/**
 * Tests the Length class.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class LengthTest extends TestCase
{
    /**
     * Tests the long form when a byte is used for the length's length and
     * another is used for the value length for a total of two bytes for the
     * length field itself.
     */
    public void testLongTwoBytes() throws DecoderException
    {
        ByteBuffer list = ByteBuffer.allocate( 2 ) ;
        list.put( (byte) 0x81 ) ;
        list.put( (byte) 0x01 ) ;
        list.flip();
        assertEquals( 0x01, Length.getLength( list ) );

        list = ByteBuffer.allocate( 2 ) ;
        list.put( (byte) 0x81 ) ;
        list.put( (byte) 0x05 ) ;
        list.flip();
        assertEquals( 0x05, Length.getLength( list ) );

        list = ByteBuffer.allocate( 2 ) ;
        list.put( (byte) 0x81 ) ;
        list.put( (byte) 0xFF ) ;
        list.flip();
        assertEquals( 0xFF, Length.getLength( list ) );
    }


    /**
     * Tests the long form when a byte is used for the length's length and
     * two more are used for the value length for a total of three bytes for the
     * length field itself.
     */
    public void testLongThreeBytes() throws DecoderException
    {
        ByteBuffer list = ByteBuffer.allocate( 3 ) ;
        list.put( (byte) 0x82 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x01 ) ;
        list.flip();
        assertEquals( 0x01, Length.getLength( list ) );

        list = ByteBuffer.allocate( 3 ) ;
        list.put( (byte) 0x82 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x05 ) ;
        list.flip();
        assertEquals( 0x05, Length.getLength( list ) );

        list = ByteBuffer.allocate( 3 ) ;
        list.put( (byte) 0x82 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0xFF ) ;
        list.flip();
        assertEquals( 0xFF, Length.getLength( list ) );

        list = ByteBuffer.allocate( 3 ) ;
        list.put( (byte) 0x82 ) ;
        list.put( (byte) 0x01 ) ;
        list.put( (byte) 0x05 ) ;
        list.flip();
        assertEquals( 0x0105, Length.getLength( list ) );

        list = ByteBuffer.allocate( 3 ) ;
        list.put( (byte) 0x82 ) ;
        list.put( (byte) 0x01 ) ;
        list.put( (byte) 0xFF ) ;
        list.flip();
        assertEquals( 0x01FF, Length.getLength( list ) );

        list = ByteBuffer.allocate( 3 ) ;
        list.put( (byte) 0x82 ) ;
        list.put( (byte) 0x80 ) ;
        list.put( (byte) 0x00 ) ;
        list.flip();
        assertEquals( 32768, Length.getLength( list ) );
    }


    /**
     * Tests the long form when a byte is used for the length's length and
     * three more are used for the value length for a total of four bytes for
     * the length field itself.
     */
    public void testLongFourBytes() throws DecoderException
    {
        ByteBuffer list = ByteBuffer.allocate( 4 ) ;
        list.put( (byte) 0x83 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x01 ) ;
        list.flip();
        assertEquals( 0x01, Length.getLength( list ) );

        list = ByteBuffer.allocate( 4 ) ;
        list.put( (byte) 0x83 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x05 ) ;
        list.flip();
        assertEquals( 0x05, Length.getLength( list ) );

        list = ByteBuffer.allocate( 4 ) ;
        list.put( (byte) 0x83 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0xFF ) ;
        list.flip();
        assertEquals( 0xFF, Length.getLength( list ) );
    }


    /**
     * Tests the long form when a byte is used for the length's length and
     * four more are used for the value length for a total of five bytes for
     * the length field itself.
     */
    public void testLongFiveBytes() throws DecoderException
    {
        ByteBuffer list = ByteBuffer.allocate( 5 ) ;
        list.put( (byte) 0x84 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x01 ) ;
        list.flip();
        assertEquals( 0x01, Length.getLength( list ) );

        list = ByteBuffer.allocate( 5 ) ;
        list.put( (byte) 0x84 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x05 ) ;
        list.flip();
        assertEquals( 0x05, Length.getLength( list ) );

        list = ByteBuffer.allocate( 5 ) ;
        list.put( (byte) 0x84 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0x00 ) ;
        list.put( (byte) 0xFF ) ;
        list.flip();
        assertEquals( 0xFF, Length.getLength( list ) );
    }


    /**
     * Tests to make sure certain length sizes are not allowed.  Basically we
     * are capping off the length at 2^32-1 which corresponds to 5 total length
     * bytes in the long form or the indeterminate form.
     */
    public void testMaxLength() throws Exception
    {
        ByteBuffer list = ByteBuffer.allocate( 6 ) ;
        list.put( (byte) 0x1 ) ;
        list.put( (byte) 0x1 ) ;
        list.put( (byte) 0x1 ) ;
        list.put( (byte) 0x1 ) ;
        list.put( (byte) 0x1 ) ;
        list.put( (byte) 0x1 ) ;
        list.flip();

        try
        {
            Length.getLength( list ) ;
            fail( "should fail before we get here" ) ;
        }
        catch ( DecoderException t )
        {
            assertNotNull( t ) ;
        }
        
        
        list.clear() ;
        list.put(( byte ) 0x7 ) ;
        list.flip() ;
        assertEquals( 7, Length.getLength( list ) ) ;
    }


    /**
     * Makes sure no additions can be made after short form fixation.
     */
    public void testShortFixation() throws Exception
    {
        byte[] bites = { (byte) 0x01, (byte) 0xff } ;
        
        Length length = new Length() ;
        length.add( bites[0] ) ;
        
        try
        {
            length.add( bites[1] ) ;
            fail( "should never get here due to illegal state" ) ;
        }
        catch ( Throwable t ) 
        {
            assertNotNull( t ) ;
        }

        assertEquals( 1, length.getLength() );
    }

    /**
     * Test that a Length could not begin with a 0xFF byte, which is
     * reserved for future extensions.
     *
     */
    public void testRestrictedValueForFutureExtension() 
    {
    	Length length = new Length();

        try
        {
        	length.add( (byte) 0xFF ) ;
        	length.add( (byte) 0x01 ) ;
            fail( "should fail before we get here" ) ;
        }
        catch ( DecoderException t )
        {
            assertNotNull( t ) ;
        }
    }

    /**
     * Test that an indefinite Length form is rejected. 
     *
     */
    public void testIndefiniteLength() 
    {
    	Length length = new Length();

        try
        {
        	length.add( (byte) 0x80 ) ;
        	length.add( (byte) 0x01 ) ;
        	length.add( (byte) 0x00 ) ;
        	length.add( (byte) 0x00 ) ;
            fail( "should fail before we get here" ) ;
        }
        catch ( Throwable t )
        {
            assertNotNull( t ) ;
        }
    }
}

