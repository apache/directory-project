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
package org.safehaus.triplesec.demo.view.borders;

import org.safehaus.triplesec.demo.security.AuthenticatedWebSession;
import org.safehaus.triplesec.demo.view.pages.HomePage;
import org.safehaus.triplesec.demo.view.pages.LoginPage;
import org.safehaus.triplesec.demo.view.pages.AdminPage;
import org.safehaus.triplesec.guardian.Profile;
import wicket.markup.html.border.Border;
import wicket.markup.html.link.Link;
import wicket.markup.html.basic.Label;
import wicket.model.IModel;

/**
 * Renders a consistent border layout for every page when included.
 */
public class PageBorder extends Border
{
    private static final long serialVersionUID = -8571671577233566370L;

    public PageBorder( String id )
    {
        this( id, null );
    }

    public PageBorder( String id, IModel model )
    {
        super( id, model );

        final AuthenticatedWebSession session =
                (AuthenticatedWebSession) getSession();

        Link homeLink = new Link( "home" )
        {
            private static final long serialVersionUID = -7817022822646528881L;

            public void onClick()
            {
                setResponsePage( HomePage.class );
            }
        };
        if ( !session.isAuthenticated() )
        {
            homeLink.setEnabled( false );
        }
        else
        {
            homeLink.setEnabled( true );
        }
        add( homeLink );

        Link logoutLink = new Link( "logout" )
        {
            private static final long serialVersionUID = 3506418292280323906L;

            public void onClick()
            {
                if ( session.isAuthenticated() )
                {
                    session.invalidate();
                    setResponsePage( LoginPage.class );
                }
            }
        };
        if ( !session.isAuthenticated() )
        {
            logoutLink.setEnabled( false );
        }
        else
        {
            logoutLink.setEnabled( true );
        }
        add( logoutLink );

        Link adminLink = new Link( "adminLink" )
        {
            private static final long serialVersionUID = 7673228318888483188L;

            public void onClick() {
                setResponsePage( AdminPage.class );
            }
        };
        Label adminLabel = new Label( "adminText", "view all accounts" );

        Profile profile = ((AuthenticatedWebSession) getSession()).getUserProfile();
        if ( profile != null && profile.isInRole( "superuser" ) )
        {
            adminLink.setVisible( true );
            adminLabel.setVisible( true );
        }
        else
        {
            adminLink.setVisible( false );
            adminLabel.setVisible( false );
        }
        add( adminLink );
        add( adminLabel );
    }
}
