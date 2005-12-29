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
package org.apache.ldap.common.berlib.asn1.encoder.compare;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.CompareRequestImpl;


/**
 * TestCase for the CompareRequestEncoder class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $
 */
public class CompareRequestEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Tests the encode() method of the CompareRequest stub encoder.
     */
    public void testEncode()
    {
        CompareRequestImpl req = new CompareRequestImpl( 444 );
        req.setName( "dc=apache,dc=org" );
        req.setAttributeId( "dc" );
        req.setAssertionValue( "apache.org" );

        byte[] expected = new byte[] {0x30, 0x2A, 0x02, 0x02, 0x01, 0xFFFFFFBC, 0x6E, 0x24, 0x04, 0x10, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67, 0x30, 0x10, 0x04, 0x02, 0x64, 0x63, 0x04, 0x0A, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2E, 0x6F, 0x72, 0x67};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = CompareRequestEncoder.INSTANCE.encode( req );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}