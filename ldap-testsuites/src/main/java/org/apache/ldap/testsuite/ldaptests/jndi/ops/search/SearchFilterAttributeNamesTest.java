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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.search;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Tests for the search operations with concentrating on attribute names.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchFilterAttributeNamesTest extends BaseProtocolTest
{

    private DirContext ctx;

    static final String[] firstNames = { "Tori", "Heather", "Alanis", "Kate", "Fiona" };

    static final String[] lastNames = { "Amos", "Nova", "Morisette", "Bush", "Apple" };

    protected Attributes getPersonAttributes(Person p)
    {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("top");
        attribute.add("person");
        attributes.put(attribute);
        attributes.put("sn", p.sn);
        attributes.put("cn", p.cn);

        return attributes;
    }

    public void setUp() throws NamingException
    {
        super.setUp();

        ctx = this.createContext();
        ctx = (DirContext) ctx.lookup(this.getTestContainerRdn());

        for (int i = 0; i < lastNames.length; ++i) {
            Person p = new Person(firstNames[i], lastNames[i]);
            ctx.createSubcontext("cn=" + p.cn, getPersonAttributes(p));
        }
    }

    public void tearDown() throws NamingException
    {
        ctx.close();
        ctx = null;

        super.tearDown();
    }

    /**
     * Tests presence filter for the objectClass attribute.
     */
    public void testObjectclassPresence() throws NamingException
    {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

        String[] filters = { "(objectClass=*)", "(objectclass=*)", "(OBJECTCLASS=*)", "(Objectclass=*)" };

        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];

            NamingEnumeration enm = ctx.search("", filter, ctls);
            assertTrue("Entries found with filter " + filter, enm.hasMore());
            int numberOfEntries = 0;
            while (enm.hasMore()) {
                SearchResult entry = (SearchResult) enm.next();
                Attributes attrs = entry.getAttributes();
                Attribute sn = attrs.get("sn");
                assertEquals(1, sn.size());

                numberOfEntries++;
            }

            assertEquals("number of entries in result with filter " + filter, lastNames.length, numberOfEntries);
        }
    }

    /**
     * Tests equals filter for the objectClass attribute.
     */
    public void testObjectclassEquals() throws NamingException
    {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String[] filters = { "(objectClass=person)", "(objectclass=person)", "(OBJECTCLASS=person)",
                "(Objectclass=person)" };

        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];

            NamingEnumeration enm = ctx.search("", filter, ctls);
            assertTrue("Entries found with filter " + filter, enm.hasMore());
            int numberOfEntries = 0;
            while (enm.hasMore()) {
                SearchResult entry = (SearchResult) enm.next();
                Attributes attrs = entry.getAttributes();
                Attribute sn = attrs.get("sn");
                assertEquals(1, sn.size());

                numberOfEntries++;
            }

            assertEquals("number of entries in result with filter " + filter, lastNames.length, numberOfEntries);
        }
    }

    private class Person
    {
        Person(String firstName, String lastName) {
            this.sn = lastName;
            this.cn = firstName + " " + lastName;
        }

        String sn;
        String cn;
    }
}