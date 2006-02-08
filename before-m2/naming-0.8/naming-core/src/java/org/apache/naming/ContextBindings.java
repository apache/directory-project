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


package org.apache.naming;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Context;

/**
 * Maintains bindings associating user-defined names, NamingContexts, threads
 * and classloaders.  User-defined names are bound to contexts using
 * <code>bindContext.</code>  Threads and classloaders are bound to 
 * NamingContexts indirectly, using <code>bindXxx</code> methods that take
 * user-defined context names as parameters.  The currently executing thread
 * and the context classloaders of the current thread are implicit arguments to
 * the thread and classloader binding methods -- that is, it is only possible
 * to bind the current thread and its context classloader. 
 * <p>
 * If a context name has a security token associated with it (set using 
 * {@link ContextAccessController#setSecurityToken(Object,Object)}), this token
 * must be supplied to bind and unbind methods.
 * <p>
 * <table border=1, cellpadding=5>
 * <tr align=left><th>Key</th><th>Value</th><th>Bind methods</th><th>Lookup method</th></tr>
 * <tr><td>User-defined name</td><td>NamingContext</td>
 *      <td>{@link #bindContext(Object,Context)}, 
 *              {@link #bindContext(Object,Context,Object)}</td>
 *      <td>none</td></tr>
 * <tr><td>Thread</td><td>NamingContext</td>
 *      <td>{@link #bindThread(Object)}, 
 *              {@link #bindThread(Object, Object)}</td>
 *      <td>{@link #getThread()}</td></tr>
 * <tr><td>ClassLoader</td><td>NamingContext</td>
 *      <td>{@link #bindClassLoader(Object, Object)}, 
 *              {@link #bindClassLoader(Object,Object, ClassLoader)}</td>
 *      <td>{@link #getClassLoader()}</td></tr>
 * </table>
 * <p>
 *  Also includes <code>unbind</code> methods and methods for determining
 *  whether or not classloaders and threads are bound to contexts.   
 * 
 * @author Remy Maucherat
 * @version $Rev$ $Date$
 */

public class ContextBindings {


    // -------------------------------------------------------------- Variables


    /**
     * Bindings name - naming context. Keyed by name.
     */
    private static Hashtable contextNameBindings = new Hashtable();


    /**
     * Bindings thread - naming context. Keyed by thread id.
     */
    private static Hashtable threadBindings = new Hashtable();


    /**
     * Bindings thread - name. Keyed by thread id.
     */
    private static Hashtable threadNameBindings = new Hashtable();


    /**
     * Bindings class loader - naming context. Keyed by CL id.
     */
    private static Hashtable clBindings = new Hashtable();


    /**
     * Bindings class loader - name. Keyed by CL id.
     */
    private static Hashtable clNameBindings = new Hashtable();


    /**
     * The string manager for this package.
     */
    protected static StringManager sm = 
        StringManager.getManager(Constants.Package);


    // --------------------------------------------------------- Public Methods


    /**
     * Binds a context name.  Overwrites any existing binding using 
     * <code>name.</code>
     * 
     * @param name Name of the context
     * @param context Associated naming context instance
     */
    public static void bindContext(Object name, Context context) {
        bindContext(name, context, null);
    }


    /**
     * Binds a context name.  Checks the security token first and does nothing
     * if the check fails.  Overwrites any existing binding using 
     * <code>name.</code>
     * 
     * @param name Name of the context
     * @param context Associated naming context instance
     * @param token Security token associated with the context
     */
    public static void bindContext(Object name, Context context, 
                                   Object token) {
        if (ContextAccessController.checkSecurityToken(name, token))
            contextNameBindings.put(name, context);
    }


    /**
     * Unbinds a context name.
     * 
     * @param name Name of the context
     */
    public static void unbindContext(Object name) {
        unbindContext(name, null);
    }


    /**
     * Unbinds a context name.  Checks the security token first and does nothing
     * if the check fails.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void unbindContext(Object name, Object token) {
        if (ContextAccessController.checkSecurityToken(name, token))
            contextNameBindings.remove(name);
    }


    /**
     * Retrieves a naming context.  Returns <code>null</code> if there is no
     * context associated with <code>name.</code>
     * 
     * @param name Name of the context
     * @return the context bound to the name
     */
    static Context getContext(Object name) {
        return (Context) contextNameBindings.get(name);
    }


    /**
     * Binds a naming context to the currently executing thread.  The 
     * <code>name</code> must have been previously bound to a 
     * <code>NamingContext</code> using 
     * {@link #bindContext(Object, Context)}; otherwise a 
     * <code>NamingException</code> is thrown.   Overwrites any existing
     * binding for the currently executing thread.
     * 
     * @param name Name of the context
     * @throws NamingException  if <code>name</code> is not bound
     *  to a context
     */
    public static void bindThread(Object name) 
        throws NamingException {
        bindThread(name, null);
    }


