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
import java.security.Permissions;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


import junit.framework.Assert;

/**
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev: 72 $
 */
public class ProfileTest extends AbstractEntityTest {
    private static final ApplicationPolicy STORE1 = new TestApplicationPolicyStore(
            "app1");

    private static final ApplicationPolicy STORE2 = new TestApplicationPolicyStore(
            "app2");

    protected Object newInstanceA1() {
        return new Profile(STORE1, "trustin", "trustin", null, null, null, false);
    }

    protected Object newInstanceA2() {
        return new Profile(STORE1, "trustin", "trustin", null, null, null, false);
    }

    protected Object newInstanceB1() {
        return new Profile(STORE1, "alex", "alex", null, null, null, false);
    }

    protected Object newInstanceB2() {
        return new Profile(STORE2, "trustin", "trustin", null, null, null, false);
    }

    public void testInstantiation() {
        StringPermission perm1 = new StringPermission("perm1");
        Roles roles = new Roles("app1", new Role[]{
                new Role(STORE1, "role1", newPermissions(perm1), null),
        });
        Permissions grants = newPermissions(perm1);
        StringPermission perm2 = new StringPermission("perm2");
        Permissions denials = newPermissions(perm2);

        // Test null parameters
        try {
            new Profile(null, "trustin", "trustin", roles, grants, denials, false);
            Assert.fail("Execption is not thrown.");
        }
        catch (NullPointerException e) {
            // OK
        }
        try {
            new Profile(STORE1, null, "trustin", roles, grants, denials, false);
            Assert.fail("Execption is not thrown.");
        }
        catch (NullPointerException e) {
            // OK
        }

        // Test empty fields
        try {
            new Profile(STORE1, "", "trustin", roles, grants, denials, false);
            Assert.fail("Execption is not thrown.");
        }
        catch (IllegalArgumentException e) {
            // OK
        }
        try {
            new Profile(new TestApplicationPolicyStore(""), "role1", "trustin", roles, grants, denials, false);
            Assert.fail("Execption is not thrown.");
        }
        catch (IllegalArgumentException e) {
            // OK
        }

        // Test unknown permissions
        //TODO could be resuscitated if we implement impliesAll
//        Permissions wrongPerms = new Permissions( "app1", new StringPermission[] {
//                new StringPermission( "app1", "wrongPerm" ),
//        });
//        try
//        {
//
//            new Profile( STORE1, "trustin", "trustin", roles, wrongPerms, denials, false );
//            Assert.fail( "Execption is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
//            // OK
//        }
//        try
//        {
//
//            new Profile( STORE1, "trustin", "trustin", roles, grants, wrongPerms, false );
//            Assert.fail( "Execption is not thrown." );
//        }
//        catch( IllegalArgumentException e )
//        {
//            // OK
//        }

        // Test mismatching application names.
        try {
            new Profile(STORE2, "role1", "trustin", roles, null, null, false);
            Assert.fail("Execption is not thrown.");
        }
        catch (IllegalArgumentException e) {
            // OK
        }
//        try {
//            new Profile(STORE2, "role1", "trustin", null, grants, null, false);
//            Assert.fail("Execption is not thrown.");
//        }
//        catch (IllegalArgumentException e) {
//            // OK
//        }
//        try {
//            new Profile(STORE2, "role1", "trustin", null, null, denials, false);
//            Assert.fail("Execption is not thrown.");
//        }
//        catch (IllegalArgumentException e) {
//            // OK
//        }

        Profile p = new Profile(STORE1, "role1", "trustin", null, null, null, false);
        Assert.assertEquals(0, p.getRoles().size());
        Assert.assertEquals(0, PermissionsUtil.size(p.getGrants()));
        Assert.assertEquals(0, PermissionsUtil.size(p.getDenials()));
        assertEquals("trustin", p.getUserName());
    }

    private Permissions newPermissions(StringPermission perm1) {
        Permissions permissions = new Permissions();
        permissions.add(perm1);
        return permissions;
    }

