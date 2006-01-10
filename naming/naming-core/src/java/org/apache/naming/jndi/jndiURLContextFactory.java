/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.naming.jndi;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.InitialContextFactory;
import org.apache.naming.SelectorJNDIContext;

/**
 * URL Context Factory or "jndi" URLs.
 *<p>
 * jndi URLs can be used to lookup objects or create initial contexts using
 * external (user-defined) context names.  For example, the URL
 * "jndi:global/config/host" refers to the entry named by "config/host" in the
 * context stored in {@link ContextBindings} under the name "global." </p>
 *
 * @version $Revision$ $Date$
 */
public class jndiURLContextFactory implements ObjectFactory, InitialContextFactory {

    /**
     * Creates a new Context.
     * <p>
     * Returns a {@link SelectorJNDIContext} instance.
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
        return new SelectorJNDIContext(environment);
    }
    
    /**
     * Returns a new {@link SelectorJNDIContext}.
     * 
     * @param environment  environment
     * @return a new Context
     * @exception NamingException if a naming exception is encountered
     */
    public Context getInitialContext(Hashtable environment)
        throws NamingException {
        
        return new SelectorJNDIContext(environment);
        }
    }
