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


import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.form.RequiredTextField;
import wicket.markup.html.form.validation.EmailAddressPatternValidator;
import wicket.model.IModel;
import wicket.model.Model;


public class WizardPanelUser extends WizardPanel
{
    private static final long serialVersionUID = 370256949857642718L;

    public WizardPanelUser(String id, IModel model)
    {
        super( id, model, "Required User Information" );

        getForm().add( new RequiredTextField( "username" ) );
        getForm().add( new PasswordTextField( "password" ) );
        getForm().add( new  PasswordTextField( "passwordConfirm" ) );
        getForm().add( new RequiredTextField( "firstName" ) );
        getForm().add( new RequiredTextField( "lastName" ) );
        
        RequiredTextField email = new RequiredTextField( "email" );
        email.add( EmailAddressPatternValidator.getInstance() );
        email.setLabel( new Model( "Email" ) );
        getForm().add( email );
        
    }
}
