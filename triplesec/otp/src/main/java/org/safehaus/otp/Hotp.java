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


import org.safehaus.crypto.HMac;
import org.safehaus.crypto.SHA1Digest;
import org.safehaus.crypto.KeyParameter;
import org.safehaus.crypto.CipherParameters;


/**
 * Generates a one time password using HMAC-SHA1.  This OTP algorithm is
 * described within an <a href="http://boxmanei.notlong.com/">ietf draft</a>.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Rev: 585 $
 */
public class Hotp
{
    private static final int[] DIGITS_POWER
            = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    /** the default minimum size for the HOTP value */
    private static final int MIN_DIGITS = 6;

    /** the default maximum size for the HOTP value */
    private static final int MAX_DIGITS = 10;


    /**
     * Generates an HOTP value using a shared secret (K), a counter for the
     * moving factor (C), and a HOTP value size (Digits).
     *
     * @param secret the shared secret key known to the token and the validator
     * @param counter the movign factor
     * @param digits the number of digits to produce for the HOTP value which
     * should be between the range of 6 to 10 inclusive
     * @return the generated HOTP value according to the specification
     */
    public static String generate( byte[] secret, long counter, int digits )
    {
        StringBuffer result = new StringBuffer();

        if ( MIN_DIGITS < digits || digits > MAX_DIGITS )
        {
            throw new IllegalArgumentException( "Number of digits not within range: "
                + MIN_DIGITS + " < digits > " + MAX_DIGITS );
        }

        if ( secret == null || secret.length == 0 )
        {
            throw new IllegalArgumentException( "Shared secret shouldn't be null or empty" );
        }

        byte[] hash = stepOne( secret, counter );

        // put selected bytes into result int
        int offset = 0;
        int binary = ( ( hash[offset] & 0x7f ) << 24 ) |
                 ( ( hash[offset + 1] & 0xff ) << 16 ) |
                 ( ( hash[offset + 2] & 0xff ) << 8 )  |
                 (   hash[offset + 3] & 0xff );

        int otp = binary % DIGITS_POWER[digits];
        result.append( Integer.toString( otp ) );
        while ( result.length() < digits )
        {
            result.insert( 0, "0" );
        }
        return result.toString();
    }


    /**
     * The first step to generate the HMAC-SHA-1 value.
     *
     * @param secretKey the shared secret key
     * @param counter the counter value (moving factor C)
     * @return the 20 byte HMAC-SHA-1 value
     */
    static byte[] stepOne( byte[] secretKey, long counter )
    {
        HMac mac = new HMac( new SHA1Digest() );
        byte[] value = new byte[mac.getMacSize()];
        CipherParameters params = new KeyParameter( secretKey );
        mac.init( params );
        mac.update( getCounterBytes( counter ), 0, 8 );
        mac.doFinal( value, 0 );
        return value;
    }


    // -------------------------------------------------------------------------
    // Utility Methods
    // -------------------------------------------------------------------------


    /**
     * Calculates the 8 bytes for the long counter where the 8 most significant
     * bits are in the first byte, then next 8 MSBs are in the second byte and
     * so on.
     *
     * @param counter the long counter value (C)
     * @return the byte array of length 8 representing the counter
     */
    static byte[] getCounterBytes( long counter )
    {
        byte[] counterBytes = new byte[8];
        counterBytes[7] = ( byte ) counter;
        counterBytes[6] = ( byte ) ( counter >> 8 );
        counterBytes[5] = ( byte ) ( counter >> 16 );
        counterBytes[4] = ( byte ) ( counter >> 24 );
        counterBytes[3] = ( byte ) ( counter >> 32 );
        counterBytes[2] = ( byte ) ( counter >> 40 );
        counterBytes[1] = ( byte ) ( counter >> 48 );
        counterBytes[0] = ( byte ) ( counter >> 56 );
        return counterBytes;
    }
}
