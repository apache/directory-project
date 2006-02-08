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
package org.apache.ldap.common.berlib.snacc ;


import java.io.IOException ;
import java.math.BigInteger;

import javax.naming.Name ;
import javax.naming.NamingException ;

import org.apache.ldap.common.name.DnParser ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;

import org.apache.ldap.common.message.ResultCodeEnum ;
import org.apache.ldap.common.message.spi.ProviderException ;


/**
 * Generic transformation helper and utility methods.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class Utils
{
	static LDAPMessage prepareEnvelope(org.apache.ldap.common.message.Message a_message)
    {
        LDAPMessage l_message = new LDAPMessage() ;
        l_message.messageID = new BigInteger( 
                Integer.toString(a_message.getMessageId())) ;
        LDAPMessageChoice l_protocolOp = new LDAPMessageChoice() ;
        l_message.controls =
            ControlTransform.transformToSnacc( a_message.getControls() ) ;
        return l_message ;
    }
}
