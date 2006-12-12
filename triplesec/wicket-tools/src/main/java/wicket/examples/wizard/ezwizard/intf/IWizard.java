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

package wicket.examples.wizard.ezwizard.intf;

import java.util.Map;

/**
 * This interface defines an Easy Wizard controller. The wizard controller
 * contains references to wizard steps and transitions, and allows to
 * move from one step to another.
 *
 * @version 0.5
 * @author Michael Jouravlev
 */
public interface IWizard {

    /**************************************************************************
     * Where are we and where did we come from
     **************************************************************************/

    /**
     * Locates the initial step of this wizard.
     * @return the initial step of this wizard
     */
    public IWizardStep getSourceStep();

    /**
     * Locates the current step of this wizard.
     * @return the current step of this wizard
     */
    public IWizardStep getCurrentStep();

    /**
     * Locates a step by its name. Step name is used by clients
     * of wizard controller, for example by Dialog Manager to refer
     * to a corresponding view.
     * @param name  step name
     * @return the first step found traversing forward from the initial step,
     *         having passed name; or null if the step not found.
     */
    public IWizardStep getStepByName(String name);

    /**
     * Returns a mapping name for a wizard panel. Usually wizard step name
     * is used as mapping name.
     * @return mapping name for a wizard view, usually path to JSP page
     */
    String getCurrentStepName();

    /**
     * Returns errors for current wizard state.
     * @return error messages for current wizard state
     */
    Map getWizardErrors();

    /**
     * Attempts to move to the next step. If it is not possible, stays on the
     * current step.
     * <p>
     * Easy Wizard does not blindly choose a transition based on input command
     * or event. Instead, it iterates over all transitions defined for current
     * step, validates them, and chooses th one which is valid. If several
     * transitions happen to be valid, the first one is chosen.
     * @return true if was able to move to a next step, false otherwise
     * @see IWizardStep#addOutgoingTransition(IWizardTransition)
     */
    public boolean forward();

    /**
     * Attempts to move to the previous step. If the previous step does not
     * exist or the current step is the initial step of the wizard, stays
     * on the current step. Transition is not validated while traversing back.
     * @return true if was able to move to a previous step, false otherwise
     */
    public boolean back();

    /**
     * Returns wizard completion status. This method is redundant,
     * but is defined to hide Wizard instance from WizardAction.
     * @return true is wizard was successfully completed and disposed;
     *         false if wizard is still active or has not been initialized yet.
     */
    boolean isCompleted();

    /**
     * Clean wizard errors. Just in case. Usually Wizard Manager
     * cleans errors if needed.
     */
    void clearWizardErrors();

    /**
     * Resets certain wizard fields, like booleans. Called with every user
     * input. The primary reason for this method is older frameworks like
     * Struts, which use default HTTP handling of boolean properties, and
     * do not notifiy a property when it is cleared.
     */
    void wizardReset();

    /**
     * Adds a listener for state change event.
     */
    void addListener(IWizardListener listener);

    /**
     * Removes a listener for state change event.
     */
    void removeListener(IWizardListener listener);

    /**
     * Removes all wizard listeners for state change event.
     */
    void removeAllListeners();

}
