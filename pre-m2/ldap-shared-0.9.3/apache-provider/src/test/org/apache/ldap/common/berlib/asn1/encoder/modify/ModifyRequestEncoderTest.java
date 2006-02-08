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
package org.apache.ldap.common.berlib.asn1.encoder.modify;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.codec.DecoderException;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.message.LockableAttributeImpl;
import org.apache.ldap.common.message.ModifyRequest;
import org.apache.ldap.common.message.ModifyRequestImpl;

import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;


/**
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 */
public class ModifyRequestEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Builds a ModifyRequest for testing purposes.
     *
     * @return the ModifyRequest to use for tests
     */
    public ModifyRequest getRequest()
    {
        // Construct the Modify request to test
        ModifyRequestImpl req = new ModifyRequestImpl( 45 );
        req.setName( "cn=admin,dc=apache,dc=org" );

        LockableAttributeImpl attr = new LockableAttributeImpl( "attr0" );
        attr.add( "val0" );
        attr.add( "val1" );
        attr.add( "val2" );
        ModificationItem item =
                new ModificationItem( DirContext.ADD_ATTRIBUTE, attr );
        req.addModification( item );

        attr = new LockableAttributeImpl( "attr1" );
        attr.add( "val3" );
        item = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, attr );
        req.addModification( item );

        attr = new LockableAttributeImpl( "attr2" );
        attr.add( "val4" );
        attr.add( "val5" );
        item = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attr );
        req.addModification( item );

        return req;
    }

    public void testEncode() throws DecoderException
    {
        ModifyRequest req = getRequest();

        byte[] expected = new byte[] {0x30, 0x76, 0x02, 0x01, 0x2D, 0x66, 0x71, 0x04, 0x19, 0x63, 0x6E, 0x3D, 0x61, 0x64, 0x6D, 0x69, 0x6E, 0x2C, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67, 0x30, 0x54, 0x30, 0x20, 0x0A, 0x01, 0x00, 0x30, 0x1B, 0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x30, 0x31, 0x12, 0x04, 0x04, 0x76, 0x61, 0x6C, 0x32, 0x04, 0x04, 0x76, 0x61, 0x6C, 0x31, 0x04, 0x04, 0x76, 0x61, 0x6C, 0x30, 0x30, 0x14, 0x0A, 0x01, 0x01, 0x30, 0x0F, 0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x31, 0x31, 0x06, 0x04, 0x04, 0x76, 0x61, 0x6C, 0x33, 0x30, 0x1A, 0x0A, 0x01, 0x02, 0x30, 0x15, 0x04, 0x05, 0x61, 0x74, 0x74, 0x72, 0x32, 0x31, 0x0C, 0x04, 0x04, 0x76, 0x61, 0x6C, 0x35, 0x04, 0x04, 0x76, 0x61, 0x6C, 0x34};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = ModifyRequestEncoder.INSTANCE.encode( req );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
}
