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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.compare;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SearchControls;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$ 
 */
public class BasicCompareTests extends BaseProtocolTest {

	DirContext ctx;
	DirContext target;

	public static final String RDN = "cn=Tori Amos";

	public void setUp() throws NamingException {
		super.setUp();
		
		ctx    = this.createContext();
		target = (DirContext) ctx.lookup(this.getTestContainerRdn()); 

		// Create a person
		Attributes attributes = AttributesFactory.createPersonAttributes("Tori Amos", "Amos");
		target.createSubcontext(RDN, attributes);
	}

	public void tearDown() throws NamingException {
		target.unbind(RDN);
		target.close();
		
		ctx.close();
		ctx = null;
		
		super.tearDown();
	}

	/**
	 * Compare an existing attribute.
	 * 
	 * @throws NamingException
	 */
	public void testCompareExisting() throws NamingException {

		// Setting up search controls
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[0]); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		// search operation, which leads to a compare
		// value is correct
		NamingEnumeration enumeration = target.search(RDN, "sn={0}",
				new String[] { "Amos" }, ctls);

		// Check, that result contains exactly one entry
		boolean result = enumeration.hasMore();
		assertTrue(result);
		if (result) {
			enumeration.next();
			assertFalse(enumeration.hasMore());
		}

		// search operation, which leads to a compare.
		// value is wrong
		enumeration = target.search(RDN, "sn={0}", new String[] { "Bush" }, ctls);

		// Check, that result contains no entry
		assertFalse(enumeration.hasMore());
	}

	/**
	 * Compare an attribute which does not exist in the entry.
	 * 
	 * @throws NamingException
	 */
	public void testCompareNonExisting() throws NamingException {

		// Setting up search controls
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[0]); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		// search operation, which leads to a compare.
		// the entry does not have a value for the attribute
		try {
			target.search(RDN, "description={0}",
					new String[] { "a description" }, ctls);
			fail("operation should cause an error");
		} catch (NoSuchAttributeException nsae) {
			// expected behaviour
		}
	}

	/**
	 * Compare an invalid attribute.
	 * 
	 * @throws NamingException
	 */
	public void testCompareInvalidAttribute() throws NamingException {

		// Setting up search controls
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[0]); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		// search operation, which leads to a compare
		// attribute does not exist
		try {
			target.search(RDN, "XXX={0}", new String[] { "a value" }, ctls);
			fail("operation should cause an error");
		} catch (NamingException iaie) {
			// expected behaviour
			
			// TODO: add a more detailed Exception here

			// I am uncertain, whether Return code 16 or 17 is appropriate here.
			// both values occur, depending on the server you use
			// 16: noSuchAttribute
			// 17: undefinedAttributeType
		}
	}
}
