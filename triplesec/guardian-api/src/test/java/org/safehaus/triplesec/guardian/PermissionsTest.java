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

import java.security.Permission;
import java.security.Permissions;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;


/**
 * @author Trustin Lee
 * @version $Rev: 52 $, $Date: 2005-08-19 23:03:36 -0400 (Fri, 19 Aug 2005) $
 */
public class PermissionsTest extends AbstractEntityTest {
    protected Object newInstanceA1() {
        return newPermissions(new StringPermission[]{
                new StringPermission("perm1"),
                new StringPermission("perm2"),
                new StringPermission("perm3"),
        });
    }

    private Permissions newPermissions(Permission[] permissions) {
        Permissions perms = new Permissions();
        for (Permission perm : permissions) {
            perms.add(perm);
        }
        return perms;
    }

    protected Object newInstanceA2() {
        return newPermissions(new StringPermission[]{
                new StringPermission("perm1"),
                new StringPermission("perm2"),
                new StringPermission("perm3"),
        });
    }

    protected Object newInstanceB1() {
        return newPermissions(new StringPermission[]{
                new StringPermission("perm1"),
        });
    }

    protected Object newInstanceB2() {
        return newPermissions(new StringPermission[0]);
    }

    public void testEquals() {
        assertTrue(PermissionsUtil.equivalent((Permissions) a1, (Permissions) a1));
        assertTrue(PermissionsUtil.equivalent((Permissions) a1, (Permissions) a2));
//        assertFalse(a1.equals(null));
        assertFalse(PermissionsUtil.equivalent((Permissions) a1, (Permissions) b1));
        assertFalse(PermissionsUtil.equivalent((Permissions) a1, (Permissions) b2));
//        assertFalse(a1.equals(wrong));
    }

    public void testHashCode()
    {
        //we can't affect Permissions.hashCode()
    }

    public void testClone() throws Exception
    {
        //Permissions is not cloneable
    }


    public void testInstantiation() {
        // Test null values
//        try
//        {
//            new Permissions( null, null );
//            Assert.fail( "Execption is not thrown." );
//        }
//        catch( NullPointerException e )
//        {
//            // OK
//        }

        // Test empty values
//        try
//        {
//            new Permissions( "", null );
//            Assert.fail( "Execption is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
//            // OK
//        }

        // Test null elements
//        Permissions perms = newPermissions(new StringPermission[] {
//                null, null, null,
//        });
//        Assert.assertTrue( PermissionsUtil.isEmpty(perms) );

        // Test mismatching application names
//        try
//        {
//            newPermissions(new StringPermission[] {
//                    new StringPermission( "app2", "perm1" ),
//            });
//            Assert.fail( "Execption is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
//            //OK
//        }

//        Assert.assertTrue( PermissionsUtil.isEmpty(perms) );
    }

    public void testProperties() {
        StringPermission p1 = new StringPermission("perm1");
        StringPermission p2 = new StringPermission("perm2");
        StringPermission p3 = new StringPermission("perm3");
        Permissions perms = newPermissions(new StringPermission[]{
                p1, p2, p3,
        });

//        Assert.assertEquals( "app1", perms.getApplicationName() );
        Assert.assertEquals(3, PermissionsUtil.size(perms));
        Assert.assertTrue(perms.implies(p1));
        Assert.assertTrue(perms.implies(p2));
        Assert.assertTrue(perms.implies(p3));

        // Test iterator integrity
        Set allPerms = new HashSet();
        allPerms.add(p1);
        allPerms.add(p2);
        allPerms.add(p3);
        for (Enumeration<Permission> i = perms.elements(); i.hasMoreElements();) {
            StringPermission p = (StringPermission) i.nextElement();
            Assert.assertTrue(allPerms.contains(p));
            allPerms.remove(p);
        }
    }

    public void testSetOperations() {
        Permissions perms1 = newPermissions(new StringPermission[]{
                new StringPermission("perm1"),
        });
        Permissions perms2 = newPermissions(new StringPermission[]{
                new StringPermission("perm2"),
        });
        Permissions perms12 = newPermissions(new StringPermission[]{
                new StringPermission("perm1"),
                new StringPermission("perm2"),
        });
        Permissions wrongPerms = new Permissions();

        // addAll
        Assert.assertTrue(PermissionsUtil.equivalent(perms12, PermissionsUtil.union(perms1, perms2)));
        Assert.assertTrue(PermissionsUtil.equivalent(perms1, PermissionsUtil.union(perms1, perms1)));
//        try
//        {
//            PermissionsUtil.union(perms1, wrongPerms );
//            Assert.fail( "Exception is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
//            // OK
//        }

        // removeAll
//        Assert.assertEquals( perms1, perms12.removeAll( perms2 ) );
//        Assert.assertEquals( perms1, perms1.removeAll( perms2 ) );
//        try
//        {
//            perms1.removeAll( wrongPerms );
//            Assert.fail( "Exception is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
        // OK
//        }

        // retainAll
//        Assert.assertEquals( perms1, perms12.retainAll( perms1 ) );
//        Assert.assertEquals(
//                new Permissions( "app1", null ), perms1.retainAll( perms2 ) );
//        try
//        {
//            perms1.retainAll( wrongPerms );
//            Assert.fail( "Exception is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
        // OK
//        }

        // containsAll
//        Assert.assertTrue( perms12.containsAll( perms12 ) );
//        Assert.assertFalse( perms1.containsAll( perms12 ) );
//        try
//        {
//            perms1.containsAll( wrongPerms );
//            Assert.fail( "Exception is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
//             OK
//        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PermissionsTest.class);
    }

}
