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

import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.asn1.ber.primitives.PrimitiveUtils;
import org.apache.asn1.ber.primitives.UniversalTag;


/**
 * A rule to Decode a BER encoded ASN.1 INTEGER into a Java primitive int.
 * <p>
 * The bytes to form the integer are extracted from the BER value which may
 * arrive in multiple chunks.  The individual bytes are temporarily stored
 * within a 4 byte array while incrementing a counter to track the capture.
 * Once gathered the bytes are decoded into a int in the finish
 * </p>
 * <p>
 * As a side effect once the decode is complete, the primitive value is pushed
 * onto the primitive int stack to be utilized by other rules later.  If there
 * is a loss of precision where the ASN.1 INTEGER is larger or smaller than
 * the maximum or minimum value of a Java primitive integer an exception is
 * thrown.
 * </p>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class PrimitiveBooleanRule extends AbstractRule
{
    /** the octet for the Java primitive boolean */
    private byte value = 0 ;
    /** boolean flag to determine if we have read the single octet */
    private boolean octetSet = false ;
    /** the tag this rule accepts */
    private final TagEnum tag ;


    // -----------------------------------------------------------------------
    // C O N S T R U C T O R S
    // -----------------------------------------------------------------------


    /**
     * Creates a default primitive boolean decoding rule that only accepts
     * tags of UniversalTag.BOOLEAN.
     */
    public PrimitiveBooleanRule()
    {
        tag = UniversalTag.BOOLEAN ;
    }


    /**
     * Creates a default primitive integer decoding rule that only accepts
     * tags of UniversalTag.INTEGER.
     */
    public PrimitiveBooleanRule( TagEnum tag )
    {
        this.tag = tag ;
    }


    // -----------------------------------------------------------------------
    // Rule Implementation
    // -----------------------------------------------------------------------


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        if ( id != tag.getTagId() )
        {
            throw new IllegalArgumentException(
                    "Expecting " + tag.getName()
                    + " with an id of " + tag.getTagId()
                    + " but instead got a tag id of " + id ) ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#length(int)
     */
    public void length( int length )
    {
        if ( length != 1 )
        {
            throw new IllegalArgumentException( "The target primitive for this "
                + "rule only requires a single octet with a length of 1.  "
                + "The length of the field however is " + length ) ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#value(java.nio.ByteBuffer)
     */
    public void value( ByteBuffer buf )
    {
        if ( octetSet )
        {
            throw new IllegalArgumentException( "The target primitive for this "
                + "rule only requires a single octet with a length of 1.  "
                + "That octet has already been set." ) ;
        }

        while ( buf.hasRemaining() )
        {
            value = buf.get() ;
            octetSet = true ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        if ( getDigester() != null )
        {
            getDigester().pushBoolean(
                    PrimitiveUtils.berDecodeBoolean( value ) ) ;
        }

        // cleanup
        this.value = 0 ;
        this.octetSet = false ;
    }
}
