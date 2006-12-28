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
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;

import junit.framework.Assert;

import org.apache.directory.server.core.unit.AbstractAdminTestCase;
import org.apache.directory.server.core.schema.bootstrap.SystemSchema;
import org.apache.directory.server.core.schema.bootstrap.CoreSchema;
import org.apache.directory.server.core.schema.bootstrap.Krb5kdcSchema;
import org.apache.directory.server.core.configuration.Configuration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.MutableInterceptorConfiguration;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.safehaus.triplesec.store.ProfileObjectFactory;
import org.safehaus.triplesec.store.ProfileStateFactory;
import org.safehaus.triplesec.store.schema.SafehausSchema;


/**
 * Test case for the PolicyProtectionInterceptor.
 *
 * @author Trustin Lee
 * @version $Rev: 957 $, $Date: 2006-09-22 09:03:23 -0400 (Fri, 22 Sep 2006) $
 */
public class PolicyProtectionInterceptorITest extends AbstractAdminTestCase
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
        super.configuration.setAccessControlEnabled( true );
        
        MutablePartitionConfiguration partitionCfg = new MutablePartitionConfiguration();
        partitionCfg.setName( "example" );
        partitionCfg.setSuffix( "dc=example,dc=com" );
        Attributes ctxEntry = new BasicAttributes();
        ctxEntry.put( "objectClass", "top" );
        ctxEntry.put( "dc", "example" );
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


    public void testAdd() throws Exception
    {
        Attribute attr;

        // Adding unrelated entries should be OK.
        ctx.bind( "ou=test,dc=example,dc=com", null, new BasicAttributes( "objectClass", "top" ) );

        // Test adding permissions
        Attributes perm = new BasicAttributes();
        attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attr.add( "policyPermission" );
        perm.put( attr );
        perm.put( "permName", "permX" );

        _testAdd( "permName=permX", "permName=mockPerm0", "ou=permissions", perm );

        // Test adding roles
        Attributes role = new BasicAttributes();
        attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attr.add( "policyRole" );
        role.put( attr );
        role.put( "roleName", "roleX" );

        _testAdd( "roleName=roleX", "roleName=mockRole0", "ou=roles", role );

        // Test adding profiles
        Attributes profile = new BasicAttributes();
        attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attr.add( "policyProfile" );
        profile.put( attr );
        profile.put( "profileId", "profileX" );
        profile.put( "user", "akarasulu" );

        _testAdd( "profileId=profileX", "profileId=mockProfile0", "ou=profiles", profile );
        
        
        // Test adding a role with non-existing permissions
        role = new BasicAttributes();
        attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attr.add( "policyRole" );
        role.put( attr );
        role.put( "roleName", "roleY" );
        role.put( "grants", "unknownPerm" );
        try
        {
            ctx.bind(
                    "roleName=roleY,ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
                    null, role);
            Assert.fail();
        }
        catch( SchemaViolationException e )
        {
            // OK
        }

        // Test adding a profile with a non-existing role
        profile = new BasicAttributes();
        attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attr.add( "policyProfile" );
        profile.put( attr );
        profile.put( "profileId", "profileY" );
        profile.put( "roles", "unknownRole" );
        
        try
        {
            ctx.bind(
                    "profileId=profileY,ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
                    null, profile);
            Assert.fail();
        }
        catch( SchemaViolationException e )
        {
            // OK
        }
        
        // Test adding a profile with non-existing permissions
        profile = new BasicAttributes();
        attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attr.add( "policyProfile" );
        profile.put( attr );
        profile.put( "uid", "profileY" );
        profile.put( "grants", "unknownPerm" );
        
        try
        {
            ctx.bind(
                    "profileId=profileY,ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
                    null, profile);
            Assert.fail();
        }
        catch( SchemaViolationException e )
        {
            // OK
        }
        
        // Test adding non-existing permission to a role
        try
        {
            ctx.modifyAttributes(
                    "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
                    DirContext.ADD_ATTRIBUTE,
                    new BasicAttributes( "grants", "unknownPerm" ) );
            Assert.fail();
        }
        catch( SchemaViolationException e )
        {
            // OK
        }
        
        // Test adding non-existing permission to a profile
        try
        {
            ctx.modifyAttributes(
                    "profileId=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
                    DirContext.ADD_ATTRIBUTE,
                    new BasicAttributes( "grants", "unknownPerm" ) );
            Assert.fail();
        }
        catch( SchemaViolationException e )
        {
            // OK
        }

        // Test adding non-existing role to a profile
        try
        {
            ctx.modifyAttributes(
                    "profileId=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
                    DirContext.ADD_ATTRIBUTE,
                    new BasicAttributes( "roles", "unknownRole" ) );
            Assert.fail();
        }
        catch( SchemaViolationException e )
        {
            // OK
        }
    }


    private void _testAdd( String rn, String siblingRN, String parentRN, Attributes entry ) throws NamingException
    {
        try
        {
            ctx.bind( rn + ", dc=example,dc=com", null, entry );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        try
        {
            ctx.bind( rn + ", ou=applications, dc=example,dc=com", null, entry );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        try
        {
            ctx.bind( rn + ", appName=mockApplication, ou=applications, dc=example,dc=com", null, entry );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        Attributes wrongEntry = ( Attributes ) entry.clone();
        wrongEntry.put( "objectClass", "top" ); // Remove other classes

        try
        {
            ctx.bind( rn + ", " + siblingRN + ", " + parentRN
                + ", appName=mockApplication, ou=applications, dc=example,dc=com", null, wrongEntry );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        ctx.bind( rn + ", " + parentRN + ", appName=mockApplication, ou=applications, dc=example,dc=com", null, entry );

        try
        {
            ctx.bind( rn + ", " + siblingRN + ", " + parentRN
                + ", appName=mockApplication, ou=applications, dc=example,dc=com", null, entry );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
    }


    public void testDelete() throws Exception
    {
        // Test deleting non-policy entries
        ctx.unbind( "uid=akarasulu, ou=Users, dc=example,dc=com" );

        // Test deleting permissions not in use
        ctx.unbind( "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com" );

        // Test deleting roles not in use
        ctx.unbind( "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com" );

        // Test deleting permissions in use
        try
        {
            ctx.unbind( "permName=mockPerm9,ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        // Test deleting roles in use
        try
        {
            ctx.unbind( "roleName=mockRole1,ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        // Test deleting profiles (should be deleted without any confirmation)
        ctx.unbind( "profileId=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com" );
    }


    public void testModify1() throws Exception
    {
        // Test modifications on non-policy entry
        ctx.modifyAttributes( "uid=akarasulu, ou=Users, dc=example,dc=com", DirContext.ADD_ATTRIBUTE,
            new BasicAttributes( "telephonenumber", "+1 904 982 6888" ) );
        ctx.modifyAttributes( "uid=akarasulu, ou=Users, dc=example,dc=com", DirContext.REMOVE_ATTRIBUTE,
            new BasicAttributes( "telephonenumber", "+1 904 982 6888" ) );

        // Test modifications on permissions

        // test attribute is not a valid schema defined attribute

        //        ctx.modifyAttributes(
        //                "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.ADD_ATTRIBUTE,
        //                new BasicAttributes( "test", "test" ) );
        //        ctx.modifyAttributes(
        //                "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.REMOVE_ATTRIBUTE,
        //                new BasicAttributes( "test", "test" ) );
        //        try
        //        {
        //            ctx.modifyAttributes(
        //                    "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
        //                    DirContext.REMOVE_ATTRIBUTE,
        //                    new BasicAttributes( "objectclass", "policyPermission" ) );
        //            Assert.fail();
        //        }
        //        catch( SchemaViolationException e )
        //        {
        //            // OK
        //        }
        ctx.modifyAttributes(
            "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
            DirContext.ADD_ATTRIBUTE, new BasicAttributes( "objectclass", "inetOrgPerson" ) );
        ctx.modifyAttributes(
            "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
            DirContext.REMOVE_ATTRIBUTE, new BasicAttributes( "objectclass", "inetOrgPerson" ) );

        // Test modifications on roles
        //        ctx.modifyAttributes(
        //                "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.ADD_ATTRIBUTE,
        //                new BasicAttributes( "test", "test" ) );
        //        ctx.modifyAttributes(
        //                "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.REMOVE_ATTRIBUTE,
        //                new BasicAttributes( "test", "test" ) );
        //        try
        //        {
        //            ctx.modifyAttributes(
        //                    "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
        //                    DirContext.REMOVE_ATTRIBUTE,
        //                    new BasicAttributes( "objectclass", "policyRole" ) );
        //            Assert.fail();
        //        }
        //        catch( SchemaViolationException e )
        //        {
        //            // OK
        //        }
        ctx.modifyAttributes( "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
            DirContext.ADD_ATTRIBUTE, new BasicAttributes( "objectclass", "inetOrgPerson" ) );
        ctx.modifyAttributes( "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
            DirContext.REMOVE_ATTRIBUTE, new BasicAttributes( "objectclass", "inetOrgPerson" ) );

        // Test modifications on profiles
        //        ctx.modifyAttributes(
        //                "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.ADD_ATTRIBUTE,
        //                new BasicAttributes( "test", "test" ) );
        //        ctx.modifyAttributes(
        //                "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.REMOVE_ATTRIBUTE,
        //                new BasicAttributes( "test", "test" ) );
        //        try
        //        {
        //            ctx.modifyAttributes(
        //                    "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                    DirContext.REMOVE_ATTRIBUTE,
        //                    new BasicAttributes( "objectclass", "policyProfile" ) );
        //            Assert.fail();
        //        }
        //        catch( SchemaViolationException e )
        //        {
        //            // OK
        //        }
        //        ctx.modifyAttributes(
        //                "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.ADD_ATTRIBUTE,
        //                new BasicAttributes( "objectclass", "test" ) );
        //        ctx.modifyAttributes(
        //                "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                DirContext.REMOVE_ATTRIBUTE,
        //                new BasicAttributes( "objectclass", "test" ) );
    }


    public void testModify2() throws Exception
    {
        // Test modifications on non-policy entry
        ctx.modifyAttributes( "uid=akarasulu, ou=Users, dc=example,dc=com",
            new ModificationItem[]
                { new ModificationItem( DirContext.ADD_ATTRIBUTE, new BasicAttribute( "telephonenumber",
                    "+1 904 982 6888" ) ) } );
        ctx.modifyAttributes( "uid=akarasulu, ou=Users, dc=example,dc=com", new ModificationItem[]
            { new ModificationItem( DirContext.REMOVE_ATTRIBUTE, new BasicAttribute( "telephonenumber",
                "+1 904 982 6888" ) ) } );

        // Test modifications on permissions
        //        ctx.modifyAttributes(
        //                "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
        //                new ModificationItem[] {
        //                        new ModificationItem(
        //                                DirContext.ADD_ATTRIBUTE,
        //                                new BasicAttribute( "test", "test" ) )
        //                } );
        //        ctx.modifyAttributes(
        //                "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
        //                new ModificationItem[] {
        //                        new ModificationItem(
        //                                DirContext.REMOVE_ATTRIBUTE,
        //                                new BasicAttribute( "test", "test" ) )
        //                } );
        //        try
        //        {
        //            ctx.modifyAttributes(
        //                    "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
        //                    new ModificationItem[] {
        //                            new ModificationItem(
        //                                    DirContext.REMOVE_ATTRIBUTE,
        //                                    new BasicAttribute( "objectclass", "policyPermission" ) )
        //                    } );
        //            Assert.fail();
        //        }
        //        catch( SchemaViolationException e )
        //        {
        //            // OK
        //        }
        /* This test doesn't work thanks to ApacheDS bug.
         ctx.modifyAttributes(
         "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
         new ModificationItem[] {
         new ModificationItem(
         DirContext.ADD_ATTRIBUTE,
         new BasicAttribute( "objectclass", "unknown" ) )
         } );
         ctx.modifyAttributes(
         "permName=mockPerm8,ou=permissions,appName=mockApplication,ou=applications,dc=example",
         new ModificationItem[] {
         new ModificationItem(
         DirContext.REMOVE_ATTRIBUTE,
         new BasicAttribute( "objectclass", "unknown" ) )
         } );
         */

        // Test modifications on roles
        //        ctx.modifyAttributes(
        //                "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
        //                new ModificationItem[] {
        //                        new ModificationItem(
        //                                DirContext.ADD_ATTRIBUTE,
        //                                new BasicAttribute( "test", "test" ) )
        //                } );
        //        ctx.modifyAttributes(
        //                "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
        //                new ModificationItem[] {
        //                        new ModificationItem(
        //                                DirContext.REMOVE_ATTRIBUTE,
        //                                new BasicAttribute( "test", "test" ) )
        //                } );
        //        try
        //        {
        //            ctx.modifyAttributes(
        //                    "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
        //                    new ModificationItem[] {
        //                            new ModificationItem(
        //                                    DirContext.REMOVE_ATTRIBUTE,
        //                                    new BasicAttribute( "objectclass", "policyRole" ) )
        //                    } );
        //            Assert.fail();
        //        }
        //        catch( SchemaViolationException e )
        //        {
        //            // OK
        //        }
        /* This test doesn't work thanks to ApacheDS bug.
         ctx.modifyAttributes(
         "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
         new ModificationItem[] {
         new ModificationItem(
         DirContext.ADD_ATTRIBUTE,
         new BasicAttribute( "objectclass", "test" ) )
         } );
         ctx.modifyAttributes(
         "roleName=mockRole0,ou=roles,appName=mockApplication,ou=applications,dc=example",
         new ModificationItem[] {
         new ModificationItem(
         DirContext.REMOVE_ATTRIBUTE,
         new BasicAttribute( "objectclass", "test" ) )
         } );
         */

        // Test modifications on profiles
        //        ctx.modifyAttributes(
        //                "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                new ModificationItem[] {
        //                        new ModificationItem(
        //                                DirContext.ADD_ATTRIBUTE,
        //                                new BasicAttribute( "test", "test" ) )
        //                } );
        //        ctx.modifyAttributes(
        //                "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                new ModificationItem[] {
        //                        new ModificationItem(
        //                                DirContext.REMOVE_ATTRIBUTE,
        //                                new BasicAttribute( "test", "test" ) )
        //                } );
        //        try
        //        {
        //            ctx.modifyAttributes(
        //                    "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
        //                    new ModificationItem[] {
        //                            new ModificationItem(
        //                                    DirContext.REMOVE_ATTRIBUTE,
        //                                    new BasicAttribute( "objectclass", "policyProfile" ) )
        //                    } );
        //            Assert.fail();
        //        }
        //        catch( SchemaViolationException e )
        //        {
        //            // OK
        //        }
        /* This test doesn't work thanks to ApacheDS bug.
         ctx.modifyAttributes(
         "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
         new ModificationItem[] {
         new ModificationItem(
         DirContext.ADD_ATTRIBUTE,
         new BasicAttribute( "objectclass", "test" ) )
         } );
         ctx.modifyAttributes(
         "uid=mockProfile0,ou=profiles,appName=mockApplication,ou=applications,dc=example",
         new ModificationItem[] {
         new ModificationItem(
         DirContext.REMOVE_ATTRIBUTE,
         new BasicAttribute( "objectclass", "test" ) )
         } );
         */
    }


    public void testModifyRn() throws Exception
    {
        ctx.rename( "uid=akarasulu, ou=Users, dc=example,dc=com", "uid=akarasuluX, ou=Users, dc=example,dc=com" );

        // Test renaming group entries
        try
        {
            ctx.rename( "ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
                "ou=profilesX,appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
        try
        {
            ctx.rename( "ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
                "ou=rolesX,appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
        try
        {
            ctx.rename( "ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
                "ou=permissionsX,appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        // Test renaming entries not in use
        ctx.rename( "permName=mockPerm8, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
            "permName=mockPermX, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com" );
        ctx.rename( "roleName=mockRole0, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
            "roleName=mockRoleX, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com" );
        ctx.rename( "profileId=mockProfile0, ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
            "profileId=mockProfileX, ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com" );

        // Test renaming entries in use
        try
        {
            ctx.rename( "permName=mockPerm9, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
                "permName=mockPermY, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
        try
        {
            ctx.rename( "roleName=mockRole1, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
                "roleName=mockRoleY, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
    }


    public void testMove1() throws Exception
    {
        ctx.rename( "uid=akarasulu, ou=Users, dc=example,dc=com", "uid=akarasulu, dc=example,dc=com" );

        // Test renaming group entries
        try
        {
            ctx.rename( "ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
                "ou=profiles,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
        try
        {
            ctx.rename( "ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
                "ou=roles,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
        try
        {
            ctx.rename( "ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
                "ou=permissions,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }

        // Test renaming entries not in use
        ctx.rename( "permName=mockPerm8, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
            "permName=mockPerm8, ou=applications,dc=example,dc=com" );
        ctx.rename( "roleName=mockRole0, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
            "roleName=mockRole0, ou=applications,dc=example,dc=com" );
        ctx.rename( "profileId=mockProfile0, ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com",
            "profileId=mockProfile0, ou=applications,dc=example,dc=com" );

        // Test renaming entries in use
        try
        {
            ctx.rename( "permName=mockPerm9, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com",
                "permName=mockPerm9, appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
        try
        {
            ctx.rename( "roleName=mockRole1, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com",
                "roleName=mockRole1, appName=mockApplication,ou=applications,dc=example,dc=com" );
            Assert.fail();
        }
        catch ( SchemaViolationException e )
        {
            // OK
        }
    }


    /* This doesn't work thanks to ApacheDS problem
     public void testMove2() throws Exception
     {
     InvocationStack.getInstance().push(
     new Invocation(ctx, "move")
     );

     InterceptorChain chain = ContextFactoryService.getInstance().getConfiguration().getInterceptorChain();
     
     chain.move(
     new LdapName( "uid=akarasulu, ou=Users, dc=example,dc=com" ),
     new LdapName( "dc=example,dc=com" ),
     "uid=akarasuluX", true );

     // Test renaming group entries
     try
     {
     chain.move(
     new LdapName( "ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "ou=applications,dc=example,dc=com" ),
     "ou=profilesX", true );
     Assert.fail();
     }
     catch( SchemaViolationException e )
     {
     // OK
     }
     try
     {
     chain.move(
     new LdapName( "ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "ou=applications,dc=example,dc=com" ),
     "ou=rolesX", true );
     Assert.fail();
     }
     catch( SchemaViolationException e )
     {
     // OK
     }
     try
     {
     chain.move(
     new LdapName( "ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "ou=applications,dc=example,dc=com" ),
     "ou=permissionsX", true );
     Assert.fail();
     }
     catch( SchemaViolationException e )
     {
     // OK
     }
     
     // Test renaming entries not in use
     chain.move(
     new LdapName( "permName=mockPerm8, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "ou=applications,dc=example,dc=com" ),
     "permName=mockPermX", true );
     chain.move(
     new LdapName( "roleName=mockRole0, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "ou=applications,dc=example,dc=com" ),
     "roleName=mockRoleX", true );
     chain.move(
     new LdapName( "uid=mockProfile0, ou=profiles,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "ou=applications,dc=example,dc=com" ),
     "uid=mockProfileX", true );

     // Test renaming entries in use
     try
     {
     chain.move(
     new LdapName( "permName=mockPerm9, ou=permissions,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "appName=mockApplication,ou=applications,dc=example,dc=com" ),
     "permName=mockPermY", true);
     Assert.fail();
     }
     catch( SchemaViolationException e )
     {
     // OK
     }
     try
     {
     chain.move(
     new LdapName( "roleName=mockRole1, ou=roles,appName=mockApplication,ou=applications,dc=example,dc=com" ),
     new LdapName( "appName=mockApplication,ou=applications,dc=example,dc=com" ),
     "roleName=mockRoleY", true );
     Assert.fail();
     }
     catch( SchemaViolationException e )
     {
     // OK
     }
     }
     */

    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( PolicyProtectionInterceptorITest.class );
    }

}
