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

package org.apache.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Initial context factory returning {@link NamingContext} instances.
 *
 * @version $Revision$ $Date$
 */
public class NamingContextFactory implements InitialContextFactory {
   
    /** Environment property holding context external name */
    public static final String NAME = "org.apache.naming.name";
    
    /** 
     * External name for shared global context created or returned when no 
     * name environment property is set
     */
    public static final String DEFAULT_NAME = "Global";
    
    /**
     * Get a new (writable) initial context. 
     * <p> 
     * If an external name is specified in the environment 
     * (under NamingFactory.NAME),  the named context is returned from 
     * {@link ContextBindings}.  If there is no such named context, a 
     * context with this name is created and added to ContextBindings.  
     * If there is no name property present in the environment, the default
     * "Global" is assumed (so all "anonymous" initial context requests refer
     * to a single, shared context).
     * <p>
     * If {@link SynchronizedContext#isSynchronized} returns true for 
     * <code>environment</code>  then the returned context is wrapped in a
     * {@link SynchronizedContext}, so accessto it will be synchronized.
     * 
     * @param environment  environment
     * @return a new Context
     * @exception NamingException if a naming exception is encountered
     */
    public Context getInitialContext(Hashtable environment)
    throws NamingException {
        String name = null;
        if (environment.containsKey(NAME)) {
            name = (String) environment.get(NAME);
        } else {
            name = DEFAULT_NAME;
        }
        Context ctx = ContextBindings.getContext(name);
        if (ctx == null) {
            ctx =  new NamingContext(environment, name);
            ContextBindings.bindContext(name, ctx);
        } 
        if (SynchronizedContext.isSynchronized(environment)) {
            return new SynchronizedContext(ctx);
        } else {
            return ctx;
        }
    }
}
