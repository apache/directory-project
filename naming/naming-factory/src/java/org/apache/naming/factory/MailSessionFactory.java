/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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

package org.apache.naming.factory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * Factory class that creates a JNDI named JavaMail Session factory,
 * which can be used for managing inbound and outbound electronic mail
 * messages via JavaMail APIs.  All messaging environment properties
 * described in the JavaMail Specification may be passed to the Session
 * factory.
 * <p>
 * This factory can be configured using the 
 * {@link org.apache.naming.config.XmlConfigurator}.
 *  An example of factory configuration is:</p>
 * <pre>
 * &lt;resource name="mail/smtp" auth="CONTAINER"
 *           type="javax.mail.Session"&gt;
 *   &lt;parameter&gt;
 *     &lt;name&gt;factory&lt;/name&gt;
 *     &lt;value&gt;org.apache.naming.factory.MailSessionFactory&lt;/value&gt;
 *   &lt;/parameter&gt;
 *   &lt;parameter&gt;
 *     &lt;name&gt;mail.smtp.host&lt;/name&gt;
 *     &lt;value&gt;mail.mycompany.com&lt;/value&gt;
 *   &lt;/parameter&gt;
 * &lt;/resource&gt;
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Revision$ $Date: 2003/10/13 08:15:11 $
 */

public class MailSessionFactory implements ObjectFactory {


    /**
     * The Java type for which this factory knows how to create objects.
     */
    protected static final String factoryType = "javax.mail.Session";


    /**
     * Create and return an object instance based on the specified
     * characteristics.
     *
     * @param refObj Reference information containing our parameters, or null
     *  if there are no parameters
     * @param name The name of this object, relative to context, or null
     *  if there is no name
     * @param context The context to which name is relative, or null if name
     *  is relative to the default initial context
     * @param env Environment variables, or null if there are none
     *
     * @exception Exception if an error occurs during object creation
     */
    public Object getObjectInstance(Object refObj, Name name, Context context,
				    Hashtable env) throws Exception 
    {

        // Return null if we cannot create an object of the requested type
	final Reference ref = (Reference) refObj;
        if (!ref.getClassName().equals(factoryType))
            return (null);

        // Create a new Session inside a doPrivileged block, so that JavaMail
        // can read its default properties without throwing Security
        // exceptions
        return AccessController.doPrivileged( new PrivilegedAction() {
        public Object run() {
                    String password = null;

                    // Create the JavaMail properties we will use
                    Properties props = new Properties();
                    props.put("mail.transport.protocol", "smtp");
                    props.put("mail.smtp.host", "localhost");
                    Enumeration attrs = ref.getAll();
                    while (attrs.hasMoreElements()) {
                        RefAddr attr = (RefAddr) attrs.nextElement();
                        if ("factory".equals(attr.getType()))
                            continue;
                        if ("password".equals(attr.getType())) {
                            password = (String)attr.getContent();
                            continue;
                        }
                        props.put(attr.getType(), (String) attr.getContent());
                    }
                    
                    Authenticator auth = null;
                    
                    // Check for authentication
                    if (password != null) {
                        String user = props.getProperty("mail.smtp.user");
                        if (user == null)
                            user = props.getProperty("mail.user");
                        if (user != null) {
                            final PasswordAuthentication pa = new PasswordAuthentication(user, password);
                            
                            auth = new Authenticator(){
                                protected PasswordAuthentication getPasswordAuthentication()
                                {
                                    return pa;
                                }
                            };
                        }
                    }
                    // Create and return the new Session object
                    Session session = Session.getInstance(props, auth);
                    return (session);

        }
        } );

    }


}
