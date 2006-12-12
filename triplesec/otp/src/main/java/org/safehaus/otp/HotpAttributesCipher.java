/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.otp;


import org.safehaus.crypto.BlockCipherWrapper;
import org.safehaus.crypto.DESEngine;

import java.io.UnsupportedEncodingException;


/**
 * Encypts and decrypts HOTP attributes.  HOTP attributes are stored as binary information
 * using the following data structure.  The result is DES encrypted then base 64 encoded.
 * <pre>
 * [[f1 value][f2 value][hotp size][factor][length][secret]]
 * where ...
 * f1 value   = 8 bytes        = the hotp value generated using factor-1 (encoded long)
 * f2 value   = 8 bytes        = the hotp value generated using factor-2 (encoded long)
 * hotp size  = 1 byte         = the unsigned # of characters in HOTP value within range [6-10]
 * factor     = 8 bytes        = the current moving factor
 * length     = 1 byte         = the unsigned length of the shared secret field (max 256)
 * secret     = length bytes   = the shared secret
 * </pre>
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpAttributesCipher
{
    /**
     * Takes HotpAttributes and formats them into a byte[] after calculating f1, f2, hotpSize,
     * factor, and length bytes.  This formated byte[] is then encrypted using the password and
     * a block cipher.  The encrypted result is then Base64 encoded for handling as a printable
     * String.
     *
     * @param password the key to use when encrypting the attributes
     * @param attributes the HotpAttributes to encrypt
     * @return the formated, then encrypted, then base64 encoded string
     * @throws UnsupportedEncodingException if the UTF-8 character encoding is not supported
     */
    public static String encrypt( String password, HotpAttributes attributes )
            throws UnsupportedEncodingException
    {
        // encode all the components of the HOTP binary record as specified above in the class level javadoc
        byte[] f1 = encodeLong( Long.parseLong( Hotp.generate( attributes.getSecret(),
                attributes.getFactor()-1, attributes.getSize() ) ) );
        byte[] f2 = encodeLong( Long.parseLong( Hotp.generate( attributes.getSecret(),
                attributes.getFactor()-2, attributes.getSize() ) ) );
        byte hotpSize = encodeUnsignedByte( attributes.getSize() );
        byte[] factor = encodeLong( attributes.getFactor() );
        byte length = encodeUnsignedByte( attributes.getSecret().length );

        // calculate and build the record by copying all components into their proper positions
        int totalLength = f1.length + f2.length + 1 + factor.length + 1 + attributes.getSecret().length;
        byte[] input = new byte[totalLength];
        int pos = 0;

        // copy the f1 bytes
        System.arraycopy( f1, 0, input, pos, f1.length );
        pos += f1.length;

        // copy the f2 bytes
        System.arraycopy( f2, 0, input, pos, f2.length );
        pos += f2.length;

        // copy the hotpSize byte
        input[pos] = hotpSize;
        pos++;

        // copy the factor bytes
        System.arraycopy( factor, 0, input, pos, factor.length );
        pos += factor.length;

        // copy the length bytes
        input[pos] = length;
        pos++;

        // copy the secret bytes
        System.arraycopy( attributes.getSecret(), 0, input, pos, attributes.getSecret().length );


        // initialize the cipher engine and encrypt the record
        BlockCipherWrapper engine = new BlockCipherWrapper( new DESEngine().getClass() );
        byte[] encrypted = engine.encrypt( password, input );

        // base64 encode the encrypted record and return the string
        return new String( Base64.encode( encrypted ) );
    }


    /**
     * Decrypts the encrypted HotpAtrributes using the provided password.  It first Base64 decodes
     * the encrypted string into the encrypted byte array.  The encrypted array is then decrypted
     * using a cypher engine.  The decrypted attributes are then decoded based on the format for
     * the hotp attributes.  If any errors or inconsistancies are found while extracting parameters
     * from the decrypted record, null is returned to denote the failure to decrypt.  Once all
     * parameters are extracted.  The HOTP credentials are verified using the f1 and f2 parameters
     * to check for integrity.
     *
     * @param password the password to use for decrypting the encrypted hotp attributes
     * @param encrypted the attributes in that are encrypted, format encoded and base64 encoded
     * @return a non-null HotpAttribute if the decryption succeed with verification, or null if it did not
     * @throws UnsupportedEncodingException if the UTF-8 character encoding is not supported
     */
    public static HotpAttributes decrypt( String password, String encrypted ) throws UnsupportedEncodingException
    {
        // base 64 decode the input
        byte[] input = Base64.decode( encrypted.toCharArray() );

        // initialize the cipher wrapper and decrypt
        BlockCipherWrapper engine = new BlockCipherWrapper( new DESEngine().getClass() );
        byte[] decrypted = engine.decrypt( password, input );

        // check that we have at least 19 bytes in the decrypted output if not return null for failure
        if ( decrypted.length < 19 )
        {
            return null;
        }

        // get the f1 and f2 hotp values as longs for integrity verification
        long f1 = decodeLong( decrypted, 0 );
        long f2 = decodeLong( decrypted, 8 );

        // get the hotpSize and make sure it's within a valid range if not return null for failure
        int hotpSize = decodeUnsignedByte( decrypted[16] );
        if ( 6 > hotpSize || hotpSize > 10 )
        {
            return null;
        }

        // get the moving factor value and the field for the length of the shared secret bytes
        // determine if the length is valid, meaning that reading this length will not cause
        // an index out of bounds exception by overrunning the size of the decrypted array
        long factor = decodeLong( decrypted, 17 );
        int secretLength = decodeUnsignedByte( decrypted[25] );
        if ( secretLength + 26 >= decrypted.length )
        {
            return null;
        }

        // acquire the secret and begin verification to make sure decryption succeeded
        byte[] secret = new byte[secretLength];
        System.arraycopy( decrypted, 26, secret, 0, secretLength );
        if ( f1 != Long.parseLong( Hotp.generate( secret, factor-1, hotpSize ) ) )
        {
            return null;
        }
        if ( f2 != Long.parseLong( Hotp.generate( secret, factor-2, hotpSize ) ) )
        {
            return null;
        }

        return new HotpAttributes( factor, secret );
    }


    /**
     * Gets unsigned value of a byte as an int.
     */
    public static int decodeUnsignedByte( byte bite )
    {
        if ( bite > 0 )
        {
            return bite;
        }

        int value = 0;
        value |= ( (int) bite ) & 0x000000ff;
        return value;
    }


    /**
     * Gets encoded byte value of a int as an unsigned bite.
     */
    public static byte encodeUnsignedByte( int unsignedByte )
    {
        if ( 0 > unsignedByte || unsignedByte > 255 )
        {
            StringBuffer buf = new StringBuffer();
            buf.append( "unsignedByte are was " );
            buf.append( unsignedByte );
            buf.append( ": value must be in range [0-255]" );
            throw new IllegalArgumentException( buf.toString() );
        }

        return ( byte ) unsignedByte;
    }


    /**
     * Encodes a long into 8 bytes.
     *
     * @param source the long to encode
     * @return the encoded 8 byte array
     */
    public static byte[] encodeLong( long source )
    {
        byte[] encoded = new byte[8];

        encoded[7] = ( byte ) source;
        encoded[6] = ( byte ) ( source >> 8 );
        encoded[5] = ( byte ) ( source >> 16 );
        encoded[4] = ( byte ) ( source >> 24 );
        encoded[3] = ( byte ) ( source >> 32 );
        encoded[2] = ( byte ) ( source >> 40 );
        encoded[1] = ( byte ) ( source >> 48 );
        encoded[0] = ( byte ) ( source >> 56 );

        return encoded;
    }


    /**
     * Decodes an 8 byte encoded long into a Java primitive long.
     *
     * @param bites the bytes containing the encoded long
     * @param offset the offset from zero where the bytes begin
     * @return the decoded primitive long
     */
    public static long decodeLong( byte[] bites, int offset )
    {
        long value = 0 ;

        value |= ( ( (long) bites[offset] )   << 56 ) & 0xff00000000000000L;
        value |= ( ( (long) bites[offset+1] ) << 48 ) & 0x00ff000000000000L;
        value |= ( ( (long) bites[offset+2] ) << 40 ) & 0x0000ff0000000000L;
        value |= ( ( (long) bites[offset+3] ) << 32 ) & 0x000000ff00000000L;
        value |= ( ( (long) bites[offset+4] ) << 24 ) & 0x00000000ff000000L;
        value |= ( ( (long) bites[offset+5] ) << 16 ) & 0x0000000000ff0000L;
        value |= ( ( (long) bites[offset+6] ) << 8  ) & 0x000000000000ff00L;
        value |=   ( (long) bites[offset+7] )         & 0x00000000000000ffL;

        return value ;
    }
}
