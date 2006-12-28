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
package org.safehaus.triplesec.guardian.ldif;


import java.io.File;
import java.security.Permissions;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.directory.shared.ldap.ldif.Entry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.GuardianException;
import org.safehaus.triplesec.guardian.PolicyChangeListener;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.guardian.Role;
import org.safehaus.triplesec.guardian.Roles;
import org.safehaus.triplesec.guardian.StringPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An LDIF file backed implementation of an application policy store.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
class LdifApplicationPolicy implements ApplicationPolicy
{
    /** the logger interface for this class */
    private static Logger log = LoggerFactory.getLogger( LdifApplicationPolicy.class );
    /** the name of the application this store is associated with */
    private final String applicationName;
    /** the dn of the application */
    private final String applicationDn;
    /** a breif description of this application */
    private String description;
    /** the LDIF file that was loaded for this application */
    private final File ldifFile;
    /** the raw entries contained within the LDIF file */
//    private final Map entries;
//    private Attributes application = null;
    /** the {@link Permissions} defined for this store's application */
    private Permissions permissions;
    /** the {@link Roles} defined for this store's application */
    private Roles roles;
    /** the {@link Profile}s loaded from LDIF */
    private Map<String, Profile> profileMap;
    /** map of userNames to sets of profile ids */
    private Map<String, Set<String>> userProfilesMap;

    boolean isClosed = false;
    /** the administrators super profile */
    private Profile adminProfile;
    private static final Set<String> EMPTY_PROFILE_SET = Collections.unmodifiableSet(new HashSet<String>(0));


    /**
     * Creates an instance of the LDIF ApplicationPolicyStore.  Two properties are 
     * expected in the info properties.  One is the dn of the application principal.  
     * The other is the path to an ldif file.
     * <table>
     *   <tr><th>property</th><th>description</th></tr>
     *   <tr><td>applicationPrincipalDN</td><td>the distinguished name of the application</td></tr>
     *   <tr><td>ldifFilePath</td><td>the path to the LDIF file containing the entries to load</td></tr>
     * </table>
     *
     * @param ldifFile the file with  the data inside
     * @param info additional information needed to load the LDIF file
     * @throws GuardianException if failures are encountered while loading objects from the backing store
     */
    public LdifApplicationPolicy( File ldifFile, Properties info ) throws GuardianException
    {
        this.userProfilesMap = new HashMap<String, Set<String>>();
        this.profileMap = new HashMap<String, Profile>();
        this.applicationDn = info.getProperty( "applicationPrincipalDN" );
        // extract the applicationName from the applicationPrincipalDN
        this.applicationName = getApplicationName( applicationDn );
        // extract the path to the LDIF file to load 
        this.ldifFile = ldifFile;
        // loads the ldifs as a map of LdapNames to Attributes
        load();
        // create the admin profile with all permissions as grants and in all roles
        this.adminProfile = new Profile( this, "admin", "admin", roles, permissions,
            new Permissions(), false );
    }

    
    private void load() throws GuardianException
    {
        Map<String, Attributes> roleMap = new HashMap<String, Attributes>();
        Map<String, Attributes> permissionMap = new HashMap<String, Attributes>();
        Map<String, Attributes> profileMap = new HashMap<String, Attributes>();
        try
        {
            LdifReader reader = new LdifReader();
            List entries = reader.parseLdifFile( ldifFile.getAbsolutePath() );
            for (Object entry1 : entries) {
                Entry entry = (Entry) entry1;
                Attributes attributes = entry.getAttributes();
                String dn = entry.getDn();

                if (dn.equals(applicationDn)) {
//                    application = attributes;
                } else if (dn.endsWith(applicationDn)) {
                    Attribute oc = attributes.get("objectClass");
                    if (oc.contains("policyPermission")) {
                        permissionMap.put(dn, attributes);
                    } else if (oc.contains("policyRole")) {
                        roleMap.put(dn, attributes);
                    } else if (oc.contains("policyProfile")) {
                        profileMap.put(dn, attributes);
                    }
                }
            }
        }
        catch ( Exception e )
        {
            String msg = "Failed to read from ldifFile '" + ldifFile + "'.";
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }
        
        loadPermissions( permissionMap );
        loadRoles( roleMap );
        loadProfiles( profileMap );
    }
    

