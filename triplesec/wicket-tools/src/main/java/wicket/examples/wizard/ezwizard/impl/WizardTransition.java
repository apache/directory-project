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

import java.io.Serializable;

import wicket.examples.wizard.ezwizard.intf.IWizard;
import wicket.examples.wizard.ezwizard.intf.IWizardStep;
import wicket.examples.wizard.ezwizard.intf.IWizardTransition;

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
public abstract class WizardTransition implements IWizardTransition, Serializable {

    /**
     * Owner wizard object, reference used to access common business fields
     */
    protected IWizard wizard;
    /**
     * Returns owner wizard object
     * @return owner wizard object
     */
    public IWizard getWizard() {return wizard;}

    /**
     * Human-readable name of this transition, or null if not set.
     * Contrary to the state name, the transition name is optional.
     */
    protected String name;

    /**
     * Returns transition name
     * @return transition name
     */
    public String getName() {return name;}

    /**
     * Source node of this edge, never null.
     */
    protected IWizardStep source;

    /**
     * Returns source state for this transition
     * @return source state for this transition
     */
    public IWizardStep getSource() {return source;}

    /**
     * Sets source state for this transition; used by wizard controller
     * @param value source state for this transition
     */
    public void setSource(IWizardStep value) {source = value;}

    /**
     * Target state for this transition, never null.
     */
    protected IWizardStep target;
    /**
     * Returns target state of this transition
     * @return target state of this transition
     */
    public IWizardStep getTarget() {return target;}

    /**
     * Constructs an transition. Source state will be set automatically after
     * this transition is added as outgoing to a state.
     *
     * @param owner  owner wizard object
     * @param name  transition name
     * @param target  transition target
     */
    public WizardTransition(IWizard owner, String name, IWizardStep target) {
        this.wizard = owner;
        this.name = name;
        this.target = target;
    }
}
