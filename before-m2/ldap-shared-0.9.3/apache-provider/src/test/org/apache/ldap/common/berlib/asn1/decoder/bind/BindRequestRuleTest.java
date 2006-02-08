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
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.BERDigester;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.BindRequest;


/**
 * TestCase used to unit test the BindRequestRule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BindRequestRuleTest extends TestCase
{
    /** rule used to create a bind request */
    BindRequestRule bindRequestRule = null ;
    BERDigester digester = new BERDigester() ;


    protected void setUp() throws Exception
    {
        super.setUp() ;
        bindRequestRule = new BindRequestRule() ;
        bindRequestRule.setDigester( digester ) ;
    }


    protected void tearDown() throws Exception
    {
        super.tearDown() ;
        bindRequestRule.setDigester( null ) ;
        bindRequestRule = null ;
    }


    public void testTag0() throws Exception
    {
        digester.pushInt( 77 ) ;
        bindRequestRule.tag( 0, true, TypeClass.APPLICATION ) ;
        assertEquals( 1, digester.getCount() ) ;
        BindRequest req = ( BindRequest ) digester.peek() ;
        assertEquals( 77, req.getMessageId() ) ;
    }


    public void testTag1() throws Exception
    {
        try
        {
            bindRequestRule.tag( 2, true, TypeClass.APPLICATION ) ;
            fail( "should have caused an exception above to never get here" ) ;
        }
        catch ( IllegalArgumentException e )
        {
        }

        assertEquals( 0, digester.getCount() ) ;
    }


    public void testFinish() throws Exception
    {
        testTag0() ;
        bindRequestRule.finish() ;
        assertEquals( 0, digester.getCount() ) ;
        BindRequest req = ( BindRequest ) digester.getRoot() ;
        assertEquals( 77, req.getMessageId() ) ;
    }


    public void testGetTag() throws Exception
    {
        assertEquals( LdapTag.BIND_REQUEST, BindRequestRule.getTag() ) ;
    }


    public void testGetNestingPattern() throws Exception
    {
        int[] pattern = BindRequestRule.getNestingPattern() ;
        assertNotNull( pattern ) ;
        assertEquals( 2, pattern.length ) ;
        assertEquals( UniversalTag.SEQUENCE_SEQUENCE_OF.getValue(),
                pattern[0] ) ;
        assertEquals( LdapTag.BIND_REQUEST.getValue(),
                pattern[1] ) ;
    }
}
