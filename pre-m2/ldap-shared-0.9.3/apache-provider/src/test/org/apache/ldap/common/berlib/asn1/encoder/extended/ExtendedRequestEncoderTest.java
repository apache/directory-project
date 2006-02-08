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
package org.apache.ldap.common.berlib.asn1.encoder.extended;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.ExtendedRequestImpl;


/**
 * TestCase for the ExtendedRequestEncoder class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ExtendedRequestEncoderTest extends AbstractEncoderTestCase
{
    public void testEncode()
    {
        ExtendedRequestImpl request = new ExtendedRequestImpl( 23 );
        request.setOid( "1.1.1.1" );
        request.setPayload( "Hello World!".getBytes() );

        byte[] expected = new byte[] {0x30, 0x1C, 0x02, 0x01, 0x17, 0x77, 0x17, 0xFFFFFF80, 0x07, 0x31, 0x2E, 0x31, 0x2E, 0x31, 0x2E, 0x31, 0xFFFFFF81, 0x0C, 0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x57, 0x6F, 0x72, 0x6C, 0x64, 0x21};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = ExtendedRequestEncoder.INSTANCE.encode( request );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
