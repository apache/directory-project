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
package org.safehaus.triplesec.admin.dao;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.safehaus.triplesec.admin.ConfigurationException;
import org.safehaus.triplesec.admin.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class DaoFactory
{
    public static final String IMPLEMENTATION_CLASS = DaoFactory.class.getName();

    private static final Class[] CONSTRUCTOR_TYPES = new Class[] { Properties.class };
    private static final Logger log = LoggerFactory.getLogger( DaoFactory.class );

    
    public static DaoFactory createInstance( Properties props ) throws DataAccessException
    {
        // -------------------------------------------------------------------
        // Error check settings
        // -------------------------------------------------------------------
        
        if ( !props.containsKey( IMPLEMENTATION_CLASS ) )
        {
            String msg = "Implementation class property not found: " + IMPLEMENTATION_CLASS;
            log.error( msg );
            throw new ConfigurationException( msg );
        }
        String className = null;
        className = props.getProperty( IMPLEMENTATION_CLASS );
        if ( className == null || className.length() == 0 )
        {
            String msg = "No value for implementation class property: " + IMPLEMENTATION_CLASS;
            log.error( msg );
            throw new ConfigurationException( msg );
        }
        
        // -------------------------------------------------------------------
        // Lookup the implementation class
        // -------------------------------------------------------------------
        
        Class clazz = null;
        try
        {
            clazz = Class.forName( className );
        }
        catch ( ClassNotFoundException e )
        {
            String msg = "Failed to find dao factory implementation class";
            log.error( msg, e );
            throw new ConfigurationException( msg );
        }
        
        // -------------------------------------------------------------------
        // Lookup the constructor which takes Properties argument
        // -------------------------------------------------------------------
        
        Constructor constructor = null;
        try
        {
            constructor = clazz.getConstructor( CONSTRUCTOR_TYPES );
        }
        catch ( SecurityException e )
        {
            String msg = "Lack perms to reflect on constructor";
            log.error( msg, e );
            throw new ConfigurationException( msg );
        }
        catch ( NoSuchMethodException e )
        {
            String msg = "No constructor with single Properties argument.";
            log.error( msg, e );
            throw new ConfigurationException( msg );
        }

        // -------------------------------------------------------------------
        // Create new instance of DAO
        // -------------------------------------------------------------------
        
        try
        {
            return ( DaoFactory ) constructor.newInstance( new Object[] { props } );
        }
        catch ( IllegalArgumentException e )
        {
            // should never happen
            String msg = "Impropert argument type";
            log.error( msg, e );
            throw new IllegalStateException( msg );
        }
        catch ( InstantiationException e )
        {
            String msg = "Implementation class " + className 
                + " must be a concrete class to instantiate it";
            log.error( msg, e );
            throw new ConfigurationException( msg );
        }
        catch ( IllegalAccessException e )
        {
            String msg = className + " constructor must be public";
            log.error( msg, e );
            throw new ConfigurationException( msg );
        }
        catch ( InvocationTargetException e )
        {
            String msg = className + "'s constructor threw an exception during instantiation";
            log.error( msg, e );
            
            if ( e.getCause() instanceof DataAccessException )
            {
                throw ( DataAccessException ) e.getCause();
            }
            
            throw new ConfigurationException( msg );
        }
    }
    
    
    public abstract PermissionDao getPermissionDao() throws DataAccessException;
    
    public abstract ApplicationDao getApplicationDao() throws DataAccessException;

    public abstract RoleDao getRoleDao() throws DataAccessException;

    public abstract ProfileDao getProfileDao() throws DataAccessException;

    public abstract GroupDao getGroupDao() throws DataAccessException;

    public abstract ExternalUserDao getExternalUserDao() throws DataAccessException;
    
    public abstract LocalUserDao getLocalUserDao() throws DataAccessException;
    
    public abstract HauskeysUserDao getHauskeysUserDao() throws DataAccessException;
    
    public abstract UserDao getUserDao() throws DataAccessException;

    public abstract void close();
}
