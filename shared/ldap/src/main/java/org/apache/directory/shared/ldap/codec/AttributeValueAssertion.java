/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.directory.shared.ldap.codec;


import org.apache.directory.shared.ldap.codec.util.LdapString;


/**
 * A class to store an attribute value assertion. Tha grammar is :
 * AttributeValueAssertion ::= SEQUENCE { attributeDesc AttributeDescription,
 * assertionValue AssertionValue } AttributeDescription ::= LDAPString
 * AssertionValue ::= OCTET STRING
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeValueAssertion
{
    // ~ Instance fields
    // ----------------------------------------------------------------------------

    /** The attribute description */
    private LdapString attributeDesc;

    /** The assertion value */
    private Object assertionValue;


    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Get the assertion value
     * 
     * @return Returns the assertionValue.
     */
    public Object getAssertionValue()
    {
        return assertionValue;
    }


    /**
     * Set the assertion value
     * 
     * @param assertionValue
     *            The assertionValue to set.
     */
    public void setAssertionValue( Object assertionValue )
    {
        this.assertionValue = assertionValue;
    }


    /**
     * Get the attribute description
     * 
     * @return Returns the attributeDesc.
     */
    public String getAttributeDesc()
    {
        return ( ( attributeDesc == null ) ? null : attributeDesc.getString() );
    }


    /**
     * Set the attribute description
     * 
     * @param attributeDesc
     *            The attributeDesc to set.
     */
    public void setAttributeDesc( LdapString attributeDesc )
    {
        this.attributeDesc = attributeDesc;
    }


    /**
     * Get a String representation of an AttributeValueAssertion
     * 
     * @param tabs
     *            The spacing to be put before the string
     * @return An AttributeValueAssertion String
     */
    public String toString( String tabs )
    {

        StringBuffer sb = new StringBuffer();

        sb.append( tabs ).append( "AttributeValueAssertion\n" );
        sb.append( tabs ).append( "    Assertion description : '" ).append( attributeDesc.toString() ).append( "'\n" );
        sb.append( tabs ).append( "    Assertion value : '" ).append( assertionValue.toString() ).append( "'\n" );

        return sb.toString();
    }


    /**
     * Get a String representation of an AttributeValueAssertion, as of RFC
     * 2254.
     * 
     * @param filterType
     *            The filter type
     * @return An AttributeValueAssertion String
     */
    public String toStringRFC2254( int filterType )
    {

        StringBuffer sb = new StringBuffer();

        sb.append( attributeDesc.toString() );

        switch ( filterType )
        {
            case LdapConstants.EQUALITY_MATCH_FILTER:
                sb.append( '=' );
                break;

            case LdapConstants.LESS_OR_EQUAL_FILTER:
                sb.append( "<=" );
                break;

            case LdapConstants.GREATER_OR_EQUAL_FILTER:
                sb.append( ">=" );
                break;

            case LdapConstants.APPROX_MATCH_FILTER:
                sb.append( "~=" );
                break;
        }

        sb.append( assertionValue.toString() );

        return sb.toString();
    }


    /**
     * Get a String representation of an AttributeValueAssertion
     * 
     * @return An AttributeValueAssertion String
     */
    public String toString()
    {
        return toString( "" );
    }
}