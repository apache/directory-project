/*
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.ldap.common.berlib.asn1.decoder.search;


/**
 * Document this class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class SearchRequestProcessing
{
    // ------------------------------------------------------------------------
    // States used by this instance
    // ------------------------------------------------------------------------

    /** state for filling in the baseObject field */
    public final State BASE_OBJECT_STATE = new BaseObjectState() ;
    
    /** state for filling in the scope field */
    public final State SCOPE_STATE = new ScopeState() ;
    
    /** state for filling in the derefAliases field */
    public final State DEREF_ALIAS_STATE = new DerefAliasState() ;
    
    /** state for filling in the sizeLimit field */
    public final State SIZE_LIMIT_STATE = new SizeLimitState() ;
    
    /** state for filling in the timeLimit field */
    public final State TIME_LIMIT_STATE = new TimeLimitState() ;
    
    /** state for filling in the typesOnly field */
    public final State TYPES_ONLY_STATE = new TypesOnlyState() ;
    
    /** state for filling in the filter field */
    public final State FILTER_STATE = new FilterState() ;
    
    /** state for filling in the attributes field */
    public final State ATTRIBUTES_STATE = new AttributesState() ;
    
    /** state for filling in the controls field */
    public final State CONTROLS_STATE = new ControlsState() ;
    
    /** the current request processing state */
    private State state = BASE_OBJECT_STATE ;

    /** gets the state of SearchRequest processing */
    public State getState()
    {
        return state;
    }

    /** resets state to the start state */
    public void reset()
    {
        state = BASE_OBJECT_STATE ;
    }

    /** sets the state to the next in line or back to the start state */
    public void next()
    {
        state.next() ;
    }

    /**
     * State base class.
     */
    public abstract class State
    {
        protected abstract void next() ;
    }

    /**
     * State class for filling in the baseObject field.
     */
    protected class BaseObjectState extends State
    {
        protected void next()
        {
            state = SCOPE_STATE ;
        }

        public String toString()
        {
            return "BaseObject";
        }
    }

    /**
     * State class for filling in the scope field.
     */
    protected class ScopeState extends State
    {
        protected void next()
        {
            state = DEREF_ALIAS_STATE ;
        }

        public String toString()
        {
            return "Scope";
        }
    }

    /**
     * State class for filling in the derefAliases field.
     */
    protected class DerefAliasState extends State
    {
        protected void next()
        {
            state = SIZE_LIMIT_STATE ;
        }

        public String toString()
        {
            return "DerefAliasState";
        }
    }

    /**
     * State class for filling in the sizeLimit field.
     */
    protected class SizeLimitState extends State
    {
        protected void next()
        {
            state = TIME_LIMIT_STATE ;
        }

        public String toString()
        {
            return "SizeLimit";
        }
    }

    /**
     * State class for filling in the timeLimit field.
     */
    protected class TimeLimitState extends State
    {
        protected void next()
        {
            state = TYPES_ONLY_STATE ;
        }

        public String toString()
        {
            return "TimeLimit";
        }
    }

    /**
     * State class for filling in the typesOnly field.
     */
    protected class TypesOnlyState extends State
    {
        protected void next()
        {
            state = FILTER_STATE ;
        }

        public String toString()
        {
            return "TypesOnly";
        }
    }

    /**
     * State class for filling in the filter.
     */
    protected class FilterState extends State
    {
        protected void next()
        {
            state = ATTRIBUTES_STATE ;
        }

        public String toString()
        {
            return "Filter";
        }
    }

    /**
     * State class for filling in the attributes.
     */
    protected class AttributesState extends State
    {
        protected void next()
        {
            state = CONTROLS_STATE ;
        }

        public String toString()
        {
            return "Attributes";
        }
    }

    /**
     * State class for filling in the controls.
     */
    protected class ControlsState extends State
    {
        protected void next()
        {
            state = BASE_OBJECT_STATE ;
        }

        public String toString()
        {
            return "Controls";
        }
    }
}
