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


import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.ldap.common.berlib.asn1.LdapTag;

import java.nio.ByteBuffer;


/**
 * A helper rule used to build a Extensible match fiter expression.  This rule
 * accessess the top object on the object stack which it presumes is the rule
 * it is helping: an instance of the ExtensibleMatchRule class.  It sets the
 * value using the setter on this rule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ExtensibleMatchValueRule extends PrimitiveOctetStringRule
{
    public ExtensibleMatchValueRule()
    {
        super( LdapTag.CONTEXT_SPECIFIC_TAG_3 );
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;

        // pop the ByteBuffer the super method pushed
        ByteBuffer buf = ( ByteBuffer ) getDigester().pop() ;

        byte[] octets = null ;
        if ( buf.limit() == buf.capacity() && buf.hasArray() )
        {
            // use the backing store
            octets = buf.array() ;
        }
        else
        {
            // copy because we don't have accessible array or data < array
            octets = new byte[buf.remaining()] ;
            buf.get( octets ) ;
        }

        ExtensibleMatchRule rule = ( ExtensibleMatchRule ) getDigester().peek();
        rule.setValue( new String( octets ) );
    }
}
