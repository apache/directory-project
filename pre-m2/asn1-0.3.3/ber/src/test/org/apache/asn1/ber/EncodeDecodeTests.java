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
package org.apache.asn1.ber ;


import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * Testing out round trip encode decode.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class EncodeDecodeTests extends TestCase
{
    public void testAbandonRequest() throws Exception
    {
        byte[] pdu = {0x30, 0x06, 0x02, 0x01, 0x01, 0x50, 0x01, 0x03};

        DefaultMutableTupleNode root = ( DefaultMutableTupleNode )
            TupleTreeDecoder.treeDecode( ByteBuffer.wrap( pdu ) ) ;
        
        ByteBuffer buf = ByteBuffer.allocate( root.size() ) ;
        root.encode( buf ) ;
        buf.flip();
        byte[] actual = new byte[buf.remaining()] ;
        buf.get( actual ) ;

        assertTrue( Arrays.equals( pdu, actual ) );
    }


    public void testBindRequest() throws Exception
    {
        byte[] pdu = {0x30, 0x33, 0x02, 0x01, 0x01, 0x60, 0x2E, 0x02, 0x01, 0x03, 0x04, 0x1F, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x2C, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0xFFFFFF80, 0x08, 0x70, 0x61, 0x73, 0x73, 0x77, 0x6F, 0x72, 0x64};

         DefaultMutableTupleNode root =
                 (DefaultMutableTupleNode) TupleTreeDecoder.treeDecode( ByteBuffer.wrap( pdu ) );

        ByteBuffer buf = ByteBuffer.allocate( root.size() ) ;
        root.encode( buf ) ;
        buf.flip() ;
        byte[] actual = new byte[buf.remaining()] ;
        buf.get( actual ) ;

        assertTrue( Arrays.equals( pdu, actual ) );
    }
}
