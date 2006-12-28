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

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JCheckBox;
import javax.swing.JButton;


public class ConnectionDialog extends JDialog
{
    private static final long serialVersionUID = -4476950680973044809L;

    private JPanel mainPanel = null;
    private JPanel textAreaPanel = null;
    private JTextArea messageTextArea = null;
    private JPanel ldapPortPanel = null;
    private JLabel ldapPortLabel = null;
    private JTextField ldapPortTextField = null;
    private JPanel hostPanel = null;
    private JLabel hostLabel = null;
    private JTextField hostTextField = null;
    private JPanel krb5PortPanel = null;
    private JLabel krb5PortLabel = null;
    private JTextField krb5TextField = null;
    private JPanel useLdapsPanel = null;
    private JLabel useLdapsLabel = null;
    private JCheckBox useLdapsCheckBox = null;
    private JPanel buttonPanel = null;
    private JButton cancelButton = null;
    private JButton connectButton = null;
    private final ConnectionInfoModifier modifier = new ConnectionInfoModifier();

    private boolean canceled = true;
    

    /**
     * This is the default constructor
     */
    public ConnectionDialog()
    {
        super();
        initialize();
    }


    /**
     * constructor for pre-setting values
     */
    public ConnectionDialog( ConnectionInfo connectionInfo )
    {
        super();
        initialize();
        
        this.ldapPortTextField.setText( String.valueOf( connectionInfo.getLdapPort() ) );
        modifier.setLdapPort( connectionInfo.getLdapPort() );
        
        this.hostTextField.setText( connectionInfo.getHost() );
        modifier.setHost( connectionInfo.getHost() );
        
        this.krb5TextField.setText( String.valueOf( connectionInfo.getKrb5Port() ) );
        modifier.setKrb5Port( connectionInfo.getKrb5Port() );
        
        this.useLdapsCheckBox.setSelected( connectionInfo.isUseLdaps() );
        modifier.setUseLdaps( connectionInfo.isUseLdaps() );
    }
    
    
    public ConnectionInfoModifier getConnectionModifier()
    {
        return modifier;
    }
    
    
    public boolean isCanceled()
    {
        return canceled;
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize( 359, 334 );
        this.setTitle( "Connection Settings" );
        this.setContentPane( getJContentPane() );
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
        if ( mainPanel == null )
        {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.insets = new java.awt.Insets( 25, 160, 0, 0 );
            gridBagConstraints5.weightx = 0.0D;
            gridBagConstraints5.gridy = 8;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.insets = new java.awt.Insets( 8, 0, 0, 12 );
            gridBagConstraints4.gridy = 7;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.insets = new java.awt.Insets( 10, 0, 0, 0 );
            gridBagConstraints3.gridy = 6;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.insets = new java.awt.Insets( 10, 31, 0, 0 );
            gridBagConstraints2.gridy = 5;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.insets = new java.awt.Insets( 20, 0, 0, 0 );
            gridBagConstraints1.gridy = 4;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridheight = 4;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout( new GridBagLayout() );
            mainPanel.add( getJPanel(), gridBagConstraints );
            mainPanel.add( getJPanel2(), gridBagConstraints1 );
            mainPanel.add( getJPanel3(), gridBagConstraints2 );
            mainPanel.add( getJPanel4(), gridBagConstraints3 );
            mainPanel.add( getJPanel5(), gridBagConstraints4 );
            mainPanel.add( getJPanel6(), gridBagConstraints5 );
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel()
    {
        if ( textAreaPanel == null )
        {
            textAreaPanel = new JPanel();
            textAreaPanel.setBorder( javax.swing.BorderFactory
                .createEtchedBorder( javax.swing.border.EtchedBorder.RAISED ) );
            textAreaPanel.add( getJTextArea(), null );
        }
        return textAreaPanel;
    }


    /**
     * This method initializes jTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getJTextArea()
    {
        if ( messageTextArea == null )
        {
            messageTextArea = new JTextArea();
            messageTextArea.setText( "You have not loaded a local server configuration \n"
                + "file so remote connection settings are required." );
            messageTextArea.setEditable( false );
        }
        return messageTextArea;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel2()
    {
        if ( ldapPortPanel == null )
        {
            ldapPortLabel = new JLabel();
            ldapPortLabel.setText( "Ldap Port: " );
            ldapPortPanel = new JPanel();
            ldapPortPanel.add( ldapPortLabel, null );
            ldapPortPanel.add( getJTextField(), null );
        }
        return ldapPortPanel;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField()
    {
        if ( ldapPortTextField == null )
        {
            ldapPortTextField = new JTextField();
            ldapPortTextField.setText( "10389" );
            ldapPortTextField.setColumns(5);
            ldapPortTextField.setPreferredSize( new java.awt.Dimension( 44, 17 ) );
            ldapPortTextField.addFocusListener( new java.awt.event.FocusAdapter()
            {
                public void focusLost( java.awt.event.FocusEvent e )
                {
                    if ( isIntFieldValid( ldapPortTextField ) )
                    {
                        ldapPortLabel.setForeground( Color.BLACK );
                    }
                    else
                    {
                        ldapPortLabel.setForeground( Color.RED );
                    }
                }
            } );
        }
        return ldapPortTextField;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel3()
    {
        if ( hostPanel == null )
        {
            hostLabel = new JLabel();
            hostLabel.setText( "Host: " );
            hostPanel = new JPanel();
            hostPanel.add( hostLabel, null );
            hostPanel.add( getJTextField2(), null );
        }
        return hostPanel;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField2()
    {
        if ( hostTextField == null )
        {
            hostTextField = new JTextField();
            hostTextField.setText( "localhost" );
            hostTextField.setColumns(10);
            hostTextField.setPreferredSize( new java.awt.Dimension( 44, 17 ) );
            hostTextField.addFocusListener( new java.awt.event.FocusAdapter()
            {
                public void focusLost( java.awt.event.FocusEvent e )
                {
                    if ( isHostValid() )
                    {
                        hostLabel.setForeground( Color.BLACK );
                    }
                    else 
                    {
                        hostLabel.setForeground( Color.RED );
                    }
                }
            } );
        }
        return hostTextField;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel4()
    {
        if ( krb5PortPanel == null )
        {
            krb5PortLabel = new JLabel();
            krb5PortLabel.setText( "Krb5 Port: " );
            krb5PortPanel = new JPanel();
            krb5PortPanel.add( krb5PortLabel, null );
            krb5PortPanel.add( getJTextField3(), null );
        }
        return krb5PortPanel;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField3()
    {
        if ( krb5TextField == null )
        {
            krb5TextField = new JTextField();
            krb5TextField.setText( "88" );
            krb5TextField.setColumns(5);
            krb5TextField.setPreferredSize( new Dimension( 44, 17 ) );
            krb5TextField.addFocusListener( new java.awt.event.FocusAdapter()
            {
                public void focusLost( java.awt.event.FocusEvent e )
                {
                    if ( isIntFieldValid( krb5TextField ) )
                    {
                        krb5PortLabel.setForeground( Color.BLACK );
                    }
                    else
                    {
                        krb5PortLabel.setForeground( Color.RED );
                    }
                }
            } );
        }
        return krb5TextField;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel5()
    {
        if ( useLdapsPanel == null )
        {
            useLdapsLabel = new JLabel();
            useLdapsLabel.setText( "Use Ldaps:    " );
            useLdapsPanel = new JPanel();
            useLdapsPanel.add( useLdapsLabel, null );
            useLdapsPanel.add( getJCheckBox(), null );
        }
        return useLdapsPanel;
    }


    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBox()
    {
        if ( useLdapsCheckBox == null )
        {
            useLdapsCheckBox = new JCheckBox();
            useLdapsCheckBox.addItemListener( new java.awt.event.ItemListener()
            {
                public void itemStateChanged( java.awt.event.ItemEvent e )
                {
                    modifier.setUseLdaps( useLdapsCheckBox.isSelected() );
                }
            } );
        }
        return useLdapsCheckBox;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel6()
    {
        if ( buttonPanel == null )
        {
            buttonPanel = new JPanel();
            buttonPanel.add( getJButton(), null );
            buttonPanel.add( getJButton1(), null );
        }
        return buttonPanel;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton()
    {
        if ( cancelButton == null )
        {
            cancelButton = new JButton();
            cancelButton.setText( "Cancel" );
            cancelButton.setPreferredSize( new java.awt.Dimension( 75, 25 ) );
            cancelButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    setVisible( false );
                    dispose();
                    canceled = true;
                }
            } );
        }
        return cancelButton;
    }


    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1()
    {
        if ( connectButton == null )
        {
            connectButton = new JButton();
            connectButton.setText( "Connect" );
            connectButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    if ( isIntFieldValid( ldapPortTextField ) && 
                         isIntFieldValid( krb5TextField ) &&
                         isHostValid() )
                    {
                        modifier.setLdapPort( Integer.parseInt( ldapPortTextField.getText() ) );
                        modifier.setKrb5Port( Integer.parseInt( krb5TextField.getText() ) );
                        modifier.setHost( hostTextField.getText() );
                        modifier.setUseLdaps( useLdapsCheckBox.isSelected() );
                        canceled = false;
                        setVisible( false );
                        dispose();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog( ConnectionDialog.this, "Empty or invalid fields remain", 
                            "Empty or Invalid Field", JOptionPane.ERROR_MESSAGE );
                    }
                }
            } );
        }
        return connectButton;
    }

    
    private boolean isIntFieldValid( JTextField field )
    {
        try
        {
            Integer.parseInt( field.getText() );
            return true;
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
    }
    
    
    private boolean isHostValid()
    {
        try
        {
            InetAddress.getByName( hostTextField.getText() );
            return true;
        }
        catch ( UnknownHostException e1 )
        {
            hostLabel.setForeground( Color.RED );
            return false;
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
