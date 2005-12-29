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
/*package org.apache.asn1new.ldap.codec.primitives;

import java.io.UnsupportedEncodingException;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.util.StringTools;

/**
 * This class parses a DN. 
 * 
 * The DN MUST respect this BNF grammar (as of RFC2253, par. 3, and RFC1779, fig. 1) <br>
 * 
 * <p>
 *-    &lt;distinguishedName&gt;      ::= &lt;name&gt; | e <br>
 *-    &lt;name&gt;                   ::= &lt;name-component&gt; &lt;name-components&gt; <br>
 *-    &lt;name-components&gt;        ::= &lt;spaces&gt; &lt;separator&gt; &lt;spaces&gt; &lt;name-component&gt; &lt;name-components&gt; | e <br>
 *-    &lt;name-component&gt;         ::= &lt;attributeType&gt; &lt;spaces&gt; '=' &lt;spaces&gt; &lt;attributeValue&gt; &lt;attributeTypeAndValues&gt; <br>
 *-    &lt;attributeTypeAndValues&gt; ::= &lt;spaces&gt; '+' &lt;spaces&gt; &lt;attributeType&gt; &lt;spaces&gt; '=' &lt;spaces&gt; &lt;attributeValue&gt; &lt;attributeTypeAndValues&gt; | e <br>
 *-    &lt;attributeType&gt;          ::= [a-zA-Z] &lt;keychars&gt; | &lt;oidPrefix&gt; [0-9] &lt;digits&gt; &lt;oids&gt; | [0-9] &lt;digits&gt; &lt;oids&gt; <br>
 *-    &lt;keychars&gt;               ::= [a-zA-Z] &lt;keychars&gt; | [0-9] &lt;keychars&gt; | '-' &lt;keychars&gt; | e <br>
 *-    &lt;oidPrefix&gt;              ::= 'OID.' | 'oid.' | e <br>
 *-    &lt;oids&gt;                   ::= '.' [0-9] &lt;digits&gt; &lt;oids&gt; | e <br>
 *-    &lt;attributeValue&gt;         ::= &lt;pairs-or-strings&gt; | '#' &lt;hexstring&gt; |'"' &lt;quotechar-or-pairs&gt; '"' <br>
 *-    &lt;pairs-or-strings&gt;       ::= '\' &lt;pairchar&gt; &lt;pairs-or-strings&gt; | &lt;stringchar&gt; &lt;pairs-or-strings&gt; | e <br>
 *-    &lt;quotechar-or-pairs&gt;     ::= &lt;quotechar&gt; &lt;quotechar-or-pairs&gt; | '\' &lt;pairchar&gt; &lt;quotechar-or-pairs&gt; | e <br>
 *-    &lt;pairchar&gt;               ::= ',' | '=' | '+' | '&lt;' | '&gt;' | '#' | ';' | '\' | '"' | [0-9a-fA-F] [0-9a-fA-F]  <br>
 *-    &lt;hexstring&gt;              ::= [0-9a-fA-F] [0-9a-fA-F] &lt;hexpairs&gt; <br>
 *-    &lt;hexpairs&gt;               ::= [0-9a-fA-F] [0-9a-fA-F] &lt;hexpairs&gt; | e <br>
 *-    &lt;digits&gt;                 ::= [0-9] &lt;digits&gt; | e <br>
 *-    &lt;stringchar&gt;             ::= [0x00-0xFF] - [,=+&lt;&gt;#;\"\n\r] <br>
 *-    &lt;quotechar&gt;              ::= [0x00-0xFF] - [\"] <br>
 *-    &lt;separator&gt;              ::= ',' | ';' <br>
 *-    &lt;spaces&gt;                 ::= ' ' &lt;spaces&gt; | e <br>
 * </p>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 *
public class LdapDN extends RelativeLdapDN
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** A null LdapDN *
    public transient static final LdapDN EMPTY_STRING = new LdapDN();

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Construct an empty LdapDN object
     *
    public LdapDN()
    {
        super(0, false);
    }
    
    /**
     * Parse a buffer and checks that it is a valid DN <br>
     * <p>
     * &lt;distinguishedName&gt;     ::= &lt;name&gt; | e <br>
     * &lt;name&gt;                ::= &lt;name-component&gt; &lt;name-components&gt; <br>
     * &lt;name-components&gt;    ::= &lt;spaces&gt; &lt;separator&gt; &lt;spaces&gt; &lt;name-component&gt; &lt;name-components&gt; | e <br>
     * </p>
     * 
     * @param bytes The byte buffer that contains the DN
     * @exception A DecoderException is thrown if the buffer does not contains a valid DN.
     *
    public LdapDN( byte[] bytes ) throws DecoderException
    {
        if ( bytes == null || bytes.length == 0)
        {
            return;
        }
        
        int pos = 0;

        // <name>             ::= <name-component> <name-components>
        // <name-components> ::= <spaces> <separator> <spaces> <name-component> <name-components> | e
        if ( ( pos = parseNameComponent( bytes, pos ) ) != -1 )
        {

            do
            {

                if ( ( StringTools.isCharASCII( bytes, pos, ',' ) == false ) &&
                        ( StringTools.isCharASCII( bytes, pos, ';' ) == false ) )
                {

                    break;
                }

                bytes[pos] = ',';
                pos++;

                pos = parseSpaces( bytes, pos );
            }
            while ( ( pos = parseNameComponent( bytes, pos ) ) != -1 );
        }
        else
        {
            try 
            {
                throw new DecoderException( "Bad DN : " + new String( bytes, "UTF-8" ) );
            }
            catch ( UnsupportedEncodingException uee )
            {
                throw new DecoderException( "Bad DN : " + StringTools.dumpBytes( bytes ) );
            }
            
        }

        setData(bytes);
    }
}
*/

