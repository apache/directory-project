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
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.SearchRequestImpl;


/**
 * A rule used to instantiate an empty SearchRequest, push it and ultimately
 * pop it off of the digester stack once it has been populated. An intermediate
 * processing state tracking object an instance of the SearchRequestProcessing
 * class is pushed onto the object stack with tag() right after the request
 * is pushed on; it is also popped off right before the request is popped.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class SearchRequestRule extends AbstractRule
{
    /** used to manage processing state */
    private final SearchRequestProcessing processing =
            new SearchRequestProcessing();

    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass );

        LdapTag tag = LdapTag.getLdapTagById( id );

        if ( LdapTag.SEARCH_REQUEST != tag )
        {
            throw new IllegalArgumentException( "Expected a SEARCH_REQUEST tag "
                + "id but got a " + tag );
        }

        SearchRequestImpl req;
        req = new SearchRequestImpl( getDigester().popInt() );
        getDigester().push( req );
        getDigester().push( processing );
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        super.finish();
        getDigester().pop();
        getDigester().pop();
        this.processing.reset();
    }
}
