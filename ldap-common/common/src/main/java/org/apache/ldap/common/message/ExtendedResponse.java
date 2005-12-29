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
 * $Id: ExtendedResponse.java,v 1.4 2003/07/31 21:44:48 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.message ;


/**
 * Extended protocol response message used to confirm the results of a extended
 * request message.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public interface ExtendedResponse
    extends ResultResponse
{
    /** Extended response message type enumeration value */
    MessageTypeEnum TYPE = MessageTypeEnum.EXTENDEDRESP ;

    /**
     * Gets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     *
     * @return the OID of the extended response type.
     */
    String getResponseName() ;

    /**
     * Sets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     *
     * @param a_oid the OID of the extended response type.
     */
    void setResponseName( String a_oid ) ;

    /**
     * Gets the reponse OID specific encoded response values.
     *
     * @return the response specific encoded response values.
     */
    byte [] getResponse() ;

    /**
     * Sets the reponse OID specific encoded response values.
     *
     * @param a_value the response specific encoded response values.
     */
    void setResponse( byte [] a_value ) ;
}
