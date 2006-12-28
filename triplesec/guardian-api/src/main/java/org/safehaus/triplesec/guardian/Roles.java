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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * Represnets an immutable set of {@link Role}s.
 *
 * @author Trustin Lee
 * @version $Rev: 52 $, $Date: 2005-08-19 23:03:36 -0400 (Fri, 19 Aug 2005) $
 */
public class Roles implements Cloneable, Serializable
{
    private static final long serialVersionUID = 654756629481872197L;
    /** An empty array of Role objects */
    private static final Role[] EMPTY_ROLE_ARRAY = new Role[0];

    /** the name of the application this roles belong to */
    private final String applicationName;
    /** <tt>Map&ltString roleName, Role role;&gt;</tt> */
    private final Map roles = new HashMap();


    /**
     * Creates a new instance.
     * 
     * @param applicationName the name of the application this roles belong to
     * @param roles the array of {@link Role}s that will belong to this role set
     */
    public Roles( String applicationName, Role[] roles )
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
        if( roles == null )
        {
            roles = EMPTY_ROLE_ARRAY;
        }
        
        this.applicationName = applicationName;

        // Add all roles while checking if application names are all
        // same with what user specified.
        for( int i = roles.length - 1; i >= 0; i -- )
        {
            Role r = roles[ i ];
            if( r == null )
            {
                continue;
            }
            
            if( !applicationName.equals( r.getApplicationName() ) )
            {
                throw new IllegalArgumentException( "Invalid applicationName: " + r.getApplicationName() );
            }
            
            this.roles.put( r.getName(), r );
        }
    }


    /**
     * Returns the name of the application this roles belong to
     * 
     * @return the name of the application this roles belong to
     */
    public String getApplicationName()
    {
        return applicationName;
    }


    /**
     * Returns <tt>true</tt> if and only if this set contains the specified
     * <tt>role</tt>.
     *
     * @param role the role to find
     * @return <tt>true</tt> if and only if this set contains the specified
     *         <tt>role</tt>
     */
    public boolean contains( Role role )
    {
        return applicationName.equals( role.getApplicationName() ) &&
               roles.containsKey( role.getName() );
    }


    /**
     * Returns <tt>true</tt> if and only if this set contains the {@link Role}
     * with the specified <tt>roleName</tt>.
     *
     * @param roleName the name of the role to find
     * @return <tt>true</tt> if and only if this set contains the specified
     *         <tt>roleName</tt>
     */
    public boolean contains( String roleName )
    {
        return roles.containsKey( roleName );
    }


    /**
     * Returns <tt>true</tt> if and only if this set contains all elements of
     * the specified <tt>roles</tt>.
     *
     * @param roles another set of roles
     * @return <tt>true</tt> if and only if this set contains all elements of
     *         the specified <tt>roles</tt>
     */
    public boolean containsAll( Roles roles )
    {
        checkApplicationName( roles );
        return this.roles.keySet().containsAll( roles.roles.keySet() );
    }


    /**
     * Returns the {@link Role} with the specified <tt>roleName</tt>.
     *
     * @param roleName the name of the role to find
     * @return <tt>null</tt> if there's no role with the specified name
     */
    public Role get( String roleName )
    {
        return ( Role ) roles.get( roleName );
    }


    /**
     * Returns <tt>true</tt> if this set is empty.
     * 
     * @return <tt>true</tt> if this set is empty
     */
    public boolean isEmpty()
    {
        return roles.isEmpty();
    }


    /**
     * Returns the number of elements this set contains.
     * 
     * @return the number of elements this set contains
     */
    public int size()
    {
        return roles.size();
    }


    /**
     * Returns an {@link Iterator} that iterates all {@link Role}s this set contains.
     * 
     * @return an {@link Iterator} that iterates all {@link Role}s this set contains
     */
    public Iterator iterator()
    {
        return Collections.unmodifiableCollection( roles.values() ).iterator();
    }


    /**
     * Creates a new set of {@link Role}s which contains all elements of
     * both this set and the specified set (OR operation).  This operation never
     * modifies this set.
     * 
     * @param roles a set of roles to add
     * @return a new set
     */
    public Roles addAll( Roles roles )
    {
        checkApplicationName( roles );
        Roles newRoles = ( Roles ) clone();
        newRoles.roles.putAll( roles.roles );
        return newRoles;
    }
    
    
    /**
     * Creates a new set of {@link Role}s which contains elements of
     * this set excluding what exists in the specified set (NAND operation).
     * This operation never modifies this set.
     * 
     * @param roles a set of roles to remove
     * @return a new set
     */
    public Roles removeAll( Roles roles )
    {
        checkApplicationName( roles );
        Roles newRoles = ( Roles ) clone();
        newRoles.roles.keySet().removeAll(
                roles.roles.keySet() );
        return newRoles;
    }


    /**
     * Creates a new set of {@link Role}s which contains elements which
     * exists in both this set and the specified set (AND operation).  This
     * operation never modifies this set.
     * 
     * @param roles a set of roles to retain.
     * @return a new set
     */
    public Roles retainAll( Roles roles )
    {
        checkApplicationName( roles );
        Roles newRoles = ( Roles ) clone();
        newRoles.roles.keySet().retainAll(
                roles.roles.keySet() );
        return newRoles;
    }

    
