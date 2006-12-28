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
package org.safehaus.triplesec.store;


import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import javax.naming.Context;

import org.apache.directory.server.core.unit.AbstractAdminTestCase;
import org.apache.directory.server.core.schema.bootstrap.*;
import org.apache.directory.server.core.configuration.MutableStartupConfiguration;

import org.apache.directory.shared.ldap.ldif.Entry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.safehaus.profile.ServerProfile;
import org.safehaus.profile.BaseServerProfileModifier;
import org.safehaus.triplesec.store.schema.SafehausSchema;

import java.util.Set;
import java.util.HashSet;


/**
 * Tests the ProfileObjectFactory and ProfileStateFactory classes.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ProfileFactoryITest extends AbstractAdminTestCase
{
    public ProfileFactoryITest()
    {
        MutableStartupConfiguration cfg = super.configuration;
        Set schemas = new HashSet();
        schemas.add( new SystemSchema() );
        schemas.add( new SafehausSchema() );
        schemas.add( new ApacheSchema() );
        schemas.add( new CoreSchema() );
        schemas.add( new CosineSchema() );
        schemas.add( new InetorgpersonSchema() );
        schemas.add( new Krb5kdcSchema() );
        cfg.setBootstrapSchemas( schemas );
        cfg.setShutdownHookEnabled( false );
        super.overrideEnvironment( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );
        super.overrideEnvironment( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
    }


    public void testObjectFactory() throws Exception
    {
        String ldif = "dn: cn=bogus\n"
            + "cn: Alex Karasulu\n" + "sn: Karasulu\n" + "givenname: Alex\n" + "objectClass: top\n"
            + "objectClass: person\n" + "objectClass: organizationalPerson\n" + "objectClass: inetOrgPerson\n"
            + "objectClass: krb5Principal\n" + "objectClass: krb5KDCEntry\n" + "objectClass: safehausProfile\n"
            + "ou: Directory\n" + "ou: Users\n" + "l: Jacksonville\n" + "uid: akarasulu2\n"
            + "krb5PrincipalName: akarasulu@EXAMPLE.COM\n" + "krb5KeyVersionNumber: 0\n"
            + "mail: akarasulu@example.com\n" + "telephonenumber: +1 904 982 6882\n"
            + "facsimiletelephonenumber: +1 904 982 6883\n" + "roomnumber: 666\n" + "userPassword: maxwell\n"
            + "safehausUid: akarasulu2\n" + "safehausRealm: example.com\n" + "safehausLabel: example realm\n"
            + "safehausFactor: 34258723456\n" + "safehausSecret:: d$U*E&*^dss%$(345j\n"
            + "safehausFailuresInEpoch: 7\n" + "safehausResynchCount: -1\n" + "safehausTokenPin: 1234\n"
            + "safehausNotifyBy: sms\n"
            + "safehausInfo: for testcases\n";

        LdifReader parser = new LdifReader();
        Entry entry = ( Entry ) parser.parseLdif( ldif ).get( 0 );
        Attributes attributes = entry.getAttributes();
        
        attributes.remove( "dn" );

        sysRoot.bind( "uid=akarasulu2", null, attributes );
        ServerProfile p = ( ServerProfile ) sysRoot.lookup( "uid=akarasulu2" );

        assertNotNull( p );
        assertEquals( 34258723456L, p.getFactor() );
        assertEquals( 7, p.getFailuresInEpoch() );
        assertEquals( -1, p.getResynchCount() );
        assertEquals( "for testcases", p.getInfo() );
        assertEquals( "example realm", p.getLabel() );
        assertEquals( "example.com", p.getRealm() );
        assertEquals( "akarasulu2", p.getUserId() );
    }


    public void testStateFactory() throws NamingException
    {
        String ldif = "dn: uid=akarasulu,ou=users,dc=example,dc=com\n" +
            "cn: Alex Karasulu\n" + "sn: Karasulu\n" +
            "givenname: Alex\n" +
            "objectClass: top\n" +
            "objectClass: person\n" +
            "objectClass: organizationalPerson\n" +
            "objectClass: inetOrgPerson\n" +
            "objectClass: krb5Principal\n" +
            "objectClass: krb5KDCEntry\n" +
            "objectClass: safehausProfile\n" +
            "ou: Directory\n" +
            "ou: Users\n" +
            "l: Jacksonville\n" +
            "uid: akarasulu2\n" +
            "krb5PrincipalName: akarasulu@EXAMPLE.COM\n" +
            "krb5KeyVersionNumber: 0\n" +
            "mail: akarasulu@example.com\n" +
            "telephonenumber: +1 904 982 6882\n" +
            "facsimiletelephonenumber: +1 904 982 6883\n" +
            "safehausNotifyBy: sms\n" +
            "roomnumber: 666\n" +
            "userPassword: maxwell\n";

        LdifReader parser = new LdifReader();
        Entry entry = ( Entry ) parser.parseLdif( ldif ).get( 0 );
        Attributes attributes = ( Attributes ) entry.getAttributes();
        attributes.remove( "dn" );

        BaseServerProfileModifier modifier = new BaseServerProfileModifier();
        modifier.setFactor( 56902748234756L );
        modifier.setFailuresInEpoch( 234 );
        modifier.setLabel( "test domain" );
        modifier.setInfo( "just some info" );
        modifier.setSecret( new byte[]
            { 'a', 's', 'd', 'f', 'a', 'd', 's', 'f', 'a', 'd', 'f', 'a', 'd', 'f', 'a', 'f', 's', 'f', 'd', 'f' } );
        modifier.setUserId( "akarasulu2" );
        modifier.setRealm( "example.com" );
        modifier.setResynchCount( 2 );

        sysRoot.bind( "uid=akarasulu2", modifier.getServerProfile(), attributes );
        Attributes reload = sysRoot.getAttributes( "uid=akarasulu2" );
        assertNotNull( reload );
        assertEquals( 56902748234756L, Long.parseLong( ( String ) reload.get( "safehausFactor" ).get() ) );
        assertEquals( 234, Integer.parseInt( ( String ) reload.get( "safehausFailuresInEpoch" ).get() ) );
        assertEquals( 2, Integer.parseInt( ( String ) reload.get( "safehausResynchCount" ).get() ) );
        assertEquals( "akarasulu2", reload.get( "safehausUid" ).get() );
        assertEquals( "example.com", reload.get( "safehausRealm" ).get() );
        assertEquals( "test domain", reload.get( "safehausLabel" ).get() );
        assertEquals( "just some info", reload.get( "safehausInfo" ).get() );
    }
}
