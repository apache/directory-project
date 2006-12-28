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


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.safehaus.triplesec.admin.HauskeysUser;
import org.safehaus.triplesec.admin.HauskeysUserModifier;
import org.safehaus.triplesec.admin.LocalUser;
import org.safehaus.triplesec.admin.LocalUserModifier;
import org.safehaus.triplesec.admin.User;


public class UserInfoPanel extends JPanel implements StatusObject, FocusListener, KeyListener
{
    private static final long serialVersionUID = 1L;
    private JTextField firstNameTextField = null;
    private JTextField lastNameTextField = null;
    private JPasswordField passwordField = null;
    private JTextField realmTextField = null;
    private JLabel jLabel = null;
    private JPasswordField confirmField = null;
    private User user;
    private StatusListener listener;
    private boolean lastStatusState = true;  // true = up to date, false means it was not up to date
    private boolean newEntityMode = false;
    
    
    /**
     * This is the default constructor
     */
    public UserInfoPanel()
    {
        super();
        initialize();
    }
    
    
    public void setNewEntityMode( boolean newEntityMode )
    {
        this.newEntityMode = newEntityMode;
    }
    
    
    public void setStatusListener( StatusListener listener ) 
    {
        this.listener = listener;
    }
    
    
    public void setFields( HauskeysUser hauskeysUser )
    {
        this.user = hauskeysUser;
        this.lastStatusState = true;
        firstNameTextField.setText( hauskeysUser.getFirstName() );
        lastNameTextField.setText( hauskeysUser.getLastName() );
        passwordField.setText( hauskeysUser.getPassword() );
        confirmField.setText( hauskeysUser.getPassword() );
        realmTextField.setText( hauskeysUser.getRealm() );
    }


