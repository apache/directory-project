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


import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JTree;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.ExternalUser;
import org.safehaus.triplesec.admin.ExternalUserModifier;
import org.safehaus.triplesec.admin.HauskeysUser;
import org.safehaus.triplesec.admin.HauskeysUserModifier;
import org.safehaus.triplesec.admin.LocalUser;
import org.safehaus.triplesec.admin.LocalUserModifier;
import org.safehaus.triplesec.admin.User;
import javax.swing.JLabel;
import javax.swing.JCheckBox;


public class UserPanel extends JPanel implements StatusListener, StatusObject, KeyListener, FocusListener
{
    private static final long serialVersionUID = 1L;

    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;
    private JButton revertButton = null;
    private JButton saveButton = null;
    private JPanel aboveButtonPanel = null;
    private UserNorthPanel userNorthPanel = null;
    private JTabbedPane centerTabbedPane = null;
    private JPanel southPanel = null;
    private JTextArea descriptionTextArea = null;
    private JButton deleteButton = null;
    private User user;
    private DefaultMutableTreeNode node;
    private JTree tree;
    private ProvisioningPanel provisioningPanel;
    private HotpSettingsPanel hotpSettingsPanel;
    private UserInfoPanel userInfoPanel;
    private GeneralPanel generalPanel;
    private ExternalLinkPanel externalLinkPanel;
    private UserDependentsPanel userDependentsPanel;
    private boolean lastStatusState = true;
    private StatusListener listener = this;
    private AdminFrame adminFrame = null;

    private JLabel jLabel = null;

    private JCheckBox disabledCheckBox = null;

