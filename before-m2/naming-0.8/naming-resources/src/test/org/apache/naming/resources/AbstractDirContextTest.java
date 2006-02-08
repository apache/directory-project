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
package org.apache.naming.resources;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NameParser;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;

import junit.framework.TestCase;

/**
 * Abstract base class for DirContext tests.
 * 
 * In addition to overriding makeInitialContext() and 
 * setting switches, test classes derived from this class should:
 * 
 * -- override isSchemaSupported() to return false if schema are not supported
 * -- override isAttributeModificationSupported() to return false if attribute
 *     modification is not supported
 * -- if attribute modification operations are supported, override 
 *     modifyAttributeName(), modifyAttributeValue(), etc. to designate names
 *     and values appropriate for firstBoundObject() (from AbstractContextTest).
 *  
 * @version $Revision$ $Date: 2003/11/30 05:36:07 $
 */
public abstract class AbstractDirContextTest extends TestCase {
    public AbstractDirContextTest(String name) {
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
    
    //-------------------- Override these methods to set up test namespace ------------------------
    
    /** firstContext name -- relative to InitialContext  */
    protected String firstContextName() {
        return "firstDir";
    }
    
    /** secondContext name -- relative to first context  */
    protected String secondContextName() {
        return "secondDir";
    }
    
    /** First name to bind */
    protected String firstBoundName() {
        return "test1";
    }
    
    /** First bound object */
    protected Object firstBoundObject() {
        return new Resource(new ByteArrayInputStream(bytes));
    }
    
    /** Second name to bind */
    protected String secondBoundName() {
        return "test2";
    }
    
    /** Second bound object */
    protected Object secondBoundObject() {
        return new Resource(new ByteArrayInputStream(bytes));
    }
    
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
        nameParser = initialContext.getNameParser("");
    }
    
    protected void tearDown() throws Exception {
        if (isWritable()) {
            removeBindings();
            firstContext.destroySubcontext(secondContextName());
            initialContext.destroySubcontext(firstContextName());
        }
        initialContext = null;
    }
    
    //---------------------- Test data for DirContext tests ---------------------------------------
    
    /** A few bytes to write to temp files created */
    protected byte[] bytes = {'a', 'b', 'c'}; 
    
    /**
     * Expected attributes associated with firstBoundObject()
     */
    protected Attributes expectedAttributes() {
        return null;
    }
    
    /** Name parser from initialContext */
    protected NameParser nameParser = null;
    
    //-------------- Switches to turn off tests if features are not supported ---------------------

    /**
     * Does this DirContext support shema -- i.e. implement getSchema(), getSchemaClassDefinition()?
     * Override to return false if schema methods are not supported.
     */
    protected boolean isSchemaSupported() {
        return true;
    }
    
    /**
     * Does this DirContext support attribute modification?
     * Override to return false if attributes cannot be modified.
     */
    protected boolean isAttributeModificationSupported() {
        return true;
    }
    
    //---------------- Attribute names and values for modification tests --------------------------
    /** 
     *   Name of the attribute associated with firstBoundObject() to replace. 
     *   Override to return the name of an attribute of firstBoundObject() whose value can be replaced
     *   if this is supported 
     */
    protected String replaceAttributeName() {
        return null;
    }
    
    /** 
     *   Replacement value for replaceAttributeName.
     *   Override to return the replacement value if this is supported.
     */
    protected Object replaceAttributeValue() {
        return null;
    }
    
    /** 
     *   Name of attribute of firstBoundObject() object to add. 
     *   Override to return the name of a new attribute to add to the attributes of firstBoundObject()
     *   if this is supported.
     */
    protected String addAttributeName() {
        return null;
    }
    
    /** 
     *   Value for addAttributeName.
     *   Override to return the value for the new attribute if this is supported.
     */
    protected Object addAttributeValue() {
        return null;
    }
    
    /** 
     *   Name of attribute of firstBoundObject() to remove. 
     *   Override to return the name of an attribute to remove from the attributes of firstBoundObject()
     *   if this is supported.
     */
    protected String removeAttributeName() {
        return null;
    }
    
    // -------------------------------- Verification methods -----------------------------------------------
    
    /**
     *  Verify that object returned by lookup operation is "same" as bound object.
     *  Override this method if the object returned by looking up the name of a bound
     *  object is not equal to the originally bound object.
     */
    protected void verifyLookup(Object boundObject, Object returnedObject) {
        assertEquals(boundObject, returnedObject);
    }
    
    /**
     * Verify that the listed bound names match expectation and
     * that the class names of bound objects are not empty
     */
    protected void verifyList(Map expected, Map returned) {
        assertEquals(expected.keySet(), returned.keySet());
        Iterator iterator = returned.values().iterator();
        while (iterator.hasNext()) {
            assertTrue(((String) iterator.next()).length() > 0);
        }
    }
    
