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
import java.util.Date;

import org.safehaus.triplesec.admin.dao.PermissionClassDao;

/**
 * @version $Rev:$ $Date:$
 */
public class PermissionClass extends AdministeredEntity {

    private final String permissionClassName;
    private final Set<PermissionActions> grants;
    private final Set<PermissionActions> denials;
    private final PermissionClassDao dao;

    public PermissionClass( String creatorsName, Date createTimestamp, String modifiersName,
        Date modifyTimestamp, PermissionClassDao dao, String permissionClassName, Set<PermissionActions> grants, Set<PermissionActions> denials) {
        super( creatorsName, createTimestamp, modifiersName, modifyTimestamp );
        this.dao = dao;
        this.permissionClassName = permissionClassName;
        this.grants = grants;
        this.denials = denials;
    }

    public String getPermissionClassName() {
        return permissionClassName;
    }

    public Set<PermissionActions> getGrants() {
        return grants;
    }

    public Set<PermissionActions> getDenials() {
        return denials;
    }
}
