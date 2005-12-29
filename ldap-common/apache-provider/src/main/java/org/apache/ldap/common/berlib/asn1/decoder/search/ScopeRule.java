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


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.primitives.PrimitiveUtils;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.message.ScopeEnum;

import java.nio.ByteBuffer;


/**
 * A rule used to set the scope of a SearchRequest.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ScopeRule extends BaseSearchRequestRule
{
    /** the bytes used to form the Java primitive integer */
    private final byte[] value = new byte[4] ;
    /** the current 8 bit position to fill in the integer */
    private int pos ;
    /** the number of bytes we must read */
    private int length ;


    public ScopeRule()
    {
        super( 3 );
    }


    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass );

        if ( getDigester().getTopTag() !=
                UniversalTag.ENUMERATED.getPrimitiveTag() )
        {
            setEnabled( false );
        }

        if ( getProcessing().getState() != getProcessing().SCOPE_STATE )
        {
            setEnabled( false );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#length(int)
     */
    public void length( int length )
    {
        if ( ! isEnabled() )
        {
            return ;
        }

        if ( length > 4 || length < 0 )
        {
            throw new IllegalArgumentException( "The target primitive for this "
                + "rule can only hold integers of 32 bits or 4 bytes.  "
                + "The length of the field however is " + length ) ;
        }

        this.length = length ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#value(java.nio.ByteBuffer)
     */
    public void value( ByteBuffer buf )
    {
        if ( ! isEnabled() )
        {
            return ;
        }

        if ( buf == null )
        {
            return ;
        }

        while ( buf.hasRemaining() && ( pos + 1 ) <= length )
        {
            value[pos] = buf.get() ;
            pos++ ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        if ( ! isEnabled() )
        {
            // cleanup
            this.pos = 0 ;
            this.length = 0 ;
            super.finish();
            return ;
        }

        // decode and push primitive integer onto stack
        int numba = PrimitiveUtils.decodeInt( this.value, 0, this.length ) ;

        switch( numba )
        {
            case 0:
                getRequest().setScope( ScopeEnum.BASEOBJECT );
                break;
                
            case 1:
                getRequest().setScope( ScopeEnum.SINGLELEVEL );
                break;
                
            case 2:
                getRequest().setScope( ScopeEnum.WHOLESUBTREE );
                break;
                
            default:
                throw new IllegalStateException(
                        "expected 0, 1, or 2 for scope but got " + numba ) ;
        }

        // cleanup
        this.pos = 0 ;
        this.length = 0 ;
        super.finish();
    }
}
