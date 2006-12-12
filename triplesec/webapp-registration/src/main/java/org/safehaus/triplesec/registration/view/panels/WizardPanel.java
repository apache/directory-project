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
package org.safehaus.triplesec.registration.view.panels;

import wicket.markup.html.panel.Panel;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.Button;
import wicket.examples.wizard.ezwizard.intf.IWizardStep;
import wicket.examples.wizard.ezwizard.intf.IWizardTransition;
import wicket.examples.wizard.ezwizard.intf.IWizard;
import wicket.model.IModel;
import wicket.model.Model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import org.safehaus.triplesec.registration.view.borders.WizardPanelBorder;
import org.safehaus.triplesec.registration.view.pages.WizardPage;

/**
 * Wizard steps are usually derived from a panel, and implement
 * IWizardStep.  Steps usually are panels, so this class should
 * serve as a base class for any wizard panel.
 */
public abstract class WizardPanel extends Panel implements IWizardStep {
    private Button next;
    private Button cancel;
    private Button back;

    /**
     * Master wizard object -- can be used to access common data
     */
    protected IWizard wizard;
    /**
     * Outgoing transitions are implemented as an array list for
     * the easy adding of new transitions.
     */
    private ArrayList outgoingTransitions = new ArrayList();
    /**
     * Incoming transition.
     */
    protected IWizardTransition incomingTransition;
    /**
     * The wizard panel's form.
     */
    private Form form;

    public WizardPanel( String id, IModel model, String title )
    {
        this(id, model, title, false, false);
    }

    public WizardPanel( String id, IModel model, String title,
                       boolean hideBack, boolean hideNext )
    {
        super( id, model );

        // create border and add it to the page
        WizardPanelBorder border = new WizardPanelBorder( "border", new Model( title ) );
        border.setTransparentResolver( true );
        super.add( border );

        // create feedback panel and add it to the panel
        add( new FeedbackPanel( "feedback" ) );

        // create form and add it to the panel
        add( form = new Form( "form", model ) );

        // create cancel button and add it to the panel
        cancel = newCancelButton( "cancel" );
        cancel.setDefaultFormProcessing( false );
        form.add( cancel );

        // create back button and add it to the panel
        back = newBackButton( "back" );
        if ( hideBack )
        {
            back.setVisible(false);
        }
        back.setDefaultFormProcessing( false );
        form.add( back );

        next = newNextButton( "next" );
        if ( hideNext )
        {
            next.setVisible(false);
        }
        form.add( next );
    }

    protected Form newForm( String id, IModel model ) {
        return new Form( id, model );
    }

    protected Button newCancelButton( String id ) {
        return new Button( id )
        {
            private static final long serialVersionUID = -2684017282683778591L;

            protected void onSubmit()
            {
                getWizardPage().onCancel();
            }
        };
    }

    protected Button newBackButton( String id ) {
        return new Button( id )
        {
            private static final long serialVersionUID = -2684017282683778591L;

            protected void onSubmit()
            {
                getWizardPage().onBack();
            }
        };
    }

    protected Button newNextButton( String id ) {
        return new Button( id )
        {
            private static final long serialVersionUID = -2684017282683778591L;

            protected void onSubmit()
            {
                boolean success = getWizardPage().onNext();
                if ( !success )
                {
                    Map errors = getWizard().getWizardErrors();
                    for ( Iterator iter = errors.values().iterator(); iter.hasNext(); )
                    {
                        form.error( (String) iter.next() );
                    }
                }
            }
        };
    }


    protected void hideButtons()
    {
        if ( next != null )
        {
            next.setVisible( false );
        }

        if ( cancel != null )
        {
            cancel.setVisible( false );
        }

        if ( back != null )
        {
            back.setVisible( false );
        }
    }

    protected Form getForm()
    {
        return form;
    }

    protected WizardPage getWizardPage()
    {
        return (WizardPage) this.getPage();
    }

