/*
 * Copyright 2005 The Apache Software Foundation.
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


package org.apache.naming;

import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;

/**
 * Context implementation that delegates JNDI operations to named
 * {@link NamingContext} instances, using names maintained in the
 * {@link ContextBindings}.  The {@link #getBoundContext} method, used to
 * locate the appropriate Context instance for delegation, returns the context
 * referred to by the most recently parsed jndi URL, or the context selected
 * (or created) by the constructor, if no jndi URLs have been parsed.
 * 
 * @version $Revision: 123167 $ $Date: 2003/11/30 05:22:15 $
 */

public class SelectorJNDIContext extends SelectorContext {

    /**
     * Namespace URL.
     */
    private static final String prefix = "jndi:";
    
    /*
     * Context named by the most recently resolved
     * jndi URL.
     */
    private Context context = null;


    /**
     * Namespace URL length.
     */
    private static final int prefixLength = prefix.length();


    /**
     * Builds a selector jndi context using the given environment.
     * <p>
     * If the environment contains a name for the context (under the 
     * key <code>NamingContextFactory.Name</code>), the current context
     * (the context used by jndi operations) is set to the named context.  If 
     * env contains no name entry, the default name, 
     * <code>NamingContextFactory.DEFAULT_NAME</code> is assumed.
     */
    public SelectorJNDIContext(Hashtable env) {
        super(env);
        try {
            context =new NamingContextFactory().getInitialContext(env);
        } catch (NamingException ex) {
            // Should never happen
        }
    }
    
    /**
     * Retrieves the named object.
     * 
     * @param name the name of the object to look up
     * @return the object bound to name
     * @exception NamingException if a naming exception is encountered
     */
    public Object lookup(String name)
    throws NamingException {
        String parsedName = parseName(name);
        return getBoundContext().lookup(parsedName);
    }
    
    /**
     * Enumerates the names bound in the named context, along with the class 
     * names of objects bound to them.
     * 
     * @param name the name of the context to list
     * @return an enumeration of the names and class names of the bindings in 
     * this context. Each element of the enumeration is of type NameClassPair.
     * @exception NamingException if a naming exception is encountered
     */
    public NamingEnumeration list(String name)
    throws NamingException {
        String parsedName = parseName(name);
        return getBoundContext().list(parsedName);
    }
    
    /**
     * Returns the bound context.
     * <p>
     * The bound context is the context named in the URL header of the most
     * recently (successfully) parsed jndi URL.  If no jndi URL has been
     * parsed, the bound context is that set by the constructor.  See
     * {@link #SelectorJNDIContext(Hashtable)}.
     * 
     * @return The bound context
     */
    protected Context getBoundContext()
        throws NamingException {
        
        return context;
    }
    
    
    protected void updateContext(String name) throws NamingException {
        Hashtable env = new Hashtable();
        env.put(NamingContextFactory.NAME, name);
        context = new NamingContextFactory().getInitialContext(env);
    }


    /**
     * If a "jndi:" URL header is present, the first component of the name
     * following the URL header is assumed to be the name of a bound context
     * in {@link ContextBindings}.  For example, "jndi:global/config/host" names
     * the "config/host" entry in the context bound under the name "global" in
     * <code>ContextBindings</code>.  If the header is present, but the named
     * context does not exist, a <code>NamingException</code> is thrown.
     * If the named context does exist, the current context is changed to refer
     * to this context and the name is returned with the URL header and context
     * name removed.  In the example above, "config/host" would be returned 
     * (assuming "global" is bound to a context).  If there is no header
     * present, <code>name</code> is returned unchanged.
     * 
     * @param name The name to parse
     * @return the parsed name
     * @exception NamingException if the jndi URL header is present, but the
     * first component of the name following the header does not correspond to
     * a bound context
     */
    protected String parseName(String name) 
        throws NamingException {
        
        if ((name.startsWith(prefix))) {
            String suffix = name.substring(prefixLength);
            CompositeName cname = new CompositeName(suffix);
            String contextName = cname.get(0);
            Context ctx = ContextBindings.getContext(contextName);
            if (ctx == null) {
                throw new NamingException(sm.getString
                        ("contextBindings.unknownContext", contextName));
            } else {
                setContext(ctx); 
            }
            return cname.getSuffix(1).toString();
        } else {
            return name;
        }
    }

     /**
     * If a "jndi:" URL header is present, the first component of the name
     * following the URL header is assumed to be the name of a bound context
     * in {@link ContextBindings}.  For example, "jndi:global/config/host" names
     * the "config/host" entry in the context bound under the name "global" in
     * <code>ContextBindings</code>.  If the header is present, but the named
     * context does not exist, a <code>NamingException</code> is thrown.
     * If the named context does exist, the current context is changed to refer
     * to this context and the name is returned with the URL header and context
     * name removed.  In the example above, "config/host" would be returned 
     * (assuming "global" is bound to a context).  If there is no header
     * present, <code>name</code> is returned unchanged.
     * 
     * @param name The name to parse
     * @return The parsed name
     * @exception NamingException if this is not an initial context and there
     * is no "java:" URL header
     */
    protected Name parseName(Name name) 
        throws NamingException {

	if ((!name.isEmpty()) 
            && (name.get(0).equals(prefix))) {   
	        updateContext(name.get(1));
            return (name.getSuffix(1));
        } else {
                return name;
            }
        }

    /**
     * Sets underlying context
     * 
     * @param context The context to set.
     */
    protected void setContext(Context context) {
        this.context = context;
    }

}

