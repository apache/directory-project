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
package org.apache.ldap.common.berlib.asn1.decoder.delete ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.ldap.common.berlib.asn1.BufferUtils;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.DeleteRequest;
import org.apache.ldap.common.message.DeleteRequestImpl;
import org.apache.ldap.common.util.StringTools;

import java.nio.ByteBuffer;


/**
 * A rule that creates an DeleteRequest and populates it.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class DeleteRequestRule extends PrimitiveOctetStringRule
{
    /**
     * Creates an DeleteRequestRule that really just reads an integer.
     */
    public DeleteRequestRule()
    {
        super( LdapTag.DEL_REQUEST ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass ) ;

        DeleteRequest req = new DeleteRequestImpl( getDigester().popInt() ) ;
        getDigester().push( req ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;

        ByteBuffer buf = ( ByteBuffer ) getDigester().pop() ;
        String name = StringTools.utf8ToString( BufferUtils.getArray( buf ) );
        DeleteRequest req = ( DeleteRequest ) getDigester().pop() ;
        req.setName( name ) ;
    }
}
