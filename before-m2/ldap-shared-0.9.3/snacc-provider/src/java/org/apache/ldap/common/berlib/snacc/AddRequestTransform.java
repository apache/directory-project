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

import org.apache.ldap.common.message.AddRequestImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.message.LockableAttributeImpl ;
import org.apache.ldap.common.message.LockableAttributesImpl ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AddRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeList ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeListSeq ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeListSeqSetOf ;


/**
 * AddRequest transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class AddRequestTransform
{
    static org.apache.ldap.common.message.AddRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the add request message
        AddRequestImpl l_request =
            new AddRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        Controls l_snaccControls = a_snaccMessage.controls ;
        ControlTransform.transformFromSnacc( l_request, l_snaccControls) ;

        // Set the distinguished name of the entry added by the request
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        AddRequest l_snaccRequest = l_protocolOp.addRequest ;
        l_request.setName( new String( l_snaccRequest.entry ) ) ;

        // --------------------------------------------------------------------
        // Start building the Attributes
        // --------------------------------------------------------------------
        //
        // Snacc request attributes is a AttributeList which contains one or
        // AttributeListSeq objects.
        //
        // AttributeListSeq contains the name of the attribute ( the id ) and
        // the set of values associated with it in a AttributeListSeqSetOf
        // Map where the entry key and value are the same: they're the binary
        // byte [] values for the attribute.
        //

        // Get the attribute or AttributeList from the snacc request PDU
        LockableAttributesImpl l_attrs =
            new LockableAttributesImpl( l_request ) ;
        l_request.setEntry( l_attrs ) ;  // Add the attributes to the entry.
        LockableAttributeImpl l_attr = null ;
        AttributeList l_snaccAttrList = l_snaccRequest.attributes ;
        AttributeListSeq l_snaccAttrSeq = null ;

        // Loop through each AttributeListSeq in the AttributeList
        for( int ii = 0; ii < l_snaccAttrList.size(); ii++ )
        {
            l_snaccAttrSeq = ( AttributeListSeq ) l_snaccAttrList.get( ii ) ;

            // For each AttributeListSeq extract the name and the values in
            // the AttributeListSeqSetOf object stored in the vals member.
            String l_attrId = new String( l_snaccAttrSeq.type ) ;
            l_attr = new LockableAttributeImpl( l_attrs, l_attrId ) ;
            AttributeListSeqSetOf l_vals = l_snaccAttrSeq.vals ;

            // Add each attribute value converted into a string to the attribute
            Iterator l_valueIterator = l_vals.values().iterator() ;
            while( l_valueIterator.hasNext() )
            {
                String l_value =
                    new String( ( byte [] ) l_valueIterator.next() ) ;
                l_attr.add( l_value ) ;
            }

            // Add the attribute to the set of Attributes
            l_attrs.put( l_attr ) ;
        }

        return l_request ;
    }


    static LDAPMessage transformToSnacc( org.apache.ldap.common.message.AddRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        l_protocolOp.choiceId = LDAPMessageChoice.ADDREQUEST_CID ;
        AddRequest l_snaccRequest = new AddRequest() ;
        l_protocolOp.addRequest = l_snaccRequest ;

        // Set the DN of the entry to add
        l_snaccRequest.entry = a_request.getName().getBytes() ;

        // Start building the AttributeList object
        Attribute l_attr = null ;
        AttributeList l_snaccAttrList = new AttributeList() ;
        AttributeListSeq l_snaccAttrListSeq = new AttributeListSeq ()  ;
        NamingEnumeration l_attrList = a_request.getEntry().getAll() ;
		String testString ;

        while( l_attrList.hasMoreElements() )
        {
            l_attr = ( Attribute ) l_attrList.nextElement() ;
			l_snaccAttrListSeq = new AttributeListSeq ()  ;
			l_snaccAttrListSeq.type = l_attr.getID().getBytes() ;

            // Add a AttributeListSeqSetOf to contain the set of values
            l_snaccAttrListSeq.vals = new AttributeListSeqSetOf() ;
            AttributeListSeqSetOf l_snaccValues = l_snaccAttrListSeq.vals ;

            // Add all the values in the attribute to the snacc value
            // container: an instance of AttributeListSeqSetOf
            NamingEnumeration l_values = null ;

            try
            {
            	l_values = l_attr.getAll() ;
            }
            catch( NamingException ne )
            {
                ProviderException pe = new ProviderException(
                    SnaccProvider.getProvider(), "Could not extract values from"
                    + " attributes!" ) ;
                pe.addThrowable( ne ) ;
                throw pe ;
            }

            String testS ;
            while( l_values.hasMoreElements() )
            {
               testS = new String ( (String) l_values.nextElement() ) ;

                byte [] l_value = testS.getBytes() ;
                //byte [] l_value = ( ( String )
                    //l_values.nextElement() ).getBytes() ;
                l_snaccValues.put( l_value, l_value ) ;
                testS =  new String (l_attr.getID() );
            }
            l_snaccAttrListSeq.vals = l_snaccValues ;
			l_snaccAttrList.add( l_snaccAttrListSeq );
        }

		l_snaccMessage.protocolOp.addRequest.attributes = l_snaccAttrList ;
        return l_snaccMessage ;
	}


}

