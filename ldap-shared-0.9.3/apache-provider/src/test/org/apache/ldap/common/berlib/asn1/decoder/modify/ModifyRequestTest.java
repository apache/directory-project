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
package org.apache.ldap.common.berlib.asn1.decoder.modify ;


import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.LockableAttributeImpl;
import org.apache.ldap.common.message.ModifyRequest;
import org.apache.ldap.common.message.ModifyRequestImpl;

import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;


/**
 * Tests the capability to end to end decode a ModifyRequest.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class ModifyRequestTest extends RuleTestCase
{
    public void testModifyRequest() throws Exception
    {
        byte[] pdu = new byte[] {0x30, 0xFFFFFF81, 0xFFFFFFA8, 0x02, 0x01, 0x21, 0x66, 0xFFFFFF81, 0xFFFFFFA2, 0x04, 0x11, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x30, 0xFFFFFF81, 0xFFFFFF8C, 0x30, 0x19, 0x0A, 0x01, 0x02, 0x30, 0x14, 0x04, 0x02, 0x64, 0x63, 0x31, 0x0E, 0x04, 0x0C, 0x6F, 0x70, 0x65, 0x6E, 0x73, 0x6F, 0x66, 0x74, 0x77, 0x61, 0x72, 0x65, 0x30, 0x23, 0x0A, 0x01, 0x01, 0x30, 0x1E, 0x04, 0x05, 0x62, 0x6F, 0x67, 0x75, 0x73, 0x31, 0x15, 0x04, 0x13, 0x73, 0x6F, 0x6D, 0x65, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x2D, 0x74, 0x6F, 0x2D, 0x72, 0x65, 0x6D, 0x6F, 0x76, 0x65, 0x30, 0x28, 0x0A, 0x01, 0x00, 0x30, 0x23, 0x04, 0x0B, 0x6F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x43, 0x6C, 0x61, 0x73, 0x73, 0x31, 0x14, 0x04, 0x12, 0x6F, 0x72, 0x67, 0x61, 0x6E, 0x69, 0x7A, 0x61, 0x74, 0x69, 0x6F, 0x6E, 0x61, 0x6C, 0x55, 0x6E, 0x69, 0x74, 0x30, 0x20, 0x0A, 0x01, 0x00, 0x30, 0x1B, 0x04, 0x02, 0x6F, 0x75, 0x31, 0x15, 0x04, 0x08, 0x4F, 0x53, 0x53, 0x20, 0x53, 0x69, 0x74, 0x65, 0x04, 0x09, 0x4D, 0x61, 0x69, 0x6E, 0x20, 0x53, 0x69, 0x74, 0x65};

        ModifyRequest decoded = ( ModifyRequest ) decode( pdu ) ;

        ModifyRequestImpl expected = new ModifyRequestImpl( 33 ) ;
        expected.setName( "dc=example,dc=com" ) ;

        Attribute attr = new LockableAttributeImpl( "dc" ) ;
        attr.add( "opensoftware" ) ;
        ModificationItem mod = new ModificationItem(
                DirContext.REPLACE_ATTRIBUTE, attr ) ;
        expected.addModification( mod ) ;

        attr = new LockableAttributeImpl( "bogus" ) ;
        attr.add( "somevalue-to-remove" ) ;
        mod = new ModificationItem(
                DirContext.REMOVE_ATTRIBUTE, attr ) ;
        expected.addModification( mod ) ;

        attr = new LockableAttributeImpl( "objectClass" ) ;
        attr.add( "organizationalUnit" ) ;
        mod = new ModificationItem( DirContext.ADD_ATTRIBUTE, attr ) ;
        expected.addModification( mod ) ;

        attr = new LockableAttributeImpl( "ou" ) ;
        attr.add( "OSS Site" ) ;
        attr.add( "Main Site" ) ;
        mod = new ModificationItem( DirContext.ADD_ATTRIBUTE, attr ) ;
        expected.addModification( mod ) ;

        assertEquals( expected, decoded ) ;
    }
}
