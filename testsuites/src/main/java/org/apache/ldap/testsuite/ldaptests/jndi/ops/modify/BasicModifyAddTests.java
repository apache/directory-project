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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.modify;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Tests for the modify operations with JNDI, which only include replace
 * modifications.
 * 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class BasicModifyAddTests extends BaseProtocolTest
{

    DirContext start;

    DirContext ctx;

    public static final String RDN = "cn=Tori Amos";

    public static final String PERSON_DESCRIPTION = "an American singer-songwriter";

    protected Attributes getPersonAttributes(String sn, String cn)
    {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("top");
        attribute.add("person");
        attributes.put(attribute);
        attributes.put("cn", cn);
        attributes.put("sn", sn);

        return attributes;
    }

    public void setUp() throws NamingException
    {
        super.setUp();

        start = this.createContext();
        ctx = (DirContext) start.lookup(this.getTestContainerRdn());

        // Create a person with description
        Attributes attributes = this.getPersonAttributes("Amos", "Tori Amos");
        attributes.put("description", PERSON_DESCRIPTION);

        ctx.createSubcontext(RDN, attributes);
    }

    public void tearDown() throws NamingException
    {
        ctx.unbind(RDN);
        ctx.close();

        start.close();
        start = null;

        super.tearDown();
    }

    /**
     * @throws NamingException
     */
    public void testAddNewAttributeValue() throws NamingException
    {

        // Change description attribute
        String newValue = "1234567890";
        Attributes attrs = new BasicAttributes("telephoneNumber", newValue);
        ctx.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE, attrs);

        // Verify, that attribute value changed
        attrs = ctx.getAttributes(RDN);
        Attribute attr = attrs.get("telephoneNumber");
        assertNotNull(attr);
        assertTrue(attr.contains(newValue));
        assertEquals(1, attr.size());
    }

    /**
     * @throws NamingException
     */
    public void testAddNewAttributeValues() throws NamingException
    {

        // Change description attribute
        String[] newValues = { "1234567890", "999999999" };
        Attribute attr = new BasicAttribute("telephoneNumber");
        attr.add(newValues[0]);
        attr.add(newValues[1]);
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);
        ctx.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE, attrs);

        // Verify, that attribute values are present
        attrs = ctx.getAttributes(RDN);
        attr = attrs.get("telephoneNumber");
        assertNotNull(attr);
        assertTrue(attr.contains(newValues[0]));
        assertTrue(attr.contains(newValues[1]));
        assertEquals(newValues.length, attr.size());
    }

    /**
     * @throws NamingException
     */
    public void testAddAdditionalAttributeValue() throws NamingException
    {

        // A new description attribute value
        String newValue = "A new description for this person";
        assertFalse(newValue.equals(PERSON_DESCRIPTION));
        Attributes attrs = new BasicAttributes("description", newValue);

        ctx.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE, attrs);

        // Verify, that attribute value is added
        attrs = ctx.getAttributes(RDN);
        Attribute attr = attrs.get("description");
        assertNotNull(attr);
        assertTrue(attr.contains(newValue));
        assertTrue(attr.contains(PERSON_DESCRIPTION));
        assertEquals(2, attr.size());
    }

    /**
     * Try to add an already existing attribute value.
     * 
     * Expected behaviour: Modify operation fails with an
     * AttributeInUseException. Original LDAP Error code: 20 (Indicates that the
     * attribute value specified in a modify or add operation already exists as
     * a value for that attribute).
     * 
     * @throws NamingException
     */
    public void testAddExistingAttributeValue() throws NamingException
    {

        // Change description attribute
        Attributes attrs = new BasicAttributes("description", PERSON_DESCRIPTION);
        try {
            ctx.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE, attrs);
            fail("Adding an already existing atribute value should fail.");
        } catch (AttributeInUseException e) {
            // expected behaviour
        }

        // Verify, that attribute is still there, and is the only one
        attrs = ctx.getAttributes(RDN);
        Attribute attr = attrs.get("description");
        assertNotNull(attr);
        assertTrue(attr.contains(PERSON_DESCRIPTION));
        assertEquals(1, attr.size());
    }

}
