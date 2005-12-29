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
package org.apache.ldap.common.berlib.asn1.encoder.abandon;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.message.AbandonRequestImpl;


/**
 * TestCase for the AbandonRequestEncoder class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class AbandonRequestEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Tests the encode method.
     */
    public void testEncode()
    {
        AbandonRequestImpl request = new AbandonRequestImpl( 33 );
        request.setAbandoned( 11 );
        byte[] expected = new byte[] {0x30, 0x06, 0x02, 0x01, 0x21, 0x50, 0x01, 0x0B};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = AbandonRequestEncoder.INSTANCE.encode( request );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
