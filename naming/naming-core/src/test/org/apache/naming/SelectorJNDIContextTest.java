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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * Unit tests for basic ops on an {@link SelectorJNDIContext}.
 *  
 * @version $Revision$ $Date$
 */
public class SelectorJNDIContextTest extends AbstractContextTest {
    
    public SelectorJNDIContextTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(SelectorJNDIContextTest.class);
    	suite.setName("SelectorJNDIContext Tests");
        return suite;
    }
    
    protected Context makeInitialContext() {
    	return new SelectorJNDIContext(new Hashtable());
    }
    
    public void testGetNameInNamespace() throws Exception {
        assertEquals(SelectorContext.prefix, initialContext.getNameInNamespace());
    }
    
    public void testGetBoundContext() throws Exception {
       // initially, context should be anonymous global context
       assertEquals(
               ContextBindings.getContext
               (NamingContextFactory.DEFAULT_NAME).lookup
                       (firstContextName() + "/" + secondContextName() + "/" + firstBoundName()),
               initialContext.lookup
               (firstContextName() + "/" + secondContextName() + "/" + firstBoundName()));
       
       /* Create a new named context and parse a jndi url so the selected
           context changes. */
       Context ctx = new NamingContext(new Hashtable(), "test");
       ctx.createSubcontext("config");
       ctx.bind("config/host", "jakarta.apache.org");
       ContextBindings.bindContext("test", ctx);
       assertEquals("jakarta.apache.org", 
               (String) initialContext.lookup("jndi:test/config/host")); 
       
       // Lookups with no URL headers go the named context
       assertEquals("jakarta.apache.org", 
               (String) initialContext.lookup("config/host")); 
       
       // Reset back so teardown works
       initialContext.lookup("jndi:" + NamingContextFactory.DEFAULT_NAME );
    }
    
    public void testConstructor() throws Exception {
        // Create a named context
        Context ctx = new NamingContext(new Hashtable(), "test2");
        ctx.createSubcontext("config");
        ctx.bind("config/host", "minotaur.apache.org");
        ContextBindings.bindContext("test2", ctx);
        
        /* Create a SelectorJNDIContext with the name of the bound context
            in its environment   */
        Hashtable env = new Hashtable();
        env.put(NamingContextFactory.NAME, "test2");
        Context selectorJNDIContext = new SelectorJNDIContext(env);
        
        // Lookups with no jndi URL header should use named context
        assertEquals("minotaur.apache.org", 
                (String) selectorJNDIContext.lookup("config/host")); 
        
        // Now switch contexts using jndi URL
        assertEquals(
                ContextBindings.getContext
                (NamingContextFactory.DEFAULT_NAME).lookup
                (firstContextName() + "/" + secondContextName() + "/" + firstBoundName()),
                selectorJNDIContext.lookup
                ("jndi:" + NamingContextFactory.DEFAULT_NAME + "/" +
                  firstContextName() + "/" + secondContextName() + "/" + firstBoundName()));  
    }
    
    public void testParseName() throws Exception {
        try {
            initialContext.lookup("jndi:" + NamingContextFactory.DEFAULT_NAME + "/boo");
        } catch (NameNotFoundException ex) {
            // expected 
        }
        try {
            initialContext.lookup("jndi:x");
        } catch (NamingException ex) {
            // expected 
        }     
    }
    
}
