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

/*
 * $Id: BindResponse.java,v 1.5 2003/07/31 21:44:49 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.message ;


/**
 * Bind protocol response message used to confirm the results of a bind
 * request message.  BindResponse consists simply of an indication from the
 * server of the status of the client's request for authentication.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public interface BindResponse
    extends ResultResponse
{
    /** Bind response message type enumeration value */
    MessageTypeEnum TYPE = MessageTypeEnum.BINDRESPONSE ;

    /**
     * Gets the optional property holding SASL authentication response paramters
     * that are SASL mechanism specific.  Will return null if the authentication
     * is simple.
     *
     * @return the sasl mech. specific credentials or null of auth. is simple
     */
    byte [] getServerSaslCreds() ;

    /**
     * Sets the optional property holding SASL authentication response paramters
     * that are SASL mechanism specific.  Leave null if authentication mode is
     * simple.
     *
     * @param a_serverSaslCreds the sasl auth. mech. specific credentials
     */
    void setServerSaslCreds( byte [] a_serverSaslCreds ) ;
}
