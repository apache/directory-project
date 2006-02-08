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
package org.apache.ldap.common.berlib.asn1.decoder.modifydn ;


import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.ModifyDnRequest;
import org.apache.ldap.common.message.ModifyDnRequestImpl;


/**
 * Document this class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ModifyDnRequestRuleTest extends RuleTestCase
{
    public void testModifyDnRequest() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0x45, 0x02, 0x01, 0x07, 0x6C, 0x40, 0x04, 0x1B, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x2C, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x04, 0x0D, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x01, 0x01, 0xFFFFFFFF, 0xFFFFFF80, 0x0F, 0x64, 0x63, 0x3D, 0x62, 0x6F, 0x67, 0x75, 0x73, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D};
        ModifyDnRequest decoded = (ModifyDnRequest) decode( pdu );

        ModifyDnRequestImpl expected = new ModifyDnRequestImpl( 7 ) ;
        expected.setDeleteOldRdn( true ) ;
        expected.setName( "uid=asulu,dc=example,dc=com" ) ;
        expected.setNewRdn( "uid=akarasulu" ) ;
        expected.setNewSuperior( "dc=bogus,dc=com" ) ;

        assertEquals( expected, decoded ) ;
    }
}
