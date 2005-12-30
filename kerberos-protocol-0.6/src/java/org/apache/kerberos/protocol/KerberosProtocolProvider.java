/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.kerberos.protocol;

import org.apache.kerberos.kdc.KdcConfiguration;
import org.apache.kerberos.store.PrincipalStore;
import org.apache.mina.protocol.ProtocolCodecFactory;
import org.apache.mina.protocol.ProtocolDecoder;
import org.apache.mina.protocol.ProtocolEncoder;
import org.apache.mina.protocol.ProtocolHandler;
import org.apache.mina.protocol.ProtocolProvider;

public class KerberosProtocolProvider implements ProtocolProvider
{
    // Protocol handler is usually a singleton.
    private static ProtocolHandler HANDLER;

    // Codec factory is also usually a singleton.
    private static ProtocolCodecFactory CODEC_FACTORY = new ProtocolCodecFactory()
    {
        public ProtocolEncoder newEncoder()
        {
            // Create a new encoder.
            return new KerberosEncoder();
        }

        public ProtocolDecoder newDecoder()
        {
            // Create a new decoder.
            return new KerberosDecoder();
        }
    };

    public KerberosProtocolProvider( KdcConfiguration config, PrincipalStore store )
    {
        HANDLER = new KerberosProtocolHandler( config, store );
    }

    public ProtocolCodecFactory getCodecFactory()
    {
        return CODEC_FACTORY;
    }

    public ProtocolHandler getHandler()
    {
        return HANDLER;
    }
}
