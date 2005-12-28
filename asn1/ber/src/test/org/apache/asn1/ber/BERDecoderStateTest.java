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
package org.apache.asn1.ber ;


import junit.framework.TestCase ;


/**
 * Tests the 
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDecoderStateTest extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(BERDecoderStateTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for BERDecoderStateTest.
     * @param arg0
     */
    public BERDecoderStateTest(String arg0)
    {
        super(arg0);
    }

    public void testGetNext()
    {
        BERDecoderState state = BERDecoderState.getStartState() ;
        
        assertEquals( BERDecoderState.TAG, state ) ;
        state = state.getNext( true ) ;
        assertEquals( BERDecoderState.LENGTH, state ) ;
        state = state.getNext( true ) ;
        assertEquals( BERDecoderState.VALUE, state ) ;

        state = state.getNext( true ) ;
        assertEquals( BERDecoderState.TAG, state ) ;
        state = state.getNext( true ) ;
        assertEquals( BERDecoderState.LENGTH, state ) ;
        state = state.getNext( false ) ;
        assertEquals( BERDecoderState.TAG, state ) ;
    }

    public void testIsEndState()
    {
        assertFalse( BERDecoderState.TAG.isEndState( true ) ) ;
        assertFalse( BERDecoderState.TAG.isEndState( false ) ) ;
        assertFalse( BERDecoderState.LENGTH.isEndState( true ) ) ;
        assertTrue( BERDecoderState.LENGTH.isEndState( false ) ) ;
        assertTrue( BERDecoderState.VALUE.isEndState( true ) ) ;
        assertTrue( BERDecoderState.VALUE.isEndState( false ) ) ;
    }

    public void testGetStartState()
    {
        assertEquals( BERDecoderState.TAG, BERDecoderState.getStartState() ) ;
    }

    /*
     * Class to test for BERDecoderState getTypeClass(String)
     */
    public void testGetStateString()
    {
        assertEquals( BERDecoderState.LENGTH, 
                BERDecoderState.getState(BERDecoderState.LENGTH.getName()) ) ;
        assertEquals( BERDecoderState.TAG, 
                BERDecoderState.getState(BERDecoderState.TAG.getName()) ) ;
        assertEquals( BERDecoderState.VALUE, 
                BERDecoderState.getState(BERDecoderState.VALUE.getName()) ) ;
        
        assertEquals( BERDecoderState.LENGTH, 
                BERDecoderState.getState("length") ) ;
        assertEquals( BERDecoderState.TAG, 
                BERDecoderState.getState("TAG") ) ;
        assertEquals( BERDecoderState.TAG, 
                BERDecoderState.getState("Tag") ) ;
        assertEquals( BERDecoderState.VALUE, 
                BERDecoderState.getState("value") ) ;
        
        try
        {
            BERDecoderState.getState("asdf") ;
            fail( "should not be reached due to thrown exception" ) ;
        }
        catch ( Throwable t )
        {
            assertNotNull( t ) ;
        }
    }

    /*
     * Class to test for BERDecoderState getTypeClass(int)
     */
    public void testGetStateint()
    {
        assertEquals( BERDecoderState.LENGTH, 
                BERDecoderState.getState(BERDecoderState.LENGTH_VAL) ) ;
        assertEquals( BERDecoderState.TAG, 
                BERDecoderState.getState(BERDecoderState.TAG_VAL) ) ;
        assertEquals( BERDecoderState.VALUE, 
                BERDecoderState.getState(BERDecoderState.VALUE_VAL) ) ;
        
        try
        {
            BERDecoderState.getState( 293847 ) ;
            fail( "should not be reached due to thrown exception" ) ;
        }
        catch ( Throwable t )
        {
            assertNotNull( t ) ;
        }
    }
}
