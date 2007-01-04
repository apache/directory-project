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
package wicket.examples.wizard.ezwizard.impl;

import wicket.examples.wizard.ezwizard.intf.IWizard;
import wicket.examples.wizard.ezwizard.intf.IWizardStep;
import wicket.examples.wizard.ezwizard.intf.IWizardTransition;
import wicket.examples.wizard.ezwizard.intf.IWizardListener;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class containts base implementation of wizard controller and handles
 * wizard traversal. The wizard controller contains references to wizard states
 * and transitions, and allows to move from one state to another.
 * <br><br>
 * The UI specifics like reporting of error messages should be handled by
 * descendant classes.
 *
 * @version 0.5
 * @author Michael Jouravlev
 */
public class Wizard implements IWizard, Serializable {

    /**************************************************************************
     * Error messages
     **************************************************************************/

    /**
     * Error list. Key is either string message or ID of a message in
     * a property file. Value is a string array of values, or null if there
     * are no arguments.
     * <p>
     * Errors are initialized in backing bean when a wizard object is created.
     * Reference to existing error object must be passed
     * to this wizard controller from backing bean.
     */
    protected Map errors = new HashMap();

    /**
     * Standard implementation returns a dummy map for error messages in
     * case the concrete implementation generates messages externally and
     * does not provide message placeholder.
     * <p>
     * Concrete implementation must provide a valid reference to message map,
     * if it wants to return messages from Rule Engine.
     *
     * @return message placeholder map
     */
    public Map getWizardErrors() {return errors;}

    /**
     * Returns true if wizard has been successfully completed. Wizard
     * is usually completes on its last step, after business accounts is updated.
     * Completed status is retained by the wizard until wizard instance
     * is disposed.
     *
     * @return true if wizard has been successfully completed and is ready
     *         to be disposed.
     */
    public boolean isCompleted() {return currentState == null;}

    /**************************************************************************
     * IWizard implementation
     **************************************************************************/

    /**
     * The intial state of this wizard. A wizard has only one initial state.
     */
    protected IWizardStep sourceState;

    /**
     * Returns source state of this wizard. A wizard has only one initial state.
     * @return source state of this wizard
     */
    public IWizardStep getSourceStep() {return sourceState;}

    /**
     * Current state of this wizard.
     */
    protected IWizardStep currentState;
    /**
     * Returns current state of this wizard; on wizard startup is the same as
     * source state.
     * @return current state of this wizard
     */
    public IWizardStep getCurrentStep() {return currentState;}

    /**
     * Locates a state in this wizard by its name
     *
     * @param name  state name
     * @return the first state starting from source,
     *         which name is equal to the needed name, or null
     *         if the state not found.
     */
    public IWizardStep getStepByName(String name) {
        IWizardStep sourceNode = getSourceStep();
        if (sourceNode == null) return null;
        if (sourceNode.getStateName().equals(name)) return sourceNode;
        return findNode(sourceNode, name);
    }

    /**
     * Tries to move to the next state. If cannot move, keeps current state.
     */
    public boolean forward() {

        if (isCompleted()) {
            return false;
        }

        clearWizardErrors();

        /*
         * Find outgoing transition, returns null if current state
         * is the last state
         */
        IWizardTransition outgoingTransition = currentState.getOutgoingTransition();

        /*
         * Process final node, it may set "completed" status
         */
        if (isLastStep()) {
            boolean canLeave = canLeave(IWizardListener.STEP_LAST);
            if (canLeave) {
                currentState = null;
            }
            return canLeave;

        /*
         * Not last step and about to move forward
         */
        } else if (outgoingTransition != null) {

            /*
             * Find next state
             */
            IWizardStep nextState = outgoingTransition.getTarget();
            if (nextState == null) return false;

            /*
             * Cannot move forward
             */
            if (!canLeave(IWizardListener.STEP_REGULAR)) {
                return false;
            }

            /*
             * Moving forward: set incoming transition for next state
             * unless next state is not a checkpoint
             */
            nextState.setIncomingTransition(currentState.isCheckpoint() ?
                                            null :
                                            outgoingTransition);

            /*
             * Move to the next node
             */
            currentState = nextState;
            return true;

        /*
         *  Wizard stayed on the same step
         */
        } else {
            return false;
        }
    }

