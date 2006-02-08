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
package org.apache.asn1new.util;

import java.io.UnsupportedEncodingException;

/**
 * Little helper class. Nothing that should stay here, but I need those 
 * to debug.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StringUtils
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** Hex chars */
    private static final byte[] HEX_CHAR =
        new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    
    private static int UTF8_MULTI_BYTES_MASK = 0x0080;
    
    private static int UTF8_TWO_BYTES_MASK = 0x00E0;
    private static int UTF8_TWO_BYTES = 0x00C0;
    
    private static int UTF8_THREE_BYTES_MASK = 0x00F0;
    private static int UTF8_THREE_BYTES = 0x00E0;

    private static int UTF8_FOUR_BYTES_MASK = 0x00F8;
    private static int UTF8_FOUR_BYTES = 0x00F0;
    
    private static int UTF8_FIVE_BYTES_MASK = 0x00FC;
    private static int UTF8_FIVE_BYTES = 0x00F8;

    private static int UTF8_SIX_BYTES_MASK = 0x00FE;
    private static int UTF8_SIX_BYTES = 0x00FC;
    
    /** <alpha>    ::= [0x41-0x5A] | [0x61-0x7A] */
    public static final boolean[] ALPHA =
    {
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true, true, true, false, false, false,
        false, false, false, true, true, true, true, true, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
        false, false, false, false, false
    };

    /** <alpha> | <digit> | '-' */
    public static final boolean[] CHAR =
    {
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, true, false, false, true, true, true, true, true,
        true, true, true, true, true, false, false, false, false, false, false, false, true, true,
        true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true, false, false, false, false, false,
        false, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true, true, true, true, false, false,
        false, false, false
    };

    /** '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' */
    public static final boolean[] DIGIT =
    {
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, true, true, true, true,
        true, true, true, true, true, true, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false,
    };

    /** <hex>    ::= [0x30-0x39] | [0x41-0x46] | [0x61-0x66] */
    private static final boolean[] HEX =
    {
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, true, true, true, true,
        true, true, true, true, true, true, false, false, false, false, false, false, false, true,
        true, true, true, true, true, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, true, true, true, true, true, true, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false
    };

    private static int CHAR_ONE_BYTE_MASK    = 0xFFFFFF80;
    
    private static int CHAR_TWO_BYTES_MASK   = 0xFFFFF800;
    
    private static int CHAR_THREE_BYTES_MASK = 0xFFFF0000;
    
    private static int CHAR_FOUR_BYTES_MASK  = 0xFFE00000;
    
    private static int CHAR_FIVE_BYTES_MASK  = 0xFC000000;
    
    private static int CHAR_SIX_BYTES_MASK   = 0x80000000;
    
    public static final int NOT_EQUAL = -1;

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Helper function that dump a byte in hex form
     * 
     * @param octet The byte to dump
     * @return A string representation of the byte
     */
    public static String dumpByte( byte octet )
    {
        return new String(
                new byte[] { '0', 'x', HEX_CHAR[( octet & 0x00F0 ) >> 4], HEX_CHAR[octet & 0x000F] } );
    }
    
    /**
     * Helper function that dump an array of bytes in hex form
     * 
     * @param octet The bytes array to dump
     * @return A string representation of the array of bytes
     */
    public static String dumpBytes( byte[] buffer )
    {
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < buffer.length ; i++)
        {
            sb.append("0x").append((char)(HEX_CHAR[( buffer[i] & 0x00F0 ) >> 4])).append((char)(HEX_CHAR[buffer[i] & 0x000F])).append(" ");
        }
        
        return sb.toString();
    }
    
    /**
     * Return the Unicode char which is coded in the bytes at position 0.
     * 
     * @param bytes The byte[] represntation of an Unicode string. 
     * @return The first char found.
     */
    public static char bytesToChar(byte[] bytes)
    {
        return bytesToChar(bytes, 0);
    }

    /**
     * Count the number of bytes needed to return an Unicode char. This
     * can be from 1 to 6. 
     * @param bytes The bytes to read
     * @param pos Position to start counting. It must be a valid start of a 
     * encoded char !
     * @return The number of bytes to create a char, or -1 if the encoding is wrong.
     * 
     * TODO : Should stop after the third byte, as a char is only 2 bytes long.
     */
    public static int countBytesPerChar(byte[] bytes, int pos)
    {
        if ((bytes[pos] & UTF8_MULTI_BYTES_MASK) == 0)
        {
            return 1;
        } else if ((bytes[pos] & UTF8_TWO_BYTES_MASK) == UTF8_TWO_BYTES)
    	{
            return 2;
    	}
    	else if ((bytes[pos] & UTF8_THREE_BYTES_MASK) == UTF8_THREE_BYTES)
    	{
    	    return 3;
    	}
    	else if ((bytes[pos] & UTF8_FOUR_BYTES_MASK) == UTF8_FOUR_BYTES)
    	{
    	    return 4;
    	}
    	else if ((bytes[pos] & UTF8_FIVE_BYTES_MASK) == UTF8_FIVE_BYTES)
    	{
    	    return 5;
    	}
    	else if ((bytes[pos] & UTF8_SIX_BYTES_MASK) == UTF8_SIX_BYTES)
    	{
    	    return 6;
        } 
    	else
    	{
    	    return -1;
    	}
    }
    
    /**
     * Return the number of bytes that hold an Unicode char.
     * 
     * @param car The character to be decoded
     * @return The number of bytes to hold the char.
     * 
     * TODO : Should stop after the third byte, as a char is only 2 bytes long.
     */
    public static int countNbBytesPerChar( char car )
    {
        if ( ( car & CHAR_ONE_BYTE_MASK ) == 0 )
        {
            return 1;
        }
        else if ( ( car & CHAR_TWO_BYTES_MASK ) == 0 )
        {
            return 2;
        }
        else if ( ( car & CHAR_THREE_BYTES_MASK ) == 0 )
        {
            return 3;
        }
        else if ( ( car & CHAR_FOUR_BYTES_MASK ) == 0 )
        {
            return 4;
        }
        else if ( ( car & CHAR_FIVE_BYTES_MASK ) == 0 )
        {
            return 5;
        }
        else if ( ( car & CHAR_SIX_BYTES_MASK ) == 0 )
        {
            return 6;
        } 
        else
        {
            return -1;
        }
    }
    
    /**
     * Count the number of bytes included in the given char[].  
     * @param chars The char array to decode
     * @return The number of bytes in the char array
     */
    public static int countBytes(char[] chars) throws UnsupportedEncodingException
    {
        int nbBytes = 0;
        int currentPos = 0;
        
        while ( currentPos < chars.length )
        {
            int nbb = countNbBytesPerChar( chars[currentPos] );
            
            // If the number of bytes necessary to encode a character is
            // above 3, we will need two UTF-16 chars
            currentPos += (nbb < 4 ? 1 : 2 );
            nbBytes += nbb;
        }

        return nbBytes;
    }
    
    /**
     * Return the Unicode char which is coded in the bytes at the given position. 
     * @param bytes The byte[] represntation of an Unicode string. 
     * @param pos The current position to start decoding the char
     * @return The char found.
     * @return The decoded char, or -1 if no char can be decoded
     * 
     * TODO : Should stop after the third byte, as a char is only 2 bytes long.
     */
    public static char bytesToChar(byte[] bytes, int pos)
    {
    	if ((bytes[pos] & UTF8_MULTI_BYTES_MASK) == 0)
		{
    		return (char)bytes[pos];
		}
    	else
    	{
    		if ((bytes[pos] & UTF8_TWO_BYTES_MASK) == UTF8_TWO_BYTES)
    		{
    			// Two bytes char
    			return (char)( 
    					( ( bytes[pos] & 0x1C ) << 6 ) + 	// 110x-xxyy 10zz-zzzz -> 0000-0xxx 0000-0000
    					( ( bytes[pos] & 0x03 ) << 6 ) + 	// 110x-xxyy 10zz-zzzz -> 0000-0000 yy00-0000
						( bytes[pos + 1] & 0x3F ) 		   	// 110x-xxyy 10zz-zzzz -> 0000-0000 00zz-zzzz
						); 								//                     -> 0000-0xxx yyzz-zzzz (07FF)
    		}
    		else if ((bytes[pos] & UTF8_THREE_BYTES_MASK) == UTF8_THREE_BYTES)
    		{
    			// Three bytes char
    			return (char)( 
    					// 1110-tttt 10xx-xxyy 10zz-zzzz -> tttt-0000-0000-0000
    					( ( bytes[pos] & 0x0F) << 12 ) + 	
						// 1110-tttt 10xx-xxyy 10zz-zzzz -> 0000-xxxx-0000-0000
    					( ( bytes[pos + 1] & 0x3C) << 6 ) + 	
						// 1110-tttt 10xx-xxyy 10zz-zzzz -> 0000-0000-yy00-0000
    					( ( bytes[pos + 1] & 0x03) << 6 ) + 	
						// 1110-tttt 10xx-xxyy 10zz-zzzz -> 0000-0000-00zz-zzzz
						( bytes[pos + 2] & 0x3F )				
						//                               -> tttt-xxxx yyzz-zzzz (FF FF)
						);   							 
    		}
    		else if ((bytes[pos] & UTF8_FOUR_BYTES_MASK) == UTF8_FOUR_BYTES)
    		{
    			// Four bytes char
    			return (char)(
    					// 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 000t-tt00 0000-0000 0000-0000
    					( ( bytes[pos] & 0x07) << 18 ) +
						// 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-00uu 0000-0000 0000-0000
    					( ( bytes[pos + 1] & 0x30) << 16 ) + 
						// 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000 vvvv-0000 0000-0000
    					( ( bytes[pos + 1] & 0x0F) << 12 ) + 
						// 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-xxxx 0000-0000
    					( ( bytes[pos + 2] & 0x3C) << 6 ) + 
						// 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-0000 yy00-0000
    					( ( bytes[pos + 2] & 0x03) << 6 ) +
						// 1111-0ttt 10uu-vvvv 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-0000 00zz-zzzz
						( bytes[pos + 3] & 0x3F )
						//                                         -> 000t-ttuu vvvv-xxxx yyzz-zzzz (1FFFFF)
						);   
    		}
    		else if ((bytes[pos] & UTF8_FIVE_BYTES_MASK) == UTF8_FIVE_BYTES)
    		{
    			// Five bytes char
    			return (char)( 
    					// 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz -> 0000-00tt 0000-0000 0000-0000 0000-0000
    					( ( bytes[pos] & 0x03) << 24 ) + 
    					// 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz -> 0000-0000 uuuu-uu00 0000-0000 0000-0000
    					( ( bytes[pos + 1] & 0x3F) << 18 ) + 
    					// 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-00vv 0000-0000 0000-0000
    					( ( bytes[pos + 2] & 0x30) << 12 ) + 
    					// 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-0000 wwww-0000 0000-0000
    					( ( bytes[pos + 2] & 0x0F) << 12 ) + 
    					// 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-0000 0000-xxxx 0000-0000
    					( ( bytes[pos + 3] & 0x3C) << 6 ) + 
    					// 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-0000 0000-0000 yy00-0000
    					( ( bytes[pos + 3] & 0x03) << 6 ) + 
						// 1111-10tt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz -> 0000-0000 0000-0000 0000-0000 00zz-zzzz
						( bytes[pos + 4] & 0x3F )
						// -> 0000-00tt uuuu-uuvv wwww-xxxx yyzz-zzzz (03 FF FF FF)
						);   
    		}
    		else if ((bytes[pos] & UTF8_FIVE_BYTES_MASK) == UTF8_FIVE_BYTES)
    		{
    			// Six bytes char
    			return (char)( 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    			        // 0s00-0000 0000-0000 0000-0000 0000-0000
    					( ( bytes[pos] & 0x01) << 30 ) + 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    			        // 00tt-tttt 0000-0000 0000-0000 0000-0000
    					( ( bytes[pos + 1] & 0x3F) << 24 ) + 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    			        // 0000-0000 uuuu-uu00 0000-0000 0000-0000
    					( ( bytes[pos + 2] & 0x3F) << 18 ) + 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    			        // 0000-0000 0000-00vv 0000-0000 0000-0000
    					( ( bytes[pos + 3] & 0x30) << 12 ) + 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    			        // 0000-0000 0000-0000 wwww-0000 0000-0000
    					( ( bytes[pos + 3] & 0x0F) << 12 ) + 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    					// 0000-0000 0000-0000 0000-xxxx 0000-0000
    					( ( bytes[pos + 4] & 0x3C) << 6 ) + 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    					// 0000-0000 0000-0000 0000-0000 yy00-0000
    					( ( bytes[pos + 4] & 0x03) << 6 ) + 
    			        // 1111-110s 10tt-tttt 10uu-uuuu 10vv-wwww 10xx-xxyy 10zz-zzzz ->
    					// 0000-0000 0000-0000 0000-0000 00zz-zzzz
						( bytes[pos + 5] & 0x3F )
    			        // -> 0stt-tttt uuuu-uuvv wwww-xxxx yyzz-zzzz (7F FF FF FF)
						);   
    		} 
    		else
    		{
    		    return (char)-1;
    		}
    	}
    }
    
    /**
     * Count the number of chars included in the given byte[].  
     * @param bytes The byte array to decode
     * @return The number of char in the byte array
     */
    public static int countChars(byte[] bytes)
    {
        int nbChars = 0;
        int currentPos = 0;
        
        while (currentPos < bytes.length)
        {
            currentPos += countBytesPerChar(bytes, currentPos);
            nbChars ++;
        }

        return nbChars;
    }
    
    /**
     * Check if a text is present at the current position in a buffer.
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     * @param text The text we want to check
     *
     * @return <code>true</code> if the buffer contains the text.
     */
    public static int areEquals( byte[] byteArray, int index, String text )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) || ( byteArray.length <= index ) ||
                ( index < 0 ) || ( text == null ) )
        {
            return NOT_EQUAL;
        }
        else
        {
            try
            {
                byte[] data = text.getBytes( "UTF-8" );

                return areEquals( byteArray, index, data );
            }
            catch ( UnsupportedEncodingException uee )
            {
                return NOT_EQUAL;
            }
        }
    }

    /**
     * Check if a text is present at the current position in a buffer.
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     * @param text The text we want to check
     *
     * @return <code>true</code> if the buffer contains the text.
     */
    public static int areEquals( char[] charArray, int index, String text )
    {
        if ( ( charArray == null ) || ( charArray.length == 0 ) || ( charArray.length <= index ) ||
                ( index < 0 ) || ( text == null ) )
        {
            return NOT_EQUAL;
        }
        else
        {
            char[] data = text.toCharArray();

            return areEquals( charArray, index, data );
        }
    }

    /**
     * Check if a text is present at the current position in a buffer.
     *
     * @param charArray The buffer which contains the data
     * @param index Current position in the buffer
     * @param charArray2 The text we want to check
     *
     * @return <code>true</code> if the buffer contains the text.
     */
    public static int areEquals( char[] charArray, int index, char[] charArray2 )
    {

        if ( ( charArray == null ) ||
                ( charArray.length == 0 ) ||
                ( charArray.length <= index ) ||
                ( index < 0 ) ||
                ( charArray2 == null ) ||
                ( charArray2.length == 0 ) ||
                ( charArray2.length > ( charArray.length + index ) ) )
        {
            return NOT_EQUAL;
        }
        else
        {
            for ( int i = 0; i < charArray2.length; i++ )
            {
                if ( charArray[index++] != charArray2[i] )
                {
                    return NOT_EQUAL;
                }
            }

            return index;
        }
    }

    /**
     * Check if a text is present at the current position in a buffer.
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     * @param byteArray2 The text we want to check
     *
     * @return <code>true</code> if the buffer contains the text.
     */
    public static int areEquals( byte[] byteArray, int index, byte[] byteArray2 )
    {

        if ( ( byteArray == null ) ||
                ( byteArray.length == 0 ) ||
                ( byteArray.length <= index ) ||
                ( index < 0 ) ||
                ( byteArray2 == null ) ||
                ( byteArray2.length == 0 ) ||
                ( byteArray2.length > ( byteArray.length + index ) ) )
        {
            return NOT_EQUAL;
        }
        else
        {
            for ( int i = 0; i < byteArray2.length; i++ )
            {
                if ( byteArray[index++] != byteArray2[i] )
                {
                    return NOT_EQUAL;
                }
            }

            return index;
        }
    }

    /**
     * Test if the current character is equal to a specific character.
     * This function works only for character between 0 and 127, as it
     * does compare a byte and a char (which is 16 bits wide)
     * 
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     * @param car The character we want to compare with the current buffer position
     *
     * @return <code>true</code> if the current character equals the given character.
     */
    public static boolean isCharASCII( byte[] byteArray, int index, char car )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) || ( index < 0 ) ||
                ( index >= byteArray.length ) )
        {
            return false;
        }
        else
        {
            return ( ( byteArray[index] == car ) ? true : false );
        }
    }

    /**
     * Test if the current character is equal to a specific character.
     * 
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     * @param car The character we want to compare with the current buffer position
     *
     * @return <code>true</code> if the current character equals the given character.
     */
    public static boolean isCharASCII( char[] chars, int index, char car )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) ||
                ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            return ( ( chars[index] == car ) ? true : false );
        }
    }

    /**
     * Check if the current character is an Hex Char
     *  <hex>    ::= [0x30-0x39] | [0x41-0x46] | [0x61-0x66]
     * 
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return <code>true</code> if the current character is a Hex Char
     */
    public static boolean isHex( byte[] byteArray, int index )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) || ( index < 0 ) ||
                ( index >= byteArray.length ) )
        {
            return false;
        }
        else
        {
            byte c = byteArray[index];

            if ( ( c > 127 ) || ( StringUtils.HEX[c] == false ) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an Hex Char
     *  <hex>    ::= [0x30-0x39] | [0x41-0x46] | [0x61-0x66]
     * 
     * @param chars The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return <code>true</code> if the current character is a Hex Char
     */
    public static boolean isHex( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) ||
                ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            char c = chars[index];

            if ( ( c > 127 ) || ( StringUtils.HEX[c] == false ) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is a digit
     * <digit>    ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param byteArray The buffer which contains the data
     *
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( byte[] byteArray )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) )
        {
            return false;
        }
        else
        {
            return ( ( ( byteArray[0] > 127 ) || ! StringUtils.DIGIT[byteArray[0]] ) ? false : true );
        }
    }

    /**
     * Test if the current character is an Alpha character :
     *  <alpha>    ::= [0x41-0x5A] | [0x61-0x7A]
     * 
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return <code>true</code> if the current character is an Alpha character
     */
    public static boolean isAlphaASCII( byte[] byteArray, int index )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) || ( index < 0 ) ||
                ( index >= byteArray.length ) )
        {
            return false;
        }
        else
        {
            byte c = byteArray[index++];

            if ( ( c > 127 ) || ( StringUtils.ALPHA[c] == false ) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is an Alpha character :
     *  <alpha>    ::= [0x41-0x5A] | [0x61-0x7A]
     * 
     * @param chars The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return <code>true</code> if the current character is an Alpha character
     */
    public static boolean isAlphaASCII( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) ||
                ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            char c = chars[index++];

            if ( ( c > 127 ) || ( StringUtils.ALPHA[c] == false ) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Test if the current character is a digit
     * <digit>    ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( byte[] byteArray, int index )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) || ( index < 0 ) ||
                ( index >= byteArray.length ) )
        {
            return false;
        }
        else
        {
            return ( ( ( byteArray[index] > 127 ) || ! StringUtils.DIGIT[byteArray[index]] ) ? false : true );
        }
    }

    /**
     * Test if the current character is a digit
     * <digit>    ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) ||
                ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            return ( ( ( chars[index] > 127 ) || ! StringUtils.DIGIT[chars[index]] ) ? false : true );
        }
    }

    /**
     * Test if the current character is a digit
     * <digit>    ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
     *
     * @param byteArray The buffer which contains the data
     *
     * @return <code>true</code> if the current character is a Digit
     */
    public static boolean isDigit( char[] chars )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) )
        {
            return false;
        }
        else
        {
            return ( ( ( chars[0] > 127 ) || ! StringUtils.DIGIT[chars[0]] ) ? false : true );
        }
    }

    /**
     * Check if the current character is an 7 bits ASCII CHAR (between 0 and 127).
     *   <char>    ::= <alpha> | <digit> | '-'
     *
     * @param byteArray The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return The position of the next character, if the current one is a CHAR.
     */
    public static boolean isAlphaDigitMinus( byte[] byteArray, int index )
    {
        if ( ( byteArray == null ) || ( byteArray.length == 0 ) || ( index < 0 ) ||
                ( index >= byteArray.length ) )
        {
            return false;
        }
        else
        {
            byte c = byteArray[index++];

            if ( ( c > 127 ) || ( StringUtils.CHAR[c] == false ) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    /**
     * Check if the current character is an 7 bits ASCII CHAR (between 0 and 127).
     *   <char>    ::= <alpha> | <digit> | '-'
     *
     * @param chars The buffer which contains the data
     * @param index Current position in the buffer
     *
     * @return The position of the next character, if the current one is a CHAR.
     */
    public static boolean isAlphaDigitMinus( char[] chars, int index )
    {
        if ( ( chars == null ) || ( chars.length == 0 ) || ( index < 0 ) ||
                ( index >= chars.length ) )
        {
            return false;
        }
        else
        {
            char c = chars[index++];

            if ( ( c > 127 ) || ( StringUtils.CHAR[c] == false ) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }
    
    // The following methods are taken from org.apache.commons.lang.StringUtils
    
    /**
     * The empty String <code>""</code>.
     * @since 2.0
     */
    public static final String EMPTY = "";

    // Empty checks
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Checks if a String is not empty ("") and not null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * <p>Removes spaces (char &lt;= 32) from both start and
     * ends of this String, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * Trim removes start and end characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string, <code>null</code> if null String input
     */
    public static String trim(String str) {
        if ( isEmpty( str ) )
        {
            return str;
        }
        
        char[] array = str.toCharArray();
        int start = 0;
        int end = array.length;
        
        while ( ( start < end ) && ( array[start] == ' ' ) )
        {
            start++;
        }
        
        while ( ( end > start ) && ( array[end - 1] == ' ' ) )
        {
            end--;
        }
        
        return new String( array, start, ( end - start ) );
    }

    /**
     * <p>Removes spaces (char &lt;= 32) from start
     * of this String, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * Trim removes start characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimLeft(null)          = null
     * StringUtils.trimLeft("")            = ""
     * StringUtils.trimLeft("     ")       = ""
     * StringUtils.trimLeft("abc")         = "abc"
     * StringUtils.trimLeft("    abc    ") = "abc    "
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string, <code>null</code> if null String input
     */
    public static String trimLeft( String str ) {
        if ( isEmpty( str ) )
        {
            return str;
        }
        
        char[] array = str.toCharArray();
        int start = 0;
        
        while ( ( start < array.length ) && ( array[start] == ' ' ) )
        {
            start++;
        }
        
        return new String( array, start, array.length - start );
    }
    /**
     * <p>Removes spaces (char &lt;= 32) from start
     * of this array, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * Trim removes start characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimLeft(null)          = null
     * StringUtils.trimLeft("")            = ""
     * StringUtils.trimLeft("     ")       = ""
     * StringUtils.trimLeft("abc")         = "abc"
     * StringUtils.trimLeft("    abc    ") = "abc    "
     * </pre>
     *
     * @param chars  the chars array to be trimmed, may be null
     * @return the position of the first char which is not a space,
     * or the last position of the array.
     */
    public static int trimLeft( char[] chars, int pos ) {
        if ( chars == null )
        {
            return pos;
        }
        
        while ( ( pos < chars.length ) && ( chars[pos] == ' ' ) )
        {
            pos++;
        }
        
        return pos;
    }

    /**
     * <p>Removes spaces (char &lt;= 32) from start
     * of this array, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * Trim removes start characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimLeft(null)          = null
     * StringUtils.trimLeft("")            = ""
     * StringUtils.trimLeft("     ")       = ""
     * StringUtils.trimLeft("abc")         = "abc"
     * StringUtils.trimLeft("    abc    ") = "abc    "
     * </pre>
     *
     * @param bytes  the byte array to be trimmed, may be null
     * @return the position of the first byte which is not a space,
     * or the last position of the array.
     */
    public static int trimLeft( byte[] bytes, int pos ) {
        if ( bytes == null )
        {
            return pos;
        }
        
        while ( ( pos < bytes.length ) && ( bytes[pos] == ' ' ) )
        {
            pos++;
        }
        
        return pos;
    }


    /**
     * <p>Removes spaces (char &lt;= 32) from end
     * of this String, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * Trim removes start characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimRight(null)          = null
     * StringUtils.trimRight("")            = ""
     * StringUtils.trimRight("     ")       = ""
     * StringUtils.trimRight("abc")         = "abc"
     * StringUtils.trimRight("    abc    ") = "    abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string, <code>null</code> if null String input
     */
    public static String trimRight( String str ) {
        if ( isEmpty( str ) )
        {
            return str;
        }
        
        char[] array = str.toCharArray();
        int start = 0;
        int end = array.length;
        
        while ( ( start < end ) && ( array[start] == ' ' ) )
        {
            start++;
        }
        
        return new String( array, start, ( end - start ) );
    }

    /**
     * <p>Removes spaces (char &lt;= 32) from end
     * of this array, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * Trim removes start characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimRight(null)          = null
     * StringUtils.trimRight("")            = ""
     * StringUtils.trimRight("     ")       = ""
     * StringUtils.trimRight("abc")         = "abc"
     * StringUtils.trimRight("    abc    ") = "    abc"
     * </pre>
     *
     * @param chars  the chars array to be trimmed, may be null
     * @return the position of the first char which is not a space,
     * or the last position of the array.
     */
    public static int trimRight( char[] chars, int pos ) {
        if ( chars == null )
        {
            return pos;
        }
        
        while ( ( pos > 0 ) && ( chars[pos - 1] == ' ' ) )
        {
            pos--;
        }
        
        return pos;
    }

    /**
     * <p>Removes spaces (char &lt;= 32) from end
     * of this array, handling <code>null</code> by returning
     * <code>null</code>.</p>
     *
     * Trim removes start characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimRight(null)          = null
     * StringUtils.trimRight("")            = ""
     * StringUtils.trimRight("     ")       = ""
     * StringUtils.trimRight("abc")         = "abc"
     * StringUtils.trimRight("    abc    ") = "    abc"
     * </pre>
     *
     * @param chars  the chars array to be trimmed, may be null
     * @return the position of the first char which is not a space,
     * or the last position of the array.
     */
    public static int trimRight( byte[] bytes, int pos ) {
        if ( bytes == null )
        {
            return pos;
        }
        
        while ( ( pos >= 0 ) && ( bytes[pos] == ' ' ) )
        {
            pos--;
        }
        
        return pos;
    }

    // Case conversion
    //-----------------------------------------------------------------------
    /**
     * <p>Converts a String to upper case as per {@link String#toUpperCase()}.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.upperCase(null)  = null
     * StringUtils.upperCase("")    = ""
     * StringUtils.upperCase("aBc") = "ABC"
     * </pre>
     *
     * @param str  the String to upper case, may be null
     * @return the upper cased String, <code>null</code> if null String input
     */
    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * <p>Converts a String to lower case as per {@link String#toLowerCase()}.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.lowerCase(null)  = null
     * StringUtils.lowerCase("")    = ""
     * StringUtils.lowerCase("aBc") = "abc"
     * </pre>
     *
     * @param str  the String to lower case, may be null
     * @return the lower cased String, <code>null</code> if null String input
     */
    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    // Equals
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two Strings, returning <code>true</code> if they are equal.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @see java.lang.String#equals(Object)
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return <code>true</code> if the Strings are equal, case sensitive, or
     *  both <code>null</code>
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
    
    /**
     * Return an UTF-8 encoded String
     * @param bytes The byte array to be transformed to a String
     * @return A String. 
     */
    public static String toUtf8( byte[] bytes )
    {
        try
        {
            return new String( bytes, "UTF-8" );
        }
        catch ( UnsupportedEncodingException uee )
        {
            return "";
        }
    }

    /**
     * Return an UTF-8 encoded String
     * @param bytes The byte array to be transformed to a String
     * @return A String. 
     */
    public static byte[] getBytesUtf8( String string )
    {
        try
        {
            return string.getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException uee )
        {
            return new byte[]{};
        }
    }
}
