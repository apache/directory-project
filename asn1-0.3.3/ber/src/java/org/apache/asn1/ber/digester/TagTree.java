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
package org.apache.asn1.ber.digester ;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections.primitives.IntStack;
import org.apache.commons.lang.Validate;


/**
 * A disjointed tree of tag patterns with and without wild cards.
 *
 * @todo find and start using a hash table keyed by primitive int instead of
 * an Integer
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TagTree
{
    /** the wild card tag value as an integer = UNIVERSAL 2,097,151 (2^21-1) */
    public static final int WILDCARD = 0x1FFFFFFF ;

    /** a map of tag nodes for normal patterns */
    private HashMap normNodes = new HashMap( 3 ) ;
    /** a map of tag nodes for wild carded patterns */
    private HashMap wildNodes = new HashMap( 3 ) ;
    /** the list of normal rule regs with rule and pattern in order */
    private ArrayList normRegistrations = new ArrayList() ;
    /** the list of wild carded rule regs with rule and pattern in order */
    private ArrayList wildRegistrations = new ArrayList() ;


    // ------------------------------------------------------------------------
    // Methods used to add rules to trees
    // ------------------------------------------------------------------------


    /**
     * Adds a Rule to this TagTree in a manner based on whether the pattern
     * contains a wild card in front or not.
     *
     * @param pattern the pattern of nested tags
     * @param rule the rule to add for the pattern
     */
    public void addRule( int[] pattern, Rule rule )
    {
        if ( pattern[0] == WILDCARD )
        {
            wildRegistrations.add( new RuleRegistration( pattern, rule ) ) ;
            addWildRule( pattern, rule ) ;
        }
        else
        {
            normRegistrations.add( new RuleRegistration( pattern, rule ) ) ;
            addNormalRule( pattern, rule ) ;
        }
    }


    /**
     * Adds a Rule to this TagTree.
     * 
     * @param pattern the pattern of nested tags
     * @param rule the rule to add for the pattern
     */
    private void addNormalRule( int[] pattern, Rule rule )
    {
        Integer tag = null ;
        TagNode node = null ;
        Stack stack = new Stack() ;

        Validate.notNull( rule, "cannot add null rule" ) ;
        Validate.notNull( pattern, "cannot add rule with null pattern" ) ;
        Validate.isTrue( pattern.length > 0, 
                "cannot add rule with empty pattern" ) ;

        tag = new Integer( pattern[0] ) ;
        if ( normNodes.containsKey( tag ) )
        {
            node = ( TagNode ) normNodes.get( tag ) ;
            stack.push( node.getTag() ) ;
        }
        else
        {
            node = new TagNode( tag ) ;
            normNodes.put( tag, node ) ;
            stack.push( node.getTag() ) ;
            addWildRulesToNewNormalNode( node, stack ) ;
        }
        
        for ( int ii = 1; ii < pattern.length; ii++ )
        {    
            Integer childTag = new Integer( pattern[ii] ) ;
            TagNode childNode = node.getChild( childTag ) ;
            
            if ( childNode == null )
            {
                childNode = new TagNode( childTag ) ;
                node.addNode( childNode ) ;
                stack.push( childNode.getTag() ) ;
                addWildRulesToNewNormalNode( childNode, stack ) ;
            }
            else
            {
                stack.push( childNode.getTag() ) ;
            }

            node = childNode ;
            tag = childTag ;
        }
        
        node.addRule( rule ) ;
    }


    /**
     * Adds a Rule using a pattern with a wild card in front to this TagTree.
     *
     * @param pattern the pattern of nested tags with starting wild card
     * @param rule the rule to add for the pattern
     */
    private void addWildRule( int[] pattern, Rule rule )
    {
        Validate.notNull( pattern,
                "Attempting to register rule " + rule
                + " with null pattern" ) ;
        Validate.isTrue( pattern.length > 0,
                "Attempting to register rule " + rule
                + " with zero length pattern" ) ;
        Validate.isTrue( pattern[0] == WILDCARD,
                "Expected rule " + rule
                + " pattern to have front wild card but it did not" ) ;
        Validate.isTrue( pattern.length > 1,
                "Cannot register only wild card \"*\" pattern for rule "
                + rule ) ;

        /*
         * Adds the rule associated with the registration to the normal tag
         * tree which involves a recursive descent on the normal tree.
         */
        TagNode node = null ;
        Iterator list = normNodes.values().iterator() ;
        while ( list.hasNext() )
        {
            node = ( TagNode ) list.next() ;
            addWildRuleToNormalTree( pattern, rule, new Stack(), node ) ;
        }

        /*
         * first just lay down the new branch without adding the rule
         * next do a recursive drilldown testing for rule addition to all nodes
         */
        Integer tag = new Integer( pattern[pattern.length-1] ) ;
        if ( wildNodes.containsKey( tag ) )
        {
            node = ( TagNode ) wildNodes.get( tag ) ;
        }
        else
        {
            node = new TagNode( tag ) ;
            wildNodes.put( tag, node ) ;
        }

        for( int ii = pattern.length - 2; ii >= 1; ii-- )
        {
            Integer childTag = new Integer( pattern[ii] ) ;
            TagNode childNode = node.getChild( childTag ) ;

            if ( childNode == null )
            {
                childNode = new TagNode( childTag ) ;
                node.addNode( childNode ) ;
            }

            node = childNode ;
            tag = childTag ;
        }

        /*
         * Recusively drill down each branch of the wild tree checking at each
         * node to see if we need to add the new rule based on the pattern
         */
        list = wildNodes.values().iterator() ;
        while( list.hasNext() )
        {
            node = ( TagNode ) list.next() ;
            addWildRuleToWildTree( pattern, rule, new Stack(), node ) ;
        }
    }


    /**
     * Adds wild carded rules to new nodes being added to the tag tree with
     * normal patterns without wild cards.
     *
     * @param node the new node added to the tree of normal tags
     * @param stack a stack of nodes encountered while walking the tree
     */
    private void addWildRulesToNewNormalNode( TagNode node, Stack stack )
    {
        for ( int jj = 0; jj < wildRegistrations.size(); jj++ )
        {
            RuleRegistration reg = ( RuleRegistration )
                    wildRegistrations.get( jj ) ;

            if ( isTailMatch( reg.getPattern(), stack ) )
            {
                node.addRule( reg.getRule() ) ;
            }
        }
    }


    /**
     * Adds rules registered via wild cards to the wild TagTree to all
     * nodes matching the pattern.  This method performs depth first recursion
     * building a stack of TagNodes as dives into the TagTree.  At each point
     * the pattern with the wild card is tested against the contents of the
     * stack to see if it matches the nesting pattern, if it does then the
     * rule is added to the current node.
     *
     * @param pattern the matching pattern with front wild card
     * @param rule the rule registered with the pattern
     * @param stack the stack storing the depth first nesting pattern
     * @param node the current node scrutinized for a match by the pattern, and
     * the current position of the depth first search
     */
    private void addWildRuleToWildTree( int[] pattern, Rule rule,
                                        Stack stack, TagNode node )
    {
        stack.push( node.getTag() ) ;

        if ( isReverseTailMatch( pattern, stack ) )
        {
            if ( ! node.getRules().contains( rule ) )
            {
                node.addRule( rule ) ;
            }
        }

        if ( ! node.isLeaf() )
        {
            Iterator children = node.getChildren() ;
            while( children.hasNext() )
            {
                addWildRuleToWildTree( pattern, rule, stack,
                        ( TagNode ) children.next() ) ;
            }
        }

        stack.pop() ;
    }


    /**
     * Called by depth first search used to add rules of wild card patterns to
     * the wild TagTree.  This method compares a stack of Integers to a
     * pattern.  The stack must have as many or more than pattern.length - 1
     * elements to match.  Elements from the second element of the pattern to
     * the last element are compared with the bottom stack element up to the
     * top.  This is the reverse order because of the inverse paths in the
     * pattern tree with wild cards.
     *
     * @param pattern the pattern with a wild card at position 0
     * @param stack the nesting stack representing the depth first search path
     * @return true if the elements [n-1] in the pattern match the bottom most
     * elements in the stack where n+1 is length of the pattern array.
     */
    private boolean isReverseTailMatch( int[] pattern, Stack stack )
    {
        if ( stack.size() < pattern.length - 1 )
        {
            return false ;
        }

        for( int ii = pattern.length - 1, jj = 0 ; ii >= 1; ii--, jj++ )
        {
            if ( pattern[ii] != ( ( Integer ) stack.get( jj ) ).intValue() )
            {
                return false ;
            }
        }

        return true ;
    }


    /**
     * Adds rules registered via wild cards to the nodes within a branch of the
     * normal TagTree.  All nodes matching the pattern with wild cards has the
     * rule added to it.  This method performs depth first recursion building
     * a stack of TagNodes as it dives into the TagTree.  At each point the
     * pattern with the wild card is tested against the contents of the
     * stack to see if it matches the nesting pattern, if it does then the
     * rule is added to the current node.
     *
     * @param pattern the matching pattern with front wild card
     * @param rule the rule registered with the pattern
     * @param stack the stack storing the depth first nesting pattern
     * @param node the current node scrutinized for a match by the pattern, and
     * the current position of the depth first search
     */
    private void addWildRuleToNormalTree( int[] pattern, Rule rule,
                                          Stack stack, TagNode node )
    {
        stack.push( node.getTag() ) ;

        if ( isTailMatch( pattern, stack ) && node.isLeaf() )
        {
            if ( ! node.getRules().contains( rule ) )
            {
                node.addRule( rule ) ;
            }
        }

        if ( ! node.isLeaf() )
        {
            Iterator children = node.getChildren() ;
            while( children.hasNext() )
            {
                addWildRuleToNormalTree( pattern, rule, stack,
                        ( TagNode ) children.next() ) ;
            }
        }

        stack.pop() ;
    }


    /**
     * Called by depth first search used to add rules of wild card patterns to
     * the normal TagTree.  This method compares a stack of Integers to a
     * pattern.  The stack must have as many or more than pattern.length - 1
     * elements to match.  From the tail of the pattern to the second element
     * is compared with the topmost stack element down.
     *
     * @param pattern the pattern with a wild card at position 0
     * @param stack the nesting stack representing the depth first search path
     * @return true if the elements [n-1] in the pattern match the topmost
     * elements in the stack where n+1 is length of the pattern array.
     */
    private boolean isTailMatch( int[] pattern, Stack stack )
    {
        if ( stack.size() < pattern.length - 1 )
        {
            return false ;
        }

        for( int ii = pattern.length - 1, jj = stack.size() - 1; ii >= 1;
             ii--, jj-- )
        {
            if ( pattern[ii] != ( ( Integer ) stack.get( jj ) ).intValue() )
            {
                return false ;
            }
        }

        return true ;
    }


    // ------------------------------------------------------------------------
    // Methods used for matching a stack or a int[]
    // ------------------------------------------------------------------------


    public List match( IntStack stack )
    {
        TagNode node = getNode( stack ) ;
        
        if ( node == null )
        {
            return Collections.EMPTY_LIST ;
        }
        
        return node.getRules() ;
    }
    
    
    public TagNode getNode( IntStack stack )
    {
        TagNode node = getNormalNode( stack ) ;

        if ( node == null )
        {
            node = getWildNode( stack ) ;
        }

        return node ;
    }


    public List match( int[] pattern )
    {
        TagNode node = getNode( pattern ) ;

        if ( node == null )
        {
            return Collections.EMPTY_LIST ;
        }

        return node.getRules() ;
    }


    public TagNode getNode( int[] pattern )
    {
        TagNode node = getNormalNode( pattern ) ;

        if ( node == null )
        {
            node = getWildNode( pattern ) ;
        }

        return node ;
    }


    private TagNode getNormalNode( IntStack stack )
    {
        Integer tag = null ;
        TagNode node = null ;

        Validate.notNull( stack, "cannot match using null pattern" ) ;
        Validate.isTrue( !stack.empty(), "cannot match with empty pattern" ) ;

        tag = new Integer( stack.get( 0 ) ) ;
        if ( normNodes.containsKey( tag ) )
        {
            node = ( TagNode ) normNodes.get( tag ) ;
        }
        else
        {
            return null ;
        }
        
        for ( int ii = 1; ii < stack.size(); ii++ )
        {    
            Integer childTag = new Integer( stack.get( ii ) ) ;
            TagNode childNode = node.getChild( childTag ) ;
            
            if ( childNode == null )
            {
                return null ;
            }
            
            node = childNode ;
            tag = childTag ;
        }
        
        return node ;
    }


    private TagNode getNormalNode( int[] pattern )
    {
        Integer tag = null ;
        TagNode node = null ;

        Validate.notNull( pattern, "cannot match using null pattern" ) ;
        Validate.isTrue( pattern.length > 0, 
                "cannot match with empty pattern" ) ;

        tag = new Integer( pattern[0] ) ;
        if ( normNodes.containsKey( tag ) )
        {
            node = ( TagNode ) normNodes.get( tag ) ;
        }
        else
        {
            return null ;
        }
        
        for ( int ii = 1; ii < pattern.length; ii++ )
        {    
            Integer childTag = new Integer( pattern[ii] ) ;
            TagNode childNode = node.getChild( childTag ) ;
            
            if ( childNode == null )
            {
                return null ;
            }
            
            node = childNode ;
            tag = childTag ;
        }
        
        return node ;
    }


    /**
     * Gets a node matching a pattern with a wild card from this TagTree.
     *
     * @param pattern the wild card pattern as an int array
     * @return the matching wild card node if any
     */
    private TagNode getWildNode( int[] pattern )
    {
        Integer tag = null ;
        TagNode node = null ;

        /*
         * Restrict empty pattern, and zero length patterns.
         */
        Validate.notNull( pattern,
                "cannot match using null pattern" ) ;
        Validate.isTrue( pattern.length > 0,
                "cannot match with empty pattern" ) ;

        /*
         * Begin reverse walk by looking up the node corresponding
         * to the last pattern element.  Return null if it does not exit.
         */
        tag = new Integer( pattern[pattern.length - 1] ) ;
        if ( wildNodes.containsKey( tag ) )
        {
            node = ( TagNode ) wildNodes.get( tag ) ;
        }
        else
        {
            return null ;
        }

        /*
         * Walk using the second to last [pattern.length-2] element down to
         * the first element at index 0 in the pattern.
         */
        for ( int ii = pattern.length-2; ii >= 0; ii-- )
        {
            Integer childTag = new Integer( pattern[ii] ) ;
            TagNode childNode = node.getChild( childTag ) ;

            /*
             * If no more children are present and we're about to walk off of
             * the tree then we return the last node we have seen here.
             */
            if ( childNode == null )
            {
                return node ;
            }

            node = childNode ;
            tag = childTag ;
        }

        return node ;
    }


    /**
     * Gets a node matching a pattern with a wild card from this TagTree.
     *
     * @param stack the wild card pattern as a stack
     * @return the matching wild card node if any
     */
    private TagNode getWildNode( IntStack stack )
    {
        Integer tag = null ;
        TagNode node = null ;

        /*
         * Restrict empty pattern, and zero length patterns.
         */
        Validate.notNull( stack, "cannot match using null pattern" ) ;
        Validate.isTrue( !stack.empty(), "cannot match with empty pattern" ) ;

        /*
         * Begin reverse walk by looking up the node corresponding
         * to the bottom stack element.  Return null if it does not exit.
         */
        tag = new Integer( stack.get( stack.size() - 1 ) ) ;
        if ( wildNodes.containsKey( tag ) )
        {
            node = ( TagNode ) wildNodes.get( tag ) ;
        }
        else
        {
            return null ;
        }

        /*
         * Walk using the element above the bottom [stack.size()-2] up to
         * the top element at index 0 in the stack.
         */
        for ( int ii = stack.size() - 2; ii >= 0; ii-- )
        {
            Integer childTag = new Integer( stack.get( ii ) ) ;
            TagNode childNode = node.getChild( childTag ) ;

            /*
             * If no more children are present and we're about to walk off of
             * the tree then we return the last node we have seen here.
             */
            if ( childNode == null )
            {
                return node ;
            }

            node = childNode ;
            tag = childTag ;
        }

        return node ;
    }
}
