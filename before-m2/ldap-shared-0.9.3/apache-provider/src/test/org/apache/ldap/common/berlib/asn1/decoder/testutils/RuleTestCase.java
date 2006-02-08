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
package org.apache.ldap.common.berlib.asn1.decoder.testutils ;


import junit.framework.TestCase;
import org.apache.asn1.ber.digester.BERDigester;
import org.apache.asn1.codec.stateful.CallbackHistory;
import org.apache.ldap.common.berlib.asn1.decoder.LdapDigesterFactory;
import org.apache.ldap.common.message.Message;
import org.apache.ldap.common.message.MessageEncoder;
import org.apache.ldap.common.message.spi.Provider;

import java.nio.ByteBuffer;
import java.util.Properties;


/**
 * Base test case used for testing the operation of digester rules.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public abstract class RuleTestCase extends TestCase
{
    private BERDigester digester ;


    protected void tearDown() throws Exception
    {
        super.tearDown() ;
        digester = null ;
    }


    protected void setUp() throws Exception
    {
        super.setUp() ;
        digester = LdapDigesterFactory.getSingleton().create() ;
    }


    protected BERDigester getDigester()
    {
        return digester ;
    }

    protected Message decode( byte[] pdu ) throws Exception
    {
        CallbackHistory history = new CallbackHistory() ;
        getDigester().setCallback( history ) ;
        getDigester().decode( ByteBuffer.wrap( pdu ) ) ;
        return ( Message ) history.getMostRecent() ;
    }
}
