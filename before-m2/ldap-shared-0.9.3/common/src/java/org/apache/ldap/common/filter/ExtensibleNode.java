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

/*
 * $Id: ExtensibleNode.java,v 1.4 2003/10/14 04:59:23 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */
package org.apache.ldap.common.filter;


/**
 * Filter expression tree node for extensible assertions.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $author$
 * @version $Revision$
 */
public class ExtensibleNode extends LeafNode
{
    /** The value of the attribute to match for */
    private final String value;
    /** The matching rules id */
    private final String m_matchingRuleId ;

    /** The name of the dn attributes */
    private boolean m_dnAttributes = false ;

 
    /**
     * Creates a new ExtensibleNode object.
     *
     * @param a_attribute the attribute used for the extensible assertion
     * @param a_value the value to match for
     * @param a_matchingRuleId the OID of the matching rule
     * @param a_dnAttributes the dn attributes
     */
    public ExtensibleNode( String a_attribute, String a_value,
        String a_matchingRuleId, boolean a_dnAttributes )
    {
        super( a_attribute, EXTENSIBLE ) ;

        this.value = a_value ;
        m_matchingRuleId = a_matchingRuleId ;
        this.m_dnAttributes = a_dnAttributes ;
    }


    /**
     * Gets the Dn attributes.
     *
     * @return the dn attributes
     */
    public boolean dnAttributes(  )
    {
        return m_dnAttributes ;
    }


    /**
     * Gets the matching rule id as an OID string.
     *
     * @return the OID 
     */
    public String getMatchingRuleId(  )
    {
        return m_matchingRuleId ;
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public final String getValue()
    {
        return value ;
    }


    /**
     * @see org.apache.ldap.common.filter.ExprNode#printToBuffer(
     * java.lang.StringBuffer)
     */
    public StringBuffer printToBuffer( StringBuffer a_buf )
    {
        a_buf.append( '(' ).append( getAttribute() );
        a_buf.append( "-" );
        a_buf.append( this.m_dnAttributes );
        a_buf.append( "-EXTENSIBLE-" );
        a_buf.append( this.m_matchingRuleId );
        a_buf.append( "-" );
        a_buf.append( this.value );
        a_buf.append( ')' );

        if ( ( null != getAnnotations() )
                && getAnnotations().containsKey( "count" ) )
        {
            a_buf.append( '[' );
            a_buf.append( getAnnotations().get( "count" ).toString() );
            a_buf.append( "] " );
        }
        else
        {
            a_buf.append( ' ' );
        }
        
        return a_buf;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer l_buf = new StringBuffer() ;
        printToBuffer( l_buf ) ;

        return ( l_buf.toString() ) ;
    }


    /**
     * @see org.apache.ldap.common.filter.ExprNode#accept(
     * org.apache.ldap.common.filter.FilterVisitor)
     */
    public void accept( FilterVisitor a_visitor )
    {
        if ( a_visitor.canVisit( this ) )
        {
            a_visitor.visit( this ) ;
        }
    }
}
