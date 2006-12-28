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

import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


/**
 * An application permission.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @author Trustin Lee
 * @version $Rev: 71 $, $Date: 2005-11-07 19:11:39 -0500 (Mon, 07 Nov 2005) $
 */
public class StringPermission extends Permission implements Comparable, Cloneable, Serializable {
    private static final long serialVersionUID = -522561010304299861L;

    /** the name of the permission */
//    private final String permissionName;
    /**
     * the name of the application this permission is associated with
     */
//    private final String applicationName;
    /**
     * a short description of the permission
     */
//    private final String description;



    /**
     * Creates a new permission instance with description.
     *
     * @param permissionName  the permissionName of the permission
     */
    public StringPermission(String permissionName) {
        super(permissionName);
        if (permissionName == null) {
            throw new NullPointerException("permissionName");
        }
        if (permissionName.length() == 0) {
            throw new IllegalArgumentException("permissionName is empty.");
        }
    }


    public String getActions() {
        return "";
    }


    // ------------------------------------------------------------------------
    // Object Overrides
    // ------------------------------------------------------------------------


    public int hashCode() {
        return getName().hashCode();
    }


    public boolean implies(Permission permission) {
        return permission instanceof StringPermission && permission.getName().equals(getName());
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        if (that instanceof StringPermission) {
            StringPermission thatP = (StringPermission) that;
            return getName().equals(thatP.getName());
        }

        return false;
    }


    public int compareTo(Object that) {
        StringPermission thatP = (StringPermission) that;
        return this.getName().compareTo(thatP.getName());
    }


    public String toString() {
        return "StringPermission(" + getName() + ')';
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new StringPermissionCollection();
    }

    private static class StringPermissionCollection extends PermissionCollection {

        private final Map<String, StringPermission> permissionMap = new HashMap<String, StringPermission>();


        public void add(Permission permission) {
            if (permission instanceof StringPermission) {
                permissionMap.put(permission.getName(), (StringPermission) permission);
            } else {
                throw new IllegalArgumentException("Permission must be a StringPermission not a " + permission.getClass());
            }
        }

        public boolean implies(Permission permission) {
            if (permission instanceof StringPermission) {
                return permissionMap.containsKey(permission.getName());
            }
            return false;
        }

        public Enumeration<Permission> elements() {
            final Iterator<StringPermission> iterator = permissionMap.values().iterator();

            return new Enumeration<Permission>() {


                public boolean hasMoreElements() {
                    return iterator.hasNext();
                }

                public StringPermission nextElement() {
                    return iterator.next();
                }
            };
        }
    }

}
