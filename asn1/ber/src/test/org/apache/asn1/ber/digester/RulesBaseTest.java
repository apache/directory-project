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


import junit.framework.TestCase ;

import java.util.List ;

import org.apache.commons.collections.primitives.IntStack;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.asn1.ber.digester.BERDigester;
import org.apache.asn1.ber.digester.RulesBase;


/**
 * A test case for the RulesBase.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RulesBaseTest extends TestCase
{
    int[] p0 = { 1, 2, 3 } ;
    IntStack s0 = new IntStack( p0 ) ;
    int[] p1 = { 1, 6, 7, 8 } ;
    IntStack s1 = new IntStack( p1 ) ;
    int[] p2 = { 4, 5, 6 } ;
    IntStack s2 = new IntStack( p2 ) ;
    MockRule r0 = new MockRule() ;
    MockRule r1 = new MockRule() ;
    MockRule r2 = new MockRule() ;
    RulesBase rulesBase ;


    /**
     * Sets up the decoder rulesBase system.
     *
     * @throws Exception
     */
    public void setUp() throws Exception
    {
        super.setUp() ;

        rulesBase = new RulesBase() ;
    }


    /**
     * Clears and nulls out the rulesBase.
     *
     * @throws Exception
     */
    public void tearDown() throws Exception
    {
        super.tearDown() ;

        rulesBase.clear() ;
        rulesBase = null ;
    }


    /**
     * Tests the RulesBase.add(int[],Rule) method.
     */
    public void testAdd()
    {

        assertTrue( "Should be empty on creation", rulesBase.rules().isEmpty() ) ;

        rulesBase.add( p0, r0 ) ;
        assertFalse( rulesBase.rules().isEmpty() ) ;
        assertEquals( "Should have 1 rule  after 1st add", 1, rulesBase.rules().size() ) ;
        assertSame( "1st rule should be r0", rulesBase.rules().get( 0 ), r0 ) ;

        rulesBase.add( p1, r1 ) ;
        assertFalse( rulesBase.rules().isEmpty() ) ;
        assertEquals( "Should have 2 rules after 2nd add", 2, rulesBase.rules().size() ) ;
        assertSame( "2nd rule should be r1", rulesBase.rules().get( 1 ), r1 ) ;

        rulesBase.add( p2, r2 ) ;
        assertFalse( rulesBase.rules().isEmpty() ) ;
        assertEquals( "Should have 3 rules after 3rd add", 3, rulesBase.rules().size() ) ;
        assertSame( "3rd rule should be r2", rulesBase.rules().get( 2 ), r2 ) ;
    }


    /**
     * Tests the RulesBase.match(int[]) method.
     */
    public void testMatchint()
    {
        List matched = null ;

        matched = rulesBase.match( p0 ) ;
        assertTrue( "match on p0 should not return any rules", matched.isEmpty() ) ;
        rulesBase.add( p0, r0 ) ;
        matched = rulesBase.match( p0 ) ;
        assertSame( "match on p0 should return r0 only", matched.get( 0 ), r0 ) ;
        assertEquals( "match on p0 should only match for one rule", 1, matched.size() ) ;

        matched = rulesBase.match( p1 ) ;
        assertTrue( "match on p1 should not return any rules", matched.isEmpty() ) ;
        rulesBase.add( p1, r1 ) ;
        matched = rulesBase.match( p1 ) ;
        assertSame( "match on p1 should return r1 only", matched.get( 0 ), r1 ) ;
        assertEquals( "match on p1 should only match for one rule", 1, matched.size() ) ;

        matched = rulesBase.match( p2 ) ;
        assertTrue( "match on p2 should not return any rules", matched.isEmpty() ) ;
        rulesBase.add( p2, r2 ) ;
        matched = rulesBase.match( p2 ) ;
        assertSame( "match on p2 should return r2 only", matched.get( 0 ), r2 ) ;
        assertEquals( "match on p2 should only match for one rule", 1, matched.size() ) ;
    }


    /**
     * Tests the RulesBase.match(int[]) method.
     */
    public void testMatchIntStack()
    {
        List matched = null ;

        matched = rulesBase.match( s0 ) ;
        assertTrue( "match on s0 should not return any rules", matched.isEmpty() ) ;
        rulesBase.add( p0, r0 ) ;
        matched = rulesBase.match( s0 ) ;
        assertSame( "match on s0 should return r0 only", matched.get( 0 ), r0 ) ;
        assertEquals( "match on s0 should only match for one rule", 1, matched.size() ) ;

        matched = rulesBase.match( s1 ) ;
        assertTrue( "match on s1 should not return any rules", matched.isEmpty() ) ;
        rulesBase.add( p1, r1 ) ;
        matched = rulesBase.match( s1 ) ;
        assertSame( "match on s1 should return r1 only", matched.get( 0 ), r1 ) ;
        assertEquals( "match on s1 should only match for one rule", 1, matched.size() ) ;

        matched = rulesBase.match( s2 ) ;
        assertTrue( "match on s2 should not return any rules", matched.isEmpty() ) ;
        rulesBase.add( p2, r2 ) ;
        matched = rulesBase.match( s2 ) ;
        assertSame( "match on s2 should return r2 only", matched.get( 0 ), r2 ) ;
        assertEquals( "match on s2 should only match for one rule", 1, matched.size() ) ;
    }


    public void testGetDigester()
    {
        assertNull( rulesBase.getDigester() ) ;
    }


    public void testSetDigester()
    {
        assertNull( rulesBase.getDigester() ) ;
        BERDigester digester = new BERDigester() ;
        rulesBase.setDigester( digester ) ;
        assertSame( digester, rulesBase.getDigester() ) ;
    }


    class MockRule extends AbstractRule {}
}