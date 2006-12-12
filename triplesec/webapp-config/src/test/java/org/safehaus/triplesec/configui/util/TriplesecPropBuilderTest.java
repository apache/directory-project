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

import java.util.Properties;

import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;

import junit.framework.TestCase;


/**
 * Tests the property build class.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecPropBuilderTest extends TestCase
{
    TriplesecPropBuilder builder = new TriplesecPropBuilder();
    
    
    public void testGetDefaults()
    {
        Properties defaults = builder.getDefault();
        assertEquals( "simple", defaults.getProperty( "java.naming.security.authentication" ) );
        assertEquals( "uid=admin,ou=system", defaults.getProperty( "java.naming.security.principal" ) );
        assertEquals( "secret", defaults.getProperty( "java.naming.security.credentials" ) );
        assertEquals( "dc=example,dc=com", defaults.getProperty( "java.naming.provider.url" ) );
        assertEquals( "org.safehaus.triplesec.store.ProfileStateFactory", 
            defaults.getProperty( "java.naming.factory.state" ) );
        assertEquals( "org.safehaus.triplesec.store.ProfileObjectFactory", 
            defaults.getProperty( "java.naming.factory.object" ) );
        assertEquals( "EXAMPLE.COM", defaults.getProperty( "kdc.primary.realm" ) );
        assertEquals( "krbtgt/EXAMPLE.COM@EXAMPLE.COM", defaults.getProperty( "kdc.principal" ) );
        assertEquals( "des-cbc-md5 des3-cbc-sha1 des3-cbc-md5 des-cbc-md4 des-cbc-crc", 
            defaults.getProperty( "kdc.encryption.types" ) );
        assertEquals( "ou=users,dc=example,dc=com", defaults.getProperty( "kdc.entryBaseDn" ) );
        assertEquals( "secret", defaults.getProperty( "kdc.java.naming.security.credentials" ) );
        assertEquals( "ou=users,dc=example,dc=com", defaults.getProperty( "changepw.entryBaseDn" ) );
        assertEquals( "secret", defaults.getProperty( "changepw.java.naming.security.credentials" ) );
        assertEquals( "kadmin/changepw@EXAMPLE.COM", defaults.getProperty( "changepw.principal" ) );
        assertEquals( "5", defaults.getProperty( "kdc.allowable.clockskew" ) );
        assertEquals( "1440", defaults.getProperty( "kdc.tgs.maximum.ticket.lifetime" ) );
        assertEquals( "10080", defaults.getProperty( "kdc.tgs.maximum.renewable.lifetime" ) );
        assertEquals( "true", defaults.getProperty( "kdc.pa.enc.timestamp.required" ) );
        assertEquals( "true", defaults.getProperty( "kdc.tgs.empty.addresses.allowed" ) );
        assertEquals( "true", defaults.getProperty( "kdc.tgs.forwardable.allowed" ) );
        assertEquals( "true", defaults.getProperty( "kdc.tgs.proxiable.allowed" ) );
        assertEquals( "true", defaults.getProperty( "kdc.tgs.postdate.allowed" ) );
        assertEquals( "true", defaults.getProperty( "kdc.tgs.renewable.allowed" ) );
        assertEquals( "ou=Users,dc=example,dc=com", defaults.getProperty( "safehaus.entry.basedn" ) );
        assertEquals( "true", defaults.getProperty( "safehaus.load.testdata" ) );
        assertEquals( "org.safehaus.triplesec.verifier.hotp.DefaultHotpSamVerifier", 
            defaults.getProperty( "kerberos.sam.type.7" ) );
    }


    public void testWithSettings()
    {
        TriplesecConfigSettings settings = new TriplesecConfigSettings();
        settings.setAdminPassword( "password" );
        settings.setClockSkew( 7 );
        settings.setEnableDemo( false );
        settings.setPrimaryRealmName( "APACHE.ORG" );
        settings.setRenewableLifetime( 777 );
        settings.setTicketLifetime( 77 );
        Properties props = builder.build( settings );
        assertEquals( "simple", props.getProperty( "java.naming.security.authentication" ) );
        assertEquals( "uid=admin,ou=system", props.getProperty( "java.naming.security.principal" ) );
        assertEquals( "password", props.getProperty( "java.naming.security.credentials" ) );
        assertEquals( "dc=apache,dc=org", props.getProperty( "java.naming.provider.url" ) );
        assertEquals( "org.safehaus.triplesec.store.ProfileStateFactory", 
            props.getProperty( "java.naming.factory.state" ) );
        assertEquals( "org.safehaus.triplesec.store.ProfileObjectFactory", 
            props.getProperty( "java.naming.factory.object" ) );
        assertEquals( "APACHE.ORG", props.getProperty( "kdc.primary.realm" ) );
        assertEquals( "krbtgt/APACHE.ORG@APACHE.ORG", props.getProperty( "kdc.principal" ) );
        assertEquals( "des-cbc-md5 des3-cbc-sha1 des3-cbc-md5 des-cbc-md4 des-cbc-crc", 
            props.getProperty( "kdc.encryption.types" ) );
        assertEquals( "ou=users,dc=apache,dc=org", props.getProperty( "kdc.entryBaseDn" ) );
        assertEquals( "password", props.getProperty( "kdc.java.naming.security.credentials" ) );
        assertEquals( "ou=users,dc=apache,dc=org", props.getProperty( "changepw.entryBaseDn" ) );
        assertEquals( "password", props.getProperty( "changepw.java.naming.security.credentials" ) );
        assertEquals( "kadmin/changepw@APACHE.ORG", props.getProperty( "changepw.principal" ) );
        assertEquals( "7", props.getProperty( "kdc.allowable.clockskew" ) );
        assertEquals( "77", props.getProperty( "kdc.tgs.maximum.ticket.lifetime" ) );
        assertEquals( "777", props.getProperty( "kdc.tgs.maximum.renewable.lifetime" ) );
        assertEquals( "true", props.getProperty( "kdc.pa.enc.timestamp.required" ) );
        assertEquals( "true", props.getProperty( "kdc.tgs.empty.addresses.allowed" ) );
        assertEquals( "true", props.getProperty( "kdc.tgs.forwardable.allowed" ) );
        assertEquals( "true", props.getProperty( "kdc.tgs.proxiable.allowed" ) );
        assertEquals( "true", props.getProperty( "kdc.tgs.postdate.allowed" ) );
        assertEquals( "true", props.getProperty( "kdc.tgs.renewable.allowed" ) );
        assertEquals( "ou=Users,dc=apache,dc=org", props.getProperty( "safehaus.entry.basedn" ) );
        assertEquals( "false", props.getProperty( "safehaus.load.testdata" ) );
        assertEquals( "org.safehaus.triplesec.verifier.hotp.DefaultHotpSamVerifier", 
            props.getProperty( "kerberos.sam.type.7" ) );
    }
}
