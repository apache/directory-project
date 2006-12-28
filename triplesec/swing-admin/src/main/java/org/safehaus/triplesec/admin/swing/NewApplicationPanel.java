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
import java.util.Enumeration;
import java.util.Iterator;

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
import org.safehaus.triplesec.admin.ApplicationModifier;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.Permission;
import org.safehaus.triplesec.admin.Profile;
import org.safehaus.triplesec.admin.ProfileModifier;
import org.safehaus.triplesec.admin.Role;
import org.safehaus.triplesec.admin.RoleModifier;
import org.safehaus.triplesec.admin.TriplesecAdmin;
import javax.swing.JPasswordField;


public class NewApplicationPanel extends JPanel
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
    private JTable existingAppsTable = null;
    private TriplesecAdmin triplesecAdmin;
    private ExistingApplicationsTableModel existingAppsTableModel = null; //  @jve:decl-index=0:visual-constraint=""
    private JLabel jLabel = null;
    private JTextField statusTextField = null;
    private JLabel jLabel1 = null;
    private JTextField applicationNameTextField = null;
    private JLabel jLabel2 = null;
    private JPanel jPanel1 = null;
    private JPasswordField applicationPasswordField = null;
    private JLabel jLabel3 = null;
    private JPasswordField confirmPasswordField = null;


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
    public NewApplicationPanel()
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "New Application",
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
            	public void actionPerformed(java.awt.event.ActionEvent e) 
                {
                    try
                    {
                        copyAction();
                    }
                    catch ( DataAccessException e1 )
                    {
                        String msg = UiUtils.wrap( "Failed to copy application: " + e1.getMessage(), 79 );
                        JOptionPane.showMessageDialog( NewApplicationPanel.this, msg, 
                            "Failed to copy application!", JOptionPane.ERROR_MESSAGE );
                    }
            	}
            
            } );
        }
        return copyButton;
    }

    
    public void copyAction() throws DataAccessException
    {
        String msg = UiUtils.wrap( "This copy operation will persist immediately replicating the entire " +
                "application subtree.  Would you still like to continue?", 79 ); 
        int response = JOptionPane.showOptionDialog( this, msg, "Continue?", JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE, null, null, null );
        if ( response == JOptionPane.NO_OPTION )
        {
            return;
        }
        
        // -------------------------------------------------------------------
        // Get ahold of the application and it's node that was to 
        // be copied with all child entities underneath it
        // -------------------------------------------------------------------
        
        int index = existingAppsTable.getSelectionModel().getAnchorSelectionIndex();
        if ( existingAppsTableModel.getRowCount() == 0 || index < 0 )
        {
            return;
        }
        Application copiedApplication = ( Application ) existingAppsTableModel.getValueAt( index, 0 );
        DefaultMutableTreeNode copiedApplicationNode = null;
        for ( Enumeration ii = node.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( child.getUserObject() == copiedApplication )
            {
                copiedApplicationNode = child;
            }
        }
        
        // -------------------------------------------------------------------
        // Now we create the new application and bail if it fails
        // -------------------------------------------------------------------
       
        DefaultMutableTreeNode newApplicationNode = createAction();
        if ( newApplicationNode == null )
        {
            return;
        }
        Application newApplication = ( Application ) newApplicationNode.getUserObject();

        // -------------------------------------------------------------------
        // Iterated and copy app's perms and create in new app
        // -------------------------------------------------------------------
        
        DefaultMutableTreeNode permsNode = null;
        DefaultMutableTreeNode newPermsNode = null;
        DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
        for ( Enumeration ii = copiedApplicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( ( ( String ) child.getUserObject() ).equals( "Permissions" ) )
            {
                permsNode = child;
            }
        }
        for ( Enumeration ii = newApplicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( ( ( String ) child.getUserObject() ).equals( "Permissions" ) )
            {
                newPermsNode = child;
            }
        }
        for ( Enumeration ii = permsNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode copiedPermissionNode = ( DefaultMutableTreeNode ) ii.nextElement();
            Permission copiedPermission = ( Permission ) copiedPermissionNode.getUserObject();
            Permission newPermission = newApplication.modifier()
                .newPermission( copiedPermission.getName() )
                .setDescription( copiedPermission.getDescription() ).add();
            model.insertNodeInto( new DefaultMutableTreeNode( newPermission ), newPermsNode, 0 );
        }
        
        // -------------------------------------------------------------------
        // Iterate and copy app's roles and create in new app
        // -------------------------------------------------------------------
        
        DefaultMutableTreeNode rolesNode = null;
        DefaultMutableTreeNode newRolesNode = null;
        for ( Enumeration ii = copiedApplicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( ( ( String ) child.getUserObject() ).equals( "Roles" ) )
            {
                rolesNode = child;
            }
        }
        for ( Enumeration ii = newApplicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( ( ( String ) child.getUserObject() ).equals( "Roles" ) )
            {
                newRolesNode = child;
            }
        }
        for ( Enumeration ii = rolesNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode copiedRoleNode = ( DefaultMutableTreeNode ) ii.nextElement();
            Role copiedRole = ( Role ) copiedRoleNode.getUserObject();
            RoleModifier modifier = newApplication.modifier().newRole( copiedRole.getName() )
                .setDescription( copiedRole.getDescription() );
            for ( Iterator jj = copiedRole.getGrants().iterator(); jj.hasNext(); /**/ )
            {
                modifier.addGrant( ( String ) jj.next() );
            }
            Role newRole = modifier.add();
            model.insertNodeInto( new DefaultMutableTreeNode( newRole ), newRolesNode, 0 );
        }
        
        // -------------------------------------------------------------------
        // Iterate and copy app's profiles and create in new app
        // -------------------------------------------------------------------
        
        DefaultMutableTreeNode profilesNode = null;
        DefaultMutableTreeNode newProfilesNode = null;
        for ( Enumeration ii = copiedApplicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( ( ( String ) child.getUserObject() ).equals( "Profiles" ) )
            {
                profilesNode = child;
            }
        }
        for ( Enumeration ii = newApplicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( ( ( String ) child.getUserObject() ).equals( "Profiles" ) )
            {
                newProfilesNode = child;
            }
        }
        for ( Enumeration ii = profilesNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode copiedProfileNode = ( DefaultMutableTreeNode ) ii.nextElement();
            Profile copiedProfile = ( Profile ) copiedProfileNode.getUserObject();
            ProfileModifier modifier = newApplication.modifier()
                .newProfile( copiedProfile.getId(), copiedProfile.getUser() )
                .setDescription( copiedProfile.getDescription() );
            for ( Iterator jj = copiedProfile.getGrants().iterator(); jj.hasNext(); /**/ )
            {
                modifier.addGrant( ( String ) jj.next() );
            }
            for ( Iterator jj = copiedProfile.getDenials().iterator(); jj.hasNext(); /**/ )
            {
                modifier.addDenial( ( String ) jj.next() );
            }
            for ( Iterator jj = copiedProfile.getRoles().iterator(); jj.hasNext(); /**/ )
            {
                modifier.addRole( ( String ) jj.next() );
            }
            Profile newProfile = modifier.add();
            model.insertNodeInto( new DefaultMutableTreeNode( newProfile ), newProfilesNode, 0 );
        }
        
        applicationNameTextField.setText( null );
        statusTextField.setText( null );
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
                "/org/safehaus/triplesec/admin/swing/new_application_48x48.png" ) ) );
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
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.insets = new java.awt.Insets(0,0,0,0);
            gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.gridy = 2;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints7.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Password:");
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new java.awt.Insets( 0, 0, 5, 5 );
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new java.awt.Insets(0,0,5,0);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints3.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText( "Application Name:" );
            jLabel1.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new java.awt.Insets( 0, 0, 5, 0 );
            gridBagConstraints2.gridx = 1;
            jLabel = new JLabel();
            jLabel.setText( "Status:" );
            jLabel.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jPanel = new JPanel();
            jPanel.setLayout( new GridBagLayout() );
            jPanel.setPreferredSize( new java.awt.Dimension( 131, 88 ) );
            jPanel.add( jLabel, gridBagConstraints6 );
            jPanel.add( getStatusTextField(), gridBagConstraints2 );
            jPanel.add(jLabel1, gridBagConstraints3);
            jPanel.add(getApplicationNameTextField(), gridBagConstraints5);
            jPanel.add(jLabel2, gridBagConstraints7);
            jPanel.add(getJPanel1(), gridBagConstraints8);
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
        existingAppsTable.setModel( new ExistingApplicationsTableModel() );
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


    public DefaultMutableTreeNode createAction()
    {
        char[] passwordChars = applicationPasswordField.getPassword();
        char[] confirmChars = confirmPasswordField.getPassword();
        
        if ( passwordChars == null || confirmChars == null )
        {
            JOptionPane.showMessageDialog( this, "Either the password or the confirmation field is null." );
            return null;
        }
        
        String passwordStr = new String( passwordChars );
        String confirmStr = new String( confirmChars );
        
        if ( ! passwordStr.equals( confirmStr ) )
        {
            JOptionPane.showMessageDialog( this, "Passwords do not match." );
            return null;
        }
        
        DefaultMutableTreeNode appNode;
        Application application;
        ApplicationModifier modifier = triplesecAdmin.newApplication( applicationNameTextField.getText() )
            .setDescription( descriptionTextArea.getText() ).setPassword( passwordStr );
        try
        {
            application = modifier.add();
            appNode = new DefaultMutableTreeNode( application );
            DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
            model.insertNodeInto( appNode, node, 0 );
            leftTreeNavigation.reloadApplication( appNode );
            leftTreeNavigation.reloadGroups();
            existingAppsTableModel.fireTableDataChanged();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( this,
                UiUtils.wrap( "Failed to create application:\n" + e.getMessage(), 79 ),
                "Application creation failure!", JOptionPane.ERROR_MESSAGE );
            return null;
        }
        applicationNameTextField.setText( null );
        statusTextField.setText( null );
        return appNode;
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
                "Copy an application from any one of these existing applications." );
        }
        return centerTabbedPane;
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
            jScrollPane.setViewportView( getExistingAppsTable() );
        }
        return jScrollPane;
    }


    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getExistingAppsTable()
    {
        if ( existingAppsTable == null )
        {
            existingAppsTable = new JTable();
            existingAppsTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            existingAppsTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
            {
                public void valueChanged( ListSelectionEvent e )
                {
                    int index = existingAppsTable.getSelectionModel().getAnchorSelectionIndex();
                    if ( existingAppsTableModel.getRowCount() == 0 || index < 0 )
                    {
                        return;
                    }
                    Application application = ( Application ) existingAppsTableModel.getValueAt( index, 0 );
                    applicationNameTextField.setText( "CopyOf" + application.getName() );
                    descriptionTextArea.setText( application.getDescription() );
                }
            } );
            existingAppsTable.setModel( getExistingAppsTableModel() );
        }
        return existingAppsTable;
    }


    /**
     * This method initializes defaultTableModel	
     * 	
     * @return javax.swing.table.DefaultTableModel	
     */
    private ExistingApplicationsTableModel getExistingAppsTableModel()
    {
        if ( existingAppsTableModel == null )
        {
            existingAppsTableModel = new ExistingApplicationsTableModel();
        }
        return existingAppsTableModel;
    }

    class ExistingApplicationsTableModel extends AbstractTableModel
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
                    return ( ( Application ) child.getUserObject() ).getCreatorsName();
                case ( 2 ):
                    return ( ( Application ) child.getUserObject() ).getCreateTimestamp().toString();
            }
            return child.getUserObject();
        }

    
        public String getColumnName( int columnIndex )
        {
            switch ( columnIndex )
            {
                case ( 0 ):
                    return "Existing Application";
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
     * This method initializes jTextField1	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getApplicationNameTextField()
    {
        if ( applicationNameTextField == null )
        {
            applicationNameTextField = new JTextField();
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
            gridBagConstraints11.insets = new java.awt.Insets(0,5,0,5);
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.weightx = 1.0;
            jLabel3 = new JLabel();
            jLabel3.setText("Confirm");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.gridx = 0;
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(getApplicationPasswordField(), gridBagConstraints9);
            jPanel1.add(jLabel3, gridBagConstraints11);
            jPanel1.add(getConfirmPasswordField(), gridBagConstraints10);
        }
        return jPanel1;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getApplicationPasswordField()
    {
        if ( applicationPasswordField == null )
        {
            applicationPasswordField = new JPasswordField();
        }
        return applicationPasswordField;
    }


    /**
     * This method initializes jPasswordField1	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getConfirmPasswordField()
    {
        if ( confirmPasswordField == null )
        {
            confirmPasswordField = new JPasswordField();
        }
        return confirmPasswordField;
    }
} //  @jve:decl-index=0:visual-constraint="10,10"
