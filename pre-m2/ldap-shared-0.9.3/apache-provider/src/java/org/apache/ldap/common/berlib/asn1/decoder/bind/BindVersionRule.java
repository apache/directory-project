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


import org.apache.asn1.ber.digester.rules.PrimitiveIntDecodeRule;
import org.apache.ldap.common.message.BindRequest;


/**
 * Rule that sets the version of the BindRequest.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class BindVersionRule extends PrimitiveIntDecodeRule
{
    /**
     * Super method authomatically decoded and pushs the int onto the stack
     * which this override pops afterwords and uses to set the version on
     * the BindRequest at the top of the object stack.
     *
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        // pushes a int onto the primitive int stack
        super.finish() ;

        // pops the int the super method pushed
        int version = getDigester().popInt() ;

        // peek at the BindRequest underneath whose name we set
        BindRequest req = ( BindRequest ) getDigester().peek() ;
        req.setVersion3( version == 3 ) ;
    }
}
