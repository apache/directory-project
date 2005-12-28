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
package org.apache.asn1.ber;


import java.util.ArrayList;
import java.util.Iterator;


/**
 * A visitor used to transform a TLV tuple tree by altering tuples to use
 * determinate length encodings rather than the indeterminate form.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class DeterminateLengthVisitor implements TupleNodeVisitor
{
    /** the visitor monitor called when notible events occur */
    private VisitorMonitor monitor = VisitorMonitor.NOOP;


    /**
     * Visits a tree of tuple nodes using a specific visitation order.
     *
     * @param node the node to visit
     */
    public void visit( TupleNode node )
    {
        /*
         * Because of a recursive depth first descent driving the
         * calculation of indeterminate child sizes we just need to
         * add the sizes of all the child nodes to find the determinate
         * length and set it for the tuple.
         */
        int length = 0;
        Iterator children = node.getChildren();
        while ( children.hasNext() )
        {
            TupleNode childTuple = ( TupleNode ) children.next();
            Tuple tlv = childTuple.getTuple() ;

            /*
             * The tuple node may have child tuple nodes that are indefinite
             * terminator nodes.  When converting to the definate length form
             * these tuples must be detached from the tree and NOT factored
             * into length computations.
             */
            if ( tlv.isIndefiniteTerminator() )
            {
                // setting the parent to null removes it from the
                // parent's child list
                ( ( MutableTupleNode ) childTuple ).setParent( null );
            }
            else
            {
                length += tlv.size();
            }
        }

        node.getTuple().setValueLength( length );
        monitor.visited( this, node );
    }


    /**
     * Checks to see if a node can be visited.
     *
     * @param node the node to be visited
     * @return whether or node the node should be visited
     */
    public boolean canVisit( TupleNode node )
    {
        return node.getTuple().isIndefinite();
    }


    /**
     * Determines whether the visitation order is prefix or postfix.
     *
     * @return true if the visitation is in prefix order, false otherwise.
     */
    public boolean isPrefix()
    {
        return false;
    }


    /**
     * Get the array of children to visit sequentially to determine the order of
     * child visitations.  Some children may not be returned at all if
     * canVisit() returns false on them.
     *
     * @param node     the parent branch node
     * @param children the child node array
     * @return the new reordered array of children
     */
    public ArrayList getOrder( TupleNode node, ArrayList children )
    {
        return children;
    }


    public void setMonitor( VisitorMonitor monitor )
    {
        this.monitor = monitor;
    }
}
