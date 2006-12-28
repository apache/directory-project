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
package org.safehaus.triplesec.changelog.beta.interceptor;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;

import org.apache.directory.server.core.DirectoryServiceConfiguration;
import org.apache.directory.server.core.configuration.InterceptorConfiguration;
import org.apache.directory.server.core.interceptor.BaseInterceptor;
import org.apache.directory.server.core.interceptor.NextInterceptor;
import org.apache.directory.server.core.invocation.InvocationStack;
import org.apache.directory.server.core.jndi.ServerContext;
import org.apache.directory.server.core.schema.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.DateUtils;
import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.safehaus.triplesec.changelog.beta.model.AddChangeEvent;
import org.safehaus.triplesec.changelog.beta.model.ChangeEvent;
import org.safehaus.triplesec.changelog.beta.model.DeleteChangeEvent;
import org.safehaus.triplesec.changelog.beta.model.ModifyChangeEvent;
import org.safehaus.triplesec.changelog.beta.model.ModifyDnChangeEvent;
import org.safehaus.triplesec.changelog.beta.model.ModifyRdnChangeEvent;
import org.safehaus.triplesec.changelog.beta.model.StringAttribute;
import org.safehaus.triplesec.changelog.beta.support.AttributeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An interceptor which maintains a change log as it intercepts changes to the
 * directory. It maintains an embedded DB for writing Changelog Event records.
 * 
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public class ChangelogService extends BaseInterceptor implements Runnable
{
    
    private static final Logger log = LoggerFactory.getLogger( ChangelogService.class );

    private static String dbProtocol = "jdbc:derby:";

    private static String dbName = "changelogDb";

    private static String dbTable = "changelogTable";

    private static String dbUserName = "user1";

    private static String dbPassword = "user1";

    /** the single db connection */
    private Connection conn = null;

    /** queue of changelog events awaiting serialization to the db */
    private LinkedList queue = new LinkedList();

    /** a handle on the attributeType registry to determine the binary nature of attributes */
    private AttributeTypeRegistry registry = null;

    /** determines if this service has been activated */
    private boolean isActive = false;

    /** thread used to asynchronously write change logs to db */
    private Thread writer = null;
    
    /** time to wait before automatically waking up the writer thread */
    private static final long WAIT_TIMEOUT_MILLIS = 1000;


    // -----------------------------------------------------------------------
    // Overridden init() and destroy() methods
    // -----------------------------------------------------------------------

    public void init( DirectoryServiceConfiguration dsConfig, InterceptorConfiguration iConfig ) throws NamingException
    {
        super.init( dsConfig, iConfig );

        // Initialize the DB backend for logging
        initDb();

        // Get a handle on the attribute registry to check if attributes are binary
        registry = dsConfig.getGlobalRegistries().getAttributeTypeRegistry();

        log.info( "# -----------------------------------------------------------------------------" );
        log.info( "# Initializing changelog service: " + DateUtils.getGeneralizedTime() );
        log.info( "# -----------------------------------------------------------------------------" );

        writer = new Thread( this );
        isActive = true;
        writer.start();
    }


    public void destroy()
    {
        // Gracefully stop writer thread and push remaining enqueued buffers ourselves
        isActive = false;
        do
        {
            // Let's notify the writer thread to make it die faster
            synchronized ( queue )
            {
                queue.notifyAll();
            }

            // Sleep tiny bit waiting for the writer to die
            try
            {
                Thread.sleep( 50 );
            }
            catch ( InterruptedException e )
            {
                log.error( "Failed to sleep while waiting for writer to die", e );
            }
        }
        while ( writer.isAlive() );

        // Ok lock down queue and start draining it
        synchronized ( queue )
        {
            while ( ! queue.isEmpty() )
            {
                ChangeEvent changeEvent = ( ChangeEvent ) queue.getFirst();
                if ( changeEvent != null )
                {
                    writeChangeEventToDb( changeEvent );
                }
            }
        }

        shutdownDb();

        log.info( "# -----------------------------------------------------------------------------" );
        log.info( "# Deactivating changelog service: " + DateUtils.getGeneralizedTime() );
        log.info( "# -----------------------------------------------------------------------------" );

        super.destroy();
    }


    // -----------------------------------------------------------------------
    // Implementation for Runnable.run() for writer Thread
    // -----------------------------------------------------------------------

    public void run()
    {
        while ( isActive )
        {
            ChangeEvent changeEvent = null;

            // Grab semphore to queue and dequeue from it
            synchronized ( queue )
            {
                try
                {
                    queue.wait( WAIT_TIMEOUT_MILLIS );
                }
                catch ( InterruptedException e )
                {
                    log.error( "Failed to to wait() on queue", e );
                }

                // replacing following jdk 1.5 poll() function with equivalent 1.4 functions
                // changeEvent = ( ChangeEvent ) queue.poll();
                if ( queue.size() == 0 )
                {
                    changeEvent = null;
                }
                else
                {
                    changeEvent = ( ChangeEvent ) queue.removeFirst();
                }

                queue.notifyAll();
            }

            // Do writing outside of synch block to allow other threads to enqueue
            if ( changeEvent != null )
            {
                writeChangeEventToDb( changeEvent );
            }
        }
    }


    // -----------------------------------------------------------------------
    // Overridden (only change inducing) intercepted methods
    // -----------------------------------------------------------------------

    public void add( NextInterceptor next, LdapDN nDn, Attributes entry ) throws NamingException
    {
        next.add( nDn, entry );

        if ( ! isActive )
        {
            return;
        }

        AddChangeEvent changeEvent = new AddChangeEvent( 0, nDn.getUpName(), getPrincipalName(), 
            new Date(), AttributeUtils.attributesToStringAttributeList( entry, registry ) );
        
        // Enqueue the buffer onto a queue that is emptied by another thread asynchronously.
        synchronized ( queue )
        {
            queue.addLast( changeEvent );
            queue.notifyAll();
        }
    }


    public void delete( NextInterceptor next, LdapDN nDn ) throws NamingException
    {
        next.delete( nDn );

        if ( ! isActive )
        {
            return;
        }

        DeleteChangeEvent changeEvent = new DeleteChangeEvent( 0, nDn.toString(), getPrincipalName(), new Date() ); 
        
        // Enqueue the buffer onto a queue that is emptied by another thread asynchronously.
        synchronized ( queue )
        {
            queue.addLast( changeEvent );
            queue.notifyAll();
        }
    }


    public void modify( NextInterceptor next, LdapDN nDn, int modOp, Attributes mods ) throws NamingException
    {
        /**
         * TODO: We need to deep copy here (before invoking the next
         * interceptor) attributes that are being deleted or replaced. They
         * should be gathered from the DIT while we do not have them passed
         * here. BTW, logging this detailed information can also be necessary
         * for "rollback support".
         * 
         * This issue is also valid for other similar operations.
         */

        next.modify( nDn, modOp, mods );

        if ( ! isActive )
        {
            return;
        }
        
        List strAttributes = AttributeUtils.attributesToStringAttributeList( mods, registry );
        ModifyChangeEvent changeEvent = new ModifyChangeEvent( 0, nDn.toString(), getPrincipalName(), new Date() );
        changeEvent.addModificationAttributes( modOp, strAttributes );
        
        // Enqueue the buffer onto a queue that is emptied by another thread asynchronously.
        synchronized ( queue )
        {
            queue.addLast( changeEvent );
            queue.notifyAll();
        }

    }


    public void modify( NextInterceptor next, LdapDN nDn, ModificationItem[] mods ) throws NamingException
    {

        next.modify( nDn, mods );

        if ( ! isActive )
        {
            return;
        }

        ModifyChangeEvent changeEvent = new ModifyChangeEvent( 0, nDn.toString(), getPrincipalName(), new Date() );
        
        for (int i = 0; i < mods.length; i++ )
        {
            StringAttribute strAttribute = AttributeUtils.attributeToStringAttribute( mods[i].getAttribute(), registry );
            changeEvent.addModificationAttribute( mods[i].getModificationOp(), strAttribute );
        }

        // Enqueue the buffer onto a queue that is emptied by another thread asynchronously.
        synchronized ( queue )
        {
            queue.addLast( changeEvent );
            queue.notifyAll();
        }
    }
    
    
    public void modifyRn( NextInterceptor next, LdapDN name, String newRn, boolean deleteOldRn ) throws NamingException
    {
        
        next.modifyRn( name, newRn, deleteOldRn );
        
        if ( ! isActive )
        {
            return;
        }
        
        ModifyRdnChangeEvent changeEvent = new ModifyRdnChangeEvent( 
            0,
            name.toString(), 
            getPrincipalName(), 
            new Date(), 
            newRn, 
            deleteOldRn);

        // Enqueue the buffer onto a queue that is emptied by another thread asynchronously.
        synchronized ( queue )
        {
            queue.addLast( changeEvent );
            queue.notifyAll();
        }       
        
    }
    
    
    public void move( NextInterceptor next, LdapDN oldName, LdapDN newParentName, String newRn, boolean deleteOldRn ) throws NamingException
    {
        
        next.move( oldName, newParentName, newRn, deleteOldRn );

        if ( ! isActive )
        {
            return;
        }

        String newDn = newRn + "," + newParentName;
        ModifyDnChangeEvent changeEvent = new ModifyDnChangeEvent( 
            0,
            oldName.toString(), 
            getPrincipalName(), 
            new Date(), 
            newDn, 
            deleteOldRn );

        // Enqueue the buffer onto a queue that is emptied by another thread asynchronously.
        synchronized ( queue )
        {
            queue.addLast( changeEvent );
            queue.notifyAll();
        }
        
    }
      
    
    public void move( NextInterceptor next, LdapDN oldName, LdapDN newParentName ) throws NamingException
    {
        next.move( oldName, newParentName );
        
        if ( ! isActive )
        {
            return;
        }
        
        String newDn = NamespaceTools.getRdn( oldName.toString() ) + "," + newParentName;
        ModifyDnChangeEvent changeEvent = new ModifyDnChangeEvent( 
            0,
            oldName.toString(), 
            getPrincipalName(), 
            new Date(), 
            newDn, 
            false );
        
        // Enqueue the buffer onto a queue that is emptied by another thread asynchronously.
        synchronized ( queue )
        {
            queue.addLast( changeEvent );
            queue.notifyAll();
        }
    }
     

    // -----------------------------------------------------------------------
    // Private utility methods used by interceptor methods
    // -----------------------------------------------------------------------

    /**
     * Gets the DN of the user currently bound to the server executing this
     * operation. If the user is anonymous "" is returned.
     * 
     * @return the DN of the user executing the current intercepted operation
     * @throws NamingException
     *             if we cannot access the interceptor stack
     */
    private String getPrincipalName() throws NamingException
    {
        
        ServerContext ctx = ( ServerContext ) InvocationStack.getInstance().peek().getCaller();
        return ctx.getPrincipal().getName();
        
    }
    

    // -----------------------------------------------------------------------
    // Private utility methods for DB access
    // -----------------------------------------------------------------------

    private void initDb() throws NamingException
    {

        Properties props = new Properties();
        props.put( "user", dbUserName );
        props.put( "password", dbPassword );

        try
        {
            conn = DriverManager.getConnection( dbProtocol + dbName, props );
        }
        catch ( SQLException e )
        {
            NamingException ne = new NamingException();
            ne.setRootCause( e );
            throw ne;
        }

    }


    private void writeChangeEventToDb( ChangeEvent changeEvent )
    {
        
        PreparedStatement insertStatement = null;

        try
        {
            insertStatement = conn.prepareStatement( "insert into " + dbTable + " values (DEFAULT, ?, ?, ?, ?, ?)" );
            insertStatement.setInt( 1, changeEvent.getEventType() );
            insertStatement.setString( 2, changeEvent.getAffectedEntryName() );
            insertStatement.setString( 3, changeEvent.getPrincipalName() );
            insertStatement.setTimestamp( 4, new Timestamp( changeEvent.getEventTime().getTime() ) );
            insertStatement.setString( 5, changeEvent.getEventMessage() );
            insertStatement.execute();
        }
        catch ( SQLException e )
        {
            log.error( "Failed to create the record insertion SQL prepared statement: " + e );
        }
        finally
        {
            if ( insertStatement != null )
            {
                try
                {
                    insertStatement.close();
                }
                catch ( SQLException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void shutdownDb()
    {
        
        if ( conn == null )
        {
            return;
        }

        try
        {
            conn.close();
        }
        catch ( SQLException e )
        {
            log.error( "Cannot close DB connection: " + e );
            e.printStackTrace();
        }

    }

}
