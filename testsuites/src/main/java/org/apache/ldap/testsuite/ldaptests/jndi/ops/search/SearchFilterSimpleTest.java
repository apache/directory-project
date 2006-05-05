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
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;
import org.apache.ldap.testsuite.ldaptests.jndi.util.AttributesFactory;

/**
 * Tests for the search operations with JNDI containing simple filter
 * expressions. All boolean operators are covered.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchFilterSimpleTest extends BaseProtocolTest
{

    private DirContext ctx;

    static final String[] firstNames = { "Tori", "Heather", "Alanis", "Kate", "Fiona" };

    static final String[] lastNames = { "Amos", "Nova", "Morisette", "Bush", "Apple" };

    public void setUp() throws NamingException
    {
        super.setUp();

        ctx = this.createContext();
        ctx = (DirContext) ctx.lookup(this.getTestContainerRdn());

        // Create person entries
        for (int i = 0; i < lastNames.length; ++i) {
            String cn = firstNames[i] + " " + lastNames[i];
            Attributes person = AttributesFactory.createPersonAttributes(cn, lastNames[i]);
            ctx.createSubcontext("cn=" + cn, person);
        }

        // Create an organization Unit
        String ou = "anOrgUnit";
        Attributes orgUnit = AttributesFactory.createOrganizationalUnitAttributes(ou);
        ctx.createSubcontext("ou=" + ou, orgUnit);
    }

    public void tearDown() throws NamingException
    {
        ctx.close();
        ctx = null;

        super.tearDown();
    }

    public void testSimpleFilter() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

        NamingEnumeration enm = ctx.search("", "(sn=Amos)", ctls);
        assertTrue(enm.hasMore());
        int numberOfEntries = 0;
        while (enm.hasMore()) {
            SearchResult entry = (SearchResult) enm.next();
            Attributes attrs = entry.getAttributes();
            Attribute sn = attrs.get("sn");

            assertEquals(1, sn.size());
            assertTrue(sn.contains("Amos"));

            numberOfEntries++;
        }

        assertEquals("number of entries in result", 1, numberOfEntries);
        enm.close();
    }

    /**
     * Test with a search filter which contains an NOT operator.
     */
    public void testFilterWithNot() throws NamingException
    {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

        // Filter "(!(objectClass=person))"
        //
        NamingEnumeration enm = ctx.search("", "(!(objectClass=person))", ctls);
        assertTrue(enm.hasMore());
        int numberOfEntries = 0;
        while (enm.hasMore()) {
            SearchResult entry = (SearchResult) enm.next();
            Attributes attrs = entry.getAttributes();
            Attribute ocls = attrs.get("objectClass");

            assertFalse(ocls.contains("person"));

            numberOfEntries++;
        }
        // Check whether number of entries foud is 1 (the org unit)
        assertEquals("number of entries in result", 1, numberOfEntries);
        enm.close();
        
        // Filter "(!(objectClass=*))"
        //
        enm = ctx.search("", "(!(objectClass=*))", ctls);
        assertFalse("entries found", enm.hasMore());
        enm.close();
    }

    /**
     * Test with search filters which contain an AND operator.
     */
    public void testFilterWithAnd() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // Filter "(&(objectClass=person)(sn=Amos))"
        //
        NamingEnumeration enm = ctx.search("", "(&(objectClass=person)(sn=Amos))", ctls);
        assertTrue(enm.hasMore());
        int numberOfEntries = 0;
        while (enm.hasMore()) {
            SearchResult entry = (SearchResult) enm.next();
            Attributes attrs = entry.getAttributes();
            Attribute sn = attrs.get("sn");
            assertTrue(sn.contains("Amos"));
            numberOfEntries++;
        }
        assertEquals("number of entries in result", 1, numberOfEntries);
        enm.close();
        
        // Filter "(&(sn=Amos)(sn=Bush))"
        //
        enm = ctx.search("", "(&(sn=Amos)(sn=Bush))", ctls);
        assertFalse("entries found", enm.hasMore());
        enm.close();
    }

    /**
     * Test with a search filter which contains an OR operator.
     */
    public void testFilterWithOr() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration enm = ctx.search("", "(|(cn=Kate Bush)(sn=Amos))", ctls);
        assertTrue(enm.hasMore());
        int numberOfEntries = 0;
        while (enm.hasMore()) {
            SearchResult entry = (SearchResult) enm.next();
            Attributes attrs = entry.getAttributes();
            Attribute sn = attrs.get("sn");
            assertTrue(sn.contains("Amos") || sn.contains("Bush"));
            numberOfEntries++;
        }
        assertEquals("number of entries in result", 2, numberOfEntries);
        enm.close();
    }
}
