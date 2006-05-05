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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.add;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * Basic tests for the LDAP add operation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BasicAddTests extends BaseProtocolTest
{

    private static String PERSON_SN_VALUE = "Amos";

    private static String PERSON_CN_VALUE = "Tori Amos";

    private static String PERSON_RDN = "cn=" + PERSON_CN_VALUE;
    
    DirContext ctx;

    public void setUp() throws NamingException
    {
        super.setUp();

        ctx = this.createContext();
        ctx = (DirContext) ctx.lookup(this.getTestContainerRdn());
    }

    public void tearDown() throws NamingException
    {
        ctx.close();
        ctx = null;

        super.tearDown();
    }

    /**
     * Just checks whether creation of a simple entry succeeds.
     * 
     * @throws NamingException
     */
    public void testAddEntry() throws NamingException
    {
        // Add a person
        Attributes attributes = AttributesFactory.createPersonAttributes(PERSON_CN_VALUE, PERSON_SN_VALUE);
        DirContext person = ctx.createSubcontext(PERSON_RDN, attributes);

        // Check wether person looks fine
        person = (DirContext) ctx.lookup(PERSON_RDN);
        assertNotNull(person);
        attributes = person.getAttributes("");

        // objectclasses
        // TODO: Move this useful algorithm to a tool class
        Attribute ocls = attributes.get("objectClass");
        String[] expectedOcls = { "top", "person" };
        for (int i = 0; i < expectedOcls.length; i++) {
            boolean found = false;
            String name = expectedOcls[i].toLowerCase();
            for (int j = 0; j < ocls.size(); j++) {
                if (name.equalsIgnoreCase(ocls.get(j).toString())) {
                    found = true;
                }
            }
            assertTrue("object class " + name + " is not present", found);
        }

        // Other attributes
        Attribute cn = attributes.get("cn");
        assertTrue("cn value", cn.contains(PERSON_CN_VALUE));
        Attribute sn = attributes.get("sn");
        assertTrue("sn value", sn.contains(PERSON_SN_VALUE));
    }

    /**
     * Checks whether adding an entry with existing DN causes an error.
     * 
     * @throws NamingException
     */
    public void testAddDuplicateEntry() throws NamingException
    {
        Attributes attributes = AttributesFactory.createPersonAttributes(PERSON_CN_VALUE, PERSON_SN_VALUE);
        ctx.createSubcontext(PERSON_RDN, attributes);

        try {
            ctx.createSubcontext(PERSON_RDN, attributes);
            fail("duplicate entry should fail");
        } catch (NameAlreadyBoundException e) {
            // Excepted behaviour;
            assertTrue(true);
        }
    }

    /**
     * Checks whether adding an entry with multi valued attribute succeeds.
     * 
     * @throws NamingException
     */
    public void testAddEntryWithMultipleValues() throws NamingException
    {
        // Create a person, description has multiple attributes
        Attributes attributes = AttributesFactory.createPersonAttributes(PERSON_CN_VALUE, PERSON_SN_VALUE);
        Attribute descr = new BasicAttribute("description");
        descr.add("a description for the person");
        descr.add("another description for this person");
        attributes.put(descr);
        int two = descr.size();

        ctx.createSubcontext(PERSON_RDN, attributes);

        DirContext tori = (DirContext) ctx.lookup(PERSON_RDN);
        attributes = tori.getAttributes("");
        descr = attributes.get("description");

        assertEquals(two, descr.size());
    }
    
    // TODO: Add test for binary data (here, or in an own test case 
}
