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
package org.safehaus.triplesec.guardian.ldap;


import org.safehaus.triplesec.guardian.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.*;
import javax.naming.event.EventDirContext;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import java.util.*;


/**
 * An LDAP backed implementation of an application policy store.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 72 $
 */
class LdapApplicationPolicy implements ApplicationPolicy
{
    private static final String[] PROF_ID = new String[] { "profileId" };
    /** the logger interface for this class */
    private static Logger log = LoggerFactory.getLogger( LdapApplicationPolicy.class );
    /** the name of the application this store is associated with */
    private final String applicationName;
    /** the application base relative name to the context given: "appName=<applicationName\>,ou=applications" */
    private final String baseRdn;
    /** a breif description of this application */
    private String description;

    /** the {@link Permission}s defined for this store's application */
    private Permissions permissions;
    /** the {@link Role}s defined for this store's application */
    private Roles roles;
    /** the JNDI Context at the base under which ou=applications can be found */
    private DirContext ctx;
    /** the profile for the admin user with all rights in all roles */
    private Profile adminProfile;


    /**
     * Creates an instance of the LDAP ApplicationPolicyStore.
     *
     * @param ctx the base context under which ou=applications and ou=users can be found
     * @param info additional information needed to
     * @throws GuardianException if failures are encountered while loading objects from the backing store
     */
    public LdapApplicationPolicy( DirContext ctx, Properties info ) throws GuardianException
    {
        if ( ctx == null )
        {
            throw new NullPointerException( "ctx cannot be null" );
        }
            
        this.ctx = ctx;
        
        // extract the applicationName from the applicationPrincipalDN
        applicationName = getApplicationName( info.getProperty( "applicationPrincipalDN" ) );

        StringBuffer buf = new StringBuffer();
        buf.append( "appName=" );
        buf.append( applicationName );
        buf.append( ",ou=applications" );
        baseRdn = buf.toString();
        
        // load the set of permissions associated with this application
        loadPermissions();

        // load the set of roles associated with this application
        loadRoles();
        
        // setup the administrator with all permissions and roles
        adminProfile = new Profile( this, "admin", "admin", roles, permissions, 
            new Permissions( applicationName, new Permission[0] ), false );

        try
        {
            Attributes appAttrs = this.ctx.getAttributes( baseRdn );
            Attribute descriptionAttr = appAttrs.get( "description" );

            if ( descriptionAttr == null || descriptionAttr.size() == 0 )
            {
                description = null;
            }
            else
            {
                description = ( String ) descriptionAttr.get();
            }
        }
        catch ( NamingException e )
        {
            log.error( "failed to read application entry: appName=" + applicationName + ",ou=applications" );
        }
        
        initializeNotifications();
    }

    
    private boolean initializeNotifications()
    {
        // attempt to get an event context and register for notifications
        SearchControls controls = new SearchControls();
        controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
        try
        {
            EventDirContext eventContext = ( EventDirContext ) ctx.lookup( "" );
            eventContext.addNamingListener( baseRdn, "(objectClass=*)", controls, new JndiListener() );
            return true;
        }
        catch ( NamingException e )
        {
            log.error( "Failed to register listener for event context: " +
                    "change notifications will not be recieved.", e );
            return false;
        }
    }

    
    private Role getRoleFromStore( String roleName ) throws NamingException
    {
        SearchControls ctrls = new SearchControls();
        ctrls.setReturningAttributes( new String[] { "roleName", "grants" } );
        ctrls.setSearchScope( SearchControls.OBJECT_SCOPE );

        StringBuffer buf = new StringBuffer();
        buf.append( "roleName=" );
        buf.append( roleName );
        buf.append( ",ou=roles," );
        buf.append( baseRdn );
        
        try
        {
            NamingEnumeration list = ctx.search( buf.toString(), "(objectClass=policyRole)", ctrls );
            if ( list.hasMore() )
            {
                SearchResult result = ( SearchResult ) list.next();
                Role role = getRole( result.getAttributes() );
                log.debug( "fetching role '" + role.getName() + "' for application '" + applicationName + "'" );
                return role;
            }
            
            return null;
        }
        catch ( NamingException e )
        {
            String msg = "Failed on search to find roles for application " + applicationName;
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }
    }
    
    
    /**
     * 
     * @throws GuardianException
     */
    private void loadRoles() throws GuardianException
    {
        Set roleSet = new HashSet();
        SearchControls ctrls = new SearchControls();
        ctrls.setReturningAttributes( new String[] { "roleName", "grants" } );
        ctrls.setSearchScope( SearchControls.ONELEVEL_SCOPE );

        try
        {
            NamingEnumeration list = ctx.search( "ou=roles," + baseRdn,
                    "(objectClass=policyRole)", ctrls );
            while ( list.hasMore() )
            {
                SearchResult result = ( SearchResult ) list.next();
                Role role = getRole( result.getAttributes() );
                roleSet.add( role );
                log.debug( "loading role '" + role.getName() + "' for application '" + applicationName + "'" );
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


    private void loadPermissions() throws GuardianException
    {
        Set permSet = new HashSet();
        SearchControls ctrls = new SearchControls();
        ctrls.setReturningAttributes( new String[] { "permName" } );
        ctrls.setSearchScope( SearchControls.ONELEVEL_SCOPE );

        try
        {
            NamingEnumeration list = ctx.search( "ou=permissions," + baseRdn,
                    "(objectClass=policyPermission)", ctrls );
            while ( list.hasMore() )
            {
                SearchResult result = ( SearchResult ) list.next();
                String permName = ( String ) result.getAttributes().get( "permName" ).get();
                Permission perm = getPermission( result.getAttributes() );
                log.debug( "loading permission " + permName + " for application " + applicationName );
                permSet.add( perm );
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed on search to find permissions for application " + applicationName;
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }

        Permission[] permArray = new Permission[permSet.size()];
        permArray = ( Permission[] ) permSet.toArray( permArray );
        this.permissions = new Permissions( applicationName, permArray );
    }
    
    
    private Permission getPermission( Attributes attrs ) throws NamingException
    {
        Permission perm;
        String permName = ( String ) attrs.get( "permName" ).get();
        Attribute description = attrs.get( "description" );
        if ( description != null )
        {
            perm = new Permission( applicationName, permName, ( String ) description.get() );
        }
        else
        {
            perm = new Permission( applicationName, permName );
        }
        return perm;
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

    
    private Role getRole( Attributes attrs ) throws NamingException
    {
        String roleName = ( String ) attrs.get( "roleName" ).get();
        Set permSet = new HashSet();
        Attribute attributes = attrs.get( "grants" );

        if ( attributes != null )
        {
            NamingEnumeration grantsEnumeration = attrs.get( "grants" ).getAll();
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

        Attribute description = attrs.get( "description" );
        Role role;
        if ( description == null || description.size() == 0 )
        {
            role = new Role( this, roleName, grants );
        }
        else
        {
            role = new Role( this, roleName, grants, ( String ) description.get() );
        }
        return role;
    }
    

    private static boolean parseBoolean( String bool )
    {
        if ( bool.equals( "true" ) )
        {
            return true;
        }
        
        return false;
    }

    
    private Profile getProfile( Attributes attrs ) throws NamingException
    {
        Permissions grants;
        Permissions denials;
        Roles roles;
        String profileId;
        String userName;
        boolean disabled = false;
        
        Attribute profileIdAttr = attrs.get( "profileId" );
        if ( profileIdAttr == null )
        {
            return null;
        }
        else 
        {
            profileId = ( String ) profileIdAttr.get();
        }

        Attribute userAttr = attrs.get( "user" );
        if ( userAttr == null )
        {
            return null;
        }
        else 
        {
            userName = ( String ) userAttr.get();
        }

        Attribute disabledAttr = attrs.get( "safehausDisabled" );
        if ( disabledAttr != null )
        {
            disabled = parseBoolean( ( ( String ) disabledAttr.get() ).toLowerCase() );
        }

        // -------------------------------------------------------------------------------
        // process and assemble the profile's granted permissions
        // -------------------------------------------------------------------------------

        Attribute grantsAttribute = attrs.get( "grants" );
        if ( grantsAttribute != null )
        {
            Set grantsSet = new HashSet();
            NamingEnumeration grantsEnumeration = grantsAttribute.getAll();
            while ( grantsEnumeration.hasMore() )
            {
                String grantedPermName = ( String ) grantsEnumeration.next();
                grantsSet.add( this.permissions.get( grantedPermName ) );
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

        Attribute denialsAttribute = attrs.get( "denials" );
        if ( denialsAttribute != null )
        {
            Set denialsSet = new HashSet();
            NamingEnumeration denialsEnumeration = denialsAttribute.getAll();
            while ( denialsEnumeration.hasMore() )
            {
                String deniedPermName = ( String ) denialsEnumeration.next();
                denialsSet.add( this.permissions.get( deniedPermName ) );
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

        Attribute rolesAttribute = attrs.get( "roles" );
        if ( rolesAttribute != null )
        {
            Set rolesSet = new HashSet();
            NamingEnumeration rolesEnumeration = rolesAttribute.getAll();
            while ( rolesEnumeration.hasMore() )
            {
                String assignedRoleName = ( String ) rolesEnumeration.next();
                rolesSet.add( this.roles.get( assignedRoleName ) );
            }
            Role[] rolesArray = new Role[rolesSet.size()];
            roles = new Roles( applicationName, ( Role[] ) rolesSet.toArray( rolesArray ) );
        }
        else
        {
            roles = new Roles( applicationName, new Role[0] );
        }

        Attribute description = attrs.get( "description" );
        Profile profile;

        if ( description == null || description.size() == 0 )
        {
            profile = new Profile( this, profileId, userName, roles, grants, denials, disabled );
        }
        else
        {
            profile = new Profile( this, profileId, userName, roles, grants, 
                denials, ( String ) description.get(), disabled );
        }
        
        return profile;
    }
    

    public Profile getProfile( String profileId )
    {
        if ( ctx == null )
        {
            throw new IllegalStateException( "This ApplicationProfileStore has been closed." );
        }

        if ( profileId.equals( "admin" ) )
        {
            return adminProfile;
        }
        
        /*
         * Searching via one level scope for a profile is better than base scope lookups because
         * if the profile is not present search will not fail but return zero entries.  Base scope
         * searches will raise an exception since the search base will be missing.  Plus profileId
         * shall be indexed by default.
         */
        SearchControls ctrls = new SearchControls();
        ctrls.setSearchScope( SearchControls.ONELEVEL_SCOPE );

        NamingEnumeration list = null;
        try
        {
            list = ctx.search( "ou=profiles," + baseRdn, "(profileId=" + profileId + ")", ctrls );
            if ( list.hasMore() )
            {
                SearchResult result = ( SearchResult ) list.next();
                Profile profile = getProfile( result.getAttributes() );

                if ( log.isDebugEnabled() )
                {
                    log.debug( "loaded profile '" + profileId + "' in application '" + applicationName + "'" );
                }

                return profile;
            }
            else
            {
                if ( log.isInfoEnabled() )
                {
                    log.info( "Profile search for profileId '" + profileId + "' in application '"
                            + applicationName + "' failed to return an entry." );
                }

                return null;
            }
        }
        catch ( NamingException e )
        {
            String msg = "Failed on search to find profile for profileId '" + profileId + "' in '" + applicationName + "'";
            log.error( msg, e );
            throw new GuardianException( msg, e );
        }
        finally
        {
            if ( list != null )
            {
                try
                {
                    list.close();
                }
                catch ( NamingException e )
                {
                    log.error( "Failed to close NamingEnumeration after profile search." );
                }
            }
        }
    }


    public void close() throws GuardianException
    {
        if ( ctx == null )
        {
            return;
        }
        
        try
        {
            ctx.close();
            ctx = null;
        }
        catch ( NamingException e )
        {
            log.error( "Encountered failure while trying to close JNDI context of store", e );
        }
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

    
    private List listeners = new ArrayList();
    

    public boolean removePolicyListener( PolicyChangeListener listener )
    {
        return listeners.remove( listener );
    }


    public boolean addPolicyListener( PolicyChangeListener listener )
    {
        if ( listeners.contains( listener ) )
        {
            return false;
        }
        
        listeners.add( listener );
        return true;
    }


    public Set getDependentProfileNames( Role role ) throws GuardianException
    {
        SearchControls controls = new SearchControls();
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        controls.setReturningAttributes( PROF_ID );
        
        String baseProfilesRdn = "ou=profiles," + this.baseRdn;
        NamingEnumeration results = null;
        Set profiles = new HashSet();
        profiles.add( "admin" );
        
        StringBuffer filter = new StringBuffer();
        filter.append( "(& (objectClass=policyProfile) (roles=" );
        filter.append( role.getName() );
        filter.append( ") )" );
        
        try
        {
            results = ctx.search( baseProfilesRdn, filter.toString(), controls );
            while ( results.hasMore() )
            {
                SearchResult result = ( SearchResult ) results.next();
                
                if ( result.getAttributes().get( "profileId" ) != null )
                {
                    profiles.add( result.getAttributes().get( "profileId" ).get() );
                }
            }
        }
        catch ( NamingException e )
        {
            throw new GuardianException( "Failed to lookup profiles dependent on role '" + 
                role.getName() + "' while searching the directory" );
        }
        
        return profiles;
    }

    
    public Set getDependentProfileNames( Permission permission ) throws GuardianException
    {
        SearchControls controls = new SearchControls();
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        controls.setReturningAttributes( PROF_ID );
        
        String baseProfilesRdn = "ou=profiles," + this.baseRdn;
        NamingEnumeration results = null;
        Set profiles = new HashSet();
        profiles.add( "admin" );
        
        StringBuffer filter = new StringBuffer();
        filter.append( "(& (objectClass=policyProfile) (| (grants=" );
        filter.append( permission.getName() );
        filter.append( ") (denials=" );
        filter.append( permission.getName() );
        filter.append( ") ) )" );
        
        try
        {
            results = ctx.search( baseProfilesRdn, filter.toString(), controls );
            while ( results.hasMore() )
            {
                SearchResult result = ( SearchResult ) results.next();
                
                if ( result.getAttributes().get( "profileId" ) != null )
                {
                    profiles.add( result.getAttributes().get( "profileId" ).get() );
                }
            }
        }
        catch ( NamingException e )
        {
            throw new GuardianException( "Failed to lookup profiles dependent on permission '" + 
                permission.getName() + "' while searching the directory" );
        }
        
        return profiles;
    }

    
    private boolean hasObjectClass( Attribute oc, String value ) throws NamingException
    {
        if ( oc == null )
        {
            throw new NullPointerException( "expecting non-null object class (oc arg)" );
        }
        
        if ( value == null )
        {
            throw new NullPointerException( "expecting non-null object class value (value arg)" );
        }
        
        NamingEnumeration all = oc.getAll();
        while( all.hasMore() )
        {
            String candidate = ( String ) all.next();
            if ( candidate.equalsIgnoreCase( value ) )
            {
                return true;
            }
        }
        
        return false;
    }
    

    /**
     * An event transducer that converts JNDI notifications of change into
     * Guardian policy change notifications.  
     */
    class JndiListener implements ObjectChangeListener, NamespaceChangeListener
    {
        private void logEvent( NamingEvent evt, Attributes entry )
        {
            if ( log.isDebugEnabled() )
            {
                StringBuffer buf = new StringBuffer();
                buf.append( "objectChanged(evt): " ).append( evt ).append( "\n" );
                buf.append( "\ttype          = " ).append( evt.getType() ).append( "\n" );
                buf.append( "\tchangeInfo    = " ).append( evt.getChangeInfo() ).append( "\n" );
                buf.append( "\teventContext  = " ).append( evt.getEventContext() ).append( "\n" );
                buf.append( "\tnewBinding    = " ).append( evt.getNewBinding() ).append( "\n" );
                buf.append( "\toldBinding    = " ).append( evt.getOldBinding() ).append( "\n" );
                buf.append( "\tsource        = " ).append( evt.getSource() ).append( "\n" );
                if ( entry == null )
                {
                    buf.append( "\tentry     = " ).append( "null" ).append( "\n" );
                }
                else
                {
                    buf.append( "\tentry     = " ).append( entry ).append( "\n" );
                }
                log.debug( buf.toString() );
                
                if ( evt.getNewBinding() != null )
                {
                    log.debug( "Binding Class = " + evt.getNewBinding().getClass() );
                }
            }
        }

        public void objectChanged( NamingEvent evt )
        {
            SearchResult result = null;
            Attributes entry = null;
            Attribute oc = null;
            
            /*
             * Workaround until https://issues.apache.org/jira/browse/DIRSERVER-587 
             * is fixed.  We simply lookup the object on the server rather than use
             * the attributes delivered to us.
             */
            result = ( SearchResult ) evt.getNewBinding();
            String name = result.getName();
            
            if ( name.indexOf( applicationName ) == -1 )
            {
                if ( log.isWarnEnabled() )
                {
                    log.warn( "Entry '" + name + "' ignored! " +
                            "It is not specific to the application: " + applicationName );
                }
                return;
            }
            
            try
            {
                entry = ctx.getAttributes( name );
            }
            catch ( NamingException e1 )
            {
                log.error( "Cannot deliver policy change notification.  " +
                        "Failed to lookup entry attributes for " + name, e1 );
            }

            logEvent( evt, entry );
            oc = entry.get( "objectClass" );
            
            try
            {
                if ( hasObjectClass( oc, "policyApplication" ) )
                {
                    log.info( "Received notification that the policyApplication has changed." );
                    return;
                }
                
                if ( hasObjectClass( oc, "policyPermission" ) )
                {
                    String permName = ( String ) entry.get( "permName" ).get();
                    if ( log.isDebugEnabled() )
                    {
                        log.debug( "Received notification that a policyPermission " + permName + " has changed." );
                    }
                    
                    /*
                     * 1. Need to update/replace the permission itelf in Permissions.
                     * 2. Need to update/replace all roles that now depend on this permission in Roles.
                     * 3. Let user application know that the permission has changed.
                     */
                    Permissions permissions = LdapApplicationPolicy.this.permissions;
                    Permission newPermission = getPermission( entry );
                    Permission oldPermission = permissions.get( newPermission.getName() );
                    Roles dependentRoles = LdapApplicationPolicy.this.roles.getDependentRoles( oldPermission );
                    Permissions newPermissions = new Permissions( applicationName, new Permission[] { newPermission } );
                    Permissions oldPermissions = new Permissions( applicationName, new Permission[] { oldPermission } );
                    permissions = permissions.removeAll( oldPermissions );
                    permissions = permissions.addAll( newPermissions );
                    LdapApplicationPolicy.this.permissions = permissions;
                    
                    List oldRoleList = new ArrayList();
                    List newRoleList = new ArrayList();
                    for ( Iterator ii = dependentRoles.iterator(); ii.hasNext(); /* */ )
                    {
                        Role oldRole = ( Role ) ii.next();
                        oldRoleList.add( oldRole );
                        
                        Role newRole = getRoleFromStore( oldRole.getName() );
                        newRoleList.add( newRole );
                    }
                    Role[] oldRolesArray = new Role[oldRoleList.size()];
                    oldRolesArray = ( Role[] ) oldRoleList.toArray( oldRolesArray );
                    Roles oldRoles = new Roles( applicationName, oldRolesArray );
                    Roles roles = LdapApplicationPolicy.this.roles;
                    roles = roles.removeAll( oldRoles );
                    
                    Role[] newRolesArray = new Role[newRoleList.size()];
                    newRolesArray = ( Role[] ) newRoleList.toArray( newRolesArray );
                    Roles newRoles = new Roles( applicationName, newRolesArray );
                    roles = roles.addAll( newRoles );
                    
                    LdapApplicationPolicy.this.roles = roles;
                    
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.permissionChanged( LdapApplicationPolicy.this, newPermission, 
                            ChangeType.MODIFY );
                    }
                }
                else if ( hasObjectClass( oc, "policyRole" ) )
                {
                    String roleName = ( String ) entry.get( "roleName" ).get();
                    
                    if ( log.isDebugEnabled() )
                    {
                        log.debug( "Received notification that a policyRole " + roleName + " has changed." );
                    }
                    
                    /*
                     * 1. Need to update/replace the role itelf in Roles.
                     * 2. Let user application know that the Role has changed.
                     */

                    Role newRole = getRole( entry );
                    Roles roles = LdapApplicationPolicy.this.roles;
                    Roles oldRoles = new Roles( applicationName, new Role[] { roles.get( roleName ) } );
                    roles = roles.removeAll( oldRoles );
                    Roles newRoles = new Roles( applicationName, new Role[] { newRole } );
                    roles = roles.addAll( newRoles );
                    LdapApplicationPolicy.this.roles = roles;
                    
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.roleChanged( LdapApplicationPolicy.this, newRole, ChangeType.MODIFY );
                    }
                }
                else if ( hasObjectClass( oc, "policyProfile" ) )
                {
                    String profileId = ( String ) entry.get( "profileId" ).get();
                    
                    if ( log.isDebugEnabled() )
                    {
                        log.debug( "Received notification that a policyProfile " + profileId + " has changed." );
                    }
                    
                    /*
                     * 1. Let user application know that the Profile has changed.
                     */
                    
                    Profile profile = getProfile( entry );
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.profileChanged( LdapApplicationPolicy.this, profile, ChangeType.MODIFY );
                    }
                }
                else 
                {
                    if ( log.isInfoEnabled() )
                    {
                        log.info( "Insignificant object type changed: " + entry );
                    }
                }

                // setup the administrator with all permissions and roles
                adminProfile = new Profile( LdapApplicationPolicy.this, "admin", "admin", roles, permissions, 
                    new Permissions( applicationName, new Permission[0] ), false );
            }
            catch ( NamingException e )
            {
                log.error( "failed to handle a notification", e );
            }
        }

        public void namingExceptionThrown( NamingExceptionEvent evt )
        {
            log.error( "Detected naming exception event in JNDI listener.", evt.getException() );
            boolean enabled = initializeNotifications();
            if ( enabled )
            {
                log.info( "Re-enabled notifications" );
            }
            else
            {
                log.error( "Could not re-enable notifications.  Notifications will no longer be recieved." );
            }
        }


        public void objectAdded( NamingEvent evt )
        {
            SearchResult result = ( SearchResult ) evt.getNewBinding();
            Attributes entry = result.getAttributes();
            Attribute oc = entry.get( "objectClass" );
            String name = result.getName();
            logEvent( evt, entry );
            
            if ( name.indexOf( applicationName ) == -1 )
            {
                if ( log.isWarnEnabled() )
                {
                    log.warn( "Entry '" + name + "' ignored! " +
                            "It is not specific to the application: " + applicationName );
                }
                return;
            }
            
            try
            {
                if ( hasObjectClass( oc, "policyPermission" ) )
                {
                    /*
                     * 1. Need to add the permission to the permissions of the application
                     * 2. Need to notify of the permission's addition to all listeners
                     */
                    Permission permission = getPermission( entry );
                    Permissions permissions = LdapApplicationPolicy.this.permissions;
                    permissions = permissions.addAll( new Permissions( applicationName, 
                        new Permission[] { permission } ) );
                    LdapApplicationPolicy.this.permissions = permissions;
                    
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.permissionChanged( LdapApplicationPolicy.this, permission, ChangeType.ADD );
                    }
                }
                else if ( hasObjectClass( oc, "policyRole" ) )
                {
                    /*
                     * 1. Need to add the role to the roles of the application
                     * 2. Need to notify of the role's addition to all listeners
                     */
                    Role role = getRole( entry );
                    add( role );

                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.roleChanged( LdapApplicationPolicy.this, role, ChangeType.ADD );
                    }
                }
                else if ( hasObjectClass( oc, "policyProfile" ) )
                {
                    /*
                     * 1. Need to notify of the profile's addition to all listeners
                     */
                    Profile profile = getProfile( entry );
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.profileChanged( LdapApplicationPolicy.this, profile, ChangeType.ADD );
                    }
                }
                else
                {
                    System.out.println( "Entry '" + name + "' ignored!" );
                    return;
                }
                
                // setup the administrator with all permissions and roles
                adminProfile = new Profile( LdapApplicationPolicy.this, "admin", "admin", roles, permissions, 
                    new Permissions( applicationName, new Permission[0] ), false );
            }
            catch ( NamingException e )
            {
                log.error( "failed to handle an event", e );
            }
        }


        public void objectRemoved( NamingEvent evt )
        {
            SearchResult result = ( SearchResult ) evt.getOldBinding();
            Attributes entry = result.getAttributes();
            Attribute oc = entry.get( "objectClass" );
            String name = result.getName();
            logEvent( evt, entry );
            
            if ( name.indexOf( applicationName ) == -1 )
            {
                if ( log.isWarnEnabled() )
                {
                    System.out.println( "Entry '" + name + "' ignored! " +
                            "It is not specific to the application: " + applicationName );
                }
                return;
            }
            
            try
            {
                if ( hasObjectClass( oc, "policyPermission" ) )
                {
                    /*
                     * 1. Need to remove the permission from the permissions of the application
                     * 2. Need to notify of the permission's removal to all listeners
                     */
                    String profileId = ( String ) entry.get( "permName" ).get();
                    Permissions permissions = LdapApplicationPolicy.this.permissions;
                    Permission permission = permissions.get( profileId );
                    permissions = permissions.removeAll( new Permissions( applicationName, 
                        new Permission[] { permission } ) );
                    LdapApplicationPolicy.this.permissions = permissions;
                    
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.permissionChanged( LdapApplicationPolicy.this, permission, ChangeType.DEL );
                    }
                }
                else if ( hasObjectClass( oc, "policyRole" ) )
                {
                    /*
                     * 1. Need to remove the role from the roles of the application
                     * 2. Need to notify of the role's removal to all listeners
                     */
                    String roleName = ( String ) entry.get( "roleName" ).get();
                    Role role = removeRole( roleName );

                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.roleChanged( LdapApplicationPolicy.this, role, ChangeType.DEL );
                    }
                }
                else if ( hasObjectClass( oc, "policyProfile" ) )
                {
                    /*
                     * 1. Need to notify of the profile's addition to all listeners
                     */
                    Profile profile = getProfile( entry );
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.profileChanged( LdapApplicationPolicy.this, profile, ChangeType.DEL );
                    }
                }
                else
                {
                    System.out.println( "Entry '" + name + "' ignored!" );
                    return;
                }

                // setup the administrator with all permissions and roles
                adminProfile = new Profile( LdapApplicationPolicy.this, "admin", "admin", roles, permissions, 
                    new Permissions( applicationName, new Permission[0] ), false );
            }
            catch ( NamingException e )
            {
                log.error( "failed to process an event", e );
            }
        }


        public void objectRenamed( NamingEvent evt )
        {
            logEvent( evt, null );
            /*
             * For permissions and roles we need to first remove the old object from 
             * the Permissions and Roles objects.  Then we need to add the new named
             * object to the Permissions and Roles.  For profiles all we need is a 
             * simple notification.  
             */
            String oldName = evt.getOldBinding().getName();
            String newName = evt.getNewBinding().getName();
            Attributes newEntry = ( ( SearchResult ) evt.getNewBinding() ).getAttributes();
            Attribute oc = newEntry.get( "objectClass" );
            
            if ( oldName.indexOf( applicationName ) == -1 )
            {
                if ( log.isWarnEnabled() )
                {
                    System.out.println( "Entry '" + oldName + "' ignored! " +
                            "It is not specific to the application: " + applicationName );
                }
                return;
            }
            
            try
            {
                String oldProfileId = getRdn( oldName );
                oldProfileId = getRdnValue( oldProfileId );
                
                if ( hasObjectClass( oc, "policyPermission" ) )
                {
                    removePermission( oldProfileId );
                    Permission newPermission = getPermission( newEntry );
                    add( newPermission );
                    
                    for ( int ii = 0; ii  < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.permissionRenamed( LdapApplicationPolicy.this, newPermission, oldProfileId );
                    }
                }            
                else if ( hasObjectClass( oc, "policyRole" ) )
                {
                    removeRole( oldProfileId );
                    Role newRole = getRole( newEntry );
                    add( newRole );
                    
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.roleRenamed( LdapApplicationPolicy.this, newRole, oldProfileId );
                    }
                }
                else if ( hasObjectClass( oc, "policyProfile" ) )
                {
                    /*
                     * 1. Need to notify of the profile's addition to all listeners
                     */
                    Profile profile = getProfile( newEntry );
                    for ( int ii = 0; ii < listeners.size(); ii++ )
                    {
                        PolicyChangeListener listener = ( PolicyChangeListener ) listeners.get( ii );
                        listener.profileRenamed( LdapApplicationPolicy.this, profile, oldProfileId );
                    }
                }
                else
                {
                    System.out.println( "Rename of entry '" + oldName + "' to '" + newName + "' ignored!" );
                    return;
                }

                // setup the administrator with all permissions and roles
                adminProfile = new Profile( LdapApplicationPolicy.this, "admin", "admin", roles, permissions, 
                    new Permissions( applicationName, new Permission[0] ), false );
            }
            catch ( NamingException e )
            {
                log.error( "failed to process an event", e );
            }
        }
    }
    
    
    /**
     * Gets the value of a single name component of a distinguished name.
     * 
     * @param rdn the name component to get the value from
     * @return the value of the single name component 
     */
    public static String getRdnValue( String rdn )
    {
        int index = rdn.indexOf( '=' );
        return rdn.substring( index + 1, rdn.length() );
    }


    /**
     * Quickly splits off the relative distinguished name component.
     * 
     * @param name the distinguished name or a name fragment
     * @return the rdn 
     */
    private static String getRdn( String name )
    {
        if ( null == name )
        {
            return null;
        }

        int commaIndex = -1;
        if ( ( commaIndex = name.indexOf( ',' ) ) == -1 )
        {
            return name;
        }

        return name.substring( 0, commaIndex );
    }


    private void add( Role role )
    {
        Roles addedRoles = new Roles( applicationName, new Role[] { role } );
        this.roles = this.roles.addAll( addedRoles );
    }

    
    private Role removeRole( String roleName )
    {
        Role role = this.roles.get( roleName );
        Roles removedRoles = new Roles( applicationName, new Role[] { role } );
        this.roles = this.roles.removeAll( removedRoles );
        return role;
    }
    
    
    private void add( Permission permission )
    {
        Permissions addedPermissions = new Permissions( applicationName, new Permission[] { permission } );
        this.permissions = this.permissions.addAll( addedPermissions );
    }

    
    private Permission removePermission( String permName )
    {
        Permission permission = this.permissions.get( permName );
        Permissions removedPermissions = new Permissions( applicationName, new Permission[] { permission } );
        this.permissions = this.permissions.removeAll( removedPermissions );
        return permission;
    }


    public Set getUserProfileIds( String userName ) throws GuardianException
    {
        SearchControls controls = new SearchControls();
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        controls.setReturningAttributes( PROF_ID );
        
        String baseProfilesRdn = "ou=profiles," + this.baseRdn;
        NamingEnumeration results = null;
        Set profiles = new HashSet();
        
        StringBuffer filter = new StringBuffer();
        filter.append( "(& (objectClass=policyProfile) (user=" );
        filter.append( userName );
        filter.append( ") )" );
        
        try
        {
            results = ctx.search( baseProfilesRdn, filter.toString(), controls );
            while ( results.hasMore() )
            {
                SearchResult result = ( SearchResult ) results.next();
                
                if ( result.getAttributes().get( "profileId" ) != null )
                {
                    profiles.add( result.getAttributes().get( "profileId" ).get() );
                }
            }
        }
        catch ( NamingException e )
        {
            throw new GuardianException( "Failed to lookup profiles for user '" + 
                userName + "' while searching the directory" );
        }
        
        return profiles;
    }


    public Iterator getProfileIdIterator() throws GuardianException
    {
        SearchControls controls = new SearchControls();
        controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        controls.setReturningAttributes( PROF_ID );
        
        String baseProfilesRdn = "ou=profiles," + this.baseRdn;
        NamingEnumeration results = null;
        try
        {
            results = ctx.search( baseProfilesRdn, "(objectClass=policyProfile)", controls );
            return new ProfileIdIterator( results );
        }
        catch ( NamingException e )
        {
            throw new GuardianException( "Failed to lookup profiles while searching the directory" );
        }
    }


    public Profile getAdminProfile()
    {
        return adminProfile;
    }
}
