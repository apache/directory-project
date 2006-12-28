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

 
public class TriplesecAdmin
{
    private DaoFactory factory;
    private ApplicationDao applicationDao;
    private RoleDao roleDao;
    private ProfileDao profileDao;
    private PermissionDao permissionDao;
    private GroupDao groupDao;
    private ExternalUserDao externalUserDao;
    private LocalUserDao localUserDao;
    private HauskeysUserDao hauskeysUserDao;
    private UserDao userDao;
    
    
    public TriplesecAdmin( Properties props ) throws DataAccessException
    {
        factory = DaoFactory.createInstance( props );
        applicationDao = factory.getApplicationDao();
        permissionDao = factory.getPermissionDao();
        roleDao = factory.getRoleDao();
        profileDao = factory.getProfileDao();
        groupDao = factory.getGroupDao();
        externalUserDao = factory.getExternalUserDao();
        localUserDao = factory.getLocalUserDao();
        hauskeysUserDao = factory.getHauskeysUserDao();
        userDao = factory.getUserDao();
    }
    
    
    // -----------------------------------------------------------------------
    // User & Respective Modifier Read Operations
    // -----------------------------------------------------------------------
    
    
    public ExternalUserModifier newExternalUser( String id, String referral )
    {
        return new ExternalUserModifier( externalUserDao, id, referral );
    }
    
    
    public LocalUserModifier newLocalUser( String id, String firstName, String lastName, String password )
    {
        return new LocalUserModifier( localUserDao, id, firstName, lastName, password );
    }
    
    
    public HauskeysUserModifier newHauskeysUser( String id, String firstName, String lastName, String password )
    {
        return new HauskeysUserModifier( hauskeysUserDao, id, firstName, lastName, password );
    }
    
    
    public User getUser( String id ) throws DataAccessException
    {
        return userDao.load( id );
    }
    
    
    public boolean hasUser( String id ) throws DataAccessException
    {
        return userDao.hasUser( id );
    }
    
    
    public String getRandomUniqueActivationKey() throws DataAccessException
    {
        return userDao.getRandomUniqueActivationKey();
    }
    
    
    // -----------------------------------------------------------------------
    // Application and ApplicationModifier Read Operations
    // -----------------------------------------------------------------------
    
    
    public ApplicationModifier newApplication( String name )
    {
        return new ApplicationModifier( applicationDao, name, 
            permissionDao, roleDao, profileDao );
    }
    
    
    public Application getApplication( String name ) throws DataAccessException
    {
        return applicationDao.load( name );
    }
    
    
    public boolean hasApplication( String name ) throws DataAccessException
    {
        return applicationDao.has( name );
    }
    

    // -----------------------------------------------------------------------
    // Group and GroupModifier Read Operations
    // -----------------------------------------------------------------------
    
    
    public GroupModifier newGroup( String name, String member )
    {
        Set members = new HashSet();
        members.add( member );
        return new GroupModifier( groupDao, name, members );
    }
    
    
    public GroupModifier newGroup( String name, Set members )
    {
        return new GroupModifier( groupDao, name, members );
    }
    
    
    public Group getGroup( String name ) throws DataAccessException
    {
        return groupDao.load( name );
    }

    
    // -----------------------------------------------------------------------
    // Iterator Access
    // -----------------------------------------------------------------------

    
    public Iterator groupIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( groupDao.iterator() );
    }


    public Iterator applicationIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( applicationDao.applicationIterator() );
    }


    public Iterator externalUserIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( externalUserDao.iterator() );
    }


    public Iterator localUserIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( localUserDao.iterator() );
    }


    public Iterator hauskeysUserIterator() throws DataAccessException
    {
        return new ReadOnlyIterator( hauskeysUserDao.iterator() );
    }
    
    
    public Iterator userIterator() throws DataAccessException
    {
        return userDao.iterator();
    }


    public void close()
    {
        factory.close();
    }
}
