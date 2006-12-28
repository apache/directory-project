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
package org.safehaus.triplesec.admin.dao;


import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.Role;
import org.safehaus.triplesec.admin.PermissionClass;


public interface RoleDao
{
    Iterator roleIterator( String applicationName )
        throws DataAccessException;

    Role load( String applicationName, String name )
        throws DataAccessException;

    Role add( String applicationName, String name, String currentValue, Set<PermissionClass> permissionClasses )
        throws DataAccessException;

    Role rename( String newName, Role archetype )
        throws DataAccessException;

    Role modify( String creatorsName, Date createTimestamp, String applicationName, String name,
        String description, Set<PermissionClass> permissionClasses, ModificationItem[] mods )  throws DataAccessException;

    void delete( String applicationName, String name )
        throws DataAccessException;
}
