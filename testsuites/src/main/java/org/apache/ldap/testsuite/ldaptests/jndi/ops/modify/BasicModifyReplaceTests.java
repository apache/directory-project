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

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Tests for the modify operations with JNDI, which only include replace
 * modifications.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class BasicModifyReplaceTests extends BaseProtocolTest
{

    DirContext ctx;

    DirContext target;

    public static final String RDN = "cn=Tori Amos";

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

        ctx = this.createContext();
        target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create a person with description
        Attributes attributes = this.getPersonAttributes("Amos", "Tori Amos");
        attributes.put("description", "an American singer-songwriter");

        target.createSubcontext(RDN, attributes);
    }

    public void tearDown() throws NamingException
    {
        target.unbind(RDN);
        target.close();

        ctx.close();
        ctx = null;

        super.tearDown();
    }

    /**
     * Replace a not required attribute.
     * 
     * Expected result: After successful deletion, attribute is not present in
     * entry
     * 
     * @throws NamingException
     */
    public void testReplaceNotRequiredAttribute() throws NamingException
    {

        // Change description attribute
        String newValue = "A new description for this person";
        Attribute attr = new BasicAttribute("description", newValue);
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);
        target.modifyAttributes(RDN, DirContext.REPLACE_ATTRIBUTE, attrs);

        // Verify, that attribute value changed
        attrs = target.getAttributes(RDN);
        attr = attrs.get("description");
        assertTrue(attr.contains(newValue));
        assertEquals(1, attr.size());

    }

    /**
     * Try to Replace RDN.
     * 
     * @throws NamingException
     */
    public void testTryToReplaceRdn() throws NamingException
    {

        // Change RDN attribute cn
        String newValue = "New Value";
        Attribute attr = new BasicAttribute("cn", newValue);
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);
        try {
            target.modifyAttributes(RDN, DirContext.REPLACE_ATTRIBUTE, attrs);
        } catch (NamingException e) {
            // expected behaviour
            // TODO: refine Exception
        }

        // Verify, that attribute value has not changed
        attrs = target.getAttributes(RDN);
        attr = attrs.get("cn");
        assertEquals(1, attr.size());
        assertFalse(attr.contains(newValue));
    }
}