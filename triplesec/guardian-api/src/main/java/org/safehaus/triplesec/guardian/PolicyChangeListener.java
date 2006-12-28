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
package org.safehaus.triplesec.guardian;




/**
 * A policy change listener interested in changes to an application's access 
 * policy which is managed by TripleSec.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$, $Date$
 */
public interface PolicyChangeListener
{
    /**
     * Notification method called when a role is added, deleted, or modified.  
     * Another overload is used to handle rename operations on objects.
     * 
     * @param policy the application policy containing the role
     * @param role the role that is added, deleted or modified
     * @param changeType the type of change: add, delete or modify.
     */
    void roleChanged( ApplicationPolicy policy, Role role, ChangeType changeType );
    
    /**
     * Notification method called when a role is renamed.
     * 
     * @param policy the application policy containing the role
     * @param role the role that was renamed and whose getName() returns the new name
     * @param oldName the old name of the role
     */
    void roleRenamed( ApplicationPolicy policy, Role role, String oldName );

    /**
     * Notification method called when a permission is added, deleted, or modified.  
     * Another overload is used to handle rename operations on objects.
     * 
     * @param policy the application policy containing the permission
     * @param permission the permission that was changed
     * @param changeType the type of change: add, delete or modify.
     */
    void permissionChanged( ApplicationPolicy policy, StringPermission permission, ChangeType changeType );
    
    /**
     * Notification method called when a permission is renamed.
     * 
     * @param policy the application policy containing the permission
     * @param permission the permission that was renamed
     * @param oldName the old name of the permission
     */
    void permissionRenamed( ApplicationPolicy policy, StringPermission permission, String oldName );

    /**
     * Notification method called when a profile is added, deleted, or modified.  
     * Another overload is used to handle rename operations on objects.
     * 
     * @param policy the application policy containing the profile
     * @param profile the profile that changed
     * @param changeType the type of change: add, delete or modify.
     */
    void profileChanged( ApplicationPolicy policy, Profile profile, ChangeType changeType );

    /**
     * Notification method called when a policy is renamed.
     * 
     * @param policy the application policy containing the profile
     * @param profile the profile that was renamed
     * @param oldName the old name of the profile
     */
    void profileRenamed( ApplicationPolicy policy, Profile profile, String oldName );
}
