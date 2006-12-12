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


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.safehaus.triplesec.admin.ExternalUser;
import org.safehaus.triplesec.admin.HauskeysUser;
import org.safehaus.triplesec.admin.LocalUser;
import org.safehaus.triplesec.admin.User;


public class UserNorthPanel extends JPanel implements StatusObject, FocusListener, KeyListener
{
    private static final long serialVersionUID = 7996658370769939502L;


    private ImageIcon localUserIcon = new ImageIcon( getClass().getResource(
        "/org/safehaus/triplesec/admin/swing/local_user_48x48.png" ) );
    private ImageIcon externalUserIcon = new ImageIcon( getClass().getResource(
        "/org/safehaus/triplesec/admin/swing/external_user_48x48.png" ) );
    private ImageIcon hauskeysUserIcon = new ImageIcon( getClass().getResource(
        "/org/safehaus/triplesec/admin/swing/hauskeys_user_48x48.png" ) );
    private JLabel iconLabel = null;
    
    private ButtonGroup userTypeButtonGroup;
    private JPanel jPanel = null;
    private JPanel jPanel4 = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JTextField statusTextField = null;
    private JLabel jLabel2 = null;
    private JTextField userIdTextField = null;
    private JPanel jPanel3 = null;
    private JRadioButton externalUserRadioButton = null;
    private JRadioButton localUserRadioButton = null;
    private JRadioButton hauskeysUserRadioButton = null;
    private User user;
    private boolean lastStatusState = true;
    private StatusListener listener;
    private boolean newEnityMode = false;

    
    /**
     * This is the default constructor
     */
    public UserNorthPanel()
    {
        super();
        initialize();
    }

    
    public void setNewEntityMode( boolean newEntityMode )
    {
        this.newEnityMode = newEntityMode;
    }
    
    
    public void setStatusListener( StatusListener listener )
    {
        this.listener = listener;
    }
    
    
    public void setStatus( Color color, String msg )
    {
        statusTextField.setForeground( color );
        statusTextField.setText( msg );
    }
    
    
    public String getId()
    {
        return userIdTextField.getText();
    }
    

