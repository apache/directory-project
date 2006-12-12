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
package org.safehaus.triplesec.demo.view;


import org.safehaus.triplesec.demo.security.AuthenticatedPage;
import org.safehaus.triplesec.demo.security.AuthenticatedWebSession;
import org.safehaus.triplesec.demo.security.PageAuthorizationStrategy;
import org.safehaus.triplesec.demo.view.pages.HomePage;
import org.safehaus.triplesec.demo.view.pages.LoginPage;
import org.safehaus.triplesec.demo.dao.AccountDao;
import org.safehaus.triplesec.demo.service.Registry;
import wicket.ISessionFactory;
import wicket.Session;
import wicket.spring.SpringWebApplication;

import javax.servlet.ServletConfig;
import java.util.Properties;


/**
 * Entry point of the Triplesec Demo web application.
 */
public class TriplesecDemoApplication extends SpringWebApplication
{
    // Ldap init parameter names
    private static final String LDAP_PORT_PARAM = "ldapPort";
    private static final String LDAP_BASE_DN_PARAM = "ldapBaseDn";
    private static final String LDAP_HOST_PARAM = "ldapHost";
    private static final String DEMO_LDAP_CREDENTIALS_PARAM = "demoAppLdapCredentials";

    // SMS init parameters names
    private static final String SMS_TRANSPORT_URL_PARAM = "smsTransportUrl";
    private static final String SMS_PASSWORD_PARAM = "smsPassword";
    private static final String SMS_USERNAME_PARAM = "smsUsername";
    private static final String SMS_ACCOUNT_PARAM = "smsAccountName";
    
    private boolean headless = false;

    /**
     * This field holds an account dao proxy that is safe to use in
     * wicket components.
     */
    private AccountDao accountDaoProxy;

    /**
     * This field holds the actual account dao retrieved from Spring
     * context. This object should never be serialized because it will
     * take the container with it, so BE CAREFUL when using this.
     */
    private AccountDao accountDao;

    /**
     * Retrieves account dao bean. This bean should not be serialized so
     * BE CARFEFUL when using it.
     *
     * @return account dao bean
     */
    public AccountDao getAccountDao()
    {
        if ( accountDao == null )
        {
            synchronized ( this )
            {
                if ( accountDao == null )
                {
                    accountDao = (AccountDao) internalGetApplicationContext()
                            .getBean( "accountDao", AccountDao.class );
                }
            }
        }
        return accountDao;
    }

    /**
     * Returns a lazy init proxy for the dao bean. This proxy is safe to
     * serialize and will take up very little space when serialized.
     *
     * @return a lazy init proxy for the dao bean
     */
    public AccountDao getAccountDaoProxy()
    {
        if ( accountDaoProxy == null )
        {
            synchronized( this )
            {
                if ( accountDaoProxy == null )
                {
                    accountDaoProxy = (AccountDao) createSpringBeanProxy(
                            AccountDao.class, "accountDao" );
                }
            }
        }
        return accountDaoProxy;
    }

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
        // strip out Wicket's tags from the rendered markup source
        getMarkupSettings().setStripWicketTags( true );

        // set authorization strategy
        getSecuritySettings().setAuthorizationStrategy(
           new PageAuthorizationStrategy( AuthenticatedPage.class, LoginPage.class )
        );

        // TODO get rid of this shit out of the web.xml
        ServletConfig config = getWicketServlet().getServletConfig();
        
        // -------------------------------------------------------------------
        // Get LDAP init parameters
        // -------------------------------------------------------------------

        String ldapHost = config.getInitParameter( LDAP_HOST_PARAM );
        String ldapBaseDn = config.getInitParameter( LDAP_BASE_DN_PARAM );
        String ldapPort = config.getInitParameter( LDAP_PORT_PARAM );

        // Credentials for demo app not the administrator's credentials so
        // we use a different init parameter here since the plugged in one 
        // is really for the admin.  
        String ldapCredentials = config.getInitParameter( DEMO_LDAP_CREDENTIALS_PARAM );
        
        // -------------------------------------------------------------------
        // Get SMS init parameters
        // -------------------------------------------------------------------

        String smsPassword = config.getInitParameter( SMS_PASSWORD_PARAM );
        String smsUsername = config.getInitParameter( SMS_USERNAME_PARAM );
        String smsAccountName = config.getInitParameter( SMS_ACCOUNT_PARAM );
        String smsTransportUrl = config.getInitParameter( SMS_TRANSPORT_URL_PARAM );
        
        String realm = config.getInitParameter( "realm" );
        
        // -------------------------------------------------------------------
        // Setup policy manager properties
        // -------------------------------------------------------------------

        Properties props = new Properties();
        StringBuffer buf = new StringBuffer();
        buf.append( "appName=demo,ou=Applications," ).append( ldapBaseDn );
        props.setProperty( "applicationPrincipalDN", buf.toString() );
        props.setProperty( "applicationCredentials", ldapCredentials );
        Registry.policyManager().setLdapProperties( props );
        Registry.policyManager().setRealm( realm );

        buf.setLength( 0 );
        buf.append( "ldap://" ).append( ldapHost ).append( ":" )
                .append( ldapPort ).append( "/" ).append( ldapBaseDn );
        Registry.policyManager().setUrl( buf.toString() );
        
        // -------------------------------------------------------------------
        // Setup sms manager properties
        // -------------------------------------------------------------------

        Registry.smsManager().setSmsAccountName( smsAccountName );
        Registry.smsManager().setSmsPassword( smsPassword );
        Registry.smsManager().setSmsTransportUrl( smsTransportUrl );
        Registry.smsManager().setSmsUsername( smsUsername );
    }


    protected ISessionFactory getSessionFactory() {
        return new ISessionFactory()
        {
            public Session newSession() {
                return new AuthenticatedWebSession( TriplesecDemoApplication.this );
            }
        };
    }

    public boolean isHeadless()
    {
        return headless;
    }


    public void setHeadless(boolean headless)
    {
        this.headless = headless;
    }
}
