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
package org.apache.ldap.common.berlib.asn1.decoder ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.ldap.common.berlib.asn1.BufferUtils;
import org.apache.ldap.common.message.LdapResult;
import org.apache.ldap.common.message.ResultResponse;

import java.nio.ByteBuffer;


/**
 * Digester rule used to set the error message field of an LDAPResult within a
 * result response carring LDAP PDU list BindResponse or DelResponse.  This
 * rule only sets the error message if it has not already been set to prevent
 * overwriting it.  Furthermore this rule only sets the error message if the
 * result's matchedDn has been set: this helps manage the order of rule
 * operation since this rule and the matchedDn rule match the same pattern.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class ErrorMessageRule extends PrimitiveOctetStringRule
{
    /** the result carried by the result containing response */
    private LdapResult result = null ;
    /** whether or not to process this rule firing */
    private boolean byPass = false ;


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int,boolean,TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        ResultResponse resp = ( ResultResponse ) getDigester().peek() ;
        result = resp.getLdapResult() ;

        byPass = result.getMatchedDn() == null ||
                result.getErrorMessage() != null ;

        if ( ! byPass )
        {
            super.tag( id, isPrimitive, typeClass ) ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#length(int)
     */
    public void length( int length )
    {
        if ( ! byPass )
        {
            super.length( length ) ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#value(java.nio.ByteBuffer)
     */
    public void value( ByteBuffer buf )
    {
        if ( ! byPass )
        {
            super.value( buf ) ;
        }
    }


   /**
    * Overrides the finish method without calling the parent so we can
    * drain the accumulator here and set the value without needlessly
    * pushing and popping the stack.
    *
    * @see org.apache.asn1.ber.digester.Rule#finish()
    */
    public void finish()
    {
        if ( isConstructed() || byPass )
        {
            return ;
        }

        ByteBuffer buf = getAccumulator().drain( 0 ) ;
        String errorMsg = new String( BufferUtils.getArray( buf ) ) ;
        result.setErrorMessage( errorMsg ) ;

        // clean up
        setConstructed( false ) ;
        byPass = false ;
        result = null ;
   }
}
