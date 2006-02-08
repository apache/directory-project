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
import java.util.ArrayList;

import org.apache.asn1.codec.EncoderException;
import org.apache.asn1.codec.stateful.AbstractStatefulEncoder;


/**
 * A tuple tree visitor that encodes tuples in prefix order into a buffer, and
 * chunking the buffer via callbacks as it is filled.  This encoder will work
 * on both determinate and indeterminate tuples.  However all indeterminate
 * tuples must be followed in sequence by an indeterminate terminator tuple.
 *
 * @todo might eventually want to make this encoder use a buffer pool to get
 * its chunk buffers rather than having it create its own
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class TupleEncodingVisitor extends AbstractStatefulEncoder
        implements TupleNodeVisitor
{
    /** An empty buffer array so we do not recreate every time on toArray */
    private static final ByteBuffer[] EMPTY_ARRAY = new ByteBuffer[0];

    /** The List storing the ByteBuffers collected during a visitation */
    private ArrayList buffers = new ArrayList();

    /** The visitor monitor used by this TupleNodeVisitor */
    private VisitorMonitor visitorMonitor = VisitorMonitor.NOOP;


    // ------------------------------------------------------------------------
    // TupleNodeVisitor Implementations
    // ------------------------------------------------------------------------


    public void encode( Object obj ) throws EncoderException
    {
        if ( obj instanceof DefaultMutableTupleNode )
        {
            ( ( DefaultMutableTupleNode ) obj ).accept( this );

            return;
        }

        throw new IllegalArgumentException( "Expected an argument of type"
                + " DefaultMutableTupleNode but instead got an instance of "
                + obj.getClass().getName() );
    }


    // ------------------------------------------------------------------------
    // TupleNodeVisitor Implementations
    // ------------------------------------------------------------------------


    /**
     * Visits a tree of tuple nodes using a specific visitation order.
     *
     * @todo major kludge in this method please see warnings inline
     * @param node the node to visit
     */
    public void visit( TupleNode node )
    {
        Tuple tlv = node.getTuple();

        int size = tlv.getTagLength() + tlv.getLengthLength();
        ByteBuffer buf = ByteBuffer.wrap( new byte[size] );
        tlv.setTag( buf, tlv.getTagLength() );
        tlv.setLength( buf, tlv.getLengthLength() );
        buffers.add( buf.flip() );

        /*
         * W A R N I N G
         * -------------
         *
         * This is a total kludge right now! Without changing the entire design
         * of the package or at least the Tuple class there really is no way to
         * get ahold of all the chunks for a Tuple.  So right now we're using
         * the last one hoping that it contains the entire value as one chunk.
         * This must be fixed and indicates a serious flaw in the design.
         *
         */

        if ( tlv.isPrimitive() )
        {
            buffers.add( tlv.getLastValueChunk() );
        }

        /*
         * N O T E
         * -------
         *
         * We presume termination tuples exist for indefinite tuples as sibling
         * nodes adjacent to the indefinite node.  This is why we do not
         * explicity handle termination here.  The termination octets actually
         * represent another TLV tuple itself with a UNIVERSAL tag of 0 and a
         * length of 0.
         *
         */
        visitorMonitor.visited( this, node );
    }


    /**
     * Checks to see if a node can be visited.
     *
     * @param node the node to be visited
     * @return whether or node the node should be visited
     */
    public boolean canVisit( TupleNode node )
    {
        return true;
    }


    /**
     * Determines whether the visitation order is prefix or postfix.
     *
     * @return true if the visitation is in prefix order, false otherwise.
     */
    public boolean isPrefix()
    {
        return true;
    }


    /**
     * Get the array of children to visit sequentially to determine the order of
     * child visitations.  Some children may not be returned at all if
     * canVisit() returns false on them.
     *
     * @param node     the parent branch node
     * @param children the child node array
     * @return the new reordered array of children
     */
    public ArrayList getOrder( TupleNode node, ArrayList children )
    {
        return children;
    }


    /**
     * Flushes out the array of ByteBuffer's collected during the visitation.
     * This is done by calling encodeOccurred in one shot.
     */
    public void flush()
    {
        ByteBuffer[] array = ( ByteBuffer [] ) buffers.toArray( EMPTY_ARRAY );
        buffers.clear();
        super.encodeOccurred( array );
    }


    public void setMonitor( VisitorMonitor monitor )
    {
        visitorMonitor = monitor;
    }
}
