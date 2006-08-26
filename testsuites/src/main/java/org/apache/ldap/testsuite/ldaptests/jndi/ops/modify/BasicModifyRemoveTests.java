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

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.NoSuchAttributeException;
import javax.naming.directory.SchemaViolationException;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Tests for the modify operations with JNDI, which only include simple remove
 * modifications.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class BasicModifyRemoveTests extends BaseProtocolTest
{

    DirContext ctx;

    DirContext target;

    public static final String RDN = "cn=Tori Amos";

    protected Attributes getInetOrgPersonAttributes(String sn, String cn)
    {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("top");
        attribute.add("person");
        attribute.add("organizationalPerson");
        attribute.add("inetOrgPerson");

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
        Attributes attributes = this.getInetOrgPersonAttributes("Amos", "Tori Amos");
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
     * Remove an attribute, which is not required.
     * 
     * Expected result: After successful deletion, attribute is not present in
     * entry.
     * 
     * @throws NamingException
     */
    public void testRemoveNotRequiredAttribute() throws NamingException
    {

        // Remove description Attribute
        Attribute attr = new BasicAttribute("description");
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);
        target.modifyAttributes(RDN, DirContext.REMOVE_ATTRIBUTE, attrs);

        // Verify, that attribute is deleted
        attrs = target.getAttributes(RDN);
        attr = attrs.get("description");
        assertNull(attr);
    }

    /**
     * Remove two not required attributes.
     * 
     * Expected result: After successful deletion, both attributes ar not
     * present in entry.
     * 
     * @throws NamingException
     */
    public void testRemoveTwoNotRequiredAttributes() throws NamingException
    {

        // add telephoneNumber to entry
        Attributes tn = new BasicAttributes("telephoneNumber", "12345678");
        target.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE, tn);

        // Remove description and telephoneNumber to Attribute
        Attributes attrs = new BasicAttributes();
        attrs.put(new BasicAttribute("description"));
        attrs.put(new BasicAttribute("telephoneNumber"));
        target.modifyAttributes(RDN, DirContext.REMOVE_ATTRIBUTE, attrs);

        // Verify, that attributes are deleted
        attrs = target.getAttributes(RDN);
        assertNull(attrs.get("description"));
        assertNull(attrs.get("telephoneNumber"));
        assertNotNull(attrs.get("cn"));
        assertNotNull(attrs.get("sn"));
    }

    /**
     * Remove a required attribute.
     * 
     * Expected Result: Deletion fails with NamingException (Schema Violation).
     * 
     * @throws NamingException
     */
    public void testRemoveRequiredAttribute() throws NamingException
    {

        // Remove sn attribute
        Attribute attr = new BasicAttribute("sn");
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);

        try {
            target.modifyAttributes(RDN, DirContext.REMOVE_ATTRIBUTE, attrs);
            fail("Deletion of required attribute should fail.");
        } catch (SchemaViolationException e) {
            // expected behaviour
        }
    }

    /**
     * Remove a required attribute from RDN.
     * 
     * Expected Result: Deletion fails with SchemaViolationException.
     * 
     * @throws NamingException
     */
    public void testRemovePartOfRdn() throws NamingException
    {

        // Remove sn attribute
        Attribute attr = new BasicAttribute("cn");
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);

        try {
            target.modifyAttributes(RDN, DirContext.REMOVE_ATTRIBUTE, attrs);
            fail("Deletion of RDN attribute should fail.");
        } catch (NamingException e) {
            // expected behaviour
        }
    }

    /**
     * Remove a not required attribute from RDN.
     * 
     * Expected Result: Deletion fails with SchemaViolationException.
     * 
     * @throws NamingException
     */
    public void testRemovePartOfRdnNotRequired() throws NamingException
    {

        // add uid attribute
        Attributes uid = new BasicAttributes("uid", "tamos");
        target.modifyAttributes(RDN, DirContext.ADD_ATTRIBUTE, uid);

        // Change RDN to another attribute (uid)
        String newRdn = "uid=tamos";
        target.addToEnvironment("java.naming.ldap.deleteRDN", "false");
        target.rename(RDN, newRdn);

        // Remove uid, which is now RDN attribute
        Attribute attr = new BasicAttribute("uid");
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);

        try {
            target.modifyAttributes(newRdn, DirContext.REMOVE_ATTRIBUTE, attrs);
            fail("Deletion of RDN attribute should fail.");
        } catch (SchemaViolationException e) {
            // expected behaviour
        } catch (InvalidNameException e) {
            // Expected behaviour
        }

        // Change RDN back to original
        target.addToEnvironment("java.naming.ldap.deleteRDN", "false");
        target.rename(newRdn, RDN);
    }

    /**
     * Remove a an attribute which is not present on the entry, but in the
     * schema.
     * 
     * Expected result: Deletion fails with NoSuchAttributeException
     * 
     * @throws NamingException
     */
    public void testRemoveAttributeNotPresent() throws NamingException
    {

        // Remove telephoneNumber Attribute
        Attribute attr = new BasicAttribute("telephoneNumber");
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);

        try {
            target.modifyAttributes(RDN, DirContext.REMOVE_ATTRIBUTE, attrs);
            fail("Deletion of attribute, which is not present in the entry, should fail.");
        } catch (NoSuchAttributeException e) {
            // expected behaviour
        }
    }

    /**
     * Remove a an attribute which is not present in the schema.
     * 
     * Expected result: Deletion fails with NoSuchAttributeException
     * 
     * @throws NamingException
     */
    public void testRemoveAttributeNotValid() throws NamingException
    {

        // Remove phantasy attribute
        Attribute attr = new BasicAttribute("XXX");
        Attributes attrs = new BasicAttributes();
        attrs.put(attr);

        try {
            target.modifyAttributes(RDN, DirContext.REMOVE_ATTRIBUTE, attrs);
            fail("Deletion of an invalid attribute should fail.");
        } catch (NoSuchAttributeException e) {
            // expected behaviour
        } catch (InvalidAttributeIdentifierException e) {
            // expected behaviour
        }
    }
}
