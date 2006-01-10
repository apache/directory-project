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

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;

import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * Unit tests for basic ops on a {@link FileDirContext}.
 *  
 * @version $Revision$ $Date: 2003/11/30 05:36:07 $
 */
public class FileDirContextTest extends AbstractDirContextTest {
    
    public FileDirContextTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(FileDirContextTest.class);
    	suite.setName("FileDirContext Tests");
        return suite;
    }
    
    //-------------------- AbstractContextTest overrides ------------------------------------------
    
    protected Context makeInitialContext() {
    	try {
    	    FileDirContext fdc = new FileDirContext();
    	    fdc.setDocBase(".");
    		return fdc;
    	} catch (Exception ex) {
    		fail("Failed to create Initial Context");
    	}
    	return null;
    }
    
    /**
     * Just verify that the bound object is a resource 
     */
    protected void verifyLookup(Object boundObject, Object returnedObject) {
        assertTrue(returnedObject instanceof Resource);
    }
    
    /**
     * Verify that the listed bound names match expectation and
     * that the bound objects are Resources
     */
    protected void verifyListBindings(Map expected, Map returned) {
        super.verifyListBindings(expected, returned);
        Iterator iterator = returned.values().iterator();
        while (iterator.hasNext()) {
            assertTrue(iterator.next() instanceof Resource);
        }
    }
    
    protected boolean isGetNameInNamespaceSupported() {
        return true;
    }
    
    protected boolean isWritable() {
        return true;
    }
    
    //-------------------- AbstractDirContextTest overrides ---------------------------------------
    
    protected boolean isSchemaSupported() {
        return false;
    }
    
    protected boolean isAttributeModificationSupported() {
        return false;
    }
    
    /**
     * Verify file attributes
     */
    public void testAttributes() throws Exception {
        super.testAttributes();
        DirContext context = (DirContext) initialContext.lookup(firstContextName()+ "/" + secondContextName());
        Attributes attrs = (Attributes) context.getAttributes(firstBoundName());
        Date creationDate = (Date) attrs.get(ResourceAttributes.CREATION_DATE).get();
        assertTrue(creationDate.before(new Date()));
        Date modifiedDate = (Date) attrs.get(ResourceAttributes.LAST_MODIFIED).get();
        assertTrue(modifiedDate.equals(creationDate));
        String displayName = (String) attrs.get(ResourceAttributes.NAME).get();
        assertEquals(displayName, firstBoundName());
        long contentLength = ((Long) attrs.get(ResourceAttributes.CONTENT_LENGTH).get()).longValue();
        assertEquals(contentLength, bytes.length);
        String resourceType = (String) attrs.get(ResourceAttributes.TYPE).get();
        assertEquals(resourceType, "");                                                // Present, but empty --  is this the correct?  
        assertNull(attrs.get(ResourceAttributes.CONTENT_TYPE));  //  Not present -- is this correct?         
        assertNull(attrs.get(ResourceAttributes.SOURCE));                //        ""        ""       
        assertNull(attrs.get(ResourceAttributes.ETAG));                      //        ""        ""        
    }
    
    public void testGetNameInNamespace() throws Exception {
        super.testGetNameInNamespace();
        Name name = nameParser.parse(firstContext.getNameInNamespace());
        assertTrue(name.endsWith(nameParser.parse(firstContextName())));
        name = nameParser.parse(secondContext.getNameInNamespace());
        assertTrue(name.endsWith(nameParser.parse(secondContextName())));
    }
}
