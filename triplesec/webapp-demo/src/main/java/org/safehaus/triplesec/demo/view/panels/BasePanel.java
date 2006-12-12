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
package org.safehaus.triplesec.demo.view.panels;

import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.model.Model;
import org.safehaus.triplesec.demo.view.borders.PanelBorder;

public class BasePanel extends Panel
{
    private static final long serialVersionUID = 2996916350325053429L;

    public BasePanel( String id, IModel model, String title ) {
        super( id, model );

        // create border and add it to the page
        PanelBorder border = new PanelBorder( "border", new Model( title ) );
        border.setTransparentResolver( true );
        super.add( border );
    }
}
