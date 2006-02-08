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


import java.nio.ByteBuffer;

import org.apache.asn1.ber.TypeClass;


/**
 * A rule base class.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class AbstractRule implements Rule
{
    private BERDigester digester = null ;

    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rule#getDigester()
     */
    public BERDigester getDigester()
    {
        return digester ;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.rulesBase.Rule#setDigester(
     * org.apache.snickers.ber.rulesBase.BERDigester)
     */
    public void setDigester( BERDigester digester )
    {
        this.digester = digester ;
    }

    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int, boolean, 
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        // do nothing base class
    }

    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#length(int)
     */
    public void length( int length )
    {
        // do nothing base class
    }

    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#value(java.nio.ByteBuffer)
     */
    public void value( ByteBuffer buf )
    {
        // do nothing base class
    }

    
    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        // do nothing base class
    }
}
