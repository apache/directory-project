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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * Unit tests for basic ops on a {@link SynchronizedContext}.
 *  
 * @version $Revision: 1.2 $ $Date: 2003/11/30 05:36:07 $
 */
public class SynchronizedContextTest extends AbstractContextTest {
    
    public SynchronizedContextTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
    	TestSuite suite = new TestSuite(SynchronizedContextTest.class);
    	suite.setName("Synchronized Context Tests");
        return suite;
    }
    
    protected Context makeInitialContext() {
    	try {
            Hashtable env = new Hashtable();
            env.put(SynchronizedContext.SYNCHRONIZED, "true");
            NamingContext ctx = new NamingContext(env, "root");
    		return new SynchronizedContext(ctx);
    	} catch (Exception ex) {
    		fail("Failed to create SynchronizedContext");
    	}
    	return null;
    }
    
    protected boolean isGetNameInNamespaceSupported() {
    	return false;
    }
    
    public void testIsSynchronized() throws Exception {
        assertTrue(SynchronizedContext.isSynchronized(initialContext.getEnvironment()));
        Hashtable env = new Hashtable();
        env.put(SynchronizedContext.SYNCHRONIZED, "false");
        assertFalse(SynchronizedContext.isSynchronized(env));
        env = null;
        assertFalse(SynchronizedContext.isSynchronized(env));
    }
}
