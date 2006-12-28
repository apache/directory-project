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


/**
 * This class contains static methods that are used to calculate the
 * One-Time Password (OTP) using JCE to provide the HMAC-SHA1.
 *
 * @author Loren Hart
 * @version 1.0
 */
public class OneTimePasswordAlgorithm
{
    private static final int[] DIGITS_POWER
            // 0 1  2   3    4     5      6       7        8
            = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};


    /**
     * This method generates an OTP value for the given
     * set of parameters.
     *
     * @param secret           the shared secret
     * @param movingFactor     the counter, time, or other value that
     *                         changes on a per use basis.
     * @param digits           the number of digits in the OTP
     * @param truncationOffset the offset into the MAC result to
     *                         begin truncation. If this value is out of
     *                         the range of 0 ... 15, then dynamic
     *                         truncation  will be used.
     *                         Dynamic truncation is when the last 4
     *                         bits of the last byte of the MAC are
     *                         used to determine the start offset.
     * @return A numeric String in base 10 that includes
     *         codeDigits digits plus the optional checksum
     *         digit if requested.
     */
    static public String generateOTP( byte[] secret, long movingFactor, int digits, int truncationOffset )
    {
        // put movingFactor value into text byte array
        StringBuffer result = new StringBuffer();

        // compute hmac hash
        byte[] hash = Hotp.stepOne( secret, movingFactor );

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;
        if ( ( 0 <= truncationOffset ) && ( truncationOffset < ( hash.length - 4 ) ) )
        {
            offset = truncationOffset;
        }

        int binary = ( ( hash[offset] & 0x7f ) << 24 ) |
                 ( ( hash[offset + 1] & 0xff ) << 16 ) |
                 ( ( hash[offset + 2] & 0xff ) << 8 )  |
                 (   hash[offset + 3] & 0xff );


        int otp = binary % DIGITS_POWER[digits];
        String initial = Integer.toString( otp );
        result.append( initial );
        while ( result.length() < digits )
        {
            result.insert( 0, "0" );
        }
        return result.toString();
    }
}
