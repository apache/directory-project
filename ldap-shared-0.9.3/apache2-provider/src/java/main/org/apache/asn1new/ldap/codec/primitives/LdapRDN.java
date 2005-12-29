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
package org.apache.asn1new.ldap.codec.primitives;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.apache.asn1new.util.StringUtils;
import org.apache.commons.collections.MultiHashMap;

/**
 * This class store the name-component part or the following BNF grammar (as of RFC2253, par. 3, 
 * and RFC1779, fig. 1) : <br>
 * -    &lt;name-component&gt;         ::= &lt;attributeType&gt; &lt;spaces&gt; '=' &lt;spaces&gt; &lt;attributeValue&gt; &lt;attributeTypeAndValues&gt; <br>
 * -    &lt;attributeTypeAndValues&gt; ::= &lt;spaces&gt; '+' &lt;spaces&gt; &lt;attributeType&gt; &lt;spaces&gt; '=' &lt;spaces&gt; &lt;attributeValue&gt; &lt;attributeTypeAndValues&gt; | e <br>
 * -    &lt;attributeType&gt;          ::= [a-zA-Z] &lt;keychars&gt; | &lt;oidPrefix&gt; [0-9] &lt;digits&gt; &lt;oids&gt; | [0-9] &lt;digits&gt; &lt;oids&gt; <br>
 * -    &lt;keychars&gt;               ::= [a-zA-Z] &lt;keychars&gt; | [0-9] &lt;keychars&gt; | '-' &lt;keychars&gt; | e <br>
 * -    &lt;oidPrefix&gt;              ::= 'OID.' | 'oid.' | e <br>
 * -    &lt;oids&gt;                   ::= '.' [0-9] &lt;digits&gt; &lt;oids&gt; | e <br>
 * -    &lt;attributeValue&gt;         ::= &lt;pairs-or-strings&gt; | '#' &lt;hexstring&gt; |'"' &lt;quotechar-or-pairs&gt; '"' <br>
 * -    &lt;pairs-or-strings&gt;       ::= '\' &lt;pairchar&gt; &lt;pairs-or-strings&gt; | &lt;stringchar&gt; &lt;pairs-or-strings&gt; | e <br>
 * -    &lt;quotechar-or-pairs&gt;     ::= &lt;quotechar&gt; &lt;quotechar-or-pairs&gt; | '\' &lt;pairchar&gt; &lt;quotechar-or-pairs&gt; | e <br>
 * -    &lt;pairchar&gt;               ::= ',' | '=' | '+' | '&lt;' | '&gt;' | '#' | ';' | '\' | '"' | [0-9a-fA-F] [0-9a-fA-F]  <br>
 * -    &lt;hexstring&gt;              ::= [0-9a-fA-F] [0-9a-fA-F] &lt;hexpairs&gt; <br>
 * -    &lt;hexpairs&gt;               ::= [0-9a-fA-F] [0-9a-fA-F] &lt;hexpairs&gt; | e <br>
 * -    &lt;digits&gt;                 ::= [0-9] &lt;digits&gt; | e <br>
 * -    &lt;stringchar&gt;             ::= [0x00-0xFF] - [,=+&lt;&gt;#;\"\n\r] <br>
 * -    &lt;quotechar&gt;              ::= [0x00-0xFF] - [\"] <br>
 * -    &lt;separator&gt;              ::= ',' | ';' <br>
 * -    &lt;spaces&gt;                 ::= ' ' &lt;spaces&gt; | e <br>
 *<br>
 * A RDN is a part of a DN. It can be composed of many types, as in the RDN 
 * following RDN :<br>
 *   ou=value + cn=other value<br>
 * <br>  
 * In this case, we have to store an 'ou' and a 'cn' in the RDN.<br>
 * <br>
 * The types are case insensitive. <br>
 * Spaces before and after types and values are not stored.<br>  
 * Spaces before and after '+' are not stored.<br>
 * <br>
 * Thus, we can consider that the following RDNs are equals :<br>
 * <br>
 * 'ou=test 1'<br>
 * '  ou=test 1'<br>
 * 'ou  =test 1'<br>
 * 'ou=  test 1'<br>
 * 'ou=test 1 '<br>
 * '  ou  =  test 1 '<br>
 * <br>
 * So are the following :<br>
 * <br>
 * 'ou=test 1+cn=test 2'<br>
 * 'ou = test 1 + cn = test 2'<br>
 * '  ou =test 1+  cn =test 2  ' <br>
 * 'cn = test 2   +ou = test 1'<br>
 * <br>
 * but the following are not equal :<br>
 * 'ou=test  1' <br>
 * 'ou=test    1'<br> 
 * because we have more than one spaces inside the value.<br>
 * <br>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 *
 */
