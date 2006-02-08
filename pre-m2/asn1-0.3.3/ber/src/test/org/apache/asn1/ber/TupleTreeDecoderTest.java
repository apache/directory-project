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
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitorAdapter;
import org.apache.asn1.codec.stateful.StatefulDecoder;
import org.apache.commons.lang.time.StopWatch;

import java.nio.ByteBuffer;
import java.util.Collections;


/**
 * Tests the TupleTreeDecoder.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TupleTreeDecoderTest extends TestCase implements DecoderCallback
{
    DefaultMutableTupleNode root = null ;
    
    public static void main( String[] args)
    {
        TupleTreeDecoderTest test = new TupleTreeDecoderTest() ;
        
        try { test.testTTD() ; } catch ( Exception e ) { e.printStackTrace() ; } 
    }
    
    public void testSetMonitor()
    {
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        decoder.setDecoderMonitor( null ) ;
        decoder.setDecoderMonitor( new DecoderMonitorAdapter() ) ;
    }
    


    public void testTTD2() throws Exception
    {
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        Tuple t = new Tuple( 1, 0, true, TypeClass.APPLICATION ) ;
        ByteBuffer encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        decoder.decode( encoded ) ;
        
        t = new Tuple( 1, 0, true, TypeClass.APPLICATION ) ;
        encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;
        decoder.decode( encoded ) ;
    }    
    

    public void testTTD4() throws Exception
    {
        Tuple t = new Tuple( 1, 0, true, TypeClass.APPLICATION ) ;
        ByteBuffer encoded = t.toEncodedBuffer( Collections.EMPTY_LIST ) ;

        ByteBuffer shorter = ByteBuffer.allocate( encoded.capacity() - 1 ) ;
        shorter.put( ( ByteBuffer ) encoded.limit( shorter.limit() - 1 ) ) ;
        assertNull( TupleTreeDecoder.treeDecode( shorter ) ) ;
    }    
    

    public void testTTD() throws Exception
    {
        // Setup the bind request
        byte[] pdu = {0x30, 0x28, 0x02, 0x01, 0x11, 0x66, 0x23, 0x04, 0x1F, 0x75, 0x69, 0x64, 0x3D, 0x61, 0x6B, 0x61, 0x72, 0x61, 0x73, 0x75, 0x6C, 0x75, 0x2C, 0x64, 0x63, 0x3D, 0x65, 0x78, 0x61, 0x6D, 0x70, 0x6C, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x63, 0x6F, 0x6D, 0x30, 0x00};

        StopWatch watch = new StopWatch() ;
        watch.start() ;
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        decoder.setCallback( this ) ;
        decoder.decode( ByteBuffer.wrap( pdu ) ) ;
        watch.stop() ;

        StringBuffer buf = new StringBuffer() ;
        root.printDepthFirst( buf, 0 ) ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderCallback#
     * decodeOccurred(org.apache.asn1.codec.stateful.StatefulDecoder,
     * java.lang.Object)
     */
    public void decodeOccurred( StatefulDecoder decoder, Object decoded )
    {
        root = ( DefaultMutableTupleNode ) decoded ;
    }
}
