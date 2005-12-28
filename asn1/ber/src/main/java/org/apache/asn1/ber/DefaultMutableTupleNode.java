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


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.asn1.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;


/**
 * The default mutable tuple node.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class DefaultMutableTupleNode implements MutableTupleNode
{
    /** this node's tuple user object */
    private Tuple tuple ;
    /** a list of this node's children */
    private ArrayList children = new ArrayList() ;
    /** this node's parent node */
    private DefaultMutableTupleNode parent ;
    /** this node's accumulated ByteBuffer value chunks */
    private List valueChunks = new ArrayList( 2 ) ;


    /**
     * Creates a node without a parent and without a tuple.
     */
    public DefaultMutableTupleNode()
    {
    }


    /**
     * Creates a node without a parent using a tuple.  If the tuple is primitive
     * and has a non-null last value chunk, that value chunk is added to the
     * list of value chunks.
     *
     * @param tuple the tuple to set for this node
     */
    public DefaultMutableTupleNode( Tuple tuple )
    {
        this.tuple = tuple ;

        if ( tuple.isPrimitive() && tuple.getLastValueChunk() != null )
        {
            valueChunks.add( tuple.getLastValueChunk() );
        }
    }


    /**
     * Creates a node without a parent.
     *
     * @param tuple the tuple to set for this node
     */
    public DefaultMutableTupleNode( Tuple tuple, List valueChunks )
    {
        this.tuple = tuple ;
        this.valueChunks.addAll( valueChunks ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#insert(
     * org.apache.asn1.ber.MutableTupleNode, int)
     */
    public void insert( MutableTupleNode child, int index )
    {
        children.add( index, child ) ;
    }


    /**
     * Adds a child node to the front of the child list.
     *
     * @param child the child to add to the front
     */
    public void addFront( DefaultMutableTupleNode child )
    {
        if ( children.isEmpty() )
        {
            children.add( child ) ;
        }
        else
        {
            children.add( 0, child ) ;
        }
    }


    /**
     * Adds a child node to the end of the child list.
     *
     * @param child the child to add to the end
     */
    public void addLast( DefaultMutableTupleNode child )
    {
        children.add( child ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#remove(int)
     */
    public void remove( int index )
    {
        children.remove( index ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#remove(
     * org.apache.asn1.ber.MutableTupleNode)
     */
    public void remove( MutableTupleNode node )
    {
        children.remove( node ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#removeFromParent()
     */
    public void removeFromParent()
    {
        parent.remove( this ) ;
        parent = null ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#setParent(
     * org.apache.asn1.ber.MutableTupleNode)
     */
    public void setParent( MutableTupleNode newParent )
    {
        if ( parent != null )
        {
            parent.remove( this ) ;
        }

        parent = ( DefaultMutableTupleNode ) newParent ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#getParent()
     */
    public TupleNode getParentTupleNode()
    {
        return parent ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#children()
     */
    public Iterator getChildren()
    {
        return Collections.unmodifiableList( children ).iterator() ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#getChildAt()
     */
    public TupleNode getChildTupleNodeAt( int index )
    {
        return ( TupleNode ) children.get( index ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#getIndex(
     * org.apache.asn1.ber.TupleNode)
     */
    public int getIndex( TupleNode node )
    {
        return children.indexOf( node ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#getChildCount()
     */
    public int getChildCount()
    {
        return children.size() ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#size()
     */
    public int size()
    {
        if ( tuple.isPrimitive() )
        {
            return tuple.size() ;
        }
        else
        {
            int size = tuple.size() ;

            if ( tuple.isIndefinite() )
            {
                TupleNode child = null ;
                for ( int ii = 0; ii < children.size(); ii++ )
                {
                    child = ( TupleNode ) children.get( ii ) ;
                    size += child.size() ;
                }

                if ( child != null )
                {
                    if ( ! child.getTuple().isIndefiniteTerminator() )
                    {
                        size += 2 ;
                    }
                }
                else
                {
                    size += 2 ;
                }
            }

            return size ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#getTuple()
     */
    public Tuple getTuple()
    {
        return tuple ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#setTuple(
     * org.apache.asn1.ber.Tuple)
     */
    public void setTuple( Tuple t )
    {
        tuple = t ;
        valueChunks.clear() ;
    }


    /*
     * (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#setTuple(
     * org.apache.asn1.ber.Tuple, java.util.List)
     */
    public void setTuple( Tuple t, List valueChunks )
    {
        tuple = t ;
        this.valueChunks.clear() ;
        this.valueChunks.addAll( valueChunks ) ;
    }


    /*
     * (non-Javadoc)
     * @see org.apache.asn1.ber.TupleNode#getValueChunks()
     */
    public List getValueChunks()
    {
        return valueChunks ;
    }


    /*
     * (non-Javadoc)
     * @see org.apache.asn1.ber.MutableTupleNode#addValueChunk(
     * java.nio.ByteBuffer)
     */
    public void addValueChunk( ByteBuffer valueChunk )
    {
        valueChunks.add( valueChunk ) ;
    }


    /**
     * Depth first generation of this tlv tuple node's encoded image.
     *
     * @see org.apache.asn1.ber.TupleNode#encode(ByteBuffer)
     */
    public void encode( ByteBuffer dest )
    {
        dest.put( tuple.toEncodedBuffer( this.valueChunks ) ) ;

        if ( tuple.isPrimitive() )
        {
            return ;
        }

        TupleNode child = null ;
        for ( int ii = 0; ii < children.size(); ii++ )
        {
            child = ( TupleNode ) children.get( ii ) ;
            child.encode( dest ) ;
        }

        if ( child != null )
        {
            Tuple childTuple = child.getTuple() ;
            if ( childTuple.isIndefiniteTerminator() )
            {
                return ;
            }
        }

        if ( tuple.isIndefinite() )
        {
            // lay down the termination
            dest.put( ( byte ) 0 ) ;
            dest.put( ( byte ) 0 ) ;
        }
    }


    /*
     * Prinsts some informative information regarding the tlv node.
     *
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer() ;
        buf.append( tuple.getId() ) ;
        buf.append( ' ' ).append( tuple.typeClass ) ;
        buf.append( '[' ).append( tuple.length ).append( ']' ) ;
        buf.append( '[' ).append( new String( tuple.getLastValueChunk().array() ) ) ;
        buf.append( ']' ) ;

        return buf.toString() ;
    }


    /**
     * Generates a depth first traversal of this node.
     *
     * @return a depth first traversal print out for this node
     */
    public String toDepthFirstString()
    {
        StringBuffer buf = new StringBuffer() ;
        printDepthFirst( buf, 0 ) ;
        return buf.toString() ;
    }


    /**
     * Gets the hex encoding of an integer with the most significant
     * bites first.
     *
     * @param val the integer to break up into 4 bytes and hex encode
     * @return the hex encoded 4 bytes of an integer
     */
    private String getHex( int val )
    {
        byte[] bites = new byte[4] ;

        bites[0] = (byte) ( ( val & 0xff000000 ) >> 24 ) ;
        bites[1] = (byte) ( ( val & 0x00ff0000 ) >> 16 ) ;
        bites[2] = (byte) ( ( val & 0x0000ff00 ) >> 8  ) ;
        bites[3] = (byte) (   val & 0x000000ff ) ;

        return new String( Hex.encodeHex( bites ) ) ;
    }


    /**
     * Generates a depth first traversal of this node.
     *
     * @param buf the buffer to capture the traversal into
     * @param level the level down into the tree
     */
    public void printDepthFirst( StringBuffer buf, int level )
    {
        DefaultMutableTupleNode child = null ;
        String levelTab = StringUtils.repeat( "\t", level ) ;

        if ( level != 0 )
        {
            buf.append( "\n" ) ;
        }

        buf.append( levelTab ).append( tuple.getId() ) ;
        buf.append( " [" ).append( "0x" ) ;
        buf.append( getHex( tuple.getRawPrimitiveTag() ) ) ;
        buf.append( ']' ) ;
        buf.append( '[' ).append( tuple.length ).append( ']' ) ;
        for ( int ii = 0; ii < children.size(); ii++ )
        {
            child = ( DefaultMutableTupleNode ) children.get( ii ) ;
            child.printDepthFirst( buf, level + 1 ) ;
        }
    }


    // ------------------------------------------------------------------------
    // java.lang.Object overrides & overloads
    // ------------------------------------------------------------------------


    /**
     * Checks to see if this node and its children equal another exactly.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof DefaultMutableTupleNode )
        {
            return equals( this, ( DefaultMutableTupleNode ) obj ) ;
        }

        return false ;
    }


    /**
     * Checks to see if two nodes equal one another.  The nodes must be exactly
     * the same even in terms of the order of their children and the children
     * of their descendants.
     *
     * @param n1 the first node
     * @param n2 the second node
     * @return true if <code>n1.equals(n2)</code> exactly otherwise false
     */
    public static boolean equals( DefaultMutableTupleNode n1,
                                  DefaultMutableTupleNode n2 )
    {
        if ( n1 == n2 )
        {
            return true ;
        }

        if ( ! n1.getTuple().equals( n2.getTuple() ) )
        {
            return false ;
        }

        if ( n1.getChildCount() != n2.getChildCount() )
        {
            return false ;
        }

        DefaultMutableTupleNode n1Child = null ;
        DefaultMutableTupleNode n2Child = null ;
        for ( int ii = 0; ii < n1.getChildCount() ; ii++ )
        {
            n1Child = ( DefaultMutableTupleNode  )
                n1.getChildTupleNodeAt( ii ) ;
            n2Child = ( DefaultMutableTupleNode  )
                n2.getChildTupleNodeAt( ii ) ;

            if ( ! equals( n1Child, n2Child ) )
            {
                return false ;
            }
        }

        return true ;
    }


    /**
     * Recursively descends the tree at this node based on the order of the
     * visitor.
     *
     * @see TupleNode#accept(TupleNodeVisitor)
     */
    public void accept( TupleNodeVisitor visitor )
    {
        if ( visitor.canVisit( this ) )
        {
            if ( visitor.isPrefix() )
            {
                ArrayList l_children = visitor.getOrder( this, children ) ;

                if ( visitor.canVisit( this ) )
                {
                    visitor.visit( this ) ;
                }

                for ( int ii = 0; ii < l_children.size(); ii++ )
                {
                    ( ( TupleNode ) l_children.get( ii ) ).accept( visitor ) ;
                }
            }
            else
            {
                ArrayList l_children = visitor.getOrder( this, children ) ;

                for ( int ii = 0; ii < l_children.size(); ii++ )
                {
                    ( ( TupleNode ) l_children.get( ii ) ).accept( visitor ) ;
                }

                if ( visitor.canVisit( this ) )
                {
                    visitor.visit( this ) ;
                }
            }
        }
    }
}
