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
package org.apache.ldap.common.berlib.asn1.decoder.delete ;


import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.DeleteRequest;
import org.apache.ldap.common.message.DeleteRequestImpl;


/**
 * Test case used to test the BER Digester with the DelRequestRule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class DeleteRequestRuleTest extends RuleTestCase
{
    public void testDelRequest() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x24, 0x02, 0x01, 0x2C, 0x4A, 0x1F, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x2C, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D};
        DeleteRequest decoded = ( DeleteRequest ) decode ( pdu );

        DeleteRequest expected = new DeleteRequestImpl( 44 ) ;
        expected.setName( "uid=akarasulu,dc=example,dc=com" ) ;
        assertEquals( expected, decoded ) ;
    }
}
