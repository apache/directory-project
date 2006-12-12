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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.safehaus.triplesec.admin.AdministeredEntity;


public class GeneralPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JTextField creatorsNameTextField = null;
    private JTextField createTimestampTextField = null;
    private JTextField modifiersNameTextField = null;
    private JTextField modifyTimestampTextField = null;
    


    /**
     * This is the default constructor
     */
    public GeneralPanel()
    {
        super();
        initialize();
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(518, 252);
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints13.gridy = 3;
        gridBagConstraints13.weightx = 1.0;
        gridBagConstraints13.insets = new java.awt.Insets( 0, 5, 0, 5 );
        gridBagConstraints13.gridx = 1;
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints12.gridy = 2;
        gridBagConstraints12.weightx = 1.0;
        gridBagConstraints12.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints12.gridx = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.insets = new java.awt.Insets( 0, 5, 0, 0 );
        gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints11.gridy = 3;
        JLabel jLabel6 = new JLabel();
        jLabel6.setText( "ModifyTimestamp:" );
        jLabel6.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        gridBagConstraints10.gridx = 0;
        gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints10.gridy = 2;
        JLabel jLabel5 = new JLabel();
        jLabel5.setText( "Modifier's Name:" );
        jLabel5.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints9.gridy = 1;
        gridBagConstraints9.weightx = 1.0;
        gridBagConstraints9.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints9.gridx = 1;
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.insets = new java.awt.Insets( 0, 5, 0, 0 );
        gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints8.gridy = 1;
        JLabel jLabel4 = new JLabel();
        jLabel4.setText( "Create Timestamp:" );
        jLabel4.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints7.gridy = 0;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.insets = new java.awt.Insets( 0, 5, 5, 5 );
        gridBagConstraints7.gridx = 1;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.insets = new java.awt.Insets( 0, 5, 0, 0 );
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.gridy = 0;
        JLabel jLabel3 = new JLabel();
        jLabel3.setText( "Creator's Name:" );
        jLabel3.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
        setLayout( new GridBagLayout() );
        setComponentOrientation( java.awt.ComponentOrientation.LEFT_TO_RIGHT );
        add( jLabel3, gridBagConstraints6 );
        add( getCreatorsNameTextField(), gridBagConstraints7 );
        add( jLabel4, gridBagConstraints8 );
        add( getCreateTimestampTextField(), gridBagConstraints9 );
        add( jLabel5, gridBagConstraints10 );
        add( jLabel6, gridBagConstraints11 );
        add( getModifiersNameTextField(), gridBagConstraints12 );
        add( getModifyTimestampTextField(), gridBagConstraints13 );
    }




    /**
     * This method initializes jTextField3  
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getCreatorsNameTextField()
    {
        if ( creatorsNameTextField == null )
        {
            creatorsNameTextField = new JTextField();
            creatorsNameTextField.setEditable( false );
        }
        return creatorsNameTextField;
    }


    /**
     * This method initializes jTextField4  
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getCreateTimestampTextField()
    {
        if ( createTimestampTextField == null )
        {
            createTimestampTextField = new JTextField();
            createTimestampTextField.setEditable( false );
        }
        return createTimestampTextField;
    }


    /**
     * This method initializes jTextField5  
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getModifiersNameTextField()
    {
        if ( modifiersNameTextField == null )
        {
            modifiersNameTextField = new JTextField();
            modifiersNameTextField.setEditable( false );
        }
        return modifiersNameTextField;
    }


    /**
     * This method initializes jTextField6  
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getModifyTimestampTextField()
    {
        if ( modifyTimestampTextField == null )
        {
            modifyTimestampTextField = new JTextField();
            modifyTimestampTextField.setEditable( false );
        }
        return modifyTimestampTextField;
    }


    public void setFields( AdministeredEntity entity )
    {
        creatorsNameTextField.setText( entity.getCreatorsName() );
        modifiersNameTextField.setText( entity.getModifiersName() );

        if ( entity.getCreateTimestamp() != null )
        {
            createTimestampTextField.setText( entity.getCreateTimestamp().toString() );
        }
        else
        {
            createTimestampTextField.setText( null );
        }
        
        if ( entity.getModifyTimestamp() != null )
        {
            modifyTimestampTextField.setText( entity.getModifyTimestamp().toString() );
        }
        else
        {
            modifyTimestampTextField.setText( null );
        }
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
