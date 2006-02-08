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
 * A type safe enumeration representing the state of a BERDecoder.  This can 
 * take one of the following three values: 
 * 
 * <ul>
 *   <li>
 *      TAG - state where the decoder is reading and composing the tag
 *   </li>
 *   <li>
 *      LENGTH - state where the decoder is reading and composing the length
 *   </li>
 *   <li>
 *      VALUE - state where the decoder is reading and composing the value
 *   </li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 *      Apache Directory Project</a>
 * @version $Rev$
 */
public final class BERDecoderState
{
    /** value for the TAG state */
    public static final int TAG_VAL = 0 ;
    /** value for the LENGTH state */
    public static final int LENGTH_VAL = 1 ;
    /** value for the VALUE state */
    public static final int VALUE_VAL = 2 ;

    /** enum for the TAG state */
    public static final BERDecoderState TAG = 
        new BERDecoderState( "TAG", TAG_VAL ) ;
    /** enum for the LENGTH state */
    public static final BERDecoderState LENGTH = 
        new BERDecoderState( "LENGTH", LENGTH_VAL ) ;
    /** enum for the VALUE state */
    public static final BERDecoderState VALUE = 
        new BERDecoderState( "VALUE", VALUE_VAL ) ;

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
    private BERDecoderState( final String name, final int value )
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
     * Gets the next state after this BERDecoderState based on the nature of the
     * present TLV being processed.
     * 
     * @param isPrimitive true if the current TLV is primitive,  false if it is
     *      constructed
     * @return the type safe enum for the next state to transit to
     */
    public final BERDecoderState getNext( boolean isPrimitive )
    {
        BERDecoderState next = null ;
        
        switch( getValue() )
        {
            case( VALUE_VAL ):
                next = TAG ;
                break ;
            case( TAG_VAL ):
                next = LENGTH ;
                break ;
            case( LENGTH_VAL ):
                if ( isPrimitive )
                {
                    next = VALUE ;
                }
                else
                {    
                    next = TAG ;
                }
                break ;
        }
        
        return next ;
    }
    

    /**
     * Determines if this present state is the processing end state for a TLV 
     * based on the nature of the current TLV tuple as either a primitive TLV 
     * or a constructed one.  The VALUE state is considered a terminal 
     * processing state for all TLV tuples.  The LENGTH state is considered a 
     * terminal processing state for constructed TLV tuples.
     * 
     * @param isPrimitive true if the current TLV is primitive,  false if it is
     *      constructed
     * @return true if the next state is the last processing state
     */
    public final boolean isEndState( boolean isPrimitive )
    {
        boolean isEndState = false ;
        
        switch( getValue() )
        {
            case( VALUE_VAL ):
                isEndState = true ;
                break ;
            case( TAG_VAL ):
                isEndState = false ;
                break ;
            case( LENGTH_VAL ):
                if ( isPrimitive )
                {
                    isEndState = false ;
                }
                else
                {    
                    isEndState = true ;
                }
                break ;
        }
        
        return isEndState ;
    }
    
    
    /**
     * Gets the start state.
     * 
     * @return the start state 
     */
    public final static BERDecoderState getStartState()
    {
        return TAG ;
    }
    
    
    /**
     * Gets the enum type for the state regardless of case.
     * 
     * @param stateName the name of the state
     * @return the BERDecoderState enum for the state name 
     */
    public final static BERDecoderState getState( String stateName )
    {
        /*
         * First try and see if a quick reference lookup will resolve the
         * name since this is the fastest way to test.  Most of the time when
         * class names are used they are references to the actual string of 
         * the state object anyway. 
         */
        
        if ( stateName == TAG.getName() )
        {
            return TAG ;
        }
        
        if ( stateName == LENGTH.getName() )
        {
            return LENGTH ;
        }
        
        if ( stateName == VALUE.getName() )
        {
            return VALUE ;
        }
        
        /*
         * As a last resort see if we can match the string to an existing
         * state.  We really should not have to resort to this but its there
         * anyway. 
         */
        
        if ( stateName.equalsIgnoreCase( BERDecoderState.TAG.getName() ) )
        {
            return BERDecoderState.TAG ;
        }
        
        if ( stateName.equalsIgnoreCase( BERDecoderState.LENGTH.getName() ) )
        {
            return BERDecoderState.LENGTH ;
        }
        
        if ( stateName.equalsIgnoreCase( BERDecoderState.VALUE.getName() ) )
        {
            return BERDecoderState.VALUE ;
        }
        
        throw new IllegalArgumentException( "Unknown decoder state"
            + stateName ) ;
    }
    
    
    /**
     * Gets the state of the decoder using a state value.
     * 
     * @param value the value of the state
     * @return the BERDecoderState for the decoder state value
     */
    public final static BERDecoderState getState( int value )
    {
        switch ( value )
        {
            case( TAG_VAL ):
                return TAG ;
            case( LENGTH_VAL ):
                return LENGTH ;
            case( VALUE_VAL ):
                return VALUE ;
            default:
                throw new IllegalStateException( "Should not be here!" ) ;
        }
    }
}
