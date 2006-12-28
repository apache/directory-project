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

import org.safehaus.triplesec.admin.Role;


public class ProfileRolesPanel extends JPanel
{
    private static final long serialVersionUID = 5014497087645715424L;

    private JPanel jPanelRoles5 = null;
    private JPanel jPanelRoles6 = null;
    private JPanel jPanelRoles7 = null;
    private JList availableRoles = null;
    private JList existingRoles = null;
    private JButton addButton = null;
    private JButton removeButton = null;
    private JScrollPane jScrollPane4 = null;
    private JScrollPane jScrollPane5 = null;
    private DefaultListModel availableModel = null; // @jve:decl-index=0:visual-constraint=""
    private DefaultListModel existingModel = null; // @jve:decl-index=0:visual-constraint=""

    
    /**
     * This is the default constructor
     */
    public ProfileRolesPanel()
    {
        super();
        initialize();
    }

    
    public void populateLists( DefaultMutableTreeNode applicationNode, Set roles )
    {
        if ( applicationNode == null )
        {
            return;
        }
        
        // -------------------------------------------------------------------
        // Find the Roles container under the application and clear lists
        // -------------------------------------------------------------------
        
        availableModel.clear();
        existingModel.clear();
        DefaultMutableTreeNode rolesNode = null;
        for ( Enumeration ii = applicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( "Roles".equals( child.getUserObject() ) )
            {
                rolesNode = child;
                break;
            }
        }
        
        // -------------------------------------------------------------------
        // Fill up the available roles list and the existing roles list
        // -------------------------------------------------------------------
        
        for ( Enumeration ii = rolesNode.children(); ii.hasMoreElements(); /**/)
        {
            Role role = ( Role ) ( ( DefaultMutableTreeNode ) ii.nextElement() ).getUserObject();
            if ( !roles.contains( role.getName() ) )
            {
                availableModel.addElement( role.getName() );
            }
        }
        for ( Iterator ii = roles.iterator(); ii.hasNext(); /**/)
        {
            existingModel.addElement( ii.next() );
        }
    }
    

    public DefaultListModel getAvailableRolesModel()
    {
        return availableModel;
    }
    
    
    public DefaultListModel getProfileRolesModel()
    {
        return existingModel;
    }
    
    
    /**
     * This method initializes this
     * 
     */
    private void initialize()
    {
        this.setSize(613, 254);
        setLayout( new BorderLayout() );
        add( getAvailableRolesPanel(), java.awt.BorderLayout.WEST );
        add( getButtonPanel(), java.awt.BorderLayout.CENTER );
        add( getExistingRolesPanel(), java.awt.BorderLayout.EAST );
    }


