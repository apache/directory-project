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
package org.apache.ldap.common.berlib.asn1.decoder ;


import org.apache.asn1.ber.digester.rules.PrimitiveEnumDecodeRule;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.message.ResultResponse;


/**
 * Put some documentation here.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ResultCodeRule extends PrimitiveEnumDecodeRule
{
    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;

        int val = getDigester().popInt() ;
        ResultCodeEnum resultCode = ResultCodeEnum.getResultCodeEnum( val ) ;
        ResultResponse resp = ( ResultResponse ) getDigester().peek() ;
        resp.getLdapResult().setResultCode( resultCode ) ;
    }
}
