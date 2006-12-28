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
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


public class SecretDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel jPanel1 = null;
    private JPanel jPanel2 = null;
    private JButton jButton = null;
    private JLabel jLabel = null;
    private JPasswordField jPasswordField = null;
    
    
    /**
     * This is the default constructor
     */
    public SecretDialog( Frame parent )
    {
        super( parent );
        setModal( true );
        initialize();
    }

    
    public void setMessage( String message )
    {
        jLabel.setText( UiUtils.wrap( message, 79 ) );
    }

    
    public String getPassword()
    {
        return new String( jPasswordField.getPassword() );
    }
    
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(622, 169);
        this.setContentPane( getJContentPane() );
        this.addWindowListener( new java.awt.event.WindowAdapter()
        {
            public void windowClosing( java.awt.event.WindowEvent e )
            {
                SecretDialog.this.setVisible( false );
                SecretDialog.this.dispose();
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
            jContentPane.setLayout( new BorderLayout() );
            jContentPane.add(getJPanel1(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getJPanel2(), java.awt.BorderLayout.SOUTH);
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
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.insets = new java.awt.Insets(10,15,10,15);
            gridBagConstraints.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("JLabel");
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(jLabel, gridBagConstraints);
            jPanel1.add(getJPasswordField(), gridBagConstraints1);
        }
        return jPanel1;
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
            jPanel2.add(getJButton(), null);
        }
        return jPanel2;
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
            jButton.setText("Ok");
            jButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                     SecretDialog.this.setVisible( false );
                     SecretDialog.this.dispose();
                }
            } );
        }
        return jButton;
    }


    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getJPasswordField()
    {
        if ( jPasswordField == null )
        {
            jPasswordField = new JPasswordField();
            jPasswordField.setColumns(18);
        }
        return jPasswordField;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
