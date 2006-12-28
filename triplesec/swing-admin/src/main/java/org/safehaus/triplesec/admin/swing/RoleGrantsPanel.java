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

import org.safehaus.triplesec.admin.Permission;


public class RoleGrantsPanel extends JPanel
{
    private static final long serialVersionUID = 7392344377397200180L;
    private JPanel jPanel5 = null;
    private JPanel jPanel6 = null;
    private JPanel jPanel7 = null;
    private JList applicationPermissions = null;
    private JList roleGrants = null;
    private JButton addPermissionButton = null;
    private JButton removePermissionButton = null;
    private DefaultListModel applicationPermissionsModel = null; // @jve:decl-index=0:visual-constraint=""
    private DefaultListModel grantsModel = null; // @jve:decl-index=0:visual-constraint=""
    private JScrollPane jScrollPane = null;
    private JScrollPane jScrollPane1 = null;


    /**
     * This is the default constructor
     */
    public RoleGrantsPanel()
    {
        super();
        initialize();
    }
    
    
    public void populateLists( DefaultMutableTreeNode applicationNode, Set grants )
    {
        applicationPermissionsModel.clear();
        grantsModel.clear();
        
        // -------------------------------------------------------------------
        // find the permissions container node under the application
        // -------------------------------------------------------------------

        DefaultMutableTreeNode permissionsNode = null;
        for ( Enumeration ii = applicationNode.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            if ( "Permissions".equals( child.getUserObject() ) )
            {
                permissionsNode = child;
                break;
            }
        }
        
        // -------------------------------------------------------------------
        // Iterate through children underneath Permssions container adding 
        // them to the available permssions model if they are not included
        // in the set of grants of the role
        // -------------------------------------------------------------------

        for ( Enumeration ii = permissionsNode.children(); ii.hasMoreElements(); /**/ )
        {
            Permission permission = ( Permission ) ( ( DefaultMutableTreeNode ) ii.nextElement() ).getUserObject();
            if ( ! grants.contains( permission.getName() ) )
            {
                applicationPermissionsModel.addElement( permission.getName() );
            }
        }
        
        // -------------------------------------------------------------------
        // Now add all the grants to the grants model for the grants list
        // -------------------------------------------------------------------
        
        for ( Iterator ii = grants.iterator(); ii.hasNext(); /**/ )
        {
            grantsModel.addElement( ii.next() );
        }
        
        jScrollPane.repaint();
        jScrollPane1.repaint();
    }

    
    public DefaultListModel getRoleGrantsModel()
    {
        return grantsModel;
    }


    public DefaultListModel getAvailablePermssionsModel()
    {
        return applicationPermissionsModel;
    }
    
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(528, 234);
        setLayout( new BorderLayout() );
        add( getJPanel5(), java.awt.BorderLayout.WEST );
        add( getJPanel6(), java.awt.BorderLayout.CENTER );
        add( getJPanel7(), java.awt.BorderLayout.EAST );

    }


    /**
     * This method initializes jPanel5
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel5()
    {
        if ( jPanel5 == null )
        {
            jPanel5 = new JPanel();
            jPanel5.setLayout( new BorderLayout() );
            jPanel5.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Available Grants",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null ) );
            jPanel5.setPreferredSize( new java.awt.Dimension( 200, 35 ) );
            jPanel5.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return jPanel5;
    }


    /**
     * This method initializes jPanel6
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel6()
    {
        if ( jPanel6 == null )
        {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.insets = new java.awt.Insets( 10, 0, 0, 0 );
            gridBagConstraints14.gridy = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 0;
            jPanel6 = new JPanel();
            jPanel6.setLayout( new GridBagLayout() );
            jPanel6.add( getAddPermissionButton(), gridBagConstraints5 );
            jPanel6.add( getRemovePermissionButton(), gridBagConstraints14 );
        }
        return jPanel6;
    }


    /**
     * This method initializes jPanel7
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel7()
    {
        if ( jPanel7 == null )
        {
            jPanel7 = new JPanel();
            jPanel7.setLayout( new BorderLayout() );
            jPanel7.setPreferredSize( new java.awt.Dimension( 200, 10 ) );
            jPanel7.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Role Grants",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font( "Dialog", java.awt.Font.BOLD, 12 ), new java.awt.Color( 51, 51, 51 ) ) );
            jPanel7.add(getJScrollPane1(), java.awt.BorderLayout.CENTER);
        }
        return jPanel7;
    }


    /**
     * This method initializes jList
     * 
     * @return javax.swing.JList
     */
    private JList getApplicationPermissions()
    {
        if ( applicationPermissions == null )
        {
            applicationPermissions = new JList();
            applicationPermissions.setPreferredSize( new java.awt.Dimension( 0, 0 ) );
            applicationPermissions.setToolTipText( "Application permissions assignable to role grants" );
            applicationPermissions.setModel( getDefaultListModel() );
        }
        return applicationPermissions;
    }


    /**
     * This method initializes jList1
     * 
     * @return javax.swing.JList
     */
    private JList getRoleGrants()
    {
        if ( roleGrants == null )
        {
            roleGrants = new JList();
            roleGrants.setToolTipText( "Permissions already assigned to role grants" );
            roleGrants.setModel( getDefaultListModel1() );
        }
        return roleGrants;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddPermissionButton()
    {
        if ( addPermissionButton == null )
        {
            addPermissionButton = new JButton();
            addPermissionButton.setToolTipText( "Add selected permissions to role grants" );
            addPermissionButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/rightarrow_16x16.png" ) ) );
            addPermissionButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedValues = applicationPermissions.getSelectedValues();
                    if ( selectedValues == null || selectedValues.length == 0 )
                    {
                        return;
                    }

                    for ( int ii = 0; ii < selectedValues.length; ii++ )
                    {
                        applicationPermissionsModel.removeElement( selectedValues[ii] );
                        grantsModel.add( 0, selectedValues[ii] );
                    }
                }
            } );
        }
        return addPermissionButton;
    }


    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemovePermissionButton()
    {
        if ( removePermissionButton == null )
        {
            removePermissionButton = new JButton();
            removePermissionButton.setToolTipText( "Remove selected permissions from role grants" );
            removePermissionButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/leftarrow_16x16.png" ) ) );
            removePermissionButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedValues = roleGrants.getSelectedValues();
                    if ( selectedValues == null || selectedValues.length == 0 )
                    {
                        return;
                    }

                    for ( int ii = 0; ii < selectedValues.length; ii++ )
                    {
                        grantsModel.removeElement( selectedValues[ii] );
                        applicationPermissionsModel.add( 0, selectedValues[ii] );
                    }
                }
            } );
        }
        return removePermissionButton;
    }


    /**
     * This method initializes defaultListModel
     * 
     * @return javax.swing.DefaultListModel
     */
    private DefaultListModel getDefaultListModel()
    {
        if ( applicationPermissionsModel == null )
        {
            applicationPermissionsModel = new DefaultListModel();
        }
        return applicationPermissionsModel;
    }


    /**
     * This method initializes defaultListModel1
     * 
     * @return javax.swing.DefaultListModel
     */
    private DefaultListModel getDefaultListModel1()
    {
        if ( grantsModel == null )
        {
            grantsModel = new DefaultListModel();
        }
        return grantsModel;
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
            jScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            jScrollPane.setViewportView(getApplicationPermissions());
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
            jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            jScrollPane1.setViewportView(getRoleGrants());
        }
        return jScrollPane1;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
