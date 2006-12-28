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
package org.safehaus.triplesec.admin.swing;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;


public class UiUtils
{
    static String showPasswordDialog( JFrame parent, String message ) 
    {
        return showPasswordDialog( parent, message, "Enter secret information!" );
    }
    
    
    static String showPasswordDialog( JFrame parent, String message, String title ) 
    {
        SecretDialog dialog = new SecretDialog( parent );
        dialog.setMessage( message );
        dialog.setTitle( title );
        dialog.setLocation( getCenteredPosition( parent ) );
        dialog.setVisible( true );
        return dialog.getPassword();
    }
    
    
    static Point getCenteredPosition( JComponent comp )
    {
        Point pt = new Point();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        pt.x = ( screenSize.width - comp.getWidth() ) / 2;
        pt.y = ( screenSize.height - comp.getHeight() ) / 2;
        return pt;
    }


    static Point getCenteredPosition( JDialog comp )
    {
        Point pt = new Point();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        pt.x = ( screenSize.width - comp.getWidth() ) / 2;
        pt.y = ( screenSize.height - comp.getHeight() ) / 2;
        return pt;
    }


    static Point getCenteredPosition( JFrame frame )
    {
        Point pt = new Point();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        pt.x = ( screenSize.width - frame.getWidth() ) / 2;
        pt.y = ( screenSize.height - frame.getHeight() ) / 2;
        return pt;
    }

    
    static String wrap( String message, int width )
    {
        StringBuffer buf = new StringBuffer();
        int counter = 0;
        for ( int ii = 0; ii < message.length(); ii++, counter++ )
        {
            if ( counter > width )
            {
                buf.append( "\n" );
                counter = 0;
            }
            buf.append( message.charAt( ii ) );
        }
        
        return buf.toString();
    }
    
    
    static boolean isFieldUpToDate( JTextComponent field, String value )
    {
        if ( field instanceof JPasswordField )
        {
            JPasswordField pfield = ( JPasswordField ) field;
            if ( ( pfield.getPassword() == null || pfield.getPassword().length == 0 )  && value == null )
            {
                return true;
            }
            if ( pfield.getPassword() == null )
            {
                return false;
            }
            return new String( pfield.getPassword() ).equals( value );
        }
        if ( ( field.getText() == null || field.getText().equals( "" ) ) && value == null )
        {
            return true;
        }
        
        if ( field.getText() == null )
        {
            return false;
        }
        
        return field.getText().equals( value );
    }
}
