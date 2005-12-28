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


import org.apache.commons.collections.primitives.IntStack;
import org.apache.commons.test.PrivateTestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;


/**
 * Tests the TagTree class.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TagTreeTest extends PrivateTestCase
{
    /**
     * Constructor for TagTreeTest.
     * @param name the name of the test case
     */
    public TagTreeTest( String name )
    {
        super( name ) ;
    }


    /**
     * Accesses the private wildNodes member to extract the TagNode 
     * with the int tag argument as a value.
     * 
     * @param tree the tree to access
     * @param tag the tag value to get the TagNode for
     * @return the tag node accessed or null
     */ 
    private TagNode getWildNode( TagTree tree, int tag )
    {
        HashMap nodes = ( HashMap ) getMember( "wildNodes", tree ) ;
        return ( TagNode ) nodes.get( new Integer( tag ) ) ;
    }


    /**
     * Accesses the private normNodes member to extract the TagNode 
     * with the int tag argument as a value.
     * 
     * @param tree the tree to access
     * @param tag the tag value to get the TagNode for
     * @return the tag node accessed or null
     */ 
    private TagNode getNormalNode( TagTree tree, int tag )
    {
        HashMap nodes = ( HashMap ) getMember( "normNodes", tree ) ;
        return ( TagNode ) nodes.get( new Integer( tag ) ) ;
    }


    /**
     * Used to white box test the private isTailMatch() method of the TagTree.
     *
     * @param tree the tree instance to test
     * @param pattern the pattern arg
     * @param stack the stack arg
     * @return the resultant true or false return value
     */
    private boolean isTailMatch( TagTree tree, int[] pattern, Stack stack )
    {
        Class[] argClasses = { pattern.getClass(), Stack.class } ;
        Object[] args = { pattern, stack } ;
        Object result = invoke( tree, TagTree.class, "isTailMatch", argClasses,
                args ) ;
        return ( ( Boolean ) result ).booleanValue() ;
    }


    /**
     * Used to white box test the private isReverseTailMatch() method of the
     * TagTree.
     *
     * @param tree the tree instance to test
     * @param pattern the pattern arg
     * @param stack the stack arg
     * @return the resultant true or false return value
     */
    private boolean isReverseTailMatch( TagTree tree, int[] pattern,
                                        Stack stack )
    {
        Class[] argClasses = { pattern.getClass(), Stack.class } ;
        Object[] args = { pattern, stack } ;
        Object result = invoke( tree, TagTree.class, "isReverseTailMatch",
                argClasses, args ) ;
        return ( ( Boolean ) result ).booleanValue() ;
    }


    /**
     * Tests the private isTailMatch() method.
     */
    public void testIsTailMatch()
    {
        TagTree tree = new TagTree() ;
        int[] pattern = {TagTree.WILDCARD,1,2,3} ;
        Stack stack = new Stack() ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 5 ) ) ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 6 ) ) ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 2 ) ) ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 3 ) ) ;
        assertTrue( isTailMatch( tree, pattern, stack ) ) ;

        tree = new TagTree() ;
        pattern = new int[]{TagTree.WILDCARD} ;
        stack = new Stack() ;
        assertTrue( isTailMatch( tree, pattern, stack ) ) ;

        tree = new TagTree() ;
        pattern = new int[]{} ;
        stack = new Stack() ;
        assertTrue( isTailMatch( tree, pattern, stack ) ) ;

        tree = new TagTree() ;
        pattern = new int[]{TagTree.WILDCARD, 1} ;
        stack = new Stack() ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 2 ) ) ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertTrue( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertTrue( isTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 2 ) ) ;
        assertFalse( isTailMatch( tree, pattern, stack ) ) ;
    }


    /**
     * Tests the private isTailMatch() method.
     */
    public void testIsReverseTailMatch()
    {
        TagTree tree = new TagTree() ;
        int[] pattern = {TagTree.WILDCARD,1,2,3} ;
        Stack stack = new Stack() ;
        stack.push( new Integer( 3 ) ) ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 2 ) ) ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertTrue( isReverseTailMatch( tree, pattern, stack ) ) ;

        tree = new TagTree() ;
        pattern = new int[]{TagTree.WILDCARD} ;
        stack = new Stack() ;
        assertTrue( isReverseTailMatch( tree, pattern, stack ) ) ;

        tree = new TagTree() ;
        pattern = new int[]{} ;
        stack = new Stack() ;
        assertTrue( isReverseTailMatch( tree, pattern, stack ) ) ;

        tree = new TagTree() ;
        pattern = new int[]{TagTree.WILDCARD, 1, 1} ;
        stack = new Stack() ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertTrue( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 2 ) ) ;
        assertTrue( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 3 ) ) ;
        assertTrue( isReverseTailMatch( tree, pattern, stack ) ) ;

        tree = new TagTree() ;
        pattern = new int[]{TagTree.WILDCARD, 1, 2} ;
        stack = new Stack() ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 1 ) ) ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
        stack.push( new Integer( 2 ) ) ;
        assertFalse( isReverseTailMatch( tree, pattern, stack ) ) ;
    }


    /**
     * Used to white box test the private addWildRuleToNormalTree() method of
     * the TagTree class.
     *
     * @param tree the tree instance to test
     * @param pattern the 1st int[] pattern arg
     * @param rule the 2nd Rule arg
     * @param stack the 3rd Stack arg
     * @param node the last TagNode arg
     */
    public void addWildRuleToNormalTree( TagTree tree, int[] pattern,
                                         Rule rule, Stack stack, TagNode node )
    {
        Class[] argClasses = {
            pattern.getClass(), Rule.class, Stack.class, TagNode.class
        } ;

        Object[] args = { pattern, rule, stack, node } ;
        invoke( tree, TagTree.class, "addWildRuleToNormalTree",
                argClasses, args ) ;
    }


    /**
     * Tests various combinations of wild card patterns and trees to make sure
     * all is working with addWildRuleToNormalTree().
     */
    public void testAddWildRuleToNormalTree2()
    {
        TagTree tree = new TagTree() ;
        int[] pattern = {1,2,3} ;
        Rule r0 = new MockRule() ;
        tree.addRule( pattern, r0 ) ;

        // Walk the branch of tag nodes and validate contents along the way
        TagNode node = getNormalNode( tree, 1 ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 3 ) ) ;
        assertEquals( 1, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;

        // setup the first wild card addition but it should not match
        int[] wildpat = { TagTree.WILDCARD,1,2} ;
        Rule r1 = new MockRule() ;
        addWildRuleToNormalTree( tree, wildpat, r1, new Stack(), node ) ;

        // the pattern *,1,2 should NOT match 1,2,3
        // walk of the branch of tag nodes and validate contents along the way
        node = getNormalNode( tree, 1 ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 3 ) ) ;
        assertEquals( 1, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;

        // now try a matching the pattern with *,1,2,3 so r1 should be present
        wildpat = new int[]{TagTree.WILDCARD, 1, 2, 3} ;
        r1 = new MockRule() ;
        node = getNormalNode( tree, 1 ) ;
        addWildRuleToNormalTree( tree, wildpat, r1, new Stack(), node ) ;

        // the pattern *,1,2,3 should match 1,2,3 now
        // walk of the branch of tag nodes and validate contents along the way
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 3 ) ) ;
        assertEquals( 2, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;
        assertEquals( r1, node.getRules().get( 1 ) ) ;

        // now try a less specific matching the pattern *,2,3
        wildpat = new int[]{TagTree.WILDCARD, 2, 3} ;
        Rule r2 = new MockRule() ;
        node = getNormalNode( tree, 1 ) ;
        addWildRuleToNormalTree( tree, wildpat, r2, new Stack(), node ) ;

        // the pattern *,2,3 should match 1,2,3 now
        // walk of the branch of tag nodes and validate contents along the way
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 3 ) ) ;
        assertEquals( 3, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;
        assertEquals( r1, node.getRules().get( 1 ) ) ;
        assertEquals( r2, node.getRules().get( 2 ) ) ;

        // now try least specific matching using pattern *,3
        wildpat = new int[]{TagTree.WILDCARD, 3} ;
        Rule r3 = new MockRule() ;
        node = getNormalNode( tree, 1 ) ;
        addWildRuleToNormalTree( tree, wildpat, r3, new Stack(), node ) ;

        // the pattern *,3 should match 1,2,3 now
        // walk of the branch of tag nodes and validate contents along the way
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 3 ) ) ;
        assertEquals( 4, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;
        assertEquals( r1, node.getRules().get( 1 ) ) ;
        assertEquals( r2, node.getRules().get( 2 ) ) ;
        assertEquals( r3, node.getRules().get( 3 ) ) ;
    }


    /**
     * Tests to see that we do not add the rule with wild card pattern more
     * than once to a node in the normal tree by first checking if it already
     * contains that rule before a rule add operation to the node.
     */
    public void testAddWildRuleToNormalTree1()
    {
        TagTree tree = new TagTree() ;
        int[] pattern = {1,2,3} ;
        Rule r0 = new MockRule() ;
        tree.addRule( pattern, r0 ) ;

        int[] wildpat = { TagTree.WILDCARD,1,2,3} ;
        TagNode node = getNormalNode( tree, 1 ) ;

        // see if the rule r0 is added - should not be!
        addWildRuleToNormalTree( tree, wildpat, r0, new Stack(), node ) ;
        node = node.getChild( new Integer( 2 ) ).getChild( new Integer( 3 ) ) ;
        List rules = node.getRules() ;

        // only one copy of r0 should exist
        assertEquals( 1, rules.size() ) ;
        assertEquals( r0, rules.get( 0 ) ) ;
    }


    /**
     * Used to white box test the private addWildRuleToNormalTree() method of
     * the TagTree class.
     *
     * @param tree the tree instance to test
     * @param pattern the 1st int[] pattern arg
     * @param rule the 2nd Rule arg
     * @param stack the 3rd Stack arg
     * @param node the last TagNode arg
     */
    public void addWildRuleToWildTree( TagTree tree, int[] pattern,
                                       Rule rule, Stack stack, TagNode node )
    {
        Class[] argClasses = {
            pattern.getClass(), Rule.class, Stack.class, TagNode.class
        } ;

        Object[] args = { pattern, rule, stack, node } ;
        invoke( tree, TagTree.class, "addWildRuleToWildTree", argClasses,
                args ) ;
    }



    /**
     * Tests various combinations of wild card patterns and trees to make sure
     * all is working with addWildRuleToWildTree().
     */
    public void testAddWildRuleToWildTree2()
    {
        TagTree tree = new TagTree() ;
        TagNode n0 = new TagNode( new Integer( 2 ) ) ;
        TagNode n1 = new TagNode( new Integer( 1 ) ) ;
        TagNode n2 = new TagNode( new Integer( 3 ) ) ;
        n0.addNode( n1 ) ;
        n1.addNode( n2 ) ;
        HashMap wildNodes = ( HashMap ) getMember( "wildNodes", tree ) ;
        wildNodes.put( new Integer( 2 ), n0 ) ;

        int[] pattern = {TagTree.WILDCARD,1,2} ;
        Rule r0 = new MockRule() ;
        addWildRuleToWildTree( tree, pattern, r0, new Stack(), n0 ) ;

        // the pattern *,1,2 should match at nodes 2-1 and 2-1-3
        assertEquals( 0, n0.getRules().size() ) ;
        assertEquals( 1, n1.getRules().size() ) ;
        assertEquals( r0, n1.getRules().get( 0 ) ) ;
        assertEquals( 1, n2.getRules().size() ) ;
        assertEquals( r0, n2.getRules().get( 0 ) ) ;
    }

    /**
     * Tests various combinations of wild card patterns and trees to make sure
     * all is working with addWildRuleToWildTree().
     */
    public void testAddWildRuleToWildTree1()
    {
        TagTree tree = new TagTree() ;
        TagNode n0 = new TagNode( new Integer( 2 ) ) ;
        TagNode n1 = new TagNode( new Integer( 1 ) ) ;
        TagNode n2 = new TagNode( new Integer( 3 ) ) ;
        n0.addNode( n1 ) ;
        n1.addNode( n2 ) ;
        HashMap wildNodes = ( HashMap ) getMember( "wildNodes", tree ) ;
        wildNodes.put( new Integer( 2 ), n0 ) ;

        int[] pattern = {TagTree.WILDCARD,1,2} ;
        Rule r0 = new MockRule() ;
        n1.addRule( r0 );
        n2.addRule( r0 );
        addWildRuleToWildTree( tree, pattern, r0, new Stack(), n0 ) ;

        // the pattern *,1,2 should match at nodes 2-1 and 2-1-3
        assertEquals( 0, n0.getRules().size() ) ;
        assertEquals( 1, n1.getRules().size() ) ;
        assertEquals( r0, n1.getRules().get( 0 ) ) ;
        assertEquals( 1, n2.getRules().size() ) ;
        assertEquals( r0, n2.getRules().get( 0 ) ) ;
    }


    /**
     * Tests the addRule method of the tree.
     */
    public void testAddRuleNormal()
    {
        TagTree tree = new TagTree() ;
        int[] pattern = {1,2,3} ;
        tree.addRule( pattern, new MockRule() ) ;
        assertNull( getNormalNode( tree, 4 ) ) ;

        TagNode node = getNormalNode( tree, 1 ) ;
        assertNotNull( node ) ;
        node = node.getChild( new Integer(2) ) ;
        assertNotNull( node ) ;
        node = node.getChild( new Integer(3) ) ;
        assertNotNull( node ) ;
        node = node.getChild( new Integer(4) ) ;
        assertNull( node ) ;

        tree.addRule( pattern, new MockRule() ) ;
    }


    /**
     * Used to white box test the private addWildRule() method of the
     * TagTree.
     *
     * @param tree the tree instance to test
     * @param pattern the pattern arg
     * @param rule the Rule arg
     */
    private void addWildRule( TagTree tree, int[] pattern, Rule rule )
    {
        Class[] argClasses = { pattern.getClass(), Rule.class } ;
        Object[] args = { pattern, rule } ;
        invoke( tree, TagTree.class, "addWildRule", argClasses, args ) ;
    }


    /**
     * Used to white box test the private addNormalRule() method of the
     * TagTree.
     *
     * @param tree the tree instance to test
     * @param pattern the pattern arg
     * @param rule the Rule arg
     */
    private void addNormalRule( TagTree tree, int[] pattern, Rule rule )
    {
        Class[] argClasses = { pattern.getClass(), Rule.class } ;
        Object[] args = { pattern, rule } ;
        invoke( tree, TagTree.class, "addNormalRule",
                argClasses, args ) ;
    }


    /**
     * Tests the addRule method of the tree.
     */
    public void testAddWildRule1()
    {
        TagTree tree = new TagTree() ;
        int[] pattern = {TagTree.WILDCARD,1,2,3} ;
        Rule r0 = new MockRule() ;
        addWildRule( tree, pattern, r0 );
        assertNull( getWildNode( tree, 4 ) ) ;

        TagNode node = getWildNode( tree, 3 ) ;
        assertNotNull( node ) ;
        node = node.getChild( new Integer(2) ) ;
        assertNotNull( node ) ;
        node = node.getChild( new Integer(1) ) ;
        assertNotNull( node ) ;
        assertEquals( 1, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;
        node = node.getChild( new Integer(4) ) ;
        assertNull( node ) ;
    }


    /**
     * Tests the addRule method of the tree.
     */
    public void testAddWildRule2()
    {
        TagTree tree = new TagTree() ;
        int[] pattern0 = {TagTree.WILDCARD,1,2} ;
        int[] pattern1 = {3,1,2} ;
        int[] pattern2 = {TagTree.WILDCARD,2} ;
        Rule r0 = new MockRule() ;
        Rule r1 = new MockRule() ;
        Rule r2 = new MockRule() ;
        addNormalRule( tree, pattern1, r1 ) ;
        addWildRule( tree, pattern0, r0 );
        assertNull( getWildNode( tree, 4 ) ) ;

        // now test that we have made the addition to the wild tree
        TagNode node = getWildNode( tree, 2 ) ;
        assertNotNull( node ) ;
        node = node.getChild( new Integer(1) ) ;
        assertNotNull( node ) ;
        assertEquals( 1, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;

        node = node.getChild( new Integer(4) ) ;
        assertNull( node ) ;

        // now test the normal tree
        HashMap normNodes = ( HashMap ) getMember( "normNodes", tree ) ;
        assertEquals( 1, normNodes.size() ) ;
        node = getNormalNode( tree, 3 ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 1 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 2, node.getRules().size() ) ;
        assertEquals( r1, node.getRules().get( 0 ) ) ;
        assertEquals( r0, node.getRules().get( 1 ) ) ;

        // now we'll add the other wild card and see if it adds correctly
        addWildRule( tree, pattern2, r2 ) ;

        // test that we have made the r2 addition to the places in wild tree
        node = getWildNode( tree, 2 ) ;
        assertNotNull( node ) ;
        assertEquals( 1, node.getRules().size() ) ;
        assertEquals( r2, node.getRules().get( 0 ) ) ;
        node = node.getChild( new Integer(1) ) ;
        assertNotNull( node ) ;
        assertEquals( 2, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;
        assertEquals( r2, node.getRules().get( 1 ) ) ;
        assertTrue( node.isLeaf() ) ;

        // test that we have added r2 to the normal tree
        assertEquals( 1, normNodes.size() ) ;
        node = getNormalNode( tree, 3 ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 1 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 3, node.getRules().size() ) ;
        assertEquals( r1, node.getRules().get( 0 ) ) ;
        assertEquals( r0, node.getRules().get( 1 ) ) ;
        assertEquals( r2, node.getRules().get( 2 ) ) ;

        // control
        node = node.getChild( new Integer(4) ) ;
        assertNull( node ) ;
    }


    /**
     * Tests the addNormalRule method of the tree after registering
     * rule patterns using wild cards.
     */
    public void testAddNormalRule1()
    {
        TagTree tree = new TagTree() ;
        int[] pattern0 = {TagTree.WILDCARD,1,2} ;
        int[] pattern1 = {3,1,2} ;
        int[] pattern2 = {TagTree.WILDCARD,2} ;
        Rule r0 = new MockRule() ;
        Rule r1 = new MockRule() ;
        Rule r2 = new MockRule() ;
        addNormalRule( tree, pattern1, r1 ) ;
        addWildRule( tree, pattern0, r0 );
        assertNull( getWildNode( tree, 4 ) ) ;

        // now test that we have made the addition to the wild tree
        TagNode node = getWildNode( tree, 2 ) ;
        assertNotNull( node ) ;
        node = node.getChild( new Integer(1) ) ;
        assertNotNull( node ) ;
        assertEquals( 1, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;

        node = node.getChild( new Integer(4) ) ;
        assertNull( node ) ;

        // now test the normal tree
        HashMap normNodes = ( HashMap ) getMember( "normNodes", tree ) ;
        assertEquals( 1, normNodes.size() ) ;
        node = getNormalNode( tree, 3 ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 1 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 2, node.getRules().size() ) ;
        assertEquals( r1, node.getRules().get( 0 ) ) ;
        assertEquals( r0, node.getRules().get( 1 ) ) ;

        // now we'll add the other wild card and see if it adds correctly
        addWildRule( tree, pattern2, r2 ) ;

        // test that we have made the r2 addition to the places in wild tree
        node = getWildNode( tree, 2 ) ;
        assertNotNull( node ) ;
        assertEquals( 1, node.getRules().size() ) ;
        assertEquals( r2, node.getRules().get( 0 ) ) ;
        node = node.getChild( new Integer(1) ) ;
        assertNotNull( node ) ;
        assertEquals( 2, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;
        assertEquals( r2, node.getRules().get( 1 ) ) ;
        assertTrue( node.isLeaf() ) ;

        // test that we have added r2 to the normal tree
        assertEquals( 1, normNodes.size() ) ;
        node = getNormalNode( tree, 3 ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 1 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 3, node.getRules().size() ) ;
        assertEquals( r1, node.getRules().get( 0 ) ) ;
        assertEquals( r0, node.getRules().get( 1 ) ) ;
        assertEquals( r2, node.getRules().get( 2 ) ) ;

        // control
        node = node.getChild( new Integer(4) ) ;
        assertNull( node ) ;

        // lets add a normal node to the normal tree matching these patterns
        int[] pattern3 = {8,1,2} ;
        Rule r3 = new MockRule() ;
        List wildRegistrations = ( List )
                getMember( "wildRegistrations", tree ) ;
        wildRegistrations.add( new RuleRegistration( pattern0, r0 ) ) ;
        wildRegistrations.add( new RuleRegistration( pattern2, r2 ) ) ;
        addNormalRule( tree, pattern3, r3 ) ;

        // test if we have added the rules and nodes correctly
        assertEquals( 2, normNodes.size() ) ;
        node = getNormalNode( tree, 8 ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 1 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 0, node.getRules().size() ) ;
        node = node.getChild( new Integer( 2 ) ) ;
        assertNotNull( node ) ;
        assertEquals( 3, node.getRules().size() ) ;
        assertEquals( r0, node.getRules().get( 0 ) ) ;
        assertEquals( r2, node.getRules().get( 1 ) ) ;
        assertEquals( r3, node.getRules().get( 2 ) ) ;
    }


    /**
     * Tests the TagTree.match(int[]) method.
     */
    public void testMatchintArray()
    {
        TagTree tree = new TagTree() ;
        int[] pattern0 = {1,2,3} ;
        int[] pattern1 = {4,2,7} ;
        int[] pattern2 = {1,5,3} ;
        Rule rule0 = new MockRule() ;
        Rule rule1 = new MockRule() ;
        Rule rule2 = new MockRule() ;
        tree.addRule( pattern0, rule0 ) ;
        tree.addRule( pattern1, rule1 ) ;
        tree.addRule( pattern2, rule2 ) ;
        assertEquals( rule0, ( (List) tree.match( pattern0 ) ).get(0) ) ;
        assertEquals( rule1, ( (List) tree.match( pattern1 ) ).get(0) ) ;
        assertEquals( rule2, ( (List) tree.match( pattern2 ) ).get(0) ) ;

        assertNotSame( rule0, ( (List) tree.match( pattern1 ) ).get(0) ) ;
        assertNotSame( rule1, ( (List) tree.match( pattern2 ) ).get(0) ) ;
        assertNotSame( rule2, ( (List) tree.match( pattern0 ) ).get(0) ) ;

        int[] pattern3 = { 12 } ;
        tree.match( pattern3 ) ;
    }


    /**
     * Tests the TagTree.match(IntStack) method.
     */
    public void testMatchIntStack()
    {
        TagTree tree = new TagTree() ;
        int[] pattern0 = {1,2,3} ;
        int[] pattern1 = {4,2,7} ;
        int[] pattern2 = {1,5,3} ;
        Rule rule0 = new MockRule() ;
        Rule rule1 = new MockRule() ;
        Rule rule2 = new MockRule() ;
        tree.addRule( pattern0, rule0 ) ;
        tree.addRule( pattern1, rule1 ) ;
        tree.addRule( pattern2, rule2 ) ;
        
        assertEquals( rule0, ( (List) tree.match( new IntStack( pattern0 ) ) )
                .get(0) ) ;
        assertEquals( rule1, ( (List) tree.match( new IntStack( pattern1 ) ) )
                .get(0) ) ;
        assertEquals( rule2, ( (List) tree.match( new IntStack( pattern2 ) ) )
                .get(0) ) ;
        assertNotSame( rule0, ( (List) tree.match( new IntStack( pattern1 ) ) )
                .get(0) ) ;
        assertNotSame( rule1, ( (List) tree.match( new IntStack( pattern2 ) ) )
                .get(0) ) ;
        assertNotSame( rule2, ( (List) tree.match( new IntStack( pattern0 ) ) )
                .get(0) ) ;
    }


    /**
     * Tests the TagTree.getNormalNode(int[]) method.
     */
    public void testGetNodeintArray()
    {
        TagTree tree = new TagTree() ;
        int[] pattern0 = {1,2,3} ;
        int[] pattern1 = {4,2,7} ;
        int[] pattern2 = {1,5,3} ;
        Rule rule0 = new MockRule() ;
        Rule rule1 = new MockRule() ;
        Rule rule2 = new MockRule() ;
        tree.addRule( pattern0, rule0 ) ;
        tree.addRule( pattern1, rule1 ) ;
        tree.addRule( pattern2, rule2 ) ;

        int[] pattern3 = { 1, 23 } ;
        assertNull( tree.getNode( pattern3 ) ) ;
    }


    class MockRule extends AbstractRule { }
}
