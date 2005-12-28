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
package org.apache.asn1.ber.digester;


import junit.framework.*;
import org.apache.asn1.ber.digester.TagNode;


/**
 * Unit test for the TagNode class.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TagNodeTest extends TestCase
{
    TagNode n0 = null ;
    TagNode n1 = null ;
    TagNode n2 = null ;


    public void setUp() throws Exception
    {
        super.setUp() ;

        n0 = new TagNode( new Integer( 0 ) ) ;
        n1 = new TagNode( new Integer( 1 ) ) ;
        n2 = new TagNode( new Integer( 2 ) ) ;
    }

    /**
     * Tests the TagNode.getDepth() method.
     */
    public void testDepth()
    {
        assertEquals( 0, n0.getDepth() ) ;
        n0.addNode( n1 ) ;
        assertEquals( 0, n0.getDepth() ) ;
        assertEquals( 1, n1.getDepth() ) ;
        n1.addNode( n2 ) ;
        assertEquals( 0, n0.getDepth() ) ;
        assertEquals( 1, n1.getDepth() ) ;
        assertEquals( 2, n2.getDepth() ) ;
    }


    /**
     * Tests the TagNode.hasChildren() method.
     */
    public void testHasChildren()
    {
        assertFalse( n0.hasChild( new Integer( 1 ) ) ) ;
        n0.addNode( n1 ) ;
        assertTrue( n0.hasChild( new Integer( 1 ) ) ) ;
    }
}