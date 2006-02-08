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
package org.apache.asn1.ber;




/**
 * A callback interface used to monitor the activities of a tuple node visitor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public interface VisitorMonitor
{
    /** The do nothing visitor monitor */
    VisitorMonitor NOOP = new VisitorMonitor() {
        public void visited( TupleNodeVisitor v, TupleNode n ) {} };

    /**
     * Notifies that a node has been visited.
     *
     * @param visitor the visitor visiting the node
     * @param node the not that has already been visited
     */
    void visited( TupleNodeVisitor visitor, TupleNode node );
}
