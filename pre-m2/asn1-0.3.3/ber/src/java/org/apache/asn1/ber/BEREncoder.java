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
package org.apache.asn1.ber;


import java.nio.ByteBuffer;

import org.apache.asn1.codec.stateful.AbstractStatefulEncoder;


/**
 * A BER TLV tuple encoder.  This encoder receives events via a
 * BEREncoderCallback.  Hence the callback is used to deliver events to this
 * encoder or event consumer.  The product is announced via an regular encoder
 * event.
 *
 * @note We tried the route of using a BEREncoderCallback here and well it was
 * ugly the event consumer producer based mechanism was a much better more
 * specific approach.  Plus it got confusing: we started confusing callbacks.
 * There was a callback for getting event then another callback for producing
 * them.  It got confusing fast.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class BEREncoder extends AbstractStatefulEncoder
        implements TupleEventConsumer
{
    private static final int DEFAULT_BUFSZ = 32;
    private ByteBuffer buf = null;


    /**
     * Creates a BEREncoder with the default buffer size.
     */
    public BEREncoder()
    {
        this( DEFAULT_BUFSZ );
    }


    /**
     * Creates a BEREncoder with a specific buffer size.
     *
     * @param bufSz the size of the buffer
     */
    public BEREncoder( int bufSz )
    {
        buf = ByteBuffer.allocateDirect( bufSz );
    }


    /**
     * Overriden encode method which does nothing but throw an exception.  This
     * has been done to prevent interference with the stream of TLV events
     * processed by this encoder.  A special BEREncoderCallback implementation
     * is used by this class to recieve events.  This callback is not the same
     * as the callback used to inform of encode events which emits ByteBuffer
     * objects.
     *
     * @param obj the object to encode
     * @throws UnsupportedOperationException every time
     */
    public void encode( Object obj )
    {
        throw new UnsupportedOperationException(
            "This encoder receives tuples ONLY via callback methods" );
    }


    /*
     * The idea here is to see if we can encode the tag bytes into the
     * remaining bytes of the buffer.  If we can then great we do so.
     * If we cannot then we have to flush out the full buffer with an
     * encodeOccurred Event.  This signals the production of encoded data
     * and free's up the buffer to have the tag bytes writen to it.
     */
    public void tag( Tuple tlv )
    {
        if ( buf.remaining() >= tlv.getTagLength() )
        {
            tlv.setTag( buf, tlv.getTagLength() );
        }
        else
        {
            buf.flip();
            encodeOccurred( buf );
            buf.clear();
            tlv.setTag( buf, tlv.getTagLength() );
        }
    }


    /*
     * Again we have the same dynamic where we no encode the lenght bytes
     * into the remaining bytes of the buffer.  If we can then great we do
     * so.  If we cannot then we have to flush out the full buffer with an
     * encodeOccurred Event.  This signals the production of encoded data
     * and free's up the buffer to have the length bytes writen to it.
     */
    public void length( Tuple tlv )
    {
        if ( buf.remaining() >= tlv.getLengthLength() )
        {
            tlv.setLength( buf, tlv.getLengthLength() );
        }
        else
        {
            buf.flip();
            encodeOccurred( buf );
            buf.clear();
            tlv.setLength( buf, tlv.getLengthLength() );
        }
    }


    /*
     * Here the situation is a little different.  The values are already
     * chunked so there is no need to copy them into a buffer.  We are
     * best off passing through this buffer to consumers with an encode but
     * before we do that we need to check if the present buffer contains
     * any material that must go out the door first.  Doing this prevents
     * us from mangling the order of bytes to send.  So if our buf contains
     * any bytes from previous operations laying down the tag and length
     * then we must flush it out.  Then we can flush out this chunk.
     */
    public void chunkedValue( Tuple tlv, ByteBuffer chunk )
    {
        if ( buf.position() > 0 )
        {
            buf.flip();
            BEREncoder.this.encodeOccurred( buf );
            buf.clear();
        }

        encodeOccurred( tlv.getLastValueChunk() );
    }


    /*
     * Keep in mind this method signals the end of a Tuple.  It is called
     * upstream from us by a higher level encoder that generates tuple
     * streams from objects.  This method simply returns if the object is
     * a primitive Tuple because all value processing has already occurred
     * for that tuple.  If on the otherhand the tuple is constructed and of
     * the indefinite form need to write the termination sequence (two
     * zeros) down into the stream.  We attempt to do this into the buffer.
     * If the buffer is full we flush is with an encodeOccurred() event.
     * Then we write the termination sequence into the buffer and flush
     * the buffer with an encodeOccurred Event.
     */
    public void finish( Tuple tlv )
    {
        if ( tlv.isPrimitive() )
        {
            return;
        }

        if ( tlv.isIndefinite() )
        {
            if ( buf.remaining() < 2 )
            {
                buf.flip();
                encodeOccurred( buf );
                buf.clear();
            }

            buf.put( (byte) 0 );
            buf.put( (byte) 0 );
            buf.flip();
            encodeOccurred( buf );
            buf.clear();
        }
    }
}
