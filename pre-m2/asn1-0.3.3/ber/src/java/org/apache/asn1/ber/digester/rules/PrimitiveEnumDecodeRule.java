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
package org.apache.asn1.ber.digester.rules ;


import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.primitives.UniversalTag;


/**
 * Rule for decoding an ASN.1 ENUMERATED type.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PrimitiveEnumDecodeRule extends PrimitiveIntDecodeRule
{
    /**
     * Simply uses the UniversalTag.ENUMERATED tag instead of int.
     */
    public PrimitiveEnumDecodeRule()
    {
        super( UniversalTag.ENUMERATED ) ;
    }


    /**
     * Simply uses the UniversalTag.ENUMERATED tag instead of int.
     */
    public PrimitiveEnumDecodeRule( TagEnum tag )
    {
        super( tag ) ;
    }
}
