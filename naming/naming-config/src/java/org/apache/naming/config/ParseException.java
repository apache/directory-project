/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.naming.config;

/**
 * Exception during reading of an XML configuration file.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Id: ParseException.java,v 1.1 2003/11/14 20:43:26 brett Exp $
 */
public class ParseException extends Exception
{
    ParseException(String msg) {
        super(msg);
    }

    ParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
