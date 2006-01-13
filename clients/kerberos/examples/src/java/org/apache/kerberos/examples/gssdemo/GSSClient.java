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
import java.io.IOException;
import java.net.Socket;
import java.security.PrivilegedAction;
import java.security.Security;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.kerberos.jaas.CallbackHandlerBean;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

class GSSClient implements PrivilegedAction
{

	private CallbackHandlerBean beanCallbackHandler = null;

	private static final int TEN_MINUTES = 10 * 60;

	private GSSContext context = null;

	private LoginContext peerLC = null;

	private Socket socket = null;
	private DataInputStream inStream;
	private DataOutputStream outStream;

	private String clientName = null;
	private String serverName = null;
	private String serverAddress = null;
	private int serverPort;

	public GSSClient(String clientName, String password, String serverName, String serverAddress,
			int serverPort, String kerberosRealm, String kdcAddress)
    {
		beanCallbackHandler = new CallbackHandlerBean(clientName, password);
		this.clientName = clientName;
		this.serverName = serverName;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		System.setProperty("java.security.krb5.realm", kerberosRealm);
		System.setProperty("java.security.krb5.kdc", kdcAddress);

		System.setProperty("sun.security.krb5.debug", "true");
		Security.setProperty("login.configuration.provider",
				"org.apache.kerberos.kdc.jaas.Krb5LoginConfiguration");
	}

	public void login()
    {
		try
        {
			peerLC = new LoginContext(clientName, beanCallbackHandler);
			peerLC.login();

			socket = new Socket(serverAddress, serverPort);
			inStream = new DataInputStream(socket.getInputStream());
			outStream = new DataOutputStream(socket.getOutputStream());

			context = (GSSContext) Subject.doAs(peerLC.getSubject(), this);
		}
        catch (Exception e)
        {
			System.out.println(">>> GSSClient ... Secure Context not established.");
			System.out.println(">>> GSSClient ... ERROR:  " + e.getMessage());
		}
	}

	public boolean hasConfidentialContext()
    {
		return context != null && context.getConfState();
	}

	// PrivilegedAction method
	public Object run()
    {
		try
        {
			GSSManager manager = GSSManager.getInstance();
			
			Oid kerberos = new Oid("1.2.840.113554.1.2.2");

			GSSName clientPeerName = manager.createName(clientName, GSSName.NT_USER_NAME);

			GSSName remotePeerName = manager.createName(serverName, GSSName.NT_USER_NAME);

			System.out.println(">>> GSSClient ... Getting client credentials");

			GSSCredential peerCredentials = manager.createCredential(clientPeerName, TEN_MINUTES,
					kerberos, GSSCredential.INITIATE_ONLY);

			System.out.println(">>> GSSClient ... GSSManager creating security context");
			GSSContext peerContext = manager.createContext(remotePeerName, kerberos,
					peerCredentials, GSSContext.DEFAULT_LIFETIME);

			peerContext.requestConf(true);
			byte[] byteToken = new byte[0];

			System.out.println(">>> GSSClient ... Sending token to server over secure context");

			while (!peerContext.isEstablished())
            {
				byteToken = peerContext.initSecContext(byteToken, 0, byteToken.length);

				if (byteToken != null)
                {
					outStream.writeInt(byteToken.length);
					outStream.write(byteToken);
					outStream.flush();
				}

				if (!peerContext.isEstablished())
                {
					byteToken = new byte[inStream.readInt()];
					inStream.readFully(byteToken);
				}
			}

			return peerContext;
		}
        catch (GSSException ge)
        {
			System.out.println(">>> GSSClient ... GSS Exception " + ge.getMessage());
		}
        catch (IOException e)
        {
			System.out.println(">>> GSSClient ... Exception " + e.getMessage());
		}
		return null;
	}

	public String sendMessageReturnReply(String message)
    {
		MessageProp msgProp = new MessageProp(0, true);

		try
        {
			System.out.println(">>> GSSClient ... Client message is [" + message + "]");
			byte[] clientMessage = context.wrap(message.getBytes(), 0, message.getBytes().length,
					msgProp);
			outStream.writeInt(clientMessage.length);
			outStream.write(clientMessage);
			outStream.flush();

			// Receiving server response and sending back to client.
			byte[] serverMessage = new byte[inStream.readInt()];
			inStream.readFully(serverMessage);
			serverMessage = context.unwrap(serverMessage, 0, serverMessage.length, msgProp);
			System.out.print(">>> GSSClient ... Server message is [");
			System.out.println(new String(serverMessage) + "]");
			return new String(serverMessage);
		}
        catch (GSSException ge)
        {
			ge.printStackTrace();
			return null;
		}
        catch (IOException ioe)
        {
			ioe.printStackTrace();
			return null;
		}
	}

	public void logout()
    {
		try
        {
			peerLC.logout();
			context.dispose();
		}
        catch (LoginException le)
        {
			le.printStackTrace();
		}
        catch (GSSException ge)
        {
			ge.printStackTrace();
		}
	}
}

