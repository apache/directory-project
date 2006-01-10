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
package org.apache.naming.java;

import java.util.Hashtable;
import javax.naming.Context;

import org.apache.naming.ContextBindings;
import org.apache.naming.NamingContext;
import org.apache.naming.SelectorContext;
import org.apache.naming.SynchronizedContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for JavaURLContextFactory
 * 
 */
public class JavaURLContextFactoryTest extends TestCase {
    
    public JavaURLContextFactoryTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(JavaURLContextFactoryTest.class);
        suite.setName("JavaURLContextFactory Tests");
        return suite;
    }
    
    protected javaURLContextFactory contextFactory = new javaURLContextFactory();
    
    public void testGetInitialContext() throws Exception {
        // nothing bound, empty environment -> NamingContext
        Hashtable env = new Hashtable();
        Context ctx = contextFactory.getInitialContext(env);
        assertTrue(ctx instanceof NamingContext);
        ContextBindings.bindContext("ctxName", ctx);
        
        // bind thread -> SelectorContext
        ContextBindings.bindThread("ctxName");
        Context selCtx = contextFactory.getInitialContext(env);
        assertTrue(selCtx instanceof SelectorContext); 
        
        // add synch property to environment, but still thread bound
        // -> SelectorContext
        env.put(SynchronizedContext.SYNCHRONIZED, "true");
        Context unSynchCtx = contextFactory.getInitialContext(env);
        assertTrue(unSynchCtx instanceof SelectorContext);
        
        // unbind thread, but no synch prop -> NamingContext
        ContextBindings.unbindThread("ctxName");
        unSynchCtx = contextFactory.getInitialContext(env);
        assertTrue(unSynchCtx instanceof NamingContext);
        // need to null shared context; otherwise will be returned again
        javaURLContextFactory.initialContext = null;
        
        // unbound thread, synch property -> SynchronizedContext
        env.put(SynchronizedContext.SYNCHRONIZED, "true");
        Context synchCtx = contextFactory.getInitialContext(env);
        assertTrue(synchCtx instanceof SynchronizedContext);
    }  
    
    public void testGetObjectInstance() throws Exception {
        // Create and name a context
        Hashtable env = new Hashtable();
        Context ctx = contextFactory.getInitialContext(env);
        ContextBindings.bindContext("ctxName", ctx);
        
        // Nothing bound -> null returned
        Object obj = contextFactory.getObjectInstance(null, null, null, env);
        assertNull(obj);
        
        // bind thread -> SelectorContext
        ContextBindings.bindThread("ctxName");
        Context getCtx = (Context) contextFactory.getObjectInstance(null, null, null, env);
        assertTrue(getCtx instanceof SelectorContext);       
    }
}