public class LdapRDN extends LdapString implements Cloneable, Comparable
{
    /** 
     * Stores all couple type = value. We may have more than one type,
     * if the '+' character appears in the AttributeTypeAndValue. The key is
     * the type, the value is a AttributeTypeAndValue.
     */
    private MultiHashMap atavs;
    
    /** 
     * A simple AttributeTypeAndValue is used to store the LdapRDN for the simple
     * case where we only have a single type=value. This will be 99.99%
     * the case. This avoids the creation of a HashMap.
     */
    private AttributeTypeAndValue atav;
    
    private transient int nbAtavs;
    
    /** Value returned by the compareTo method if values are not equals */
    public final static int NOT_EQUALS = -1;

    /** Value returned by the compareTo method if values are equals */
    public final static int EQUALS = 0;
    
    /**
     * A empty constructor.
     */
    public LdapRDN()
    {
        super();
        
        // Don't waste space... This is not so often we have multiple
        // name-components in a RDN... So we won't initialize the Map.
        atavs = null;
        atav = null;
        nbAtavs = 0;
    }
    
    /**
     * A constructor that parse a String RDN
     * 
     * @param rdn The String containing the RDN to parse
     * @throws InvalidNameException If the RDN is invalid
     */
    public LdapRDN( String rdn ) throws InvalidNameException
    {
        super();
        
        if ( StringUtils.isNotEmpty( rdn ) )
        {
            try
            {
                // Check that the String is a Valid UTF-8 string
                rdn.getBytes( "UTF-8" );
            }
            catch ( UnsupportedEncodingException uee )
            {
                throw new InvalidNameException( "The byte array is not an UTF-8 encoded Unicode String : " + uee.getMessage() );
            }

            // Parse the string and normalize the RDN
            RDNParser.parse( rdn, this );
            normalize();
        }
    }
    
    /**
     * A constructor that parse a RDN from a byte array. This method
     * is called when whe get a LdapMessage which contains a byte
     * array representing the ASN.1 RelativeRDN
     * 
     * @param rdn The byte array containing the RDN to parse
     * @throws InvalidNameException If the RDN is invalid
     */
    
    public LdapRDN( byte[] bytes ) throws InvalidNameException
    {
        super();
        
        try
        {
            RDNParser.parse( new String( bytes, "UTF-8" ), this );
            normalize();
        }
        catch ( UnsupportedEncodingException uee )
        {
            throw new InvalidNameException( "The byte array is not an UTF-8 encoded Unicode String : " + uee.getMessage() );
        }
    }
    
    /**
     * A constructor that constructs a RDN from a type and a value
     * @param type The type of the RDN
     * @param value The value of the RDN
     * @throws InvalidNameException If the RDN is invalid
     */
    public LdapRDN( String type, String value ) throws InvalidNameException
    {
        super();
        
        // Don't waste space... This is not so often we have multiple
        // name-components in a RDN... So we won't initialize the Map.
        atavs = null;
        atav = new AttributeTypeAndValue( type, value );
        nbAtavs = 1;
        normalize();
    }

    /**
     * Transform the external representation of the current RDN
     * to an internal normalized form where :
     * - types are trimmed and lowercased
     * - values are trimmed and lowercased
     */
    private void normalize()
    {
        switch ( nbAtavs )
        {
            case 0 :
                // An empty RDN
                string = "";
                break;
                
            case 1:
                // We have a single AttributeTypeAndValue
                // We will trim and lowercase type and value.
                string = StringUtils.lowerCase( StringUtils.trim( atav.getType() ) ) +
                                '=' + StringUtils.trim( atav.getValue() );
                break;
                
            default :
                // We have more than one AttributeTypeAndValue
                StringBuffer sb = new StringBuffer();

                Iterator elems = atavs.values().iterator();
                boolean isFirst = true;
                
                while ( elems.hasNext() )
                {
                    AttributeTypeAndValue ata = (AttributeTypeAndValue)elems.next();
                    
                    if ( isFirst )
                    {
                        isFirst = false;
                    }
                    else
                    {
                        sb.append( '+' );
                    }
                    
                    
                    sb.append( ata.normalize() );
                }
                
                string = sb.toString();
                break;
        }

        try
        {
            bytes = string.getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException uee )
        {
            // We can't reach this point.
        }
    }

