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
package org.safehaus.triplesec.demo.view.panels;

import org.safehaus.triplesec.demo.dao.AccountDao;
import org.safehaus.triplesec.demo.model.Account;
import org.safehaus.triplesec.demo.security.AuthenticatedWebSession;
import org.safehaus.triplesec.demo.service.Registry;
import org.safehaus.triplesec.demo.view.TriplesecDemoApplication;
import org.safehaus.triplesec.demo.view.pages.HomePage;
import org.safehaus.triplesec.jaas.AccountLockedOutException;
import org.safehaus.triplesec.jaas.PreauthFailedException;
import org.safehaus.triplesec.jaas.ResynchInProgressException;
import org.safehaus.triplesec.jaas.ResynchStartingException;
import org.safehaus.triplesec.jaas.SafehausPrincipal;
import wicket.Application;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.CompoundPropertyModel;

import javax.security.auth.login.LoginException;
import java.io.Serializable;


public class BuyPanel extends BasePanel
{
    private static final long serialVersionUID = 1912653768008190645L;
    private String userId;

    public BuyPanel( String id, String userid )
    {
        super( id, null, "Invest an additional 1,000 in a fund?" );
        this.userId = userid;

        final Input input = new Input();
        setModel( new CompoundPropertyModel( input ) );

        // create feedback panel and add to page
        final FeedbackPanel feedback = new FeedbackPanel( "feedback" );
        add( feedback );

        Form form = new Form( "buyForm" ) {
            private static final long serialVersionUID = 7733755424957404354L;

            protected void onSubmit() {
                AuthenticatedWebSession session = (AuthenticatedWebSession) getSession();
                SafehausPrincipal principal = null;
                String username = session.getUsername();
                String password = session.getPassword();
                try
                {
                    principal = Registry.policyManager().getPrincipal( username,
                            password, input.passcode );
                }
                catch ( AccountLockedOutException e )
                {
                    error("Account locked for user '" + username + "'!");
                }
                catch ( PreauthFailedException e )
                {
                    error("Hotp authentication failed for user '" + username + "'!");
                }
                catch ( ResynchInProgressException e )
                {
                    error("User '" + username + "' is still out of sych! Please " +
                            "enter another consecutive single-use password.");
                }
                catch ( ResynchStartingException e )
                {
                    error("User '" + username + "' is out of synch! Initiating " +
                            "resynch protocol. Enter another consecutive single-use " +
                            "password.");
                }
                catch ( LoginException e )
                {
                    error("User '" + username + "' failed authentication: " +
                            e.getMessage());
                }
                if ( principal != null )
                {
                    int index = Account.getIndex( input.fund );
                    Account account = getAccountDao().get( getUserId() );
                    account.set( input.fund, account.get( index ) + 1000 );
                    getAccountDao().update( account );
                    setResponsePage( HomePage.class );
                }
            }
        };
        add( form );

        form.add( new DropDownChoice( "fund", Account.FUNDS ) );
        form.add( new PasswordTextField( "passcode" ).setRequired( false ) );
    }

    public String getUserId()
    {
        return userId;
    }

    private static class Input implements Serializable
    {
        private static final long serialVersionUID = -399677402650538731L;

        public String fund;
        public String passcode;


        public String toString() {
            return "Input{" +
                    "fund='" + fund + '\'' +
                    ", passcode='" + passcode + '\'' +
                    '}';
        }
    }

    private AccountDao getAccountDao()
    {
        return ( (TriplesecDemoApplication) Application.get() ).getAccountDaoProxy();
    }


}
