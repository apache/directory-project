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

package org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


public class BackAction extends Action implements HistoryListener
{

    private HistoryManager historyManager;


    public BackAction( HistoryManager historyManager )
    {
        super.setText( "Back" );
        super.setToolTipText( "Back" );
        super.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_TOOL_BACK ) );
        super.setEnabled( false );

        this.historyManager = historyManager;
        this.historyManager.addHistoryListener( this );
    }


    public void run()
    {
        this.historyManager.back();
    }


    public void dispose()
    {
        this.historyManager.removeHistoryListener( this );
        this.historyManager = null;
    }


    public void historyModified()
    {
        super.setEnabled( this.historyManager.isBackPossible() );
    }

}