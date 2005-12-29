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
package org.apache.ldap.common.berlib.asn1.decoder.extended ;


import org.apache.commons.lang.ArrayUtils;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.ExtendedResponse;
import org.apache.ldap.common.message.ExtendedResponseImpl;
import org.apache.ldap.common.message.LdapResult;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.Referral;
import org.apache.ldap.common.message.ReferralImpl;
import org.apache.ldap.common.message.ResultCodeEnum;

import java.util.Iterator;


/**
 * Tests the population of an LdapResult using a ResultRule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class ExtendedResponseRuleTest extends RuleTestCase
{
    public void testExtendedResponse() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x59, 0x02, 0x01, 0x08, 0x78, 0x54, 0x0A, 0x01, 0x33, 0x04, 0x1F, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x2C, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x04, 0x11, 0x41, 0x6E, 0x20, 0x45, 0x72, 0x72, 0x6F, 0x72, 0x20, 0x4D, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x21, 0xFFFFFFA3, 0x0E, 0x04, 0x05, 0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x04, 0x05, 0x77, 0x6F, 0x72, 0x6C, 0x64, 0xFFFFFF8A, 0x07, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0xFFFFFF8B, 0x02, 0x05, 0x06};
        ExtendedResponse decoded = ( ExtendedResponse ) decode( pdu );

        ExtendedResponseImpl expected = new ExtendedResponseImpl( 8 ) ;
        expected.setResponseName( "0.0.0.0" ) ;
        byte[] payload = new byte[2] ;
        payload[0] = 5 ;
        payload[1] = 6 ;
        expected.setResponse( payload ) ;
        LdapResultImpl result = new LdapResultImpl( expected ) ;
        result.setResultCode( ResultCodeEnum.BUSY ) ;
        result.setErrorMessage( "An Error Message!" ) ;
        result.setMatchedDn( "uid=akarasulu,dc=example,dc=com" ) ;
        ReferralImpl referral = new ReferralImpl( result ) ;
        referral.addLdapUrl( "hello" ) ;
        referral.addLdapUrl( "world" ) ;
        result.setReferral( referral ) ;
        expected.setLdapResult( result ) ;

        assertEquals( expected, decoded ) ;
    }

}
