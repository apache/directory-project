/*
 * @(#) $Id$
 */
package org.apache.mina.transport.vmpipe;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.IoFilterChainBuilder;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.support.BaseIoAcceptor;
import org.apache.mina.transport.vmpipe.support.VmPipe;

/**
 * Binds the specified {@link IoHandler} to the specified
 * {@link VmPipeAddress}.
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public class VmPipeAcceptor extends BaseIoAcceptor
{
    static final Map boundHandlers = new HashMap();

    public void bind( SocketAddress address, IoHandler handler, IoFilterChainBuilder filterChainBuilder ) throws IOException
    {
        if( address == null )
            throw new NullPointerException( "address" );
        if( handler == null )
            throw new NullPointerException( "handler" );
        if( !( address instanceof VmPipeAddress ) )
            throw new IllegalArgumentException(
                    "address must be VmPipeAddress." );

        if( filterChainBuilder == null )
        {
            filterChainBuilder = IoFilterChainBuilder.NOOP;
        }

        synchronized( boundHandlers )
        {
            if( boundHandlers.containsKey( address ) )
            {
                throw new IOException( "Address already bound: " + address );
            }

            boundHandlers.put( address, 
                               new VmPipe( this,
                                          ( VmPipeAddress ) address,
                                          handler, filterChainBuilder ) );
        }
    }

    public void unbind( SocketAddress address )
    {
        // TODO: DIRMINA-93
        if( address == null )
            throw new NullPointerException( "address" );

        synchronized( boundHandlers )
        {
            boundHandlers.remove( address );
        }
    }
}
