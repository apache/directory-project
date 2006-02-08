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
package org.apache.ldap.common.berlib.asn1.encoder.unbind;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.UnbindRequestImpl;


/**
 * TestCase for the UnbindRequestEncoder class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class UnbindRequestEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Tests the round trip encoding and decoding of an UnbindRequest.
     */
    public void testEncode()
    {
        UnbindRequestImpl request = new UnbindRequestImpl( 34 );

        byte[] expected = new byte[] {0x30, 0x05, 0x02, 0x01, 0x22, 0x42, 0x00};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = UnbindRequestEncoder.INSTANCE.encode( request );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
