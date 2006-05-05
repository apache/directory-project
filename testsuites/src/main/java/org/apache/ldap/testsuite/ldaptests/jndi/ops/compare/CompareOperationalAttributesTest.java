/*
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.ldap.testsuite.ldaptests.jndi.ops.compare;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * Tests with compare operations on the operational attributes createTimestamp,
 * modifyTimestamp, creatorsName, modifiersName.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$ 
 */
public class CompareOperationalAttributesTest extends BaseProtocolTest {

	private LdapContext ctx = null;

    public static final String OU_VALUE = "Peoples Front of Judea";
    
	public static final String RDN = "ou="+OU_VALUE;

	public void setUp() throws NamingException {
		super.setUp();

		ctx = this.createContext();
		ctx = (LdapContext) ctx.lookup(this.getTestContainerRdn());

		// Create an organizational Unit
		Attributes attributes = AttributesFactory.createOrganizationalUnitAttributes(OU_VALUE);
		ctx.createSubcontext(RDN, attributes);
	}

	public void tearDown() throws NamingException {
		ctx.unbind(RDN);
		ctx.close();
		ctx = null;

		super.tearDown();
	}

	/**
	 * Determine an attribute value with a search op.
	 * 
	 * @param attributeName
	 *            name of attribute
	 * @return attribute value
	 * @throws NamingException
	 */
	protected Object getAttributeValues(String attributeName)
			throws NamingException {

		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] { attributeName });
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		NamingEnumeration enumeration = ctx
				.search(RDN, "(objectClass=*)", ctls);
		assertTrue(enumeration.hasMore());
		SearchResult rs = (SearchResult) enumeration.next();
		Attribute attr = rs.getAttributes().get(attributeName);
		assertNotNull(attr);
		Object value = (String) attr.get();
		enumeration.close();

		return value;
	}

	/**
	 * Execute compare operations on the operational attribute createTimestamp.
	 * 
	 * @throws NamingException
	 */
	public void testCreateTimestamp() throws NamingException {
		// Determine createTimestamp of entry
		String createTimestamp = (String) this
				.getAttributeValues("createTimestamp");
		assertNotNull(createTimestamp);
		
		System.err.println(createTimestamp);

		// Setting up search controls for compare op
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		// Compare values and expected results
		String[] values = { createTimestamp, "19710401000000Z" };
		boolean[] expected = { false, true, false };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			NamingEnumeration enumeration = ctx.search(RDN,
					"createTimestamp={0}", new Object[] { value }, ctls);
			boolean result = enumeration.hasMore();
			assertEquals("compare 'createTimestamp' with '" + value + "'",
					expected[i], result);
			enumeration.close();
		}
	}

	/**
	 * Execute compare operations on the operational attribute creatorsName.
	 * 
	 * @throws NamingException
	 */
	public void testCreatorsName() throws NamingException {
		// Determine creatorsName of entry
		String creatorsName = (String) this.getAttributeValues("creatorsName");
		assertNotNull(creatorsName);

		// Setting up search controls for compare op
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		// Compare values and expected results
		String[] values = { "", creatorsName, "cn=Not the right value" };
		boolean[] expected = { false, true, false };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			NamingEnumeration enumeration = ctx.search(RDN, "creatorsName={0}",
					new Object[] { value }, ctls);
			boolean result = enumeration.hasMore();
			assertEquals("compare 'creatorsName' with '" + value + "'",
					expected[i], result);
			enumeration.close();
		}
	}

	/**
	 * Execute compare operations on the operational attribute modifiersName.
	 * 
	 * @throws NamingException
	 */
	public void testModifiersName() throws NamingException {
		// modify entry
		ctx.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE,
				new BasicAttributes("description", "Change this"));

		String modifiersName = (String) this
				.getAttributeValues("modifiersName");

		// Setting up search controls for compare op
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		// Compare values and expected results
		String[] values = { "", modifiersName, "cn=Not the right value" };
		boolean[] expected = { false, true, false };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];

			NamingEnumeration enumeration = ctx.search(RDN,
					"modifiersName={0}", new Object[] { value }, ctls);
			boolean result = enumeration.hasMore();
			assertEquals("compare 'modifiersName' with '" + value + "'",
					expected[i], result);
			enumeration.close();
		}
	}

	/**
	 * Execute compare operations on the operational attribute modifyTimestamp.
	 * 
	 * @throws NamingException
	 */
	public void testModifyTimestamp() throws NamingException {
		// wait 2s
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ignored) {
		}

		// modify entry
		ctx.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE,
				new BasicAttributes("description", "Change this"));

		// Determine timestamps of entry
		String modifyTimestamp = (String) this
				.getAttributeValues("modifyTimestamp");
		String createTimestamp = (String) this
				.getAttributeValues("createTimestamp");

		assertNotNull(modifyTimestamp);
		assertNotNull(createTimestamp);

		// Setting up search controls for compare op
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		// Compare values and expected results
		String[] values = { modifyTimestamp, createTimestamp,
				"19710401000000Z" };
		boolean[] expected = { false, true, false, false };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			NamingEnumeration enumeration = ctx.search(RDN,
					"modifyTimestamp={0}", new Object[] { value }, ctls);
			boolean result = enumeration.hasMore();
			assertEquals("compare 'modifyTimestamp' with '" + value + "'",
					expected[i], result);
			enumeration.close();
		}
	}
}
