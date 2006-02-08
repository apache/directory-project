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
package org.apache.ldap.common.berlib.snacc ;


import java.util.Iterator ;
import javax.naming.NamingException ;
import javax.naming.NamingEnumeration ;
import javax.naming.directory.Attribute ;

import org.apache.ldap.common.message.LdapResult ;
import org.apache.ldap.common.message.LdapResultImpl ;
import org.apache.ldap.common.message.ResultCodeEnum ;
import org.apache.ldap.common.message.SearchResponseEntry ;
import org.apache.ldap.common.message.LockableAttributeImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;
import org.apache.ldap.common.message.LockableAttributesImpl ;
import org.apache.ldap.common.message.SearchResponseEntryImpl ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPResultEnum ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SearchResultEntry ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.PartialAttributeList ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.PartialAttributeListSeq ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.PartialAttributeListSeqSetOf ;


/**
 * Search result entry transformation functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchResponseEntryTransform
{
	static org.apache.ldap.common.message.SearchResponseEntry
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
		SearchResponseEntryImpl l_response =
            new SearchResponseEntryImpl( a_snaccMessage.messageID.intValue() ) ;
        LDAPMessageChoice l_snaccOp = a_snaccMessage.protocolOp ;
        SearchResultEntry l_snaccResponse = l_snaccOp.searchResEntry ;

        ControlTransform.transformFromSnacc( l_response,
            a_snaccMessage.controls ) ;

        // Set the dn of the object returned
        l_response.setObjectName( new String( l_snaccResponse.objectName ) ) ;

        // Set the attributes of the object
        LockableAttributesImpl l_attributes =
            new LockableAttributesImpl( l_response ) ;
        l_response.setAttributes( l_attributes ) ;

        // Add each attribute to the attributes
        PartialAttributeList l_snaccPal = l_snaccResponse.attributes ;
        Iterator l_list = l_snaccPal.iterator() ;
        while( l_list.hasNext() )
        {
            PartialAttributeListSeq l_snaccAttr =
                ( PartialAttributeListSeq ) l_list.next() ;
            LockableAttributeImpl l_attr = new LockableAttributeImpl(
                l_attributes, new String( l_snaccAttr.type ) ) ;

            // Now add all the values to the attribute.
            PartialAttributeListSeqSetOf l_snaccValuesSet = l_snaccAttr.vals ;
            Iterator l_snaccValues = l_snaccValuesSet.values().iterator() ;
            while( l_snaccValues.hasNext() )
            {
                l_attr.add( new String( ( byte [] ) l_snaccValues.next() ) ) ;
            }

            l_attributes.put( l_attr ) ;
        }

        return l_response ;
    }


    static LDAPMessage
        transformToSnacc( org.apache.ldap.common.message.SearchResponseEntry a_resp )
        throws ProviderException
    {
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_resp ) ;
        LDAPMessageChoice l_snaccOp = l_snaccMessage.protocolOp ;
		l_snaccOp.choiceId = LDAPMessageChoice.SEARCHRESENTRY_CID ;

        SearchResultEntry l_snaccResponse = new SearchResultEntry() ;
        l_snaccOp.searchResEntry = l_snaccResponse ;

        // Set the dn of the entry being returned.
        l_snaccResponse.objectName = a_resp.getObjectName().getBytes() ;

        // Set the attributes of the entry being returned
        PartialAttributeList l_snaccAttributes = new PartialAttributeList() ;
        l_snaccResponse.attributes = l_snaccAttributes ;

        // Add a PartialAttributeListSeqf for each attribute
        NamingEnumeration l_attributeList = a_resp.getAttributes().getAll() ;
        while( l_attributeList.hasMoreElements() )
        {
            Attribute l_attr = ( Attribute ) l_attributeList.nextElement() ;
            PartialAttributeListSeq l_snaccAttr =
                new PartialAttributeListSeq() ;
            PartialAttributeListSeqSetOf l_snaccValues =
                new PartialAttributeListSeqSetOf() ;
            l_snaccAttr.type = l_attr.getID().getBytes() ;
            l_snaccAttr.vals = l_snaccValues ;

            // Add all the attribute's values to the
            // PartialAttributeListSeqSetOf
            for(int ii = 0; ii < l_attr.size(); ii++ )
            {
                byte [] l_value = null ;

                try
                {
                    l_value = ( ( String ) l_attr.get( ii ) ).getBytes() ;
                }
                catch( NamingException ne )
                {
                    ProviderException pe = new ProviderException(
                        SnaccProvider.getProvider(),
                        "NamingException while extracting attribute value from "
                        + l_attr.getID() ) ;
                    pe.addThrowable( ne ) ;
                    throw pe ;
                }

                l_snaccValues.put( l_value, l_value ) ;
            }

            // Add the attribute to the partial attributes list
            l_snaccAttributes.add( l_snaccAttr ) ;
        }

        return l_snaccMessage ;
    }
}
