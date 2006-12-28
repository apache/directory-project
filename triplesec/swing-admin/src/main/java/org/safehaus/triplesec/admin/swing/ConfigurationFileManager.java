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
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;

import org.safehaus.triplesec.TriplesecInstallationLayout;
import org.safehaus.triplesec.configuration.MutableTriplesecStartupConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * 
 *
 */
public class ConfigurationFileManager
{
    private static final Logger log = LoggerFactory.getLogger( ConfigurationFileManager.class );
    private TriplesecInstallationLayout layout;
    private MutableTriplesecStartupConfiguration configuration;
    private Properties env;
    private boolean isLoading = false;
    private AdminFrame adminFrame;


    public ConfigurationFileManager( AdminFrame adminFrame )
    {
        this.adminFrame = adminFrame;
    }


    public boolean prompt( File selectedFile )
    {
        if ( isConfigurationLoaded() )
        {
            int response = JOptionPane.showConfirmDialog( adminFrame, "Abort loaded configuration "
                + layout.getBaseDirectory().getAbsolutePath() + "?" );
            switch ( response )
            {
                case ( JOptionPane.NO_OPTION  ): 
                case ( JOptionPane.CANCEL_OPTION  ):
                    return false;
            }
        }

        log.info( "Prompting for configuration file selection." );
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle( "Open Triplesec Configuration File" );
        fileChooser.setLocation( UiUtils.getCenteredPosition( fileChooser ) );
        fileChooser.setFileFilter( new FileFilter()
        {
            public boolean accept( File f )
            {
                return f.getAbsolutePath().endsWith( "server.xml" );
            }


            public String getDescription()
            {
                return "Triplesec Server Configuration File";
            }
        } );
        if ( selectedFile != null )
        {
            fileChooser.setSelectedFile( selectedFile );
        }

        int returnValue = fileChooser.showOpenDialog( this.adminFrame );
        if ( returnValue == JFileChooser.APPROVE_OPTION )
        {
            log.info( "Opening file: " + fileChooser.getSelectedFile().getAbsolutePath() );
            layout = new TriplesecInstallationLayout( fileChooser.getSelectedFile().getParentFile().getParentFile() );
            isLoading = true;
            LoadProgress progress = new LoadProgress();
            progress.setLocation( UiUtils.getCenteredPosition( progress ) );
            progress.setVisible( true );
            Thread progressThread = new Thread( progress );
            progressThread.start();

            Loader loader = new Loader();
            Thread loaderThread = new Thread( loader );
            loaderThread.start();
            return true;
        }
        return false;
    }


    public void save() throws IOException
    {
        throw new RuntimeException( "Not implemented yet" );
    }


    public void clear()
    {
        layout = null;
        configuration = null;
        env = null;
    }


    public boolean isConfigurationLoaded()
    {
        return configuration != null && env != null;
    }


    public MutableTriplesecStartupConfiguration getConfiguration()
    {
        return configuration;
    }


    public Properties getEnvironment()
    {
        return env;
    }


    public TriplesecInstallationLayout getLayout()
    {
        return layout;
    }

    public class Loader implements Runnable
    {
        public void run()
        {
            try
            {
                isLoading = true;
                adminFrame.setStatusMessage( "Opening " + layout.getConfigurationFile(), new ImageIcon( getClass()
                    .getResource( "/org/safehaus/triplesec/admin/swing/fileopen_16x16.png" ) ) );
                ApplicationContext factory = new FileSystemXmlApplicationContext( layout.getConfigurationFile().toURL()
                    .toString() );
                configuration = ( MutableTriplesecStartupConfiguration ) factory.getBean( "configuration" );
                env = ( Properties ) factory.getBean( "environment" );
                isLoading = false;
                adminFrame.setStatusMessage( "ok", new ImageIcon( getClass().getResource(
                    "/org/safehaus/triplesec/admin/swing/status_ok_16x16.png" ) ) );
            }
            catch ( Exception e )
            {
                isLoading = false;
                String msg = "Failed to open " + layout.getConfigurationFile();
                adminFrame.setStatusMessage( msg, new ImageIcon( getClass().getResource(
                    "/org/safehaus/triplesec/admin/swing/error_16x16.png" ) ) );
                log.error( msg, e );
                JOptionPane.showMessageDialog( adminFrame, msg, "Failed to load.", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    public class LoadProgress extends JDialog implements Runnable
    {
        private static final long serialVersionUID = 1L;
        private JPanel jContentPane = null;
        private JPanel jPanel = null;
        private JButton jButton = null;
        private JProgressBar jProgressBar = null;
        private boolean bypass = false;


        public void run()
        {
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
            jProgressBar.setEnabled( true );
            jProgressBar.setMinimum( 0 );
            jProgressBar.setMaximum( 10 );
            jProgressBar.setPreferredSize( new Dimension( 100, 10 ) );
            jProgressBar.setIndeterminate( true );
            while ( !bypass && isLoading )
            {
                try
                {
                    Thread.sleep( 50 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
                jProgressBar.setValue( jProgressBar.getValue() + 2 );
                this.repaint();
            }

            setCursor( null );
            setVisible( false );
            dispose();
        }


        /**
         * This is the default constructor
         */
        public LoadProgress()
        {
            super();
            initialize();
        }


        /**
         * This method initializes this
         * 
         * @return void
         */
        private void initialize()
        {
            this.setSize( 300, 104 );
            this.setContentPane( getJContentPane() );
        }


        /**
         * This method initializes jContentPane
         * 
         * @return javax.swing.JPanel
         */
        private JPanel getJContentPane()
        {
            if ( jContentPane == null )
            {
                jContentPane = new JPanel();
                jContentPane.setLayout( new BorderLayout() );
                jContentPane.add( getJPanel(), java.awt.BorderLayout.SOUTH );
                jContentPane.add( getJProgressBar(), java.awt.BorderLayout.CENTER );
            }
            return jContentPane;
        }


        /**
         * This method initializes jPanel
         * 
         * @return javax.swing.JPanel
         */
        private JPanel getJPanel()
        {
            if ( jPanel == null )
            {
                jPanel = new JPanel();
                jPanel.add( getJButton(), null );
            }
            return jPanel;
        }


        /**
         * This method initializes jButton
         * 
         * @return javax.swing.JButton
         */
        private JButton getJButton()
        {
            if ( jButton == null )
            {
                jButton = new JButton();
                jButton.setText( "Close" );
                jButton.addActionListener( new java.awt.event.ActionListener()
                {
                    public void actionPerformed( java.awt.event.ActionEvent e )
                    {
                        bypass = true;
                    }
                } );
            }
            return jButton;
        }


        /**
         * This method initializes jProgressBar
         * 
         * @return javax.swing.JProgressBar
         */
        private JProgressBar getJProgressBar()
        {
            if ( jProgressBar == null )
            {
                jProgressBar = new JProgressBar();
            }
            return jProgressBar;
        }

    } // @jve:decl-index=0:visual-constraint="10,10"
}
