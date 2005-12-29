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

/*
 * $Id: FilterTransform.java,v 1.2 2003/05/01 20:51:02 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.berlib.snacc ;


import java.util.ArrayList ;
import java.util.Enumeration ;

import org.apache.ldap.common.filter.ExprNode ;
import org.apache.ldap.common.filter.LeafNode ;
import org.apache.ldap.common.filter.BranchNode ;
import org.apache.ldap.common.filter.SimpleNode ;
import org.apache.ldap.common.filter.PresenceNode ;
import org.apache.ldap.common.filter.SubstringNode ;
import org.apache.ldap.common.filter.ExtensibleNode ;

import org.apache.ldap.common.message.spi.ProviderException ;
import org.apache.ldap.common.util.StringTools;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Filter ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.FilterSetOf ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.FilterSetOf1 ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SubstringFilter ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SubstringFilterSeqOf ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.MatchingRuleAssertion ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeValueAssertion ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SubstringFilterSeqOfChoice ;


/**
 * Utility methods used to transfrom a Snacc Filter stub into a LDAPd filter
 * expression tree and vice versa.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class FilterTransform
{
    /**
     * Transforms a snacc Filter stub into a filter expression tree.  This code
     * is primarily a replica of the code previously placed in the search
     * protocol handler/processor's ExprTreeComposer classes's compose method.
     * That class will eventually be removed since its functionality will reside
     * here and not be needed there in the protocol engine.
     *
     * @param a_filter the root snacc Filter stub.
     * @return the root expression node representing the filter tree for the
     * snacc Filter stub argument.
     */
    public static ExprNode transformFromSnacc( Filter a_filter )
    {
        String l_attr ;
        String l_value ;
        Enumeration l_filters ;
        SimpleNode l_ava ;
        BranchNode l_bn ;

        switch( a_filter.choiceId )
        {

        case( Filter.AND_CID ):
            FilterSetOf l_and = a_filter.and ;
            l_bn = new BranchNode( BranchNode.AND ) ;
            l_filters = l_and.elements() ;
            while( l_filters.hasMoreElements() )
            {
                l_bn.getChildren().add( transformFromSnacc( ( Filter )
                    l_filters.nextElement() ) ) ;
            }

            return l_bn ;

        case( Filter.NOT_CID ):
            l_bn = new BranchNode( BranchNode.NOT ) ;
            l_bn.getChildren().add( transformFromSnacc( a_filter.not ) ) ;

            return l_bn ;

        case( Filter.OR_CID ):
            FilterSetOf1 l_or = a_filter.or ;
            l_bn = new BranchNode( BranchNode.OR ) ;
            l_filters = l_or.elements() ;
            while( l_filters.hasMoreElements() )
            {
                l_bn.getChildren().add(  transformFromSnacc( ( Filter )
                    l_filters.nextElement() ) ) ;
            }

            return l_bn ;

        case( Filter.APPROXMATCH_CID ):
            AttributeValueAssertion l_approxAva = a_filter.approxMatch ;
            l_attr = new String( l_approxAva.attributeDesc ) ;
            l_value = new String( l_approxAva.assertionValue ) ;
            l_ava = new SimpleNode( l_attr, l_value, SimpleNode.APPROXIMATE ) ;

            return l_ava ;

        case( Filter.EQUALITYMATCH_CID ):
            AttributeValueAssertion l_equalityAva = a_filter.equalityMatch ;
            l_attr = new String(l_equalityAva.attributeDesc) ;
            l_value = new String(l_equalityAva.assertionValue) ;
            l_ava = new SimpleNode (l_attr, l_value, SimpleNode.EQUALITY) ;

            return l_ava ;

        case( Filter.EXTENSIBLEMATCH_CID ):
            MatchingRuleAssertion l_snaccMra = a_filter.extensibleMatch ;
            l_attr = new String( l_snaccMra.type ) ;
            l_value = new String( l_snaccMra.matchValue ) ;
            String l_matchingRule = new String( l_snaccMra.matchingRule ) ;
            boolean dnAttributes = l_snaccMra.dnAttributes ;
            ExtensibleNode l_en = new ExtensibleNode( l_attr, l_value,
                l_matchingRule, dnAttributes ) ;

            return l_en ;

        case( Filter.GREATEROREQUAL_CID ):
            AttributeValueAssertion l_greaterOrEqualAva = a_filter.greaterOrEqual ;
            l_attr = new String( l_greaterOrEqualAva.attributeDesc ) ;
            l_value = new String( l_greaterOrEqualAva.assertionValue ) ;
            l_ava = new SimpleNode( l_attr, l_value, SimpleNode.GREATEREQ ) ;

            return l_ava ;

        case( Filter.LESSOREQUAL_CID ):
            AttributeValueAssertion l_lessOrEqualAva = a_filter.lessOrEqual ;
            l_attr = new String( l_lessOrEqualAva.attributeDesc ) ;
            l_value = new String( l_lessOrEqualAva.assertionValue ) ;
            l_ava = new SimpleNode( l_attr, l_value, SimpleNode.LESSEQ ) ;

            return l_ava ;

        case( Filter.PRESENT_CID ):
            PresenceNode l_pn =
                new PresenceNode( new String( a_filter.present ) ) ;

            return l_pn ;

        case( Filter.SUBSTRINGS_CID ):
            SubstringFilter l_subStrFilter = a_filter.substrings ;
            String l_initial = null ;
            String l_final = null ;
            l_attr = new String( l_subStrFilter.type ) ;

            SubstringFilterSeqOf l_subfilters = l_subStrFilter.substrings ;
            Enumeration l_substrings = l_subfilters.elements() ;
            ArrayList l_any = new ArrayList( 2 ) ;
            SubstringFilterSeqOfChoice l_element = null ;
            while( l_substrings.hasMoreElements() )
            {
                l_element = ( SubstringFilterSeqOfChoice )
                    l_substrings.nextElement() ;
                if( l_element.ANY_CID == l_element.choiceId )
                {
                    l_any.add( new String( l_element.any ) ) ;
                }
                else if( l_element.INITIAL_CID == l_element.choiceId )
                {
                    l_initial = new String( l_element.initial ) ;
                }
                else if( l_element.FINAL1_CID == l_element.choiceId )
                {
                    l_final = new String( l_element.final1 ) ;
                }
                else
                {
                    throw new IllegalArgumentException(
                        "Undefined substring filter sequence choice:\n"
                        + l_element + " in filter " + a_filter ) ;
                }
            }

            SubstringNode l_sn =
                new SubstringNode( l_any, l_attr, l_initial, l_final ) ;

            return l_sn ;

        default:
            throw new IllegalArgumentException(
                "Undefined choice id for filter expression:\n" + a_filter ) ;
        }
    }


    /**
     * Primary entry method which creates a populated Snacc4J Filter stub using
     * the information contained in a root filter AST ExprNode.
     *
     * @param a_rootNode the ExprNode that is the root of the expression tree.
     * @return the Snacc Filter stub representing the filter expression tree.
     */
    public static Filter transformToSnacc( ExprNode a_rootNode )
        throws ProviderException
    {
        Filter l_snaccFilter = new Filter() ;
        transformToSnacc( a_rootNode, l_snaccFilter ) ;
        return l_snaccFilter ;
    }


    /**
     * Helper method used to compose a SubstringFilter stub representing a
     * substring assertion within a filter expression.
     *
     * @param a_node a substring expression tree node.
     * @return a Snacc SubstringFilter stub representing the substring node
     */
    private static SubstringFilter getSubstringFilter( SubstringNode a_node )
    {
        SubstringFilter l_subStrFilter = new SubstringFilter() ;
        l_subStrFilter.type = StringTools.getBytesUtf8( a_node.getAttribute() ) ;
        SubstringFilterSeqOf l_seq = new SubstringFilterSeqOf() ;
        l_subStrFilter.substrings = l_seq ;

        SubstringFilterSeqOfChoice l_initial =
            new SubstringFilterSeqOfChoice() ;
        l_initial.choiceId = SubstringFilterSeqOfChoice.INITIAL_CID ;
        l_initial.initial = StringTools.getBytesUtf8( a_node.getInitial() ) ;
        l_seq.add( l_initial ) ;

        for( int ii = 0; ii < a_node.getAny().size(); ii ++ )
        {
            SubstringFilterSeqOfChoice l_any =
                new SubstringFilterSeqOfChoice() ;
            l_any.choiceId = SubstringFilterSeqOfChoice.ANY_CID ;
            l_any.any = StringTools.getBytesUtf8( ( ( String ) a_node.getAny().get( ii ) ) ) ;
            l_seq.add( l_any ) ;
        }

        SubstringFilterSeqOfChoice l_final =
            new SubstringFilterSeqOfChoice() ;
        l_final.choiceId = SubstringFilterSeqOfChoice.FINAL1_CID ;
        l_final.final1 = StringTools.getBytesUtf8( a_node.getFinal() ) ;
        l_seq.add( l_final ) ;

        return l_subStrFilter ;
    }


    /**
     * Generates a MatchRuleAssertion Snacc stub for an ExtensibleNode within
     * the filter expression tree.
     *
     * @param a_node the ExtensibleNode to transform to an equivalent snacc stub
     * @return the MatchingRuleAssertion snacc stub for the ExtensibleNode.
     */
    private static MatchingRuleAssertion getMra( ExtensibleNode a_node )
    {
        MatchingRuleAssertion l_mra = new MatchingRuleAssertion() ;
        l_mra.type = StringTools.getBytesUtf8( a_node.getAttribute() ) ;
        l_mra.matchValue = a_node.getValue() ;
        l_mra.matchingRule = StringTools.getBytesUtf8( a_node.getMatchingRuleId() ) ;
        l_mra.dnAttributes = a_node.dnAttributes() ;

        return l_mra ;
    }


    /**
     * Generates an AttributeValueAssertion Snacc stub that represents a
     * SimpleNode within a filter expression tree.
     *
     * @param a_node the expression tree's SimpleNode to be converted.
     * @return the generated AVA snacc stub representing the SimpleNode.
     */
    private static AttributeValueAssertion getAva( SimpleNode a_node )
        throws ProviderException
    {
        AttributeValueAssertion l_ava = new AttributeValueAssertion() ;
        l_ava.attributeDesc = StringTools.getBytesUtf8( a_node.getAttribute() ) ;
        l_ava.assertionValue = StringTools.getBytesUtf8( a_node.getValue() );

        return l_ava ;
    }


    /**
     * The start of the recursive chain which populates a snacc Filter stub to
     * mirror the expression tree within a node.  Simply checks if the node is
     * a leaf in which case there is no recursion since the node info is created
     * for the filter and the choiceId is set.  If the node is a branch node
     * then recursion does occur calling this method and others in a recursive
     * chain until all leaf nodes are populated into the top level Filter stub.
     * 
     * @param a_node a filter expression tree node
     * @param a_filter the snacc Filter stub to populate with the filter node
     */
    private static void transformToSnacc( ExprNode a_node, Filter a_filter )
        throws ProviderException
    {
        if( a_node.isLeaf() )
        {
            LeafNode l_leaf = ( LeafNode ) a_node ;

            switch( l_leaf.getAssertionType() )
            {
            case( LeafNode.APPROXIMATE ):
                a_filter.choiceId = Filter.APPROXMATCH_CID ;
                a_filter.approxMatch = getAva( ( SimpleNode ) a_node ) ;
                break ;
            case( LeafNode.EQUALITY ):
                a_filter.choiceId = Filter.EQUALITYMATCH_CID ;
                a_filter.equalityMatch = getAva( ( SimpleNode ) a_node ) ;
                break ;
            case( LeafNode.EXTENSIBLE ):
                a_filter.choiceId = Filter.EXTENSIBLEMATCH_CID ;
                a_filter.extensibleMatch = getMra( ( ExtensibleNode ) a_node ) ;
                break ;
            case( LeafNode.GREATEREQ ):
                a_filter.choiceId = Filter.GREATEROREQUAL_CID ;
                a_filter.greaterOrEqual = getAva( ( SimpleNode ) a_node ) ;
                break ;
            case( LeafNode.LESSEQ ):
                a_filter.choiceId = Filter.LESSOREQUAL_CID ;
                a_filter.lessOrEqual = getAva( ( SimpleNode ) a_node ) ;
                break ;
            case( LeafNode.PRESENCE ):
                a_filter.choiceId = Filter.PRESENT_CID ;
                a_filter.present =
                    StringTools.getBytesUtf8( ( ( PresenceNode ) a_node ).getAttribute() ) ;
                break ;
            case( LeafNode.SUBSTRING ):
                a_filter.choiceId = Filter.SUBSTRINGS_CID ;
                a_filter.substrings =
                    getSubstringFilter( ( SubstringNode ) a_node ) ;
                break ;
            case( LeafNode.SCOPE ):
                throw new ProviderException( SnaccProvider.getProvider(),
                    "Scope should not be factored into filter expressions!" ) ;
            default:
                throw new ProviderException( SnaccProvider.getProvider(),
                    "Unknown leaf node assertion type value: "
                    + l_leaf.getAssertionType() ) ;
            }
        }
        else
        {
            BranchNode l_branchNode = ( BranchNode ) a_node ;

            switch( l_branchNode.getOperator() )
            {
            case( BranchNode.OR ):
                a_filter.choiceId = Filter.OR_CID ;

                // Participates in chain recursion
                a_filter.or = getOrExpression( l_branchNode ) ;
                break ;
            case( BranchNode.AND ):
                a_filter.choiceId = Filter.AND_CID ;

                // Participates in chain recursion
                a_filter.and = getAndExpression( l_branchNode ) ;
                break ;
            case( BranchNode.NOT ):
                a_filter.choiceId = Filter.NOT_CID ;
                a_filter.not = new Filter() ;

                // Call this method recursively using the only child of a NOT
                // branch node and the newly created filter for the not member
                transformToSnacc(
                    ( ExprNode ) l_branchNode.getChildren().get( 0 ),
                    a_filter.not ) ;
                break ;
            default:
                throw new ProviderException( SnaccProvider.getProvider(),
                    "Unknown brach node operator value: "
                    + l_branchNode.getOperator() ) ;
            }
        }
    }


    /**
     * The FilterSetOf1 stub generated by the Snacc4J compiler represents a
     * set of filters that are OR'ed together.  Each element in FilterSetOf1
     * is a filter in itself.  The key and the value of a filter entry in the
     * FilterSetOf1 hash is the filter itself.  We cycle throught the children
     * of the OR branch expression node creating a filter for each child
     * ExprNode.  The filter and the node are then fed in a chain recursion
     * into the transformToSnacc method above to construct the child filter.
     *
     * @param a_node an OR'ed expression branch node.
     * @return the set of filters for an OR'ed expression.
     */
    private static FilterSetOf1 getOrExpression( BranchNode a_node )
    {
        if( a_node.getOperator() != BranchNode.OR )
        {
            throw new ProviderException( SnaccProvider.getProvider(),
                "Attempting to create an OR expression from another "
                + "type of BranchNode" ) ;
        }

        FilterSetOf1 l_filters = new FilterSetOf1() ;
        ArrayList l_children = a_node.getChildren() ;

        for( int ii = 0; ii < l_children.size(); ii++ )
        {
            Filter l_filter = new Filter() ;
            ExprNode l_node = ( ExprNode ) l_children.get( ii ) ;
            transformToSnacc( l_node, l_filter ) ;
            l_filters.put( l_filter, l_filter ) ;
        }

        return l_filters ;
    }


    /**
     * The FilterSetOf stub generated by the Snacc4J compiler represents a
     * set of filters that are AND'ed together.  Each element in FilterSetOf
     * is a filter in itself.  The key and the value of a filter entry in the
     * FilterSetOf hash is the filter itself.  We cycle throught the children
     * of the AND branch expression node creating a filter for each child
     * ExprNode.  The filter and the node are then fed in a chain recursion
     * into the transformToSnacc method above to construct the child filter.
     *
     * @param a_node an AND'ed expression branch node.
     * @return the set of filters for an AND'ed expression.
     */
    private static FilterSetOf getAndExpression( BranchNode a_node )
    {
        if( a_node.getOperator() != BranchNode.AND )
        {
            throw new ProviderException( SnaccProvider.getProvider(),
                "Attempting to create an AND expression from another "
                + "type of BranchNode" ) ;
        }

        FilterSetOf l_filters = new FilterSetOf() ;
        ArrayList l_children = a_node.getChildren() ;

        for( int ii = 0; ii < l_children.size(); ii++ )
        {
            Filter l_filter = new Filter() ;
            ExprNode l_node = ( ExprNode ) l_children.get( ii ) ;
            transformToSnacc( l_node, l_filter ) ;
            l_filters.put( l_filter, l_filter ) ;
        }

        return l_filters ;
    }
}
