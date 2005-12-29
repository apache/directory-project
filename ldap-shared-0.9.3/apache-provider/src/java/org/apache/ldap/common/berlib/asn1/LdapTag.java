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


import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.primitives.ContextSpecificTag;

import java.util.List;
import java.util.Map;


/**
 * Prefabricated LDAP APPLICATION type class tags encoded as integers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class LdapTag extends TagEnum
{
    /** the bind request tag encoded as an integer */
    private static final int BIND_REQUEST_TAG = 0x60000000 ;
    /** the bind response tag encoded as an integer */
    private static final int BIND_RESPONSE_TAG = 0x41000000 ;

    /** the unbind request tag encoded as an integer */
    private static final int UNBIND_REQUEST_TAG = 0x42000000 ;

    /** the search request tag encoded as an integer */
    private static final int SEARCH_REQUEST_TAG = 0x63000000 ;
    /** the search result entry response tag encoded as an integer */
    private static final int SEARCH_RESULT_ENTRY_TAG = 0x64000000 ;
    /** the search result done tag encoded as an integer */
    private static final int SEARCH_RESULT_DONE_TAG = 0x65000000 ;
    /** the search result reference response tag encoded as an integer */
    private static final int SEARCH_RESULT_REFERENCE_TAG = 0x73000000 ;

    /** the modify request tag encoded as an integer */
    private static final int MODIFY_REQUEST_TAG = 0x66000000 ;
    /** the modify response tag encoded as an integer */
    private static final int MODIFY_RESPONSE_TAG = 0x67000000 ;

    /** the add request tag encoded as an integer */
    private static final int ADD_REQUEST_TAG = 0x68000000 ;
    /** the add response tag encoded as an integer */
    private static final int ADD_RESPONSE_TAG = 0x69000000 ;

    /** the delete request tag encoded as an integer */
    private static final int DEL_REQUEST_TAG = 0x4a000000 ;
    /** the delete response tag encoded as an integer */
    private static final int DEL_RESPONSE_TAG = 0x6b000000 ;

    /** the modify dn request tag encoded as an integer */
    private static final int MODIFYDN_REQUEST_TAG = 0x6c000000 ;
    /** the modify dn response tag encoded as an integer */
    private static final int MODIFYDN_RESPONSE_TAG = 0x6d000000 ;

    /** the compare request tag encoded as an integer */
    private static final int COMPARE_REQUEST_TAG = 0x6e000000 ;
    /** the compare response tag encoded as an integer */
    private static final int COMPARE_RESPONSE_TAG = 0x6f000000 ;

    /** the abandon request tag encoded as an integer */
    private static final int ABANDON_REQUEST_TAG = 0x50000000 ;

    /** the extended request tag encoded as an integer */
    private static final int EXTENDED_REQUEST_TAG = 0x77000000 ;
    /** the extended response tag encoded as an integer */
    private static final int EXTENDED_RESPONSE_TAG = 0x78000000 ;


    // -----------------------------------------------------------------------
    // Tag Identifiers
    // -----------------------------------------------------------------------


    /** the bind request id as an integer */
    static final int BIND_REQUEST_ID =               0 ;
    /** the bind response id as an integer */
    static final int BIND_RESPONSE_ID =              1 ;

    /** the unbind request id as an integer */
    static final int UNBIND_REQUEST_ID =             2 ;

    /** the search request id as an integer */
    static final int SEARCH_REQUEST_ID =             3 ;
    /** the search result entry response id as an integer */
    static final int SEARCH_RESULT_ENTRY_ID =        4 ;
    /** the search result done id as an integer */
    static final int SEARCH_RESULT_DONE_ID =         5 ;
    /** the search result reference response id as an integer */
    static final int SEARCH_RESULT_REFERENCE_ID =    19 ;

    /** the modify request id as an integer */
    static final int MODIFY_REQUEST_ID =             6 ;
    /** the modify response id as an integer */
    static final int MODIFY_RESPONSE_ID =            7 ;

    /** the add request id as an integer */
    static final int ADD_REQUEST_ID =                8 ;
    /** the add response id as an integer */
    static final int ADD_RESPONSE_ID =               9 ;

    /** the delete request id as an integer */
    static final int DEL_REQUEST_ID =                10 ;
    /** the delete response id as an integer */
    static final int DEL_RESPONSE_ID =               11 ;

    /** the modify dn request id as an integer */
    static final int MODIFYDN_REQUEST_ID =           12 ;
    /** the modify dn response id as an integer */
    static final int MODIFYDN_RESPONSE_ID =          13 ;

    /** the compare request id as an integer */
    static final int COMPARE_REQUEST_ID =            14 ;
    /** the compare response id as an integer */
    static final int COMPARE_RESPONSE_ID =           15 ;

    /** the abandon request id as an integer */
    static final int ABANDON_REQUEST_ID =            16 ;

    /** the extended request id as an integer */
    static final int EXTENDED_REQUEST_ID =           23 ;
    /** the extended response id as an integer */
    static final int EXTENDED_RESPONSE_ID =          24 ;

    
    // -----------------------------------------------------------------------
    // Enumerations
    // -----------------------------------------------------------------------

    
    public static final LdapTag BIND_REQUEST = new LdapTag(
            "BIND_REQUEST", BIND_REQUEST_TAG, BIND_REQUEST_ID ) ;
    public static final LdapTag BIND_RESPONSE = new LdapTag(
            "BIND_RESPONSE", BIND_RESPONSE_TAG, BIND_RESPONSE_ID ) ;

    public static final LdapTag UNBIND_REQUEST = new LdapTag(
            "UNBIND_REQUEST", UNBIND_REQUEST_TAG, UNBIND_REQUEST_ID ) ;

    public static final LdapTag SEARCH_REQUEST = new LdapTag(
            "SEARCH_REQUEST", SEARCH_REQUEST_TAG, SEARCH_REQUEST_ID ) ;
    public static final LdapTag SEARCH_RESULT_DONE = new LdapTag(
            "SEARCH_RESULT_DONE", SEARCH_RESULT_DONE_TAG,
            SEARCH_RESULT_DONE_ID ) ;
    public static final LdapTag SEARCH_RESULT_ENTRY = new LdapTag(
            "SEARCH_RESULT_ENTRY", SEARCH_RESULT_ENTRY_TAG,
            SEARCH_RESULT_ENTRY_ID ) ;
    public static final LdapTag SEARCH_RESULT_REFERENCE = new LdapTag(
            "SEARCH_RESULT_REFERENCE", SEARCH_RESULT_REFERENCE_TAG,
            SEARCH_RESULT_REFERENCE_ID ) ;

    public static final LdapTag MODIFY_REQUEST = new LdapTag(
            "MODIFY_REQUEST", MODIFY_REQUEST_TAG, MODIFY_REQUEST_ID ) ;
    public static final LdapTag MODIFY_RESPONSE = new LdapTag(
            "MODIFY_RESPONSE", MODIFY_RESPONSE_TAG, MODIFY_RESPONSE_ID ) ;

    public static final LdapTag ADD_REQUEST = new LdapTag(
            "ADD_REQUEST", ADD_REQUEST_TAG, ADD_REQUEST_ID ) ;
    public static final LdapTag ADD_RESPONSE = new LdapTag(
            "ADD_RESPONSE", ADD_RESPONSE_TAG, ADD_RESPONSE_ID ) ;

    public static final LdapTag DEL_REQUEST = new LdapTag(
            "DEL_REQUEST", DEL_REQUEST_TAG, DEL_REQUEST_ID ) ;
    public static final LdapTag DEL_RESPONSE = new LdapTag(
            "DEL_RESPONSE", DEL_RESPONSE_TAG, DEL_RESPONSE_ID ) ;

    public static final LdapTag MODIFYDN_REQUEST = new LdapTag(
            "MODIFYDN_REQUEST", MODIFYDN_REQUEST_TAG, MODIFYDN_REQUEST_ID ) ;
    public static final LdapTag MODIFYDN_RESPONSE = new LdapTag(
            "MODIFYDN_RESPONSE", MODIFYDN_RESPONSE_TAG,
            MODIFYDN_RESPONSE_ID ) ;

    public static final LdapTag COMPARE_REQUEST = new LdapTag(
            "COMPARE_REQUEST", COMPARE_REQUEST_TAG, COMPARE_REQUEST_ID ) ;
    public static final LdapTag COMPARE_RESPONSE = new LdapTag(
            "COMPARE_RESPONSE", COMPARE_RESPONSE_TAG, COMPARE_RESPONSE_ID ) ;

    public static final LdapTag ABANDON_REQUEST = new LdapTag(
            "ABANDON_REQUEST", ABANDON_REQUEST_TAG, ABANDON_REQUEST_ID ) ;

    public static final LdapTag EXTENDED_REQUEST = new LdapTag(
            "EXTENDED_REQUEST", EXTENDED_REQUEST_TAG, EXTENDED_REQUEST_ID ) ;
    public static final LdapTag EXTENDED_RESPONSE = new LdapTag(
            "EXTENDED_RESPONSE", EXTENDED_RESPONSE_TAG,
            EXTENDED_RESPONSE_ID ) ;


    /** Context specific 8 tag */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_11 =
            new ContextSpecificTag( 11, true );

    /** Context specific 8 tag */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_10 =
            new ContextSpecificTag( 10, true );

    /** Context specific 8 tag */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_8 =
            new ContextSpecificTag( 8, true );

    /** Context specific 7 tag */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_7 =
            new ContextSpecificTag( 7, true );

    /** Context specific 6 tag */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_6 =
            new ContextSpecificTag( 6, true );

    /** Context specific 5 tag */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_5 =
            new ContextSpecificTag( 5, true );

    /** Context specific tag used for SearchRequest expr node greaterOrEqual*/
    public static final ContextSpecificTag GREATER_OR_EQUAL_TAG =
            CONTEXT_SPECIFIC_TAG_5;

    /** Context specific 3 tag */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_3 =
            new ContextSpecificTag( 3, true ) ;

    /** Context specific tag used for LDAPResult referrals */
    public static final ContextSpecificTag REFERRAL_TAG =
            CONTEXT_SPECIFIC_TAG_3 ;

    /** Context specific tag used for BindResponse serverSaslCreds */
    public static final ContextSpecificTag SERVER_SASL_CREDS_TAG =
            CONTEXT_SPECIFIC_TAG_7 ;

    /** Context specific tag used for CONTEXT_SPECIFIC 0 */
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_0 =
            new ContextSpecificTag( 0, false ) ;

    /** Context specific tag used for ExtendedRequest requestName */
    public static final ContextSpecificTag EXTENDED_REQUEST_NAME_TAG =
            CONTEXT_SPECIFIC_TAG_0 ;

    /** Context specific tag used for ExtendedResponse responseName */
    public static final ContextSpecificTag EXTENDED_RESPONSE_NAME_TAG =
            new ContextSpecificTag( 10, false ) ;

    /** Context specific tag used for ExtendedResponse response value */
    public static final ContextSpecificTag EXTENDED_RESPONSE_VALUE_TAG =
            new ContextSpecificTag( 11, false ) ;

    /** Context specific tag used for ModifyDn request new superior dn */
    public static final ContextSpecificTag MODIFYDN_REQUEST_NEWSUP_TAG =
            CONTEXT_SPECIFIC_TAG_0 ;

    /** Context specific tag used for Search request new superior dn */
    public static final ContextSpecificTag SEARCH_REQUEST_EQUALITY_MATH_TAG =
            CONTEXT_SPECIFIC_TAG_3 ;

    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_4 =
        new ContextSpecificTag( 4, true );

    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_1 =
            new ContextSpecificTag( 1, true ) ;

    /** Context specific tag used for ExtendedRequest request value */
    public static final ContextSpecificTag EXTENDED_REQUEST_VALUE_TAG =
           CONTEXT_SPECIFIC_TAG_1;
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_2 = 
            new ContextSpecificTag( 2, true ) ;
    public static final ContextSpecificTag CONTEXT_SPECIFIC_TAG_9 = 
            new ContextSpecificTag( 9, true ) ;


    // -----------------------------------------------------------------------
    // Members
    // -----------------------------------------------------------------------

    /**
     * Private constructor so no other instances can be created other than the
     * public static constants in this class.
     *
     * @param name a string name for the enumeration value.
     * @param value the integer value of the enumeration.
     */
    private LdapTag( final String name, final int value, final int id )
    {
        super( name, value, id ) ;
    }


    /**
     * Gets a Factory.
     *
     * @param value the first octet of the TLV
     * @return the valued enum for the LDAPv3 ASN.1 type tag
     */
    public static LdapTag getLdapTag( int value )
    {
        LdapTag type = null ;

        switch ( value )
        {
            case( BIND_REQUEST_TAG ):
                type = BIND_REQUEST ;
                break ;
            case( BIND_RESPONSE_TAG ):
                type = BIND_RESPONSE ;
                break ;
            case( UNBIND_REQUEST_TAG ):
                type = UNBIND_REQUEST ;
                break ;
            case( SEARCH_REQUEST_TAG ):
                type = SEARCH_REQUEST ;
                break ;
            case( SEARCH_RESULT_ENTRY_TAG ):
                type = SEARCH_RESULT_ENTRY ;
                break ;
            case( SEARCH_RESULT_DONE_TAG ):
                type = SEARCH_RESULT_DONE ;
                break ;
            case( SEARCH_RESULT_REFERENCE_TAG ):
                type = SEARCH_RESULT_REFERENCE ;
                break ;
            case( MODIFY_REQUEST_TAG ):
                type = MODIFY_REQUEST ;
                break ;
            case( MODIFY_RESPONSE_TAG ):
                type = MODIFY_RESPONSE ;
                break ;
            case( ADD_REQUEST_TAG ):
                type = ADD_REQUEST ;
                break ;
            case( ADD_RESPONSE_TAG ):
                type = ADD_RESPONSE ;
                break ;
            case( DEL_REQUEST_TAG ):
                type = DEL_REQUEST ;
                break ;
            case( DEL_RESPONSE_TAG ):
                type = DEL_RESPONSE ;
                break ;
            case( MODIFYDN_REQUEST_TAG ):
                type = MODIFYDN_REQUEST ;
                break ;
            case( MODIFYDN_RESPONSE_TAG ):
                type = MODIFYDN_RESPONSE ;
                break ;
            case( COMPARE_REQUEST_TAG ):
                type = COMPARE_REQUEST ;
                break ;
            case( COMPARE_RESPONSE_TAG ):
                type = COMPARE_RESPONSE ;
                break ;
            case( ABANDON_REQUEST_TAG ):
                type = ABANDON_REQUEST ;
                break ;
            case( EXTENDED_REQUEST_TAG ):
                type = EXTENDED_REQUEST ;
                break ;
            case( EXTENDED_RESPONSE_TAG ):
                type = EXTENDED_RESPONSE ;
                break ;
            default:
                String msg ;
                // no reasonable protocol has more than 1K tags defined
                if ( value < 10000 && value > -1 )
                {
                    msg = "Looks like you're using an id instead of a tag" ;
                }
                else
                {
                    msg = "Tag value of " + value + " not recognized" ;
                }

                throw new IllegalArgumentException( msg ) ;
        }

        return type ;
    }


    /**
     * Gets the ASN.1 LDAP APPLICATION type tag's enum using a tag id.
     *
     * @param id the first octet of the TLV
     * @return the valued enum for the ASN.1 LDAPv3 APPLICATION type tag
     */
    public static LdapTag getLdapTagById( int id )
    {
        LdapTag type = null ;

        switch ( id )
        {
            case( BIND_REQUEST_ID ):
                type = BIND_REQUEST ;
                break ;
            case( BIND_RESPONSE_ID ):
                type = BIND_RESPONSE ;
                break ;
            case( UNBIND_REQUEST_ID ):
                type = UNBIND_REQUEST ;
                break ;
            case( SEARCH_REQUEST_ID ):
                type = SEARCH_REQUEST ;
                break ;
            case( SEARCH_RESULT_ENTRY_ID ):
                type = SEARCH_RESULT_ENTRY ;
                break ;
            case( SEARCH_RESULT_DONE_ID ):
                type = SEARCH_RESULT_DONE ;
                break ;
            case( SEARCH_RESULT_REFERENCE_ID ):
                type = SEARCH_RESULT_REFERENCE ;
                break ;
            case( MODIFY_REQUEST_ID ):
                type = MODIFY_REQUEST ;
                break ;
            case( MODIFY_RESPONSE_ID ):
                type = MODIFY_RESPONSE ;
                break ;
            case( ADD_REQUEST_ID ):
                type = ADD_REQUEST ;
                break ;
            case( ADD_RESPONSE_ID ):
                type = ADD_RESPONSE ;
                break ;
            case( DEL_REQUEST_ID ):
                type = DEL_REQUEST ;
                break ;
            case( DEL_RESPONSE_ID ):
                type = DEL_RESPONSE ;
                break ;
            case( MODIFYDN_REQUEST_ID ):
                type = MODIFYDN_REQUEST ;
                break ;
            case( MODIFYDN_RESPONSE_ID ):
                type = MODIFYDN_RESPONSE ;
                break ;
            case( COMPARE_REQUEST_ID ):
                type = COMPARE_REQUEST ;
                break ;
            case( COMPARE_RESPONSE_ID ):
                type = COMPARE_RESPONSE ;
                break ;
            case( ABANDON_REQUEST_ID ):
                type = ABANDON_REQUEST ;
                break ;
            case( EXTENDED_REQUEST_ID ):
                type = EXTENDED_REQUEST ;
                break ;
            case( EXTENDED_RESPONSE_ID ):
                type = EXTENDED_RESPONSE ;
                break ;
            default:
                String msg ;
                if ( id > 10000 || id < 0 )
                {
                    msg = "Looks like you're using a tag instead of an id" ;
                }
                else
                {
                    msg = "Id of " + id + " not recognized as a LDAPv3 id" ;
                }

                throw new IllegalArgumentException( msg ) ;
        }

        return type ;
    }
}
