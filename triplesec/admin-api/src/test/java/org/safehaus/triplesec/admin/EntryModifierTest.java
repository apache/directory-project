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
package org.safehaus.triplesec.admin;


import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;


public class EntryModifierTest extends TestCase
{
    public void testPermissionModifier0()
    {
        PermissionModifier modifier = new PermissionModifier( null, "testApp", "testPerm" );
        assertFalse( modifier.isUpdateNeeded() );
        assertFalse( modifier.isUpdatableEntry() ); 
        assertTrue( modifier.isNewEntry() );

        modifier.setDescription( "description test" );
        assertTrue( modifier.isUpdateNeeded() );
    }

    
    public void testPermissionModifier1()
    {
        Permission archetype = new Permission( null, null, null, "", "archetypePerm", "first description" );
        PermissionModifier modifier = new PermissionModifier( null, archetype );
        assertFalse( modifier.isUpdateNeeded() );
        assertTrue( modifier.isUpdatableEntry() );
        assertFalse( modifier.isNewEntry() );
        
        modifier.setDescription( "description test" );
        assertTrue( modifier.isUpdateNeeded() );
    }

    
    public void testRoleModifier0()
    {
        RoleModifier modifier = new RoleModifier( null, "testApp", "testRole" );
        assertFalse( modifier.isUpdateNeeded() );
        assertFalse( modifier.isUpdatableEntry() );
        assertTrue( modifier.isNewEntry() );
        
        // change the description
        modifier.setDescription( "description test" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.setDescription( null );
        assertFalse( modifier.isUpdateNeeded() );
        
        // add some grants
        modifier.addGrant( "testPerm0" );
        modifier.removeGrant( "testPerm0" );
        assertFalse( modifier.isUpdateNeeded() );
    }
    
    
    public void testRoleModifier1()
    {
        Set grants = new HashSet();
        grants.add( "bend" );
        grants.add( "fold" );
        grants.add( "spindle" );
        Role archetype = new Role( null, null, null, "testApp", "testRole", null, grants );
        
        RoleModifier modifier = new RoleModifier( null, archetype );
        assertFalse( modifier.isUpdateNeeded() );
        assertTrue( modifier.isUpdatableEntry() );
        assertFalse( modifier.isNewEntry() );
        
        modifier.addGrant( "mutilate" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.setDescription( null );
        modifier.removeGrant( "mutilate" );
        assertFalse( modifier.isUpdateNeeded() );
        
        // add some grants
        modifier.addGrant( "testPerm0" );
        modifier.removeGrant( "testPerm0" );
        assertFalse( modifier.isUpdateNeeded() );
    }

    
    public void testRoleModifier2()
    {
        Set grants = new HashSet();
        grants.add( "bend" );
        grants.add( "fold" );
        grants.add( "spindle" );
        Role archetype = new Role( null, null, null, "testApp", "testRole", null, grants );
        
        RoleModifier modifier = new RoleModifier( null, archetype );
        assertFalse( modifier.isUpdateNeeded() );
        assertTrue( modifier.isUpdatableEntry() );
        assertFalse( modifier.isNewEntry() );
        
        modifier.addGrant( "mutilate" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.removeGrant( "fold" );
        
        modifier.setDescription( null );
        modifier.removeGrant( "mutilate" );
        modifier.addGrant( "fold" );
        assertFalse( modifier.isUpdateNeeded() );
        
        // add some grants
        modifier.addGrant( "testPerm0" );
        modifier.removeGrant( "testPerm0" );
        assertFalse( modifier.isUpdateNeeded() );
    }
    
    
    public void testProfileModifier0()
    {
        ProfileModifier modifier = new ProfileModifier( null, "testApp", "testProfile", "testUser" );
        assertFalse( modifier.isUpdateNeeded() );
        assertFalse( modifier.isUpdatableEntry() );
        assertTrue( modifier.isNewEntry() );

        modifier.addDenial( "fold" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.removeDenial( "fold" );
        assertFalse( modifier.isUpdateNeeded() );

        modifier.addGrant( "twist" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.removeGrant( "twist" );
        assertFalse( modifier.isUpdateNeeded() );
    
        modifier.addRole( "admin" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.removeRole( "admin" );
        assertFalse( modifier.isUpdateNeeded() );
        
        modifier.setDescription( "test description" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.setDescription( null );
        assertFalse( modifier.isUpdateNeeded() );
    }

    
    public void testProfileModifier2()
    {
        Set grants = new HashSet();
        grants.add( "spindle" );
        grants.add( "mutilate" );
        grants.add( "twist" );
        Set denials = new HashSet();
        denials.add( "fold" );
        Set roles = new HashSet();
        roles.add( "trusted" );
        Profile archetype = new Profile( null, null, null, "testApp", "archetype", "akarasulu", 
            "archetype profile", grants, denials, roles );
        
        ProfileModifier modifier = new ProfileModifier( null, archetype );
        assertFalse( modifier.isUpdateNeeded() );
        assertTrue( modifier.isUpdatableEntry() );
        assertFalse( modifier.isNewEntry() );

        modifier.addDenial( "fold" );
        assertFalse( modifier.isUpdateNeeded() );
        modifier.removeDenial( "fold" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.addDenial( "fold" );

        modifier.addGrant( "twist" );
        assertFalse( modifier.isUpdateNeeded() );
        modifier.removeGrant( "twist" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.addGrant( "twist" );
    
        modifier.addRole( "admin" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.removeRole( "admin" );
        assertFalse( modifier.isUpdateNeeded() );
        
        modifier.setDescription( "test description" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.setDescription( "archetype profile" );
        assertFalse( modifier.isUpdateNeeded() );
    }
    
    
    public void testGroupModifier()
    {
        Set members = new HashSet();
        Group group = new Group( null, null, null, "testGroup", members );
        assertEquals( "testGroup", group.getName() );
        assertEquals( 0, group.getMembers().size() );
        members.add( "testUser0" );
        assertEquals( 0, group.getMembers().size() );
        
        GroupModifier modifier = new GroupModifier( null, group );
        assertTrue( modifier.isUpdatableEntry() );
        assertFalse( modifier.isNewEntry() );
        assertFalse( modifier.isUpdateNeeded() );
        assertTrue( modifier.isValid() );
        modifier.addMember( "testUser0" );
        assertTrue( modifier.isUpdateNeeded() );
        modifier.removeMember( "testUser0" );
        assertFalse( modifier.isUpdateNeeded() );
    }
}
