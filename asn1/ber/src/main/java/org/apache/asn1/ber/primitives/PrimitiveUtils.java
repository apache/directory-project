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


/**
 * Utilities for decoding and encoding primitive constructs.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PrimitiveUtils
{
    private static final int ONE_BYTE_MAX   =  (1<<7)-1;
    private static final int ONE_BYTE_MIN   = -(1<<7);
    private static final int TWO_BYTE_MAX   =  (1<<15)-1;
    private static final int TWO_BYTE_MIN   = -(1<<15);
    private static final int THREE_BYTE_MAX =  (1<<23)-1;
    private static final int THREE_BYTE_MIN = -(1<<23);
    private static final int FOUR_BYTE_MAX  =  Integer.MAX_VALUE;
    private static final int FOUR_BYTE_MIN  =  Integer.MIN_VALUE;
    private static final byte[] TRUE_ARRAY = new byte[] { ( byte ) 0xFF };
    private static final byte[] FALSE_ARRAY = new byte[] { ( byte ) 0x00 };



    public static boolean berDecodeBoolean( byte value )
    {
        if ( value == 0 )
        {
            return false ;
        }

        return true ;
    }


    /**
     * Encodes a boolean as a byte following the stricter CER and DER
     * requirements where true must be a value of 0xFF and false is 0x00.
     *
     * @param bool the boolean to encode
     * @return 0xFF if bool is true, 0x00 if the bool is false
     */
    public static byte encodeBooleanAsByte( boolean bool )
    {
        return bool ? ( byte ) 0xFF : ( byte ) 0;
    }


    /**
     * Encodes a boolean as a byte[] with one byte following the stricter CER
     * and DER requirements where true must be a value of 0xFF and false is
     * 0x00.  Although there is alway one byte returned we return a byte [] as
     * a convenience since most of the time a byte[] is expected after encoding
     * a primitive type.
     *
     * @param bool the boolean to encode
     * @return a byte array of length 1 where the single byte is 0xFF if bool
     * is true, 0x00 if the bool is false
     */
    public static byte[] encodeBoolean( boolean bool )
    {
        return bool ? TRUE_ARRAY : FALSE_ARRAY ;
    }


    public static boolean derCerDecodeBoolean( byte value )
    {
        if ( value == 0 )
        {
            return false ;
        }
        else if ( value == 0xFF )
        {
            return true ;
        }
        else
        {
            String msg = "For DER and CER encodings of boolean values the only "
                    + " permisable values are 0x00 for false and 0xFF for true."
                    + " A value of " + value + " is not allowed!" ;
            throw new IllegalArgumentException( msg ) ;
        }
    }


    public static byte[] encodeInt( int source )
    {
        byte[] encoded = null;

        if ( source >= ONE_BYTE_MIN && source <= ONE_BYTE_MAX )
        {
            encoded = new byte[1];
            encoded[0] = ( byte ) source;
        }
        else if ( source >= TWO_BYTE_MIN && source <= TWO_BYTE_MAX )
        {
            encoded = new byte[2];
            encoded[1] = ( byte ) source;
            encoded[0] = ( byte ) ( source >> 8 );
        }
        else if ( source >= THREE_BYTE_MIN && source <= THREE_BYTE_MAX )
        {
            encoded = new byte[3];
            encoded[2] = ( byte ) source;
            encoded[1] = ( byte ) ( source >> 8 );
            encoded[0] = ( byte ) ( source >> 16 );
        }
        else if ( source >= FOUR_BYTE_MIN && source <= FOUR_BYTE_MAX )
        {
            encoded = new byte[4];
            encoded[3] = ( byte ) source;
            encoded[2] = ( byte ) ( source >> 8 );
            encoded[1] = ( byte ) ( source >> 16 );
            encoded[0] = ( byte ) ( source >> 24 );
        }

        return encoded;
    }


    /**
     * Decodes a BER encoded ASN.1 INTEGER into a Java primitive int.
     *
     * @param bites the bytes containing the encoded ASN.1 INTEGER
     * @param offset the offset from zero where the bytes begin
     * @param length the length of the bytes to read from the offset
     * @return the decoded primitive int or zero if the length is 0
     * @throws IllegalArgumentException if the length is not within
     * range [0-4]
     * @throws IndexOutOfBoundsException if offset is less than 0 or
     * it is greater than bites.length - length
     */
    public static int decodeInt( byte[] bites, int offset, int length )
    {
        int value = 0 ;

        if ( length == 0 )
        {
            return 0 ;
        }

        switch( length )
        {
            case( 1 ):
                if ( ( bites[offset+0] & 0x80 ) == 0x80 )
                {
                    value |= bites[offset+0] | 0xffffff00 ;
                }
                else
                {
                    value |= bites[offset+0] & 0x000000ff ;
                }

                break ;
            case( 2 ):
                if ( ( bites[offset+0] & 0x80 ) == 0x80 )
                {
                    value |= ( bites[offset+0] << 8 ) | 0xffff0000 ;
                }
                else
                {
                    value |= ( bites[offset+0] << 8 ) ;
                }

                value |= ( bites[offset+1] & 0x000000ff );
                break ;
            case( 3 ):
                if ( ( bites[offset+0] & 0x80 ) == 0x80 )
                {
                    value |= ( bites[offset+0] << 16 ) | 0xff000000 ;
                }
                else
                {
                    value |= ( bites[offset+0] << 16 ) ;
                }

                value |= ( bites[offset+1] << 8 ) & 0x0000ff00;
                value |= bites[offset+2] & 0x000000ff;
                break ;
            case( 4 ):
                value |= ( bites[offset+0] << 24 ) & 0xff000000;
                value |= ( bites[offset+1] << 16 ) & 0x00ff0000;
                value |= ( bites[offset+2] << 8 )  & 0x0000ff00;
                value |= bites[offset+3] & 0x000000ff;
                break ;
            default:
                throw new IllegalArgumentException(
                        "Length should be in range [0-4]" ) ;
        }

        return value ;
    }
}
