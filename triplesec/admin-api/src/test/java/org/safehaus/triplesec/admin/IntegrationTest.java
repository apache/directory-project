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
package org.safehaus.triplesec.admin;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.shared.ldap.util.StringTools;
import org.safehaus.triplesec.admin.dao.DaoFactory;
import org.safehaus.triplesec.admin.dao.PermissionClassDao;
import org.safehaus.triplesec.admin.dao.ldap.LdapDaoFactory;
import org.safehaus.triplesec.admin.dao.ldap.LdapPermissionClassDao;
import org.safehaus.triplesec.integration.TriplesecIntegration;



/**
 * Test cases for JNDI data access object and state factories.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class IntegrationTest extends TriplesecIntegration
{
    private Properties props;
    private DaoFactory factory;
    private DirContext ctx;
    private TriplesecAdmin admin;

    
    public IntegrationTest() throws Exception
    {
        super();
    }

    
    public void setUp() throws Exception
    {
        super.setUp();
        
        props = new Properties();
        props.setProperty( DaoFactory.IMPLEMENTATION_CLASS, LdapDaoFactory.class.getName() );
        props.setProperty( "java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory" );
        props.setProperty( "java.naming.provider.url", "ldap://localhost:" + getLdapPort() + "/dc=example,dc=com" );
        props.setProperty( "java.naming.security.principal", "uid=admin,ou=system" );
        props.setProperty( "java.naming.security.credentials", "secret" );
        props.setProperty( "java.naming.security.authentication", "simple" );
        //apparently socket sometimes needs time to close??? from previous run?????
        Thread.sleep(1000);
        factory = DaoFactory.createInstance( props );
        ctx = new InitialDirContext( props );
        admin = new TriplesecAdmin( props );
        
        assertNotNull( factory );
    }

    
    /**
     * Tests the following {@link LdapPermissionClassDao} methods:
     * 
     * <ul>
     *   <li>{@link PermissionClassDao#delete(String, String)}</li>
     *   <li>{@link PermissionClassDao#load(String, String)}</li>
     *   <li>{@link PermissionClassDao#modify(String, String, String, ModificationItem[])}</li>
     *   <li>{@link PermissionClassDao#rename(String, PermissionClass)}</li>
     *   <li>{@link PermissionClassDao#permissionIterator(String)}</li>
     *   <li>{@link PermissionClassDao#permissionClassNameIterator(String)}</li>
     * </ul>
     */
    public void XtestPermissionClassDao() throws Exception
    {
        PermissionClassDao dao = factory.getPermissionClassDao();
        
        // add a permission via add( String, String )
        dao.add( "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications", getName() + "0", null, null );
        Attributes attrs = ctx.getAttributes( "permClassName=" + getName() + "0, " + "" +
                "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications" );
        assertEquals( getName() + "0", ( String ) attrs.get( "permClassName" ).get() );
        assertNull( attrs.get( "description" ) );
        
        // add a permission via add( String, StringPermission )
//        PermissionModifier modifier = new PermissionModifier( dao, "mockApplication", getName() + "1" );
//        modifier.setDescription( "a non-null description" ).add();
//        attrs = ctx.getAttributes( "permName=" + getName()
//            + "1, ou=permissions, appName=mockApplication, ou=applications" );
//        assertEquals( getName() + "1", ( String ) attrs.get( "permName" ).get() );
//        assertNotNull( attrs.get( "description" ) );
//        assertEquals( "a non-null description", ( String ) attrs.get( "description" ).get() );
//        dao.delete( "mockApplication", getName() + "1" );

        // test the lookup of the newly added permission
//        Permission permission = dao.load( "mockApplication", getName() + "0" );
//        assertNotNull( permission );
//        assertEquals( getName() + "0", permission.getName() );
//        assertEquals( null, permission.getDescription() );
        
        // test the modification of the newly added permission
//        modifier = permission.modifier().setDescription( "updated description" );
//        modifier.modify();
//        permission = dao.load( "mockApplication", getName() + "0" );
//        assertNotNull( permission );
//        assertEquals( getName() + "0", permission.getName() );
//        assertEquals( "updated description", permission.getDescription() );
        
        // test the rename of the updated permission 
//        permission = dao.rename( getName()+ "0renamed", permission );
//        assertNotNull( permission );
//        assertEquals( getName() + "0renamed", permission.getName() );
//        assertEquals( "updated description", permission.getDescription() );
//        permission = dao.load( "mockApplication", getName()+ "0renamed" );
//        assertNotNull( permission );
//        assertEquals( getName() + "0renamed", permission.getName() );
//        assertEquals( "updated description", permission.getDescription() );
        
        // test the delete of the newly added permission
//        dao.delete( "mockApplication", getName() + "0renamed" );
//        try
//        {
//            permission = dao.load( "mockApplication", getName() + "0renamed" );
//            fail( "should never get here" );
//        }
//        catch ( NoSuchEntryException e )
//        {
//        }
        
        // test the permissionNameIterator() method
        /*
        Iterator iterator = dao.permissionClassNameIterator( "mockApplication" );
        Set permNames = new HashSet();
        while( iterator.hasNext() )
        {
            permNames.add( iterator.next() );
        }
        assertEquals( 10, permNames.size() );
        assertFalse( permNames.contains( "bogus" ) );
        assertTrue( permNames.contains( "mockPerm0" ) );
        assertTrue( permNames.contains( "mockPerm1" ) );
        assertTrue( permNames.contains( "mockPerm2" ) );
        assertTrue( permNames.contains( "mockPerm3" ) );
        assertTrue( permNames.contains( "mockPerm4" ) );
        assertTrue( permNames.contains( "mockPerm5" ) );
        assertTrue( permNames.contains( "mockPerm6" ) );
        assertTrue( permNames.contains( "mockPerm7" ) );
        assertTrue( permNames.contains( "mockPerm8" ) );
        assertTrue( permNames.contains( "mockPerm9" ) );
*/
        // test the permissionIterator() method
//        iterator = dao.permissionIterator( "mockApplication" );
//        Set perms = new HashSet();
//        while( iterator.hasNext() )
//        {
//            permission = ( Permission ) iterator.next();
//            perms.add( permission );
//            assertTrue( permNames.contains( permission.getName() ) );
//        }
//        assertEquals( 10, perms.size() );
    }
    
    
    public void testApplicationDao() throws Exception
    {
        // get and modify the application
        Application app = admin.getApplication( "mockApplication" );
        assertNotNull( app );
        assertEquals( "mockApplication", app.getName() );
        assertEquals( null, app.getDescription() );
        assertEquals( "testing", app.getPassword() );
        app = app.modifier().setDescription( "test" ).modify();
        assertEquals( "test", app.getDescription() );
        
        // create a new application
        app = admin.newApplication( "test" ).add();
        app = app.modifier().setDescription( "test" ).setPassword( "secret" ).modify();
        assertNotNull( app );
        assertEquals( "test", app.getName() );
        assertEquals( "test", app.getDescription() );
        assertEquals( "secret", app.getPassword() );
        app = admin.getApplication( "test" );
        assertNotNull( app );
        assertEquals( "test", app.getName() );
        assertEquals( "test", app.getDescription() );
        assertEquals( "secret", app.getPassword() );
        
        // rename the new application
        app = app.modifier().rename( "newName" );
        app = admin.getApplication( "newName" );
        assertNotNull( app );
        assertEquals( "newName", app.getName() );
        assertEquals( "test", app.getDescription() );
        assertEquals( "secret", app.getPassword() );
        
        // create a permission for the new application
//        Permission perm = app.modifier().newPermission( "testPerm" ).
//            setDescription( "test description" ).add();
//        assertEquals( "newName", perm.getApplicationName() );
//        assertEquals( "testPerm", perm.getName() );
//        assertEquals( "test description", perm.getDescription() );
//        perm = app.getPermission( perm.getName() );
//        assertEquals( "newName", perm.getApplicationName() );
//        assertEquals( "testPerm", perm.getName() );
//        assertEquals( "test description", perm.getDescription() );
        
        // delete the permission and make sure it's not there
//        perm.modifier().delete();
//        try
//        {
//            app.getPermission( perm.getName() );
//            fail( "should never get here" );
//        }
//        catch( DataAccessException e )
//        {
//        }
        
        // delete the application and make sure it's not there
        app.modifier().delete();
        try
        {
            admin.getApplication( "test" );
        }
        catch( DataAccessException e )
        {
        }
    }
    
    
    public void testProfileIteration() throws Exception
    {
        Application app = admin.getApplication( "mockApplication" );
        app.modifier().newProfile( "extra", "lockedout" ).add();
        for ( Iterator ii = app.profileIterator( "akarasulu" ); ii.hasNext(); /**/ )
        {
            Profile profile = ( Profile ) ii.next(); 
            assertEquals( "akarasulu", profile.getUser() );
        }
        for ( Iterator ii = app.profileIterator( "extra" ); ii.hasNext(); /**/ )
        {
            Profile profile = ( Profile ) ii.next();
            assertEquals( "extra", profile.getUser() );
        }
    }
    
    
    public void testRoleDao() throws Exception
    {
        Application app = admin.getApplication( "mockApplication" );
        
        // create a new role after changing modifier's description and grants
        Role role = app.modifier().newRole( "testRole" ).setDescription( "test role" )
//            .addPermissionClass( "mockPerm0" ).addPermissionClass( "mockPerm1" )
                .add();
        assertNotNull( role );
        assertEquals( "mockApplication", role.getApplicationName() );
        assertEquals( "testRole", role.getName() );
        assertEquals( "test role", role.getDescription() );
//        assertEquals( 2, role.getPermissionClasses().size() );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm0" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm1" ) );
//        assertFalse( role.getPermissionClasses().contains( "bogus" ) );
        
        // lookup and confirm values again
        role = app.getRole( "testRole" );
        assertNotNull( role );
        assertEquals( "mockApplication", role.getApplicationName() );
        assertEquals( "testRole", role.getName() );
        assertEquals( "test role", role.getDescription() );
//        assertEquals( 2, role.getPermissionClasses().size() );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm0" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm1" ) );
//        assertFalse( role.getPermissionClasses().contains( "bogus" ) );
        
        // remove existing grant, add two new ones, and modify
        role = role.modifier()
//                .removePermissionClass( "mockPerm1" ).addPermissionClass( "mockPerm2" )
//            .addPermissionClass( "mockPerm3" )
                .setDescription( "changed description" ).modify();
//        assertNotNull( role );
//        assertEquals( "changed description", role.getDescription() );
//        assertEquals( 3, role.getPermissionClasses().size() );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm0" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm2" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm3" ) );
//        assertFalse( role.getPermissionClasses().contains( "bogus" ) );
        
        // rename the role, test values, look it up again and test values again
        role = role.modifier().rename( "renamedRole" );
        assertNotNull( role );
        assertEquals( "mockApplication", role.getApplicationName() );
        assertEquals( "renamedRole", role.getName() );
        assertEquals( "changed description", role.getDescription() );
//        assertEquals( 3, role.getPermissionClasses().size() );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm0" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm2" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm3" ) );
//        assertFalse( role.getPermissionClasses().contains( "bogus" ) );

        role = app.getRole( "renamedRole" );
        assertNotNull( role );
        assertEquals( "mockApplication", role.getApplicationName() );
        assertEquals( "renamedRole", role.getName() );
        assertEquals( "changed description", role.getDescription() );
//        assertEquals( 3, role.getPermissionClasses().size() );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm0" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm2" ) );
//        assertTrue( role.getPermissionClasses().contains( "mockPerm3" ) );
//        assertFalse( role.getPermissionClasses().contains( "bogus" ) );
        
        // delete the role
        role.modifier().delete();
        try
        {
            app.getRole( "renamedRole" );
            fail( "should never get here" );
        }
        catch( NoSuchEntryException e )
        {
        }
    }


    public void testProfileDao() throws Exception
    {
        Application app = admin.getApplication( "mockApplication" );
        
        // create a new profile after changing modifier's description with permission and roles
//        Profile profile = app.modifier().newProfile( "testProfile", "testUser" ).setDescription( "test profile" )
//            .addPermissionClass( "mockPerm0" ).addPermissionClass( "mockPerm1" ).addDenial( "mockPerm4" ).addRole( "mockRole2" ).add();
//        assertNotNull( profile );
//        assertEquals( "mockApplication", profile.getApplicationName() );
//        assertEquals( "testProfile", profile.getId() );
//        assertEquals( "testUser", profile.getUser() );
//        assertEquals( "test profile", profile.getDescription() );
//        assertEquals( 2, profile.getPermissionClasses().size() );
//        Set<PermissionClass> permissionClasses = profile.getPermissionClasses();
//        assertTrue( permissionClasses.size() == 1 );
//        PermissionClass permissionClass = permissionClasses.iterator().next();
//        Set<PermissionActions> grants = permissionClass.getGrants();
//        assertTrue( profile.getPermissionClasses().contains( "mockPerm1" ) );
//        assertFalse( profile.getPermissionClasses().contains( "bogus" ) );
//        assertEquals( 1, profile.getDenials().size() );
//        assertTrue( profile.getDenials().contains( "mockPerm4" ) );
//        assertFalse( profile.getDenials().contains( "bogus" ) );
//        assertEquals( 1, profile.getRoles().size() );
//        assertTrue( profile.getRoles().contains( "mockRole2" ) );
//        assertFalse( profile.getRoles().contains( "bogus" ) );
        
/*
        // lookup and confirm values again
        profile = app.getProfile( "testProfile" );
        assertNotNull( profile );
        assertEquals( "mockApplication", profile.getApplicationName() );
        assertEquals( "testProfile", profile.getId() );
        assertEquals( "testUser", profile.getUser() );
        assertEquals( "test profile", profile.getDescription() );
        assertEquals( 2, profile.getPermissionClasses().size() );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm0" ) );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm1" ) );
        assertFalse( profile.getPermissionClasses().contains( "bogus" ) );
        assertEquals( 1, profile.getDenials().size() );
        assertTrue( profile.getDenials().contains( "mockPerm4" ) );
        assertFalse( profile.getDenials().contains( "bogus" ) );
        assertEquals( 1, profile.getRoles().size() );
        assertTrue( profile.getRoles().contains( "mockRole2" ) );
        assertFalse( profile.getRoles().contains( "bogus" ) );
        
        // remove existing grant, add two new ones, remove existing grant, add a role and modify
        profile = profile.modifier().removePermissionClass( "mockPerm1" ).addPermissionClass( "mockPerm2" )
            .addPermissionClass( "mockPerm3" ).removeDenial( "mockPerm4" ).addRole( "mockRole3" )
            .setDescription( "changed description" ).modify();
        assertNotNull( profile );
        assertEquals( "changed description", profile.getDescription() );
        assertEquals( 0, profile.getDenials().size() );
        assertEquals( 3, profile.getPermissionClasses().size() );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm0" ) );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm2" ) );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm3" ) );
        assertFalse( profile.getPermissionClasses().contains( "bogus" ) );
        assertEquals( 2, profile.getRoles().size() );
        assertTrue( profile.getRoles().contains( "mockRole2" ) );
        assertTrue( profile.getRoles().contains( "mockRole3" ) );
        assertFalse( profile.getRoles().contains( "bogus" ) );
        
        // rename the profile, test values, look it up again and test values again
        profile = profile.modifier().rename( "renamedProfile" );
        assertNotNull( profile );
        assertEquals( "mockApplication", profile.getApplicationName() );
        assertEquals( "renamedProfile", profile.getId() );
        assertEquals( "testUser", profile.getUser() );
        assertEquals( "changed description", profile.getDescription() );
        assertEquals( 0, profile.getDenials().size() );
        assertEquals( 3, profile.getPermissionClasses().size() );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm0" ) );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm2" ) );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm3" ) );
        assertFalse( profile.getPermissionClasses().contains( "bogus" ) );
        assertEquals( 2, profile.getRoles().size() );
        assertTrue( profile.getRoles().contains( "mockRole2" ) );
        assertTrue( profile.getRoles().contains( "mockRole3" ) );
        assertFalse( profile.getRoles().contains( "bogus" ) );

        profile = app.getProfile( "renamedProfile" );
        assertNotNull( profile );
        assertEquals( "mockApplication", profile.getApplicationName() );
        assertEquals( "renamedProfile", profile.getId() );
        assertEquals( "testUser", profile.getUser() );
        assertEquals( "changed description", profile.getDescription() );
        assertEquals( 0, profile.getDenials().size() );
        assertEquals( 3, profile.getPermissionClasses().size() );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm0" ) );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm2" ) );
        assertTrue( profile.getPermissionClasses().contains( "mockPerm3" ) );
        assertFalse( profile.getPermissionClasses().contains( "bogus" ) );
        assertEquals( 2, profile.getRoles().size() );
        assertTrue( profile.getRoles().contains( "mockRole2" ) );
        assertTrue( profile.getRoles().contains( "mockRole3" ) );
        assertFalse( profile.getRoles().contains( "bogus" ) );
        
        // delete the profile
        profile.modifier().delete();
        try
        {
            app.getProfile( "renamedProfile" );
            fail( "should never get here" );
        }
        catch( NoSuchEntryException e )
        {
        }
  */
    }
    
    
    private String getKerberosKeyAsString( String id, String realm, String password ) throws Exception
    {
        StringBuffer buf = new StringBuffer();
        String krb5PrincipalName = buf.append( id ).append( "@" ).append( realm.toUpperCase() ).toString();
        buf.setLength( 0 );
        KerberosPrincipal kerberosPrincipal = new KerberosPrincipal( krb5PrincipalName );
        KerberosKey key = new KerberosKey( kerberosPrincipal, password.toCharArray(), "DES" );
        return StringTools.utf8ToString( key.getEncoded() );
    }
    
    
    public void testLocalUserDao() throws Exception
    {
        assertFalse( admin.hasUser( "testLocalUser" ) );
        LocalUserModifier modifier = admin.newLocalUser( "testLocalUser", "joe", "smith", "secret" );
        modifier.setAddress1( "address1" ).setAddress2( "address2" ).setCity( "city" ).
            setCompany( "company" ).setCountry( "country" ).setStateProvRegion( "stateProvRegion" )
            .setZipPostalCode( "zipPostalCode" ).setEmail( "email" ).add();
        assertTrue( admin.hasUser( "testLocalUser" ) );
        
        LocalUser user = ( LocalUser ) admin.getUser( "testLocalUser" );
        assertNotNull( user );
        assertEquals( "testLocalUser", user.getId() );
        assertEquals( "joe", user.getFirstName() );
        assertEquals( "smith", user.getLastName() );
        assertEquals( "secret", user.getPassword() );
        assertEquals( "address1", user.getAddress1() );
        assertEquals( "address2", user.getAddress2() );
        assertEquals( "city", user.getCity() );
        assertEquals( "stateProvRegion", user.getStateProvRegion() );
        assertEquals( "company", user.getCompany() );
        assertEquals( "country", user.getCountry() );
        assertEquals( "zipPostalCode", user.getZipPostalCode() );
        assertEquals( "email", user.getEmail() );
        assertFalse( user.isDisabled() );
        
        // now let's check and make sure the kerberos key is properly set
        Attributes attrs = this.ctx.getAttributes( "uid=testLocalUser,ou=Users" );
        assertEquals( getKerberosKeyAsString( "testLocalUser", "EXAMPLE.COM", "secret" ), 
            attrs.get( "krb5Key" ).get() );
        
        user = user.modifier().setDescription( "test description" )
            .setDisabled( true ).modify();
        assertEquals( "test description", user.getDescription() );
        assertEquals( "secret", user.getPassword() );
        assertTrue( user.isDisabled() ); 
        
        user = user.modifier().setPassword( "reset" )
            .setAddress1( "address1-" ).setAddress2( "address2-" ).setCity( "city-" )
            .setCompany( "company-" ).setCountry( "country-" ).setStateProvRegion( "stateProvRegion-" )
            .setZipPostalCode( "zipPostalCode-" ).setEmail( "email-" ).modify();
        assertEquals( "reset", user.getPassword() );
        user = ( LocalUser ) admin.getUser( "testLocalUser" );
        assertNotNull( user );
        assertEquals( "reset", user.getPassword() );
        assertEquals( "address1-", user.getAddress1() );
        assertEquals( "address2-", user.getAddress2() );
        assertEquals( "city-", user.getCity() );
        assertEquals( "stateProvRegion-", user.getStateProvRegion() );
        assertEquals( "company-", user.getCompany() );
        assertEquals( "country-", user.getCountry() );
        assertEquals( "zipPostalCode-", user.getZipPostalCode() );
        assertEquals( "email-", user.getEmail() );

        // now let's check and make sure the kerberos key is properly set for the new password
        attrs = this.ctx.getAttributes( "uid=testLocalUser,ou=Users" );
        assertEquals( getKerberosKeyAsString( "testLocalUser", "EXAMPLE.COM", "reset" ), 
            attrs.get( "krb5Key" ).get() );
        assertEquals( "TRUE", attrs.get( Constants.KRB5_DISABLED_ID ).get() );

        user = user.modifier().setDisabled( false ).modify();
        attrs = this.ctx.getAttributes( "uid=testLocalUser,ou=Users" );
        assertEquals( "FALSE", attrs.get( Constants.KRB5_DISABLED_ID ).get() );

        user.modifier().delete();
        assertFalse( admin.hasUser( "testLocalUser" ) );
    }
    
    
    public void testHauskeysUserDao() throws Exception
    {
        assertFalse( admin.hasUser( "testHauskeysUser" ) );
        HauskeysUserModifier modifier = admin.newHauskeysUser( "testHauskeysUser", "joe", "smith", "secret" );
        modifier.setAddress1( "address1" ).setAddress2( "address2" ).setCity( "city" ).
        setCompany( "company" ).setCountry( "country" ).setStateProvRegion( "stateProvRegion" )
        .setZipPostalCode( "zipPostalCode" ).setLabel( "testHauskeysUser" ).setNotifyBy( "sms" ).add();
        assertTrue( admin.hasUser( "testHauskeysUser" ) );
        
        HauskeysUser user = ( HauskeysUser ) admin.getUser( "testHauskeysUser" );
        assertNotNull( user );
        assertEquals( "testHauskeysUser", user.getId() );
        assertEquals( "joe", user.getFirstName() );
        assertEquals( "smith", user.getLastName() );
        assertEquals( "secret", user.getPassword() );
        assertEquals( "secret", user.getPassword() );
        assertEquals( "address1", user.getAddress1() );
        assertEquals( "address2", user.getAddress2() );
        assertEquals( "city", user.getCity() );
        assertEquals( "stateProvRegion", user.getStateProvRegion() );
        assertEquals( "company", user.getCompany() );
        assertEquals( "country", user.getCountry() );
        assertEquals( "zipPostalCode", user.getZipPostalCode() );
        assertFalse( user.isDisabled() );
        
        // now let's check and make sure the kerberos key is properly set
        Attributes attrs = this.ctx.getAttributes( "uid=testHauskeysUser,ou=Users" );
        assertEquals( getKerberosKeyAsString( "testHauskeysUser", "EXAMPLE.COM", "secret" ), 
            attrs.get( "krb5Key" ).get() );
        
        user = user.modifier().setDescription( "test description" )
            .setDisabled( true ).modify();
        assertEquals( "test description", user.getDescription() );
        assertEquals( "secret", user.getPassword() );
        assertTrue( user.isDisabled() ); 
        
        user = user.modifier().setPassword( "reset" )
            .setAddress1( "address1-" ).setAddress2( "address2-" ).setCity( "city-" )
            .setCompany( "company-" ).setCountry( "country-" ).setStateProvRegion( "stateProvRegion-" )
            .setZipPostalCode( "zipPostalCode-" ).modify();
        assertEquals( "reset", user.getPassword() );
        user = ( HauskeysUser ) admin.getUser( "testHauskeysUser" );
        assertNotNull( user );
        assertEquals( "reset", user.getPassword() );
        assertEquals( "address1-", user.getAddress1() );
        assertEquals( "address2-", user.getAddress2() );
        assertEquals( "city-", user.getCity() );
        assertEquals( "stateProvRegion-", user.getStateProvRegion() );
        assertEquals( "company-", user.getCompany() );
        assertEquals( "country-", user.getCountry() );
        assertEquals( "zipPostalCode-", user.getZipPostalCode() );

        // now let's check and make sure the kerberos key is properly set for the new password
        attrs = this.ctx.getAttributes( "uid=testHauskeysUser,ou=Users" );
        assertEquals( getKerberosKeyAsString( "testHauskeysUser", "EXAMPLE.COM", "reset" ), 
            attrs.get( "krb5Key" ).get() );
        assertEquals( "TRUE", attrs.get( Constants.KRB5_DISABLED_ID ).get() );

        user = user.modifier().setDisabled( false ).modify();
        attrs = this.ctx.getAttributes( "uid=testHauskeysUser,ou=Users" );
        assertEquals( "FALSE", attrs.get( Constants.KRB5_DISABLED_ID ).get() );

        user.modifier().delete();
        assertFalse( admin.hasUser( "testHauskeysUser" ) );
    }
    
    
    public void testExternalUserDao() throws Exception
    {
        final String uid = "testExternalUser";
        final String referral = "ldap://localhost:10389/uid=someExternalUser,ou=Users,dc=example,dc=com";
        assertFalse( admin.hasUser( uid ) );
        ExternalUserModifier modifier = admin.newExternalUser( uid, referral );
        modifier.add();
        assertTrue( admin.hasUser( uid ) );
        
        ExternalUser user = ( ExternalUser ) admin.getUser( uid );
        assertNotNull( user );
        assertEquals( uid, user.getId() );
        assertEquals( referral, user.getReferral() );
        assertFalse( user.isDisabled() );
        
        user = user.modifier().setDescription( "test description" )
            .setDisabled( true ).modify();
        assertEquals( "test description", user.getDescription() );
        assertTrue( user.isDisabled() ); 
        
        // now let's check and make sure the kerberos key is properly set for the new password
        Attributes attrs = this.ctx.getAttributes( "uid=" + uid + ",ou=Users" );
        assertEquals( "TRUE", attrs.get( Constants.KRB5_DISABLED_ID ).get() );

        user = user.modifier().setDisabled( false ).modify();
        attrs = this.ctx.getAttributes( "uid=" + uid + ",ou=Users" );
        assertEquals( "FALSE", attrs.get( Constants.KRB5_DISABLED_ID ).get() );
        
        user.modifier().delete();
        assertFalse( admin.hasUser( uid ) );
    }
    
    
    public void testGroupDao() throws Exception
    {
        GroupModifier modifier = admin.newGroup( "testGroup", "firstMember" );
        modifier.add();
        
        Group group = admin.getGroup( "testGroup" );
        assertEquals( "testGroup", group.getName() );
        assertEquals( 1, group.getMembers().size() );
        assertTrue( group.getMembers().contains( "firstMember" ) );
        
        group = group.modifier().addMember( "secondMember" ).addMember( "thirdMember" ).modify();
        assertEquals( "testGroup", group.getName() );
        assertEquals( 3, group.getMembers().size() );
        assertTrue( group.getMembers().contains( "firstMember" ) );
        assertTrue( group.getMembers().contains( "secondMember" ) );
        assertTrue( group.getMembers().contains( "thirdMember" ) );
        
        group = group.modifier().removeMember( "secondMember" ).modify();
        assertEquals( "testGroup", group.getName() );
        assertEquals( 2, group.getMembers().size() );
        assertTrue( group.getMembers().contains( "firstMember" ) );
        assertTrue( group.getMembers().contains( "thirdMember" ) );
        
        group = group.modifier().addMember( "secondMember" ).removeMember( "thirdMember" ).modify();
        assertEquals( 2, group.getMembers().size() );
        assertTrue( group.getMembers().contains( "firstMember" ) );
        assertTrue( group.getMembers().contains( "secondMember" ) );
        
        group = group.modifier().rename( "renamedGroup" );
        assertEquals( "renamedGroup", group.getName() );
        assertEquals( 2, group.getMembers().size() );
        assertTrue( group.getMembers().contains( "firstMember" ) );
        assertTrue( group.getMembers().contains( "secondMember" ) );
        group = admin.getGroup( "renamedGroup" );
        assertEquals( "renamedGroup", group.getName() );
        assertEquals( 2, group.getMembers().size() );
        assertTrue( group.getMembers().contains( "firstMember" ) );
        assertTrue( group.getMembers().contains( "secondMember" ) );
        try
        {
            group = admin.getGroup( "testGroup" );
            fail();
        }
        catch ( NoSuchEntryException e )
        {
            assertEquals( "renamedGroup", group.getName() );
            assertEquals( 2, group.getMembers().size() );
            assertTrue( group.getMembers().contains( "firstMember" ) );
            assertTrue( group.getMembers().contains( "secondMember" ) );
        }
        
        group.modifier().delete();
        try
        {
            group = admin.getGroup( "renamedGroup" );
            fail();
        }
        catch ( NoSuchEntryException e )
        {
        }        
    }
}
