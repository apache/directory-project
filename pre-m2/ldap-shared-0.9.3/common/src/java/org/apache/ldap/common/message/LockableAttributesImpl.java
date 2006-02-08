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
package org.apache.ldap.common.message;


import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.ldap.common.util.ExceptionUtils;

import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.AbstractLockable;


/**
 * A case-insensitive Lockable JNDI Attributes implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class LockableAttributesImpl
    extends AbstractLockable implements LockableAttributes
{
    static final long serialVersionUID = -69864533495992471L;
    /** Map of user provided String ids to Attributes */
    private final Map map = new HashMap();
    /** Cache of lowercase id Strings to mixed cased user provided String ids */
    private Map keyMap;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates a LockableAttributes without a parent Lockable.
     */
    public LockableAttributesImpl()
    {
        super( false );
        keyMap = new HashMap();
    }


    /**
     * Creates a LockableAttributes with a parent Lockable.
     *
     * @param parent the parent lockable to use for overriding lock state.
     */
    public LockableAttributesImpl( Lockable parent )
    {
        super( parent, false );
        keyMap = new HashMap();
    }


    /**
     * Used by clone to create a LockableAttributes.
     *
     * @param parent the parent Lockable
     * @param map the primary user provided id to Attribute Map
     * @param keyMap the canonical key to user provided id Map
     */
    private LockableAttributesImpl( Lockable parent, Map map, Map keyMap )
    {
        super( parent, false );

        this.keyMap = new HashMap();

        if ( keyMap != null )
        {
            this.keyMap.putAll( keyMap );
        }

        Iterator list = map.values().iterator();
        while ( list.hasNext() )
        {
            Attribute attr = ( Attribute ) list.next();
            this.map.put( attr.getID(), attr.clone() );
        }
    }


    // ------------------------------------------------------------------------
    // javax.naming.directory.Attributes Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Determines whether the attribute set ignores the case of
     * attribute identifiers when retrieving or adding attributes.
     *
     * @return true always.
     */
    public boolean isCaseIgnored()
    {
        return true;
    }


    /**
     * Retrieves the number of attributes in the attribute set.
     *
     * @return The nonnegative number of attributes in this attribute set.
     */
    public int size()
    {
        return map.size();
    }


    /**
     * Retrieves the attribute with the given attribute id from the
     * attribute set.
     *
     * @param attrId The non-null id of the attribute to retrieve.
     *        If this attribute set ignores the character
     *        case of its attribute ids, the case of attrID
     *        is ignored.
     * @return The attribute identified by attrID; null if not found.
     * @see #put
     * @see #remove
     */
    public Attribute get( String attrId )
    {
        String l_key = getUserProvidedId( attrId );

        if ( l_key == null )
        {
            return null;
        }

        return ( Attribute ) map.get( l_key );
    }


    /**
     * Retrieves an enumeration of the attributes in the attribute set.
     * The effects of updates to this attribute set on this enumeration
     * are undefined.
     *
     * @return A non-null enumeration of the attributes in this attribute set.
     *         Each element of the enumeration is of class <tt>Attribute</tt>.
     *         If attribute set has zero attributes, an empty enumeration
     *         is returned.
     */
    public NamingEnumeration getAll()
    {
        return new IteratorNamingEnumeration( map.values().iterator() );
    }


    /**
     * Retrieves an enumeration of the ids of the attributes in the
     * attribute set.
     * The effects of updates to this attribute set on this enumeration
     * are undefined.
     *
     * @return A non-null enumeration of the attributes' ids in
     *         this attribute set. Each element of the enumeration is
     *         of class String. If attribute set has zero attributes, an empty 
     *         enumeration is returned.
     */
    public NamingEnumeration getIDs()
    {
        return new ArrayNamingEnumeration( map.keySet().toArray() );
    }


    /**
     * Adds a new attribute to the attribute set.
     *
     * @param attrId non-null The id of the attribute to add.
     *        If the attribute set ignores the character
     *        case of its attribute ids, the case of attrID
     *        is ignored.
     * @param val The possibly null value of the attribute to add.
     *        If null, the attribute does not have any values.
     * @return The Attribute with attrID that was previous in this attribute set
     *         null if no such attribute existed.
     * @see #remove
     */
    public Attribute put( String attrId, Object val )
    {
        super.lockCheck( "Attempt to add value to locked Attributes" );

        if ( get( attrId ) == null )
        {
            setUserProvidedId( attrId );
        }

        Attribute attr = new LockableAttributeImpl( this, attrId );
        attr.add( val );
        map.put( attrId, attr );
        return attr;
    }


    /**
     * Adds a new attribute to the attribute set.
     *
     * @param attr The non-null attribute to add.  If the attribute set
     *        ignores the character case of its attribute ids, the case of
     *        attr's identifier is ignored.
     * @return The Attribute with the same ID as attr that was previous in this 
     *         attribute set; null if no such attribute existed.
     * @see #remove
     */
    public Attribute put( Attribute attr )
    {
        super.lockCheck( "Attempt to Attribute to locked Attributes" );
        Attribute l_old = get( attr.getID() );

        if ( l_old != null )
        {
            map.remove( l_old.getID() );

            if ( keyMap != null )
            {
                keyMap.remove( l_old.getID().toLowerCase() );
            }
        }

        map.put( attr.getID(), attr );
        setUserProvidedId( attr.getID() );
        return l_old;
    }


    /**
      * Removes the attribute with the attribute id 'attrID' from
      * the attribute set. If the attribute does not exist, ignore.
      *
      * @param attrId The non-null id of the attribute to remove. If the
      * attribute set ignores the character case of its attribute ids, the case 
      * of attrID is ignored.
      * @return The Attribute with the same ID as attrID that was previous in 
      * the attribute set; null if no such attribute existed.
      */
    public Attribute remove( String attrId )
    {
        super.lockCheck(
            "Attempt to remove Attribute from locked Attributes" );
        Attribute l_old = get( attrId );

        if ( l_old != null )
        {
            map.remove( l_old.getID() );

            if ( keyMap != null )
            {
                keyMap.remove( l_old.getID().toLowerCase() );
            }
        }

        return l_old;
    }


    /**
      * Makes a shallow copy of the attribute set.  The new set contains the
      * same attributes as the original set.
      *
      * @return A non-null copy of this attribute set.
      */
    public Object clone()
    {
        return new LockableAttributesImpl( getParent(), map, keyMap );
    }
    
    
    /**
     * Prints out the attributes as an LDIF.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer l_buf = new StringBuffer();
        
        Iterator l_attrs = map.values().iterator();
        while ( l_attrs.hasNext() )
        {
            Attribute l_attr = ( Attribute ) l_attrs.next();
            
            try 
            {
                NamingEnumeration l_values = l_attr.getAll();
                while ( l_values.hasMore() )
                {
                    Object l_value = l_values.next();
                    l_buf.append( l_attr.getID() );
                    l_buf.append( ": " );
                    l_buf.append( l_value );
                    l_buf.append( '\n' );
                }
            } 
            catch ( NamingException e )
            {
                l_buf.append( ExceptionUtils.getFullStackTrace( e ) );
            }
        }
        
        return l_buf.toString();
    }


    /**
     * Checks to see if this Attributes implemenation is equivalent to another.
     * The comparision does not take into account the implementation or any
     * Lockable interface properties.  Case independent lookups by Attribute ID
     * is considered to be significant.
     *
     * @param obj the Attributes object to test for equality to this
     * @return true if the Attributes are equal false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( ! ( obj instanceof Attributes ) )
        {
            return false;
        }

        Attributes attrs = ( Attributes ) obj;

        if ( attrs.size() != size() )
        {
            return false;
        }

        if ( attrs.isCaseIgnored() != isCaseIgnored() )
        {
            return false;
        }

        NamingEnumeration list = attrs.getAll();
        while ( list.hasMoreElements() )
        {
            Attribute attr = ( Attribute ) list.nextElement();
            Attribute myAttr = get( attr.getID() );

            if ( myAttr == null )
            {
                return false;
            }

            if ( ! myAttr.equals( attr ) )
            {
                return false;
            }
        }

        return true;
    }


    // ------------------------------------------------------------------------
    // Utility Methods
    // ------------------------------------------------------------------------


    /**
     * Sets the user provided key by normalizing it and adding a record into the
     * keymap for future lookups.
     *
     * @param userProvidedId the id of the Attribute gotten from the attribute
     * instance via getID().
     */
    private void setUserProvidedId( String userProvidedId )
    {
        if ( keyMap == null )
        {
            keyMap = new HashMap();
            keyMap.put( userProvidedId.toLowerCase(), userProvidedId );
            return;
        }

        if ( keyMap.get( userProvidedId ) == null )
        {
            keyMap.put( userProvidedId.toLowerCase(), userProvidedId );
        }
    }


    /**
     * Gets the user provided key by looking it up using the normalized key in
     * the key map.
     *
     * @param attrId the id of the Attribute in any case.
     * @return the attribute id as it would be returned on a call to the
     * Attribute's getID() method.
     */
    private String getUserProvidedId( String attrId )
    {
        // First check if it is correct form to save string creation below.
        if ( map.containsKey( attrId ) )
        {
            return attrId;
        }

        if ( keyMap == null )
        {
            keyMap = new HashMap();
            return null;
        }

        return ( String ) keyMap.get( attrId.toLowerCase() );
    }
}
