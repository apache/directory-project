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
package org.apache.ldap.common.berlib.asn1.encoder.delete;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.DeleteRequestImpl;


/**
 * TestCase for the DeleteRequestEncoder class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 */
public class DeleteRequestEncoderTest extends AbstractEncoderTestCase
{

    public void testEncode()
    {
        DeleteRequestImpl req = new DeleteRequestImpl( 12 );
        req.setName( "uid=akarasulu,dc=apache,dc=org" );

        byte[] expected = new byte[] {0x30, 0x23, 0x02, 0x01, 0x0C, 0x4A, 0x1E, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x2C, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = DeleteRequestEncoder.INSTANCE.encode( req );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
