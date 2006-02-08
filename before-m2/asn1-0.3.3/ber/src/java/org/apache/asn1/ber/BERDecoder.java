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


import java.nio.ByteBuffer;
import java.util.Stack;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.stateful.DecoderCallback;
import org.apache.asn1.codec.stateful.DecoderMonitor;
import org.apache.asn1.codec.stateful.DecoderMonitorAdapter;
import org.apache.asn1.codec.stateful.StatefulDecoder;
import org.apache.commons.lang.ArrayUtils;


/**
 * A decoder that decodes BER encoded bytes to Tag Value Length (TLV) tuples.
 * This decoder is a low level event based parser which operates in a fashion
 * similar to the way SAX works except the elements of concern are the tag,
 * length, and value entities.  The decoder is a state machine which processes
 * input as it is made available.
 * <p>
 * A Stack is used to track the state of the decoder between decode calls.  It
 * maintains the nesting of TLV tuples.  Rather than creating new TLV tuple
 * instances every time a single tuple is reused for primitive types and new
 * tlv tuples are cloned for constructed types which are pushed onto the stack.
 * The tuple fed to the callback must therefore be used very carefully - its
 * values must be copied to prevent their loss if they are to be used later
 * after the callback invokation has returned.
 * </p>
 * <p>
 * Note that all tuples are not created equal.  Constructed TLVs nesting others
 * will have null value members or empty buffers.  Only TLV tuples of primitive
 * types or the leaf TLV tuples of the TLV tuple tree will contain non null
 * values.  Therefore the nature of a TLV tuple should be investigated by
 * callbacks before attempting to interpret their values.  Also this decoder
 * chunks value data returning it in parts rather than in one complete peice
 * in the end.  The value of the TLV Tuple returned is the part of the value
 * that was read from the input fed into the decoder.  These 'chunks' returned
 * by callback makes it so there are no size limits to the value of a TLV. Again
 * to reiterate chunking on values is only performed on primitive TLV Tuple
 * types.
 * </p>
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDecoder implements StatefulDecoder, DecoderCallback
{
    /** empty byte buffer to be reused */
    private static final ByteBuffer EMPTY_BUFFER =
        ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;
    /** the callback used by this decoder */
    private static final BERDecoderCallback DEFAULT_CALLBACK =
        new BERDecoderCallbackAdapter() ;
    /** the monitor used by this decoder */
    private static final DecoderMonitor DEFAULT_MONITOR =
        new DecoderMonitorAdapter() ;

    /** this decoder's callback */
    private BERDecoderCallback cb = DEFAULT_CALLBACK ;
    /** the monitor used by this decoder */
    private DecoderMonitor monitor = DEFAULT_MONITOR ;

    /** the single TLV tuple used by this decoder */
    private final Tuple tlv = new Tuple() ;

    /** a decoder used to decode tag octets */
    private final TagDecoder tagDecoder = new TagDecoder() ;
    /** a decoder used to decode length octets */
    private final LengthDecoder lengthDecoder = new LengthDecoder() ;

    /** stack of nested/constructed TLV tuples */
    private final Stack tlvStack = new Stack() ;

    /** the state of this decoder */
    private BERDecoderState state = BERDecoderState.getStartState() ;


    /**
     * Creates a stateful BER decoder which limits the tuple's value size.
     */
    public BERDecoder()
    {
        tagDecoder.setCallback( this ) ;
        lengthDecoder.setCallback( this ) ;
    }


    // ------------------------------------------------------------------------
    // StatefulDecoder Methods
    // ------------------------------------------------------------------------


    /**
     * Expects a ByteBuffer containing BER encoded data.
     *
     * @see org.apache.asn1.codec.stateful.StatefulDecoder#decode(
     * java.lang.Object)
     * @throws ClassCastException if the encoded argument is not a ByteBuffer
     * @throws IllegalArgumentException if the buffer is null or empty
     */
    public void decode( Object encoded ) throws DecoderException
    {
        ByteBuffer buf = ( ByteBuffer ) encoded ;

        /* --------------------------------------------------------------------
           Handle any unusual input by informing the monitor.
           ------------------------------------------------------------------ */

        if ( buf == null && monitor != null )
        {
            String msg = "ignoring null argument to decode()" ;
            monitor.warning( this, new IllegalArgumentException( msg ) ) ;
            return ;
        }

        if ( buf.remaining() == 0 && monitor != null )
        {
            String msg = "ignoring empty buffer" ;
            monitor.warning( this, new IllegalArgumentException( msg ) ) ;
            return ;
        }

        /*
         * This loop is used instead of costly recursion.  This requires each
         * of the statewise decode methods to process bytes from the buffer.  If
         * they can process enough to switch state they do and return
         * immediately.  This loop makes sure the next processing state is
         * handled if there is more data for that state.
         */
        while ( buf.hasRemaining() )
        {
            switch( state.getValue() )
            {
                case( BERDecoderState.TAG_VAL ):
                    tagDecoder.decode( buf ) ;
                    break ;
                case( BERDecoderState.LENGTH_VAL ):
                    lengthDecoder.decode( buf ) ;
                    break ;
                case( BERDecoderState.VALUE_VAL ):
                    decodeValue( buf ) ;
                    break ;
            }
        }
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.StatefulDecoder#setCallback(
     * org.apache.asn1.codec.stateful.DecoderCallback)
     */
    public void setCallback( DecoderCallback cb )
    {
        this.cb = ( BERDecoderCallback ) cb ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.StatefulDecoder#setDecoderMonitor(
     * org.apache.asn1.codec.stateful.DecoderMonitor)
     */
    public void setDecoderMonitor( DecoderMonitor monitor )
    {
        this.monitor = monitor ;
    }


    // ------------------------------------------------------------------------
    // State Based Decode Methods
    // ------------------------------------------------------------------------


    /**
     * Extracts the value portion from the buffer for a primitive type.
     *
     * @param buf the byte byffer containing BER encoded data
     */
    private void decodeValue( ByteBuffer buf )
    {
        int needToRead = Length.UNDEFINED ;

        /*
         * setup to start decoding the value by figuring how much we need to
         * read at this point - previous reads of the value may have already
         * occurred.
         */
        if ( tlv.valueIndex == Length.UNDEFINED )
        {
            needToRead = tlv.length ;
        }
        else
        {
            needToRead = tlv.length - tlv.valueIndex ;
        }

        /*
         * check if we have the remainder of the value to complete the
         * TLV within the current buffer - if so we read all of it
         */
        if ( buf.remaining() >= needToRead )
        {
            tlv.valueChunk = ( ByteBuffer ) buf.slice().limit( needToRead ) ;
            buf.position( buf.position() + needToRead ) ;
            tlv.valueIndex = tlv.length ;
            tlv.index += tlv.length ;

            cb.partialValueDecoded( tlv ) ;
            fireDecodeOccurred( tlv ) ;
            updateStack( needToRead ) ;
            tlv.clear() ;
            state = BERDecoderState.TAG ;
        }

        /*
         * the buffer does not contain the rest of the value we need in order
         * to complete the current TLV - the value is fragmented so we read
         * what we can and update indices by that amount.
         */
        else
        {
            if ( tlv.valueIndex == Length.UNDEFINED )
            {
                tlv.valueIndex = 0 ;
            }

            int remaining = buf.remaining() ;
            tlv.valueChunk = buf.slice() ;
            buf.position( buf.limit() ) ;
            tlv.valueIndex += remaining ;
            tlv.index +=remaining ;

            cb.partialValueDecoded( tlv ) ;
            updateStack( remaining ) ;
        }
    }



    /* (non-Javadoc)
     * @see org.apache.asn1.codec.stateful.DecoderCallback#decodeOccurred(
     * org.apache.asn1.codec.stateful.StatefulDecoder, java.lang.Object)
     */
    public void decodeOccurred( StatefulDecoder decoder, Object decoded )
    {
        if ( decoder == tagDecoder )
        {
            Tag tag = ( Tag ) decoded ;
            tlv.rawTag = tag.getRawTag() ;
            tlv.id = tag.getId() ;
            tlv.isPrimitive = tag.isPrimitive() ;
            tlv.typeClass = tag.getTypeClass() ;
            tlv.index = tag.size() ;

            if ( ! tlv.isIndefiniteTerminator() )
            {
                fireTagDecoded() ;
                updateStack( tag.size() ) ;
            }

            state = state.getNext( tag.isPrimitive() ) ;
        }
        else if ( decoder == lengthDecoder )
        {
            Length length = ( Length ) decoded ;
            tlv.length = length.getLength() ;

            if ( tlv.length == Length.INDEFINITE )
            {
                tlv.index = Length.INDEFINITE ;
                tlv.valueIndex = Length.INDEFINITE ;
            }
            else
            {
                tlv.index += length.size() ;
            }

            if ( ! tlv.isIndefiniteTerminator() )
            {
                fireLengthDecoded() ;
            }
            updateStack( length.size() ) ;

            if ( ! tlv.isPrimitive )
            {
                if ( tlv.isIndefinite() || tlv.length > 0 )
                {
                    tlvStack.push( tlv.clone() ) ;
                }
                else
                {
                    state = BERDecoderState.VALUE ;
                    fireDecodeOccurred( tlv ) ;
                }

                state = BERDecoderState.TAG ;
                tlv.clear() ;
            }
            else if ( tlv.isIndefiniteTerminator() )
            {
                return ;
            }
            else if ( tlv.length > 0 )
            {
                state = BERDecoderState.VALUE ;
            }
            else
            {
                state = BERDecoderState.VALUE ;
                tlv.valueChunk = EMPTY_BUFFER ;
                cb.partialValueDecoded( tlv ) ;
                fireDecodeOccurred( tlv ) ;
                state = BERDecoderState.TAG ;
            }
        }
        else
        {
            throw new IllegalArgumentException( "unrecognized decoder" ) ;
        }
    }


    // ------------------------------------------------------------------------
    // private utility methods
    // ------------------------------------------------------------------------


    /**
     * Fires a tag decoded event by making the appropriate calls to the
     * callback and the monitor.   If the monitor is a BERDecoderMonitor with
     * extended reporting, then those methods are invoked.
     *
     * Also as a side-effect this method clears the tag buffer once it has
     * finished notifying the monitor and calling the callback.
     */
    private void fireTagDecoded()
    {
        if ( cb != null )
        {
            cb.tagDecoded( tlv ) ;
        }

        if ( monitor != null && monitor instanceof BERDecoderMonitor )
        {
            BERDecoderMonitor berMonitor = ( BERDecoderMonitor ) monitor ;
            berMonitor.tagDecoded( tlv ) ;
        }
    }


    /**
     * Fires a length decoded event by making the appropriate calls to the
     * callback and the monitor.   If the monitor is a BERDecoderMonitor with
     * extended reporting, then those methods are invoked.
     *
     * Also as a side-effect this method clears the length buffer once it has
     * finished notifying the monitor and calling the callback.
     */
    private void fireLengthDecoded()
    {
        if ( cb != null )
        {
            cb.lengthDecoded( tlv ) ;
        }

        if ( monitor != null && monitor instanceof BERDecoderMonitor )
        {
            BERDecoderMonitor berMonitor = ( BERDecoderMonitor ) monitor ;
            berMonitor.lengthDecoded( tlv ) ;
        }
    }


    /**
     * Fires a complete TLV decoded event by making the appropriate calls to
     * the callback and the monitor.
     */
    private void fireDecodeOccurred( Tuple tlv )
    {
        if ( cb != null )
        {
            cb.decodeOccurred( this, tlv ) ;
        }

        if ( monitor != null )
        {
            monitor.callbackOccured( this, cb, tlv ) ;
        }
    }


    /**
     * Increments the indices of constructed TLV's within the TLV Stack.
     *
     * @param increment the amount to increment indices by.
     */
    private void updateStack( int increment )
    {
        for ( int ii = 0; ii < tlvStack.size(); ii++ )
        {
            Tuple t = ( Tuple ) tlvStack.get( ii ) ;

            if ( t.isIndefinite() )
            {
                continue ;
            }

            t.index += increment ;

            if ( t.valueIndex == Length.UNDEFINED )
            {
                t.valueIndex = 0 ;
            }

            t.valueIndex += increment ;
        }

        if ( tlvStack.isEmpty() )
        {
            return ;
        }

        do
        {
            Tuple top = ( Tuple ) tlvStack.peek() ;

            if ( top.isIndefinite() && tlv.isIndefiniteTerminator() )
            {
                tlvStack.pop() ;
                state = BERDecoderState.VALUE ;
                fireDecodeOccurred( top ) ;
                state = BERDecoderState.TAG ;
                break;
            }
            else if ( top.isIndefinite() )
            {
                break ;
            }
            else if ( top.valueIndex >= top.length )
            {
                tlvStack.pop() ;
                state = BERDecoderState.VALUE ;
                fireDecodeOccurred( top ) ;
                state = BERDecoderState.TAG ;
            }
            else
            {
                break ;
            }

        } while( tlvStack.size() > 0 ) ;
    }


    /*

     Why copy the raw tag here when we can maintain our own stack in the
     digester that does the pushing and popping instead?  Keep this here
     until we decide what to do.

    public int[] getTagNestingPattern()
    {
        int stackSz = tlvStack.size() ;
        int[] pattern = new int[stackSz+1] ;
        pattern[stackSz] = tlv.rawTag ;

        for ( int ii = 0; ii < stackSz; ii++ )
        {
            pattern[ii] = ( ( Tuple ) tlvStack.get( ii ) ).rawTag ;
        }

        return pattern ;
    }
    */


    // ------------------------------------------------------------------------
    // Methods used for testing
    // ------------------------------------------------------------------------


    /**
     * Gets the current state of this BERDecoder.  Used only for debugging and
     * testing.
     *
     * @return the state enum
     */
    BERDecoderState getState()
    {
        return state ;
    }


    /**
     * Gets a cloned copy of the current tuple.  Used only for debugging and
     * testing.
     *
     * @return a clone of the current tlv
     */
    Tuple getCurrentTuple()
    {
        return ( Tuple ) tlv.clone() ;
    }


    /**
     * Gets a deep copy of the constructed tuple stack.  Used only for debugging
     * and testing.
     *
     * @return a deep copy of the tuple stack
     */
    Stack getTupleStack()
    {
        Stack stack = new Stack() ;

        for ( int ii = 0; ii < tlvStack.size(); ii++ )
        {
            Tuple t = ( Tuple ) tlvStack.get( ii ) ;
            stack.add( t.clone() ) ;
        }

        return stack ;
    }
}
