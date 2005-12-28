/*
 *   Copyright 2004-2005 The Apache Software Foundation
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


import java.util.Collections;
import java.util.Iterator ;

import java.nio.ByteBuffer ;

import org.apache.asn1.ber.AbstractDecoderTestCase;
import org.apache.asn1.ber.DefaultMutableTupleNode;


/**
 * Tests the default MutableTupleNode implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class DefaultMutableTupleNodeTest extends AbstractDecoderTestCase
{
    public DefaultMutableTupleNodeTest()
    {
        super ( DefaultMutableTupleNodeTest.class.getName() ) ;
    }


    /*
     * Class to test for void insert(MutableTupleNode, int)
     */
    public void testInsertMutableTupleNodeint()
    {
    }

    /*
     * Class to test for void remove(int)
     */
    public void testRemoveint()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        assertEquals(1,parent.getChildCount()) ;
        parent.remove( 0 ) ;
        assertEquals(0,parent.getChildCount()) ;
    }

    /*
     * Class to test for void remove(MutableTupleNode)
     */
    public void testRemoveMutableTupleNode()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        assertEquals(1,parent.getChildCount()) ;
        parent.remove( (MutableTupleNode) node ) ;
        assertEquals(0,parent.getChildCount()) ;

        parent.insert( node, 0 ) ;
        assertEquals(1,parent.getChildCount()) ;
        parent.remove( node ) ;
        assertEquals(0,parent.getChildCount()) ;
    }

    public void testRemoveFromParent()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;

        assertEquals(1,parent.getChildCount()) ;
        node.removeFromParent() ;
        assertEquals(0,parent.getChildCount()) ;
    }

    /*
     * Class to test for void setParent(MutableTupleNode)
     */
    public void testSetParentMutableTupleNode()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;
        assertEquals(parent, node.getParentTupleNode()) ;
        node.setParent((MutableTupleNode)new DefaultMutableTupleNode()) ;
        assertNotSame(parent, node.getParentTupleNode()) ;
    }

    /*
     * Class to test for void setParent(MutableTupleNode)
     */
    public void testSetParentMutableTreeNode()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( node, 0 ) ;
        node.setParent( parent) ;
        assertEquals(parent, node.getParentTupleNode()) ;
        node.setParent( new DefaultMutableTupleNode()) ;
        assertNotSame( parent, node.getParentTupleNode() ) ;
    }

    public void testSetTuple()
    {
        Tuple t = new Tuple() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        node.setTuple( t ) ;
        assertEquals(t, node.getTuple()) ;
    }

    public void testGetParentTupleNode()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;
        assertSame(parent, node.getParentTupleNode()) ;
        assertSame(parent, node.getParentTupleNode()) ;
    }

    public void testGetChildren()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;
        assertSame(parent, node.getParentTupleNode()) ;
        assertTrue( parent.getChildren().hasNext()) ;
    }


    public void testGetChildTupleNodeAt()
    {
    }

    /*
     * Class to test for int getIndex(TupleNode)
     */
    public void testGetIndexTupleNode()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;
        assertEquals( 0, parent.getIndex((MutableTupleNode)node));
        assertEquals( 0, parent.getIndex(node));
    }

    public void testGetChildCount()
    {
    }

    public void testSize()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;

        // fact that we use the indefinite form we automatically add two to
        // the contained size
        assertEquals(9, parent.size()) ;
    }

    public void testSize2()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, 5, TypeClass.APPLICATION ) ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;

        assertEquals(7, parent.size()) ;
    }

    public void testSize3()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        parent.setTuple( constructed ) ;

        assertEquals(4, parent.size()) ;
    }

    public void testEncode3() throws Exception
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        parent.setTuple( constructed ) ;
        assertEquals(4, parent.size()) ;
        ByteBuffer buf = ByteBuffer.allocate( 4 ) ;
        parent.encode( buf ) ;


        /*
        final ArrayList list = new ArrayList() ;
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        decoder.setCallback( new DecoderCallback(){
            public void decodeOccurred(StatefulDecoder arg0, Object arg1)
            {
                list.add( arg1 ) ;
            }}) ;

        decoder.decode(buf.flip()) ;
        DefaultMutableTupleNode decoded = ( DefaultMutableTupleNode )
            list.get(0);
        assertEquals( decoded.getTuple(), parent.getTuple()) ;
        */
    }

    public void testGetTuple()
    {
        Tuple t = new Tuple() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        node.setTuple(t) ;
        assertSame(t, node.getTuple()) ;
    }

    public void testEncode0() throws Exception
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;

        DefaultMutableTupleNode end = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) end, 0 ) ;
        end.setParent( (MutableTupleNode) parent) ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        terminator.typeClass = TypeClass.UNIVERSAL ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;
        end.setTuple(terminator) ;

        assertEquals(9, parent.size()) ;

        ByteBuffer buf = ByteBuffer.allocate( 9 ) ;
        parent.encode( buf ) ;

        /**
        final ArrayList list = new ArrayList() ;
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        decoder.setCallback( new DecoderCallback(){
            public void decodeOccurred(StatefulDecoder arg0, Object arg1)
            {
                list.add( arg1 ) ;
            }}) ;

        decoder.decode(buf.flip()) ;
        DefaultMutableTupleNode decoded = ( DefaultMutableTupleNode )
            list.get(0);
        assertEquals( decoded.getTuple(), parent.getTuple()) ;
        */
    }

    public void testEncode1() throws Exception
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        parent.insert( (MutableTupleNode) node, 0 ) ;
        node.setParent( (MutableTupleNode) parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;

        // 2 extra bytes added for indefinite form
        assertEquals(9, parent.size()) ;

        ByteBuffer buf = ByteBuffer.allocate( 9 ) ;
        parent.encode( buf ) ;

        /*
        final ArrayList list = new ArrayList() ;
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        decoder.setCallback( new DecoderCallback(){
            public void decodeOccurred(StatefulDecoder arg0, Object arg1)
            {
                list.add( arg1 ) ;
            }}) ;

        decoder.decode(buf.flip()) ;
        DefaultMutableTupleNode decoded = ( DefaultMutableTupleNode )
            list.get(0);
        assertEquals( decoded.getTuple(), parent.getTuple()) ;
        */
    }

    public void testEncode2() throws Exception
    {
        DefaultMutableTupleNode top = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode middle = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode bottom = new DefaultMutableTupleNode() ;
        top.insert( (MutableTupleNode) middle, 0 ) ;
        middle.setParent( (MutableTupleNode) top) ;
        middle.insert( (MutableTupleNode) bottom, 0 ) ;
        bottom.setParent( (MutableTupleNode) middle) ;

        Tuple middleTuple = new Tuple( 1, 3, TypeClass.APPLICATION  ) ;
        Tuple topTuple = new Tuple ( 2, 5, TypeClass.APPLICATION ) ;
        Tuple bottomTuple = new Tuple( 3, 1, true, TypeClass.APPLICATION ) ;

        bottomTuple.typeClass = TypeClass.UNIVERSAL ;
        top.setTuple( topTuple ) ;
        middle.setTuple( middleTuple ) ;
        bottom.setTuple( bottomTuple ) ;

        assertEquals(7, top.size()) ;

        ByteBuffer buf = ByteBuffer.allocate( 7 ) ;
        top.encode( buf ) ;

        /*
        final ArrayList list = new ArrayList() ;
        TupleTreeDecoder decoder = new TupleTreeDecoder() ;
        decoder.setCallback( new DecoderCallback(){
            public void decodeOccurred(StatefulDecoder arg0, Object arg1)
            {
                list.add( arg1 ) ;
            }}) ;

        decoder.decode(buf.flip()) ;
        DefaultMutableTupleNode decoded = ( DefaultMutableTupleNode )
            list.get(0);
        assertEquals( decoded.getTuple(), top.getTuple()) ;
        */
    }


    public void testEquals()
    {
        DefaultMutableTupleNode top = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode middle = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode bottom = new DefaultMutableTupleNode() ;
        top.insert( (MutableTupleNode) middle, 0 ) ;
        middle.setParent( (MutableTupleNode) top) ;
        middle.insert( (MutableTupleNode) bottom, 0 ) ;
        bottom.setParent( (MutableTupleNode) middle) ;

        Tuple middleTuple = new Tuple( 1, 3, TypeClass.APPLICATION  ) ;
        Tuple topTuple = new Tuple ( 2, 5, TypeClass.APPLICATION ) ;
        Tuple bottomTuple = new Tuple( 3, 1, true, TypeClass.APPLICATION ) ;

        bottomTuple.typeClass = TypeClass.UNIVERSAL ;
        top.setTuple( topTuple ) ;
        middle.setTuple( middleTuple ) ;
        bottom.setTuple( bottomTuple ) ;

        assertTrue( top.equals( top ) ) ;

        DefaultMutableTupleNode topClone = new DefaultMutableTupleNode() ;
        topClone.setTuple( topTuple ) ;
        assertFalse( top.equals(topClone)) ;

        topClone = new DefaultMutableTupleNode() ;
        topClone.setTuple( bottomTuple ) ;
        assertFalse( top.equals(topClone)) ;

        topClone = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode middleClone = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode bottomClone = new DefaultMutableTupleNode() ;
        topClone.insert( (MutableTupleNode) middleClone, 0 ) ;
        middleClone.setParent( (MutableTupleNode) topClone) ;
        middleClone.insert( (MutableTupleNode) bottomClone, 0 ) ;
        bottomClone.setParent( (MutableTupleNode) middleClone) ;

        Tuple middleTupleClone = new Tuple( 1, 3, TypeClass.APPLICATION  ) ;
        Tuple topTupleClone = new Tuple ( 2, 5, TypeClass.APPLICATION ) ;
        Tuple bottomTupleClone= new Tuple( 3, 1, true, TypeClass.APPLICATION ) ;

        bottomTupleClone.typeClass = TypeClass.UNIVERSAL ;
        topClone.setTuple( topTupleClone ) ;
        middleClone.setTuple( middleTupleClone ) ;
        bottomClone.setTuple( bottomTupleClone ) ;

        assertTrue( bottom.equals( bottomClone ) ) ;
        assertTrue( middle.equals( middleClone ) ) ;
        assertTrue( top.equals( topClone ) ) ;
     }


    public void testEquals2()
    {
        DefaultMutableTupleNode top = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode middle = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode bottom = new DefaultMutableTupleNode() ;
        top.insert( (MutableTupleNode) middle, 0 ) ;
        middle.setParent( (MutableTupleNode) top) ;
        middle.insert( (MutableTupleNode) bottom, 0 ) ;
        bottom.setParent( (MutableTupleNode) middle) ;

        Tuple middleTuple = new Tuple( 1, 3, TypeClass.APPLICATION  ) ;
        Tuple topTuple = new Tuple ( 2, 5, TypeClass.APPLICATION ) ;
        Tuple bottomTuple = new Tuple( 3, 1, true, TypeClass.APPLICATION ) ;

        bottomTuple.typeClass = TypeClass.UNIVERSAL ;
        top.setTuple( topTuple ) ;
        middle.setTuple( middleTuple ) ;
        bottom.setTuple( bottomTuple ) ;

        assertTrue( top.equals( top ) ) ;

        top.printDepthFirst(new StringBuffer(), 0 ) ;

        DefaultMutableTupleNode topClone = new DefaultMutableTupleNode() ;
        topClone.setTuple( topTuple ) ;
        assertFalse( top.equals(topClone)) ;

        topClone = new DefaultMutableTupleNode() ;
        topClone.setTuple( bottomTuple ) ;
        assertFalse( top.equals(topClone)) ;

        topClone = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode middleClone = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode bottomClone = new DefaultMutableTupleNode() ;
        topClone.insert( (MutableTupleNode) middleClone, 0 ) ;
        middleClone.setParent( (MutableTupleNode) topClone) ;
        middleClone.insert( (MutableTupleNode) bottomClone, 0 ) ;
        bottomClone.setParent( (MutableTupleNode) middleClone) ;

        Tuple middleTupleClone = new Tuple( 1, 3, TypeClass.APPLICATION  ) ;
        Tuple topTupleClone = new Tuple ( 2, 5, TypeClass.APPLICATION ) ;
        Tuple bottomTupleClone= new Tuple( 3, 1, true, TypeClass.APPLICATION ) ;

        bottomTupleClone.typeClass = TypeClass.UNIVERSAL ;
        topClone.setTuple( topTupleClone ) ;
        middleClone.setTuple( bottomTupleClone ) ;
        bottomClone.setTuple( middleTupleClone ) ;

        assertFalse( bottom.equals( bottomClone ) ) ;
        assertFalse( middle.equals( middleClone ) ) ;
        assertFalse( top.equals( topClone ) ) ;
        assertFalse( top.equals( new Object() ) ) ;
        assertFalse( top.equals( null ) ) ;
     }


    /*
     * Class to test for String toString()
     */
    public void testToString()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode end = new DefaultMutableTupleNode() ;
        parent.insert( end, 0 ) ;
        end.setParent( parent) ;
        parent.insert( node, 0 ) ;
        node.setParent( parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        terminator.typeClass = TypeClass.UNIVERSAL ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;
        end.setTuple( terminator ) ;

        assertNotNull( parent.toString() ) ;
    }

    public void testSetTupleTupleList()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode end = new DefaultMutableTupleNode() ;
        parent.insert( end, 0 ) ;
        end.setParent( parent) ;
        parent.insert( node, 0 ) ;
        node.setParent( parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        terminator.typeClass = TypeClass.UNIVERSAL ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive, Collections.EMPTY_LIST ) ;
        end.setTuple( terminator ) ;

        assertTrue( node.getValueChunks().isEmpty() ) ;


        node.addValueChunk( ByteBuffer.allocate( 3 ) ) ;
        assertFalse( node.getValueChunks().isEmpty() ) ;
    }

    public void testChildren()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode end = new DefaultMutableTupleNode() ;
        parent.insert( end, 0 ) ;
        end.setParent( parent) ;
        parent.insert( node, 0 ) ;
        node.setParent( parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        terminator.typeClass = TypeClass.UNIVERSAL ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;
        end.setTuple( terminator ) ;

        Iterator list = parent.getChildren() ;

        assertEquals( node, list.next() ) ;
        assertEquals( end, list.next() ) ;

        try
        {
            list.next() ;
            fail( "should never get here due to thrown exception" ) ;
        }
        catch( Throwable t )
        {
            assertNotNull( t ) ;
        }
    }

    public void testGetAllowsChildren()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode end = new DefaultMutableTupleNode() ;
        parent.insert( end, 0 ) ;
        end.setParent( parent) ;
        parent.insert( node, 0 ) ;
        node.setParent( parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        terminator.typeClass = TypeClass.UNIVERSAL ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;
        end.setTuple( terminator ) ;
    }

    public void testAddFirst()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode end = new DefaultMutableTupleNode() ;
        parent.addFront( end ) ;
        end.setParent( parent) ;
        parent.addFront( node ) ;
        node.setParent( parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        terminator.typeClass = TypeClass.UNIVERSAL ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;
        end.setTuple( terminator ) ;
        parent.toDepthFirstString() ;
    }


    public void testAddLast()
    {
        DefaultMutableTupleNode parent = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode node = new DefaultMutableTupleNode() ;
        DefaultMutableTupleNode end = new DefaultMutableTupleNode() ;
        parent.addFront( node ) ;
        node.setParent( (MutableTupleNode) parent) ;
        parent.addLast( end ) ;
        end.setParent( (MutableTupleNode) parent) ;

        Tuple primitive = new Tuple( 1, 3, true, TypeClass.APPLICATION ) ;
        Tuple constructed = new Tuple ( 2, TypeClass.APPLICATION ) ;
        Tuple terminator = new Tuple( 0, 0, true, TypeClass.UNIVERSAL ) ;
        terminator.typeClass = TypeClass.UNIVERSAL ;
        parent.setTuple( constructed ) ;
        node.setTuple( primitive ) ;
        end.setTuple(terminator) ;
    }
}
