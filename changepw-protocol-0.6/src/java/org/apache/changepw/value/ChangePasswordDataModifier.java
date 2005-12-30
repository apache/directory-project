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
package org.apache.changepw.value;

import org.apache.kerberos.messages.value.PrincipalName;

public class ChangePasswordDataModifier
{
    private byte[] password;
    private PrincipalName principalName;
    private String realm;

    public ChangePasswordData getChangePasswdData()
    {
        return new ChangePasswordData( password, principalName, realm );
    }

    public void setNewPassword( byte[] password )
    {
        this.password = password;
    }

    public void setTargetName( PrincipalName principalName )
    {
        this.principalName = principalName;
    }

    public void setTargetRealm( String realm )
    {
        this.realm = realm;
    }
}
