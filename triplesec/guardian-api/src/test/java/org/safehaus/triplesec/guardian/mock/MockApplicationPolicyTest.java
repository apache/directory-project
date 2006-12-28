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


/**
 * Test cases for the mock application policy store.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class MockApplicationPolicyTest extends TestCase
{
    MockApplicationPolicy store;

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
        assertEquals( 5, store.getRoles().size() );
        Profile p = store.getProfile( "mockProfile0" );
        assertTrue( p.getEffectivePermissions().isEmpty() );
        assertTrue( p.getRoles().isEmpty() );
    }

    public void testProfile1()
    {
        Profile p = store.getProfile( "mockProfile1" );
        assertEquals( 2, p.getEffectivePermissions().size() );
        assertTrue( p.hasPermission( "mockPerm0" ) );
        assertTrue( p.hasPermission( "mockPerm1" ) );
        assertFalse( p.hasPermission( "mockPerm3") );
        assertEquals( 2, p.getRoles().size() );
    }

    public void testProfile2()
    {
        Profile p = store.getProfile( "mockProfile2" );
        assertEquals( 2, p.getEffectivePermissions().size() );
        assertTrue( p.hasPermission( "mockPerm0" ) );
        assertTrue( p.hasPermission( "mockPerm1" ) );
        assertFalse( p.hasPermission( "mockPerm3") );
        assertEquals( 1, p.getRoles().size() );
        assertTrue( p.getRoles().contains( "mockRole2" ) );
    }

    public void testProfile3()
    {
        Profile p = store.getProfile( "mockProfile3" );
        assertEquals( 4, p.getEffectivePermissions().size() );
        assertTrue( p.hasPermission( "mockPerm0" ) );
        assertTrue( p.hasPermission( "mockPerm7" ) );
        assertTrue( p.hasPermission( "mockPerm2" ) );
        assertTrue( p.hasPermission( "mockPerm3" ) );
        assertFalse( p.hasPermission( "mockPerm4" ) );
        assertEquals( 1, p.getRoles().size() );
        assertTrue( p.getRoles().contains( "mockRole3" ) );
    }

    public void testProfile4()
    {
        Profile p = store.getProfile( "mockProfile4" );
        assertEquals( 7, p.getEffectivePermissions().size() );
        assertTrue( p.hasPermission( "mockPerm0" ) );
        assertFalse( p.hasPermission( "mockPerm1" ) );
        assertTrue( p.hasPermission( "mockPerm2" ) );
        assertTrue( p.hasPermission( "mockPerm3" ) );
        assertTrue( p.hasPermission( "mockPerm4" ) );
        assertTrue( p.hasPermission( "mockPerm5" ) );
        assertTrue( p.hasPermission( "mockPerm6" ) );
        assertFalse( p.hasPermission( "mockPerm7" ) );
        assertFalse( p.hasPermission( "mockPerm8" ) );
        assertTrue( p.hasPermission( "mockPerm9" ) );

        assertFalse( p.hasPermission( "mockPerm14" ) );
        assertEquals( 2, p.getRoles().size() );
        assertTrue( p.getRoles().contains( "mockRole3" ) );
        assertTrue( p.getRoles().contains( "mockRole4" ) );
    }
}
