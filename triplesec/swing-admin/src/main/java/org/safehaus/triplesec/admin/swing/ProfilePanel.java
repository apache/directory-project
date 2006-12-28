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

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JTree;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.Profile;
import org.safehaus.triplesec.admin.ProfileModifier;
import org.safehaus.triplesec.admin.User;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;


public class ProfilePanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;
    private JButton revertButton = null;
    private JButton saveButton = null;
    private JPanel aboveButtonPanel = null;
    private JPanel northPanel = null;
    private JTabbedPane centerTabbedPane = null;
    private JPanel southPanel = null;
    private GeneralPanel generalPanel = null;
    private JLabel iconLabel = null;
    private JPanel jPanel = null;
    private JTextArea descriptionTextArea = null;
    private JPanel jPanel4 = null;
    private JTree tree = null;
    private Profile profile = null;
    private DefaultMutableTreeNode node = null;
    private DefaultComboBoxModel usersComboBoxModel = new DefaultComboBoxModel();
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JTextField statusTextField = null;
    private JTextField applicationNameTextField = null;
    private JButton deleteButton = null;
    private JPanel jPanel1 = null;
    private JTextField profileIdTextField = null;
    private JLabel jLabel7 = null;
    private JComboBox usersComboBox = null;
    private ProfilePermissionsPanel profileGrantsPanel;
    private ProfilePermissionsPanel profileDenialsPanel;
    private ProfileRolesPanel profileRolesPanel;
    private JLabel jLabel3 = null;
    private JCheckBox disabledCheckBox = null;


    /**
     * This is the default constructor
     */
    public ProfilePanel()
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Existing Profile",
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
                    try
                    {
                        setProfileFields();
                    }
                    catch ( DataAccessException e1 )
                    {
                        JOptionPane.showMessageDialog( ProfilePanel.this,
                            "Failed to access application permissions for profile: " + profile.getId() + "\n\n"
                                + e1.getMessage(), "Data access error", JOptionPane.ERROR_MESSAGE );
                    }
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
            aboveButtonPanel.add( getNorthPanel(), java.awt.BorderLayout.NORTH );
            aboveButtonPanel.add( getCenterTabbedPane(), java.awt.BorderLayout.CENTER );
            aboveButtonPanel.add( getSouthPanel(), java.awt.BorderLayout.SOUTH );
        }
        return aboveButtonPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel()
    {
        if ( northPanel == null )
        {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.gridheight = 3;
            gridBagConstraints1.insets = new java.awt.Insets( 0, 10, 0, 10 );
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridy = 0;
            northPanel = new JPanel();
            northPanel.setLayout( new GridBagLayout() );
            northPanel.setPreferredSize( new java.awt.Dimension( 179, 68 ) );
            northPanel.add( getJPanel4(), new GridBagConstraints() );
            northPanel.add( getJPanel(), gridBagConstraints1 );
        }
        return northPanel;
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
            centerTabbedPane.addTab( "Grants", null, getProfileGrantsPanel(), null );
            centerTabbedPane.addTab( "Denials", null, getProfileDenialsPanel(), null );
            centerTabbedPane.addTab( "Roles", null, getProfileRolesPanel(), null );
        }
        return centerTabbedPane;
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
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private GeneralPanel getGeneralPanel()
    {
        if ( generalPanel == null )
        {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new java.awt.Insets(0,0,0,05);
            gridBagConstraints6.gridy = 4;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints5.gridy = 4;
            jLabel3 = new JLabel();
            jLabel3.setText("Disabled:");
            jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            generalPanel = new GeneralPanel();
            generalPanel.add(jLabel3, gridBagConstraints5);
            generalPanel.add(getDisabledCheckBox(), gridBagConstraints6);
        }
        return generalPanel;
    }


    /**
     * This method initializes iconLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getIconLabel()
    {
        if ( iconLabel == null )
        {
            iconLabel = new JLabel();
            iconLabel.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/profile2_48x48.png" ) ) );
            iconLabel.setPreferredSize( new java.awt.Dimension( 48, 48 ) );
            iconLabel.setText( "" );
            iconLabel.setVerticalTextPosition( javax.swing.SwingConstants.BOTTOM );
            iconLabel.setVerticalAlignment( javax.swing.SwingConstants.BOTTOM );
            iconLabel.setEnabled( true );
        }
        return iconLabel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel()
    {
        if ( jPanel == null )
        {
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 1;
            gridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints17.gridy = 2;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridy = 1;
            gridBagConstraints16.weightx = 1.0;
            gridBagConstraints16.insets = new java.awt.Insets( 0, 0, 5, 0 );
            gridBagConstraints16.gridx = 1;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new java.awt.Insets( 0, 0, 5, 0 );
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new java.awt.Insets( 0, 0, 5, 5 );
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new java.awt.Insets( 0, 0, 0, 5 );
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText( "Profile Id:" );
            jLabel2.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new java.awt.Insets( 0, 0, 5, 5 );
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText( "Application Name:" );
            jLabel1.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jLabel = new JLabel();
            jLabel.setText( "Status:" );
            jLabel.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jPanel = new JPanel();
            jPanel.setLayout( new GridBagLayout() );
            jPanel.setPreferredSize( new java.awt.Dimension( 131, 88 ) );
            jPanel.add( jLabel, gridBagConstraints4 );
            jPanel.add( jLabel1, gridBagConstraints2 );
            jPanel.add( jLabel2, gridBagConstraints3 );
            jPanel.add( getStatusTextField(), gridBagConstraints15 );
            jPanel.add( getApplicationNameTextField(), gridBagConstraints16 );
            jPanel.add( getJPanel1(), gridBagConstraints17 );
        }
        return jPanel;
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
        }
        return descriptionTextArea;
    }


    /**
     * This method initializes jPanel4
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel4()
    {
        if ( jPanel4 == null )
        {
            jPanel4 = new JPanel();
            jPanel4.setBorder( javax.swing.BorderFactory.createEtchedBorder( javax.swing.border.EtchedBorder.RAISED ) );
            jPanel4.add( getIconLabel(), null );
        }
        return jPanel4;
    }


    private void setProfileFields() throws DataAccessException
    {
        if ( profile == null || node == null || node.getParent() == null )
        {
            return;
        }
        disabledCheckBox.setSelected( profile.isDisabled() );
        generalPanel.setFields( profile );
        applicationNameTextField.setText( profile.getApplicationName() );
        profileIdTextField.setText( profile.getId() );
        descriptionTextArea.setText( profile.getDescription() );

        DefaultMutableTreeNode applicationNode = ( DefaultMutableTreeNode ) node.getParent().getParent();

        // -------------------------------------------------------------------
        // load the grants, denials and roles into respective panels
        // -------------------------------------------------------------------

        Set grants = profile.getGrants();
        profileGrantsPanel.populateLists( applicationNode, grants );
        Set denials = profile.getDenials();
        profileDenialsPanel.populateLists( applicationNode, denials );
        Set roles = profile.getRoles();
        profileRolesPanel.populateLists( applicationNode, roles );

        // -------------------------------------------------------------------
        // clear and load users into the user combo box
        // -------------------------------------------------------------------

        usersComboBoxModel.removeAllElements();
        DefaultMutableTreeNode rootNode = ( DefaultMutableTreeNode ) applicationNode.getParent().getParent();
        DefaultMutableTreeNode usersNode = null;
        for ( Enumeration ii = rootNode.children(); ii.hasMoreElements(); /**/)
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( child.getUserObject() instanceof String )
            {
                if ( "Users".equals( child.getUserObject() ) )
                {
                    usersNode = child;
                }
            }
        }

        for ( Enumeration ii = usersNode.children(); ii.hasMoreElements(); /**/)
        {
            DefaultMutableTreeNode userNode = ( DefaultMutableTreeNode ) ii.nextElement();
            usersComboBoxModel.addElement( ( ( User ) userNode.getUserObject() ).getId() );
        }
        usersComboBox.setSelectedItem( profile.getUser() );
    }


    public void setTree( JTree tree )
    {
        this.tree = tree;
    }


    public void setTreeNode( DefaultMutableTreeNode node )
    {
        if ( node == null )
        {
            return;
        }
        this.node = node;
        this.profile = ( Profile ) node.getUserObject();

        try
        {
            setProfileFields();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( ProfilePanel.this, "Failed to access application permissions for profile: "
                + profile.getId() + "\n\n" + e.getMessage(), "Data access error", JOptionPane.ERROR_MESSAGE );
        }
    }


    public String noNull( Object obj )
    {
        if ( obj == null )
        {
            return "";
        }
        return obj.toString();
    }


    public DefaultMutableTreeNode getTreeNode()
    {
        return node;
    }


    public void saveAction()
    {
        if ( profile == null )
        {
            return;
        }

        // change the description and add remove values from list views then see if anything changed
        ProfileModifier modifier = profile.modifier().setDescription( descriptionTextArea.getText() );
        modifier.setUser( ( String ) usersComboBox.getSelectedItem() );
        modifier.setDisable( disabledCheckBox.isSelected() );
        for ( Enumeration ii = profileGrantsPanel.getAvailableModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.removeGrant( ( String ) ii.nextElement() );
        }
        for ( Enumeration ii = profileGrantsPanel.getExistingModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.addGrant( ( String ) ii.nextElement() );
        }

        for ( Enumeration ii = profileDenialsPanel.getAvailableModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.removeDenial( ( String ) ii.nextElement() );
        }
        for ( Enumeration ii = profileDenialsPanel.getExistingModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.addDenial( ( String ) ii.nextElement() );
        }

        for ( Enumeration ii = profileRolesPanel.getAvailableRolesModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.removeRole( ( String ) ii.nextElement() );
        }
        for ( Enumeration ii = profileRolesPanel.getProfileRolesModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.addRole( ( String ) ii.nextElement() );
        }

        if ( modifier.isUpdateNeeded() )
        {
            try
            {
                profile = modifier.modify();
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this,
                    UiUtils.wrap( "Failed to modify profile:\n" + e.getMessage(), 79 ),
                    "Profile modification failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        if ( !profile.getId().equals( profileIdTextField.getText() ) )
        {
            try
            {
                profile = profile.modifier().rename( profileIdTextField.getText() );
                ( ( DefaultTreeModel ) tree.getModel() ).valueForPathChanged( new TreePath( node.getPath() ), profile );
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this,
                    UiUtils.wrap( "Failed to rename profile:\n" + e.getMessage(), 79 ), "Profile rename failure!",
                    JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        node.setUserObject( profile );
        try
        {
            setProfileFields();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( ProfilePanel.this, "Failed to access application permissions for profile: "
                + profile.getId() + "\n\n" + e.getMessage(), "Data access error", JOptionPane.ERROR_MESSAGE );
        }
    }


    /**
     * This method initializes jPanel5
     * 
     * @return javax.swing.JPanel
     */
    private ProfilePermissionsPanel getProfileGrantsPanel()
    {
        if ( profileGrantsPanel == null )
        {
            profileGrantsPanel = new ProfilePermissionsPanel();
        }
        return profileGrantsPanel;
    }


    /**
     * This method initializes jPanel5
     * 
     * @return javax.swing.JPanel
     */
    private ProfilePermissionsPanel getProfileDenialsPanel()
    {
        if ( profileDenialsPanel == null )
        {
            profileDenialsPanel = new ProfilePermissionsPanel( false );
        }
        return profileDenialsPanel;
    }

    
    private ProfileRolesPanel getProfileRolesPanel()
    {
        if ( profileRolesPanel == null )
        {
            profileRolesPanel = new ProfileRolesPanel();
        }
        return profileRolesPanel;
    }
    

    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getStatusTextField()
    {
        if ( statusTextField == null )
        {
            statusTextField = new JTextField();
            statusTextField.setEditable( false );
        }
        return statusTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getApplicationNameTextField()
    {
        if ( applicationNameTextField == null )
        {
            applicationNameTextField = new JTextField();
            applicationNameTextField.setEditable( false );
        }
        return applicationNameTextField;
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
                    try
                    {
                        profile.modifier().delete();
                        DefaultMutableTreeNode parentNode = ( DefaultMutableTreeNode ) node.getParent();
                        DefaultTreeModel treeModel = ( DefaultTreeModel ) tree.getModel();
                        treeModel.removeNodeFromParent( node );
                        TreePath path = new TreePath( parentNode.getPath() );
                        tree.setSelectionPaths( new TreePath[]
                            { path } );
                    }
                    catch ( DataAccessException e1 )
                    {
                        JOptionPane.showMessageDialog( ProfilePanel.this, "Failed to delete profile: "
                            + e1.getMessage(), "Delete Failed", JOptionPane.ERROR_MESSAGE );
                    }
                }
            } );
        }
        return deleteButton;
    }


    /**
     * This method initializes jPanel1	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel1()
    {
        if ( jPanel1 == null )
        {
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints20.weightx = 1.0;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints19.insets = new java.awt.Insets( 0, 5, 0, 5 );
            jLabel7 = new JLabel();
            jLabel7.setText( "User:" );
            jLabel7.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.gridy = 0;
            gridBagConstraints18.weightx = 1.0;
            gridBagConstraints18.gridx = 0;
            jPanel1 = new JPanel();
            jPanel1.setLayout( new GridBagLayout() );
            jPanel1.add( getJTextField(), gridBagConstraints18 );
            jPanel1.add( jLabel7, gridBagConstraints19 );
            jPanel1.add( getJComboBox(), gridBagConstraints20 );
        }
        return jPanel1;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField()
    {
        if ( profileIdTextField == null )
        {
            profileIdTextField = new JTextField();
        }
        return profileIdTextField;
    }


    /**
     * This method initializes jComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJComboBox()
    {
        if ( usersComboBox == null )
        {
            usersComboBox = new JComboBox();
            usersComboBox.setModel( usersComboBoxModel );
            usersComboBox.setPreferredSize( new java.awt.Dimension( 32, 19 ) );
        }
        return usersComboBox;
    }


    /**
     * This method initializes jCheckBox	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getDisabledCheckBox()
    {
        if ( disabledCheckBox == null )
        {
            disabledCheckBox = new JCheckBox();
        }
        return disabledCheckBox;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
