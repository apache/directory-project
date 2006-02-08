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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A speed (verses size) optimized data structure to match tag patterns.  
 * As tuples are pushed and popped off of the decoder's stack and the tag 
 * nesting path changes this tree is traversed.  A position member of type
 * TagNode is used to track the current position in this tree.  If the nesting
 * does not correspond to a valid node then it is null and thus underfined so
 * no rules are correlated with the position.  When a node is located and set
 * the rules contained in that node are triggered.
 *   
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TagNode
{
    private Integer tag ;
    private int depth ;
    private HashMap children = new HashMap( 3 ) ;
    private ArrayList rules = new ArrayList( 3 ) ;
    
    
    TagNode( Integer tag )
    {
        this.tag = tag ;
    }
    
    
    void addNode( TagNode node )
    {
        children.put( node.getTag(), node ) ;
        node.setDepth( depth + 1 ) ;
    }
    
    
    void addRule( Rule rule )
    {
        rules.add( rule ) ;
    }
    
    
    void setDepth( int depth )
    {
        this.depth = depth ;
    }
    
    
    public Integer getTag() 
    {
        return tag ;
    }
    
    
    public int getDepth()
    {
        return depth ;
    }
    
    
    public List getRules()
    {
        return rules ;
    }
    
    
    public boolean hasChild( Integer tag )
    {
        return children.containsKey( tag ) ;
    }


    public boolean isLeaf()
    {
        return children.isEmpty() ;
    }


    public Iterator getChildren()
    {
        return children.values().iterator() ;
    }


    public TagNode getChild( Integer tag )
    {
        return ( TagNode ) children.get( tag ) ;
    }
}
