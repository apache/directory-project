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


import junit.framework.TestCase;
import org.apache.asn1.ber.TypeClass;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.EmptyStackException;


/**
 * A test case for the BERDigester.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BERDigesterTest extends TestCase
{
    BERDigester digester ;


    /**
     * Sets up the decoder rulesBase system.
     *
     * @throws Exception
     */
    public void setUp() throws Exception
    {
        super.setUp();

        digester = new BERDigester() ;
    }


    /**
     * Clears and nulls out the rulesBase.
     *
     * @throws Exception
     */
    public void tearDown() throws Exception
    {
        super.tearDown();

        digester.clear() ;
        digester = null ;
    }


    /**
     * Tests the BERDigester.addRule(int[],Rule) method.
     */
    public void testAddRule() throws Exception
    {
        int[] pat0 = { 1, 2, 3 } ;
        MockRule rule0 = new MockRule() ;

        digester.addRule( pat0, rule0 ) ;
    }


    /**
     * Tests the BERDigester.setRules(Rules) method.
     */
    public void testSetRules() throws Exception
    {
        RulesBase rules = new RulesBase() ;
        digester.setRules( rules ) ;
        assertSame( rules.getDigester(), digester ) ;
    }


    /**
     * Tests the BERDigester.getRules() method.
     */
    public void testGetRules() throws Exception
    {
        Rules rules = digester.getRules() ;
        assertNotNull( rules ) ;
        assertTrue( rules.rules().isEmpty() ) ;
    }


    /**
     * Tests the BERDigester.getCount() method.
     */
    public void testGetCount() throws Exception
    {
        assertEquals( 0, digester.getCount() ) ;
    }


    /**
     * Tests the BERDigester.peek() method.
     */
    public void testPeek() throws Exception
    {
        try
        {
            digester.peek() ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never reach this line" ) ;
    }


    /**
     * Tests the BERDigester.peek(int) method.
     */
    public void testPeekint() throws Exception
    {
        try
        {
            digester.peek() ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
        }

        try
        {
            digester.peek( -1 ) ;
        }
        catch( IndexOutOfBoundsException e )
        {
            assertNotNull( e ) ;
        }

        try
        {
            digester.peek( Integer.MAX_VALUE ) ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
        }

        try
        {
            digester.peek( Integer.MIN_VALUE ) ;
        }
        catch( IndexOutOfBoundsException e )
        {
            assertNotNull( e ) ;
        }
    }


    /**
     * Tests the BERDigester.pop() method.
     */
    public void testPop() throws Exception
    {
        try
        {
            digester.pop() ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never reach this line" ) ;
    }


    /**
     * Tests the BERDigester.push(Object) method.
     */
    public void testPush() throws Exception
    {
        Object o0 = new Object() ;
        Object o1 = new Object() ;
        Object o2 = new Object() ;

        try
        {
            digester.pop() ;
            fail( "should not get here" ) ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
        }

        digester.push( o0 ) ;
        assertSame( o0, digester.peek() ) ;
        assertSame( o0, digester.peek( 0 ) ) ;

        digester.push( o1 ) ;
        assertSame( o1, digester.peek() ) ;
        assertSame( o1, digester.peek( 0 ) ) ;
        assertSame( o0, digester.peek( 1 ) ) ;

        digester.push( o2 ) ;
        assertSame( o2, digester.peek() ) ;
        assertSame( o2, digester.peek( 0 ) ) ;
        assertSame( o1, digester.peek( 1 ) ) ;
        assertSame( o0, digester.peek( 2 ) ) ;

        assertSame( o2, digester.pop() ) ;
        assertSame( o1, digester.pop() ) ;
        assertSame( o0, digester.pop() ) ;

        try
        {
            digester.pop() ;
            fail( "should not get here" ) ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
        }
    }


    /**
     * Tests the BERDigester.GetRoot() method.
     */
    public void testGetRoot() throws Exception
    {
        Object o0 = new Object() ;
        Object o1 = new Object() ;
        Object o2 = new Object() ;

        try
        {
            digester.pop() ;
            fail( "should not get here" ) ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
        }

        digester.push( o0 ) ;
        assertSame( o0, digester.getRoot() ) ;

        digester.push( o1 ) ;
        assertSame( o0, digester.getRoot() ) ;

        digester.push( o2 ) ;

        assertSame( o2, digester.pop() ) ;
        assertSame( o1, digester.pop() ) ;
        assertSame( o0, digester.pop() ) ;

        try
        {
            digester.pop() ;
            fail( "should not get here" ) ;
        }
        catch( EmptyStackException e )
        {
            assertNotNull( e ) ;
        }

        assertSame( o0, digester.getRoot() ) ;
    }


    /**
     * Tests the BERDigester.getTopTag() method.
     */
    public void testGetTopTag() throws Exception
    {
        assertEquals( BERDigester.NO_TOP_TAG, digester.getTopTag() ) ;
        digester.decode( ByteBuffer.wrap( new byte[] { (byte) 3} ) ) ;
        assertFalse( BERDigester.NO_TOP_TAG == digester.getTopTag() ); ;
    }


    /**
     * Tests the BERDigester.getClassLoader() method.
     */
    public void testGetClassLoader() throws Exception
    {
        assertSame( BERDigester.class.getClassLoader(), digester.getClassLoader() ) ;
    }


    /**
     * Tests the BERDigester.setClassLoader() method.
     */
    public void testSetClassLoader() throws Exception
    {
        assertSame( BERDigester.class.getClassLoader(), digester.getClassLoader() ) ;
        URL[] urls = { new URL( "file:///." ) } ;
        ClassLoader cl = new URLClassLoader( urls ) ;
        digester.setClassLoader( cl ) ;
        assertSame( cl, digester.getClassLoader() ) ;
    }


    /**
     * Tests the BERDigester.getUseContextClassLoader() method.
     */
    public void testGetUseContextClassLoader() throws Exception
    {
        assertSame( BERDigester.class.getClassLoader(), digester.getClassLoader() ) ;
        assertFalse( digester.getUseContextClassLoader() ) ;
    }


    /**
     * Tests the BERDigester.setUseContextClassLoader() method.
     */
    public void testSetUseContextClassLoader() throws Exception
    {
        digester.setUseContextClassLoader( true ) ;
        assertTrue( digester.getUseContextClassLoader() ) ;
        assertSame( Thread.currentThread().getContextClassLoader(),
                digester.getClassLoader() ) ;
        Thread.currentThread().setContextClassLoader( null ) ;
        assertSame( BERDigester.class.getClassLoader(), digester.getClassLoader() ) ;
    }


    // ------------------------------------------------------------------------
    // test the event fireing routines that trigger rules
    // ------------------------------------------------------------------------


    /**
     * Tests the BERDigester.fireTagEvent(int,boolean,TypeClass) method.
     */
    public void testFireTagEvent() throws Exception
    {
        digester.fireTagEvent( 0, true, TypeClass.UNIVERSAL ) ;

        int[] pat0 = { 0x10000000 } ;
        CollectorRule rule0 = new CollectorRule() ;
        digester.addRule( pat0, rule0 ) ;
        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        // Nothing should have fired yet
        assertFalse( rule0.tagFired ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;

        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 16, rule0.id ) ;
        assertEquals( TypeClass.UNIVERSAL, rule0.typeClass ) ;
        assertEquals( true, rule0.isPrimitive ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;
    }


    /**
     * Tests the BERDigester.fireLengthEvent(int) method.
     */
    public void testFireLengthEvent() throws Exception
    {
        digester.fireLengthEvent( 0 ) ;

        int[] pat0 = { 0x10000000 } ;
        CollectorRule rule0 = new CollectorRule() ;
        digester.addRule( pat0, rule0 ) ;
        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        // Nothing should have fired yet
        assertFalse( rule0.tagFired ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;

        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 16, rule0.id ) ;
        assertEquals( TypeClass.UNIVERSAL, rule0.typeClass ) ;
        assertEquals( true, rule0.isPrimitive ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;
    }


    /**
     * Tests the BERDigester.fireValueEvent(ByteBuffer) method.
     */
    public void testFireValueEvent() throws Exception
    {
        digester.fireValueEvent( ByteBuffer.allocate( 0 ) ) ;

        int[] pat0 = { 0x10000000 } ;
        CollectorRule rule0 = new CollectorRule() ;
        digester.addRule( pat0, rule0 ) ;
        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        // Nothing should have fired yet
        assertFalse( rule0.tagFired ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;

        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 16, rule0.id ) ;
        assertEquals( TypeClass.UNIVERSAL, rule0.typeClass ) ;
        assertEquals( true, rule0.isPrimitive ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;
        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 2, rule0.length ) ;
        assertTrue( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x07 ) ;
        buf.flip() ;
        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 2, rule0.length ) ;
        assertTrue( rule0.lengthFired ) ;
        assertTrue( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;
        assertEquals( buf.get(0), rule0.buf.get(0) ) ;
    }


    /**
     * Tests the BERDecoder.fireFinishEvent() methods.
     */
    public void testFireFinishEvent() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        CollectorRule rule0 = new CollectorRule() ;
        digester.addRule( pat0, rule0 ) ;
        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        // Nothing should have fired yet
        assertFalse( rule0.tagFired ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;

        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 16, rule0.id ) ;
        assertEquals( TypeClass.UNIVERSAL, rule0.typeClass ) ;
        assertEquals( true, rule0.isPrimitive ) ;
        assertFalse( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;
        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 2, rule0.length ) ;
        assertTrue( rule0.lengthFired ) ;
        assertFalse( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x07 ) ;
        buf.flip() ;
        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 2, rule0.length ) ;
        assertTrue( rule0.lengthFired ) ;
        assertTrue( rule0.valueFired ) ;
        assertFalse( rule0.hasFinished ) ;
        assertEquals( 0x10000000, digester.getTopTag() ) ;
        assertEquals( buf.get(0), rule0.buf.get(0) ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x09 ) ;
        buf.flip() ;
        digester.decode( buf ) ;
        assertTrue( rule0.tagFired ) ;
        assertEquals( 2, rule0.length ) ;
        assertTrue( rule0.lengthFired ) ;
        assertTrue( rule0.valueFired ) ;
        assertTrue( rule0.hasFinished ) ;
        assertEquals( BERDigester.NO_TOP_TAG, digester.getTopTag() ) ;
        assertEquals( buf.get(0), rule0.buf.get(0) ) ;
    }


    /**
     * Tests the when rules throw errors.
     */
    public void testErrorOnTag() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ErrorRule rule0 = new ErrorRule() ;
        digester.addRule( pat0, rule0 ) ;
        rule0.throwErrors() ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        try
        {
            digester.decode( buf ) ;
        }
        catch( Error e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    public void testErrorOnLength() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ErrorRule rule0 = new ErrorRule() ;
        digester.addRule( pat0, rule0 ) ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        rule0.throwErrors() ;
        try
        {
            digester.decode( buf ) ;
        }
        catch( Error e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    public void testErrorOnValue() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ErrorRule rule0 = new ErrorRule() ;
        digester.addRule( pat0, rule0 ) ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        rule0.throwErrors() ;
        try
        {
            digester.decode( buf ) ;
        }
        catch( Error e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    public void testErrorOnFinish() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ErrorRule rule0 = new ErrorRule() ;
        digester.addRule( pat0, rule0 ) ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x07 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x09 ) ;
        buf.flip() ;

        rule0.throwErrorOnFinish() ;
        try
        {
            digester.decode( buf ) ;
        }
        catch( Error e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    /**
     * Tests the when rules throw errors.
     */
    public void testExceptionOnTag() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ExceptionRule rule0 = new ExceptionRule() ;
        digester.addRule( pat0, rule0 ) ;
        rule0.throwErrors() ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        try
        {
            digester.decode( buf ) ;
        }
        catch( RuntimeException e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    public void testExceptionOnLength() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ExceptionRule rule0 = new ExceptionRule() ;
        digester.addRule( pat0, rule0 ) ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        rule0.throwErrors() ;
        try
        {
            digester.decode( buf ) ;
        }
        catch( RuntimeException e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    public void testExceptionOnValue() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ExceptionRule rule0 = new ExceptionRule() ;
        digester.addRule( pat0, rule0 ) ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        rule0.throwErrors() ;
        try
        {
            digester.decode( buf ) ;
        }
        catch( RuntimeException e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    public void testExceptionOnFinish() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        ExceptionRule rule0 = new ExceptionRule() ;
        digester.addRule( pat0, rule0 ) ;

        ByteBuffer buf = ByteBuffer.allocate( 1 ) ;
        buf.put( ( byte ) 0x10 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x02 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x07 ) ;
        buf.flip() ;

        digester.decode( buf ) ;

        buf.rewind() ;
        buf.put( ( byte ) 0x09 ) ;
        buf.flip() ;

        rule0.throwErrorOnFinish() ;
        try
        {
            digester.decode( buf ) ;
        }
        catch( RuntimeException e )
        {
            assertNotNull( e ) ;
            return ;
        }

        fail( "should never get here" ) ;
    }


    public void testFullTlv() throws Exception
    {
        int[] pat0 = { 0x10000000 } ;
        int[] pat1 = { 0x11000000 } ;
        CollectorRule rule0 = new CollectorRule() ;
        CollectorRule rule1 = new CollectorRule() ;
        digester.addRule( pat0, rule0 ) ;
        digester.addRule( pat1, rule1 ) ;
        byte[] bites = { 0x10, 0x02, 0x07, 0x09 } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        digester.decode( buf ) ;
    }


    public void testNestedTlvs() throws Exception
    {
        int[] pat0 = { 0x14000000, 0x10000000 } ;
        int[] pat1 = { 0x11000000 } ;
        CollectorRule rule0 = new CollectorRule() ;
        CollectorRule rule1 = new CollectorRule() ;
        digester.addRule( pat0, rule0 ) ;
        digester.addRule( pat1, rule1 ) ;
        byte[] bites = {
            0x34, 0x04,                 // constructed id = 20 w/ 4 byte value
            0x10, 0x02, 0x07, 0x09      // primitive id = 16 w/ 2 byte value
        } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        digester.decode( buf ) ;
    }


    public void testNestedTlvsWithErrors() throws Exception
    {
        int[] pat0 = { 0x14000000, 0x10000000 } ;
        int[] pat1 = { 0x14000000 } ;
        ErrorRule rule0 = new ErrorRule() ;
        ErrorRule rule1 = new ErrorRule() ;
        digester.addRule( pat0, rule0 ) ;
        digester.addRule( pat1, rule1 ) ;
        byte[] bites = {
            0x34, 0x04,                 // constructed id = 20 w/ 4 byte value
            0x10, 0x02, 0x07            // primitive id = 16 w/ 2 byte value
            // last byte missing
        } ;
        ByteBuffer buf = ByteBuffer.wrap( bites ) ;
        digester.decode( buf ) ;

        rule1.throwErrorOnFinish() ;
        buf = ByteBuffer.allocate( 1 ) ;
        buf.put( 0, ( byte ) 0x09 ) ;

        try
        {
            digester.decode( buf ) ;
        }
        catch( Error e )
        {
            return ;
        }

        fail( "should never get here" ) ;
    }


    class MockRule extends AbstractRule {}


    class CollectorRule extends AbstractRule
    {
        boolean tagFired = false ;
        boolean lengthFired = false ;
        boolean valueFired = false ;
        int id = -1 ;
        boolean isPrimitive = false ;
        TypeClass typeClass = null ;
        int length = -1 ;
        ByteBuffer buf = null ;
        boolean hasFinished = false ;

        public void tag( int id, boolean isPrimitive, TypeClass typeClass )
        {
            this.id = id ;
            this.typeClass = typeClass ;
            this.isPrimitive = isPrimitive ;
            this.tagFired = true ;
        }

        public void length( int length )
        {
            this.length = length ;
            this.lengthFired = true ;
        }

        public void value( ByteBuffer buf )
        {
            this.buf = buf ;
            this.valueFired = true ;
        }

        public void finish()
        {
            this.hasFinished = true ;
        }
    }


    class ErrorRule extends AbstractRule
    {
        private boolean doThrow = false ;
        private boolean doThrowOnFinish = false ;

        public void throwErrors()
        {
            doThrow = true ;
        }

        public void throwErrorOnFinish()
        {
            doThrowOnFinish = true ;
        }

        public void tag( int id, boolean isPrimitive, TypeClass typeClass )
        {
            if ( doThrow )
            {
                throw new Error() ;
            }
        }

        public void length( int length )
        {
            if ( doThrow )
            {
                throw new Error() ;
            }
        }

        public void value( ByteBuffer buf )
        {
            if ( doThrow )
            {
                throw new Error() ;
            }
        }

        public void finish()
        {
            if ( doThrowOnFinish )
            {
                throw new Error() ;
            }
        }
    }


    class ExceptionRule extends AbstractRule
    {
        private boolean doThrow = false ;
        private boolean doThrowOnFinish = false ;

        public void throwErrors()
        {
            doThrow = true ;
        }

        public void throwErrorOnFinish()
        {
            doThrowOnFinish = true ;
        }

        public void tag( int id, boolean isPrimitive, TypeClass typeClass )
        {
            if ( doThrow )
            {
                throw new RuntimeException() ;
            }
        }

        public void length( int length )
        {
            if ( doThrow )
            {
                throw new RuntimeException() ;
            }
        }

        public void value( ByteBuffer buf )
        {
            if ( doThrow )
            {
                throw new RuntimeException() ;
            }
        }

        public void finish()
        {
            if ( doThrowOnFinish )
            {
                throw new RuntimeException() ;
            }
        }
    }
}