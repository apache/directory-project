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
package org.safehaus.triplesec.guardian.mock;


import java.security.Permissions;
import java.security.Permission;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.GuardianException;
import org.safehaus.triplesec.guardian.PolicyChangeListener;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.guardian.Role;
import org.safehaus.triplesec.guardian.Roles;
import org.safehaus.triplesec.guardian.StringPermission;


/**
 * A mock implementation of an ApplicationPolicyStore for testing purposes.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 72 $
 */
class MockApplicationPolicy implements ApplicationPolicy
{
    private final Roles roles;
    private final Permissions perms;
    private final String name;
    private final Map profileByName;


    public MockApplicationPolicy()
    {
        name = "mockApplication";
        profileByName = new HashMap();
        Set<Permission> permSet = new HashSet<Permission>();
        Set roleSet = new HashSet();

        // --------------------------------------------------------------------------------
        // add permissions
        // --------------------------------------------------------------------------------

        StringPermission perm0 = new StringPermission("mockPerm0" ); permSet.add( perm0 );
        StringPermission perm1 = new StringPermission("mockPerm1" ); permSet.add( perm1 );
        StringPermission perm2 = new StringPermission("mockPerm2" ); permSet.add( perm2 );
        StringPermission perm3 = new StringPermission("mockPerm3" ); permSet.add( perm3 );
        StringPermission perm4 = new StringPermission("mockPerm4" ); permSet.add( perm4 );
        StringPermission perm5 = new StringPermission("mockPerm5" ); permSet.add( perm5 );
        StringPermission perm6 = new StringPermission("mockPerm6" ); permSet.add( perm6 );
        StringPermission perm7 = new StringPermission("mockPerm7" ); permSet.add( perm7 );
        StringPermission perm8 = new StringPermission("mockPerm8" ); permSet.add( perm8 );
        StringPermission perm9 = new StringPermission("mockPerm9" ); permSet.add( perm9 );

        perms = new Permissions();
         for (Permission perm: permSet) {
             perms.add(perm);
         }

        // --------------------------------------------------------------------------------
        // add roles
        // --------------------------------------------------------------------------------

        // role without any permissions toggled
        Permissions grants = new Permissions();
        Role role0 = new Role( this, "mockRole0", grants, null);
        roleSet.add( role0 );

        // role with permission mockPerm0
        grants = new Permissions();
        grants.add(perm0);
        Role role1 = new Role( this, "mockRole1", grants, null);
        roleSet.add( role1 );

        // role with permission mockPerm1
        grants = new Permissions();
        grants.add(perm1);
        Role role2 = new Role( this, "mockRole2", grants, null);
        roleSet.add( role2 );

        // role with permission mockPerm2 and mochPerm3
        grants = new Permissions();
        grants.add(perm2);
        grants.add(perm3);
        Role role3 = new Role( this, "mockRole3", grants, null);
        roleSet.add( role3 );

        // role with permission mockPerm4, mockPerm5, mockPerm6, mockPerm7, mockPerm9
        grants = new Permissions();
        grants.add(perm4);
        grants.add(perm5);
        grants.add(perm6);
        grants.add(perm7);
        grants.add(perm9);
        Role role4 = new Role( this, "mockRole4", grants, null);
        roleSet.add( role4 );

        // role with permission mockPerm4, mockPerm5, mockPerm6, mockPerm7, mockPerm9
        grants = new Permissions();
        grants.add(perm4);
        grants.add(perm5);
        grants.add(perm6);
        grants.add(perm7);
        grants.add(perm9);
        Permissions denials = new Permissions();
        denials.add(perm6);
        Role role5 = new Role( this, "mockRole5", grants, denials);
        roleSet.add( role5 );

        Role[] rolesArray = ( Role [] ) roleSet.toArray( new Role[0] );
        roles = new Roles( name, rolesArray );

        // --------------------------------------------------------------------------------
        // add profiles
        // --------------------------------------------------------------------------------

        // a profile that has no permissions at all, and no roles (basis case)
        grants = new Permissions();
        denials = new Permissions();
        Roles roles = new Roles( name, new Role[0] );
        Profile profile = new Profile( this, "mockProfile0", "trustin", roles, grants, denials, false );
        profileByName.put( profile.getProfileId(), profile );

        // a profile for checking union of role1 and role2 - inherits perm0 and perm1
        grants = new Permissions();
        denials = new Permissions();
        roles = new Roles( name, new Role[] { role1, role2 } );
        profile = new Profile( this, "mockProfile1", "trustin", roles, grants, denials, false );
        profileByName.put( profile.getProfileId(), profile );

        // a profile for checking union of roles with grants - granted perm0 and inherits perm1
        grants = new Permissions();
        grants.add(perm0 );
        denials = new Permissions();
        roles = new Roles( name, new Role[] { role2 } );
        profile = new Profile( this, "mockProfile2", "trustin", roles, grants, denials, false );
        profileByName.put( profile.getProfileId(), profile );

        // a profile for checking union of roles with grants - granted perm0, perm7 and inherits perm2 and perm3
        grants = new Permissions();
        grants.add(perm0);
        grants.add(perm7);
        denials = new Permissions();
        roles = new Roles( name, new Role[] { role3 } );
        profile = new Profile( this, "mockProfile3", "trustin", roles, grants, denials, false );
        profileByName.put( profile.getProfileId(), profile );

        // a profile for checking union of roles with grants and denials
        // granted perm0, in role3 and role4 but denied inherited perm7
        grants = new Permissions();
        grants.add(perm0);
        denials = new Permissions();
        denials.add(perm7);
        roles = new Roles( name, new Role[] { role3, role4 } );
        profile = new Profile( this, "mockProfile4", "trustin", roles, grants, denials, false );
        profileByName.put( profile.getProfileId(), profile );

        // a profile for checking union of roles with grants and denials
        // granted perm0, in role3 and role4 but denied inherited perm7
        grants = new Permissions();
        grants.add(perm0);
        denials = new Permissions();
        denials.add(perm7);
        roles = new Roles( name, new Role[] { role3, role4, role5 } );
        profile = new Profile( this, "mockProfile5", "trustin", roles, grants, denials, false );
        profileByName.put( profile.getProfileId(), profile );
    }


    public String getApplicationName()
    {
        return name;
    }


    public Roles getRoles()
    {
        return roles;
    }


    public Permissions getPermissions()
    {
        return perms;
    }


    public Profile getProfile( String username )
    {
        return ( Profile ) profileByName.get( username );
    }


    public String getDescription()
    {
        return "a mock application";
    }


    public void close()
    {
    }


    public boolean removePolicyListener( PolicyChangeListener listener )
    {
        return false;
    }


    public boolean addPolicyListener( PolicyChangeListener listener )
    {
        return false;
    }


    public Set getDependentProfileNames( Role role ) throws GuardianException
    {
        return null;
    }


    public Set getDependentProfileNames( StringPermission permission ) throws GuardianException
    {
        return null;
    }


    public Set getUserProfileIds( String userName ) throws GuardianException
    {
        return Collections.EMPTY_SET;
    }


    public Iterator getProfileIdIterator() throws GuardianException
    {
        return null;
    }


    public Profile getAdminProfile()
    {
        return null;
    }
}
