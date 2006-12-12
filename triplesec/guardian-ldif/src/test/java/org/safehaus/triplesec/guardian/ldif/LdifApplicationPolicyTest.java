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


import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TestCase to test the LDAP ApplicationPolicyStore implementation.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LdifApplicationPolicyTest extends TestCase
{
    Logger log = LoggerFactory.getLogger( LdifApplicationPolicyTest.class );
    LdifApplicationPolicy policy;


    public LdifApplicationPolicyTest( String string ) throws Exception
    {
        super( string );
    }


    public LdifApplicationPolicyTest() throws Exception
    {
        super();
    }


    protected void setUp() throws Exception
    {
        super.setUp();
        Properties props = new Properties();
        props.setProperty( "applicationPrincipalDN", "appName=mockApplication,ou=applications,dc=example,dc=com" );
        Class.forName( "org.safehaus.triplesec.guardian.ldif.LdifConnectionDriver" );
        String url = System.getProperty( "ldif.url", "file://src/test/resources/server.ldif" );
        log.info( "using url for ldif file: " + url );
        policy = ( LdifApplicationPolicy ) ApplicationPolicyFactory.newInstance( url, props );
    }


    protected void tearDown() throws Exception
    {
        super.tearDown();
        policy.close();
        policy = null;
    }


    public void testGetProfileIds() throws Exception
    {
        Set ids = new HashSet();
        for ( Iterator ii = this.policy.getProfileIdIterator(); ii.hasNext(); /**/ )
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


    public void testGetApplicationNameString()
    {
        String applicationName = LdifApplicationPolicy.getApplicationName(
                "appName=testingApp,ou=applications,dc=example,dc=com" );
        assertEquals( "testingApp", applicationName );

        try
        {
            LdifApplicationPolicy.getApplicationName( "notanapp=blahblah" );
            fail( "should never get here due to an exception" );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }


    public void testNonExistantProfile()
    {
        Profile p = policy.getProfile( "nonexistant" );
        assertNull( p );
    }


    public void testProfile0()
    {
        Profile p = policy.getProfile( "mockProfile0" );
        assertTrue( p.getEffectivePermissions().isEmpty() );
        assertEquals( 5, policy.getRoles().size() );
        assertEquals( p, policy.getProfile( "mockProfile0" ) );
    }


    public void testProfile1()
    {
        Profile p = policy.getProfile( "mockProfile1" );
        assertEquals( 2, p.getEffectivePermissions().size() );
        assertTrue( p.hasPermission( "mockPerm0" ) );
        assertTrue( p.hasPermission( "mockPerm1" ) );
        assertFalse( p.hasPermission( "mockPerm3") );
        assertEquals( p, policy.getProfile( "mockProfile1" ) );
    }


    public void testProfile2()
    {
        Profile p = policy.getProfile( "mockProfile2" );
        assertEquals( 2, p.getEffectivePermissions().size() );
        assertTrue( p.hasPermission( "mockPerm0" ) );
        assertTrue( p.hasPermission( "mockPerm1" ) );
        assertFalse( p.hasPermission( "mockPerm3") );
        assertEquals( p, policy.getProfile( "mockProfile2" ) );
    }


    public void testProfile3()
    {
        Profile p = policy.getProfile( "mockProfile3" );
        assertEquals( 4, p.getEffectivePermissions().size() );
        assertTrue( p.hasPermission( "mockPerm0" ) );
        assertTrue( p.hasPermission( "mockPerm7" ) );
        assertTrue( p.hasPermission( "mockPerm2" ) );
        assertTrue( p.hasPermission( "mockPerm3" ) );
        assertFalse( p.hasPermission( "mockPerm4" ) );
        assertEquals( p, policy.getProfile( "mockProfile3" ) );
    }


    public void testProfile4()
    {
        Profile p = policy.getProfile( "mockProfile4" );
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
        assertEquals( p, policy.getProfile( "mockProfile4" ) );
    }
    
    
    public void testGetUserProfileIds() 
    {
        Set ids = policy.getUserProfileIds( "akarasulu" );
        assertEquals( 5, ids.size() );
        ids = policy.getUserProfileIds( "trustin" );
        assertEquals( 0, ids.size() );
    }


    public void testClosedState()
    {
        policy.close();
        try
        {
            policy.getProfile( "asdf" );
            fail( "should never get here due to an exception" );
        }
        catch ( Exception e )
        {

        }
    }
}
                                                     