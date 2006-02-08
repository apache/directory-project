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
package org.apache.ldap.common.util ;


import java.io.File ;
import java.util.List ;
import java.io.FileFilter ;
import java.util.ArrayList ;

import org.apache.regexp.RE ;
import org.apache.regexp.RESyntaxException ;


/**
 * Various string manipulation methods that are more efficient then chaining
 * string operations: all is done in the same buffer without creating a bunch
 * of string objects.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class StringTools
{
    /**
     * Trims several consecutive characters into one. 
     *
     * @param str the string to trim consecutive characters of
     * @param ch the character to trim down
     * @return the newly trimmed down string
     */
    public static String trimConsecutiveToOne( String str, char ch )
    {
        char[] buffer = str.toCharArray();
        char[] newbuf = new char[buffer.length];
        int pos = 0;
        boolean same = false;

        for ( int i = 0; i < buffer.length; i++ )
        {
            char car = buffer[i];
            
            if ( car == ch )
            {
                if ( same )
                {
                    continue;
                }
                else
                { 
                    same = true;
                    newbuf[pos++] = car ;
                }
            }
            else
            {
                same = false;
                newbuf[pos++] = car ;
            }
        }

        return new String(newbuf, 0, pos);
    }


    /**
     * A deep trim of a string remove whitespace from the ends as well as
     * excessive whitespace within the inside of the string between
     * non-whitespace characters.  A deep trim reduces internal whitespace
     * down to a single space to perserve the whitespace separated tokenization
     * order of the String.
     *
     * @param string the string to deep trim.
     * @return the trimmed string.
     */
    public static String deepTrim( String string )
    {
        return deepTrim( string, false ) ;
    }


    /**
     * This does the same thing as a trim but we also lowercase the string while
     * performing the deep trim within the same buffer.  This saves us from
     * having to create multiple String and StringBuffer objects and is much
     * more efficient.
     * 
     * @see StringTools#deepTrim( String )
     */
    public static String deepTrimToLower( String string )
    {
        return deepTrim( string, true ) ;
    }


    /**
     * Put common code to deepTrim(String) and deepTrimToLower here.
     * 
     * @param str the string to deep trim
     * @param toLowerCase how to normalize for case: upper or lower
     * @return the deep trimmed string
     * @see StringTools#deepTrim( String )
     */
    public static String deepTrim( String str, boolean toLowerCase )
    {
        if ( null == str )
        {
            return null ;
        }

        char ch ;
        char[] buf = str.toCharArray();
        char[] newbuf = new char[buf.length];
        boolean wsSeen = true;
        int pos = 0;

        for ( int i = 0; i < str.length(); i++ )
        {
            ch = buf[i] ;

            // filter out all uppercase characters
            if ( toLowerCase)
            {
                if ( Character.isUpperCase( ch ) ) 
                {
                    ch = Character.toLowerCase( ch ) ;
                }
            }

            // Check to see if we should add space
            if ( Character.isWhitespace( ch ) )
            {
                // If the buffer has had characters added already check last
                // added character.  Only append a spc if last character was
                // not whitespace.
                if ( wsSeen) 
                {
                    continue;
                }
                else
                {
                    wsSeen = true;
                    newbuf[pos++] = ch;
                }
            } 
            else 
            {
                // Add all non-whitespace
                wsSeen = false;
                newbuf[pos++] = ch;
            }
        }

        return new String( newbuf, 0, (wsSeen ? pos - 1 : pos) );
    }


    /**
     * Truncates large Strings showing a portion of the String's head and tail
     * with the center cut out and replaced with '...'. Also displays the total
     * length of the truncated string so size of '...' can be interpreted.
     * Useful for large strings in UIs or hex dumps to log files.
     *
     * @param a_str the string to truncate
     * @param a_head the amount of the head to display
     * @param a_tail the amount of the tail to display
     * @return the center truncated string
     */
    public static String centerTrunc( String a_str, int a_head, int a_tail )
    {
        StringBuffer l_buf = null ;

        // Return as-is if String is smaller than or equal to the head plus the
        // tail plus the number of characters added to the trunc representation
        // plus the number of digits in the string length.
        if ( a_str.length() <= ( a_head + a_tail + 7 + a_str.length() / 10 ) ) 
        {
            return a_str ;
        }

        l_buf = new StringBuffer() ;
        l_buf.append( '[' ).append( a_str.length() ).append( "][" ) ;
        l_buf.append( a_str.substring( 0, a_head ) ).append( "..." ) ;
        l_buf.append( a_str.substring( a_str.length() - a_tail ) ) ; 
        l_buf.append( ']' ) ;
        return l_buf.toString() ;
    }


    /**
     * Gets a hex string from byte array.
     *
     * @param a_res the byte array
     * @return the hex string representing the binary values in the array
     */
    public static String toHexString( byte[] a_res ) 
    {
        StringBuffer l_buf = new StringBuffer( a_res.length << 1 ) ;
        for ( int ii = 0; ii < a_res.length; ii++ ) 
        {
            String l_digit = Integer.toHexString( 0xFF & a_res[ii] ) ;
            if ( l_digit.length() == 1 ) 
            {
                l_digit = '0' + l_digit ;
            }
            l_buf.append( l_digit ) ;
        }
        return l_buf.toString().toUpperCase() ;
    }


    /**
     * Get byte array from hex string
     *
     * @param a_hexString the hex string to convert to a byte array
     * @return the byte form of the hex string.
     */
    public static byte[] toByteArray( String a_hexString ) 
    {
        int l_arrLength = a_hexString.length() >> 1 ;
        byte l_buf[] = new byte[l_arrLength] ;
        for ( int ii = 0; ii < l_arrLength; ii++ ) 
        {
            int l_index = ii << 1 ;
            String l_digit = a_hexString.substring( l_index, l_index + 2 ) ;
            l_buf[ii] = ( byte ) Integer.parseInt( l_digit, 16 ) ;
        }
        return l_buf;
    }

    /**
     * This method is used to insert HTML block dynamically
     *
     * @param a_source the HTML code to be processes
     * @param a_bReplaceNl if true '\n' will be replaced by <br>
     * @param a_bReplaceTag if true '<' will be replaced by &lt; and
     *                          '>' will be replaced by &gt;
     * @param a_bReplaceQuote if true '\"' will be replaced by &quot;
     * @return the formated html block
     */
    public static String formatHtml( String a_source, boolean a_bReplaceNl,
        boolean a_bReplaceTag, boolean a_bReplaceQuote ) 
    {
        StringBuffer l_buf = new StringBuffer() ;
        int l_len = a_source.length() ;
        
        for ( int ii = 0; ii < l_len; ii++ ) 
        {
            char ch = a_source.charAt( ii ) ;
            switch ( ch ) 
            {
            case '\"':
                if ( a_bReplaceQuote ) 
                {
                    l_buf.append( "&quot;" ) ;
                }
                else 
                {
                    l_buf.append( ch ) ;
                }
                break ; 

            case '<':
                if ( a_bReplaceTag ) 
                {
                    l_buf.append( "&lt;" ) ;
                }
                else 
                {
                    l_buf.append( ch );
                }
                break ;

            case '>':
                if ( a_bReplaceTag ) 
                {
                    l_buf.append( "&gt;" ) ;
                }
                else
                {
                    l_buf.append( ch ) ;
                }
                break ;

            case '\n':
                if ( a_bReplaceNl ) 
                {
                    if ( a_bReplaceTag ) 
                    {
                        l_buf.append( "&lt;br&gt;" ) ;
                    }
                    else 
                    {
                        l_buf.append( "<br>" ) ;
                    }
                } 
                else 
                {
                    l_buf.append( ch ) ;
                }
                break ;

            case '\r':
                break ;

            case '&':
                l_buf.append( "&amp;" ) ;
                break ;

            default:
                l_buf.append( ch ) ;
                break ;
            }
        }
        
        return l_buf.toString() ;
    }


    /**
     * Creates a regular expression from an LDAP substring assertion 
     * filter specification.
     *
     * @param a_initial the initial fragment before wildcards
     * @param a_any fragments surrounded by wildcards if any
     * @param a_final the final fragment after last wildcard if any
     * @return the regular expression for the substring match filter
     * @throws RESyntaxException if a syntactically correct regular expression
     * cannot be compiled
     */
    public static RE getRegex( String a_initial, String [] a_any, 
        String a_final ) throws RESyntaxException
    {
        StringBuffer l_buf = new StringBuffer() ;

        if ( a_initial != null ) 
        {
            l_buf.append( '^' ).append( a_initial ) ;
        }

        if ( a_any != null ) 
        {
            for ( int ii = 0; ii < a_any.length; ii++ ) 
            {
                l_buf.append( ".*" ).append( a_any[ii] ) ;
            }
        }

        if ( a_final != null ) 
        {
            l_buf.append( ".*" ).append( a_final ) ;
        }

        return new RE( l_buf.toString() ) ;
    }


    /**
     * Generates a regular expression from an LDAP substring match expression by
     * parsing out the supplied string argument.
     *
     * @param a_ldapRegex the substring match expression
     * @return the regular expression for the substring match filter
     * @throws RESyntaxException if a syntactically correct regular expression
     * cannot be compiled
     */
    public static RE getRegex( String a_ldapRegex )
        throws RESyntaxException
    {
        if ( a_ldapRegex == null ) 
        {
            throw new RESyntaxException( "Regex was null" ) ;
        }

        ArrayList l_any = new ArrayList() ;
        String l_remaining = a_ldapRegex ;
        int l_index = l_remaining.indexOf( '*' ) ;

        if ( l_index == -1 ) 
        {
            throw new RESyntaxException( "Ldap regex must have wild cards!" ) ;
        }

        String l_initial = null ;
        if ( l_remaining.charAt( 0 ) != '*' ) 
        {
            l_initial = l_remaining.substring( 0, l_index ) ;
        }
        
        l_remaining = l_remaining.substring( 
            l_index + 1, l_remaining.length() ) ;

        while ( ( l_index = l_remaining.indexOf( '*' ) ) != -1 ) 
        {
            l_any.add( l_remaining.substring( 0, l_index ) ) ;
            l_remaining = l_remaining.substring( l_index + 1,
                l_remaining.length() ) ;
        }

        String l_final = null ;
        if ( !l_remaining.endsWith( "*" ) && l_remaining.length() > 0 ) 
        {
            l_final = l_remaining ;
        }

        if ( l_any.size() > 0 ) 
        {
            String [] l_anyStrs = new String [ l_any.size() ] ;
            for ( int ii = 0; ii < l_anyStrs.length; ii++ ) 
            {
                l_anyStrs[ii] = ( String ) l_any.get( ii ) ;
            }
    
            return getRegex( l_initial, l_anyStrs, l_final ) ;
        }

        return getRegex( l_initial, null, l_final ) ;
    }


    /**
     * Splits apart a OS separator delimited set of paths in a string into
     * multiple Strings.  File component path strings are returned within a
     * List in the order they are found in the composite path string.
     * Optionally, a file filter can be used to filter out path strings to
     * control the components returned.  If the filter is null all path
     * components are returned.
     *
     * @param a_paths a set of paths delimited using the OS path separator
     * @param a_filter a FileFilter used to filter the return set
     * @return the filter accepted path component Strings in the order
     * encountered
     */
    public static List getPaths( String a_paths, FileFilter a_filter )
    {
        final int l_max = a_paths.length() - 1 ;
        int l_start = 0 ;
        int l_stop = -1 ;
        String l_path = null ;
        ArrayList l_list = new ArrayList() ;

        // Abandon with no values if paths string is null
        if ( a_paths == null || a_paths.trim().equals( "" ) )
        {
            return l_list ;
        }

        // Loop spliting string using OS path separator: terminate
        // when the start index is at the end of the paths string.
        while ( l_start < l_max )
        {
            l_stop = a_paths.indexOf( File.pathSeparatorChar, l_start ) ;

            // The is no file sep between the start and the end of the string
            if ( l_stop == -1 )
            {
                // If we have a trailing path remaining without ending separator
                if ( l_start < l_max )
                {
                    // Last path is everything from start to the string's end
                    l_path = a_paths.substring( l_start ) ;

                    // Protect against consecutive separators side by side
                    if ( !l_path.trim().equals( "" ) )
                    {
                        // If filter is null add path, if it is not null add the
                        // path only if the filter accepts the path component.
                        if ( a_filter == null ||
                            a_filter.accept( new File( l_path ) ) )
                        {
                            l_list.add( l_path ) ;
                        }
                    }
                }

                break ; // Exit loop no more path components left!
            }

            // There is a separator between start and the end if we got here!
            // start index is now at 0 or the index of last separator + 1
            // stop index is now at next separator in front of start index
            l_path = a_paths.substring( l_start, l_stop ) ;

            // Protect against consecutive separators side by side
            if ( !l_path.trim().equals( "" ) )
            {
                // If filter is null add path, if it is not null add the path
                // only if the filter accepts the path component.
                if ( a_filter == null || a_filter.accept( new File( l_path ) ) )
                {
                    l_list.add( l_path ) ;
                }
            }

            // Advance start index past separator to start of next path comp
            l_start = l_stop + 1 ;
        }

        return l_list ;
    }

    /**
     * Forked version of a.o.commons.lang.StringUtils.isEmpty
     * 
     * Checks if a string is null or empty
     * 
     * @param string The string to be tested
     * @return <code>true</code> if the string is null or empty.
     */
    public static boolean isEmpty(String string) 
    {
        return ( (string == null) || (string.length() == 0) );
    }
}

