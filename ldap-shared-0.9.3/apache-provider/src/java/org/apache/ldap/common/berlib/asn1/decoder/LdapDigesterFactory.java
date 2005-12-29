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


import org.apache.asn1.ber.digester.BERDigester;
import org.apache.asn1.ber.digester.TagTree;
import org.apache.asn1.ber.digester.rules.Octets2StringRule;
import org.apache.asn1.ber.digester.rules.PopOnFinish;
import org.apache.asn1.ber.digester.rules.PrimitiveIntDecodeRule;
import org.apache.asn1.ber.digester.rules.PrimitiveOctetStringRule;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.berlib.asn1.decoder.abandon.AbandonRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.add.AddAttributeValueRule;
import org.apache.ldap.common.berlib.asn1.decoder.add.AddRequestAttributesRule;
import org.apache.ldap.common.berlib.asn1.decoder.add.AddRequestEntryDnRule;
import org.apache.ldap.common.berlib.asn1.decoder.add.AddRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.add.AddResponseRule;
import org.apache.ldap.common.berlib.asn1.decoder.bind.BindNameRule;
import org.apache.ldap.common.berlib.asn1.decoder.bind.BindRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.bind.BindResponseRule;
import org.apache.ldap.common.berlib.asn1.decoder.bind.BindSimpleCredentialsRule;
import org.apache.ldap.common.berlib.asn1.decoder.bind.BindVersionRule;
import org.apache.ldap.common.berlib.asn1.decoder.compare.CompareAVARule;
import org.apache.ldap.common.berlib.asn1.decoder.compare.CompareEntryRule;
import org.apache.ldap.common.berlib.asn1.decoder.compare.CompareRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.compare.CompareResponseRule;
import org.apache.ldap.common.berlib.asn1.decoder.delete.DeleteRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.delete.DeleteResponseRule;
import org.apache.ldap.common.berlib.asn1.decoder.extended.ExtendedRequestOidRule;
import org.apache.ldap.common.berlib.asn1.decoder.extended.ExtendedRequestPayloadRule;
import org.apache.ldap.common.berlib.asn1.decoder.extended.ExtendedRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.extended.ExtendedResponseOidRule;
import org.apache.ldap.common.berlib.asn1.decoder.extended.ExtendedResponsePayloadRule;
import org.apache.ldap.common.berlib.asn1.decoder.extended.ExtendedResponseRule;
import org.apache.ldap.common.berlib.asn1.decoder.modify.ModificationItemRule;
import org.apache.ldap.common.berlib.asn1.decoder.modify.ModifyRequestAttributeValueRule;
import org.apache.ldap.common.berlib.asn1.decoder.modify.ModifyRequestEntryDnRule;
import org.apache.ldap.common.berlib.asn1.decoder.modify.ModifyRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.modify.ModifyResponseRule;
import org.apache.ldap.common.berlib.asn1.decoder.modifydn.ModifyDnRequestDeleteOldRdnRule;
import org.apache.ldap.common.berlib.asn1.decoder.modifydn.ModifyDnRequestEntryRule;
import org.apache.ldap.common.berlib.asn1.decoder.modifydn.ModifyDnRequestNewRdnRule;
import org.apache.ldap.common.berlib.asn1.decoder.modifydn.ModifyDnRequestNewSuperiorRule;
import org.apache.ldap.common.berlib.asn1.decoder.modifydn.ModifyDnRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.modifydn.ModifyDnResponseRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.AndRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.ApproxMatchRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.BaseObjectRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.DerefAliasRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.EqualityMatchRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.ExtensibleMatchDnAttributesRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.ExtensibleMatchMatchingRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.ExtensibleMatchRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.ExtensibleMatchTypeRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.ExtensibleMatchValueRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.GreaterOrEqualRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.LessOrEqualRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.NotRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.OrRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.PresentRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.RequestedAttributesRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.RequestedAttributesStateChangeRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.ScopeRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SearchRequestRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SearchResponseDoneRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SearchResponseEntryAttributesRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SearchResponseEntryDnRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SearchResponseEntryRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SearchResponseReferenceRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SearchResponseReferralRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SizeLimitRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SubstringMatchAnyRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SubstringMatchFinalRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SubstringMatchInitialRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.SubstringMatchRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.TerminateFilterStateRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.TimeLimitRule;
import org.apache.ldap.common.berlib.asn1.decoder.search.TypesOnlyRule;
import org.apache.ldap.common.berlib.asn1.decoder.unbind.UnbindRequestRule;


