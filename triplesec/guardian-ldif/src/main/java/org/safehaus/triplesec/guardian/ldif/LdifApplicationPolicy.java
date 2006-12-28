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


import org.apache.directory.shared.ldap.ldif.Entry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.safehaus.triplesec.guardian.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.*;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;

import java.io.File;
import java.util.*;


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
    private Map profileMap;
    /** map of userNames to sets of profile ids */
    private Map userProfilesMap;

    boolean isClosed = false;
    /** the administrators super profile */
    private Profile adminProfile;

    
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
     * @param ctx the base context under which ou=applications and ou=users can be found
     * @param info additional information needed to load the LDIF file
     * @throws GuardianException if failures are encountered while loading objects from the backing store
     */
    public LdifApplicationPolicy( File ldifFile, Properties info ) throws GuardianException
    {
        this.userProfilesMap = new HashMap();
        this.profileMap = new HashMap();
        this.applicationDn = info.getProperty( "applicationPrincipalDN" );
        // extract the applicationName from the applicationPrincipalDN
        this.applicationName = getApplicationName( applicationDn );
        // extract the path to the LDIF file to load 
        this.ldifFile = ldifFile;
        // loads the ldifs as a map of LdapNames to Attributes
        load();
        // create the admin profile with all permissions as grants and in all roles
        this.adminProfile = new Profile( this, "admin", "admin", roles, permissions, 
            new Permissions( applicationName, new Permission[0] ), false );
    }

    
    private Map load() throws GuardianException
    {
        Map roleMap = new HashMap();
        Map permissionMap = new HashMap();
        Map profileMap = new HashMap();
        Map entryMap = new HashMap();
        try
        {
            LdifReader reader = new LdifReader();
            List entries = reader.parseLdifFile( ldifFile.getAbsolutePath() );
            for ( int ii = 0; ii < entries.size(); ii++ )
            {
                Entry entry = ( Entry ) entries.get( ii );
                Attributes attributes = entry.getAttributes();
                String dn = entry.getDn();
                entryMap.put( dn, attributes );
                
                if ( dn.equals( applicationDn ) )
                {
//                    application = attributes;
                }
                else if ( dn.endsWith( applicationDn ) )
                {
                    Attribute oc = attributes.get( "objectClass" );
                    if ( oc.contains( "policyPermission" ) )
                    {
                        permissionMap.put( dn, attributes );
                    }
                    else if ( oc.contains( "policyRole" ) )
                    {
                        roleMap.put( dn, attributes );
                    }
                    else if ( oc.contains( "policyProfile" ) )
                    {
                        profileMap.put( dn, attributes );
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
        return entryMap;
    }
    

    /**
     * Loads the role entries extracted from the LDIF.
     * 
     * @throws GuardianException if there is a problem with a role 
     */
    private void loadRoles( Map roleMap ) throws GuardianException
    {
        Set roleSet = new HashSet();

        try
        {
            Iterator keys = roleMap.keySet().iterator();
            while ( keys.hasNext() )
            {
                String dn = ( String ) keys.next();
                Attributes entry = ( Attributes ) roleMap.get( dn );
                String roleName = ( String ) entry.get( "roleName" ).get();
                Set permSet = new HashSet();
                Attribute attributes = entry.get( "grants" );

                if ( attributes != null )
                {
                    NamingEnumeration grantsEnumeration = entry.get( "grants" ).getAll();
                    while ( grantsEnumeration.hasMore() )
                    {
                        String permName = ( String ) grantsEnumeration.next();
                        permSet.add( permissions.get( permName ) );
                        log.debug( "granting permission '" + permName + "' to role '" + roleName
                                + " in application '" + applicationName + "'" );
                    }
                }
                Permission[] permArray = new Permission[permSet.size()];
                Permissions grants = new Permissions( applicationName, ( Permission[] ) permSet.toArray( permArray ) );

                Attribute description = entry.get( "description" );
                Role role;
                if ( description == null || description.size() == 0 )
                {
                    role = new Role( this, roleName, grants );
                }
                else
                {
                    role = new Role( this, roleName, grants, ( String ) description.get() );
                }

                roleSet.add( role );
                log.debug( "loading role '" + roleName + "' for application '" + applicationName + "'" );
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed on search to find roles for application " + applicationName;
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }

        Role[] roleArray = new Role[roleSet.size()];
        roleArray = ( Role[] ) roleSet.toArray( roleArray );
        this.roles = new Roles( applicationName, roleArray );
    }


    /**
     * Loads the permission entries extracted from the LDIF.
     * 
     * @throws GuardianException if there is a problem with a permission
     */
    private void loadPermissions( Map permissionMap ) throws GuardianException
    {
        Set permSet = new HashSet();

        try
        {
            Iterator keys = permissionMap.keySet().iterator();
            while ( keys.hasNext() )
            {
                String dn = ( String ) keys.next();
                Attributes entry = ( Attributes ) permissionMap.get( dn );
                String permName = ( String ) entry.get( "permName" ).get();
                Permission perm;
                Attribute description = entry.get( "description" );
                if ( description != null )
                {
                    perm = new Permission( applicationName, permName, ( String ) description.get() );
                }
                else
                {
                    perm = new Permission( applicationName, permName );
                }
                log.debug( "loading permission " + permName + " for application " + applicationName );
                permSet.add( perm );
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed on load of permissions for application " + applicationName;
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }

        Permission[] permArray = new Permission[permSet.size()];
        permArray = ( Permission[] ) permSet.toArray( permArray );
        this.permissions = new Permissions( applicationName, permArray );
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
        if ( bool.equals( "true" ) )
        {
            return true;
        }
        
        return false;
    }

    
    /**
     * Loads the profile entries extracted from the LDIF.
     * 
     * @throws GuardianException if there is a problem with a profile 
     */
    private void loadProfiles( Map profileEntryMap ) throws GuardianException
    {
        String[] profileDns = new String[profileEntryMap.size()];
        profileEntryMap.keySet().toArray( profileDns );
        
        for ( int ii = 0; ii < profileDns.length; ii++ )
        {
            Profile profile;
            Permissions grants;
            Permissions denials;
            Roles roles;
            String dn = profileDns[ii];
            Attributes entry = ( Attributes ) profileEntryMap.get( dn );
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
            if ( grantsAttribute != null )
            {
                Set grantsSet = new HashSet();
                try
                {
                    NamingEnumeration grantsEnumeration = grantsAttribute.getAll();
                    while ( grantsEnumeration.hasMore() )
                    {
                        String grantedPermName = ( String ) grantsEnumeration.next();
                        grantsSet.add( this.permissions.get( grantedPermName ) );
                    }
                }
                catch ( NamingException e )
                {
                    throw new GuardianException( "Failed to get grants for profile: " + dn );
                }

                Permission[] grantsArray = new Permission[grantsSet.size()];
                grants = new Permissions( applicationName, ( Permission[] ) grantsSet.toArray( grantsArray ) );
            }
            else
            {
                grants = new Permissions( applicationName, new Permission[0] );
            }

            // -------------------------------------------------------------------------------
            // process and assemble the profile's granted permissions
            // -------------------------------------------------------------------------------

            Attribute denialsAttribute = entry.get( "denials" );
            if ( denialsAttribute != null )
            {
                Set denialsSet = new HashSet();
                try
                {
                    NamingEnumeration denialsEnumeration = denialsAttribute.getAll();
                    while ( denialsEnumeration.hasMore() )
                    {
                        String deniedPermName = ( String ) denialsEnumeration.next();
                        denialsSet.add( this.permissions.get( deniedPermName ) );
                    }
                }
                catch ( NamingException e )
                {
                    throw new GuardianException( "Failed to get denials for profile: " + dn );
                }
                Permission[] denialsArray = new Permission[denialsSet.size()];
                denials = new Permissions( applicationName, ( Permission[] ) denialsSet.toArray( denialsArray ) );
            }
            else
            {
                denials = new Permissions( applicationName, new Permission[0] );
            }

            // -------------------------------------------------------------------------------
            // process and assemble the profile's assigned roles
            // -------------------------------------------------------------------------------

            Attribute rolesAttribute = entry.get( "roles" );
            if ( rolesAttribute != null )
            {
                Set rolesSet = new HashSet();
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
                roles = new Roles( applicationName, ( Role[] ) rolesSet.toArray( rolesArray ) );
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
                String desc = "null";
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
            
            Set profileIdSet = ( Set ) userProfilesMap.get( userName );
            if ( profileIdSet == null )
            {
                profileIdSet = new HashSet();
                userProfilesMap.put( userName, profileIdSet );
            }
            profileIdSet.add( profileId );

            if ( log.isDebugEnabled() )
            {
                log.debug( "loaded profile '" + profileId + "' in application '" + applicationName + "'" );
            }
        }
    }
    

    public Profile getProfile( String userName ) throws GuardianException
    {
        if ( isClosed )
        {
            throw new IllegalStateException( "This policy object has been closed." );
        }

        if ( profileMap.containsKey( userName ) )
        {
            return ( Profile ) profileMap.get( userName );
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


    public Set getDependentProfileNames( Permission permission ) throws GuardianException
    {
        throw new RuntimeException( "Not implemented yet!" );
    }


    public Set getUserProfileIds( String userName ) throws GuardianException
    {
        Set profileSet = ( Set ) userProfilesMap.get( userName );
        if ( profileSet == null )
        {
            return Collections.EMPTY_SET;
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
