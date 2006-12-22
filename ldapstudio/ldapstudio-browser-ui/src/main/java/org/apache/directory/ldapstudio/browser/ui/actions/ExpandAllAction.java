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


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;


public class ExpandAllAction extends Action
{

    protected TreeViewer viewer;


    public ExpandAllAction( TreeViewer viewer )
    {
        super( "Expand All", BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPANDALL ) );
        super.setToolTipText( getText() );
        super.setEnabled( true );

        this.viewer = viewer;
    }


    public void run()
    {
        this.viewer.expandAll();
    }


    public void dispose()
    {
        this.viewer = null;
    }

}
