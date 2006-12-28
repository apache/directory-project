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
package org.safehaus.triplesec.integration;


import junit.framework.TestCase;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.mina.util.AvailablePortFinder;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.safehaus.triplesec.TriplesecInstallationLayout;
import org.safehaus.triplesec.Service;


/**
 * A unit test case base class for implementing Triplesec Unit tests.  To
 * use TriplesecUnit properly you must set two System properties.
 * TrilpesecUnit builds a minimal install footprint to start up Triplesec
 * and needs to know where this home directory should be created along with
 * where to find configuration files which it copies over to the home
 * directory.
 *
 * Generally one can create a src/test/resource directory for a project and
 * point the org.safehaus.triplesec.unit.resource.dir property to the absolute
 * path for this directory.  Absolute paths must be used, however with Maven
 * the ${basedir} macro can be used in the project.properties file to set this
 * path in a position independent fashion.  Within an IDE the test run must
 * have the absolute path specified as a jvm -D parameter before running the
 * test.  Within this directory you put the server.xml file that is needed
 * as well as any files ending in the ldif extention to be imported on server
 * startup.  An optional log4j.properties can be put here as well however
 * for Maven to use it the log4j.properties must be explicity specified within
 * the resources section of the project.xml to be copied into the test-classes
 * directory on test runs.
 *
 * At the present point in time the unit test must be run as root otherwise
 * Triplesec cannot bind to the Kerberos port.  We will make this configurable
 * so we can search for a free bindable port so tests can pass as non-root
 * users.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecIntegration extends TestCase
{
    /** the resources property to set */
    public static final String RESOURCES_DIRECTORY = "org.safehaus.triplesec.integration.resourcesDirectory";
    /** a comma delimited list of web applications */
    public static final String WEBAPPS_PROPERTY = "org.safehaus.triplesec.integration.webapps";
    /** the base property for a the path to a webapp war */
    public static final String WEBAPPWARBASE_PROPERTY = "org.safehaus.triplesec.integration.webappWarBase";
    /** the SLF4J logger for this class */
    private static final Logger log = LoggerFactory.getLogger( TriplesecIntegration.class );

    /** a handle on the server's newly created "install" home directory */
    private File serverHome;
    /**
     * The configuration directory is where TriplesecUnit searches for configuration 
     * files to copy into the server home directory it will create.  Within this 
     * directory where ever it may be, must reside a server.xml configuration file 
     * for Triplesec Server.  Optional LDIF files can also be placed here for import 
     * when the server starts up.
     */
    private File resourcesDirectory;
    private Service server;
    private int httpPort;
    private int ldapPort;
    private int ldapsPort;
    private int krb5Port;
    private int changepwPort;
    private int ntpPort;


    /**
     * Creates a unit test case that sets up and runs the Triplesec server
     * for each unit test with a fresh database.
     *
     * @param resourcesDirectory the directory containing triplesec resource files
     * @throws Exception if there are configuration issues with the unit test
     */
    public TriplesecIntegration() throws Exception
    {
        init();
    }


    /**
     * Creates a unit test case that sets up and runs the Triplesec server
     * for each unit test with a fresh database.
     *
     * @param resourcesDirectory the directory containing triplesec resource files
     * @param string the name of the unit test
     * @throws Exception if there are configuration issues with the unit test
     */
    public TriplesecIntegration( String string ) throws Exception
    {
        super( string );
        init();
    }


    /**
     * Get's the LDAP port the server starts on.
     *
     * @return the LDAP port the server starts on
     */
    protected int getLdapPort()
    {
        return ldapPort;
    }


    /**
     * Get's the HTTP port the server starts on.
     *
     * @return the HTTP port the server starts on
     */
    protected int getHttpPort()
    {
        return httpPort;
    }


    /**
     * Get's the LDAPS port the server starts on.
     *
     * @return the LDAPS port the server starts on
     */
    protected int getLdapsPort()
    {
        return ldapsPort;
    }


    /**
     * Gets the Kerberos port the server binds to.
     *
     * @return the Kerberos port the server binds to.
     */
    protected int getKerberosPort()
    {
        return krb5Port;
    }


    /**
     * Gets the Changepw port the server binds to.
     *
     * @return the Changepw port the server binds to.
     */
    protected int getChangepwPort()
    {
        return changepwPort;
    }


    /**
     * Gets the Ntp port the server binds to.
     *
     * @return the Ntp port the server binds to.
     */
    protected int getNtpPort()
    {
        return ntpPort;
    }


    /**
     * Gets the file for the server home which is the directory containing the
     * minimal install footprint for the Triplesec Server.
     *
     * @return the server home
     */
    protected File getServerHome()
    {
        return serverHome;
    }


    /**
     * Gets a handle to the directory containing the configuration resources
     * that were copied over to the server home directory.
     *
     * @return the handle to the configuration resources directory
     */
    protected File getConfigResourcesDir()
    {
        return resourcesDirectory;
    }
    
    
    private void initWebapps( File webappsDirectory ) throws IOException
    {
        String value = System.getProperties().getProperty( WEBAPPS_PROPERTY, null );
        if ( value == null )
        {
            return;
        }
        
        String[] webapps = value.split( "," );
        for ( int ii = 0; ii < webapps.length; ii++ )
        {
            String warPath = System.getProperties().getProperty( WEBAPPWARBASE_PROPERTY + "." + webapps[ii], null );
            if ( warPath == null )
            {
                log.warn( "Abandoning webapplication. No path for webapp's war: " + webapps[ii] );
                continue;
            }
            
            if ( ! new File( warPath ).exists() )
            {
                log.warn( "War for webapp " + webapps[ii] + " does not exist for path: " + warPath 
                    + ".\nCheck that the war is present or rebuild that webapp module." );
                continue;
            }
            
            File explodedDirectory = new File( webappsDirectory, webapps[ii] );
            explodedDirectory.mkdirs();
            try
            {
                explodeWar( explodedDirectory, new File( warPath ) );
            }
            catch ( IOException e )
            {
                log.error( "Failed to explode war at " + warPath, e );
            }
        }
    }


    private void explodeWar( File explodedDirectory, File warFile ) throws IOException
    {
        log.info( "Exploding war " + warFile.getAbsolutePath() + " into directory " 
            + explodedDirectory.getAbsolutePath()  );
        Expand expand = new Expand();
        expand.setProject( new Project() );
        expand.setSrc( warFile );
        expand.setOverwrite( true );
        expand.setDest( explodedDirectory );
        expand.execute();
    }


    /**
     * Initializes TriplesecUnit once (not on every test run) by cleaning out
     * the old server home if it exists and building it once again using the
     * configuration files present within the configuration resources directory.
     * This method does not start the server.  The server is started with a
     * fresh new database for each run within the setUp() method.
     *
     * @throws Exception if there are problems with the T-unit configuration
     */
    private void init() throws Exception
    {
        this.resourcesDirectory = new File ( System.getProperties().getProperty( 
            RESOURCES_DIRECTORY, "src/test/resources" ) );

        if ( ! resourcesDirectory.exists() )
        {
            String msg = "The configuration resources directory '" +
                    resourcesDirectory + "' does not exist will search classpath for resources";
            fail( msg );
        }

        // --------------------------------------------------------------------
        // Setup the serverHome directory
        // --------------------------------------------------------------------

        File targetDirectory = new File( "target" );
        if ( ! targetDirectory.exists() )
        {
            targetDirectory.mkdirs();
        }
        
        serverHome = new File( targetDirectory, "serverHome" );
        if ( serverHome.exists() )
        {
            FileUtils.deleteDirectory( serverHome );
        }

        // --------------------------------------------------------------------
        // Create minimal install footprint with configuration files
        // --------------------------------------------------------------------

        serverHome.mkdirs();
        File logsDir = new File( serverHome, "logs" );
        logsDir.mkdir();
        File confDir = new File( serverHome, "conf" );
        confDir.mkdir();
        File webappsDir = new File( serverHome, "webapps" );
        webappsDir.mkdir();
        initWebapps( webappsDir );
        
        FileUtils.copyFileToDirectory( new File( resourcesDirectory, "server.xml" ), confDir );
        File [] ldifFiles = resourcesDirectory.listFiles( new FileFilter()
        {
            public boolean accept( File file )
            {
                return file.getName().endsWith( "ldif" );
            }
        });
        for ( int ii = 0; ii < ldifFiles.length; ii++ )
        {
            FileUtils.copyFileToDirectory( ldifFiles[ii], confDir );
        }
    }


    protected void setUp() throws Exception
    {
        super.setUp();

        // must delete the contents under the partitions directory
        File partitionDir = new File( serverHome, "var" );
        partitionDir = new File( partitionDir, "partitions" );
        if ( partitionDir.exists() )
        {
            FileUtils.forceDelete( partitionDir );
        }
        partitionDir.mkdir();

        server = new Service();
        server.setEnableShutdownHook( false );
        
        if ( ! AvailablePortFinder.available( 88 ) )
        {
            krb5Port = AvailablePortFinder.getNextAvailable( 1088 );
            server.setKrb5PortOverride( krb5Port );
        }
        else
        {
            krb5Port = 88;
        }

        if ( ! AvailablePortFinder.available( 464 ) )
        {
            changepwPort = AvailablePortFinder.getNextAvailable( 1464 );
            server.setChangepwPortOverride( changepwPort );
        }
        else
        {
            changepwPort = 464;
        }

        if ( ! AvailablePortFinder.available( 123 ) )
        {
            ntpPort = AvailablePortFinder.getNextAvailable( 1123 );
            server.setNtpPortOverride( ntpPort );
        }
        else
        {
            ntpPort = 123;
        }

        if ( ! AvailablePortFinder.available( 10389 ) )
        {
            ldapPort = AvailablePortFinder.getNextAvailable( 10389 );
            server.setLdapPortOverride( ldapPort );
        }
        else
        {
            ldapPort = 10389;
        }

        if ( ! AvailablePortFinder.available( 8383 ) )
        {
            httpPort = AvailablePortFinder.getNextAvailable( 8383 );
            server.setHttpPortOverride( httpPort );
        }
        else
        {
            httpPort = 8383;
        }

        if ( ! AvailablePortFinder.available( 10636 ) )
        {
            ldapsPort = AvailablePortFinder.getNextAvailable( 10636 );
            server.setLdapsPortOverride( ldapsPort );
        }
        else
        {
            ldapsPort = 10636;
        }

        String home = serverHome.getCanonicalPath();
        log.debug( "server home used = " + home );
        server.init( new TriplesecInstallationLayout( serverHome ), new String[] { home, "start" } );
        log.info( "initialized Triplesec Server" );
        server.start();
        log.info( "started Triplesec Server" );
    }


    protected void tearDown() throws Exception
    {
        server.stop( null );
        server.destroy();
        server = null;
        super.tearDown();
    }
}
