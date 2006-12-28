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
package org.safehaus.triplesec.guardian;

import java.io.Serializable;


/**
 * An application permission.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @author Trustin Lee
 * @version $Rev: 71 $, $Date: 2005-11-07 19:11:39 -0500 (Mon, 07 Nov 2005) $
 */
public class Permission implements Comparable, Cloneable, Serializable
{
    private static final long serialVersionUID = -522561010304299861L;

    /** the name of the permission */
    private final String permissionName;
    /** the name of the application this permission is associated with */
    private final String applicationName;
    /** a short description of the permission */
    private final String description;


    /**
     * Creates a new permission instance.
     *
     * @param applicationName the name of the application this permission is associated with
     * @param permissionName the permissionName of the permission
     */
    public Permission( String applicationName, String permissionName )
    {
        this( applicationName, permissionName, null );
    }


    /**
     * Creates a new permission instance with description.
     *
     * @param applicationName the name of the application this permission is associated with
     * @param permissionName the permissionName of the permission
     */
    public Permission( String applicationName, String permissionName, String description )
    {
        if( applicationName == null )
        {
            throw new NullPointerException( "applicationName" );
        }
        if( permissionName == null )
        {
            throw new NullPointerException( "permissionName" );
        }
        if( applicationName.length() == 0 )
        {
            throw new IllegalArgumentException( "applicationName is empty.");
        }
        if( permissionName.length() == 0 )
        {
            throw new IllegalArgumentException( "permissionName is empty.");
        }

        this.permissionName = permissionName;
        this.applicationName = applicationName;
        this.description = description;
    }


    /**
     * Gets the name of this permission.
     *
     * @return the name
     */
    public String getName()
    {
        return permissionName;
    }


    /**
     * Gets the application name this permission is defined for.
     *
     * @return the name of the application.
     */
    public String getApplicationName()
    {
        return applicationName;
    }


    /**
     * Gets the name of this permission.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }


    // ------------------------------------------------------------------------
    // Object Overrides
    // ------------------------------------------------------------------------


    public int hashCode()
    {
        return applicationName.hashCode() ^ permissionName.hashCode();
    }


    public boolean equals( Object that )
    {
        if( this == that )
        {
            return true;
        }
        
        if( that instanceof Permission )
        {
            Permission thatP = ( Permission ) that;
            return this.applicationName.equals( thatP.applicationName ) &&
                    this.permissionName.equals( thatP.permissionName );
        }
        
        return false;
    }


    public int compareTo( Object that )
    {
        Permission thatP = ( Permission ) that;
        int ret = this.applicationName.compareTo( thatP.applicationName );
        if( ret != 0 )
        {
            return ret;
        }
        
        return this.permissionName.compareTo( thatP.permissionName );
    }


    public String toString()
    {
        return "Permission(" + applicationName + ": " + permissionName + ')';
    }


    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            throw new InternalError();
        }
    }
}
