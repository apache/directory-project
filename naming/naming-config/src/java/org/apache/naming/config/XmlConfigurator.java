/*
 * Copyright 2004 The Apache Software Foundation.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import org.apache.naming.ContextBindings;
import org.apache.naming.ContextUtils;
import org.apache.naming.NamingContextFactory;

/**
 * Configure an in-memory JNDI implementation using an XML configuration file.
 * <p>
 * Multiple named contexts can be configured, and names configured in the file
 * can be made relative to a base name.  Use the "base" attribute of the context
 * element in the xml config to specify a base name and use the "name" attribute
 * to specify an external name for the context, which will be its key in 
 * {@link org.apache.naming.ContextBindings}.  Configurations added using 
 * {@link #loadConfiguration} add / update data for contexts already defined.
 * To destroy contexts already defined, use either 
 * {@link #destroyInitialContext()}, which destroys the "anonymous" context
 * (what new InitialContext() returns); or {@link #destroyAll}, which destroys
 * all defined contexts (named or "anonymous").
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Id: XmlConfigurator.java,v 1.2 2003/12/01 02:02:45 brett Exp $
 */
public class XmlConfigurator {
    private static final String COMP_CONTEXT_NAME = "java:comp";
    private static final String ENV_CONTEXT_NAME = "env";
    private static final String ROOT_ELEMENT = "naming";
    private static final String CONTEXT_ELEMENT = ROOT_ELEMENT + "/context";
    private static final String ENV_ELEMENT = CONTEXT_ELEMENT + "/environment";
    private static final String RES_ELEMENT = CONTEXT_ELEMENT + "/resource";
    private static final String LINK_ELEMENT = CONTEXT_ELEMENT + "/link";
    private static final String RES_PARAM_ELEMENT = RES_ELEMENT + "/parameter";
    private static Context envContext = null;

    private static final Log LOG = LogFactory.getLog(XmlConfigurator.class);

    /**
     * Sets up initial context using <code>org.apache.naming.NamingContextFactory</code>.
     * <p>
     * Also creates "env" subcontext in "java:comp" namespace.
     * 
     * @throws NamingException if a NamingException occurs.
     */
    public static synchronized void setupInitialContext()
        throws NamingException {
        setSystemProperties();
        Context initialContext = new InitialContext();
        envContext = initialContext.createSubcontext(
                COMP_CONTEXT_NAME).createSubcontext(ENV_CONTEXT_NAME);
    }

    /**
     * Destroys "anonymous" initial context, destoying subcontexts recursively.
     * 
     * @throws NamingException if a NamingException occurs
     */
    public static synchronized void destroyInitialContext()
        throws NamingException {
        ContextUtils.destroyInitialContext();
    }

    /**
     * Destroys all named contexts as well as "anonymous" context.
     * 
     * @throws NamingException if a naming exception occurs
     */
    public static synchronized void destroyAll() throws NamingException {
        ContextBindings.destroyBoundContexts();
        ContextUtils.destroyInitialContext();
    }

