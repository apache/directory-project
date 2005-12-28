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
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.primitives.IntStack;


/**
 * A base Rules implementation using a fast pattern match.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class RulesBase implements Rules
{
    private TagTree tagTree ;
    private ArrayList rules ;
    private BERDigester digester ;
    
    
    /**
     * Creates a base Rules instance.
     */
    public RulesBase()
    {
        tagTree = new TagTree() ;
        rules = new ArrayList() ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rules#setDigester(
     * org.apache.snickers.ber.rulesBase.BERDigester)
     */
    public void setDigester( BERDigester digester )
    {
        this.digester = digester ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rules#getDigester()
     */
    public BERDigester getDigester()
    {
        return digester ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rules#add(int[],
     * org.apache.snickers.ber.rulesBase.Rule)
     */
    public void add( int[] pattern, Rule rule )
    {
        tagTree.addRule( pattern, rule ) ;
        rules.add( rule ) ;
    }

    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rules#clear()
     */
    public void clear()
    {
        tagTree = new TagTree() ;
        rules.clear() ;
    }
    

    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rules#match(int[])
     */
    public List match( int[] pattern )
    {
        return tagTree.match( new IntStack( pattern ) ) ;
    }
    

    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rules#match(int[])
     */
    public List match( IntStack pattern )
    {
        return tagTree.match( pattern ) ;
    }
    

    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rules#rules()
     */
    public List rules()
    {
        return Collections.unmodifiableList( rules ) ;
    }
}
