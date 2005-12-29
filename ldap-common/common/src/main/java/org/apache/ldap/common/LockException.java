/*
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

/*
 * $Id: LockException.java,v 1.2 2003/07/29 21:17:34 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common ;


/**
 * Thrown when a change inducing or mutator method call is made on a locked
 * Lockable.  Can be thrown by the Lockable.setLocked() implementation or by
 * user defined class methods that should not produce an affect when the
 * Lockable is in the locked state.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class LockException extends RuntimeException
{
    static final long serialVersionUID = -1616238620780893932L;

    /** Lockable that threw this LockException */
    private final Lockable m_lockable ;

    /**
     * Constructs an LockException without a message.
     *
     * @param a_lockable the Lockable object source that threw this exception
     */
    public LockException( final Lockable a_lockable )
    {
        super() ;
        m_lockable = null ;
    }


    /**
     * Constructs an LockException with a detailed message.
     * 
     * @param a_lockable the Lockable object source that threw this exception
     * @param a_message The message associated with the exception.
     */
    public LockException( final Lockable a_lockable, String a_message )
    {
        super( a_message ) ;
        m_lockable = a_lockable ;
    }


    /**
     * Gets the Lockable object that threw this LockException.
     *
     * @return get the Lockable instance.
     */
    public Lockable getLockable()
    {
        return m_lockable ;
    }
}
