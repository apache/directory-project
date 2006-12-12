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
import javax.swing.JButton;
import java.awt.GridBagLayout;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.Profile;
import org.safehaus.triplesec.admin.Role;


public class RoleDependentsPanel extends JPanel
{
    private static final long serialVersionUID = -5711894948847093836L;
    private JPanel centerPanel = null;
    private JPanel southPanel = null;
    private JButton removeButton = null;
    private JScrollPane jScrollPane = null;
    private JTable dependentsTable = null;
    private List dependents = new ArrayList();
    private Role role;
    private DependencyModel dependencyModel = null;
    

    /**
     * This is the default constructor
     */
    public RoleDependentsPanel()
    {
        super();
        initialize();
    }


    public void setSelectedNode( DefaultMutableTreeNode node )
    {
        this.role = ( Role ) node.getUserObject();
        this.dependents.clear();
        
        if ( node == null || node.getParent() == null || node.getParent().getParent() == null )
        {
            return;
        }

        // -------------------------------------------------------------------
        // Find the application, and subordinate "Profiles" node
        // -------------------------------------------------------------------

        DefaultMutableTreeNode applicationNode = ( DefaultMutableTreeNode ) node.getParent().getParent();
        DefaultMutableTreeNode profilesNode = null;
        for ( Enumeration ii = applicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( "Profiles".equals( child.getUserObject() ) )
            {
                profilesNode = child;
                break;
            }
        }
        
        // -------------------------------------------------------------------
        // Find the profile dependents
        // -------------------------------------------------------------------
        
        for ( Enumeration ii = profilesNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            Profile profile = ( Profile ) ( child ).getUserObject();
            if ( profile.getRoles().contains( role.getName() ) )
            {
                dependents.add( child );
            }
        }
        
        dependencyModel.fireTableDataChanged();
    }
    
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setLayout(new BorderLayout());
        this.setSize(590, 289);
        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
        this.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getCenterPanel()
    {
        if ( centerPanel == null )
        {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(10,10,10,10);
            gridBagConstraints.gridx = 0;
            centerPanel = new JPanel();
            centerPanel.setLayout(new GridBagLayout());
            centerPanel.add(getJScrollPane(), gridBagConstraints);
        }
        return centerPanel;
    }


    /**
     * This method initializes jPanel1	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSouthPanel()
    {
        if ( southPanel == null )
        {
            southPanel = new JPanel();
            southPanel.add(getRemoveButton(), null);
        }
        return southPanel;
    }


    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getRemoveButton()
    {
        if ( removeButton == null )
        {
            removeButton = new JButton();
            removeButton.setText("Remove");
            removeButton.setToolTipText("Remove the link to the dependent object");
            removeButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    String msg = UiUtils.wrap( "Removing dependency relationships will effect " +
                            "entities other than this role.  You cannot automatically revert from operation.  " +
                            "Would you like to continue?", 79 );
                    int response = JOptionPane.showOptionDialog( RoleDependentsPanel.this, msg, 
                        "Irreverable operation!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, 
                        null, null, null );
                    if ( response == JOptionPane.NO_OPTION )
                    {
                        return;
                    }
                    
                    int[] selectedRows = dependentsTable.getSelectedRows();
                    Set removed = new HashSet();
                    for ( int ii = 0; ii < selectedRows.length; ii++ )
                    {
                        DefaultMutableTreeNode dependentNode = 
                            ( DefaultMutableTreeNode ) dependents.get( selectedRows[ii] ); 
                        Profile profile = ( Profile ) dependentNode.getUserObject();
                        try
                        {
                            dependentNode.setUserObject( profile.modifier().removeRole( role.getName() ).modify() );
                            removed.add( dependentNode );
                        }
                        catch ( DataAccessException dae )
                        {
                            msg = UiUtils.wrap( "Failed to remove all dependency relationships for role: "
                                + dae.getMessage(), 79 );
                            JOptionPane.showMessageDialog( RoleDependentsPanel.this, msg, 
                                "Dependency removal failure!", JOptionPane.ERROR_MESSAGE );
                        }
                    }
                    
                    for ( Iterator ii = removed.iterator(); ii.hasNext(); /**/ )
                    {
                        dependents.remove( ii.next() );
                    }
                    
                    if ( removed.size() > 0 )
                    {
                        dependencyModel.fireTableDataChanged();
                    }
                }
            } );
        }
        return removeButton;
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
            jScrollPane.setViewportView(getDependentsTable());
        }
        return jScrollPane;
    }


    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getDependentsTable()
    {
        if ( dependentsTable == null )
        {
            dependentsTable = new JTable();
            dependentsTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            dependentsTable.setToolTipText("Role dependents");
            dependencyModel = new DependencyModel();
            dependentsTable.setModel( dependencyModel );
            dependentsTable.setShowGrid(true);
        }
        return dependentsTable;
    }

    
    class DependencyModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 5348529870374118604L;
        private final String[] COLNAMES = new String[] { "Profile Id" };


        public String getColumnName( int columnIndex )
        {
            return COLNAMES[columnIndex];
        }
        
        public int getRowCount()
        {
            return dependents.size();
        }

        public int getColumnCount()
        {
            return 1;
        }

        public Object getValueAt( int rowIndex, int columnIndex )
        {
            Object dependent = ( ( DefaultMutableTreeNode ) dependents.get( rowIndex ) ).getUserObject();
            if ( dependent instanceof Role )
            {
                switch( columnIndex )
                {
                    case ( 0 ):
                        return dependent;
                    default:
                        throw new IndexOutOfBoundsException( "Only 1 column present so columnIndex is invalid: "
                            + columnIndex );
                }
            }
            else if ( dependent instanceof Profile )
            {
                switch( columnIndex )
                {
                    case ( 0 ):
                        return dependent;
                    default:
                        throw new IndexOutOfBoundsException( "Only 1 columns present so columnIndex is invalid: "
                            + columnIndex );
                }
            }
            else
            {
                throw new IllegalStateException( "Only expecting Profile dependents for Permissions not " 
                    + dependent.getClass() );
            }
        }
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
