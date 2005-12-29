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
package org.apache.ldap.common.berlib.asn1.decoder.compare ;


import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.CompareResponse;
import org.apache.ldap.common.message.CompareResponseImpl;
import org.apache.ldap.common.message.LdapResult;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.ReferralImpl;
import org.apache.ldap.common.message.ResultCodeEnum;


/**
 * Document this class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class CompareResponseRuleTest extends RuleTestCase
{
    public void testCompareRequest() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x4C, 0x02, 0x01, 0x07, 0x6F, 0x47, 0x0A, 0x01, 0x33, 0x04, 0x1F, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x2C, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x04, 0x11, 0x41, 0x6E, 0x20, 0x45, 0x72, 0x72, 0x6F, 0x72, 0x20, 0x4D, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x21, 0xFFFFFFA3, 0x0E, 0x04, 0x05, 0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x04, 0x05, 0x77, 0x6F, 0x72, 0x6C, 0x64};
        CompareResponse decoded = ( CompareResponse ) decode( pdu );

        CompareResponseImpl expected = new CompareResponseImpl( 7 ) ;
        LdapResult result = new LdapResultImpl( expected ) ;
        expected.setLdapResult( result ) ;
        result.setReferral( new ReferralImpl( result ) ) ;
        result.setErrorMessage( "An Error Message!" ) ;
        result.setMatchedDn( "uid=akarasulu,dc=example,dc=com" ) ;
        result.setResultCode( ResultCodeEnum.BUSY ) ;
        result.getReferral().addLdapUrl( "hello" ) ;
        result.getReferral().addLdapUrl( "world" ) ;

        assertEquals( expected, decoded );
    }
}
