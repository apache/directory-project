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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.LinkRef;
import javax.naming.StringRefAddr;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.naming.ResourceRef;

/**
 * Configuration classes. 
 * 
 * @author <a href="brett@apache.org">Brett Porter</a>
 * @version $Id: Config.java,v 1.2 2003/12/01 02:02:45 brett Exp $
 */
public final class Config
{
    /**
     * Naming context configuration.
     */
    public static final class Naming
    {
        /** list of context configurations */
        private final Collection contextList = new LinkedList();
        
        /**
         *  Adds a new Context configuration to the context list.
         * 
         * @param context Context configuration to add.
         */
        public void addContext(Context context)
        {
            contextList.add(context);
        }
        
        /**
         * Returns the context list.
         * 
         * @return  context list.
         */
        public Collection getContextList()
        {
            return Collections.unmodifiableCollection(contextList);
        }
        
        /**
         * Generates and returns a map, keyed on context name, of sorted sets
         * of all configured names.
         * 
         * @return map of sets of configured names, keyed on context name.
         * @throws InvalidNameException if an invalid name is encountered
         */
        public Map generateSortedSubcontextNameSet() throws InvalidNameException
        {
            Map sortedSubcontextNameMap = new HashMap();
            for (Iterator i = contextList.iterator(); i.hasNext();)
            {
                Set sortedSubcontextNameSet = new TreeSet();
                Context context = (Context) i.next();
                context.addSubContextNames(sortedSubcontextNameSet);
                sortedSubcontextNameMap.put(context.getName(), sortedSubcontextNameSet);
            }
            return UnmodifiableMap.decorate(sortedSubcontextNameMap);
        }
        
