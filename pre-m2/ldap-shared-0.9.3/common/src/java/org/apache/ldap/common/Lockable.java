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
 * $Id: Lockable.java,v 1.3 2003/07/29 21:17:34 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common ;


/**
 * Interface to represent objects that can lock down a configuration preventing
 * mutator methods from changing the object's state while locked.  The methods
 * to lock are determined by the implementation.  A locked object may be locked
 * forever or until it is explicity unlocked.  In this respect a Lockable can
 * be qualified as either a "permanant" or "reversable" lockable.  This nature
 * of the Lockable is determined by the semantic behavoir of the isUnlockable
 * and setLockable methods.
 *
 * Lockable containment trees can be locked by leveraging the state of the
 * parent Lockable.  Children take on the state of the parent if a parent
 * Lockable exists.  The presence of parent Lockables puts the behavoir of the
 * setLocked() method into question.  Does the method change the state of the
 * parent or not?  We decided to disable setLocked() on non-root Lockables with
 * parents by throwing a LockException.  The root of the Lockable containment
 * tree is the definitive source for state changes.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public interface Lockable
{
    /**
     * Gets the optional parent Lockable whose state overrides this Lockable's
     * state.
     *
     * @return state overriding Lockable parent
     */
    Lockable getParent() ;

    /**
     * Gets whether or not the object currently rejects alterations via
     * change inducing methods and property mutators.
     *
     * @return true if the object is locked, false if it is not
     */
    boolean isLocked() ;

    /**
     * Gets whether or not the object currently rejects alterations via
     * change inducing methods and property mutators.
     *
     * @return true if the object is locked, false if it is not
     */
    boolean getLocked() ;

    /**
     * Sets the locked flag on this Lockable.  On the first call with a false
     * argument there is no affect since all Lockables should start in the
     * unlocked state.  The first call with a true argument locks the Lockable.
     * Now if the Lockable is "permanant", future attempts to unlock with a
     * false argument will throw a LockException.  "Reverable" or "unlockable"
     * Lockables will not complain on subsequent attempts to unset the lock
     * flag.
     *
     * @param a_isLocked When set to true locks, unlocks when set to false.
     * @throws LockException If the behavoirally this Lockable is implemented as
     * a "permanant" lock, subsequent attempts to unlock a locked Lockable
     * should throw a LockException.
     */
    void setLocked( boolean a_isLocked ) throws LockException ;

    /**
     * Checks whether or not a lock is reversable on this Lockable using a
     * setLocked with a true argument.  "Reversable" Lockables return true and
     * "permanant" Lockables return false.
     *
     * @return true if the Lockable can be unlocked, false if the first lock is
     * permanant.
     */
    boolean isUnlockable() ;
}