package org.apache.asn1new.ldap.codec.primitives;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import org.apache.ldap.common.util.StringTools;

/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapDN extends LdapString implements Name
{
    /**
     * Declares the Serial Version Uid.
     * 
     * @see <a href="http://c2.com/cgi/wiki?AlwaysDeclareSerialVersionUid">Always Declare Serial Version Uid</a>
     */
    private static final long serialVersionUID = 1L;

    /** Value returned by the compareTo method if values are not equals */
    public final static int NOT_EQUALS = -1;

    /** Value returned by the compareTo method if values are equals */
    public final static int EQUALS = 0;

    //~ Static fields/initializers -----------------------------------------------------------------
    /** The RDNs that are elements of the DN */
    private List rdns = new ArrayList(5); 
    
    /** The user provided name */
    private String upName;
    
    /** A null LdapDN */
    public transient static final LdapDN EMPTY_LDAPDN = new LdapDN();

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Construct an empty LdapDN object
     */
    public LdapDN()
    {
        super();
        upName = "";
    }
    
    /**
     * Parse a String and checks that it is a valid DN <br>
     * <p>
     * &lt;distinguishedName&gt;     ::= &lt;name&gt; | e <br>
     * &lt;name&gt;                ::= &lt;name-component&gt; &lt;name-components&gt; <br>
     * &lt;name-components&gt;    ::= &lt;spaces&gt; &lt;separator&gt; &lt;spaces&gt; &lt;name-component&gt; &lt;name-components&gt; | e <br>
     * </p>
     * 
     * @param bytes The byte buffer that contains the DN
     * @exception A InvalidNameException is thrown if the buffer does not contains a valid DN.
     */
    public LdapDN( String string ) throws InvalidNameException
    {
        if ( StringTools.isNotEmpty( string ) )
        {
            try
            {
                string.getBytes( "UTF-8" );
            }
            catch ( UnsupportedEncodingException uee )
            {
                throw new InvalidNameException( "The byte array is not an UTF-8 encoded Unicode String : " + uee.getMessage() );
            }

            DNParser.parse( string, rdns );
        }
        else
        {
            this.string = "";
        }
        
        normalize();
        upName = string;
    }
    
    /**
     * Parse a buffer and checks that it is a valid DN <br>
     * <p>
     * &lt;distinguishedName&gt;     ::= &lt;name&gt; | e <br>
     * &lt;name&gt;                ::= &lt;name-component&gt; &lt;name-components&gt; <br>
     * &lt;name-components&gt;    ::= &lt;spaces&gt; &lt;separator&gt; &lt;spaces&gt; &lt;name-component&gt; &lt;name-components&gt; | e <br>
     * </p>
     * 
     * @param bytes The byte buffer that contains the DN
     * @exception A InvalidNameException is thrown if the buffer does not contains a valid DN.
     */
    public LdapDN( byte[] bytes ) throws InvalidNameException
    {
        try
        {
            upName = new String( bytes, "UTF-8" );
            DNParser.parse( upName, rdns );
            string = toString();
             
            normalize();
        }
        catch ( UnsupportedEncodingException uee )
        {
            throw new InvalidNameException( "The byte array is not an UTF-8 encoded Unicode String : " + uee.getMessage() );
        }
    }
    

    /**
     * Normalize the DN by triming useless spaces and lowercasing names.
     * @return a normalized form of the DN
     */
    private void normalize()
    {
        StringBuffer sb = new StringBuffer();
        
        if ( rdns == null )
        {
            string = "";
        }
        
        Iterator elements = rdns.iterator();
        boolean isFirst = true;
        
        while ( elements.hasNext() )
        {
            LdapRDN rdn = (LdapRDN)elements.next();
            
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append(',');
            }
            
            sb.append( rdn.toString() );
        }

        string = sb.toString();
        
        try
        {
            bytes = string.getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException uee )
        {
            // We can't reach this point
        }
    }
    
    /**
     * Return the normalized DN as a String,
     * @return A String representing the normalized DN
     */
    public String toString()
    {
        if ( ( rdns == null ) || ( rdns.size() == 0 ) )
        {
            return "";
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            
            for ( int i = 0; i < rdns.size(); i++ )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( ',' );
                }
                
                sb.append( ( (LdapRDN)rdns.get( i ) ) );
            }
            
            return sb.toString();
        }
    }

    /**
     * Get the initial DN (without normalization) 
     * @return The DN as a String
     */
    public String getName()
    {
        return ( upName == null ? "" : upName );
    }

    /**
     * Get the number of NameComponent conatained in this LdapDN
     * 
     * @return The number of NameComponent conatained in this LdapDN
     */
    public int size()
    {
        return rdns.size();
    }
    
    /**
     * Determines whether this name starts with a specified prefix.
     * A name <tt>name</tt> is a prefix if it is equal to
     * <tt>getPrefix(name.size())</tt>.
     * 
     * Be aware that for a specific DN like :
     * 
     * cn=xxx, ou=yyy
     * 
     * the startsWith method will retourn true with ou=yyy, and
     * false with cn=xxx
     *
     * @param name the name to check
     * @return  true if <tt>name</tt> is a prefix of this name, false otherwise
     */
    public boolean startsWith ( Name name )
    {
        if ( name instanceof LdapDN )
        {
            LdapDN nameDN = (LdapDN)name;
            
            if ( nameDN.size() == 0 )
            {
                return true;
            }

            if ( nameDN.size() > size() )
            {
                // The name is longer than the current LdapDN.
                return false;
            }
            
            // Ok, iterate through all the RDN of the name,
            // starting a the end of the current list.
            
            for ( int i = nameDN.size() - 1; i >= 0; i-- )
            {
                LdapRDN nameRdn = (LdapRDN)(nameDN.rdns.get( nameDN.rdns.size() - i - 1 ));
                LdapRDN ldapRdn = (LdapRDN)rdns.get( rdns.size() - i - 1 );
                
                if ( nameRdn.compareTo(ldapRdn) != 0 )
                {
                    return false;
                }
            }
            
            return true;
        }
        else
        {
            // We don't accept a Name which is not a LdapName
            return name == null;
        }
    }

    /**
     * Determines whether this name ends with a specified suffix.
     * A name <tt>name</tt> is a suffix if it is equal to
     * <tt>getSuffix(size()-name.size())</tt>.
     * 
     * Be aware that for a specific DN like :
     * 
     * cn=xxx, ou=yyy
     * 
     * the endsWith method will retourn true with cn=xxx, and
     * false with ou=yyy
     *
     * @param name the name to check
     * @return  true if <tt>name</tt> is a suffix of this name, false otherwise
     */
    public boolean endsWith ( Name name )
    {
        if ( name instanceof LdapDN )
        {
            LdapDN nameDN = (LdapDN)name;
            
            if ( nameDN.size() == 0 )
            {
                return true;
            }

            if ( nameDN.size() > size() )
            {
                // The name is longer than the current LdapDN.
                return false;
            }
            
            // Ok, iterate through all the RDN of the name
            for ( int i = 0; i < nameDN.size(); i++ )
            {
                LdapRDN nameRdn = (LdapRDN)(nameDN.rdns.get( i ));
                LdapRDN ldapRdn = (LdapRDN)rdns.get( i );
                
                if ( nameRdn.compareTo(ldapRdn) != 0 )
                {
                    return false;
                }
            }
            
            return true;
        }
        else
        {
            // We don't accept a Name which is not a LdapName
            return name == null;
        }
    }
    
    /**
     * Determines whether this name is empty.
     * An empty name is one with zero components.
     *
     * @return  true if this name is empty, false otherwise
     */
    public boolean isEmpty()
    {
        return ( rdns.size() == 0 );
    }
    
    /**
     * Retrieves a component of this name.
     *
     * @param posn
     *      the 0-based index of the component to retrieve.
     *      Must be in the range [0,size()).
     * @return  the component at index posn
     * @throws  ArrayIndexOutOfBoundsException
     *      if posn is outside the specified range
     */
    public String get( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return "";
        }
        else
        {
            LdapRDN rdn = (LdapRDN)rdns.get( rdns.size() - posn - 1 );
        
            return rdn.toString();
        }
    }
    
    /**
     * Retrieves the components of this name as an enumeration
     * of strings.  The effect on the enumeration of updates to
     * this name is undefined.  If the name has zero components,
     * an empty (non-null) enumeration is returned.
     *
     * @return  an enumeration of the components of this name, each a string
     */
    public Enumeration getAll()
    {
        /*
         * Note that by accessing the name component using the get() method on
         * the name rather than get() on the list we are reading components from
         * right to left with increasing index values.  LdapName.get() does the
         * index translation on m_list for us. 
         */
        return new Enumeration() 
        {
            private int pos ;

            public boolean hasMoreElements()
            {
                return pos < rdns.size() ;
            }

            public Object nextElement()
            {
                if ( pos >= rdns.size() ) 
                {
                    throw new NoSuchElementException() ;
                }

                Object obj = get( pos ) ;
                pos++ ;
                return obj ;
            }
        };
    }
    
    /**
     * Creates a name whose components consist of a prefix of the
     * components of this name.  Subsequent changes to
     * this name will not affect the name that is returned and vice versa.
     *
     * @param posn
     *      the 0-based index of the component at which to stop.
     *      Must be in the range [0,size()].
     * @return  a name consisting of the components at indexes in
     *      the range [0,posn].
     * @throws  ArrayIndexOutOfBoundsException
     *      if posn is outside the specified range
     */
    public Name getPrefix( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return EMPTY_LDAPDN;
        }
        
        if ( ( posn < 0 ) || ( posn > rdns.size() ) )
        {
            throw new ArrayIndexOutOfBoundsException("The posn(" + posn + ") should be in the range [0, " + rdns.size() + "]");
        }
        
        LdapDN newLdapDN = new LdapDN();
        
        for (int i = rdns.size() - posn; i < rdns.size(); i++ )
        {
            // Don't forget to clone the rdns !
            newLdapDN.rdns.add( ( (LdapRDN)rdns.get( i ) ).clone() );
        }

        newLdapDN.normalize();
        newLdapDN.string = newLdapDN.toString();
        newLdapDN.upName = newLdapDN.string;
        
        return newLdapDN;
    }

    /**
     * Creates a name whose components consist of a suffix of the
     * components in this name.  Subsequent changes to
     * this name do not affect the name that is returned and vice versa.
     *
     * @param posn
     *      the 0-based index of the component at which to start.
     *      Must be in the range [0,size()].
     * @return  a name consisting of the components at indexes in
     *      the range [posn,size()].  If posn is equal to 
     *      size(), an empty name is returned.
     * @throws  ArrayIndexOutOfBoundsException
     *      if posn is outside the specified range
     */
    public Name getSuffix( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return EMPTY_LDAPDN;
        }
        
        if ( ( posn < 0 ) || ( posn > rdns.size() ) )
        {
            throw new ArrayIndexOutOfBoundsException("The posn(" + posn + ") should be in the range [0, " + rdns.size() + "]");
        }
        
        LdapDN newLdapDN = new LdapDN();
        
        for (int i = 0; i < size() - posn; i++ )
        {
            // Don't forget to clone the rdns !
            newLdapDN.rdns.add( ( (LdapRDN)rdns.get( i ) ).clone() );
        }

        newLdapDN.normalize();
        newLdapDN.string = newLdapDN.toString();
        newLdapDN.upName = newLdapDN.string;

        return newLdapDN;
    }
    /**
     * Adds the components of a name -- in order -- to the end of this name.
     *
     * @param suffix
     *      the components to add
     * @return  the updated name (not a new one)
     *
     * @throws  InvalidNameException if <tt>suffix</tt> is not a valid name,
     *      or if the addition of the components would violate the syntax
     *      rules of this name
     */
    public Name addAll( Name suffix ) throws InvalidNameException
    {
        addAll( rdns.size(), suffix );
        
        return this;
    }

    /**
     * Adds the components of a name -- in order -- at a specified position
     * within this name.
     * Components of this name at or after the index of the first new
     * component are shifted up (away from 0) to accommodate the new
     * components.
     *
     * @param name
     *      the components to add
     * @param posn
     *      the index in this name at which to add the new
     *      components.  Must be in the range [0,size()].
     * @return  the updated name (not a new one)
     *
     * @throws  ArrayIndexOutOfBoundsException
     *      if posn is outside the specified range
     * @throws  InvalidNameException if <tt>n</tt> is not a valid name,
     *      or if the addition of the components would violate the syntax
     *      rules of this name
     */
    public Name addAll(int posn, Name name) throws InvalidNameException
    {
        if (name instanceof LdapDN )
        {
            if ( ( name == null ) || ( name.size() == 0 ) )
            {
                return this;
            }
            
            // Concatenate the rdns
            rdns.addAll( size() - posn, ((LdapDN)name).rdns );

            // Regenerate the normalized name and the original string
            normalize();
            
            upName = toString();
            
            return this;
        }
        else
        {
            throw new InvalidNameException( "The suffix is not a LdapDN" );
        }
    }

    /**
     * Adds a single component to the end of this name.
     *
     * @param comp
     *      the component to add
     * @return  the updated name (not a new one)
     *
     * @throws  InvalidNameException if adding <tt>comp</tt> would violate
     *      the syntax rules of this name
     */
    public Name add(String comp) throws InvalidNameException
    {
        // We have to parse the nameComponent which is given as an argument
        LdapRDN newRdn = new LdapRDN( comp );
        
        rdns.add( 0, newRdn );
        normalize();

        upName = toString();

        return this;
    }

    /**
     * Adds a single component at a specified position within this name.
     * Components of this name at or after the index of the new component
     * are shifted up by one (away from index 0) to accommodate the new
     * component.
     *
     * @param comp
     *      the component to add
     * @param posn
     *      the index at which to add the new component.
     *      Must be in the range [0,size()].
     * @return  the updated name (not a new one)
     *
     * @throws  ArrayIndexOutOfBoundsException
     *      if posn is outside the specified range
     * @throws  InvalidNameException if adding <tt>comp</tt> would violate
     *      the syntax rules of this name
     */
    public Name add(int posn, String comp) throws InvalidNameException
    {
        if ( ( posn < 0 ) || ( posn > size() ) )
        {
            throw new ArrayIndexOutOfBoundsException("The posn(" + posn + ") should be in the range [0, " + rdns.size() + "]"); 
        }

        // We have to parse the nameComponent which is given as an argument
        LdapRDN newRdn = new LdapRDN( comp );
        
        int realPos = size() - posn;
        rdns.add( realPos, newRdn );
        normalize();
        
        upName = toString();
        
        return this;
    }

    /**
     * Removes a component from this name.
     * The component of this name at the specified position is removed.
     * Components with indexes greater than this position
     * are shifted down (toward index 0) by one.
     *
     * @param posn
     *      the index of the component to remove.
     *      Must be in the range [0,size()).
     * @return  the component removed (a String)
     *
     * @throws  ArrayIndexOutOfBoundsException
     *      if posn is outside the specified range
     * @throws  InvalidNameException if deleting the component
     *      would violate the syntax rules of the name
     */
    public Object remove(int posn) throws InvalidNameException
    {
        if ( rdns.size() == 0 )
        {
            return EMPTY_LDAPDN;
        }
        
        if ( ( posn < 0 ) || ( posn >= rdns.size() ) )
        {
            throw new ArrayIndexOutOfBoundsException("The posn(" + posn + ") should be in the range [0, " + rdns.size() + "]");
        }
        
        int realPos = size() - posn - 1;
        LdapRDN rdn = (LdapRDN)rdns.remove( realPos );
        normalize();
        
        upName = toString();
        
        return rdn;
    }

    /**
     * Generates a new copy of this name.
     * Subsequent changes to the components of this name will not
     * affect the new copy, and vice versa.
     *
     * @return  a copy of this name
     *
     * @see Object#clone()
     */
    public Object clone()
    {
        try
        {
            LdapDN dn = (LdapDN)super.clone();
            
            for ( int i = 0; i < rdns.size(); i++ )
            {
                dn.rdns.set( i, ((LdapRDN)rdns.get( i ) ).clone() );
            }
            
            return dn;
        }
        catch ( CloneNotSupportedException cnse )
        {
            throw new Error( "Assertion failure" );
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof String )
        {
            return toString().equals( obj ) ;
        } 
        else if ( obj instanceof LdapDN )
        {
            LdapDN name = ( LdapDN ) obj ;

            if ( name.size() != this.size() )
            {
                return false ;
            }

            for ( int i = 0; i < size(); i++ ) 
            {
                if ( ( (LdapRDN)name.rdns.get( i ) ).compareTo( rdns.get( i ) ) == LdapRDN.NOT_EQUALS)
                {
                    return false;
                }
            }

            // All components matched so we return true
            return true ;
        } 
        else 
        {
            return false ;
        }
    }

    /**
     * Compares this name with another name for order.
     * Returns a negative integer, zero, or a positive integer as this
     * name is less than, equal to, or greater than the given name.
     *
     * <p> As with <tt>Object.equals()</tt>, the notion of ordering for names 
     * depends on the class that implements this interface.
     * For example, the ordering may be
     * based on lexicographical ordering of the name components.
     * Specific attributes of the name, such as how it treats case,
     * may affect the ordering.  In general, two names of different
     * classes may not be compared.
     *
     * @param   obj the non-null object to compare against.
     * @return  a negative integer, zero, or a positive integer as this name
     *      is less than, equal to, or greater than the given name
     * @throws  ClassCastException if obj is not a <tt>Name</tt> of a
     *      type that may be compared with this name
     *
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Object obj)
    {
        if ( obj instanceof LdapDN )
        {
            LdapDN ldapDN = (LdapDN)obj;
            
            if ( ldapDN.size() != size() )
            {
                return NOT_EQUALS;
            }
            
            Iterator dn1Iter = rdns.iterator();
            Iterator dn2Iter = ldapDN.rdns.iterator();
            
            while ( dn1Iter.hasNext() && dn2Iter.hasNext() )
            {
                LdapRDN rdn1 = (LdapRDN)dn1Iter.next();
                LdapRDN rdn2 = (LdapRDN)dn2Iter.next();
                
                if ( rdn1.compareTo( rdn2 ) == LdapRDN.NOT_EQUALS )
                {
                    return NOT_EQUALS;
                }
            }
            
            return EQUALS;
        }
        else
        {
            return NOT_EQUALS;
        }
    }
}
