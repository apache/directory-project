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
package org.apache.ldap.common.berlib.asn1.decoder.abandon ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.rules.PrimitiveIntDecodeRule;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.AbandonRequest;
import org.apache.ldap.common.message.AbandonRequestImpl;


/**
 * A rule that creates an AbandonRequest and populates it.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AbandonRequestRule extends PrimitiveIntDecodeRule
{
    /**
     * Creates an AbandonRequestRule that really just reads an integer.
     */
    public AbandonRequestRule()
    {
        super( LdapTag.ABANDON_REQUEST ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass ) ;

        AbandonRequest req = new AbandonRequestImpl( getDigester().popInt() ) ;
        getDigester().push( req ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;
        AbandonRequest req = ( AbandonRequest ) getDigester().pop() ;
        req.setAbandoned( getDigester().popInt() ) ;
    }
}
