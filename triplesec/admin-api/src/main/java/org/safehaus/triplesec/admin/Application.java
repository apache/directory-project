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


import java.util.Date;
import java.util.Iterator;

import org.safehaus.triplesec.admin.dao.ApplicationDao;
import org.safehaus.triplesec.admin.dao.PermissionDao;
import org.safehaus.triplesec.admin.dao.ProfileDao;
import org.safehaus.triplesec.admin.dao.RoleDao;


public class Application extends AdministeredEntity
{
    private final RoleDao roleDao;
    private final ProfileDao profileDao;
    private final PermissionDao permissionDao;
    private final String name;
    private final String description;
    private final String password;
    private final ApplicationDao dao;
    
    
    public Application( String creatorsName, Date creationTimestamp, ApplicationDao dao, String name, 
        String description, String password, PermissionDao permissionDao, RoleDao roleDao, ProfileDao profileDao )
    {
        this( creatorsName, creationTimestamp, null, null, dao, name, description, password,
            permissionDao, roleDao, profileDao );
    }
    
    
    public Application( String creatorsName, Date creationTimestamp, String modifiersName, Date modifyTimestamp, 
        ApplicationDao dao, String name, String description, String userPassword, PermissionDao permissionDao, 
        RoleDao roleDao, ProfileDao profileDao )
    {
        super( creatorsName, creationTimestamp, modifiersName, modifyTimestamp );
        this.name = name;
        this.dao = dao;
        this.description = description;
        this.permissionDao = permissionDao;
        this.profileDao = profileDao;
        this.roleDao = roleDao;
        this.password = userPassword;
    }
    
    
    // -----------------------------------------------------------------------
    // Package friendly dao accessors
    // -----------------------------------------------------------------------
    
    
    PermissionDao getPermissionDao()
    {
        return permissionDao;
    }
    
    
    RoleDao getRoleDao()
    {
        return roleDao;
    }
    
    
    ProfileDao getProfileDao()
    {
        return profileDao;
    }
    
    
    // -----------------------------------------------------------------------
    // Property accessors
    // -----------------------------------------------------------------------
    
    
    public String getName()
    {
        return name;
    }
    
    
    public String getDescription()
    {
        return description;
    }
    
    
    public String getPassword()
    {
        return password;
    }
    
    
    // -----------------------------------------------------------------------
    // Accessors to contained entities
    // -----------------------------------------------------------------------
    
    
    public Permission getPermission( String permName ) throws DataAccessException
    {
        return permissionDao.load( name, permName );
    }
    
    
    public Role getRole( String roleName ) throws DataAccessException
    {
        return roleDao.load( name, roleName );
    }
    
    
    public Profile getProfile( String profileId ) throws DataAccessException
    {
        return profileDao.load( name, profileId );
    }
    
    
    // -----------------------------------------------------------------------
    // ReadOnly Iterator methods
    // -----------------------------------------------------------------------
    
    
    public Iterator permissionIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( permissionDao.permissionIterator( name ) );
    }
    
    
    public Iterator roleIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( roleDao.roleIterator( name ) );
    }
    
    
    public Iterator profileIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( profileDao.profileIterator( name ) );
    }
    
    
    public Iterator profileIterator( String user) throws DataAccessException
    {
        return new ReadOnlyIterator( profileDao.profileIterator( name, user ) );
    }
    
    
    // -----------------------------------------------------------------------
    // Modifier factory methods
    // -----------------------------------------------------------------------
    
    
    public ApplicationModifier modifier()
    {
        return new ApplicationModifier( dao, this );
    }
    
    
    public String toString()
    {
        return name;
    }
}
