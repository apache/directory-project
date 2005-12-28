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

import org.apache.asn1.codec.DecoderException;


/**
 * The length component of a BER TLV Tuple.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class Length
{
    /** used to mark length as indefinite */
    public static final int INDEFINITE = -2 ;
    /** used to mark length as undefined */
    public static final int UNDEFINED = -1 ;
    /** the end long form terminate bit flag mask */
    public static final int END_MASK = 0x80 ;

    /** the value for this tlv length */
    private int value = UNDEFINED ;
    /** the number of octets needed to complete this length component */
    private int numOctets = UNDEFINED ;
    /** whether or not this length has been fixated */
    private boolean isFixated = false ;
    /** a byte buffer used to collect the arriving length octets */
    private final ByteBuffer buf = ByteBuffer.allocate( 5 ) ;


    /**
     * Checks to see if the length has been fixated.
     *
     * @return true if it is fixated, false if not
     */
    public boolean isFixated()
    {
        return isFixated ;
    }


    /**
     * Clears this tag's data of all bytes and values calculated so all is as it
     * was when this instance was created.
     */
    void clear()
    {
        isFixated = false ;
        value = 0 ;
        numOctets = 1 ;
        buf.clear() ;
    }


    /**
     * Fixates the data within this Length calculating all the derived
     * properties from the existing set of octets.  While fixated octets
     * cannot be added.
     *
     * @throws org.apache.asn1.codec.DecoderException if this Length is invalid
     */
    void fixate() throws DecoderException
    {
        buf.flip() ;
        value = getLength( buf ) ;
        isFixated = true ;
    }


    /**
     * Adds an octet to this Length component and as a side effect fixates the
     * Length component if all the required length data has arrived.
     *
     * @param octet the 8 bit byte to add
     */
    void add( byte octet ) throws DecoderException
    {
        if ( isFixated )
        {
            throw new IllegalStateException( "data added to fixated length" ) ;
        }

        buf.put( octet ) ;

        if ( buf.position() == 1 )
        {
            // if its the long form, but not above 126 octets : (1)111 1111 is not
        	// allowed : this value is reserved for future extension.
            if ( END_MASK == ( octet & END_MASK ))
            {
            	int typeLength = octet & 0x7F;

            	if (typeLength == 0)
            	{
                    numOctets = INDEFINITE;
                    fixate() ;
            	}
            	else if (typeLength == 0x7F)
            	{
            		throw new DecoderException( "The number of octets must not be 127 (reserved for future extension) " ) ;
            	}
            	else
            	{
	                // capture number of octets we need to compute length
	                numOctets = octet & 0x7F ;
            	}
            }
            else
            {
                fixate() ;
            }
        }

        /*
         * if we have collected all the octets needed for computing the long
         * form length so we need to calculate the length and just fixate
         */
        else if ( buf.position() >= numOctets + 1 )
        {
            fixate() ;
        }
    }


    /**
     * Gets the length of the value.
     *
     * @return the length of the value
     */
    public int getLength()
    {
        return value ;
    }


    /**
     * Gets the number of octets currently in this Length component.
     *
     * @return the number of octets currently within this Length component
     */
    public int size()
    {
        return buf.position() ;
    }


    /**
     * Decodes the length of a value for a tlv using the Length field bytes.
     *
     * @param octets the length field bytes in the TLV
     * @return the length of the TLV
     * @throws DecoderException if the precision cannot hold the number
     */
    public static int getLength( ByteBuffer octets ) throws DecoderException
    {
        if ( octets.remaining() >= 6 )
        {
            /*
             * If this exception is ever thrown which is highly unlikely, then
             * we need to switch to another data type to return because after
             * 5 bytes the int can no longer hold the number.
             */
            throw new DecoderException( "Length number is too large." ) ;
        }

        byte octet = octets.get() ;

        // if we are using the short form then just return the first octet
        if ( ( octet & END_MASK ) == 0 )
        {
            return octet ;
        }
        // using the indefinite form
        else if ( ( octet & 0x7F ) == 0 )
        {
            return INDEFINITE ;
        }

        // using the long form so we calculate the length from all octets
        int length = 0 ;
        for ( int ii = octets.remaining(), shift = (ii-1)<<3; ii > 0; ii--, shift -= 8 )
        {
            length |= ( 0xFF & ( int ) octets.get() ) << shift ;
        }

        // calculate tag value w/ long tag format
//        int shift = 0 ;
//        do
//        {
//            length |= ( 0xFF & ( int ) octets.get() ) << shift ;
//            shift += 8 ;
//        }
//        while ( octets.hasRemaining() ) ;

        return length ;
    }
}
