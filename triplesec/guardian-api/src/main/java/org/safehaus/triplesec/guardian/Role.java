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
import java.security.AccessControlException;


/**
 * An application role.  Roles are application specific and contain a set
 * of permission grants.  Users assigned to these Roles inherit the set of 
 * permission grants from their roles.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @author Trustin Lee
 * @version $Rev: 74 $, $Date: 2005-11-11 02:03:22 -0500 (Fri, 11 Nov 2005) $
 */
public class Role implements Comparable, Cloneable, Serializable 
{
    private static final long serialVersionUID = 6190625586883412135L;

    /** an empty byte array used as a placeholder for empty grants */
    private static final Permission[] EMPTY_PERMISSION_ARRAY = new Permission[0];
    
    /** the name of this Role */
    private final String name;
    /** the store the Role is defined for */
    private final ApplicationPolicy store;
    /** the permissions granted for this role */
    private final Permissions permissions;
    /** a brief description of the Role */
    private final String description;


    /**
     * Creates a new Role instance with a description.
     * 
     * @param store the parent store this role is defined for
     * @param name the name of this role
     * @param permissions a set of permissions granted for this role
     * @param description a breif description of the role
     */
    public Role( ApplicationPolicy store, String name, Permissions permissions, String description )
    {
        if( store == null )
        {
            throw new NullPointerException( "store" );
        }
        if( name == null )
        {
            throw new NullPointerException( "name" );
        }
        if( name.length() == 0 )
        {
            throw new IllegalArgumentException( "name is empty." );
        }
        
        if( permissions == null )
        {
            permissions = new Permissions(
                    store.getApplicationName(), EMPTY_PERMISSION_ARRAY );
        }
        if( !store.getApplicationName().equals( permissions.getApplicationName() ) )
        {
            throw new IllegalArgumentException(
                    "Invalid applicationName in permissions: " +
                    permissions.getApplicationName() );
        }
        
        if( !store.getPermissions().containsAll( permissions ) )
        {
            throw new IllegalArgumentException(
                    "store doesn't provide all permissions specified: " +
                    permissions );
        }
        
        this.store = store;
        this.name = name;
        this.permissions = permissions;
        this.description = description;
    }


    /**
     * Creates a new Role instance.
     *
     * @param store the parent store this role is defined for
     * @param name the name of this role
     * @param permissions a set of permissions granted for this role
     */
    public Role( ApplicationPolicy store, String name, Permissions permissions )
    {
        this ( store, name, permissions, null );
    }


    /**
     * Gets the name of this Role.
     * 
     * @return the name of this Role
     */
    public String getName()
    {
        return name;
    }


    /**
     * Gets a brief description for this Role if one exists.
     *
     * @return a description for this Role
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Gets the application name this Role is defined for.
     *  
     * @return the name of the application this Role is defined for.
     */
    public String getApplicationName()
    {
        return store.getApplicationName();
    }


    /**
     * Gets a set of permissions granted to this role.
     * 
     * @return a set of permissions granted to this role.
     */
    public Permissions getGrants()
    {
        return permissions;
    }


    /**
     * Assertive permission check to test if this role has the effective
     * permission.
     *
     * @param permission the permission to check for
     * @throws AccessControlException if the permission is not granted
     */
    public void checkPermission( Permission permission )
    {
        checkPermission(
                permission,
                "Role '" + name + "' " +
                "in application '" + getApplicationName() + '\'' +
                "does not posess the permission '" + permission.getName() + "'." );
    }


    /**
     * Get's whether or not this Role has the permission.
     *
     * @param permissionName the permission to check for
     * @return true if the permission is granted,false otherwise
     */
    public boolean hasPermission( String permissionName )
    {
        return permissions.get( permissionName ) != null;
    }


    /**
     * Get's whether or not this Role has the permission.
     *
     * @param permission the name of permission to check for
     * @return true if the permission is granted,false otherwise
     */
    public boolean hasPermission( Permission permission )
    {
        return permissions.contains( permission );
    }


    /**
     * Assertive permission check to test if this role has the effective 
     * permission.
     * 
     * @param permissionName the name of the permission to check for
     * @throws AccessControlException if the permission is not granted
     */
    public void checkPermission( String permissionName )
    {
        checkPermission(
                permissionName,
                "Role '" + name + "' " +
                "in application '" + getApplicationName() + '\'' +
                "does not posess the permission '" + permissionName + "'." );
    }


    /**
     * Assertive permission check to test if this Role has the effective 
     * permission.
     * 
     * @param permission the permission to check for
     * @param message to use for AccessControlException if it is thrown
     * @throws AccessControlException if the permission is not granted
     */
    public void checkPermission( Permission permission, String message )
    {
        if ( permission == null )
        {
            throw new NullPointerException( "permission" );    
        }
        
        if ( !permissions.contains( permission ) )
        {
            throw new AccessControlException( message );
        }
    }


    /**
     * Assertive permission check to test if this role has the effective 
     * permission.
     * 
     * @param permissionName the permission name to check for
     * @param message to use for AccessControlException if it is thrown
     * @throws AccessControlException if the permission is not granted
     */
    public void checkPermission( String permissionName, String message )
    {
        if ( permissionName == null )
        {
            throw new NullPointerException( "permissionName" );    
        }
        
        if ( !permissions.contains( permissionName ) )
        {
            throw new AccessControlException( message );
        }
    }


    // ------------------------------------------------------------------------
    // Object Overrides
    // ------------------------------------------------------------------------


    public int hashCode()
    {
        return getApplicationName().hashCode() ^ name.hashCode(); 
    }


    public boolean equals( Object that )
    {
        if( this == that )
        {
            return true;
        }
        
        if( that instanceof Role )
        {
            Role thatR = ( Role ) that;
            return this.getApplicationName().equals( thatR.getApplicationName() ) &&
                   this.getName().equals( thatR.getName() );
        }
        
        return false;
    }


    public int compareTo( Object that )
    {
        Role thatR = ( Role ) that;
        int ret = this.getApplicationName().compareTo( thatR.getApplicationName() );
        if( ret != 0 )
        {
            return ret;
        }
        else
        {
            return this.getName().compareTo( thatR.getName() );
        }
    }


    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            throw new InternalError();
        }
    }


    public String toString()
    {
        return "Role(" + getName() + ": " + permissions + ')';
    }
}
