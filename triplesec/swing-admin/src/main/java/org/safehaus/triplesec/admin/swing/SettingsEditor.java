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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.io.IOException;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JRadioButton;

import org.safehaus.triplesec.configuration.SmsConfiguration;
import org.safehaus.triplesec.configuration.SmtpConfiguration;


public class SettingsEditor extends JDialog
{
    private static final long serialVersionUID = -3628282784728303562L;
    private JPanel jContentPane = null;
    private JPanel jPanel1 = null;
    private JButton jButton = null;
    private JButton jButton1 = null;
    private JTabbedPane jTabbedPane = null;
    private JPanel generalPanel = null;
    private JPanel connectionPanel = null;
    private JPanel smsPanel = null;
    private JPanel mailPanel = null;
    private JLabel jLabel = null;
    private JPasswordField adminToolPasswordField = null;
    private JLabel jLabel1 = null;
    private JPasswordField adminToolConfirmPasswordField = null;
    private JLabel jLabel2 = null;
    private JPasswordField settingsPassphraseField = null;
    private JLabel jLabel3 = null;
    private JPasswordField settingsPassphraseConfirmField = null;
    private JLabel jLabel4 = null;
    private JTextField connHostTextField = null;
    private JLabel jLabel5 = null;
    private JTextField connLdapPortTextField = null;
    private JLabel jLabel6 = null;
    private JTextField connKrb5PortTextField = null;
    private JLabel jLabel7 = null;
    private JTextField connRealmTextField = null;
    private JLabel jLabel8 = null;
    private JTextField connPrincipalTextField = null;
    private JLabel jLabel9 = null;
    private JPasswordField connCredentialsField = null;
    private JLabel jLabel10 = null;
    private JPasswordField connCredentialsConfirmField = null;
    private JPanel jPanel = null;
    private JRadioButton connUseLdapsRadioButton = null;
    private JRadioButton connNoPasscodeRadioButton = null;
    private JLabel jLabel11 = null;
    private JTextField smsAccountNameTextField = null;
    private JLabel jLabel12 = null;
    private JPasswordField smsPasswordField = null;
    private JLabel jLabel13 = null;
    private JPasswordField smsConfirmPasswordField = null;
    private JLabel jLabel14 = null;
    private JTextField smsTransportUrlTextField = null;
    private JLabel jLabel15 = null;
    private JTextField smsUsernameTextField = null;
    private JLabel jLabel16 = null;
    private JTextField mailHostTextField = null;
    private JLabel jLabel17 = null;
    private JTextField mailFromTextField = null;
    private JLabel jLabel18 = null;
    private JTextField mailSubjectTextField = null;
    private JRadioButton mailAuthenticateRadioButton = null;
    private JLabel jLabel19 = null;
    private JTextField mailUsernameTextField = null;
    private JLabel jLabel20 = null;
    private JPasswordField mailPasswordField = null;
    private JLabel jLabel21 = null;
    private JPasswordField mailConfirmPasswordField = null;
    private AdminToolSettings settings = null;
    
    
    /**
     * This is the default constructor
     */
    public SettingsEditor()
    {
        super();
        setModal( true );
        initialize();
    }

    
    public void setSettings( AdminToolSettings settings )
    {
        this.settings = settings;
        
        if ( settings == null )
        {
            setSmartDefaults();
            return;
        }

        adminToolConfirmPasswordField.setText( settings.getAdminToolPassword() );
        adminToolPasswordField.setText( settings.getAdminToolPassword() );
        settingsPassphraseConfirmField.setText( settings.getSettingsPassphrase() );
        settingsPassphraseField.setText( settings.getSettingsPassphrase() );
        
        connCredentialsConfirmField.setText( settings.getDefaultConnectionInfo().getCredentials() );
        connCredentialsField.setText( settings.getDefaultConnectionInfo().getCredentials() );
        connHostTextField.setText( settings.getDefaultConnectionInfo().getHost() );
        connLdapPortTextField.setText( String.valueOf( settings.getDefaultConnectionInfo().getLdapPort() ) );
        connKrb5PortTextField.setText( String.valueOf( settings.getDefaultConnectionInfo().getKrb5Port() ) );
        connRealmTextField.setText( settings.getDefaultConnectionInfo().getRealm() );
        connPrincipalTextField.setText( settings.getDefaultConnectionInfo().getPrincipal() );
        connUseLdapsRadioButton.setSelected( settings.getDefaultConnectionInfo().isUseLdaps() );
        connNoPasscodeRadioButton.setSelected( settings.isPasscodePromptEnabled() );
        
        smsAccountNameTextField.setText( settings.getDefaultSmsConfig().getSmsAccountName() );
        smsConfirmPasswordField.setText( settings.getDefaultSmsConfig().getSmsPassword() );
        smsPasswordField.setText( settings.getDefaultSmsConfig().getSmsPassword() );
        smsTransportUrlTextField.setText( settings.getDefaultSmsConfig().getSmsTransportUrl() );
        smsUsernameTextField.setText( settings.getDefaultSmsConfig().getSmsUsername() );
        
        mailFromTextField.setText( settings.getDefaultSmtpConfig().getSmtpFrom() );
        mailHostTextField.setText( settings.getDefaultSmtpConfig().getSmtpHost() );
        mailSubjectTextField.setText( settings.getDefaultSmtpConfig().getSmtpSubject() );

        if ( settings.getDefaultSmtpConfig().isSmtpAuthenticate() )
        {
            mailAuthenticateRadioButton.setSelected( true );
            mailConfirmPasswordField.setText( settings.getDefaultSmtpConfig().getSmtpPassword() );
            mailPasswordField.setText( settings.getDefaultSmtpConfig().getSmtpPassword() );
            mailPasswordField.setEnabled( true );
            mailConfirmPasswordField.setEnabled( true );
            mailUsernameTextField.setEnabled( true );
            mailUsernameTextField.setText( settings.getDefaultSmtpConfig().getSmtpUsername() );
        }
        else
        {
            mailConfirmPasswordField.setText( null );
            mailPasswordField.setText( null );
            mailAuthenticateRadioButton.setSelected( false );
            mailPasswordField.setEnabled( false );
            mailConfirmPasswordField.setEnabled( false );
            mailUsernameTextField.setEnabled( false );
            mailUsernameTextField.setText( null );
        }
    }
    

