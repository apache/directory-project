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


package org.apache.naming.factory;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceLinkRef;


/**
 * <p>Object factory for resource links.</p>
 * 
 * @author Remy Maucherat
 * @version $Revision$ $Date: 2003/10/13 08:15:11 $
 */

public class ResourceLinkFactory
    implements ObjectFactory {


    // ----------------------------------------------------------- Constructors


    // ------------------------------------------------------- Static Variables


    /**
     * Global naming context.
     */
    private static Context globalContext = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Set the global context (note: can only be used once).
     * 
     * @param newGlobalContext new global context value
     */
    public static void setGlobalContext(Context newGlobalContext) {
        if (globalContext != null)
            return;
        globalContext = newGlobalContext;
    }


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Create a new ResourceLink instance.
     * 
     * @param obj The reference object describing the ResourceLink
     * @param name The name of this object relative to nameCtx, or null if no name is specified.
     * @param nameCtx The context relative to which the name  parameter is specified, 
     * or null if name is relative to the default initial context.
     * @param environment  The possibly null environment that is used in 
     * creating the object.
     * @return The object created; null if an instance cannot be created.
     * @throws NamingException if a NamingException occurs
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws NamingException {
        
        if (!(obj instanceof ResourceLinkRef))
            return null;

        // Can we process this request?
        Reference ref = (Reference) obj;

        String type = ref.getClassName();

        // Read the global ref addr
        String globalName = null;
        RefAddr refAddr = ref.get(ResourceLinkRef.GLOBALNAME);
        if (refAddr != null) {
            globalName = refAddr.getContent().toString();
            Object result = null;
            result = globalContext.lookup(globalName);
            // FIXME: Check type
            return result;
        }

        return (null);

        
    }


}
