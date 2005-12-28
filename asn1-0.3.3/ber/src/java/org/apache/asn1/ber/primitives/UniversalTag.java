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
package org.apache.asn1.ber.primitives ;


import org.apache.asn1.ber.TagEnum ;


/**
 * Type safe enum for ASN.1 UNIVERSAL class tags.  The tags values are
 * constructed using the SNACC representation for tags without the 
 * primitive/constructed bit.  This is done because several bit, octet and
 * character string types can be encoded as primitives or as constructed types 
 * to chunk the value out.
 * <p>
 * These tags can have one of the following values:
 * <p>
 * <table border=1 cellspacing=1 width=60% >
 * <tr><th>Id</th><th>Usage</th></tr>
 * <tr><td>[UNIVERSAL 0]</td>       <td>reserved for BER</td></tr>
 * <tr><td>[UNIVERSAL 1]</td>       <td>BOOLEAN</td></tr>
 * <tr><td>[UNIVERSAL 2]</td>       <td>INTEGER</td></tr>
 * <tr><td>[UNIVERSAL 3]</td>       <td>BIT STRING</td></tr>
 * <tr><td>[UNIVERSAL 4]</td>       <td>OCTET STRING</td></tr>
 * <tr><td>[UNIVERSAL 5]</td>       <td>NULL</td></tr>
 * <tr><td>[UNIVERSAL 6]</td>       <td>OBJECT IDENTIFIER</td></tr>
 * <tr><td>[UNIVERSAL 7]</td>       <td>ObjectDescriptor</td></tr>
 * <tr><td>[UNIVERSAL 8]</td>       <td>EXTERNAL, INSTANCE OF</td></tr>
 * <tr><td>[UNIVERSAL 9]</td>       <td>REAL</td></tr>
 * <tr><td>[UNIVERSAL 10]</td>      <td>ENUMERATED</td></tr>
 * <tr><td>[UNIVERSAL 11]</td>      <td>EMBEDDED PDV</td></tr>
 * <tr><td>[UNIVERSAL 12]</td>      <td>UTF8String</td></tr>
 * <tr><td>[UNIVERSAL 13]</td>      <td>RELATIVE-OID</td></tr>
 * <tr><td>[UNIVERSAL 14]</td>      <td>reserved for future use</td></tr>
 * <tr><td>[UNIVERSAL 15]</td>      <td>reserved for future use</td></tr>
 * <tr><td>[UNIVERSAL 16]</td>      <td>SEQUENCE, SEQUENCE OF</td></tr>
 * <tr><td>[UNIVERSAL 17]</td>      <td>SET, SET OF</td></tr>
 * <tr><td>[UNIVERSAL 18]</td>      <td>NumericString</td></tr>
 * <tr><td>[UNIVERSAL 19]</td>      <td>PrintableString</td></tr>
 * <tr><td>[UNIVERSAL 20]</td>      <td>TeletexString, T61String</td></tr>
 * <tr><td>[UNIVERSAL 21]</td>      <td>VideotexString</td></tr>
 * <tr><td>[UNIVERSAL 22]</td>      <td>IA5String</td></tr>
 * <tr><td>[UNIVERSAL 23]</td>      <td>UTCTime</td></tr>
 * <tr><td>[UNIVERSAL 24]</td>      <td>GeneralizedTime</td></tr>
 * <tr><td>[UNIVERSAL 25]</td>      <td>GraphicString</td></tr>
 * <tr><td>[UNIVERSAL 26]</td>      <td>VisibleString, ISO646String</td></tr>
 * <tr><td>[UNIVERSAL 27]</td>      <td>GeneralString</td></tr>
 * <tr><td>[UNIVERSAL 28]</td>      <td>UniversalString</td></tr>
 * <tr><td>[UNIVERSAL 29]</td>      <td>CHARACTER STRING</td></tr>
 * <tr><td>[UNIVERSAL 30]</td>      <td>BMPString</td></tr>
 * <tr><td>[UNIVERSAL 31]</td>      <td>reserved for future use</td></tr>
 * </table> 
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class UniversalTag extends TagEnum
{
    /** value for the tag */
    private static final int RESERVED0_VAL =                 0x00000000 ;
    /** value for the tag */
    private static final int BOOLEAN_VAL =                   0x01000000 ;
    /** value for the tag */
    private static final int INTEGER_VAL =                   0x02000000 ;
    /** value for the tag */
    private static final int BIT_STRING_VAL =                0x03000000 ;
    /** value for the tag */
    private static final int OCTET_STRING_VAL =              0x04000000 ;
    /** value for the tag */
    private static final int NULL_VAL =                      0x05000000 ;
    /** value for the tag */
    private static final int OBJECT_IDENTIFIER_VAL =         0x06000000 ;
    /** value for the tag */
    private static final int OBJECT_DESCRIPTOR_VAL =         0x07000000 ;
    /** value for the tag */
    private static final int EXTERNAL_INSTANCE_OF_VAL =      0x08000000 ;
    /** value for the tag */
    private static final int REAL_VAL =                      0x09000000 ;
    /** value for the tag */
    private static final int ENUMERATED_VAL =                0x0a000000 ;
    /** value for the tag */
    private static final int EMBEDDED_PDV_VAL =              0x0b000000 ;
    /** value for the tag */
    private static final int UTF8_STRING_VAL =               0x0c000000 ;
    /** value for the tag */
    private static final int RELATIVE_OID_VAL =              0x0d000000 ;
    /** value for the tag */
    private static final int RESERVED14_VAL =                0x0e000000 ;
    /** value for the tag */
    private static final int RESERVED15_VAL =                0x0f000000 ;
    /** value for the tag */
    private static final int SEQUENCE_SEQUENCE_OF_VAL =      0x10000000 ;
    /** value for the tag */
    private static final int SET_SET_OF_VAL =                0x11000000 ;
    /** value for the tag */
    private static final int NUMERIC_STRING_VAL =            0x12000000 ;
    /** value for the tag */
    private static final int PRINTABLE_STRING_VAL =          0x13000000 ;
    /** value for the tag */
    private static final int TELETEX_STRING_VAL =            0x14000000 ;
    /** value for the tag */
    private static final int VIDEOTEX_STRING_VAL =           0x15000000 ;
    /** value for the tag */
    private static final int IA5_STRING_VAL =                0x16000000 ;
    /** value for the tag */
    private static final int UTC_TIME_VAL =                  0x17000000 ;
    /** value for the tag */
    private static final int GENERALIZED_TIME_VAL =          0x18000000 ;
    /** value for the tag */
    private static final int GRAPHIC_STRING_VAL =            0x19000000 ;
    /** value for the tag */
    private static final int VISIBLE_STRING_VAL =            0x1a000000 ;
    /** value for the tag */
    private static final int GENERAL_STRING_VAL =            0x1b000000 ;
    /** value for the tag */
    private static final int UNIVERSAL_STRING_VAL =          0x1c000000 ;
    /** value for the tag */
    private static final int CHARACTER_STRING_VAL =          0x1d000000 ;
    /** value for the tag */
    private static final int BMP_STRING_VAL =                0x1e000000 ;
    /** value for the tag */
    private static final int RESERVED31_VAL =                0x1f000000 ;

    /** value for the tag */
    private static final int RESERVED0_ID =                 0 ;
    /** value for the tag */
    private static final int BOOLEAN_ID =                   1 ;
    /** value for the tag */
    private static final int INTEGER_ID =                   2 ;
    /** value for the tag */
    private static final int BIT_STRING_ID =                3 ;
    /** value for the tag */
    private static final int OCTET_STRING_ID =              4 ;
    /** value for the tag */
    private static final int NULL_ID =                      5 ;
    /** value for the tag */
    private static final int OBJECT_IDENTIFIER_ID =         6 ;
    /** value for the tag */
    private static final int OBJECT_DESCRIPTOR_ID =         7 ;
    /** value for the tag */
    private static final int EXTERNAL_INSTANCE_OF_ID =      8 ;
    /** value for the tag */
    private static final int REAL_ID =                      9 ;
    /** value for the tag */
    private static final int ENUMERATED_ID =                10 ;
    /** value for the tag */
    private static final int EMBEDDED_PDV_ID =              11 ;
    /** value for the tag */
    private static final int UTF8_STRING_ID =               12 ;
    /** value for the tag */
    private static final int RELATIVE_OID_ID =              13 ;
    /** value for the tag */
    private static final int RESERVED14_ID =                14 ;
    /** value for the tag */
    private static final int RESERVED15_ID =                15 ;
    /** value for the tag */
    private static final int SEQUENCE_SEQUENCE_OF_ID =      16 ;
    /** value for the tag */
    private static final int SET_SET_OF_ID =                17 ;
    /** value for the tag */
    private static final int NUMERIC_STRING_ID =            18 ;
    /** value for the tag */
    private static final int PRINTABLE_STRING_ID =          19 ;
    /** value for the tag */
    private static final int TELETEX_STRING_ID =            20 ;
    /** value for the tag */
    private static final int VIDEOTEX_STRING_ID =           21 ;
    /** value for the tag */
    private static final int IA5_STRING_ID =                22 ;
    /** value for the tag */
    private static final int UTC_TIME_ID =                  23 ;
    /** value for the tag */
    private static final int GENERALIZED_TIME_ID =          24 ;
    /** value for the tag */
    private static final int GRAPHIC_STRING_ID =            25 ;
    /** value for the tag */
    private static final int VISIBLE_STRING_ID =            26 ;
    /** value for the tag */
    private static final int GENERAL_STRING_ID =            27 ;
    /** value for the tag */
    private static final int UNIVERSAL_STRING_ID =          28 ;
    /** value for the tag */
    private static final int CHARACTER_STRING_ID =          29 ;
    /** value for the tag */
    private static final int BMP_STRING_ID =                30 ;
    /** value for the tag */
    private static final int RESERVED31_ID =                31 ;

    /** enum for the tag */
    public static final UniversalTag BOOLEAN = 
        new UniversalTag( "BOOLEAN", BOOLEAN_VAL, BOOLEAN_ID ) ;
    /** enum for the tag */
    public static final UniversalTag RESERVED0 = 
        new UniversalTag( "RESERVED0", RESERVED0_VAL, RESERVED0_ID ) ;
    /** enum for the tag */
    public static final UniversalTag INTEGER = 
        new UniversalTag( "INTEGER", INTEGER_VAL, INTEGER_ID ) ;
    /** enum for the tag */
    public static final UniversalTag BIT_STRING = 
        new UniversalTag( "BIT_STRING", BIT_STRING_VAL, BIT_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag OCTET_STRING = 
        new UniversalTag( "OCTET_STRING", OCTET_STRING_VAL, OCTET_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag NULL = 
        new UniversalTag( "NULL", NULL_VAL, NULL_ID ) ;
    /** enum for the tag */
    public static final UniversalTag OBJECT_IDENTIFIER = 
        new UniversalTag( "OBJECT_IDENTIFIER", OBJECT_IDENTIFIER_VAL,
                OBJECT_IDENTIFIER_ID ) ;
    /** enum for the tag */
    public static final UniversalTag OBJECT_DESCRIPTOR = 
        new UniversalTag( "OBJECT_DESCRIPTOR", OBJECT_DESCRIPTOR_VAL,
                OBJECT_DESCRIPTOR_ID ) ;
    /** enum for the tag */
    public static final UniversalTag EXTERNAL_INSTANCE_OF = 
        new UniversalTag( "EXTERNAL_INSTANCE_OF", EXTERNAL_INSTANCE_OF_VAL,
                EXTERNAL_INSTANCE_OF_ID ) ;
    /** enum for the tag */
    public static final UniversalTag REAL = 
        new UniversalTag( "REAL", REAL_VAL, REAL_ID ) ;
    /** enum for the tag */
    public static final UniversalTag ENUMERATED = 
        new UniversalTag( "ENUMERATED", ENUMERATED_VAL, ENUMERATED_ID ) ;
    /** enum for the tag */
    public static final UniversalTag EMBEDDED_PDV = 
        new UniversalTag( "EMBEDDED_PDV", EMBEDDED_PDV_VAL, EMBEDDED_PDV_ID ) ;
    /** enum for the tag */
    public static final UniversalTag UTF8_STRING = 
        new UniversalTag( "UTF8_STRING", UTF8_STRING_VAL, UTF8_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag RELATIVE_OID = 
        new UniversalTag( "RELATIVE_OID", RELATIVE_OID_VAL, RELATIVE_OID_ID ) ;
    /** enum for the tag */
    public static final UniversalTag RESERVED14 = 
        new UniversalTag( "RESERVED14", RESERVED14_VAL, RESERVED14_ID ) ;
    /** enum for the tag */
    public static final UniversalTag RESERVED15 = 
        new UniversalTag( "RESERVED15", RESERVED15_VAL, RESERVED15_ID ) ;
    /** enum for the tag */
    public static final UniversalTag SEQUENCE_SEQUENCE_OF = 
        new UniversalTag( "SEQUENCE_SEQUENCE_OF", SEQUENCE_SEQUENCE_OF_VAL,
                SEQUENCE_SEQUENCE_OF_ID ) ;
    /** enum for the tag */
    public static final UniversalTag SET_SET_OF = 
        new UniversalTag( "SET_SET_OF", SET_SET_OF_VAL, SET_SET_OF_ID ) ;
    /** enum for the tag */
    public static final UniversalTag NUMERIC_STRING = 
        new UniversalTag( "NUMERIC_STRING", NUMERIC_STRING_VAL,
                NUMERIC_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag PRINTABLE_STRING = 
        new UniversalTag( "PRINTABLE_STRING", PRINTABLE_STRING_VAL,
                PRINTABLE_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag TELETEX_STRING = 
        new UniversalTag( "TELETEX_STRING", TELETEX_STRING_VAL,
                TELETEX_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag VIDEOTEX_STRING = 
        new UniversalTag( "VIDEOTEX_STRING", VIDEOTEX_STRING_VAL,
                VIDEOTEX_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag IA5_STRING = 
        new UniversalTag( "IA5_STRING", IA5_STRING_VAL, IA5_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag UTC_TIME = 
        new UniversalTag( "UTC_TIME", UTC_TIME_VAL, UTC_TIME_ID ) ;
    /** enum for the tag */
    public static final UniversalTag GENERALIZED_TIME = 
        new UniversalTag( "GENERALIZED_TIME", GENERALIZED_TIME_VAL,
                GENERALIZED_TIME_ID ) ;
    /** enum for the tag */
    public static final UniversalTag GRAPHIC_STRING = 
        new UniversalTag( "GRAPHIC_STRING", GRAPHIC_STRING_VAL,
                GRAPHIC_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag VISIBLE_STRING = 
        new UniversalTag( "VISIBLE_STRING", VISIBLE_STRING_VAL,
                VISIBLE_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag GENERAL_STRING = 
        new UniversalTag( "GENERAL_STRING", GENERAL_STRING_VAL,
                GENERAL_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag UNIVERSAL_STRING = 
        new UniversalTag( "UNIVERSAL_STRING", UNIVERSAL_STRING_VAL,
                UNIVERSAL_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag CHARACTER_STRING = 
        new UniversalTag( "CHARACTER_STRING", CHARACTER_STRING_VAL,
                CHARACTER_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag BMP_STRING = 
        new UniversalTag( "BMP_STRING", BMP_STRING_VAL, BMP_STRING_ID ) ;
    /** enum for the tag */
    public static final UniversalTag RESERVED31 = 
        new UniversalTag( "RESERVED31", RESERVED31_VAL, RESERVED31_ID ) ;


    // -----------------------------------------------------------------------
    // C O N S T R U C T O R S
    // -----------------------------------------------------------------------


    /**
     * Private constructor so no other instances can be created other than the
     * public static constants in this class.
     *
     * @param name a string name for the enumeration value.
     * @param value the integer value of the enumeration.
     */
    private UniversalTag( final String name, final int value, final int id )
    {
        super( name, value, id ) ;
    }


    // -----------------------------------------------------------------------
    // Members
    // -----------------------------------------------------------------------


    /**
     * Gets the ASN.1 UNIVERSAL type tag's enum using a tag value.
     * 
     * @param value the first octet of the TLV
     * @return the valued enum for the ASN.1 UNIVERSAL type tag
     */
    public static UniversalTag getUniversalTag( int value )
    {
        UniversalTag type = null ;
        value &= 0xdfffffff ;
        
        switch ( value )
        {
            case( RESERVED0_VAL ):
                type = RESERVED0 ;
                break ;
            case( BOOLEAN_VAL ):
                type = BOOLEAN ;
                break ;
            case( INTEGER_VAL ):
                type = INTEGER ;
                break ;
            case( BIT_STRING_VAL ):
                type = BIT_STRING ;
                break ;
            case( OCTET_STRING_VAL ):
                type = OCTET_STRING ;
                break ;
            case( NULL_VAL ):
                type = NULL ;
                break ;
            case( OBJECT_IDENTIFIER_VAL ):
                type = OBJECT_IDENTIFIER ;
                break ;
            case( OBJECT_DESCRIPTOR_VAL ):
                type = OBJECT_DESCRIPTOR ;
                break ;
            case( EXTERNAL_INSTANCE_OF_VAL ):
                type = EXTERNAL_INSTANCE_OF ;
                break ;
            case( REAL_VAL ):
                type = REAL ;
                break ;
            case( ENUMERATED_VAL ):
                type = ENUMERATED ;
                break ;
            case( EMBEDDED_PDV_VAL ):
                type = EMBEDDED_PDV ;
                break ;
            case( UTF8_STRING_VAL ):
                type = UTF8_STRING ;
                break ;
            case( RELATIVE_OID_VAL ):
                type = RELATIVE_OID ;
                break ;
            case( RESERVED14_VAL ):
                type = RESERVED14 ;
                break ;
            case( RESERVED15_VAL ):
                type = RESERVED15 ;
                break ;
            case( SEQUENCE_SEQUENCE_OF_VAL ):
                type = SEQUENCE_SEQUENCE_OF ;
                break ;
            case( SET_SET_OF_VAL ):
                type = SET_SET_OF ;
                break ;
            case( NUMERIC_STRING_VAL ):
                type = NUMERIC_STRING ;
                break ;
            case( PRINTABLE_STRING_VAL ):
                type = PRINTABLE_STRING ;
                break ;
            case( TELETEX_STRING_VAL ):
                type = TELETEX_STRING ;
                break ;
            case( VIDEOTEX_STRING_VAL ):
                type = VIDEOTEX_STRING ;
                break ;
            case( IA5_STRING_VAL ):
                type = IA5_STRING ;
                break ;
            case( UTC_TIME_VAL ):
                type = UTC_TIME ;
                break ;
            case( GENERALIZED_TIME_VAL ):
                type = GENERALIZED_TIME ;
                break ;
            case( GRAPHIC_STRING_VAL ):
                type = GRAPHIC_STRING ;
                break ;
            case( VISIBLE_STRING_VAL ):
                type = VISIBLE_STRING ;
                break ;
            case( GENERAL_STRING_VAL ):
                type = GENERAL_STRING ;
                break ;
            case( UNIVERSAL_STRING_VAL ):
                type = UNIVERSAL_STRING ;
                break ;
            case( CHARACTER_STRING_VAL ):
                type = CHARACTER_STRING ;
                break ;
            case( BMP_STRING_VAL ):
                type = BMP_STRING ;
                break ;
            case( RESERVED31_VAL ):
                type = RESERVED31 ;
                break ;
            default:
                String msg ;
                if ( value < 31 && value > -1 )
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
     * Gets the ASN.1 UNIVERSAL type tag's enum using a tag value.
     *
     * @param id the first octet of the TLV
     * @return the valued enum for the ASN.1 UNIVERSAL type tag
     */
    public static UniversalTag getUniversalTagById( int id )
    {
        UniversalTag type = null ;

        switch ( id )
        {
            case( RESERVED0_ID ):
                type = RESERVED0 ;
                break ;
            case( BOOLEAN_ID ):
                type = BOOLEAN ;
                break ;
            case( INTEGER_ID ):
                type = INTEGER ;
                break ;
            case( BIT_STRING_ID ):
                type = BIT_STRING ;
                break ;
            case( OCTET_STRING_ID ):
                type = OCTET_STRING ;
                break ;
            case( NULL_ID ):
                type = NULL ;
                break ;
            case( OBJECT_IDENTIFIER_ID ):
                type = OBJECT_IDENTIFIER ;
                break ;
            case( OBJECT_DESCRIPTOR_ID ):
                type = OBJECT_DESCRIPTOR ;
                break ;
            case( EXTERNAL_INSTANCE_OF_ID ):
                type = EXTERNAL_INSTANCE_OF ;
                break ;
            case( REAL_ID ):
                type = REAL ;
                break ;
            case( ENUMERATED_ID ):
                type = ENUMERATED ;
                break ;
            case( EMBEDDED_PDV_ID ):
                type = EMBEDDED_PDV ;
                break ;
            case( UTF8_STRING_ID ):
                type = UTF8_STRING ;
                break ;
            case( RELATIVE_OID_ID ):
                type = RELATIVE_OID ;
                break ;
            case( RESERVED14_ID ):
                type = RESERVED14 ;
                break ;
            case( RESERVED15_ID ):
                type = RESERVED15 ;
                break ;
            case( SEQUENCE_SEQUENCE_OF_ID ):
                type = SEQUENCE_SEQUENCE_OF ;
                break ;
            case( SET_SET_OF_ID ):
                type = SET_SET_OF ;
                break ;
            case( NUMERIC_STRING_ID ):
                type = NUMERIC_STRING ;
                break ;
            case( PRINTABLE_STRING_ID ):
                type = PRINTABLE_STRING ;
                break ;
            case( TELETEX_STRING_ID ):
                type = TELETEX_STRING ;
                break ;
            case( VIDEOTEX_STRING_ID ):
                type = VIDEOTEX_STRING ;
                break ;
            case( IA5_STRING_ID ):
                type = IA5_STRING ;
                break ;
            case( UTC_TIME_ID ):
                type = UTC_TIME ;
                break ;
            case( GENERALIZED_TIME_ID ):
                type = GENERALIZED_TIME ;
                break ;
            case( GRAPHIC_STRING_ID ):
                type = GRAPHIC_STRING ;
                break ;
            case( VISIBLE_STRING_ID ):
                type = VISIBLE_STRING ;
                break ;
            case( GENERAL_STRING_ID ):
                type = GENERAL_STRING ;
                break ;
            case( UNIVERSAL_STRING_ID ):
                type = UNIVERSAL_STRING ;
                break ;
            case( CHARACTER_STRING_ID ):
                type = CHARACTER_STRING ;
                break ;
            case( BMP_STRING_ID ):
                type = BMP_STRING ;
                break ;
            case( RESERVED31_ID ):
                type = RESERVED31 ;
                break ;
            default:
                String msg ;
                if ( id > 31 || id < 0 )
                {
                    msg = "Looks like you're using a tag instead of an id" ;
                }
                else
                {
                    msg = "Id of " + id + " not recognized as a universal id" ;
                }

                throw new IllegalArgumentException( msg ) ;
        }

        return type ;
    }
}
