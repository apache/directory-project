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
package org.apache.naming.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.naming.NamingContext;
import org.apache.naming.ContextBindings;


/**
 * Unit tests for jndiURLFactory.
 *  
 * @version $Revision: 123481 $ $Date: 2003/11/30 05:36:07 $
 */
public class jndiURLFactoryTest extends TestCase {
    
    public jndiURLFactoryTest(String name) {
        super(name);
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(jndiURLFactoryTest.class);
    	suite.setName("jndi URL Factory Tests");
        return suite;
    }
    
    public void testListFailure() throws Exception {
        try {
            Object obj = new InitialContext().list("jndi:global/config/host");
            fail("Expecting NamingException");
        } catch (NamingException ex) {
            // expected
        }
    }
    
    public void testLookupSuccess() throws Exception {
       Context ctx = new NamingContext(new Hashtable(), "test");
       ctx.createSubcontext("config");
       ctx.bind("config/host", "www.apache.org");
       ContextBindings.bindContext("test", ctx);
       assertEquals("www.apache.org", 
               (String) new InitialContext().lookup("jndi:test/config/host")); 
    }
    
}
