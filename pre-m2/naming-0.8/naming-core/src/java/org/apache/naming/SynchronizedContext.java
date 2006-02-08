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


package org.apache.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * 
 * Synchronized JNDI context implementation.  Wraps a Context instance,
 * synchronizing access to JNDI methods.  
 *
 */
public class SynchronizedContext implements Context {

    /** Environment property controlling synchronization */
    public static final String SYNCHRONIZED = 
        "org.apache.naming.synchronization";

    /** Underlying JNDI Context */
    private Context context;

    /** 
     * Creates a new SynchronizedContext based on the given Context.
     * 
     * @param context  underlying JNDI context
     */
    public SynchronizedContext(Context context) {
        this.context = context;
    }

    /**
     * Retrieves the named object. If name is empty, returns a new instance 
     * of this context (which represents the same naming context as this 
     * context, but its environment may be modified independently and it may 
     * be accessed concurrently).
     * <p>
     * Like other methods, this method delegates to the underlying Context
     * instance.  If a new context is returned, whether or not the returned
     * context will be Synchronized therefore depends on the type of the
     * Context (and the value of its {@link #SYNCHRONIZED} environment
     * property).
     * 
     * @param name the name of the object to look up
     * @return the object bound to name
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized Object lookup(Name name)
        throws NamingException {
        return this.context.lookup(name);
    }

    /**
     * Retrieves the named object.
     * 
     * @param name the name of the object to look up
     * @return the object bound to name
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized Object lookup(String name)
        throws NamingException {
        return this.context.lookup(name);
    }

    /**
     * Binds a name to an object. All intermediate contexts and the target 
     * context (that named by all but terminal atomic component of the name) 
     * must already exist.
     * 
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @exception NameAlreadyBoundException if name is already bound
     * @exception InvalidAttributesException if object did not supply all 
     * mandatory attributes
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void bind(Name name, Object obj)
        throws NamingException {
        this.context.bind(name, obj);
    }

    /**
     * Binds a name to an object.
     * 
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @exception NameAlreadyBoundException if name is already bound
     * @exception InvalidAttributesException if object did not supply all 
     * mandatory attributes
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void bind(String name, Object obj)
        throws NamingException {
        this.context.bind(name, obj);
    }

    /**
     * Binds a name to an object, overwriting any existing binding. All 
     * intermediate contexts and the target context (that named by all but 
     * terminal atomic component of the name) must already exist.
     * <p>
     * If the object is a DirContext, any existing attributes associated with 
     * the name are replaced with those of the object. Otherwise, any 
     * existing attributes associated with the name remain unchanged.
     * 
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @exception InvalidAttributesException if object did not supply all 
     * mandatory attributes
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void rebind(Name name, Object obj)
        throws NamingException {
        this.context.rebind(name, obj);
    }

    /**
     * Binds a name to an object, overwriting any existing binding.
     * 
     * @param name the name to bind; may not be empty
     * @param obj the object to bind; possibly null
     * @exception InvalidAttributesException if object did not supply all 
     * mandatory attributes
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void rebind(String name, Object obj)
        throws NamingException {
        this.context.rebind(name, obj);
    }

    /**
     * Unbinds the named object. Removes the terminal atomic name in name 
     * from the target context--that named by all but the terminal atomic 
     * part of name.
     * <p>
     * This method is idempotent. It succeeds even if the terminal atomic 
     * name is not bound in the target context, but throws 
     * NameNotFoundException if any of the intermediate contexts do not exist. 
     * 
     * @param name the name to bind; may not be empty
     * @exception NameNotFoundException if an intermediate context does not 
     * exist
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void unbind(Name name)
        throws NamingException {
        this.context.unbind(name);
    }

    /**
     * Unbinds the named object.
     * 
     * @param name the name to bind; may not be empty
     * @exception NameNotFoundException if an intermediate context does not 
     * exist
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void unbind(String name)
        throws NamingException {
        this.context.unbind(name);
    }

    /**
     * Binds a new name to the object bound to an old name, and unbinds the 
     * old name. Both names are relative to this context. Any attributes 
     * associated with the old name become associated with the new name. 
     * Intermediate contexts of the old name are not changed.
     * 
     * @param oldName the name of the existing binding; may not be empty
     * @param newName the name of the new binding; may not be empty
     * @exception NameAlreadyBoundException if newName is already bound
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void rename(Name oldName, Name newName)
        throws NamingException {
        this.context.rename(oldName, newName);
    }

    /**
     * Binds a new name to the object bound to an old name, and unbinds the 
     * old name.
     * 
     * @param oldName the name of the existing binding; may not be empty
     * @param newName the name of the new binding; may not be empty
     * @exception NameAlreadyBoundException if newName is already bound
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized void rename(String oldName, String newName)
        throws NamingException {
        this.context.rename(oldName, newName);
    }

    /**
     * Enumerates the names bound in the named context, along with the class 
     * names of objects bound to them. The contents of any subcontexts are 
     * not included.
     * <p>
     * If a binding is added to or removed from this context, its effect on 
     * an enumeration previously returned is undefined.
     * 
     * @param name the name of the context to list
     * @return an enumeration of the names and class names of the bindings in 
     * this context. Each element of the enumeration is of type NameClassPair.
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized NamingEnumeration list(Name name)
        throws NamingException {
        return this.context.list(name);
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
    public synchronized NamingEnumeration list(String name)
        throws NamingException {
        return this.context.list(name);
    }

    /**
     * Enumerates the names bound in the named context, along with the 
     * objects bound to them. The contents of any subcontexts are not 
     * included.
     * <p>
     * If a binding is added to or removed from this context, its effect on 
     * an enumeration previously returned is undefined.
     * 
     * @param name the name of the context to list
     * @return an enumeration of the bindings in this context. 
     * Each element of the enumeration is of type Binding.
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized NamingEnumeration listBindings(Name name)
        throws NamingException {
        return this.context.listBindings(name);
    }

    /**
     * Enumerates the names bound in the named context, along with the 
     * objects bound to them.
     * 
     * @param name the name of the context to list
     * @return an enumeration of the bindings in this context. 
     * Each element of the enumeration is of type Binding.
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized NamingEnumeration listBindings(String name)
        throws NamingException {
        return this.context.listBindings(name);
    }

    /**
     * Destroys the named context and removes it from the namespace. Any 
     * attributes associated with the name are also removed. Intermediate 
     * contexts are not destroyed.
     * <p>
     * This method is idempotent. It succeeds even if the terminal atomic 
     * name is not bound in the target context, but throws 
     * NameNotFoundException if any of the intermediate contexts do not exist. 
     * 
     * In a federated naming system, a context from one naming system may be 
     * bound to a name in another. One can subsequently look up and perform 
     * operations on the foreign context using a composite name. However, an 
     * attempt destroy the context using this composite name will fail with 
     * NotContextException, because the foreign context is not a "subcontext" 
     * of the context in which it is bound. Instead, use unbind() to remove 
     * the binding of the foreign context. Destroying the foreign context 
     * requires that the destroySubcontext() be performed on a context from 
     * the foreign context's "native" naming system.
     * 
     * @param name the name of the context to be destroyed; may not be empty
     * @exception NameNotFoundException if an intermediate context does not 
     * exist
     * @exception NotContextException if the name is bound but does not name 
     * a context, or does not name a context of the appropriate type
     */
    public synchronized void destroySubcontext(Name name)
        throws NamingException {
        this.context.destroySubcontext(name);
    }

