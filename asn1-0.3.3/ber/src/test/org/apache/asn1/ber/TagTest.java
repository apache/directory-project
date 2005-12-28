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


import org.apache.commons.lang.ArrayUtils ;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.asn1.ber.Tag;

import junit.framework.TestCase ;

import java.nio.BufferOverflowException;


/**
 * Tests the BER utility functions.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TagTest extends TestCase
{
    private static final int BIT_0 = 0x01 ;
    private static final int BIT_1 = 0x02 ;
    private static final int BIT_2 = 0x04 ;
    private static final int BIT_3 = 0x08 ;
    private static final int BIT_4 = 0x10 ;
    private static final int BIT_5 = 0x20 ;
    private static final int BIT_6 = 0x40 ;
    private static final int BIT_7 = 0x80 ;

    
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( TagTest.class ) ;
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
     * Constructor for TagTest.
     * @param arg0
     */
    public TagTest(String arg0)
    {
        super(arg0);
    }
    
    
    public void getTypeClass()
    {
        assertEquals( TypeClass.UNIVERSAL, TypeClass.getTypeClass( 0 ) ) ;
    }
    
    
    public void testIsPrimitive() throws Exception
    {
        byte octet = BIT_5 ;
        
        assertFalse( Tag.isPrimitive( octet ) ) ;
        assertFalse( Tag.isPrimitive( BIT_5 ) ) ;
        
        assertTrue( Tag.isPrimitive( 0 ) ) ;
        assertTrue( Tag.isPrimitive( BIT_0 ) ) ;
        assertTrue( Tag.isPrimitive( BIT_1 ) ) ;
        assertTrue( Tag.isPrimitive( BIT_2 ) ) ;
        assertTrue( Tag.isPrimitive( BIT_3 ) ) ;
        assertTrue( Tag.isPrimitive( BIT_4 ) ) ;
        assertTrue( Tag.isPrimitive( BIT_6 ) ) ;
        assertTrue( Tag.isPrimitive( BIT_7 ) ) ;
    }


    public void testIsConstructed() throws Exception
    {
        byte octet = BIT_5 ;
        
        assertTrue( Tag.isConstructed( octet ) ) ;
        assertTrue( Tag.isConstructed( BIT_5 ) ) ;
        
        assertFalse( Tag.isConstructed( 0 ) ) ;
        assertFalse( Tag.isConstructed( BIT_0 ) ) ;
        assertFalse( Tag.isConstructed( BIT_1 ) ) ;
        assertFalse( Tag.isConstructed( BIT_2 ) ) ;
        assertFalse( Tag.isConstructed( BIT_3 ) ) ;
        assertFalse( Tag.isConstructed( BIT_4 ) ) ;
        assertFalse( Tag.isConstructed( BIT_6 ) ) ;
        assertFalse( Tag.isConstructed( BIT_7 ) ) ;
    }
    
    
    public void testGetTagIdInt() throws Exception
    {
        int rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 0, false );
        assertEquals( Tag.getTagId( rawTag ), 0 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 3, false );
        assertEquals( Tag.getTagId( rawTag ), 3 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 30, false );
        assertEquals( Tag.getTagId( rawTag ), 30 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 31, false );
        assertEquals( Tag.getTagId( rawTag ), 31 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 126, false );
        assertEquals( Tag.getTagId( rawTag ), 126 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 127, false );
        assertEquals( Tag.getTagId( rawTag ), 127 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 128, false );
        assertEquals( Tag.getTagId( rawTag ), 128 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 16382, false );
        assertEquals( Tag.getTagId( rawTag ), 16382 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 16383, false );
        assertEquals( Tag.getTagId( rawTag ), 16383 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 16384, false );
        assertEquals( Tag.getTagId( rawTag ), 16384 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 2097150, false );
        assertEquals( Tag.getTagId( rawTag ), 2097150 );

        rawTag = Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 2097151, false );
        assertEquals( Tag.getTagId( rawTag ), 2097151 );
    }


    public void testGetTagIdByteArray() throws Exception
    {
        byte[] octets = new byte[1] ;

        for ( int ii = 0 ; ii < 128; ii++ )
        {
            octets[0] = ( byte ) ii ;

            if ( ii < 31 )
            {
                assertEquals( Tag.getTagId( octets ), ii ) ;
            }
            else
            {
                assertTrue( Tag.getTagId( octets ) != ii ) ;
            }
        }

        octets = new byte[2] ;
        octets[0] = 31 ;
        octets[1] = 0 ;

        for ( int ii = 31 ; ii < 255; ii++ )
        {
            octets[1] = ( byte ) ii ;

            if ( ii < 128 )
            {
                assertEquals( Tag.getTagId( octets ), ii ) ;
            }
            else
            {
                assertTrue( Tag.getTagId( octets ) != ii ) ;
            }
        }

        octets = new byte[3] ;
        octets[0] = 31 ;
        octets[1] = 0 ;
        octets[2] = 0 ;

        for ( int ii = 128 ; ii < 20000; ii++ )
        {
            octets[1] = (byte)((ii >> 7) & Tag.LONG_MASK);
            octets[2] = (byte)(ii & Tag.LONG_MASK);

            if ( ii < 16384 )
            {
                assertEquals( Tag.getTagId( octets ), ii ) ;
            }
            else
            {
                assertTrue( Tag.getTagId( octets ) != ii ) ;
            }
        }

        octets = new byte[4] ;
        octets[0] = 31 ;
        octets[1] = 0 ; // shift 0
        octets[2] = 0 ; // shift 7
        octets[3] = 0 ; // shift 14

        for ( int ii = 16384 ; ii < 2100000 ; ii++ )
        {
            octets[1] = (byte)((ii >> 14) & Tag.LONG_MASK);
            octets[2] = (byte)((ii >> 7) & Tag.LONG_MASK);
            octets[3] = (byte)(ii & Tag.LONG_MASK);

            if ( ii < 2097152 )
            {
                assertEquals( Tag.getTagId( octets ), ii ) ;
            }
            else
            {
                assertTrue( Tag.getTagId( octets ) != ii ) ;
            }
        }

        try
        {
            Tag.getTagId( new byte[5] ) ;
            fail( "should fail before getting here" ) ;
        }
        catch ( Throwable t )
        {
            assertNotNull( t ) ;
        }


        try
        {
            Tag.getTagId( new byte[12] ) ;
            fail( "should fail before getting here" ) ;
        }
        catch ( Throwable t )
        {
            assertNotNull( t ) ;
        }
    }


    public void testIsRawTagConstructed()
    {
        int rawTag = Tag.getIntEncodedTag( TypeClass.APPLICATION, 1234, true );
        assertTrue( Tag.isRawTagConstructed( rawTag ) );

        rawTag = Tag.getIntEncodedTag( TypeClass.APPLICATION, 1234, false );
        assertFalse( Tag.isRawTagConstructed( rawTag ) );

        rawTag = Tag.getIntEncodedTag( TypeClass.CONTEXT_SPECIFIC, 128, true );
        assertTrue( Tag.isRawTagConstructed( rawTag ) );

        rawTag = Tag.getIntEncodedTag( TypeClass.PRIVATE, 234, false );
        assertFalse( Tag.isRawTagConstructed( rawTag ) );
    }


    public void testTagLimits() throws Exception
    {
        byte[] bites = { (byte) 0xff, (byte) 0xff, (byte) 0x8f, (byte) 0x0f } ;

        Tag tag = new Tag() ;
        tag.add( bites[0] ) ;
        tag.add( bites[1] ) ;
        tag.add( bites[2] ) ;
        tag.add( bites[3] ) ;

        byte[] octets = tag.getOctets() ;
        assertTrue( ArrayUtils.isEquals( bites, octets ) ) ;

        byte[] tooMany = { (byte) 0xff, (byte) 0xff, (byte) 0x8f, (byte) 0x8f, (byte) 0x0f } ;

        tag = new Tag() ;
        tag.add( tooMany[0] ) ;
        tag.add( tooMany[1] ) ;
        tag.add( tooMany[2] ) ;
        tag.add( tooMany[3] ) ;

        try
        {
            tag.add( tooMany[4] ) ;
            fail( "should never get here due to exception" ) ;
        }
        catch( BufferOverflowException e )
        {
        }
    }


    public void testGetOctets() throws Exception
    {
        byte[] bites = { (byte) 0xff, (byte) 0xff, (byte) 0x0f } ;
        
        Tag tag = new Tag() ;
        tag.add( bites[0] ) ;
        tag.add( bites[1] ) ;
        tag.add( bites[2] ) ;
        
        byte[] octets = tag.getOctets() ;
        assertTrue( ArrayUtils.isEquals( bites, octets ) ) ;
    }
    
    
    public void testGetOctets2() throws Exception
    {
        byte[] bites = { (byte) 0x00, (byte) 0xff } ;
        
        Tag tag = new Tag() ;
        tag.add( bites[0] ) ;
        
        try
        {
            tag.add( bites[1] ) ;
            fail( "should never get here due to illegal state" ) ;
        }
        catch ( Throwable t ) 
        {
            assertNotNull( t ) ;
        }
    }


    public void testGetIntEncodedTag()
    {
        assertEquals( UniversalTag.INTEGER.getPrimitiveTag(),
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL,
                        UniversalTag.INTEGER.getTagId(), false ) ) ;

        assertEquals( 0x01000000,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 1, false ) ) ;

        assertEquals( 0x0F000000,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 15, false ) ) ;

        assertEquals( 0x1E000000,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 30, false ) ) ;

        assertEquals( 0x1F1F0000,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 31, false ) ) ;

        assertEquals( 0x1F7E0000,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 126, false ) ) ;

        assertEquals( 0x1F7F0000,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 127, false ) ) ;

        assertEquals( 0x1F810000,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 128, false ) ) ;

        assertEquals( 0x1F810100,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, 129, false ) ) ;

        assertEquals( 0x3FFF7E00,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, (1<<14)-2, true ) ) ;

        assertEquals( 0x1FFF7F00,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, (1<<14)-1, false ) ) ;

        assertEquals( 0xDF818000,
                Tag.getIntEncodedTag( TypeClass.PRIVATE, (1<<14), false ) ) ;

        assertEquals( 0x5F818001,
                Tag.getIntEncodedTag( TypeClass.APPLICATION, (1<<14)+1, false ) ) ;

        assertEquals( 0x9FFFFF7E,
                Tag.getIntEncodedTag( TypeClass.CONTEXT_SPECIFIC,
                        (1<<21)-2, false ) ) ;

        assertEquals( 0x1FFFFF7F,
                Tag.getIntEncodedTag( TypeClass.UNIVERSAL, (1<<21)-1, false ) ) ;

        try
        {
            Tag.getIntEncodedTag( TypeClass.UNIVERSAL, (1<<21), false ) ;
            fail( "should never get here due to an exception" ) ;
        }
        catch( IllegalArgumentException e )
        {
        }
    }
}