    /**
     * Loads the role entries extracted from the LDIF.
     * 
     * @throws GuardianException if there is a problem with a role 
     */
    private void loadRoles( Map<String, Attributes> roleMap ) throws GuardianException
    {
        Set<Role> roleSet = new HashSet<Role>();

        try
        {
            for (String dn : roleMap.keySet()) {
                Attributes entry = roleMap.get(dn);
                String roleName = (String) entry.get("roleName").get();
                Attribute grantsAttribute = entry.get("grants");
                Permissions grantedPermissions = new Permissions();
                if (grantsAttribute != null) {
                    NamingEnumeration grantsEnumeration = grantsAttribute.getAll();
                    while (grantsEnumeration.hasMore()) {
                        String permName = (String) grantsEnumeration.next();
                        grantedPermissions.add(new StringPermission(permName));
                        log.debug("granting permission '" + permName + "' to role '" + roleName
                                + " in application '" + applicationName + "'");
                    }
                }

                Permissions deniedPermissions = new Permissions();
                Attribute denialsAttribute = entry.get("denials");
                if (denialsAttribute != null) {
                    NamingEnumeration denialsEnumeration = denialsAttribute.getAll();
                    while (denialsEnumeration.hasMore()) {
                        String permName = (String) denialsEnumeration.next();
                        deniedPermissions.add(new StringPermission(permName));
                        log.debug("granting permission '" + permName + "' to role '" + roleName
                                + " in application '" + applicationName + "'");
                    }
                }

                Attribute description = entry.get("description");
                Role role;
                if (description == null || description.size() == 0) {
                    role = new Role(this, roleName, grantedPermissions, deniedPermissions);
                } else {
                    role = new Role(this, roleName, grantedPermissions, deniedPermissions, (String) description.get());
                }

                roleSet.add(role);
                log.debug("loading role '" + roleName + "' for application '" + applicationName + "'");
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed on search to find roles for application " + applicationName;
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }

        Role[] roleArray = new Role[roleSet.size()];
        roleArray = roleSet.toArray( roleArray );
        this.roles = new Roles( applicationName, roleArray );
    }


    /**
     * Loads the permission entries extracted from the LDIF.
     * 
     * @throws GuardianException if there is a problem with a permission
     */
    private void loadPermissions( Map<String, Attributes> permissionMap ) throws GuardianException
    {
        permissions = new Permissions();
        try
        {
            for (String dn : permissionMap.keySet()) {
                Attributes entry = permissionMap.get(dn);
                String permName = (String) entry.get("permName").get();
                StringPermission perm;
                Attribute description = entry.get("description");
                if (description != null) {
                    perm = new StringPermission(permName);
                } else {
                    perm = new StringPermission(permName);
                }
                log.debug("loading permission " + permName + " for application " + applicationName);
                permissions.add(perm);
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed on load of permissions for application " + applicationName;
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }

    }


    public String getApplicationName()
    {
        return this.applicationName;
    }


    public String getDescription()
    {
        return this.description;
    }


    public Roles getRoles()
    {
        return this.roles;
    }


    public Permissions getPermissions()
    {
        return permissions;
    }

    
    private static boolean parseBoolean( String bool )
    {
        return bool.equals("true");

    }

    
    /**
     * Loads the profile entries extracted from the LDIF.
     * 
     * @throws GuardianException if there is a problem with a profile 
     */
    private void loadProfiles( Map<String, Attributes> profileEntryMap ) throws GuardianException
    {

        for (Map.Entry<String, Attributes> mapEntry: profileEntryMap.entrySet() )
        {
            Profile profile;
            Roles roles;
            String dn = mapEntry.getKey();
            Attributes entry = mapEntry.getValue();
            String profileId;
            String userName;
            boolean disabled = false;
            
            Attribute disabledAttr = entry.get( "safehausDisabled" );
            try
            {
                if ( disabledAttr != null )
                {
                    disabled = parseBoolean( ( ( String ) disabledAttr.get() ).toLowerCase() );
                }
            }
            catch ( Exception e )
            {
                throw new GuardianException( "Failed trying to access safehausDiabled attribute: " + dn );
            }
            
            try
            {
                profileId = ( String ) entry.get( "profileId" ).get();
            }
            catch ( Exception e )
            {
                throw new GuardianException( "Could not find profileId attribute for profile: " + dn );
            }

            try
            {
                userName = ( String ) entry.get( "user" ).get();
            }
            catch ( Exception e )
            {
                throw new GuardianException( "Could not find user attribute for profile: " + dn );
            }

            // -------------------------------------------------------------------------------
            // process and assemble the profile's granted permissions
            // -------------------------------------------------------------------------------

            Attribute grantsAttribute = entry.get( "grants" );
            Permissions grants = new Permissions();
            if ( grantsAttribute != null )
            {
                try
                {
                    NamingEnumeration grantsEnumeration = grantsAttribute.getAll();
                    while ( grantsEnumeration.hasMore() )
                    {
                        String grantedPermName = ( String ) grantsEnumeration.next();
                        grants.add( new StringPermission(grantedPermName ) );
                    }
                }
                catch ( NamingException e )
                {
                    throw new GuardianException( "Failed to get grants for profile: " + dn );
                }

            }

            // -------------------------------------------------------------------------------
            // process and assemble the profile's denied permissions
            // -------------------------------------------------------------------------------

            Attribute denialsAttribute = entry.get( "denials" );
            Permissions denials = new Permissions();
            if ( denialsAttribute != null )
            {
                try
                {
                    NamingEnumeration denialsEnumeration = denialsAttribute.getAll();
                    while ( denialsEnumeration.hasMore() )
                    {
                        String deniedPermName = ( String ) denialsEnumeration.next();
                        denials.add( new StringPermission(deniedPermName ) );
                    }
                }
                catch ( NamingException e )
                {
                    throw new GuardianException( "Failed to get denials for profile: " + dn );
                }
            }

            // -------------------------------------------------------------------------------
            // process and assemble the profile's assigned roles
            // -------------------------------------------------------------------------------

            Attribute rolesAttribute = entry.get( "roles" );
            if ( rolesAttribute != null )
            {
                Set<Role> rolesSet = new HashSet<Role>();
                try
                {
                    NamingEnumeration rolesEnumeration = rolesAttribute.getAll();
                    while ( rolesEnumeration.hasMore() )
                    {
                        String assignedRoleName = ( String ) rolesEnumeration.next();
                        rolesSet.add( this.roles.get( assignedRoleName ) );
                    }
                }
                catch ( NamingException e )
                {
                    throw new GuardianException( "Failed to get roles for profile: " + dn );
                }
                Role[] rolesArray = new Role[rolesSet.size()];
                roles = new Roles( applicationName, rolesSet.toArray( rolesArray ) );
            }
            else
            {
                roles = new Roles( applicationName, new Role[0] );
            }

            Attribute description = entry.get( "description" );
            if ( description == null || description.size() == 0 )
            {
                profile = new Profile( this, profileId, userName, roles, grants, denials, disabled );
            }
            else
            {
                String desc;
                try
                {
                    desc = ( String ) description.get();
                }
                catch ( NamingException e )
                {
                    throw new GuardianException( "Failed to get description for profile: " + dn );
                }
                profile = new Profile( this, profileId, userName, roles, grants, denials, desc, disabled );
            }
            
            profileMap.put( profileId, profile );
            
            Set<String> profileIdSet = userProfilesMap.get( userName );
            if ( profileIdSet == null )
            {
                profileIdSet = new HashSet<String>();
                userProfilesMap.put( userName, profileIdSet );
            }
            profileIdSet.add( profileId );

            if ( log.isDebugEnabled() )
            {
                log.debug( "loaded profile '" + profileId + "' in application '" + applicationName + "'" );
            }
        }
    }
    
    //TODO previously the parameter was called "userId" but from the userProfilesMap it looks like a user can have lots of profiles
    public Profile getProfile( String profileId ) throws GuardianException
    {
        if ( isClosed )
        {
            throw new IllegalStateException( "This policy object has been closed." );
        }

        if ( profileMap.containsKey( profileId ) )
        {
            return profileMap.get( profileId );
        }

        return null;
    }


    public void close() throws GuardianException
    {
        isClosed = true;
    }


    static String getApplicationName( String principalDN )
    {
        String rdn = principalDN.split( "," )[0].trim();
        String[] rdnPair = rdn.split( "=" );

        if ( ! rdnPair[0].trim().equalsIgnoreCase( "appName" ) )
        {
            throw new IllegalArgumentException( "Application principal name '" + principalDN
                    + "' is not an application DN" );
        }

        return rdnPair[1].trim();
    }


    public boolean removePolicyListener( PolicyChangeListener listener )
    {
        throw new RuntimeException( "Not implemented yet!" );
    }


    public boolean addPolicyListener( PolicyChangeListener listener )
    {
        throw new RuntimeException( "Not implemented yet!" );
    }


    public Set getDependentProfileNames( Role role ) throws GuardianException
    {
        throw new RuntimeException( "Not implemented yet!" );
    }


    public Set getDependentProfileNames( StringPermission permission ) throws GuardianException
    {
        throw new RuntimeException( "Not implemented yet!" );
    }


    public Set<String> getUserProfileIds( String userName ) throws GuardianException
    {
        Set<String> profileSet = userProfilesMap.get( userName );
        if ( profileSet == null )
        {
            return EMPTY_PROFILE_SET;
        }
        return Collections.unmodifiableSet( profileSet );
    }
    
    
    public Iterator getProfileIdIterator()
    {
        return profileMap.keySet().iterator();
    }


    public Profile getAdminProfile()
    {
        return adminProfile;
    }
}
