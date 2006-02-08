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

import java.nio.ByteBuffer;

import org.apache.asn1.ber.AbstractDecoderTestCase;
import org.apache.asn1.ber.BERDecoderState;


/**
 * Performs simple primitive tlv tests.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class SimplePrimitiveTLVTests extends AbstractDecoderTestCase
{

    /**
     * @param arg0
     */
    public SimplePrimitiveTLVTests( String arg0 )
    {
        super( arg0 ) ;
    }
    
    
    public void testSingleSimpleTLV() throws Exception
    {
        // decode tag
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        
        // decode length 
        tlv = decode( "00000001" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 1, tlv.length ) ;
        
        // decode value
        tlv = decode( "01010101" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
        assertEquals( 1, tlvList.size() ) ;
        assertNotNull( tlv.valueChunk ) ;
        assertEquals( 0x0055, 0x00ff & tlv.valueChunk.get( 0 ) ) ;
    }


    public void testMultipleSimpleTLV() throws Exception
    {
        // decode tag
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        
        // decode length 
        tlv = decode( "00000001" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 1, tlv.length ) ;
        
        // decode value
        tlv = decode( "01010101" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
        assertEquals( 1, tlvList.size() ) ;
        assertNotNull( tlv.valueChunk ) ;
        assertEquals( 0x0055, 0x00ff & tlv.valueChunk.get( 0 ) ) ;

    
        // decode tag
        tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 1, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        
        // decode length 
        tlv = decode( "00000001" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 1, tlv.length ) ;
        
        // decode value
        tlv = decode( "01010101" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
        assertEquals( 2, tlvList.size() ) ;
        assertNotNull( tlv.valueChunk ) ;
        assertEquals( 0x0055, 0x00ff & tlv.valueChunk.get( 0 ) ) ;

    
        // decode tag
        tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 2, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        
        // decode length 
        tlv = decode( "00000001" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 1, tlv.length ) ;
        
        // decode value
        tlv = decode( "01010101" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
        assertEquals( 3, tlvList.size() ) ;
        assertNotNull( tlv.valueChunk ) ;
        assertEquals( 0x0055, 0x00ff & tlv.valueChunk.get( 0 ) ) ;
    }


    public void testSingleSimpleLongTLV() throws Exception
    {
        // decode tag
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        
        // decode length 
        tlv = decode( "10000001" ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;
        
        tlv = decode( "00000111" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 7, tlv.length ) ;
        
        // decode value
        tlv = decode( "01010101" + "01010101" + "01010101" + "01010101"
                + "01010101" + "01010101" + "01010101" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
        assertEquals( 1, tlvList.size() ) ;
        assertNotNull( tlv.valueChunk ) ;
        assertEquals( 7, tlv.valueChunk.capacity() ) ;
        
        ByteBuffer value = tlv.valueChunk.duplicate() ;
        for ( int ii = 0 ; ii < 7; ii++ )
        {    
            assertEquals( 0x0055, 0x00ff & value.get( ii ) ) ;
        }
    }
}