    /**
     * Verify that the listed bound names match expectation
     */
    protected void verifyListBindings(Map expected, Map returned) {
        assertEquals(expected.keySet(), returned.keySet());
    }
    
    /**
     * Verify that the Attributes associated with <name> in <context> include an Attribute with 
     * attribute ID = <attributeName> and this Attribute contains the value <attributeValue>
     */
    protected void verifyAttribute(DirContext context, String name, String attributeName, 
            Object attributeValue) throws Exception {
        String attrIds[] = new String[1];
        attrIds[0] = attributeName;
        Attributes attrs = context.getAttributes(name, attrIds);
        assertTrue(attrs.get(attributeName).contains(attributeValue)); 
    }
    
    /**
     * Verify that the Attributes associated with <name> in <context> do not include an Attribute with 
     * attribute ID = <attributeName>
     */
    protected void verifyAttributeMissing(DirContext context, String name, String attributeName) throws Exception {
        String attrIds[] = new String[1];
        attrIds[0] = attributeName;
        Attributes attrs = context.getAttributes(name, attrIds);
        assertEquals(0, attrs.size()); 
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
    
    /**
     * Verify that getAttributes returns a valid NamingEnumeration of Attributes.
     * If expectedAttributes() has been overriden to return a non-null set of attributes, 
     * this set is tested for equality with the Attributes returned by getAttributes.
     */
   public void testAttributes() throws Exception {
      DirContext context = (DirContext) initialContext.lookup(firstContextName()+ "/" + secondContextName());
      Attributes attrs = (Attributes) context.getAttributes(firstBoundName());
      NamingEnumeration enumeration = attrs.getAll();
      while (enumeration.hasMoreElements()) {
          assertTrue(enumeration.nextElement() instanceof Attribute);
      }
      Attributes expected = expectedAttributes();
      if (expected != null) {
          assertEquals(expected, attrs);
      }
   }
   
   /**
    * Verify that getSchema and getSchemaClassDefinition return non-null DirContexts.
    * This test will only be run if isSchemaSupported returns true.
    * @throws Exception
    */
   public void testGetSchema() throws Exception {
       if (!isSchemaSupported()) {
           try {
               ((DirContext) initialContext.lookup("")).getSchema("");
               fail("Expecting OperationNotSupportedException");
           } catch (OperationNotSupportedException ex) {
               // expected
           }
           return;
       }
       DirContext context = (DirContext) initialContext.lookup("");
       DirContext schemaContext = context.getSchema("");
       assertNotNull(schemaContext);
       Name name = nameParser.parse(firstContext.getNameInNamespace());
       schemaContext = context.getSchema(name);
       assertNotNull(schemaContext);
       schemaContext = context.getSchema(firstBoundName());
       assertNotNull(schemaContext);
       schemaContext = context.getSchemaClassDefinition(name);
       assertNotNull(schemaContext);
       schemaContext = context.getSchemaClassDefinition(firstBoundName());
       assertNotNull(schemaContext);          
   }
   
   /**
    * Tests attribute modification operations on attributes -- add, update, delete.
    * If no attribute modifications are supported, override isAttributeModificationSupported() to return
    * false and this test will be skipped.  If one or more of add, update, delete are not supported,
    * just leave the associated *attributeName() method returning null and the associated operation
    * will not be tested. 
    */
   public void testAttributeModification() throws Exception {
       if (!isAttributeModificationSupported()) {
           return;
       }    
       ModificationItem[] modifications = new ModificationItem[1];
       DirContext context = (DirContext) initialContext.lookup(firstContextName() + "/"
               + secondContextName() +"/" + firstBoundName()); 
      
      if(addAttributeName() != null) {
          Attribute addModification = new BasicAttribute(addAttributeName(), addAttributeValue());
          modifications[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, addModification);     
          context.modifyAttributes("", modifications);
          verifyAttribute(context, "", addAttributeName(), addAttributeValue());
      }
      
      if(replaceAttributeName() != null) {
          Attribute replaceModification = new BasicAttribute(replaceAttributeName(), replaceAttributeValue());
          modifications[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, replaceModification);     
          context.modifyAttributes("", modifications);
          verifyAttribute(context, "", replaceAttributeName(), replaceAttributeValue());
      }
      
      if(removeAttributeName() != null) {
          Attribute removeModification = new BasicAttribute(removeAttributeName());
          modifications[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, removeModification);     
          context.modifyAttributes("", modifications);
          verifyAttributeMissing(context, "", removeAttributeName());
      }
   }
  
}
