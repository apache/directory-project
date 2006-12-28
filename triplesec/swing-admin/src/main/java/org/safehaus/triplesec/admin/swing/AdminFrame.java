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
package org.safehaus.triplesec.admin.swing;


import java.awt.BorderLayout;

import javax.security.auth.login.LoginException;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.CardLayout;

import javax.swing.ImageIcon;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.safehaus.triplesec.TriplesecInstallationLayout;
import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.Group;
import org.safehaus.triplesec.admin.PermissionClass;
import org.safehaus.triplesec.admin.Profile;
import org.safehaus.triplesec.admin.Role;
import org.safehaus.triplesec.admin.User;
import org.safehaus.triplesec.guardian.ApplicationPolicy;
import org.safehaus.triplesec.guardian.ApplicationPolicyFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;


/**
 * The main administration swing UI frame.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class AdminFrame extends JFrame implements TreeSelectionListener
{
    private static final long serialVersionUID = -150826562775754154L;

    private static final Logger log = LoggerFactory.getLogger( AdminFrame.class );

    private JPanel mainPane = null;
    private JMenuBar mainMenuBar = null;
    private JMenu fileMenu = null;
    private JMenuItem openMenuItem = null;
    private JMenuItem closeMenuItem = null;
    private JMenuItem saveMenuItem = null;
    private JMenuItem exitMenuItem = null;
    private JToolBar mainToolBar = null;
    private JButton openToolbarButton = null;
    private JButton closeToolbarButton = null;
    private JButton saveToolbarButton = null;
    private JPanel statusPanel = null;
    private JPanel mainPanel = null;
    private JSplitPane jSplitPane = null;
    private JLabel statusLabel = null;
    private JPanel rightDetailPanel = null;
    private LeftTreeNavigation leftNavigation = null;
    private ConnectionManager connectionManager = null;
    private ConfigurationFileManager configurationFileManager = null;
    private CardLayout rightDetailPanelLayout;
    private TriplesecInstallationLayout layout;
    private JMenuItem connectMenuItem = null;
    private JMenuItem disconnectMenuItem = null;
    private JButton connectButton = null;
    private JButton disconnectButton = null;
    private JMenu connectionMenu = null;
    private JPanel emptyPanel = null;
//    private PermissionPanel permissionPanel = null;
    private RolePanel rolePanel;
    private UserPanel userPanel;
    private ProfilePanel profilePanel;
    private ApplicationPanel applicationPanel;
    private NewApplicationPanel newApplicationPanel;
    private GroupPanel groupPanel;
    private NewGroupPanel newGroupPanel;
    private NewUserPanel newUserPanel;
//    private NewPermissionPanel newPermissionPanel;
    private NewRolePanel newRolePanel;
    private NewProfilePanel newProfilePanel;
    private JMenu settingsMenu = null;
    private JMenuItem editSettingsMenuItem = null;
    private AdminToolSettings settings = null;

    private JMenuItem loadSettingsMenuItem = null;
    

    /**
     * This is the default constructor
     */
    public AdminFrame()
    {
        super();
//        SplashScreen splashScreen = new SplashScreen( "splashscreen.png", this, 500 );
//        splashScreen.setVisible( true );
        initialize();
        applicationPanel.setLeftTreeNavigation( leftNavigation );
        configurationFileManager = new ConfigurationFileManager( this );
        connectionManager = new ConnectionManager();
        connectionManager.addObserver( leftNavigation );
    }

    
    public AdminToolSettings getSettings()
    {
        return settings;
    }
    
    
    public ConnectionManager getConnectionManager()
    {
        return connectionManager;
    }
    
    
    public ConfigurationFileManager getConfigurationFileManager()
    {
        return configurationFileManager;
    }
    

    /**
     * This method initializes this
     * 
     */
    private void initialize()
    {
        this.setSize(940, 560);
        this.setJMenuBar( getMainMenuBar() );
        this.setContentPane( getMainPane() );
        this.setTitle( "Triplesec Administration Tool" );
        this.addWindowListener( new java.awt.event.WindowAdapter()
        {
            public void windowClosing( java.awt.event.WindowEvent e )
            {
                AdminFrame.this.setVisible( false );
                AdminFrame.this.dispose();
                System.exit( 0 );
            }
        } );
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane()
    {
        if ( mainPane == null )
        {
            mainPane = new JPanel();
            mainPane.setLayout( new BorderLayout() );
            mainPane.add( getMainToolBar(), java.awt.BorderLayout.NORTH );
            mainPane.add( getStatusPanel(), java.awt.BorderLayout.SOUTH );
            mainPane.add( getMainPanel(), java.awt.BorderLayout.CENTER );
        }
        return mainPane;
    }


    /**
     * This method initializes jJMenuBar
     * 
     * @return javax.swing.JMenuBar
     */
    private JMenuBar getMainMenuBar()
    {
        if ( mainMenuBar == null )
        {
            mainMenuBar = new JMenuBar();
            mainMenuBar.add( getFileMenu() );
            mainMenuBar.add(getConnectionMenu());
            mainMenuBar.add(getSettingsMenu());
        }
        return mainMenuBar;
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
            fileMenu.setMnemonic( java.awt.event.KeyEvent.VK_F );
            fileMenu.add( getOpenMenuItem() );
            fileMenu.add( getCloseMenuItem() );
            fileMenu.add( getSaveMenuItem() );
            fileMenu.add( getExitMenuItem() );
        }
        return fileMenu;
    }


    /**
     * This method initializes jMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getOpenMenuItem()
    {
        if ( openMenuItem == null )
        {
            openMenuItem = new JMenuItem();
            openMenuItem.setText( "Open" );
            openMenuItem.setMnemonic( java.awt.event.KeyEvent.VK_O );
            openMenuItem.setToolTipText( "Open Triplesec Configuration File" );
            openMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    openActionTaken();
                }
            } );
        }
        return openMenuItem;
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
            closeMenuItem.setText( "Close" );
            closeMenuItem.setMnemonic( java.awt.event.KeyEvent.VK_C );
            closeMenuItem.setToolTipText( "Close Triplesec Configuration File" );
        }
        return closeMenuItem;
    }


    /**
     * This method initializes jMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getSaveMenuItem()
    {
        if ( saveMenuItem == null )
        {
            saveMenuItem = new JMenuItem();
            saveMenuItem.setText( "Save" );
            saveMenuItem.setMnemonic( java.awt.event.KeyEvent.VK_S );
            saveMenuItem.setToolTipText( "Save Triplesec Configuration File" );
        }
        return saveMenuItem;
    }


    /**
     * This method initializes jMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExitMenuItem()
    {
        if ( exitMenuItem == null )
        {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setText( "Exit" );
            exitMenuItem.setMnemonic( java.awt.event.KeyEvent.VK_X );
            exitMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    log.info( "Application exited." );
                    AdminFrame.this.setVisible( false );
                    AdminFrame.this.dispose();
                    System.exit( 0 );
                }
            } );
        }
        return exitMenuItem;
    }


    /**
     * This method initializes jJToolBarBar
     * 
     * @return javax.swing.JToolBar
     */
    private JToolBar getMainToolBar()
    {
        if ( mainToolBar == null )
        {
            mainToolBar = new JToolBar();
            mainToolBar.add( getOpenToolbarButton() );
            mainToolBar.add( getCloseToolbarButton() );
            mainToolBar.add( getSaveToolbarButton() );
            mainToolBar.add( getConnectButton() );
            mainToolBar.add( getDisconnectButton() );
        }
        return mainToolBar;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOpenToolbarButton()
    {
        if ( openToolbarButton == null )
        {
            openToolbarButton = new JButton();
            openToolbarButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/fileopen_22x22.png" ) ) );
            openToolbarButton.setBorder( new EtchedBorder() );
            openToolbarButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    openActionTaken();
                }
            } );
        }
        return openToolbarButton;
    }


    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseToolbarButton()
    {
        if ( closeToolbarButton == null )
        {
            closeToolbarButton = new JButton();
            closeToolbarButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/fileclose_22x22.png" ) ) );
            closeToolbarButton.setBorder( new EtchedBorder() );
        }
        return closeToolbarButton;
    }


    /**
     * This method initializes jButton2
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveToolbarButton()
    {
        if ( saveToolbarButton == null )
        {
            saveToolbarButton = new JButton();
            saveToolbarButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/filesave_22x22.png" ) ) );
            saveToolbarButton.setToolTipText( "Save Triplesec Configuration File" );
            saveToolbarButton.setBorder( new EtchedBorder() );
        }
        return saveToolbarButton;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel()
    {
        if ( statusPanel == null )
        {
            statusLabel = new JLabel();
            statusLabel.setText( " Status: ok" );
            statusLabel.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/status_ok_16x16.png" ) ) );
            statusLabel.setPreferredSize( new java.awt.Dimension( 38, 19 ) );
            statusPanel = new JPanel();
            statusPanel.setLayout( new BorderLayout() );
            statusPanel.add( statusLabel, java.awt.BorderLayout.NORTH );
        }
        return statusPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel()
    {
        if ( mainPanel == null )
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows( 1 );
            mainPanel = new JPanel();
            mainPanel.setLayout( gridLayout );
            mainPanel.setBorder( javax.swing.BorderFactory.createBevelBorder( javax.swing.border.BevelBorder.LOWERED ) );
            mainPanel.add( getJSplitPane(), null );
        }
        return mainPanel;
    }


    /**
     * This method initializes jSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getJSplitPane()
    {
        if ( jSplitPane == null )
        {
            jSplitPane = new JSplitPane();
            jSplitPane.setDividerSize(6);
            jSplitPane.setOneTouchExpandable(true);
            jSplitPane.setRightComponent( getRightPanel() );
            jSplitPane.setLeftComponent( getLeftNavigation() );
            leftNavigation.addTreeSelectionListener( this );
            jSplitPane.setDividerLocation(250);
        }
        return jSplitPane;
    }


    private LeftTreeNavigation getLeftNavigation()
    {
        if ( leftNavigation == null )
        {
            leftNavigation = new LeftTreeNavigation();
            leftNavigation.setPreferredSize(new java.awt.Dimension(250,323));
            leftNavigation.setRightDetailPanel( rightDetailPanel );
        }
        return leftNavigation;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRightPanel()
    {
        if ( rightDetailPanel == null )
        {
            rightDetailPanel = new JPanel();
            rightDetailPanelLayout = new CardLayout();
            rightDetailPanel.setLayout( rightDetailPanelLayout );
            rightDetailPanel.add( getEmptyPanel(), getEmptyPanel().getName() );
//            rightDetailPanel.add( getPermissionPanel(), getPermissionPanel().getName() );
            rightDetailPanel.add( getRolePanel(), getRolePanel().getName() );
            rightDetailPanel.add( getProfilePanel(), getProfilePanel().getName() );
            rightDetailPanel.add( getApplicationPanel(), getApplicationPanel().getName() );
            rightDetailPanel.add( getGroupPanel(), getGroupPanel().getName() );
            rightDetailPanel.add( getUserPanel(), getUserPanel().getName() );
            rightDetailPanel.add( getNewApplicationPanel(), getNewApplicationPanel().getName() );
            rightDetailPanel.add( getNewGroupPanel(), getNewGroupPanel().getName() );
            rightDetailPanel.add( getNewUserPanel(), getNewUserPanel().getName() );
//            rightDetailPanel.add( getNewPermissionPanel(), getNewPermissionPanel().getName() );
            rightDetailPanel.add( getNewRolePanel(), getNewRolePanel().getName() );
            rightDetailPanel.add( getNewProfilePanel(), getNewProfilePanel().getName() );
        }
        return rightDetailPanel;
    }

    
    public void setInstallationLayout( TriplesecInstallationLayout layout )
    {
        this.layout = layout;
    }


    /**
     * This method initializes jMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getConnectMenuItem()
    {
        if ( connectMenuItem == null )
        {
            connectMenuItem = new JMenuItem();
            connectMenuItem.setToolTipText( "Connect to Triplesec Server" );
            connectMenuItem.setText( "Connect" );
            connectMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
            connectMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    connectActionTaken();
                }
            } );
        }
        return connectMenuItem;
    }


    /**
     * This method initializes jMenuItem1
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getDisconnectMenuItem()
    {
        if ( disconnectMenuItem == null )
        {
            disconnectMenuItem = new JMenuItem();
            disconnectMenuItem.setToolTipText( "Disconnect from Triplesec Server" );
            disconnectMenuItem.setText( "Disconnect" );
            disconnectMenuItem.setMnemonic( KeyEvent.VK_D );
            disconnectMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    performDisconnect();
                }
            } );
        }
        return disconnectMenuItem;
    }


    private void performDisconnect()
    {
        if ( !connectionManager.isConnected() )
        {
            JOptionPane.showMessageDialog( this , "Triplesec Admin has already disconnected.", 
                "Already Disconnected", JOptionPane.INFORMATION_MESSAGE );
            return;
        }
        boolean isDisconnected = connectionManager.disconnect();
        if ( ! isDisconnected )
        {
            JOptionPane.showMessageDialog( this , connectionManager.getLastFailure().getMessage(), 
                "Disconnect Failure", JOptionPane.ERROR_MESSAGE );
        }
        rightDetailPanelLayout.show( rightDetailPanel, "emptyPanel" );
        settings = null;
        configurationFileManager.clear();
    }

    
    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getConnectButton()
    {
        if ( connectButton == null )
        {
            connectButton = new JButton();
            connectButton.setBorder( new EtchedBorder() );
            connectButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/connect_22x22.png" ) ) );
            connectButton.setToolTipText( "Connect to Triplesec Server" );
            connectButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    connectActionTaken();
                }
            } );
        }
        return connectButton;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDisconnectButton()
    {
        if ( disconnectButton == null )
        {
            disconnectButton = new JButton();
            disconnectButton.setBorder( new EtchedBorder() );
            disconnectButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/disconnect_22x22.png" ) ) );
            disconnectButton.setToolTipText( "Disconnect from Triplesec Server" );
            disconnectButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    performDisconnect();
                }
            } );
        }
        return disconnectButton;
    }


    /**
     * This method initializes jMenu	
     * 	
     * @return javax.swing.JMenu	
     */
    private JMenu getConnectionMenu()
    {
        if ( connectionMenu == null )
        {
            connectionMenu = new JMenu();
            connectionMenu.setText("Connection");
            connectionMenu.setMnemonic(java.awt.event.KeyEvent.VK_C);
            connectionMenu.add(getConnectMenuItem());
            connectionMenu.add(getDisconnectMenuItem());
        }
        return connectionMenu;
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getEmptyPanel()
    {
        if ( emptyPanel == null )
        {
            emptyPanel = new JPanel();
            emptyPanel.setName("emptyPanel");
        }
        return emptyPanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
//    private PermissionPanel getPermissionPanel()
//    {
//        if ( permissionPanel == null )
//        {
//            permissionPanel = new PermissionPanel();
//            permissionPanel.setName( "permissionPanel" );
//        }
//        return permissionPanel;
//    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private RolePanel getRolePanel()
    {
        if ( rolePanel == null )
        {
            rolePanel = new RolePanel();
            rolePanel.setName( "rolePanel" );
        }
        return rolePanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private ProfilePanel getProfilePanel()
    {
        if ( profilePanel == null )
        {
            profilePanel = new ProfilePanel();
            profilePanel.setName( "profilePanel" );
        }
        return profilePanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private ApplicationPanel getApplicationPanel()
    {
        if ( applicationPanel == null )
        {
            applicationPanel = new ApplicationPanel();
            applicationPanel.setName( "applicationPanel" );
        }
        return applicationPanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private GroupPanel getGroupPanel()
    {
        if ( groupPanel == null )
        {
            groupPanel = new GroupPanel();
            groupPanel.setName( "groupPanel" );
        }
        return groupPanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private UserPanel getUserPanel()
    {
        if ( userPanel == null )
        {
            userPanel = new UserPanel();
            userPanel.setAdminFrame( this );
            userPanel.setName( "userPanel" );
        }
        return userPanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private NewApplicationPanel getNewApplicationPanel()
    {
        if ( newApplicationPanel == null )
        {
            newApplicationPanel = new NewApplicationPanel();
            newApplicationPanel.setName( "newApplicationPanel" );
        }
        return newApplicationPanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private NewGroupPanel getNewGroupPanel()
    {
        if ( newGroupPanel == null )
        {
            newGroupPanel = new NewGroupPanel();
            newGroupPanel.setName( "newGroupPanel" );
        }
        return newGroupPanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private NewUserPanel getNewUserPanel()
    {
        if ( newUserPanel == null )
        {
            newUserPanel = new NewUserPanel();
            newUserPanel.setName( "newUserPanel" );
        }
        return newUserPanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
//    private NewPermissionPanel getNewPermissionPanel()
//    {
//        if ( newPermissionPanel == null )
//        {
//            newPermissionPanel = new NewPermissionPanel();
//            newPermissionPanel.setName( "newPermissionPanel" );
//        }
//        return newPermissionPanel;
//    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private NewRolePanel getNewRolePanel()
    {
        if ( newRolePanel == null )
        {
            newRolePanel = new NewRolePanel();
            newRolePanel.setName( "newRolePanel" );
        }
        return newRolePanel;
    }


    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private NewProfilePanel getNewProfilePanel()
    {
        if ( newProfilePanel == null )
        {
            newProfilePanel = new NewProfilePanel();
            newProfilePanel.setName( "newProfilePanel" );
        }
        return newProfilePanel;
    }


    public void setStatusMessage( String msg, Icon icon )
    {
        statusLabel.setText( " Status: " + msg );
        statusLabel.setIcon( icon );
    }


    private boolean doConnectDialogs()
    {
        if ( SettingsUtils.isAvailableSettings() && settings == null )
        {
            String msg = "You have encrypted settings stored on disk. \nWould you like to use these settings?" +
                    "\nCancel to use smart defaults.";
            int response = JOptionPane.showConfirmDialog( this, msg, "Use Discovered Settings?", 
                JOptionPane.YES_NO_OPTION );
            
            if ( response == JOptionPane.YES_OPTION )
            {
                int count = 0;
                String passphrase = null;
                while ( ( passphrase = UiUtils.showPasswordDialog( this, "Enter passphrase for encrypted settings." ) ).length() == 0 )
                {
                    count++;
                    if ( count == 3 )
                    {
                        JOptionPane.showMessageDialog( this, "You just can't seem to get it right - I'm aborting!" );
                        return false;
                    }
                    JOptionPane.showMessageDialog( this, "Invalid passphrase try again." );
                }

                try
                {
                    settings = SettingsUtils.load( passphrase );
                }
                catch ( IOException e )
                {
                    return false;
                }
            }
        }
        
        // -------------------------------------------------------------------
        // Acquire Connection Info and AdminTool app's Guardian Password 
        // -------------------------------------------------------------------
        
        ConnectionInfo connectionInfo = null;
        String adminToolPassword = null;
        if ( settings != null )
        {
            ConnectionInfoModifier modifier = new ConnectionInfoModifier( settings.getDefaultConnectionInfo() );
            adminToolPassword = settings.getAdminToolPassword();
            String passcode = UiUtils.showPasswordDialog( this, "If user " 
                + modifier.getConnectionInfo().getPrincipal() +
                " requires a passcode generate and enter it now. Otherwise just continue ... " );
            if ( passcode != null && passcode.length() > 0 )
            {
                modifier.setPasscode( passcode );
            }
            connectionInfo = modifier.getConnectionInfo();
        }
        else
        {
            ConnectionDialog connectionDialog = new ConnectionDialog();
            connectionDialog.setModal( true );
            connectionDialog.setLocation( UiUtils.getCenteredPosition( connectionDialog ) );
            connectionDialog.setVisible( true );
            if ( connectionDialog.isCanceled() )
            {
                return false;
            }
            
            ConnectionInfoModifier modifier = connectionDialog.getConnectionModifier();
            LoginDialog loginDialog = new LoginDialog( modifier );
            loginDialog.setModal( true );
            loginDialog.setLocation( UiUtils.getCenteredPosition( connectionDialog ) );
            loginDialog.setVisible( true );
            if ( loginDialog.isCanceled() )
            {
                return false;
            }
            
            connectionInfo = modifier.getConnectionInfo();

            int count = 0;
            while ( ( adminToolPassword = UiUtils.showPasswordDialog( this, "Enter adminTool's guardian password." ) ).length() == 0 )
            {
                count++;
                if ( count == 3 )
                {
                    JOptionPane.showMessageDialog( this, "You just can't seem to get it right - I'm aborting!" );
                    return false;
                }
                JOptionPane.showMessageDialog( this, "Invalid guardian password for adminTool try again." );
            }
        }
        
        try
        {
            // -------------------------------------------------------------------
            // Need to connect to the server via guardian first
            // -------------------------------------------------------------------
            
            Properties props = new Properties();
            StringBuffer buf = new StringBuffer();
            buf.append( "appName=tsecAdminTool,ou=Applications," ).append( connectionInfo.getLdapRealmBase() );
            props.setProperty( "applicationPrincipalDN", buf.toString() );
            props.setProperty( "applicationCredentials", adminToolPassword );

            try
            {
                Class.forName( "org.safehaus.triplesec.guardian.ldap.LdapConnectionDriver" );
            }
            catch ( ClassNotFoundException e1 )
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            buf.setLength( 0 );
            buf.append( "ldap://" ).append( connectionInfo.getHost() ).append( ":" )
                .append( connectionInfo.getLdapPort() ).append( "/" ).append( connectionInfo.getLdapRealmBase() );
            ApplicationPolicy policy = 
                ApplicationPolicyFactory.newInstance( buf.toString(), props );
            return connectionManager.connect( connectionInfo, policy );
        }
        catch ( LoginException t )
        {
            log.error( "Authentication Failed", t );
            JOptionPane.showMessageDialog( this, UiUtils.wrap( t.getMessage(), 79 ), "Connection Failed", 
                JOptionPane.ERROR_MESSAGE );
            return false;
        }
    }

    
    private void connectActionTaken()
    {
        if ( connectionManager.isConnected() )
        {
            JOptionPane.showMessageDialog( this, "Triplesec Admin is already connected.", 
                "Already Connected", JOptionPane.INFORMATION_MESSAGE );
            return;
        }
        
        boolean isConnected = false;
        if ( configurationFileManager.isConfigurationLoaded() ) 
        {
            isConnected = connectionManager.connect( configurationFileManager.getConfiguration(), 
                configurationFileManager.getEnvironment() );
        }
        else
        {
            isConnected = doConnectDialogs();
        }
        
        if ( ! isConnected )
        {
            if ( connectionManager.getLastFailure() == null )
            {
                return;
            }
            String msg = UiUtils.wrap( connectionManager.getLastFailure().getMessage(), 79 );
            JOptionPane.showMessageDialog( this, msg, 
                "Connection Failed", JOptionPane.ERROR_MESSAGE );
        }
        else
        {
            JOptionPane.showMessageDialog( this, "Connected and authenticated successfully.", 
                "Success", JOptionPane.INFORMATION_MESSAGE );
        }
        
        newApplicationPanel.setTriplesecAdmin( connectionManager.getTriplesecAdmin() );
        newGroupPanel.setTriplesecAdmin( connectionManager.getTriplesecAdmin() );
        newUserPanel.setTriplesecAdmin( connectionManager.getTriplesecAdmin() );
    }


    public void connect( ConnectionInfo connectInfo, ApplicationPolicy policy ) throws LoginException
    {
        connectionManager.connect( connectInfo, policy );
        newApplicationPanel.setTriplesecAdmin( connectionManager.getTriplesecAdmin() );
        newGroupPanel.setTriplesecAdmin( connectionManager.getTriplesecAdmin() );
        newUserPanel.setTriplesecAdmin( connectionManager.getTriplesecAdmin() );
    }
    
    
    public void openActionTaken()
    {
        if ( layout == null )
        {
            configurationFileManager.prompt( new File( System.getProperty( "user.home" ) ) );
        }
        else
        {
            configurationFileManager.prompt( this.layout.getBootstrapperConfigurationFile() );
        }
    }


    public void valueChanged( TreeSelectionEvent e )
    {
        if ( ! connectionManager.isConnected() )
        {
            return;
        }
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) e.getPath().getLastPathComponent();
        if ( node == null )
        {
            rightDetailPanelLayout.show( rightDetailPanel, "emptyPanel" );
            return;
        }
        
        Object obj = node.getUserObject();
//        if ( obj instanceof Permission )
//        {
//            permissionPanel.setTreeNode( node );
//            permissionPanel.setTree( leftNavigation.getTree() );
//            rightDetailPanelLayout.show( rightDetailPanel, "permissionPanel" );
//        }
//        else
        if ( obj instanceof Role )
        {
            rolePanel.setTreeNode( node );
            rolePanel.setTree( leftNavigation.getTree() );
            rightDetailPanelLayout.show( rightDetailPanel, "rolePanel" );
        }
        else if ( obj instanceof Profile )
        {
            profilePanel.setTreeNode( node );
            profilePanel.setTree( leftNavigation.getTree() );
            rightDetailPanelLayout.show( rightDetailPanel, "profilePanel" );
        }
        else if ( obj instanceof Application )
        {
            applicationPanel.setTreeNode( node );
            applicationPanel.setTree( leftNavigation.getTree() );
            rightDetailPanelLayout.show( rightDetailPanel, "applicationPanel" );
        }
        else if ( obj instanceof Group )
        {
            groupPanel.setTreeNode( node );
            groupPanel.setTree( leftNavigation.getTree() );
            rightDetailPanelLayout.show( rightDetailPanel, "groupPanel" );
        }
        else if ( obj instanceof User )
        {
            userPanel.setTreeNode( node );
            userPanel.setTree( leftNavigation.getTree() );
            rightDetailPanelLayout.show( rightDetailPanel, "userPanel" );
        }
        else if ( obj instanceof String )
        {
            if ( ( ( String ) obj ).equalsIgnoreCase( "Applications" ) )
            {
                newApplicationPanel.setTreeNode( node );
                newApplicationPanel.setLeftTreeNavigation( leftNavigation );
                rightDetailPanelLayout.show( rightDetailPanel, "newApplicationPanel" );
            }
            else if ( ( ( String ) obj ).equalsIgnoreCase( "Groups" ) )
            {
                newGroupPanel.setTreeNode( node );
                newGroupPanel.setLeftTreeNavigation( leftNavigation );
                rightDetailPanelLayout.show( rightDetailPanel, "newGroupPanel" );
            }
            else if ( ( ( String ) obj ).equalsIgnoreCase( "Users" ) )
            {
                newUserPanel.setTreeNode( node );
                newUserPanel.setLeftTreeNavigation( leftNavigation );
                rightDetailPanelLayout.show( rightDetailPanel, "newUserPanel" );
            }
//            else if ( ( ( String ) obj ).equalsIgnoreCase( "Permissions" ) )
//            {
//                newPermissionPanel.setTreeNode( node );
//                newPermissionPanel.setLeftTreeNavigation( leftNavigation );
//                rightDetailPanelLayout.show( rightDetailPanel, "newPermissionPanel" );
//            }
            else if ( ( ( String ) obj ).equalsIgnoreCase( "Roles" ) )
            {
                newRolePanel.setTreeNode( node );
                newRolePanel.setLeftTreeNavigation( leftNavigation );
                rightDetailPanelLayout.show( rightDetailPanel, "newRolePanel" );
            }
            else if ( ( ( String ) obj ).equalsIgnoreCase( "Profiles" ) )
            {
                newProfilePanel.setTreeNode( node );
                newProfilePanel.setLeftTreeNavigation( leftNavigation );
                rightDetailPanelLayout.show( rightDetailPanel, "newProfilePanel" );
            }
            else
            {
                rightDetailPanelLayout.show( rightDetailPanel, "emptyPanel" );
            }
        }
        else
        {
            rightDetailPanelLayout.show( rightDetailPanel, "emptyPanel" );
        }
    }

    
    /**
     * This method initializes jMenu	
     * 	
     * @return javax.swing.JMenu	
     */
    private JMenu getSettingsMenu()
    {
        if ( settingsMenu == null )
        {
            settingsMenu = new JMenu();
            settingsMenu.setText("Settings");
            settingsMenu.setMnemonic(java.awt.event.KeyEvent.VK_S);
            settingsMenu.add(getLoadSettingsMenuItem());
            settingsMenu.add(getEditSettingsMenuItem());
        }
        return settingsMenu;
    }


    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getEditSettingsMenuItem()
    {
        if ( editSettingsMenuItem == null )
        {
            editSettingsMenuItem = new JMenuItem();
            editSettingsMenuItem.setText("Edit");
            editSettingsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_E);
            editSettingsMenuItem.addActionListener( new java.awt.event.ActionListener()
            {   
            	public void actionPerformed(java.awt.event.ActionEvent e) {    
            	    if ( SettingsUtils.isAvailableSettings() && settings == null )
                    {
                        String passphrase = UiUtils.showPasswordDialog( AdminFrame.this, 
                        "Enter passphrase to decrypt settings." );
                        try
                        {
                            settings = SettingsUtils.load( passphrase );
                        }
                        catch ( IOException e1 )
                        {
                            String msg = UiUtils.wrap( "Could load settings file: " + e1.getMessage(), 79 );
                            JOptionPane.showMessageDialog( AdminFrame.this, msg, "Load Failure!", JOptionPane.ERROR_MESSAGE );
                            return;
                        }
                    }
                    
                    SettingsEditor editor = new SettingsEditor();
                    editor.setSettings( settings );
                    editor.setVisible( true );
                }
            } );
        }
        return editSettingsMenuItem;
    }
    
    
    public boolean doLoadSettings()
    {
        if ( ! SettingsUtils.isAvailableSettings() )
        {
            String msg = UiUtils.wrap( "No default settings file found for " 
                + System.getProperty( "user.name" ) + ".  You can use Settings->Edit instead and save " +
                        "your settings after replying no to this dialog.  Or select yes to import " +
                        "another non-default settings file." , 79 );
            int response = JOptionPane.showConfirmDialog( AdminFrame.this, msg, "Import Settings File?", 
                JOptionPane.YES_NO_OPTION );
            
            if ( response == JOptionPane.YES_OPTION )
            {
                // TODO add yes handling for importing non-default settings file
            }
            return false;
        }
        
        String passphrase = UiUtils.showPasswordDialog( AdminFrame.this, 
            "Enter passphrase to decrypt settings." );
        try
        {
            settings = SettingsUtils.load( passphrase );
        }
        catch ( IOException e1 )
        {
            String msg = UiUtils.wrap( "Could load settings file: " + e1.getMessage(), 79 );
            JOptionPane.showMessageDialog( AdminFrame.this, msg, "Load Failure!", JOptionPane.ERROR_MESSAGE );
        }
        
        return true;
    }


    /**
     * This method initializes jMenuItem	
     * 	
     * @return javax.swing.JMenuItem	
     */
    private JMenuItem getLoadSettingsMenuItem()
    {
        if ( loadSettingsMenuItem == null )
        {
            loadSettingsMenuItem = new JMenuItem();
            loadSettingsMenuItem.setText("Load");
            loadSettingsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_L);
            loadSettingsMenuItem.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    doLoadSettings();
                }
            } );
        }
        return loadSettingsMenuItem;
    }


    public static void main( String[] args )
    {
        AdminFrame frame = new AdminFrame();
        if ( args.length > 0 )
        {
            frame.setInstallationLayout( new TriplesecInstallationLayout( args[0] ) );
        }
        frame.setLocation( UiUtils.getCenteredPosition( frame ) );
        frame.setVisible( true );
    }


} // @jve:decl-index=0:visual-constraint="10,10"
