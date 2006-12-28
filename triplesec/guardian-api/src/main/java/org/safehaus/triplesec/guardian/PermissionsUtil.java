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


package org.safehaus.triplesec.guardian;

import java.util.Enumeration;
import java.util.Set;
import java.security.Permission;
import java.security.Permissions;

/**
 * @version $Rev:$ $Date:$
 */
public class PermissionsUtil {
    private PermissionsUtil() {
    }

    public static boolean isEmpty(Permissions permissions) {
        return !permissions.elements().hasMoreElements();
    }

    /**
     * @deprecated used only in tests
     * @param permissions
     * @return number of Permissions in the Permissions.
     */
    public static int size(Permissions permissions) {
        int i = 0;
        for (Enumeration<Permission> elements = permissions.elements(); elements.hasMoreElements();) {
            elements.nextElement();
            i++;
        }
        return i;
    }

    public static Permissions union(Permissions first, Permissions second) {
        Permissions result = new Permissions();
        for (Enumeration<Permission> elements = first.elements(); elements.hasMoreElements();) {
            result.add(elements.nextElement());
        }
        for (Enumeration<Permission> elements = second.elements(); elements.hasMoreElements();) {
            result.add(elements.nextElement());
        }
        return result;
    }
    
    public static void addAll(Permissions first, Permissions second) {
        for (Enumeration<Permission> elements = second.elements(); elements.hasMoreElements();) {
            first.add(elements.nextElement());
        }
    }

    public static Permissions difference(Permissions whole, Permissions remove) {
        Permissions result = new Permissions();
        for (Enumeration<Permission> elements = whole.elements(); elements.hasMoreElements();) {
            Permission permission = elements.nextElement();
            if (!remove.implies(permission)) {
                result.add(permission);
            }
        }
        return result;
    }

    public static Permissions remove(Permissions whole, Permission remove) {
        Permissions result = new Permissions();
        for (Enumeration<Permission> elements = whole.elements(); elements.hasMoreElements();) {
            Permission permission = elements.nextElement();
            if (!remove.implies(permission)) {
                result.add(permission);
            }
        }
        return result;
    }

    public static boolean equivalent(Permissions a, Permissions b) {
        return impliesAll(a, b) && impliesAll(b, a);
    }

    public static boolean impliesAll(Permissions a, Permissions b) {
        for (Enumeration<Permission> elements = b.elements(); elements.hasMoreElements();) {
            if (!a.implies(elements.nextElement())) {
                return false;
            }
        }
        return true;
    }

//    public static void addPermissions(Permissions permissions, Set<Permission> permSet) {
//        for (Permission perm: permSet) {
//            permissions.add(perm);
//        }
//    }
}
