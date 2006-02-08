/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


package org.apache.asn1.ber.digester ;


import java.util.List;

import org.apache.commons.collections.primitives.IntStack;


/**
 * Public interface defining a collection of Rule instances (and corresponding
 * matching patterns) plus an implementation of a matching policy that selects
 * the rules that match a particular pattern of nested elements discovered
 * during parsing.  The interface has been inspired by the rulesBase equivalent.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public interface Rules 
{
    /**
     * Get the <code>BERDigester</code> instance with which this <code>Rules
     * </code> instance is associated.
     * 
     * @return the BERDigester associated with this Rules instance
     */
    public BERDigester getDigester() ;

    /**
     * Get the <code>BERDigester</code> instance with which this <code>Rules
     * </code> instance is associated.
     * 
     * @param digester the new BERDigester to be associated with this Rules 
     * instance
     */
    public void setDigester( BERDigester digester ) ;

    /**
     * Register a new Rule instance matching the specified pattern.
     *
     * @param pattern Tag nesting pattern to be matched for this Rule
     * @param rule Rule instance to be registered
     */
    public void add( int[] pattern, Rule rule ) ;

    /**
     * Clear all existing Rule instance registrations.
     */
    public void clear() ;

    /**
     * Return a List of all registered Rule instances that match the specified
     * nesting pattern, or a zero-length List if there are no matches.  If more
     * than one Rule instance matches, they <strong>must</strong> be returned
     * in the order originally registered through the <code>add()</code>
     * method.
     *
     * @param pattern Nesting pattern to be matched
     */
    public List match( int[] pattern ) ;

    /**
     * Return a List of all registered Rule instances that match the specified
     * nesting pattern, or a zero-length List if there are no matches.  If more
     * than one Rule instance matches, they <strong>must</strong> be returned
     * in the order originally registered through the <code>add()</code>
     * method.  The IntStack argument should not be affected by the match call.
     *
     * @param pattern Nesting pattern to be matched
     */
    public List match( IntStack pattern ) ;
    
    /**
     * Return a List of all registered Rule instances, or a zero-length List
     * if there are no registered Rule instances.  If more than one Rule
     * instance has been registered, they <strong>must</strong> be returned
     * in the order originally registered through the <code>add()</code>
     * method.
     */
    public List rules() ;
}
