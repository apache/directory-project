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
package org.apache.ldap.common.berlib.asn1;

import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ldap.common.message.BindRequest;
import org.apache.ldap.common.message.spi.Provider;

import java.io.ByteArrayInputStream;


/**
 * Test cases for the SnickersDecoder class.
 *
 * @todo test with all PDU types - only BindRequest has been tested up to now
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SnickersDecoderTest extends TestCase
{
    SnickersDecoder decoder;

    protected void setUp() throws Exception
    {
        SnickersProvider provider = ( SnickersProvider ) Provider.getProvider();
        decoder = ( SnickersDecoder ) provider.getDecoder();
    }

    public void testDecoderOnBindRequest()
    {
        byte[] bind = {0x30, 0x2C, 0x02, 0x01, 0x1B, 0x60, 0x27, 0x02, 0x01, 0x03, 0x04, 0x1A, 0x63, 0x6E, 0x3D, 0x61, 0x64, 0x6D, 0x69, 0x6E, 0x2C, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0xFFFFFF80, 0x06, 0x70, 0x61, 0x73, 0x73, 0x77, 0x64};
        BindRequest decoded = ( BindRequest ) decoder.decode( null, new ByteArrayInputStream( bind ) );

        assertEquals( 27, decoded.getMessageId() );
        assertEquals( "cn=admin,dc=example,dc=com", decoded.getName() );
        assertTrue( ArrayUtils.isEquals( "passwd".getBytes(),
                decoded.getCredentials() ) );
        assertTrue( decoded.getSimple() );
        assertTrue( decoded.getVersion3() );
    }
}
