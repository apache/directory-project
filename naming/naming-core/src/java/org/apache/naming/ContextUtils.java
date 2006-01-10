/*
 * Copyright 2005 The Apache Software Foundation.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.apache.naming.ContextBindings;
import org.apache.naming.NamingContextFactory;

/**
 * Collection of static methods that manipulate naming contexts.
 */
public class ContextUtils {
   
    /*
     * Prevent instantiation.
     */
    private ContextUtils() {}
   
    /**
     * Destroys a context, recursively destroying all subcontexts.
     * Does not attempt to destroy foreign contexts named by link references.
     *
     * @param context context to destroy recursively
     * @throws NamingException
     */
    public static synchronized void destroyContext(Context context)
    throws NamingException {
        NamingEnumeration contexts =  context.listBindings("");
        List bindings = new ArrayList();
        while (contexts.hasMore()) {
            bindings.add(contexts.next());
        }      
        for (int i =0; i < bindings.size(); i++) {
            Binding binding = (Binding) bindings.get(i);
            Object obj = binding.getObject();
            String name = binding.getName();
            try {
                context.destroySubcontext(name);  
            } catch (NotContextException ex) {
                context.unbind(name);
            } catch (ContextNotEmptyException e) {
                destroyContext((Context) obj);
            }    
        } 
    }
    
    /**
     * Destroys the context with the given external name and removes it from
     * the context bindings table.
     * 
     * @param name external name (i.e. key in {@link ContextBindings} of the
     * context to destroy
     * @throws NamingException
     */
    public static synchronized void destroyContext(String name) 
    throws NamingException {
        Context context = ContextBindings.getContext(name);
        if (context == null) {
            throw new NamingException("Named context not found");
        }
        destroyContext(context);
        ContextBindings.unbindContext(name);
    }
    
    /**
     * Destroys initial context, destoying subcontexts recursively.
     * 
     * @throws NamingException if a NamingException occurs
     */
    public static synchronized void destroyInitialContext() throws NamingException {
        Context initialContext = new InitialContext();
        destroyContext(initialContext);
        ContextBindings.unbindContext(NamingContextFactory.DEFAULT_NAME);
    }
    
    /**
     * Destroys the initial context based on the given environment, destoying
     * subcontexts recursively.  Removes context binding if environment contains
     * the NamingContextFactory.NAME key.
     * 
     * @throws NamingException if a NamingException occurs
     */
    public static synchronized void destroyInitialContext(Hashtable env) 
        throws NamingException {
        Context initialContext = new InitialContext(env);
        destroyContext(initialContext);
        if (env.containsKey(NamingContextFactory.NAME)) {
            ContextBindings.unbindContext(env.get(NamingContextFactory.NAME));      
        }
    }
   
}

