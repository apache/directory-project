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
package org.apache.ldap.common.berlib.asn1.decoder.bind ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.BindRequestImpl;


/**
 * Creates an instance of an LDAP BindRequest bean using a message Id stored
 * on the primitive integer stack.  As a side effect the integer is popped
 * off of the stack and the BindRequest is pushed onto the Object stack.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BindRequestRule extends AbstractRule
{
    /** the tag nesting pattern without the constructed bits */
    private static final int[] NESTING_PATTERN = {
        UniversalTag.SEQUENCE_SEQUENCE_OF.getValue(),
        LdapTag.BIND_REQUEST.getValue()
    } ;


    /**
     * Gets the PDU Tag for a BindRequest message without the constructed bit.
     *
     * @return PDU Tag for a BindRequest message without the constructed bit
     */
    public static LdapTag getTag()
    {
        return LdapTag.BIND_REQUEST ;
    }


    /**
     * Gets the nesting pattern for a BindRequest.
     *
     * @return the nesting pattern of tags for a bind request
     */
    public static int[] getNestingPattern()
    {
        return NESTING_PATTERN ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        LdapTag tag = LdapTag.getLdapTagById( id ) ;

        if ( LdapTag.BIND_REQUEST != tag )
        {
            throw new IllegalArgumentException( "Expected a BIND_REQUEST tag "
                + "id but got a " + tag ) ;
        }

        BindRequestImpl req = new BindRequestImpl( getDigester().popInt() ) ;
        getDigester().push( req ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        getDigester().pop() ;
    }
}
