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
package org.safehaus.triplesec.adminui.view;

import org.safehaus.triplesec.adminui.security.AuthenticatedPage;
import org.safehaus.triplesec.adminui.security.AuthenticatedWebSession;
import org.safehaus.triplesec.adminui.security.PageAuthorizationStrategy;
import org.safehaus.triplesec.adminui.view.pages.HomePage;
import org.safehaus.triplesec.adminui.view.pages.LoginPage;
import wicket.authorization.IUnauthorizedComponentInstantiationListener;
import wicket.authorization.UnauthorizedInstantiationException;
import wicket.protocol.http.WebApplication;
import wicket.Component;
import wicket.Session;
import wicket.Page;
import wicket.RestartResponseAtInterceptPageException;
import wicket.ISessionFactory;

/**
 * Entry point of the Triplesec Demo web application.
 */
public class TriplesecAdminApplication extends WebApplication
    implements IUnauthorizedComponentInstantiationListener
{
    /**
     * Returns the class of the default home page of the application.
     */
    public Class getHomePage()
    {
        return HomePage.class;
    }


    /**
     * Provides a runtime hook for custom initialization of the application.
     */
    protected void init()
    {
        getMarkupSettings().setStripWicketTags( true );

        // set authorization strategy
        getSecuritySettings().setAuthorizationStrategy(
            new PageAuthorizationStrategy( AuthenticatedPage.class )
        );

        // set unauthorized instantiation instantiation listener
        getSecuritySettings().setUnauthorizedComponentInstantiationListener( this );
    }


    /**
     * @see wicket.protocol.http.WebApplication#getSessionFactory()
     */
    protected ISessionFactory getSessionFactory()
    {
        return new ISessionFactory()
        {
            private static final long serialVersionUID = 1L;

            public Session newSession()
            {
                return new AuthenticatedWebSession( TriplesecAdminApplication.this );
            }
        };
    }

    public void onUnauthorizedInstantiation(final Component component)
    {
        if ( component instanceof Page )
        {
            if ( !isAuthenticated() )
            {
                throw new RestartResponseAtInterceptPageException( LoginPage.class );
            }
        }
        else
        {
            // the component was not a page, so throw an exception
            throw new UnauthorizedInstantiationException( component.getClass() );
        }
    }

    private boolean isAuthenticated()
    {
        return (((AuthenticatedWebSession) Session.get()).isAuthenticated());
    }
}