    /**
     * Destroys the named context and removes it from the namespace.
     * 
     * @param name the name of the context to be destroyed; may not be empty
     * @exception NameNotFoundException if an intermediate context does not 
     * exist
     * @exception NotContextException if the name is bound but does not name 
     * a context, or does not name a context of the appropriate type
     */
    public synchronized void destroySubcontext(String name)
        throws NamingException {
        this.context.destroySubcontext(name);
    }

    /**
     * Creates and binds a new context. Creates a new context with the given 
     * name and binds it in the target context (that named by all but 
     * terminal atomic component of the name). All intermediate contexts and 
     * the target context must already exist.
     * <p>
     * Like other methods, this method delegates to the underlying Context
     * instance.  Whether or not the newly created context will be Synchronized
     * therefore depends on the type of the Context (and the value of its
     * {@link #SYNCHRONIZED} environment property).
     * 
     * @param name the name of the context to create; may not be empty
     * @return the newly created context
     * @exception NameAlreadyBoundException if name is already bound
     * @exception InvalidAttributesException if creation of the subcontext 
     * requires specification of mandatory attributes
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized Context createSubcontext(Name name)
        throws NamingException {
        return this.context.createSubcontext(name);
    }

    /**
     * Creates and binds a new context.
     * <p>
     * Like other methods, this method delegates to the underlying Context
     * instance.  Whether or not the newly created context will be Synchronized
     * therefore depends on the type of the Context (and the value of its
     * {@link #SYNCHRONIZED} environment property).
     * 
     * @param name the name of the context to create; may not be empty
     * @return the newly created context
     * @exception NameAlreadyBoundException if name is already bound
     * @exception InvalidAttributesException if creation of the subcontext 
     * requires specification of mandatory attributes
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized Context createSubcontext(String name)
        throws NamingException {
        return this.context.createSubcontext(name);
    }

    /**
     * Retrieves the named object, following links except for the terminal 
     * atomic component of the name. If the object bound to name is not a 
     * link, returns the object itself.
     * 
     * @param name the name of the object to look up
     * @return the object bound to name, not following the terminal link 
     * (if any).
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized Object lookupLink(Name name)
        throws NamingException {
        return this.context.lookupLink(name);
    }

    /**
     * Retrieves the named object, following links except for the terminal 
     * atomic component of the name.
     * 
     * @param name the name of the object to look up
     * @return the object bound to name, not following the terminal link 
     * (if any).
     * @exception NamingException if a naming exception is encountered
     */
    public synchronized Object lookupLink(String name)
        throws NamingException {
        return this.context.lookupLink(name);
    }