    /**
     * Add a AttributeTypeAndValue to the current RDN
     * 
     * @param type The type of the added RDN. 
     * @param value The value of the added RDN
     * @throws InvalidNameException If the RDN is invalid
     */
    public void addAttributeTypeAndValue( String type, String value) throws InvalidNameException
    {
        // First, let's normalize the type
        String normalizedType = StringUtils.lowerCase( StringUtils.trim( type ) );
        String normalizedValue = StringUtils.trim( value );
        
        switch ( nbAtavs )
        {
            case 0 :
                // This is the first AttributeTypeAndValue. Just stores it.
                atav = new AttributeTypeAndValue( normalizedType, normalizedValue );
                nbAtavs = 1;
                break;
                
            case 1 :
                // We already have an atav. We have to put it in the HashMap
                // before adding a new one.
                // First, create the HashMap,
                atavs = new MultiHashMap();
                
                // and store the existing AttributeTypeAndValue into it.
                atavs.put( atav.getType(), atav );
                atav = null;

                // add a new AttributeTypeAndValue
                atavs.put( normalizedType, new AttributeTypeAndValue( normalizedType, normalizedValue ) );

                nbAtavs = 2;
                break;
                
            default :
                // add a new AttributeTypeAndValue
                atavs.put( normalizedType, new AttributeTypeAndValue( normalizedType, normalizedValue ) );

                nbAtavs++;
                break;
                    
        }
        
        if ( StringUtils.isEmpty( string ) )
        {
            string = normalizedType + '=' + normalizedValue;
        }
        else
        {
            string = string + '+' + normalizedType + '=' + normalizedValue;
        }
    }

    /**
     * Remove a Name from a RDN
     * 
     * @param type The nome to remove
     * @throws InvalidNameException If the name does not exists or if the RDN is empty
     */
    public void removeAttributeTypeAndValue( String type ) throws InvalidNameException
    {
        if ( StringUtils.isEmpty( type ) )
        {
            return;
        }
        
        // First, let's normalize the type
        String normalizedType = StringUtils.lowerCase( StringUtils.trim( type ) );
        
        switch ( nbAtavs )
        {
            case 0 :
                throw new InvalidNameException( "Cannot remove a AttributeTypeAndValue form an empty RDN" );
                
            case 1 :
                if ( normalizedType.equals( atav.getType() ) )
                {
                    atav = null;
                    string = "";
                    nbAtavs--;
                }
                else
                {
                    throw new InvalidNameException( "Name '" + normalizedType + "' is not valid for the RDN '" + this.toString() + "'");
                }
                
                break;
                
            default :
                if ( atavs.containsKey( normalizedType ) )
                {
                    atavs.remove( normalizedType );
                    normalize();
                    nbAtavs --;
                }
                else
                {
                    throw new InvalidNameException( "Name '" + normalizedType + "' is not valid for the RDN '" + this.toString() + "'");
                }
            
                break;
            
        }
    }
    
    /**
     * Clear the RDN, removing all the AttributeTypeAndValues.
     */
    public void clear()
    {
        atav = null;
        atavs = null;
        nbAtavs = 0;
        string = "";
        bytes = EMPTY_BYTES;
    }
    
    /**
     * Get the Value of the AttributeTypeAndValue which type is given as an argument.
     * 
     * @param type The type of the NameArgument
     * @return The Value to be returned, or null if none found.
     */
    public String getValue( String type ) throws InvalidNameException
    {
        // First, let's normalize the type
        String normalizedType = StringUtils.lowerCase( StringUtils.trim( type ) );
        
        switch ( nbAtavs )
        {
            case 0:
                return "";
                
            case 1:
                if ( StringUtils.equals( atav.getType(), normalizedType ) )
                {
                    return atav.getValue();
                }
                else
                {
                    return "";
                }
                
            default :
                if ( atavs.containsKey( normalizedType ) )
                {
                    List avas = (ArrayList)atavs.get( normalizedType );
                    StringBuffer sb = new StringBuffer();
                    boolean isFirst = true;
                    
                    for ( int i = 0; i < avas.size(); i++ )
                    {
                        if ( isFirst )
                        {
                            isFirst = false;
                        }
                        else
                        {
                            sb.append( ',' );
                        }
                        
                        sb.append( ( (AttributeTypeAndValue)avas.get( i ) ).getValue() );
                    }
                    
                    return sb.toString();
                }
                else
                {
                    return "";
                }
        }
    }

