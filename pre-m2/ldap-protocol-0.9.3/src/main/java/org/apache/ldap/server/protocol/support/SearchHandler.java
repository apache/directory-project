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
package org.apache.ldap.server.protocol.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.ldap.common.exception.LdapException;
import org.apache.ldap.common.message.*;
import org.apache.ldap.common.name.LdapName;
import org.apache.ldap.common.util.ArrayUtils;
import org.apache.ldap.common.util.ExceptionUtils;
import org.apache.ldap.common.filter.PresenceNode;
import org.apache.ldap.server.jndi.ServerLdapContext;
import org.apache.ldap.server.protocol.SessionRegistry;
import org.apache.ldap.server.configuration.StartupConfiguration;
import org.apache.ldap.server.configuration.Configuration;
import org.apache.mina.protocol.ProtocolSession;
import org.apache.mina.protocol.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A handler for processing search requests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SearchHandler implements MessageHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( SearchHandler.class );
    private static final String DEREFALIASES_KEY = "java.naming.ldap.derefAliases";


    public void messageReceived( ProtocolSession session, Object request )
    {
        LdapContext ctx;
        SearchRequest req = ( SearchRequest ) request;
        NamingEnumeration list = null;

        // check the attributes to see if a referral's ref attribute is included
        String[] ids = null;
        Collection retAttrs = new HashSet();
        retAttrs.addAll( req.getAttributes() );

        if( retAttrs.size() > 0 && !retAttrs.contains( "ref" ) )
        {
            retAttrs.add( "ref" );
            ids = ( String[] ) retAttrs.toArray( ArrayUtils.EMPTY_STRING_ARRAY );
        }
        else if( retAttrs.size() > 0 )
        {
            ids = ( String[] ) retAttrs.toArray( ArrayUtils.EMPTY_STRING_ARRAY );
        }

        // prepare all the search controls
        SearchControls controls = new SearchControls();
        controls.setCountLimit( req.getSizeLimit() );
        controls.setTimeLimit( req.getTimeLimit() );
        controls.setSearchScope( req.getScope().getValue() );
        controls.setReturningObjFlag( req.getTypesOnly() );
        controls.setReturningAttributes( ids );
        controls.setDerefLinkFlag( true );

        try
        {
            boolean isBaseIsRoot = req.getBase().trim().equals( "" );
            boolean isBaseScope = req.getScope() == ScopeEnum.BASEOBJECT;
            boolean isRootDSEFilter = false;
            if ( req.getFilter() instanceof PresenceNode )
            {
                isRootDSEFilter = ( ( PresenceNode ) req.getFilter() ).getAttribute().equalsIgnoreCase( "objectClass" );
            }
            boolean isRootDSESearch = isBaseIsRoot && isBaseScope && isRootDSEFilter;

            // bypass checks to disallow anonymous binds for search on RootDSE with base obj scope
            if ( isRootDSESearch )
            {
                ctx = SessionRegistry.getSingleton().getLdapContextOnRootDSEAccess( session, null );
            }
            // all those search operations are subject to anonymous bind checks when anonymous binda are disallowed
            else
            {
                ctx = ( LdapContext ) SessionRegistry.getSingleton().getLdapContext( session, null, true ).lookup( "" );
            }

            if ( ! ( ctx instanceof ServerLdapContext ) )
            {
                ctx = ( ServerLdapContext ) ctx.lookup( "" );
            }

            StartupConfiguration cfg = ( StartupConfiguration ) Configuration.toConfiguration( ctx.getEnvironment() );
            boolean allowAnonymousBinds = cfg.isAllowAnonymousAccess();
            boolean isAnonymousUser = ( ( ServerLdapContext ) ctx ).getPrincipal().getName().trim().equals( "" );

            if ( isAnonymousUser && ! allowAnonymousBinds && ! isRootDSESearch )
            {
                SearchResponseDone resp = new SearchResponseDoneImpl( req.getMessageId() );
                LdapResultImpl result = new LdapResultImpl( resp );
                resp.setLdapResult( result );
                result.setResultCode( ResultCodeEnum.INSUFFICIENTACCESSRIGHTS );
                String msg = "Bind failure: Anonymous binds have been disabled!";
                result.setErrorMessage( msg );
                session.write( resp );
                return;
            }

            ctx.addToEnvironment( DEREFALIASES_KEY, req.getDerefAliases().getName() );
            list = ( ( ServerLdapContext ) ctx ).search( new LdapName( req.getBase() ), req.getFilter(), controls );

            if( list.hasMore() )
            {
                Iterator it = new SearchResponseIterator( req, list );
                while( it.hasNext() )
                {
                    session.write( it.next() );
                }

                return;
            }
            else
            {
                list.close();
                SearchResponseDone resp = new SearchResponseDoneImpl( req.getMessageId() );
                resp.setLdapResult( new LdapResultImpl( resp ) );
                resp.getLdapResult().setResultCode( ResultCodeEnum.SUCCESS );
                resp.getLdapResult().setMatchedDn( req.getBase() );
                Iterator it = Collections.singleton( resp ).iterator();

                while( it.hasNext() )
                {
                    session.write( it.next() );
                }

                return;
            }
        }
        catch( NamingException e )
        {
            String msg = "failed on search operation";

            if ( LOG.isDebugEnabled() )
            {
                msg += ":\n" + req + ":\n" + ExceptionUtils.getStackTrace( e );
            }

            SearchResponseDone resp = new SearchResponseDoneImpl( req.getMessageId() );
            ResultCodeEnum rc = null;

            if( e instanceof LdapException )
            {
                rc = ( ( LdapException ) e ).getResultCode();
            }
            else
            {
                rc = ResultCodeEnum.getBestEstimate( e, req.getType() );
            }

            resp.setLdapResult( new LdapResultImpl( resp ) );
            resp.getLdapResult().setResultCode( rc );
            resp.getLdapResult().setErrorMessage( msg );

            if( e.getResolvedName() != null )
            {
                resp.getLdapResult().setMatchedDn( e.getResolvedName().toString() );
            }
            else
            {
                resp.getLdapResult().setMatchedDn( "" );
            }

            Iterator it = Collections.singleton( resp ).iterator();

            while( it.hasNext() )
            {
                session.write( it.next() );
            }
        }
    }


    SearchResponseDone getResponse( SearchRequest req, NamingException e )
    {
        String msg = "failed on search operation";

        if ( LOG.isDebugEnabled() )
        {
            msg += ":\n" + req + ":\n" + ExceptionUtils.getStackTrace( e );
        }

        SearchResponseDone resp = new SearchResponseDoneImpl( req.getMessageId() );

        ResultCodeEnum rc = null;

        if( e instanceof LdapException )
        {
            rc = ( ( LdapException ) e ).getResultCode();
        }
        else
        {
            rc = ResultCodeEnum.getBestEstimate( e, req.getType() );
        }

        resp.setLdapResult( new LdapResultImpl( resp ) );

        resp.getLdapResult().setResultCode( rc );

        resp.getLdapResult().setErrorMessage( msg );

        if( e.getResolvedName() != null )
        {
            resp.getLdapResult().setMatchedDn( e.getResolvedName().toString() );
        }
        else
        {
            resp.getLdapResult().setMatchedDn( "" );
        }

        return resp;
    }

    class SearchResponseIterator implements Iterator
    {
        private final SearchRequest req;

        private final NamingEnumeration underlying;

        private SearchResponseDone respDone;

        private boolean done = false;

        private Object prefetched;

        /**
         * Creates a search response iterator for the resulting enumeration
         * over a search request.
         *
         * @param req the search request to generate responses to
         * @param underlying the underlying JNDI enumeration containing SearchResults
         */
        public SearchResponseIterator( SearchRequest req,
                                      NamingEnumeration underlying )
        {
            this.req = req;
            this.underlying = underlying;

            try
            {
                if( underlying.hasMore() )
                {
                    SearchResult result = ( SearchResult ) underlying.next();

                    /*
                     * Now we have to build the prefetched object from the 'result'
                     * local variable for the following call to next()
                     */
                    Attribute ref = result.getAttributes().get( "ref" );

                    if( ref == null || ref.size() > 0 )
                    {
                        SearchResponseEntry respEntry;

                        respEntry = new SearchResponseEntryImpl( req.getMessageId() );

                        respEntry.setAttributes( result.getAttributes() );

                        respEntry.setObjectName( result.getName() );

                        prefetched = respEntry;
                    }
                    else
                    {
                        SearchResponseReference respRef;

                        respRef = new SearchResponseReferenceImpl( req.getMessageId() );

                        respRef.setReferral( new ReferralImpl( respRef ) );

                        for( int ii = 0; ii < ref.size(); ii ++ )
                        {
                            String url;

                            try
                            {
                                url = ( String ) ref.get( ii );

                                respRef.getReferral().addLdapUrl( url );
                            }
                            catch( NamingException e )
                            {
                                try
                                {
                                    underlying.close();
                                }
                                catch( Throwable t )
                                {
                                }

                                prefetched = null;

                                respDone = getResponse( req, e );
                            }
                        }

                        prefetched = respRef;
                    }
                }
            }
            catch( NamingException e )
            {
                try
                {
                    this.underlying.close();
                }
                catch( Exception e2 )
                {
                }

                respDone = getResponse( req, e );
            }
        }

        public boolean hasNext()
        {
            return !done;
        }

        public Object next()
        {
            Object next = prefetched;

            SearchResult result = null;

            // if we're done we got nothing to give back
            if( done )
            {
                throw new NoSuchElementException();
            }

            // if respDone has been assembled this is our last object to return
            if( respDone != null )
            {
                done = true;

                return respDone;
            }

            /*
             * If we have gotten this far then we have a valid next entry
             * or referral to return from this call in the 'next' variable.
             */
            try
            {
                /*
                 * If we have more results from the underlying cursorr then
                 * we just set the result and build the response object below.
                 */
                if( underlying.hasMore() )
                {
                    result = ( SearchResult ) underlying.next();
                }
                else
                {
                    try
                    {
                        underlying.close();
                    }
                    catch( Throwable t )
                    {
                    }

                    respDone = new SearchResponseDoneImpl( req.getMessageId() );

                    respDone.setLdapResult( new LdapResultImpl( respDone ) );

                    respDone.getLdapResult().setResultCode( ResultCodeEnum.SUCCESS );

                    respDone.getLdapResult().setMatchedDn( req.getBase() );

                    prefetched = null;

                    return next;
                }
            }
            catch( NamingException e )
            {
                try
                {
                    underlying.close();
                }
                catch( Throwable t )
                {
                }

                prefetched = null;

                respDone = getResponse( req, e );

                return next;
            }

            /*
             * Now we have to build the prefetched object from the 'result'
             * local variable for the following call to next()
             */
            Attribute ref = result.getAttributes().get( "ref" );

            if( ref == null || ref.size() > 0 )
            {
                SearchResponseEntry respEntry = new SearchResponseEntryImpl( req.getMessageId() );

                respEntry.setAttributes( result.getAttributes() );

                respEntry.setObjectName( result.getName() );

                prefetched = respEntry;
            }
            else
            {
                SearchResponseReference respRef = new SearchResponseReferenceImpl( req.getMessageId() );

                respRef.setReferral( new ReferralImpl( respRef ) );

                for( int ii = 0; ii < ref.size(); ii ++ )
                {
                    String url;

                    try
                    {
                        url = ( String ) ref.get( ii );

                        respRef.getReferral().addLdapUrl( url );
                    }
                    catch( NamingException e )
                    {
                        try
                        {
                            underlying.close();
                        }
                        catch( Throwable t )
                        {
                        }

                        prefetched = null;

                        respDone = getResponse( req, e );

                        return next;
                    }
                }

                prefetched = respRef;
            }

            return next;
        }

        /**
         * Unsupported so it throws an exception.
         *
         * @throws UnsupportedOperationException
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
