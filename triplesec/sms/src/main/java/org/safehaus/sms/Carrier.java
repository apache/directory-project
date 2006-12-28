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
package org.safehaus.sms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Supported carriers.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class Carrier
{
    public static final Carrier ATT = new Carrier( "AT&T", 31001 );
    public static final Carrier CINGULAR = new Carrier( "Cingular", 31002 );
    public static final Carrier VERIZON = new Carrier( "Verizon", 31003 );
    public static final Carrier T_MOBILE = new Carrier( "T-Mobile", 31004 );
    public static final Carrier SPRINT = new Carrier( "Sprint", 31005 );
    public static final Carrier NEXTEL = new Carrier( "Nextel", 31007 );
    public static final List ALL_CARRIERS;
    public static final List ALL_CARRIER_STRINGS;
    public static final Map CARRIER_CODE_MAP;
    
    static 
    {
        Map codeMap = new HashMap( 6 );
        List carriers = new ArrayList( 6 );
        List carrierStrings = new ArrayList( 6 );
        
        carriers.add( ATT );
        carrierStrings.add( ATT.toString() );
        codeMap.put( ATT.getName(), new Integer( ATT.getValue()) );
        
        carriers.add( CINGULAR );
        carrierStrings.add( CINGULAR.toString() );
        codeMap.put( CINGULAR.getName(), new Integer( CINGULAR.getValue()) );
        
        carriers.add( VERIZON );
        carrierStrings.add( VERIZON.toString() );
        codeMap.put( VERIZON.getName(), new Integer( VERIZON.getValue()) );
        
        carriers.add( T_MOBILE );
        carrierStrings.add( T_MOBILE.toString() );
        codeMap.put( T_MOBILE.getName(), new Integer( T_MOBILE.getValue()) );
        
        carriers.add( SPRINT );
        carrierStrings.add( SPRINT.toString() );
        codeMap.put( SPRINT.getName(), new Integer( SPRINT.getValue()) );
        
        carriers.add( NEXTEL );
        carrierStrings.add( NEXTEL.toString() );
        codeMap.put( NEXTEL.getName(), new Integer( NEXTEL.getValue()) );
        
        /* add more here */
        
        ALL_CARRIERS = Collections.unmodifiableList( carriers );
        ALL_CARRIER_STRINGS = Collections.unmodifiableList( carrierStrings );
        CARRIER_CODE_MAP = Collections.unmodifiableMap( codeMap );
    }
    
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
    
    
    public static int getCarrierCode( String carrierName )
    {
        Integer integer = ( Integer ) CARRIER_CODE_MAP.get( carrierName );
        if ( integer == null )
        {
            throw new IllegalArgumentException( "Unkown carrier name: " + carrierName );
        }
        
        return integer.intValue();
    }
}
