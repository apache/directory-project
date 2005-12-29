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
import org.apache.ldap.common.filter.ExtensibleNode;

import java.nio.ByteBuffer;


/**
 * [*, 0x89000000]
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class ExtensibleMatchRule extends AbstractRule
{
    private boolean isEnabled = true;
    
    /** optional matching rule property */
    private String matchingRule = null;
    
    /** optional type property */
    private String type = null;
    
    /** required matchingValue property */
    private String value = null;
    
    /** dnAttributes property that defaults to FALSE */
    private boolean dnAttributes = false;


    /*
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        // check to see we are within limits - have the right number of tags
        int tagCount = getDigester().getTagCount();
        
        if ( tagCount < 3 )
        {
            this.isEnabled = false;
            return;
        }

        /*
         * check to see that we're dealing within a search request - this is
         * done by making sure the tag right above the bottom tag is equal
         * to the SEARCH_REQUEST tag. If not we must disable this rule.
         */
        if ( getDigester().getTag( tagCount - 2 ) !=
                LdapTag.SEARCH_REQUEST.getPrimitiveTag() )
        {
            this.isEnabled = false;
            return;
        }

        super.tag( id, isPrimitive, typeClass );

        getDigester().push( this );
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


    /**
     * Creates a SubstringNode using all the values that have been set by other
     * helper rules and pushes the node onto the object stack.
     *
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        if ( isEnabled )
        {
            super.finish();
            ExtensibleNode node;

            node = new ExtensibleNode( type, value, matchingRule,
                    dnAttributes );

            if ( getDigester().peek() == this )
            {
                getDigester().pop();
            }

            getDigester().push( node );
        }

        isEnabled = true;
        type = null;
        value = null;
        matchingRule = null;
        dnAttributes = false;
    }


    // ------------------------------------------------------------------------
    // Members called by other rules to populate the fields of this rule
    // ------------------------------------------------------------------------


    public void setMatchingRule( String matchingRule )
    {
        this.matchingRule = matchingRule;
    }


    public void setType( String type )
    {
        this.type = type;
    }


    public void setValue( String value )
    {
        this.value = value;
    }


    public void setDnAttributes( boolean dnAttributes )
    {
        this.dnAttributes = dnAttributes;
    }
}
