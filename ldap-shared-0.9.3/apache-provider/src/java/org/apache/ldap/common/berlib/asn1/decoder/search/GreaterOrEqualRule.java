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
import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.ldap.common.filter.SimpleNode;

import java.nio.ByteBuffer;


/**
 * Rule used to gather and push an equality match node onto the stack.  This
 * rule is registered using a wild card pattern: [*, 0x83000000, 0x04000000 ].
 * It does check that all patterns causing a firing have the starting pattern
 * of a SearchRequest.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class GreaterOrEqualRule extends PrimitiveOctetStringRule
{
    private boolean isEnabled = true ;
    private String name = null ;
    private String value = null ;


    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass );

        int ii = getDigester().getCount() - 2;
        SearchRequestProcessing processing;
        processing = ( SearchRequestProcessing ) getDigester().peek(ii);

        if ( processing.getState() != processing.FILTER_STATE )
        {
            isEnabled = false ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#length(int)
     */
    public void length( int length )
    {
        if ( isEnabled )
        {
            super.length( length );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#value(java.nio.ByteBuffer)
     */
    public void value( ByteBuffer buf )
    {
        if ( isEnabled )
        {
            super.value( buf );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        if ( ! isEnabled )
        {
            isEnabled = true ;
            return ;
        }

        // pushes a ByteBuffer onto the stack
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
            octets = new byte[buf.remaining()];
            buf.get( octets );
        }

        if ( name == null && value == null )
        {
            name = new String( octets ) ;
        }
        else if ( name != null && value == null )
        {
            value = new String( octets ) ;


            SimpleNode node;
            node = new SimpleNode( name, value, SimpleNode.GREATEREQ );
            getDigester().push( node ) ;

            name = null ;
            value = null ;
        }
        else
        {
            throw new IllegalStateException( "name = " + name
                    + " and value = " + value );
        }
    }
}
