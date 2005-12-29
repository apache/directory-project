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

package org.apache.ldap.common ;


import java.io.Serializable ;


/**
 * Abstract Lockable implementation base class for extention only.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public abstract class AbstractLockable
    implements Lockable, Serializable
{
    static final long serialVersionUID = 4182530114379409248L;
    
    /** Optional state overriding parent */
    private Lockable m_parent ;
    /** controls whether or not a locked Lockable can be unlocked afterwords */
    private boolean m_isUnlockable = true ;
    /** lock flag variable */
    private boolean m_isLocked = false ;


    /**
     * Creates a "reversable" root AbstractLockable.
     * 
     * Used primarily by serialization mechanism.
     */
    public AbstractLockable()
    {
    }


    /**
     * Creates a root AbstractLockable in either of the two semantic flavors:
     * "permanent" and "reversable" Lockables.  The single boolean argument
     * controls whether or not a locked Lockable can be unlocked afterwords.
     *
     * @param a_isUnlockable controls whether or not a locked Lockable can be
     * unlocked afterwords.
     */
    protected AbstractLockable( boolean a_isUnlockable )
    {
        m_isUnlockable = a_isUnlockable ;
        m_parent = null ;
    }


    /**
     * Creates a AbstractLockable in either of the two semantic flavors:
     * "permanent" and "reversable" Lockables.  The single boolean argument
     * controls whether or not a locked Lockable can be unlocked afterwords.
     *
     * @param a_parent optional state overriding parent.
     * @param a_isUnlockable controls whether or not a locked Lockable can be
     * unlocked afterwords.
     */
    protected AbstractLockable( Lockable a_parent, boolean a_isUnlockable ) 
    {
        m_isUnlockable = a_isUnlockable ;
        m_parent = a_parent ;
    }


    /**
     * Checks whether or not a lock is reversable on this Lockable using a
     * setLocked with a true argument.  "Reversable" Lockables return true and
     * "permanant" Lockables return false.
     *
     * @return true if the Lockable can be unlocked, false if the first lock is
     * permanant.
     */
    public boolean isUnlockable()
    {
        if ( m_parent == null ) 
        {
            return m_isUnlockable ;
        }

        return m_parent.isUnlockable() ;
    }


    /**
     * Gets whether or not the object currently rejects alterations via
     * change inducing methods and property mutators.
     *
     * @return true if the object is locked, false if it is not
     */
    public boolean isLocked()
    {
        if ( m_parent == null ) 
        {
            return m_isLocked ;
        }

        return m_parent.isLocked() ;
    }


    /**
     * Gets whether or not the object currently rejects alterations via
     * change inducing methods and property mutators.
     *
     * @return true if the object is locked, false if it is not
     */
    public boolean getLocked()
    {
        if ( m_parent == null ) 
        {
            return m_isLocked ;
        }

        return m_parent.isLocked() ;
    }


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
    public void setLocked( boolean a_isLocked ) throws LockException
    {
        if ( m_parent == null ) 
        {
            // Do nothing if state change has no effect
            if ( m_isLocked == a_isLocked )
            {
                return ;
            }

            // Do anything you want to do to an reversable Lockable
            if ( m_isUnlockable )
            {
                m_isLocked = a_isLocked ;
            }
            else if ( m_isLocked )  
            { // change's to locked "permanent" Lockables blow up
                throw new LockException(
                    this, "Cannot unlock a \"permanent\" Lockable!" ) ;
            }

            return ;
        }

        throw new LockException( this,
            "Cannot [un]lock a non-root Lockable!" ) ;
    }


    /**
     * Gets the state overriding Lockable parent to this child Lockable.
     *
     * @return the state overriding parent Lockable.
     */
    public Lockable getParent()
    {
        return m_parent ;
    }


    /**
     * Checks to see if the object is locked.  If so a LockException is thrown
     * otherwise call returns without any affect.
     *
     * @param a_msg The message to use within the LockException.
     */
    protected void lockCheck( final String a_msg )
    {
        if ( isLocked() )
        {
            throw new LockException( this, a_msg ) ;
        }
    }


    /**
     * Checks to see if the object is locked.  If so a LockException is thrown
     * otherwise call returns without any affect.
     */
    protected void lockCheck()
    {
        if ( isLocked() )
        {
            throw new LockException( this ) ;
        }
    }
}
