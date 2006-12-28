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
package org.safehaus.triplesec.tools;


import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.directory.server.tools.BaseCommand;
import org.apache.directory.server.tools.request.BaseToolCommandCL;


/**
 * The main() application which executes command targets.
 */
public class Tools
{
    public static void main( String[] args ) throws Exception
    {
        BaseCommand tools = getInstance();
        
        if ( !BaseCommand.hasBannerOption( args ) )
        {
            tools.printBanner();
        }

        if ( args.length == 0 )
        {
            System.err.println( "Type " + tools.getProductCommand() + " help for usage." );
            System.exit( 1 );
        }

        // help is a special command 
        String command = args[0].toLowerCase();
        if ( "help".equals( command ) )
        {
            CommandLine cmdline = tools.getCommandLine( command, args );
            if ( cmdline.getArgs().length > 1 )
            {
                tools.helpOnCommand( cmdline.getArgs()[1] );
                System.exit( 0 );
            }
            else
            {
                tools.printUsage();
                System.exit( 0 );
            }
        }
        else if ( command.equals( "-version" ) )
        {
            System.out.println( tools.getProductCommand() + " version " + tools.getProductVersion() );
            System.exit( 0 );
        }

        BaseToolCommandCL cmd = (BaseToolCommandCL) tools.getCommands().get( command );
        if ( cmd == null )
        {
            System.err.println( "Unknown command: " + args[0] );
            System.err.println( "Type " + tools.getProductCommand() + " help for usage." );
            System.exit( 1 );
        }

        CommandLine cmdline = tools.getCommandLine( command, args );
        if ( cmdline.hasOption( 'd' ) )
        {
            cmd.setDebugEnabled( true );
            BaseCommand.dumpArgs( "raw command line arguments: ", args );
            BaseCommand.dumpArgs( "parsed arguments: ", cmdline.getArgs() );
        }

        cmd.setQuietEnabled( cmdline.hasOption( 'q' ) );
        cmd.setDebugEnabled( cmdline.hasOption( 'd' ) );
        cmd.setVerboseEnabled( cmdline.hasOption( 'v' ) );
        cmd.setVersion( tools.getProductVersion() );
        if ( cmdline.getOptionValue( 'i' ) != null )
        {
            cmd.processOptions(cmdline);
//            cmd.setLayout( cmdline.getOptionValue( 'i' ) );
//            if ( !cmd.isQuietEnabled() )
//            {
//                System.out.println( "loading settings from: " + cmd.getLayout().getConfigurationFile() );
//            }
//            ApplicationContext factory = null;
//            URL configUrl = cmd.getLayout().getConfigurationFile().toURL();
//            factory = new FileSystemXmlApplicationContext( configUrl.toString() );
//            cmd.setConfiguration( ( ServerStartupConfiguration ) factory.getBean( "configuration" ) );
        }
        else if ( cmdline.hasOption( 'c' ) )
        {
            System.err.println( "forced configuration load (-c) requires the -i option" );
            System.exit( 1 );
        }

        cmd.execute( cmdline, null );
    }


    public static BaseCommand getInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        Properties props = new Properties();
        try
        {
            props.load( Tools.class.getResourceAsStream( "product.properties" ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        String productVersion = props.getProperty( "product.version", "UNKNOWN" );
        String productUrl = props.getProperty( "product.url", "http://triplesec.safehaus.org" );
        String productDisplayName = props.getProperty( "product.display.name", "Triplesec Strong Identity Server" );
        String productCommand = props.getProperty( "product.command", "triplesec-tools" );
        String productBanner = props.getProperty( "product.banner", "Triplesec Strong Identity Server" );
        String productClass = props.getProperty( "product.class", "org.apache.directory.server.tools.BaseCommand" );
        
        BaseCommand baseCommand = ( BaseCommand ) Class.forName( productClass ).newInstance();
        baseCommand.setProductBanner( productBanner );
        baseCommand.setProductDisplayName( productDisplayName );
        baseCommand.setProductUrl( productUrl );
        baseCommand.setProductVersion( productVersion );
        baseCommand.setProductCommand( productCommand );
        return baseCommand;
    }
}
