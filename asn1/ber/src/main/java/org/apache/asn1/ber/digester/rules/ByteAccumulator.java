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
package org.apache.asn1.ber.digester.rules ;


import java.nio.ByteBuffer;

import org.apache.commons.lang.ArrayUtils;


/**
 * Gathers bytes from buffers while dynamically growing to accomodate a new size.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ByteAccumulator
{
    /** the default initial size */
    private static final int DEFAULT_INIT_SIZE = 0 ;
    /** the default initial size */
    private static final int DEFAULT_INCREMENT = 100 ;

    /** the accumulator's backing store */
    private byte[] bs ;
    /** the current position of the accumulator */
    private int pos ;
    /** the growth increment used to augment the backing store */
    private int increment ;
    /** the initial size of the backing store */
    private int initial = DEFAULT_INIT_SIZE ;


    // -----------------------------------------------------------------------
    // C O N S T R U C T O R S
    // -----------------------------------------------------------------------

    /**
     * Creates a ByteAccumulator used to gather bytes from various sources
     * with a default initial size and a default growth increment.
     */
    public ByteAccumulator()
    {
        bs = ArrayUtils.EMPTY_BYTE_ARRAY ;
        pos = 0 ;
        initial = DEFAULT_INIT_SIZE ;
        increment = DEFAULT_INCREMENT ;
    }


    /**
     * Creates a ByteAccumulator used to gather bytes from various sources
     * with a default initial size and a default growth increment.
     *
     * @param initial the initial size for the backing store
     */
    ByteAccumulator( int initial )
    {
        if ( initial <= 0 )
        {
            bs = ArrayUtils.EMPTY_BYTE_ARRAY ;
            this.initial = 0 ;
        }
        else
        {
            bs = new byte[initial] ;
            this.initial = initial ;
        }

        pos = 0 ;
        increment = DEFAULT_INCREMENT ;
    }


    /**
     * Fills this accumulator with the content of the argument buffer into
     * this accumulator.  The buffer argument is fully drained when this
     * operation completes.
     *
     * @param buf the buffer to fill into this accumulator
     */
    public void fill( ByteBuffer buf )
    {
        while ( buf.hasRemaining() )
        {
            if ( pos >= bs.length )
            {
                int size = 0 ;

                if ( buf.remaining() > increment )
                {
                    size = bs.length + buf.remaining() ;
                }
                else
                {
                    size = bs.length + increment ;
                }

                byte[] dest = new byte[ size ] ;
                System.arraycopy( bs, 0, dest, 0, bs.length ) ;
                bs = dest ;
            }

            /*
             * Find out how much space we have left and if it can hold the
             * remaining contents of the buffer.
             */
            int spaceLeft = bs.length - pos ;
            if ( buf.remaining() <= spaceLeft )
            {
                int remaining = buf.remaining() ;
                buf.get( bs, pos, remaining ) ;
                pos += remaining ;
                return ;
            }

            /*
             * there are more bytes in the buffer than we have space so we read
             * as much as we can into the empty space filling it up all the way
             * until another cycle of this loop allocates more space.
             */
            buf.get( bs, pos, spaceLeft ) ;
            pos += spaceLeft ;
        }
    }


    /**
     * Wraps a ByteBuffer around the populated bytes of this ByteAccumulator
     * and resets the backing store to a newly allocated byte array of initial
     * size.
     *
     * @return the compacted byte[] wrapped as a ByteBuffer
     */
    public ByteBuffer drain()
    {
        ByteBuffer compacted ;

        if ( pos == bs.length )
        {
            compacted = ByteBuffer.wrap( bs ) ;
        }
        else
        {
            compacted = ByteBuffer.wrap( bs, 0, pos ) ;
        }

        if ( initial <= 0 )
        {
            bs = ArrayUtils.EMPTY_BYTE_ARRAY ;
        }
        else
        {
            bs = new byte[initial] ;
        }

        pos = 0 ;
        return compacted ;
    }


    /**
     * Wraps a ByteBuffer around the populated bytes of this ByteAccumulator
     * and resets the backing store to a newly allocated byte array of initial
     * size.
     *
     * @return the compacted byte[] wrapped as a ByteBuffer
     */
    public ByteBuffer drain( int initial )
    {
        ByteBuffer compacted ;

        if ( pos == bs.length )
        {
            compacted = ByteBuffer.wrap( bs ) ;
        }
        else
        {
            compacted = ByteBuffer.wrap( bs, 0, pos ) ;
        }

        if ( initial <= 0 )
        {
            bs = ArrayUtils.EMPTY_BYTE_ARRAY ;
        }
        else
        {
            bs = new byte[initial] ;
        }

        pos = 0 ;
        return compacted ;
    }


    /**
     * Allocates memory to handle a capacity without the need to grow.
     * This serves to control growth for more efficient use.
     *
     * @param capacity the capacity to hold without the need to grow
     */
    public void ensureCapacity( int capacity )
    {
        if ( bs.length < capacity )
        {
            byte[] newArray = new byte[capacity] ;

            if ( bs != ArrayUtils.EMPTY_BYTE_ARRAY )
            {
                System.arraycopy( bs, 0, newArray, 0, pos + 1 ) ;
            }

            bs = newArray ;
        }
    }


    /**
     * The growth increment by which the backing store is augmented.
     *
     * @return the number of bytes to grow the backing store by
     */
    public int getGrowthIncrement()
    {
        return increment ;
    }


    /**
     * The initial size of the backing store.
     *
     * @return the initial size in bytes of the backing store
     */
    public int getInitialSize()
    {
        return initial ;
    }


    /**
     * The current capacity of the backing store which may change as this
     * accumulator is filled with bytes.
     *
     * @return the current capacity in bytes
     */
    public int getCapacity()
    {
        return bs.length ;
    }


    /**
     * The remaining free space that can be filled before having to grow the
     * backing store of the accumulator.
     *
     * @return the remaining free space until the next growth spurt
     */
    public int getRemainingSpace()
    {
        return bs.length - pos ;
    }


    /**
     * The current position within the backing store marking the point to
     * which this accumulator is filled.
     *
     * @return the current fill position
     */
    public int getPosition()
    {
        return pos ;
    }


    /**
     * Gets a compacted copy of this ByteAccumulator's backing store.  The
     * compacted byte array is equal to the amount of bytes put into this
     * ByteAccumulator which may still have free space to populate since the
     * last growth took place.
     *
     * @return the compacted copy of this ByteAccumulator
     */
/*
    public byte[] getCompactedCopy()
    {
        byte[] compacted ;

        if ( pos == bs.length )
        {
            compacted = (byte[]) bs.clone() ;
        }
        else
        {
            compacted = new byte[pos+1] ;
        }

        System.arraycopy( bs, 0, compacted, 0, pos + 1 ) ;
        return compacted ;
    }
*/
}
