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
package org.apache.asn1.ber.digester.rules ;


import junit.framework.TestCase ;

import java.nio.ByteBuffer ;

import org.apache.asn1.ber.digester.rules.ByteAccumulator;


/**
 *  Tests the ByteAccumulator class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ByteAccumulatorTest extends TestCase
{
    ByteAccumulator accumulator = null ;


    protected void setUp() throws Exception
    {
        super.setUp();
        accumulator = new ByteAccumulator() ;
    }


    protected void tearDown() throws Exception
    {
        super.tearDown();
        accumulator = null ;
    }


    public void testByteAccumulatorInt()
    {
        ByteAccumulator ba = new ByteAccumulator( 72 ) ;
        assertEquals( ba.getInitialSize(), ba.getCapacity() ) ;
        assertEquals( ba.getInitialSize(), ba.getRemainingSpace() ) ;
        assertEquals( 0, ba.getPosition() ) ;

        // shuts up clover
        ba = new ByteAccumulator( -1 ) ;
        assertEquals( ba.getInitialSize(), ba.getCapacity() ) ;
        assertEquals( ba.getInitialSize(), ba.getRemainingSpace() ) ;
        assertEquals( 0, ba.getPosition() ) ;
    }


    /**
     * Fills the buffer by varying amounts so that either the remaining
     * amount is used for growth increment or the set increment value is
     * used.  Basically tests the growth behavior and byte accounting.
     */
    public void testFill()
    {
        assertEquals( accumulator.getInitialSize(),
                accumulator.getCapacity() ) ;
        assertEquals( accumulator.getInitialSize(),
                accumulator.getRemainingSpace() ) ;
        assertEquals( 0, accumulator.getPosition() ) ;

        ByteBuffer buf = ByteBuffer.allocate( 70 ) ;
        buf.position( 70 ).flip() ;
        accumulator.fill( buf ) ;
        assertEquals( 100, accumulator.getCapacity() ) ;
        assertEquals( 30, accumulator.getRemainingSpace() ) ;
        assertEquals( 70, accumulator.getPosition() ) ;

        buf = ByteBuffer.allocate( 70 ) ;
        buf.position( 70 ).flip() ;
        accumulator.fill( buf ) ;
        assertEquals( 200, accumulator.getCapacity() ) ;
        assertEquals( 60, accumulator.getRemainingSpace() ) ;

        buf = ByteBuffer.allocate( 160 ) ;
        buf.position( 160 ).flip() ;
        accumulator.fill( buf ) ;
        assertEquals( 300, accumulator.getCapacity() ) ;
        assertEquals( 0, accumulator.getRemainingSpace() ) ;

        buf = ByteBuffer.allocate( 110 ) ;
        buf.position( 110 ).flip() ;
        accumulator.fill( buf ) ;
        assertEquals( 410, accumulator.getCapacity() ) ;

        // test the trivial case
        accumulator.fill( buf ) ;
        assertEquals( 410, accumulator.getCapacity() ) ;
        assertEquals( 0, accumulator.getRemainingSpace() ) ;
    }


    /**
     * Tests to make sure drains reset the backing store to the initial state
     * and the right amount of compacted buffer is returned.  Make sure the
     * inputs are the same as the outputs.
     */
    public void testDrain()
    {
        ByteBuffer buf = ByteBuffer.allocate( 200 ) ;
        buf.position( 200 ).flip() ;
        accumulator.fill( buf ) ;
        assertEquals( 200, accumulator.getCapacity() ) ;
        ByteBuffer total = accumulator.drain() ;
        assertEquals( 200, total.remaining() ) ;
        assertEquals( 0, accumulator.getCapacity() ) ;
        assertEquals( 0, accumulator.getRemainingSpace() ) ;

        buf = ByteBuffer.allocate( 30 ) ;
        buf.position( 30 ).flip() ;
        accumulator.fill( buf ) ;
        assertEquals( 100, accumulator.getCapacity() ) ;
        assertEquals( 70, accumulator.getRemainingSpace() ) ;
        total = accumulator.drain() ;
        assertEquals( 30, total.remaining() ) ;
        assertEquals( 0, accumulator.getCapacity() ) ;
        assertEquals( 0, accumulator.getRemainingSpace() ) ;

        buf = ByteBuffer.allocate( 3 ) ;
        buf.put( (byte) 0x01 ) ;
        buf.put( (byte) 0x02 ) ;
        buf.put( (byte) 0x03 ) ;
        buf.flip() ;
        accumulator.fill( buf ) ;
        assertEquals( 100, accumulator.getCapacity() ) ;
        assertEquals( 97, accumulator.getRemainingSpace() ) ;
        buf = accumulator.drain() ;
        assertEquals( 0, accumulator.getCapacity() ) ;
        assertEquals( 0, accumulator.getRemainingSpace() ) ;
        assertEquals( 0x01, buf.get() ) ;
        assertEquals( 0x02, buf.get() ) ;
        assertEquals( 0x03, buf.get() ) ;
        assertFalse( buf.hasRemaining() ) ;

        ByteAccumulator ba = new ByteAccumulator( 1 ) ;
        ba.fill( ByteBuffer.allocate( 1 ) ) ;
        buf = ba.drain() ;
        buf.get() ;
        assertFalse( buf.hasRemaining() ) ;
    }


    public void testDrainInt0()
    {
        accumulator.fill( ByteBuffer.allocate( 200 ) ) ;
        accumulator.drain( -1 ) ;
        assertEquals( 0, accumulator.getCapacity() ) ;
        assertEquals( 0, accumulator.getRemainingSpace() ) ;
    }


    public void testDrainInt1()
    {
        accumulator.fill( ByteBuffer.allocate( 200 ) ) ;
        accumulator.drain( 10 ) ;
        assertEquals( 10, accumulator.getCapacity() ) ;
        assertEquals( 10, accumulator.getRemainingSpace() ) ;
    }


    public void testDrainInt2()
    {
        accumulator.fill( ByteBuffer.allocate( 20 ) ) ;
        accumulator.drain( 10 ) ;
        assertEquals( 10, accumulator.getCapacity() ) ;
        assertEquals( 10, accumulator.getRemainingSpace() ) ;
    }


    public void testEnsureCapacity()
    {
        accumulator.ensureCapacity( 150 ) ;
        accumulator.ensureCapacity( 10 ) ;
        assertEquals( 150, accumulator.getCapacity() ) ;
        assertEquals( 150, accumulator.getRemainingSpace() ) ;
        accumulator.ensureCapacity( 222 ) ;
        assertEquals( 222, accumulator.getCapacity() ) ;
        assertEquals( 222, accumulator.getRemainingSpace() ) ;
    }


    public void testGrowthIncrement()
    {
        int increment = accumulator.getGrowthIncrement() ;
        ByteBuffer buf = ByteBuffer.allocate(
                accumulator.getInitialSize() + 1 ) ;
        buf.position( buf.capacity() ).flip() ;
        accumulator.fill( buf ) ;
        assertEquals( increment + accumulator.getInitialSize(),
                accumulator.getCapacity() ) ;
    }
}