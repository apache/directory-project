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
import javax.naming.directory.DirContext ;
import javax.naming.directory.ModificationItem ;

import org.apache.ldap.common.message.ModifyRequestImpl ;
import org.apache.ldap.common.message.LockableAttributeImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.ModifyRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.ModifyRequestSeqOf ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.ModifyRequestSeqOfSeq ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeTypeAndValues ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.ModifyRequestSeqOfSeqEnum ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeTypeAndValuesSetOf ;


/**
 * Modify request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class ModifyRequestTransform
{
    static org.apache.ldap.common.message.ModifyRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the modified request message
        ModifyRequestImpl l_request =
            new ModifyRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls) ;

        // Set the distinguished name of the object modified by the request
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        ModifyRequest l_snaccRequest = l_protocolOp.modifyRequest ;
        l_request.setName( new String( l_snaccRequest.object ) ) ;

        // Now we need to extract ModificationItems from the ModifyRequestSeqOf
        // in the ModifyRequest using its the modification member.
        ModifyRequestSeqOf l_snaccModifications = l_snaccRequest.modification ;
        Iterator l_snaccModsList = l_snaccModifications.iterator() ;
        while( l_snaccModsList.hasNext() )
        {
            // Set convenient handles to various points in the snacc pdu
            // containment tree for easy access to inner members.
            ModifyRequestSeqOfSeq l_snaccMod = ( ModifyRequestSeqOfSeq )
                l_snaccModsList.next() ;
            AttributeTypeAndValues l_snaccAttr = l_snaccMod.modification ;
            AttributeTypeAndValuesSetOf l_snaccValues = l_snaccAttr.vals ;
            ModifyRequestSeqOfSeqEnum l_snaccModOp = l_snaccMod.operation ;

            // Create a lockable attribute with the request as the parent
            // setting its name to the modification attribute name/id.
            LockableAttributeImpl l_attr = new LockableAttributeImpl( l_request,
                new String( l_snaccAttr.type ) ) ;

            // Iterate through the set of values associated with the
            // modification operation and add the values to the attribute.
            Iterator l_attrValues = l_snaccValues.values().iterator() ;
            while( l_attrValues.hasNext() )
            {
                byte [] l_value = ( byte [] ) l_attrValues.next() ;
                l_attr.add( new String( l_value ) ) ;
            }

            // Switch on the ModifyRequestSeqOfSeqEnum instance value member to
            // determine the operation of the ModificationItem to create.
            ModificationItem l_item = null ;
            switch( l_snaccModOp.value )
            {
            case( ModifyRequestSeqOfSeqEnum.ADD ):
                l_item = new ModificationItem(
                    DirContext.ADD_ATTRIBUTE, l_attr ) ;
                break ;
            case( ModifyRequestSeqOfSeqEnum.DELETE ):
                l_item = new ModificationItem(
                    DirContext.REMOVE_ATTRIBUTE, l_attr ) ;
                break ;
            case( ModifyRequestSeqOfSeqEnum.REPLACE ):
                l_item = new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE, l_attr ) ;
                break ;
            default:
                throw new ProviderException( SnaccProvider.getProvider(),
                    "Unrecognized snacc modification request operation!" ) ;
            }

            // Add the built modification item to the request.
            l_request.addModification( l_item ) ;
        }

        return l_request ;
    }


      /* -----------------------------------------------------------------------

        ModifyRequest ::= [APPLICATION 6] SEQUENCE {
                object          LDAPDN,
                modification    SEQUENCE OF SEQUENCE {
                        operation       ENUMERATED {
                                                add     (0),
                                                delete  (1),
                                                replace (2) },
                        modification    AttributeTypeAndValues } }

        AttributeTypeAndValues ::= SEQUENCE {
                type    AttributeDescription,
                vals    SET OF AttributeValue }

        ModifyResponse ::= [APPLICATION 7] LDAPResult

        --------------------------------------------------------------------- */

    /**
     * We have to process the modify request on an existing entry.  We
     * loop through modification items which are ModifyRequestSeqOfSeq.
     * Each ModifyRequestSeqOfSeq object in the sequence of changes
     * contains an operation specified by a ModifyRequestSeqOfSeqEnum,
     * a set of values contained in a AttributeTypeAndValuesSetOf
     * object, and the name of the attribute being modified in a
     * byte array held by a AttributeTypeAndValues object handle.  These
     * values are handed off to the appropriate add, remove and replace
     * methods using a switch.  These methods do the work of altering
     * the entry.
     *
     * SNACC GENERATED CLASSES ARE BASED ON THE FOLLOWING ASN.1 NOTATION
     */
    static LDAPMessage transformToSnacc(
        org.apache.ldap.common.message.ModifyRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        ModifyRequest l_snaccRequest = new ModifyRequest() ;
        l_protocolOp.modifyRequest = l_snaccRequest ;
        l_protocolOp.choiceId = LDAPMessageChoice.MODIFYREQUEST_CID ;

        // Set the DN of the entry to modify
        l_snaccRequest.object = a_request.getName().getBytes() ;

        // Create modifications container which holds ModifyRequestSeqOfSeq
        // class instances.  These are the modifications per attribute or are
        // equivalent to the ModificationItem.  Each ModificationItem will
        // create a ModifyRequestSeqOfSeq instance.
        ModifyRequestSeqOf l_snaccModifications = new ModifyRequestSeqOf() ;
        l_snaccRequest.modification = l_snaccModifications ;

        // Build ModifyRequestSeqOfSeq for each ModificationItem
        Iterator l_modList = a_request.getModificationItems().iterator() ;
        while( l_modList.hasNext() )
        {
            // Build an empty ModifyRequestSeqOfSeq structure ready to be set
            // to a attribute for an operation using the values in moditems.
            ModifyRequestSeqOfSeq l_snaccMod = new ModifyRequestSeqOfSeq() ;
            AttributeTypeAndValues l_snaccAttr = new AttributeTypeAndValues() ;
            ModifyRequestSeqOfSeqEnum l_snaccModOp =
                new ModifyRequestSeqOfSeqEnum() ;
            AttributeTypeAndValuesSetOf l_snaccAttrValues =
                new AttributeTypeAndValuesSetOf() ;
            l_snaccAttr.vals = l_snaccAttrValues ;
            l_snaccMod.modification = l_snaccAttr ;
            l_snaccMod.operation = l_snaccModOp ;

            // Set the ModifyRequestSeqOfSeqEnum value which corresponds to the
            // ModificationItem operation value.  Switch translates op to value.
            ModificationItem l_item = ( ModificationItem ) l_modList.next() ;
            switch( l_item.getModificationOp() )
            {
            case( DirContext.ADD_ATTRIBUTE ):
                l_snaccModOp.value = ModifyRequestSeqOfSeqEnum.ADD ;
                break ;
            case( DirContext.REMOVE_ATTRIBUTE ):
                l_snaccModOp.value = ModifyRequestSeqOfSeqEnum.DELETE ;
                break ;
            case( DirContext.REPLACE_ATTRIBUTE ):
                l_snaccModOp.value = ModifyRequestSeqOfSeqEnum.REPLACE ;
                break ;
            default:
                throw new ProviderException( SnaccProvider.getProvider(),
                    "Unrecognized modification operation value: " +
                    l_item.getModificationOp() ) ;
            }

            // Extract the attribute from the ModificationItem and set the
            // id of the of the attribute.
            Attribute l_attr = l_item.getAttribute() ;
            l_snaccAttr.type = l_attr.getID().getBytes() ;

            // Use the values in the attribute to fill up an instance of
            // AttributeTypeAndValuesSetOf with the attribute values for the op.
            NamingEnumeration l_valueList = null ;

            try
            {
                l_valueList = l_attr.getAll() ;
            }
            catch( NamingException ne )
            {
                ProviderException pe = new ProviderException(
                    SnaccProvider.getProvider(),
                    "Could not acquire enumeration over attribute values!" ) ;
                pe.addThrowable( ne ) ;
                throw pe ;
            }


            while( l_valueList.hasMoreElements())
            {
                byte [] l_value = ( ( String )
                    l_valueList.nextElement() ).getBytes() ;
                l_snaccAttrValues.put( l_value, l_value ) ;
            }

            // Add the newly composed snacc modification which is an instance
            // of a ModifyRequestSeqOfSeq to the ModifyRequestSeqOf mods
            l_snaccModifications.add( l_snaccMod ) ;
        }
        l_snaccMessage.protocolOp.modifyRequest.modification 
            = l_snaccModifications ;
        return l_snaccMessage ;
    }
}
