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


/**
 * This Action toggles the Show Raw Values Preference.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ShowRawValuesAction extends Action
{
    /**
     * Creates a new instance of ShowRawValuesAction.
     */
    public ShowRawValuesAction()
    {
        super( "Show Raw Values", AS_CHECK_BOX );
        super.setToolTipText( getText() );
        super.setEnabled( true );

        super.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES ) );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES,
            super.isChecked() );
    }


    /**
     * {@inheritDoc}
     */
    public void setChecked( boolean checked )
    {
        super.setChecked( checked );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isChecked()
    {
        return super.isChecked();
    }
}