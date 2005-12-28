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
import java.util.ArrayList;
import java.util.Stack;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.stateful.CallbackHistory;
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitor;
import org.apache.asn1.codec.stateful.DecoderMonitorAdapter;
import org.apache.asn1.codec.stateful.StatefulDecoder;


/**
 * Builds a TLV tree from the TLV stream emitted from the decoder.  The decoded
 * objects delivered to this StatefulDecoder's DecoderCallback are 
 * DefaultMutableTupleNodes.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TupleTreeDecoder implements StatefulDecoder
{
    /** a stack of nested constructed tuples used to track state */
    Stack stack = new Stack() ;
    /** the underlying BER data stream to TLV stream decoder */
    BERDecoder decoder = new BERDecoder() ;
    /** the callback to use for this StatefulDecoder */
    DecoderCallback cb = null ;
    /** the monitor to use for this StatefulDecoder */
    DecoderMonitor monitor = new DecoderMonitorAdapter() ;
    /** the value chunks buffer collection */
    ArrayList valueChunks = new ArrayList() ;

    
    /**
     * Creates a simple BER byte stream to TLV Tuple tree decoder.
     */
    public TupleTreeDecoder()
    {
        BERDecoderCallback berCallback = new BERDecoderCallback()
        {
            public void tagDecoded( Tuple tlv ) { }
            
            public void partialValueDecoded( Tuple tlv ) 
            {
                ByteBuffer copy = ByteBuffer.allocate( 
                        tlv.getLastValueChunk().remaining() ) ;
                copy.put( tlv.getLastValueChunk() ) ;
                tlv.getLastValueChunk().rewind() ;
                copy.rewind() ;
                valueChunks.add( copy ) ;
            }
        
            public void lengthDecoded( Tuple tlv )
            {
                if ( ! tlv.isPrimitive ) 
                {
                    DefaultMutableTupleNode child = null ;
                    DefaultMutableTupleNode parent = null ;
                    Tuple cloned = ( Tuple ) tlv.clone() ;
                    
                    if ( stack.isEmpty() )
                    {
                        stack.push( new DefaultMutableTupleNode( cloned ) ) ;
                        return ;
                    }
                    
                    parent = ( DefaultMutableTupleNode ) stack.peek() ;
                    child = new DefaultMutableTupleNode( cloned ) ;
                    child.setParent( parent ) ;
                    parent.addLast( child ) ;
                    stack.push( child ) ;
                }
            }
    
            public void 
                decodeOccurred( StatefulDecoder decoder, Object decoded )
            {
                handleTuple( ( Tuple ) decoded ) ;
            }
        } ;
        
        decoder.setCallback( berCallback ) ;
        decoder.setDecoderMonitor( monitor ) ;
    }
    
    
    /**
     * Handles a tuple recieved from the underlying BER byte stream decoder.
     * 
     * @param t the tuple to handle
     */
    private void handleTuple( Tuple t )
    {
        DefaultMutableTupleNode node = null ;
        DefaultMutableTupleNode parent = null ;
        
        if ( t.isPrimitive )
        {
            node = new DefaultMutableTupleNode( ( Tuple ) t.clone(),
                    valueChunks ) ;
            valueChunks.clear() ;
                
            if ( ! stack.isEmpty() )
            {    
                parent = ( DefaultMutableTupleNode ) stack.peek() ;
                node.setParent( parent ) ;
                parent.addLast( node ) ;
            }
            
            return ;
        }
            
        node = ( DefaultMutableTupleNode ) stack.pop() ;
        
        if ( cb != null && stack.isEmpty() )
        {
            cb.decodeOccurred( this, node ) ;
        }
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.StatefulDecoder#decode(
     * java.lang.Object)
     */
    public void decode( Object encoded ) throws DecoderException
    {
        decoder.decode( encoded ) ;
    }
    
    
    /**
     * Decodes a BER byte buffer into a tree of TLV tuples.
     * 
     * @param buf the buffer to decode
     * @return the TLV tuple node with children if applicable
     * @throws DecoderException if there is a problem decoding the data
     * @throws java.util.NoSuchElementException if there is not enough data
     *  to properly decode a complete TLV tree
     */
    public static TupleNode treeDecode( ByteBuffer buf ) throws DecoderException
    {
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        CallbackHistory history = new CallbackHistory( 1 ) ;
        
        decoder.setCallback( history ) ;
        decoder.decode( buf ) ;
        
        if ( history.isEmpty() )
        {
            return null ;
        }
        
        return ( TupleNode ) history.getMostRecent() ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.StatefulDecoder#setCallback(
     * org.apache.asn1.codec.stateful.DecoderCallback)
     */
    public void setCallback( DecoderCallback cb )
    {
        this.cb = cb ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.StatefulDecoder#setDecoderMonitor(
     * org.apache.asn1.codec.stateful.DecoderMonitor)
     */
    public void setDecoderMonitor( DecoderMonitor monitor )
    {
        this.monitor = monitor ;
        decoder.setDecoderMonitor( monitor ) ;
    }
}
