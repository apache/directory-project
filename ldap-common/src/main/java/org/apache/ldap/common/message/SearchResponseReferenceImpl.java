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
package org.apache.ldap.common.message ;


/**
 * Lockable SearchResponseReference implementation
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class SearchResponseReferenceImpl
    extends AbstractResponse implements SearchResponseReference
{
    static final long serialVersionUID = 7423807019951309810L;
    /** Referral holding the reference urls */
    private Referral m_referral ;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates a Lockable SearchResponseReference as a reply to an SearchRequest
     * to indicate the end of a search operation.
     *
     * @param a_id the session unique message id
     */
    public SearchResponseReferenceImpl( final int a_id )
    {
        super( a_id, TYPE ) ;
    }


    // ------------------------------------------------------------------------
    // SearchResponseReference Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets the sequence of LdapUrls as a Referral instance.
     *
     * @return the sequence of LdapUrls
     */
    public Referral getReferral()
    {
        return m_referral ;
    }


    /**
     * Sets the sequence of LdapUrls as a Referral instance.
     *
     * @param a_referral the sequence of LdapUrls
     */
    public void setReferral( Referral a_referral )
    {
        lockCheck(
            "Attempt to alter referrals of a locked SearchRequestReference!" ) ;
        m_referral = a_referral ;
    }


    /**
     * Checks to see if an object is equal to this SearchResponseReference stub.
     *
     * @param obj the object to compare to this response stub
     * @return true if the objects are equivalent false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( ! super.equals( obj ) )
        {
            return false;
        }

        SearchResponseReference resp = ( SearchResponseReference ) obj;

        if ( m_referral != null && resp.getReferral() == null )
        {
            return false;
        }

        if ( m_referral == null && resp.getReferral() != null )
        {
            return false;
        }

        if ( m_referral != null && resp.getReferral() != null )
        {
            if ( ! m_referral.equals( resp.getReferral() ) )
            {
                return false;
            }
        }

        return true;
    }
}
