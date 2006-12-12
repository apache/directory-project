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


public class HotpSettingsPanel extends JPanel implements StatusObject, KeyListener, FocusListener
{
    private static final long serialVersionUID = 1L;
    private JPasswordField secretPasswordField = null;
    private JTextField movingFactorTextField = null;
    private JTextField activationKeyTextField = null;
    private JTextField failuresInEpochTextField = null;
    private HauskeysUser user;
    private StatusListener listener;
    private boolean lastStatusState = true;  // true = up to date, false means it was not up to date
    private boolean newEntityMode = false;
    

    /**
     * This is the default constructor
     */
    public HotpSettingsPanel()
    {
        super();
        initialize();
    }

    
    public void setNewEntityMode( boolean newEntityMode )
    {
        this.newEntityMode = newEntityMode;
    }
    
    
    public void alterModifier( Object unkownModifier )
    {
        if ( unkownModifier instanceof HauskeysUserModifier )
        {
            HauskeysUserModifier modifier = ( HauskeysUserModifier ) unkownModifier;
            modifier.setSecret( new String( secretPasswordField.getPassword() ) );
            modifier.setMovingFactor( movingFactorTextField.getText() );
            modifier.setActivationKey( activationKeyTextField.getText() );
            modifier.setFailuresInEpoch( failuresInEpochTextField.getText() );
        }
    }
    
    
    public void setFields( HauskeysUser user )
    {
        this.user = user;
        this.lastStatusState = true;
        secretPasswordField.setText( user.getSecret() );
        movingFactorTextField.setText( user.getMovingFactor() );
        activationKeyTextField.setText( user.getActivationKey() );
        failuresInEpochTextField.setText( user.getFailuresInEpoch() );
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(517, 241);
        GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
        gridBagConstraints52.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints52.gridy = 3;
        gridBagConstraints52.weightx = 1.0;
        gridBagConstraints52.insets = new java.awt.Insets( 0, 0, 0, 5 );
        gridBagConstraints52.gridx = 1;
        GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
        gridBagConstraints51.gridx = 0;
        gridBagConstraints51.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints51.insets = new java.awt.Insets(0,0,0,5);
        gridBagConstraints51.gridy = 3;
        JLabel jLabel24 = new JLabel();
        jLabel24.setText( "Failures In Epoch:" );
        jLabel24.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
        gridBagConstraints50.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints50.gridy = 2;
        gridBagConstraints50.weightx = 1.0;
        gridBagConstraints50.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints50.gridx = 1;
        GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
        gridBagConstraints49.gridx = 0;
        gridBagConstraints49.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints49.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints49.gridy = 2;
        JLabel jLabel23 = new JLabel();
        jLabel23.setText( "Activation Key:" );
        jLabel23.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
        gridBagConstraints48.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints48.gridy = 1;
        gridBagConstraints48.weightx = 1.0;
        gridBagConstraints48.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints48.gridx = 1;
        GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
        gridBagConstraints47.gridx = 0;
        gridBagConstraints47.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints47.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints47.gridy = 1;
        JLabel jLabel22 = new JLabel();
        jLabel22.setText( "Moving Factor:" );
        jLabel22.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
        gridBagConstraints46.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints46.gridy = 0;
        gridBagConstraints46.weightx = 1.0;
        gridBagConstraints46.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints46.gridx = 1;
        GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
        gridBagConstraints45.gridx = 0;
        gridBagConstraints45.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints45.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints45.gridy = 0;
        JLabel jLabel21 = new JLabel();
        jLabel21.setText( "Secret:" );
        jLabel21.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        setLayout( new GridBagLayout() );
        add( jLabel21, gridBagConstraints45 );
        add( getSecretPasswordField(), gridBagConstraints46 );
        add( jLabel22, gridBagConstraints47 );
        add( getMovingFactorTextField(), gridBagConstraints48 );
        add( jLabel23, gridBagConstraints49 );
        add( getActivationKeyTextField(), gridBagConstraints50 );
        this.add(jLabel24, gridBagConstraints51);
        add( getFailuresInEpochTextField(), gridBagConstraints52 );
    }


    /**
     * This method initializes jPasswordField   
     *  
     * @return javax.swing.JPasswordField   
     */
    private JPasswordField getSecretPasswordField()
    {
        if ( secretPasswordField == null )
        {
            secretPasswordField = new JPasswordField();
            secretPasswordField.addFocusListener( this );
            secretPasswordField.addKeyListener( this );
        }
        return secretPasswordField;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getMovingFactorTextField()
    {
        if ( movingFactorTextField == null )
        {
            movingFactorTextField = new JTextField();
            movingFactorTextField.addFocusListener( this );
            movingFactorTextField.addKeyListener( this );
        }
        return movingFactorTextField;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getActivationKeyTextField()
    {
        if ( activationKeyTextField == null )
        {
            activationKeyTextField = new JTextField();
            activationKeyTextField.addFocusListener( this );
            activationKeyTextField.addKeyListener( this );
        }
        return activationKeyTextField;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getFailuresInEpochTextField()
    {
        if ( failuresInEpochTextField == null )
        {
            failuresInEpochTextField = new JTextField();
            failuresInEpochTextField.addFocusListener( this );
            failuresInEpochTextField.addKeyListener( this );
        }
        return failuresInEpochTextField;
    }


    public String getActivationKey()
    {
        return activationKeyTextField.getText();
    }
    
    
    public String getFailuresInEpoch()
    {
        return failuresInEpochTextField.getText();
    }
    
    
    public String getSecret()
    {
        return new String( secretPasswordField.getPassword() );
    }


    public String getMovingFactor()
    {
        return movingFactorTextField.getText();
    }


    public boolean isUpToDate()
    {
        if ( newEntityMode )
        {
            return UiUtils.isFieldUpToDate( this.activationKeyTextField, null ) &&
            UiUtils.isFieldUpToDate( this.failuresInEpochTextField, null ) &&
            UiUtils.isFieldUpToDate( this.movingFactorTextField, null ) &&
            UiUtils.isFieldUpToDate( this.secretPasswordField, null );
        }
        return UiUtils.isFieldUpToDate( this.activationKeyTextField, user.getActivationKey() ) &&
            UiUtils.isFieldUpToDate( this.failuresInEpochTextField, user.getFailuresInEpoch() ) &&
            UiUtils.isFieldUpToDate( this.movingFactorTextField, user.getMovingFactor() ) &&
            UiUtils.isFieldUpToDate( this.secretPasswordField, user.getSecret() );
    }

    
    public void setStatusListener( StatusListener listener ) 
    {
        this.listener = listener;
    }
    
    
    private void checkStatus()
    {
        boolean upToDate = isUpToDate();
        if ( listener != null && lastStatusState != upToDate )
        {
            listener.statusChanged( HotpSettingsPanel.this );
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
