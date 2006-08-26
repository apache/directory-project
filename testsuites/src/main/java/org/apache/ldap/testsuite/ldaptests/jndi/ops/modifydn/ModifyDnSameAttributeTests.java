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

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ModifyDnSameAttributeTests extends BaseProtocolTest
{
    /**
     * Modify Rdn of an entry, delete its old rdn value.
     * 
     * @throws NamingException
     */
    public void testModifyRdnAndDeleteOld() throws NamingException
    {
        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create an organizational unit, ou value is rdn
        String oldOu = "Judean People's Front";
        String oldRdn = "ou=" + oldOu;
        Attributes attributes = AttributesFactory.createOrganizationalUnitAttributes(oldOu);
        target.createSubcontext(oldRdn, attributes);

        // modify Rdn
        String newOu = "People's Front of Judea";
        String newRdn = "ou=" + newOu;
        target.addToEnvironment("java.naming.ldap.deleteRDN", "true");
        target.rename(oldRdn, newRdn);

        // Check, whether old Entry does not exists
        try {
            target.lookup(oldRdn);
            fail("Entry must not exist");
        } catch (NameNotFoundException ignored) {
            // expected behaviour
            assertTrue(true);
        }

        // Check, whether new Entry exists
        DirContext ouEntry = (DirContext) target.lookup(newRdn);
        assertNotNull(ouEntry);

        // Check values of ou
        Attribute ou = ouEntry.getAttributes("").get("ou");
        assertTrue(ou.contains(newOu));
        assertTrue(!ou.contains(oldOu)); // old vaue is gone
        assertEquals(1, ou.size());

        ctx.close();
    }

    /**
     * Modify Rdn of an entry, keep its old rdn value.
     * 
     * @throws NamingException
     */
    public void testModifyRdnAndKeepOld() throws NamingException
    {

        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create an organizational unit, ou value is rdn
        String oldOu = "Judean People's Front";
        String oldRdn = "ou=" + oldOu;
        Attributes attributes = AttributesFactory.createOrganizationalUnitAttributes(oldOu);
        target.createSubcontext(oldRdn, attributes);

        // modify Rdn
        String newOu = "People's Front of Judea";
        String newRdn = "ou=" + newOu;
        target.addToEnvironment("java.naming.ldap.deleteRDN", "false");
        target.rename(oldRdn, newRdn);

        // Check, whether old entry does not exist
        try {
            target.lookup(oldRdn);
            fail("Entry must not exist");
        } catch (NameNotFoundException ignored) {
            // expected behaviour
            assertTrue(true);
        }

        // Check, whether new entry exists
        DirContext ouEntry = (DirContext) target.lookup(newRdn);
        assertNotNull(ouEntry);

        // Check values of cn
        Attribute ou = ouEntry.getAttributes("").get("ou");
        assertTrue(ou.contains(newOu));
        assertTrue(ou.contains(oldOu)); // old value is still present
        assertEquals(2, ou.size());

        ouEntry.close();
        target.close();
        ctx.close();
    }

    /**
     * Modify Rdn of an entry, delete its old rdn value. Here, the rdn attribute
     * cn has another value as well.
     * 
     * @throws NamingException
     */
    public void testModifyRdnAndDeleteOldVariant() throws NamingException
    {
        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create an organizational unit, ou value is rdn
        String oldOu = "Judean People's Front";
        String oldRdn = "ou=" + oldOu;
        Attributes attributes = AttributesFactory.createOrganizationalUnitAttributes(oldOu);

        // add a second ou value
        String alternateOu = "J.P.F.";
        Attribute ou = attributes.get("ou");
        ou.add(alternateOu);
        assertEquals(2, ou.size());

        target.createSubcontext(oldRdn, attributes);

        // modify Rdn
        String newOu = "People's Front of Judea";
        String newRdn = "ou=" + newOu;
        target.addToEnvironment("java.naming.ldap.deleteRDN", "true");
        target.rename(oldRdn, newRdn);

        // Check, whether old Entry does not exist anymore
        try {
            target.lookup(oldRdn);
            fail("Entry must not exist");
        } catch (NameNotFoundException ignored) {
            // expected behaviour
            assertTrue(true);
        }

        // Check, whether new Entry exists
        DirContext ouEntry = (DirContext) target.lookup(newRdn);
        assertNotNull(ouEntry);

        // Check values of cn
        ou = ouEntry.getAttributes("").get("ou");
        assertTrue(ou.contains(newOu));
        assertTrue(!ou.contains(oldOu)); // old value is gone
        assertTrue(ou.contains(alternateOu)); // alternate value is still
        assertEquals(2, ou.size());
    }
}
