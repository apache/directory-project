/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.naming;

import java.util.Enumeration;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Context;
import javax.naming.StringRefAddr;

/**
 * Represents a reference address to a resource.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Revision$ $Date: 2003/10/13 08:16:47 $
 */

public class ResourceRef
    extends Reference {


    // -------------------------------------------------------------- Constants


    /**
     * Default factory for this reference.
     */
    public static final String DEFAULT_FACTORY = 
        Constants.DEFAULT_RESOURCE_FACTORY;


    /**
     * Description address type.
     */
    public static final String DESCRIPTION = "description";


    /**
     * Scope address type.
     */
    public static final String SCOPE = "scope";


    /**
     * Auth address type.
     */
    public static final String AUTH = "auth";


    // ----------------------------------------------------------- Constructors


    /**
     * Resource Reference.
     * 
     * @param resourceClass Resource class
     * @param description Resource description
     * @param scope Resource scope
     * @param auth Resource authetication
     */
    public ResourceRef(String resourceClass, String description, 
                       String scope, String auth) {
        this(resourceClass, description, scope, auth, null, null);
    }


    /**
     * Resource Reference.
     * 
     * @param resourceClass Resource class
     * @param description Resource description
     * @param scope Resource scope
     * @param auth Resource authentication
     * @param factory Factory class name
     * @param factoryLocation  Location from which to load the factory
     */
    public ResourceRef(String resourceClass, String description, 
                       String scope, String auth, String factory,
                       String factoryLocation) {
        super(resourceClass, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (description != null) {
            refAddr = new StringRefAddr(DESCRIPTION, description);
            add(refAddr);
        }
        if (scope != null) {
            refAddr = new StringRefAddr(SCOPE, scope);
            add(refAddr);
        }
        if (auth != null) {
            refAddr = new StringRefAddr(AUTH, auth);
            add(refAddr);
        }
    }


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------ Reference Methods


    /**
     * Retrieves the class name of the factory of the object to which this 
     * reference refers.
     * <p>
     * If the factory class name is not set and the 
     * <code>Context.OBJECT_FACTORIES</code> system property is not present, 
     * the name of the default Resource factory is returned 
     * ("org.apache.naming.factory.ResourceFactory").  Null is returned if 
     * the factory class name is not set, but the system property is present.
     * 
     * @return  The factory class name
     */
    public String getFactoryClassName() {
        String factory = super.getFactoryClassName();
        if (factory != null) {
            return factory;
        } else {
            factory = System.getProperty(Context.OBJECT_FACTORIES);
            if (factory != null) {
                return null;
            } else {
                return DEFAULT_FACTORY;
            }
        }
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return a String rendering of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ResourceRef[");
        sb.append("className=");
        sb.append(getClassName());
        sb.append(",factoryClassLocation=");
        sb.append(getFactoryClassLocation());
        sb.append(",factoryClassName=");
        sb.append(getFactoryClassName());
        Enumeration refAddrs = getAll();
        while (refAddrs.hasMoreElements()) {
            RefAddr refAddr = (RefAddr) refAddrs.nextElement();
            sb.append(",{type=");
            sb.append(refAddr.getType());
            sb.append(",content=");
            sb.append(refAddr.getContent());
            sb.append("}");
        }
        sb.append("]");
        return (sb.toString());

    }


    // ------------------------------------------------------------- Properties


}
