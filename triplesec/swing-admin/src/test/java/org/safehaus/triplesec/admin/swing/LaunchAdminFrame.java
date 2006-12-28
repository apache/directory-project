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
package org.safehaus.triplesec.admin.swing;


import java.util.Properties;

import org.safehaus.triplesec.TriplesecInstallationLayout;
import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.integration.TriplesecIntegration;


public class LaunchAdminFrame extends TriplesecIntegration
{
    public LaunchAdminFrame() throws Exception
    {
        super();
    }
    
    
    public void testAdminFrame() throws Exception
    {
        ConnectionInfoModifier modifier = new ConnectionInfoModifier();
        modifier.setCredentials( "secret" );
        modifier.setHost( "localhost" );
        modifier.setKrb5Port( super.getKerberosPort() );
        modifier.setLdapPort( super.getLdapPort() );
        modifier.setPrincipal( "admin" );
        modifier.setRealm( "EXAMPLE.COM" );
        modifier.setUseLdaps( false );
        
        AdminFrame frame = new AdminFrame();
        frame.setInstallationLayout( 
            new TriplesecInstallationLayout( System.getProperty( "serverConfigurationPath" ) ) );
        frame.setLocation( UiUtils.getCenteredPosition( frame ) ); 
        frame.setVisible( true );
        
        ConnectionInfo connectionInfo = modifier.getConnectionInfo();
        
        // -------------------------------------------------------------------
        // Need to connect to the server via guardian first
        // -------------------------------------------------------------------
        
        Properties props = new Properties();
        StringBuffer buf = new StringBuffer();
        buf.append( "appName=tsecAdminTool,ou=Applications," ).append( connectionInfo.getLdapRealmBase() );
        props.setProperty( "applicationPrincipalDN", buf.toString() );
        props.setProperty( "applicationCredentials", "secret" );

        try
        {
            Class.forName( "org.safehaus.triplesec.guardian.ldap.LdapConnectionDriver" );
        }
        catch ( ClassNotFoundException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        buf.setLength( 0 );
        buf.append( "ldap://" ).append( connectionInfo.getHost() ).append( ":" )
            .append( connectionInfo.getLdapPort() ).append( "/" ).append( connectionInfo.getLdapRealmBase() );
        ApplicationPolicy policy = 
            ApplicationPolicyFactory.newInstance( "ldap://localhost:10389/dc=example,dc=com", props );
        frame.connect( connectionInfo, policy );
        
        System.out.println( "Press any key to end test ..." );
        System.in.read();
    }
}
