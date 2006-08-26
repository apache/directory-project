/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.ldap.testsuite.ldaptests.jndi.ops.modifydn;

import junit.framework.TestSuite;

/**
 * Contains the test classes for the LDAP modify DN operation executed via JNDI.
 * It is used to form the complete suite into a structure.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AllTests extends TestSuite
{

    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite("Modify DN");

        suite.addTestSuite(BasicModifyDnTests.class);
        suite.addTestSuite(ModifyDnSameAttributeTests.class);

        return suite;
    }
}
