/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.naming.java.javaURLContextFactory;

/**
 * Unit tests for basic ops on an {@link NamingContext}.
 * Adapted from o.a.geronimo.naming test of the same name
 *  
 * @version $Revision$ $Date$
 */
public class BasicContextTest extends TestCase {
    private HashMap envBinding;
    private Context initialContext;
    private Context compContext;
    private Context envContext;
    
    public BasicContextTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(BasicContextTest.class);
    	suite.setName("Basic Context Tests");
        return suite;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        Hashtable env = new Hashtable();
        envBinding = new HashMap();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
            "org.apache.naming.java.javaURLContextFactory");
        env.put(Context.URL_PKG_PREFIXES,"org.apache.naming");
        initialContext = new InitialContext(env);
        compContext = initialContext.createSubcontext("java:comp");
        envContext =  compContext.createSubcontext("env");
        envContext.bind("hello", "Hello");
        envContext.bind("world", "World");
        envBinding.put("hello", "Hello");
        envBinding.put("world", "World");
        ContextAccessController.setReadOnly(javaURLContextFactory.MAIN);
        ContextAccessController.setSecurityToken(javaURLContextFactory.MAIN,"x");
    }
    
    protected void tearDown() throws Exception {
        ContextAccessController.setWritable(javaURLContextFactory.MAIN,"x");
        compContext.destroySubcontext("env");
        initialContext.destroySubcontext("java:comp");
        initialContext = null;
    }

    public void testInitialContext() throws NamingException {
        assertEquals("Hello", initialContext.lookup("java:comp/env/hello"));
        assertEquals("World", initialContext.lookup(new CompositeName("java:comp/env/world")));
        assertEquals(envContext.lookup("hello"), 
            ((Context) envContext.lookup("")).lookup("hello"));  
    }

    public void testLookup() throws NamingException {
        assertEquals("Hello", envContext.lookup("hello"));
        assertEquals("Hello", compContext.lookup("env/hello"));
        try {
            envContext.lookup("foo");
            fail("expecting NamingException");
        } catch (NamingException e) {
            // OK
        }
        assertEquals("Hello", envContext.lookup(new CompositeName("hello")));
        assertEquals("Hello", compContext.lookup(new CompositeName("env/hello")));
        assertEquals("World", 
            ((Context) initialContext.lookup("java:comp")).lookup("env/world"));
    }
    

    public void testComposeName() throws NamingException {
        assertEquals("org/research/user/jane", 
            envContext.composeName("user/jane", "org/research"));
        assertEquals("research/user/jane", 
            envContext.composeName("user/jane", "research"));
        assertEquals(new CompositeName("org/research/user/jane"), 
            envContext.composeName(new CompositeName("user/jane"), 
                new CompositeName("org/research")));
        assertEquals(new CompositeName("research/user/jane"), 
            envContext.composeName(new CompositeName("user/jane"), 
                new CompositeName("research")));
    }

    public void testList() throws NamingException {
        NamingEnumeration enumeration;
        Map expected;
        Map result;

        expected = new HashMap();
        for (Iterator i = envBinding.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            expected.put(entry.getKey(), entry.getValue().getClass().getName());
        }
        enumeration = envContext.list("");
        result = new HashMap();
        while (enumeration.hasMore()) {
            NameClassPair pair = (NameClassPair) enumeration.next();
            result.put(pair.getName(), pair.getClassName());
        }
        assertEquals(expected, result);

        try {
            enumeration.next();
            fail();
        } catch (NoSuchElementException e) {
            // ok
        }
        try {
            enumeration.nextElement();
            fail();
        } catch (NoSuchElementException e) {
            // ok
        }
    }

    public void testListBindings() throws NamingException {
        NamingEnumeration enumeration;
        Map result;
        enumeration = envContext.listBindings("");
        result = new HashMap();
        while (enumeration.hasMore()) {
            Binding pair = (Binding) enumeration.next();
            result.put(pair.getName(), pair.getObject());
        }
        assertEquals(envBinding, result);

        try {
            enumeration.next();
            fail();
        } catch (NoSuchElementException e) {
            // ok
        }
        try {
            enumeration.nextElement();
            fail();
        } catch (NoSuchElementException e) {
            // ok
        }
    }
    
    public void testAccess() throws NamingException {
        try {
            envContext.bind("goodbye", "Goodbye");
            fail("expecting NamingException"); // Context is read only
        } catch (NamingException ex) {}   
        ContextAccessController.setWritable(javaURLContextFactory.MAIN,"x");
        envContext.bind("goodbye", "Goodbye"); // Unlocked now
    }  
}
