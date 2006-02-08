/*
 * Copyright 2004,2004 The Apache Software Foundation.
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
 * Unit tests for basic ops on an {@link SelectorContext}.
 *  
 * @version $Revision$ $Date: 2003/11/30 05:36:07 $
 */
public class SelectorContextTest extends AbstractContextTest {
    
    public SelectorContextTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(SelectorContextTest.class);
    	suite.setName("Selector Context Tests");
        return suite;
    }
    
    protected Context makeInitialContext() {
    	return new SelectorContext(new Hashtable(), true);
    }
    
    public void testGetNameInNamespace() throws Exception {
        assertEquals(SelectorContext.prefix, initialContext.getNameInNamespace());
    }
        
    public void testParseName() throws Exception {
        try {
            initialContext.lookup("java:x");
        } catch (NameNotFoundException ex) {
            // expected 
        }
        try {
            initialContext.lookup("hava:x");
        } catch (NamingException ex) {
            // expected 
        }     
    }
    
    public void testGetBoundContext() throws Exception {
       
        Context initCtx = new SelectorContext(new Hashtable(), true);
        // Initial context should be bound under the name IC_PREFIX
        initCtx.bind("java:comp:a", "initial");
        assertEquals("initial", ContextBindings.getContext
                (SelectorContext.IC_PREFIX).lookup("java:comp:a"));
        
        // Bind classloader 
        Context clContext = new NamingContext(new Hashtable(), "cl");
        clContext.bind("a", "classloader");
        ContextBindings.bindContext("cl", clContext);
        ContextBindings.bindClassLoader("cl");  
        
        // Get a SelectorContext that is not an initial context 
        Context selCtx = new SelectorContext(new Hashtable(), false);
        // lookup should strip URL header, then delegate to clContext
        assertEquals("classloader", (String) selCtx.lookup("java:a")); // URL is stripped
        
        // Now bind thread 
        Context threadContext = new NamingContext(new Hashtable(), "th");
        threadContext.bind("a", "thread");
        ContextBindings.bindContext("th", threadContext);
        ContextBindings.bindThread("th");
        // thread binding takes precedence, so threadContext should get the call
        assertEquals("thread", (String) selCtx.lookup("java:a")); // URL is stripped
        
        // Fix up so teardown does not blow
        initialContext.createSubcontext(firstContextName());
    }
}
