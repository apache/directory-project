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
package org.apache.ldap.common.berlib.asn1.decoder;

import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.ldap.common.berlib.asn1.BufferUtils;
import org.apache.ldap.common.message.LdapResult;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.message.ResultResponse;

import java.nio.ByteBuffer;


/**
 * A rule used to create and populate an LdapResult object.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ResultRule extends AbstractRule
{
    private LdapResult result = null ;


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass ) ;
        ResultResponse resp = ( ResultResponse ) getDigester().peek() ;
        result = new LdapResultImpl( resp ) ;
        getDigester().push( result ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;

        ByteBuffer buf = ( ByteBuffer ) getDigester().pop() ;
        result.setErrorMessage( new String( BufferUtils.getArray( buf ) ) ) ;
        buf = ( ByteBuffer ) getDigester().pop() ;
        result.setMatchedDn( new String( BufferUtils.getArray( buf ) ) ) ;
        int resultCode = getDigester().popInt() ;
        result.setResultCode( ResultCodeEnum.getResultCodeEnum( resultCode ) ) ;
        getDigester().pop() ;
    }
}
