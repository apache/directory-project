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


import org.dom4j.Element;


/**
 * A wrapper around a filter initialization parameter element in a 
 * web.xml document.
 *
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class InitParameter
{
    private final Element parameter;

    
    public InitParameter( Element parameter )
    {
        this.parameter = parameter;
    }
    
    
    public String getName()
    {
        return this.parameter.elementTextTrim( "param-name" );
    }
    
    
    public String getValue()
    {
        return this.parameter.elementTextTrim( "param-value" );
    }
    
    
    public String setValue( String value )
    {
        String old = getValue();
        this.parameter.element( "param-value" ).setText( value );
        return old;
    }
}
