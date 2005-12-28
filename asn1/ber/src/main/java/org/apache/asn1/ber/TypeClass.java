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
package org.apache.asn1.ber ;


/**
 * Type safe enum for an ASN.1 type class.  This can be take one of the 
 * following four values: 
 * <ul>
 * <li>UNIVERSAL</li>
 * <li>APPLICATION</li>
 * <li>CONTEXT_SPECIFIC</li>
 * <li>PRIVATE</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TypeClass
{
    /** value for the universal type class */
    public static final int UNIVERSAL_VAL = 0 ;
    /** value for the application type class */
    public static final int APPLICATION_VAL = 0x40 ;
    /** value for the context specific type class */
    public static final int CONTEXT_SPECIFIC_VAL = 0x80 ;
    /** value for the private type class */
    public static final int PRIVATE_VAL = 0xc0 ;

    /** enum for the universal type class */
    public static final TypeClass UNIVERSAL = 
        new TypeClass( "UNIVERSAL", UNIVERSAL_VAL ) ;
    /** enum for the application type class */
    public static final TypeClass APPLICATION = 
        new TypeClass( "APPLICATION", APPLICATION_VAL ) ;
    /** enum for the context specific type class  */
    public static final TypeClass CONTEXT_SPECIFIC = 
        new TypeClass( "CONTEXT_SPECIFIC", CONTEXT_SPECIFIC_VAL ) ;
    /** enum for the private type class  */
    public static final TypeClass PRIVATE = 
        new TypeClass( "PRIVATE", PRIVATE_VAL ) ;

    /** the name of this enumeration element */
    private final String name;
    /** the value of this enumeration element */
    private final int value;


    /**
     * Private constructor so no other instances can be created other than the
     * public static constants in this class.
     *
     * @param name a string name for the enumeration value.
     * @param value the integer value of the enumeration.
     */
    private TypeClass( final String name, final int value )
    {
        this.name = name;
        this.value = value;
    }


    /**
     * Get's the name of this enumeration element.
     *
     * @return the name of the enumeration element
     */
    public final String getName()
    {
        return this.name;
    }


    /**
     * Get's the value of this enumeration element.
     *
     * @return the value of the enumeration element
     */
    public final int getValue()
    {
        return this.value;
    }


    /**
     * Gets the enumeration type for the type class regardless of case.
     * 
     * @param className the type class name
     * @return the TypeClass for the name
     */
    public static TypeClass getTypeClass( String className )
    {
        // check first using == since it will be the predominate use case
        if ( className == APPLICATION.getName() )
        {
            return APPLICATION ;
        }
        else if ( className == CONTEXT_SPECIFIC.getName() )
        {
            return CONTEXT_SPECIFIC ;
        }
        else if ( className == PRIVATE.getName() )
        {
            return PRIVATE ;
        }
        else if ( className == UNIVERSAL.getName() )
        {
            return UNIVERSAL ;
        }
        
        if ( className.equalsIgnoreCase( TypeClass.PRIVATE.getName() ) )
        {
            return TypeClass.PRIVATE ;
        }
        
        if ( className.equalsIgnoreCase( TypeClass.UNIVERSAL.getName() ) )
        {
            return TypeClass.UNIVERSAL ;
        }
        
        if ( className.equalsIgnoreCase( TypeClass.APPLICATION.getName() ) )
        {
            return TypeClass.APPLICATION ;
        }
        
        if ( className.equalsIgnoreCase( 
                        TypeClass.CONTEXT_SPECIFIC.getName() ) )
        {
            return TypeClass.CONTEXT_SPECIFIC ;
        }
        
        throw new IllegalArgumentException( "Unknown type class name"
            + className ) ;
    }
    
    
    /**
     * Gets the ASN.1 type's class using a TLV tag.
     * 
     * @param octet the first octet of the TLV
     * @return the TypeClass enumeration for the ASN.1 type's class
     */
    public static TypeClass getTypeClass( int octet )
    {
        TypeClass tc = null ;
        int l_value = octet & PRIVATE_VAL ;
        
        switch ( l_value )
        {
            case( UNIVERSAL_VAL ):
                tc = UNIVERSAL ;
                break ;
            case( APPLICATION_VAL ):
                tc = APPLICATION ;
                break ;
            case( CONTEXT_SPECIFIC_VAL ):
                tc = CONTEXT_SPECIFIC ;
                break ;
            case( PRIVATE_VAL ):
                tc = PRIVATE ;
                break ;
        }
        
        return tc ;
    }
}
