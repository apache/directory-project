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


import java.math.BigInteger;
import java.util.Iterator ;

import org.apache.ldap.common.message.ScopeEnum ;
import org.apache.ldap.common.message.DerefAliasesEnum ;
import org.apache.ldap.common.message.SearchRequestImpl ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.Controls ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SearchRequest ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SearchRequestEnum ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.SearchRequestEnum1 ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AttributeDescriptionList ;


/**
 * Search request transform utilities and functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchRequestTransform
{
    static org.apache.ldap.common.message.SearchRequest
        transformFromSnacc( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Create the search request message
        SearchRequestImpl l_request =
            new SearchRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Add controls to envelope
        Controls l_snaccControls = a_snaccMessage.controls ;
        ControlTransform.transformFromSnacc( l_request, l_snaccControls) ;

        // Set the distinguished name of the entry used as the search base.
        LDAPMessageChoice l_protocolOp = a_snaccMessage.protocolOp ;
        SearchRequest l_snaccRequest = l_protocolOp.searchRequest ;
        l_request.setBase( new String( l_snaccRequest.baseObject ) ) ;

        // Set the timeLimit parameter
        l_request.setTimeLimit( l_snaccRequest.timeLimit.intValue() ) ;

        // Set the sizeLimit parameter
        l_request.setSizeLimit( l_snaccRequest.sizeLimit.intValue() ) ;

        // Set the types only flag parameter
        l_request.setTypesOnly( l_snaccRequest.typesOnly ) ;

        // Set the search scope
        SearchRequestEnum l_snaccScope = l_snaccRequest.scope ;
        switch( l_snaccScope.value )
        {
        case( SearchRequestEnum.BASEOBJECT ):
            l_request.setScope( ScopeEnum.BASEOBJECT ) ;
            break ;
        case( SearchRequestEnum.SINGLELEVEL ):
            l_request.setScope( ScopeEnum.SINGLELEVEL ) ;
            break ;
        case( SearchRequestEnum.WHOLESUBTREE ):
            l_request.setScope( ScopeEnum.WHOLESUBTREE ) ;
            break ;
        default:
            throw new ProviderException( SnaccProvider.getProvider(),
                "Unrecognized snacc scope enumeration value!" ) ;
        }

        // Set the alias dereferencing mode parameter
        SearchRequestEnum1 l_derefAliases = l_snaccRequest.derefAliases ;
        switch( l_derefAliases.value )
        {
        case( SearchRequestEnum1.DEREFALWAYS ):
            l_request.setDerefAliases( DerefAliasesEnum.DEREFALWAYS ) ;
            break ;
        case( SearchRequestEnum1.DEREFFINDINGBASEOBJ ):
            l_request.setDerefAliases( DerefAliasesEnum.DEREFFINDINGBASEOBJ ) ;
            break ;
        case( SearchRequestEnum1.DEREFINSEARCHING ):
            l_request.setDerefAliases( DerefAliasesEnum.DEREFINSEARCHING ) ;
            break ;
        case( SearchRequestEnum1.NEVERDEREFALIASES ):
            l_request.setDerefAliases( DerefAliasesEnum.NEVERDEREFALIASES ) ;
            break ;
        default:
            throw new ProviderException( SnaccProvider.getProvider(),
                "Unrecognized snacc derefAliases enumeration value!" ) ;
        }

        // Set the AttributeDescriptionList using the attributes member
        AttributeDescriptionList l_snaccAttrs = l_snaccRequest.attributes ;
        Iterator l_attrList = l_snaccAttrs.iterator() ;
        while( l_attrList.hasNext() )
        {
            l_request.addAttribute(
                new String( ( byte [] ) l_attrList.next() ) ) ;
        }

        // Set the search request filter
        l_request.setFilter(
            FilterTransform.transformFromSnacc( l_snaccRequest.filter ) ) ;

        return l_request ;
    }


    static LDAPMessage transformToSnacc(
        org.apache.ldap.common.message.SearchRequest a_request )
        throws ProviderException
    {
        // Prepare the Snacc based PDU envelope
        LDAPMessage l_snaccMessage = Utils.prepareEnvelope( a_request ) ;
        LDAPMessageChoice l_protocolOp = l_snaccMessage.protocolOp ;
        l_protocolOp.choiceId = LDAPMessageChoice.SEARCHREQUEST_CID ;
        SearchRequest l_snaccRequest = new SearchRequest() ;
        l_protocolOp.searchRequest = l_snaccRequest ;

        // Set the distinguished name of the entry used as the search base.
        l_snaccRequest.baseObject = a_request.getBase().getBytes() ;

        // Set the timeLimit parameter
        l_snaccRequest.timeLimit = new BigInteger( Integer.toString(  
                        a_request.getTimeLimit() ) ) ;

        // Set the sizeLimit parameter
        l_snaccRequest.sizeLimit = new BigInteger( Integer.toString(
                        a_request.getSizeLimit() ) ) ;

        // Set the types only flag parameter
        l_snaccRequest.typesOnly = a_request.getTypesOnly() ;

        // Set the search scope parameter
        SearchRequestEnum l_scope = new SearchRequestEnum() ;
        switch( a_request.getScope().getValue() )
        {
        case( ScopeEnum.BASEOBJECT_VAL ):
            l_scope.value = SearchRequestEnum.BASEOBJECT ;
            break ;
        case( ScopeEnum.SINGLELEVEL_VAL ):
            l_scope.value = SearchRequestEnum.SINGLELEVEL ;
            break ;
        case( ScopeEnum.WHOLESUBTREE_VAL ):
            l_scope.value = SearchRequestEnum.WHOLESUBTREE ;
            break ;
        default:
            throw new ProviderException( SnaccProvider.getProvider(),
                "Unrecognized search scope parameter value: "
                + a_request.getScope().getValue() ) ;
        }
        l_snaccRequest.scope = l_scope ;
        
        // Set the alias dereferencing mode parameter
        SearchRequestEnum1 l_derefAliases = new SearchRequestEnum1() ;
        switch( a_request.getDerefAliases().getValue() )
        {
        case( DerefAliasesEnum.DEREFALWAYS_VAL ):
            l_derefAliases.value = SearchRequestEnum1.DEREFALWAYS ;
            break ;
        case( DerefAliasesEnum.DEREFFINDINGBASEOBJ_VAL ):
            l_derefAliases.value = SearchRequestEnum1.DEREFFINDINGBASEOBJ ;
            break ;
        case( DerefAliasesEnum.DEREFINSEARCHING_VAL ):
            l_derefAliases.value = SearchRequestEnum1.DEREFINSEARCHING ;
            break ;
        case( DerefAliasesEnum.NEVERDEREFALIASES_VAL ):
            l_derefAliases.value = SearchRequestEnum1.NEVERDEREFALIASES ;
            break ;
        default:
            throw new ProviderException( SnaccProvider.getProvider(),
                "Unrecognized search derefAliases parameter value: "
                + a_request.getDerefAliases().getValue() ) ;
        }
        l_snaccRequest.derefAliases = l_derefAliases ;
        
        // Set the AttributeDescriptionList using the attributes member
        AttributeDescriptionList l_snaccAttrList = new AttributeDescriptionList() ;
        Iterator l_list = a_request.getAttributes().iterator() ;
        while( l_list.hasNext() )
        {
            l_snaccAttrList.add( ( ( String ) l_list.next() ).getBytes() ) ;
        }

        l_snaccRequest.attributes =  l_snaccAttrList ;
        // Set the search request filter
        l_snaccRequest.filter =
            FilterTransform.transformToSnacc( a_request.getFilter() ) ;

        return l_snaccMessage ;
    }
}
