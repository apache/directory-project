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
package org.apache.ldap.common.berlib.asn1.encoder.modifyDn;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.ModifyDnRequestImpl;


/**
 * Tests the ModifyDnRequest encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 */
public class ModifyDnRequestEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Tests the encoder's encode() method.
     */
    public void testEncode()
    {
        // Construct the ModifyDn request to test
        ModifyDnRequestImpl request = new ModifyDnRequestImpl( 45 );
        request.setDeleteOldRdn( true );
        request.setName( "dc=admins,dc=apache,dc=org" );
        request.setNewRdn( "dc=administrators" );
        request.setNewSuperior( "dc=groups,dc=apache,dc=org" );

        byte[] expected = new byte[] {0x30, 0x53, 0x02, 0x01, 0x2D, 0x6C, 0x4E, 0x04, 0x1A, 0x64, 0x63, 0x3D, 0x61, 0x64, 0x6D, 0x69, 0x6E, 0x73, 0x2C, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67, 0x04, 0x11, 0x64, 0x63, 0x3D, 0x61, 0x64, 0x6D, 0x69, 0x6E, 0x69, 0x73, 0x74, 0x72, 0x61, 0x74, 0x6F, 0x72, 0x73, 0x01, 0x01, 0xFFFFFFFF, 0xFFFFFF80, 0x1A, 0x64, 0x63, 0x3D, 0x67, 0x72, 0x6F, 0x75, 0x70, 0x73, 0x2C, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = ModifyDnRequestEncoder.INSTANCE.encode( request );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