//    public Roles getDependentRoles( StringPermission perm )
//    {
//        if ( ! perm.getApplicationName().equals( getApplicationName() ) )
//        {
//            throw new IllegalArgumentException( "The permission '" + perm.getName() + "' is not " +
//                    "\nassociated with this application.  It is associated with " + perm.getApplicationName() );
//        }
//
//        List dependents = new ArrayList();
//        for ( Iterator ii = this.roles.values().iterator(); ii.hasNext(); /**/ )
//        {
//            Role role = ( Role ) ii.next();
//            if ( role.hasPermission( perm ) )
//            {
//                dependents.add( role );
//            }
//        }
//
//        if ( dependents.size() == 0 )
//        {
//            return new Roles( getApplicationName(), EMPTY_ROLE_ARRAY );
//        }
//
//        Role[] roleArray = new Role[dependents.size()];
//        dependents.toArray( roleArray );
//        return new Roles( getApplicationName(), roleArray );
//    }
    

    // ------------------------------------------------------------------------
    // Object Overrides
    // ------------------------------------------------------------------------


    public Object clone()
    {
        Role[] roleArray = new Role[ size() ];
        roleArray = ( Role[] ) roles.values().toArray( roleArray );
        return new Roles( applicationName, roleArray );
    }


    public int hashCode()
    {
        return applicationName.hashCode() ^ roles.hashCode();
    }


    public boolean equals( Object that )
    {
        if( this == that )
        {
            return true;
        }
        
        if( that instanceof Roles )
        {
            Roles thatP = ( Roles ) that;
            // We don't compare application name because roles already
            // contain it.
            return this.roles.equals( thatP.roles );
        }
        
        return false;
    }


    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "Roles(" );
        buf.append( applicationName );
        buf.append( ": " );

        // Sort roles by name
        Set sortedRoles = new TreeSet( roles.values() );
        Iterator i = sortedRoles.iterator();
        
        // Add the first one
        if( i.hasNext() )
        {
            Role r = ( Role ) i.next();
            buf.append( r.getName() );
            
            // Add others
            while( i.hasNext() )
            {
                r = ( Role ) i.next();
                buf.append( ", " );
                buf.append( r.getName() );
            }
        }
        else
        {
            buf.append( "empty" );
        }
        
        buf.append( ')' );
        
        return buf.toString();
    }


    private void checkApplicationName( Roles roles )
    {
        if( !applicationName.equals( roles.getApplicationName() ) )
        {
            throw new IllegalArgumentException( "Wrong application name: " + roles.getApplicationName() );
        }
    }
}
