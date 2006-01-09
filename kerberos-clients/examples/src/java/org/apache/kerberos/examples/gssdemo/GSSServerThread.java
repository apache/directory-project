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
package org.apache.kerberos.examples.gssdemo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivilegedAction;
import java.security.Security;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import org.apache.kerberos.jaas.CallbackHandlerBean;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

public class GSSServerThread implements PrivilegedAction
{

	//Handles callback from the JAAS framework.
	CallbackHandlerBean beanCallbackHandler = null;

	//The main object that handles all JAAS login.
	LoginContext serverLC = null;

	//The context for secure communication with client.
	GSSContext serverGSSContext = null;

	//Socket and streams used for communication.
	ServerSocket serverSocket = null;
	DataInputStream inStream = null;
	DataOutputStream outStream = null;

	//Name and port of server.
	private String _serverName;
	private int    _serverPort;
	private String _password;
	private String _realm;
	private String _kdc;

	//Configuration file and the name of the client configuration.
	String _confFile = null;
	String _confName = null;

	// GSSServerThread constructor
	public GSSServerThread()
    {

		_serverName = "ldap";
		_password   = "keyrand";
		_serverPort = 1082;
		_realm      = "25OZ.COM";
		_kdc        = "enrique.25oz.com";

		beanCallbackHandler = new CallbackHandlerBean(_serverName, _password);
		System.setProperty("java.security.krb5.realm", _realm);
		System.setProperty("java.security.krb5.kdc", _kdc);
		System.setProperty("sun.security.krb5.debug", "true");
		Security.setProperty("login.configuration.provider",
				"org.apache.kerberos.kdc.jaas.Krb5LoginConfiguration");
	}

	public boolean startServer()
    {

		try
        {
			serverLC = new LoginContext(_serverName, beanCallbackHandler);
			serverLC.login();
			Subject.doAs(serverLC.getSubject(), this);
			return true;
		}
        catch (Exception e)
        {
			System.out.println(">>> GSSServerThread ... Secure Context not established..");
			e.printStackTrace();
			return false;
		}
	}

	public Object run()
    {
		while (true)
        {
			try
            {
				serverSocket = new ServerSocket(_serverPort);
				GSSManager manager = GSSManager.getInstance();
				Oid kerberos = new Oid("1.2.840.113554.1.2.2");

				System.out.println(">>> GSSServerThread started ... Waiting for incoming connection");

				GSSName serverGSSName = manager.createName(_serverName, null);
				GSSCredential serverGSSCreds = manager.createCredential(serverGSSName,
						GSSCredential.INDEFINITE_LIFETIME, kerberos, GSSCredential.ACCEPT_ONLY);

				serverGSSContext = manager.createContext(serverGSSCreds);

				Socket clientSocket = serverSocket.accept();
				inStream = new DataInputStream(clientSocket.getInputStream());
				outStream = new DataOutputStream(clientSocket.getOutputStream());

				byte[] byteToken = null;

				while (!serverGSSContext.isEstablished())
                {
					byteToken = new byte[inStream.readInt()];
					inStream.readFully(byteToken);
					byteToken = serverGSSContext.acceptSecContext(byteToken, 0, byteToken.length);

					if (byteToken != null)
                    {
						outStream.writeInt(byteToken.length);
						outStream.write(byteToken);
						outStream.flush();
					}
				}

				String clientName = serverGSSContext.getTargName().toString();
				String serverName = serverGSSContext.getSrcName().toString();
				MessageProp msgProp = new MessageProp(0, false);

				byteToken = new byte[inStream.readInt()];
				inStream.readFully(byteToken);

				// Unwrapping and verifying the received message.
				byte[] message = serverGSSContext.unwrap(byteToken, 0, byteToken.length, msgProp);
				System.out.print(">>> GSSServerThread Message [ ");
				System.out.println(new String(message) + " ] received");

				// Wrapping the response message.
				message = new String(">>> GSSServerThread Secure Context established between " + "["
						+ clientName + "] and [" + serverName + "]").getBytes();

				byte[] secureMessage = serverGSSContext.wrap(message, 0, message.length, msgProp);

				outStream.writeInt(secureMessage.length);
				outStream.write(secureMessage);
				outStream.flush();
				System.out.println(">>> GSSServerThread Message [" + new String(message) + "] sent");

				// Disposing and closing client and server sockets.
				serverGSSContext.dispose();
				clientSocket.close();
				serverSocket.close();
				System.out.println(">>> GSSServerThread waiting ... ");
			}
            catch (java.lang.Exception e)
            {
				e.printStackTrace();
			}
		}
	}
}

