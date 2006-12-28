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
import wicket.model.Model;
import wicket.markup.html.form.RequiredTextField;

public class WizardPanelRealm extends WizardPanel
{
    private static final long serialVersionUID = 370256949857642718L;

    public WizardPanelRealm(String id, IModel model)
    {
        super( id, model, "Realm Configuration" );

        getForm().add( new RequiredTextField( "primaryRealmName" )
                .setLabel( new Model( "Primary Realm Name" ) ) );
        
        // these fields can be made to appear when user selects advanced view ??
        
//        getForm().add( new RequiredTextField( "clockSkew" )
//                .setLabel( new Model( "Clock Skew" ) ) );
//        getForm().add( new RequiredTextField( "ticketLifetime" )
//                .setLabel( new Model( "Ticket Lifetime" ) ) );
//        getForm().add( new RequiredTextField( "renewableLifetime" )
//                .setLabel( new Model( "Renewable Lifetime" ) ) );
    }
}
