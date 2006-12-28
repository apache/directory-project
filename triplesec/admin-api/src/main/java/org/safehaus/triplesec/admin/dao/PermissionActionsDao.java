/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.safehaus.triplesec.admin.dao;

import java.util.Iterator;
import java.util.Set;
import java.util.Date;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.PermissionActions;
import org.safehaus.triplesec.admin.DataAccessException;

/**
 * @version $Rev:$ $Date:$
 */
public interface PermissionActionsDao {
    Iterator<PermissionActions> permissionActionsIterator( String contextDn, boolean isGrant ) throws DataAccessException;

    PermissionActions load( String contextDn, boolean isGrant, String permName ) throws DataAccessException;

    PermissionActions add( String contextDn, boolean isGrant, String permName, Set<String> actions )
        throws DataAccessException;

    PermissionActions rename( String contextDn, boolean isGrant, String newPermissionName, PermissionActions permissionActions ) throws DataAccessException;

    PermissionActions modify( String creatorsName, Date createTimestamp, String contextDn, boolean isGrant, String permissionName,
        Set<String> actions, ModificationItem[] mods )
        throws DataAccessException;

    void delete( String contextDn, boolean isGrant,  String permissionName ) throws DataAccessException;
}
