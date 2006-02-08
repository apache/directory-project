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


package org.apache.naming.java;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.InitialContextFactory;
import org.apache.naming.SelectorContext;
import org.apache.naming.NamingContext;
import org.apache.naming.SynchronizedContext;
import org.apache.naming.ContextBindings;

/**
 * Context factory for the "java:" namespace.
 * <p>
 * <b>Important note</b> : This factory MUST be associated with the "java" URL
 * prefix, which can be done by either :
 * <ul>
 * <li>Adding a 
 * java.naming.factory.url.pkgs=org.apache.naming property
 * to the JNDI properties file</li>
 * <li>Setting an environment variable named Context.URL_PKG_PREFIXES with 
 * its value including the org.apache.naming package name. 
 * More detail about this can be found in the JNDI documentation : 
 * {@link javax.naming.spi.NamingManager#getURLContext(java.lang.String, java.util.Hashtable)}.</li>
 * </ul>
 * 
 * @author Remy Maucherat
 * @version $Revision$ $Date$
 */

public class javaURLContextFactory
    implements ObjectFactory, InitialContextFactory {


    // ----------------------------------------------------------- Constructors


    // -------------------------------------------------------------- Constants


    public static final String MAIN = "initialContext";


    // ----------------------------------------------------- Instance Variables


    /**
     * Initial context.
     */
    protected static Context initialContext = null;


    // --------------------------------------------------------- Public Methods


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Create a new Context.  Returns null unless
     * <code>(ContextBindings.isThreadBound()) || 
     *      (ContextBindings.isClassLoaderBound())).</code>
     * <p>
     * Returns a {@link SelectorContext} instance.
     * 
     * @param obj Object instance (ignored)
     * @param name Name (ignored)
     * @param nameCtx Context (ignored)
     * @param environment Environment
     * @return context
     * @exception NamingException if a naming exception is encountered
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws NamingException {
        if ((ContextBindings.isThreadBound()) || 
            (ContextBindings.isClassLoaderBound())) {
            return new SelectorContext(environment);
        } else {
            return null;
        }
    }


    /**
     * Get a new (writable) initial context.  If  
     * <code>(ContextBindings.isThreadBound()) || 
     *      (ContextBindings.isClassLoaderBound()))</code>, a new
     * {@link SelectorContext} is created.  If the thread is not bound
     * and no shared writable context has been created, a new shared,
     * writable {@link NamingContext} is created and returned.  
     * 
     * @param environment  environment
     * @return a new Context
     * @exception NamingException if a naming exception is encountered
     */
    public Context getInitialContext(Hashtable environment)
        throws NamingException {
        if (ContextBindings.isThreadBound() || 
            (ContextBindings.isClassLoaderBound())) {
            // Redirect the request to the bound initial context
            return new SelectorContext(environment, true);
        } else {
            // If the thread is not bound, return a shared writable context
            if (initialContext == null) {
                Context ctx = new NamingContext(environment, MAIN);
                if (SynchronizedContext.isSynchronized(environment)) {
                    initialContext = new SynchronizedContext(ctx);
                } else {
                    initialContext = ctx;
                }
            }
            return initialContext;
        }
    }


}

