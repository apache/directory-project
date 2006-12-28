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


import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.safehaus.triplesec.admin.dao.ProfileDao;


public class Profile extends AdministeredEntity
{
    private final ProfileDao dao;
    private final Set<PermissionClass> permissionClasses;
    private final Set<String> roles;
    private final String id;
    private final String user;
    private final String description;
    private final String applicationName;
    private final boolean disabled;
    
    
    public Profile( String creatorsName, Date createTimestamp, ProfileDao dao, String applicationName, 
        String id, String user, String description, Set<PermissionClass> permissionClasses, Set<String> roles )
    {
        this( creatorsName, createTimestamp, null, null, dao, applicationName, id, 
            user, description, permissionClasses, roles, false );
    }
    
    
    public Profile( String creatorsName, Date createTimestamp, String modifiersName, Date modifyTimestamp, 
        ProfileDao dao, String applicationName, String id, String user, String description, 
        Set<PermissionClass> permissionClasses, Set<String> roles, boolean disabled )
    {
        super( creatorsName, createTimestamp, modifiersName, modifyTimestamp );
        this.dao = dao;
        this.applicationName = applicationName;
        this.id = id;
        this.user = user;
        this.permissionClasses = new HashSet<PermissionClass>( permissionClasses );
        this.roles = new HashSet<String>( roles );
        this.description = description;
        this.disabled = disabled;
    }
    
    
    public Set<PermissionClass> getPermissionClasses()
    {
        return Collections.unmodifiableSet( permissionClasses );
    }



    public Set<String> getRoles()
    {
        return Collections.unmodifiableSet( roles );
    }


    public String getId()
    {
        return id;
    }


    public String getUser()
    {
        return user;
    }


    public String getDescription()
    {
        return description;
    }


    public String getApplicationName()
    {
        return applicationName;
    }
    
    
    public ProfileModifier modifier()
    {
        return new ProfileModifier( dao, this );
    }

    
    public boolean isDisabled()
    {
        return disabled;
    }
    
    
    public String toString()
    {
        return id;
    }
}
