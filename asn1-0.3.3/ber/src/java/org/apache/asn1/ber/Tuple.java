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
import java.util.List;

import org.apache.commons.lang.ArrayUtils;


/**
 * TLV Tuple used by the value chunking decoder.  Because the length field is
 * a primitive int it's maximum value is 2,147,483,647 a single TLV's tuple
 * cannot have a length over this amount or a value size over 2 GB.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class Tuple
{
    /** empty buffer reused for handling null */
    private static final ByteBuffer EMPTY_BUFFER =
        ByteBuffer.wrap( ArrayUtils.EMPTY_BYTE_ARRAY ) ;

    /** mask for bit 5 with 0-based index */
    private static final int BIT_5 = 0x20 ;
    /** mask for bit 6 with 0-based index */
    private static final int BIT_6 = 0x40 ;
    /** mask for bit 7 with 0-based index */
    private static final int BIT_7 = 0x80 ;

    /** precalculated left shift of 1 by 14 places */
    private static final int BIT_13 = 1 << 14 ;
    /** precalculated left shift of 1 by 16 places */
    private static final int BIT_15 = 1 << 16 ;
    /** precalculated left shift of 1 by 21 places */
    private static final int BIT_20 = 1 << 21 ;
    /** precalculated left shift of 1 by 24 places */
    private static final int BIT_23 = 1 << 24 ;
    /** precalculated left shift of 1 by 28 places */
    private static final int BIT_27 = 1 << 28 ;

    /** the raw tag data */
    int rawTag = 0 ;
    /** the tag id for this TLV tuple */
    int id = 0 ;
    /** the flag for whether or not this TLV is constructed or primitive */
    boolean isPrimitive = true ;
    /** the type class for this TLV */
    TypeClass typeClass = TypeClass.APPLICATION ;
    /** the length for this TLV tuple's value field */
    int length = 0 ;
    /** the present value chunk buffer read for this TLV tuple */
    ByteBuffer valueChunk = EMPTY_BUFFER ;

    /** tlv byte index */
    int index = Length.UNDEFINED ;
    /** tlv value index for how far into the value we have read */
    int valueIndex = Length.UNDEFINED ;


    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------


    /**
     * Empty do nothing tuple.
     */
    public Tuple()
    {
    }


    /**
     * Creates constructed application type tlv tuples.  Constructed TLV's with
     * a definate length will use this constructor predominantly.  The TypeClass
     * defualts to APPLICATION.
     *
     * @param id the tag id of the tlv
     * @param length the length of the value which is the length of all the
     *      nested tuples.
     */
    public Tuple( int id, int length )
    {
        this( id, length, TypeClass.APPLICATION ) ;
    }


    /**
     * Creates constructed application type tlv tuples.  Constructed TLV's with
     * a definate length will use this constructor predominantly.
     *
     * @param id the tag id of the tlv
     * @param length the length of the value which is the length of all the
     *      nested tuples.
     * @param typeClass the type class of this tlv tuple
     */
    public Tuple( int id, int length, TypeClass typeClass )
    {
        this.id = id ;
        this.length = length ;
        valueChunk = EMPTY_BUFFER ;
        isPrimitive = false ;

        if ( typeClass != null )
        {
            this.typeClass = typeClass ;
        }
    }


    /**
     * Creates constructed application type tlv tuples.  Constructed TLV's with
     * a definate length will use this constructor predominantly.
     *
     * @param id the tag id of the tlv
     * @param length the length of the value which is the length of all the
     *      nested tuples.
     * @param isPrimitive whether or not this Tuple is primitive or constructed
     * @param typeClass the type class of this tlv tuple
     */
    public Tuple( int id, int length, boolean isPrimitive, TypeClass typeClass )
    {
        this.id = id ;
        this.length = length ;
        valueChunk = EMPTY_BUFFER ;
        isPrimitive = false ;

        if ( typeClass != null )
        {
            this.typeClass = typeClass ;
        }
    }


    /**
     * Creates a tuple where the length is indefinite.  The tuple according to
     * the BER encoding must be of the constructed type.
     *
     * @param id the tag id of the tlv
     * @param typeClass the type class for the tlv
     */
    public Tuple( int id, TypeClass typeClass )
    {
        this.id = id ;
        this.isPrimitive = false ;
        valueChunk = EMPTY_BUFFER ;
        length = Length.INDEFINITE ;

        if ( typeClass != null )
        {
            this.typeClass = typeClass ;
        }
    }


    // ------------------------------------------------------------------------
    // Public Accessors and Mutators
    // ------------------------------------------------------------------------


    /**
     * Gets the tag id for this TLV Tuple.
     *
     * @return the tag id
     */
    public int getId()
    {
        return id ;
    }


    /**
     * Sets the id of this Tuple and as a side effect the rawTag.
     *
     * @param id the new tag id to set
     */
    public void setId( int id )
    {
        this.id = id ;
        rawTag = Tag.setIntEncodedId( rawTag, id );
    }


    /**
     * Gets the raw tag as it is stuffed into a primitive int.
     *
     * @return a primitive int stuffed with the first four octets of the tag
     */
    public int getRawTag()
    {
        return rawTag ;
    }


    /**
     * Sets the raw tag encoded as a primitive int and as a side effect this
     * call also sets the id, primitive flag, and typeClass of this TLV tuple.
     *
     * @param rawTag the raw primitive int encoded tag.
     */
    public void setRawTag( int rawTag )
    {
        this.rawTag = rawTag;
        this.id = Tag.getTagId( rawTag );
        this.isPrimitive = ! Tag.isRawTagConstructed( rawTag );
        this.typeClass = TypeClass.getTypeClass( rawTag >> 24 );
    }


    /**
     * Sets the tag parameters using a tag enumeration type.  This operation
     * sets the id, isPrimitive, typeClass, and rawTag fields at the same time.
     *
     * @param tag the tag enumeration constant
     */
    public void setTag( TagEnum tag )
    {
        this.rawTag = tag.getValue();
        this.id = tag.getTagId();
        this.isPrimitive = ! Tag.isRawTagConstructed( tag.getValue() ) ;
        this.typeClass = tag.getTypeClass();
    }


    /**
     * Sets the tag parameters using a tag enumeration type explicitly setting
     * the primitive/constructed bit.  This operation sets the id, isPrimitive,
     * typeClass, and rawTag fields at the same time.
     *
     * @param tag the tag enumeration constant
     * @param isPrimitive primitive/constructed bit override
     */
    public void setTag( TagEnum tag, boolean isPrimitive )
    {
        this.rawTag = tag.getValue();
        this.id = tag.getTagId();
        this.isPrimitive = isPrimitive;
        this.typeClass = tag.getTypeClass();
    }


    /**
     * Gets the raw tag with the primitive/constructed flag dubbed out.
     * Effectively this makes every tag appear primitive and is done
     * to remove encoding ambiguities that could interfere with pattern
     * matching.
     *
     * @return the raw tag with the primitive/constructed flag dubbed out
     */
    public int getRawPrimitiveTag()
    {
        return rawTag & 0xDFFFFFFF ;
    }


    /**
     * Get's whether or not this tuples's length is indefinite.
     *
     * @return whether or not this tuple's length is indefinite
     */
    public boolean isIndefinite()
    {
        return length == Length.INDEFINITE ;
    }


    /**
     * Get's whether or not this tuple terminates an indefinite constructed
     * tuple.  This means that length == 0 && isPrimitive = true && id == 0
     * and the type class is universal.
     *
     * @return whether or not this node's length is indefinite
     */
    public boolean isIndefiniteTerminator()
    {
        return isPrimitive && id == 0 && length <= 0 &&
            typeClass.equals( TypeClass.UNIVERSAL ) ;
    }


    /**
     * Gets whether or not this TLV tuple is primitive or constructed.
     *
     * @return true if it is primitive, false if it is constructed
     */
    public boolean isPrimitive()
    {
        return isPrimitive ;
    }


    /**
     * Gets the value length for this TLV Tuple.
     *
     * @return the length in bytes of the value field for this TLV tuple
     */
    public int getLength()
    {
        return length ;
    }



    public void setLength( int length )
    {
        this.length = length;
    }


    /**
     * Gets the BER TLV TypeClass for this TLV Tuple.
     *
     * @return the BER TLV TypeClass for this TLV Tuple
     */
    public TypeClass getTypeClass()
    {
        return typeClass ;
    }


    /**
     * Gets the last chunk read for the value field (V-part) for this TLV Tuple.
     *
     * @return the last valueChunk field for this TLV Tuple
     */
    public ByteBuffer getLastValueChunk()
    {
        return valueChunk ;
    }


    /**
     * Sets the value representing the last chunk read or the last chunch to
     * write.
     *
     * @param buf the last chunk as a buffer
     */
    public void setLastValueChunk( ByteBuffer buf )
    {
        this.valueChunk = buf;
    }


    /**
     * Gets the total size of this TLV tuple in bytes.  This includes the
     * length of the tag field, the length of the length field and the length
     * of the value feild.
     *
     * @return the total TLV size in bytes
     */
    public int size()
    {
        if ( this.length == Length.INDEFINITE )
        {
            return getTagLength() + getLengthLength() ;
        }
        else
        {
            return getTagLength() + getLengthLength() + length ;
        }
    }


    // ------------------------------------------------------------------------
    // Utility methods and java.lang.Object overrides
    // ------------------------------------------------------------------------


    /**
     * Clears the values of this tuple.
     */
    public void clear()
    {
        this.id = 0 ;
        this.index = 0 ;
        this.rawTag = 0 ;
        this.isPrimitive = true ;
        this.length = Length.UNDEFINED ;
        this.typeClass = TypeClass.APPLICATION ;
        this.valueChunk = EMPTY_BUFFER ;
        this.valueIndex = Length.UNDEFINED ;
    }


    /**
     * Does not take into account the value, index or the valueIndex values when
     * checking for equality.  Technically if both are being constructed by
     * the decoder then they should only be equal when these values are equal
     * because the tag, length or value would not be correct.  Plus since this
     * is a chunking tuple the valueChunk means nothing with respect to the
     * final value.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o )
    {
        if ( o == this )
        {
            return true ;
        }

        if ( o instanceof Tuple )
        {
            Tuple t = ( Tuple ) o ;

            if ( t.id != id )
            {
                return false ;
            }

            if ( t.isPrimitive != isPrimitive )
            {
                return false ;
            }

            if ( t.length != length )
            {
                return false ;
            }

            if ( t.typeClass != typeClass )
            {
                return false ;
            }

            return true ;
        }

        return false ;
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        Tuple t = new Tuple() ;
        t.id = id ;
        t.rawTag = rawTag ;
        t.isPrimitive = isPrimitive ;
        t.typeClass = typeClass ;
        t.length = length ;

        /* ------------------------------------------------------------------ +/
         * @todo figure out if we should remove this section
         *
         * Do think we need this anymore since the last valueChunk does not
         * factor into the result returned by the equals(Object) method?
         * ------------------------------------------------------------------ */
        ByteBuffer bb = valueChunk ;
        ByteBuffer cloned = ByteBuffer.allocate( bb.capacity() ) ;
        int oldPos = bb.position() ;
        bb.rewind() ;
        cloned.put( bb ) ;
        cloned.limit( bb.limit() ) ;
        bb.position( oldPos ) ;
        cloned.rewind() ;
        t.valueChunk = cloned ;
        /* ------------------------------------------------------------------ */

        t.index = index ;
        t.valueIndex = valueIndex ;

        return t ;
    }


    // ------------------------------------------------------------------------
    // Tuple encoding operations
    // ------------------------------------------------------------------------


    /**
     * If this is a primitive TLV then the valueBytes argument is used to
     * produce an encoded image of this TLV.  If it is constructed then
     * only the TL part of the tuple is encoded leaving the value to be encoded
     * by the set of child TLVs.
     *
     * @todo this should produce chunking output and needs to be removed from
     * here actually and made into a standalone encoder.  You give it a buffer
     * and it fills it as much as it can remembering where the encode stopped.
     * Hence it is stateful as expected from the statemachine.
     *
     * @return partial encoded image if constructed or complete TLV if primitive
     */
    public ByteBuffer toEncodedBuffer( List valueChunks )
    {
        ByteBuffer octets = null ;
        int tagLength = getTagLength() ;
        int lengthLength = getLengthLength() ;
        int total = tagLength + lengthLength ;

        if ( isPrimitive )
        {
            total += length ;
        }

        octets = ByteBuffer.allocate( total ) ;
        setTag( octets, tagLength ) ;
        setLength( octets, lengthLength ) ;

        if ( isPrimitive )
        {
            for ( int ii = 0; ii < valueChunks.size(); ii++ )
            {
                octets.put( ( ByteBuffer ) valueChunks.get(ii) ) ;
            }
        }

        return ( ByteBuffer ) octets.flip() ;
    }


    /**
     * Sets the tag section within the buffer.
     *
     * @param octets the buffer to set the tag in
     * @param tagLength the length of the tag section
     */
    public void setTag( ByteBuffer octets, int tagLength )
    {
        if ( tagLength >= 6 )
        {
            throw new IllegalArgumentException( "cannot support id's as large "
                    + "as " + id + " unless we start using longs for the id" ) ;
        }

        byte octet = ( byte ) typeClass.getValue() ;
        int i = octets.position();

        if ( ! isPrimitive )
        {
            octet |= BIT_5;
        }

		if ( id < 31 )
		{
            octets.put( ( byte ) ( octet | (id & Tag.SHORT_MASK ) ) ) ;
            return;

		}
		else
		{
            octets.put( ( byte ) ( octet | Tag.SHORT_MASK ) ) ;
            i++;
		}

		switch ( tagLength - 1) {
        	case 5 :
                octets.put( ( byte ) ( ( ( id >> 21 ) & Tag.LONG_MASK ) | BIT_7 ) ) ;
                i++;
                // fallthrough

        	case 4:
                octets.put( ( byte ) ( ( ( id >> 21 ) & Tag.LONG_MASK ) | BIT_7 ) ) ;
                i++;
                // fallthrough

        	case 3 :
                octets.put( ( byte ) ( ( ( id >> 14 ) & Tag.LONG_MASK ) | BIT_7 ) ) ;
                i++;
                // fallthrough

        	case 2 :
                octets.put( ( byte ) ( ( ( id >> 7 ) & Tag.LONG_MASK ) | BIT_7 ) ) ;
                i++;
                // fallthrough

        	case 1 :
                octets.put( ( byte ) ( id  & Tag.LONG_MASK ) ) ;
                break;
        }

		//octets.f
        return ;
    }


    /**
     * Sets the value length of this Tuple.
     *
     * @param length the length of this tuple's value.
     * @see Tuple#size() to get the entire determinate length of tuple
     */
    public void setValueLength( int length )
    {
        this.length = length;
    }


    /**
     * Sets the length bytes.
     *
     * @param octets the byte [] to set length in
     * @param lengthBytes the number bytes for the length section
     */
    public void setLength( ByteBuffer octets, int lengthBytes )
    {
        if ( lengthBytes >= 6 )
        {
            throw new IllegalArgumentException( "cannot support lengths larger "
                    + "than a max integer using " + lengthBytes
                    + " bytes unless we start using longs or BigIntegers for "
                    + "the length" ) ;
        }

        if ( length == Length.INDEFINITE )
        {
            octets.put( ( byte ) BIT_7 ) ;
            return ;
        }
        else if ( lengthBytes == 1 )
        {
            octets.put( ( byte ) length ) ;
            return ;
        }
        else
        {
            /*
             * Here we basically set the first byte of the length field which
             * is in the long form.  In this case the first byte's 7 least
             * significant bits hold the length of the length field or the
             * number of bytes used to hold the size of the value.  BTW the
             * first most significant bit is set to 1 to mark the long form and
             * hence why we bitwise or it with 0x80 which is BIT_7.
             */

            /*
             * the lengthBytes argument is the number of octets for the L field
             * total which for the long form includes the first octet for the
             * length of length (N) value where N < 127.  Technically with the
             * 7 bits we can specify an N of up to 127 but this value of N is
             * reserved.  Anyway below we subtract one from lengthBytes to get
             * N which is set as the last 7 bits of the first octet of the L
             * field.
             */
            octets.put( ( byte ) ( BIT_7 | ( lengthBytes - 1 ) ) ) ;
        }


        // using the long form so we calculate the length from all octets
        for ( int ii = 0, shift = (lengthBytes-2)<<3; ii <= lengthBytes-2; ii++, shift -= 8 )
        {
            octets.put( octets.position() + ii, ( byte ) ( ( ( 0xff << shift ) & length ) >> shift ) );
        }

        octets.position( octets.position() + lengthBytes - 1 );

//        if ( lengthBytes >= 2 )
//        {
//            octets.put( ( byte ) ( 0xff & length ) ) ;
//        }
//        else
//        {
//            return ;
//        }
//
//        if ( lengthBytes >= 3 )
//        {
//            octets.put( ( byte ) ( ( 0xff00 & length ) >> 8 ) ) ;
//        }
//        else
//        {
//            return ;
//        }
//
//        if ( lengthBytes >= 4 )
//        {
//            octets.put( ( byte ) ( ( 0xff0000 & length ) >> 16 ) ) ;
//        }
//        else
//        {
//            return ;
//        }
//
//        if ( lengthBytes >= 5 )
//        {
//            octets.put( ( byte ) ( ( 0xff000000 & length ) >> 24 ) ) ;
//        }
//        else
//        {
//            return ;
//        }

    }


    /**
     * Gets the length in bytes of the tag section for this TLV tuple.
     *
     * @return the length in bytes of the tag section for this TLV tuple
     */
    public int getTagLength()
    {
        if ( id < 31 )
        {
            return 1 ;
        }
        else if ( id < BIT_6 )
        {
            return 2 ;
        }

        else if ( id < BIT_13 )
        {
            return 3 ;
        }
        else if ( id < BIT_20 )
        {
            return 4 ;
        }
        else if ( id < BIT_27 )
        {
            return 5 ;
        }

        throw new IllegalArgumentException( "cannot support id's larger than "
                + id + " unless we start using longs for the id" ) ;
    }


    /**
     * Gets the length in bytes of the length section of this TLV Tuple.
     *
     * @return the length in bytes of the length section
     */
    public int getLengthLength()
    {
        if ( length == Length.INDEFINITE )
        {
            return 1 ;
        }

        if ( length < 0 )
        {
            throw new IllegalArgumentException( "integer overflow makes id "
                    + "negative with a value of " + id
                    + " - unless we start using longs for"
                    + " the id there you've hit a limitation" ) ;
        }
        else if ( length < BIT_7 )
        {
            return 1 ;
        }
        else if ( length < 256 )
        {
            return 2 ;
        }
        else if ( length < BIT_15 )
        {
            return 3 ;
        }
        else if ( length < BIT_23 )
        {
            return 4 ;
        }
        else
        {
            return 5 ;
        }
    }
}
