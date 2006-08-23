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


package org.apache.naming.factory;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.TransactionRef;

/**
 * Object factory for user transactions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Revision$ $Date: 2003/10/13 08:15:11 $
 */

public class TransactionFactory
    implements ObjectFactory {


    // ----------------------------------------------------------- Constructors


    // -------------------------------------------------------------- Constants


    // ----------------------------------------------------- Instance Variables


    // --------------------------------------------------------- Public Methods


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Create a new user transaction instance.
     * 
     * @param obj The reference object describing the transaction
     * @param name The name of this object relative to nameCtx, or null if no name is specified.
     * @param nameCtx The context relative to which the name  parameter is specified, 
     * or null if name is relative to the default initial context.
     * @param environment  The possibly null environment that is used in 
     * creating the object.
     * @return The object created; null if an instance cannot be created.
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws Exception {
        
        if (obj instanceof TransactionRef) {
            Reference ref = (Reference) obj;
            ObjectFactory factory = null;
            RefAddr factoryRefAddr = ref.get(Constants.FACTORY);
            if (factoryRefAddr != null) {
                // Using the specified factory
                String factoryClassName = 
                    factoryRefAddr.getContent().toString();
                // Loading factory
                ClassLoader tcl = 
                    Thread.currentThread().getContextClassLoader();
                Class factoryClass = null;
                if (tcl != null) {
                    try {
                        factoryClass = tcl.loadClass(factoryClassName);
                    } catch(ClassNotFoundException e) {
                    }
                } else {
                    try {
                        factoryClass = Class.forName(factoryClassName);
                    } catch(ClassNotFoundException e) {
                    }
                }
                if (factoryClass != null) {
                    try {
                        factory = (ObjectFactory) factoryClass.newInstance();
                    } catch(Throwable t) {
                    }
                }
            }
            if (factory != null) {
                return factory.getObjectInstance
                    (obj, name, nameCtx, environment);
            } else {
                throw new NamingException
                    ("Cannot create resource instance");
            }
            
        }
        
        return null;
        
    }


}

