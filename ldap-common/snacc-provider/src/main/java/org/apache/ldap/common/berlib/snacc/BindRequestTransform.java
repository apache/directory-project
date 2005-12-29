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


import org.apache.ldap.common.NotImplementedException;
import org.apache.ldap.common.berlib.snacc.ldap_v3.AuthenticationChoice;
import org.apache.ldap.common.berlib.snacc.ldap_v3.BindRequest;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice;
import org.apache.ldap.common.message.BindRequestImpl;
import org.apache.ldap.common.message.spi.ProviderException;

import java.math.BigInteger;


/**
 * Bind request transformation functions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class BindRequestTransform
{
	public static final BigInteger LDAPV2 = new BigInteger("2") ;
	public static final BigInteger LDAPV3 = new BigInteger("3") ;


	static org.apache.ldap.common.message.BindRequest
        transform( LDAPMessage a_snaccMessage )
        throws ProviderException
    {
        // Construct new bind request envelope.
		BindRequestImpl l_request =
            new BindRequestImpl( a_snaccMessage.messageID.intValue() ) ;

        // Transform and add controls to the bind request.
        ControlTransform.transformFromSnacc( l_request,
            a_snaccMessage.controls ) ;

        // Set the ldap version flag for the bind behavoir to exhibit
        if( a_snaccMessage.protocolOp.bindRequest.version.equals( LDAPV3 ) )
        {
            l_request.setVersion3( true ) ;
        }
        else
        {
            l_request.setVersion3( false ) ;
        }

        // Sets the authentication mode flag
        if( a_snaccMessage.protocolOp.bindRequest.authentication.choiceId ==
            AuthenticationChoice.SIMPLE_CID )
        {
            l_request.setSimple( true ) ;
        }
        else
        {
            throw new NotImplementedException(
                "The SASL Authentication Mechanism not implemented yet!" ) ;
        }

		// --------------------------------------------------------------------
        // Below here auth is simple so we get the Dn and simple credentials
		// --------------------------------------------------------------------

		// Now we set the distinguished name of the user attempting to bind
        // taking special care to check for an the annonymous bind attempt with
        // a null or empty string distinguished name for the name field of PDU
        byte [] l_dn = a_snaccMessage.protocolOp.bindRequest.name ;
        if( l_dn == null )
        {
            l_request.setName( "" ) ;
        }
        else
        {
			l_request.setName( new String( l_dn ) ) ;
        }

        // Get the simple user credentials and set them within the message.
        byte [] l_credentials = a_snaccMessage.protocolOp.
            bindRequest.authentication.simple ;
        if( l_credentials == null )
        {
            l_credentials = new byte [0] ;
        }
		l_request.setCredentials( l_credentials ) ;

        return l_request ;
    }


    static LDAPMessage transform( org.apache.ldap.common.message.BindRequest a_req )
    {
        // Build the envelop minus everyting specific to this request type
        LDAPMessage l_message = Utils.prepareEnvelope(a_req) ;
        BindRequest l_request = new BindRequest() ;
        AuthenticationChoice l_authChoice = new AuthenticationChoice() ;

        // Set protocol choice id and set bind req handle to new BindRequest
        l_message.protocolOp.choiceId = LDAPMessageChoice.BINDREQUEST_CID ;
        l_message.protocolOp.bindRequest = l_request ;

        // Set the name of the authenticating user.
        l_request.name = a_req.getName().getBytes() ;

        // Setup the authentication mode to be simple or sasl.
        l_request.authentication = l_authChoice ;
        if( a_req.isSimple() )
        {
            l_authChoice.choiceId = AuthenticationChoice.SIMPLE_CID ;
			l_authChoice.simple = a_req.getCredentials() ;
        }
        else
        {
            l_authChoice.choiceId = AuthenticationChoice.SASL_CID ;
            throw new NotImplementedException(
                "SASL authentication has not been implemented yet!" ) ;
        }

		if( a_req.isVersion3() )
        {
			l_request.version = LDAPV3 ;
        }
        else
        {
            l_request.version = LDAPV2 ;
        }

        return l_message ;
    }
}
