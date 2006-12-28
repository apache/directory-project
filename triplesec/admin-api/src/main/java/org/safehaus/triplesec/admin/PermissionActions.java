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


package org.safehaus.triplesec.admin;

import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Date;

import org.safehaus.triplesec.admin.dao.PermissionActionsDao;

/**
 * @version $Rev:$ $Date:$
 */
public class PermissionActions extends AdministeredEntity implements Constants {

    private final String permissionName;
    private final Set<String> actions;

    public PermissionActions( String creatorsName, Date createTimestamp, String modifiersName,
        Date modifyTimestamp, PermissionActionsDao dao, String permissionName, Set<String> actions) {
        super( creatorsName, createTimestamp, modifiersName, modifyTimestamp );
        this.permissionName = permissionName;
        this.actions = new HashSet<String>(actions);
    }

    public String getPermissionName() {
        return permissionName;
    }

    public Set<String> getActions() {
        return Collections.unmodifiableSet(actions);
    }

}
