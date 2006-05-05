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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.delete;

import javax.naming.ContextNotEmptyException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BasicDeleteTests extends BaseProtocolTest
{
    /**
     * Basic deletion test. Creates an entry, delete it, and check whether it is
     * gone.
     * 
     * @throws NamingException
     */
    public void testDeleteEntry() throws NamingException
    {

        final String ou = "myUnit";
        final String rdn = "ou=myUnit";

        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Add an entry
        Attributes attributes = AttributesFactory.createOrganizationalUnitAttributes(ou);
        DirContext orgUnit = target.createSubcontext(rdn, attributes);

        // Check wether entry is created
        orgUnit = (DirContext) target.lookup(rdn);
        assertNotNull(orgUnit);

        // Delete Entry
        target.unbind(rdn);

        // Check whether it is gone
        try {
            orgUnit = (DirContext) target.lookup(rdn);
            fail("Deletion of entry failed");
        } catch (NameNotFoundException nnfe) {
            // expected behaviour
            assertTrue(true);
        }

        ctx.close();
    }

    /**
     * Deletion of an entry which is not a leaf.
     * 
     * @throws NamingException
     */
    public void testDeleteNonEmptyEntry() throws NamingException
    {
        DirContext ctx = this.createContext();
        DirContext target = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Add an entry
        final String ou = "nonLeaf";
        final String rdn = "ou=" + ou;
        Attributes attributes = AttributesFactory.createOrganizationalUnitAttributes(ou);
        target.createSubcontext(rdn, attributes);

        // Check wether entry is created
        DirContext nonLeafRdn = (DirContext) target.lookup(rdn);
        assertNotNull(nonLeafRdn);

        // Add an entry below this
        final String ouLeaf = "leaf";
        final String rdnLeaf = "ou=" + ouLeaf;
        attributes = AttributesFactory.createOrganizationalUnitAttributes(ouLeaf);
        nonLeafRdn.createSubcontext(rdnLeaf, attributes);

        // Try to delete subtree Entry
        try {
            target.unbind(rdn);
            fail("deletion of entry should fail.");
        } catch (ContextNotEmptyException e) {
            // expected;
        }

        // Verify that entry soes exist
        nonLeafRdn = (DirContext) target.lookup(rdn);
        assertNotNull(nonLeafRdn);

        ctx.close();
    }
}
