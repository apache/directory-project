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
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junit.framework.TestCase;


/**
 * Unit tests for  {@link ContextUtils}.
 *  
 * @version $Revision: 56478 $ $Date: 2003/11/30 05:36:07 $
 */
public class ContextUtilsTest extends TestCase {
    
    public ContextUtilsTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(ContextUtilsTest.class);
    	suite.setName("ContextUils Tests");
        return suite;
    }
    
    public void setUp() throws Exception {
    }
    
    public void tearDown() throws Exception {
    }
    
   public void testDestroyInitialContext() throws Exception {
       // First try with named context
       Hashtable env = new Hashtable();
       env.put(NamingContextFactory.NAME, "test");
       env.put(Context.INITIAL_CONTEXT_FACTORY,
               "org.apache.naming.NamingContextFactory");
       env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
       checkInitialContext(env);
       
       // Anonymous
       env.clear();
       env.put(Context.INITIAL_CONTEXT_FACTORY,
       "org.apache.naming.NamingContextFactory");
       env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
       checkInitialContext(env);
   }
   
   public void testDestroyContext() throws Exception {
       Hashtable env = new Hashtable();    
       
       // Anonymous
       env.put(Context.INITIAL_CONTEXT_FACTORY,
       "org.apache.naming.NamingContextFactory");
       env.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
       Context initialContext = makeInitialContext(env);
       Context victim = (Context) initialContext.lookup("sub/next");
       ContextUtils.destroyContext(victim);
       try {
           initialContext.lookup("sub/next");
           fail("Expecting NamingException");
       } catch (NamingException ex) {
           // Expected
       }
       ContextUtils.destroyInitialContext(env);
       
       // Named
       initialContext = makeInitialContext(env);
       victim = (Context) initialContext.lookup("sub/next");
       ContextBindings.bindContext("victim", victim);
       ContextUtils.destroyContext("victim");
       try {
           initialContext.lookup("sub/next");
           fail("Expecting NamingException");
       } catch (NamingException ex) {
           // Expected
       }
       assertNull(ContextBindings.getContext("victim"));   
       
       try { 
           ContextUtils.destroyContext("victim");
           fail("Expecting NamingException");
       } catch (NamingException e) {
           // Expected
       }
       
       ContextUtils.destroyInitialContext(env);
   }
   
   protected void checkInitialContext(Hashtable env) throws Exception {
       killInitialContext(env, makeInitialContext(env));
   }
   
   protected Context makeInitialContext(Hashtable env) throws Exception {
       Context initialContext = new InitialContext(env);
       initialContext.bind("foo", "bar");
       initialContext.bind("int", new Integer(1));
       initialContext.createSubcontext("sub");
       Context subContext = (Context) initialContext.lookup("sub");
       subContext.bind("foofoo","barbar");
       subContext.createSubcontext("next");
       Context nextContext = (Context) subContext.lookup("next");
       subContext.bind("parent", initialContext);      // cycle
       nextContext.bind("grandpa", initialContext);  // multi-generational cycle 
       assertEquals("initial lookup", "barbar", initialContext.lookup("sub/foofoo"));
       assertEquals("inbred lookup", "barbar", initialContext.lookup("sub/next/grandpa/sub/foofoo")); 
       return initialContext;
   }
   
   protected void killInitialContext(Hashtable env, Context context) throws Exception {    
       ContextUtils.destroyInitialContext(env);
       assertNull(ContextBindings.getContext("test"));
       assertFalse(context.list("").hasMore());
   }
}
