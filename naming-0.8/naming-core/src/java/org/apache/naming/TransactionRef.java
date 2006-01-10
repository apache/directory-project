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

import javax.naming.Reference;
import javax.naming.Context;

/**
 * Represents a reference address to a transaction.
 *
 * @author Remy Maucherat
 * @version $Revision$ $Date: 2003/11/30 05:15:06 $
 */

public class TransactionRef
    extends Reference {


    // -------------------------------------------------------------- Constants


    /**
     * Default factory for this reference.
     */
    public static final String DEFAULT_FACTORY = 
        Constants.DEFAULT_TRANSACTION_FACTORY;


    // ----------------------------------------------------------- Constructors


    /**
     * Transaction Reference.
     */
    public TransactionRef() {
        this(null, null);
    }


    /**
     * Transaction Reference.
     * 
     * @param factory Factory class name
     * @param factoryLocation Factory location
     */
    public TransactionRef(String factory, String factoryLocation) {
        super("javax.transaction.UserTransaction", factory, factoryLocation);
    }


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------ Reference Methods


    /**
     * Retrieves the class name of the factory of the object to which this 
     * reference refers.
     * <p>
     * If the factory class name is not set and the 
     * <code>Context.OBJECT_FACTORIES</code> system property is not present, 
     * the name of the default transaction factory is returned 
     * ("org.apache.naming.factory.TransactionFactory").  Null is returned if the
     *  factory class name is not set, but the system property is present.
     * 
     * @return Factory class name
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


    // ------------------------------------------------------------- Properties


}
