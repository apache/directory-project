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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

import junit.framework.TestCase;

/**
 * Abstract base class for Context tests.
 * 
 * Test classes derived from this class must implement makeInitialContext().
 * If the context is not writable, override isWritable() to return false.
 * If getNameInNamespace() is not supported, override isGetNameInNamespace() to
 * return false.
 * 
 * For writable contexts, the default setup() implementation creates:
 * 
 *  -- a subcontext (firstContext) of the InitialContext returned by makeInitialContext(), 
 *     using firstContextName(), which defaults to "java:comp"
 *  -- a subcontext (secondContext) of firstContext (secondContext) using secondContextName() (default = "env")
 *  -- two object bindings on secondContext:  firstBoundObject() bound to firstBoundName() and
 *     secondBoundObject() bound to secondBoundName()
 * 
 * Basic tests included verify binding, context lookup, name composition, and list operations.
 * 
 * @version $Revision$ $Date$
 */
public abstract class AbstractContextTest extends TestCase {
    
    public AbstractContextTest(String name) {
        super(name);
    }
    
    //-------------------------- Contexts to use in tests ----------------------------------------------------
   
    /** Initial Context used in tests */
    protected Context initialContext;
    
    /** Immediate subcontext of initialContext */
    protected Context firstContext;
    
    /** Immediate subcontext of firstContext */
    protected Context secondContext;
    
    /** HashMap of Object Bindings for verification */
    protected HashMap binding;
    
    //-------------------- Override these methods to set up test namespace ------------------------
    
    /** firstContext name -- relative to InitialContext  */
    protected String firstContextName() {
        return "java:comp";
    }
    
    /** secondContext name -- relative to first context  */
    protected String secondContextName() {
       return "env";
    }
    
    /** First name to bind */
    protected String firstBoundName() {
        return "hello";
    }
    
    /** First bound object */
    protected Object firstBoundObject() {
        return "Hello";
    }
    
    /** Second name to bind */
    protected String secondBoundName() {
        return "world";
    }
    
    /** Second bound object */
    protected Object secondBoundObject() {
       return "World";
    }
    
    //----------------- Switches to turn off tests for unsupported operations ----------------------
    
    /**
     * Does this context support getNameInNamespace()?
     * Defaults to true -- override if not supported
     */
    protected boolean isGetNameInNamespaceSupported() {
    	return true;
    }
    
    /**
     * Can bindings be added to this context?
     * Defaults to true -- override if not supported
     */
    protected boolean isWritable() {
        return true;
    }
    
    /**
     * Create an initial context to be used in tests
     */
    protected abstract Context makeInitialContext();
    
    //----------------------------------  Setup / teardown operations -----------------------------
    
    /**
     *  Add bindings
     */
    protected void addBindings() throws Exception {
        secondContext.bind(firstBoundName(), firstBoundObject());
        secondContext.bind(secondBoundName(), secondBoundObject());
        binding.put(firstBoundName(), firstBoundObject());
        binding.put(secondBoundName(), secondBoundObject());     
    }
    
    /**
     *  Remove bindings
     */
    protected void removeBindings() throws Exception {
        secondContext.unbind(firstBoundName());
        secondContext.unbind(secondBoundName());
        binding.clear(); 
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        binding = new HashMap();
        initialContext = makeInitialContext();
        if (isWritable()) {
            firstContext = initialContext.createSubcontext(firstContextName());
            secondContext =  firstContext.createSubcontext(secondContextName());
            addBindings();
        }
    }
    
    protected void tearDown() throws Exception {
        if (isWritable()) {
            removeBindings();
            firstContext.destroySubcontext(secondContextName());
            initialContext.destroySubcontext(firstContextName());
        }
        initialContext = null;
    }
    
    //-------------------------- Verification methods -------------------------------------------------
    
    /**
     *  Verify that object returned by lookup operation is "same" as bound object.
     *  Override this method if the object returned by looking up the name of a bound
     *  object is not equal to the originally bound object.
     */
    protected void verifyLookup(Object boundObject, Object returnedObject) {
        assertEquals(boundObject, returnedObject);
    }
    
    /**
     * Verify that the names and classes in the returned Map (from list() method)
     * match expected results.  Override this method if the classes of bound objects
     * returned by list() are not the same as the objects originally bound.
     */
    protected void verifyList(Map expected, Map returned) {
        assertEquals(expected, returned);
    }
    
