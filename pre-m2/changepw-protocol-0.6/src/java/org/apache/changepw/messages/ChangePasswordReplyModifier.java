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
package org.apache.changepw.messages;

import org.apache.kerberos.messages.application.ApplicationReply;
import org.apache.kerberos.messages.application.PrivateMessage;

public class ChangePasswordReplyModifier extends AbstractPasswordMessageModifier
{
    private ApplicationReply applicationReply;
    private PrivateMessage privateMessage;

    public ChangePasswordReply getChangePasswordReply()
    {
        return new ChangePasswordReply( messageLength, versionNumber, authHeaderLength,
                applicationReply, privateMessage );
    }

    public void setApplicationReply( ApplicationReply applicationReply )
    {
        this.applicationReply = applicationReply;
    }

    public void setPrivateMessage( PrivateMessage privateMessage )
    {
        this.privateMessage = privateMessage;
    }
}