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
 * An do nothing convenience adapter for a PolicyChangeListener.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$, $Date$
 */
public class PolicyChangeAdapter implements PolicyChangeListener
{
    public void roleChanged( ApplicationPolicy policy, Role role, ChangeType changeType )
    {
    }


    public void roleRenamed( ApplicationPolicy policy, Role role, String oldName )
    {
    }


    public void permissionChanged( ApplicationPolicy policy, StringPermission permission, ChangeType changeType )
    {
    }


    public void permissionRenamed( ApplicationPolicy policy, StringPermission permission, String oldName )
    {
    }


    public void profileChanged( ApplicationPolicy policy, Profile profile, ChangeType changeType )
    {
    }


    public void profileRenamed( ApplicationPolicy policy, Profile profile, String oldName )
    {
    }
}
