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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;


class SplashScreen extends JWindow
{
    private static final long serialVersionUID = 2703571434489131933L;


    public SplashScreen( String filename, Frame parent, int waitTime )
    {
        super( parent );
        JLabel label = new JLabel( new ImageIcon( 
            getClass().getResource( "/org/safehaus/triplesec/admin/swing/splashscreen.png" ) ) );
        getContentPane().add( label, BorderLayout.CENTER );
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = label.getPreferredSize();
        setLocation( screenSize.width / 2 - ( labelSize.width / 2 ), 
            screenSize.height / 2 - ( labelSize.height / 2 ) );
        addMouseListener( new MouseAdapter()
        {
            public void mousePressed( MouseEvent e )
            {
                setVisible( false );
                dispose();
            }
        } );
        
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
        {
            public void run()
            {
                setVisible( false );
                dispose();
            }
        };
        Runnable waitRunner = new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep( pause );
                    SwingUtilities.invokeAndWait( closerRunner );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        };
        setVisible( true );
        Thread splashThread = new Thread( waitRunner, "SplashThread" );
        splashThread.start();
    }
}
