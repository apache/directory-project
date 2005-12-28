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


import org.apache.asn1.codec.DecoderException;


/**
 * The Tag component of a BER TLV Tuple.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class Tag
{
    /** tag flag for the primitive/constructed bit - 0010 0000 - 0x20 */
    private static final int CONSTRUCTED_FLAG = 0x20 ;
    /** tag mask for the primitive/constructed bit - 1101 1111 - 0xDF */
    // private static final int CONSTRUCTED_MASK = ~CONSTRUCTED_FLAG ;

    /** tag mask for the short tag format - 0001 1111 - 0x1F */
    static final int SHORT_MASK = 0x1F ;
    /** tag mask for the long tag format - 0111 1111 - 0x7F */
    static final int LONG_MASK = 0x7F ;
    /** tag flag indicating the use of the long tag encoding form */
    private static final int LONG_FLAG = 0x80 ;

    /** the max id size with one tag octet */
    private static final int ONE_OCTET_IDMAX = 30 ;
    /** the max id size with two tag octets */
    private static final int TWO_OCTET_IDMAX = (1<<7)-1 ;
    /** the max id size with three tag octets */
    private static final int THREE_OCTET_IDMAX = (1<<14)-1 ;
    /** the max id size with four tag octets */
    private static final int FOUR_OCTET_IDMAX = (1<<21)-1 ;

    /** tag id */
    private int id = 0 ;
    /** whether or not this tag represents a primitive type */
    private boolean isPrimitive = true ;
    /** whether or not this tag has been fixated */
    private boolean isFixated = false ;
    /** the type class of this tag */
    private TypeClass typeClass = TypeClass.APPLICATION ;
    /** buffer backed by a Java int to collect the arriving tag octets */
    private final TagOctetCollector buf = new TagOctetCollector() ;


    /**
     * Clears this tag's data of all bytes and values calculated so all is as it
     * was when this instance was created.
     */
    void clear()
    {
        id = 0 ;
        isFixated = false ;
        isPrimitive = true ;
        typeClass = TypeClass.APPLICATION ;
        buf.clear() ;
    }
    
    
    /**
     * Fixates the data within this Tag calculating all the derived 
     * properties from the existing set of octets.  While fixated octets
     * cannot be added.
     * 
     * @throws DecoderException if this Tag is invalid
     */
    void fixate() throws DecoderException
    {
        isFixated = true ;
        id = getTagId( buf ) ;
        isPrimitive = isPrimitive( buf.get( 0 ) ) ;
        typeClass = TypeClass.getTypeClass( buf.get( 0 ) ) ;
    }
    
    
    /**
     * Adds an octet to this Tag and as a size effect may fixate the Tag if
     * all the expected data has arrived.
     * 
     * @param octet the 8 bit byte to add
     */
    void add( byte octet ) throws DecoderException
    {
        if ( isFixated )
        {  
            throw new IllegalStateException( "data added to fixated tag" ) ;
        }
        
        buf.put( octet ) ;
        
        if ( buf.size() == 1 )
        {
            // if its the short form so we just fixate now!
            if ( ( SHORT_MASK & octet ) != SHORT_MASK )
            {
                fixate() ;
            }
        }
        
        /*
         * From here on we're dealing with the long form of the tag.  The
         * terminating octet for the long form uses a 0 for the most 
         * significant bit to flag the end of the train of octets for the 
         * tag id.
         */ 
        else if ( ( octet & LONG_FLAG ) == 0 )
        {
            fixate() ;
        }
    }
    
    
    /**
     * Gets a copy of the octets composing this Tag.
     * 
     * @return the octets representing this Tag
     */
    public byte[] getOctets()
    {
        return buf.toArray() ;
    }
    
    
    /**
     * Gets the number of octets in this Tag.
     * 
     * @return the number of octets within this Tag
     */
    public int size()
    {
        return buf.size() ;
    }

    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId()
    {
        return id ;
    }
    
    
    /**
     * Gets the raw tag as it is stuffed into a primitive int.
     * 
     * @return a primitive int stuffed with the first four octets of the tag
     */
    public int getRawTag()
    {
        return buf.getIntValue() ;
    }
    
    
    /**
     * Checks to see if the tag represented by this Tag is primitive or 
     * constructed.
     * 
     * @return true if it is primitive, false if it is constructed
     */
    public boolean isPrimitive()
    {
        return isPrimitive ;
    }
    
    
    /**
     * Checks to see if the tag has been fixated.
     * 
     * @return true if it is fixated, false if not
     */
    public boolean isFixated()
    {
        return isFixated ;
    }
    
    
    /**
     * Gets the type class for this Tag.
     * 
     * @return the typeClass for this Tag
     */
    public TypeClass getTypeClass()
    {
        return typeClass ;
    }


    // ------------------------------------------------------------------------
    // Utility Methods For Dealing With Tags and Tag Octets
    // ------------------------------------------------------------------------


    /**
     * Sets the id of a tag encoded as a Java primitive integer.
     *
     * @param encodedTag the tag encoded as a Java primitive integer
     * @param id the new tag id to set within the encodedTag
     * @return the modified Java primitive int encoded tag with the new tag id
     */
    public final static int setIntEncodedId( int encodedTag, int id )
    {
        if ( id <= ONE_OCTET_IDMAX )
        {
            encodedTag |= ( id << 24 ) ;
        }
        else if ( id <= TWO_OCTET_IDMAX )
        {
            encodedTag |= ( SHORT_MASK << 24 ) ;
            encodedTag |= ( id & 0x0000007F ) << 16 ;
        }
        else if ( id <= THREE_OCTET_IDMAX )
        {
            encodedTag |= ( SHORT_MASK << 24 ) ;
            encodedTag |= ( id & 0x00003F80 ) << 9 ;
            encodedTag |= ( id & 0x0000007F ) << 8 ;
            encodedTag |= 0x00800000 ;
        }
        else if ( id <= FOUR_OCTET_IDMAX )
        {
            encodedTag |= ( SHORT_MASK << 24 ) ;
            encodedTag |= ( id & 0x001FC000 ) << 2 ;
            encodedTag |= ( id & 0x00003F80 ) << 1 ;
            encodedTag |= ( id & 0x0000007F ) ;
            encodedTag |= 0x00808000 ;
        }
        else
        {
            String msg = "Id argument value of " + id
                    + " was greater than the maximum supported id of "
                    + FOUR_OCTET_IDMAX ;
            throw new IllegalArgumentException( msg ) ;
        }

        return encodedTag;
    }


    /**
     * Assembles the Java primitive int based encoding for a tag using a set
     * of parameters.
     *
     * @param type
     * @param id
     * @param isConstructed
     * @return
     */
    public final static int
            getIntEncodedTag( TypeClass type, int id, boolean isConstructed )
    {
        int value = type.getValue() << 24 ;

        if ( isConstructed )
        {
            value |= ( CONSTRUCTED_FLAG << 24 ) ;
        }

        value = setIntEncodedId( value, id );

        return value ;
    }


    /**
     * Gets the tag id of a TLV from tag octets.
     *
     * @param octets the set of octets needed to determine the tag value
     *      (a.k.a identifier octets)
     * @return the tag id
     * @throws DecoderException if the id cannot be determined due to
     *      type limitations of this method's return type.
     */
    public final static int getTagId( byte[] octets )
        throws DecoderException
    {
        if ( octets.length > 4 )
        {
            /*
             * If this exception is ever thrown which is highly unlikely, then
             * we need to switch to another data type to return because after
             * 4 bytes the int can no longer hold the number.
             */
            throw new DecoderException( "Tag number is too large." ) ;
        }

        int id = octets[0] & SHORT_MASK ;

        // if bits are not all 1's then return the value which is less than 31
        if ( id != SHORT_MASK && octets.length == 1 )
        {
            return id ;
        }

        // clear the id now
        id = 0 ;

        // calculate tag value w/ long tag format
        for( int ii = 1 ; ii < octets.length; ii++ )
        {
        	id = (id << 7) | (octets[ii] & LONG_MASK);
        }

        return id ;
    }


    /**
     * Gets the tag id of a TLV from tag octets encoded as a Java primitive int.
     *
     * @param octets the tag octets encoded as a Java primitive int
     * @return the tag id
     */
    public final static int getTagId( int octets )
    {
        // set id to the most significant octet in the int
        int id = ( octets >> 24 ) & SHORT_MASK;

        // if bits are not all 1's then return the value which is less than 31
        if ( id != SHORT_MASK )
        {
            return id;
        }

        // clear the id now to prepare for long tag form
        id = 0;

        // get the second most significant octet from int and apply it to the id
        int octet = ( octets & 0x00ff0000 ) >> 16;
        id |= octet & LONG_MASK ;

        // if this is the last octet in long form return
        if ( ( octet & 0x80 ) == 0 )
        {
            return id ;
        }

        // clear octet and get the third most significant octet and apply it
        octet = 0;
        octet = ( octets & 0x0000ff00 ) >> 8;

        if ( octet == 0 )
        {
            return id << 7 ;
        }

        id <<= 7;
        id |= octet & LONG_MASK;

        // if this is the last octet in long form return
        if ( ( octet & 0x80 ) == 0 )
        {
            return id ;
        }

        // clear octet and get the least significant octet and apply it
        octet = 0;
        octet = octets & 0x000000ff;
        id <<= 7;
        id |= octet & LONG_MASK;

        return id ;
    }


    /**
     * Gets the tag id of a TLV from the tag octets.
     * 
     * @param octets the set of octets needed to determine the tag value 
     *      (a.k.a identifier octets)
     * @return the tag id
     */
    public final static int getTagId( TagOctetCollector octets )
    {
        int id = octets.get( 0 ) & SHORT_MASK ;
        
        // if bits are not all 1's then return the value which is less than 31
        if ( id != SHORT_MASK && octets.size() == 1 )
        {
            return id ;
        }
        
        // clear the id now
        id = 0 ;
    
        // calculate tag value w/ long tag format
        for( int ii = 1 ; ii < octets.size(); ii++ )
        {    
        	id = (id << 7) | (octets.get(ii) & LONG_MASK);
        }
        
        return id ;
    }


    /**
     * Checks to see if the tag is a primitive.
     * 
     * @param octet the first octet of the tag
     * @return true if this tag is of the simple type, false if constructed
     */
    public final static boolean isPrimitive( int octet )
    {
        return ( octet & CONSTRUCTED_FLAG ) == 0 ;
    }


    /**
     * Checks to see if the tag is constructed.
     * 
     * @param octet the first octet of the tag
     * @return true if constructed, false if primitive
     */
    public final static boolean isConstructed( int octet )
    {
        return ( octet & CONSTRUCTED_FLAG ) == CONSTRUCTED_FLAG ;
    }


    public static boolean isRawTagConstructed( int rawTag )
    {
        if ( ( rawTag & 0x20000000 ) > 0 )
        {
            return true;
        }

        return false;
    }
}
