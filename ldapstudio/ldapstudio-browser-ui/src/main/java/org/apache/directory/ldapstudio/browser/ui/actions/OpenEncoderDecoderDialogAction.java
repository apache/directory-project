/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.ui.dialogs.EncoderDecoderDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;


public class OpenEncoderDecoderDialogAction extends BrowserAction
{

    public OpenEncoderDecoderDialogAction()
    {
        super();
    }


    public void run()
    {
        EncoderDecoderDialog dlg = new EncoderDecoderDialog( PlatformUI.getWorkbench().getDisplay().getActiveShell() );
        dlg.open();
    }


    public String getText()
    {
        return "Open Encoder/Decoder";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return true;
    }

}