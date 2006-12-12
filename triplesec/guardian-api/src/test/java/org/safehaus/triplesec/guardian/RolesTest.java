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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import junit.framework.Assert;


/**
 * 
 *
 * @author Trustin Lee
 * @version $Rev: 72 $, $Date: 2005-11-07 21:37:46 -0500 (Mon, 07 Nov 2005) $
 */
public class RolesTest extends AbstractEntityTest
{
    private static final ApplicationPolicy STORE1 = new TestApplicationPolicyStore(
            "app1" );

    private static final ApplicationPolicy STORE2 = new TestApplicationPolicyStore(
            "app2" );

    protected Object newInstanceA1()
    {
        return new Roles( "app1", new Role[] {
                new Role( STORE1, "role1", null ),
                new Role( STORE1, "role2", null ),
                new Role( STORE1, "role3", null ),
        });
    }

    protected Object newInstanceA2()
    {
        return new Roles( "app1", new Role[] {
                new Role( STORE1, "role1", null ),
                new Role( STORE1, "role2", null ),
                new Role( STORE1, "role3", null ),
        });
    }

    protected Object newInstanceB1()
    {
        return new Roles( "app1", new Role[] {
                new Role( STORE1, "role1", null ),
        });
    }

    protected Object newInstanceB2()
    {
        return new Roles( "app2", null );
    }
    
    public void testInstantiation()
    {
        // Test null values
        try
        {
            new Roles( null, null );
            Assert.fail( "Execption is not thrown." );
        }
        catch( NullPointerException e )
        {
            // OK
        }
        
        // Test empty values
        try
        {
            new Roles( "", null );
            Assert.fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        
        // Test null elements
        Roles roles = new Roles( "app1", new Role[] {
                null, null, null,
        });
        Assert.assertTrue( roles.isEmpty() );
        
        // Test mismatching application names
        try
        {
            new Roles( "app1", new Role[] {
                    new Role( STORE2, "role1", null ),
            });
            Assert.fail( "Execption is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            //OK
        }
        
        Assert.assertTrue( roles.isEmpty() );
    }
    
    public void testProperties()
    {
        Role r1 = new Role( STORE1, "role1", null );
        Role r2 = new Role( STORE1, "role2", null );
        Role r3 = new Role( STORE1, "role3", null );
        Roles roles = new Roles( "app1", new Role[] {
                r1, r2, r3,
        });
        
        Assert.assertEquals( "app1", roles.getApplicationName() );
        Assert.assertEquals( 3, roles.size() );
        Assert.assertTrue( roles.contains( r1 ) );
        Assert.assertTrue( roles.contains( r2 ) );
        Assert.assertTrue( roles.contains( r3 ) );
        Assert.assertTrue( roles.contains( r1.getName() ) );
        Assert.assertTrue( roles.contains( r2.getName() ) );
        Assert.assertTrue( roles.contains( r3.getName() ) );
        Assert.assertEquals( r1, roles.get( r1.getName() ) );
        Assert.assertEquals( r2, roles.get( r2.getName() ) );
        Assert.assertEquals( r3, roles.get( r3.getName() ) );
        
        // Test iterator integrity
        Set allRoles = new HashSet();
        allRoles.add( r1 );
        allRoles.add( r2 );
        allRoles.add( r3 );
        for( Iterator i = roles.iterator(); i.hasNext(); )
        {
            Role p = ( Role ) i.next();
            Assert.assertTrue( allRoles.contains( p ) );
            allRoles.remove( p );
        }
    }
    
    public void testSetOperations()
    {
        Roles roles1 = new Roles( "app1", new Role[] {
                new Role( STORE1, "role1", null ),
        });
        Roles roles2 = new Roles( "app1", new Role[] {
                new Role( STORE1, "role2", null ),
        });
        Roles roles12 = new Roles( "app1", new Role[] {
                new Role( STORE1, "role1", null ),
                new Role( STORE1, "role2", null ),
        });
        Roles wrongRoles = new Roles( "wrongApp", null );
        
        
        // addAll
        Assert.assertEquals( roles12, roles1.addAll( roles2 ) );
        Assert.assertEquals( roles1, roles1.addAll( roles1 ) );
        try
        {
            roles1.addAll( wrongRoles );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        
        // removeAll
        Assert.assertEquals( roles1, roles12.removeAll( roles2 ) );
        Assert.assertEquals( roles1, roles1.removeAll( roles2 ) );
        try
        {
            roles1.removeAll( wrongRoles );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
        
        // retainAll
        Assert.assertEquals( roles1, roles12.retainAll( roles1 ) );
        Assert.assertEquals(
                new Roles( "role1", null ), roles1.retainAll( roles2 ) );
        try
        {
            roles1.retainAll( wrongRoles );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }

        // containsAll
        Assert.assertTrue( roles12.containsAll( roles12 ) );
        Assert.assertFalse( roles1.containsAll( roles12 ) );
        try
        {
            roles1.containsAll( wrongRoles );
            Assert.fail( "Exception is not thrown." );
        }
        catch( IllegalArgumentException e )
        {
            // OK
        }
    }
    
    
    public void testGetDependentRoles()
    {
        Role role1 = new Role( STORE1, "role1", STORE1.getPermissions() );
        Role role2 = new Role( STORE1, "role2", null );
        Roles roles12 = new Roles( "app1", new Role[] { role1, role2 });

        Roles dependents = roles12.getDependentRoles( "perm1" );
        assertEquals( 1, dependents.size() );
        assertEquals( role1, dependents.get( "role1" ) );
        
        dependents = roles12.getDependentRoles( STORE1.getPermissions().get( "perm1" ) );
        assertEquals( 1, dependents.size() );
        assertEquals( role1, dependents.get( "role1" ) );

        dependents = roles12.getDependentRoles( "perm99" );
        assertEquals( 0, dependents.size() );

        dependents = roles12.getDependentRoles( new Permission( "app1", "perm99" ) );
        assertEquals( 0, dependents.size() );
        
        try
        {
            dependents = roles12.getDependentRoles( new Permission( "blah", "perm99" ) );
            fail( "Should never get here due to an exception" );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }
    
    
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( RolesTest.class );
    }

    private static class TestApplicationPolicyStore implements ApplicationPolicy
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