    /**
     * Binds a naming context to the currently executing thread.  The 
     * <code>name</code> must have been previously bound to a 
     * <code>NamingContext</code> using 
     * {@link #bindContext(Object, Context, Object)}; otherwise a 
     * <code>NamingException</code> is thrown.  Checks the security token first
     * and does nothing if the check fails. Overwrites any existing
     * binding for the currently executing thread.
     * 
     * @param name Name of the context
     * @param token Security token
     * @throws NamingException  if <code>name</code> is not bound
     *  to a context
     */
    public static void bindThread(Object name, Object token) 
        throws NamingException {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            Context context = (Context) contextNameBindings.get(name);
            if (context == null)
                throw new NamingException
                    (sm.getString("contextBindings.unknownContext", name));
            threadBindings.put(Thread.currentThread(), context);
            threadNameBindings.put(Thread.currentThread(), name);
        }
    }


    /**
     * Unbinds a naming context from the currently executing thread.  
     * Does nothing if <code>name</code> is not bound to a context.
     * 
     * @param name Name of the context
     */
    public static void unbindThread(Object name) {
        unbindThread(name, null);
    }


    /**
     * Unbinds a naming context from the currently executing thread.  Checks
     * the security token first and does nothing if the check fails or if
     * <code>name</code> is not bound to a context. 
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void unbindThread(Object name, Object token) {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            threadBindings.remove(Thread.currentThread());
            threadNameBindings.remove(Thread.currentThread());
        }
    }


    /**
     * Retrieves the naming context bound to the currently executing thread. 
     * Throws a <code>NamingException</code> if the current thread is not bound
     * to a context.
     * 
     * @return  context bound to the current thread
     * @throws NamingException if the current thread is not bound
     * @see #bindThread(Object)
     */
    public static Context getThread()
        throws NamingException {
        Context context = 
            (Context) threadBindings.get(Thread.currentThread());
        if (context == null)
            throw new NamingException
                (sm.getString("contextBindings.noContextBoundToThread"));
        return context;
    }


    /**
     * Retrieves the (user-defined) name of the naming context bound to the
     * currently executing thread. Throws a <code>NamingException</code> if the
     * current thread is not bound to a context.
     * 
     * @return  name of context bound to the current thread
     * @throws NamingException if the current thread is not bound
     * @see #bindThread(Object)
     * @see #bindContext(Object, Context)
     */
    static Object getThreadName()
        throws NamingException {
        Object name = threadNameBindings.get(Thread.currentThread());
        if (name == null)
            throw new NamingException
                (sm.getString("contextBindings.noContextBoundToThread"));
        return name;
    }


    /**
     * Tests if current thread is bound to a context.
     * 
     * @return true if the current thread is bound
     */
    public static boolean isThreadBound() {
        return (threadBindings.containsKey(Thread.currentThread()));
    }


    /**
     * Binds a naming context to the context ClassLoader for the currently
     * executing thread.  The <code>name</code> must have been previously
     * bound to a <code>NamingContext</code> using 
     * {@link #bindContext(Object, Context, Object)}; otherwise a 
     * <code>NamingException</code> is thrown. 
     * <p>
     * Note:  this method does <strong>not</strong> overwrite existing bindings.
     * If the ClassLoader is already bound to a context, activating this method
     * with a new context name does nothing.
     * 
     * @param name Name of the context
     * @throws NamingException if the current thread is not bound
     * @throws SecurityException if a security manager exists and its 
     * <code>checkPermission</code> method doesn't allow getting the context
     * ClassLoader
     */
    public static void bindClassLoader(Object name) 
        throws NamingException {
        bindClassLoader(name, null);
    }


    /**
     * Binds a naming context to the context ClassLoader for the currently
     * executing thread.  The <code>name</code> must have been previously
     * bound to a <code>NamingContext</code> using 
     * {@link #bindContext(Object, Context, Object)}; otherwise a 
     * <code>NamingException</code> is thrown.   Checks the security token
     * and does nothing if the check fails.
     * <p>
     * Note:  this method does <strong>not</strong> overwrite existing bindings.
     * If the ClassLoader is already bound to a context, activating this method
     * with a new context name does nothing.
     * 
     * @param name Name of the context
     * @param token Security token associated with the name
     * @throws NamingException if the current thread is not bound
     * @throws SecurityException if a security manager exists and its 
     * <code>checkPermission</code> method doesn't allow getting the context
     * ClassLoader
     */
    public static void bindClassLoader(Object name, Object token) 
        throws NamingException {
        bindClassLoader
            (name, token, Thread.currentThread().getContextClassLoader());
    }


    /**
     * Binds a naming context to a ClassLoader.  The <code>name</code>
     * must have been previously bound to a <code>NamingContext</code> using 
     * {@link #bindContext(Object, Context, Object)}; otherwise a 
     * <code>NamingException</code> is thrown.   Checks the security token
     * and does nothing if the check fails.
     * <p>
     * Note:  this method does <strong>not</strong> overwrite existing bindings.
     * If the ClassLoader is already bound to a context, activating this method
     * with a new context name does nothing.
     * 
     * @param name Name of the context
     * @param token Security token
     * @param classLoader  ClassLoader to bind
     * @throws NamingException if the name is not bound to a context
     */
    public static void bindClassLoader(Object name, Object token, 
                                       ClassLoader classLoader) 
        throws NamingException {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            Context context = (Context) contextNameBindings.get(name);
            if (context == null)
                throw new NamingException
                    (sm.getString("contextBindings.unknownContext", name));
            Object n = clNameBindings.get(classLoader);
            // Only bind CL if it isn't already bound to the context
            if (n == null) {
                clBindings.put(classLoader, context);
                clNameBindings.put(classLoader, name);
            }
        }
    }


    /**
     * Unbinds a naming context from the context ClassLoader for the currently
     * executing thread.
     * 
     * @param name Name of the context
     * @throws SecurityException if a security manager exists and its 
     * <code>checkPermission</code> method doesn't allow getting the context
     * ClassLoader
     */
    public static void unbindClassLoader(Object name) {
        unbindClassLoader(name, null);
    }


    /**
     * Unbinds a naming context from the context ClassLoader for the currently
     * executing thread.  Checks the security token first and does nothing
     * if the check fails.
     * 
     * @param name Name of the context
     * @param token Security token
     * @throws SecurityException if a security manager exists and its 
     * <code>checkPermission</code> method doesn't allow getting the context
     * ClassLoader
     */
    public static void unbindClassLoader(Object name, Object token) {
        unbindClassLoader(name, token, 
                          Thread.currentThread().getContextClassLoader());
    }


    /**
     * Unbinds a naming context from a class loader.  Checks the security
     * token first and does nothing if the check fails.
     * 
     * @param name Name of the context
     * @param token Security token
     * @param classLoader ClassLoader to unbind
     */
    public static void unbindClassLoader(Object name, Object token, 
                                         ClassLoader classLoader) {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            Object n = clNameBindings.get(classLoader);
            if ((n==null) || !(n.equals(name)))  {
                return;
            }
            clBindings.remove(classLoader);
            clNameBindings.remove(classLoader);
        }
    }


    /**
     * Retrieves the naming context bound to the context ClassLoader for
     * the currently executing thread, if that ClassLoader is bound.  If the
     * context ClassLoader is not bound, the ClassLoader bindings table is
     * queried for parent ClassLoaders, moving up the hierarchy until a bound
     * ClassLoader is found.  Throws a <code>NamingException</code>
     * if no ClassLoader in the hierarchy is bound to a context.
     * 
     * @return  context bound to the first bound ClassLoader found
     * @throws NamingException if no bound ClassLoader is found
     * @throws SecurityException if a security manager exists and its 
     * <code>checkPermission</code> blocks access to a ClassLoader
     * @see #bindClassLoader(Object)
     */
    public static Context getClassLoader()
        throws NamingException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Context context = null;
        do {
            context = (Context) clBindings.get(cl);
            if (context != null) {
                return context;
            }
        } while ((cl = cl.getParent()) != null);
        throw new NamingException
            (sm.getString("contextBindings.noContextBoundToCL"));
    }


    /**
     * Retrieves the (user-defined) name of the context bound to the context
     * ClassLoader for the currently executing thread, if that ClassLoader
     * is bound.  If the context ClassLoader is not bound, the ClassLoader
     * bindings table is queried for parent ClassLoaders, moving up the
     * hierarchy until a bound ClassLoader is found.  Throws a 
     * <code>NamingException</code> if no ClassLoader in the hierarchy
     * is bound to a context.
     * 
     * @return name of context bound to the first bound ClassLoader found
     * @throws NamingException if no bound ClassLoader is found
     * @throws SecurityException if a security manager exists and its 
     * <code>checkPermission</code> blocks access to a ClassLoader
     * @see #bindClassLoader(Object)
     */
    static Object getClassLoaderName()
        throws NamingException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Object name = null;
        do {
            name = clNameBindings.get(cl);
            if (name != null) {
                return name;
            }
        } while ((cl = cl.getParent()) != null);
        throw new NamingException
            (sm.getString("contextBindings.noContextBoundToCL"));
    }


    /**
     * Tests if current class loader is bound to a context.
     * 
     * @return true if the context ClassLoader for the current thread is bound
     */
    public static boolean isClassLoaderBound() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        do {
            if (clBindings.containsKey(cl)) {
                return true;
            }
        } while ((cl = cl.getParent()) != null);
        return false;
    }
    

}

