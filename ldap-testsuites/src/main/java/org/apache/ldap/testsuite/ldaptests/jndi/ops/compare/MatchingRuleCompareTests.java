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
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Tests for the Compare operation with JNDI.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $ 
 */

public class MatchingRuleCompareTests extends BaseProtocolTest {

	DirContext ctx;

	public static final String PERSON_CN = "Tori Amos";
	
	public static final String PERSON_SN = "Amos";
	
	public static final String PERSON_RDN = "cn="+PERSON_CN;

	public static final String PERSON_TELEPHONE = "1234567890abc";

	public static final String PERSON_PWD = "Secret1!";

	public static final String GROUP_CN = "Artists";
	
	public static final String GROUP_RDN = "cn=Artists";

	protected Attributes getPersonAttributes(String sn, String cn) {
		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		attribute.add("top");
		attribute.add("person");
		attributes.put(attribute);
		attributes.put("cn", cn);
		attributes.put("sn", sn);

		return attributes;
	}

	protected Attributes getGroupOfNamesAttributes(String cn, String member) {
		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		attribute.add("top");
		attribute.add("groupOfNames");
		attributes.put(attribute);
		attributes.put("cn", cn);
		attributes.put("member", member);

		return attributes;
	}

	public void setUp() throws NamingException {
		super.setUp();

		ctx = this.createContext();
		ctx = (DirContext) ctx.lookup(this.getTestContainerRdn());

		// Create a person
		Attributes attributes = this.getPersonAttributes(PERSON_SN, PERSON_CN);
		attributes.put("telephoneNumber", PERSON_TELEPHONE);
		attributes.put("userPassword", PERSON_PWD);
		ctx.createSubcontext(PERSON_RDN, attributes);

		// Create a group
		DirContext member = (DirContext) ctx.lookup(PERSON_RDN);
		attributes = this.getGroupOfNamesAttributes(GROUP_CN, member
				.getNameInNamespace());
		ctx.createSubcontext(GROUP_RDN, attributes);

	}

	public void tearDown() throws NamingException {

		ctx.unbind(PERSON_RDN);
		ctx.unbind(GROUP_RDN);

		ctx.close();
		ctx = null;

		super.tearDown();
	}

	/**
	 * Compare with caseIgnoreMatch matching rule.
	 * 
	 * @throws NamingException
	 */
	public void testCaseIgnoreMatch() throws NamingException {
		// Setting up search controls
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		String[] values = { PERSON_SN, PERSON_SN.toUpperCase(), PERSON_SN.toLowerCase(), PERSON_SN+"X" };
		boolean[] expected = { true, true, true, false };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];

			NamingEnumeration enumeration = ctx.search(PERSON_RDN, "sn={0}",
					new String[] { value }, ctls);
			// Check, that result contains exactly one entry
			boolean result = enumeration.hasMore();

			assertEquals("compare sn value '" + PERSON_SN + "' with '" + value + "'", expected[i],
					result);

			enumeration.close();
		}
	}

	//

	/**
	 * Compare with telephoneNumberMatch matching rule.
	 * 
	 * @throws NamingException
	 */
	public void testTelephoneNumberMatch() throws NamingException {
		// Setting up search controls
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		String[] values = { "", "1234567890abc", "   1234567890 A B C",
				"123 456 7890 abc", "123-456-7890 abC", "123456-7890 A bc" };
		boolean[] expected = { false, true, true, true, true, true };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];

			NamingEnumeration enumeration = ctx.search(PERSON_RDN,
					"telephoneNumber={0}", new String[] { value }, ctls);
			boolean result = enumeration.hasMore();

			assertEquals("compare '1234567890' with '" + value + "'",
					expected[i], result);

			enumeration.close();
		}
	}

	/**
	 * Compare with octetStringMatch matching rule.
	 * 
	 * @throws NamingException
	 */
	public void testOctetStringMatch() throws NamingException {
		// Setting up search controls
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		String[] values = { "", PERSON_PWD, PERSON_PWD.toUpperCase(), PERSON_PWD.toLowerCase(), PERSON_PWD+"X" };
		boolean[] expected = { false, true, false, false, false };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];

			NamingEnumeration enumeration = ctx.search(PERSON_RDN,
					"userPassword={0}", new String[] { value }, ctls);
			boolean result = enumeration.hasMore();

			assertEquals("compare 'Secret1!' with '" + value + "'",
					expected[i], result);

			enumeration.close();
		}
	}

	public void testDistinguishedNameMatch() throws NamingException {

		// determine member DN
		DirContext member = (DirContext) ctx.lookup(PERSON_RDN);
		String memberDN = member.getNameInNamespace();

		// Setting up search controls for compare op
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(new String[] {}); // no attributes
		ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

		String[] values = { "", memberDN, "cn=nobody", memberDN.toLowerCase(),
				PERSON_RDN + " , " + ctx.getNameInNamespace() };
		boolean[] expected = { false, true, false, true, true };

		for (int i = 0; i < values.length; i++) {
			String value = values[i];

			NamingEnumeration enumeration = ctx.search(GROUP_RDN, "member={0}",
					new Object[] { value }, ctls);
			boolean result = enumeration.hasMore();

			assertEquals("compare 'member' with '" + value + "'", expected[i],
					result);

			enumeration.close();
		}
	}
}