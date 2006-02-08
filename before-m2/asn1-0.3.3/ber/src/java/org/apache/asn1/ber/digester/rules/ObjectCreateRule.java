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
package org.apache.asn1.ber.digester.rules ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.asn1.ber.digester.BERDigester;
import org.apache.commons.lang.exception.NestableRuntimeException;


/**
 * Rule implementation that creates a new object and pushes it onto the
 * object stack when a TLV is encountered.  When the TLV is complete,
 * the object will be popped off of the stack.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Direclectory Project</a>
 * @version $Rev$
 */
public class ObjectCreateRule extends AbstractRule
{
    /** the class of object to instantiate and push */
    private final Class clazz ;


    /**
     * Creates a rule that creates an instance of an object when the tag
     *
     * @param clazz the class to create an instance of.
     */
    public ObjectCreateRule( BERDigester digester, Class clazz )
    {
        this.clazz = clazz ;
        setDigester( digester ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        try
        {
            Object obj = clazz.newInstance() ;
            getDigester().push( obj ) ;
        }
        catch ( InstantiationException e )
        {
            throw new NestableRuntimeException( e ) ;
        }
        catch ( IllegalAccessException e )
        {
            throw new NestableRuntimeException( e ) ;
        }
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        getDigester().pop() ;
    }
}

