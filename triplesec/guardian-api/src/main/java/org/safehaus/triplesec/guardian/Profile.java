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
import java.util.Iterator;


/**
 * <p>
 * A user's application authorization profile.  Authorization policy is used
 * to manage access controls for user profiles associated with applications.
 * Profiles associate users with applications.  This class models that profile
 * by linking the user with an application and allowing the assignment of an
 * application specific {@link Role} set and {@link Permission} set to the 
 * profile.
 * </p>  
 * <p>
 * Profiles contain three sets of Permissions and a set of Roles used for 
 * managing an authorization policy of a user.  A Role Based Access Control 
 * (RBAC) model is used to easily manage the Profile.  The three Permission
 * sets are: grants, denials and the effective calculated permissions for the 
 * profile.  Roles assigned to the Profile lead to the inheritance of Permission
 * granted to Role.  Besides Role based Permission inheritence, additional
 * Permission may be granted or denied to influence the total effective Permission.  
 * The grants Permissions set contains extra granted Permissions which may not be 
 * inherited by assigned Roles.  The denials Permissions set contains
 * {@link Permissions} that are denied whether they are inherited by assigned
 * {@link Role}s or granted through the grants Permissions set.  Denials
 * take precedence.  For more information take a look at the documentation here:
 * </p>
 * <ul>
 *   <li><a href="http://guardian.safehaus.org/User%27s+Guide">Guardian User's Guide</a></li>
 * </ul>
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @author Trustin Lee
 * @version $Rev: 74 $, $Date: 2005-11-11 02:03:22 -0500 (Fri, 11 Nov 2005) $
 */
public class Profile implements Comparable, Cloneable, Serializable
{
    /** */
    private static final long serialVersionUID = 1762844758784443519L;

    /** the store this Profile is for */
    private final ApplicationPolicy store;
    /** the name of the User this Profile is for */
    private final String userName;
    /** the id of this Profile is for */
    private final String profileId;
    /** the roles assigned to this Profile */
    private final Roles roles;
    /** the permissions granted to this Profile */
    private final Permissions grants;
    /** the permissions denied by this Profile */
    private final Permissions denials;
    /** the effective calculated permissions for this Profile */
    private final Permissions effectivePermissions;
    /** a brief description of the Profile */
    private final String description;
    /** whether or not this profile is disabled */
    private final boolean disabled;


    /**
     * Creates a default User Profile for an ApplicationPolicyStore.
     *
     * @param profileId the id of this Profile
     * @param userName the name of the User this Profile is for
     * @param store the store this Profile is for
     * @param roles the roles assigned to this Profile
     * @param grants the permissions granted to this Profile
     * @param denials the permissions denied by this Profile
     * @param disabled true if this Profile is disabled otherwise false
     */
    public Profile(
            ApplicationPolicy store,
            String profileId, String userName, Roles roles,
            Permissions grants, Permissions denials, boolean disabled )
    {
        this ( store, profileId, userName, roles, grants, denials, null, disabled );
    }


    /**
     * Creates a default User Profile for an ApplicationPolicyStore.
     * 
     * @param profileId the name of the User this Profile is for
     * @param store the store this Profile is for
     * @param roles the roles assigned to this Profile
     * @param grants the permissions granted to this Profile
     * @param denials the permissions denied by this Profile
     * @param description a brief description for this Profile
     * @param disabled true if this Profile is disabled otherwise false
     */
    public Profile(
            ApplicationPolicy store,
            String profileId, String userName, Roles roles,
            Permissions grants, Permissions denials, String description, boolean disabled )
    {
        if( store == null )
        {
            throw new NullPointerException( "store" );
        }
        if( profileId == null )
        {
            throw new NullPointerException( "profileId" );
        }
        if( userName == null )
        {
            throw new NullPointerException( "userName" );
        }
        if( profileId.length() == 0 )
        {
            throw new IllegalArgumentException( "profileId is empty." );
        }
        if( roles == null )
        {
            roles = new Roles( store.getApplicationName(), null );
        }
        if( !store.getApplicationName().equals( roles.getApplicationName() ) )
        {
            throw new IllegalArgumentException( "Invalid applicationName in roles: " + roles.getApplicationName() );
        }
        if( grants == null )
        {
            grants = new Permissions( store.getApplicationName(), null );
        }
        if( !store.getApplicationName().equals( grants.getApplicationName() ) )
        {
            throw new IllegalArgumentException( "Invalid applicationName in grants: " + grants.getApplicationName() );
        }
        if( !store.getPermissions().containsAll( grants ) )
        {
            throw new IllegalArgumentException(
                    "store doesn't provide all permissions specified: " +
                    grants );
        }
        if( denials == null )
        {
            denials = new Permissions( store.getApplicationName(), null );
        }
        if( !store.getApplicationName().equals( denials.getApplicationName() ) )
        {
            throw new IllegalArgumentException( "Invalid applicationName in denials: " + denials.getApplicationName() );
        }
        if( !store.getPermissions().containsAll( denials ) )
        {
            throw new IllegalArgumentException(
                    "store doesn't provide all permissions specified: " +
                    denials );
        }
        
        this.disabled = disabled;
        this.store = store;
        this.profileId = profileId;
        this.userName = userName;
        this.roles = roles;
        this.grants = grants;
        this.denials = denials;
        this.description = description;

        // Calculate effective permissions
        Permissions effectivePermissions = new Permissions( store.getApplicationName(), null );
        for( Iterator i = roles.iterator(); i.hasNext(); )
        {
            Role r = ( Role ) i.next();
            effectivePermissions = effectivePermissions.addAll( r.getGrants() );
        }
        effectivePermissions = effectivePermissions.addAll( grants );
        this.effectivePermissions = effectivePermissions.removeAll( denials );
    }

    
    /**
     * Checks whether or not this Profile has been disabled.
     * 
     * @return true if this Profile is disabled, false if enabled
     */
    public boolean isDisabled()
    {
        return disabled;
    }
    

