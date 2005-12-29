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
package org.apache.ldap.common.berlib.asn1 ;


import org.apache.ldap.common.message.AbandonRequestImpl;
import org.apache.ldap.common.message.AddRequestImpl;
import org.apache.ldap.common.message.AddResponseImpl;
import org.apache.ldap.common.message.BindRequestImpl;
import org.apache.ldap.common.message.BindResponseImpl;
import org.apache.ldap.common.message.CompareRequestImpl;
import org.apache.ldap.common.message.CompareResponseImpl;
import org.apache.ldap.common.message.DeleteRequestImpl;
import org.apache.ldap.common.message.DeleteResponseImpl;
import org.apache.ldap.common.message.ExtendedRequestImpl;
import org.apache.ldap.common.message.ExtendedResponseImpl;
import org.apache.ldap.common.message.Message;
import org.apache.ldap.common.message.ModifyDnRequestImpl;
import org.apache.ldap.common.message.ModifyDnResponseImpl;
import org.apache.ldap.common.message.ModifyRequestImpl;
import org.apache.ldap.common.message.ModifyResponseImpl;
import org.apache.ldap.common.message.SearchRequestImpl;
import org.apache.ldap.common.message.SearchResponseDoneImpl;
import org.apache.ldap.common.message.SearchResponseEntryImpl;
import org.apache.ldap.common.message.SearchResponseReferenceImpl;
import org.apache.ldap.common.message.UnbindRequestImpl;


/**
 * A static factory for creating LDAPv3 Message objects based on the LDAP tag
 * of the specific LDAP message PDU.  The factory is static because its all
 * hard coded as a switch on constants - it makes no sense to deal with
 * instance methods or factory instances.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public final class LdapMessageFactory
{
    /**
     * Creates an empty LDAP Message instance whose types corresponds to the
     * tagId provided.  The only field that is populated is the final message
     * identifier.
     *
     * @param tag the LDAPv3 APPLICATION tag for the PDU type
     * @param messageId the unique identifier for messages within a session
     * @return an empty LDAP Message (PDU) instance
     */
    public static final Message create( LdapTag tag, int messageId )
    {
        return create( tag.getTagId(), messageId ) ;
    }
    

    /**
     * Creates an empty LDAP Message instance whose types corresponds to the
     * tagId provided.  The only field that is populated is the final message
     * identifier.
     *
     * @param tagId the LDAPv3 APPLICATION tagId for the PDU type
     * @param messageId the unique identifier for messages within a session
     * @return an empty LDAP Message (PDU) instance
     */
    public static final Message create( int tagId, int messageId )
    {
        Message pdu = null ;

        switch ( tagId )
        {
            case( LdapTag.BIND_REQUEST_ID ):
                pdu = new BindRequestImpl( messageId ) ;
                break ;
            case( LdapTag.BIND_RESPONSE_ID ):
                pdu = new BindResponseImpl( messageId ) ;
                break ;
            case( LdapTag.UNBIND_REQUEST_ID ):
                pdu = new UnbindRequestImpl( messageId ) ;
                break ;
            case( LdapTag.SEARCH_REQUEST_ID ):
                pdu = new SearchRequestImpl( messageId ) ;
                break ;
            case( LdapTag.SEARCH_RESULT_ENTRY_ID ):
                pdu = new SearchResponseEntryImpl( messageId ) ;
                break ;
            case( LdapTag.SEARCH_RESULT_DONE_ID ):
                pdu = new SearchResponseDoneImpl( messageId ) ;
                break ;
            case( LdapTag.SEARCH_RESULT_REFERENCE_ID ):
                pdu = new SearchResponseReferenceImpl( messageId ) ;
                break ;
            case( LdapTag.MODIFY_REQUEST_ID ):
                pdu = new ModifyRequestImpl( messageId ) ;
                break ;
            case( LdapTag.MODIFY_RESPONSE_ID ):
                pdu = new ModifyResponseImpl( messageId ) ;
                break ;
            case( LdapTag.ADD_REQUEST_ID ):
                pdu = new AddRequestImpl( messageId ) ;
                break ;
            case( LdapTag.ADD_RESPONSE_ID ):
                pdu = new AddResponseImpl( messageId ) ;
                break ;
            case( LdapTag.DEL_REQUEST_ID ):
                pdu = new DeleteRequestImpl( messageId ) ;
                break ;
            case( LdapTag.DEL_RESPONSE_ID ):
                pdu = new DeleteResponseImpl( messageId ) ;
                break ;
            case( LdapTag.MODIFYDN_REQUEST_ID ):
                pdu = new ModifyDnRequestImpl( messageId ) ;
                break ;
            case( LdapTag.MODIFYDN_RESPONSE_ID ):
                pdu = new ModifyDnResponseImpl( messageId ) ;
                break ;
            case( LdapTag.COMPARE_REQUEST_ID ):
                pdu = new CompareRequestImpl( messageId ) ;
                break ;
            case( LdapTag.COMPARE_RESPONSE_ID ):
                pdu = new CompareResponseImpl( messageId ) ;
                break ;
            case( LdapTag.ABANDON_REQUEST_ID ):
                pdu = new AbandonRequestImpl( messageId ) ;
                break ;
            case( LdapTag.EXTENDED_REQUEST_ID ):
                pdu = new ExtendedRequestImpl( messageId ) ;
                break ;
            case( LdapTag.EXTENDED_RESPONSE_ID ):
                pdu = new ExtendedResponseImpl( messageId ) ;
                break ;
            default:
                throw new IllegalStateException(
                        "shouldn't happen - if it does then we have issues" ) ;
        }

        return pdu ;
    }
}
