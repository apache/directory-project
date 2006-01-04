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
package org.apache.ldap.common.codec.util;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;

/**
 * A Attribute Type And Value, which is the basis of all RDN.
 * It contains a type, and a value.
 * 
 * The type must not be case sensitive. Superfluous leading
 * and trailing spaces MUST have been trimmed before.
 * 
 * The value MUST be in UTF8 format, according to RFC 2253. If the type 
 * is in OID form, then the value must be a hexadecimal string prefixed 
 * by a '#' character. Otherwise, the string must respect the RC 2253
 * grammar. No further normalization will be done, because we don't
 * have any knowledge of the Schema definition in the parser. 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeAndValue implements Cloneable, Comparable
{
    /** The Name type */
    private String type;
    
    /** The name value */
    private String value;
    
    /**
     * Construct an empty AttributeTypeAndValue
     */ 
    public AttributeTypeAndValue()
    {
        type = null;
        value = null;
    }
    
    /**
     * Construct an AttributeTypeAndValue. The type and value are normalized :
     * - the type is trimmed and lowercased
     * - the value is trimmed
     * 
     * @param type The type
     * @param value the value
     */ 
    public AttributeTypeAndValue( String type, String value ) throws InvalidNameException
    {
        this.type = type;
        this.value = value;
    }
    
    /**
     * Get the type of a AttributeTypeAndValue
     * 
     * @return The type
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * Store the type 
     * 
     * @param type The AttributeTypeAndValue type 
     */
    public void setType( String type ) throws InvalidNameException
    {
        if ( StringUtils.isEmpty( type ) )
        {
            throw new InvalidNameException( "The AttributeTypeAndValue type cannot be null : " );
        }
        
        this.type = type;
    }
    
    /**
     * Store the type, after having trimmed and lowercased it.
     * 
     * @param type The AttributeTypeAndValue type 
     */
    public void setTypeNormalized( String type ) throws InvalidNameException
    {
        this.type = StringUtils.lowerCase( StringUtils.trim( type ) );

        if ( StringUtils.isEmpty( this.type ) )
        {
            throw new InvalidNameException( "The AttributeTypeAndValue type cannot be null : " );
        }
    }
    
    /**
     * Get the Value of a AttributeTypeAndValue
     * 
     * @return The value
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Store the value of a AttributeTypeAndValue.
     * 
     * @param value The value of the AttributeTypeAndValue
     */
    public void setValue( String value )
    {
        this.value = StringUtils.isEmpty( value ) ? "" : value;
    }

    /**
     * Store the value of a AttributeTypeAndValue, after having trimmed it. 
     * 
     * @param value The value of the AttributeTypeAndValue
     */
    public void setValueNormalized( String value )
    {
        this.value = StringUtils.trim( value );

        if ( StringUtils.isEmpty( value ) )
        {
            this.value = "";
        }
    }
    
    /**
     * Implements the cloning.
     * 
     * @return a clone of this object
     */
    public Object clone() 
    {
        try 
        {
            return super.clone();
        }
        catch ( CloneNotSupportedException cnse )
        {
            throw new Error( "Assertion failure" );
        }
    }
    
    /**
     * Compares two NamezComponent. They are equals if :
     * - types are equals,
     * - values are equals
     * - comparizon are case insensitive
     * 
     * @param object
     * @return
     */
    public int compareTo( Object object )
    {
        if ( object instanceof AttributeTypeAndValue )
        {
            AttributeTypeAndValue nc = (AttributeTypeAndValue)object;
            
            return ( compare( type, nc.type ) && compare( value, nc.value ) ? 0 : -1 );
        }
        else
        {
            return -1;
        }
    }
    
    /**
     * Compare two strings, trimed and case insensitive
     * @param val1 First String
     * @param val2 Second String
     * @return true if both strings are equals or null.
     */
    private boolean compare( String val1, String val2 )
    {
        if ( StringUtils.isEmpty( val1 ) )
        {
            return StringUtils.isEmpty( val2 );
        }
        else if ( StringUtils.isEmpty( val2 ) )
        {
            return false;
        }
        else
        {
            return ( StringUtils.lowerCase( StringUtils.trim( val1 ) ) ).equals(StringUtils.lowerCase( StringUtils.trim( val2 ) ) );
        }
    }

    /**
     * A Normalized String representation of a AttributeTypeAndValue :
     * - type is trimed and lowercased
     * - value is trimed and lowercased
     * 
     * @return A normalized string representing a AttributeTypeAndValue
     */
    public String normalize()
    {
        return StringUtils.lowerCase( StringUtils.trim( type ) ) + '=' +
        StringUtils.trim( value );
    }

    /**
     * A String representation of a AttributeTypeAndValue.
     * 
     * @return A string representing a AttributeTypeAndValue
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( type ).append( "=" ).append( value );
        
        return sb.toString();
    }
}