        /**
         * Returns a string representation of the context list.
         * 
         * @return context list as a string.
         */
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("contextList", contextList)
            .toString();
        }
    }
    
    /**
     * Configuration for a Context.  Contexts contain lists of 
     * {@link org.apache.naming.config.Config$Environment} entries,
     * {@link org.apache.naming.config.Config$Resource} references, and
     * {@link org.apache.naming.config.Config$Link} links.
     */
    public static final class Context
    {
        /** External name of the context -- key in ContextBindings */
        private String name;
        
        /** Base name relative to which property and resource bindings are set up */
        private String base;
        
        private final Collection environmentList = new LinkedList();
        private final Collection resourceList = new LinkedList();
        private final Collection linkList = new LinkedList();
        
        /**
         * Adds an Environment configuration to the environment list.
         * 
         * @param environment environment configuration to add.
         */
        public void addEnvironment(Environment environment)
        {
            environmentList.add(environment);
        }
        
        /**
         * Adds the subcontext names in this Context to the input set.
         * 
         * @param sortedSubcontextNameSet  set to be augmented with names from
         *  this context.
         * @throws InvalidNameException  if the configured string name of a
         * Resource or Environment in this context is not a valid JNDI name.
         */
        public void addSubContextNames(Set sortedSubcontextNameSet) throws InvalidNameException
        {
            if ((name != null) && !environmentList.iterator().hasNext() && !resourceList.iterator().hasNext())
            {
                sortedSubcontextNameSet.add(name);
            }
            for (Iterator i = environmentList.iterator(); i.hasNext();)
            {
                Environment e = (Environment) i.next();
                CompositeName name = new CompositeName(e.getName());
                addSubContextNames(name, sortedSubcontextNameSet);
            }
            for (Iterator i = resourceList.iterator(); i.hasNext();)
            {
                Resource r = (Resource) i.next();
                CompositeName name = new CompositeName(r.getName());
                addSubContextNames(name, sortedSubcontextNameSet);
            }
        }
        
        private void addSubContextNames(CompositeName name, Set sortedSubcontextNameSet) {
            for (int j = 1; j <= name.size() - 1; j++) {
                sortedSubcontextNameSet.add(name.getPrefix(j).toString());
            }
        }
        
        /**
         * Adds a Resource configuration to the resource list.
         * 
         * @param resource resource configuration to add.
         */
        public void addResource(Resource resource)
        {
            resourceList.add(resource);
        }
        
        /**
         * Adds a Link configuration to the link list.
         * 
         * @param link link configuration to add.
         */
        public void addLink(Link link)
        {
            linkList.add(link);
        }
        
        
        /**
         * Returns the environment list.
         * 
         * @return list of Environment configurations in the Context
         */
        public Collection getEnvironmentList()
        {
            return Collections.unmodifiableCollection(environmentList);
        }
        
        /**
         * Returns the name of this context.
         * 
         * @return context name
         */
        public String getName()
        {
            return name;
        }
        
        /**
         * Sets the name of this context.
         * 
         * @param name  the name
         */
        public void setName(String name)
        {
            this.name = name;
        }
        
        /**
         * Returns the resource list.
         * 
         * @return list of Resource configurations in the Context.
         */
        public Collection getResourceList()
        {
            return Collections.unmodifiableCollection(resourceList);
        }
        
        /**
         * Returns the link list.
         * 
         * @return list of Link configurations in the Context.
         */
        public Collection getLinkList()
        {
            return Collections.unmodifiableCollection(linkList);
        }
        
        /**
         * Returns a string representation of the name, environment list and
         * resource list of this context.
         * 
         * @return string representation of this context.
         */
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("name", name)
            .append("environmentList", environmentList)
            .append("resourceList", resourceList)
            .toString();
        }
        /**
         * @return Returns the base.
         */
        public String getBase() {
            return base;
        }
        
        /**
         * @param base The base to set.
         */
        public void setBase(String base) {
            this.base = base;
        }
        
    }
    
    /**
     * Configuration for an Environment entry.  Environment entries represent
     * JNDI environment properties that take values that are primitive java
     *  types.  The Environment configuration includes the type, the value and
     * the JNDI name as a string, relative to the initial context.
     */
    public static final class Environment
    {
        private String name;
        private String value;
        private String type;
        
        /**
         * Gets the name of this environment.
         * 
         * @return name  the name
         */
        public String getName()
        {
            return name;
        }
        
        /**
         * Sets the name of this environment.
         * 
         * @param name  the name
         */
        public void setName(String name)
        {
            this.name = name;
        }
        
        /**
         * Returns the class name of this environment entry.
         * 
         * @return Environment entry class name
         */
        public String getType()
        {
            return type;
        }
        
        /**
         * Sets the class name of this environment entry.
         * 
         * @param type  class name
         */
        public void setType(String type)
        {
            this.type = type;
        }
        
        /**
         * Returns the value of this environment entry as a String.
         * 
         * @return String representation of the value
         */
        public String getValue()
        {
            return value;
        }
        
        /**
         * Sets the (String) value of this environment entry.
         * 
         * @param value
         */
        public void setValue(String value)
        {
            this.value = value;
        }
        
        /**
         * Returns the JNDI name, type and value of this environment entry as
         * as String.
         * 
         * @return String representation of this environment entry.
         */
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("name", name)
            .append("type", type)
            .append("value", value)
            .toString();
        }
        
        /**
         * Tries to create an instance of type <code>this.type</code> using 
         * <code>this.value</code>. 
         * <p> 
         * Only primitive types are currently supported. Wrapper object
         * <code>valueOf</code> methods are used and runtime exceptions
         *  are not handled.  If <code>this.type</code> is not a primitive type,
         * <code>null</code> is returned.
         * 
         * @return object instance
         */
        public Object createValue()
        //TODO: handle / rethrow exceptions, support more types?
        {
            if (type.equals(String.class.getName()))
            {
                return value;
            }
            else if (type.equals(Boolean.class.getName()))
            {
                return Boolean.valueOf(value);
            }
            else if (type.equals(Integer.class.getName()))
            {
                return Integer.valueOf(value);
            }
            else if (type.equals(Short.class.getName()))
            {
                return Short.valueOf(value);
            }
            else if (type.equals(Character.class.getName()))
            {
                return new Character(value.charAt(0));
            }
            else if (type.equals(Double.class.getName()))
            {
                return Double.valueOf(value);
            }
            else if (type.equals(Float.class.getName()))
            {
                return Float.valueOf(value);
            }
            else if (type.equals(Byte.class.getName()))
            {
                return Byte.valueOf(value);
            }
            else if (type.equals(Long.class.getName()))
            {
                return Long.valueOf(value);
            }
            return null;
        }
    }
    
    /**
     * Configuration for a JNDI resource reference.  Resource references 
     * include the type of the resource, the parameters to be used in creating
     * the resource instance and the JNDI name of the resource as a string, 
     * relative to the initial context.
     */
    public static final class Resource
    {
        private String name;
        private String type;
        private final Map parameters = new HashMap();
        
        /**
         * Adds a name-value pair to the parameters associated with this resource.
         * 
         * @param name parameter name
         * @param value parameter value
         */
        public void addParameter(String name, String value)
        {
            parameters.put(name, value);
        }
        
        /**
         * Returns the name of this resource.
         *
         *  @return name
         */
        public String getName()
        {
            return name;
        }
        
        /**
         * Sets the name of this resource.
         * 
         * @param name name.
         */
        public void setName(String name)
        {
            this.name = name;
        }
        
        /**
         * Returns the parameters associated with this resource as a Map.
         * The keys of the map are the parameter names.
         * 
         * @return parameters
         */
        public Map getParameters()
        {
            return parameters;
        }
        
        /**
         * Returns the type of this resource.
         * 
         * @return class name
         */
        public String getType()
        {
            return type;
        }
        
        /**
         * Sets the type of this resource.
         * 
         * @param type  class name.
         */
        public void setType(String type)
        {
            this.type = type;
        }
        
        /**
         * Creates a {@link ResourceRef} based on the configuration
         * properties of this resource.
         * 
         * @return ResourceRef instance.
         */
        public Object createValue()
        //TODO: exceptions?
        {
            ResourceRef ref = new ResourceRef(type, null, null, null);
            for (Iterator i = parameters.keySet().iterator(); i.hasNext();)
            {
                String name = (String) i.next();
                String value = (String) parameters.get(name);
                ref.add(new StringRefAddr(name, value));
            }
            return ref;
        }
        
        /**
         * Returns the name, type and parameter list as a String.
         * 
         * @return String representation of this resource reference configuration.
         */
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("name", name)
            .append("type", type)
            .append("parameters", parameters)
            .toString();
        }
    }
    
    /**
     * Configuration for a link.
     * <code>name</code> property is the (local) jndi name of the link.
     * <code>rname</code> property is the link content, a jndi URL referencing
     * an entry in an external jndi context.
     */
    public static final class Link {
        private String name;
        private String rname;
        private String context;
        /**
         * @return Returns the (local) name for the link.
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name to set.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return Returns the link content, a jndi URL pointing to an
         * entry in an external naming context
         */
        public String getRname() {
            return rname;
        }

        /**
         * @param rname The rname to set.
         */
        public void setRname(String rname) {
            this.rname = rname;
        }
        
        /**
         * Creates a LinkRef with a jndi or ldap URL as content, based on the external
         * context name and the name of the entry in the external context.
         * Link content is of the form "jndi:context/rname" if context is not null;
         * otherwise just rname is passed to LinkRef constructor.
         * 
         * @return object instance
         */
        public Object createValue() {
            String linkRef = rname;
            if (context != null && context.length() > 0) {
                linkRef="jndi:"+ context + "/" + rname; 
            }  
            return new LinkRef(linkRef);              
        }

        /**
         * @return Returns the name of the external context.
         */
        public String getContext() {
            return context;
        }

        /**
         * @param context sets the external context name.
         */
        public void setContext(String context) {
            this.context = context;
        }

    }
}

