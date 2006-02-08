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


import org.apache.asn1.ber.AbstractDecoderTestCase;
import org.apache.asn1.ber.BERDecoderState;


/**
 * Tests single byte length encodings in a BER TLV.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class MultiByteLengthTests extends AbstractDecoderTestCase
{
    /**
     * Creates a single byte lenth test case.
     * 
     * @param name the name of this test
     */
    public MultiByteLengthTests( String name )
    {
        super( name ) ;
    }

    
    public void testLength128() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;
        
        tlv = decode( "10000001" ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;

        tlv = decode( "10000000" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 128, tlv.length ) ;
    }


    public void testLength129() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;
        
        tlv = decode( "10000001" ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;

        tlv = decode( "10000001" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 129, tlv.length ) ;
    }


    public void testLength255() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;
        
        tlv = decode( "10000001" ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;

        tlv = decode( "11111111" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 255, tlv.length ) ;
    }
    
    
    public void testLength32768() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;
        
        tlv = decode( (byte)0x82 ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;

        tlv = decode( (byte) 0x80 ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;

        tlv = decode( (byte) 0x00 ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 32768, tlv.length ) ;
    }
    
    
    public void testLength65535() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;
        
        tlv = decode( "10000010" ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;

        tlv = decode( "11111111" ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;

        tlv = decode( "11111111" ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 65535, tlv.length ) ;
    }
}