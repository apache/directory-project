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


import java.nio.BufferOverflowException;

import org.apache.commons.lang.ArrayUtils;


/**
 * Collects up to 4 tag octets.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TagOctetCollector
{
    /** the int used to store the tag octets */
    private int intValue = 0 ;
    /** the number of octets currently stored */
    private int _size = 0 ;
    
    
    /**
     * Puts an octet into this collector.
     * 
     * @param octet the octet to put into the collector.
     */
    public void put( byte octet )
    {
        switch( _size )
        {
            case(0):
                intValue = octet << 24 ;
                _size = 1 ;
                break ;
            case(1):
                intValue |= ( octet << 16 ) & 0x00FF0000 ;
                _size = 2 ;
                break ;
            case(2):
                intValue |= ( octet << 8 ) & 0x0000FF00 ;
                _size = 3 ;
                break ;
            case(3):
                intValue |= octet ;
                _size = 4 ;
                break ;
            default:
                throw new BufferOverflowException() ;
        }
    }
    
    
    /**
     * Clears all the tag octets resetting the tag and size to zero.
     */
    public void clear()
    {
        intValue = 0 ;
        _size = 0 ;
    }
    
    
    /**
     * Gets the number of octets stored by this TagOctetCollector
     * 
     * @return
     */
    public int size()
    {
        return _size ;
    }
    
    
    /**
     * Gets a unique integer value representing the tag octets.
     * 
     * @return the integer value of the tag.
     */
    public int getIntValue()
    {
        return intValue ;
    }
    
    
    /**
     * Gets the 4 octets for the tag.
     * 
     * @return
     */
    public byte[] toArray()
    {
        byte[] octets = new byte[_size] ;
        
        switch( _size )
        {
            case(0):
                octets = ArrayUtils.EMPTY_BYTE_ARRAY ;
                break ;
            case(1):
                octets[0] = ( byte ) ( ( intValue & 0xff000000 ) >> 24 ) ;
                break ;
            case(2):
                octets[0] = ( byte ) ( ( intValue & 0xff000000 ) >> 24 ) ;
                octets[1] = ( byte ) ( ( intValue & 0x00ff0000 ) >> 16 ) ;
                break ;
            case(3):
                octets[0] = ( byte ) ( ( intValue & 0xff000000 ) >> 24 ) ;
                octets[1] = ( byte ) ( ( intValue & 0x00ff0000 ) >> 16 ) ;
                octets[2] = ( byte ) ( ( intValue & 0x0000ff00 ) >>  8 ) ;
                break ;
            case(4):
                octets[0] = ( byte ) ( ( intValue & 0xff000000 ) >> 24 ) ;
                octets[1] = ( byte ) ( ( intValue & 0x00ff0000 ) >> 16 ) ;
                octets[2] = ( byte ) ( ( intValue & 0x0000ff00 ) >>  8 ) ;
                octets[3] = ( byte )   ( intValue & 0x000000ff ) ;
                break ;
            default:
                throw new IllegalArgumentException( 
                        "Cannot support more than 4 octets" ) ;
        }
        
        return octets ;
    }
    
    
    /**
     * Gets the byte at a specific index.
     * 
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     */
    public byte get( int index )
    {
        byte octet ;
        
        if ( index >= _size )
        {
            throw new IndexOutOfBoundsException( "accesing index " + index
                    + " with a size of " + _size ) ;
        }

        switch( index )
        {
            case(0):
                octet = ( byte ) ( ( intValue & 0xff000000 ) >> 24 ) ;
                break ;
            case(1):
                octet = ( byte ) ( ( intValue & 0x00ff0000 ) >> 16 ) ;
                break ;
            case(2):
                octet = ( byte ) ( ( intValue & 0x0000ff00 ) >>  8 ) ;
                break ;
            case(3):
                octet = ( byte )   ( intValue & 0x000000ff ) ;
                break ;
            default:
                throw new IllegalArgumentException( 
                        "Cannot support more than 4 octets" ) ;
        }
        
        return octet ;
    }
}
