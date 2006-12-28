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

import org.safehaus.triplesec.admin.PermissionClass;


public class ProfilePermissionsPanel extends JPanel
{
    private static final long serialVersionUID = 789126136169615296L;

    private JPanel jPanel5 = null;
    private JPanel jPanel6 = null;
    private JPanel jPanel7 = null;
    private JList availableList = null;
    private JScrollPane jScrollPane = null;
    private JScrollPane jScrollPane1 = null;
    private JList existingList = null;
    private JButton addButton = null;
    private JButton removeButton = null;
    private DefaultListModel availableModel = null; // @jve:decl-index=0:visual-constraint=""
    private DefaultListModel existingModel = null; // @jve:decl-index=0:visual-constraint=""
    private final boolean forGrants;

    
    /**
     * This is the default constructor
     */
    public ProfilePermissionsPanel()
    {
        super();
        initialize();
        forGrants = true;
    }


    /**
     * This is the default constructor
     */
    public ProfilePermissionsPanel( boolean forGrants )
    {
        super();
        initialize();
        this.forGrants = forGrants;
    }


    public void populateLists( DefaultMutableTreeNode applicationNode, Set existing )
    {
        // -------------------------------------------------------------------
        // clear both lists and find the permissions container for the app
        // -------------------------------------------------------------------
        
        availableModel.clear();
        existingModel.clear();
        if ( applicationNode == null )
        {
            return;
        }
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
        // load both lists: do not include perms as available if in grants
        // -------------------------------------------------------------------
        
//        for ( Enumeration ii = permissionsNode.children(); ii.hasMoreElements(); /**/)
//        {
//            Permission permission = ( Permission ) ( ( DefaultMutableTreeNode ) ii.nextElement() ).getUserObject();
//            if ( ! existing.contains( permission.getName() ) )
//            {
//                availableModel.addElement( permission.getName() );
//            }
//        }
        for ( Iterator ii = existing.iterator(); ii.hasNext(); /**/)
        {
            existingModel.addElement( ii.next() );
        }
    }
    
    
    public DefaultListModel getAvailableModel()
    {
        return availableModel;
    }


    public DefaultListModel getExistingModel()
    {
        return existingModel;
    }


    /**
     * This method initializes this
     * 
     */
    private void initialize()
    {
        this.setSize( 637, 268 );
        setLayout( new BorderLayout() );
        add( getAvailablePanel(), java.awt.BorderLayout.WEST );
        add( getButtonPanel(), java.awt.BorderLayout.CENTER );
        add( getExistingPanel(), java.awt.BorderLayout.EAST );
    }


    /**
     * This method initializes jPanel5
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAvailablePanel()
    {
        if ( jPanel5 == null )
        {
            jPanel5 = new JPanel();
            jPanel5.setLayout( new BorderLayout() );
            String title = null;
            if ( forGrants )
            {
                title = "Available Grants";
            }
            else
            {
                title = "Available Denials";
            }
            jPanel5.setBorder( javax.swing.BorderFactory.createTitledBorder( null, title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null ) );
            jPanel5.setPreferredSize( new java.awt.Dimension( 200, 35 ) );
            jPanel5.add( getJScrollPane(), java.awt.BorderLayout.CENTER );
        }
        return jPanel5;
    }


    /**
     * This method initializes jPanel6
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel()
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
    private JPanel getExistingPanel()
    {
        if ( jPanel7 == null )
        {
            jPanel7 = new JPanel();
            jPanel7.setLayout( new BorderLayout() );
            jPanel7.setPreferredSize( new java.awt.Dimension( 200, 10 ) );
            String title = null;
            if ( forGrants )
            {
                title = "Available Grants";
            }
            else
            {
                title = "Available Denials";
            }
            jPanel7.setBorder( javax.swing.BorderFactory.createTitledBorder( null, title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font( "Dialog", java.awt.Font.BOLD, 12 ), new java.awt.Color( 51, 51, 51 ) ) );
            jPanel7.add( getJScrollPane1(), java.awt.BorderLayout.CENTER );
        }
        return jPanel7;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddPermissionButton()
    {
        if ( addButton == null )
        {
            addButton = new JButton();
            String tooltip = null;
            if ( forGrants )
            {
                tooltip = "Add selected permissions to profile grants";
            }
            else
            {
                tooltip = "Add selected permissions to profile denials";
            }
            addButton.setToolTipText( tooltip );
            addButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/rightarrow_16x16.png" ) ) );
            addButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedValues = availableList.getSelectedValues();
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
    private JButton getRemovePermissionButton()
    {
        if ( removeButton == null )
        {
            removeButton = new JButton();
            String tooltip = null;
            if ( forGrants )
            {
                tooltip = "Remove selected permissions from profile grants";
            }
            else
            {
                tooltip = "Remove selected permissions from profile denials";
            }
            removeButton.setToolTipText( tooltip );
            removeButton.setIcon( new ImageIcon( getClass().getResource(
                "/org/safehaus/triplesec/admin/swing/leftarrow_16x16.png" ) ) );
            removeButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    Object[] selectedValues = existingList.getSelectedValues();
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
     * This method initializes jScrollPane  
     *  
     * @return javax.swing.JScrollPane  
     */
    private JScrollPane getJScrollPane()
    {
        if ( jScrollPane == null )
        {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView( getApplicationPermissions() );
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
            jScrollPane1.setViewportView( getProfileGrants() );
        }
        return jScrollPane1;
    }


    /**
     * This method initializes jList
     * 
     * @return javax.swing.JList
     */
    private JList getApplicationPermissions()
    {
        if ( availableList == null )
        {
            availableList = new JList();
            availableList.setPreferredSize( new java.awt.Dimension( 0, 0 ) );
            String tooltip = null;
            if ( forGrants )
            {
                tooltip = "Application permissions assignable to profile grants";
            }
            else
            {
                tooltip = "Application permissions assignable to profile denials";
            }
            availableList.setToolTipText( tooltip );
            availableList.setModel( getDefaultListModel() );
        }
        return availableList;
    }


    /**
     * This method initializes jList1
     * 
     * @return javax.swing.JList
     */
    private JList getProfileGrants()
    {
        if ( existingList == null )
        {
            existingList = new JList();
            String tooltip = null;
            if ( forGrants )
            {
                tooltip = "Permissions already assigned to profile grants";
            }
            else
            {
                tooltip = "Permissions already assigned to profile denials";
            }
            existingList.setToolTipText( tooltip );
            existingList.setModel( getDefaultListModel1() );
        }
        return existingList;
    }


    /**
     * This method initializes defaultListModel
     * 
     * @return javax.swing.DefaultListModel
     */
    private DefaultListModel getDefaultListModel()
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
    private DefaultListModel getDefaultListModel1()
    {
        if ( existingModel == null )
        {
            existingModel = new DefaultListModel();
        }
        return existingModel;
    }

} //  @jve:decl-index=0:visual-constraint="10,10"
