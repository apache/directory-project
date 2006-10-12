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
package org.apache.directory.mitosis.service.protocol.message;

import org.apache.directory.mitosis.service.protocol.Constants;
import org.apache.directory.shared.ldap.util.EqualsBuilder;
import org.apache.directory.shared.ldap.util.HashCodeBuilder;

public class LogEntryAckMessage extends ResponseMessage
{
    public LogEntryAckMessage( int sequence, int responseCode )
    {
        super(sequence, responseCode);
    }

    public int getType()
    {
        return Constants.LOG_ENTRY_ACK;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) 
    {
        if (object == this) 
        {
            return true;
        }
        
        if (!(object instanceof LogEntryAckMessage)) 
        {
            return false;
        }
        
        return new EqualsBuilder().appendSuper(super.equals(object)).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() 
    {
        return new HashCodeBuilder(-873557437, -1464393829).appendSuper(
                super.hashCode()).toHashCode();
    }
    
    public String toString()
    {
        return "[LogEntryAck] " + super.toString();
    }
}