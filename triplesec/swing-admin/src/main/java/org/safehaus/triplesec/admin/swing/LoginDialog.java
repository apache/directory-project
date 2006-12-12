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
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JButton;


/**
 * A login dialog used to capture optional 2-factor authentication credentials
 * as well as standard static password credentials.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LoginDialog extends JDialog
{
    private static final long serialVersionUID = -7314707823982966424L;

    private JPanel mainPanel = null;
    private JPanel inputPanel = null;
    private JPanel usernamePanel = null;
    private JLabel usernameLabel = null;
    private JTextField usernameField = null;
    private JPanel passwordPanel = null;
    private JLabel passwordLabel = null;
    private JPasswordField passwordField = null;
    private JPanel realmPanel = null;
    private JLabel realmLabel = null;
    private JComboBox realmComboBox = null;
    private JPanel passcodePanel = null;
    private JLabel passcodeLabel = null;
    private JPasswordField passcodeField = null;
    private JPanel buttonPanel = null;
    private JButton loginButton = null;
    private JButton cancelButton = null;
    private ConnectionInfoModifier modifier;
    

    // -----------------------------------------------------------------------
    // Constructors and main initialization routine
    // -----------------------------------------------------------------------
    
    
    /**
     * This is the default constructor
     */
    public LoginDialog()
    {
        super();
        modifier = new ConnectionInfoModifier();
        setModal( true );
        initialize();
    }


    public LoginDialog( ConnectionInfoModifier modifier )
    {
        super();
        this.modifier = modifier;
        setModal( true );
        initialize();
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize( 325, 257 );
        //this.setPreferredSize( new java.awt.Dimension( 400, 200 ) );
        this.setTitle( "Login" );
        this.setContentPane( getMainPanel() );
    }

    
    public ConnectionInfo getConnectionInfo()
    {
        return modifier.getConnectionInfo();
    }
    

    // -----------------------------------------------------------------------
    // Individual subcomponent intialization methods 
    // -----------------------------------------------------------------------
    
    
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel()
    {
        if ( mainPanel == null )
        {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridy = 6;
            gridBagConstraints11.gridwidth = 1;
            gridBagConstraints11.gridheight = 2;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints11.insets = new java.awt.Insets(12,0,0,0);
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridheight = 5;
            gridBagConstraints.gridy = 1;
            mainPanel = new JPanel();
            mainPanel.setLayout( new GridBagLayout() );
            mainPanel.add( getInputPanel(), gridBagConstraints );
            mainPanel.add(getJPanel15(), gridBagConstraints11);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInputPanel()
    {
        if ( inputPanel == null )
        {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridy = 3;
            gridBagConstraints3.gridx = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            inputPanel = new JPanel();
            inputPanel.setLayout( new GridBagLayout() );
            inputPanel.setBorder( javax.swing.BorderFactory.createEtchedBorder( javax.swing.border.EtchedBorder.RAISED ) );
            inputPanel.add( getJPanel1(), new GridBagConstraints() );
            inputPanel.add( getJPanel12(), gridBagConstraints1 );
            inputPanel.add( getJPanel13(), gridBagConstraints2 );
            inputPanel.add( getJPanel14(), gridBagConstraints3 );
        }
        return inputPanel;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1()
    {
        if ( usernamePanel == null )
        {
            usernameLabel = new JLabel();
            usernameLabel.setText( "Username:" );
            usernamePanel = new JPanel();
            usernamePanel.add( usernameLabel, null );
            usernamePanel.add( getUsernameField(), null );
        }
        return usernamePanel;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUsernameField()
    {
        if ( usernameField == null )
        {
            usernameField = new JTextField();
            usernameField.setColumns( 12 );
            usernameField.setText("");
        }
        return usernameField;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel12()
    {
        if ( passwordPanel == null )
        {
            passwordLabel = new JLabel();
            passwordLabel.setText( "Password:" );
            passwordPanel = new JPanel();
            passwordPanel.add( passwordLabel, null );
            passwordPanel.add( getPasswordField(), null );
        }
        return passwordPanel;
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
            passwordField.setColumns( 12 );
        }
        return passwordField;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel13()
    {
        if ( realmPanel == null )
        {
            realmLabel = new JLabel();
            realmLabel.setText("    Realm:");
            realmPanel = new JPanel();
            realmPanel.add( realmLabel, null );
            realmPanel.add( getRealmComboBox(), null );
        }
        return realmPanel;
    }


    /**
     * This method initializes jComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getRealmComboBox()
    {
        if ( realmComboBox == null )
        {
            realmComboBox = new JComboBox();
            realmComboBox.setPreferredSize( new java.awt.Dimension( 132, 18 ) );
            realmComboBox.setEditable( true );
        }
        return realmComboBox;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel14()
    {
        if ( passcodePanel == null )
        {
            passcodeLabel = new JLabel();
            passcodeLabel.setText( "Passcode:" );
            passcodePanel = new JPanel();
            passcodePanel.add( passcodeLabel, null );
            passcodePanel.add( getPasscodeField(), null );
        }
        return passcodePanel;
    }


    /**
     * This method initializes jPasswordField1
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getPasscodeField()
    {
        if ( passcodeField == null )
        {
            passcodeField = new JPasswordField();
            passcodeField.setColumns( 12 );
        }
        return passcodeField;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel15()
    {
        if ( buttonPanel == null )
        {
            buttonPanel = new JPanel();
            buttonPanel.setBorder( javax.swing.BorderFactory.createEtchedBorder( javax.swing.border.EtchedBorder.RAISED ) );
            buttonPanel.add( getCancelButton(), null );
            buttonPanel.add( getLoginButton(), null );
        }
        return buttonPanel;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoginButton()
    {
        if ( loginButton == null )
        {
            loginButton = new JButton();
            loginButton.setText( "Login" );
            loginButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    if ( getPassword() == null || getPassword().length == 0 )
                    {
                        JOptionPane.showMessageDialog( LoginDialog.this, 
                            "Password not supplied.", "Login Failed", JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                    
                    if ( getUserName() == null || getUserName().length() == 0 )
                    {
                        JOptionPane.showMessageDialog( LoginDialog.this, 
                            "Username not supplied.", "Login Failed", JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                    
                    if ( getRealm() == null || getRealm().length() == 0 )
                    {
                        JOptionPane.showMessageDialog( LoginDialog.this, 
                            "Realm not supplied.", "Login Failed", JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                    
                    modifier.setCredentials( new String( getPassword() ) );
                    modifier.setRealm( getRealm() );
                    modifier.setPrincipal( getUserName() );
                    modifier.setPasscode( new String( passcodeField.getPassword() ) );
                    LoginDialog.this.setVisible( false );
                    LoginDialog.this.dispose();
                    canceled = false;
                }
            } );
        }
        return loginButton;
    }


    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton()
    {
        if ( cancelButton == null )
        {
            cancelButton = new JButton();
            cancelButton.setText( "Cancel" );
            cancelButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    LoginDialog.this.setVisible( false );
                    LoginDialog.this.dispose();
                    canceled = true;
                }
            } );
        }
        return cancelButton;
    }

    
    // -----------------------------------------------------------------------
    // Value accessors from fields in UI used by login processor
    // -----------------------------------------------------------------------
    
    
    public String getUserName()
    {
        return usernameField.getText();
    }
    
    
    public char[] getPassword()
    {
        return passwordField.getPassword();
    }
    
    
    public char[] getPasscode()
    {
        return passcodeField.getPassword();
    }
    
    
    public String getRealm()
    {
        return ( String ) realmComboBox.getSelectedItem();
    }
    
    
    boolean canceled = true;
    public boolean isCanceled()
    {
        return canceled;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
