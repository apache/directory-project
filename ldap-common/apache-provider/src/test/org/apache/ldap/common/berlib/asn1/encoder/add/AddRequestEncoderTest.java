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
package org.apache.ldap.common.berlib.asn1.encoder.add;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.AddRequestImpl;
import org.apache.ldap.common.message.LockableAttributesImpl;
import org.apache.ldap.common.message.LockableAttributeImpl;

import javax.naming.NamingException;


/**
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class AddRequestEncoderTest extends AbstractEncoderTestCase
{
    public void testEncode() throws NamingException
    {
        AddRequestImpl request = new AddRequestImpl( 33 );
        request.setEntry( "dc=apache,dc=org" );
        LockableAttributesImpl attrs = new LockableAttributesImpl( request );
        LockableAttributeImpl objectClass = new LockableAttributeImpl( "objectClass" );
        objectClass.add( "top" );
        objectClass.add( "dcObject" );
        attrs.put( objectClass );
        attrs.put( "dc", "example.com" );
        request.setAttributes( attrs );

        byte[] expected = new byte[] {0x30, 0x4E, 0x02, 0x01, 0x21, 0x68, 0x49, 0x04, 0x10, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67, 0x30, 0x35, 0x30, 0x1E, 0x04, 0x0B, 0x6F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x43, 0x6C, 0x61, 0x73, 0x73, 0x31, 0x0F, 0x04, 0x08, 0x64, 0x63, 0x4F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x04, 0x03, 0x74, 0x6F, 0x70, 0x30, 0x13, 0x04, 0x02, 0x64, 0x63, 0x31, 0x0D, 0x04, 0x0B, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2E, 0x63, 0x6F, 0x6D};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = AddRequestEncoder.INSTANCE.encode( request );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
