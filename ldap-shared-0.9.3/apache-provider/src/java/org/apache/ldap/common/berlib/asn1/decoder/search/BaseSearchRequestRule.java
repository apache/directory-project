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
import org.apache.commons.lang.Validate;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.SearchRequestImpl;

import java.nio.ByteBuffer;


/**
 * A base class for all SearchRequest processing rules.  This rule turns itself
 * off and on based on the nesting pattern of certain rule firings.  It has
 * the ability to bypass a firing this way if it was not triggered in the
 * proper scope.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class BaseSearchRequestRule extends AbstractRule
{
    /**
     * Determines whether or not this rule is enabled for a specific firing.
     * This will be reset every time to true after a rule's final() stage
     * method completes.
     */
    private boolean isEnabled = true;
    /**
     * The minimum number of tag's required in the tag stack of the digester
     * to enable this rule for a firing.  This is initialized in the protected
     * constructor.  So different rules can operate at different levels even
     * if they match the same pattern.
     */
    private final int minTagCount;
    /**
     * A handle on the search request currently being processed for this
     * firing.  This reference is set when tag() completes and nulled out when
     * the rule's final() stage method completes.
     */
    private SearchRequestImpl req;
    /**
     * A handle on the search request processing object used to track
     * processing state.  This reference is set when tag() completes and
     * nulled out when the rule's final() stage method completes.
     */
    private SearchRequestProcessing processing;


    /**
     * Creates a rule used to process some part of a search request.
     *
     * @param minTagCount the minimum number of tags required in the digester
     * tag stack to enable this rule
     */
    protected BaseSearchRequestRule( int minTagCount )
    {
        Validate.isTrue( minTagCount >= 2,
                "minimum tag count must be 2 or more" );
        this.minTagCount = minTagCount;
    }


    /**
     * Gets the SearchRequest object being processed.
     *
     * @return the SearchRequest object being processed
     */
    protected SearchRequestImpl getRequest()
    {
        return req;
    }


    /**
     * Gets the SearchRequestProcessing object tracking processing state.
     *
     * @return the SearchRequestProcessing object tracking processing state
     */
    protected SearchRequestProcessing getProcessing()
    {
        return processing;
    }


    /**
     * Checks to see if this rule is enabled for the rest of this firing.
     *
     * @return true if the rule is enabled for this firing false otherwise
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }


    /**
     * Sets whether or not this rule is disabled for the rest of this firing.
     *
     * @param enabled true if rule is enabled for this firing false otherwise
     */
    protected void setEnabled( boolean enabled )
    {
        isEnabled = enabled;
    }


    /**
     * It's a good idea to call this method first before anything else to
     * make sure the enabled flag gets set.  This guy sets the enabled flag
     * based on the tag nesting pattern.  Also check to make sure the rule is
     * enabled before writing code in any overloaded stage method like this
     * one.
     *
     * Also note that all tag overrides should after calling this method check
     * things like the processing state to determine if this rule needs to be
     * disabled.
     *
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        // check to see we are within limits - have the right number of tags
        int tagCount = getDigester().getTagCount();
        if ( tagCount < minTagCount )
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
         * Now that we're ready to go lets get a handle on the search request
         * and it's current processing state.  The processing state may be used
         * to determine the 'salience' of some competing rules.
         */
        int ii = getDigester().getCount() - 1 ;
        req = ( SearchRequestImpl ) getDigester().peek( ii ) ;
        processing = ( SearchRequestProcessing ) getDigester().peek( ii - 1 ) ;
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
        if ( isEnabled )
        {
            processing.next();
            super.finish();
        }

        req = null;
        isEnabled = true;
        processing = null;
    }
}