    /**
     * Retrieves the parser associated with the named context. In a 
     * federation of namespaces, different naming systems will parse names 
     * differently. This method allows an application to get a parser for 
     * parsing names into their atomic components using the naming convention 
     * of a particular naming system. Within any single naming system, 
     * NameParser objects returned by this method must be equal (using the 
     * equals() test).
     * 
     * @param name the name of the context from which to get the parser
     * @return a name parser that can parse compound names into their atomic 
     * components
     * @exception NamingException if a naming exception is encountered
     */
    public NameParser getNameParser(Name name)
        throws NamingException {
        return this.context.getNameParser(name);
    }

    /**
     * Retrieves the parser associated with the named context.
     * 
     * @param name the name of the context from which to get the parser
     * @return a name parser that can parse compound names into their atomic 
     * components
     * @exception NamingException if a naming exception is encountered
     */
    public NameParser getNameParser(String name)
        throws NamingException {
        return this.context.getNameParser(name);
    }

    /**
     * Composes the name of this context with a name relative to this context.
     * <p>
     * Given a name (name) relative to this context, and the name (prefix) 
     * of this context relative to one of its ancestors, this method returns 
     * the composition of the two names using the syntax appropriate for the 
     * naming system(s) involved. That is, if name names an object relative 
     * to this context, the result is the name of the same object, but 
     * relative to the ancestor context. None of the names may be null.
     * 
     * @param name a name relative to this context
     * @param prefix the name of this context relative to one of its ancestors
     * @return the composition of prefix and name
     * @exception NamingException if a naming exception is encountered
     */
    public Name composeName(Name name, Name prefix)
        throws NamingException {
        return this.context.composeName(name, prefix);
    }

    /**
     * Composes the name of this context with a name relative to this context.
     * 
     * @param name a name relative to this context
     * @param prefix the name of this context relative to one of its ancestors
     * @return the composition of prefix and name
     * @exception NamingException if a naming exception is encountered
     */
    public String composeName(String name, String prefix)
        throws NamingException {
        return this.context.composeName(name, prefix);
    }

    /**
     * Adds a new environment property to the environment of this context. If 
     * the property already exists, its value is overwritten.
     * 
     * @param propName the name of the environment property to add; may not 
     * be null
     * @param propVal the value of the property to add; may not be null
     * @exception NamingException if a naming exception is encountered
     */
    public Object addToEnvironment(String propName, Object propVal)
        throws NamingException {
        return this.context.addToEnvironment(propName, propVal);
    }

    /**
     * Removes an environment property from the environment of this context. 
     * 
     * @param propName the name of the environment property to remove; 
     * may not be null
     * @exception NamingException if a naming exception is encountered
     */
    public Object removeFromEnvironment(String propName)
        throws NamingException {
        return this.context.removeFromEnvironment(propName);
    }

    /**
     * Retrieves the environment in effect for this context. See class 
     * description for more details on environment properties. 
     * The caller should not make any changes to the object returned: their 
     * effect on the context is undefined. The environment of this context 
     * may be changed using addToEnvironment() and removeFromEnvironment().
     * 
     * @return the environment of this context; never null
     * @exception NamingException if a naming exception is encountered
     */
    public Hashtable getEnvironment()
        throws NamingException {
        return this.context.getEnvironment();
    }

    /**
     * Closes this context. This method releases this context's resources 
     * immediately, instead of waiting for them to be released automatically 
     * by the garbage collector.
     * This method is idempotent: invoking it on a context that has already 
     * been closed has no effect. Invoking any other method on a closed 
     * context is not allowed, and results in undefined behaviour.
     * 
     * @exception NamingException if a naming exception is encountered
     */
    public void close()
        throws NamingException {
        this.context.close();
    }

    /**
     * Retrieves the full name of this context within its own namespace.
     * <p>
     * Many naming services have a notion of a "full name" for objects in 
     * their respective namespaces. For example, an LDAP entry has a 
     * distinguished name, and a DNS record has a fully qualified name. This 
     * method allows the client application to retrieve this name. The string 
     * returned by this method is not a JNDI composite name and should not be 
     * passed directly to context methods. In naming systems for which the 
     * notion of full name does not make sense, 
     * OperationNotSupportedException is thrown.
     * 
     * @return this context's name in its own namespace; never null
     * @exception OperationNotSupportedException if the naming system does 
     * not have the notion of a full name
     * @exception NamingException if a naming exception is encountered
     */
    public String getNameInNamespace()
        throws NamingException {
        return this.context.getNameInNamespace();
    }

    /**
     * Determines whether or not the given environment contains a 
     * {@link #SYNCHRONIZED} property with booleanValue true. 
     * 
     * @param environment environment to inspect
     * @return true if the environment contains a SYNCHRONIZED property
     * with value "true"
     */
    public static boolean isSynchronized(Hashtable environment) {
        if (environment != null) {
            Object prop = environment.get(SYNCHRONIZED);
            if (prop instanceof String) {
                return Boolean.valueOf((String)prop).booleanValue();
            }
        }
        return false;
    }

}

