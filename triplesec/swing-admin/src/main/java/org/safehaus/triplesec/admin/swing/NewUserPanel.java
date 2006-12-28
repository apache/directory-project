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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.ExternalUser;
import org.safehaus.triplesec.admin.ExternalUserModifier;
import org.safehaus.triplesec.admin.HauskeysUser;
import org.safehaus.triplesec.admin.HauskeysUserModifier;
import org.safehaus.triplesec.admin.LocalUser;
import org.safehaus.triplesec.admin.LocalUserModifier;
import org.safehaus.triplesec.admin.TriplesecAdmin;
import org.safehaus.triplesec.admin.User;


public class NewUserPanel extends JPanel implements StatusListener, StatusObject, KeyListener, FocusListener
{
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;
    private JButton createButton = null;
    private JPanel aboveButtonPanel = null;
    private UserNorthPanel userNorthPanel = null;
    private JPanel southPanel = null;
    private JTextArea descriptionTextArea = null;
    private DefaultMutableTreeNode node = null;
    private LeftTreeNavigation leftTreeNavigation;
    private JTabbedPane centerTabbedPane = null;
    private JPanel existingPanelTab = null;
    private JScrollPane jScrollPane = null;
    private JTable existingUsersTable = null;
    private TriplesecAdmin triplesecAdmin;
    private ExistingUsersTableModel existingUsersTableModel = null; //  @jve:decl-index=0:visual-constraint=""
    private ImageIcon newUserIcon = new ImageIcon( getClass().getResource(
        "/org/safehaus/triplesec/admin/swing/new_user_48x48.png" ) );
    private ProvisioningPanel provisioningPanel;
    private HotpSettingsPanel hotpSettingsPanel;
    private ExternalLinkPanel externalLinkPanel;
    private UserInfoPanel userInfoPanel;
    private boolean lastStatusState = true;
    private StatusListener listener = this;


    public void setLeftTreeNavigation( LeftTreeNavigation leftTreeNavigation )
    {
        this.leftTreeNavigation = leftTreeNavigation;
    }


    public void setTriplesecAdmin( TriplesecAdmin triplesecAdmin )
    {
        this.triplesecAdmin = triplesecAdmin;
    }


