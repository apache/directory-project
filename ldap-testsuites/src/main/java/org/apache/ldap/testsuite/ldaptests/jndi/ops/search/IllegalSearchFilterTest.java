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

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.SearchControls;

import org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest;

/**
 * Tests for the search operations with JNDI which use illegal search filters.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class IllegalSearchFilterTest extends BaseProtocolTest
{

    private DirContext ctx;

    public void setUp() throws NamingException
    {
        super.setUp();

        ctx = this.createContext();
        ctx = (DirContext) ctx.lookup(this.getTestContainerRdn());
    }

    public void tearDown() throws NamingException
    {
        ctx.close();
        ctx = null;

        super.tearDown();
    }

    public void testFiltersWithSyntaxErrors() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

        String[] filters = { null, "", ",", "&", "1", "This is no filter" };

        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];

            // Search
            try {
                ctx.search("", filter, ctls);
                fail("Filter [" + filter + "] has syntax errors and should cause an error.");
            } catch (InvalidSearchFilterException e) {
                // Expected behaviour
            }
        }
    }

    public void testFilterWithUnbalancedParenthesis() throws NamingException
    {

        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

        String[] filters = { "((sn=Amos)", "(&(sn=Amos)(cn=Tori Amos)", "(!(sn=Amos)" };

        for (int i = 0; i < filters.length; i++) {
            String filter = filters[i];

            // Search
            try {
                ctx.search("", filter, ctls);
                fail("Filter [" + filter + "] has unbalanced parenthesis and should cause an error.");
            } catch (InvalidSearchFilterException e) {
                // Expected behaviour
            }
        }
    }
}