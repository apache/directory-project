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
package org.apache.ldap.common.berlib.asn1;


import org.apache.ldap.common.message.spi.Provider;
import org.apache.ldap.common.message.spi.ProviderDecoder;
import org.apache.ldap.common.message.spi.ProviderEncoder;
import org.apache.ldap.common.message.spi.ProviderException;
import org.apache.ldap.common.message.spi.TransformerSpi;


/**
 * The Snickers specific BER provider for LDAP.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SnickersProvider extends Provider
{
    private final SnickersTransformer transformer;


    /**
     * Creates an instance of a Snickers based LDAP BER Provider.
     */
    private SnickersProvider()
    {
        super( "Snickers LDAP BER Provider", "Apache Directory Project" );
        transformer = new SnickersTransformer( this );
    }


    /** the singleton SnickersProvider instance */
	private static SnickersProvider singleton ;


    /**
     * Gets a handle on the singleton SnaccProvider.  Only one instance should
     * have to be instantiated for the entire jvm.
     *
     * @return the singleton SnaccProvider instance
     */
    public static Provider getProvider()
    {
        if( singleton == null )
        {
            singleton = new SnickersProvider() ;
        }

        return singleton ;
    }



    /**
     * Gets the encoder associated with this provider.
     *
     * @return the provider's encoder.
     * @throws org.apache.ldap.common.message.spi.ProviderException
     *          if the provider or its encoder cannot be found
     */
    public ProviderEncoder getEncoder() throws ProviderException
    {
        return new SnickersEncoder( this );
    }


    /**
     * Gets the decoder associated with this provider.
     *
     * @return the provider's decoder.
     * @throws org.apache.ldap.common.message.spi.ProviderException
     *          if the provider or its decoder cannot be found
     */
    public ProviderDecoder getDecoder() throws ProviderException
    {
        return new SnickersDecoder( this );
    }


    /**
     * Gets the transformer associated with this provider.
     *
     * @return the provider's transformer.
     * @throws org.apache.ldap.common.message.spi.ProviderException
     *          if the provider or its transformer cannot be found
     */
    public TransformerSpi getTransformer() throws ProviderException
    {
        return transformer;
    }
}