    /**
     * Get the AttributeTypeAndValue which type is given as an argument.
     * If we have more than one value associated with the type, we will
     * return only the first one.
     * 
     * @param type The type of the NameArgument to be returned
     * @return The AttributeTypeAndValue, of null if none is found.
     */
    public AttributeTypeAndValue getAttributeTypeAndValue( String type )
    {
        // First, let's normalize the type
        String normalizedType = StringUtils.lowerCase( StringUtils.trim( type ) );

        switch ( nbAtavs )
        {
            case 0:
                return null;
                
            case 1:
                if ( atav.getType().equals( normalizedType ) )
                {
                    return atav;
                }
                else
                {
                    return null;
                }
                
            default :
                if ( atavs.containsKey( normalizedType ) )
                {
                    List values =  (ArrayList)atavs.get( normalizedType );
                    
                    return (AttributeTypeAndValue)values.get( 0 );
                }
                else
                {
                    return null;
                }
        }
    }
    
    /**
     * Clone the LdapRDN
     */
    public Object clone()
    {
        try
        {
            LdapRDN rdn = (LdapRDN)super.clone();
            
            // The AttributeTypeAndValue is immutable. We won't clone it
            
            if ( atavs != null )
            {
                rdn.atavs = new MultiHashMap( nbAtavs );
                
                Iterator values = atavs.values().iterator();
                
                while ( values.hasNext() )
                {
                    AttributeTypeAndValue ava = (AttributeTypeAndValue)values.next();
                    
                    rdn.atavs.put( ava.getType(), ava );
                }
            }
            
            return rdn;
        }
        catch ( CloneNotSupportedException cnse )
        {
            throw new Error( "Assertion failure" );
        }
    }
    
    /**
     * Compares two RDN. They are equals if :
     * - their have the same number of NC (AttributeTypeAndValue)
     * - for each NC in object, their is one NC which is equal
     * - comparizon are done case insensitive
     * @param object
     * @return
     */
    public int compareTo( Object object )
    {
        if ( object instanceof LdapRDN )
        {
            LdapRDN rdn = (LdapRDN)object;

            if ( ( rdn == null ) || ( rdn.nbAtavs != nbAtavs ) )
            {
                return NOT_EQUALS;
            }
            
            switch ( nbAtavs )
            {
                case 0:
                    return EQUALS;
                    
                case 1:
                    return atav.compareTo( rdn.atav );
                    
                default :
                    // We have more than one value. We will
                    // go through all of them.
                    Iterator keys = ((MultiHashMap)atavs).keySet().iterator();
                    
                    while ( keys.hasNext() )
                    {
                        String type = (String)keys.next();
                        
                        if ( ((MultiHashMap)rdn.atavs).containsKey( type ) )
                        {
                            List atavList = (List)((MultiHashMap)atavs).get( type );
                            List atavList2 = (List)((MultiHashMap)rdn.atavs).get( type );
                            
                            // Ok, let's go for ugliness : 
                            // We are not supposed to have a lot of multi-valued RDN
                            // with a type that contains multiple values. In fact,
                            // I don't think that we will ever have this kind of RDN :
                            // "ou=test+ou=test2".
                            // And if we do, I won't put a cent on an application that
                            // have this kind of RDNs, especially if the number of AVA
                            // is higher than 2...
                            Iterator atavIter1 = atavList.iterator();
                            Set atavSet = new HashSet();
                            
                            while ( atavIter1.hasNext() )
                            {
                                AttributeTypeAndValue atavValue = (AttributeTypeAndValue)atavIter1.next();
                                atavSet.add( StringUtils.lowerCase( atavValue.getValue() ) );
                            }
                            
                            Iterator atavIter2 = atavList2.iterator();
                            
                            while ( atavIter2.hasNext() )
                            {
                                AttributeTypeAndValue atavValue = (AttributeTypeAndValue)atavIter2.next();
                                
                                if ( atavSet.contains( StringUtils.lowerCase( atavValue.getValue() ) ) )
                                {
                                    atavSet.remove( StringUtils.lowerCase( atavValue.getValue() ) );
                                }
                                else
                                {
                                    return NOT_EQUALS;
                                }
                            }
                            
                            // Last, not least, the Set must be empty for
                            // both RDN to be equals
                            if ( atavSet.size() != 0 )
                            {
                                return NOT_EQUALS;
                            }
                        }
                    }
                    
                    return EQUALS;
            }
        }
        else
        {
            return NOT_EQUALS;
        }
    }

    /**
     * Returns a String representation of the RDN
     */
    public String toString()
    {
        return string;
    }

    /**
     * @return Returns the nbAtavs.
     */
    public int getNbAtavs()
    {
        return nbAtavs;
    }
}
