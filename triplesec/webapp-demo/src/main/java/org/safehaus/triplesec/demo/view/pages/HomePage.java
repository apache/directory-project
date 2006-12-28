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

import org.safehaus.triplesec.demo.view.ChartResource;
import org.safehaus.triplesec.demo.dao.AccountDao;
import org.safehaus.triplesec.demo.model.Account;
import org.safehaus.triplesec.demo.security.AuthenticatedPage;
import org.safehaus.triplesec.demo.security.AuthenticatedWebSession;
import org.safehaus.triplesec.demo.view.TriplesecDemoApplication;
import org.safehaus.triplesec.demo.view.panels.BuyPanel;
import org.safehaus.triplesec.guardian.Profile;
import wicket.Application;
import wicket.markup.html.basic.Label;
import wicket.markup.html.image.Image;

/**
 * The default home page of the Safehaus Demo web application.
 */
public class HomePage extends BasePage implements AuthenticatedPage
{
    private static final long serialVersionUID = 944257846240276627L;

    public HomePage()
    {
        AuthenticatedWebSession session = (AuthenticatedWebSession) getSession();
        Profile profile = session.getUserProfile();
        add( new Label( "user", profile.getUserName() ) );
        add( new Label( "roles", profile.getRoles().toString() ) );

        Account account = getAccountDao().get( profile.getUserName() );
        add( new Image( "chartImage", new ChartResource( account ) ) );

        BuyPanel buyPanel = new BuyPanel( "buyPanel", profile.getUserName() );
        if ( profile.isInRole( "untrusted" ) )
        {
            buyPanel.setVisible( false );
        }
        add( buyPanel );

    }

    private AccountDao getAccountDao()
    {
        return ( (TriplesecDemoApplication) Application.get() ).getAccountDaoProxy();
    }

}
