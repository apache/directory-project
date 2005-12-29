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
package org.apache.ldap.common.berlib.asn1.decoder ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.ldap.common.berlib.asn1.LdapMessageFactory;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.LdapResultImpl;
import org.apache.ldap.common.message.ResultResponse;


/**
 * A rule base class used to build LDAPv3 response messages carrying operation
 * result information.  This PDU class is referred to as a ResultResponses.
 * This rule creates one and places the message on the object stack.  This rule
 * performs the following tasks in the firing stage method specified:
 *
 * <ul>
 *   <li>tag(): pops the primitive int stack to get the messageId</li>
 *   <li>tag(): instantiates a ResultResponse message with the messageId</li>
 *   <li>tag(): populates the ResultResponse with a new LdapResult object</li>
 *   <li>tag(): pushes the ResultResponse message onto the object stack</li>
 *   <li>finish(): pops object stack if top object is a ResultResponse</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class ResultResponseRule extends AbstractRule
{
    /** the LdapTag associated with the message type */
    private final LdapTag tag ;


    /**
     * Creates a ResultResponse instantiating rule for a specific LDAP
     * APPLICTION tag.
     *
     * @param tag the ASN.1 application tag for the LDAP message type
     */
    protected ResultResponseRule( LdapTag tag )
    {
        this.tag = tag ;
    }


    /**
     * Rule firing stage called first when a PDU response with a result is
     * encountered.  The tag id must equal the tag id of this Rule.  If the
     * tag id matches then the following tasks are performed in this stage:
     *
     * <ul>
     *   <li>pops the primitive int stack to get the messageId</li>
     *   <li>instantiates a ResultResponse message with the messageId</li>
     *   <li>populates the ResultResponse with a new LdapResult object</li>
     *   <li>pushes the ResultResponse message onto the object stack</li>
     * </ul>
     *
     * @throws IllegalStateException if the id does not eqaul tag.getTagId()
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        if ( tag.getTagId() != id )
        {
            throw new IllegalStateException( "Rule firing stage method "
                    + this + ".tag() expected an id of " + tag.getTagId()
                    + " for tag " + tag + " but instead encountered a tag id "
                    + " of " + id ) ;
        }

        ResultResponse resp ;
        super.tag( id, isPrimitive, typeClass ) ;

        // pop the message id off of the int stack
        int messageId = getDigester().popInt() ;

        // create the ResultResponse object
        resp = ( ResultResponse ) LdapMessageFactory.create( id, messageId ) ;

        // instantiate an set the LdapResult object
        resp.setLdapResult( new LdapResultImpl( resp ) ) ;

        // push the pdu onto the object stack
        getDigester().push( resp ) ;
    }



    /**
     * Final rule firing stage method which checks to see if the top of the
     * object stack is an instance of ResultResponse, if so the object is
     * popped.
     *
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        if ( getDigester().peek() instanceof ResultResponse )
        {
            getDigester().pop() ;
        }
    }
}
