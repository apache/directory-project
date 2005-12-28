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
public class SingleByteLengthTests extends AbstractDecoderTestCase
{
    /**
     * Creates a single byte lenth test case.
     * 
     * @param name the name of this test
     */
    public SingleByteLengthTests( String name )
    {
        super( name ) ;
    }

    
    public void testLength0() throws Exception
    {
        Tuple tlv = decode( "00000000" + "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 1, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
        assertEquals( 0, tlv.length ) ;
    }


    public void testLength1() throws Exception
    {
        Tuple tlv = decode( "00000001" + "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 1, tlv.length ) ;
    }


    public void testLength3() throws Exception
    {
        Tuple tlv = decode( "00000011" + "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 3, tlv.length ) ;
    }


    public void testLength127() throws Exception
    {
        Tuple tlv = decode( "01111111" + "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.VALUE, decoder.getState() ) ;
        assertEquals( 127, tlv.length ) ;
    }
}