    /**
     * Sets System JNDI properties.
     */
    protected static synchronized void setSystemProperties() {
        System.setProperty(
            Context.INITIAL_CONTEXT_FACTORY,
            "org.apache.naming.NamingContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
    }

    /**
     * Parses input file, returning a <code>Config.Naming</code> structure.
     * 
     * @param inputFile file to be parsed
     * @return  Config.Naming representing the config data in the file
     * @throws ParseException  if an error occurs parsing the file
     */
    protected static synchronized Config.Naming parseFile(
            InputStream inputFile) throws ParseException {
        Digester digester = new Digester();
        digester.addObjectCreate(ROOT_ELEMENT, Config.Naming.class);
        digester.addObjectCreate(CONTEXT_ELEMENT, Config.Context.class);
        digester.addSetProperties(CONTEXT_ELEMENT);
        digester.addSetNext(CONTEXT_ELEMENT, "addContext");
        digester.addObjectCreate(ENV_ELEMENT, Config.Environment.class);
        digester.addSetProperties(ENV_ELEMENT);
        digester.addSetNext(ENV_ELEMENT, "addEnvironment");
        digester.addObjectCreate(LINK_ELEMENT, Config.Link.class);
        digester.addSetProperties(LINK_ELEMENT);
        digester.addSetNext(LINK_ELEMENT, "addLink");
        digester.addObjectCreate(RES_ELEMENT, Config.Resource.class);
        digester.addSetProperties(RES_ELEMENT);
        digester.addSetNext(RES_ELEMENT, "addResource");
        digester.addCallMethod(RES_PARAM_ELEMENT + "", "addParameter", 2);
        digester.addCallParam(RES_PARAM_ELEMENT + "/name", 0);
        digester.addCallParam(RES_PARAM_ELEMENT + "/value", 1);

        try {
            Config.Naming naming = (Config.Naming) digester.parse(inputFile);
            if (naming == null) {
                throw new ParseException(
                    "Unable to find root element '" + ROOT_ELEMENT + "'");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML configuration loaded: " + naming.toString());
            }
            return naming;
        } catch (IOException e) {
            throw new ParseException("Error reading configuration file", e);
        } catch (SAXException e) {
            throw new ParseException("Error reading configuration file", e);
        }
    }

    /**
     * Creates naming contexts based on the configuration data in the
     * input <code>Config.Naming</code> structure.  
     * <p>
     * Uses update semantics -- i.e., contexts / entries that already exist are
     * updated with the data in the input configuration.
     * 
     * @param naming  Config.Naming structure containing the context 
     * configuration data
     * @throws NamingException  if a NamingException occurs
     */
    protected static synchronized void makeContexts(Config.Naming naming)
        throws NamingException {
        
        // Get the map of sets of sorted context names, keyed on external name
        Map sortedContextNames = naming.generateSortedSubcontextNameSet();
        
        // Create named contexts
        Hashtable env = new Hashtable();
        for (Iterator i = naming.getContextList().iterator(); i.hasNext();) {
            env.clear();
            Config.Context ctx = (Config.Context) i.next();
            Context jndiCtx = envContext;
            if (ctx.getName() != null) {
                env.put(NamingContextFactory.NAME, ctx.getName());
            } else {
                env.put( NamingContextFactory.NAME,
                    NamingContextFactory.DEFAULT_NAME);
            }

            if (ctx.getBase() != null) {
                Context initialContext = new InitialContext(env);
                Name nm = initialContext.getNameParser("").parse(ctx.getBase());
                try {
                    envContext = initialContext.createSubcontext(nm.get(0));
                } catch (NameAlreadyBoundException e) {
                    envContext = (Context) initialContext.lookup(nm.get(0));
                }
                jndiCtx = envContext;
                for (int k = 1; k < nm.size(); k++) {
                    try {
                        jndiCtx = jndiCtx.createSubcontext(nm.get(k));
                    } catch (NameAlreadyBoundException e) {
                        jndiCtx = (Context) jndiCtx.lookup(nm.get(k));
                    }
                }
            } else {
                Context initialContext = new InitialContext(env);
                try {
                    envContext = initialContext.createSubcontext(
                            COMP_CONTEXT_NAME).createSubcontext(
                            ENV_CONTEXT_NAME);
                } catch (NameAlreadyBoundException e) {
                    envContext = (Context) initialContext.lookup(
                            COMP_CONTEXT_NAME + "/" + ENV_CONTEXT_NAME);
                }
                jndiCtx = envContext;
            }

            precreateSubcontextTree( jndiCtx, 
                    (Set) sortedContextNames.get( ctx.getName()));

            for (Iterator j = ctx.getEnvironmentList().iterator();
                j.hasNext();) {
                Config.Environment e = (Config.Environment) j.next();
                jndiCtx.rebind(e.getName(), e.createValue());
            }

            for (Iterator j = ctx.getResourceList().iterator(); j.hasNext();) {
                Config.Resource r = (Config.Resource) j.next();
                jndiCtx.rebind(r.getName(), r.createValue());
            }
            
            for (Iterator j = ctx.getLinkList().iterator(); j.hasNext();) {
                Config.Link l = (Config.Link) j.next();
                jndiCtx.rebind(l.getName(), l.createValue());
            }
        }
    }

    /**
     * Loads xml configuration data from <code>inputFile</code>. Uses update
     * semantics -- i.e., does not clear and create new context(s) and
     * bindings,  but adds to current definitions, overwriting previous
     * configuration when contexts or bindings already exist. To clear existing
     * configuration, use {@link #destroyInitialContext} or {@link #destroyAll}.
     * 
     * @param inputFile input xml configuration file
     * @throws NamingException if a NamingException occurs.
     * @throws ParseException if an error occurs parsing the configuration file.
     */
    public static synchronized void loadConfiguration(InputStream inputFile)
        throws NamingException, ParseException {
        // Set system properties
        setSystemProperties();
        // Parse file and create contexts
        makeContexts(parseFile(inputFile));
    }

    /**
     * Creates all subcontexts named in <code>sortedSubcontextNameSet</code>.
     * Some / all subcontexts may already exist. NamingException is only thrown
     * if the names are invalid or required intermediary contexts are missing.
     * 
     * @param ctx context
     * @param sortedSubcontextNameSet set of names to Create
     * @throws NamingException if names are invalid required intermediate
     *  contexts are missing
     */
    private static void precreateSubcontextTree(
        Context ctx,
        Set sortedSubcontextNameSet)
        throws NamingException {
        for (Iterator i = sortedSubcontextNameSet.iterator(); i.hasNext();) {
            String name = (String) i.next();
            try {
                ctx.createSubcontext(name);
            } catch (NameAlreadyBoundException e) {
                // ignore -- already created
            }
        }
    }

}
