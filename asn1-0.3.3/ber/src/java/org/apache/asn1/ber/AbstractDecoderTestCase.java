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
import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.binary.BinaryCodec;
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitor;
import org.apache.asn1.codec.stateful.StatefulDecoder;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;


/**
 * Tests the BER decoder.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class AbstractDecoderTestCase extends TestCase 
    implements BERDecoderCallback, DecoderMonitor
{
    /** list of encountered TLV's as we recieve completed decode callbacks */
    protected ArrayList tlvList = new ArrayList() ;
    /** the decoder that is constructed every time */
    protected BERDecoder decoder = null ;
    /** value accumulator */
    ByteBuffer buf = ByteBuffer.allocate(11111) ;
    

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp() ;
        decoder = new BERDecoder() ;
        decoder.setCallback( this ) ;
        decoder.setDecoderMonitor( this ) ;
    }

    
    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown() ;
        tlvList.clear() ;
        decoder = null ;
    }

    
    /**
     * Constructor for BERDecoderTest.
     * @param arg0
     */
    public AbstractDecoderTestCase( String arg0 )
    {
        super( arg0 ) ;
    }
    
    
    /**
     * Fragments an array of bytes into multiple arrays 'attempting' to keep 
     * them the same size however the last fragment will be an array 
     * bites.length%size which may or may not be of the requested fragment size.
     * However it will never be greater.  Use this method to break appart TLV 
     * byte arrays to feed them into the decoder for testing.
     * 
     * @param bites the bites to fragment
     * @param size the maximum size of a fragment
     * @return the array of byte[] fragments
     */
    public byte[][] fragment( byte[] bites, int size )
    {
        byte[][] fragments = null ;
        
        if ( size <= 0 )
        {    
            throw new IllegalArgumentException( 
                    "fragment size should be 1 or more but was " + size ) ;
        }
        
        int wholeFrags = bites.length/size ;
        int partialFragSize = bites.length % size ; 

        /*
         * Allocate what we need depending on the size of our remainder
         */
        if ( partialFragSize == 0 ) 
        {
            fragments = new byte[wholeFrags][] ;
        }
        else
        {
            fragments = new byte[wholeFrags+1][] ;
            fragments[wholeFrags] = new byte[partialFragSize] ;
        }
        
        for ( int ii = 0; ii < wholeFrags; ii++ )
        {
            fragments[ii] = new byte[size] ;
            System.arraycopy( bites, ii * size, fragments[ii], 0, size ) ;
        }
        
        if ( partialFragSize != 0 )
        {
            int srcPos = wholeFrags * size ;
            byte[] src = fragments[wholeFrags] ;
            System.arraycopy( bites, srcPos, src, 0, partialFragSize ) ;
        }
        
        return fragments ;
    }
    
    
    /**
     * Fragments a byte buffer into multiple buffer 'attempting' to keep 
     * them the same size however the last fragment will be an array 
     * bites.length%size which may or may not be of the requested fragment size.
     * However it will never be greater.  Use this method to break appart TLV 
     * bytes to feed them into the decoder for testing.
     * 
     * @param bites the bites to fragment
     * @param size the maximum size of a fragment
     * @return the buffer fragment
     */
    public ByteBuffer[] fragment( ByteBuffer bites, int size )
    {
        bites = bites.duplicate() ;
        ByteBuffer[] fragments = null ;
        
        if ( size <= 0 )
        {    
            throw new IllegalArgumentException( 
                    "fragment size should be 1 or more but was " + size ) ;
        }
        
        int wholeFrags = bites.remaining()/size ;
        int partialFragSize = bites.remaining() % size ; 

        /*
         * Allocate what we need depending on the size of our remainder
         */
        if ( partialFragSize == 0 ) 
        {
            fragments = new ByteBuffer[wholeFrags] ;
        }
        else
        {
            fragments = new ByteBuffer[wholeFrags+1] ;
            fragments[wholeFrags] = ByteBuffer.allocate( partialFragSize ) ;
        }
        
        for ( int ii = 0; ii < wholeFrags; ii++ )
        {
            fragments[ii] = ( ByteBuffer ) bites.slice().limit( size ) ;
            bites.position( bites.position() + size ) ;
        }
        
        if ( partialFragSize != 0 )
        {
            fragments[wholeFrags].put( bites ) ;
            fragments[wholeFrags].flip() ;
        }
        
        return fragments ;
    }
    
    
    /**
     * BER decodes a string of 0's and 1's.
     * 
     * @param bitString a string of ascii 0's and 1's
     * @return a copy of the decoded tuple or the partially decoded current tlv
     * @throws DecoderException if there are errors while decoding.
     */
    public Tuple decode( String bitString ) throws DecoderException
    {
        byte [] bites = BinaryCodec.fromAscii( bitString.getBytes() ) ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        int lastSize = tlvList.size() ;
        decoder.decode( buf ) ;
        
        if ( tlvList.isEmpty() || tlvList.size() == lastSize )
        {
            return decoder.getCurrentTuple() ;
        }
        
        return ( Tuple ) tlvList.get( tlvList.size() - 1 ) ;
    }

    
    /**
     * BER decodes a single byte.
     * 
     * @param bite a single byte to decode
     * @return a copy of the decoded tuple or the partially decoded current tlv
     * @throws DecoderException if there are errors while decoding.
     */
    public Tuple decode( byte bite ) throws DecoderException
    {
        byte [] bites = { bite } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        int lastSize = tlvList.size() ;
        decoder.decode( buf ) ;
        
        if ( tlvList.isEmpty() || tlvList.size() == lastSize )
        {
            return decoder.getCurrentTuple() ;
        }
        
        return ( Tuple ) tlvList.get( tlvList.size() - 1 ) ;
    }

    
    /**
     * BER decodes a byte buffer.
     * 
     * @param bites a byte buffer to decode
     * @return a copy of the decoded tuple or the partially decoded current tlv
     * @throws DecoderException if there are errors while decoding.
     */
    public Tuple decode( ByteBuffer bites ) throws DecoderException
    {
        int lastSize = tlvList.size() ;
        decoder.decode( bites ) ;
        
        if ( tlvList.isEmpty() || tlvList.size() == lastSize )
        {
            return decoder.getCurrentTuple() ;
        }
        
        return ( Tuple ) tlvList.get( tlvList.size() - 1 ) ;
    }

    
    /**
     * First BER encodes then BER decodes a Tuple.
     * 
     * @param tlv a tuple to encode then decode
     * @return a copy of the decoded tuple or the partially decoded current tlv
     * @throws DecoderException if there are errors while decoding
     */
    public Tuple decode( Tuple tlv, ByteBuffer value ) throws DecoderException
    {
        ArrayList list = new ArrayList() ;
        list.add( value ) ;
        ByteBuffer buf = tlv.toEncodedBuffer( list ) ;
        int lastSize = tlvList.size() ;
        decoder.decode( buf ) ;
        
        if ( tlvList.isEmpty() || tlvList.size() == lastSize )
        {
            Stack stack = decoder.getTupleStack() ;
            
            if ( stack.isEmpty() )
            {    
                return decoder.getCurrentTuple() ;
            }
            else
            {
                return ( Tuple ) stack.peek() ;
            }
        }
        
        return ( Tuple ) tlvList.get( tlvList.size() - 1 ) ;
    }

    
    /**
     * First BER encodes then BER decodes a train of Tuples.
     * 
     * @param tlvs a tuple array to encode then decode
     * @return a copy of the decoded tuple or the partially decoded current tlv
     * @throws DecoderException if there are errors while decoding
     */
    public Tuple decode( Tuple[] tlvs, ByteBuffer[] values ) 
        throws DecoderException
    {
        int lastSize = tlvList.size() ;

        for ( int ii = 0; ii < tlvs.length; ii++ )
        {
            decode( tlvs[ii], values[ii] ) ;
        }
        
        if ( tlvList.isEmpty() || tlvList.size() == lastSize )
        {
            return decoder.getCurrentTuple() ;
        }
        
        return ( Tuple ) tlvList.get( tlvList.size() - 1 ) ;
    }
    
    public Tuple decode( byte[] pdu ) throws DecoderException
    {
        decode( ByteBuffer.wrap( pdu ) ) ;
        return ( Tuple ) tlvList.get( tlvList.size() - 1 ) ;
    }

    /* (non-Javadoc)
     * @see org.apache.asn1.ber.BERDecoderCallback#tagDecoded(
     * org.apache.asn1.ber.Tuple)
     */
    public void tagDecoded( Tuple tlv )
    {
        assertTrue( decoder.getCurrentTuple().equals( tlv ) ) ;
        assertEquals( BERDecoderState.TAG, decoder.getState() ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.BERDecoderCallback#lengthDecoded(
     * org.apache.asn1.ber.Tuple)
     */
    public void lengthDecoded( Tuple tlv )
    {
        assertTrue( decoder.getCurrentTuple().equals( tlv ) ) ;
        assertEquals( BERDecoderState.LENGTH, decoder.getState() ) ;
        buf.clear() ;
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.ber.BERDecoderCallback#partialValueDecoded(
     * org.apache.asn1.ber.Tuple)
     */
    public void partialValueDecoded( Tuple tlv )
    {
        buf.put( tlv.valueChunk ) ;
    }
    

    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderCallback#decodeOccurred(
     * org.apache.asn1.codec.stateful.StatefulDecoder, java.lang.Object)
     */
    public void decodeOccurred( StatefulDecoder decoder, Object decoded )
    {
        Tuple t = ( Tuple ) decoded ;
        tlvList.add( t.clone() ) ;
        assertEquals( BERDecoderState.VALUE, this.decoder.getState() ) ;
        buf.flip() ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderMonitor#callbackOccured(
     * org.apache.asn1.codec.stateful.StatefulDecoder,
     * org.apache.asn1.codec.stateful.DecoderCallback, java.lang.Object)
     */
    public void callbackOccured( StatefulDecoder decoder, DecoderCallback cb,
								 Object decoded )
    {
        assertEquals( this, cb ) ;
        assertEquals( this.decoder, decoder ) ;
        Tuple t = ( Tuple ) decoded ;
        assertNotNull( t ) ;
        if ( t.isPrimitive )
        {    
            assertTrue( this.decoder.getCurrentTuple().equals( decoded ) ) ;
        }
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderMonitor#callbackSet(
     * org.apache.asn1.codec.stateful.StatefulDecoder,
     * org.apache.asn1.codec.stateful.DecoderCallback,
     * org.apache.asn1.codec.stateful.DecoderCallback)
     */
    public void callbackSet( StatefulDecoder decoder, DecoderCallback oldcb,
							 DecoderCallback newcb )
    {
        assertEquals( this, newcb ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderMonitor#error(
     * org.apache.asn1.codec.stateful.StatefulDecoder, java.lang.Exception)
     */
    public void error( StatefulDecoder decoder, Exception exception )
    {
        fail( ExceptionUtils.getFullStackTrace( exception ) ) ;
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderMonitor#fatalError(
     * org.apache.asn1.codec.stateful.StatefulDecoder, java.lang.Exception)
     */
    public void fatalError( StatefulDecoder decoder, Exception exception )
    {
        fail( ExceptionUtils.getFullStackTrace( exception ) ) ;
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderMonitor#monitorSet(
     * org.apache.asn1.codec.stateful.StatefulDecoder,
     * org.apache.asn1.codec.stateful.DecoderMonitor)
     */
    public void monitorSet( StatefulDecoder decoder, DecoderMonitor oldmon )
    {
        assertEquals( this, oldmon ) ;
    }

    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderMonitor#warning(
     * org.apache.asn1.codec.stateful.StatefulDecoder, java.lang.Exception)
     */
    public void warning( StatefulDecoder decoder, Exception exception )
    {
        assertNotNull( exception ) ;
    }
}
