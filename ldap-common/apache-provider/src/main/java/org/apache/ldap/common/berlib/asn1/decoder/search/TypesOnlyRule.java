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
package org.apache.ldap.common.berlib.asn1.decoder.search;


import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.asn1.ber.primitives.PrimitiveUtils;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.message.SearchRequestImpl;

import java.nio.ByteBuffer;


/**
 * Rule used to set the typesOnly field of a SearchRequest.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class TypesOnlyRule extends AbstractRule
{
    /** the octet for the Java primitive boolean */
    private byte value = 0 ;
    /** boolean flag to determine if we have read the single octet */
    private boolean octetSet = false ;
    /** the tag this rule accepts */
    private final TagEnum tag = UniversalTag.BOOLEAN  ;


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
        // peek at SearchRequest underneath processing obj whose octets we set
        SearchRequestImpl req = ( SearchRequestImpl ) getDigester().peek( 1 );
        req.setTypesOnly( PrimitiveUtils.berDecodeBoolean( value ) );

        // switch state
        ( ( SearchRequestProcessing ) getDigester().peek() ).next();

        // cleanup
        this.value = 0 ;
        this.octetSet = false ;
    }
}
