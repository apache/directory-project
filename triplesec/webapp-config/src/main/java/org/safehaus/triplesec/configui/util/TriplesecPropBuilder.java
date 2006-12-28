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

import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;


/**
 * Used to build the server's properties from configuration settings filled in by a wizard. 
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecPropBuilder
{
    public Properties build( TriplesecConfigSettings settings )
    {
        Properties props = getDefault();
        
        /*
         * Alter the properties here according to new config settings
         */
        // variables reused all over
        String password = settings.getAdminPassword();
        String realm = settings.getPrimaryRealmName();
        String baseDn = NamespaceTools.inferLdapName( settings.getPrimaryRealmName() );
        baseDn = baseDn.toLowerCase();

        props.put( "java.naming.security.credentials", password );
        props.put( "kdc.java.naming.security.credentials", password );
        props.put( "changepw.java.naming.security.credentials", password );
        
        props.put( "java.naming.provider.url", baseDn );
        props.put( "kdc.primary.realm", realm.toUpperCase() );
        props.put( "kdc.principal", "krbtgt/" + realm + "@" + realm.toUpperCase() );
        props.put( "kdc.entryBaseDn", "ou=users," + baseDn );
        props.put( "changepw.entryBaseDn", "ou=users," + baseDn );
        props.put( "changepw.principal", "kadmin/changepw@" + realm.toUpperCase() );
        
        props.put( "kdc.allowable.clockskew", String.valueOf( settings.getClockSkew() ) );
        props.put( "safehaus.entry.basedn", "ou=Users," + baseDn );
        props.put( "safehaus.load.testdata", String.valueOf( settings.isEnableDemo() ) );
        
        props.put( "kdc.tgs.maximum.ticket.lifetime", String.valueOf( settings.getTicketLifetime() ) );
        props.put( "kdc.tgs.maximum.renewable.lifetime", String.valueOf( settings.getRenewableLifetime() ) );

        return props;
    }
    
    
    public Properties getDefault()
    {
        Properties props = new Properties();
        props.put( "java.naming.security.authentication", "simple" );
        props.put( "java.naming.security.principal", "uid=admin,ou=system" );
        props.put( "java.naming.security.credentials", "secret" );
        props.put( "java.naming.provider.url", "dc=example,dc=com" );
        props.put( "java.naming.factory.state", "org.safehaus.triplesec.store.ProfileStateFactory" );
        props.put( "java.naming.factory.object", "org.safehaus.triplesec.store.ProfileObjectFactory" );
        props.put( "kdc.primary.realm", "EXAMPLE.COM" );
        props.put( "kdc.principal", "krbtgt/EXAMPLE.COM@EXAMPLE.COM" );
        props.put( "kdc.encryption.types", "des-cbc-md5 des3-cbc-sha1 des3-cbc-md5 des-cbc-md4 des-cbc-crc" );
        props.put( "kdc.entryBaseDn", "ou=users,dc=example,dc=com" );
        props.put( "kdc.java.naming.security.credentials", "secret" );
        props.put( "changepw.entryBaseDn", "ou=users,dc=example,dc=com" );
        props.put( "changepw.java.naming.security.credentials", "secret" );
        props.put( "changepw.principal", "kadmin/changepw@EXAMPLE.COM" );
        props.put( "kdc.allowable.clockskew", "5" );

        props.put( "kdc.tgs.maximum.ticket.lifetime", "1440" );
        props.put( "kdc.tgs.maximum.renewable.lifetime", "10080" );
        props.put( "kdc.pa.enc.timestamp.required", "true" );
        props.put( "kdc.tgs.empty.addresses.allowed", "true" );
        props.put( "kdc.tgs.forwardable.allowed", "true" );
        props.put( "kdc.tgs.proxiable.allowed", "true" );
        props.put( "kdc.tgs.postdate.allowed", "true" );
        props.put( "kdc.tgs.renewable.allowed", "true" );
        
        props.put( "safehaus.entry.basedn", "ou=Users,dc=example,dc=com" );
        props.put( "safehaus.load.testdata", "true" );
        props.put( "kerberos.sam.type.7", "org.safehaus.triplesec.verifier.hotp.DefaultHotpSamVerifier" );
        return props;
    }
}
