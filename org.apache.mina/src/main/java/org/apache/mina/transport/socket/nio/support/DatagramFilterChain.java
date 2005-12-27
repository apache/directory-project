package org.apache.mina.transport.socket.nio.support;

import org.apache.mina.common.CloseFuture;
import org.apache.mina.common.IoFilterChain;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoFilter.WriteRequest;
import org.apache.mina.common.support.AbstractIoFilterChain;
import org.apache.mina.util.Queue;

/**
 * An {@link IoFilterChain} for datagram transport (UDP/IP).
 * 
 * @author The Apache Directory Project
 */
class DatagramFilterChain extends AbstractIoFilterChain {

    DatagramFilterChain( IoSession parent )
    {
        super( parent );
    }
    
    protected void doWrite( IoSession session, WriteRequest writeRequest )
    {
        DatagramSessionImpl s = ( DatagramSessionImpl ) session;
        Queue writeRequestQueue = s.getWriteRequestQueue();
        
        synchronized( writeRequestQueue )
        {
            writeRequestQueue.push( writeRequest );
            if( writeRequestQueue.size() == 1 && session.getTrafficMask().isWritable() )
            {
                // Notify DatagramSessionManager only when writeRequestQueue was empty.
                s.getManagerDelegate().flushSession( s );
            }
        }
    }

    protected void doClose( IoSession session, CloseFuture closeFuture )
    {
        DatagramSessionImpl s = ( DatagramSessionImpl ) session;
        DatagramSessionManager manager = s.getManagerDelegate();
        if( manager instanceof DatagramConnectorDelegate )
        {
            ( ( DatagramConnectorDelegate ) manager ).closeSession( s );
        }
        else
        {
            closeFuture.setClosed();
        }
    }
}
