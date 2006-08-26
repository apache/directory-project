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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.delete;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class TreeDeleteControlTests extends BaseProtocolTest
{
    class TreeDeleteControl implements Control
    {
        private static final long serialVersionUID = 1L;

        private boolean critical;

        TreeDeleteControl() {
            this(Control.CRITICAL);
        }

        TreeDeleteControl(boolean criticalValue) {
            this.critical = criticalValue;
        }

        public String getID()
        {
            return "1.2.840.113556.1.4.805";
        }

        public boolean isCritical()
        {
            return this.critical;
        }

        public byte[] getEncodedValue()
        {
            return null;
        }
    }

    public void testDeleteSubtreeWithControl() throws NamingException
    {
        LdapContext ctx = this.createContext();
        LdapContext target = (LdapContext) ctx.lookup(this.getTestContainerRdn());

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
        boolean supported = true;
        try {
            target.setRequestControls(new Control[] { new TreeDeleteControl(Control.CRITICAL) });
            target.unbind(rdn);
        } catch (OperationNotSupportedException e) {
            // expected;
            supported = false;
        }
        target.setRequestControls(null);

        // Check, whether entry is deleted
        boolean deleted = false;
        try {
            target.lookup(rdn);
        } catch (NameNotFoundException e) {
            deleted = true;
        }

        // Two option are valid here:
        // 1. The control is not supported and the entry still exists
        // 2. The control is supported and te entry is deleted
        assertTrue((!supported & !deleted) || (supported & deleted));

        ctx.close();
    }
}
