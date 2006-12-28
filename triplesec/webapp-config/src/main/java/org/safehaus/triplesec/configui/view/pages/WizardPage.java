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
package org.safehaus.triplesec.configui.view.pages;


import org.safehaus.triplesec.configui.view.TriplesecConfigWizard;
import org.safehaus.triplesec.configui.view.panels.*;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;
import wicket.model.IModel;
import wicket.model.CompoundPropertyModel;
import wicket.protocol.http.WebApplication;

import java.io.File;


/**
 * Page that contains all the panels that represent the app's wizard steps.
 */
public class WizardPage extends BasePage
{
    private static final long serialVersionUID = 1L;

    private TriplesecConfigWizard wizard;

    // array of wizard panels
    WizardPanel[] wizardPanels = new WizardPanel[10];


    public WizardPage()
    {
        // always turn versioning off...
        setVersioned( false );

        // raw wizard model is just a javabean
        TriplesecConfigSettings rawModel = new TriplesecConfigSettings();

        // set the default value for the LDAP cert file path...
        WebApplication application = ( WebApplication ) this.getApplication();
        File configDir = new File( application.getWicketServlet().getServletContext().getRealPath( "" ) );
        File sslDir = new File( new File( configDir.getParentFile().getParentFile(), "var" ), "ssl" );
        File certFile = new File( sslDir, "default.cert" );
        rawModel.setLdapCertFilePath( certFile.getAbsolutePath() );

        // wrap raw model with IModel so Wicket can work with it
        IModel wizardModel = new CompoundPropertyModel( rawModel );

        // Each panel is a wizard step. Initialization in the loop
        // cannot be used, because each step defines different model-
        // related methods, thus all steps have different types.
        wizardPanels[TriplesecConfigWizard.STEP_INTRO] = new WizardPanelIntro( "wp" + TriplesecConfigWizard.STEP_INTRO,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_REALM] = new WizardPanelRealm( "wp" + TriplesecConfigWizard.STEP_REALM,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_LDAP] = new WizardPanelLdap( "wp" + TriplesecConfigWizard.STEP_LDAP,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_LDAPS] = new WizardPanelSecureLdap( "wp"
            + TriplesecConfigWizard.STEP_LDAPS, wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_SMS] = new WizardPanelSms( "wp" + TriplesecConfigWizard.STEP_SMS,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_SMTP] = new WizardPanelSmtp( "wp" + TriplesecConfigWizard.STEP_SMTP,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_USER] = new WizardPanelUser( "wp" + TriplesecConfigWizard.STEP_USER,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_ADMIN] = new WizardPanelAdmin( "wp" + TriplesecConfigWizard.STEP_ADMIN,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_DEMO] = new WizardPanelDemo( "wp" + TriplesecConfigWizard.STEP_DEMO,
            wizardModel );
        wizardPanels[TriplesecConfigWizard.STEP_FINISH] = new WizardPanelFinish( "wp"
            + TriplesecConfigWizard.STEP_FINISH, wizardModel );

        // Wizard controller is ok with raw model... also sets reference to
        // wizard controller in each step
        wizard = new TriplesecConfigWizard( rawModel, wizardPanels );

        // add all panels to wizard page...
        for ( int i = 0; i < wizardPanels.length; i++ )
        {
            add( wizardPanels[i] );
        }

        // hide all panels except the first one
        updatePanels();
    }


    public void onCancel()
    {
        wizard = null;
        updatePanels();
    }


    public boolean onBack()
    {
        boolean movedBack = false;
        if ( wizard != null )
        {
            movedBack = wizard.back();
        }
        updatePanels();
        return movedBack;
    }


    public boolean onNext()
    {
        boolean movedNext = false;
        if ( wizard != null )
        {
            movedNext = wizard.forward();
        }
        updatePanels();
        return movedNext;
    }


    public void updatePanels()
    {
        for ( int i = 0; i < wizardPanels.length; i++ )
        {
            wizardPanels[i].setVisible( wizard != null && ( "wp" + i ).equals( wizard.getCurrentStepName() ) );
        }
    }
}
