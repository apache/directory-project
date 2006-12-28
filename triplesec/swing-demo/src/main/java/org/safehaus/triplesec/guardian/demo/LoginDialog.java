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
package org.safehaus.triplesec.guardian.demo;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;


public class LoginDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel jPanel1 = null;
    private JButton jButton = null;
    private JButton jButton1 = null;
    private JPanel jPanel = null;
    private JPanel jPanel2 = null;
    private String[] profiles = null;
    private boolean loginSelected = false;
    private JLabel jLabel = null;
    private JComboBox profilesComboBox = null;
    private JLabel jLabel1 = null;
    private JPasswordField passwordField = null;
    private JLabel jLabel2 = null;
    private JPasswordField passcodeField = null;
    
    
    public String[] getProfileIds()
    {
        return profiles;
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
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.CENTER);
            jPanel1 = new JPanel();
            jPanel1.setLayout(flowLayout);
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
            jButton.setText("Login");
            jButton.setFocusable( true );
            jButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    System.out.println( "actionPerformed()" ); 
                    if ( passwordField.getPassword() != null && passwordField.getPassword().length > 0 )
                    {
                        loginSelected = true;
                        LoginDialog.this.setVisible( false );
                    }
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
            jButton1.setText("Cancel");
            jButton1.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    setLoginSelected( false );
                    passwordField.setText( null );
                    passcodeField.setText( null );
                    LoginDialog.this.setVisible( false );
                }
            } );
        }
        return jButton1;
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
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.insets = new java.awt.Insets(0,0,0,5);
            gridBagConstraints31.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Passcode:");
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("Password:");
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new java.awt.Insets(0,0,5,5);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new java.awt.Insets(0,5,5,5);
            gridBagConstraints1.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Login Profile:");
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.setPreferredSize(new java.awt.Dimension(40,25));
            jPanel.add(jLabel, gridBagConstraints1);
            jPanel.add(getProfilesComboBox(), gridBagConstraints2);
            jPanel.add(jLabel1, gridBagConstraints);
            jPanel.add(getPasswordField(), gridBagConstraints3);
            jPanel.add(jLabel2, gridBagConstraints31);
            jPanel.add(getPasscodeField(), gridBagConstraints4);
        }
        return jPanel;
    }


    /**
     * This method initializes jPanel2	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel2()
    {
        if ( jPanel2 == null )
        {
            jPanel2 = new JPanel();
            jPanel2.setLayout(new BoxLayout(getJPanel2(), BoxLayout.Y_AXIS));
        }
        return jPanel2;
    }


    /**
     * This is the default constructor
     */
    public LoginDialog()
    {
        super();
        initialize();
        setModal( true );
    }


    /**
     * This is the default constructor
     */
    public LoginDialog( String[] profiles )
    {
        super();
        this.profiles = profiles;
        initialize();
        setModal( true );
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(292, 188);
        this.setTitle("Login");
        this.setContentPane( getJContentPane() );
        this.addWindowListener( new java.awt.event.WindowAdapter()
        {
            public void windowClosing( java.awt.event.WindowEvent e )
            {
                System.out.println( "windowClosing()" ); 
                LoginDialog.this.setVisible( false );
                LoginDialog.this.dispose();
            }
        } );
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
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
            jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getJPanel2(), java.awt.BorderLayout.EAST);
        }
        return jContentPane;
    }


    public void setLoginSelected( boolean loginSelected )
    {
        this.loginSelected = loginSelected;
    }


    public boolean isLoginSelected()
    {
        return loginSelected;
    }


    public String getPassword()
    {
        return new String( passwordField.getPassword() );
    }


    public String getPasscode()
    {
        return new String( passcodeField.getPassword() );
    }


    public String getSelectedProfile()
    {
        return ( String ) profilesComboBox.getSelectedItem();
    }


    /**
     * This method initializes jComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getProfilesComboBox()
    {
        if ( profilesComboBox == null )
        {
            if ( profiles != null )
            {
                profilesComboBox = new JComboBox( profiles );
            }
            else
            {
                profilesComboBox = new JComboBox();
            }
            profilesComboBox.setPreferredSize(new java.awt.Dimension(32,19));
        }
        return profilesComboBox;
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
        }
        return passwordField;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getPasscodeField()
    {
        if ( passcodeField == null )
        {
            passcodeField = new JPasswordField();
        }
        return passcodeField;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
