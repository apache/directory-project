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
import wicket.markup.html.form.CheckBox;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.PasswordTextField;

public class WizardPanelSecureLdap extends WizardPanel
{
    private static final long serialVersionUID = 370256949857642718L;

    public WizardPanelSecureLdap(String id, IModel model) {
        super(id, model, "Secure LDAP Configuration");

        getForm().add( new CheckBox( "enableLdaps" ) );
        getForm().add( new TextField( "ldapsPort", Integer.class ) );
        getForm().add( new TextField( "ldapCertFilePath" ) );
        getForm().add( new PasswordTextField( "ldapCertPassword" ) );
    }
}