    private void setSmartDefaults()
    {
        adminToolConfirmPasswordField.setText( null );
        adminToolPasswordField.setText( null );
        settingsPassphraseConfirmField.setText( null );
        settingsPassphraseField.setText( null );
        
        connCredentialsConfirmField.setText( null );
        connCredentialsField.setText( null );
        connHostTextField.setText( "localhost" );
        connLdapPortTextField.setText( "10389" );
        connKrb5PortTextField.setText( "88" );
        connRealmTextField.setText( null );
        connPrincipalTextField.setText( null );
        connUseLdapsRadioButton.setSelected( false );
        connNoPasscodeRadioButton.setSelected( true );
        
        smsAccountNameTextField.setText( null );
        smsConfirmPasswordField.setText( null );
        smsPasswordField.setText( null );
        smsTransportUrlTextField.setText( "http://www.nbroadcasting.com/customers/messages/Sender.asp" );
        smsUsernameTextField.setText( null );
        
        mailConfirmPasswordField.setText( null );
        mailPasswordField.setText( null );
        mailFromTextField.setText( "dev@safehaus.org" );
        mailHostTextField.setText( "localhost" );
        mailSubjectTextField.setText( "Triplesec Notifaction" );
        mailAuthenticateRadioButton.setSelected( false );
        mailPasswordField.setEnabled( false );
        mailConfirmPasswordField.setEnabled( false );
        mailUsernameTextField.setEnabled( false );
        mailUsernameTextField.setText( null );
    }

    
    public AdminToolSettings getSettings()
    {
        return this.settings;
    }
    

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(594, 418);
        this.setTitle("AdminTool Settings Editor");
        this.setContentPane( getJContentPane() );
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
        if ( jContentPane == null )
        {
            jContentPane = new JPanel();
            jContentPane.setLayout( new BorderLayout() );
            jContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
            jContentPane.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
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
            jPanel1 = new JPanel();
            jPanel1.add(getJButton(), null);
            jPanel1.add(getJButton1(), null);
        }
        return jPanel1;
    }


    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton()
    {
        if ( jButton == null )
        {
            jButton = new JButton();
            jButton.setText("Close");
            jButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    SettingsEditor.this.setVisible( false );
                    SettingsEditor.this.dispose();
                }
            } );
        }
        return jButton;
    }


    /**
     * This method initializes jButton1	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton1()
    {
        if ( jButton1 == null )
        {
            jButton1 = new JButton();
            jButton1.setText("Save");
            jButton1.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    if ( settings == null )
                    {
                        settings = new AdminToolSettings();
                        settings.setDefaultSmsConfig( new SmsConfiguration() );
                        settings.setDefaultSmtpConfig( new SmtpConfiguration() );
                    }

                    // -------------------------------------------------------
                    // handle the admin tool password field
                    // -------------------------------------------------------

                    if ( adminToolPasswordField.getPassword() == null || adminToolPasswordField.getPassword().length == 0 )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "AdminTool password cannot be null." );
                        return;
                    }
                    String adminToolPassword = new String( adminToolPasswordField.getPassword() );
                    String adminToolPasswordConfirm = new String( adminToolConfirmPasswordField.getPassword() );
                    if ( ! adminToolPassword.equals( adminToolPasswordConfirm ) )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "AdminTool password and confirmation fields are not the same." );
                        return;
                    }
                    settings.setAdminToolPassword( adminToolPassword );

                    // -------------------------------------------------------
                    // handle the settings passphrase field
                    // -------------------------------------------------------

                    if ( settingsPassphraseField.getPassword() == null || settingsPassphraseField.getPassword().length == 0 )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "Settings passphrase field cannot be null." );
                        return;
                    }
                    String settingsPassphrase = new String( settingsPassphraseField.getPassword() );
                    String settingsPassphraseConfirm = new String( settingsPassphraseConfirmField.getPassword() );
                    if ( ! settingsPassphrase.equals( settingsPassphraseConfirm ) )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "Settings passphrase and confirmation fields are not the same." );
                        return;
                    }
                    settings.setSettingsPassphrase( settingsPassphrase );

                    // -------------------------------------------------------
                    // handle the no passcode prompt field
                    // -------------------------------------------------------

                    settings.setPasscodePromptEnabled( connNoPasscodeRadioButton.isSelected() );
                    
                    // -------------------------------------------------------
                    // Handle connection fields
                    // -------------------------------------------------------
                    
                    ConnectionInfoModifier modifier = new ConnectionInfoModifier();
                    if ( connCredentialsField.getPassword() == null || connCredentialsField.getPassword().length == 0 )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "Connection credentials field cannot be null." );
                        return;
                    }
                    String connCredentials = new String( connCredentialsField.getPassword() );
                    String connCredentialsConfirm = new String( connCredentialsConfirmField.getPassword() );
                    if ( ! connCredentials.equals( connCredentialsConfirm ) )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "Connection credential fields are not the same." );
                        return;
                    }
                    modifier.setCredentials( connCredentials );
                    modifier.setHost( connHostTextField.getText() );
                    modifier.setKrb5Port( Integer.parseInt( connKrb5PortTextField.getText() ) );
                    modifier.setLdapPort( Integer.parseInt( connLdapPortTextField.getText() ) );
                    modifier.setRealm( connRealmTextField.getText() );
                    modifier.setPrincipal( connPrincipalTextField.getText() );
                    modifier.setUseLdaps( connUseLdapsRadioButton.isSelected() );
                    settings.setDefaultConnectionInfo( modifier.getConnectionInfo() );
                    
                    // -------------------------------------------------------
                    // Handle sms fields
                    // -------------------------------------------------------
                    
                    SmsConfiguration smsConfig = settings.getDefaultSmsConfig();
                    smsConfig.setSmsAccountName( smsAccountNameTextField.getText() );
                    smsConfig.setSmsTransportUrl( smsTransportUrlTextField.getText() );
                    smsConfig.setSmsUsername( smsUsernameTextField.getText() );
                    if ( smsPasswordField.getPassword() == null || smsPasswordField.getPassword().length == 0 )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "Sms password field cannot be null." );
                        return;
                    }
                    String smsPassword = new String( smsPasswordField.getPassword() );
                    String smsPasswordConfirm = new String( smsConfirmPasswordField.getPassword() );
                    if ( ! smsPassword.equals( smsPasswordConfirm ) )
                    {
                        JOptionPane.showMessageDialog( SettingsEditor.this, 
                            "Sms password and confirmation fields are not the same." );
                        return;
                    }
                    smsConfig.setSmsPassword( smsPassword );
                    
                    // -------------------------------------------------------
                    // Handle mail fields
                    // -------------------------------------------------------

                    SmtpConfiguration mailConfig = settings.getDefaultSmtpConfig();
                    mailConfig.setSmtpFrom( mailFromTextField.getText() );
                    mailConfig.setSmtpHost( mailHostTextField.getText() );
                    mailConfig.setSmtpSubject( mailSubjectTextField.getText() );
                    
                    if ( mailAuthenticateRadioButton.isSelected() )
                    {
                        mailConfig.setSmtpAuthenticate( true );
                        mailConfig.setSmtpUsername( mailUsernameTextField.getText() );
                        
                        if ( mailPasswordField.getPassword() == null || mailPasswordField.getPassword().length == 0 )
                        {
                            JOptionPane.showMessageDialog( SettingsEditor.this, 
                                "Smpt (mail) password field cannot be null." );
                            return;
                        }
                        String mailPassword = new String( mailPasswordField.getPassword() );
                        String mailPasswordConfirm = new String( mailConfirmPasswordField.getPassword() );
                        if ( ! mailPassword.equals( mailPasswordConfirm ) )
                        {
                            JOptionPane.showMessageDialog( SettingsEditor.this, 
                                "Smtp (mail) password and confirmation fields are not the same." );
                            return;
                        }
                        mailConfig.setSmtpPassword( mailPassword );
                    }
                    else
                    {
                        mailConfig.setSmtpAuthenticate( false );
                    }

                    try
                    {
                        SettingsUtils.store( settings );
                    }
                    catch ( IOException e1 )
                    {
                        String msg = UiUtils.wrap( "Failed to save admin tool settings: " + e1.getMessage(), 79 );
                        JOptionPane.showMessageDialog( SettingsEditor.this, msg );
                        return;
                    }
                    
                    SettingsEditor.this.setVisible( false );
                    SettingsEditor.this.dispose();
                }
            } );
        }
        return jButton1;
    }


    /**
     * This method initializes jTabbedPane	
     * 	
     * @return javax.swing.JTabbedPane	
     */
    private JTabbedPane getJTabbedPane()
    {
        if ( jTabbedPane == null )
        {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab("General", null, getGeneralPanel(), null);
            jTabbedPane.addTab("Connection", null, getConnectionPanel(), null);
            jTabbedPane.addTab("Sms", null, getSmsPanel(), null);
            jTabbedPane.addTab("Mail", null, getMailPanel(), null);
        }
        return jTabbedPane;
    }


    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getGeneralPanel()
    {
        if ( generalPanel == null )
        {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 3;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints31.gridy = 3;
            jLabel3 = new JLabel();
            jLabel3.setText("Settings Passphrase Confirm:");
            jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridy = 2;
            gridBagConstraints21.weightx = 1.0;
            gridBagConstraints21.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints21.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Settings Passphrase:");
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints2.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("AdminTool Password Confirm:");
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("AdminTool Password:");
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            generalPanel = new JPanel();
            generalPanel.setLayout(new GridBagLayout());
            generalPanel.add(jLabel, gridBagConstraints);
            generalPanel.add(getAdminToolPasswordField(), gridBagConstraints1);
            generalPanel.add(jLabel1, gridBagConstraints2);
            generalPanel.add(getAdminToolConfirmPasswordField(), gridBagConstraints3);
            generalPanel.add(jLabel2, gridBagConstraints11);
            generalPanel.add(getSettingsPassphraseField(), gridBagConstraints21);
            generalPanel.add(jLabel3, gridBagConstraints31);
            generalPanel.add(getSettingsPassphraseConfirmField(), gridBagConstraints4);
        }
        return generalPanel;
    }


    /**
     * This method initializes jPanel2	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getConnectionPanel()
    {
        if ( connectionPanel == null )
        {
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 1;
            gridBagConstraints20.insets = new java.awt.Insets(10,0,0,0);
            gridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints20.gridy = 7;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints19.gridy = 6;
            gridBagConstraints19.weightx = 1.0;
            gridBagConstraints19.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints19.gridx = 1;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints18.gridy = 6;
            jLabel10 = new JLabel();
            jLabel10.setText("Credentials Confirm:");
            jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 5;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints17.gridx = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints16.gridy = 5;
            jLabel9 = new JLabel();
            jLabel9.setText("Credentials:");
            jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 4;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.gridy = 4;
            jLabel8 = new JLabel();
            jLabel8.setText("Principal:");
            jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridy = 3;
            gridBagConstraints13.weightx = 1.0;
            gridBagConstraints13.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints13.gridx = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 3;
            jLabel7 = new JLabel();
            jLabel7.setText("Realm:");
            jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 2;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 2;
            jLabel6 = new JLabel();
            jLabel6.setText("Krb5 Port:");
            jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 1;
            jLabel5 = new JLabel();
            jLabel5.setText("Ldap Port:");
            jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints5.gridy = 0;
            jLabel4 = new JLabel();
            jLabel4.setText("Host:");
            jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            connectionPanel = new JPanel();
            connectionPanel.setLayout(new GridBagLayout());
            connectionPanel.add(jLabel4, gridBagConstraints5);
            connectionPanel.add(getConnHostTextField(), gridBagConstraints6);
            connectionPanel.add(jLabel5, gridBagConstraints7);
            connectionPanel.add(getConnLdapPortTextField(), gridBagConstraints8);
            connectionPanel.add(jLabel6, gridBagConstraints9);
            connectionPanel.add(getConnKrb5PortTextField(), gridBagConstraints10);
            connectionPanel.add(jLabel7, gridBagConstraints12);
            connectionPanel.add(getConnRealmTextField(), gridBagConstraints13);
            connectionPanel.add(jLabel8, gridBagConstraints14);
            connectionPanel.add(getConnPrincipalTextField(), gridBagConstraints15);
            connectionPanel.add(jLabel9, gridBagConstraints16);
            connectionPanel.add(getConnCredentialsField(), gridBagConstraints17);
            connectionPanel.add(jLabel10, gridBagConstraints18);
            connectionPanel.add(getConnCredentialsConfirmField(), gridBagConstraints19);
            connectionPanel.add(getJPanel(), gridBagConstraints20);
        }
        return connectionPanel;
    }


    /**
     * This method initializes jPanel3	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSmsPanel()
    {
        if ( smsPanel == null )
        {
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints32.gridy = 4;
            gridBagConstraints32.weightx = 1.0;
            gridBagConstraints32.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints32.gridx = 1;
            GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
            gridBagConstraints30.gridx = 0;
            gridBagConstraints30.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints30.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints30.gridy = 4;
            jLabel15 = new JLabel();
            jLabel15.setText("Username:");
            jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
            gridBagConstraints29.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints29.gridy = 3;
            gridBagConstraints29.weightx = 1.0;
            gridBagConstraints29.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints29.gridx = 1;
            GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
            gridBagConstraints28.gridx = 0;
            gridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints28.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints28.gridy = 3;
            jLabel14 = new JLabel();
            jLabel14.setText("Transport Url:");
            jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints27.gridy = 2;
            gridBagConstraints27.weightx = 1.0;
            gridBagConstraints27.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints27.gridx = 1;
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.gridx = 0;
            gridBagConstraints26.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints26.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints26.gridy = 2;
            jLabel13 = new JLabel();
            jLabel13.setText("Password Confirm:");
            jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints25.gridy = 1;
            gridBagConstraints25.weightx = 1.0;
            gridBagConstraints25.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints25.gridx = 1;
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.gridx = 0;
            gridBagConstraints24.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints24.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints24.gridy = 1;
            jLabel12 = new JLabel();
            jLabel12.setText("Password:");
            jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints23.gridy = 0;
            gridBagConstraints23.weightx = 1.0;
            gridBagConstraints23.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints23.gridx = 1;
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints22.gridy = 0;
            jLabel11 = new JLabel();
            jLabel11.setText("Account Name:");
            jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            smsPanel = new JPanel();
            smsPanel.setLayout(new GridBagLayout());
            smsPanel.add(jLabel11, gridBagConstraints22);
            smsPanel.add(getSmsAccountNameTextField(), gridBagConstraints23);
            smsPanel.add(jLabel12, gridBagConstraints24);
            smsPanel.add(getSmsPasswordField(), gridBagConstraints25);
            smsPanel.add(jLabel13, gridBagConstraints26);
            smsPanel.add(getSmsConfirmPasswordField(), gridBagConstraints27);
            smsPanel.add(jLabel14, gridBagConstraints28);
            smsPanel.add(getSmsTransportUrlTextField(), gridBagConstraints29);
            smsPanel.add(jLabel15, gridBagConstraints30);
            smsPanel.add(getSmsUsernameTextField(), gridBagConstraints32);
        }
        return smsPanel;
    }


    /**
     * This method initializes jPanel4	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMailPanel()
    {
        if ( mailPanel == null )
        {
            GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
            gridBagConstraints45.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints45.gridy = 6;
            gridBagConstraints45.weightx = 1.0;
            gridBagConstraints45.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints45.gridx = 1;
            GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
            gridBagConstraints44.gridx = 0;
            gridBagConstraints44.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints44.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints44.gridy = 6;
            jLabel21 = new JLabel();
            jLabel21.setText("Confirm Pasword:");
            jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
            gridBagConstraints43.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints43.gridy = 5;
            gridBagConstraints43.weightx = 1.0;
            gridBagConstraints43.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints43.gridx = 1;
            GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
            gridBagConstraints42.gridx = 0;
            gridBagConstraints42.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints42.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints42.gridy = 5;
            jLabel20 = new JLabel();
            jLabel20.setText("Password:");
            jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints41.gridy = 4;
            gridBagConstraints41.weightx = 1.0;
            gridBagConstraints41.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints41.gridx = 1;
            GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
            gridBagConstraints40.gridx = 0;
            gridBagConstraints40.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints40.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints40.gridy = 4;
            jLabel19 = new JLabel();
            jLabel19.setText("Username:");
            jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
            gridBagConstraints39.gridx = 1;
            gridBagConstraints39.insets = new java.awt.Insets(10,0,5,0);
            gridBagConstraints39.gridy = 3;
            GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
            gridBagConstraints38.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints38.gridy = 2;
            gridBagConstraints38.weightx = 1.0;
            gridBagConstraints38.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints38.gridx = 1;
            GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
            gridBagConstraints37.gridx = 0;
            gridBagConstraints37.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints37.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints37.gridy = 2;
            jLabel18 = new JLabel();
            jLabel18.setText("Subject:");
            jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
            gridBagConstraints36.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints36.gridy = 1;
            gridBagConstraints36.weightx = 1.0;
            gridBagConstraints36.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints36.gridx = 1;
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.gridx = 0;
            gridBagConstraints35.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints35.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints35.gridy = 1;
            jLabel17 = new JLabel();
            jLabel17.setText("From:");
            jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
            gridBagConstraints34.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints34.gridy = 0;
            gridBagConstraints34.weightx = 1.0;
            gridBagConstraints34.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints34.gridx = 1;
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints33.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints33.gridy = 0;
            jLabel16 = new JLabel();
            jLabel16.setText("Smtp Server:");
            jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            mailPanel = new JPanel();
            mailPanel.setLayout(new GridBagLayout());
            mailPanel.add(jLabel16, gridBagConstraints33);
            mailPanel.add(getMailHostTextField(), gridBagConstraints34);
            mailPanel.add(jLabel17, gridBagConstraints35);
            mailPanel.add(getMailFromTextField(), gridBagConstraints36);
            mailPanel.add(jLabel18, gridBagConstraints37);
            mailPanel.add(getMailSubjectTextField(), gridBagConstraints38);
            mailPanel.add(getMailAuthenticateRadioButton(), gridBagConstraints39);
            mailPanel.add(jLabel19, gridBagConstraints40);
            mailPanel.add(getMailUsernameTextField(), gridBagConstraints41);
            mailPanel.add(jLabel20, gridBagConstraints42);
            mailPanel.add(getMailPasswordField(), gridBagConstraints43);
            mailPanel.add(jLabel21, gridBagConstraints44);
            mailPanel.add(getMailConfirmPasswordField(), gridBagConstraints45);
        }
        return mailPanel;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getAdminToolPasswordField()
    {
        if ( adminToolPasswordField == null )
        {
            adminToolPasswordField = new JPasswordField();
            adminToolPasswordField.setColumns(12);
        }
        return adminToolPasswordField;
    }


    /**
     * This method initializes jPasswordField1	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getAdminToolConfirmPasswordField()
    {
        if ( adminToolConfirmPasswordField == null )
        {
            adminToolConfirmPasswordField = new JPasswordField();
        }
        return adminToolConfirmPasswordField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getSettingsPassphraseField()
    {
        if ( settingsPassphraseField == null )
        {
            settingsPassphraseField = new JPasswordField();
        }
        return settingsPassphraseField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getSettingsPassphraseConfirmField()
    {
        if ( settingsPassphraseConfirmField == null )
        {
            settingsPassphraseConfirmField = new JPasswordField();
        }
        return settingsPassphraseConfirmField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getConnHostTextField()
    {
        if ( connHostTextField == null )
        {
            connHostTextField = new JTextField();
        }
        return connHostTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getConnLdapPortTextField()
    {
        if ( connLdapPortTextField == null )
        {
            connLdapPortTextField = new JTextField();
        }
        return connLdapPortTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getConnKrb5PortTextField()
    {
        if ( connKrb5PortTextField == null )
        {
            connKrb5PortTextField = new JTextField();
        }
        return connKrb5PortTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getConnRealmTextField()
    {
        if ( connRealmTextField == null )
        {
            connRealmTextField = new JTextField();
        }
        return connRealmTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getConnPrincipalTextField()
    {
        if ( connPrincipalTextField == null )
        {
            connPrincipalTextField = new JTextField();
        }
        return connPrincipalTextField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getConnCredentialsField()
    {
        if ( connCredentialsField == null )
        {
            connCredentialsField = new JPasswordField();
        }
        return connCredentialsField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getConnCredentialsConfirmField()
    {
        if ( connCredentialsConfirmField == null )
        {
            connCredentialsConfirmField = new JPasswordField();
        }
        return connCredentialsConfirmField;
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
            jPanel = new JPanel();
            jPanel.add(getConnUseLdapsRadioButton(), null);
            jPanel.add(getConnNoPasscodeRadioButton(), null);
        }
        return jPanel;
    }


    /**
     * This method initializes jRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getConnUseLdapsRadioButton()
    {
        if ( connUseLdapsRadioButton == null )
        {
            connUseLdapsRadioButton = new JRadioButton();
            connUseLdapsRadioButton.setText("Use Ldaps");
        }
        return connUseLdapsRadioButton;
    }


    /**
     * This method initializes jRadioButton1	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getConnNoPasscodeRadioButton()
    {
        if ( connNoPasscodeRadioButton == null )
        {
            connNoPasscodeRadioButton = new JRadioButton();
            connNoPasscodeRadioButton.setText("Do Not Prompt for Passcode");
        }
        return connNoPasscodeRadioButton;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getSmsAccountNameTextField()
    {
        if ( smsAccountNameTextField == null )
        {
            smsAccountNameTextField = new JTextField();
        }
        return smsAccountNameTextField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getSmsPasswordField()
    {
        if ( smsPasswordField == null )
        {
            smsPasswordField = new JPasswordField();
        }
        return smsPasswordField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getSmsConfirmPasswordField()
    {
        if ( smsConfirmPasswordField == null )
        {
            smsConfirmPasswordField = new JPasswordField();
        }
        return smsConfirmPasswordField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getSmsTransportUrlTextField()
    {
        if ( smsTransportUrlTextField == null )
        {
            smsTransportUrlTextField = new JTextField();
        }
        return smsTransportUrlTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getSmsUsernameTextField()
    {
        if ( smsUsernameTextField == null )
        {
            smsUsernameTextField = new JTextField();
        }
        return smsUsernameTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getMailHostTextField()
    {
        if ( mailHostTextField == null )
        {
            mailHostTextField = new JTextField();
        }
        return mailHostTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getMailFromTextField()
    {
        if ( mailFromTextField == null )
        {
            mailFromTextField = new JTextField();
        }
        return mailFromTextField;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getMailSubjectTextField()
    {
        if ( mailSubjectTextField == null )
        {
            mailSubjectTextField = new JTextField();
        }
        return mailSubjectTextField;
    }


    /**
     * This method initializes jRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getMailAuthenticateRadioButton()
    {
        if ( mailAuthenticateRadioButton == null )
        {
            mailAuthenticateRadioButton = new JRadioButton();
            mailAuthenticateRadioButton.setText("Enable Authentication");
            mailAuthenticateRadioButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    if ( mailAuthenticateRadioButton.isSelected() )
                    {
                        mailUsernameTextField.setEnabled( true );
                        mailPasswordField.setEnabled( true );
                        mailConfirmPasswordField.setEnabled( true );
                    }
                    else 
                    {
                        mailUsernameTextField.setEnabled( false );
                        mailPasswordField.setEnabled( false );
                        mailConfirmPasswordField.setEnabled( false );
                    }
                }
            } );
        }
        return mailAuthenticateRadioButton;
    }


    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getMailUsernameTextField()
    {
        if ( mailUsernameTextField == null )
        {
            mailUsernameTextField = new JTextField();
        }
        return mailUsernameTextField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getMailPasswordField()
    {
        if ( mailPasswordField == null )
        {
            mailPasswordField = new JPasswordField();
        }
        return mailPasswordField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getMailConfirmPasswordField()
    {
        if ( mailConfirmPasswordField == null )
        {
            mailConfirmPasswordField = new JPasswordField();
        }
        return mailConfirmPasswordField;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
