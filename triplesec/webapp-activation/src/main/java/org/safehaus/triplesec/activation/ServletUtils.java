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
package org.safehaus.triplesec.activation;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Useful servlet utilities.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class ServletUtils 
{
	public static void printRequest( HttpServletRequest req, PrintWriter out )
	{
		out.println( "-[characterEncoding: " + req.getCharacterEncoding() + "]-" );
		out.println( "-[contentLength: " + req.getContentLength() + "]-" );
		out.println( "-[contentType: " + req.getContentType() + "]-" );
		out.println( "-[contentPath: " + req.getContextPath() + "]-" );
		out.println( "-[method: " + req.getMethod() + "]-" );
		out.println( "-[pathInfo: " + req.getPathInfo() + "]-" );
		out.println( "-[pathTranslated: " + req.getPathTranslated() + "]-" );
		out.println( "-[protocol: " + req.getProtocol() + "]-" );
		out.println( "-[queryString: " + req.getQueryString() + "]-" );
		out.println( "-[remoteAddr" + req.getRemoteAddr() + "]-" );
		out.println( "-[remoteHost: " + req.getRemoteHost() + "]-" );
		out.println( "-[remotePort: " + req.getRemotePort() + "]-" );
		out.println( "-[remoteUser: " + req.getRemoteUser() + "]-" );
		out.println( "-[requestedSessionId: " + req.getRequestedSessionId() + "]-" );
		out.println( "-[requestURI: " + req.getRequestURI() + "]-" );
		out.println( "-[requestURL: " + req.getRequestURL() + "]-" );
		out.println( "-[scheme: " + req.getScheme() + "]-" );
		out.println( "-[serverName: " + req.getServerName() + "]-" );
		out.println( "-[serverPort: " + req.getServerPort() + "]-" );
		out.println( "-[servletPath: " + req.getServletPath() + "]-" );
		out.println( "-[locale: " + req.getLocale() + "]-" );

		Enumeration list = req.getAttributeNames();
		out.println( "--> requestAttributes <-- " );
		while ( list.hasMoreElements() )
		{
			String name = (String) list.nextElement();
			out.println( "\t-[" + name + ": " + req.getAttribute( name ) + "]-" );
		}
		
		list = req.getParameterNames();
		out.println( "--> parameters <-- " );
		while ( list.hasMoreElements() )
		{
			String name = (String) list.nextElement();
			out.println( "\t-[" + name + ": " + req.getParameter( name ) + "]-" );
		}
		
		list = req.getHeaderNames();
		out.println( "--> headers <-- " );
		while ( list.hasMoreElements() )
		{
			String name = (String) list.nextElement();
			out.println( "\t-[" + name + ": " + req.getHeader( name ) + "]-" );
		}
	}
	
    
    public static String getNotNull( HttpServletRequest req, HttpServletResponse resp, String param ) throws ServletException, IOException
    {
        String value = req.getParameter( param );

        if ( value == null )
        {
            String msg = "error: value for parameter " + param + " was not found";
            resp.getWriter().print( msg );
            resp.getWriter().close();
            throw new ServletException( msg );
        }
        else
        {
            return value;
        }
    }
}
