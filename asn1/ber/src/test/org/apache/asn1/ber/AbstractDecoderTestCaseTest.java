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

import org.apache.commons.lang.ArrayUtils ;
import org.apache.asn1.ber.AbstractDecoderTestCase;


/**
 * Tests the base test class functions.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class AbstractDecoderTestCaseTest extends AbstractDecoderTestCase
{
    
    public AbstractDecoderTestCaseTest()
    {
        super ( AbstractDecoderTestCaseTest.class.getName() ) ; 
    }

    public void testFragment()
    {
        byte[] all = new byte[3] ;
        assertEquals( 1, fragment(all, 3).length) ;
        try
        {
            fragment(ArrayUtils.EMPTY_BYTE_ARRAY, 0) ;
            fail( "should have thrown exception before reaching this line" ) ;
        }
        catch( IllegalArgumentException e )
        {
            assertNotNull( e ) ;
        }
    }

    /*
     * Class to test for Tuple decode(byte)
     */
    public void testDecodebyte() throws Exception
    {
        decode( ( byte ) 1 ) ;
        decode( ( byte ) 1 ) ;
        Tuple t = decode( ( byte ) 1 ) ;
        assertEquals( 1, t.id ) ;
        assertEquals( 1, t.length ) ;
    }

    /*
     * Class to test for Tuple decode(Tuple[])
     */
    public void testDecodeTupleArray() throws Exception
    {
        Tuple [] tuples = new Tuple[2] ;
        tuples[0] = new Tuple( 1, 0 ) ;
        tuples[1] = new Tuple( 1, 0 ) ;
        
        ByteBuffer[] buffers = new ByteBuffer[2] ; 
        buffers[0] = ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;
        buffers[1] = ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;
        decode( tuples, buffers ) ;
        decode( tuples[0], ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ) ;
    }

    public void testCallbackOccured() throws Exception
    {
        decoder.setDecoderMonitor( this ) ;
        Tuple [] tuples = new Tuple[2] ;
        tuples[0] = new Tuple(1, 0) ;
        tuples[1] = new Tuple(1, 0) ;
        ByteBuffer[] buffers = new ByteBuffer[2] ; 
        buffers[0] = ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;
        buffers[1] = ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;
        decode(tuples, buffers) ;
        callbackOccured(decoder, this, tuples[1]) ;
    }

    public void testCallbackSet()
    {
        decoder.setCallback(this) ;
        callbackSet(decoder, this, this) ;
    }

    public void testError()
    {
        try
        {
            error( decoder, new Exception() ) ;
            fail("should not get here") ;
        }
        catch ( Throwable e )
        {
            assertNotNull( e ) ;
        }
    }

    public void testFatalError()
    {
        try
        {
            fatalError( decoder, new Exception() ) ;
            fail("should not get here") ;
        }
        catch ( Throwable e )
        {
            assertNotNull( e ) ;
        }
    }

    public void testMonitorSet()
    {
        monitorSet( decoder, this ) ;
    }

    public void testWarning()
    {
        try
        {
            warning( decoder, new Exception() ) ;
            fail("should not get here") ;
        }
        catch ( Throwable e )
        {
            assertNotNull( e ) ;
        }
    }
}
