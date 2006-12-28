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
package org.safehaus.triplesec.registration.view;


import wicket.examples.wizard.ezwizard.impl.Wizard;
import wicket.examples.wizard.ezwizard.impl.WizardTransition;
import wicket.examples.wizard.ezwizard.intf.IWizardStep;
import wicket.util.string.Strings;

import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.NoSuchEntryException;
import org.safehaus.triplesec.registration.model.RegistrationInfo;


/**
 * This class defines the registration wizard of the Triplesec server.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class TriplesecRegistrationWizard extends Wizard
{

    // Raw model so wizard can query it to find out model status
    private RegistrationInfo model;

    // Friendly indices in the array of wizard panels
    public static int STEP_INTRO = 0;
    public static int STEP_USER = 1;
    public static int STEP_ADDR = 2;
    public static int STEP_DEPLOY = 3;
    public static int STEP_FINISH = 4;

    protected static final String NO_PSWD = "Password field must be set.";
    protected static final String NO_PSWD_CONFIRM = "Password confirmation field must be set.";
    protected static final String CONFIRM_FAILURE = "Password and confirmation fields do not match.";
    protected static final String NO_DEP_MECH = "A deployment mechanism must be selected.";
    protected static final String NO_CARRIER = "A mobile carrier must be selected.";

    
    /**
     * Constructs a TripleSec configuration wizard.
     */
    public TriplesecRegistrationWizard( final TriplesecRegistrationApplication app, 
        final RegistrationInfo model, IWizardStep[] wizardSteps )
    {

        // Store a reference to the model, so steps and transitions
        // can use it.
        this.model = model;

        // set a reference to this master controller in each wizard
        // step, so steps can refer to common wizard data -- like model
        for ( int i = 0; i < wizardSteps.length; i++ )
        {
            wizardSteps[i].setOwner( this );
        }

        // Start wizard from the INTRO node. The fact that steps are
        // defined in an array does not imply the wizard flow sequence.
        // Sequence is controlled by transition objects.
        sourceState = wizardSteps[STEP_INTRO];

        // Set the current state to source node...
        currentState = sourceState;

        // wizard steps and transitions...
        wizardSteps[STEP_INTRO].addOutgoingTransition( new WizardTransition( this, "INTRO to USER",
            wizardSteps[STEP_USER] )
        {
            //  no validation needed for this transition
            public boolean validate()
            {
                return true;
            }
        } );
        wizardSteps[STEP_USER].addOutgoingTransition( new WizardTransition( this, "USER TO ADDR",
            wizardSteps[STEP_ADDR] )
        {
            public boolean validate()
            {
                // -----------------------------------------------------------
                // Some simple presence validations
                // -----------------------------------------------------------
                
                if ( Strings.isEmpty( model.getPassword() ) )
                {
                    errors.put( "noPswd", NO_PSWD );
                    return false;
                }
                if ( Strings.isEmpty( model.getPasswordConfirm() ) )
                {
                    errors.put( "noPswdConfirm", NO_PSWD_CONFIRM );
                    return false;
                }
                if ( ! Strings.isEqual( model.getPassword(), model.getPasswordConfirm() ) )
                {
                    errors.put( "ConfirmFailure", CONFIRM_FAILURE );
                    return false;
                }
                
                // -----------------------------------------------------------
                // Let's validate that the username is available for new user
                // -----------------------------------------------------------

                try
                {
                    if ( app.getAdmin().hasUser( model.getUsername() ) )
                    {
                        errors.put( "usernameTaken", "A user already exists with the name: " + model.getUsername() );
                        return false;
                    }
                }
                catch ( NoSuchEntryException e )
                {
                    // this is what we hope for 
                }
                catch ( DataAccessException e )
                {
                    // this is bad news but nothing we can do here but log it
                    app.getWicketServlet().log( "Failure when trying to check for user existance.", e );
                }
                
                return true;
            }
        } );
        wizardSteps[STEP_ADDR].addOutgoingTransition( new WizardTransition( this, "ADDR TO DEPLOY",
            wizardSteps[STEP_DEPLOY] )
        {
            public boolean validate()
            {
                return true;
            }
        } );
        wizardSteps[STEP_DEPLOY].addOutgoingTransition( new WizardTransition( this, "DEPLOY TO FINISH",
            wizardSteps[STEP_FINISH] )
        {
            public boolean validate()
            {
                // -----------------------------------------------------------
                // Some simple presence validations
                // -----------------------------------------------------------
                
                if ( Strings.isEmpty( model.getDeploymentMechanism() ) )
                {
                    errors.put( "noDepMech", NO_DEP_MECH );
                    return false;
                }
                if ( Strings.isEmpty( model.getMobileCarrier()) )
                {
                    errors.put( "noCarrier", NO_CARRIER );
                    return false;
                }
                if ( Strings.isEmpty( model.getTokenPin() ) )
                {
                    errors.put( "noPin", "You must enter a numeric pin of 4 or more digits." );
                    return false;
                }
                if ( Strings.isEmpty( model.getTokenPinConfirm() ) )
                {
                    errors.put( "noPinConfirm", "You must enter a numeric pin confirmation of 4 or more digits." );
                    return false;
                }
                if ( ! model.getTokenPin().equals( model.getTokenPinConfirm() ) )
                {
                    errors.put( "pinConfirmNotEqual", "The pin and pin confirmation values are not equal." );
                    return false;
                }
                

                // -----------------------------------------------------------
                // Let's validate the correct format of pin and mobile number
                // -----------------------------------------------------------
                
                for ( int ii = 0; ii < model.getTokenPin().length(); ii++ )
                {
                    if ( ! Character.isDigit( model.getTokenPin().charAt( ii ) ) )
                    {
                        errors.put( "pinFormat", "Pin must contain only numeric characters." );
                        return false;
                    }
                }
                
                if ( model.getTokenPin().length() < 4 )
                {
                    errors.put( "pinTooShort", "Pin too short. Length must be at least 4 digits in length" );
                    return false;
                }
                
                for ( int ii = 0; ii < model.getMobile().length(); ii++ )
                {
                    if ( ! Character.isDigit( model.getMobile().charAt( ii ) ) )
                    {
                        errors.put( "mobileNumInvalid", "Mobile phone number must contain only numeric characters." );
                        return false;
                    }
                }
                
                if ( model.getMobile().length() != 10 )
                {
                    errors.put( "mobileNumLengthInvalid", "Mobile phone number should be ten digits long." );
                    return false;
                }
                
                return true;
            }
        } );
    }

    
    /**
     * Returns the raw model bean for the TripleSec configuration wizard.
     */
    public RegistrationInfo getModel()
    {
        return model;
    }
}
