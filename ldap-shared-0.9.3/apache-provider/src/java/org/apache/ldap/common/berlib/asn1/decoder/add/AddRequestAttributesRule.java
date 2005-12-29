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
package org.apache.ldap.common.berlib.asn1.decoder.add ;


import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.message.AddRequestImpl;
import org.apache.ldap.common.message.LockableAttributesImpl;


/**
 * Document this class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class AddRequestAttributesRule extends AbstractRule
{
    /** the tag this rule expects to encounter */
    private final TagEnum expected ;


    /**
     * Creates a new AddRequestAttributesRule using an expected tag to enforce.
     *
     * @param expected the tag the rule must encounter to fire properly
     */
    public AddRequestAttributesRule( TagEnum expected )
    {
        this.expected = expected ;
    }


    /**
     * Creates a new AddRequestAttributesRule that expects a SEQUENCE tag.
     */
    public AddRequestAttributesRule()
    {
        this.expected = UniversalTag.SEQUENCE_SEQUENCE_OF ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass ) ;

        if ( id != expected.getTagId() )
        {
            throw new IllegalArgumentException( "expected tag with id "
                    + expected.getTagId() + " for " + expected
                    + " but got " + id + "instead" ) ;
        }

        AddRequestImpl req = ( AddRequestImpl ) getDigester().peek() ;
        LockableAttributesImpl attrs = new LockableAttributesImpl( req ) ;
        req.setEntry( attrs ) ;
        getDigester().push( attrs ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;

        getDigester().pop() ;
    }
}
