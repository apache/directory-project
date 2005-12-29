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
 * $Id: LdapResultImpl.java,v 1.4 2003/07/31 21:44:48 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.message;


import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.AbstractLockable;


/**
 * Lockable LdapResult implementation.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class LdapResultImpl
    extends AbstractLockable implements LdapResult
{
    static final long serialVersionUID = -1446626887394613213L;
    /** Lowest matched entry Dn - defaults to empty string */
    private String matchedDn;
    /** Referral associated with this LdapResult if the errorCode is REFERRAL */
    private Referral referral;
    /** Decriptive error message - defaults to empty string */
    private String errorMessage;
    /** Resultant operation error code - defaults to SUCCESS */
    private ResultCodeEnum resultCode = ResultCodeEnum.SUCCESS;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates a non-root Lockable LdapResult using a parent for state.
     *
     * @param parent the overriding parent Lockable.
     */
    public LdapResultImpl( final Lockable parent )
    {
        super( parent, false );
    }


    // ------------------------------------------------------------------------
    // LdapResult Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets the descriptive error message associated with the error code.  May
     * be null for SUCCESS, COMPARETRUE, COMPAREFALSE and REFERRAL operations.
     *
     * @return the descriptive error message.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }


    /**
     * Sets the descriptive error message associated with the error code.  May
     * be null for SUCCESS, COMPARETRUE, and COMPAREFALSE operations.
     *
     * @param errorMessage the descriptive error message.
     */
    public void setErrorMessage( String errorMessage )
    {
        lockCheck( "Attempt to alter error message of locked LdapResult!" );
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the lowest entry in the directory that was matched.  
     * For result codes of noSuchObject, aliasProblem, invalidDNSyntax and
     * aliasDereferencingProblem, the matchedDN field is set to the name of
     * the lowest entry (object or alias) in the directory that was matched.
     * If no aliases were dereferenced while attempting to locate the entry,
     * this will be a truncated form of the name provided, or if aliases
     * were dereferenced, of the resulting name, as defined in section 12.5
     * of X.511 [8]. The matchedDN field is to be set to a zero length
     * string with all other result codes.
     *
     * @return the Dn of the lowest matched entry.
     */
    public String getMatchedDn()
    {
        return matchedDn;
    }


    /**
     * Sets the lowest entry in the directory that was matched.
     * 
     * @see #getMatchedDn()
     * @param matchedDn the Dn of the lowest matched entry.
     */
    public void setMatchedDn( String matchedDn )
    {
        lockCheck( "Attempt to alter matchedDn of locked LdapResult!" );
        this.matchedDn = matchedDn;
    }


    /**
     * Gets the result code enumeration associated with the response.
     * Corresponds to the <b> resultCode </b> field within the LDAPResult ASN.1
     * structure.
     *
     * @return the result code enum value.
     */
    public ResultCodeEnum getResultCode()
    {
        return resultCode;
    }


    /**
     * Sets the result code enumeration associated with the response.
     * Corresponds to the <b> resultCode </b> field within the LDAPResult ASN.1
     * structure.
     *
     * @param resultCode the result code enum value.
     */
    public void setResultCode( ResultCodeEnum resultCode )
    {
        lockCheck( "Attempt to alter the resultCode of a locked LdapResult!" );
        this.resultCode = resultCode;
    }


    /**
     * Gets the Referral associated with this LdapResult if the resultCode
     * property is set to the REFERRAL ResultCodeEnum.
     *
     * @return the referral on REFERRAL errors, null on all others.
     */
    public Referral getReferral()
    {
        return referral;
    }


    /**
     * Gets whether or not this result represents a Referral.  For referrals the
     * error code is set to REFERRAL and the referral property is not null.
     *
     * @return true if this result represents a referral.
     */
    public boolean isReferral()
    {
        return referral != null;
    }


    /**
     * Sets the Referral associated with this LdapResult if the resultCode
     * property is set to the REFERRAL ResultCodeEnum.  Setting this property
     * will result in a true return from isReferral and the resultCode should
     * be set to REFERRAL.
     *
     * @param referral optional referral on REFERRAL errors.
     */
    public void setReferral( Referral referral )
    {
        lockCheck( "Attempt to alter the referral of a locked LdapResult!" );
        this.referral = referral;
    }


    /**
     *
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        // quickly return true if this is the obj
        if ( obj == this )
        {
            return true;
        }

        // return false if object does not implement interface
        if ( ! ( obj instanceof LdapResult ) )
        {
            return false;
        }

        // compare all the like elements of the two LdapResult objects
        LdapResult result = ( LdapResult ) obj;

        if ( referral == null && result.getReferral() != null )
        {
            return false;
        }

        if ( result.getReferral() == null && referral != null )
        {
            return false;
        }

        if ( referral != null && result.getReferral() != null )
        {
            if ( ! referral.equals( result.getReferral() ) )
            {
                return false;
            }
        }

        if ( ! resultCode.equals( result.getResultCode() ) )
        {
            return false;
        }


        // Handle Error Messages where "" is considered equivalent to null
        String errMsg0 = errorMessage;
        String errMsg1 = result.getErrorMessage();

        if ( errMsg0 == null )
        {
            errMsg0 = "";
        }

        if ( errMsg1 == null )
        {
            errMsg1 = "";
        }

        if ( ! errMsg0.equals( errMsg1 ) )
        {
            return false;
        }


        if ( matchedDn != null )
        {
            if ( ! matchedDn.equals( result.getMatchedDn() ) )
            {
                return false;
            }
        }
        else if ( result.getMatchedDn() != null ) // one is null other is not
        {
            return false;
        }

        return true;
    }
}
