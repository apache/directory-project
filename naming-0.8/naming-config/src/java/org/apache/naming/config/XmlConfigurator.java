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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

/**
 * Configure an in-memory JNDI implementation using an XML configuration file.
 * 
 * @author <a href="brett@apache.org">Brett Porter</a>
 * @version $Id: XmlConfigurator.java,v 1.2 2003/12/01 02:02:45 brett Exp $
 */
public class XmlConfigurator
{
    private static final String COMP_CONTEXT_NAME = "java:comp";
    private static final String ENV_CONTEXT_NAME = "env";
    private static final String ROOT_ELEMENT = "naming";
    private static final String CONTEXT_ELEMENT = ROOT_ELEMENT + "/context";
    private static final String ENV_ELEMENT = CONTEXT_ELEMENT + "/environment";
    private static final String RES_ELEMENT = CONTEXT_ELEMENT + "/resource";
    private static final String RES_PARAM_ELEMENT = RES_ELEMENT + "/parameter";
    private static Context envContext = null;

    private static final Log LOG = LogFactory.getLog(XmlConfigurator.class);

    /**
     * Sets up initial context using 
     * <code>org.apache.naming.java.javaURLContextFactory</code>.
     * <p>
     * Also creates "env" subcontext in "java:comp" namespace.
     * 
     * @throws NamingException if a NamingException occurs.
     */
    public static synchronized void setupInitialContext() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

        Context initialContext = new InitialContext();
        envContext = initialContext.createSubcontext(COMP_CONTEXT_NAME).createSubcontext(ENV_CONTEXT_NAME);
    }

    /**
     * Destroys initial context.
     * <p>
     * Invokes <code>Context.destroySubcontext(Name)</code> only on top-level
     * subcontexts.
     * 
     * @throws NamingException if a NamingException occurs.
     */
    public static synchronized void destroyInitialContext() throws NamingException {
        Context initialContext = new InitialContext();
        NamingEnumeration contexts = initialContext.list("");
        while (contexts.hasMore()) {
            initialContext.destroySubcontext(((NameClassPair) contexts.next()).getName());          
        }
        envContext = null;    
        initialContext = null;
    }

    /**
     * Loads xml configuration data from <code>inputFile</code> into initial context.
     * 
     * @param inputFile  input xml configuration file
     * @throws NamingException if a NamingException occurs.
     * @throws ParseException if an error occurs parsing the configuration file.
     */
    public static synchronized void loadConfiguration(InputStream inputFile) throws NamingException, ParseException {
        if (envContext == null)
        {
            setupInitialContext();
        }

        Digester digester = new Digester();
// TODO: string constants
        digester.addObjectCreate(ROOT_ELEMENT, Config.Naming.class);
        digester.addObjectCreate(CONTEXT_ELEMENT, Config.Context.class);
        digester.addSetProperties(CONTEXT_ELEMENT);
        digester.addSetNext(CONTEXT_ELEMENT, "addContext");
        // TODO: handle context inside context?
        digester.addObjectCreate(ENV_ELEMENT, Config.Environment.class);
        digester.addSetProperties(ENV_ELEMENT);
        digester.addSetNext(ENV_ELEMENT, "addEnvironment");
        digester.addObjectCreate(RES_ELEMENT, Config.Resource.class);
        digester.addSetProperties(RES_ELEMENT);
        digester.addSetNext(RES_ELEMENT, "addResource");
        digester.addCallMethod(RES_PARAM_ELEMENT + "", "addParameter", 2);
        digester.addCallParam(RES_PARAM_ELEMENT + "/name", 0);
        digester.addCallParam(RES_PARAM_ELEMENT + "/value", 1);

        try
        {
            Config.Naming naming = (Config.Naming) digester.parse(inputFile);
            if (naming == null) {
                throw new ParseException("Unable to find root element '" + ROOT_ELEMENT + "'");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("XML configuration loaded: " + naming.toString());
            }


            for (Iterator i = naming.getContextList().iterator(); i.hasNext();)
            {
                Config.Context ctx = (Config.Context) i.next();
                Context jndiCtx = envContext;
                if (ctx.getName() != null)
                {
                    destroyInitialContext();
                    Context initialContext = new InitialContext();
                    Name nm = initialContext.getNameParser("").parse(ctx.getName());
                    envContext = initialContext.createSubcontext(nm.get(0));
                    jndiCtx = envContext;
                    for (int k = 1; k < nm.size(); k++) {
                        jndiCtx = jndiCtx.createSubcontext(nm.get(k));
                    }
                }
                precreateSubcontextTree(jndiCtx, naming.generateSortedSubcontextNameSet());

                for (Iterator j = ctx.getEnvironmentList().iterator(); j.hasNext();)
                {
                    Config.Environment e = (Config.Environment) j.next();
                    jndiCtx.rebind(e.getName(), e.createValue());
                }

                for (Iterator j = ctx.getResourceList().iterator(); j.hasNext();)
                {
                    Config.Resource r = (Config.Resource) j.next();
                    jndiCtx.bind(r.getName(), r.createValue());
                }
            }
        }
        catch (IOException e)
        {
            throw new ParseException("Error reading configuration file", e);
        }
        catch (SAXException e)
        {
            throw new ParseException("Error reading configuration file", e);
        }
    }

    private static void precreateSubcontextTree(Context ctx, Set sortedSubcontextNameSet) throws NamingException
    {
        // TODO: don't recreate
        for (Iterator i = sortedSubcontextNameSet.iterator(); i.hasNext();)
        {
            String name = (String) i.next();
            ctx.createSubcontext(name);
        }
    }

}
