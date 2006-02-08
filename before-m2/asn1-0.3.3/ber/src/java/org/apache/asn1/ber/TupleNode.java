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


import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;


/**
 * A TLV Tuple tree node modeled in the likeness of a TreeNode.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public interface TupleNode
{
    /**
     * Gets the parent tuple node to this node or null if a parent does not 
     * exist.  The analogous method on the <code>TreeNode</code> interface 
     * would be <code>getParent()</code>.
     * 
     * @return the parent node or null if one does not exist
     */
    TupleNode getParentTupleNode() ;
    
    /**
     * Gets an iterator over this node's children.  The analogous interface on
     * the <code>TreeNode</code> interface would be <code>children</code> which
     * returns an <code>Enumeration</code> instead of an <code>Iterator</code>.
     *  
     * @return an iterator over this node's children
     */
    Iterator getChildren() ;
    
    /**
     * Gets a tuple node at an index.  The analogous interface on <code>TreeNode
     * </code> would be the <code>getChildAt</code> method.
     * 
     * @param index the index of the child to get
     * @return the child node at the specified index
     */
    TupleNode getChildTupleNodeAt( int index ) ;
    
    /**
     * Gets the chunked value buffer fragments collected within this node.  
     * 
     * @return the value buffer parts for this node
     */
    List getValueChunks() ;
    
    /**
     * Gets the index of a child if the child node if it exists.  The analog
     * within <code>TreeNode<code> takes a <code>TreeNode</code> instead of a 
     * <code>TupleNode</code>.
     * 
     * @param node the child node to get the index for
     * @return the index of the child node or -1 if the node does not exist
     */
    int getIndex( TupleNode node ) ;
    
    /**
     * Gets the number of child nodes contained.  This is the same as in 
     * <code>TreeNode.getChildCount()</code> as well.
     * 
     * @return the number of child nodes contained.
     */
    int getChildCount() ;
    
    /**
     * Gets the number of child nodes contained.  This is the same as in 
     * <code>TreeNode.size()</code> as well.
     * 
     * @return the number of children
     */
    int size() ;
    
    /**
     * Gets the Tuple this node represents.   This is the analogous to <code>
     * TreeNode.getUserObject()</code>.
     * 
     * @return the tuple this node represents or null if one has not been 
     * assigned
     */
    Tuple getTuple() ;
    
    /**
     * Recursively encodes the tree rooted at this node. 
     * 
     * @param buf the buffer populated with the BER encoded tlv tree contents
     */
    void encode( ByteBuffer buf ) ;
    
    /**
     * Checks to see if two trees are equal.  Note that the order of children
     * within the tree as well as the tuples and their contents make a 
     * difference to <code>equals()</code>.
     * 
     * @param obj the object to compare this node to
     * @return true if the obj and this node are exact replicas of one another
     */
    boolean equals( Object obj ) ;

    /**
     * Element/node accept method for visitor pattern.
     *
     * @param visitor the tuple node tree structure visitor
     * @see TupleNodeVisitor
     */
    void accept( TupleNodeVisitor visitor ) ;
}
