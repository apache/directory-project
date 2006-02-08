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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junit.framework.TestCase;


/**
 * Unit tests for  {@link ContextBindings}.
 *  
 * @version $Revision$ $Date: 2003/11/30 05:36:07 $
 */
public class ContextBindingsTest extends TestCase {
    
    public ContextBindingsTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(ContextBindingsTest.class);
    	suite.setName("ContextBindings Tests");
        return suite;
    }
    
    protected Context testContext = null;
    protected Context testSubContext = null;
    protected String testToken1 = "test1";
    protected String testToken2 = "test2";
    protected String contextName = "context";
    protected String subName = "sub";
    
    public void setUp() throws Exception {
        testContext = new SelectorContext(new Hashtable(), true);
        testSubContext = testContext.createSubcontext("env:comp");
    }
    
    public void tearDown() throws Exception {
        ContextAccessController.unsetSecurityToken(contextName, testToken1);
        ContextAccessController.unsetSecurityToken(subName, testToken2);
        ContextBindings.unbindContext(contextName);
        ContextBindings.unbindContext(subName);
        testContext.destroySubcontext("env:comp");
        testContext = null;
    }
    
   public void testNameBindings() throws Exception {
       // bind an unsecured context
       ContextBindings.bindContext(contextName, testContext);
       // test lookup
       assertEquals(testContext, ContextBindings.getContext(contextName));
       // lookup on unbound name returns null 
       assertNull(ContextBindings.getContext(subName));
       // overwrite
       ContextBindings.bindContext(contextName, testSubContext);
       assertEquals(testSubContext, ContextBindings.getContext(contextName));
       // two names are OK
       ContextBindings.bindContext(subName, testSubContext);
       assertEquals(testSubContext, ContextBindings.getContext(subName));
       //unbind
       ContextBindings.unbindContext(subName);
       assertNull(ContextBindings.getContext(subName));
       ContextBindings.unbindContext(contextName);
       // secure and rebind
       ContextAccessController.setSecurityToken(contextName, testToken1);
       // wrong token does nothing
       ContextBindings.bindContext(contextName, testContext, testToken2);
       assertNull(ContextBindings.getContext(contextName));
       // good token works
       ContextBindings.bindContext(contextName, testContext, testToken1);
       assertEquals(testContext, ContextBindings.getContext(contextName));
       // unbind fails silently with wrong token
       ContextBindings.unbindContext(contextName, testToken2);
       assertEquals(testContext, ContextBindings.getContext(contextName));
       // good token works
       ContextBindings.unbindContext(contextName, testToken1);
       assertNull(ContextBindings.getContext(contextName));
   }
   
   public void testThreadBindings() throws Exception {
       // unbound name should generate NamingExeption for bindThread
       try {
           ContextBindings.bindThread(contextName);
           fail("Expecting NamingException for unbound name");
       } catch (NamingException ex) {
           // expected
       }
       // unbound thread should generate NamingExeption for getThread
       try {
           ContextBindings.getThread();
           fail("Expecting NamingException for unbound thread");
       } catch (NamingException ex) {
           // expected
       }
       // unbound thread should generate NamingExeption for getThreadName
       try {
           ContextBindings.getThreadName();
           fail("Expecting NamingException for unbound thread");
       } catch (NamingException ex) {
           // expected
       }
       // now bind name and bind thread to context
       ContextBindings.bindContext(contextName, testContext);
       ContextBindings.bindThread(contextName);
       assertTrue(ContextBindings.isThreadBound());
       // test lookup
       assertEquals(testContext, ContextBindings.getThread());
       assertEquals(contextName, ContextBindings.getThreadName());
       // switch name binding -- thread binding does not change
       ContextBindings.bindContext(contextName, testSubContext);
       assertEquals(testContext, ContextBindings.getThread());
       //unbind
       ContextBindings.unbindThread(contextName);
       assertFalse(ContextBindings.isThreadBound());
       // secure and rebind
       ContextAccessController.setSecurityToken(contextName, testToken1);
       // wrong token does nothing
       ContextBindings.bindThread(contextName, testToken2);
       assertFalse(ContextBindings.isThreadBound());
       // good token works
       ContextBindings.bindThread(contextName, testToken1);
       assertEquals(testSubContext, ContextBindings.getThread());
       // unbind fails silently with wrong token
       ContextBindings.unbindThread(contextName, testToken2);
       assertEquals(testSubContext, ContextBindings.getThread());
       assertTrue(ContextBindings.isThreadBound());
       // good token works
       ContextBindings.unbindThread(contextName, testToken1);
       assertFalse(ContextBindings.isThreadBound());   
   }
   
   public void testClassloaderBindings() throws Exception {
       // unbound name should generate NamingExeption for bindClassLoader
       try {
           ContextBindings.bindClassLoader(contextName);
           fail("Expecting NamingException for unbound name");
       } catch (NamingException ex) {
           // expected
       }
       // unbound cl should generate NamingExeption for getClassLoader
       try {
           ContextBindings.getClassLoader();
           fail("Expecting NamingException for unbound classloader");
       } catch (NamingException ex) {
           // expected
       }
       // unbound cl should generate NamingExeption for getClassLoaderName
       try {
           ContextBindings.getClassLoaderName();
           fail("Expecting NamingException for unbound classloader");
       } catch (NamingException ex) {
           // expected
       }
       // now bind name and indirectly bind cl to context
       ContextBindings.bindContext(contextName, testContext);
       ContextBindings.bindClassLoader(contextName);
       assertTrue(ContextBindings.isClassLoaderBound());
       // test lookup
       assertEquals(testContext, ContextBindings.getClassLoader());
       assertEquals(contextName, ContextBindings.getClassLoaderName());
       // switch name binding -- cl binding does not change
       ContextBindings.bindContext(contextName, testSubContext);
       assertEquals(testContext, ContextBindings.getClassLoader());
       //unbind
       ContextBindings.unbindClassLoader(contextName);
       assertFalse(ContextBindings.isClassLoaderBound());
       // secure and rebind
       ContextAccessController.setSecurityToken(contextName, testToken1);
       // wrong token does nothing
       ContextBindings.bindClassLoader(contextName, testToken2);
       assertFalse(ContextBindings.isClassLoaderBound());
       // good token works
       ContextBindings.bindClassLoader(contextName, testToken1);
       assertEquals(testSubContext, ContextBindings.getClassLoader());
       // unbind fails silently with wrong token
       ContextBindings.unbindClassLoader(contextName, testToken2);
       assertEquals(testSubContext, ContextBindings.getClassLoader());
       assertTrue(ContextBindings.isClassLoaderBound());
       // good token works
       ContextBindings.unbindClassLoader(contextName, testToken1);
       assertFalse(ContextBindings.isClassLoaderBound());   
       // bind current thread classloader explicitly
       ContextBindings.bindClassLoader(contextName, testToken1, 
               Thread.currentThread().getContextClassLoader());
       assertTrue(ContextBindings.isClassLoaderBound());
       ContextBindings.unbindClassLoader(contextName, testToken1);
       assertFalse(ContextBindings.isClassLoaderBound());   
   } 
}