    /**
     * Tries to move to the previous state. Stays on the current state
     * if traversal back is impossible either because this state is
     * the initial wizard state or was marked as checkpoint.
     */
    public boolean back() {

        if (isCompleted()) {
            return false;
        }

        /*
         * Find the incoming transition for the current state
         */
        IWizardTransition incomingTransition = currentState.getIncomingTransition();

        /*
         * Traverse back if incoming transition exists
         */
        if (incomingTransition != null) {
            currentState = incomingTransition.getSource();
            clearWizardErrors();
            return true;
        }
        return false;
    }

    /**
     * Verifies is current wizard step a last step.
     * @return true if current wizard step a last step
     */
    protected boolean isLastStep() {
        IWizardTransition[] transitions = currentState.getOutgoingTransitions();
        return transitions == null || transitions.length == 0;
    }

    /**
     * Executed after the forward transition is chosen, but before the state
     * is changed. Verifies with listening application, is it allowed to
     * perform a state change
     * @param event  event that is occurring now, like moving to next step
     * @return true if all listeners allow to move to the next step
     */
    protected boolean canLeave(int event) {
        boolean canLeave = true;
        Iterator lisIte = getListeners().values().iterator();
        while(lisIte.hasNext()) {
            IWizardListener wizardListener = (IWizardListener) lisIte.next();
            canLeave &= wizardListener.onTransition(event);
        }
        return canLeave;
    }

    /**
     * Returns a name which is used to display proper wizard panel.
     * @return mapping name for a wizard view, usually path to JSP page
     */
    public String getCurrentStepName() {
        IWizardStep currentStep = getCurrentStep();
        return currentStep == null ? null : getCurrentStep().getStateName();
    }

    /**
     * Listeners, checking for state transition
     */
    protected Map listeners = new HashMap();

    /**
     * Adds a listener for state change event.
     */
    public void addListener(IWizardListener listener) {
        synchronized(listeners) {
            listeners.put(listener.getClass().getName(), listener);
        }
    }

    /**
     * Removes a listener for state change event.
     */
    public void removeListener(IWizardListener listener) {
        synchronized(listeners) {
            Iterator lisIte = listeners.values().iterator();
            while(lisIte.hasNext()) {
                IWizardListener wizardListener = (IWizardListener) lisIte.next();
                if (wizardListener == listener) {
                    lisIte.remove();
                }
            }
        }
    }

    /**
     * Removes all wizard listeners for state change event.
     */
    public void removeAllListeners() {
        synchronized(listeners) {
            listeners.clear();
        }
    }

    /**
     * Returns all current listeners for wizard event
     */
    public Map getListeners() {
        return listeners;
    }

    /**************************************************************************
     * Helper methods
     **************************************************************************/

    /**
     * Finds a state by its name. Need to pass staring state as an argument
     * because of the recursive calls. Starting state parameter allows
     * to search from any state.
     * <p>
     * Important: starting state itself is not checked
     *
     * @param state  the state where to start search, this state itself is not
     *        verified and must be checked outside this method
     * @param name  the name of the state to look for
     * @return the state with the needed name, or null if state not found
     */
    public static IWizardStep findNode(IWizardStep state, String name) {
        if (state == null || name == null) return null;
        IWizardTransition[] transitions = state.getOutgoingTransitions();
        if (transitions != null) {
            for (int i = 0; i < transitions.length; i++) {
                IWizardStep probedState = transitions[i].getTarget();
                IWizardStep resultState =
                    name.equals(probedState.getStateName()) ?
                    probedState :
                    findNode(probedState, name);
                if (resultState != null) return resultState;
            }
        }
        return null;
    }

    /**************************************************************************
     * Implementing IWizardManager
     **************************************************************************/

    /**
     * Clear wizard errors. Does nothing here (need to implement).
     * Backing bean will clear errors when needed.
     */
    public void clearWizardErrors() {
        Map errors = getWizardErrors();
        if (errors != null && errors.size() > 0) {
            errors.clear();
        }
    }

    public void wizardReset() {
//        clearWizardErrors();
        getCurrentStep().resetBooleans();
    }

}
