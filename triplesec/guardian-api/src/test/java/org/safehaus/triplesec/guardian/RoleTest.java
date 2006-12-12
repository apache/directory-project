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


import java.security.AccessControlException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;



/**
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 74 $
 */
public class RoleTest extends AbstractEntityTest
{
    private static final ApplicationPolicy STORE1 = new TestApplicationPolicyStore(
            "app1" );

    private static final ApplicationPolicy STORE2 = new TestApplicationPolicyStore(
            "app2" );

    protected Object newInstanceA1()
    {
        return new Role( STORE1, "role1", null );
    }

    protected Object newInstanceA2()
    {
        return new Role( STORE1, "role1", null );
    }

    protected Object newInstanceB1()
    {
        return new Role( STORE1, "role2", null );
    }

    protected Object newInstanceB2()
    {
        return new Role( STORE2, "role1", null );
    }

    public void testInstantiation()
    {
        Permissions perms = new Permissions( "app1", null );

        // Test null parameters
        try
        {
            new Role( null, "role1", perms );
            fail( "Execption is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }
        try
        {
            new Role( STORE1, null, perms );
            fail( "Execption is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }

        // Test empty fields
        try
        {
            new Role( STORE2, "", perms );
            fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        try
        {
            new Role( new TestApplicationPolicyStore( "" ), "role1", perms );
            fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        
        // Test unknown permissions
        try
        {
            Permissions wrongPerms = new Permissions( "app1", new Permission[] {
                    new Permission( "app1", "wrongPerm" ),
            });
                                                                             
            new Role( STORE1, "role1", wrongPerms );
            fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        

        // Test mismatching application names.
        try
        {
            new Role( STORE2, "role1", perms );
            fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }

        Role r = new Role( STORE1, "role1", null );
        assertEquals( 0, r.getGrants().size() );
    }

    public void testProperties()
    {
        Permission perm1= new Permission( "app1", "perm1" );
        Permissions perms = new Permissions( "app1", new Permission[] {
                perm1,
                new Permission( "app1", "perm2" ),
                new Permission( "app1", "perm3" ), } );

        Role r = new Role( STORE1, "role1", perms, "test description" );
        assertEquals( "app1", r.getApplicationName() );
        assertEquals( "role1", r.getName() );
        assertEquals( perms, r.getGrants() );
        assertEquals( "test description", r.getDescription() );
        assertTrue( r.hasPermission( perm1 ) ) ;
        assertTrue( r.hasPermission( perm1.getName() ) ) ;
    }

    public void testRolePermissions()
    {
        Permission perm = new Permission( "app1", "perm1" );
        Permission wrongPerm = new Permission( "app1", "perm2" );
        Permissions perms = new Permissions( "app1", new Permission[] { perm, } );

        Role r = new Role( STORE1, "role1", perms );

        // Check existing permissions
        r.checkPermission( perm );
        assertTrue( r.hasPermission( perm.getName() ) );
        assertTrue( r.hasPermission( perm ) );
        r.checkPermission( perm, "unused" );
        r.checkPermission( perm.getName() );
        r.checkPermission( perm.getName(), "unused" );

        // Check null parameters
        try
        {
            r.checkPermission( ( Permission ) null );
            fail( "Exception is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }
        try
        {
            r.checkPermission( ( String ) null );
            fail( "Exception is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }
        try
        {
            r.checkPermission( ( Permission ) null, "unused" );
            fail( "Exception is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }
        try
        {
            r.checkPermission( ( String ) null, "unused" );
            fail( "Exception is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }

        // Check non-existing permissions
        try
        {
            r.checkPermission( wrongPerm );
            fail( "Exception is not thrown." );
        }
        catch( AccessControlException e )
        {
            // OK
        }
        try
        {
            r.checkPermission( wrongPerm, "unused" );
            fail( "Exception is not thrown." );
        }
        catch( AccessControlException e )
        {
            // OK
        }
        try
        {
            r.checkPermission( wrongPerm.getName() );
            fail( "Exception is not thrown." );
        }
        catch( AccessControlException e )
        {
            // OK
        }
        try
        {
            r.checkPermission( wrongPerm.getName(), "unused" );
            fail( "Exception is not thrown." );
        }
        catch( AccessControlException e )
        {
            // OK
        }
    }
    
    
    
    protected void _testClone( Object a, Object b )
    {
        Role ra = ( Role ) a;
        Role rb = ( Role ) b;
        assertEquals( ra.getGrants(), rb.getGrants() );
    }

    private static class TestApplicationPolicyStore implements
            ApplicationPolicy
    {
        private final String appName;

        public TestApplicationPolicyStore( String appName )
        {
            this.appName = appName;
        }

        public String getApplicationName()
        {
            return appName;
        }

        public Roles getRoles()
        {
            return null;
        }

        public Permissions getPermissions()
        {
            Permission[] perms = new Permission[] {
                    new Permission( appName, "perm1" ),
                    new Permission( appName, "perm2" ),
                    new Permission( appName, "perm3" ),
            };
            return new Permissions( appName, perms );
        }

        public Profile getProfile( String userName )
        {
            return null;
        }

        public String getDescription()
        {
            return null;
        }

        public void close() {}

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

        public Set getDependentProfileNames( Permission permission ) throws GuardianException
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
}
