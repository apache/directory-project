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
package org.apache.directory.server.tools;


import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import jdbm.helper.MRU;
import jdbm.recman.BaseRecordManager;
import jdbm.recman.CacheRecordManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.directory.server.core.partition.impl.btree.Tuple;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmMasterTable;
import org.apache.directory.server.core.schema.AttributeTypeRegistry;
import org.apache.directory.server.core.schema.bootstrap.BootstrapRegistries;
import org.apache.directory.server.core.schema.bootstrap.BootstrapSchemaLoader;
import org.apache.directory.shared.ldap.message.LockableAttributeImpl;
import org.apache.directory.shared.ldap.message.LockableAttributesImpl;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.UsageEnum;


/**
 * Simple tool used to dump the contents of a jdbm based partition.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class DumpCommand extends ToolCommand
{
    private BootstrapRegistries bootstrapRegistries = new BootstrapRegistries();
    private BootstrapSchemaLoader loader = new BootstrapSchemaLoader();
    private Set exclusions = new HashSet();
    private boolean includeOperational = false;


    public DumpCommand()
    {
        super( "dump" );
    }


    public void execute( CommandLine cmdline ) throws Exception
    {
        getLayout().verifyInstallation();
        loader.load( getConfiguration().getBootstrapSchemas(), bootstrapRegistries );

        includeOperational = cmdline.hasOption( 'o' );
        String[] partitions = cmdline.getOptionValues( 'p' );
        String outputFile = cmdline.getOptionValue( 'f' );
        PrintWriter out = null;

        String[] excludedAttributes = cmdline.getOptionValues( 'e' );
        if ( excludedAttributes != null )
        {
            AttributeTypeRegistry registry = bootstrapRegistries.getAttributeTypeRegistry();
            for ( int ii = 0; ii < excludedAttributes.length; ii++ )
            {
                AttributeType type = registry.lookup( excludedAttributes[ii] );
                exclusions.add( type.getName() );
            }
        }

        if ( outputFile == null )
        {
            out = new PrintWriter( System.out );
        }
        else
        {
            out = new PrintWriter( new FileWriter( outputFile ) );
        }

        for ( int ii = 0; ii < partitions.length; ii++ )
        {
            File partitionDirectory = new File( getLayout().getPartitionsDirectory(), partitions[ii] );
            out.println( "\n\n" );
            dump( partitionDirectory, out );
        }
    }


    private void dump( File partitionDirectory, PrintWriter out ) throws Exception
    {
        if ( !partitionDirectory.exists() )
        {
            System.err.println( "Partition directory " + partitionDirectory + " does not exist!" );
            System.exit( 1 );
        }

        out.println( "# ========================================================================" );
        out.println( "# ApacheDS Tools Version: " + getVersion() );
        out.println( "# Partition Directory: " + partitionDirectory );
        out.println( "# ========================================================================\n\n" );

        String path = partitionDirectory.getPath() + File.separator + "master";
        BaseRecordManager base = new BaseRecordManager( path );
        base.disableTransactions();
        CacheRecordManager recMan = new CacheRecordManager( base, new MRU( 1000 ) );

        JdbmMasterTable master = new JdbmMasterTable( recMan );
        AttributeType attributeType = bootstrapRegistries.getAttributeTypeRegistry().lookup( "apacheUpdn" );
        JdbmIndex idIndex = new JdbmIndex( attributeType, partitionDirectory );

        out.println( "#---------------------" );
        NamingEnumeration list = master.listTuples();
        StringBuffer buf = new StringBuffer();
        while ( list.hasMore() )
        {
            Tuple tuple = ( Tuple ) list.next();
            BigInteger id = ( BigInteger ) tuple.getKey();
            String dn = ( String ) idIndex.reverseLookup( id );
            Attributes entry = ( Attributes ) tuple.getValue();

            filterAttributes( dn, entry );

            if ( !( entry instanceof LockableAttributesImpl ) )
            {
                Attributes tmp = entry;
                entry = new LockableAttributesImpl();
                NamingEnumeration attrs = tmp.getAll();
                while ( attrs.hasMore() )
                {
                    Attribute attr = ( Attribute ) attrs.next();
                    LockableAttributeImpl myattr = new LockableAttributeImpl( attr.getID() );
                    entry.put( myattr );
                    for ( int ii = 0; ii < attr.size(); ii++ )
                    {
                        myattr.add( attr.get( ii ) );
                    }
                }
            }

            buf.append( "# Entry: " ).append( id ).append( "\n#---------------------\n\n" );
            buf.append( "dn: " ).append( dn ).append( "\n" ).append( entry );
            if ( list.hasMore() )
            {
                buf.append( "\n\n#---------------------\n" );
            }
            out.print( buf.toString() );
            out.flush();
            buf.setLength( 0 );
        }
    }


    private void filterAttributes( String dn, Attributes entry ) throws NamingException
    {
        List toRemove = new ArrayList();
        AttributeTypeRegistry registry = bootstrapRegistries.getAttributeTypeRegistry();
        NamingEnumeration attrs = entry.getAll();
        while ( attrs.hasMore() )
        {
            Attribute attr = ( Attribute ) attrs.next();
            if ( !registry.hasAttributeType( attr.getID() ) )
            {
                if ( !isQuietEnabled() )
                {
                    System.out
                        .println( "# Cannot properly filter unrecognized attribute " + attr.getID() + " in " + dn );
                }
                continue;
            }

            AttributeType type = registry.lookup( attr.getID() );
            boolean isOperational = type.getUsage() != UsageEnum.USERAPPLICATIONS;
            if ( exclusions.contains( attr.getID() ) || ( isOperational && ( !includeOperational ) ) )
            {
                toRemove.add( attr.getID() );
            }
        }
        for ( int ii = 0; ii < toRemove.size(); ii++ )
        {
            String id = ( String ) toRemove.get( ii );
            entry.remove( id );
            if ( isDebugEnabled() )
            {
                System.out.println( "# Excluding attribute " + id + " in " + dn );
            }
        }
    }


    public Options getOptions()
    {
        Options opts = new Options();
        Option op = new Option( "f", "file", true, "file to output the dump to" );
        op.setRequired( false );
        opts.addOption( op );
        op = new Option( "p", "partitions", true, "the partitions to dump" );
        op.setRequired( true );
        op.setValueSeparator( File.pathSeparatorChar );
        opts.addOption( op );
        op = new Option( "e", "excluded-attributes", true, "the attributes to exclude" );
        op.setRequired( false );
        op.setValueSeparator( File.pathSeparatorChar );
        opts.addOption( op );
        op = new Option( "o", "include-operational", false, "include operational attributes: defaults to false" );
        op.setRequired( false );
        opts.addOption( op );
        op = new Option( "i", "install-path", true, "path to apacheds installation directory" );
        op.setRequired( true );
        opts.addOption( op );
        return opts;
    }
}