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
package org.apache.ldap.testsuite.ldaptests.jndi;

import javax.naming.Binding;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import junit.framework.TestCase;

import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class BaseProtocolTest extends TestCase
{

    public void setUp() throws NamingException
    {

        // Check whether test container entry exists
        // If yes: delete it
        DirContext ctx = this.createContext();
        try {
            LdapContext entry = (LdapContext) ctx.lookup(getTestContainerRdn());
            this.deleteSubtree(entry);
        } catch (NameNotFoundException ignored) {
        }

        // Create an entry
        Attributes attributes = AttributesFactory.createOrganizationalUnitAttributes("playground");

        DirContext pg = ctx.createSubcontext(getTestContainerRdn(), attributes);
        assertNotNull(pg);

        pg = (DirContext) ctx.lookup(getTestContainerRdn());
        assertNotNull(pg);

        ctx.close();
        ctx = null;
    }

    public void tearDown() throws NamingException
    {
        LdapContext ctx = this.createContext();

        LdapContext pg = (LdapContext) ctx.lookup(getTestContainerRdn());
        this.deleteSubtree(pg);

        try {
            ctx.lookup(getTestContainerRdn());
            fail("test entry not deleted");
        } catch (NameNotFoundException ignored) {
        }

        ctx.close();
        ctx = null;
    }

    private void deleteSubtree(LdapContext entry) throws NamingException
    {
        NamingEnumeration enumeration = entry.listBindings("");
        while (enumeration.hasMore()) {
            Binding b = (Binding) enumeration.next();
            if (b.getObject() instanceof LdapContext) {
                deleteSubtree((LdapContext) b.getObject());
            }
        }
        entry.unbind("");
    }

    protected LdapContext createContext() throws NamingException
    {
        return new InitialLdapContext();
    }

    protected String getTestContainerRdn()
    {
        return "ou=playground";
    }
}