    /**
     * This is the default constructor
     */
    public UserPanel()
    {
        super();
        initialize();
    }
    
    
    public void setAdminFrame( AdminFrame adminFrame )
    {
        this.adminFrame = adminFrame;
        this.provisioningPanel.setAdminFrame( adminFrame );
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Existing User",
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
            buttonPanel.add( getDeleteButton(), null );
            buttonPanel.add( getRevertButton(), null );
            buttonPanel.add( getSaveButton(), null );
        }
        return buttonPanel;
    }


    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRevertButton()
    {
        if ( revertButton == null )
        {
            revertButton = new JButton();
            revertButton.setText( "Revert" );
            revertButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    setUserFields();
                }
            } );
        }
        return revertButton;
    }


    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getSaveButton()
    {
        if ( saveButton == null )
        {
            saveButton = new JButton();
            saveButton.setText( "Save" );
            saveButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    saveAction();
                }
            } );
        }
        return saveButton;
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
            aboveButtonPanel.add( getCenterTabbedPane(), java.awt.BorderLayout.CENTER );
            aboveButtonPanel.add( getSouthPanel(), java.awt.BorderLayout.SOUTH );
        }
        return aboveButtonPanel;
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
            centerTabbedPane.addTab( "General", null, getGeneralPanel(), null );
            centerTabbedPane.addTab( "External Link", null, getExternalLinkPanel(), null );
            centerTabbedPane.addTab( "User Info", null, getUserInfoPanel(), null );
            centerTabbedPane.addTab( "Provisioning", null, getProvisioningPanel(), null );
            centerTabbedPane.addTab( "HOTP Settings", null, getHotpSettingsPanel(), null );
            centerTabbedPane.addTab( "Dependents", null, getUserDependentsPanel(), null );
        }
        return centerTabbedPane;
    }
    
    
    public UserDependentsPanel getUserDependentsPanel()
    {
        if ( userDependentsPanel == null )
        {
            userDependentsPanel = new UserDependentsPanel();
        }
        return userDependentsPanel;
    }
    
    
    public UserNorthPanel getUserNorthPanel()
    {
        if ( userNorthPanel == null )
        {
            userNorthPanel = new UserNorthPanel();
            userNorthPanel.setStatusListener( this );
        }
        
        return userNorthPanel;
    }


    public GeneralPanel getGeneralPanel()
    {
        if ( generalPanel == null )
        {
            generalPanel = new GeneralPanel();
            
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 5;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 5;
            jLabel = new JLabel();
            jLabel.setText("Disabled:");
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            generalPanel.add(jLabel, gridBagConstraints1);
            generalPanel.add(getJCheckBox(), gridBagConstraints2);
            
        }
        
        return generalPanel;
    }


    public ExternalLinkPanel getExternalLinkPanel()
    {
        if ( externalLinkPanel == null )
        {
            externalLinkPanel = new ExternalLinkPanel();
            externalLinkPanel.setStatusListener( this );
        }
        
        return externalLinkPanel;
    }


    public ProvisioningPanel getProvisioningPanel()
    {
        if ( provisioningPanel == null )
        {
            provisioningPanel = new ProvisioningPanel();
            provisioningPanel.setAdminFrame( this.adminFrame );
            provisioningPanel.setStatusListener( this );
        }
        
        return provisioningPanel;
    }


    public UserInfoPanel getUserInfoPanel()
    {
        if ( userInfoPanel == null )
        {
            userInfoPanel = new UserInfoPanel();
            userInfoPanel.setStatusListener( this );
        }
        
        return userInfoPanel;
    }


    public HotpSettingsPanel getHotpSettingsPanel()
    {
        if ( hotpSettingsPanel == null )
        {
            hotpSettingsPanel = new HotpSettingsPanel();
            hotpSettingsPanel.setStatusListener( this );
        }
        
        return hotpSettingsPanel;
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
            descriptionTextArea.setRows(3); 
            descriptionTextArea.addFocusListener( this );
            descriptionTextArea.addKeyListener( this );
        }
        return descriptionTextArea;
    }

    
    public void checkStatus()
    {
        boolean upToDate = isUpToDate();
        if ( listener != null && lastStatusState != upToDate )
        {
            listener.statusChanged( UserPanel.this );
            lastStatusState = upToDate;
        }
    }
    
    
    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDeleteButton()
    {
        if ( deleteButton == null )
        {
            deleteButton = new JButton();
            deleteButton.setText( "Delete" );
            deleteButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    if ( userDependentsPanel.hasDependents() )
                    {
                        String msg = UiUtils.wrap( "This user has dependent objects.  Remove all " +
                                "dependency relationships before attempting to delete this user.", 79 );
                        JOptionPane.showMessageDialog( UserPanel.this, msg, "User has dependents!", 
                            JOptionPane.INFORMATION_MESSAGE );
                        return;
                    }
                    
                    try
                    {
                        if ( user instanceof ExternalUser )
                        {
                            ( ( ExternalUser ) user ).modifier().delete();
                        }
                        else if ( user instanceof LocalUser )
                        {
                            ( ( LocalUser ) user ).modifier().delete();
                        }
                        else if ( user instanceof HauskeysUser )
                        {
                            ( ( HauskeysUser ) user ).modifier().delete();
                        }
                        else
                        {
                            throw new IllegalStateException( "Unknown user type: " + user.getClass() );
                        }

                        DefaultMutableTreeNode parentNode = ( DefaultMutableTreeNode ) node.getParent();
                        DefaultTreeModel treeModel = ( DefaultTreeModel ) tree.getModel();
                        treeModel.removeNodeFromParent( node );
                        TreePath path = new TreePath( parentNode.getPath() );
                        tree.setSelectionPaths( new TreePath[]
                            { path } );
                    }
                    catch ( DataAccessException e1 )
                    {
                        String msg = UiUtils.wrap( "Failed to delete user: " + e1.getMessage(), 79 );
                        JOptionPane.showMessageDialog( UserPanel.this, msg, "Delete Failed", JOptionPane.ERROR_MESSAGE );
                    }
                }
            } );
        }
        return deleteButton;
    }


    private void setUserFields()
    {
        generalPanel.setFields( user );
        disabledCheckBox.setSelected( user.isDisabled() );
        userNorthPanel.setFields( user );

        if ( user instanceof ExternalUser )
        {
            ExternalUser externalUser = ( ExternalUser ) user;
            centerTabbedPane.addTab( "External Link", null, externalLinkPanel, null );
            centerTabbedPane.remove( userInfoPanel );
            centerTabbedPane.remove( provisioningPanel );
            centerTabbedPane.remove( hotpSettingsPanel );
            externalLinkPanel.setFields( externalUser );
        }
        else if ( user instanceof LocalUser )
        {
            LocalUser localUser = ( LocalUser ) user;
            centerTabbedPane.remove( externalLinkPanel );
            centerTabbedPane.remove( provisioningPanel );
            centerTabbedPane.remove( hotpSettingsPanel );
            centerTabbedPane.remove( userInfoPanel );
            centerTabbedPane.addTab( "User Info", null, userInfoPanel, null );
            userInfoPanel.setFields( localUser, "not implemented" );
        }
        else if ( user instanceof HauskeysUser )
        {
            HauskeysUser hauskeysUser = ( HauskeysUser ) user;
            centerTabbedPane.remove( externalLinkPanel );
            centerTabbedPane.remove( userInfoPanel );
            centerTabbedPane.addTab( "User Info", null, userInfoPanel, null );
            centerTabbedPane.addTab( "Provisioning", null, provisioningPanel, null );
            centerTabbedPane.addTab( "HOTP Settings", null, hotpSettingsPanel, null );
            userInfoPanel.setFields( hauskeysUser );
            hotpSettingsPanel.setFields( hauskeysUser );
            provisioningPanel.setFields( hauskeysUser );
        }

        descriptionTextArea.setText( user.getDescription() );
    }


    public void setTree( JTree tree )
    {
        this.tree = tree;
    }


    public void setTreeNode( DefaultMutableTreeNode node )
    {
        this.node = node;
        this.user = ( User ) node.getUserObject();
        setUserFields();
        this.userDependentsPanel.setSelectedNode( node, tree );
    }


    public DefaultMutableTreeNode getTreeNode()
    {
        return node;
    }


    public void saveAction( HauskeysUser hauskeysUser )
    {
        HauskeysUserModifier modifier = hauskeysUser.modifier().setDescription( descriptionTextArea.getText() );
        modifier.setDisabled( disabledCheckBox.isSelected() );
        userInfoPanel.alterModifier( modifier );
        
        if ( ! userInfoPanel.isPasswordOk() )
        {
            JOptionPane.showMessageDialog( this, "Passwords are not equal or are invalid.  Save aborted!" );
            return;
        }
        
        hotpSettingsPanel.alterModifier( modifier );
        provisioningPanel.alterModifier( modifier );

        if ( modifier.isUpdateNeeded() )
        {
            try
            {
                user = modifier.modify();
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to modify user:\n" + e.getMessage(), 79 ),
                    "User modification failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
            node.setUserObject( user );
        }

        if ( !user.getId().equals( userNorthPanel.getId() ) )
        {
            try
            {
                user = hauskeysUser.modifier().rename( userNorthPanel.getId() );
                ( ( DefaultTreeModel ) tree.getModel() ).valueForPathChanged( new TreePath( node.getPath() ), user );
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to rename user:\n" + e.getMessage(), 79 ),
                    "User rename failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        node.setUserObject( user );
        setUserFields();
    }


    public void saveAction( LocalUser localUser )
    {
        LocalUserModifier modifier = localUser.modifier().setDescription( descriptionTextArea.getText() );
        modifier.setDisabled( disabledCheckBox.isSelected() );
        userInfoPanel.alterModifier( modifier );
        
        if ( ! userInfoPanel.isPasswordOk() )
        {
            JOptionPane.showMessageDialog( this, "Passwords are not equal or are invalid.  Save aborted!" );
            return;
        }
        
        if ( modifier.isUpdateNeeded() )
        {
            try
            {
                user = modifier.modify();
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to modify user:\n" + e.getMessage(), 79 ),
                    "User modification failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
            node.setUserObject( user );
        }

        if ( !user.getId().equals( userNorthPanel.getId() ) )
        {
            try
            {
                user = localUser.modifier().rename( userNorthPanel.getId() );
                ( ( DefaultTreeModel ) tree.getModel() ).valueForPathChanged( new TreePath( node.getPath() ), user );
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to rename user:\n" + e.getMessage(), 79 ),
                    "User rename failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        node.setUserObject( user );
        setUserFields();
    }


    public void saveAction( ExternalUser externalUser )
    {
        ExternalUserModifier modifier = externalUser.modifier().setDescription( descriptionTextArea.getText() );
        modifier.setDisabled( disabledCheckBox.isSelected() );
        externalLinkPanel.alterModifier( modifier );
        if ( modifier.isUpdateNeeded() )
        {
            try
            {
                user = modifier.modify();
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to modify user:\n" + e.getMessage(), 79 ),
                    "User modification failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
            node.setUserObject( user );
        }

        if ( !user.getId().equals( userNorthPanel.getId() ) )
        {
            try
            {
                user = externalUser.modifier().rename( userNorthPanel.getId() );
                ( ( DefaultTreeModel ) tree.getModel() ).valueForPathChanged( new TreePath( node.getPath() ), user );
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to rename user:\n" + e.getMessage(), 79 ),
                    "User rename failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        node.setUserObject( user );
        setUserFields();
    }


    public void saveAction()
    {
        if ( user instanceof ExternalUser )
        {
            saveAction( ( ExternalUser ) user );
        }
        else if ( user instanceof LocalUser )
        {
            saveAction( ( LocalUser ) user );
        }
        else if ( user instanceof HauskeysUser )
        {
            saveAction( ( HauskeysUser ) user );
        }
        else
        {
            throw new IllegalStateException( "Unknown user type: " + user.getClass() );
        }
    }


    public void statusChanged( StatusObject obj )
    {
        if ( ! obj.isUpToDate() )
        {
//            userNorthPanel.setStatus( Color.RED, "Save needed!" );
            return;
        }
        
        if ( isUpToDate() && userNorthPanel.isUpToDate() && userInfoPanel.isUpToDate() 
            && provisioningPanel.isUpToDate() && hotpSettingsPanel.isUpToDate() )
        {
//            userNorthPanel.setStatus( Color.GREEN, "Up to date!" );
        }
    }


    public boolean isUpToDate()
    {
        return UiUtils.isFieldUpToDate( descriptionTextArea, user.getDescription() );
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


    /**
     * This method initializes jCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckBox()
    {
        if ( disabledCheckBox == null )
        {
            disabledCheckBox = new JCheckBox();
        }
        return disabledCheckBox;
    }
} //  @jve:decl-index=0:visual-constraint="10,10"
