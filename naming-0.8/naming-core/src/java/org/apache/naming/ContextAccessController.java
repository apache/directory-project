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

/**
 * Handles the access control on JNDI contexts.
 * <p>
 *  Contexts are referenced by their external names, established using
 * {@link ContextBindings#bindContext(Object,Context)}.  Supported operations
 * include:
 * <dl>
 *   <dt>Making contexts read-only / read-write</dt>
 *     <dd>{@link #setReadOnly(Object)},  
 *              {@link #setWritable(Object,Object)}</dd>
 *   <dt>Associating security tokens with named contexts</dt> 
 *     <dd>{@link #setSecurityToken(Object,Object)}, 
 *              {@link #unsetSecurityToken(Object,Object)}</dd>
 *   <dt>Validating security tokens</dt> 
 *     <dd>{@link #checkSecurityToken(Object,Object)}</dd>
 * </dl>
 * 
 * @author Remy Maucherat
 * @version $Revision$ $Date: 2003/11/30 05:15:06 $
 */

public class ContextAccessController {


    // -------------------------------------------------------------- Variables


    /**
     * Context names on which writing is not allowed.
     */
    private static Hashtable readOnlyContexts = new Hashtable();


    /**
     * Security tokens repository.
     */
    private static Hashtable securityTokens = new Hashtable();


    // --------------------------------------------------------- Public Methods


    /**
     * Set a security token for a context.  Does nothing if the token has
     * already been set or is null.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void setSecurityToken(Object name, Object token) {
        if ((!securityTokens.containsKey(name)) && (token != null)) {
            securityTokens.put(name, token);
        }
    }


    /**
     * Remove a security token for a context.  Checks the token first and does
     * nothing if there is no token set or the token passed in does not match
     * the token in the repository.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void unsetSecurityToken(Object name, Object token) {
        if (checkSecurityToken(name, token)) {
            securityTokens.remove(name);
        }
    }


    /**
     * Check a submitted security token. The submitted token must be equal to
     * the token present in the repository. If no token is present for the 
     * context, then returns true.
     * 
     * @param name name of the context
     * @param token submitted security token
     * @return true if the check succeeds
     */
    public static boolean checkSecurityToken
        (Object name, Object token) {
        Object refToken = securityTokens.get(name);
        if (refToken == null)
            return (true);
        if ((refToken != null) && (refToken.equals(token)))
            return (true);
        return (false);
    }


    /**
     * Remove read-only restriction on the given context.
     * Checks security token first.
     * 
     * @param name Name of the context
     * @param token Security token
     */
    public static void setWritable(Object name, Object token) {
        if (checkSecurityToken(name, token))
            readOnlyContexts.remove(name);
    }


    /**
     * Make the given context read only.
     * 
     * @param name Name of the context
     */
    public static void setReadOnly(Object name) {
        readOnlyContexts.put(name, name);
    }


    /**
     * Returns if a context is writable.
     * 
     * @param name Name of the context
     * @return true if the context is writable
     */
    public static boolean isWritable(Object name) {
        return !(readOnlyContexts.containsKey(name));
    }


}

