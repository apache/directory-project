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


import org.apache.ldap.common.message.Message;
import org.apache.ldap.common.message.spi.Provider;
import org.apache.ldap.common.message.spi.TransformerSpi;


/**
 * A do nothing Message transformer.  When the Snickers provider is used there
 * is no need to transform Messages to and from a provider stub.  The provider
 * stubs are messages themselves.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SnickersTransformer implements TransformerSpi
{
    /** the provider this transformer is part of */
    private final SnickersProvider provider;


    /**
     * Creates a passthrough transformer that really does nothing at all.
     *
     * @param provider the povider for this transformer
     */
    public SnickersTransformer( SnickersProvider provider )
    {
        this.provider = provider;
    }


    /**
     * Gets the Provider associated with this SPI implementation object.
     *
     * @return Provider.
     */
    public Provider getProvider()
    {
        return provider;
    }


    /**
     * Returns the object passed in as is.  There is no need for a
     * transformation when Snickers is used.
     *
     * @param obj the object to transform
     * @return the object cast to a Message without any transformation at all
     */
    public Message transform( Object obj )
    {
        return ( Message ) obj;
    }


    /**
     * Returns the Message passed in as is.  There is no need for a
     * transformation when Snickers is used.
     *
     * @param msg the message to transform
     * @return the same msg arg untouched
     */
    public Object transform( Message msg )
    {
        return msg;
    }
}
