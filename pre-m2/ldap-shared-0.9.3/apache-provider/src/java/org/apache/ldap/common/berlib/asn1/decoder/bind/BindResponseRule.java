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


import org.apache.ldap.common.berlib.asn1.BufferUtils;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.decoder.ResultResponseRule;
import org.apache.ldap.common.message.BindResponse;

import java.nio.ByteBuffer;


/**
 * A digester rule which fires to build BindRespond containment trees.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BindResponseRule extends ResultResponseRule
{
    /**
     * Creates a digester rule which fires to build BindRespond containment
     * trees.
     */
    public BindResponseRule()
    {
        super( LdapTag.BIND_RESPONSE ) ;
    }


    /**
     * This final rule firing stage peeks at the object buffer to see if an
     * optional SASL crendential is available.  If it is, the SASL
     * crendentials are popped off of the object stack and used to populate
     * the BindResponse, otherwise they are left as is: null.
     *
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;

        if ( ( getDigester().getCount() != 0 ) && ( getDigester().peek() instanceof ByteBuffer ) )
        {
            ByteBuffer buf = ( ByteBuffer ) getDigester().pop() ;
            BindResponse resp = ( BindResponse ) getDigester().pop() ;
            resp.setServerSaslCreds( BufferUtils.getArray( buf ) ) ;
        }
    }
}