    /**
     * Verify that the names and objects in the returned Map (from listBindings() method)
     * match expected results.  Override this method if bound objects
     * returned by list() are not the same as the objects originally bound.
     */
    protected void verifyListBindings(Map expected, Map returned) {
        assertEquals(expected, returned);
    }
    
   //--------------------------- Default implementations for basic tests -------------------------- 

    public void testInitialContext() throws NamingException {
        verifyLookup(firstBoundObject(), 
                initialContext.lookup(firstContextName() + "/"
                        + secondContextName() +"/" + firstBoundName()));
        verifyLookup(secondBoundObject(), 
                initialContext.lookup(new CompositeName
                        (firstContextName() + "/" + secondContextName()  + "/" + secondBoundName())));
        verifyLookup(secondContext.lookup(firstBoundName()), 
            ((Context) secondContext.lookup("")).lookup(firstBoundName()));  
    }

    public void testLookup() throws NamingException {
        verifyLookup(firstBoundObject(), secondContext.lookup(firstBoundName()));
        verifyLookup(firstBoundObject(), 
                firstContext.lookup(secondContextName() + "/" +firstBoundName()));
        try {
            secondContext.lookup("foo");
            fail("expecting NamingException");
        } catch (NamingException e) {
            // expected
        }
        verifyLookup(firstBoundObject(), 
                secondContext.lookup(new CompositeName(firstBoundName())));
        verifyLookup(firstBoundObject(), 
                firstContext.lookup(new CompositeName(secondContextName() + "/" + firstBoundName())));
        verifyLookup(secondBoundObject(), 
            ((Context) initialContext.lookup(firstContextName())).lookup(secondContextName() + "/" + secondBoundName()));
    }
    
    public void testComposeName() throws NamingException {
        assertEquals("org/research/user/jane", 
            secondContext.composeName("user/jane", "org/research"));
        assertEquals("research/user/jane", 
            secondContext.composeName("user/jane", "research"));
        assertEquals(new CompositeName("org/research/user/jane"), 
            secondContext.composeName(new CompositeName("user/jane"), 
                new CompositeName("org/research")));
        assertEquals(new CompositeName("research/user/jane"), 
            secondContext.composeName(new CompositeName("user/jane"), 
                new CompositeName("research")));
    }

    public void testList() throws NamingException {
        NamingEnumeration enumeration;
        Map expected;
        Map result;

        expected = new HashMap();
        for (Iterator i = binding.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            expected.put(entry.getKey(), entry.getValue().getClass().getName());
        }
        enumeration = secondContext.list("");
        result = new HashMap();
        while (enumeration.hasMore()) {
            NameClassPair pair = (NameClassPair) enumeration.next();
            result.put(pair.getName(), pair.getClassName());
        }
        verifyList(expected, result);

        try {
            enumeration.next();
            fail("Expecting NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
        try {
            enumeration.nextElement();
            fail("Expecting NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void testListBindings() throws NamingException {
        NamingEnumeration enumeration;
        Map result;
        enumeration = secondContext.listBindings("");
        result = new HashMap();
        while (enumeration.hasMore()) {
            Binding pair = (Binding) enumeration.next();
            result.put(pair.getName(), pair.getObject());
        }
        verifyListBindings(binding, result);

        try {
            enumeration.next();
            fail("Expecting NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
        try {
            enumeration.nextElement();
            fail("Expecting NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }
    
    /**
     * Default implementation just tests to make sure non-null names are returned
     * or correct exception is thrown.
     */
    public void testGetNameInNamespace() throws Exception {
    	if (isGetNameInNamespaceSupported()) {
    		String name = initialContext.getNameInNamespace();
    		if (name == null) {
    			fail("Null NameInNamespace for initial context");
    		}
    	} else {
    		try {
    			String name = firstContext.getNameInNamespace();
    			fail("Expecting OperationNotSupportedException");
    		} catch(OperationNotSupportedException ex) {
    			// expected
    		}
    	}	
    }
    
    /**
     *  Test rebind -- swap bound objects and verify
     */
    public void testRebind() throws Exception {
        secondContext.rebind(firstBoundName(), secondBoundObject());
        secondContext.rebind(secondBoundName(), firstBoundObject());
        binding.put(firstBoundName(), secondBoundObject());
        binding.put(secondBoundName(), firstBoundObject());      
        NamingEnumeration enumeration;
        Map result;
        enumeration = secondContext.listBindings("");
        result = new HashMap();
        while (enumeration.hasMore()) {
            Binding pair = (Binding) enumeration.next();
            result.put(pair.getName(), pair.getObject());
        }
        verifyListBindings(binding, result);     
    }
}
