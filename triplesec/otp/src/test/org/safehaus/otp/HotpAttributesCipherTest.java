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


import junit.framework.TestCase;


/**
 * Tests the HotpAttributesCipher.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class HotpAttributesCipherTest extends TestCase
{
    public void testUnsignedByteEncoding()
    {
        assertEquals( 0, HotpAttributesCipher.decodeUnsignedByte( HotpAttributesCipher.encodeUnsignedByte( 0 ) ) );
        assertEquals( 1, HotpAttributesCipher.decodeUnsignedByte( HotpAttributesCipher.encodeUnsignedByte( 1 ) ) );
        assertEquals( 128, HotpAttributesCipher.decodeUnsignedByte( HotpAttributesCipher.encodeUnsignedByte( 128 ) ) );
        assertEquals( 129, HotpAttributesCipher.decodeUnsignedByte( HotpAttributesCipher.encodeUnsignedByte( 129 ) ) );
        assertEquals( 255, HotpAttributesCipher.decodeUnsignedByte( HotpAttributesCipher.encodeUnsignedByte( 255 ) ) );

        try
        {
            HotpAttributesCipher.decodeUnsignedByte( HotpAttributesCipher.encodeUnsignedByte( -2 ) );
            fail( "should never get here due to IllegalArguemntException on -2" );
        }
        catch( IllegalArgumentException e )
        {
        }

        try
        {
            HotpAttributesCipher.decodeUnsignedByte( HotpAttributesCipher.encodeUnsignedByte( 256 ) );
            fail( "should never get here due to IllegalArguemntException on 256" );
        }
        catch( IllegalArgumentException e )
        {
        }
    }


    public void testLongEncoding()
    {
        assertEquals( 0, HotpAttributesCipher.decodeLong( HotpAttributesCipher.encodeLong( 0 ), 0 ) );
        assertEquals( 1, HotpAttributesCipher.decodeLong( HotpAttributesCipher.encodeLong( 1 ), 0 ) );

        assertEquals( 128, HotpAttributesCipher.decodeLong( HotpAttributesCipher.encodeLong( 128 ), 0 ) );
        assertEquals( -128, HotpAttributesCipher.decodeLong( HotpAttributesCipher.encodeLong( -128 ), 0 ) );

        assertEquals( 2147483647, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( 2147483647 ), 0 ) );
        assertEquals( -2147483648, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( -2147483648L ), 0 ) );

        assertEquals( 549755813887L, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( 549755813887L ), 0 ) );
        assertEquals( -549755813888L, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( -549755813888L ), 0 ) );

        assertEquals( 140737488355327L, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( 140737488355327L ), 0 ) );
        assertEquals( -140737488355328L, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( -140737488355328L ), 0 ) );

        assertEquals( -36028797018963968L, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( -36028797018963968L ), 0 ) );
        assertEquals( 36028797018963967L, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( 36028797018963967L ), 0 ) );

        assertEquals( Long.MAX_VALUE, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( Long.MAX_VALUE ), 0 ) );
        assertEquals( Long.MIN_VALUE, HotpAttributesCipher.decodeLong(
                HotpAttributesCipher.encodeLong( Long.MIN_VALUE ), 0 ) );
    }


    public void testEncryptDecrypt() throws Exception
    {
        HotpAttributes attributes = new HotpAttributes( 12341234,
                new byte[] { 0x45, 0x23, 0x12, 0x34, 0x45, 0x23, 0x23, 0x61 } );
        String encrypted = HotpAttributesCipher.encrypt( "secret", attributes );
        HotpAttributes decrypted = HotpAttributesCipher.decrypt( "secret", encrypted );
        assertEquals( attributes, decrypted );

        attributes = new HotpAttributes( 3282543502398475L,
                new byte[] { 0x45, 0x23, 0x12, 0x34, 0x45, 0x23, 0x12, 0x34, 0x45, 0x23, 0x23, 0x61 } );
        encrypted = HotpAttributesCipher.encrypt( "longer than expected secret", attributes );
        decrypted = HotpAttributesCipher.decrypt( "longer than expected secret", encrypted );
        assertEquals( attributes, decrypted );

        attributes = new HotpAttributes( 3282543502398475L,
                new byte[] { 0x45, 0x23, 0x12, 0x34, 0x45, 0x23, 0x12, 0x34, 0x45, 0x23, 0x23, 0x61 } );
        encrypted = HotpAttributesCipher.encrypt( "longer than expected secret", attributes );
        decrypted = HotpAttributesCipher.decrypt( "secret", encrypted );
        assertNull( decrypted );
    }
}
