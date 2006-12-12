/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.triplesec.guardian;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * Represnets an immutable set of {@link Permission}s.
 *
 * @author Trustin Lee
 * @version $Rev: 52 $, $Date: 2005-08-19 23:03:36 -0400 (Fri, 19 Aug 2005) $
 */
public class Permissions implements Cloneable, Serializable
{
    private static final long serialVersionUID = 824005229641450076L;
    /** An empty array of {@link Permission}s which is used when <tt>null</tt> is specified */
    private static final Permission[] EMPTY_PERMISSION_ARRAY = new Permission[0];

    /** the name of application this permissions belong to */
    private final String applicationName;
    /** <tt>Map&lt;String permissionName, Permission permission&gt;</tt> */
    private final Map permissions = new HashMap();


    /**
     * Creates a new instance.
     * 
     * @param applicationName The name of the application this permissions belong to
     * @param permissions The array of {@link Permission}s that will belong to this permission set
     */
    public Permissions( String applicationName, Permission[] permissions )
    {
        // Check nulls and emptiness
        if( applicationName == null )
        {
            throw new NullPointerException( "applicationName" );
        }
        if( applicationName.length() == 0 )
        {
            throw new IllegalArgumentException( "applicationName is empty." );
        }
        if( permissions == null )
        {
            permissions = EMPTY_PERMISSION_ARRAY;
        }
        
        this.applicationName = applicationName;

        // Add all permissions while checking if application names are all
        // same with what user specified.
        for( int i = permissions.length - 1; i >= 0; i -- )
        {
            Permission p = permissions[ i ];
            if( p == null )
            {
                continue;
            }
            
            if( !applicationName.equals( p.getApplicationName() ) )
            {
                throw new IllegalArgumentException( "Invalid applicationName: " + p.getApplicationName() );
            }
            
            this.permissions.put( p.getName(), p );
        }
    }


    /**
     * Returns the name of the application this permissions belong to
     * 
     * @return the name of the application this permissions belong to
     */
    public String getApplicationName()
    {
        return applicationName;
    }


    /**
     * Returns <tt>true</tt> if and only if this set contains the specified
     * <tt>permission</tt>.
     *
     * @param permission the permission to find
     * @return <tt>true</tt> if and only if this set contains the specified
     *         <tt>permission</tt>
     */
    public boolean contains( Permission permission )
    {
        return applicationName.equals( permission.getApplicationName() ) &&
               permissions.containsKey( permission.getName() );
    }


    /**
     * Returns <tt>true</tt> if and only if this set contains the {@link Permission}
     * with the specified <tt>permissionName</tt>.
     *
     * @param permissionName the name of the permission to find
     * @return <tt>true</tt> if and only if this set contains the specified
     *         <tt>permissionName</tt>
     */
    public boolean contains( String permissionName )
    {
        return permissions.containsKey( permissionName );
    }


    /**
     * Returns <tt>true</tt> if and only if this set contains all elements of
     * the specified <tt>permissions</tt>.
     *
     * @param permissions another set of permissions
     * @return <tt>true</tt> if and only if this set contains all elements of
     *         the specified <tt>permissions</tt>
     */
    public boolean containsAll( Permissions permissions )
    {
        checkApplicationName( permissions );
        return this.permissions.keySet().containsAll( permissions.permissions.keySet() );
    }


    /**
     * Returns the {@link Permission} with the specified <tt>permissionName</tt>.
     *
     * @param permissionName the name of the permission to find
     * @return <tt>null</tt> if there's no permission with the specified name
     */
    public Permission get( String permissionName )
    {
        return ( Permission ) permissions.get( permissionName );
    }


    /**
     * Returns <tt>true</tt> if this set is empty.
     * 
     * @return <tt>true</tt> if this set is empty
     */
    public boolean isEmpty()
    {
        return permissions.isEmpty();
    }


