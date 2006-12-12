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


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;


/**
 * A wrapper around the web.xml document allowing for alterations.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class WebappConfiguration
{
    /** the web.xml parsed document */
    Document webxml;
    
    /** filter configuration objects by name */
    Map filters = new HashMap();
    Map servlets = new HashMap();
    Map contextParameters = new HashMap();
    
    
    public WebappConfiguration( Document webxml )
    {
        this.webxml = webxml;
        
        for ( Iterator ii = webxml.getRootElement().elementIterator( "filter" ); ii.hasNext(); /**/ )
        {
            FilterConfiguration filterConfig = new FilterConfiguration( ( Element ) ii.next() );
            filters.put( filterConfig.getFilterName(), filterConfig );
        }

        for ( Iterator ii = webxml.getRootElement().elementIterator( "servlet" ); ii.hasNext(); /**/ )
        {
            ServletConfiguration servletConfig = new ServletConfiguration( ( Element ) ii.next() );
            servlets.put( servletConfig.getServletName(), servletConfig );
        }

        for ( Iterator ii = webxml.getRootElement().elementIterator( "context-param" ); ii.hasNext(); /**/ )
        {
            ContextParameter parameter = new ContextParameter ( ( Element ) ii.next() );
            contextParameters.put( parameter.getName(), parameter );
        }
    }
    
    
    public WebappConfiguration( File webxmlFile ) throws IOException, DocumentException
    {
        this ( XmlUtils.readDocument( webxmlFile.toURL() ) );
    }
    
    
    public WebappConfiguration( String webxmlPath ) throws IOException, DocumentException
    {
        this ( XmlUtils.readDocument( new File( webxmlPath ).toURL() ) );
    }
    
    
    public WebappConfiguration( URL webxmlUrl ) throws IOException, DocumentException
    {
        this ( XmlUtils.readDocument( webxmlUrl ) );
    }
    
    
    public FilterConfiguration getFilterConfiguration( String name )
    {
        return ( FilterConfiguration ) filters.get( name );
    }
    
    
    public ServletConfiguration getServletConfiguration( String name )
    {
        return ( ServletConfiguration ) servlets.get( name );
    }
    
    
    public Iterator getServletConfigurations()
    {
        return servlets.values().iterator();
    }
    
    
    public Iterator getFilterConfigurations()
    {
        return filters.values().iterator();
    }
    
    
    public Iterator getContextParameters()
    {
        return contextParameters.values().iterator();
    }
    
    
    private ContextParameter getContextParameter( String name )
    {
        return ( ContextParameter ) contextParameters.get( name );
    }
    
    
    public String getContextParameterValue( String name )
    {
        return getContextParameter( name ).getValue();
    }
    
    
    public String setContextParameterValue( String name, String value )
    {
        ContextParameter parameter = getContextParameter( name );
        return parameter.setValue( value );
    }


    public String addContextParameter( String name, String value )
    {
        ContextParameter parameter = getContextParameter( name );
        
        if ( parameter == null )
        {
            Element parameterElement = webxml.getRootElement().addElement( "contet-param" );
            parameterElement.addElement( "param-name" ).setText( name );
            parameterElement.addElement( "param-value" );
            parameter = new ContextParameter( parameterElement );
            contextParameters.put( parameter.getName(), parameter );
        }
        
        return parameter.setValue( value );
    }
    
    
    public Document getDocument()
    {
        return webxml;
    }
}
