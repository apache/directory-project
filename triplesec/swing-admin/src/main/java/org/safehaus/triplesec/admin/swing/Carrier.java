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
package org.safehaus.triplesec.admin.swing;


public class Carrier
{
    public static final Carrier ATT = new Carrier( "AT&T", 31001 );
    public static final Carrier CINGULAR = new Carrier( "Cingular", 31002 );
    public static final Carrier VERIZON = new Carrier( "Verizon", 31003 );
    public static final Carrier T_MOBILE = new Carrier( "T-Mobile", 31004 );
    public static final Carrier SPRINT = new Carrier( "Sprint", 31005 );
    public static final Carrier NEXTEL = new Carrier( "Nextel", 31007 );
    public static final Carrier[] CARRIERS = { ATT, CINGULAR, VERIZON, T_MOBILE, SPRINT, NEXTEL };

    private final String name;
    private final int value;
 

    private Carrier( String name, int value )
    {
        this.name = name;
        this.value = value;
    }


    public String getName()
    {
        return name;
    }


    public int getValue()
    {
        return value;
    }
    
    
    public String toString()
    {
        return name;
    }
    
    
    public static Carrier getCarrier( String name )
    {
        if ( name.equalsIgnoreCase( ATT.getName() ) )
        {
            return ATT;
        }
        if ( name.equalsIgnoreCase( CINGULAR.getName() ) )
        {
            return CINGULAR;
        }
        if ( name.equalsIgnoreCase( VERIZON.getName() ) )
        {
            return VERIZON;
        }
        if ( name.equalsIgnoreCase( T_MOBILE.getName() ) )
        {
            return T_MOBILE;
        }
        if ( name.equalsIgnoreCase( SPRINT.getName() ) )
        {
            return SPRINT;
        }
        if ( name.equalsIgnoreCase( NEXTEL.getName() ) )
        {
            return NEXTEL;
        }
        
        throw new IllegalStateException( "Unknown carrier name: " + name );
    }
}
