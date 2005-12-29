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
package org.apache.ldap.common.berlib.asn1.decoder.abandon ;

import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.AbandonRequest;
import org.apache.ldap.common.message.AbandonRequestImpl;

/**
 * Test case used to test the BER Digester with the AbandonRequestRule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class AbandonRequestRuleTest extends RuleTestCase
{
    /**
     * Tests the ability to decode an AbandonRequest with Snickers'
     * BERDigester.
     */
    public void testAbandonRequest() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x06, 0x02, 0x01, 0x2C, 0x50, 0x01, 0x0B};

        AbandonRequest expected = new AbandonRequestImpl( 44 );
        expected.setAbandoned( 11 );

        AbandonRequest decoded = ( AbandonRequest ) decode( pdu ) ;
        assertEquals( expected, decoded ) ;
    }
}
