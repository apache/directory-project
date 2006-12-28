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

import org.safehaus.triplesec.demo.security.AuthenticatedWebSession;
import org.safehaus.triplesec.demo.view.TriplesecDemoApplication;
import org.safehaus.triplesec.demo.view.pages.WapPushPage;
import org.safehaus.triplesec.demo.service.Registry;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Button;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.form.RequiredTextField;
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.Model;
import wicket.model.PropertyModel;
import wicket.util.value.ValueMap;

/**
 * Login panel with username, password, and passcode, as well
 * as support for cookie persistence of all three. When the
 * panel's form is submitted, the abstract method
 * <code>login(String, String, String)</code> is called,
 * passing the username, password, and passcode submitted.
 * The <code>login()</code> method should log the user in and
 * return null if no error occured, or a descriptive String in
 * the event that the login fails.
 */
public abstract class LoginPanel extends BasePanel
{
    /** Input field for password */
    private PasswordTextField password;

    /** Input field for passcode */
    private PasswordTextField passcode;

    /** Input field for user name */
    private RequiredTextField username;

    /** Checkbox for graphics mode */
    private boolean mode = true;


    /**
     * Login form.
     */
    public final class LoginForm extends Form
    {
        private static final long serialVersionUID = 8944436409011853603L;

        /** El-cheapo model for form */
        private final ValueMap properties = new ValueMap();

        /**
         * Constructor.
         *
         * @param id id of the form component
         */
        public LoginForm( final String id )
        {
            super( id );

            // Attach textfield components for username, password,
            // and passcode that edit properties map in lieu of a
            // formal beans model
            username = new RequiredTextField( "username",
                    new PropertyModel( properties, "username" ) );
            username.setLabel( new Model("Username") );
            add( username );
            password = new PasswordTextField( "password",
                    new PropertyModel( properties, "password" ) );
            password.setRequired( true );
            password.setLabel( new Model( "Password" ) );
            add( password );
            passcode = new PasswordTextField( "passcode",
                    new PropertyModel( properties, "passcode" ) );
            passcode.setLabel( new Model( "Passcode" ) );
            passcode.setRequired( false );
            add( passcode );
            add( new CheckBox( "mode",
                    new PropertyModel( LoginPanel.this, "mode" ) ) );

            add( new Button("submit") );

        }


        public final void onSubmit()
        {
            ( (TriplesecDemoApplication) getApplication() )
                    .setHeadless( !isMode() );

            if ( login( getUsername(), getPassword(), getPasscode() ) )
            {
                // if login has been called because the user was not
                // yet logged in, then continue to the original
                // destination; otherwise to the Home page...
                if ( !continueToOriginalDestination() )
                {
                    // HTTP redirect response has been committed. No
                    // more data shall be written to the response
                    gotoHomePage();
                }
                else
                {
                    // Try the component based localizer first. If not
                    // found try the application localizer. Else use the
                    // default
                    final String errmsg = getLocalizer()
                            .getString( "loginError", this, "Unable to log you in" );
                    error( errmsg );
                }
            }
            // login failed... get reason... display to feedback panel...
            else
            {
                error( ((AuthenticatedWebSession) getSession()).getErrorMessage() );
            }
        }

        private void gotoHomePage()
        {
            setResponsePage(
                    getApplication().getSessionSettings().getPageFactory()
                            .newPage(getApplication().getHomePage())
            );
        }
    }


    public LoginPanel( final String id )
    {
        super( id, null, "Login" );

        // create feedback panel and add to page
        final FeedbackPanel feedback = new FeedbackPanel( "feedback" );
        add( feedback );

        String realm = Registry.policyManager().getRealm();
        add( new Label( "realm", realm ) );

        // add login form to page, passing feedback panel as
        // validation error handler
        add( new LoginForm( "loginForm" ) );

        add( new Link( "smsLink" )
        {
            private static final long serialVersionUID = -3757666140892829417L;

            public void onClick()
            {
                setResponsePage( WapPushPage.class );
            }
        } );
    }


    /**
     * Convenience method to access the passcode.
     */
    public String getPasscode()
    {
        return passcode.getModelObjectAsString();
    }


    /**
     * Convenience method to access the password.
     */
    public String getPassword()
    {
        return password.getModelObjectAsString();
    }


    /**
     * Convenience method to access the username.
     */
    public String getUsername()
    {
        return username.getModelObjectAsString();
    }


    /**
     * Convenience method to access graphical mode switch.
     */
    public boolean isMode()
    {
        return mode;
    }


    /**
     * Set model object for graphical mode.
     */
    public void setMode( boolean mode )
    {
        this.mode = mode;
    }


    /**
     * Login user to the application.
     *
     * @param username the username
     * @param password the password
     * @param passcode the passcode
     *
     * @return <b>true</b> if login was successful
     */
    public abstract boolean login( final String username,
                                   final String password,
                                   final String passcode );

}
