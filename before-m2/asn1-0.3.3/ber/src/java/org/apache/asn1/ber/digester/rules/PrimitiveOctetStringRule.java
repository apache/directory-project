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
package org.apache.asn1.ber.digester.rules ;


import java.nio.ByteBuffer;

import org.apache.asn1.ber.Length;
import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.asn1.ber.primitives.UniversalTag;


/**
 * A rule that collects the value bytes of an ASN.1 OCTET STRING and pushes
 * the buffer of bytes onto the digester's Object stack as a ByteBuffer.
 * <p>
 * This rule can only handle primitive octet strings.  Constructed OCTET STRING
 * values are simply ignored by this rule rather than throwing exceptions.
 * </p>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PrimitiveOctetStringRule extends AbstractRule
{
    /** used to accumulate value bytes */
    private final ByteAccumulator accumulator = new ByteAccumulator( 0 ) ;
    /** used to determine if our type is constructed or primitive */
    private boolean isConstructed = false ;
    /** the tag to be accepted which defaults to an UNIVERSAL OCTET_STRING */
    private final TagEnum tag ;


    // -----------------------------------------------------------------------
    // C O N S T R U C T O R S
    // -----------------------------------------------------------------------


    /**
     * Creates a rule using defaults where only the OCTET_STRING tag id
     * is accepted.
     */
    public PrimitiveOctetStringRule()
    {
        tag = UniversalTag.OCTET_STRING ;
    }


    /**
     * Creates a rule where only a specific tag is accepted.  Sometimes
     * OCTET_STRING fields are tagged with application specific tags.  In
     * this case we match for a different tag.
     *
     * @param tag the tag to accept
     */
    public PrimitiveOctetStringRule( TagEnum tag )
    {
        this.tag = tag ;
    }


    // -----------------------------------------------------------------------
    // Rule event method overrides
    // -----------------------------------------------------------------------


    /**
     * Rejects tag id's that are not equal to this Rules's id.
     *
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        isConstructed = ! isPrimitive ;

        if ( isConstructed )
        {
            return ;
        }

        if ( this.tag.getTagId() != id )
        {
            throw new IllegalArgumentException(
                    "Expecting " + this.tag.getName()
                    + " with an id of " + this.tag.getTagId()
                    + " but instead got a tag id of " + id ) ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#length(int)
     */
    public void length( int length )
    {
        if ( isConstructed )
        {
            return ;
        }

        // @todo Length should not be visible outside of the digester
        // package.  The digester or a contants interface should contain
        // these constants.
        if ( Length.INDEFINITE != length )
        {
            accumulator.ensureCapacity( length ) ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#value(java.nio.ByteBuffer)
     */
    public void value( ByteBuffer buf )
    {
        if ( isConstructed )
        {
            return ;
        }

        if ( buf == null || !buf.hasRemaining() )
        {
            return ;
        }

        accumulator.fill( buf ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        if ( isConstructed )
        {
            return ;
        }

        // push the octet string onto the digester's object stack
        getDigester().push( accumulator.drain( 0 ) ) ;

        // clean up
        isConstructed = false ;
    }


    // -----------------------------------------------------------------------
    // Protected Methods
    // -----------------------------------------------------------------------


    /**
     * Gets the ByteAccumulator used by this octet string gathering rule.
     *
     * @return the accumulator used to store octets
     */
    protected ByteAccumulator getAccumulator()
    {
        return accumulator ;
    }


    /**
     * Gets whether or not the current TLV for this octet string is
     * constructed.
     *
     * @return true if it's constructed, false otherwise
     */
    protected boolean isConstructed()
    {
        return isConstructed ;
    }


    /**
     * Gets whether or not the current TLV for this octet string is
     * constructed.
     *
     * @param isConstructed true to set to constructed, false otherwise
     */
    protected void setConstructed( boolean isConstructed )
    {
        this.isConstructed = isConstructed ;
    }


    /**
     * Gets the tag associated with this rule.
     *
     * @return the tag associated with this rule
     */
    protected TagEnum getTag()
    {
        return tag ;
    }
}
