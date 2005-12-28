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
public class MultiByteTagTests extends AbstractDecoderTestCase
{
    /** precalculated left shift of 1 by 14 places */
    private static final int BIT_13 = 1 << 14 ;
//    /** precalculated left shift of 1 by 16 places */
//    private static final int BIT_15 = 1 << 16 ;
    /** precalculated left shift of 1 by 21 places */
    private static final int BIT_20 = 1 << 21 ;
//    /** precalculated left shift of 1 by 24 places */
//    private static final int BIT_23 = 1 << 24 ;
//    /** precalculated left shift of 1 by 28 places */
//    private static final int BIT_27 = 1 << 28 ;
    

    /**
     * @param arg0
     */
    public MultiByteTagTests( String arg0 )
    {
        super( arg0 ) ;
    }
    
    
    public void testId31() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "00011111" ) ;
        assertEquals( 31, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId100() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "01100100" ) ;
        assertEquals( 100, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId127() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "01111111" ) ;
        assertEquals( 127, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId128() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "10000000" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "00000001" ) ;
        assertEquals( 1, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testId129() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "10000001" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "00000001" ) ;
        assertEquals( 129, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }




    public void testIdShift14() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "10000001" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "10000000" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "00000000" ) ;
        assertEquals( BIT_13, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testIdShift14Minus1() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "11111111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "01111111" ) ;
        assertEquals( BIT_13 - 1, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }


    public void testIdShift14Plus1() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "10000001" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "10000000" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "00000001" ) ;
        assertEquals( BIT_13 + 1, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }

    
    public void testIdShift21Minus1() throws Exception
    {
        Tuple tlv = decode( "01011111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( true, tlv.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, tlv.typeClass ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "11111111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "11111111" ) ;
        assertEquals( 0, tlv.id ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

        tlv = decode( "01111111" ) ;
        assertEquals( BIT_20 - 1, tlv.id ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
    }
}
