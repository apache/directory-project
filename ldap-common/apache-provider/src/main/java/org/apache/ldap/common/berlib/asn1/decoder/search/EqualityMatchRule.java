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
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.filter.SimpleNode;
import org.apache.ldap.common.util.StringTools;

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
public class EqualityMatchRule extends PrimitiveOctetStringRule
{
    private boolean isEnabled = true;

    private String name = null;

    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        // check to see we are within limits - have the right number of tags
        int tagCount = getDigester().getTagCount();

        if ( tagCount < 4 )
        {
            this.isEnabled = false;

            return;
        }

        /*
         * check to see that we're dealing within a search request - this is
         * done by making sure the tag right above the bottom tag is equal
         * to the SEARCH_REQUEST tag. If not we must disable this rule.
         */
        if ( getDigester().getTag( tagCount - 2 ) != LdapTag.SEARCH_REQUEST.getPrimitiveTag() )
        {
            this.isEnabled = false;

            return;
        }

        super.tag( id, isPrimitive, typeClass );

        int ii = getDigester().getCount() - 2;

        SearchRequestProcessing processing;

        processing = ( SearchRequestProcessing ) getDigester().peek(ii);

        if ( processing.getState() != processing.FILTER_STATE )
        {
            isEnabled = false;
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

        if ( name == null )
        {
            name = StringTools.utf8ToString( octets ) ;
        }
        else 
        {
            SimpleNode node = new SimpleNode( name, octets, SimpleNode.EQUALITY );
            getDigester().push( node ) ;

            name = null ;
        }
    }
}
