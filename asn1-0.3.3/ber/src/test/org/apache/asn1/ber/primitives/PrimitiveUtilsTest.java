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
package org.apache.asn1.ber.primitives ;


import junit.framework.TestCase ;

import java.math.BigInteger ;

import org.apache.commons.lang.ArrayUtils;
import org.apache.asn1.ber.primitives.PrimitiveUtils;


/**
 * Tests the PrimitiveUtil methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PrimitiveUtilsTest extends TestCase
{
    public static byte[] bites0 = { ( byte ) 0x96, 0x46 } ;
    public static byte[] bites1 = { ( byte ) 0x76, 0x46 } ;
    public static byte[] bites2 = { ( byte ) 0x96 } ;
    public static byte[] bites3 = { ( byte ) 0x46 } ;
    public static byte[] bites4 = { ( byte ) 0x46, 0x34, 0x12 } ;
    public static byte[] bites5 = { ( byte ) 0x96, 0x34, 0x12 } ;
    public static byte[] bites6 = { ( byte ) 0x4F, 0x46, 0x34, 0x12 } ;
    public static byte[] bites7 = { ( byte ) 0xFF, 0x26, 0x34, 0x12 } ;

    public static byte[][] byteArrays = {
        bites0, bites1, bites2, bites3, bites4, bites5, bites6, bites7
    } ;

    public static int[] values = {
        ( new BigInteger( bites0 ) ).intValue(),
        ( new BigInteger( bites1 ) ).intValue(),
        ( new BigInteger( bites2 ) ).intValue(),
        ( new BigInteger( bites3 ) ).intValue(),
        ( new BigInteger( bites4 ) ).intValue(),
        ( new BigInteger( bites5 ) ).intValue(),
        ( new BigInteger( bites6 ) ).intValue(),
        ( new BigInteger( bites7 ) ).intValue()
    };


    /**
     * Tests the PrimitiveUtils.decodeInt(byte[], int, int) method.
     * Uses the BigInteger class to verify correct encoding because
     * that's what a BigInteger uses.
     */
    public void testDecodeInt()
    {
        byte[] bites = new byte[1];
        bites[0] = (byte)0x80;
        assertEquals( -128, PrimitiveUtils.decodeInt( bites, 0, 1 ) );

        bites = new byte[2];
        bites[0] = 0;
        bites[1] = (byte)0x80;
        assertEquals( 128, PrimitiveUtils.decodeInt( bites, 0, 2 ) );

        bites = new byte[1];
        bites[0] = (byte)0x80;
        assertEquals( -128, PrimitiveUtils.decodeInt( bites, 0, 1 ) );

        assertEquals( 0, PrimitiveUtils.decodeInt( null, 0, 0 ) ) ;

        for ( int ii = 0; ii < byteArrays.length; ii++ )
        {
            int value = PrimitiveUtils.decodeInt( byteArrays[ii], 0,
                    byteArrays[ii].length ) ;
            assertEquals( values[ii], value ) ;
        }

        try
        {
            PrimitiveUtils.decodeInt( bites7, 0, -1 ) ;
            fail( "should never get here due to an exception" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }


    public void testEncodeInt()
    {
        byte[] encoded = PrimitiveUtils.encodeInt( 0 );
        byte[] actual = new BigInteger( "0" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( 0, PrimitiveUtils.decodeInt( encoded, 0, 1 ) );

        encoded = PrimitiveUtils.encodeInt( -1 );
        actual = new BigInteger( "-1" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( -1, PrimitiveUtils.decodeInt( encoded, 0, 1 ) );

        encoded = PrimitiveUtils.encodeInt( 1 );
        actual = new BigInteger( "1" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( 1, PrimitiveUtils.decodeInt( encoded, 0, 1 ) );

        encoded = PrimitiveUtils.encodeInt( -100 );
        actual = new BigInteger( "-100" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( -100, PrimitiveUtils.decodeInt( encoded, 0, 1 ) );

        encoded = PrimitiveUtils.encodeInt( 100 );
        actual = new BigInteger( "100" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( 100, PrimitiveUtils.decodeInt( encoded, 0, 1 ) );

        encoded = PrimitiveUtils.encodeInt( -128 );
        actual = new BigInteger( "-128" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( -128, PrimitiveUtils.decodeInt( encoded, 0, 1 ) );

        encoded = PrimitiveUtils.encodeInt( 127 );
        actual = new BigInteger( "127" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( 127, PrimitiveUtils.decodeInt( encoded, 0, 1 ) );

        // --------------------------------------------------------------------
        // TWO BYTES
        // --------------------------------------------------------------------

        encoded = PrimitiveUtils.encodeInt( 128 );
        actual = new BigInteger( "128" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( 128, PrimitiveUtils.decodeInt( encoded, 0, 2 ) );

        encoded = PrimitiveUtils.encodeInt( -129 );
        actual = new BigInteger( "-129" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( -129, PrimitiveUtils.decodeInt( encoded, 0, 2 ) );

        encoded = PrimitiveUtils.encodeInt( 129 );
        actual = new BigInteger( "129" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( 129, PrimitiveUtils.decodeInt( encoded, 0, 2 ) );

        encoded = PrimitiveUtils.encodeInt( -1000 );
        actual = new BigInteger( "-1000" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( -1000, PrimitiveUtils.decodeInt( encoded, 0, 2 ) );

        encoded = PrimitiveUtils.encodeInt( 1000 );
        actual = new BigInteger( "1000" ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( 1000, PrimitiveUtils.decodeInt( encoded, 0, 2 ) );

        encoded = PrimitiveUtils.encodeInt( -(1<<15) );
        actual = new BigInteger( new Integer(-(1<<15))
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( -(1<<15), PrimitiveUtils.decodeInt( encoded, 0, 2 ) );

        encoded = PrimitiveUtils.encodeInt( (1<<15)-1 );
        actual = new BigInteger( new Integer((1<<15)-1)
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (1<<15)-1, PrimitiveUtils.decodeInt( encoded, 0, 2 ) );


        // --------------------------------------------------------------------
        // THREE BYTES
        // --------------------------------------------------------------------

        encoded = PrimitiveUtils.encodeInt( (1<<15) );
        actual = new BigInteger( new Integer((1<<15))
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (1<<15), PrimitiveUtils.decodeInt( encoded, 0, 3 ) );

        encoded = PrimitiveUtils.encodeInt( (-(1<<15))-1 );
        actual = new BigInteger( new Integer((-(1<<15))-1)
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (-(1<<15))-1, PrimitiveUtils.decodeInt( encoded, 0, 3 ) );

        encoded = PrimitiveUtils.encodeInt( (1<<15)+1000 );
        actual = new BigInteger( new Integer((1<<15)+1000)
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (1<<15)+1000, PrimitiveUtils.decodeInt( encoded, 0, 3 ) );

        encoded = PrimitiveUtils.encodeInt( (-(1<<15))-1000 );
        actual = new BigInteger( new Integer((-(1<<15))-1000)
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (-(1<<15))-1000, PrimitiveUtils
                .decodeInt( encoded, 0, 3 ) );

        encoded = PrimitiveUtils.encodeInt( (1<<23)-1 );
        actual = new BigInteger( new Integer((1<<23)-1)
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (1<<23)-1, PrimitiveUtils.decodeInt( encoded, 0, 3 ) );

        encoded = PrimitiveUtils.encodeInt( (-(1<<23) ));
        actual = new BigInteger( new Integer(-(1<<23))
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( -(1<<23), PrimitiveUtils.decodeInt( encoded, 0, 3 ) );

        // --------------------------------------------------------------------
        // FOUR BYTES
        // --------------------------------------------------------------------

        encoded = PrimitiveUtils.encodeInt( (1<<23) );
        actual = new BigInteger( new Integer((1<<23))
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (1<<23), PrimitiveUtils.decodeInt( encoded, 0, 4 ) );

        encoded = PrimitiveUtils.encodeInt( (-(1<<23))-1 );
        actual = new BigInteger( new Integer((-(1<<23))-1)
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (-(1<<23))-1, PrimitiveUtils.decodeInt( encoded, 0, 4 ) );

        encoded = PrimitiveUtils.encodeInt( (1<<23) + 10000 );
        actual = new BigInteger( new Integer((1<<23) + 10000 )
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (1<<23) + 10000,
                PrimitiveUtils.decodeInt( encoded, 0, 4 ) );

        encoded = PrimitiveUtils.encodeInt( (-(1<<23))-10000 );
        actual = new BigInteger( new Integer((-(1<<23))-10000 )
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( (-(1<<23))-10000,
                PrimitiveUtils.decodeInt( encoded, 0, 4 ) );

        encoded = PrimitiveUtils.encodeInt( Integer.MAX_VALUE );
        actual = new BigInteger( new Integer( Integer.MAX_VALUE )
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( Integer.MAX_VALUE,
                PrimitiveUtils.decodeInt( encoded, 0, 4 ) );

        encoded = PrimitiveUtils.encodeInt( Integer.MIN_VALUE );
        actual = new BigInteger( new Integer( Integer.MIN_VALUE )
                .toString() ).toByteArray();
        assertTrue( ArrayUtils.isEquals( actual, encoded ) );
        assertEquals( Integer.MIN_VALUE,
                PrimitiveUtils.decodeInt( encoded, 0, 4 ) );

        for ( int ii = 0; ii < values.length; ii++ )
        {
            encoded = PrimitiveUtils.encodeInt( values[ii] ) ;
            assertTrue( ArrayUtils.isEquals( byteArrays[ii], encoded ) );
        }
    }
}