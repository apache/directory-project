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


import junit.framework.TestCase;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.guardian.StringPermission;
import org.safehaus.triplesec.guardian.PermissionsUtil;


/**
 * Test cases for the mock application policy store.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class MockApplicationPolicyTest extends TestCase
{
    MockApplicationPolicy store;
    private static final String APP_NAME = "mockApplication";

    protected void setUp() throws Exception
    {
        super.setUp();
        Class.forName( "org.safehaus.triplesec.guardian.mock.MockConnectionDriver" );
        store = ( MockApplicationPolicy ) ApplicationPolicyFactory.newInstance( "mockApplication", null );
    }


    protected void tearDown() throws Exception
    {
        super.tearDown();
        store.close();
        store = null;
    }


    public void testProfile0()
    {
        assertEquals( 6, store.getRoles().size() );
        Profile p = store.getProfile( "mockProfile0" );
        assertTrue( PermissionsUtil.isEmpty(p.getEffectiveGrantedPermissions()) );
        assertTrue( p.getRoles().isEmpty() );
    }

    public void testProfile1()
    {
        Profile p = store.getProfile( "mockProfile1" );
        assertEquals( 2, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertTrue( p.implies( new StringPermission("mockPerm1" )));
        assertFalse( p.implies( new StringPermission("mockPerm3")));
        assertEquals( 2, p.getRoles().size() );
    }

    public void testProfile2()
    {
        Profile p = store.getProfile( "mockProfile2" );
        assertEquals( 2, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertTrue( p.implies( new StringPermission("mockPerm1" )));
        assertFalse( p.implies( new StringPermission("mockPerm3")));
        assertEquals( 1, p.getRoles().size() );
        assertTrue( p.getRoles().contains( "mockRole2" ) );
    }

    public void testProfile3()
    {
        Profile p = store.getProfile( "mockProfile3" );
        assertEquals( 4, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertTrue( p.implies( new StringPermission("mockPerm7" )));
        assertTrue( p.implies( new StringPermission("mockPerm2" )));
        assertTrue( p.implies( new StringPermission("mockPerm3" )));
        assertFalse( p.implies( new StringPermission("mockPerm4" )));
        assertEquals( 1, p.getRoles().size() );
        assertTrue( p.getRoles().contains( "mockRole3" ) );
    }

    public void testProfile4()
    {
        Profile p = store.getProfile( "mockProfile4" );
        assertEquals( 8, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertEquals( 1, PermissionsUtil.size(p.getEffectiveDeniedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertFalse( p.implies( new StringPermission("mockPerm1" )));
        assertTrue( p.implies( new StringPermission("mockPerm2" )));
        assertTrue( p.implies( new StringPermission("mockPerm3" )));
        assertTrue( p.implies( new StringPermission("mockPerm4" )));
        assertTrue( p.implies( new StringPermission("mockPerm5" )));
        assertTrue( p.implies( new StringPermission("mockPerm6" )));
        assertFalse( p.implies( new StringPermission("mockPerm7" )));
        assertFalse( p.implies( new StringPermission("mockPerm8" )));
        assertTrue( p.implies( new StringPermission("mockPerm9" )));

        assertFalse( p.implies( new StringPermission("mockPerm14" )));
        assertEquals( 2, p.getRoles().size() );
        assertTrue( p.getRoles().contains( "mockRole3" ) );
        assertTrue( p.getRoles().contains( "mockRole4" ) );
    }

    public void testProfile5()
    {
        Profile p = store.getProfile( "mockProfile5" );
        assertEquals( 8, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertEquals( 2, PermissionsUtil.size(p.getEffectiveDeniedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertFalse( p.implies( new StringPermission("mockPerm1" )));
        assertTrue( p.implies( new StringPermission("mockPerm2" )));
        assertTrue( p.implies( new StringPermission("mockPerm3" )));
        assertTrue( p.implies( new StringPermission("mockPerm4" )));
        assertTrue( p.implies( new StringPermission("mockPerm5" )));
        //from denial in role5
        assertFalse( p.implies( new StringPermission("mockPerm6" )));
        assertFalse( p.implies( new StringPermission("mockPerm7" )));
        assertFalse( p.implies( new StringPermission("mockPerm8" )));
        assertTrue( p.implies( new StringPermission("mockPerm9" )));

        assertFalse( p.implies( new StringPermission("mockPerm14" )));
        assertEquals( 3, p.getRoles().size() );
        assertTrue( p.getRoles().contains( "mockRole3" ) );
        assertTrue( p.getRoles().contains( "mockRole4" ) );
        assertTrue( p.getRoles().contains( "mockRole5" ) );
    }
}
