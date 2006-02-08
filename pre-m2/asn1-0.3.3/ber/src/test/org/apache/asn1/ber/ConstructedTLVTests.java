/*
 *   Copyright 2004-2005 The Apache Software Foundation
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
 * Performs constructed tlv tests.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ConstructedTLVTests extends AbstractDecoderTestCase
{

    /**
     * @param arg0
     */
    public ConstructedTLVTests( String arg0 )
    {
        super( arg0 ) ;
    }


    public void testConstructedDefinateTLV() throws Exception
    {
        // decode tag
        Tuple outter = decode( "01100001" ) ;
        assertEquals( 1, outter.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( false, outter.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, outter.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;

        // decode length
        outter = decode( "00000011" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;

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
        assertEquals( 2, tlvList.size() ) ;
    }


    public void testMultipleIndefiniteTLV() throws Exception
    {
        // --------------------------------------------------------------------

        // decode tag
        Tuple outter = decode( "01100001" ) ;
        assertEquals( 1, outter.id ) ;
        assertEquals( 0, tlvList.size() ) ;
        assertEquals( false, outter.isPrimitive ) ;
        assertEquals( TypeClass.APPLICATION, outter.typeClass ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;

        // decode length
        outter = decode( "10000000" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;


        // --------------------------------------------------------------------


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
        assertEquals( 0, tlvList.size() ) ;

        // decode value
        tlv = decode( "01010101" ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
        assertEquals( 1, tlvList.size() ) ;
        assertNotNull( tlv.valueChunk ) ;
        assertEquals( 0x0055, 0x00ff & tlv.valueChunk.get( 0 ) ) ;


        // --------------------------------------------------------------------


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


        // --------------------------------------------------------------------


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

        decode( "00000000" ) ;
        decode( "00000000" ) ;

        assertEquals( 4, tlvList.size() ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
    }


    public void testIllegalState() throws Exception
    {
        try
        {
            decode( "00000000" ) ;
            decode( "00000000" ) ;
            fail( "should throw an exception before getting here" ) ;
        }
        catch( Throwable e )
        {
            assertNotNull( e ) ;
        }
    }


    public void testIllegalStateNoMonitor() throws Exception
    {
        decoder.setDecoderMonitor( null ) ;

        try
        {
            decode( "00000000" ) ;
            decode( "00000000" ) ;
            fail( "should throw an exception before getting here" ) ;
        }
        catch( Throwable e )
        {
            assertNotNull( e ) ;
        }
    }
}
