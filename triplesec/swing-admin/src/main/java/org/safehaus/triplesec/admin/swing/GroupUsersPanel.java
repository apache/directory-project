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
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.safehaus.triplesec.admin.User;


public class GroupUsersPanel extends JPanel
{
    private static final long serialVersionUID = 1527321087922436546L;

    private JPanel availableUsersPanel = null;
    private JList availableUsersList = null;
    private JPanel usersInGroupPanel = null;
    private JList usersInGroupList = null;
    private JPanel usersButtonPanel = null;
    private JButton addUsersButton = null;
    private JButton removeUsersButton = null;
    private DefaultListModel usersInGroupListModel = null; //  @jve:decl-index=0:visual-constraint=""
    private DefaultListModel availableUsersListModel = null;
    private JScrollPane jScrollPane = null;
    private JScrollPane jScrollPane1 = null;

    
    /**
     * This is the default constructor
     */
    public GroupUsersPanel()
    {
        super();
        initialize();
    }

    
    public void populateLists( DefaultMutableTreeNode rootNode, Set users )
    {
        clear();
        for ( Iterator ii = users.iterator(); ii.hasNext(); /**/)
        {
            usersInGroupListModel.add( 0, ii.next() );
        }
        
        if ( rootNode == null )
        {
            return;
        }
        DefaultMutableTreeNode usersNode = null;
        for ( Enumeration ii = rootNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( ( ( String ) child.getUserObject() ).equalsIgnoreCase( "Users" ) )
            {
                usersNode = child;
                break;
            }
        }

        for ( Enumeration ii = usersNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode userNode = ( DefaultMutableTreeNode ) ii.nextElement();
            User user = ( User ) userNode.getUserObject();
            if ( ! users.contains( user.getId() ) )
            {
                availableUsersListModel.addElement( user.getId() );
            }
        }
    }
    
    
    public DefaultListModel getUsersInGroupModel()
    {
        return usersInGroupListModel;
    }
    
    
    public DefaultListModel getAvailableUsersModel()
    {
        return availableUsersListModel;
    }
    

    public void clear()
    {
        usersInGroupListModel.clear();
        availableUsersListModel.clear();
    }
    
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(548, 246);
        setLayout( new BorderLayout() );
        setName( "usersPanelTab" );
        add( getAvailableUsersPanel(), java.awt.BorderLayout.WEST );
        add( getUsersInGroupPanel(), java.awt.BorderLayout.EAST );
        add( getUsersButtonPanel(), java.awt.BorderLayout.CENTER );
    }


    /**
     * This method initializes jPanel3  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getAvailableUsersPanel()
    {
        if ( availableUsersPanel == null )
        {
            availableUsersPanel = new JPanel();
            availableUsersPanel.setLayout( new BorderLayout() );
            availableUsersPanel.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Available Users",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null ) );
            availableUsersPanel.setPreferredSize( new java.awt.Dimension( 200, 35 ) );
            availableUsersPanel.setToolTipText( "Available users that are not in the group" );
            availableUsersPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return availableUsersPanel;
    }


    /**
     * This method initializes jList    
     *  
     * @return javax.swing.JList    
     */
    private JList getAvailableUsersList()
    {
        if ( availableUsersList == null )
        {
            availableUsersList = new JList();
            availableUsersList.setModel( getAvailableUsersListModel() );
            
        }
        return availableUsersList;
    }


    /**
     * This method initializes jPanel3  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getUsersInGroupPanel()
    {
        if ( usersInGroupPanel == null )
        {
            usersInGroupPanel = new JPanel();
            usersInGroupPanel.setLayout( new BorderLayout() );
            usersInGroupPanel.setPreferredSize( new java.awt.Dimension( 200, 10 ) );
            usersInGroupPanel.setToolTipText( "Existing members of the group" );
            usersInGroupPanel.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Group Members",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null ) );
            usersInGroupPanel.add(getJScrollPane1(), java.awt.BorderLayout.CENTER);
        }
        return usersInGroupPanel;
    }


    /**
     * This method initializes jList    
     *  
     * @return javax.swing.JList    
     */
    private JList getUsersInGroupList()
    {
        if ( usersInGroupList == null )
        {
            usersInGroupList = new JList();
            usersInGroupList.setModel( getUsersInGroupListModel() );
        }
        return usersInGroupList;
    }


    /**
     * This method initializes jPanel3  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getUsersButtonPanel()
    {
        if ( usersButtonPanel == null )
        {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new java.awt.Insets( 10, 0, 0, 0 );
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            usersButtonPanel = new JPanel();
            usersButtonPanel.setLayout( new GridBagLayout() );
            usersButtonPanel.add( getAddUsersButton(), gridBagConstraints4 );
            usersButtonPanel.add( getRemoveUsersButton(), gridBagConstraints5 );
        }
        return usersButtonPanel;
    }


    /**
     * This method initializes jButton  
     *  
     * @return javax.swing.JButton  
     */
    private JButton getAddUsersButton()
    {
        if ( addUsersButton == null )
        {
            addUsersButton = new JButton();
            addUsersButton.setText( "" );
            addUsersButton.setToolTipText( "Add selected users to group" );
            addUsersButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/rightarrow_16x16.png" ) ) );
            addUsersButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedUsers = availableUsersList.getSelectedValues();
                    for ( int ii = 0; ii < selectedUsers.length; ii++ )
                    {
                        usersInGroupListModel.addElement( selectedUsers[ii] );
                        availableUsersListModel.removeElement( selectedUsers[ii] );
                    }
                }
            } );
        }
        return addUsersButton;
    }


    /**
     * This method initializes jButton  
     *  
     * @return javax.swing.JButton  
     */
    private JButton getRemoveUsersButton()
    {
        if ( removeUsersButton == null )
        {
            removeUsersButton = new JButton();
            removeUsersButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/leftarrow_16x16.png" ) ) );
            removeUsersButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedUsers = usersInGroupList.getSelectedValues();
                    for ( int ii = 0; ii < selectedUsers.length; ii++ )
                    {
                        usersInGroupListModel.removeElement( selectedUsers[ii] );
                        availableUsersListModel.addElement( selectedUsers[ii] );
                    }
                }
            } );
        }
        return removeUsersButton;
    }


    /**
     * This method initializes defaultListModel 
     *  
     * @return javax.swing.DefaultListModel 
     */
    private DefaultListModel getUsersInGroupListModel()
    {
        if ( usersInGroupListModel == null )
        {
            usersInGroupListModel = new DefaultListModel();
        }
        return usersInGroupListModel;
    }


    /**
     * This method initializes defaultListModel 
     *  
     * @return javax.swing.DefaultListModel 
     */
    private DefaultListModel getAvailableUsersListModel()
    {
        if ( availableUsersListModel == null )
        {
            availableUsersListModel = new DefaultListModel();
        }
        return availableUsersListModel;
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
            jScrollPane.setViewportView(getAvailableUsersList());
        }
        return jScrollPane;
    }


    /**
     * This method initializes jScrollPane1 
     *  
     * @return javax.swing.JScrollPane  
     */
    private JScrollPane getJScrollPane1()
    {
        if ( jScrollPane1 == null )
        {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setViewportView(getUsersInGroupList());
        }
        return jScrollPane1;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
