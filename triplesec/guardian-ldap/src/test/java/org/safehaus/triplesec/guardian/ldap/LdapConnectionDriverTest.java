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


import junit.framework.TestCase;

import java.util.Properties;


/**
 * Tests the LDAP ConnectionDriver.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LdapConnectionDriverTest extends TestCase
{
    public void testNullProperties()
    {
        LdapConnectionDriver driver = new LdapConnectionDriver();

        try
        {
            driver.newStore( "", null );
            fail( "should not get here due to exception" );
        }
        catch( IllegalArgumentException e )
        {
        }
    }


    public void testNullUrl()
    {
        LdapConnectionDriver driver = new LdapConnectionDriver();
        Properties props = new Properties();
        props.setProperty( "applicationPrincipalDN", "appName=something" );
        props.setProperty( "applicationCredentials", "secret" );
        try
        {
            driver.newStore( null, props );
            fail( "should never get here due to an exception" );
        }
        catch ( IllegalArgumentException e )
        {

        }
    }


    public void testNoPrincipalName()
    {
        LdapConnectionDriver driver = new LdapConnectionDriver();

        try
        {
            Properties props = new Properties();
            props.setProperty( "applicationCredentials", "secret" );
            driver.newStore( "", props );
            fail( "should never get here due to an exception" );
        }
        catch ( IllegalArgumentException e )
        {

        }
    }


    public void testNoCredentials()
    {
        LdapConnectionDriver driver = new LdapConnectionDriver();
        Properties props = new Properties();
        props.setProperty( "applicationPrincipalDN", "appName=something" );
        try
        {
            driver.newStore( "", props );
            fail( "should never get here due to an exception" );
        }
        catch ( IllegalArgumentException e )
        {

        }
    }
}
