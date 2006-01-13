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

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GSSClientApplet extends Applet
{

	private GSSClient gssClient = null;

	// UI parameters
	private Label lblUserName = new Label("Username:");
	private Label lblPassword = new Label("Password:");

	private TextField tfUserName = new TextField(12);
	private TextField tfPassword = new TextField(12);

	private Button buttonPartner1 = new Button("  Login to ldap  ");

	private Color bgColor = new Color(204, 204, 255);

	private TextArea taResponse = null;

	// GSS parameters.
	private String remotePeer = null;
	private String kerberosRealm = null;
	private String kdcAddress = null;
	private String addressOfRemotePeer = null;
	private int portOfRemotePeer;

	public void init()
    {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(lblUserName);
		add(tfUserName);
		add(lblPassword);
		add(tfPassword);

		buttonPartner1.setBackground(bgColor);

		kerberosRealm       = "25OZ.COM";
		kdcAddress          = "localhost";
		addressOfRemotePeer = "localhost";

		add(buttonPartner1);
		buttonPartner1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				remotePeer = "ldap";
				portOfRemotePeer = 1082;
				login();
			}
		});

		taResponse = new TextArea("[Output Window] ...\n\r", 12, 58);
		taResponse.setBackground(Color.white);
		add(taResponse);
	}

	private void login()
    {
		try
        {
			if (tfUserName.getText().equals("") && tfPassword.getText().equals(""))
				taResponse.append("Please use your username to login ...\n\r");
			else
            {
				gssClient = new GSSClient(tfUserName.getText() + "@" + kerberosRealm,
						tfPassword.getText(), remotePeer, addressOfRemotePeer,
						portOfRemotePeer, kerberosRealm, kdcAddress);

				taResponse.append(tfUserName.getText() + " being logged in ...\n\r");
				
				gssClient.login();
				
				if (gssClient.hasConfidentialContext())
                {
					String message = new String("Sample secret message from client");
					taResponse.append("You are successfully logged in ... \n\r");
					taResponse.append("Sending [" + message + "] to server \n\r");
					String response = gssClient.sendMessageReturnReply(message);
					taResponse.append("Server response ... " + response + "\n\r");
				}
                else
                {
					taResponse.append("Confidential context failed. \n\r");
				}

				try
                {
					gssClient.logout();
				}
                catch (Exception e)
                {
					e.printStackTrace();
				}
			}
		}
        catch (Exception e)
        {
			taResponse.append("Exception ..." + e.getMessage() + "\n\r");
		}
	}
}

