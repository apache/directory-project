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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JTree;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.Enumeration;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.Role;
import org.safehaus.triplesec.admin.RoleModifier;


public class RolePanel extends JPanel
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
    private RoleGrantsPanel roleGrantsPanel;
    private JTree tree = null;
    private Role role = null;
    private DefaultMutableTreeNode node = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JTextField statusTextField = null;
    private JTextField applicationNameTextField = null;
    private JTextField roleNameTextField = null;
    private JButton deleteButton = null;
    private RoleDependentsPanel roleDependentsPanel;


    /**
     * This is the default constructor
     */
    public RolePanel()
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Existing Role",
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
            buttonPanel.add(getDeleteButton(), null);
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
                        setRoleFields();
                    }
                    catch ( DataAccessException e1 )
                    {
                        JOptionPane.showMessageDialog( RolePanel.this,
                            "Failed to access application permissions for role: " + role.getName() + "\n\n"
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
            centerTabbedPane.addTab( "General", null, getGeneralPanelTab(), null );
            centerTabbedPane.addTab( "Grants", null, getRoleGrantsPanel(), null );
            centerTabbedPane.addTab( "Dependent Profiles",  null, getRoleDependentsPanel(), null );
        }
        return centerTabbedPane;
    }


    private RoleDependentsPanel getRoleDependentsPanel()
    {
        if ( roleDependentsPanel == null )
        {
            roleDependentsPanel = new RoleDependentsPanel();
        }
        return roleDependentsPanel;
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
    private JPanel getGeneralPanelTab()
    {
        if ( generalPanel == null )
        {
            generalPanel = new GeneralPanel();
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
                "/org/safehaus/triplesec/admin/swing/role_48x48.png" ) ) );
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
            gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 2;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.gridx = 1;
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
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new java.awt.Insets( 0, 0, 0, 5 );
            gridBagConstraints4.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText( "Role Name:" );
            jLabel2.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new java.awt.Insets( 0, 0, 5, 5 );
            gridBagConstraints3.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText( "Application Name:" );
            jLabel1.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new java.awt.Insets( 0, 0, 5, 5 );
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText( "Status:" );
            jLabel.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jPanel = new JPanel();
            jPanel.setLayout( new GridBagLayout() );
            jPanel.setPreferredSize( new java.awt.Dimension( 131, 88 ) );
            jPanel.add( jLabel, gridBagConstraints2 );
            jPanel.add( jLabel1, gridBagConstraints3 );
            jPanel.add( jLabel2, gridBagConstraints4 );
            jPanel.add( getStatusTextField(), gridBagConstraints15 );
            jPanel.add( getApplicationNameTextField(), gridBagConstraints16 );
            jPanel.add( getRoleNameTextField(), gridBagConstraints17 );
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


    private void setRoleFields() throws DataAccessException
    {
        if ( role == null || node == null || node.getParent() == null )
        {
            return;
        }
        generalPanel.setFields( role );
        applicationNameTextField.setText( role.getApplicationName() );
        roleNameTextField.setText( role.getName() );
        descriptionTextArea.setText( role.getDescription() );
        roleGrantsPanel.populateLists( ( DefaultMutableTreeNode ) node.getParent().getParent(), role.getGrants() );
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
        this.role = ( Role ) node.getUserObject();

        try
        {
            setRoleFields();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( RolePanel.this, "Failed to access application permissions for role: "
                + role.getName() + "\n\n" + e.getMessage(), "Data access error", JOptionPane.ERROR_MESSAGE );
        }
        roleDependentsPanel.setSelectedNode( node );
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
        if ( role == null )
        {
            return;
        }

        // change the description and add remove values from list views then see if anything changed
        RoleModifier modifier = role.modifier().setDescription( descriptionTextArea.getText() );
        for ( Enumeration ii = roleGrantsPanel.getAvailablePermssionsModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.removeGrant( ( String ) ii.nextElement() );
        }
        for ( Enumeration ii = roleGrantsPanel.getRoleGrantsModel().elements(); ii.hasMoreElements(); /**/)
        {
            modifier.addGrant( ( String ) ii.nextElement() );
        }

        if ( modifier.isUpdateNeeded() )
        {
            try
            {
                role = modifier.modify();
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to modify role:\n" + e.getMessage(), 79 ),
                    "Role modification failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
            node.setUserObject( role );
        }

        if ( !role.getName().equals( roleNameTextField.getText() ) )
        {
            try
            {
                role = role.modifier().rename( roleNameTextField.getText() );
                ( ( DefaultTreeModel ) tree.getModel() ).valueForPathChanged( new TreePath( node.getPath() ), role );
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to rename role:\n" + e.getMessage(), 79 ),
                    "Role rename failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        node.setUserObject( role );
        try
        {
            setRoleFields();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( RolePanel.this, "Failed to access application permissions for role: "
                + role.getName() + "\n\n" + e.getMessage(), "Data access error", JOptionPane.ERROR_MESSAGE );
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
            applicationNameTextField.setEditable( false );
        }
        return applicationNameTextField;
    }


    /**
     * This method initializes jTextField2	
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
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDeleteButton()
    {
        if ( deleteButton == null )
        {
            deleteButton = new JButton();
            deleteButton.setText("Delete");
            deleteButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    try
                    {
                        role.modifier().delete();
                        DefaultMutableTreeNode parentNode = ( DefaultMutableTreeNode ) node.getParent();
                        DefaultTreeModel treeModel = ( DefaultTreeModel ) tree.getModel();
                        treeModel.removeNodeFromParent( node );
                        TreePath path = new TreePath( parentNode.getPath() );
                        tree.setSelectionPaths( new TreePath[] { path } );
                    }
                    catch ( DataAccessException e1 )
                    {
                        JOptionPane.showMessageDialog( RolePanel.this, 
                            "Failed to delete role: " + e1.getMessage(), "Delete Failed", 
                            JOptionPane.ERROR_MESSAGE );
                    }
                }
            } );
        }
        return deleteButton;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
