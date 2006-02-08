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
import java.util.List;


/**
 * A mutable TupleNode used for building TLV Tuple trees.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public interface MutableTupleNode extends TupleNode
{
    /**
     * Adds child to the receiver at index.
     * 
     * @param child the child to add 
     * @param index the index at which to insert the child
     */
    void insert( MutableTupleNode child, int index ) ; 
              
    /**
     * Removes the child at index from the receiver.
     * 
     * @param index the index at which to remove the child
     */
    void remove( int index ) ;
              
    /**
     * Removes node from the receiver. 
     * 
     * @param node the node to remove
     */
     void remove( MutableTupleNode node ) ; 
              
     /**
      * Removes the receiver from its parent.
      */
     void removeFromParent() ; 
               
     /**
      * Sets the parent of the receiver to newParent.
      * 
      * @param newParent the new parent to set
      */
     void setParent( MutableTupleNode newParent ) ; 
              
    /**
     * Resets the Tuple of the receiver object.  Also clears the value chunk 
     * buffers accumulated for the previous tuple if any.
     * 
     * @param t the tuple to set for this node
     */
    void setTuple( Tuple t ) ;  
     
    /**
     * Resets the Tuple of the receiver object.  Also clears the value chunk 
     * buffers accumulated for the previous tuple if any.
     * 
     * @param t the tuple to set for this node
     * @param valueChunks the list of value chunk buffers
     */
    void setTuple( Tuple t, List valueChunks ) ;  
     
     /**
      * Adds a buffer containing the entire buffer or a chunked peice of it.
      *
      * @param valueChunk a chunk of the value as a byte buffer
      */
     void addValueChunk( ByteBuffer valueChunk ) ;
}
