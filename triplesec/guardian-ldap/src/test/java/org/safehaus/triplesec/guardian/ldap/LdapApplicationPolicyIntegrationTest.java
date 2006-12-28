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


import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.InitialLdapContext;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.ChangeType;
import org.safehaus.triplesec.guardian.StringPermission;
import org.safehaus.triplesec.guardian.PolicyChangeListener;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.guardian.Role;
import org.safehaus.triplesec.guardian.PermissionsUtil;
import org.safehaus.triplesec.integration.TriplesecIntegration;


/**
 * TestCase to test the LDAP ApplicationPolicyStore implementation.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LdapApplicationPolicyIntegrationTest extends TriplesecIntegration
{

    private static final String APP_NAME = "mockApplication";
    private Object lockObject = new Object();
    private String originalName;
    private ChangeType changeType;
    private Profile profile;
    private Role role;
    private StringPermission permission;
    private LdapApplicationPolicy store;


    public LdapApplicationPolicyIntegrationTest( String string ) throws Exception
    {
        super( string );
    }


    public LdapApplicationPolicyIntegrationTest() throws Exception
    {
        super();
    }


    protected void setUp() throws Exception
    {
        super.setUp();
        Thread.sleep(500);
        Properties props = new Properties();
        props.setProperty( "applicationPrincipalDN", "appName=" + APP_NAME + ",ou=applications,dc=example,dc=com" );
        props.setProperty( "applicationCredentials", "testing" );

        Class.forName( "org.safehaus.triplesec.guardian.ldap.LdapConnectionDriver" );
        store = ( LdapApplicationPolicy ) ApplicationPolicyFactory.
                newInstance( "ldap://localhost:"+super.getLdapPort()+"/dc=example,dc=com", props );
    }


    protected void tearDown() throws Exception
    {
        super.tearDown();
        store.close();
        store = null;
        changeType = null;
        profile = null;
        role = null;
        permission = null;
        originalName = null;
    }


    public void testGetApplicationNameAndProfile()
    {
        String applicationName = LdapApplicationPolicy.getApplicationName(
                "appName=testingApp,ou=applications,dc=example,dc=com" );
        assertEquals( "testingApp", applicationName );

        try
        {
            LdapApplicationPolicy.getApplicationName( "notanapp=blahblah" );
            fail( "should never get here due to an exception" );
        }
        catch ( IllegalArgumentException e )
        {

        }

        Profile p = store.getProfile( "nonexistant" );
        assertNull( p );

        p = store.getProfile( "mockProfile0" );
        assertTrue( PermissionsUtil.isEmpty(p.getEffectiveGrantedPermissions()) );
        assertEquals( 5, store.getRoles().size() );
        assertEquals( p, store.getProfile( "mockProfile0" ) );

        p = store.getProfile( "mockProfile1" );
//        assertEquals( 2, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm0" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm1" )));
//        assertFalse( p.implies( new StringPermission(APP_NAME, "mockPerm3")));
        assertEquals( p, store.getProfile( "mockProfile1" ) );

        p = store.getProfile( "mockProfile2" );
//        assertEquals( 2, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm0" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm1" )));
//        assertFalse( p.implies( new StringPermission(APP_NAME, "mockPerm3")));
        assertEquals( p, store.getProfile( "mockProfile2" ) );

        p = store.getProfile( "mockProfile3" );
//        assertEquals( 4, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm0" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm7" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm2" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm3" )));
//        assertFalse( p.implies( new StringPermission(APP_NAME, "mockPerm4" )));
        assertEquals( p, store.getProfile( "mockProfile3" ) );

        p = store.getProfile( "mockProfile4" );
//        assertEquals( 7, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm0" )));
//        assertFalse( p.implies( new StringPermission(APP_NAME, "mockPerm1" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm2" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm3" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm4" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm5" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm6" )));
//        assertFalse( p.implies( new StringPermission(APP_NAME, "mockPerm7" )));
//        assertFalse( p.implies( new StringPermission(APP_NAME, "mockPerm8" )));
//        assertTrue( p.implies( new StringPermission(APP_NAME, "mockPerm9" )));
//        assertFalse( p.implies( new StringPermission(APP_NAME, "mockPerm14" )));
        assertEquals( p, store.getProfile( "mockProfile4" ) );

        store.close();

        try
        {
            store.getProfile( "asdf" );
            fail( "should never get here due to an exception" );
        }
        catch ( IllegalStateException e )
        {

        }
    }

    
    public void testGetDependantProfilesRole() throws Exception
    {
        Role role0 = store.getRoles().get( "mockRole0" );
        Set dependents = store.getDependentProfileNames( role0 );
        assertEquals( 1, dependents.size() );
        
        Role role1 = store.getRoles().get( "mockRole1" );
        dependents = store.getDependentProfileNames( role1 );
        assertEquals( 2, dependents.size() );
        assertTrue( dependents.contains( "mockProfile1" ) );
        
        Role role2 = store.getRoles().get( "mockRole2" );
        dependents = store.getDependentProfileNames( role2 );
        assertEquals( 3, dependents.size() );
        assertTrue( dependents.contains( "mockProfile1" ) );
        assertTrue( dependents.contains( "mockProfile2" ) );
        
//        StringPermission perm1 = new StringPermission(APP_NAME, "mockPerm1" );
//        assertTrue(store.getPermissions().implies(perm1));
//        dependents = store.getDependentProfileNames( perm1 );
//        assertEquals( 1, dependents.size() );
//
//        StringPermission perm7 = new StringPermission(APP_NAME,  "mockPerm7" );
//        assertTrue(store.getPermissions().implies(perm7));
//        dependents = store.getDependentProfileNames( perm7 );
//        assertEquals( 3, dependents.size() );
//        assertTrue( dependents.contains( "mockProfile3" ) );
//        assertTrue( dependents.contains( "mockProfile4" ) );
//
//        StringPermission perm0 = new StringPermission(APP_NAME,  "mockPerm0" );
//        assertTrue(store.getPermissions().implies(perm0));
//        dependents = store.getDependentProfileNames( perm0 );
//        assertEquals( 4, dependents.size() );
//        assertTrue( dependents.contains( "mockProfile2" ) );
//        assertTrue( dependents.contains( "mockProfile3" ) );
//        assertTrue( dependents.contains( "mockProfile4" ) );
    }
    
    
    public void testGetUserProfileIds() throws Exception
    {
        assertEquals( 5, this.store.getUserProfileIds( "akarasulu" ).size() );
        assertEquals( 0, this.store.getUserProfileIds( "trustin" ).size() );
    }


    public void testGetProfileIds() throws Exception
    {
        Set ids = new HashSet();
        for ( Iterator ii = this.store.getProfileIdIterator(); ii.hasNext(); /**/ )
        {
            ids.add( ii.next() );
        }
        assertEquals( 5, ids.size() );
        assertTrue( ids.contains( "mockProfile0" ) );
        assertTrue( ids.contains( "mockProfile1" ) );
        assertTrue( ids.contains( "mockProfile2" ) );
        assertTrue( ids.contains( "mockProfile3" ) );
        assertTrue( ids.contains( "mockProfile4" ) );
        assertFalse( ids.contains( "bogus" ) );
    }


    private InitialLdapContext getNewAppContext() throws NamingException
    {
        Hashtable env = new Hashtable();
        env.put( "java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory" );
        env.put( "java.naming.provider.url", "ldap://localhost:" +  super.getLdapPort()
            + "/appName=mockApplication,ou=applications,dc=example,dc=com" );
        env.put( "java.naming.security.principal", "uid=admin,ou=system" );
        env.put( "java.naming.security.credentials", "secret" );
        env.put( "java.naming.security.authentication", "simple" );
        return new InitialLdapContext( env, null );
    }
    
    
    public void testAddDelNotifications() throws Exception
    {
        // get a connection to the server to be used for alterations
        InitialLdapContext ctx = getNewAppContext();
        
        // prepare listener for notifications
        store.addPolicyListener( new TestListener() );
        Thread.sleep( 200 );
        
        // -------------------------------------------------------------------
        // Test StringPermission Addition and Notification
        // -------------------------------------------------------------------

//        Attributes attrs = new BasicAttributes( "objectClass", "policyPermission", true );
//        attrs.put( "permName", "mockPerm10" );
//        attrs.put( "description", "testValue" );
//        ctx.createSubcontext( "permName=mockPerm10,ou=permissions", attrs );

        // wait until the object is set or exit in 10 seconds
//        long startTime = System.currentTimeMillis();
//        long totalWaitTime = 0;
//        while ( totalWaitTime < 10000 )
//        {
//            synchronized( lockObject )
//            {
//                lockObject.wait( 200 );
//                if ( this.permission != null )
//                {
//                    break;
//                }
//                else
//                {
//                    totalWaitTime = System.currentTimeMillis() - startTime;
//                }
//            }
//        }

//        assertNull( this.profile );
//        assertNull( this.role );
//        assertNotNull( this.permission );
//        assertEquals( "mockPerm10", this.permission.getName() );
//        assertEquals( ChangeType.ADD, this.changeType );
//        assertEquals( "testValue", this.permission.getDescription() );
        
        // make sure that policy is updated with this new perm
//        assertEquals( this.permission, this.store.getPermissions().get( "mockPerm10" ) );
//        assertTrue(this.store.getPermissions().implies(this.permission));
//        this.permission = null;
//        this.changeType = null;
        
        // -------------------------------------------------------------------
        // Test StringPermission Deletion and Notification
        // -------------------------------------------------------------------

//        ctx.destroySubcontext( "permName=mockPerm10,ou=permissions" );
        
        // wait until the object is set or exit in 10 seconds
//        startTime = System.currentTimeMillis();
//        totalWaitTime = 0;
//        while ( totalWaitTime < 10000 )
//        {
//            synchronized( lockObject )
//            {
//                lockObject.wait( 200 );
//                if ( this.permission != null )
//                {
//                    break;
//                }
//                else
//                {
//                    totalWaitTime = System.currentTimeMillis() - startTime;
//                }
//            }
//        }
//
//        assertNull( this.profile );
//        assertNull( this.role );
//        assertNotNull( this.permission );
//        assertEquals( "mockPerm10", this.permission.getName() );
//        assertEquals( ChangeType.DEL, this.changeType );
//        assertEquals( "testValue", this.permission.getDescription() );
        
        // make sure that policy is updated with this new perm
//        assertNull( this.store.getPermissions().get( "mockPerm10" ) );
//        assertFalse(this.store.getPermissions().implies(this.permission));
//        this.permission = null;
//        this.changeType = null;

        // -------------------------------------------------------------------
        // Test Role Addition and Notification
        // -------------------------------------------------------------------

        Attributes attrs = new BasicAttributes( "objectClass", "policyRole", true );
        attrs.put( "roleName", "mockRole5" );
        attrs.put( "description", "testValue" );
        attrs.put( "grants", "mockPerm8" );
        ctx.createSubcontext( "roleName=mockRole5,ou=roles", attrs );

        // wait until the object is set or exit in 10 seconds
        long startTime = System.currentTimeMillis();
        long totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( this.role != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNull( this.profile );
        assertNull( this.permission );
        assertNotNull( this.role );

        assertEquals( "mockRole5", this.role.getName() );
        assertEquals( ChangeType.ADD, this.changeType );
        assertEquals( "testValue", this.role.getDescription() );
        assertTrue( role.hasPermission(new StringPermission("mockPerm8" )));
        assertFalse( role.hasPermission(new StringPermission("mockPerm1" )));
        
        // make sure that policy is updated with this new role
        assertEquals( this.role, this.store.getRoles().get( "mockRole5" ) );
        this.role = null;
        this.changeType = null;

        // -------------------------------------------------------------------
        // Test Role Deletions and Notification
        // -------------------------------------------------------------------

        ctx.destroySubcontext( "roleName=mockRole5,ou=roles" );

        // wait until the object is set or exit in 10 seconds
        startTime = System.currentTimeMillis();
        totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( this.role != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNull( this.profile );
        assertNull( this.permission );
        assertNotNull( this.role );

        assertEquals( "mockRole5", this.role.getName() );
        assertEquals( ChangeType.DEL, this.changeType );
        assertEquals( "testValue", this.role.getDescription() );
        assertTrue( role.hasPermission(new StringPermission("mockPerm8" )));
        assertFalse( role.hasPermission(new StringPermission("mockPerm1" )));
        
        // make sure that policy is updated with this new role
        assertNull( this.store.getRoles().get( "mockRole5" ) );
        this.role = null;
        this.changeType = null;

        // -------------------------------------------------------------------
        // Test Profile Addition and Notification
        // -------------------------------------------------------------------

        attrs = new BasicAttributes( "objectClass", "policyProfile", true );
        attrs.put( "profileId", "mockProfile5" );
        attrs.put( "description", "testValue" );
        attrs.put( "grants", "mockPerm8" );
        attrs.put( "user", "akarasulu" );
        ctx.createSubcontext( "profileId=mockProfile5,ou=profiles", attrs );

        // wait until the object is set or exit in 10 seconds
        startTime = System.currentTimeMillis();
        totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( this.profile != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNull( this.role );
        assertNull( this.permission );
        assertNotNull( this.profile );

        assertEquals( "mockProfile5", this.profile.getProfileId() );
        assertEquals( ChangeType.ADD, this.changeType );
        assertEquals( "testValue", this.profile.getDescription() );
        assertTrue( profile.implies( new StringPermission("mockPerm8" )));
        assertFalse( profile.implies( new StringPermission("mockPerm1" )));

        // -------------------------------------------------------------------
        // Test Profile Deletion and Notification
        // -------------------------------------------------------------------

        ctx.destroySubcontext( "profileId=mockProfile5,ou=profiles" );

        // wait until the object is set or exit in 10 seconds
        startTime = System.currentTimeMillis();
        totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( this.profile != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNull( this.role );
        assertNull( this.permission );
        assertNotNull( this.profile );

        assertEquals( "mockProfile5", this.profile.getProfileId() );
        assertEquals( ChangeType.DEL, this.changeType );
        assertEquals( "testValue", this.profile.getDescription() );
        assertTrue( profile.implies( new StringPermission("mockPerm8" )));
        assertFalse( profile.implies( new StringPermission("mockPerm1" )));
    }

    
    public void testModifyNotifications() throws Exception
    {
        // get a connection to the server to be used for alterations
        InitialLdapContext ctx = getNewAppContext();
        
        // prepare listener for notifications
        store.addPolicyListener( new TestListener() );
        Thread.sleep( 200 );
        
        // -------------------------------------------------------------------
        // Test Profile Alteration and Notification
        // -------------------------------------------------------------------

        ctx.modifyAttributes( "profileId=mockProfile3,ou=profiles", new ModificationItem[] {
            new ModificationItem( DirContext.ADD_ATTRIBUTE, 
                new BasicAttribute( "description", "testValue" ) ),
            new ModificationItem( DirContext.REPLACE_ATTRIBUTE, 
                new BasicAttribute( "grants", "mockPerm1" ) )
        } );
        
        // wait until the object is set or exit in 10 seconds
        long startTime = System.currentTimeMillis();
        long totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( profile != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNotNull( profile );
        assertEquals( "mockProfile3", profile.getProfileId() );
        assertEquals( ChangeType.MODIFY, changeType );
        assertEquals( "testValue", profile.getDescription() );
        assertTrue( profile.getGrants().implies( new StringPermission("mockPerm1" )));
        assertFalse( profile.getGrants().implies( new StringPermission("mockPerm0" )));
        assertFalse( profile.getGrants().implies( new StringPermission("mockPerm7" )));
        profile = null;
        changeType = null;
        
        // -------------------------------------------------------------------
        // Test Role Alteration and Notification
        // -------------------------------------------------------------------

        ctx.modifyAttributes( "roleName=mockRole1,ou=roles", new ModificationItem[] {
            new ModificationItem( DirContext.ADD_ATTRIBUTE, 
                new BasicAttribute( "description", "testValue" ) ),
            new ModificationItem( DirContext.REPLACE_ATTRIBUTE, 
                new BasicAttribute( "grants", "mockPerm1" ) )
        } );
        
        // wait until the object is set or exit in 10 seconds
        startTime = System.currentTimeMillis();
        totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( role != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNull( profile );
        assertNotNull( role );
        assertEquals( "mockRole1", role.getName() );
        assertEquals( ChangeType.MODIFY, changeType );
        assertEquals( "testValue", role.getDescription() );
        assertTrue( role.getGrantedPermissions().implies( new StringPermission("mockPerm1" )));
        assertFalse( role.getGrantedPermissions().implies( new StringPermission("mockPerm0" )));
        
        // make sure that policy is updated with this changed role
        assertEquals( role, store.getRoles().get( "mockRole1" ) );
        this.role = null;
        this.changeType = null;
        
        // -------------------------------------------------------------------
        // Test StringPermission Alteration and Notification
        // -------------------------------------------------------------------

//        ctx.modifyAttributes( "permName=mockPerm1,ou=permissions", new ModificationItem[] {
//            new ModificationItem( DirContext.ADD_ATTRIBUTE,
//                new BasicAttribute( "description", "testValue" ) )
//        } );
//
//        // wait until the object is set or exit in 10 seconds
//        startTime = System.currentTimeMillis();
//        totalWaitTime = 0;
//        while ( totalWaitTime < 10000 )
//        {
//            synchronized( lockObject )
//            {
//                lockObject.wait( 200 );
//                if ( this.permission != null )
//                {
//                    break;
//                }
//                else
//                {
//                    totalWaitTime = System.currentTimeMillis() - startTime;
//                }
//            }
//        }
//
//        assertNull( this.profile );
//        assertNull( this.role );
//        assertNotNull( this.permission );
//        assertEquals( "mockPerm1", this.permission.getName() );
//        assertEquals( ChangeType.MODIFY, this.changeType );
//        assertEquals( "testValue", this.permission.getDescription() );
//
//        // make sure that policy is updated with this changed perm
//        assertTrue( this.store.getPermissions().implies(this.permission) );
//        assertTrue( this.store.getRoles().get( "mockRole1" ).getGrantedPermissions().implies(this.permission) );
//        assertTrue( this.store.getRoles().get( "mockRole2" ).getGrantedPermissions().implies(this.permission) );
//        assertFalse( this.store.getRoles().get( "mockRole0" ).getGrantedPermissions().implies(this.permission) );
//        assertFalse( this.store.getRoles().get( "mockRole3" ).getGrantedPermissions().implies(this.permission) );
//        assertFalse( this.store.getRoles().get( "mockRole4" ).getGrantedPermissions().implies(this.permission) );
        
        ctx.close();
    }
    
    
    public void testRenameNotifications() throws Exception
    {
        // get a connection to the server to be used for alterations
        InitialLdapContext ctx = getNewAppContext();
        
        // prepare listener for notifications
        store.addPolicyListener( new TestListener() );
        Thread.sleep( 200 );
        
        // -------------------------------------------------------------------
        // Test Profile Rename and Notification
        // -------------------------------------------------------------------

        ctx.rename( "profileId=mockProfile3,ou=profiles", "profileId=renamed,ou=profiles" );
        
        // wait until the object is set or exit in 10 seconds
        long startTime = System.currentTimeMillis();
        long totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( profile != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNotNull( profile );
        assertEquals( "renamed", profile.getProfileId() );
        assertNotNull( originalName );
        assertEquals( "mockProfile3", originalName );
        profile = null;
        originalName = null;
        
        // -------------------------------------------------------------------
        // Test Role Rename and Notification
        // -------------------------------------------------------------------

        assertNotNull( store.getRoles().get( "mockRole0" ) );
        assertNull( store.getRoles().get( "renamed" ) );
        ctx.rename( "roleName=mockRole0,ou=roles", "roleName=renamed,ou=roles" );
        
        // wait until the object is set or exit in 10 seconds
        startTime = System.currentTimeMillis();
        totalWaitTime = 0;
        while ( totalWaitTime < 10000 )
        {
            synchronized( lockObject )
            {
                lockObject.wait( 200 );
                if ( role != null )
                {
                    break;
                }
                else
                {
                    totalWaitTime = System.currentTimeMillis() - startTime;
                }
            }
        }

        assertNull( profile );
        assertNull( permission );
        assertNull( store.getRoles().get( "mockRole0" ) );
        assertNotNull( store.getRoles().get( "renamed" ) );
        assertNotNull( role );
        assertEquals( "renamed", role.getName() );
        assertNotNull( originalName );
        assertEquals( "mockRole0", originalName );
        role = null;
        originalName = null;

        // -------------------------------------------------------------------
        // Test StringPermission Rename and Notification
        // -------------------------------------------------------------------

//        Attributes attrs = new BasicAttributes( "objectClass", "policyPermission", true );
//        attrs.put( "permName", "mockPerm10" );
//        attrs.put( "description", "testValue" );
//        ctx.createSubcontext( "permName=mockPerm10,ou=permissions", attrs );
//        ctx.rename( "permName=mockPerm10,ou=permissions", "permName=renamed,ou=permissions" );
//
//        // wait until the object is set or exit in 10 seconds
//        startTime = System.currentTimeMillis();
//        totalWaitTime = 0;
//        while ( totalWaitTime < 10000 )
//        {
//            synchronized( lockObject )
//            {
//                lockObject.wait( 250 );
//                if ( permission != null )
//                {
//                    break;
//                }
//                else
//                {
//                    totalWaitTime = System.currentTimeMillis() - startTime;
//                }
//            }
//        }
//
//        assertNull( profile );
//        assertNull( role );
//        assertNotNull( permission );
//        assertTrue( store.getPermissions().implies(permission) );
//        assertEquals( "renamed", permission.getName() );
//        assertNotNull( originalName );
//        assertEquals( "mockPerm10", originalName );
    }


    class TestListener implements PolicyChangeListener
    {
        public void roleChanged( ApplicationPolicy policy, Role role, ChangeType changeType )
        {
            synchronized( lockObject )
            {
                LdapApplicationPolicyIntegrationTest.this.role = role;
                LdapApplicationPolicyIntegrationTest.this.changeType = changeType;
                LdapApplicationPolicyIntegrationTest.this.lockObject.notifyAll();
            }
        }

        public void roleRenamed( ApplicationPolicy policy, Role role, String oldName )
        {
            synchronized( lockObject )
            {
                LdapApplicationPolicyIntegrationTest.this.originalName = oldName;
                LdapApplicationPolicyIntegrationTest.this.role = role;
                LdapApplicationPolicyIntegrationTest.this.lockObject.notifyAll();
            }
        }

        public void permissionChanged( ApplicationPolicy policy, StringPermission permission, ChangeType changeType )
        {
            synchronized( lockObject )
            {
                LdapApplicationPolicyIntegrationTest.this.permission = permission;
                LdapApplicationPolicyIntegrationTest.this.changeType = changeType;
                LdapApplicationPolicyIntegrationTest.this.lockObject.notifyAll();
            }
        }

        public void permissionRenamed( ApplicationPolicy policy, StringPermission permission, String oldName )
        {
            synchronized( lockObject )
            {
                LdapApplicationPolicyIntegrationTest.this.originalName = oldName;
                LdapApplicationPolicyIntegrationTest.this.permission = permission;
                LdapApplicationPolicyIntegrationTest.this.lockObject.notifyAll();
            }
        }

        public void profileChanged( ApplicationPolicy policy, Profile profile, ChangeType changeType )
        {
            synchronized( lockObject )
            {
                LdapApplicationPolicyIntegrationTest.this.profile = profile;
                LdapApplicationPolicyIntegrationTest.this.changeType = changeType;
                LdapApplicationPolicyIntegrationTest.this.lockObject.notifyAll();
            }
        }

        public void profileRenamed( ApplicationPolicy policy, Profile profile, String oldName )
        {
            synchronized( lockObject )
            {
                LdapApplicationPolicyIntegrationTest.this.originalName = oldName;
                LdapApplicationPolicyIntegrationTest.this.profile = profile;
                LdapApplicationPolicyIntegrationTest.this.lockObject.notifyAll();
            }
        }
    }
}
                                                     