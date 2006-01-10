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
    
    /**
     * Get a new (writable) initial context.  Always returns a 
     * {@link NamingContext}.  If
     * {@link SynchronizedContext#isSynchronized} returns true for 
     * <code>environment</code>  then the
     * returned context is wrapped in a {@link SynchronizedContext}, so access
     * to it will be synchronized.
     * 
     * @param environment  environment
     * @return a new Context
     * @exception NamingException if a naming exception is encountered
     */
    public Context getInitialContext(Hashtable environment)
    throws NamingException {
        Context ctx = new NamingContext(environment, "initialContext");
        if (SynchronizedContext.isSynchronized(environment)) {
            return new SynchronizedContext(ctx);
        } else {
            return ctx;
        }
    }
}
