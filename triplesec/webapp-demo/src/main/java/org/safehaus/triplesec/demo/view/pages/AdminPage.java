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

import org.safehaus.triplesec.demo.security.AuthenticatedPage;
import org.safehaus.triplesec.demo.security.AuthenticatedWebSession;
import org.safehaus.triplesec.demo.view.panels.ActionPanel;
import org.safehaus.triplesec.demo.view.TriplesecDemoApplication;
import org.safehaus.triplesec.demo.view.ProxyDataProvider;
import org.safehaus.triplesec.demo.dao.AccountDao;
import org.safehaus.triplesec.guardian.Profile;
import wicket.markup.html.basic.Label;
import wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import wicket.model.Model;
import wicket.model.IModel;
import wicket.Application;

import java.util.ArrayList;


public class AdminPage extends BasePage implements AuthenticatedPage
{
    private static final long serialVersionUID = 944257846240276627L;

    public AdminPage()
    {
        AuthenticatedWebSession session = (AuthenticatedWebSession) getSession();
        Profile profile = session.getUserProfile();
        add( new Label( "user", profile.getUserName() ) );
        add( new Label( "roles", profile.getRoles().toString() ) );

        ArrayList cols = new ArrayList();
        cols.add( new PropertyColumn( new Model( "user id" ), "uid", "uid" ) );
        cols.add( new PropertyColumn( new Model( "bonds" ), "bonds" ) );
        cols.add( new PropertyColumn( new Model( "tech stocks" ), "techStocks" ) );
        cols.add( new PropertyColumn( new Model( "high yield" ), "volatileHighYield" ) );
        cols.add( new PropertyColumn( new Model( "t-bills" ), "tBills" ) );
        cols.add( new PropertyColumn( new Model( "foreign" ), "foreign" ) );
        cols.add( new AbstractColumn( new Model( "actions" ) )
        {
            private static final long serialVersionUID = -410352249728650137L;

            public void populateItem( Item item, String id, IModel model )
            {
                item.add( new ActionPanel( id, model ) );
            }
        });

        add( new DefaultDataTable( "accounts", cols, getDataProvider(), 5 ) );
    }

    private AccountDao getAccountDao()
    {
        return ( (TriplesecDemoApplication) Application.get() ).getAccountDaoProxy();
    }

    protected SortableDataProvider getDataProvider()
    {
        return new ProxyDataProvider( getAccountDao() );
    }
    
}
