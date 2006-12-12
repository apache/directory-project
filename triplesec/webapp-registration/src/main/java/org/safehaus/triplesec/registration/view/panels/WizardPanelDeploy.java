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
package org.safehaus.triplesec.registration.view.panels;


import java.util.Arrays;
import java.util.List;

import org.safehaus.sms.Carrier;

import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.form.RequiredTextField;
import wicket.model.IModel;


public class WizardPanelDeploy extends WizardPanel
{
    private static final long serialVersionUID = 370256949857642718L;
    private static final List MECHANISMS = Arrays.asList( new String[] { "sms", "email" } );

    public WizardPanelDeploy( String id, IModel model )
    {
        super( id, model, "Deployment Settings" );

        getForm().add( new RequiredTextField( "midletName" ) );
        getForm().add( new RequiredTextField( "mobile" ) );
        getForm().add( new PasswordTextField( "tokenPin" ) );
        getForm().add( new PasswordTextField( "tokenPinConfirm" ) );
        getForm().add( new DropDownChoice( "mobileCarrier", Carrier.ALL_CARRIER_STRINGS ) );
        getForm().add( new DropDownChoice( "deploymentMechanism", MECHANISMS ) );
    }
}