    /**
     * This is the default constructor
     */
    public NewUserPanel()
    {
        super();
        initialize();
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.weighty = 1.0D;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets( 10, 10, 10, 10 );
        gridBagConstraints.gridy = 0;
        this.setLayout( new GridBagLayout() );
        this.setSize( 550, 417 );
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "New User",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
            null, null ) );
        this.add( getMainPanel(), gridBagConstraints );
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
            mainPanel = new JPanel();
            mainPanel.setLayout( new BorderLayout() );
            mainPanel.add( getButtonPanel(), java.awt.BorderLayout.SOUTH );
            mainPanel.add( getAboveButtonPanel(), java.awt.BorderLayout.CENTER );
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel()
    {
        if ( buttonPanel == null )
        {
            buttonPanel = new JPanel();
            buttonPanel.setBorder( javax.swing.BorderFactory.createEmptyBorder( 0, 0, 0, 0 ) );
            buttonPanel.add( getCreateButton(), null );
        }
        return buttonPanel;
    }


    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getCreateButton()
    {
        if ( createButton == null )
        {
            createButton = new JButton();
            createButton.setText( "Create" );
            createButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    createAction();
                }
            } );
        }
        return createButton;
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getAboveButtonPanel()
    {
        if ( aboveButtonPanel == null )
        {
            aboveButtonPanel = new JPanel();
            aboveButtonPanel.setLayout( new BorderLayout() );
            aboveButtonPanel.add( getUserNorthPanel(), java.awt.BorderLayout.NORTH );
            aboveButtonPanel.add( getSouthPanel(), java.awt.BorderLayout.SOUTH );
            aboveButtonPanel.add( getCenterTabbedPane(), java.awt.BorderLayout.CENTER );
        }
        return aboveButtonPanel;
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private UserNorthPanel getUserNorthPanel()
    {
        if ( userNorthPanel == null )
        {
            userNorthPanel = new UserNorthPanel();
            userNorthPanel.setNewEntityMode( true );
            userNorthPanel.setStatusListener( this );
            userNorthPanel.setIcon( newUserIcon );
            userNorthPanel.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    if ( userNorthPanel.isExternalUserSelected() )
                    {
                        centerTabbedPane.removeAll();
                        centerTabbedPane.addTab( "Existing", null, existingPanelTab );
                        centerTabbedPane.addTab( "External Link", null, externalLinkPanel );
                    }
                    else if ( userNorthPanel.isLocalUserSelected() )
                    {
                        centerTabbedPane.removeAll();
                        centerTabbedPane.addTab( "Existing", null, existingPanelTab );
                        centerTabbedPane.addTab( "User Info", null, userInfoPanel );
                    }
                    else if ( userNorthPanel.isHauskeysUserSelected() )
                    {
                        centerTabbedPane.removeAll();
                        centerTabbedPane.addTab( "Existing", null, existingPanelTab );
                        centerTabbedPane.addTab( "User Info", null, userInfoPanel );
                        centerTabbedPane.addTab( "Provisioning", null, provisioningPanel );
                        centerTabbedPane.addTab( "HOTP Settings", null, hotpSettingsPanel );
                    }
                    else
                    {
                        throw new IllegalStateException( "Radio buttons should be mutually exclusive!" );
                    }
                }
            } );
        }
        return userNorthPanel;
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSouthPanel()
    {
        if ( southPanel == null )
        {
            southPanel = new JPanel();
            southPanel.setLayout( new BorderLayout() );
            southPanel.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Description",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null ) );
            southPanel.add( getDescriptionTextArea(), java.awt.BorderLayout.NORTH );
        }
        return southPanel;
    }


    /**
     * This method initializes jTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getDescriptionTextArea()
    {
        if ( descriptionTextArea == null )
        {
            descriptionTextArea = new JTextArea();
            descriptionTextArea.setRows( 3 );
            descriptionTextArea.addKeyListener( this );
            descriptionTextArea.addFocusListener( this );
        }
        return descriptionTextArea;
    }


    public void setTreeNode( DefaultMutableTreeNode node )
    {
        this.node = node;
        existingUsersTable.setModel( new ExistingUsersTableModel() );
    }


    public DefaultMutableTreeNode getTreeNode()
    {
        return node;
    }


    public void createExternalUser()
    {
        ExternalUser user;
        ExternalUserModifier modifier = triplesecAdmin.newExternalUser( userNorthPanel.getId(),
            externalLinkPanel.getReferral() ).setDescription( descriptionTextArea.getText() );
        try
        {
            user = modifier.add();
            DefaultMutableTreeNode userNode = new DefaultMutableTreeNode( user );
            DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
            model.insertNodeInto( userNode, node, 0 );
            existingUsersTableModel.fireTableDataChanged();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to create user:\n" + e.getMessage(), 79 ),
                "User creation failure!", JOptionPane.ERROR_MESSAGE );
            return;
        }
    }


    public void createLocalUser()
    {
        if ( ! userInfoPanel.isPasswordOk() )
        {
            JOptionPane.showMessageDialog( this, "Passwords are not equal or are invalid.  Create aborted!" );
            return;
        }
        
        LocalUser user;
        LocalUserModifier modifier = triplesecAdmin.newLocalUser( userNorthPanel.getId(), userInfoPanel.getFirstName(), 
                userInfoPanel.getLastName(), userInfoPanel.getPassword() );
        modifier.setDescription( descriptionTextArea.getText() );
        try
        {
            user = modifier.add();
            DefaultMutableTreeNode userNode = new DefaultMutableTreeNode( user );
            DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
            model.insertNodeInto( userNode, node, 0 );
            existingUsersTableModel.fireTableDataChanged();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to create user:\n" + e.getMessage(), 79 ),
                "User creation failure!", JOptionPane.ERROR_MESSAGE );
            return;
        }
    }


    public void createHauskeysUser()
    {
        if ( ! userInfoPanel.isPasswordOk() )
        {
            JOptionPane.showMessageDialog( this, "Passwords are not equal or are invalid.  Create aborted!" );
            return;
        }

        HauskeysUser user;
        HauskeysUserModifier modifier = triplesecAdmin
            .newHauskeysUser( userNorthPanel.getId(), userInfoPanel.getFirstName(), 
                userInfoPanel.getLastName(), userInfoPanel.getPassword() )
            .setDescription( descriptionTextArea.getText() ).setPassword( userInfoPanel.getPassword() )
            .setActivationKey( hotpSettingsPanel.getActivationKey() )
            .setSecret( hotpSettingsPanel.getSecret() )
            .setMovingFactor( hotpSettingsPanel.getMovingFactor() )
            .setFailuresInEpoch( hotpSettingsPanel.getFailuresInEpoch() )
            .setEmail( provisioningPanel.getEmail() )
            .setMidletName( provisioningPanel.getMidletName() )
            .setMobile( provisioningPanel.getMobile() )
            .setMobileCarrier( String.valueOf( provisioningPanel.getMobileCarrier() ) )
            .setNotifyBy( provisioningPanel.getNotifyBy() )
            .setRealm( userInfoPanel.getRealm() )
            .setTokenPin( provisioningPanel.getTokenPin() );
        try
        {
            user = modifier.add();
            DefaultMutableTreeNode userNode = new DefaultMutableTreeNode( user );
            DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
            model.insertNodeInto( userNode, node, 0 );
            existingUsersTableModel.fireTableDataChanged();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to create user:\n" + e.getMessage(), 79 ),
                "User creation failure!", JOptionPane.ERROR_MESSAGE );
            return;
        }
    }


    public void createAction()
    {
        if ( userNorthPanel.getId() == null || userNorthPanel.getId().equals( "" ) )
        {
            return;
        }

        if ( userNorthPanel.isExternalUserSelected() )
        {
            createExternalUser();
        }
        else if ( userNorthPanel.isLocalUserSelected() )
        {
            createLocalUser();
        }
        else if ( userNorthPanel.isHauskeysUserSelected() )
        {
            createHauskeysUser();
        }
        else
        {
            throw new IllegalStateException( "Radio buttons on north user panel should be mutually exclusive." );
        }

        clearAll();
    }


    public void clearAll()
    {
        userNorthPanel.setId( null );
        userNorthPanel.setStatus( null );
        this.lastStatusState = true;
    }


    /**
     * This method initializes jTabbedPane	
     * 	
     * @return javax.swing.JTabbedPane	
     */
    private JTabbedPane getCenterTabbedPane()
    {
        if ( centerTabbedPane == null )
        {
            centerTabbedPane = new JTabbedPane();
            centerTabbedPane.addTab( "Existing", null, getExistingPanelTab(),
                "Copy a user from any one of these existing users." );
            getExternalLinkPanel();
            getUserInfoPanel();
            getProvisioningPanel();
            getHotpSettingsPanel();
        }
        return centerTabbedPane;
    }


    private HotpSettingsPanel getHotpSettingsPanel()
    {
        if ( hotpSettingsPanel == null )
        {
            hotpSettingsPanel = new HotpSettingsPanel();
            hotpSettingsPanel.setNewEntityMode( true );
            hotpSettingsPanel.setStatusListener( this );
        }
        return hotpSettingsPanel;
    }


    private ExternalLinkPanel getExternalLinkPanel()
    {
        if ( externalLinkPanel == null )
        {
            externalLinkPanel = new ExternalLinkPanel();
            externalLinkPanel.setNewEntityMode( true );
            externalLinkPanel.setStatusListener( this );
        }
        return externalLinkPanel;
    }


    private UserInfoPanel getUserInfoPanel()
    {
        if ( userInfoPanel == null )
        {
            userInfoPanel = new UserInfoPanel();
            userInfoPanel.setNewEntityMode( true );
            userInfoPanel.setStatusListener( this );
        }
        return userInfoPanel;
    }


    private ProvisioningPanel getProvisioningPanel()
    {
        if ( provisioningPanel == null )
        {
            provisioningPanel = new ProvisioningPanel();
            provisioningPanel.setNewEntityMode( true );
            provisioningPanel.setStatusListener( this );
        }
        return provisioningPanel;
    }


    /**
     * This method initializes jPanel3	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getExistingPanelTab()
    {
        if ( existingPanelTab == null )
        {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.gridx = 0;
            existingPanelTab = new JPanel();
            existingPanelTab.setLayout( new GridBagLayout() );
            existingPanelTab.add( getJScrollPane(), gridBagConstraints4 );
        }
        return existingPanelTab;
    }


    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane()
    {
        if ( jScrollPane == null )
        {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView( getExistingUsersTable() );
        }
        return jScrollPane;
    }


    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getExistingUsersTable()
    {
        if ( existingUsersTable == null )
        {
            existingUsersTable = new JTable();
            existingUsersTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            existingUsersTable.setModel( getExistingUsersTableModel() );
            existingUsersTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
                public void valueChanged( ListSelectionEvent e )
                {
                    int index = existingUsersTable.getSelectionModel().getAnchorSelectionIndex();
                    if ( index < 0 )
                    {
                        return;
                    }

                    User archetype = ( User ) existingUsersTable.getValueAt( index, 0 );
                    userNorthPanel.setFields( archetype );
                    userNorthPanel.setIcon( newUserIcon );
                    userNorthPanel.setId( "CopyOf" + archetype.getId() );
                    if ( archetype instanceof ExternalUser )
                    {
                        externalLinkPanel.setFields( ( ExternalUser ) archetype );
                        descriptionTextArea.setText( ( ( ExternalUser ) archetype ).getDescription() );
                        centerTabbedPane.removeAll();
                        centerTabbedPane.addTab( "Existing", null, existingPanelTab );
                        centerTabbedPane.addTab( "External Link", null, externalLinkPanel );
                    }
                    else if ( archetype instanceof LocalUser )
                    {
                        userInfoPanel.setFields( ( LocalUser ) archetype, "Not implemented" );
                        descriptionTextArea.setText( ( ( LocalUser ) archetype ).getDescription() );
                        centerTabbedPane.removeAll();
                        centerTabbedPane.addTab( "Existing", null, existingPanelTab );
                        centerTabbedPane.addTab( "User Info", null, userInfoPanel );
                    }
                    else if ( archetype instanceof HauskeysUser )
                    {
                        userInfoPanel.setFields( ( HauskeysUser ) archetype );
                        hotpSettingsPanel.setFields( ( HauskeysUser ) archetype );
                        provisioningPanel.setFields( ( HauskeysUser ) archetype );
                        descriptionTextArea.setText( ( ( HauskeysUser ) archetype ).getDescription() );
                        centerTabbedPane.removeAll();
                        centerTabbedPane.addTab( "Existing", null, existingPanelTab );
                        centerTabbedPane.addTab( "User Info", null, userInfoPanel );
                        centerTabbedPane.addTab( "Provisioning", null, provisioningPanel );
                        centerTabbedPane.addTab( "HOTP Settings", null, hotpSettingsPanel );
                    }
//                    userNorthPanel.setStatus( Color.RED, "Create operation needed!" );
                }});
        }
        return existingUsersTable;
    }


    /**
     * This method initializes defaultTableModel	
     * 	
     * @return javax.swing.table.DefaultTableModel	
     */
    private ExistingUsersTableModel getExistingUsersTableModel()
    {
        if ( existingUsersTableModel == null )
        {
            existingUsersTableModel = new ExistingUsersTableModel();
        }
        return existingUsersTableModel;
    }

    class ExistingUsersTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 1L;


        public int getRowCount()
        {
            if ( node == null )
            {
                return 0;
            }
            return node.getChildCount();
        }


        public int getColumnCount()
        {
            return 3;
        }


        public Object getValueAt( int rowIndex, int columnIndex )
        {
            if ( node == null )
            {
                return null;
            }

            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) node.getChildAt( rowIndex );
            switch ( columnIndex )
            {
                case ( 0 ):
                    return child.getUserObject();
                case ( 1 ):
                    return ( ( User ) child.getUserObject() ).getCreatorsName();
                case ( 2 ):
                    return ( ( User ) child.getUserObject() ).getCreateTimestamp();
            }
            return child.getUserObject();
        }


        public String getColumnName( int columnIndex )
        {
            switch ( columnIndex )
            {
                case ( 0 ):
                    return "Existing User";
                case ( 1 ):
                    return "Creator's Name";
                case ( 2 ):
                    return "Create Timestamp";
                default:
                    throw new IndexOutOfBoundsException();
            }
        }
    }


    public void statusChanged( StatusObject obj )
    {
        if ( ! obj.isUpToDate() )
        {
//            userNorthPanel.setStatus( Color.RED, "Create operation needed!" );
            return;
        }
        
        if ( isUpToDate() && userNorthPanel.isUpToDate() && userInfoPanel.isUpToDate() 
            && provisioningPanel.isUpToDate() && hotpSettingsPanel.isUpToDate() )
        {
//            userNorthPanel.setStatus( Color.GREEN, "Nothing to do!" );
        }
    }


    public void checkStatus()
    {
        boolean upToDate = isUpToDate();
        if ( listener != null && lastStatusState != upToDate )
        {
            listener.statusChanged( NewUserPanel.this );
            lastStatusState = upToDate;
        }
    }
    
    
    public boolean isUpToDate()
    {
        return UiUtils.isFieldUpToDate( descriptionTextArea, null );
    }


    public void keyTyped( KeyEvent e )
    {
        checkStatus();
    }


    public void keyPressed( KeyEvent e )
    {
        checkStatus();
    }


    public void keyReleased( KeyEvent e )
    {
        checkStatus();
    }


    public void focusGained( FocusEvent e )
    {
        checkStatus();
    }


    public void focusLost( FocusEvent e )
    {
        checkStatus();
    }
} //  @jve:decl-index=0:visual-constraint="10,10"
