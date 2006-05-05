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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.modify;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SchemaViolationException;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Tests with several modification items within one modify operation, which
 * cause an error. Goal is to chcek whether the modify operation is atomic.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class MixedModifyFailureTests extends BaseProtocolTest
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
     * Try to add a new attribute (allowed) and to replace the RDN attribute
     * (forbidden).
     * 
     * @throws NamingException
     */
    public void testAddAndIllegalReplace() throws NamingException
    {

        // Add telephoneNumber attribute
        String newValue = "1234567890";
        Attribute telephoneNumber = new BasicAttribute("telephoneNumber", newValue);
        ModificationItem modAdd = new ModificationItem(DirContext.ADD_ATTRIBUTE, telephoneNumber);

        // Try to replace a desciption value, which does not exist
        Attribute cn = new BasicAttribute("cn", "XXX");
        ModificationItem modReplace = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, cn);

        // execute modify: 1. add, 2. replace
        ModificationItem[] mods = new ModificationItem[] { modAdd, modReplace };
        try {
            ctx.modifyAttributes(RDN, mods);
            fail("mofify operation should fail.");
        } catch (SchemaViolationException e) {
            // Expected behaviour
        } catch (InvalidNameException e) {
            // Expected behaviour
        }

        // Verify, that no telephone attribute was created, and cn has bot been
        // changed
        Attributes attrs = ctx.getAttributes(RDN);
        assertNull(attrs.get("telephoneNumber"));
        cn = attrs.get("cn");
        assertNotNull(cn);
        assertTrue(cn.contains("Tori Amos"));

        // Mix the sequence of modification items: 1. replace, 2. add
        mods = new ModificationItem[] { modReplace, modAdd };
        try {
            ctx.modifyAttributes(RDN, mods);
            fail("modify operation should fail.");
        } catch (SchemaViolationException e) {
            // Expected behaviour
        } catch (InvalidNameException e) {
            // Expected behaviour
        }

        // Verify again, that no telephone attribute was created, and cn has bot
        // been changed
        attrs = ctx.getAttributes(RDN);
        assertNull(attrs.get("telephoneNumber"));
        cn = attrs.get("cn");
        assertNotNull(cn);
        assertTrue(cn.contains("Tori Amos"));
    }

    /**
     * Try to remove an attribute (allowed) and add an attribute value twice
     * (forbidden) within one modify op. Expected behaviour is that the modify
     * op fails and the entry is not changed.
     * 
     * @throws NamingException
     */
    public void testRemoveAndDuplicateAdd() throws NamingException
    {

        // Remove description attribute
        Attribute description = new BasicAttribute("description");
        ModificationItem modRemove = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, description);

        // Try to add a new desciption value
        Attribute telephoneNumber = new BasicAttribute("telephoneNumber", "01234567890");
        ModificationItem modAdd = new ModificationItem(DirContext.ADD_ATTRIBUTE, telephoneNumber);

        // execute modify: 1. remove, 2. add, 3. same add
        ModificationItem[] mods = new ModificationItem[] { modRemove, modAdd, modAdd };
        try {
            ctx.modifyAttributes(RDN, mods);
            fail("modify operation should fail.");
        } catch (AttributeInUseException e) {
            // Expected behaviour
        }

        // Verify, that description has not been removed, and telephoneNumber
        // has not been added
        Attributes attrs = ctx.getAttributes(RDN);
        System.err.println(attrs);
        description = attrs.get("description");
        assertNotNull(description);
        assertTrue(description.contains(PERSON_DESCRIPTION));
        assertNull(attrs.get("telephoneNumber"));
    }
}
