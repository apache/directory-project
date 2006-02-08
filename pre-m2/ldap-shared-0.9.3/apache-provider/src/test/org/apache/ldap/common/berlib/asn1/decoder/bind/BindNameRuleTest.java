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
package org.apache.ldap.common.berlib.asn1.decoder.bind ;


import junit.framework.TestCase;
import org.apache.asn1.ber.digester.BERDigester;
import org.apache.ldap.common.message.BindRequest;
import org.apache.ldap.common.message.BindRequestImpl;

import java.nio.ByteBuffer;


/**
 * Tests the BindNameRule to make sure it behaves as expected.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BindNameRuleTest extends TestCase
{
    BERDigester digester ;
    BindNameRule bindNameRule ;


    /**
     * Resets the test instances.
     *
     * @throws Exception on super call
     */
    protected void setUp() throws Exception
    {
        super.setUp() ;
        digester = new BERDigester() ;
        bindNameRule = new BindNameRule() ;
        bindNameRule.setDigester( digester ) ;
    }


    /**
     * Clears (nulls out) the test instances.
     *
     * @throws Exception on super call
     */
    protected void tearDown() throws Exception
    {
        super.tearDown() ;
        digester = null ;
        bindNameRule.setDigester( null ) ;
        bindNameRule = null ;
    }


    /**
     * Tests the finish() method of this Rule.
     */
    public void testFinish()
    {
        digester.push( new BindRequestImpl( 77 ) ) ;
        ByteBuffer buf = ByteBuffer.wrap( "uid=akarasulu".getBytes() ) ;
        bindNameRule.value( buf ) ;
        bindNameRule.finish() ;
        BindRequest req = ( BindRequest ) digester.pop() ;
        assertEquals( "uid=akarasulu", req.getName() ) ;
    }
}
