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


import junit.framework.TestCase;
import org.apache.ldap.common.message.*;
import org.apache.ldap.common.filter.FilterParserImpl;
import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.filter.BranchNormalizedVisitor;
import org.apache.asn1.codec.stateful.CallbackHistory;

import java.io.IOException;
import java.text.ParseException;
import java.nio.ByteBuffer;


/**
 * Tests for a bug where the filter expression is being rearranged by the decoder.
 *
 * @see <a href="http://issues.apache.org/jira/browse/DIRLDAP-38">DIRLDAP-38</a>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RoundTripFilterTest extends TestCase
{
    /**
     * According to the JIRA issue the following expression is being rearranged:
     *
     * <pre>
     *  (& (a=A) (| (b=B) (c=C) ) )
     *
     *  The resulting expression object after the BER parser is finished is:
     *
     *  (& (| (c=C) (b=B) (a=A) ) )
     * </pre>
     *
     */
    public void testRearrangingExpression() throws IOException, ParseException
    {
        SearchRequestImpl req = new SearchRequestImpl( 1 );

        req.setBase( "ou=system" );

        FilterParserImpl parser = new FilterParserImpl();

        String originalFilter = "(& (a=A) (| (b=B) (c=C) ) )";

        ExprNode node = parser.parse( originalFilter );

        req.setFilter( node );

        req.setScope( ScopeEnum.SINGLELEVEL );

        req.setDerefAliases( DerefAliasesEnum.DEREFALWAYS );

        MessageEncoder encoder = new MessageEncoder();

        ByteBuffer buf = encoder.encodeBlocking( req );

        CallbackHistory history = new CallbackHistory();

        MessageDecoder decoder = new MessageDecoder();

        decoder.setCallback( history );

        decoder.decode( buf );

        SearchRequest decodedReq = ( SearchRequest ) history.getMostRecent();

        assertNotNull( decodedReq );

        String decodedNormalized = BranchNormalizedVisitor.getNormalizedFilter( decodedReq.getFilter() );

        String originalNormalized = BranchNormalizedVisitor.getNormalizedFilter( originalFilter );

        assertEquals( originalNormalized, decodedNormalized );
    }
}
