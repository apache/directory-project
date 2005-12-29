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
package org.apache.ldap.common.berlib.asn1.decoder.add ;


import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import junit.framework.Assert;

import org.apache.ldap.common.berlib.asn1.decoder.testutils.RuleTestCase;
import org.apache.ldap.common.message.AddRequest;

/**
 * Tests the capability to end to end decode a AddRequest.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class AddRequestTest extends RuleTestCase
{
    /**
     * Tests an add request decode.
     */
    public void testAddRequest() throws Exception
    {
        byte[] pdu = {0x30, 0x4E, 0x02, 0x01, 0x21, 0x68, 0x49, 0x04, 0x11, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x30, 0x34, 0x30, 0x1E, 0x04, 0x0B, 0x6F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x43, 0x6C, 0x61, 0x73, 0x73, 0x31, 0x0F, 0x04, 0x03, 0x74, 0x6F, 0x70, 0x04, 0x08, 0x64, 0x63, 0x4F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x30, 0x12, 0x04, 0x02, 0x64, 0x63, 0x31, 0x0C, 0x04, 0x0A, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65};

        AddRequest decoded = ( AddRequest ) decode( pdu ) ;
        
        Attributes attrs = decoded.getAttributes();

        assertEquals( 2, attrs.size() );
        
        NamingEnumeration attrsIter = attrs.getAll();
        
        while ( attrsIter.hasMoreElements() )
        {
            Attribute attr = (Attribute)attrsIter.nextElement();
            
            if ( attr.getID().equals( "objectClass" ) )
            {
                assertEquals( 2, attr.size() );
                
                if ( attr.get(0).equals( "top" ) )
                    {
                    assertEquals( "dcObject", attr.get(1) );
                    }
                else
                {
                    assertEquals( "dcObject", attr.get(0) );
                    assertEquals("top", attr.get(0) );
                }
            }
            else if ( attr.getID().equals( "dc" ) ) 
            {
                assertEquals( 1, attr.size() );
                assertEquals( "dc=example", attr.get() );
            }
            else
            {
                Assert.fail();
            }
        }
    }
}