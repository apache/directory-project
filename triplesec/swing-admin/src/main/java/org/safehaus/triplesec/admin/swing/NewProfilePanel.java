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
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.Profile;
import org.safehaus.triplesec.admin.ProfileModifier;
import org.safehaus.triplesec.admin.User;

import javax.swing.JComboBox;


public class NewProfilePanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;
    private JButton copyButton = null;
    private JButton createButton = null;
    private JPanel aboveButtonPanel = null;
    private JPanel northPanel = null;
    private JPanel southPanel = null;
    private JLabel iconLabel = null;
    private JPanel jPanel = null;
    private JTextArea descriptionTextArea = null;
    private JPanel iconPanel = null;
    private DefaultMutableTreeNode node = null;
    private LeftTreeNavigation leftTreeNavigation;
    private JTabbedPane centerTabbedPane = null;
    private JPanel existingPanelTab = null;
    private JScrollPane jScrollPane = null;
    private JTable existingProfilesTable = null;
    private ExistingProfilesTableModel existingProfilesTableModel = null; //  @jve:decl-index=0:visual-constraint=""
    private JLabel jLabel = null;
    private JTextField statusTextField = null;
    private JLabel jLabel2 = null;
    private JTextField applicationNameTextField = null;
    private JPanel jPanel1 = null;
    private JLabel jLabel1 = null;
    private JTextField profileIdTextField = null;
    private JLabel jLabel3 = null;
    private JComboBox usersComboBox = null;
    private DefaultComboBoxModel usersComboBoxModel = new DefaultComboBoxModel();
//    private ProfilePermissionsPanel profileGrantsPanel;
//    private ProfilePermissionsPanel profileDenialsPanel;
    private ProfileRolesPanel profileRolesPanel;
    
    
    public void setLeftTreeNavigation( LeftTreeNavigation leftTreeNavigation )
    {
        this.leftTreeNavigation = leftTreeNavigation;
    }


    /**
     * This is the default constructor
     */
    public NewProfilePanel()
    {
        super();
        initialize();
    }


    /**
     * This method initializes this
     * 
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "New Profile",
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
            buttonPanel.add( getCopyButton(), null );
            buttonPanel.add( getCreateButton(), null );
        }
        return buttonPanel;
    }


    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getCopyButton()
    {
        if ( copyButton == null )
        {
            copyButton = new JButton();
            copyButton.setText( "Copy" );
            copyButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                }
            } );
        }
        return copyButton;
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
            aboveButtonPanel.add( getNorthPanel(), java.awt.BorderLayout.NORTH );
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
            northPanel.add( getIconPanel(), new GridBagConstraints() );
            northPanel.add( getJPanel(), gridBagConstraints1 );
        }
        return northPanel;
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
                "/org/safehaus/triplesec/admin/swing/new_profile2_48x48.png" ) ) );
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
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints5.gridy = 2;
            jLabel1 = new JLabel();
            jLabel1.setText("Profile Id:");
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.weightx = 2.0D;
            gridBagConstraints3.gridy = 2;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new java.awt.Insets(0,0,5,0);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints7.gridy = 1;
            jLabel2 = new JLabel();
            jLabel2.setText("Application Name:");
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new java.awt.Insets(0,0,5,5);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new java.awt.Insets(0,0,5,0);
            gridBagConstraints2.gridx = 1;
            jLabel = new JLabel();
            jLabel.setText( "Status:" );
            jLabel.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jPanel = new JPanel();
            jPanel.setLayout( new GridBagLayout() );
            jPanel.setPreferredSize( new java.awt.Dimension( 131, 88 ) );
            jPanel.add(jLabel, gridBagConstraints6);
            jPanel.add(getStatusTextField(), gridBagConstraints2);
            jPanel.add(jLabel2, gridBagConstraints7);
            jPanel.add(getApplicationNameTextField(), gridBagConstraints8);
            jPanel.add(getJPanel1(), gridBagConstraints3);
            jPanel.add(jLabel1, gridBagConstraints5);
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
    private JPanel getIconPanel()
    {
        if ( iconPanel == null )
        {
            iconPanel = new JPanel();
            iconPanel
                .setBorder( javax.swing.BorderFactory.createEtchedBorder( javax.swing.border.EtchedBorder.RAISED ) );
            iconPanel.add( getIconLabel(), null );
        }
        return iconPanel;
    }


    public void setTreeNode( DefaultMutableTreeNode node )
    {
        this.node = node;
        existingProfilesTable.setModel( new ExistingProfilesTableModel() );
        DefaultMutableTreeNode applicationNode = ( DefaultMutableTreeNode ) node.getParent();
        Application application = ( Application ) ( applicationNode ).getUserObject();
        applicationNameTextField.setText( application.getName() );
        
        // -------------------------------------------------------------------
        // clear and load users into the user combo box
        // -------------------------------------------------------------------

        usersComboBoxModel.removeAllElements();
        DefaultMutableTreeNode rootNode = ( DefaultMutableTreeNode ) node.getParent().getParent().getParent();
        DefaultMutableTreeNode usersNode = null;
        for ( Enumeration ii = rootNode.children(); ii.hasMoreElements(); /**/ )
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
        
        for ( Enumeration ii = usersNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode userNode = ( DefaultMutableTreeNode ) ii.nextElement();
            usersComboBoxModel.addElement( ( ( User ) userNode.getUserObject() ).getId() );
        }
        
        // -------------------------------------------------------------------
        // Fill up the various panels for grants, denials and roles
        // -------------------------------------------------------------------

