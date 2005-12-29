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
package org.apache.ldap.common.berlib.asn1.decoder.search ;


import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.ReferralImpl;
import org.apache.ldap.common.message.SearchResponseReference;
import org.apache.ldap.common.message.SearchResponseReferenceImpl;


/**
 * Tests the population of an LdapResult using a ResultRule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class SearchResponseReferenceRuleTest extends RuleTestCase
{
    public void testSearchResponseReference() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x78, 0x02, 0x01, 0x08, 0x73, 0x73, 0x04, 0x3F, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x64, 0x69, 0x72, 0x65, 0x63, 0x74, 0x6F, 0x72, 0x79, 0x2E, 0x75, 0x67, 0x61, 0x2E, 0x65, 0x64, 0x75, 0x2F, 0x6F, 0x75, 0x3D, 0x73, 0x74, 0x75, 0x64, 0x65, 0x6E, 0x74, 0x73, 0x2C, 0x6F, 0x3D, 0x75, 0x67, 0x61, 0x2C, 0x63, 0x3D, 0x75, 0x73, 0x3F, 0x6F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x43, 0x6C, 0x61, 0x73, 0x73, 0x3F, 0x6F, 0x6E, 0x65, 0x04, 0x30, 0x6C, 0x64, 0x61, 0x70, 0x3A, 0x2F, 0x2F, 0x64, 0x69, 0x72, 0x65, 0x63, 0x74, 0x6F, 0x72, 0x79, 0x2E, 0x75, 0x67, 0x61, 0x2E, 0x65, 0x64, 0x75, 0x2F, 0x6F, 0x3D, 0x75, 0x67, 0x61, 0x2C, 0x63, 0x3D, 0x75, 0x73, 0x3F, 0x63, 0x72, 0x65, 0x61, 0x74, 0x6F, 0x72, 0x73, 0x6E, 0x61, 0x6D, 0x65};
        SearchResponseReference decoded = ( SearchResponseReference ) decode( pdu );

        SearchResponseReferenceImpl expected =
                new SearchResponseReferenceImpl( 8 ) ;
        ReferralImpl referral = new ReferralImpl( expected ) ;
        expected.setReferral( referral ) ;
        referral.addLdapUrl(
           "ldap://directory.uga.edu/ou=students,o=uga,c=us?objectClass?one" ) ;
        referral.addLdapUrl(
           "ldap://directory.uga.edu/o=uga,c=us?creatorsname" ) ;

        assertEquals( expected, decoded ) ;
    }
}
