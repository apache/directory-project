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
package org.safehaus.triplesec.store.interceptor;


import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.directory.server.core.unit.AbstractAdminTestCase;
import org.apache.directory.server.core.schema.bootstrap.SystemSchema;
import org.apache.directory.server.core.schema.bootstrap.CoreSchema;
import org.apache.directory.server.core.schema.bootstrap.Krb5kdcSchema;
import org.apache.directory.server.core.configuration.Configuration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.MutableInterceptorConfiguration;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.shared.ldap.message.LockableAttributeImpl;
import org.apache.directory.shared.ldap.message.LockableAttributesImpl;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.safehaus.triplesec.store.ProfileObjectFactory;
import org.safehaus.triplesec.store.ProfileStateFactory;
import org.safehaus.triplesec.store.schema.SafehausSchema;


/**
 * Test case for the PolicyProtectionInterceptor.
 *
 * @author Trustin Lee
 * @version $Rev: 946 $, $Date: 2006-09-18 22:59:56 -0400 (Mon, 18 Sep 2006) $
 */
public class ApplicationACIManagerITest extends AbstractAdminTestCase
{
    private DirContext ctx;


    public void setUp() throws Exception
    {
        Set schemas = super.configuration.getBootstrapSchemas();
        schemas.add( new CoreSchema() );
        schemas.add( new SystemSchema() );
        schemas.add( new Krb5kdcSchema() );
        schemas.add( new SafehausSchema() );
        super.configuration.setBootstrapSchemas( schemas );
        super.configuration.setShutdownHookEnabled( false );
        
        MutablePartitionConfiguration partitionCfg = new MutablePartitionConfiguration();
        partitionCfg.setName( "example" );
        partitionCfg.setSuffix( "dc=example,dc=com" );
        Attributes ctxEntry = new LockableAttributesImpl();
        ctxEntry.put( "objectClass", "top" );
        ctxEntry.put( "dc", "example" );
        ctxEntry.put( "administrativeRole", "accessControlSpecificArea" );
        partitionCfg.setContextEntry( ctxEntry );
        partitionCfg.setContextPartition( new JdbmPartition() );

        Set partitions = super.configuration.getContextPartitionConfigurations();
        partitions.add( partitionCfg );
        super.configuration.setContextPartitionConfigurations( partitions );

        List interceptors = super.configuration.getInterceptorConfigurations();
        MutableInterceptorConfiguration interceptorCfg = new MutableInterceptorConfiguration();
        interceptorCfg.setName( "protector" );
        interceptorCfg.setInterceptor( new PolicyProtectionInterceptor() );
        interceptors.add( interceptorCfg );
        super.configuration.setInterceptorConfigurations( interceptors );
        super.configuration.setAccessControlEnabled( true );
        
        super.overrideEnvironment( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );
        super.overrideEnvironment( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
        super.setLdifPath( "/interceptor.ldif", getClass() );
        super.setUp();

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.core.jndi.CoreContextFactory" );
        env.put( Context.PROVIDER_URL, "" );
        env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, "secret" );
        env.put( Configuration.JNDI_KEY, super.configuration );
        env.put( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
        env.put( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );

        ctx = new InitialDirContext( env );
    }


    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    
    private void assertNoAccessToAdminGroupByApp( String appName, String userPassword ) throws Exception
    {
        if ( userPassword == null )
        {
            userPassword = "secret";
        }
        
        LdapDN dn = new LdapDN( "appName="+appName+",ou=Applications,dc=example,dc=com" );

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.core.jndi.CoreContextFactory" );
        env.put( Context.PROVIDER_URL, "dc=example,dc=com" );
        env.put( Context.SECURITY_PRINCIPAL, dn.getUpName() );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, userPassword );
        env.put( Configuration.JNDI_KEY, super.configuration );
        env.put( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
        env.put( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );

        InitialDirContext ctx = new InitialDirContext( env );

        try
        {
            ctx.lookup( "cn=" + appName + "AdminGroup,ou=Groups" );
            fail( dn + " should not have access to it's admin group" );
        }
        catch( NoPermissionException e )
        {
        }
    }
    
    
    private boolean aciItemsExist( String appName ) throws Exception
    {
        // check for the Aci just for the application's access
        LdapDN dn = new LdapDN( "cn="+appName+"Aci,dc=example,dc=com" );
        
        try
        {
            Attributes attrs = ctx.getAttributes( dn, new String[] { "cn", "objectClass", 
                "subtreeSpecification", "prescriptiveACI" } );
            
            if ( attrs == null )
            {
                return false;
            }
            
            Attribute prescriptiveACI = attrs.get( "prescriptiveACI" );

            if ( prescriptiveACI == null )
            {
                return false;
            }
            
            return true;
        }
        catch ( NameNotFoundException e )
        {
            return false;
        }
    }

    
    private boolean adminGroupExists( String appName ) throws Exception
    {
        LdapDN dn = new LdapDN( "cn="+appName+"AdminGroup,ou=Groups,dc=example,dc=com" );

        try
        {
            Attributes attrs = ctx.getAttributes( dn );
            if ( attrs == null )
            {
                return false;
            }
            
            Attribute uniqueMembers = attrs.get( "uniqueMember" );

            if ( uniqueMembers == null )
            {
                return false;
            }
            
            return true;
        }
        catch ( NameNotFoundException e )
        {
            return false;
        }
    }
    
    
    private DirContext getAppContextAsApp( String appName ) throws NamingException
    {
        return getAppContextAsApp( appName, null );
    }
    
    
    private DirContext getAppContextAsApp( String appName, String userPassword ) throws NamingException
    {
        if ( userPassword == null )
        {
            userPassword = "secret";
        }
        
        LdapDN dn = new LdapDN( "appName="+appName+",ou=Applications,dc=example,dc=com" );

        Hashtable env = new Hashtable();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.core.jndi.CoreContextFactory" );
        env.put( Context.PROVIDER_URL, dn.getUpName() );
        env.put( Context.SECURITY_PRINCIPAL, dn.getUpName() );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_CREDENTIALS, userPassword );
        env.put( Configuration.JNDI_KEY, super.configuration );
        env.put( Context.STATE_FACTORIES, ProfileStateFactory.class.getName() );
        env.put( Context.OBJECT_FACTORIES, ProfileObjectFactory.class.getName() );

        return new InitialDirContext( env );
    }
    
    
    private void createApplication( String appName, String userPassword ) throws NamingException
    {
        // create the main application entry
        Attributes attrs = new LockableAttributesImpl();
        Attribute oc = new LockableAttributeImpl( "objectClass" );
        oc.add( "top" );
        oc.add( "policyApplication" );
        attrs.put( oc );
        attrs.put( "appName", appName );
        if ( userPassword != null )
        {
            attrs.put( "userPassword", userPassword );
        }
        LdapDN dn = new LdapDN( "appName="+appName+",ou=Applications,dc=example,dc=com" );
        ctx.createSubcontext( dn, attrs );
        
        // create ou=permissions
        attrs = new LockableAttributesImpl();
        oc = new LockableAttributeImpl( "objectClass" );
        oc.add( "top" );
        oc.add( "organizationalUnit" );
        attrs.put( oc );
        attrs.put( "ou", "permissions" );
        dn = new LdapDN( "ou=permissions,appName="+appName+",ou=Applications,dc=example,dc=com" );
        ctx.createSubcontext( dn, attrs );

        // create ou=roles
        attrs = new LockableAttributesImpl();
        oc = new LockableAttributeImpl( "objectClass" );
        oc.add( "top" );
        oc.add( "organizationalUnit" );
        attrs.put( oc );
        attrs.put( "ou", "roles" );
        dn = new LdapDN( "ou=roles,appName="+appName+",ou=Applications,dc=example,dc=com" );
        ctx.createSubcontext( dn, attrs );

        // create ou=profiles
        attrs = new LockableAttributesImpl();
        oc = new LockableAttributeImpl( "objectClass" );
        oc.add( "top" );
        oc.add( "organizationalUnit" );
        attrs.put( oc );
        attrs.put( "ou", "profiles" );
        dn = new LdapDN( "ou=profiles,appName="+appName+",ou=Applications,dc=example,dc=com" );
        ctx.createSubcontext( dn, attrs );
    }
    
    
    public void addAppUserToAdminGroup( String appName ) throws NamingException
    {
        LdapDN dn = new LdapDN( "appName="+appName+",ou=Applications,dc=example,dc=com" );
        Attributes attrs = new LockableAttributesImpl();
        attrs.put( "uniqueMember", dn.getUpName() );
        
        ctx.modifyAttributes( "cn=" + appName + "AdminGroup,ou=Groups,dc=example,dc=com", 
            DirContext.ADD_ATTRIBUTE, attrs );
    }
    
    
    private boolean canWriteToPermissions( String appName ) throws NamingException
    {
        DirContext appUserCtx = getAppContextAsApp( appName );
        Attributes attrs = new LockableAttributesImpl();
        attrs.put( "objectClass", "policyPermission" );
        attrs.put( "permName", "testPerm" );
        
        try
        {
            appUserCtx.createSubcontext( "permName=testPerm,ou=permissions", attrs );
            return true;
        }
        catch ( NoPermissionException e )
        {
            return false;
        }
        finally
        {
            try
            {
                appUserCtx.destroySubcontext( "permName=testPerm,ou=permissions" );
            }
            catch( Throwable t )
            {
            }
        }
    }
    
    
    public void testAddApplication() throws Exception
    {
        createApplication( "testApp", "secret" );
        assertTrue( adminGroupExists( "testApp" ) );
        assertTrue( aciItemsExist( "testApp" ) );
        assertNoAccessToAdminGroupByApp( "testApp", "secret" );
        assertFalse( canWriteToPermissions( "testApp" ) );
        addAppUserToAdminGroup( "testApp" );
        assertTrue( canWriteToPermissions( "testApp" ) );
    }
    

    public void testRemoveApplication() throws Exception
    {
        testAddApplication();
        destroyApplication( "testApp" );
        assertFalse( adminGroupExists( "testApp" ) );
        assertFalse( aciItemsExist( "testApp" ) );
    }
    

    private void destroyApplication( String appName ) throws Exception
    {
        DirContext appCtx = ( DirContext ) ctx.lookup( "appName="+appName+",ou=Applications,dc=example,dc=com" );
        appCtx.destroySubcontext( "ou=permissions" );
        appCtx.destroySubcontext( "ou=profiles" );
        appCtx.destroySubcontext( "ou=roles" );
        appCtx.close();
        
        ctx.destroySubcontext( "appName="+appName+",ou=Applications,dc=example,dc=com" );
    }


    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( ApplicationACIManagerITest.class );
    }
}

