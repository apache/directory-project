/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.triplesec.demo.view.pages;

import org.safehaus.triplesec.demo.view.panels.LoginPanel;
import org.safehaus.triplesec.demo.security.AuthenticatedWebSession;


public class LoginPage extends BasePage
{
    private static final long serialVersionUID = 1331634141979504338L;

    public LoginPage()
    {
        add( new LoginPanel( "loginPanel" )
        {
            private static final long serialVersionUID = -2645418704580992221L;

            public boolean login(final String username, final String password, final String passcode) {
                return ( (AuthenticatedWebSession) getSession() )
                        .authenticate( username, password, passcode );
            }
        } );
    }
}