//        profileGrantsPanel.populateLists( applicationNode, Collections.EMPTY_SET );
//        profileDenialsPanel.populateLists( applicationNode, Collections.EMPTY_SET );
        profileRolesPanel.populateLists( applicationNode, Collections.EMPTY_SET );
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


    public void createAction()
    {
        if ( profileIdTextField.getText() == null || profileIdTextField.getText().equals( "" ) )
        {
            return;
        }
        
        DefaultMutableTreeNode appNode = ( DefaultMutableTreeNode ) node.getParent();
        Application application = ( Application ) appNode.getUserObject();
        Profile profile;
        ProfileModifier modifier = application.modifier().newProfile( profileIdTextField.getText(), 
            String.valueOf( usersComboBox.getSelectedItem() ) ).setDescription( descriptionTextArea.getText() );
        
        // -------------------------------------------------------------------
        // Iterate through and add denials, grants, and roles in list panels
        // -------------------------------------------------------------------

//        for ( Enumeration ii = profileGrantsPanel.getExistingModel().elements(); ii.hasMoreElements(); /**/ )
//        {
//            modifier.addGrant( ( String ) ii.nextElement() );
//        }
//        for ( Enumeration ii = profileDenialsPanel.getExistingModel().elements(); ii.hasMoreElements(); /**/ )
//        {
//            modifier.addDenial( ( String ) ii.nextElement() );
//        }
        for ( Enumeration ii = profileRolesPanel.getProfileRolesModel().elements(); ii.hasMoreElements(); /**/ )
        {
            modifier.addRole( ( String ) ii.nextElement() );
        }
        
        try
        {
            profile = modifier.add();
            DefaultMutableTreeNode profileNode = new DefaultMutableTreeNode( profile );
            DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
            model.insertNodeInto( profileNode, node, 0 );
            existingProfilesTableModel.fireTableDataChanged();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( this,
                UiUtils.wrap( "Failed to create profile:\n" + e.getMessage(), 79 ),
                "Profile creation failure!", JOptionPane.ERROR_MESSAGE );
            return;
        }
        profileIdTextField.setText( null );
        statusTextField.setText( null );
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
                "Copy a profile from any one of these existing profiles." );
//            centerTabbedPane.addTab( "Grants", null, getProfileGrantsPanel() );
//            centerTabbedPane.addTab( "Denials", null, getProfileDenialsPanel() );
            centerTabbedPane.addTab( "Roles", null, getProfileRolesPanel() );
        }
        return centerTabbedPane;
    }


    /**
     * This method initializes jPanel5
     * 
     * @return javax.swing.JPanel
     */
//    private ProfilePermissionsPanel getProfileGrantsPanel()
//    {
//        if ( profileGrantsPanel == null )
//        {
//            profileGrantsPanel = new ProfilePermissionsPanel();
//        }
//        return profileGrantsPanel;
//    }


    /**
     * This method initializes jPanel5
     * 
     * @return javax.swing.JPanel
     */
