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
import org.apache.ldap.common.filter.SubstringNode;

import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * A rule used to collect and instantiate a substring filter expression.  This
 * rule reacts with others because it itself is pushed onto the stack.
 * Interfaces on the rule allow for the collection of substring filter
 * parameters.
 * <br/>
 * The rule is registered with wild card pattern [*, 0x84000000, 0x10000000] on
 * a search request rather than using [*, 0x84000000].  This is done to have
 * more specific registration.  It excepts a String the attribute name or "type"
 * according to the ASN.1 definition to be available on the stack deposited by
 * a rule using the [*, 0x84000000, 0x04000000] pattern.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class SubstringMatchRule extends AbstractRule
{
    private boolean isEnabled = true;
    
    /** the initial substring gotten from the stack */
    private String type = null;
    
    private String initialStr = null;
    
    private String finalStr = null;
    
    private ArrayList any = new ArrayList();


    /*
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
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
        if ( getDigester().getTag( tagCount - 2 ) !=
                LdapTag.SEARCH_REQUEST.getPrimitiveTag() )
        {
            this.isEnabled = false;
            return;
        }

        super.tag( id, isPrimitive, typeClass );

        /*
         * If enabled we need to pop the string deposited there from a helper
         * rule next we push this rule onto the stack to be populated by other
         * rules.
         */
        if ( isEnabled )
        {
            type = ( String ) getDigester().pop();
            getDigester().push( this );
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
            SubstringNode node;
            node = new SubstringNode( any, type, initialStr, finalStr );

            if ( getDigester().peek() == this )
            {
                getDigester().pop();
            }

            getDigester().push( node );
        }

        type = null;
        finalStr = null;
        isEnabled = true;
        initialStr = null;
        any = new ArrayList();
    }


    // ------------------------------------------------------------------------
    // Members called by other rules to populate the fields of this rule
    // ------------------------------------------------------------------------


    /**
     * Sets the initial portion of the substring match expression.
     *
     * @param initialStr the initial String
     */
    public void setInitial( String initialStr )
    {
        this.initialStr = initialStr;
    }


    /**
     * Sets the final portion of the substring match expression.
     *
     * @param finalStr the final String
     */
    public void setFinalStr( String finalStr )
    {
        this.finalStr = finalStr;
    }


    /**
     * Adds middle any region String to the substring match expression.
     *
     * @param anyStr a middle any region String
     */
    public void addAny( String anyStr )
    {
        this.any.add( anyStr ) ;
    }
}
