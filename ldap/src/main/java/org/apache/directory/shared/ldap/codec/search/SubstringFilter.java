/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.directory.shared.ldap.codec.search;


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.directory.shared.asn1.ber.tlv.Length;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.util.LdapString;


/**
 * A Object that stores the substring filter. A substring filter follow this
 * grammar : substring = attr "=" ([initial] any [final] | (initial [any]
 * [final) | ([initial] [any] final) initial = value any = "*" *(value "*")
 * final = value
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SubstringFilter extends Filter
{
    // ~ Instance fields
    // ----------------------------------------------------------------------------

    /** The substring filter type (an attributeDescription) */
    private LdapString type;

    /**
     * This member is used to control the length of the three parts of the
     * substring filter *
     */
    private transient int substringsLength;

    /** The initial filter */
    private LdapString initialSubstrings;

    /** The any filter. It's a list of LdapString */
    private ArrayList anySubstrings;

    /** The final filter */
    private LdapString finalSubstrings;

    private transient int substringsFilterLength;

    private transient int substringsFilterSequenceLength;


    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * The constructor. We will create the 'any' subsring arraylist with only
     * one element.
     */
    public SubstringFilter()
    {
        anySubstrings = new ArrayList( 1 );
    }


    /**
     * Get the internal substrings
     * 
     * @return Returns the anySubstrings.
     */
    public ArrayList getAnySubstrings()
    {
        return anySubstrings;
    }


    /**
     * Add a internal substring
     * 
     * @param anySubstrings
     *            The anySubstrings to set.
     */
    public void addAnySubstrings( LdapString anySubstrings )
    {
        this.anySubstrings.add( anySubstrings );
    }


    /**
     * Get the final substring
     * 
     * @return Returns the finalSubstrings.
     */
    public LdapString getFinalSubstrings()
    {
        return finalSubstrings;
    }


    /**
     * Set the final substring
     * 
     * @param finalSubstrings
     *            The finalSubstrings to set.
     */
    public void setFinalSubstrings( LdapString finalSubstrings )
    {
        this.finalSubstrings = finalSubstrings;
    }


    /**
     * Get the initial substring
     * 
     * @return Returns the initialSubstrings.
     */
    public LdapString getInitialSubstrings()
    {
        return initialSubstrings;
    }


    /**
     * Set the initial substring
     * 
     * @param initialSubstrings
     *            The initialSubstrings to set.
     */
    public void setInitialSubstrings( LdapString initialSubstrings )
    {
        this.initialSubstrings = initialSubstrings;
    }


    /**
     * Get the attribute
     * 
     * @return Returns the type.
     */
    public LdapString getType()
    {
        return type;
    }


    /**
     * Set the attribute to match
     * 
     * @param type
     *            The type to set.
     */
    public void setType( LdapString type )
    {
        this.type = type;
    }


    /**
     * @return Returns the substringsLength.
     */
    public int getSubstringsLength()
    {
        return substringsLength;
    }


    /**
     * @param substringsLength
     *            The substringsLength to set.
     */
    public void setSubstringsLength( int substringsLength )
    {
        this.substringsLength = substringsLength;
    }


    /**
     * Compute the SubstringFilter length SubstringFilter : 0xA4 L1 | +--> 0x04
     * L2 type +--> 0x30 L3 | [+--> 0x80 L4 initial] [+--> 0x81 L5-1 any] [+-->
     * 0x81 L5-2 any] [+--> ... [+--> 0x81 L5-i any] [+--> ... [+--> 0x81 L5-n
     * any] [+--> 0x82 L6 final]
     */
    public int computeLength()
    {
        // The type
        substringsFilterLength = 1 + Length.getNbBytes( type.getNbBytes() ) + type.getNbBytes();
        substringsFilterSequenceLength = 0;

        if ( initialSubstrings != null )
        {
            substringsFilterSequenceLength += 1 + Length.getNbBytes( initialSubstrings.getNbBytes() )
                + initialSubstrings.getNbBytes();
        }

        if ( anySubstrings != null )
        {
            Iterator anyIterator = anySubstrings.iterator();

            while ( anyIterator.hasNext() )
            {
                LdapString any = ( LdapString ) anyIterator.next();
                substringsFilterSequenceLength += 1 + Length.getNbBytes( any.getNbBytes() ) + any.getNbBytes();
            }
        }

        if ( finalSubstrings != null )
        {
            substringsFilterSequenceLength += 1 + Length.getNbBytes( finalSubstrings.getNbBytes() )
                + finalSubstrings.getNbBytes();
        }

        substringsFilterLength += 1 + Length.getNbBytes( substringsFilterSequenceLength )
            + substringsFilterSequenceLength;

        return 1 + Length.getNbBytes( substringsFilterLength ) + substringsFilterLength;
    }


    /**
     * Encode the Substrings Filter to a PDU. Substrings Filter : 0xA4 LL 0x30
     * LL substringsFilter 0x04 LL type 0x30 LL substrings sequence | 0x80 LL
     * initial | / [0x81 LL any]* |/ [0x82 LL final] +--[0x81 LL any]+ \ [0x82
     * LL final] \ 0x82 LL final
     * 
     * @param buffer
     *            The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( "Cannot put a PDU in a null buffer !" );
        }

        try
        {
            // The SubstringFilter Tag
            buffer.put( ( byte ) LdapConstants.SUBSTRINGS_FILTER_TAG );
            buffer.put( Length.getBytes( substringsFilterLength ) );

            // The type
            Value.encode( buffer, type.getBytes() );

            // The SubstringSequenceFilter Tag
            buffer.put( UniversalTag.SEQUENCE_TAG );
            buffer.put( Length.getBytes( substringsFilterSequenceLength ) );

            if ( ( initialSubstrings == null ) && ( ( anySubstrings == null ) || ( anySubstrings.size() == 0 ) )
                && ( finalSubstrings == null ) )
            {
                throw new EncoderException( "Cannot have a null initial, any and final substring" );
            }

            // The initial substring
            if ( initialSubstrings != null )
            {
                buffer.put( ( byte ) LdapConstants.SUBSTRINGS_FILTER_INITIAL_TAG );
                buffer.put( Length.getBytes( initialSubstrings.getNbBytes() ) );
                buffer.put( initialSubstrings.getBytes() );
            }

            // The any substrings
            if ( anySubstrings != null )
            {
                Iterator anyIterator = anySubstrings.iterator();

                while ( anyIterator.hasNext() )
                {
                    LdapString any = ( LdapString ) anyIterator.next();
                    buffer.put( ( byte ) LdapConstants.SUBSTRINGS_FILTER_ANY_TAG );
                    buffer.put( Length.getBytes( any.getNbBytes() ) );
                    buffer.put( any.getBytes() );
                }
            }

            // The final substring
            if ( finalSubstrings != null )
            {
                buffer.put( ( byte ) LdapConstants.SUBSTRINGS_FILTER_FINAL_TAG );
                buffer.put( Length.getBytes( finalSubstrings.getNbBytes() ) );
                buffer.put( finalSubstrings.getBytes() );
            }
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( "The PDU buffer size is too small !" );
        }

        return buffer;
    }


    /**
     * Return a string compliant with RFC 2254 representing a Substring filter
     * 
     * @return The substring filter string
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        if ( initialSubstrings != null )
        {
            sb.append( initialSubstrings.toString() );
        }

        sb.append( '*' );

        if ( anySubstrings != null )
        {
            Iterator anyIterator = anySubstrings.iterator();

            while ( anyIterator.hasNext() )
            {
                sb.append( ( LdapString ) anyIterator.next() ).append( '*' );
            }
        }

        if ( finalSubstrings != null )
        {
            sb.append( finalSubstrings.toString() );
        }

        return sb.toString();
    }
}