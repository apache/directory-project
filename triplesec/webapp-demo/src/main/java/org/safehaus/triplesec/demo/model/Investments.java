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
package org.safehaus.triplesec.demo.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;


public class Investments implements Serializable
{
    private static final long serialVersionUID = 144993312912085394L;

    private final String uid;
    private int bonds = 10000;
    private int techStocks = 10000;
    private int volatileHighYield = 10000;
    private int tbills = 10000;
    private int emergingMarkets = 10000;


    /** a constant unmodifiable list of funds */
    public static final List FUNDS;

    /** a constant unmodifiable Map of fund indices */
    public static final Map INDICES;


    static
    {
        ArrayList list = new ArrayList( 5 );
        list.add( "Bonds" );
        list.add( "T-Bills" );
        list.add( "Emerging Markets" );
        list.add( "Tech Stocks" );
        list.add( "Volatile High Yield" );
        FUNDS = Collections.unmodifiableList( list );
        HashMap map = new HashMap( 5 );
        map.put( list.get( 0 ), new Integer( 0 ) );
        map.put( list.get( 1 ), new Integer( 1 ) );
        map.put( list.get( 2 ), new Integer( 2 ) );
        map.put( list.get( 3 ), new Integer( 3 ) );
        map.put( list.get( 4 ), new Integer( 4 ) );
        INDICES = Collections.unmodifiableMap( map );
    }


    public static int getNumberOfFunds()
    {
        return FUNDS.size();
    }


    public static String getFund( int index )
    {
        return ( String ) FUNDS.get( index );
    }


    public static int getIndex( String fund )
    {
        return ( ( Integer ) INDICES.get( fund ) ).intValue();
    }


    public void set( int index, int value )
    {
        switch ( index )
        {
        case( 0 ):
            setBonds( value );
            break;
        case( 1 ):
            setTbills( value );
            break;
        case( 2 ):
            setEmergingMarkets( value );
            break;
        case( 3 ):
            setTechStocks( value );
            break;
        case( 4 ):
            setVolatileHighYield( value );
            break;
        default:
            throw new IndexOutOfBoundsException( "Index value out of bounds: " + index );
        }
    }


    public int get( int index )
    {
        switch ( index )
        {
        case( 0 ):
            return getBonds();
        case( 1 ):
            return getTbills();
        case( 2 ):
            return getEmergingMarkets();
        case( 3 ):
            return getTechStocks();
        case( 4 ):
            return getVolatileHighYield();
        default:
            throw new IndexOutOfBoundsException( "Index value out of bounds: " + index );
        }
    }


    public void set( String fund, int value )
    {
        int index = getIndex( fund );

        switch ( index )
        {
        case( 0 ):
            setBonds( value );
            break;
        case( 1 ):
            setTbills( value );
            break;
        case( 2 ):
            setEmergingMarkets( value );
            break;
        case( 3 ):
            setTechStocks( value );
            break;
        case( 4 ):
            setVolatileHighYield( value );
            break;
        default:
            throw new IndexOutOfBoundsException( "Index value out of bounds: " + index );
        }
    }


    public Investments( String uid )
    {
        this.uid = uid;
    }


    public String getUid()
    {
        return this.uid;
    }


    public void setBonds( int bonds )
    {
        this.bonds = bonds;
    }


    public int getBonds()
    {
        return bonds;
    }


    public void setTechStocks( int techStocks )
    {
        this.techStocks = techStocks;
    }


    public int getTechStocks()
    {
        return techStocks;
    }


    public void setVolatileHighYield( int volatileHighYield )
    {
        this.volatileHighYield = volatileHighYield;
    }


    public int getVolatileHighYield()
    {
        return volatileHighYield;
    }


    public void setTbills( int tbills )
    {
        this.tbills = tbills;
    }


    public int getTbills()
    {
        return tbills;
    }


    public void setEmergingMarkets( int emergingMarkets )
    {
        this.emergingMarkets = emergingMarkets;
    }


    public int getEmergingMarkets()
    {
        return emergingMarkets;
    }
}