    public void setFields( LocalUser localUser, String realm )
    {
        this.user = localUser;
        this.lastStatusState = true;
        firstNameTextField.setText( localUser.getFirstName() );
        lastNameTextField.setText( localUser.getLastName() );
        passwordField.setText( localUser.getPassword() );
        confirmField.setText( localUser.getPassword() );
        realmTextField.setText( realm );
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new java.awt.Insets(0,0,5,5);
        gridBagConstraints1.gridx = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0,5,5,5);
        gridBagConstraints.gridy = 3;
        jLabel = new JLabel();
        jLabel.setText("Password Confirm:");
        jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        this.setSize(577, 272);
        GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
        gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints32.gridy = 4;
        gridBagConstraints32.weightx = 1.0;
        gridBagConstraints32.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints32.gridx = 1;
        GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
        gridBagConstraints31.gridx = 0;
        gridBagConstraints31.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints31.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints31.gridy = 4;
        JLabel jLabel14 = new JLabel();
        jLabel14.setText( "Realm:" );
        jLabel14.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
        gridBagConstraints30.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints30.gridy = 2;
        gridBagConstraints30.weightx = 1.0;
        gridBagConstraints30.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints30.gridx = 1;
        GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
        gridBagConstraints29.gridx = 0;
        gridBagConstraints29.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints29.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints29.gridy = 2;
        JLabel jLabel13 = new JLabel();
        jLabel13.setText( "Password:" );
        jLabel13.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
        gridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints28.gridy = 1;
        gridBagConstraints28.weightx = 1.0;
        gridBagConstraints28.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints28.gridx = 1;
        GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
        gridBagConstraints27.gridx = 0;
        gridBagConstraints27.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints27.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints27.gridy = 1;
        JLabel jLabel12 = new JLabel();
        jLabel12.setText( "Last Name:" );
        jLabel12.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
        gridBagConstraints26.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints26.gridy = 0;
        gridBagConstraints26.weightx = 1.0;
        gridBagConstraints26.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints26.gridx = 1;
        GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
        gridBagConstraints25.gridx = 0;
        gridBagConstraints25.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints25.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints25.gridy = 0;
        JLabel jLabel11 = new JLabel();
        jLabel11.setText( "First Name:" );
        jLabel11.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        setLayout( new GridBagLayout() );
        add( jLabel11, gridBagConstraints25 );
        add( getFirstNameTextField(), gridBagConstraints26 );
        add( jLabel12, gridBagConstraints27 );
        add( getLastNameTextField(), gridBagConstraints28 );
        add( jLabel13, gridBagConstraints29 );
        add( getPasswordField(), gridBagConstraints30 );
        this.add(jLabel14, gridBagConstraints31);
        this.add(getRealmTextField(), gridBagConstraints32);
        this.add(jLabel, gridBagConstraints);
        this.add(getConfirmField(), gridBagConstraints1);
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getFirstNameTextField()
    {
        if ( firstNameTextField == null )
        {
            firstNameTextField = new JTextField();
            firstNameTextField.addFocusListener( this );
            firstNameTextField.addKeyListener( this );
        }
        return firstNameTextField;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getLastNameTextField()
    {
        if ( lastNameTextField == null )
        {
            lastNameTextField = new JTextField();
            lastNameTextField.addFocusListener( this );
            lastNameTextField.addKeyListener( this );
        }
        return lastNameTextField;
    }


    /**
     * This method initializes jPasswordField   
     *  
     * @return javax.swing.JPasswordField   
     */
    private JPasswordField getPasswordField()
    {
        if ( passwordField == null )
        {
            passwordField = new JPasswordField();
            passwordField.addFocusListener( this );
            passwordField.addKeyListener( this );
        }
        return passwordField;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getRealmTextField()
    {
        if ( realmTextField == null )
        {
            realmTextField = new JTextField();
            realmTextField.setEditable( false );
        }
        return realmTextField;
    }


    public void alterModifier( HauskeysUserModifier modifier )
    {
        modifier.setFirstName( firstNameTextField.getText() );
        modifier.setLastName( lastNameTextField.getText() );
        modifier.setPassword( new String( passwordField.getPassword() ) );
    }


    public void alterModifier( LocalUserModifier modifier )
    {
        modifier.setFirstName( firstNameTextField.getText() );
        modifier.setLastName( lastNameTextField.getText() );
        modifier.setPassword( new String( passwordField.getPassword() ) );
    }


    public String getFirstName()
    {
        return firstNameTextField.getText();
    }
    
    
    public String getLastName()
    {
        return lastNameTextField.getText();
    }
    
    
    public String getPassword()
    {
        return new String( passwordField.getPassword() );
    }
    
    
    public String getPasswordConfirm()
    {
        return new String( confirmField.getPassword() );
    }
    
    
    public boolean isPasswordOk()
    {
        if ( passwordField.getPassword() == null || confirmField.getPassword() == null )
        {
            return false;
        }
        String password = new String( passwordField.getPassword() );
        String confirm = new String( confirmField.getPassword() );
        return password.equals( confirm );
    }
    
    
    public String getRealm()
    {
        return realmTextField.getText();
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getConfirmField()
    {
        if ( confirmField == null )
        {
            confirmField = new JPasswordField();
            confirmField.addFocusListener( this );
            confirmField.addKeyListener( this );
        }
        return confirmField;
    }

    
    public boolean isUpToDate()
    {
        if ( newEntityMode )
        {
            return UiUtils.isFieldUpToDate( firstNameTextField, null ) &&
            UiUtils.isFieldUpToDate( lastNameTextField, null ) &&
            UiUtils.isFieldUpToDate( passwordField, null ) &&
            UiUtils.isFieldUpToDate( confirmField, null );
        }

        if ( user instanceof LocalUser )
        {
            LocalUser lu = ( LocalUser ) user;
            return UiUtils.isFieldUpToDate( firstNameTextField, lu.getFirstName() ) &&
                UiUtils.isFieldUpToDate( lastNameTextField, lu.getLastName() ) &&
                UiUtils.isFieldUpToDate( passwordField, lu.getPassword() ) &&
                UiUtils.isFieldUpToDate( confirmField, lu.getPassword() );
        }

        HauskeysUser hu = ( HauskeysUser ) user;
        return UiUtils.isFieldUpToDate( firstNameTextField, hu.getFirstName() ) &&
            UiUtils.isFieldUpToDate( lastNameTextField, hu.getLastName() ) &&
            UiUtils.isFieldUpToDate( passwordField, hu.getPassword() ) &&
            UiUtils.isFieldUpToDate( confirmField, hu.getPassword() );
    }

    
    private void checkStatus()
    {
        boolean upToDate = isUpToDate();
        if ( listener != null && lastStatusState != upToDate )
        {
            listener.statusChanged( UserInfoPanel.this );
            lastStatusState = upToDate;
        }
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
