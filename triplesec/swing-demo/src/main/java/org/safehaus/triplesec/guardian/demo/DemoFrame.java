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
package org.safehaus.triplesec.guardian.demo;


import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.security.Permission;

import javax.security.auth.login.LoginException;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;
import org.safehaus.triplesec.guardian.ChangeType;
import org.safehaus.triplesec.guardian.StringPermission;
import org.safehaus.triplesec.guardian.PolicyChangeListener;
import org.safehaus.triplesec.guardian.Profile;
import org.safehaus.triplesec.guardian.Role;


public class DemoFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static final String CONNECTION_URL_KEY = "connectionUrl";
    private static final String CREDENTIALS_KEY = "applicationCredentials";
    private static final String PRINCIPALDN_KEY = "applicationPrincipalDn";
    private static final String DRIVER_KEY = "driver";
    private static final String REALM_KEY = "realm";

    private JPanel jContentPane = null;
    private JScrollPane scrollPane = null;
    private JMenuBar jJMenuBar = null;
    private JMenu fileMenu = null;
    private JMenuItem closeMenuItem = null;
    private JMenuItem switchUserMenuItem = null;
    private JMenu operationsMenu = null;
    private JMenuItem bendMenuItem = null;
    private JMenuItem foldMenuItem = null;
    private JMenuItem mutilateMenuItem = null;
    private JMenuItem spindleMenuItem = null;
    private JMenuItem twistMenuItem = null;
    private JTextPane jTextPane = null;

    static ApplicationPolicy policy = null;
    static String driver = "org.safehaus.triplesec.guardian.ldap.LdapConnectionDriver";
    static String connectionUrl = "ldap://localhost:10389/dc=example,dc=com";
    static String applicationPrincipalDn = "appname=demo,ou=Applications,dc=example,dc=com";
    static String applicationCredentials = "secret";
    static String realm = "example.com";
    static Profile currentProfile = null;


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JScrollPane getScrollPane()
    {
        if ( scrollPane == null )
        {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView( getJTextPane() );
        }
        return scrollPane;
    }


    /**
     * This method initializes jJMenuBar	
     * 	
     * @return javax.swing.JMenuBar	
     */
    private JMenuBar getJJMenuBar()
    {
        if ( jJMenuBar == null )
        {
            jJMenuBar = new JMenuBar();
            jJMenuBar.add( getFileMenu() );
            jJMenuBar.add( getOperationsMenu() );
        }
        return jJMenuBar;
    }


    /**
     * This method initializes jMenu	
     * 	
     * @return javax.swing.JMenu	
     */
    private JMenu getFileMenu()
    {
        if ( fileMenu == null )
        {
            fileMenu = new JMenu();
            fileMenu.setText( "File" );
            fileMenu.add( getCloseMenuItem() );
            fileMenu.add( getSwitchUserMenuItem() );
        }
        return fileMenu;
    }


    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getCloseMenuItem()
    {
        if ( closeMenuItem == null )
        {
            closeMenuItem = new JMenuItem();
            closeMenuItem.setText( "close" );
            closeMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed(close)" );
                    DemoFrame.this.setVisible( false );
                    DemoFrame.this.dispose();
                    System.exit( 0 );
                }
            } );
        }
        return closeMenuItem;
    }


    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getSwitchUserMenuItem()
    {
        if ( switchUserMenuItem == null )
        {
            switchUserMenuItem = new JMenuItem();
            switchUserMenuItem.setText( "switch user" );
            switchUserMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed(switch user)" );
                    boolean userLoggedIn = login( false );

                    if ( userLoggedIn )
                    {
                        resetMenus( currentProfile );
                    }
                }
            } );
        }
        return switchUserMenuItem;
    }


    /**
     * This method initializes jMenu	
     * 	
     * @return javax.swing.JMenu	
     */
    private JMenu getOperationsMenu()
    {
        if ( operationsMenu == null )
        {
            operationsMenu = new JMenu();
            operationsMenu.setText( "Operations" );

            if ( currentProfile.implies( makePermission("bend" )) )
            {
                System.out.println( "enabling bend" );
                operationsMenu.add( getBendMenuItem() );
            }

            if ( currentProfile.implies( makePermission( "fold" )) )
            {
                System.out.println( "enabling fold" );
                operationsMenu.add( getFoldMenuItem() );
            }

            if ( currentProfile.implies( makePermission( "mutilate" )) )
            {
                System.out.println( "enabling mutilate" );
                operationsMenu.add( getMutilateMenuItem() );
            }

            if ( currentProfile.implies( makePermission( "spindle" )) )
            {
                System.out.println( "enabling spindle" );
                operationsMenu.add( getSpindleMenuItem() );
            }

            if ( currentProfile.implies( makePermission( "twist" )) )
            {
                System.out.println( "enabling twist" );
                operationsMenu.add( getTwistMenuItem() );
            }
        }
        return operationsMenu;
    }

    private Permission makePermission(String s) {
        return new StringPermission(s);
    }


    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getBendMenuItem()
    {
        if ( bendMenuItem == null )
        {
            bendMenuItem = new JMenuItem();
            bendMenuItem.setText( "bend" );
            bendMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed(bend)" );
                    String appended = jTextPane.getText() + "\tbend\t==>\t" + new Date() + "\n";
                    jTextPane.setText( appended );
                }
            } );
        }
        return bendMenuItem;
    }


    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getFoldMenuItem()
    {
        if ( foldMenuItem == null )
        {
            foldMenuItem = new JMenuItem();
            foldMenuItem.setText( "fold" );
            foldMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed(fold)" );
                    String appended = jTextPane.getText() + "\tfold\t==>\t" + new Date() + "\n";
                    jTextPane.setText( appended );
                }
            } );
        }
        return foldMenuItem;
    }


    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getMutilateMenuItem()
    {
        if ( mutilateMenuItem == null )
        {
            mutilateMenuItem = new JMenuItem();
            mutilateMenuItem.setText( "mutilate" );
            mutilateMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed(mutilate)" );
                    String appended = jTextPane.getText() + "\tmutilate\t==>\t" + new Date() + "\n";
                    jTextPane.setText( appended );
                }
            } );
        }
        return mutilateMenuItem;
    }


    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getSpindleMenuItem()
    {
        if ( spindleMenuItem == null )
        {
            spindleMenuItem = new JMenuItem();
            spindleMenuItem.setText( "spindle" );
            spindleMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed(spindle)" );
                    String appended = jTextPane.getText() + "\tspindle\t==>\t" + new Date() + "\n";
                    jTextPane.setText( appended );
                }
            } );
        }
        return spindleMenuItem;
    }


    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getTwistMenuItem()
    {
        if ( twistMenuItem == null )
        {
            twistMenuItem = new JMenuItem();
            twistMenuItem.setText( "twist" );
            twistMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed(twist)" );
                    String appended = jTextPane.getText() + "\ttwist\t==>\t" + new Date() + "\n";
                    jTextPane.setText( appended );
                }
            } );
        }
        return twistMenuItem;
    }


    /**
     * This method initializes jTextPane	
     * 	
     * @return javax.swing.JTextPane	
     */
    private JTextPane getJTextPane()
    {
        if ( jTextPane == null )
        {
            jTextPane = new JTextPane();
            jTextPane.setText( "\n\nOperations Performed:\n\n" );
            jTextPane.setEditable( false );
        }
        return jTextPane;
    }


    public static void main( String[] args ) throws Exception
    {
        // find the properties file or use defaults
        Properties properties = new Properties();
        if ( args.length > 0 )
        {
            File configurationFile = new File( args[0] );
            if ( configurationFile.exists() )
            {
                properties.load( new FileInputStream( configurationFile ) );
                extractConnectionParameters( properties );
            }
            else
            {
                System.err.println( "no such file: " + configurationFile );
                printUsage();
                System.exit( 1 );
            }
        }
        else if ( System.getProperty( "config.properties" ) != null )
        {
            File configurationFile = new File( args[0] );
            if ( configurationFile.exists() )
            {
                properties.load( new FileInputStream( configurationFile ) );
                extractConnectionParameters( properties );
            }
            else
            {
                System.err.println( "no such file: " + configurationFile );
                printUsage();
                System.exit( 1 );
            }
        }
    
        // initialize the driver and load the application's base policy from the store
        Properties driverProps = new Properties();
        driverProps.setProperty( "applicationPrincipalDN", applicationPrincipalDn );
        driverProps.setProperty( "applicationCredentials", applicationCredentials );
        Class.forName( driver );
        policy = ApplicationPolicyFactory.newInstance( connectionUrl, driverProps );
        login( true );
        DemoFrame demoFrame = new DemoFrame();
        demoFrame.setVisible( true );
    }


    static boolean login( boolean doExit )
    {
        List profileIdList = new ArrayList();
        for ( Iterator ii = policy.getProfileIdIterator(); ii.hasNext(); /**/ )
        {
            profileIdList.add( ii.next() );
        }
        String[] profileStrings = new String[profileIdList.size()];
        profileStrings = ( String[] ) profileIdList.toArray( profileStrings );
        LoginDialog loginDialog = new LoginDialog( profileStrings );
        loginDialog.setVisible( true );
        if ( loginDialog.isLoginSelected() )
        {
            String password = loginDialog.getPassword();
            String profileId = loginDialog.getSelectedProfile();
            String passcode = loginDialog.getPasscode();
    
            System.out.println( "password = " + password );
            System.out.println( "passcode = " + password );
            System.out.println( "profile = " + profileId );
    
            boolean isSuccessful = false;
            try
            {
                LoginCommand command = new LoginCommand( profileId, password, realm, passcode, policy );
                isSuccessful = command.execute();
            }
            catch ( LoginException e )
            {
                e.printStackTrace();
            }
            
            if ( !isSuccessful )
            {
                System.out.println( "Authentication failed for user profile: " + profileId );
                loginDialog.dispose();
                if ( doExit )
                {
                    System.exit( 1 );
                }
                return false;
            }
            else
            {
                loginDialog.dispose();
                currentProfile = policy.getProfile( profileId );
                System.out.println( "got profile: " + currentProfile );
                return true;
            }
        }
        return false;
    }


    private static void extractConnectionParameters( Properties p ) throws Exception
    {
        if ( p.containsKey( CONNECTION_URL_KEY ) && p.getProperty( CONNECTION_URL_KEY ) != null )
        {
            connectionUrl = p.getProperty( CONNECTION_URL_KEY );
        }

        if ( p.containsKey( REALM_KEY ) && p.getProperty( REALM_KEY ) != null )
        {
            realm = p.getProperty( REALM_KEY );
        }

        if ( p.containsKey( CREDENTIALS_KEY ) && p.getProperty( CREDENTIALS_KEY ) != null )
        {
            applicationCredentials = p.getProperty( CREDENTIALS_KEY );
        }

        if ( p.containsKey( DRIVER_KEY ) && p.getProperty( DRIVER_KEY ) != null )
        {
            driver = p.getProperty( DRIVER_KEY );
        }

        if ( p.containsKey( PRINCIPALDN_KEY ) && p.getProperty( PRINCIPALDN_KEY ) != null )
        {
            applicationPrincipalDn = p.getProperty( PRINCIPALDN_KEY );
        }
    }


    private static void printUsage()
    {
        System.out.println( "Usage: java -jar guardian-demo-${version} path-to-config-properties or\n" );
        System.out.println( "       java -Dconfig.properties=path-to-config-properties -jar guardian-demo-${version}" );
    }


    /**
     * This is the default constructor
     */
    public DemoFrame()
    {
        super();
        initialize();
        policy.addPolicyListener( new DemoListener() );
    }


    private void resetMenus( Profile currentProfile )
    {
        setTitle( "Triplesec Guardian Demo - " + currentProfile.getProfileId() );
        operationsMenu.removeAll();
        if ( currentProfile.implies( makePermission( "bend" )) )
        {
            System.out.println( "enabling bend" );
            operationsMenu.add( getBendMenuItem() );
        }

        if ( currentProfile.implies( makePermission( "fold" )) )
        {
            System.out.println( "enabling fold" );
            operationsMenu.add( getFoldMenuItem() );
        }

        if ( currentProfile.implies( makePermission( "mutilate" )) )
        {
            System.out.println( "enabling mutilate" );
            operationsMenu.add( getMutilateMenuItem() );
        }

        if ( currentProfile.implies( makePermission( "spindle" )) )
        {
            System.out.println( "enabling spindle" );
            operationsMenu.add( getSpindleMenuItem() );
        }

        if ( currentProfile.implies( makePermission( "twist" )) )
        {
            System.out.println( "enabling twist" );
            operationsMenu.add( getTwistMenuItem() );
        }

        repaint();
    }
    

    class DemoListener implements PolicyChangeListener
    {
        public void roleChanged( ApplicationPolicy policy, Role role, ChangeType changeType )
        {
            System.out.println( "role changed: " + role );

            if ( currentProfile.isInRole( role.getName() ) )
            {
                currentProfile = policy.getProfile( currentProfile.getProfileId() );
                resetMenus( currentProfile );
            }
        }
        
        public void profileChanged( ApplicationPolicy policy, Profile profile, ChangeType changeType )
        {
            if ( currentProfile.equals( profile ) )
            {
                resetMenus( profile );
            }
        }

        public void roleRenamed( ApplicationPolicy policy, Role role, String oldName ) {}
        public void permissionChanged( ApplicationPolicy policy, StringPermission permission, ChangeType changeType ) {}
        public void permissionRenamed( ApplicationPolicy policy, StringPermission permission, String oldName ){}
        public void profileRenamed( ApplicationPolicy policy, Profile profile, String oldName ){}
    }


    /**
     * This method initializes this
     * 
     */
    private void initialize()
    {
        this.setSize( 674, 384 );
        this.setJMenuBar( getJJMenuBar() );
        this.setContentPane( getJContentPane() );
        this.setTitle( "Triplesec Guardian Demo - " + currentProfile.getProfileId() );
        this.addWindowListener( new java.awt.event.WindowAdapter()
        {
            public void windowClosing( java.awt.event.WindowEvent e )
            {
                System.out.println( "windowClosing()" ); 
                DemoFrame.this.setVisible( false );
                DemoFrame.this.dispose();
                System.exit( 0 );
            }
        } );
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
        if ( jContentPane == null )
        {
            jContentPane = new JPanel();
            jContentPane.setLayout( new BorderLayout() );
            jContentPane.add( getScrollPane(), java.awt.BorderLayout.CENTER );
        }
        return jContentPane;
    }

} //  @jve:decl-index=0:visual-constraint="10,10"
