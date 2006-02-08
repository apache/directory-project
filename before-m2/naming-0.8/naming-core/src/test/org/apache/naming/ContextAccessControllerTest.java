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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junit.framework.TestCase;


/**
 * Unit tests for  {@link ContextAccessController}.
 *  
 * @version $Revision$ $Date: 2003/11/30 05:36:07 $
 */
public class ContextAccessControllerTest extends TestCase {
    
    public ContextAccessControllerTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(ContextAccessControllerTest.class);
    	suite.setName("ContextAccessController Tests");
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
        testContext.destroySubcontext("env:comp");
        testContext = null;
    }
    
   public void testAccessControl() throws Exception {
       // no token set, check should return true for any token
       assertTrue(ContextAccessController.checkSecurityToken(contextName, testToken1));
       
       // set token and check access
       ContextAccessController.setSecurityToken(contextName, testToken1);
       assertTrue(ContextAccessController.checkSecurityToken(contextName, testToken1));
       assertFalse(ContextAccessController.checkSecurityToken(contextName, testToken2));      
      
       // no token set, return true
       assertTrue(ContextAccessController.checkSecurityToken(subName, testToken1));
       
       // set null token, should have no effect
       ContextAccessController.setSecurityToken(contextName, null);
       assertTrue(ContextAccessController.checkSecurityToken(subName, testToken1));
       
       // set valid token
       ContextAccessController.setSecurityToken(subName, testToken2);
       // parent's token should not work
       assertFalse(ContextAccessController.checkSecurityToken(subName, testToken1)); 
       assertTrue(ContextAccessController.checkSecurityToken(subName, testToken2)); 
       
       // try to unset using wrong token, should have no effect
       ContextAccessController.unsetSecurityToken(contextName, testToken2);
       assertFalse(ContextAccessController.checkSecurityToken(contextName, testToken2));
       ContextAccessController.unsetSecurityToken(contextName, null);
       assertFalse(ContextAccessController.checkSecurityToken(contextName, testToken2));
       
       // unset token
       ContextAccessController.unsetSecurityToken(contextName, testToken1);
       assertTrue(ContextAccessController.checkSecurityToken(contextName, testToken2));
       assertTrue(ContextAccessController.checkSecurityToken(contextName, null));      
   }
   
   public void testMutationControl() throws Exception {
       // verify that contexts are writable by default
       assertTrue(ContextAccessController.isWritable(contextName));
       assertTrue(ContextAccessController.isWritable(subName));
       
       // make context read only without setting token
       ContextAccessController.setReadOnly(contextName);
       assertFalse(ContextAccessController.isWritable(contextName));
       // property is not inherited, however, by subcontext
       assertTrue(ContextAccessController.isWritable(subName));
       // can use any token value to make writable
       ContextAccessController.setWritable(contextName, null);
       assertTrue(ContextAccessController.isWritable(contextName));
       
       // set token, so setWritable will need correct token
       ContextAccessController.setSecurityToken(contextName, testToken1);
       ContextAccessController.setReadOnly(contextName);
       // wrong token will not work
       ContextAccessController.setWritable(contextName, testToken2);
       assertFalse(ContextAccessController.isWritable(contextName));
       // now here's the ticket...
       ContextAccessController.setWritable(contextName, testToken1);
       assertTrue(ContextAccessController.isWritable(contextName));
   }
    
}
