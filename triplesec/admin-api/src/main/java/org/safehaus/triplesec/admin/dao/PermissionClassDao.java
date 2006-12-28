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

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.PermissionActions;
import org.safehaus.triplesec.admin.PermissionClass;


public interface PermissionClassDao
{
    PermissionClass add( String contextDn, String permClassName, Set<PermissionActions> grants, Set<PermissionActions> denials )
        throws DataAccessException;

    void delete( String contextDn, String permClassName )
        throws DataAccessException;

    PermissionClass modify( String creatorsName, Date createTimestamp, String contextDn,
        String permClassName )
        throws DataAccessException;

    PermissionClass rename(  String contextDn, String newPermClassName, PermissionClass permClass )
        throws DataAccessException;

    PermissionClass load( String applicationName, String name )
        throws DataAccessException;

    boolean has( String applicationName, String name )
        throws DataAccessException;

    Iterator permissionClassNameIterator( String applicationName )
        throws DataAccessException;

}