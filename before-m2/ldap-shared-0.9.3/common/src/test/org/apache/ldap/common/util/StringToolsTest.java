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
package org.apache.ldap.common.util;


import junit.framework.TestCase;


/**
 * Tests the StringTools class methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class StringToolsTest extends TestCase
{
    public void testTrimConsecutiveToOne()
    {
        String input = null;
        String result = null;

        input = "akarasulu**";
        result = StringTools.trimConsecutiveToOne( input, '*' );
        assertEquals( "akarasulu*", result );

        input = "*****akarasulu**";
        result = StringTools.trimConsecutiveToOne( input, '*' );
        assertEquals( "*akarasulu*", result );

        input = "**akarasulu";
        result = StringTools.trimConsecutiveToOne( input, '*' );
        assertEquals( "*akarasulu", result );

        input = "**akar****asulu**";
        result = StringTools.trimConsecutiveToOne( input, '*' );
        assertEquals( "*akar*asulu*", result );

        input = "akarasulu";
        result = StringTools.trimConsecutiveToOne( input, '*' );
        assertEquals( "akarasulu", result );

        input = "*a*k*a*r*a*s*u*l*u*";
        result = StringTools.trimConsecutiveToOne( input, '*' );
        assertEquals( "*a*k*a*r*a*s*u*l*u*", result );

    }
}
