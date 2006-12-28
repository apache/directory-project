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


import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.form.RequiredTextField;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.validation.EmailAddressPatternValidator;
import wicket.model.IModel;
import wicket.model.Model;


public class WizardPanelSmtp extends WizardPanel
{
    private static final long serialVersionUID = -5651485130659672755L;


    public WizardPanelSmtp( String id, IModel model )
    {
        super( id, model, "Mail Server Configuration" );

        getForm().add( new CheckBox( "smtpAuthenticate" ) );
        getForm().add( new TextField( "smtpUsername" ) );
        getForm().add( new PasswordTextField( "smtpPassword" ) );
        getForm().add( new RequiredTextField( "smtpHost" ) );
        getForm().add( new RequiredTextField( "smtpSubject" ) );
        RequiredTextField from = new RequiredTextField( "smtpFrom" );
        from.add( EmailAddressPatternValidator.getInstance() );
        from.setLabel( new Model( "SMTP From" ) );
        getForm().add( from );

    }
}
