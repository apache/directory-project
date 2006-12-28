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
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.ApplicationModifier;
import org.safehaus.triplesec.admin.DataAccessException;
import javax.swing.JPasswordField;


public class ApplicationPanel extends JPanel
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
    private Application application = null;
    private JTree tree = null;
    private DefaultMutableTreeNode node = null;
    private LeftTreeNavigation leftTreeNavigation;
    private JLabel jLabel = null;
    private JTextField statusTextField = null;
    private JLabel jLabel1 = null;
    private JTextField applicationNameTextField = null;
    private JButton deleteButton = null;
    private JLabel jLabel2 = null;
    private JPanel jPanel1 = null;
    private JPasswordField applicationPasswordField = null;
    private JLabel jLabel3 = null;
    private JPasswordField confirmPasswordField = null;
    public void setLeftTreeNavigation( LeftTreeNavigation leftTreeNavigation )
    {
        this.leftTreeNavigation = leftTreeNavigation;
    }


    /**
     * This is the default constructor
     */
    public ApplicationPanel()
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
        this.setBorder( javax.swing.BorderFactory.createTitledBorder( null, "Existing Application",
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
                    setApplicationFields();
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
        }
        return centerTabbedPane;
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
                "/org/safehaus/triplesec/admin/swing/application_48x48.png" ) ) );
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
            gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints8.gridy = 2;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 2;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints6.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Password:");
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText( "Application Name:" );
            jLabel1.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets( 0, 0, 5, 5 );
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new java.awt.Insets( 0, 0, 5, 5 );
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            jLabel = new JLabel();
            jLabel.setText( "Status:" );
            jLabel.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jPanel = new JPanel();
            jPanel.setLayout( new GridBagLayout() );
            jPanel.setPreferredSize( new java.awt.Dimension( 131, 88 ) );
            jPanel.add( jLabel, gridBagConstraints2 );
            jPanel.add( getStatusTextField(), gridBagConstraints3 );
            jPanel.add(jLabel1, gridBagConstraints4);
            jPanel.add(getApplicationNameTextField(), gridBagConstraints5);
            jPanel.add(jLabel2, gridBagConstraints6);
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


    private void setApplicationFields()
    {
        generalPanel.setFields( application );
        applicationNameTextField.setText( application.getName() );
        applicationPasswordField.setText( application.getPassword() );
        confirmPasswordField.setText( application.getPassword() );
        descriptionTextArea.setText( application.getDescription() );
    }


    public void setTree( JTree tree )
    {
        this.tree = tree;
    }


    public void setTreeNode( DefaultMutableTreeNode node )
    {
        this.node = node;
        this.application = ( Application ) node.getUserObject();
        setApplicationFields();
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
        char[] passwordChars = applicationPasswordField.getPassword();
        char[] confirmChars = confirmPasswordField.getPassword();
        
        if ( passwordChars == null || confirmChars == null )
        {
            JOptionPane.showMessageDialog( this, "Either the password or the confirmation field is null." );
            return;
        }
        
        String passwordStr = new String( passwordChars );
        String confirmStr = new String( confirmChars );
        
        if ( ! passwordStr.equals( confirmStr ) )
        {
            JOptionPane.showMessageDialog( this, "Passwords do not match." );
            return;
        }
        
        ApplicationModifier modifier = application.modifier()
            .setDescription( descriptionTextArea.getText() )
            .setPassword( passwordStr );
        if ( modifier.isUpdateNeeded() )
        {
            try
            {
                application = modifier.modify();
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to modify application:\n" + e.getMessage(),
                    79 ), "Application modification failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
            node.setUserObject( application );
        }

        if ( !application.getName().equals( applicationNameTextField.getText() ) )
        {
            try
            {
                application = application.modifier().rename( applicationNameTextField.getText() );
                ( ( DefaultTreeModel ) tree.getModel() ).valueForPathChanged( new TreePath( node.getPath() ),
                    application );
                leftTreeNavigation.reloadApplication( this.node );
            }
            catch ( DataAccessException e )
            {
                JOptionPane.showMessageDialog( this, UiUtils.wrap( "Failed to rename application:\n" + e.getMessage(),
                    79 ), "Application rename failure!", JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        node.setUserObject( application );
        setApplicationFields();
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
        }
        return applicationNameTextField;
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
                        application.modifier().delete();
                        DefaultMutableTreeNode parentNode = ( DefaultMutableTreeNode ) node.getParent();
                        DefaultTreeModel treeModel = ( DefaultTreeModel ) tree.getModel();
                        treeModel.removeNodeFromParent( node );
                        leftTreeNavigation.reloadGroups();
                        TreePath path = new TreePath( parentNode.getPath() );
                        tree.setSelectionPaths( new TreePath[] { path } );
                    }
                    catch ( DataAccessException e1 )
                    {
                        JOptionPane.showMessageDialog( ApplicationPanel.this, 
                            "Failed to delete application: " + e1.getMessage(), "Delete Failed", 
                            JOptionPane.ERROR_MESSAGE );
                    }
                }
            } );
        }
        return deleteButton;
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
            gridBagConstraints11.weightx = 1.0;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.insets = new java.awt.Insets(0,5,0,5);
            jLabel3 = new JLabel();
            jLabel3.setText("Confirm:");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.gridx = 0;
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(getApplicationPasswordField(), gridBagConstraints9);
            jPanel1.add(jLabel3, gridBagConstraints10);
            jPanel1.add(getConfirmPasswordField(), gridBagConstraints11);
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
