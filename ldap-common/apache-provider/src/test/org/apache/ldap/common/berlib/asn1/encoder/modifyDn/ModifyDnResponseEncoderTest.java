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
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.ModifyDnResponseImpl;
import org.apache.ldap.common.message.ReferralImpl;
import org.apache.ldap.common.message.ResultCodeEnum;


/**
 * Tests the ModifyDnResponse encoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 */
public class ModifyDnResponseEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Tests the encoder's encode() method.
     */
    public void testEncode()
    {
        // Construct the ModifyDn response to test with results and referrals
        ModifyDnResponseImpl response = new ModifyDnResponseImpl( 45 );
        LdapResultImpl result = new LdapResultImpl( response );
        response.setLdapResult( result );
        result.setMatchedDn( "dc=example,dc=com" );
        result.setResultCode( ResultCodeEnum.SUCCESS );
        ReferralImpl refs = new ReferralImpl( result );
        refs.addLdapUrl( "ldap://someserver.com" );
        refs.addLdapUrl( "ldap://apache.org" );
        refs.addLdapUrl( "ldap://another.net" );
        result.setReferral( refs );

        byte[] expected = new byte[] {0x30, 0x5D, 0x02, 0x01, 0x2D, 0x6D, 0x58, 0x0A, 0x01, 0x00, 0x04, 0x11, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x04, 0x00, 0xFFFFFFA3, 0x3E, 0x04, 0x15, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x73, 0x6F, 0x6D, 0x65, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x2E, 0x63, 0x6F, 0x6D, 0x04, 0x12, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x61, 0x6E, 0x6F, 0x74, 0x68, 0x65, 0x72, 0x2E, 0x6E, 0x65, 0x74, 0x04, 0x11, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2E, 0x6F, 0x72, 0x67};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = ModifyDnResponseEncoder.INSTANCE.encode( response );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
