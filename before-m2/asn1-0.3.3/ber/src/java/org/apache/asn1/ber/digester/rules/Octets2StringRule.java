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


/**
 * Rule that collects octets and leaves octets as a string on the stack.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class Octets2StringRule extends PrimitiveOctetStringRule
{
    public Octets2StringRule()
    {
        super();
    }


    public Octets2StringRule( TagEnum tag )
    {
        super( tag );
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

        getDigester().push( new String( octets ) ) ;
    }
}