    /**
     * This method initializes jPanel5
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAvailableRolesPanel()
    {
        if ( jPanelRoles5 == null )
        {
            jPanelRoles5 = new JPanel();
            jPanelRoles5.setLayout( new BorderLayout() );
            jPanelRoles5.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Available Roles",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null ) );
            jPanelRoles5.setPreferredSize( new java.awt.Dimension( 200, 35 ) );
            jPanelRoles5.add( getJScrollPane4(), java.awt.BorderLayout.CENTER );
        }
        return jPanelRoles5;
    }


    /**
     * This method initializes jPanel6
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel()
    {
        if ( jPanelRoles6 == null )
        {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.insets = new java.awt.Insets( 10, 0, 0, 0 );
            gridBagConstraints14.gridy = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 0;
            jPanelRoles6 = new JPanel();
            jPanelRoles6.setLayout( new GridBagLayout() );
            jPanelRoles6.add( getAddRolesButton(), gridBagConstraints5 );
            jPanelRoles6.add( getRemoveRolesButton(), gridBagConstraints14 );
        }
        return jPanelRoles6;
    }


    /**
     * This method initializes jPanel7
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExistingRolesPanel()
    {
        if ( jPanelRoles7 == null )
        {
            jPanelRoles7 = new JPanel();
            jPanelRoles7.setLayout( new BorderLayout() );
            jPanelRoles7.setPreferredSize( new java.awt.Dimension( 200, 10 ) );
            jPanelRoles7.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Profile Roles",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font( "Dialog", java.awt.Font.BOLD, 12 ), new java.awt.Color( 51, 51, 51 ) ) );
            jPanelRoles7.add( getJScrollPane5(), java.awt.BorderLayout.CENTER );
        }
        return jPanelRoles7;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddRolesButton()
    {
        if ( addButton == null )
        {
            addButton = new JButton();
            addButton.setToolTipText( "Add selected roles to profile" );
            addButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/rightarrow_16x16.png" ) ) );
            addButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedValues = availableRoles.getSelectedValues();
                    if ( selectedValues == null || selectedValues.length == 0 )
                    {
                        return;
                    }

                    for ( int ii = 0; ii < selectedValues.length; ii++ )
                    {
                        availableModel.removeElement( selectedValues[ii] );
                        existingModel.add( 0, selectedValues[ii] );
                    }
                }
            } );
        }
        return addButton;
    }


    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveRolesButton()
    {
        if ( removeButton == null )
        {
            removeButton = new JButton();
            removeButton.setToolTipText( "Remove selected roles from profile" );
            removeButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/leftarrow_16x16.png" ) ) );
            removeButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedValues = existingRoles.getSelectedValues();
                    if ( selectedValues == null || selectedValues.length == 0 )
                    {
                        return;
                    }

                    for ( int ii = 0; ii < selectedValues.length; ii++ )
                    {
                        existingModel.removeElement( selectedValues[ii] );
                        availableModel.add( 0, selectedValues[ii] );
                    }
                }
            } );
        }
        return removeButton;
    }


    /**
     * This method initializes jScrollPane4 
     *  
     * @return javax.swing.JScrollPane  
     */
    private JScrollPane getJScrollPane4()
    {
        if ( jScrollPane4 == null )
        {
            jScrollPane4 = new JScrollPane();
            jScrollPane4.setViewportView( getApplicationRoles() );
        }
        return jScrollPane4;
    }


    /**
     * This method initializes jScrollPane5 
     *  
     * @return javax.swing.JScrollPane  
     */
    private JScrollPane getJScrollPane5()
    {
        if ( jScrollPane5 == null )
        {
            jScrollPane5 = new JScrollPane();
            jScrollPane5.setViewportView( getProfileRoles() );
        }
        return jScrollPane5;
    }


    /**
     * This method initializes jList1
     * 
     * @return javax.swing.JList
     */
    private JList getProfileRoles()
    {
        if ( existingRoles == null )
        {
            existingRoles = new JList();
            existingRoles.setToolTipText( "Roles already assigned to profile roles" );
            existingRoles.setModel( getRolesDefaultListModel1() );
        }
        return existingRoles;
    }


    /**
     * This method initializes jList
     * 
     * @return javax.swing.JList
     */
    private JList getApplicationRoles()
    {
        if ( availableRoles == null )
        {
            availableRoles = new JList();
            availableRoles.setPreferredSize( new java.awt.Dimension( 0, 0 ) );
            availableRoles.setToolTipText( "Application roles assignable to profile" );
            availableRoles.setModel( getRolesDefaultListModel() );
        }
        return availableRoles;
    }


    /**
     * This method initializes defaultListModel
     * 
     * @return javax.swing.DefaultListModel
     */
    private DefaultListModel getRolesDefaultListModel()
    {
        if ( availableModel == null )
        {
            availableModel = new DefaultListModel();
        }
        return availableModel;
    }


    /**
     * This method initializes defaultListModel1
     * 
     * @return javax.swing.DefaultListModel
     */
    private DefaultListModel getRolesDefaultListModel1()
    {
        if ( existingModel == null )
        {
            existingModel = new DefaultListModel();
        }
        return existingModel;
    }


}  //  @jve:decl-index=0:visual-constraint="10,10"
