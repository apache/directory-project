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
package org.safehaus.triplesec.configui.view;

import wicket.examples.wizard.ezwizard.impl.Wizard;
import wicket.examples.wizard.ezwizard.impl.WizardTransition;
import wicket.examples.wizard.ezwizard.intf.IWizardStep;
import wicket.util.string.Strings;
import org.safehaus.triplesec.configui.model.TriplesecConfigSettings;
import org.safehaus.triplesec.configui.util.CertificateUtil;

import java.io.File;

/**
 * This class defines the configuration wizard of the TripleSec
 * server.
 */
public class TriplesecConfigWizard extends Wizard {
    // Raw model so wizard can query it to find out model status
    private TriplesecConfigSettings model;

    // Friendly indices in the array of wizard panels
    public static int STEP_INTRO = 0;
    public static int STEP_REALM = 1;
    public static int STEP_LDAP = 2;
    public static int STEP_LDAPS = 3;
    public static int STEP_SMS = 4;
    public static int STEP_SMTP = 5;
    public static int STEP_USER = 6;
    public static int STEP_ADMIN = 7;
    public static int STEP_DEMO = 8;
    public static int STEP_FINISH = 9;

    public static String NO_CERT_PATH_ERR =
            "Certificate path required when Secure LDAP is enabled.";
    public static String NO_CERT_PSWD_ERR =
            "Certificate password required when Secure LDAP is enabled";
    public static String BAD_CERT_PATH_ERR =
            "Failed to create certificate file: ";
    public static String NO_SMTP_USERNAME =
            "SMTP Account username required when SMTP authentication enabled";
    public static String NO_SMTP_PASSWORD =
            "SMPT Account password required when SMTP authentication enabled";
    public static String ADMIN_PSWD_MISMATCH =
            "Must provide admin password that match";
    public static String NO_SMS_PSWD =
            "Password required for SMS configuration";
    /**
     * Constructs a TripleSec configuration wizard.
     */
    public TriplesecConfigWizard(
            final TriplesecConfigSettings model,
                                 IWizardStep[] wizardSteps) {

        // Store a reference to the model, so steps and transitions
        // can use it.
        this.model = model;

        // set a reference to this master controller in each wizard
        // step, so steps can refer to common wizard data -- like model
        for (int i = 0; i < wizardSteps.length; i++) {
            wizardSteps[i].setOwner(this);
        }

        // Start wizard from the INTRO node. The fact that steps are
        // defined in an array does not imply the wizard flow sequence.
        // Sequence is controlled by transition objects.
        sourceState = wizardSteps[STEP_INTRO];

        // Set the current state to source node...
        currentState = sourceState;

        // wizard steps and transitions...
        wizardSteps[STEP_INTRO].addOutgoingTransition(
                new WizardTransition(this, "INTRO to REALM", wizardSteps[STEP_REALM]) {
                    //  no validation needed for this transition
                    public boolean validate() {
                        return true;
                    }
                }
        );
        wizardSteps[STEP_REALM].addOutgoingTransition(
                new WizardTransition(this, "REALM TO LDAP", wizardSteps[STEP_LDAP]) {
                    public boolean validate() {
                        return true;
                    }
                }
        );
        wizardSteps[STEP_LDAP].addOutgoingTransition(
                new WizardTransition(this, "LDAP TO LDAPS", wizardSteps[STEP_LDAPS]) {
                    public boolean validate() {
                        return true;
                    }
                }
        );
        wizardSteps[STEP_LDAPS].addOutgoingTransition(
                new WizardTransition(this, "LDAPS TO SMS", wizardSteps[STEP_SMS]) {
                    public boolean validate()
                    {
                        String certPath = model.getLdapCertFilePath();
                        String certPswd = model.getLdapCertPassword();
                        if ( model.isEnableLdaps() )
                        {
                            // No certificate path...
                            if ( Strings.isEmpty( certPath ) )
                            {
                                errors.put( "noCertPath", NO_CERT_PATH_ERR );
                                return false;
                            }
                            // No certificate password
                            if ( Strings.isEmpty( certPswd ) )
                            {
                                errors.put( "noCertPswd", NO_CERT_PSWD_ERR );
                                return false;
                            }
                            // Invalid certificate path...
                            File certFile = new File( certPath );
                            if ( !certFile.exists() )
                            {
                                try
                                {
                                    CertificateUtil.create( certFile, model );
                                }
                                catch ( Exception e )
                                {
                                    errors.put("badCertPath",
                                            BAD_CERT_PATH_ERR + certFile.getAbsolutePath() );
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }
        );
        wizardSteps[STEP_SMS].addOutgoingTransition(
                new WizardTransition(this, "SMS TO SMTP", wizardSteps[STEP_SMTP]) {
                    public boolean validate() {
                        if ( Strings.isEmpty( model.getSmsPassword() ) )
                        {
                            errors.put( "noSmsPswd", NO_SMS_PSWD );
                            return false;
                        }
                        if ( Strings.isEmpty( model.getSmsUsername()  ) )
                        {
                            errors.put( "noSmsUsername", "An SMS account username is required." );
                        }
                        return true;
                    }
                }
        );
        wizardSteps[STEP_SMTP].addOutgoingTransition(
                new WizardTransition(this, "SMTP TO USER", wizardSteps[STEP_USER]) {
                    public boolean validate() {
                        if ( model.isSmtpAuthenticate() ) {
                            // No SMTP username
                            if ( Strings.isEmpty( model.getSmtpUsername() ) )
                            {
                                errors.put( "noSmtpUser", NO_SMTP_USERNAME );
                                return false;
                            }
                            if ( Strings.isEmpty( model.getSmtpPassword() ) )
                            {
                                errors.put( "noSmtpPswd", NO_SMTP_PASSWORD );
                                return false;
                            }
                        }
                        return true;
                    }
                }
        );
        wizardSteps[STEP_USER].addOutgoingTransition(
                new WizardTransition(this, "USER TO ADMIN", wizardSteps[STEP_ADMIN]) {
                    public boolean validate() {
                        return true;
                    }
                }
        );
        wizardSteps[STEP_ADMIN].addOutgoingTransition(
                new WizardTransition(this, "ADMIN TO DEMO", wizardSteps[STEP_DEMO]) {
                    public boolean validate() {
                        if ( Strings.isEmpty( model.getAdminPassword() ) )
                        {
                            errors.put("badAdminPswd", ADMIN_PSWD_MISMATCH);
                            return false;
                        }
                        if ( Strings.isEmpty( model.getAdminPassword2() ) )
                        {
                            errors.put("badAdminPswd", ADMIN_PSWD_MISMATCH);
                            return false;
                        }
                        if ( !model.getAdminPassword().equals( model.getAdminPassword2() ) )
                        {
                            errors.put("badAdminPswd", ADMIN_PSWD_MISMATCH);
                            return false;
                        }
                        return true;
                    }
                }
        );
        wizardSteps[STEP_DEMO].addOutgoingTransition(
                new WizardTransition(this, "DEMO TO FINISH", wizardSteps[STEP_FINISH]) {
                    public boolean validate() {
                        return true;
                    }
                }
        );
    }

    /**
     * Returns the raw model bean for the TripleSec configuration wizard.
     */
    public TriplesecConfigSettings getModel() {
        return model;
    }
}
