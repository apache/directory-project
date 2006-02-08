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
package org.apache.ldap.common.berlib.asn1.encoder.search;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.ReferralImpl;
import org.apache.ldap.common.message.SearchResponseReferenceImpl;


/**
 * TestCase for the SearchResponseReferenceEncoder.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SearchResponseReferenceEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Tests the encoder's encode() method.
     */
    public void testEncode()
    {
        // Construct the Search reference response
        SearchResponseReferenceImpl response =
                new SearchResponseReferenceImpl( 45 );
        ReferralImpl ref = new ReferralImpl( response );
        response.setReferral( ref );
        ref.addLdapUrl( "ldap://apache.org" );
        ref.addLdapUrl( "ldap://abc.com" );
        ref.addLdapUrl( "ldap://openldap.org" );
        ref.addLdapUrl( "ldap://xyz.net" );

        byte[] expected = new byte[] {0x30, 0x4D, 0x02, 0x01, 0x2D, 0x73, 0x48, 0x04, 0x13, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x6F, 0x70, 0x65, 0x6E, 0x6C, 0x64, 0x61, 0x70, 0x2E, 0x6F, 0x72, 0x67, 0x04, 0x0E, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x78, 0x79, 0x7A, 0x2E, 0x6E, 0x65, 0x74, 0x04, 0x0E, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x61, 0x62, 0x63, 0x2E, 0x63, 0x6F, 0x6D, 0x04, 0x11, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2E, 0x6F, 0x72, 0x67};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = SearchResponseReferenceEncoder.
                INSTANCE.encode( response );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