    public void setFields( User user )
    {
        this.user = user;
        this.lastStatusState = true;
        userIdTextField.setText( user.getId() );
        
        if ( user instanceof ExternalUser )
        {
            iconLabel.setIcon( externalUserIcon );
            userTypeButtonGroup.setSelected( externalUserRadioButton.getModel(), true );
        }
        else if ( user instanceof LocalUser )
        {
            iconLabel.setIcon( localUserIcon );
            userTypeButtonGroup.setSelected( localUserRadioButton.getModel(), true );
        }
        else if ( user instanceof HauskeysUser )
        {
            iconLabel.setIcon( hauskeysUserIcon );
            userTypeButtonGroup.setSelected( hauskeysUserRadioButton.getModel(), true );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown user type: " + user.getClass() );
        }
    }

    
    public boolean isExternalUserSelected()
    {
        return externalUserRadioButton.isSelected();
    }
    
    
    public boolean isHauskeysUserSelected()
    {
        return hauskeysUserRadioButton.isSelected();
    }
    
    
    public boolean isLocalUserSelected()
    {
        return localUserRadioButton.isSelected();
    }
    
    
    public void addActionListener( ActionListener listener )
    {
        externalUserRadioButton.addActionListener( listener );
        localUserRadioButton.addActionListener( listener );
        hauskeysUserRadioButton.addActionListener( listener );
    }
    
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(724, 119);
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.gridheight = 3;
        gridBagConstraints1.insets = new java.awt.Insets( 0, 10, 0, 10 );
        gridBagConstraints1.weighty = 1.0D;
        gridBagConstraints1.gridy = 0;
        setLayout( new GridBagLayout() );
        setPreferredSize( new java.awt.Dimension( 179, 68 ) );
        add( getJPanel4(), new GridBagConstraints() );
        add( getJPanel(), gridBagConstraints1 );
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
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 1;
            gridBagConstraints14.insets = new java.awt.Insets( 0, 5, 0, 0 );
            gridBagConstraints14.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints14.gridy = 2;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 1;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new java.awt.Insets( 0, 0, 0, 0 );
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new java.awt.Insets( 0, 0, 5, 5 );
            gridBagConstraints3.gridy = 1;
            jLabel2 = new JLabel();
            jLabel2.setText( "User Id:" );
            jLabel2.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new java.awt.Insets( 0, 0, 5, 0 );
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new java.awt.Insets( 0, 0, 5, 5 );
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new java.awt.Insets( 0, 0, 0, 5 );
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 2;
            jLabel1 = new JLabel();
            jLabel1.setText( "User Type:" );
            jLabel1.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jLabel = new JLabel();
            jLabel.setText( "Status:" );
            jLabel.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jPanel = new JPanel();
            jPanel.setLayout( new GridBagLayout() );
            jPanel.setPreferredSize( new java.awt.Dimension( 131, 88 ) );
            jPanel.add( jLabel, gridBagConstraints4 );
            jPanel.add( jLabel1, gridBagConstraints2 );
            jPanel.add( getStatusTextField(), gridBagConstraints5 );
            jPanel.add( jLabel2, gridBagConstraints3 );
            jPanel.add( getUserIdTextField(), gridBagConstraints15 );
            jPanel.add( getJPanel3(), gridBagConstraints14 );
        }
        return jPanel;
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
                "/org/safehaus/triplesec/admin/swing/local_user_48x48.png" ) ) );
            iconLabel.setPreferredSize( new java.awt.Dimension( 48, 48 ) );
            iconLabel.setText( "" );
            iconLabel.setVerticalTextPosition( javax.swing.SwingConstants.BOTTOM );
            iconLabel.setVerticalAlignment( javax.swing.SwingConstants.BOTTOM );
            iconLabel.setEnabled( true );
        }
        return iconLabel;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getUserIdTextField()
    {
        if ( userIdTextField == null )
        {
            userIdTextField = new JTextField();
            userIdTextField.addFocusListener( this );
            userIdTextField.addKeyListener( this );
        }
        return userIdTextField;
    }


    private void checkStatus()
    {
        boolean upToDate = isUpToDate();
        if ( listener != null && lastStatusState != upToDate )
        {
            listener.statusChanged( UserNorthPanel.this );
            lastStatusState = upToDate;
        }
    }
    
    
    /**
     * This method initializes jPanel3  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getJPanel3()
    {
        if ( jPanel3 == null )
        {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment( java.awt.FlowLayout.LEFT );
            jPanel3 = new JPanel();
            jPanel3.setLayout( flowLayout );
            jPanel3.setPreferredSize( new java.awt.Dimension( 333, 20 ) );
            jPanel3.add( getExternalUserRadioButton(), null );
            jPanel3.add( getLocalUserRadioButton(), null );
            jPanel3.add( getHauskeysUserRadioButton(), null );
            userTypeButtonGroup = new ButtonGroup();
            userTypeButtonGroup.add( getExternalUserRadioButton() );
            userTypeButtonGroup.add( getLocalUserRadioButton() );
            userTypeButtonGroup.add( getHauskeysUserRadioButton() );
        }
        return jPanel3;
    }


    /**
     * This method initializes jRadioButton 
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getExternalUserRadioButton()
    {
        if ( externalUserRadioButton == null )
        {
            externalUserRadioButton = new JRadioButton();
            externalUserRadioButton.setText( "External User" );
            externalUserRadioButton.setPreferredSize( new java.awt.Dimension( 107, 16 ) );
        }
        return externalUserRadioButton;
    }


    /**
     * This method initializes jRadioButton1    
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getLocalUserRadioButton()
    {
        if ( localUserRadioButton == null )
        {
            localUserRadioButton = new JRadioButton();
            localUserRadioButton.setText( "Local User" );
            localUserRadioButton.setPreferredSize( new java.awt.Dimension( 89, 16 ) );
        }
        return localUserRadioButton;
    }


    /**
     * This method initializes jRadioButton2    
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getHauskeysUserRadioButton()
    {
        if ( hauskeysUserRadioButton == null )
        {
            hauskeysUserRadioButton = new JRadioButton();
            hauskeysUserRadioButton.setText( "Hauskeys User" );
            hauskeysUserRadioButton.setPreferredSize( new java.awt.Dimension( 117, 16 ) );
        }
        return hauskeysUserRadioButton;
    }


    public void setId( String id )
    {
        userIdTextField.setText( id );
    }


    public void setStatus( String status )
    {
        statusTextField.setText( status );
    }


    public String getStatus()
    {
        return statusTextField.getText();
    }


    public void setIcon( ImageIcon icon )
    {
        iconLabel.setIcon( icon );
    }


    public boolean isUpToDate()
    {
        if ( newEnityMode )
        {
            return UiUtils.isFieldUpToDate( userIdTextField, null );
        }
        return UiUtils.isFieldUpToDate( userIdTextField, user.getId() );
    }


    public void focusGained( FocusEvent e )
    {
        checkStatus();
    }


    public void focusLost( FocusEvent e )
    {
        checkStatus();
    }


    public void keyTyped( KeyEvent e )
    {
        checkStatus();
    }


    public void keyPressed( KeyEvent e )
    {
        checkStatus();
    }


    public void keyReleased( KeyEvent e )
    {
        checkStatus();
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
