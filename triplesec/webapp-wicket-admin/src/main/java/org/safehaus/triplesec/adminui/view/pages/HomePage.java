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
package org.safehaus.triplesec.adminui.view.pages;

import wicket.markup.html.basic.Label;
import org.safehaus.triplesec.adminui.security.AuthenticatedPage;

/**
 * The default home page of the Safehaus Demo web application.
 */
public class HomePage extends BasePage implements AuthenticatedPage
{
    private static final long serialVersionUID = 944257846240276627L;

    public HomePage()
    {
        add( new Label( "message", "Hello, World!" ) );
    }
}
