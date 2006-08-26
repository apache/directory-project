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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.search;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NameNotFoundException;
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
 * Tests for the search operations with JNDI.
 * 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchScopeTest extends BaseProtocolTest
{

    private DirContext ctx;

    private static final String MATCH_ALL_FILTER = "(objectClass=*)";

    private static final String UNITS_NAME = "units";

    private static final String UNITS_RDN = "ou=" + UNITS_NAME;

    private static final int SUBTREE_LEVEL_DEPTH = 2;

    private static final int ENTRIES_PER_LEVEL = 3;

    protected void createSubtrees(DirContext target, int depth, int maxDepth, int count) throws NamingException
    {
        if (depth < maxDepth) {
            for (int i = 0; i < count; ++i) {
                String ou = "unit_" + depth + "_" + (i);
                Attributes attr = AttributesFactory.createOrganizationalUnitAttributes(ou);
                DirContext entry = target.createSubcontext("ou=" + ou, attr);
                createSubtrees(entry, depth + 1, maxDepth, count);
            }
        }

    }

    public void setUp() throws NamingException
    {
        super.setUp();

        ctx = this.createContext();
        ctx = (DirContext) ctx.lookup(this.getTestContainerRdn());

        DirContext units = ctx.createSubcontext(UNITS_RDN, AttributesFactory.createOrganizationalUnitAttributes(UNITS_NAME));
        createSubtrees(units, 0, SUBTREE_LEVEL_DEPTH, ENTRIES_PER_LEVEL);

    }

    public void tearDown() throws NamingException
    {
        ctx.close();
        ctx = null;

        super.tearDown();
    }

    public void testBaseScopeExisting() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.OBJECT_SCOPE);
        ctls.setReturningAttributes(new String[] { "ou" });

        // Search
        NamingEnumeration enm = ctx.search(UNITS_RDN, MATCH_ALL_FILTER, ctls);

        // Exactly one entry in result
        assertTrue(enm.hasMore());
        SearchResult sr = (SearchResult) enm.next();
        assertFalse(enm.hasMore());

        // The right entry in result
        Attributes attrs = sr.getAttributes();
        Attribute ou = attrs.get("ou");
        assertTrue(ou.contains(UNITS_NAME));
    }

    public void testBaseScopeNotExistingBase() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.OBJECT_SCOPE);
        ctls.setReturningAttributes(new String[] { "ou" });

        try {
            ctx.search(UNITS_RDN + "_", MATCH_ALL_FILTER, ctls);
            fail("Search on a not existing base should fail");
        } catch (NameNotFoundException nnfe) {
            // expected behaviour
        }
    }

    public void testOneLevelScope() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        ctls.setReturningAttributes(new String[] { "ou" });

        // Search op
        //
        NamingEnumeration enm = ctx.search(UNITS_RDN, MATCH_ALL_FILTER, ctls);

        // Analyse result data
        assertTrue(enm.hasMore());
        Set entries = new HashSet();
        int count = 0;
        while (enm.hasMore()) {
            count++;
            SearchResult sr = (SearchResult) enm.next();
            Attribute ou = sr.getAttributes().get("ou");
            entries.add(ou.get());
        }
        assertEquals("number of entries in result", ENTRIES_PER_LEVEL, count);
        assertEquals("number of different entries in result", ENTRIES_PER_LEVEL, entries.size());
        assertFalse("base included in result", entries.contains(UNITS_NAME));
    }
}
