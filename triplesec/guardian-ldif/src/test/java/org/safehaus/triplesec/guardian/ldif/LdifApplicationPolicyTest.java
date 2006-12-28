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
import java.net.URL;

import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.guardian.StringPermission;
import org.safehaus.triplesec.guardian.PermissionsUtil;
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
    private static final String APP_NAME = "mockApplication";


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
        URL ldifURL = getClass().getClassLoader().getResource("server.ldif");
        String url = ldifURL.toString();
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
        assertEquals( 6, ids.size() );
        assertTrue( ids.contains( "mockProfile0" ) );
        assertTrue( ids.contains( "mockProfile1" ) );
        assertTrue( ids.contains( "mockProfile2" ) );
        assertTrue( ids.contains( "mockProfile3" ) );
        assertTrue( ids.contains( "mockProfile4" ) );
        assertTrue( ids.contains( "mockProfile5" ) );
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
        assertTrue( PermissionsUtil.isEmpty(p.getEffectiveGrantedPermissions()) );
        assertEquals( 6, policy.getRoles().size() );
        assertEquals( p, policy.getProfile( "mockProfile0" ) );
    }


    public void testProfile1()
    {
        Profile p = policy.getProfile( "mockProfile1" );
        assertEquals( 2, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertTrue( p.implies( new StringPermission("mockPerm1" )));
        assertFalse( p.implies( new StringPermission("mockPerm3")));
        assertEquals( p, policy.getProfile( "mockProfile1" ) );
    }


    public void testProfile2()
    {
        Profile p = policy.getProfile( "mockProfile2" );
        assertEquals( 2, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertTrue( p.implies( new StringPermission("mockPerm1" )));
        assertFalse( p.implies( new StringPermission("mockPerm3")));
        assertEquals( p, policy.getProfile( "mockProfile2" ) );
    }


    public void testProfile3()
    {
        Profile p = policy.getProfile( "mockProfile3" );
        assertEquals( 4, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertTrue( p.implies( new StringPermission("mockPerm7" )));
        assertTrue( p.implies( new StringPermission("mockPerm2" )));
        assertTrue( p.implies( new StringPermission("mockPerm3" )));
        assertFalse( p.implies( new StringPermission("mockPerm4" )));
        assertEquals( p, policy.getProfile( "mockProfile3" ) );
    }


    public void testProfile4()
    {
        Profile p = policy.getProfile( "mockProfile4" );
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
        assertEquals( p, policy.getProfile( "mockProfile4" ) );
    }
    
    public void testProfile5()
    {
        Profile p = policy.getProfile( "mockProfile5" );
        assertEquals( 8, PermissionsUtil.size(p.getEffectiveGrantedPermissions()) );
        assertEquals( 2, PermissionsUtil.size(p.getEffectiveDeniedPermissions()) );
        assertTrue( p.implies( new StringPermission("mockPerm0" )));
        assertFalse( p.implies( new StringPermission("mockPerm1" )));
        assertTrue( p.implies( new StringPermission("mockPerm2" )));
        assertTrue( p.implies( new StringPermission("mockPerm3" )));
        assertTrue( p.implies( new StringPermission("mockPerm4" )));
        assertTrue( p.implies( new StringPermission("mockPerm5" )));
        assertFalse( p.implies( new StringPermission("mockPerm6" )));
        assertFalse( p.implies( new StringPermission("mockPerm7" )));
        assertFalse( p.implies( new StringPermission("mockPerm8" )));
        assertTrue( p.implies( new StringPermission("mockPerm9" )));
        assertFalse( p.implies( new StringPermission("mockPerm14" )));
        assertEquals( p, policy.getProfile( "mockProfile5" ) );
    }


    public void testGetUserProfileIds() 
    {
        Set<String> ids = policy.getUserProfileIds( "akarasulu" );
        assertEquals( 6, ids.size() );
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
                                                     