/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.asn1new.ldap.codec;

import java.nio.ByteBuffer;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1new.ber.Asn1Decoder;
import org.apache.asn1new.ber.containers.IAsn1Container;
import org.apache.asn1new.ldap.pojo.CompareResponse;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.apache.asn1new.util.StringUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test the CompareResponse codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompareResponseTest extends TestCase {
    /**
     * Test the decoding of a CompareResponse
     */
    public void testDecodeCompareResponseSuccess()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x0E );
        
        stream.put(
            new byte[]
            {
                0x30, 0x0C, 		// LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	//         messageID MessageID
				0x6F, 0x07, 		//        CHOICE { ..., compareResponse CompareResponse, ...
                        			// CompareResponse ::= [APPLICATION 15] LDAPResult
				0x0A, 0x01, 0x00, 	//   LDAPResult ::= SEQUENCE {
									//		resultCode ENUMERATED {
									//			success (0), ...
				 					//      },
				0x04, 0x00,			//		matchedDN    LDAPDN,
				0x04, 0x00  		//      errorMessage LDAPString,
									//		referral     [3] Referral OPTIONAL }
									// }
            } );

        String decodedPdu = StringUtils.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the CompareResponse PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }
    	
        // Check the decoded CompareResponse PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        CompareResponse compareResponse      = message.getCompareResponse();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( 0, compareResponse.getLdapResult().getResultCode() );
        Assert.assertEquals( "", compareResponse.getLdapResult().getMatchedDN() );
        Assert.assertEquals( "", compareResponse.getLdapResult().getErrorMessage() );

        // Check the length
        Assert.assertEquals(0x0E, message.computeLength());
        
        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringUtils.dumpBytes( bb.array() ); 
            
            Assert.assertEquals(encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            Assert.fail( ee.getMessage() );
        }
    }
}