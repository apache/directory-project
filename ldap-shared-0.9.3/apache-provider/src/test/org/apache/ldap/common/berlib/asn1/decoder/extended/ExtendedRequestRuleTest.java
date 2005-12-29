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


import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.ExtendedRequest;
import org.apache.ldap.common.message.ExtendedRequestImpl;


/**
 * An extended request rule test case. 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ExtendedRequestRuleTest extends RuleTestCase
{
    public void testCompareRequest() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x12, 0x02, 0x01, 0x07, 0x77, 0x0D, 0xFFFFFF80, 0x07, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0xFFFFFF81, 0x02, 0x12, 0x34};
        ExtendedRequest decoded = ( ExtendedRequest ) decode( pdu );

        ExtendedRequestImpl expected = new ExtendedRequestImpl( 7 ) ;
        expected.setOid( "0.0.0.0" ) ;
        byte[] payload = new byte[2] ;
        payload[0] = 0x12 ;
        payload[1] = 0x34 ;
        expected.setPayload( payload ) ;

        assertEquals( expected, decoded ) ;
    }
}
