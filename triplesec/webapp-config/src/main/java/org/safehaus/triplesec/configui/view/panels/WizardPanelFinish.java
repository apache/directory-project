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


import java.io.File;

import wicket.model.IModel;
import wicket.model.Model;
import wicket.markup.html.form.Button;
import wicket.markup.html.basic.Label;
import wicket.protocol.http.WebApplication;
import wicket.Component;

import org.apache.commons.io.FileUtils;
import org.safehaus.triplesec.TriplesecInstallationLayout;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;
import org.safehaus.triplesec.configui.util.TriplesecConfigTool;


public class WizardPanelFinish extends WizardPanel
{
    private static final long serialVersionUID = 1L;
    private String message = "Click the Done button to apply settings to the TripleSec server.";


    public WizardPanelFinish( String id, IModel model )
    {
        super( id, model, "Finished!" );
        getForm().add( new Label( "message", new Model()
        {
            private static final long serialVersionUID = 5029629236706738579L;

            public Object getObject(Component component)
            {
                return message;
            }
        }
        ) );
    }


    protected Button newNextButton( String id )
    {
        Button done = new Button( id )
        {
            private static final long serialVersionUID = -2684017282683778591L;


            protected void onSubmit()
            {
                WebApplication app = ( WebApplication ) this.getApplication();
                String realPath = app.getWicketServlet().getServletContext().getRealPath( "" );
                File configuiDir = new File( realPath );
                File webappsDir = configuiDir.getParentFile();
                File installDir = webappsDir.getParentFile();
                TriplesecInstallationLayout layout = new TriplesecInstallationLayout( installDir );
                try
                {
                    TriplesecConfigTool.writeConfiguration( layout, ( TriplesecConfigSettings ) getForm()
                        .getModelObject() );
                    FileUtils.touch( new File( configuiDir, "configured" ) );
                    message = "Triplesec Server will now start automatically...";
                    hideButtons();
                    info( "Server configuration completed." );
                }
                catch ( Exception e )
                {
                    error( e.toString() );
                }
            }
        };
        done.setModel( new Model( "Done" ) );
        return done;
    }
}
