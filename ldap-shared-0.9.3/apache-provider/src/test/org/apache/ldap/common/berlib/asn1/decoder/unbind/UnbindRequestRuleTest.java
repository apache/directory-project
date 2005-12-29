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
package org.apache.ldap.common.berlib.asn1.decoder.unbind ;


import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.UnbindRequest;
import org.apache.ldap.common.message.UnbindRequestImpl;


/**
 * Test case used to test the BER Digester with the UnbindRequestRule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class UnbindRequestRuleTest extends RuleTestCase
{
    public void testUnbindRequest() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x05, 0x02, 0x01, 0x2C, 0x42, 0x00};
        UnbindRequest decoded = ( UnbindRequest ) decode( pdu );

        UnbindRequest expected = new UnbindRequestImpl( 44 ) ;
        assertEquals( expected, decoded ) ;
    }
}