    /**
     * Gets the id of the this Profile.
     * 
     * @return the id of this Profile
     */
    public String getProfileId()
    {
        return profileId;
    }


    /**
     * Gets the name of the user who owns this Profile.
     * 
     * @return the name of the user associated with this Profile
     */
    public String getUserName()
    {
        return userName;
    }


    /**
     * Gets a brief description for this Profile if one exists.
     *
     * @return a description for this Profile
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Gets the name of the application this Profile is associated with.
     * 
     * @return the name of the application this Profile is associated with
     */
    public String getApplicationName()
    {
        return store.getApplicationName();
    }


    /**
     * Gets a set of {@link Role}s which are assigned to this Profile.
     * 
     * @return a container of {@link Role} objects which are assigned to this Profile
     */
    public Roles getRoles()
    {
        return roles;
    }


    /**
     * Checks to see if the user according to this Profile is in a Role.  
     *
     * @param roleName the name of the Role to check for
     * @return true if the user is in the Role, false otherwise
     */
    public boolean isInRole( String roleName )
    {
        return roles.contains( roleName );
    }


    /**
     * Gets the set of {@link Permission}s granted to this Profile.
     * 
     * @return a container of granted {@link Permission} objects
     */
    public Permissions getGrants()
    {
        return grants;
    }

    /**
     * Gets a set of permissions explicitly denied by this profile.
     * This is the only time and place where negative permissions will ever be
     * found.
     * 
     * @return a container of denied {@link Permission} objects
     */
    public Permissions getDenials()
    {
        return denials;
    }


    /**
     * Gets the set of effective (net calculated) permissions for this Profile.
     * An effective permission is calculated from the assigned {@link Role}s,
     * granted {@link Permissions} and denied {@link Permissions} of this
     * Profile.
     * 
     * @return a container of effective {@link Permission} objects for this profile.
     */
    public Permissions getEffectivePermissions()
    {
        return effectivePermissions;
    }


    /**
     * Assertive check to test if this Profile has the effective {@link Permission}.
     * 
     * @param permissionName the permission name to check for
     * @throws AccessControlException if the permission is not granted or
     *      inherited from an assigned Role
     */
    public void checkPermission( String permissionName )
    {
        checkPermission(
                permissionName,
                "User '" + profileId + "' " +
                "in application '" + getApplicationName() + '\'' +
                "does not posess the permission '" + permissionName + "'." );
    }


    /**
     * Get's whether or not this Profile has the permission.
     *
     * @param permission the permission to check for
     * @return true if the permission is granted, false otherwise
     */
    public boolean hasPermission( Permission permission )
    {
        return effectivePermissions.contains( permission );
    }


    /**
     * Get's whether or not this Profile has the permission.
     *
     * @param permissionName the permission to check for
     * @return true if the permission is granted, false otherwise
     */
    public boolean hasPermission( String permissionName )
    {
        return effectivePermissions.get( permissionName ) != null;
    }


    /**
     * Assertive permission check to test if this Profile has the effective 
     * permission.
     * 
     * @param permission the permission to check for
     * @throws AccessControlException if the permission is not granted or
     *      inherited from an assigned Role
     */
    public void checkPermission( Permission permission )
    {
        checkPermission(
                permission,
                "User '" + profileId + "' " +
                "in application '" + getApplicationName() + '\'' +
                "does not posess the permission '" + permission.getName() + "'." );
    }


    /**
     * Assertive permission check to test if this Profile has the effective 
     * permission.
     * 
     * @param permissionName the permission name to check for
     * @param message to use for AccessControlException if it is thrown
     * @throws AccessControlException if the permission is not granted or
     *      inherited from an assigned Role
     */
    public void checkPermission( String permissionName, String message )
    {
        if ( permissionName == null )
        {
            throw new NullPointerException( "permissionName" );    
        }
        
        if ( !effectivePermissions.contains( permissionName ) )
        {
            throw new AccessControlException( message );
        }
    }


    /**
     * Assertive permission check to test if this Profile has the effective 
     * permission.
     * 
     * @param permission the permission to check for
     * @param message to use for AccessControlException if it is thrown
     * @throws AccessControlException if the permission is not granted or
     *      inherited from an assigned Role
     */
    public void checkPermission( Permission permission, String message )
    {
        if ( permission == null )
        {
            throw new NullPointerException( "permission" );    
        }
        
        if ( !effectivePermissions.contains( permission ) )
        {
            throw new AccessControlException( message );
        }
    }


    // ------------------------------------------------------------------------
    // Object Overrides
    // ------------------------------------------------------------------------


    public int hashCode()
    {
        return getApplicationName().hashCode() ^ profileId.hashCode();
    }


    public boolean equals( Object that )
    {
        if( this == that )
        {
            return true;
        }
        
        if( that instanceof Profile )
        {
            Profile thatP = ( Profile ) that;
            return this.getApplicationName().equals( thatP.getApplicationName() ) &&
                   this.getProfileId().equals( thatP.getProfileId() );
        }
        
        return false;
    }


    public int compareTo( Object that )
    {
        Profile thatP = ( Profile ) that;
        int ret = this.getApplicationName().compareTo( thatP.getApplicationName() );
        if( ret != 0 )
        {
            return ret;
        }
        
        return this.getProfileId().compareTo( thatP.getProfileId() );
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
        return "Profile(" + getProfileId() + ": " + effectivePermissions + ')';
    }
}
