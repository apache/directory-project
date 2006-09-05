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

package org.apache.directory.shared.ldap.trigger;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;

/**
 * The Trigger Specification Bean.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev:$, $Date:$
 */
public class TriggerSpecification
{
    
    private LdapOperation ldapOperation;
    
    private ActionTime actionTime;
    
    private String storedProcedureName;
    
    private List storedProcedureOptions;
    
    private List storedProcedureParameters;
    
    public TriggerSpecification( LdapOperation ldapOperation, ActionTime actionTime, String storedProcedureName, List storedProcedureOptions, List storedProcedureParameters )
    {
        super();
        if ( ldapOperation == null || 
            actionTime == null || 
            storedProcedureName == null || 
            storedProcedureOptions == null || 
            storedProcedureParameters == null )
        {
            throw new NullArgumentException( "TriggerSpecification cannot be initialized with any NULL argument." );
        }
        this.ldapOperation = ldapOperation;
        this.actionTime = actionTime;
        this.storedProcedureName = storedProcedureName;
        this.storedProcedureOptions = storedProcedureOptions;
        this.storedProcedureParameters = storedProcedureParameters;
    }

    public ActionTime getActionTime()
    {
        return actionTime;
    }

    public LdapOperation getLdapOperation()
    {
        return ldapOperation;
    }

    public String getStoredProcedureName()
    {
        return storedProcedureName;
    }

    public List getStoredProcedureOptions()
    {
        return storedProcedureOptions;
    }

    public List getStoredProcedureParameters()
    {
        return storedProcedureParameters;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( actionTime );
        buffer.append( " " );
        buffer.append( ldapOperation );
        buffer.append( " CALL \"" );
        buffer.append( storedProcedureName );
        buffer.append( "\" { " );
        if ( storedProcedureOptions.size() > 0 )
        {
            Iterator it = storedProcedureOptions.iterator();
            while ( it.hasNext() )
            {
                StoredProcedureOption spOption = ( StoredProcedureOption ) it.next();
                buffer.append( spOption );
                if ( it.hasNext() )
                {
                    buffer.append( ", " );
                }
            }
        }
        buffer.append( " } ( " );
        if ( storedProcedureParameters.size() > 0 )
        {
            Iterator it = storedProcedureParameters.iterator();
            while ( it.hasNext() )
            {
                StoredProcedureParameter spParameter = ( StoredProcedureParameter ) it.next();
                buffer.append( spParameter.toString() );
                if ( it.hasNext() )
                {
                    buffer.append( ", " );
                }
            }
        }
        buffer.append( " )" );
        
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( actionTime == null ) ? 0 : actionTime.hashCode() );
        result = PRIME * result + ( ( ldapOperation == null ) ? 0 : ldapOperation.hashCode() );
        result = PRIME * result + ( ( storedProcedureName == null ) ? 0 : storedProcedureName.hashCode() );
        result = PRIME * result + ( ( storedProcedureOptions == null ) ? 0 : storedProcedureOptions.hashCode() );
        result = PRIME * result + ( ( storedProcedureParameters == null ) ? 0 : storedProcedureParameters.hashCode() );
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        final TriggerSpecification other = ( TriggerSpecification ) obj;
        if ( actionTime == null )
        {
            if ( other.actionTime != null )
                return false;
        }
        else if ( !actionTime.equals( other.actionTime ) )
            return false;
        if ( ldapOperation == null )
        {
            if ( other.ldapOperation != null )
                return false;
        }
        else if ( !ldapOperation.equals( other.ldapOperation ) )
            return false;
        if ( storedProcedureName == null )
        {
            if ( other.storedProcedureName != null )
                return false;
        }
        else if ( !storedProcedureName.equals( other.storedProcedureName ) )
            return false;
        if ( storedProcedureOptions == null )
        {
            if ( other.storedProcedureOptions != null )
                return false;
        }
        else if ( !storedProcedureOptions.equals( other.storedProcedureOptions ) )
            return false;
        if ( storedProcedureParameters == null )
        {
            if ( other.storedProcedureParameters != null )
                return false;
        }
        else if ( !storedProcedureParameters.equals( other.storedProcedureParameters ) )
            return false;
        return true;
    }
    
}
