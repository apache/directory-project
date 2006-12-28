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
package org.safehaus.triplesec.adminui.view.borders;

import wicket.markup.html.border.Border;
import wicket.markup.html.basic.Label;
import wicket.model.IModel;

/**
 * Renders a consistent border layout for a panel when included.
 */
public class PanelBorder extends Border
{
    private static final long serialVersionUID = 2132244418269079978L;

    public PanelBorder( String id, IModel titleModel )
    {
        super( id );

        // add the panel title to the panel border...
        add( new Label( "title", titleModel ) );
    }
}