    /**
     * Returns the number of elements this set contains.
     * 
     * @return the number of elements this set contains
     */
    public int size()
    {
        return permissions.size();
    }


    /**
     * Returns an {@link Iterator} that iterates all {@link Permission}s this set contains.
     * 
     * @return an {@link Iterator} that iterates all {@link Permission}s this set contains
     */
    public Iterator iterator()
    {
        return Collections.unmodifiableCollection( permissions.values() ).iterator();
    }


    /**
     * Creates a new set of {@link Permission}s which contains all elements of
     * both this set and the specified set (OR operation).  This operation never
     * modifies this set.
     * 
     * @param permissions a set of permissions to add
     * @return a new set
     */
    public Permissions addAll( Permissions permissions )
    {
        checkApplicationName( permissions );
        Permissions newPermissions = ( Permissions ) clone();
        newPermissions.permissions.putAll( permissions.permissions );
        return newPermissions;
    }


    /**
     * Creates a new set of {@link Permission}s which contains elements of
     * this set excluding what exists in the specified set (NAND operation).
     * This operation never modifies this set.
     * 
     * @param permissions a set of permissions to remove
     * @return a new set
     */
    public Permissions removeAll( Permissions permissions )
    {
        checkApplicationName( permissions );
        Permissions newPermissions = ( Permissions ) clone();
        newPermissions.permissions.keySet().removeAll(
                permissions.permissions.keySet() );
        return newPermissions;
    }


    /**
     * Creates a new set of {@link Permission}s which contains elements which
     * exists in both this set and the specified set (AND operation).  This
     * operation never modifies this set.
     * 
     * @param permissions a set of permissions to retain.
     * @return a new set
     */
    public Permissions retainAll( Permissions permissions )
    {
        checkApplicationName( permissions );
        Permissions newPermissions = ( Permissions ) clone();
        newPermissions.permissions.keySet().retainAll(
                permissions.permissions.keySet() );
        return newPermissions;
    }


    // ------------------------------------------------------------------------
    // Object Overrides
    // ------------------------------------------------------------------------


    public Object clone()
    {
        Permission[] permissionArray = new Permission[ size() ];
        permissionArray = ( Permission[] ) permissions.values().toArray( permissionArray );
        return new Permissions( applicationName, permissionArray );
    }


    public int hashCode()
    {
        return applicationName.hashCode() ^ permissions.hashCode();
    }
    

    public boolean equals( Object that )
    {
        if( this == that )
        {
            return true;
        }
        
        if( that instanceof Permissions )
        {
            Permissions thatP = ( Permissions ) that;
            // We don't compare application name because permissions already
            // contain it.
            return this.permissions.equals( thatP.permissions );
        }
        
        return false;
    }


    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "Permissions(" );
        buf.append( applicationName );
        buf.append( ": " );

        // Sort permissions by name
        Set sortedPermissions = new TreeSet( permissions.values() );
        Iterator i = sortedPermissions.iterator();
        
        // Add the first one
        if( i.hasNext() )
        {
            Permission p = ( Permission ) i.next();
            buf.append( p.getName() );
            
            // Add others
            while( i.hasNext() )
            {
                p = ( Permission ) i.next();
                buf.append( ", " );
                buf.append( p.getName() );
            }
        }
        else
        {
            buf.append( "empty" );
        }
        
        buf.append( ')' );
        
        return buf.toString();
    }


    // ------------------------------------------------------------------------
    // Private Methods
    // ------------------------------------------------------------------------


    /**
     * Checks if the application name of the specified <tt>permissions</tt>
     * equals to that of this set.
     *  
     * @param permissions the permissions to check the application name
     * @throws IllegalArgumentException if mismatches
     */
    private void checkApplicationName( Permissions permissions )
    {
        if( !applicationName.equals( permissions.getApplicationName() ) )
        {
            throw new IllegalArgumentException( "Wrong application name: " + permissions.getApplicationName() );
        }
    }
}
