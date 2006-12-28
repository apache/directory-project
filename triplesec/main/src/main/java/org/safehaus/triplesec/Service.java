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
package org.safehaus.triplesec;


import java.io.File;
import java.io.FileFilter;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.directory.daemon.DaemonApplication;
import org.apache.directory.daemon.InstallationLayout;
import org.apache.directory.server.core.configuration.ShutdownConfiguration;
import org.apache.directory.server.core.configuration.SyncConfiguration;
import org.apache.directory.server.kerberos.sam.SamSubsystem;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.PropertiesUtils;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

import org.safehaus.profile.BaseServerProfileModifier;
import org.safehaus.profile.ProfileTestData;
import org.safehaus.profile.ServerProfile;
import org.safehaus.triplesec.configuration.MutableTriplesecStartupConfiguration;
import org.safehaus.triplesec.store.DefaultServerProfileStore;
import org.safehaus.triplesec.store.ServerProfileStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * The Triplesec service launched by daemons.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class Service implements DaemonApplication
{
    /** this is the value the port override members have when they are not defined */
    private static final int UNDEFINED_PORT_OVERRIDE = -2;
    /** The period in milliseconds to wait before flushing db buffer cache */
    private static final int SYNCPERIOD_MILLIS = 20000;
    /** THe default port for the HTTP service */
    private static final String HTTP_PORT_DEFAULT = "8383";

    /** Setup a logger */
    private static Logger log = LoggerFactory.getLogger( TriplesecUberjarMain.class );

    /** The environment setting used to start the server */
    private Properties env;
    /** the time the server was started */
    private long startTime;
    /** a possible override port to use instead of what is present within the configuration */
    private int httpPortOverride = UNDEFINED_PORT_OVERRIDE;
    /** a possible override port to use instead of what is present within the configuration */
    private int ldapPortOverride = UNDEFINED_PORT_OVERRIDE;
    /** a possible override port to use instead of what is present within the configuration */
    private int ldapsPortOverride = UNDEFINED_PORT_OVERRIDE;
    /** a possible override port to use instead of what is present within the configuration */
    private int krb5PortOverride = UNDEFINED_PORT_OVERRIDE;
    /** a possible override port to use instead of what is present within the configuration */
    private int changepwPortOverride = UNDEFINED_PORT_OVERRIDE;
    /** a possible override port to use instead of what is present within the configuration */
    private int ntpPortOverride = UNDEFINED_PORT_OVERRIDE;
    /** embedded Jetty httpService */
    private Server httpService;
    /** setting to disable the shutdown hook for test cases: enabled by default */
    private boolean enableShutdownHook = true;
	/** setting to disable embedded HTTP web service: enabled by default */
	private boolean enableHttpService = true;

    private TriplesecInstallationLayout layout;
    private Thread workerThread = null;
    private SynchWorker worker = new SynchWorker();
    private boolean startNoWait = false;
    
    
    /**
     * Sets the override port for the HTTP server.  If this value is set then the value is
     * used for the HTTP server port instead of what is set within the configuration.
     *
     * @param httpPortOverride the HTTP port to use
     */
    public void setHttpPortOverride( int httpPortOverride )
    {
        this.httpPortOverride = httpPortOverride;
    }


    /**
     * Sets the override port for the LDAP server.  If this value is set then the value is
     * used for the LDAP server port instead of what is set within the configuration.
     *
     * @param ldapPortOverride the LDAP port to use
     */
    public void setLdapPortOverride( int ldapPortOverride )
    {
        this.ldapPortOverride = ldapPortOverride;
    }


    /**
     * Sets the override port for the LDAPS server.  If this value is set then the value is
     * used for the LDAPS server port instead of what is set within the configuration.
     *
     * @param ldapsPortOverride the LDAP port to use
     */
    public void setLdapsPortOverride( int ldapsPortOverride )
    {
        this.ldapsPortOverride = ldapsPortOverride;
    }


    /**
     * Sets the override port for the Kerberos KDC.  If this value is set then the value is
     * used for the KDC server port instead of what is set within the configuration.
     *
     * @param krb5PortOverride the Kerberos port to use
     */
    public void setKrb5PortOverride( int krb5PortOverride )
    {
        this.krb5PortOverride = krb5PortOverride;
    }


    /**
     * Sets the override port for the Changepw Server.  If this value is set then the value is
     * used for the Changepw server port instead of what is set within the configuration.
     *
     * @param changepwPortOverride the Changepw port to use
     */
    public void setChangepwPortOverride( int changepwPortOverride )
    {
        this.changepwPortOverride = changepwPortOverride;
    }


    /**
     * Sets the override port for the NTP server.  If this value is set then the value is
     * used for the NTP server port instead of what is set within the configuration.
     *
     * @param ntpPortOverride the NTP port to use
     */
    public void setNtpPortOverride( int ntpPortOverride )
    {
        this.ntpPortOverride = ntpPortOverride;
    }

    
    private void configure( TriplesecInstallationLayout layout ) throws Exception
    {
        File configWebapp = new File( layout.getWebappsDirectory(), "config" );
        File configured = new File( configWebapp, "configured" );
        
        // if the configuration application is not present then don't bother
        if ( ! configWebapp.exists() )
        {
            return;
        }

        // if config app is present along with configured file inside then we exit too
        if ( configured.exists() )
        {
            return;
        }

        // below here the server has not been configured and has the config app
        httpService = new Server();
        SocketListener listener = new SocketListener();
        listener.setPort( Integer.parseInt( HTTP_PORT_DEFAULT ) );
        httpService.addListener( listener );
        httpService.addWebApplication( "config", configWebapp.getCanonicalPath() );
        httpService.start();

        // warn to let user know server is waiting and to make sure user sees the message
        if ( log.isWarnEnabled() )
        {
            log.warn( "Waiting for admin to configure the server on port " + HTTP_PORT_DEFAULT );
        }
        
        // waits until the webapp places a 'configured' file after the user completes configuration
        while ( ! configured.exists() )
        {
            // check every second and a half for the directory's presence
            Thread.sleep( 1500 );
        }
        
        if ( log.isInfoEnabled() )
        {
            log.info( "Configuration stage completed ... starting normal initialization" );
        }

        httpService.stop();
        httpService.destroy();
        httpService = null;
    }
    

    public void init( InstallationLayout installationLayout, String[] args ) throws Exception
    {
        MutableTriplesecStartupConfiguration cfg;

        log.debug( "init(InstallationLayout,String[]) called" );
        
        if ( ! ( installationLayout instanceof TriplesecInstallationLayout ) )
        {
            layout = new TriplesecInstallationLayout ( installationLayout.getBaseDirectory() );
        }
        else
        {
            layout = ( TriplesecInstallationLayout ) installationLayout;
        }

        configure( layout );
        startTime = System.currentTimeMillis();
        
        if ( layout != null )
        {
            log.info( "server: loading settings from ", layout.getConfigurationFile() );
            ApplicationContext factory = null;
            factory = new FileSystemXmlApplicationContext( layout.getConfigurationFile().toURL().toString() );
            cfg = ( MutableTriplesecStartupConfiguration ) factory.getBean( "configuration" );
            env = ( Properties ) factory.getBean( "environment" );
        }
        else if ( args.length > 0 && new File( args[0] ).exists() ) // hack that takes server.xml file argument
        {
            log.info( "server: loading settings from ", args[0] );
            ApplicationContext factory = null;
            factory = new FileSystemXmlApplicationContext( new File( args[0] ).toURL().toString() );
            cfg = ( MutableTriplesecStartupConfiguration ) factory.getBean( "configuration" );
            env = ( Properties ) factory.getBean( "environment" );
        }
        else
        {
            throw new Exception( "Can't figure out where to fine my installation." ); 
        }

        cfg.setShutdownHookEnabled( enableShutdownHook );
		cfg.setEnableHttp( enableHttpService );
        cfg.setLdifDirectory( layout.getConfigurationDirectory().getAbsoluteFile() );

        if ( httpPortOverride != UNDEFINED_PORT_OVERRIDE )
        {
            cfg.setHttpPort( httpPortOverride );
        }

        if ( ldapPortOverride != UNDEFINED_PORT_OVERRIDE )
        {
            cfg.setLdapPort( ldapPortOverride );
        }

        if ( ldapsPortOverride != UNDEFINED_PORT_OVERRIDE )
        {
            cfg.setLdapsPort( ldapsPortOverride );
        }

        if ( layout != null )
        {
            cfg.setWorkingDirectory( layout.getPartitionsDirectory() );
        }

        env.setProperty( Context.PROVIDER_URL, "ou=system" );
        env.setProperty( Context.INITIAL_CONTEXT_FACTORY, TriplsecContextFactory.class.getName() );
        env.putAll( cfg.toJndiEnvironment() );

        if ( krb5PortOverride != UNDEFINED_PORT_OVERRIDE )
        {
            cfg.setEnableKerberos( true );
            env.put( "kdc.ipPort", Integer.toString( krb5PortOverride ) );
            env.put( "kdc.ipPort", Integer.toString( krb5PortOverride ) );
        }

        if ( changepwPortOverride != UNDEFINED_PORT_OVERRIDE )
        {
            cfg.setEnableChangePassword( true );
            env.put( "changepw.ipPort", Integer.toString( changepwPortOverride ) );
            env.put( "changepw.ipPort", Integer.toString( changepwPortOverride ) );
        }

        if ( ntpPortOverride != UNDEFINED_PORT_OVERRIDE )
        {
            cfg.setEnableNtp( true );
            env.put( "ntp.ipPort", Integer.toString( ntpPortOverride ) );
            env.put( "ntp.ipPort", Integer.toString( ntpPortOverride ) );
        }

        // -------------------------------------------------------------------
        // Get and/or create the userContext where profiles are subordinates
        // -------------------------------------------------------------------

        DirContext userContext = null;
        try
        {
            LdapDN dn = new LdapDN( env.getProperty( "safehaus.entry.basedn" ) );
            dn.remove( dn.size() - 1 );
            env.setProperty( Context.PROVIDER_URL, dn.toString() );
            userContext = new InitialDirContext( env );
        }
        catch ( NamingException e )
        {
            e.printStackTrace();
            System.exit( -5 );
        }

        // set the user context for the sam subsystem
        SamSubsystem.getInstance().setUserContext( userContext, "ou=users" );

        // setup demo profiles
        try
        {
            if ( PropertiesUtils.get( env, "safehaus.load.testdata", true ) )
            {
                ServerProfileStore store;
                store = new DefaultServerProfileStore( ( DirContext ) userContext.lookup( "ou=Users" ) );
                addDemoProfiles( store, env.getProperty( "kdc.primary.realm" ) );
            }
        }
        catch ( NamingException e )
        {
            e.printStackTrace();
            System.exit( -7 );
        }
        
        try
        {
            if ( cfg.isEnableHttp() )
            {
                setupHttpService( cfg.getHttpPort() );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( -8 );
        }

        workerThread = new Thread( worker, "SynchWorkerThread" );

        if ( log.isInfoEnabled() )
        {
            log.info( "server: started in {} milliseconds", ( System.currentTimeMillis() - startTime ) + "" );
        }
    }
    
    
    public void setupHttpService( int port ) throws Exception
    {
        httpService = new Server();
        httpService.setStatsOn( true );
        httpService.setTrace( true );
        
        log.warn( "Setting trace and stats to on for Jetty" );
        
        SocketListener listener = new SocketListener();
        listener.setPort( port );
        httpService.addListener( listener );
        
        File webappsDir = new File( layout.getBaseDirectory(), "webapps" );
        File[] webapps = webappsDir.listFiles( new FileFilter() {
            public boolean accept( File dir ) { return dir.isDirectory(); }
        } );
        
        if ( webapps == null )
        {
            return;
        }
        
        for ( int ii = 0; ii < webapps.length; ii++ )
        {
            WebApplicationContext appContext = null;
            
            if ( webapps[ii].getName().equals( "ROOT" ) )
            {
                appContext = httpService.addWebApplication( "/", webapps[ii].getCanonicalPath() );
            }
            else if ( webapps[ii].getName().equals( "config" ) )
            {
            	continue;
            }
            else
            {
                appContext = httpService.addWebApplication( webapps[ii].getName(), webapps[ii].getCanonicalPath() );
            }
            
            appContext.setExtractWAR( true );
            appContext.setMimeMapping( "jar", "application/java-archive" );
            appContext.setMimeMapping( "jad", "text/vnd.sun.j2me.app-descriptor" );
            appContext.setMimeMapping( "cod", "application/vnd.rim.cod"  );
            appContext.setMimeMapping( "wml", "text/vnd.wap.wml" );
            appContext.setMimeMapping( "wmls", "text/vnd.wap.wmlscript"  );
            appContext.setMimeMapping( "wbxml", "application/vnd.wap.wbxml"  );
            appContext.setMimeMapping( "wmlc", "application/vnd.wap.wmlc" );
            appContext.setMimeMapping( "wmlsc", "application/vnd.wap.wmlscriptc" );
        }
        
        //httpService.start();
    }


    public void destroy()
    {
        log.debug( "destroy() called" );
    }


    public void start()
    {
        try
        {
			if ( httpService != null )
			{
				httpService.start();
			}
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( -8 );
        }
        workerThread.start();
        return;
    }


    public void stop( String[] args ) throws Exception
    {

        try
        {
			if ( httpService != null )
			{
				httpService.stop();
				httpService.destroy();
				httpService = null;
			}
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
		worker.stop = true;
        synchronized ( worker.lock )
        {
            worker.lock.notify();
        }

        while ( startNoWait && workerThread.isAlive() )
        {
            log.info( "Waiting for SynchWorkerThread to die." );
            workerThread.join( 500 );
        }

        env.putAll( new ShutdownConfiguration().toJndiEnvironment() );
        new InitialDirContext( env );
    }


    /**
     * Adds profile test/demo data which is also setup on the client
     * application.
     *
     * @param store the store to add the test data to
     * @throws NamingException on failures while adding test profiles
     */
    private static void addDemoProfiles( ServerProfileStore store, String realm ) throws NamingException
    {
        for ( int ii = 0; ii < ProfileTestData.PROFILES.length; ii++ )
        {
            ServerProfile profile = ProfileTestData.PROFILES[ii];
            BaseServerProfileModifier modifier = new BaseServerProfileModifier();
            modifier.setActivationKey( profile.getActivationKey() );
            modifier.setFactor( profile.getFactor() );
            modifier.setFailuresInEpoch( profile.getFailuresInEpoch() );
            modifier.setInfo( profile.getInfo() );
            modifier.setLabel( profile.getLabel() );
            modifier.setPassword( profile.getPassword() );
            modifier.setRealm( realm );
            modifier.setResynchCount( profile.getResynchCount() );
            modifier.setSecret( profile.getSecret() );
            modifier.setTokenPin( profile.getTokenPin() );
            modifier.setUserId( profile.getUserId() );
            profile = modifier.getServerProfile();
            String name = profile.getUserId() + "@" + realm;
            KerberosPrincipal principal = new KerberosPrincipal( name );

            if ( ! store.hasProfile( principal ) )
            {
                store.add( profile );
            }
        }
    }


    public void synch() throws Exception
    {
        env.putAll( new SyncConfiguration().toJndiEnvironment() );
        new InitialDirContext( env );
    }


    class SynchWorker implements Runnable
    {
        Object lock = new Object();
        boolean stop = false;


        public void run()
        {
            while ( !stop )
            {
                synchronized ( lock )
                {
                    try
                    {
                        lock.wait( SYNCPERIOD_MILLIS );
                    }
                    catch ( InterruptedException e )
                    {
                        log.warn( "SynchWorker failed to wait on lock.", e );
                    }
                }

                try
                {
                    synch();
                }
                catch ( Exception e )
                {
                    log.error( "SynchWorker failed to synch directory.", e );
                }
            }
        }
    }

    public static final String BANNER = "           _                     _          ____  ____   \n"
        + "          / \\   _ __   __ _  ___| |__   ___|  _ \\/ ___|  \n"
        + "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ | | \\___ \\   \n"
        + "        / ___ \\| |_) | (_| | (__| | | |  __/ |_| |___) |  \n"
        + "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|____/|____/   \n"
        + "               |_|                                                               \n";


    public static void printBanner()
    {
        System.out.println( BANNER );
    }


    public void setEnableShutdownHook( boolean enableShutdownHook )
    {
        this.enableShutdownHook = enableShutdownHook;
    }


    public boolean isEnableShutdownHook()
    {
        return enableShutdownHook;
    }

	public void setEnableHttpService( boolean enableHttpService )
	{
		this.enableHttpService = enableHttpService;
	}

	public boolean isEnableHttpService()
	{
		return enableHttpService;
	}
}
