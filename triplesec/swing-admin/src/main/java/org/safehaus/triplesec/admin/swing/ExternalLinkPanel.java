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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.safehaus.triplesec.admin.ExternalUser;
import org.safehaus.triplesec.admin.ExternalUserModifier;


public class ExternalLinkPanel extends JPanel implements StatusObject, KeyListener, FocusListener
{
    private static final long serialVersionUID = -1447912774145082027L;
    private JPanel jPanel1 = null;
    private JPanel jPanel2 = null;
    private JButton jButton = null;
    private JLabel jLabel7 = null;
    private JTextField linkUrlTextField = null;
    private ExternalUser user = null;
    private StatusListener listener;
    private boolean lastStatusState = true;  // true = up to date, false means it was not up to date
    private boolean newEntityMode = false;


    /**
     * This is the default constructor
     */
    public ExternalLinkPanel()
    {
        super();
        initialize();
    }

    
    public void setNewEntityMode ( boolean newEntityMode )
    {
        this.newEntityMode = newEntityMode;
    }
    

    public void setFields( ExternalUser externalUser )
    {
        user = externalUser;
        lastStatusState = true;
        linkUrlTextField.setText( externalUser.getReferral() );
    }
    
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(553, 225);
        setLayout( new BorderLayout() );
        add( getJPanel1(), java.awt.BorderLayout.CENTER );
        add( getJPanel2(), java.awt.BorderLayout.SOUTH );
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
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 0;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.insets = new java.awt.Insets( 0, 5, 0, 10 );
            gridBagConstraints17.gridx = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.insets = new java.awt.Insets( 0, 10, 0, 0 );
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridy = 0;
            jLabel7 = new JLabel();
            jLabel7.setText( "Link URL:" );
            jLabel7.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
            jPanel1 = new JPanel();
            jPanel1.setLayout( new GridBagLayout() );
            jPanel1.add( jLabel7, gridBagConstraints16 );
            jPanel1.add( getLinkUrlTextField(), gridBagConstraints17 );
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
            jPanel2.add( getJButton(), null );
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
            jButton.setText( "Test" );
        }
        return jButton;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getLinkUrlTextField()
    {
        if ( linkUrlTextField == null )
        {
            linkUrlTextField = new JTextField();
            linkUrlTextField.addFocusListener( this );
            linkUrlTextField.addKeyListener( this );
        }
        return linkUrlTextField;
    }


    public void alterModifier( ExternalUserModifier modifier )
    {
        modifier.setReferral( linkUrlTextField.getText() );
    }


    public String getReferral()
    {
        return linkUrlTextField.getText();
    }


    public boolean isUpToDate()
    {
        if ( newEntityMode )
        {
            return UiUtils.isFieldUpToDate( linkUrlTextField, null );
        }
        return UiUtils.isFieldUpToDate( linkUrlTextField, user.getReferral() );
    }


    private void checkStatus()
    {
        boolean upToDate = isUpToDate();
        if ( listener != null && lastStatusState != upToDate )
        {
            listener.statusChanged( ExternalLinkPanel.this );
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


    public void setStatusListener( StatusListener listener )
    {
        this.listener = listener;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
