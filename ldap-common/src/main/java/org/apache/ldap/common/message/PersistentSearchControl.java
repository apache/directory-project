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
package org.apache.ldap.common.message;


/**
 * The control for a persistent search operation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PersistentSearchControl extends ControlImpl
{
    private static final long serialVersionUID = -2356861450876343999L;

    /** 
     * If changesOnly is TRUE, the server MUST NOT return any existing
     * entries that match the search criteria.  Entries are only
     * returned when they are changed (added, modified, deleted, or 
     * subject to a modifyDN operation).
     */
    private boolean changesOnly;

    /**
     * If returnECs is TRUE, the server MUST return an Entry Change 
     * Notification control with each entry returned as the result of
     * changes.
     */
    private boolean returnECs;
    
    /**
     * As changes are made to the server, the effected entries MUST be
     * returned to the client if they match the standard search cri-
     * teria and if the operation that caused the change is included in
     * the changeTypes field.  The changeTypes field is the logical OR
     * of one or more of these values: add (1), delete (2), modify (4),
     * modDN (8).
     */
    private int changeTypes;

    
    public byte[] getEncodedValue()
    {
        return null;
    }


    public void setChangesOnly( boolean changesOnly )
    {
        this.changesOnly = changesOnly;
    }

    
    public boolean isChangesOnly()
    {
        return changesOnly;
    }


    public void setReturnECs( boolean returnECs )
    {
        this.returnECs = returnECs;
    }


    public boolean isReturnECs()
    {
        return returnECs;
    }


    public void setChangeTypes( int changeTypes )
    {
        this.changeTypes = changeTypes;
    }


    public int getChangeTypes()
    {
        return changeTypes;
    }
}
