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
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * Unit tests for NamingFactory.
 *  
 * @version $Revision: 123481 $ $Date: 2003/11/30 05:36:07 $
 */
public class NamingFactoryTest extends TestCase {
    
    public NamingFactoryTest(String name) {
        super(name);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
            "org.apache.naming.NamingContextFactory");
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(NamingFactoryTest.class);
    	suite.setName("Naming Factory Tests");
        return suite;
    }
    
    public void testGlobal() throws Exception {
    	 Context initialContext = new InitialContext(new Hashtable());
         initialContext.bind("foo", "bar");
         assertNotNull("global context stored", 
                 ContextBindings.getContext(NamingContextFactory.DEFAULT_NAME));
         Context ctx2 = new InitialContext(new Hashtable());
         assertEquals("global context retrieved","bar", initialContext.lookup("foo"));
         ctx2.unbind("foo");
         ContextBindings.unbindContext(NamingContextFactory.DEFAULT_NAME);
    }
    
    public void testNamed() throws Exception {
        Hashtable env = new Hashtable();
        env.put(NamingContextFactory.NAME, "app1");
        Context initialContext = new InitialContext(env);
        initialContext.bind("foo", "bar");
        assertNotNull("named context stored", 
                ContextBindings.getContext("app1")); 
        Context ctx2 = new InitialContext(env);
        assertEquals("named context retrieved","bar", initialContext.lookup("foo"));
        env.put(NamingContextFactory.NAME, "app2");  
        Context ctx3 = new InitialContext(env);
        try {
            ctx3.lookup("foo");
            fail("Expecting NamingException");
        } catch (NamingException ex) { }
        initialContext.unbind("foo");
        ContextBindings.unbindContext("app1");
        ContextBindings.unbindContext("app2");
    }  
}
