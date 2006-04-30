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
package org.apache.directory.shared.ldap.message;


/**
 * Bind protocol response message used to confirm the results of a bind request
 * message. BindResponse consists simply of an indication from the server of the
 * status of the client's request for authentication.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public interface BindResponse extends ResultResponse
{
    /** Bind response message type enumeration value */
    MessageTypeEnum TYPE = MessageTypeEnum.BINDRESPONSE;


    /**
     * Gets the optional property holding SASL authentication response paramters
     * that are SASL mechanism specific. Will return null if the authentication
     * is simple.
     * 
     * @return the sasl mech. specific credentials or null of auth. is simple
     */
    byte[] getServerSaslCreds();


    /**
     * Sets the optional property holding SASL authentication response paramters
     * that are SASL mechanism specific. Leave null if authentication mode is
     * simple.
     * 
     * @param a_serverSaslCreds
     *            the sasl auth. mech. specific credentials
     */
    void setServerSaslCreds( byte[] a_serverSaslCreds );
}
