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
 * Here we test simple 1 byte tag and length values to test the decoder's 
 * ability to handle these most simple tlvs.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class SingleByteTagTests extends AbstractDecoderTestCase
{

    /**
     * @param arg0
     */
    public SingleByteTagTests( String arg0 )
    {
        super( arg0 ) ;
    }
    
    
    public void testAppTypeClass() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testPrivTypeClass() throws Exception
    {
        Tuple tlv = decode( "11000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.PRIVATE, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testCtxTypeClass() throws Exception
    {
        Tuple tlv = decode( "10000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.CONTEXT_SPECIFIC, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }

    
    public void testUniTypeClass() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;

    }
    
    public void testIllegalStateWithUniTypeClass() throws Exception
    {
        try
        {
            decode( "00000001" ) ;
        }
        catch( IllegalStateException e ) 
        {
            assertNotNull( e ) ;
        }
    }

    public void testId1() throws Exception
    {
        Tuple tlv = decode( "01000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId2() throws Exception
    {
        Tuple tlv = decode( "01000010" ) ;
        assertEquals( 2, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId3() throws Exception
    {
        Tuple tlv = decode( "01000011" ) ;
        assertEquals( 3, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId4() throws Exception
    {
        Tuple tlv = decode( "01000100" ) ;
        assertEquals( 4, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId5() throws Exception
    {
        Tuple tlv = decode( "01000101" ) ;
        assertEquals( 5, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId6() throws Exception
    {
        Tuple tlv = decode( "01000110" ) ;
        assertEquals( 6, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId7() throws Exception
    {
        Tuple tlv = decode( "01000111" ) ;
        assertEquals( 7, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId8() throws Exception
    {
        Tuple tlv = decode( "01001000" ) ;
        assertEquals( 8, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId9() throws Exception
    {
        Tuple tlv = decode( "01001001" ) ;
        assertEquals( 9, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId10() throws Exception
    {
        Tuple tlv = decode( "01001010" ) ;
        assertEquals( 10, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId11() throws Exception
    {
        Tuple tlv = decode( "01001011" ) ;
        assertEquals( 11, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId12() throws Exception
    {
        Tuple tlv = decode( "01001100" ) ;
        assertEquals( 12, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId13() throws Exception
    {
        Tuple tlv = decode( "01001101" ) ;
        assertEquals( 13, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId14() throws Exception
    {
        Tuple tlv = decode( "01001110" ) ;
        assertEquals( 14, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId15() throws Exception
    {
        Tuple tlv = decode( "01001111" ) ;
        assertEquals( 15, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId16() throws Exception
    {
        Tuple tlv = decode( "01010000" ) ;
        assertEquals( 16, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId17() throws Exception
    {
        Tuple tlv = decode( "01010001" ) ;
        assertEquals( 17, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId18() throws Exception
    {
        Tuple tlv = decode( "01010010" ) ;
        assertEquals( 18, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId19() throws Exception
    {
        Tuple tlv = decode( "01010011" ) ;
        assertEquals( 19, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId20() throws Exception
    {
        Tuple tlv = decode( "01010100" ) ;
        assertEquals( 20, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId21() throws Exception
    {
        Tuple tlv = decode( "01010101" ) ;
        assertEquals( 21, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId22() throws Exception
    {
        Tuple tlv = decode( "01010110" ) ;
        assertEquals( 22, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId23() throws Exception
    {
        Tuple tlv = decode( "01010111" ) ;
        assertEquals( 23, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId24() throws Exception
    {
        Tuple tlv = decode( "01011000" ) ;
        assertEquals( 24, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId25() throws Exception
    {
        Tuple tlv = decode( "01011001" ) ;
        assertEquals( 25, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId26() throws Exception
    {
        Tuple tlv = decode( "01011010" ) ;
        assertEquals( 26, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId27() throws Exception
    {
        Tuple tlv = decode( "01011011" ) ;
        assertEquals( 27, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId28() throws Exception
    {
        Tuple tlv = decode( "01011100" ) ;
        assertEquals( 28, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId29() throws Exception
    {
        Tuple tlv = decode( "01011101" ) ;
        assertEquals( 29, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId30() throws Exception
    {
        Tuple tlv = decode( "01011110" ) ;
        assertEquals( 30, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }
    
    
    public void testIdOverLimit() throws Exception
    {
        // this is the long form
        Tuple tlv = decode( "01011111" ) ;
        
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        
        // state did not switch since we are still reading the long tag  
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
    }

    
    public void testIsConstructed() throws Exception
    {
        Tuple tlv = decode( "01111110" ) ;
        assertEquals( 30, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( false, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }
}
