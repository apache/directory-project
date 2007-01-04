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


import wicket.examples.wizard.ezwizard.intf.*;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Represents a state of a wizard Finite State Machine (FSM). Contains common
 * navigation methods. Derived concrete state should also contain
 * setters/getters for domain data, relevant to this state.
 * 
 * @version 0.5
 * @author Michael Jouravlev
 */
public abstract class WizardStep implements IWizardStep, Serializable
{

    /***************************************************************************
     * IWizardStep implementation
     **************************************************************************/

    /**
     * Checkpoint flag. If set, then wizard cannot return back from this state.
     */
    private boolean checkpoint;


    /**
     * Returns true if this state is marked as checkpoint
     * 
     * @return true if this state is marked as checkpoint
     */
    public boolean isCheckpoint()
    {
        return checkpoint;
    }


    /**
     * Sets this state as checkpoint. This should be done before wizard is
     * traversed back from this state.
     * 
     * @param checkpoint
     *            true if this state should be marked as checkpoint
     */
    public void setCheckpoint( boolean checkpoint )
    {
        this.checkpoint = checkpoint;
    }

    /**
     * The name of this state
     */
    protected String stateName;


    /**
     * Returns the state name
     * 
     * @return state name
     */
    public String getStateName()
    {
        return stateName;
    }

    /**
     * Master wizard object; this reference can be used to access common data
     * defined in the wizard object itself.
     */
    protected IWizard wizard;


    /**
     * Returns master wizard object
     * 
     * @return master wizard object
     */
    public IWizard getWizard()
    {
        return wizard;
    }

    /**
     * Incoming transition.
     */
    protected IWizardTransition incomingTransition;


    /**
     * Returns incoming transition for this state, used during actual wizard
     * traversal
     * 
     * @return incoming transition for this node
     */
    public IWizardTransition getIncomingTransition()
    {
        return incomingTransition;
    }


    /**
     * Sets incoming transition, used by wizard controller. Defined as public
     * because it belongs to the IWizardStep interface.
     * 
     * @param value
     *            incoming transition
     */
    public void setIncomingTransition( IWizardTransition value )
    {
        incomingTransition = value;
    }

    /**
     * In this version outgoing trasitions are implemented as array list for
     * easy adding of new transitions.
     */
    private ArrayList outgoingTransitions = new ArrayList();


    /**
     * Returns array of outgoing transitions in the same order in which they
     * were added by addOutgoingTransition() method.
     * 
     * @return array of outbound transitions, the transition with the most
     *         weight is returned first.
     */
    public IWizardTransition[] getOutgoingTransitions()
    {
        return ( IWizardTransition[] ) outgoingTransitions.toArray( new IWizardTransition[0] );
    }


    /**
     * Adds an outgoing transition. Transitions must be added in the order of
     * their weight, the edge with highest weight must be added first. <br>
     * <br>
     * Future versions may support dynamic adjustment of weight, this version
     * does not support it.
     * 
     * @param value
     *            outgoing transition
     */
    public void addOutgoingTransition( IWizardTransition value )
    {
        /*
         * Add a transition as outgoing, and set this state as the source state
         * for the transition. Transition objects are not shared between states.
         */
        outgoingTransitions.add( value );
        value.setSource( this );
    }


    /**
     * Returns a transition which will be chosen if a "next" command is selected
     * for this node. <br>
     * <br>
     * All outbound transitions are validated in the order they were added to
     * this state. The first valid transition is chosen.
     * 
     * @return a first valid outgoing transition or null if no valid transitions
     *         found
     */
    public IWizardTransition getOutgoingTransition()
    {
        IWizardTransition[] edges = this.getOutgoingTransitions();
        for ( int i = 0; i < edges.length; i++ )
        {
            if ( edges[i].validate() )
            {
                return edges[i];
            }
        }
        return null;
    }


    /**
     * Verifies that this state is included in the path to the current state
     * (the current state is past this state).
     * 
     * @return true if this state is found in the actual path from the initial
     *         state to the current state; false otherwise.
     */
    public boolean isStateInPath()
    {
        return ( this == wizard.getCurrentStep() || checkTraverseBack( wizard.getCurrentStep(), this ) );
    }


    /***************************************************************************
     * Helpers and constructor
     **************************************************************************/

    /**
     * Verifies if a state is included in the path to the current state
     * 
     * @param startState
     *            the state where to start backward traversal
     * @param searchState
     *            the state which we are looking for in the travseral path
     * @return true if searchNode is found in the actual path while traversing
     *         back from startNode; false otherwise.
     */
    public static boolean checkTraverseBack( IWizardStep startState, IWizardStep searchState )
    {
        IWizardTransition incomingEdge = startState.getIncomingTransition();
        if ( incomingEdge == null )
            return false;
        IWizardStep srcState = incomingEdge.getSource();
        return srcState == searchState ? true : checkTraverseBack( srcState, searchState );
    }


    /**
     * Constructs the state, sets a name and stores a reference to the owner
     * wizard object.
     * 
     * @param owner
     *            owner wizard object, cannot be null
     * @param name
     *            name of this state, cannot be null, must be unique within the
     *            wizard
     */
    public WizardStep( IWizard owner, String name )
    {
        this.wizard = owner;
        this.stateName = name;
    }
}
