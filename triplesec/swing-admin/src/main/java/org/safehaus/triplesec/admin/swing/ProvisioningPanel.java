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


import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.safehaus.triplesec.admin.HauskeysUser;
import org.safehaus.triplesec.admin.HauskeysUserModifier;
import org.safehaus.triplesec.configuration.MutableTriplesecStartupConfiguration;

import com.sun.mail.smtp.SMTPTransport;

import javax.swing.JButton;


public class ProvisioningPanel extends JPanel implements StatusObject, KeyListener, FocusListener
{
    private static final long serialVersionUID = 1L;

    private JTextField emailTextField = null;
    private JTextField mobileTextField = null;
    private JComboBox mobileCarrierComboBox = null;
    private JPasswordField tokenPinPasswordField = null;
    private JTextField midletNameTextField = null;
    private JPanel jPanel5 = null;
    private JRadioButton emailRadioButton = null;
    private JRadioButton smsRadioButton = null;
    private ButtonGroup notifyByButtonGroup;
    private HauskeysUser user;
    private StatusListener listener;
    private boolean lastStatusState = true;  // true = up to date, false means it was not up to date
    private boolean newEntityMode = false;
    private JButton deployButton = null;
    private AdminFrame adminFrame = null;
    
    
    /**
     * This is the default constructor
     */
    public ProvisioningPanel()
    {
        super();
        initialize();
    }
    
    
    public void setAdminFrame( AdminFrame adminFrame )
    {
        this.adminFrame = adminFrame;
    }
    
    
    public void setNewEntityMode( boolean newEnityMode )
    {
        this.newEntityMode = newEnityMode;
    }
    
    
    public void setStatusListener( StatusListener listener )
    {
        this.listener = listener;
    }
    
    
    public void setFields( HauskeysUser hauskeysUser )
    {
        user = hauskeysUser;
        this.lastStatusState = true;
        emailTextField.setText( hauskeysUser.getEmail() );
        mobileTextField.setText( hauskeysUser.getMobile() );
        tokenPinPasswordField.setText( hauskeysUser.getTokenPin() );
        midletNameTextField.setText( hauskeysUser.getMidletName() );
        if ( hauskeysUser.getNotifyBy() == null || hauskeysUser.getNotifyBy().equals( "null" ) )
        {
            notifyByButtonGroup.setSelected( null, false );
            notifyByButtonGroup.remove( smsRadioButton );
            notifyByButtonGroup.remove( emailRadioButton );
            smsRadioButton.setSelected( false );
            emailRadioButton.setSelected( false );
            notifyByButtonGroup.add( smsRadioButton );
            notifyByButtonGroup.add( emailRadioButton );
        }
        else if ( hauskeysUser.getNotifyBy().equalsIgnoreCase( "sms" ) )
        {
            notifyByButtonGroup.setSelected( smsRadioButton.getModel(), true );
        }
        else if ( hauskeysUser.getNotifyBy().equalsIgnoreCase( "email" ) )
        {
            notifyByButtonGroup.setSelected( emailRadioButton.getModel(), true );
        }
        else
        {
            throw new IllegalStateException( "Unknown value for notifyBy property in user: " 
                + hauskeysUser.getNotifyBy() );
        }

        if ( hauskeysUser.getMobileCarrier() != null && ! hauskeysUser.getMobileCarrier().equals( "null" ) )
        {
            Carrier carrier = Carrier.getCarrier( hauskeysUser.getMobileCarrier() );
            mobileCarrierComboBox.setSelectedItem( carrier );
        }
        else
        {
            mobileCarrierComboBox.setSelectedIndex( -1 );
        }
    }
    
    
    public void alterModifier( HauskeysUserModifier modifier )
    {
        modifier.setEmail( emailTextField.getText() );
        modifier.setMobile( mobileTextField.getText() );
        modifier.setMobileCarrier( String.valueOf( mobileCarrierComboBox.getSelectedItem() ) );
        modifier.setTokenPin( new String( tokenPinPasswordField.getPassword() ) );
        modifier.setMidletName( midletNameTextField.getText() );
        if ( emailRadioButton.isSelected() )
        {
            modifier.setNotifyBy( "email" );
        }
        else
        {
            modifier.setNotifyBy( "sms" );
        }
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        this.setSize(525, 242);
        GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
        gridBagConstraints44.gridx = 1;
        gridBagConstraints44.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints44.insets = new java.awt.Insets(0,0,5,5);
        gridBagConstraints44.gridy = 5;
        GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
        gridBagConstraints43.gridx = 0;
        gridBagConstraints43.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints43.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints43.gridy = 5;
        JLabel jLabel20 = new JLabel();
        jLabel20.setText( "Provisioning Mechanism:" );
        jLabel20.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
        gridBagConstraints42.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints42.gridy = 4;
        gridBagConstraints42.weightx = 1.0;
        gridBagConstraints42.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints42.gridx = 1;
        GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
        gridBagConstraints41.gridx = 0;
        gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints41.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints41.gridy = 4;
        JLabel jLabel19 = new JLabel();
        jLabel19.setText( "Midlet Name:" );
        jLabel19.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
        gridBagConstraints40.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints40.gridy = 3;
        gridBagConstraints40.weightx = 1.0;
        gridBagConstraints40.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints40.gridx = 1;
        GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
        gridBagConstraints39.gridx = 0;
        gridBagConstraints39.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints39.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints39.gridy = 3;
        JLabel jLabel18 = new JLabel();
        jLabel18.setText( "Hauskeys Pin:" );
        jLabel18.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
        gridBagConstraints38.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints38.gridy = 2;
        gridBagConstraints38.weightx = 0.5D;
        gridBagConstraints38.insets = new java.awt.Insets( 0, 0, 5, 200 );
        gridBagConstraints38.gridx = 1;
        GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
        gridBagConstraints37.gridx = 0;
        gridBagConstraints37.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints37.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints37.gridy = 2;
        JLabel jLabel17 = new JLabel();
        jLabel17.setText( "Mobile Carrier:" );
        jLabel17.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
        gridBagConstraints36.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints36.gridy = 1;
        gridBagConstraints36.weightx = 1.0;
        gridBagConstraints36.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints36.gridx = 1;
        GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
        gridBagConstraints35.gridx = 0;
        gridBagConstraints35.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints35.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints35.gridy = 1;
        JLabel jLabel16 = new JLabel();
        jLabel16.setText( "Mobile:" );
        jLabel16.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
        gridBagConstraints34.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints34.gridy = 0;
        gridBagConstraints34.weightx = 1.0;
        gridBagConstraints34.insets = new java.awt.Insets( 0, 0, 5, 5 );
        gridBagConstraints34.gridx = 1;
        GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
        gridBagConstraints33.gridx = 0;
        gridBagConstraints33.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints33.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints33.gridy = 0;
        JLabel jLabel15 = new JLabel();
        jLabel15.setText( "Email:" );
        jLabel15.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        setLayout( new GridBagLayout() );
        add( jLabel15, gridBagConstraints33 );
        add( getEmailTextField(), gridBagConstraints34 );
        add( jLabel16, gridBagConstraints35 );
        add( getMobileTextField(), gridBagConstraints36 );
        add( jLabel17, gridBagConstraints37 );
        add( getMobileCarrierComboBox(), gridBagConstraints38 );
        add( jLabel18, gridBagConstraints39 );
        add( getTokenPinPasswordField(), gridBagConstraints40 );
        add( jLabel19, gridBagConstraints41 );
        add( getMidletNameTextField(), gridBagConstraints42 );
        add( jLabel20, gridBagConstraints43 );
        this.add(getJPanel5(), gridBagConstraints44);
        this.add(getDeployButton(), gridBagConstraints);
    }

    
    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getEmailTextField()
    {
        if ( emailTextField == null )
        {
            emailTextField = new JTextField();
            emailTextField.addFocusListener( this );
            emailTextField.addKeyListener( this );
        }
        return emailTextField;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getMobileTextField()
    {
        if ( mobileTextField == null )
        {
            mobileTextField = new JTextField();
            mobileTextField.addFocusListener( this );
            mobileTextField.addKeyListener( this );
        }
        return mobileTextField;
    }


    /**
     * This method initializes jComboBox    
     *  
     * @return javax.swing.JComboBox    
     */
    private JComboBox getMobileCarrierComboBox()
    {
        if ( mobileCarrierComboBox == null )
        {
            mobileCarrierComboBox = new JComboBox( Carrier.CARRIERS );
            mobileCarrierComboBox.setPreferredSize( new java.awt.Dimension( 32, 19 ) );
            mobileCarrierComboBox.addActionListener( new ActionListener () {
                public void actionPerformed( ActionEvent e )
                {
                    checkStatus();
                }});
        }
        return mobileCarrierComboBox;
    }


    /**
     * This method initializes jPasswordField   
     *  
     * @return javax.swing.JPasswordField   
     */
    private JPasswordField getTokenPinPasswordField()
    {
        if ( tokenPinPasswordField == null )
        {
            tokenPinPasswordField = new JPasswordField();
            tokenPinPasswordField.addFocusListener( this );
            tokenPinPasswordField.addKeyListener( this );
        }
        return tokenPinPasswordField;
    }


    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getMidletNameTextField()
    {
        if ( midletNameTextField == null )
        {
            midletNameTextField = new JTextField();
            midletNameTextField.addFocusListener( this );
            midletNameTextField.addKeyListener( this );
        }
        return midletNameTextField;
    }


    /**
     * This method initializes jPanel5  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getJPanel5()
    {
        if ( jPanel5 == null )
        {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setAlignment( java.awt.FlowLayout.LEFT );
            flowLayout1.setHgap(10);
            jPanel5 = new JPanel();
            jPanel5.setLayout( flowLayout1 );
            jPanel5.add( getEmailRadioButton(), null );
            jPanel5.add( getSmsRadioButton(), null );
            notifyByButtonGroup = new ButtonGroup();
            notifyByButtonGroup.add( getEmailRadioButton() );
            notifyByButtonGroup.add( getSmsRadioButton() );
        }
        return jPanel5;
    }


    /**
     * This method initializes jRadioButton 
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getEmailRadioButton()
    {
        if ( emailRadioButton == null )
        {
            emailRadioButton = new JRadioButton();
            emailRadioButton.setText( "Email" );
            emailRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e )
                {
                    checkStatus();
                }} );
        }
        return emailRadioButton;
    }


    /**
     * This method initializes jRadioButton1    
     *  
     * @return javax.swing.JRadioButton 
     */
    private JRadioButton getSmsRadioButton()
    {
        if ( smsRadioButton == null )
        {
            smsRadioButton = new JRadioButton();
            smsRadioButton.setText( "SMS WAP Push" );
            smsRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e )
                {
                    checkStatus();
                }} );
        }
        return smsRadioButton;
    }


    public String getEmail()
    {
        return emailTextField.getText();
    }
    
    
    public String getMobile()
    {
        return mobileTextField.getText();
    }
    
    
    public Carrier getMobileCarrier()
    {
        return ( Carrier ) mobileCarrierComboBox.getSelectedItem();
    }
    
    
    public String getTokenPin()
    {
        return new String( tokenPinPasswordField.getPassword() );
    }
    
    
    public String getMidletName()
    {
        return midletNameTextField.getText();
    }
    
    
    public String getNotifyBy()
    {
        if ( emailRadioButton.isSelected() )
        {
            return "email";
        }
        return "sms";
    }

    
    private void checkStatus()
    {
        boolean upToDate = isUpToDate();
        if ( listener != null && lastStatusState != upToDate )
        {
            listener.statusChanged( ProvisioningPanel.this );
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


    private boolean isNotifyByUpToDate( String value )
    {
        if ( value == null && !emailRadioButton.isSelected() && !smsRadioButton.isSelected() )
        {
            return true;
        }
        
        if ( value == null )
        {
            return false;
        }
        
        if ( emailRadioButton.isSelected() && value.equals( "email" ) )
        {
            return true;
        }
        
        if ( this.smsRadioButton.isSelected() && value.equals( "sms" ) )
        {
            return true;
        }
        
        return false;
    }
    
    
    private boolean isCarrierUpToDate( String carrier )
    {
        if ( carrier == null && mobileCarrierComboBox.getSelectedIndex() == -1 )
        {
            return true;
        }

        if ( carrier == null )
        {
            return false;
        }

        if ( carrier.equals( "null" ) )
        {
            return true;
        }   
        
        return carrier.equals( mobileCarrierComboBox.getSelectedItem().toString() );
    }

    
    public boolean isUpToDate()
    {
        if ( newEntityMode )
        {
            return UiUtils.isFieldUpToDate( this.emailTextField, null ) &&
            UiUtils.isFieldUpToDate( this.midletNameTextField, null ) &&
            UiUtils.isFieldUpToDate( this.mobileTextField, null ) &&
            UiUtils.isFieldUpToDate( this.tokenPinPasswordField, null ) &&
            isNotifyByUpToDate( null ) && isCarrierUpToDate( null );
        }
        return UiUtils.isFieldUpToDate( this.emailTextField, user.getEmail() ) &&
            UiUtils.isFieldUpToDate( this.midletNameTextField, user.getMidletName() ) &&
            UiUtils.isFieldUpToDate( this.mobileTextField, user.getMobile() ) &&
            UiUtils.isFieldUpToDate( this.tokenPinPasswordField, user.getTokenPin() ) &&
            isNotifyByUpToDate( user.getNotifyBy() ) && isCarrierUpToDate( user.getMobileCarrier() );
    }


    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDeployButton()
    {
        if ( deployButton == null )
        {
            deployButton = new JButton();
            deployButton.setText("Deploy");
            deployButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    if ( emailRadioButton.isSelected() )
                    {
                        if ( emailTextField.getText() == null || emailTextField.getText().length() == 0 )
                        {
                            JOptionPane.showMessageDialog( ProvisioningPanel.this, 
                                "Cannot deploy via email without a valid email address." );
                            return;
                        }
                        
                        try
                        {
                            deployByEmail();
                        }
                        catch ( Exception e1 )
                        {
                            String msg = UiUtils.wrap( "Failed to provision application: " + e1.getMessage(), 79 );
                            JOptionPane.showMessageDialog( ProvisioningPanel.this, 
                                msg, "Failed to deploy!", JOptionPane.ERROR_MESSAGE );
                            return;
                        }
                    }
                    else
                    {
                        if ( mobileTextField.getText() == null || mobileTextField.getText().length() == 0 )
                        {
                            JOptionPane.showMessageDialog( ProvisioningPanel.this, 
                                "Cannot deploy via email without a valid email address." );
                            return;
                        }
                        
                        try
                        {
                            deployBySms();
                        }
                        catch ( Exception e1 )
                        {
                            String msg = UiUtils.wrap( "Failed to provision application: " + e1.getMessage(), 79 );
                            JOptionPane.showMessageDialog( ProvisioningPanel.this, 
                                msg, "Failed to deploy!", JOptionPane.ERROR_MESSAGE );
                            return;
                        }
                    }
                }

            } );
        }
        return deployButton;
    }

    
    private void deployBySms() throws HttpException, IOException
    {
        String smsTransportUrl = null;
        String activationUrl = null;
        String mobile = mobileTextField.getText();
        String activationKey = user.getActivationKey();
        String smsUsername = null; 
        String smsAccountName = null;
        String smsPassword = null;
        String carrier = this.getMobileCarrierComboBox().getSelectedItem().toString();
        
        if ( adminFrame.getSettings() == null && !adminFrame.getConfigurationFileManager().isConfigurationLoaded() )
        {
            if ( ! adminFrame.doLoadSettings() )
            {
                return;
            }
            
            smsTransportUrl = adminFrame.getSettings().getDefaultSmsConfig().getSmsTransportUrl();
            smsPassword = adminFrame.getSettings().getDefaultSmsConfig().getSmsPassword();
            smsUsername = adminFrame.getSettings().getDefaultSmsConfig().getSmsUsername();
            smsAccountName = adminFrame.getSettings().getDefaultSmsConfig().getSmsAccountName();
            activationUrl = adminFrame.getSettings().getPresentationBaseUrl() + "/activation";
        }
        else if ( adminFrame.getConfigurationFileManager().isConfigurationLoaded() )
        {
            MutableTriplesecStartupConfiguration config = adminFrame.getConfigurationFileManager().getConfiguration();
            smsTransportUrl = config.getSmsConfiguration().getSmsTransportUrl();
            smsUsername = config.getSmsConfiguration().getSmsUsername();
            smsAccountName = config.getSmsConfiguration().getSmsAccountName();
            smsPassword = config.getSmsConfiguration().getSmsPassword();
            activationUrl = config.getPresentationBaseUrl() + "/activation";
        }
        
        HttpClient client = new HttpClient();
        HttpMethod method = new PostMethod( smsTransportUrl );
        int carrierCode = 31004;
        if ( carrier.equalsIgnoreCase( "AT&T" ) )
        {
            carrierCode = 31001;
        }
        else if ( carrier.equalsIgnoreCase( "Cingular" ) )
        {
            carrierCode = 31002;
        }
        else if ( carrier.equalsIgnoreCase( "Verizon" ) )
        {
            carrierCode = 31003;
        }
        else if ( carrier.equalsIgnoreCase( "T-Mobile" ) )
        {
            carrierCode = 31004;
        }
        else if ( carrier.equalsIgnoreCase( "Sprint" ) )
        {
            carrierCode = 31005;
        }
        else if ( carrier.equalsIgnoreCase( "Nextel" ) )
        {
            carrierCode = 31007;
        }

        if ( mobile.startsWith( "+" ) )
        {
            mobile = mobile.substring( 1 );
        }
        
        if ( mobile.startsWith( "1" ) )
        {
            mobile = mobile.substring( 1 );
            if ( mobile.length() < 10 )
            {
                throw new RuntimeException( "mobile phone number was not 10 digits in length" );
            }
        }
        NameValuePair[] params = new NameValuePair[] {
                new NameValuePair( "Carrier", String.valueOf( carrierCode ) ),
                new NameValuePair( "UID", smsUsername ),
                new NameValuePair( "PWD", smsPassword ),
                new NameValuePair( "Campaign", smsAccountName ),
                new NameValuePair( "CellNumber", mobile ),
                new NameValuePair( "msg", activationUrl + "/" + activationKey + "/HausKeys.jar")
        };
        method.setQueryString( params );
        client.executeMethod( method );
    }
    

    private void deployByEmail() throws AddressException, MessagingException
    {
        String email = emailTextField.getText();
        String activationKey = user.getActivationKey();
        String activationUrl = null;
        String smtpHost = null;
        String smtpFrom = null;
        boolean doAuthentication = false;
        String smtpUsername = null;
        String smtpPassword = null;
        
        
        if ( adminFrame.getSettings() == null && !adminFrame.getConfigurationFileManager().isConfigurationLoaded() )
        {
            if ( ! adminFrame.doLoadSettings() )
            {
                return;
            }
            
            smtpHost = adminFrame.getSettings().getDefaultSmtpConfig().getSmtpHost();
            smtpFrom = adminFrame.getSettings().getDefaultSmtpConfig().getSmtpFrom();
            doAuthentication = adminFrame.getSettings().getDefaultSmtpConfig().isSmtpAuthenticate();
            smtpUsername = adminFrame.getSettings().getDefaultSmtpConfig().getSmtpUsername();
            smtpPassword = adminFrame.getSettings().getDefaultSmtpConfig().getSmtpPassword();
            activationUrl = adminFrame.getSettings().getPresentationBaseUrl() + "/activation";
        }
        else if ( adminFrame.getConfigurationFileManager().isConfigurationLoaded() )
        {
            MutableTriplesecStartupConfiguration config = adminFrame.getConfigurationFileManager().getConfiguration();
            activationUrl = config.getPresentationBaseUrl() + "/activation";
            smtpHost = config.getSmtpConfiguration().getSmtpHost();
            smtpFrom = config.getSmtpConfiguration().getSmtpFrom();
            smtpUsername = config.getSmtpConfiguration().getSmtpUsername();
            smtpPassword = config.getSmtpConfiguration().getSmtpPassword();
            doAuthentication = config.getSmtpConfiguration().isSmtpAuthenticate();
        }

        Properties props = new Properties();
        props.setProperty( "mail.smtp.host", smtpHost );
        Session session = Session.getInstance( props, null );

        MimeMessage msg = new MimeMessage( session );
        if ( smtpFrom != null )
        {
            msg.setFrom( new InternetAddress( smtpFrom ) );
        }
        else
        {
            msg.setFrom();
        }

        msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( email, false ) );
        msg.setSubject( "Triplesec account ready for download." );
        msg.setText( "Download your custom built hauskeys application here:\n" 
            + activationUrl + "/" + activationKey + "/HausKeys.jar" );
        msg.setHeader( "X-Mailer", "triplesec-registration" );
        msg.setSentDate( new Date() );
        SMTPTransport transport = ( SMTPTransport ) session.getTransport( "smtp" );
        if ( doAuthentication )
        {
            transport.connect( smtpHost, smtpUsername, smtpPassword );
        }
        else
        {
            transport.connect();
        }
        transport.sendMessage( msg, msg.getAllRecipients() );
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
