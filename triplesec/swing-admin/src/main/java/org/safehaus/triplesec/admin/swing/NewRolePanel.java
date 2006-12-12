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
import org.safehaus.triplesec.admin.Role;
import org.safehaus.triplesec.admin.RoleModifier;


public class NewRolePanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;
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
    private JTable existingRolesTable = null;
    private ExistingRolesTableModel existingRolesTableModel = null; //  @jve:decl-index=0:visual-constraint=""
    private JLabel jLabel = null;
    private JTextField statusTextField = null;
    private JLabel jLabel1 = null;
    private JTextField roleNameTextField = null;
    private JLabel jLabel2 = null;
    private JTextField applicationNameTextField = null;
    private RoleGrantsPanel roleGrantsPanel;
    
    
    public void setLeftTreeNavigation( LeftTreeNavigation leftTreeNavigation )
    {
        this.leftTreeNavigation = leftTreeNavigation;
    }


    /**
     * This is the default constructor
     */
    public NewRolePanel()
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "New Role",
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
                "/org/safehaus/triplesec/admin/swing/new_role_48x48.png" ) ) );
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
            gridBagConstraints6.insets = new java.awt.Insets( 0, 0, 5, 5 );
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new java.awt.Insets( 0, 0, 0, 5 );
            gridBagConstraints3.gridy = 2;
            jLabel1 = new JLabel();
            jLabel1.setText("Role Name:");
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
            jPanel.add(getRoleNameTextField(), gridBagConstraints5);
            jPanel.add(jLabel2, gridBagConstraints7);
            jPanel.add(getApplicationNameTextField(), gridBagConstraints8);
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
        existingRolesTable.setModel( new ExistingRolesTableModel() );
        DefaultMutableTreeNode appNode = ( DefaultMutableTreeNode ) node.getParent();
        Application application = ( Application ) appNode.getUserObject();
        applicationNameTextField.setText( application.getName() );
        roleGrantsPanel.populateLists( appNode, Collections.EMPTY_SET );
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
        if ( roleNameTextField.getText() == null || roleNameTextField.getText().equals( "" ) )
        {
            return;
        }
        
        DefaultMutableTreeNode appNode = ( DefaultMutableTreeNode ) node.getParent();
        Application application = ( Application ) appNode.getUserObject();
        Role role;
        RoleModifier modifier = application.modifier().newRole( roleNameTextField.getText() )
            .setDescription( descriptionTextArea.getText() );
        
        for ( Enumeration ii = roleGrantsPanel.getRoleGrantsModel().elements(); ii.hasMoreElements(); /**/ )
        {
            modifier.addGrant( ( String ) ii.nextElement() );
        }
        for ( Enumeration ii = roleGrantsPanel.getAvailablePermssionsModel().elements(); ii.hasMoreElements(); /**/ )
        {
            modifier.removeGrant( ( String ) ii.nextElement() );
        }
        
        try
        {
            role = modifier.add();
            DefaultMutableTreeNode roleNode = new DefaultMutableTreeNode( role );
            DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
            model.insertNodeInto( roleNode, node, 0 );
            existingRolesTableModel.fireTableDataChanged();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( this,
                UiUtils.wrap( "Failed to create role:\n" + e.getMessage(), 79 ),
                "Role creation failure!", JOptionPane.ERROR_MESSAGE );
            return;
        }
        roleNameTextField.setText( null );
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
                "Copy a role from any one of these existing roles." );
            centerTabbedPane.addTab( "Grants", null, getRoleGrantsPanel() );
        }
        return centerTabbedPane;
    }

    
    private RoleGrantsPanel getRoleGrantsPanel()
    {
        if ( roleGrantsPanel == null )
        {
            roleGrantsPanel = new RoleGrantsPanel();
        }
        return roleGrantsPanel;
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
            jScrollPane.setViewportView( getExistingRolesTable() );
        }
        return jScrollPane;
    }


    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getExistingRolesTable()
    {
        if ( existingRolesTable == null )
        {
            existingRolesTable = new JTable();
            existingRolesTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            existingRolesTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
            {
                public void valueChanged( ListSelectionEvent e )
                {
                    int index = existingRolesTable.getSelectionModel().getAnchorSelectionIndex();
                    if ( existingRolesTableModel.getRowCount() == 0 || index < 0 )
                    {
                        return;
                    }
                    Role role = ( Role ) existingRolesTableModel.getValueAt( index, 0 );
                    roleNameTextField.setText( "CopyOf" + role.getName() );
                    descriptionTextArea.setText( role.getDescription() );
                    roleGrantsPanel.populateLists( ( DefaultMutableTreeNode ) node.getParent(), role.getGrants() );
                }
            } );
            existingRolesTable.setModel( getExistingRolesTableModel() );
        }
        return existingRolesTable;
    }


    /**
     * This method initializes defaultTableModel	
     * 	
     * @return javax.swing.table.DefaultTableModel	
     */
    private ExistingRolesTableModel getExistingRolesTableModel()
    {
        if ( existingRolesTableModel == null )
        {
            existingRolesTableModel = new ExistingRolesTableModel();
        }
        return existingRolesTableModel;
    }
    

    class ExistingRolesTableModel extends AbstractTableModel
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
                    return ( ( Role ) child.getUserObject() ).getCreatorsName();
                case ( 2 ):
                    return ( ( Role) child.getUserObject() ).getCreateTimestamp();
            }
            return child.getUserObject();
        }

    
        public String getColumnName( int columnIndex )
        {
            switch ( columnIndex )
            {
                case ( 0 ):
                    return "Existing Role";
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
    private JTextField getRoleNameTextField()
    {
        if ( roleNameTextField == null )
        {
            roleNameTextField = new JTextField();
        }
        return roleNameTextField;
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
} //  @jve:decl-index=0:visual-constraint="10,10"
