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
package org.apache.asn1.ber.digester;

import junit.framework.*;
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;

import java.nio.ByteBuffer;


/**
 * Tests the AbstractRule which does not have much but is
 * really used to shut clover up.
 */
public class AbstractRuleTest extends TestCase
{
    public void testAll()
    {
        AbstractRule rule = new MockRule() ;
        rule.getDigester() ;
        rule.setDigester( null ) ;
        rule.tag( 0, true, TypeClass.APPLICATION ) ;
        rule.length( 3 ) ;
        rule.value( ByteBuffer.allocate( 3 ) ) ;
        rule.finish() ;
    }

    class MockRule extends AbstractRule {}
}