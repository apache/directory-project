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
 * $Id: SnaccProvider.java,v 1.5 2003/08/04 04:27:15 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.berlib.snacc ;


import java.util.Set;

import org.apache.ldap.common.message.spi.*;


/**
 * Snacc4J provider implementation.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class SnaccProvider extends Provider
{
    /** the singleton SnaccProvider instance */
	private static SnaccProvider s_singleton ;


    /**
     * Gets a handle on the singleton SnaccProvider.  Only one instance should
     * have to be instantiated for the entire jvm.
     *
     * @return the singleton SnaccProvider instance
     */
    public static Provider getProvider()
    {
        if( s_singleton == null )
        {
            new SnaccProvider() ;
        }

        return s_singleton ;
    }



    // ------------------------------------------------------------------------
    // Provider Properties
    // ------------------------------------------------------------------------

    /** This provider's EncoderSpi implementation */
    private final ProviderEncoder m_encoder ;
    /** This provider's Decoder implementation */
    private final ProviderDecoder decoder ;
    /** This provider's TransformerSpi implementation */
    private final TransformerSpi m_transformer ;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates an instance of a Provider extracting the required parameter
     * information from a composite set of properties.
     */
    public SnaccProvider() throws ProviderException
    {
        super( "Snacc4J BER Library Provider", "IBM Alphaworks" ) ;
		m_encoder = new SnaccEncoder( this ) ;
		decoder = new SnaccDecoder( this ) ;
		m_transformer = new SnaccTransformer( this ) ;

        if( s_singleton != null )
        {
            throw new ProviderException( s_singleton,
                "Attempt to create more than one SnaccProvider instance." ) ;
        }

		s_singleton = this ;
    }


    // ------------------------------------------------------------------------
    // Property Accessor Mutator Methods
    // ------------------------------------------------------------------------


    /**
     * Gets the EncoderSpi implementation instance associated with this
     * Provider.
     *
     * @return an instance of the EncoderSpi implementation.
     */
    public ProviderEncoder getEncoder()
    {
        return m_encoder ;
    }


    /**
     * Gets the Decoder implementation instance associated with this
     * Provider.
     *
     * @return an instance of the Decoder implementation.
     */
    public ProviderDecoder getDecoder()
    {
        return decoder ;
    }


    /**
     * Gets the Decoder implementation instance associated with this
     * Provider.
     *
     * @return an instance of the Decoder implementation.
     */
    public ProviderDecoder getDecoder( Set binaries )
    {
        return decoder ;
    }


    /**
     * Gets the TransformerSpi implementation instance associated with this
     * Provider.
     *
     * @return an instance of the TransformerSpi implementation.
     */
    public TransformerSpi getTransformer()
    {
        return m_transformer ;
    }
}
