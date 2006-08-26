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

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SchemaViolationException;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Basic Tests for the Modify DN operation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class BasicModifyDnTests extends BaseProtocolTest
{

    /**
     * Creates attributes for a inetOrgPerson with minimum required attributes.
     * 
     * @param sn
     *            sn value of the person (surname)
     * @param cn
     *            cn value of the person (common name)
     * @return attributes object for this person
     */
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

    /**
     * Modify DN of an entry, changing RDN from cn to uid.
     * 
     * @throws NamingException
     */
    public void testModifyRdnAndKeepOld() throws NamingException
    {
        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create a person, cn value is rdn
        String cnVal = "Tori Amos";
        String snVal = "Amos";
        String uidVal = "tamos";
        String oldRdn = "cn=" + cnVal;
        Attributes attributes = this.getInetOrgPersonAttributes(snVal, cnVal);
        attributes.put("uid", uidVal);
        target.createSubcontext(oldRdn, attributes);

        // modify Rdn
        String newRdn = "uid=" + uidVal;
        target.addToEnvironment("java.naming.ldap.deleteRDN", "false");
        target.rename(oldRdn, newRdn);

        // Check, whether old Entry does not exists
        try {
            target.lookup(oldRdn);
            fail("Entry must not exist");
        } catch (NameNotFoundException ignored) {
            // expected behaviour
        }

        // Check, whether new Entry exists
        DirContext tori = (DirContext) target.lookup(newRdn);
        assertNotNull(tori);

        // Check values of cn and uid
        Attribute cn = tori.getAttributes("").get("cn");
        assertTrue(cn.contains(cnVal));
        assertEquals(1, cn.size());
        Attribute uid = tori.getAttributes("").get("uid");
        assertTrue(uid.contains(uidVal));
        assertEquals(1, uid.size());

        ctx.close();
    }

    /**
     * Modify Rdn of an entry, changing it from cn to uid, but try delete old
     * rdn. This should cause a schema violation.
     * 
     * @throws NamingException
     */
    public void testModifyRdnAndTryToDeleteOld() throws NamingException
    {

        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create a person, cn value is rdn
        String cnVal = "Tori Amos";
        String snVal = "Amos";
        String uidVal = "tamos";
        String oldRdn = "cn=" + cnVal;
        Attributes attributes = this.getInetOrgPersonAttributes(snVal, cnVal);
        target.createSubcontext(oldRdn, attributes);

        // modify Rdn, but try delete old val
        try {
            String newRdn = "uid=" + uidVal;
            target.addToEnvironment("java.naming.ldap.deleteRDN", "true");
            target.rename(oldRdn, newRdn);
            fail("modify DN should cause a schema violation");
        } catch (SchemaViolationException sve) {
            // expected behaviour
        }

        // Check that old entry still exists
        DirContext tori = (DirContext) target.lookup(oldRdn);
        assertNotNull(tori);

        ctx.close();
    }

    /**
     * Modify Rdn of an entry, changing it from cn to uid. But an entry with
     * corresponding RDN already exists. This should cause an exception.
     * 
     * @throws NamingException
     */
    public void testModifyRdnToExistingValue() throws NamingException
    {

        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create a person, cn value is rdn
        String cnVal = "Tori Amos";
        String snVal = "Amos";
        String oldRdn = "cn=" + cnVal;
        String uidVal = "tamos";
        Attributes attributes = this.getInetOrgPersonAttributes(snVal, cnVal);
        target.createSubcontext(oldRdn, attributes);

        // create same person with sn as RDN
        String newRdn = "uid=" + uidVal;
        attributes.put("uid", uidVal);
        target.createSubcontext(newRdn, attributes);

        // modify DN, use existing value
        target.addToEnvironment("java.naming.ldap.deleteRDN", "false");
        try {
            target.rename(oldRdn, newRdn);
            fail("modify DN should cause an exception");
        } catch (NameAlreadyBoundException nbe) {
            // expected behaviour
        }

        // Check that old entry still exists
        DirContext tori = (DirContext) target.lookup(oldRdn);
        assertNotNull(tori);

        ctx.close();
    }
}
