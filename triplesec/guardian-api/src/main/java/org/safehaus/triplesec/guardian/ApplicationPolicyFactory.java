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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * Connects to {@link ApplicationPolicy} and manages
 * {@link ConnectionDriver}s.
 *
 * @author <a href="mailto:trustin@safehaus.org">Trustin Lee</a>
 * 
 * @version $Rev: 68 $, $Date: 2005-08-28 10:08:31 -0400 (Sun, 28 Aug 2005) $
 *
 */
public abstract class ApplicationPolicyFactory
{
    /**
     * A property key that specifies the maxinum number of retries in case of
     * connection failure in {@link #newInstance(String, Properties)}.
     */
    public static final String RETRY_COUNT = ApplicationPolicyFactory.class.getName() + ".retryCount";
    
    /**
     * A property key that specified the delay in seconds between connection retries
     * in {@link #newInstance(String, Properties)}.
     */
    public static final String RETRY_DELAY = ApplicationPolicyFactory.class.getName() + ".retryDelay";

    /** a static list of registered {@link ConnectionDriver}s */
    private static final List drivers = new ArrayList();


    /**
     * Registers a {@link ConnectionDriver} with this factory.
     *
     * @param driver the {@link ConnectionDriver} being registered
     * @return <tt>true</tt> if and only if the driver is registered
     */
    public static boolean registerDriver( ConnectionDriver driver )
    {
        synchronized( drivers )
        {
            for( Iterator i = drivers.iterator(); i.hasNext(); )
            {
                if( driver.getClass().equals( i.next().getClass() ) )
                {
                    return false;
                }
            }
            
            drivers.add( driver );
        }
        
        return true;
    }


    /**
     * Deregisters all {@link ConnectionDriver}s of the specified
     * <tt>driverClass</tt> type.
     * 
     * @param driverClass the type of {@link ConnectionDriver}s to deregister
     * @return <tt>true</tt> if and only if any drivers are deregistered
     */
    public static boolean deregisterDriver( Class driverClass )
    {
        boolean removed = false;
        synchronized( drivers )
        {
            for( Iterator i = drivers.iterator(); i.hasNext(); )
            {
                if( driverClass.isAssignableFrom( i.next().getClass() ) )
                {
                    i.remove();
                    removed = true;
                }
            }
        }
        
        return removed;
    }


    /**
     * Connects to the {@link ApplicationPolicy} with the specified <tt>urls</tt>
     * and extra connection <tt>info</tt> using an appropriate {@link ConnectionDriver}.
     * <p>
     * URLs are separated by whitespace characters.  This operation tries the specified
     * URLs in random order to distribute server-side load.
     * 
     * @param urls the whitespace-separated URLs of the {@link ApplicationPolicy}
     * @param info the extra information to pass to {@link ConnectionDriver}
     * @return the connected store
     * @throws GuardianException if failed to connect to the store
     */
    public static ApplicationPolicy newInstance( String urls, Properties info ) throws GuardianException
    {
        List urlList = new ArrayList();
        StringTokenizer tk = new StringTokenizer( urls );
        while( tk.hasMoreElements() )
        {
            urlList.add( tk.nextToken() );
        }
        
        Collections.shuffle( urlList );
        
        GuardianException ex = null;
        for( Iterator ui = urlList.iterator(); ui.hasNext(); )
        {
            String url = ( String ) ui.next();
            try
            {
                ex = null;
                return newInstance0( url, info );
            }
            catch( GuardianException e )
            {
                ex = e;
            }
            catch( Throwable t )
            {
                ex = new GuardianException( "Driver exception.", t );
            }
        }
        
        throw ex;
    }
    
    private static ApplicationPolicy newInstance0( String url, Properties info )
    {
        ConnectionDriver driver = null;
        
        synchronized( drivers )
        {
            for( Iterator i = drivers.iterator(); i.hasNext(); )
            {
                ConnectionDriver d = ( ConnectionDriver ) i.next();
                if( d.accept( url ) )
                {
                    driver = d;
                    break;
                }
            }
        }
        
        if( driver == null )
        {
            throw new NoConnectionDriverException( url );
        }
        
        if( info == null )
        {
            info = new Properties();
        }
    
        String retryCountStr = info.getProperty( RETRY_COUNT );
        String retryDelayStr = info.getProperty( RETRY_DELAY );
        int retryCount; 
        int retryDelay;
        
        // Get retryCount
        if( retryCountStr == null )
        {
            retryCount = 0;
        }
        else
        {
            retryCount = Integer.parseInt( retryCountStr );
        }
        
        // Adjust if invalid
        if( retryCount < 0 )
        {
            retryCount = 0;
        }
        
        // Get retryDelay
        if( retryDelayStr == null )
        {
            retryDelay = 1;
        }
        else
        {
            retryDelay = Integer.parseInt( retryDelayStr );
        }
        
        // Adjust if invalid
        if( retryDelay < 0 )
        {
            retryDelay = 0;
        }
        
        // Try to connect
        for( int i = 0;; i++ )
        {
            try
            {
                return driver.newStore( url, info );
            }
            catch( StoreConnectionException e )
            {
                // Propagate exception if exceeded max retryCount.
                if( i >= retryCount )
                {
                    throw e;
                }
                else
                {
                    // or sleep for the next try
                    try
                    {
                        Thread.sleep( retryDelay * 1000L );
                    }
                    catch( InterruptedException e1 )
                    {
                    }
                }
            }
        }        
    }
}
