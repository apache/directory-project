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
package org.safehaus.triplesec.configui.view.panels;


import wicket.model.IModel;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.RequiredTextField;
import wicket.markup.html.form.PasswordTextField;

import java.util.Arrays;
import java.util.List;


public class WizardPanelSms extends WizardPanel
{
    private static final long serialVersionUID = 8637953029881838294L;
    private static final List PROVIDERS = Arrays.asList( new String[]
        { "NMSI HTTP", "Clickatell HTTP" } );


    public WizardPanelSms(String id, IModel model)
    {
        super( id, model, "SMS Gateway Account Setup" );

        getForm().add( new DropDownChoice( "smsProvider", PROVIDERS ) );
        getForm().add( new RequiredTextField( "smsUsername") );
        getForm().add( new PasswordTextField( "smsPassword" ) );
        getForm().add( new RequiredTextField( "smsAccountName" ) );
        getForm().add( new RequiredTextField( "smsTransportUrl" ) );
    }
}
