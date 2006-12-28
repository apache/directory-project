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
package org.safehaus.triplesec.registration.view.pages;


import org.safehaus.triplesec.registration.model.RegistrationInfo;
import org.safehaus.triplesec.registration.view.TriplesecRegistrationApplication;
import org.safehaus.triplesec.registration.view.TriplesecRegistrationWizard;
import org.safehaus.triplesec.registration.view.panels.*;

import wicket.model.IModel;
import wicket.model.CompoundPropertyModel;


/**
 * Page that contains all the panels that represent the app's wizard steps.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class WizardPage extends BasePage
{
    private static final long serialVersionUID = 1L;

    private TriplesecRegistrationWizard wizard;

    // array of wizard panels
    WizardPanel[] wizardPanels = new WizardPanel[5];


    public WizardPage()
    {
        // always turn versioning off...
        setVersioned( false );

        // raw wizard model is just a javabean
        RegistrationInfo rawModel = new RegistrationInfo();

        // set default values for various fields here

        // wrap raw model with IModel so Wicket can work with it
        IModel wizardModel = new CompoundPropertyModel( rawModel );

        // Each panel is a wizard step. Initialization in the loop
        // cannot be used, because each step defines different model-
        // related methods, thus all steps have different types.
        wizardPanels[TriplesecRegistrationWizard.STEP_INTRO] = 
            new WizardPanelIntro( "wp" + TriplesecRegistrationWizard.STEP_INTRO, wizardModel );
        wizardPanels[TriplesecRegistrationWizard.STEP_USER] = 
            new WizardPanelUser( "wp" + TriplesecRegistrationWizard.STEP_USER, wizardModel );
        wizardPanels[TriplesecRegistrationWizard.STEP_ADDR] = 
            new WizardPanelAddress( "wp" + TriplesecRegistrationWizard.STEP_ADDR, wizardModel );
        wizardPanels[TriplesecRegistrationWizard.STEP_DEPLOY] = 
            new WizardPanelDeploy( "wp" + TriplesecRegistrationWizard.STEP_DEPLOY, wizardModel );
        wizardPanels[TriplesecRegistrationWizard.STEP_FINISH] = new WizardPanelFinish( "wp"
            + TriplesecRegistrationWizard.STEP_FINISH, wizardModel );

        // Wizard controller is ok with raw model... also sets reference to
        // wizard controller in each step
        wizard = new TriplesecRegistrationWizard( ( TriplesecRegistrationApplication ) getApplication(), 
            rawModel, wizardPanels ); 

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
