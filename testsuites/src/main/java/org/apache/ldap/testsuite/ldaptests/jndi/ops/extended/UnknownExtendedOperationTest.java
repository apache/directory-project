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
package org.apache.ldap.testsuite.ldaptests.jndi.ops.extended;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;

/**
 * Checks the behaviour of the server for an unknown extended operation. Because
 * extended operations are server dependent, this is the only test case for them
 * within the suite.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class UnknownExtendedOperationTest extends org.apache.ldap.testsuite.ldaptests.jndi.BaseProtocolTest
{

    /**
     * Checks the behaviour of the server for an unknown extended operation. It
     * calls an extended exception, which does not exist. Expected behaviour is
     * a CommunicationException.
     * 
     * @throws NamingException
     */
    public void testUnknownExtendedOperation() throws NamingException
    {
        LdapContext ctx = this.createContext();

        try {
            ctx.extendedOperation(new UnknownExtendedOperationRequest());
            fail("Calling an unknown extended operation should fail.");
        } catch (CommunicationException ce) {
            // expected behaviour
        }
    }

    /**
     * Class for the request of an extended operation which does not exist. This
     * is forced by using OID "1.1".
     */
    private class UnknownExtendedOperationRequest implements ExtendedRequest
    {

        private static final long serialVersionUID = 1L;

        public static final String OID = "1.1";

        /**
         * Returns the OID of the extended operation.
         * 
         * @return the OID
         */
        public String getID()
        {
            return OID;
        }

        public byte[] getEncodedValue()
        {
            return null;
        }

        public ExtendedResponse createExtendedResponse(String id, byte[] berValue, int offset, int length)
                throws NamingException
        {
            return null;
        }
    }
}
