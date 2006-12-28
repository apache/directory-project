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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * Utilities for reading and writing xml files.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class XmlUtils
{
    public static void writeDocument( Document document, File configurationFile, OutputFormat format ) throws IOException
    {
        XMLWriter writer = null;
        
        if ( format == null )
        {
            writer = new XMLWriter( new FileWriter( configurationFile ) );
        }
        else
        {
            writer = new XMLWriter( new FileWriter( configurationFile ), format );
        }
        
        writer.write( document );
        writer.flush();
        writer.close();
    }


    public static void writeDocument( Document document, StringWriter out, OutputFormat format ) throws IOException
    {
        XMLWriter writer = null;
        
        if ( format == null )
        {
            writer = new XMLWriter( out );
        }
        else
        {
            writer = new XMLWriter( out, format );
        }
        
        writer.write( document );
        writer.flush();
        writer.close();
    }
    
    
    public static void writeDocument( Document document, StringWriter out ) throws IOException
    {
    	writeDocument( document, out, null );
    }
    
    
    public static void writeDocument( Document document, File configurationFile ) throws IOException
    {
    	writeDocument( document, configurationFile, null );
    }

    
    public static Document readDocument( URL url ) throws IOException, DocumentException
    {
        Document document = null;
        SAXReader reader = new SAXReader();
        document = reader.read( url );
        return document;
    }
}
