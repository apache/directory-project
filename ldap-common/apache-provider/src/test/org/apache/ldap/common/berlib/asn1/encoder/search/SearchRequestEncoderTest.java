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
package org.apache.ldap.common.berlib.asn1.encoder.search;


import org.apache.asn1.ber.DefaultMutableTupleNode;
import org.apache.asn1.ber.TupleNode;
import org.apache.ldap.common.berlib.asn1.encoder.AbstractEncoderTestCase;
import org.apache.ldap.common.berlib.asn1.decoder.testutils.TestUtils;
import org.apache.ldap.common.filter.FilterParserImpl;
import org.apache.ldap.common.message.DerefAliasesEnum;
import org.apache.ldap.common.message.ScopeEnum;
import org.apache.ldap.common.message.SearchRequestImpl;


/**
 * TestCase for the SearchRequestEncoder class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SearchRequestEncoderTest extends AbstractEncoderTestCase
{
    /**
     * Tests the encode method.
     */
    public void testEncode() throws Exception
    {
        FilterParserImpl parser = new FilterParserImpl();
        SearchRequestImpl request = new SearchRequestImpl( 33 );
        request.setBase( "dc=apache,dc=org" );
        request.setDerefAliases( DerefAliasesEnum.DEREFINSEARCHING );
        request.setFilter( parser.parse(
                    "(& (ou=Engineering) (l=Sunnyvale) )" ) );
        request.setScope( ScopeEnum.SINGLELEVEL );
        request.setSizeLimit( 12 );
        request.setTimeLimit( 300 );
        request.setTypesOnly( true );

        byte[] expected = new byte[] {0x30, 0x4E, 0x02, 0x01, 0x21, 0x63, 0x49, 0x04, 0x10, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67, 0x0A, 0x01, 0x01, 0x0A, 0x01, 0x01, 0x02, 0x01, 0x0C, 0x02, 0x02, 0x01, 0x2C, 0x01, 0x01, 0xFFFFFFFF, 0xFFFFFFA0, 0x23, 0xFFFFFFA3, 0x11, 0x04, 0x02, 0x6F, 0x75, 0x04, 0x0B, 0x45, 0x6E, 0x67, 0x69, 0x6E, 0x65, 0x65, 0x72, 0x69, 0x6E, 0x67, 0xFFFFFFA3, 0x0E, 0x04, 0x01, 0x6C, 0x04, 0x09, 0x53, 0x75, 0x6E, 0x6E, 0x79, 0x76, 0x61, 0x6C, 0x65, 0x30, 0x00};

        // Encode stub into tuple tree then into the accumulator
        TupleNode node = SearchRequestEncoder.INSTANCE.encode( request );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
    
    /**
     * Tests the encode method.
     */
    public void testEncodePresenceFilter() throws Exception
    {
        FilterParserImpl parser = new FilterParserImpl();
        SearchRequestImpl request = new SearchRequestImpl( 33 );
        request.setBase( "dc=apache,dc=org" );
        request.setDerefAliases( DerefAliasesEnum.DEREFINSEARCHING );
        request.setFilter( parser.parse(
                    "(objectClass=*)" ) );
        request.setScope( ScopeEnum.SINGLELEVEL );
        request.setSizeLimit( 12 );
        request.setTimeLimit( 300 );
        request.setTypesOnly( true );
    
        byte[] expected = new byte[] {0x30, 0x36, 0x02, 0x01, 0x21, 0x63, 0x31, 0x04, 0x10, 0x64, 0x63, 0x3D, 0x61, 0x70, 0x61, 0x63, 0x68, 0x65, 0x2C, 0x64, 0x63, 0x3D, 0x6F, 0x72, 0x67, 0x0A, 0x01, 0x01, 0x0A, 0x01, 0x01, 0x02, 0x01, 0x0C, 0x02, 0x02, 0x01, 0x2C, 0x01, 0x01, 0xFFFFFFFF, 0xFFFFFF87, 0x0B, 0x6F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x43, 0x6C, 0x61, 0x73, 0x73, 0x30, 0x00};
    
        // Encode stub into tuple tree then into the accumulator
        TupleNode node = SearchRequestEncoder.INSTANCE.encode( request );
        encode( ( DefaultMutableTupleNode ) node );
        TestUtils.assertEquals( expected, getEncoded() );
    }
    
}
