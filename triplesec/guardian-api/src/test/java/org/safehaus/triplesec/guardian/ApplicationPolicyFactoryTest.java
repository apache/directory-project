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
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;


import junit.framework.Assert;
import junit.framework.TestCase;

public class ApplicationPolicyFactoryTest extends TestCase
{

    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( ApplicationPolicyFactoryTest.class );
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }
    
    public void testDriverRegistration() throws Exception
    {
        ConnectionDriver testDriver1 = new TestConnectionDriver1();
        ConnectionDriver testDriver2 = new TestConnectionDriver2();
        
        // Register driver and make sure it works.
        Assert.assertTrue( ApplicationPolicyFactory.registerDriver( testDriver1 ) );
        Assert.assertTrue( ApplicationPolicyFactory.registerDriver( testDriver2 ) );
        Assert.assertFalse( ApplicationPolicyFactory.registerDriver( testDriver1 ) );
        ApplicationPolicy testStore = ApplicationPolicyFactory.newInstance( "test2:dummy", new Properties() );
        Assert.assertEquals( "Test", testStore.getApplicationName() );
        
        // Deregister driver and make sure it doesn't work.
        Assert.assertTrue( ApplicationPolicyFactory.deregisterDriver( testDriver1.getClass() ) );
        Assert.assertFalse( ApplicationPolicyFactory.deregisterDriver( testDriver1.getClass() ) );
        Assert.assertFalse( ApplicationPolicyFactory.deregisterDriver( Integer.class ) );
        try
        {
            ApplicationPolicyFactory.newInstance( "test:dummy", new Properties() );
            Assert.fail( "Exception is not thrown." );
        }
        catch( NoConnectionDriverException e )
        {
            // OK
        }
    }
    
    public void testConnectionRetry()
    {
        FailingConnectionDriver driver = new FailingConnectionDriver();
        ApplicationPolicyFactory.registerDriver( driver );
        
        String url = "failure:dummy";
        Properties info = new Properties();

        // No retries
        try
        {
            ApplicationPolicyFactory.newInstance( url, null );
            Assert.fail( "Exception is not thrown." );
        }
        catch( StoreConnectionException e )
        {
            // OK
        }
        
        // Two retries
        driver.reset();
        info.setProperty( ApplicationPolicyFactory.RETRY_COUNT, "2" );
        ApplicationPolicyFactory.newInstance( url, info );
        
        // Wrong retry count
        driver.reset();
        info.setProperty( ApplicationPolicyFactory.RETRY_COUNT, "-1" );
        try
        {
            ApplicationPolicyFactory.newInstance( url, info );
            Assert.fail( "Exception is not thrown." );
        }
        catch( StoreConnectionException e )
        {
            // OK
        }
        
        // With retry delay
        driver.reset();
        info.setProperty( ApplicationPolicyFactory.RETRY_COUNT, "2" );
        info.setProperty( ApplicationPolicyFactory.RETRY_DELAY, "1" );
        ApplicationPolicyFactory.newInstance( url, info );

        // With wrong retry delay
        driver.reset();
        info.setProperty( ApplicationPolicyFactory.RETRY_COUNT, "2" );
        info.setProperty( ApplicationPolicyFactory.RETRY_DELAY, "-1" );
        ApplicationPolicyFactory.newInstance( url, info );
    }
    
    private static class TestConnectionDriver implements ConnectionDriver
    {
        private final String prefix;
        
        public TestConnectionDriver( String prefix )
        {
            this.prefix = prefix;
        }
        
        public boolean accept(String url) {
            return url.startsWith( prefix );
        }

        public ApplicationPolicy newStore(String url, Properties info) throws GuardianException {
            return new ApplicationPolicy()
            {
                public String getApplicationName() {
                    return "Test";
                }

                public Roles getRoles() {
                    return null;
                }

                public Permissions getPermissions() {
                    return null;
                }

                public Profile getProfile(String userName) {
                    return null;
                }

                public void close() {}


                public String getDescription()
                {
                    return null;
                }

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
            };
        }
    }

    private static class TestConnectionDriver1 extends TestConnectionDriver
    {
        public TestConnectionDriver1()
        {
            super( "test1" );
        }
    }

    private static class TestConnectionDriver2 extends TestConnectionDriver
    {
        public TestConnectionDriver2()
        {
            super( "test2" );
        }
    }

    private static class FailingConnectionDriver extends TestConnectionDriver
    {
        private int counter = 0;
        public FailingConnectionDriver()
        {
            super( "failure" );
        }
        
        public void reset()
        {
            counter = 0;
        }

        public ApplicationPolicy newStore( String url, Properties info ) throws GuardianException
        {
            counter++;
            if( counter == 3 )
            {
                return super.newStore( url, info );
            }
            
            throw new StoreConnectionException();
        }
    }
}