    public void testProperties() {
        Permissions permissions = new Permissions();
        permissions.add(new StringPermission("perm2"));
        permissions.add(new StringPermission("perm3"));
        permissions.add(new StringPermission("perm4"));
        Roles roles = new Roles("app1", new Role[]{
                new Role(STORE1, "role1", permissions, null),
        });
        Permissions grants = new Permissions();
        grants.add(new StringPermission("perm1"));
        grants.add(new StringPermission("perm2"));

        Permissions denials = newPermissions(new StringPermission("perm3"));

        Profile p = new Profile(STORE1, "trustin", "trustin", roles, grants, denials, "test description", false);
        assertEquals("app1", p.getApplicationName());
        assertEquals("trustin", p.getProfileId());
        assertEquals(roles, p.getRoles());
        assertEquals(grants, p.getGrants());
        assertEquals(denials, p.getDenials());
        assertEquals("test description", p.getDescription());

        Permissions effectivePermissions = new Permissions();
        effectivePermissions.add(new StringPermission("perm1"));
        effectivePermissions.add(new StringPermission("perm2"));
        effectivePermissions.add(new StringPermission("perm3"));
        effectivePermissions.add(new StringPermission("perm4"));
        assertTrue(PermissionsUtil.equivalent(effectivePermissions, p.getEffectiveGrantedPermissions()));
        assertTrue(PermissionsUtil.equivalent(denials, p.getEffectiveDeniedPermissions()));

        assertTrue(p.isInRole("role1"));
    }

    public void testRolePermissions() {
        StringPermission perm = new StringPermission("perm1");
        StringPermission wrongPerm = new StringPermission("perm2");
        Permissions perms = newPermissions(perm);

        // Effective permissions will be: 'perm1'
        Profile p = new Profile(
                STORE1, "trustin", "trustin",
                new Roles("app1", null),
                perms, null, false);

        // Check existing permissions
        p.checkPermission(perm);
        p.checkPermission(perm, "unused");
        assertTrue(p.implies(perm));
        assertFalse(p.implies(new StringPermission("nonexistant")));

        // Check null parameters
        try {
            p.checkPermission((StringPermission) null);
            Assert.fail("Exception is not thrown.");
        }
        catch (NullPointerException e) {
            // OK
        }
        try {
            p.checkPermission((StringPermission) null, "unused");
            Assert.fail("Exception is not thrown.");
        }
        catch (NullPointerException e) {
            // OK
        }
        // Check non-existing permissions
        try {
            p.checkPermission(wrongPerm);
            Assert.fail("Exception is not thrown.");
        }
        catch (AccessControlException e) {
            // OK
        }
        try {
            p.checkPermission(wrongPerm, "unused");
            Assert.fail("Exception is not thrown.");
        }
        catch (AccessControlException e) {
            // OK
        }
    }


    protected void _testClone(Object a, Object b) {
        Profile pa = (Profile) a;
        Profile pb = (Profile) b;
        Assert.assertEquals(pa.getRoles(), pb.getRoles());
        Assert.assertEquals(pa.getGrants(), pb.getGrants());
        Assert.assertEquals(pa.getDenials(), pb.getDenials());
    }

    private static class TestApplicationPolicyStore implements
            ApplicationPolicy {
        private final String appName;

        public TestApplicationPolicyStore(String appName) {
            this.appName = appName;
        }

        public String getApplicationName() {
            return appName;
        }

        public Roles getRoles() {
            return null;
        }

        public Permissions getPermissions() {
            Permissions perms = new Permissions();
            perms.add(new StringPermission("perm1"));
            perms.add(new StringPermission("perm2"));
            perms.add(new StringPermission("perm3"));
            perms.add(new StringPermission("perm4"));
            return perms;
        }

        public Profile getProfile(String userName) {
            return null;
        }


        public String getDescription() {
            return null;
        }


        public void close() {
        }

        public boolean removePolicyListener(PolicyChangeListener listener) {
            return false;
        }

        public boolean addPolicyListener(PolicyChangeListener listener) {
            return false;
        }

        public Set getDependentProfileNames(Role role) throws GuardianException {
            return null;
        }

        public Set getDependentProfileNames(StringPermission permission) throws GuardianException {
            return null;
        }

        public Set getUserProfileIds(String userName) throws GuardianException {
            return Collections.EMPTY_SET;
        }

        public Iterator getProfileIdIterator() throws GuardianException {
            return null;
        }

        public Profile getAdminProfile() {
            return null;
        }
    }
}
