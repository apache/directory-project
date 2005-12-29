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
package org.apache.ldap.common.util;


import org.apache.ldap.common.message.LockableAttributeImpl;

import javax.naming.directory.Attribute;
import javax.naming.NamingException;


/**
 * A set of utility fuctions for working with Attributes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AttributeUtils
{
    /**
     * Creates a new attribute which contains the values representing the difference
     * of two attributes.  If both attributes are null then we cannot determine the
     * attribute ID and an {@link IllegalArgumentException} is raised.  Note that the
     * order of arguments makes a difference.
     *
     * @param attr0 the first attribute
     * @param attr1 the second attribute
     * @return a new attribute with the difference of values from both attribute arguments
     * @throws NamingException if there are problems accessing attribute values
     */
    public static Attribute getDifference( Attribute attr0, Attribute attr1 ) throws NamingException
    {
        String id;

        if ( attr0 == null && attr1 == null )
        {
            throw new IllegalArgumentException( "Cannot figure out attribute ID if both args are null" );
        }
        else if ( attr0 == null )
        {
            return new LockableAttributeImpl( attr1.getID() );
        }
        else if ( attr1 == null )
        {
            return ( Attribute ) attr0.clone();
        }
        else if ( ! ( ( String ) attr0.getID() ).equalsIgnoreCase( attr1.getID() ) )
        {
            throw new IllegalArgumentException( "Cannot take difference of attributes with different IDs!" );
        }
        else
        {
            id = attr0.getID();
        }

        Attribute attr = new LockableAttributeImpl( id );

        if ( attr0 != null )
        {
            for ( int ii = 0; ii < attr0.size(); ii++ )
            {
                attr.add( attr0.get( ii ) );
            }
        }

        if ( attr1 != null )
        {
            for ( int ii = 0; ii < attr1.size(); ii++ )
            {
                attr.remove( attr1.get( ii ) );
            }
        }

        return attr;
    }


    /**
     * Creates a new attribute which contains the values representing the union of
     * two attributes.  If one attribute is null then the resultant attribute
     * returned is a copy of the non-null attribute.  If both are null then we cannot
     * determine the attribute ID and an {@link IllegalArgumentException} is raised.
     *
     * @param attr0 the first attribute
     * @param attr1 the second attribute
     * @return a new attribute with the union of values from both attribute arguments
     * @throws NamingException if there are problems accessing attribute values
     */
    public static Attribute getUnion( Attribute attr0, Attribute attr1 ) throws NamingException
    {
        String id;

        if ( attr0 == null && attr1 == null )
        {
            throw new IllegalArgumentException( "Cannot figure out attribute ID if both args are null" );
        }
        else if ( attr0 == null )
        {
            id = attr1.getID();
        }
        else if ( attr1 == null )
        {
            id = attr0.getID();
        }
        else if ( ! ( ( String ) attr0.getID() ).equalsIgnoreCase( attr1.getID() ) )
        {
            throw new IllegalArgumentException( "Cannot take union of attributes with different IDs!" );
        }
        else
        {
            id = attr0.getID();
        }

        Attribute attr = new LockableAttributeImpl( id );

        if ( attr0 != null )
        {
            for ( int ii = 0; ii < attr0.size(); ii++ )
            {
                attr.add( attr0.get( ii ) );
            }
        }

        if ( attr1 != null )
        {
            for ( int ii = 0; ii < attr1.size(); ii++ )
            {
                attr.add( attr1.get( ii ) );
            }
        }

        return attr;
    }
}
