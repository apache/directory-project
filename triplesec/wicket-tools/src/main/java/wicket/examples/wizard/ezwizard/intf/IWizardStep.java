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

/**
 * Represents a state of a wizard Finite State Machine (FSM).
 * <br/><br/>
 * A base state contains common navigation methods, a concrete state also
 * contains setters/getters for domain data, relevant to this state.
 *
 * @version 0.5
 * @author Michael Jouravlev
 */
public interface IWizardStep {

    /**
     * Returns array of outgoing transitions for this state. The transitions
     * are weighed according to the order in which they were added to this
     * state.
     *
     * @return array of outgoing transitions
     */
    IWizardTransition[] getOutgoingTransitions();

    /**
     * Selects a transition which will be used to move forward from
     * from this state.
     * <p>
     * All outgoing transitions are validated in the order of their weight.
     * The first valid transition is chosen.
     *
     * @return a valid outgoing edge or null if no valid edges found
     */
    IWizardTransition getOutgoingTransition();

    /**
     * Returns the incoming transition, was used to get to this state.
     *
     * @return incoming transition
     */
    IWizardTransition getIncomingTransition();

    /**
     * Sets the incoming transition. Used during forward traversal
     * to mark the way through the wizard. The incoming transition
     * is used for backward traversal.
     *
     * @param value  incoming transition
     */
    void setIncomingTransition(IWizardTransition value);

    /**
     * Adds an outgoing transition. Order in which transitions are added
     * to a state, defines their weight (or rank). Transition added to
     * a state first, has the highest weight. Transitions are validated
     * during forward traversal in order of their weight, and the first
     * valid transition is chosen for state change.
     *
     * @param value outgoing transition
     */
    void addOutgoingTransition(IWizardTransition value);

    /**
     * Returns true if this state has been marked as checkpoint. This means
     * that a wizard cannot return back from this state.
     *
     * @return true if this state is a checkpoint
     */
    boolean isCheckpoint();

    /**
     * Returns the name of this state. Each state must have a unique name..
     *
     * @return state name
     */
    String getStateName();

    /**
     * Returns the wizard, which contains this state as part
     * of the wizard sequence.
     *
     * @return wizard which owns this node
     */
    IWizard getWizard();

    /**
     * Verifies that this state is included in the path to the current state.
     *
     * @return true if this state is present in the actual path to the current
     *          state, but is not a current state itself; false otherwise.
     */
    boolean isStateInPath();

    /**
     * Instructs the state to clear all its boolean properties.
     * Usually called before the properties are about to be set.
     * <br><br>
     * The reason for this method is that browsers/HTTP do not notify
     * server about cleared checkboxes or radiobuttons. So, these values
     * should be cleared before they are set with client values.
     * <br><br>
     * This method is not used in JSF.
     */
    void resetBooleans();

    /**
     * Sets master wizard controller for this step.
     */
    void setOwner(IWizard wizard);
}
