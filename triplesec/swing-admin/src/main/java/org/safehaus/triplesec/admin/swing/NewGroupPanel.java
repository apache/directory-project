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

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.Group;
import org.safehaus.triplesec.admin.GroupModifier;
import org.safehaus.triplesec.admin.TriplesecAdmin;


public class NewGroupPanel extends JPanel
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
    private JTable existingGroupsTable = null;
    private TriplesecAdmin triplesecAdmin;
    private ExistingGroupsTableModel existingGroupsTableModel = null; //  @jve:decl-index=0:visual-constraint=""
    private JLabel jLabel = null;
    private JTextField statusTextField = null;
    private JLabel jLabel1 = null;
    private JTextField groupNameTextField = null;
    private GroupUsersPanel groupUsersPanel = null;


    public void setLeftTreeNavigation( LeftTreeNavigation leftTreeNavigation )
    {
        this.leftTreeNavigation = leftTreeNavigation;
    }


    public void setTriplesecAdmin( TriplesecAdmin triplesecAdmin )
    {
        this.triplesecAdmin = triplesecAdmin;
    }


    private GroupUsersPanel getGroupUsersPanel()
    {
        if ( groupUsersPanel == null )
        {
            groupUsersPanel = new GroupUsersPanel();
        }
        return groupUsersPanel;
    }


    /**
     * This is the default constructor
     */
    public NewGroupPanel()
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "New Group",
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
                "/org/safehaus/triplesec/admin/swing/new_group_48x48.png" ) ) );
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
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new java.awt.Insets( 0, 0, 5, 5 );
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new java.awt.Insets( 0, 0, 0, 5 );
            gridBagConstraints3.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("Group Name:");
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
            jPanel.add( jLabel1, gridBagConstraints3 );
            jPanel.add( getGroupNameTextField(), gridBagConstraints5 );
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
        existingGroupsTable.setModel( new ExistingGroupsTableModel() );
        groupUsersPanel.populateLists( ( DefaultMutableTreeNode ) node.getParent(), Collections.EMPTY_SET );
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
        if ( groupNameTextField.getText() == null || groupNameTextField.getText().equals( "" ) )
        {
            return;
        }
        Group group;
        GroupModifier modifier = triplesecAdmin.newGroup( groupNameTextField.getText(), "admin" );
        for ( Enumeration ii = groupUsersPanel.getUsersInGroupModel().elements(); ii.hasMoreElements(); /**/ )
        {
            modifier.addMember( ( String ) ii.nextElement() );
        }
     //       .setDescription( descriptionTextArea.getText() );
        try
        {
            group = modifier.add();
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode( group );
            DefaultTreeModel model = ( DefaultTreeModel ) leftTreeNavigation.getTree().getModel();
            model.insertNodeInto( groupNode, node, 0 );
            existingGroupsTableModel.fireTableDataChanged();
        }
        catch ( DataAccessException e )
        {
            JOptionPane.showMessageDialog( this,
                UiUtils.wrap( "Failed to create group:\n" + e.getMessage(), 79 ),
                "Group creation failure!", JOptionPane.ERROR_MESSAGE );
            return;
        }
        groupNameTextField.setText( null );
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
                "Copy a group from any one of these existing groups." );
            centerTabbedPane.addTab( "Users", null, getGroupUsersPanel() );
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
            jScrollPane.setViewportView( getExistingGroupsTable() );
        }
        return jScrollPane;
    }


    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getExistingGroupsTable()
    {
        if ( existingGroupsTable == null )
        {
            existingGroupsTable = new JTable();
            existingGroupsTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            existingGroupsTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
            {
                public void valueChanged( ListSelectionEvent e )
                {
                    int index = existingGroupsTable.getSelectionModel().getAnchorSelectionIndex();
                    if ( existingGroupsTableModel.getRowCount() == 0 || index < 0 )
                    {
                        return;
                    }
                    Group group = ( Group ) existingGroupsTableModel.getValueAt( index, 0 );
                    groupNameTextField.setText( "CopyOf" + group.getName() );
                    // descriptionTextArea.setText( group.getDescription() );
                    
                    groupUsersPanel.populateLists( ( DefaultMutableTreeNode ) node.getParent(), group.getMembers() );
                }
            } );
            existingGroupsTable.setModel( getExistingGroupsTableModel() );
        }
        return existingGroupsTable;
    }


    /**
     * This method initializes defaultTableModel	
     * 	
     * @return javax.swing.table.DefaultTableModel	
     */
    private ExistingGroupsTableModel getExistingGroupsTableModel()
    {
        if ( existingGroupsTableModel == null )
        {
            existingGroupsTableModel = new ExistingGroupsTableModel();
        }
        return existingGroupsTableModel;
    }

    class ExistingGroupsTableModel extends AbstractTableModel
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
                    return ( ( Group ) child.getUserObject() ).getCreatorsName();
                case ( 2 ):
                    return ( ( Group) child.getUserObject() ).getCreateTimestamp();
            }
            return child.getUserObject();
        }

    
        public String getColumnName( int columnIndex )
        {
            switch ( columnIndex )
            {
                case ( 0 ):
                    return "Existing Group";
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
    private JTextField getGroupNameTextField()
    {
        if ( groupNameTextField == null )
        {
            groupNameTextField = new JTextField();
        }
        return groupNameTextField;
    }
} //  @jve:decl-index=0:visual-constraint="10,10"
