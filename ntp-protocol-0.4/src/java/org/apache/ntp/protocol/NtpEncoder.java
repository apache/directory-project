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

package org.apache.ntp.protocol;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.protocol.ProtocolEncoder;
import org.apache.mina.protocol.ProtocolEncoderOutput;
import org.apache.mina.protocol.ProtocolSession;
import org.apache.mina.protocol.ProtocolViolationException;
import org.apache.ntp.io.NtpMessageEncoder;
import org.apache.ntp.messages.NtpMessage;

public class NtpEncoder implements ProtocolEncoder
{
    public void encode( ProtocolSession session, Object message, ProtocolEncoderOutput out )
            throws ProtocolViolationException
    {
        NtpMessageEncoder encoder = new NtpMessageEncoder();

        ByteBuffer buf = ByteBuffer.allocate( 1024 );
        encoder.encode( buf.buf(), (NtpMessage) message );

        buf.flip();

        out.write( buf );
    }
}