//    private ProfilePermissionsPanel getProfileDenialsPanel()
//    {
//        if ( profileDenialsPanel == null )
//        {
//            profileDenialsPanel = new ProfilePermissionsPanel( false );
//        }
//        return profileDenialsPanel;
//    }

    
    private ProfileRolesPanel getProfileRolesPanel()
    {
        if ( profileRolesPanel == null )
        {
            profileRolesPanel = new ProfileRolesPanel();
        }
        return profileRolesPanel;
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
            jScrollPane.setViewportView( getExistingProfilesTable() );
        }
        return jScrollPane;
    }


    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getExistingProfilesTable()
    {
        if ( existingProfilesTable == null )
        {
            existingProfilesTable = new JTable();
            existingProfilesTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            existingProfilesTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
            {
                public void valueChanged( ListSelectionEvent e )
                {
                    int index = existingProfilesTable.getSelectionModel().getAnchorSelectionIndex();
                    if ( existingProfilesTableModel.getRowCount() == 0 || index < 0 )
                    {
                        return;
                    }
                    Profile profile = ( Profile ) existingProfilesTableModel.getValueAt( index, 0 );
                    profileIdTextField.setText( "CopyOf" + profile.getId() );
                    descriptionTextArea.setText( profile.getDescription() );
                    usersComboBox.setSelectedItem( profile.getUser() );

                    // -------------------------------------------------------------------
                    // Fill up the various panels for grants, denials and roles
                    // -------------------------------------------------------------------

                    DefaultMutableTreeNode applicationNode = ( DefaultMutableTreeNode ) node.getParent();
                    if ( applicationNode == null )
                    {
                        return;
                    }
                    
//                    profileGrantsPanel.populateLists( applicationNode, profile.getGrants() );
//                    profileDenialsPanel.populateLists( applicationNode, profile.getDenials() );
                    profileRolesPanel.populateLists( applicationNode, profile.getRoles() );
                }
            } );
            existingProfilesTable.setModel( getExistingProfilesTableModel() );
        }
        return existingProfilesTable;
    }


    /**
     * This method initializes defaultTableModel	
     * 	
     * @return javax.swing.table.DefaultTableModel	
     */
    private ExistingProfilesTableModel getExistingProfilesTableModel()
    {
        if ( existingProfilesTableModel == null )
        {
            existingProfilesTableModel = new ExistingProfilesTableModel();
        }
        return existingProfilesTableModel;
    }
    

    class ExistingProfilesTableModel extends AbstractTableModel
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
                    return ( ( Profile ) child.getUserObject() ).getCreatorsName();
                case ( 2 ):
                    return ( ( Profile) child.getUserObject() ).getCreateTimestamp();
            }
            return child.getUserObject();
        }

    
        public String getColumnName( int columnIndex )
        {
            switch ( columnIndex )
            {
                case ( 0 ):
                    return "Existing Profile";
                case ( 1 ):
                    return "Creator's Name";
                case ( 2 ):
                    return "Create Timestamp";
                default:
                    throw new IndexOutOfBoundsException();
            }
        }
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
            applicationNameTextField.setEditable(false);
        }
        return applicationNameTextField;
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
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.gridx = 2;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.insets = new java.awt.Insets(0,10,0,5);
            jLabel3 = new JLabel();
            jLabel3.setText("User:");
            jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.weightx = 1.0;
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(getProfileIdTextField(), gridBagConstraints9);
            jPanel1.add(jLabel3, gridBagConstraints10);
            jPanel1.add(getUsersComboBox(), gridBagConstraints11);
        }
        return jPanel1;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProfileIdTextField()
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
    private JComboBox getUsersComboBox()
    {
        if ( usersComboBox == null )
        {
            usersComboBox = new JComboBox();
            usersComboBox.setModel( usersComboBoxModel );
            usersComboBox.setPreferredSize(new java.awt.Dimension(32,19));
            usersComboBox.setEditable(false);
        }
        return usersComboBox;
    }
} //  @jve:decl-index=0:visual-constraint="10,10"