    /**
     * Returns array of outgoing transitions in the same order in which
     * they were added by addOutgoingTransition() method.
     *
     * @return array of outgoing transitions, the transition with the most
     * weight is returned first
     */
    public IWizardTransition[] getOutgoingTransitions()
    {
        return ( IWizardTransition[] )
                outgoingTransitions.toArray( new IWizardTransition[0] );
    }

    /**
     * Returns a transition which will be chosen if a "next" command
     * is selected for this node.
     * <p>
     * All outbound transitions are validated in the order they were
     * added to this state. The first valid transition is chosen.
     *
     * @return a first valid outgoing transition or null if no valid
     * transitions found
     */
    public IWizardTransition getOutgoingTransition()
    {
        IWizardTransition[] edges = this.getOutgoingTransitions();
        for ( int i = 0; i < edges.length; i++ ) {
            if ( edges[i].validate() )
            {
                return edges[i];
            }
        }
        return null;
    }

    /**
     * Returns the incoming transition for this state; used
     * during actual wizard traversal.
     *
     * @return incoming transition
     */
    public IWizardTransition getIncomingTransition()
    {
        return incomingTransition;
    }

    /**
     * Sets the incoming transition. Used during forward traversal
     * to mark the way through the wizard. The incoming transition
     * is used for backward traversal.
     *
     * @param value incoming transition
     */
    public void setIncomingTransition(IWizardTransition value)
    {
        incomingTransition = value;
    }

    /**
     * Adds an outgoing transition. Transitions must be added in the
     * same order should be validated.
     *
     * @param value outgoing transition
     */
    public void addOutgoingTransition(IWizardTransition value)
    {
        outgoingTransitions.add( value );
        value.setSource( this );
    }

    /**
     * Returns true if this state has been marked as checkpoint. This means
     * that a wizard cannot return back from this state.
     *
     * @return true if this state is a checkpoint
     */
    public boolean isCheckpoint()
    {
        // checkpoints not supported...
        return false;
    }

    /**
     * Returns the name of this state. Each state must have a unique name..
     *
     * @return state name
     */
    public String getStateName()
    {
        // use Wicket component ID as step name
        return getId();
    }

    /**
     * Returns the wizard, which contains this state as part
     * of the wizard sequence.
     *
     * @return wizard which owns this node
     */
    public IWizard getWizard()
    {
        return wizard;
    }

    /**
     * Verifies that this state is included in the path to the current state.
     *
     * @return true if this state is present in the actual path to the current
     *         state, but is not a current state itself; false otherwise.
     */
    public boolean isStateInPath()
    {
        return (
                this == wizard.getCurrentStep() ||
                        checkTraverseBack( wizard.getCurrentStep(), this )
                );
    }

    /**
     * Instructs the state to clear all its boolean properties.
     * Usually called before the properties are about to be set.
     * <br><br>
     * The reason for this method is that browsers/HTTP do not notify
     * server about cleared checkboxes or radiobuttons. So, these values
     * should be cleared before they are set with client values.
     * <br><br>
     * This method is used in Struts; not used in JSF and Wicket.
     */
    public void resetBooleans()
    {
        /* no-op */
    }

    /**
     * Sets master wizard controller for this step.
     */
    public void setOwner(IWizard owner)
    {
        wizard = owner;
    }

    /**
     * Verifies if a state is included in the path to the current state.
     *
     * @param startState the state where to start backward traversal
     * @param searchState the state which we are looking for in the
     * traversal path
     * @return true if search node is found in the actual path while
     * traversing back from start node; false otherwise
     */
    private static boolean checkTraverseBack( IWizardStep startState,
                                              IWizardStep searchState )
    {

        IWizardTransition incomingEdge = startState.getIncomingTransition();
        if ( incomingEdge == null )
        {
            return false;
        }
        IWizardStep srcState = incomingEdge.getSource();
        return srcState == searchState || checkTraverseBack( srcState, searchState );
    }
}
