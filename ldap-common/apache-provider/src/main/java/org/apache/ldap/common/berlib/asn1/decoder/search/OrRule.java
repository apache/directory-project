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
import org.apache.ldap.common.filter.BranchNode;
import org.apache.ldap.common.filter.ExprNode;

import java.nio.ByteBuffer;


/**
 * A rule used to build filter expressions that are OR'ed using the '|'
 * operator.  This rule pops the stack for its arguments which it
 * checks to see is a filter expression tree node of the type ExprNode.  This
 * rule will consume all expression nodes on the stack until it reaches an
 * object stack element that is not of the ExprNode class.  It uses these
 * expression nodes to construct a anded expression with it, and pushes
 * the newly created OR'ed node onto the stack.  This rule also makes sure
 * that it is enabled only under a search request.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class OrRule extends AbstractRule
{
    private boolean isEnabled = true;


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int, boolean,
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
        if ( getDigester().getTag( tagCount - 2 ) != LdapTag.SEARCH_REQUEST.getPrimitiveTag() )
        {
            this.isEnabled = false;

            return;
        }

        /*
         * This rule is registered using [*, 0x81000000] which matches all
         * context sensitive 0 tags in the search request.  This makes it
         * cross react with substring match expression components.  We need to
         * check if the next tags in stack are equal to substring match context
         * specific tags to see if we need to disable this rule.
         */
        if
        ( getDigester().getTag( 2 ) == LdapTag.CONTEXT_SPECIFIC_TAG_4.getPrimitiveTag() ||
          getDigester().getTag( 1 ) == LdapTag.CONTEXT_SPECIFIC_TAG_9.getPrimitiveTag() )
        {
            this.isEnabled = false ;

            return ;
        }

        /*
         * At this point we know the OrRule is enabled but we need to push a separator
         * onto the stack so when the OrRule finishes it knows when to stop popping
         * subexpressions off of the stack.  We just push the rule itself onto the stack.
         * and test for it in the finish() method's while loop to terminate.
         */
        getDigester().push( this );

        super.tag( id, isPrimitive, typeClass );
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
            super.finish();

            BranchNode node;

            node = new BranchNode( BranchNode.OR );

            while( getDigester().peek() instanceof ExprNode )
            {
                // TODO correct obvious performance hit here if this code stays around.
                node.addNodeToHead( ( ExprNode ) getDigester().pop() );
            }

            if ( getDigester().peek() == this )
            {
                getDigester().pop();
            }
            else
            {
                String msg = "Expected to see an OrRule object on stack but got ";

                msg += getDigester().peek().getClass() + " instead";

                throw new IllegalStateException( msg );
            }

            getDigester().push( node );
        }

        this.isEnabled = true;
    }
}
