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
package org.safehaus.triplesec.configui.util;


import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;
import org.safehaus.triplesec.configuration.MutableTriplesecStartupConfiguration;

import junit.framework.TestCase;



/**
 * Tests the config builder class.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecConfigBuilderTest extends TestCase
{
    TriplesecConfigBuilder builder = new TriplesecConfigBuilder();
    
    
    public void testWithSettings() throws NamingException
    {
        TriplesecConfigSettings settings = new TriplesecConfigSettings();
        settings.setLdapPort( 1389 );
        settings.setPrimaryRealmName( "APACHE.ORG" );
        
        MutableTriplesecStartupConfiguration config = builder.build( settings );
        assertEquals( 1389, config.getLdapPort() );
        assertEquals( 1, config.getContextPartitionConfigurations().size() );
        MutablePartitionConfiguration partition = ( MutablePartitionConfiguration ) 
            config.getContextPartitionConfigurations().iterator().next();
        assertEquals( "apache", partition.getName() );
        assertEquals( "dc=apache,dc=org", partition.getSuffix() );
        Attributes attrs = partition.getContextEntry();
        assertTrue( attrs.get( "dc" ).contains( "apache" ) );
        assertTrue( attrs.get( "administrativeRole" ).contains( "accessControlSpecificArea" ) );
        assertTrue( attrs.get( "administrativeRole" ).contains( "collectiveAttributeSpecificArea" ) );
    }
}
