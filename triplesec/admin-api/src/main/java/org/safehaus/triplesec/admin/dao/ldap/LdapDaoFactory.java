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
package org.safehaus.triplesec.admin.dao.ldap;


import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.dao.ApplicationDao;
import org.safehaus.triplesec.admin.dao.DaoFactory;
import org.safehaus.triplesec.admin.dao.ExternalUserDao;
import org.safehaus.triplesec.admin.dao.GroupDao;
import org.safehaus.triplesec.admin.dao.HauskeysUserDao;
import org.safehaus.triplesec.admin.dao.LocalUserDao;
import org.safehaus.triplesec.admin.dao.PermissionDao;
import org.safehaus.triplesec.admin.dao.ProfileDao;
import org.safehaus.triplesec.admin.dao.RoleDao;
import org.safehaus.triplesec.admin.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapDaoFactory extends DaoFactory
{
    private static final Logger log = LoggerFactory.getLogger( LdapDaoFactory.class );
    private DirContext ctx;
    

    public LdapDaoFactory( Properties props ) throws DataAccessException
    {
        try
        {
            ctx = new InitialLdapContext( props, null );
        }
        catch ( NamingException e )
        {
            String msg = "Failed to initialize LDAP context: " + e.getMessage();
            log.error( msg, e );
            throw new DataAccessException( msg );
        }
    }
    
    
    public PermissionDao getPermissionDao() throws DataAccessException
    {
        return new LdapPermissionDao( ctx );
    }
    
    
    public ApplicationDao getApplicationDao() throws DataAccessException
    {
        return new LdapApplicationDao( ctx, getPermissionDao(), getRoleDao(), getProfileDao() );
    }

    
    public RoleDao getRoleDao() throws DataAccessException
    {
        return new LdapRoleDao( ctx ); 
    }

    
    public ProfileDao getProfileDao() throws DataAccessException
    {
        return new LdapProfileDao( ctx ); 
    }


    public GroupDao getGroupDao() throws DataAccessException
    {
        return new LdapGroupDao( ctx ); 
    }
    
    
    public ExternalUserDao getExternalUserDao() throws DataAccessException
    {
        return new LdapExternalUserDao( ctx ); 
    }
    
    
    public LocalUserDao getLocalUserDao() throws DataAccessException
    {
        return new LdapLocalUserDao( ctx ); 
    }


    public HauskeysUserDao getHauskeysUserDao() throws DataAccessException
    {
        return new LdapHauskeysUserDao( ctx );
    }


    public UserDao getUserDao() throws DataAccessException
    {
        return new LdapUserDao( ctx, ( LdapExternalUserDao ) getExternalUserDao(), 
            ( LdapLocalUserDao ) getLocalUserDao(), ( LdapHauskeysUserDao ) getHauskeysUserDao() );
    }
    
    
    public void close() 
    {
        try
        {
            ctx.close();
        }
        catch ( NamingException e )
        {
            log.error( "Failed while trying to close context to triplesec server.", e );
        }
    }
}
