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
package org.safehaus.triplesec.configuration;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;


/**
 * A servlet element wrapper from a web.xml document.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ServletConfiguration
{
    private final Element servlet;
    private final Map parameters = new HashMap();
    

    public ServletConfiguration( Element servlet )
    {
        this.servlet = servlet;
        
        // load all init params 
        for ( Iterator ii = servlet.elementIterator( "init-param" ); ii.hasNext(); /**/ )
        {
            InitParameter parameter = new InitParameter ( ( Element ) ii.next() );
            parameters.put( parameter.getName(), parameter );
        }
    }
    
    
    private InitParameter getInitParameter( String name )
    {
        return ( InitParameter ) parameters.get( name );
    }
    
    
    public String getInitParameterValue( String name )
    {
        return getInitParameter( name ).getValue();
    }
    
    
    public String setInitParameterValue( String name, String value )
    {
        InitParameter parameter = getInitParameter( name );
        return parameter.setValue( value );
    }


    public String addInitParameter( String name, String value )
    {
        InitParameter parameter = getInitParameter( name );
        
        if ( parameter == null )
        {
            Element parameterElement = servlet.addElement( "init-param" );
            parameterElement.addElement( "param-name" ).setText( name );
            parameterElement.addElement( "param-value" );
            parameter = new InitParameter( parameterElement );
            parameters.put( parameter.getName(), parameter );
        }
        
        return parameter.setValue( value );
    }
    
    
    public String getServletName()
    {
        return servlet.elementText( "servlet-name" );
    }
    
    
    public String getServletClass()
    {
        return servlet.elementText( "servlet-class" );
    }


    public boolean hasInitParameter( String name )
    {
        return parameters.containsKey( name );
    }
}

