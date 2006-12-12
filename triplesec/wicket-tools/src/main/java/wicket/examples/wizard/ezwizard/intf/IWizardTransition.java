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
 * Represents a state transition. In Easy Wizard a transition is an object, it
 * can refer to its source and target states, and can validate itself. When
 * wizard controller moves from one state to another, it iterates through
 * transitions of current state, and validates each of them in order of their
 * weight. The first valid transition is used to change the state.
 *
 * @version 0.5
 * @author Michael Jouravlev
 */
public interface IWizardTransition {

    /**
     * Returns human-readable name of the transition, or null if not set.
     * <br><br>
     * The name is not needed for wizard navigation and thus is optional.
     * Nevertheless, names are highly recommended because they allow to
     * find a transition by name, and also may provide mapping name
     * for UI layer.
     *
     * @return transition name or null if name is not set
     */
    public String getName();

    /**
     * Returns source of this state, cannot be null. Each transition refers
     * to its source and target states.
     *
     * @return source of this transition
     */
    public IWizardStep getSource();

    /**
     * Sets source state for this transition
     * @param value  source state for this transition
     */
    public void setSource(IWizardStep value);

    /**
     * Returns target state of this transition, cannot be null. Each transition
     * refers to its source and target states.
     *
     * @return target state of this transition
     */
    public IWizardStep getTarget();

    /**
     * Validates this transition. Usually checks the domain accounts, which
     * is referenced from the source state of this transition.
     * <br><br>
     * Outgoing transitions are validated in order of their weight,
     * when state is about to change. The first valid transition is chosen
     * for traversal.
     *
     * @return true if this transition is valid
     */
    public boolean validate();

    /**
     * Returns wizard controller object, which owns this state
     * @return wizard which owns this state
     */
    IWizard getWizard();
}