/**
 * A factory that creates a digester for processing LDAPv3 message PDUs.
 *
 * @todo extract a protocol independent DigesterFactory interface from this and
 * put it back into the ber-codec then have this class implement that interface.
 * Determine if we want to have factories instantiate and configure (build?)
 * digesters using configuration files?
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class LdapDigesterFactory
{
    /** static singleton singleton */
    private static LdapDigesterFactory singleton ;


    /**
     * Private default (only) constructor which is only called once to create
     * the singleton.
     */
    private LdapDigesterFactory()
    {
        singleton = this ;
    }


    /**
     * Gets a handle on the singleton factory instance.
     *
     * @return the singlton instance for the ldap digester factory
     */
    public static LdapDigesterFactory getSingleton()
    {
        if ( singleton == null )
        {
            singleton = new LdapDigesterFactory() ;
        }

        return singleton ;
    }


    /**
     * Creates and populates the BERDigester with rules to process all LDAPv3
     * message types.
     *
     * @return a digester that can process all LDAPv3 PDU types
     */
    public BERDigester create()
    {
        BERDigester digester = new BERDigester() ;

        addMessageIdRules( digester ) ;
        addAbandonRequestRules( digester ) ;
        addAddResponseRules( digester ) ;
        addAddRequestRules( digester ) ;
        addUnbindRequestRules( digester ) ;
        addBindRequestRules( digester ) ;
        addBindResponseRules( digester ) ;
        addCompareRequestRules( digester ) ;
        addCompareResponseRules( digester ) ;
        addDeleteRequestRules( digester ) ;
        addDeleteResponseRules( digester ) ;
        addExtendedRequestRules( digester ) ;
        addExtendedResponseRules( digester ) ;
        addModifyDnRequestRules( digester ) ;
        addModifyDnResponseRules( digester ) ;
        addModifyRequestRules( digester ) ;
        addModifyResponseRules( digester ) ;
        addSearchRequestRules( digester ) ;
        addSearchResponseDoneRules( digester ) ;
        addSearchResponseReferenceRules( digester ) ;
        addSearchResponseEntryRules( digester ) ;

        return digester ;
    }


    private void addSearchRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2];

        // set pattern and addRule for the SearchRequest
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag();
        pattern[1] = LdapTag.SEARCH_REQUEST.getPrimitiveTag();
        digester.addRule( pattern, new SearchRequestRule() );

        // setup pattern to set the dn of the entry added
        pattern = new int[3];
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag();
        pattern[1] = LdapTag.SEARCH_REQUEST.getPrimitiveTag();
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag();
        digester.addRule( pattern, new BaseObjectRule() );

        pattern[2] = UniversalTag.ENUMERATED.getPrimitiveTag();
        digester.addRule( pattern, new ScopeRule() );
        digester.addRule( pattern, new DerefAliasRule() );

        pattern[2] = UniversalTag.INTEGER.getPrimitiveTag();
        digester.addRule( pattern, new SizeLimitRule() );
        digester.addRule( pattern, new TimeLimitRule() );

        pattern[2] = UniversalTag.BOOLEAN.getPrimitiveTag();
        digester.addRule( pattern, new TypesOnlyRule() );

        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag();
        digester.addRule( pattern, new TerminateFilterStateRule() );
        digester.addRule( pattern, new RequestedAttributesStateChangeRule() );

        pattern = new int[4];
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag();
        pattern[1] = LdapTag.SEARCH_REQUEST.getPrimitiveTag();
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag();
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag();
        digester.addRule( pattern, new RequestedAttributesRule() );

        pattern = new int[2];
        pattern[0] = TagTree.WILDCARD;
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_7.getPrimitiveTag();
        digester.addRule( pattern, new PresentRule() );

        pattern = new int[3];
        pattern[0] = TagTree.WILDCARD;
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_3.getPrimitiveTag();
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag();
        digester.addRule( pattern, new EqualityMatchRule() );

        //
        // substring filter and helper rules
        //

        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_4.getPrimitiveTag();
        digester.addRule( pattern, new Octets2StringRule() );
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag();
        digester.addRule( pattern, new SubstringMatchRule() );

        pattern = new int[4];
        pattern[0] = TagTree.WILDCARD;
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_4.getPrimitiveTag();
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag();
        pattern[3] = LdapTag.CONTEXT_SPECIFIC_TAG_0.getPrimitiveTag();
        digester.addRule( pattern, new SubstringMatchInitialRule() );

        pattern[3] = LdapTag.CONTEXT_SPECIFIC_TAG_1.getPrimitiveTag();
        digester.addRule( pattern, new SubstringMatchAnyRule() );

        pattern[3] = LdapTag.CONTEXT_SPECIFIC_TAG_2.getPrimitiveTag();
        digester.addRule( pattern, new SubstringMatchFinalRule() );

        //
        // end substring filter rules
        //

        pattern = new int[3];
        pattern[0] = TagTree.WILDCARD;
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_5.getPrimitiveTag();
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag();
        digester.addRule( pattern, new GreaterOrEqualRule() );

        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_6.getPrimitiveTag();
        digester.addRule( pattern, new LessOrEqualRule() );

        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_8.getPrimitiveTag();
        digester.addRule( pattern, new ApproxMatchRule() );

        //
        // Extensible match filter rules
        //
        pattern = new int[2];
        pattern[0] = TagTree.WILDCARD;
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_9.getPrimitiveTag();
        digester.addRule( pattern, new ExtensibleMatchRule() );

        pattern = new int[3];
        pattern[0] = TagTree.WILDCARD;
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_9.getPrimitiveTag();
        pattern[2] = LdapTag.CONTEXT_SPECIFIC_TAG_1.getPrimitiveTag();
        digester.addRule( pattern, new ExtensibleMatchMatchingRule() );

        pattern[2] = LdapTag.CONTEXT_SPECIFIC_TAG_2.getPrimitiveTag();
        digester.addRule( pattern, new ExtensibleMatchTypeRule() );

        pattern[2] = LdapTag.CONTEXT_SPECIFIC_TAG_3.getPrimitiveTag();
        digester.addRule( pattern, new ExtensibleMatchValueRule() );

        pattern[2] = LdapTag.CONTEXT_SPECIFIC_TAG_4.getPrimitiveTag();
        digester.addRule( pattern, new ExtensibleMatchDnAttributesRule() );

        //
        // End Extensible match filter rules
        //

        //
        // Branch filter expresion rules
        //

        pattern = new int[2];
        pattern[0] = TagTree.WILDCARD;
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_0.getPrimitiveTag();
        digester.addRule( pattern, new AndRule() );
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_1.getPrimitiveTag();
        digester.addRule( pattern, new OrRule() );
        pattern[1] = LdapTag.CONTEXT_SPECIFIC_TAG_2.getPrimitiveTag();
        digester.addRule( pattern, new NotRule() );
    }


    private void addModifyRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // set pattern and addRule for the ModifyRequest
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_REQUEST.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyRequestRule() ) ;

        // setup pattern to set the dn of the entry added
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyRequestEntryDnRule() ) ;

        pattern = new int[5] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[4] = UniversalTag.ENUMERATED.getPrimitiveTag() ;
        digester.addRule( pattern,
                new PrimitiveIntDecodeRule( UniversalTag.ENUMERATED ) ) ;

        pattern[4] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        digester.addRule( pattern, new PopOnFinish() ) ;

        pattern = new int[6] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[4] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[5] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ModificationItemRule() ) ;

        pattern = new int[7] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[4] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[5] = UniversalTag.SET_SET_OF.getPrimitiveTag() ;
        pattern[6] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyRequestAttributeValueRule() ) ;
    }


    private void addSearchResponseEntryRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // set pattern and addRule for the AddRequest
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_ENTRY.getPrimitiveTag() ;
        digester.addRule( pattern, new SearchResponseEntryRule() ) ;

        // setup pattern to set the dn of the entry added
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_ENTRY.getPrimitiveTag() ;
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new SearchResponseEntryDnRule() ) ;

        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_ENTRY.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        digester.addRule( pattern, new SearchResponseEntryAttributesRule() ) ;

        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_ENTRY.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        digester.addRule( pattern, new PopOnFinish() ) ;

        pattern = new int[5] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_ENTRY.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[4] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new Octets2StringRule() ) ;

        pattern = new int[6] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_ENTRY.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[4] = UniversalTag.SET_SET_OF.getPrimitiveTag() ;
        pattern[5] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new AddAttributeValueRule() ) ;
    }


    private void addAddRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // set pattern and addRule for the AddRequest
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_REQUEST.getPrimitiveTag() ;
        digester.addRule( pattern, new AddRequestRule() ) ;

        // setup pattern to set the dn of the entry added
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new AddRequestEntryDnRule() ) ;

        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        digester.addRule( pattern, new AddRequestAttributesRule() ) ;

        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        digester.addRule( pattern, new PopOnFinish() ) ;

        pattern = new int[5] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[4] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new Octets2StringRule() ) ;

        pattern = new int[6] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[4] = UniversalTag.SET_SET_OF.getPrimitiveTag() ;
        pattern[5] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new AddAttributeValueRule() ) ;
    }


    private void addSearchResponseReferenceRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // set pattern and addRule for the SearchResponseReference
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_REFERENCE.getPrimitiveTag() ;
        digester.addRule( pattern, new SearchResponseReferenceRule() ) ;
        digester.addRule( pattern, new SearchResponseReferralRule() ) ;


        // expand the pattern for 3 elements
        pattern = new int[3] ;

        // set pattern for the SearchResponseReference
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_REFERENCE.getPrimitiveTag() ;
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    private void addSearchResponseDoneRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // modify pattern and addRule for the SearchResponseDoneEncoder
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_DONE.getPrimitiveTag() ;
        digester.addRule( pattern, new SearchResponseDoneRule() ) ;

        // modify pattern and addRule for the SearchResponseDoneEncoder
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_DONE.getPrimitiveTag() ;

        // for the resultCode
        pattern[2] = UniversalTag.ENUMERATED.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.SEARCH_RESULT_DONE.getPrimitiveTag() ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    private void addModifyResponseRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // modify pattern and addRule for the ModifyResponse
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_RESPONSE.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyResponseRule() ) ;

        // modify pattern and addRule for the ModifyResponse
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_RESPONSE.getPrimitiveTag() ;

        // for the resultCode
        pattern[2] = UniversalTag.ENUMERATED.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFY_RESPONSE.getPrimitiveTag() ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Add responses.
     *
     * @param digester the digester to add the rules to
     */
    private void addAddResponseRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // modify pattern and addRule for the AddResponse
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_RESPONSE.getPrimitiveTag() ;
        digester.addRule( pattern, new AddResponseRule() ) ;

        // modify pattern and addRule for the AddResponse
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_RESPONSE.getPrimitiveTag() ;

        // for the resultCode
        pattern[2] = UniversalTag.ENUMERATED.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.ADD_RESPONSE.getPrimitiveTag() ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 ModifyDn requests.
     *
     * @param digester the digester to add the rules to
     */
    private void addModifyDnRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFYDN_REQUEST.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyDnRequestRule() ) ;

        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFYDN_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyDnRequestEntryRule() ) ;

        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyDnRequestNewRdnRule() ) ;

        pattern[2] = UniversalTag.BOOLEAN.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyDnRequestDeleteOldRdnRule() ) ;

        pattern[2] = LdapTag.MODIFYDN_REQUEST_NEWSUP_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyDnRequestNewSuperiorRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 ModifyDn responses.
     *
     * @param digester the digester to add the rules to
     */
    private void addModifyDnResponseRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // modify pattern and addRule for the ModifyDnResponse
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFYDN_RESPONSE.getPrimitiveTag() ;
        digester.addRule( pattern, new ModifyDnResponseRule() ) ;

        // modify pattern and addRule for the ModifyDnResponse
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFYDN_RESPONSE.getPrimitiveTag() ;

        // for the resultCode
        pattern[2] = UniversalTag.ENUMERATED.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.MODIFYDN_RESPONSE.getPrimitiveTag() ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Extended requests.
     *
     * @param digester the digester to add the rules to
     */
    private void addExtendedRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.EXTENDED_REQUEST.getPrimitiveTag() ;
        digester.addRule( pattern, new ExtendedRequestRule() ) ;

        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.EXTENDED_REQUEST.getPrimitiveTag() ;
        pattern[2] = LdapTag.EXTENDED_REQUEST_NAME_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ExtendedRequestOidRule() ) ;

        pattern[2] = LdapTag.EXTENDED_REQUEST_VALUE_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ExtendedRequestPayloadRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Extended responses.
     *
     * @param digester the digester to add the rules to
     */
    private void addExtendedResponseRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // modify pattern and addRule for the ExtendedResponse
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.EXTENDED_RESPONSE.getPrimitiveTag() ;
        digester.addRule( pattern, new ExtendedResponseRule() ) ;

        // modify pattern and addRule for the ExtendedResponse
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.EXTENDED_RESPONSE.getPrimitiveTag() ;

        // for the resultCode
        pattern[2] = UniversalTag.ENUMERATED.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for responseName
        pattern[2] = LdapTag.EXTENDED_RESPONSE_NAME_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ExtendedResponseOidRule() ) ;

        // for response value
        pattern[2] = LdapTag.EXTENDED_RESPONSE_VALUE_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ExtendedResponsePayloadRule() ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.EXTENDED_RESPONSE.getPrimitiveTag() ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    /**
     * Generic rules used to capture the messageId for the LDAP message.
     *
     * @param digester the digester to add the rules to
     */
    private void addMessageIdRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getValue() ;
        pattern[1] = UniversalTag.INTEGER.getValue() ;
        digester.addRule( pattern, new PrimitiveIntDecodeRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Delete responses.
     *
     * @param digester the digester to add the rules to
     */
    private void addDeleteResponseRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // for the DeleteResponse and the LdapResult
        pattern[0] = 0x10000000 ;
        pattern[1] = 0x4b000000 ;
        digester.addRule( pattern, new DeleteResponseRule() ) ;

        pattern = new int[3] ;
        pattern[0] = 0x10000000 ;
        pattern[1] = 0x4b000000 ;

        // for the resultCode
        pattern[2] = 0x0a000000 ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = 0x04000000 ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = 0x10000000 ;
        pattern[1] = 0x4b000000 ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = 0x04000000 ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Delete requests.
     *
     * @param digester the digester to add the rules to
     */
    private void addDeleteRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // for the DeleteRequest and the LdapResult
        pattern[0] = 0x10000000 ;
        pattern[1] = LdapTag.DEL_REQUEST.getPrimitiveTag() ;
        digester.addRule( pattern, new DeleteRequestRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Compare requests.
     *
     * @param digester the digester to add the rules to
     */
    private void addCompareRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.COMPARE_REQUEST.getPrimitiveTag() ;
        digester.addRule( pattern, new CompareRequestRule() ) ;

        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.COMPARE_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new CompareEntryRule() ) ;

        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.COMPARE_REQUEST.getPrimitiveTag() ;
        pattern[2] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new CompareAVARule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Compare responses.
     *
     * @param digester the digester to add the rules to
     */
    private void addCompareResponseRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // modify pattern and addRule for the CompareResponse
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.COMPARE_RESPONSE.getPrimitiveTag() ;
        digester.addRule( pattern, new CompareResponseRule() ) ;

        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.COMPARE_RESPONSE.getPrimitiveTag() ;

        // for the resultCode
        pattern[2] = 0x0a000000 ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.COMPARE_RESPONSE.getPrimitiveTag() ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Bind Responses.
     *
     * @param digester the digester to add the rules to
     */
    private void addBindResponseRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;

        // modify pattern and addRule for the BindResponse
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.BIND_RESPONSE.getPrimitiveTag() ;
        digester.addRule( pattern, new BindResponseRule() ) ;

        // modify pattern and addRule for the BindResponse
        pattern = new int[3] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.BIND_RESPONSE.getPrimitiveTag() ;

        // for the resultCode
        pattern[2] = UniversalTag.ENUMERATED.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultCodeRule() ) ;

        // for matchedDN and errorMessage
        pattern[2] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ResultMatchedDNRule() ) ;
        digester.addRule( pattern, new ErrorMessageRule() ) ;

        // for referral
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralRule() ) ;

        // for serverSaslCreds
        pattern[2] = LdapTag.SERVER_SASL_CREDS_TAG.getPrimitiveTag() ;
        digester.addRule( pattern, new PrimitiveOctetStringRule(
                LdapTag.SERVER_SASL_CREDS_TAG ) ) ;

        // for LDAPURLs of referral
        pattern = new int[4] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getPrimitiveTag() ;
        pattern[1] = LdapTag.BIND_RESPONSE.getPrimitiveTag() ;
        pattern[2] = LdapTag.REFERRAL_TAG.getPrimitiveTag() ;
        pattern[3] = UniversalTag.OCTET_STRING.getPrimitiveTag() ;
        digester.addRule( pattern, new ReferralUrlRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Bind requests.
     *
     * @todo implement sasl based binds as well - for now its only simple auth
     * @param digester the digester to add the rules to
     */
    private void addBindRequestRules( BERDigester digester )
    {
        int[] reqPat = { 0x10000000, 0x40000000 } ;
        digester.addRule( reqPat, new BindRequestRule() ) ;

        int[] versionPat = { 0x10000000, 0x40000000, 0x02000000 } ;
        digester.addRule( versionPat, new BindVersionRule() ) ;

        int[] namePat = { 0x10000000, 0x40000000, 0x04000000 } ;
        digester.addRule( namePat, new BindNameRule() ) ;

        int[] credPat = { 0x10000000, 0x40000000, 0x80000000 } ;
        digester.addRule( credPat, new BindSimpleCredentialsRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Unbind requests.
     *
     * @param digester the digester to add the rules to
     */
    private void addUnbindRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getValue() ;
        pattern[1] = LdapTag.UNBIND_REQUEST.getValue() ;
        digester.addRule( pattern, new UnbindRequestRule() ) ;
    }


    /**
     * Adds digester rules for processing LDAPv3 Abandon requests.
     *
     * @param digester the digester to add the rules to
     */
    private void addAbandonRequestRules( BERDigester digester )
    {
        int[] pattern = new int[2] ;
        pattern[0] = UniversalTag.SEQUENCE_SEQUENCE_OF.getValue() ;
        pattern[1] = LdapTag.ABANDON_REQUEST.getValue() ;
        digester.addRule( pattern, new AbandonRequestRule() ) ;
    }
}
