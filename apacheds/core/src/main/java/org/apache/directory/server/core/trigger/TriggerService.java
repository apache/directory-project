/*
 *   Copyright 2006 The Apache Software Foundation
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

package org.apache.directory.server.core.trigger;


import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.directory.server.core.DirectoryServiceConfiguration;
import org.apache.directory.server.core.authn.LdapPrincipal;
import org.apache.directory.server.core.configuration.InterceptorConfiguration;
import org.apache.directory.server.core.interceptor.BaseInterceptor;
import org.apache.directory.server.core.interceptor.NextInterceptor;
import org.apache.directory.server.core.invocation.Invocation;
import org.apache.directory.server.core.invocation.InvocationStack;
import org.apache.directory.server.core.jndi.ServerContext;
import org.apache.directory.server.core.jndi.ServerLdapContext;
import org.apache.directory.server.core.partition.DirectoryPartitionNexusProxy;
import org.apache.directory.server.core.schema.AttributeTypeRegistry;
import org.apache.directory.server.core.sp.LdapClassLoader;
import org.apache.directory.shared.ldap.exception.LdapNamingException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.schema.NormalizerMappingResolver;
import org.apache.directory.shared.ldap.trigger.ActionTime;
import org.apache.directory.shared.ldap.trigger.LdapOperation;
import org.apache.directory.shared.ldap.trigger.TriggerSpecification;
import org.apache.directory.shared.ldap.trigger.TriggerSpecificationParser;
import org.apache.directory.shared.ldap.util.DirectoryClassUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Trigger Service based on the Trigger Specification.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev:$
 */
public class TriggerService extends BaseInterceptor
{
    /** the logger for this class */
    private static final Logger log = LoggerFactory.getLogger( TriggerService.class );
    
    /** the entry trigger attribute string: entryTrigger */
    private static final String ENTRY_TRIGGER_ATTR = "entryTrigger";

    /**
     * the multivalued operational attribute used to track the prescriptive
     * trigger subentries that apply to an entry
     */
    private static final String TRIGGER_SUBENTRIES_ATTR = "triggerSubentries";


    /** a triggerSpecCache that responds to add, delete, and modify attempts */
    private TriggerSpecCache triggerSpecCache;
    /** a normalizing Trigger Specification parser */
    private TriggerSpecificationParser triggerParser;
    /** the attribute type registry */
    private AttributeTypeRegistry attrRegistry;
    /** whether or not this interceptor is activated */
    private boolean enabled = true;

    /**
     * Adds prescriptiveTrigger TriggerSpecificaitons to a collection of
     * TriggerSpeficaitions by accessing the tupleCache.  The trigger specification
     * cache is accessed for each trigger subentry associated with the entry.
     * Note that subentries are handled differently: their parent, the administrative
     * entry is accessed to determine the perscriptiveTriggers effecting the AP
     * and hence the subentry which is considered to be in the same context.
     *
     * @param triggerSpecs the collection of trigger specifications to add to
     * @param dn the normalized distinguished name of the entry
     * @param entry the target entry that is considered as the trigger source
     * @throws NamingException if there are problems accessing attribute values
     */
    private void addPrescriptiveTriggerSpecs( List triggerSpecs, DirectoryPartitionNexusProxy proxy,
        LdapDN dn, Attributes entry ) throws NamingException
    {
        
        /*
         * If the protected entry is a subentry, then the entry being evaluated
         * for perscriptiveTriggerss is in fact the administrative entry.  By
         * substituting the administrative entry for the actual subentry the
         * code below this "if" statement correctly evaluates the effects of
         * perscriptiveTrigger on the subentry.  Basically subentries are considered
         * to be in the same naming context as their access point so the subentries
         * effecting their parent entry applies to them as well.
         */
        if ( entry.get( "objectClass" ).contains( "subentry" ) )
        {
            LdapDN parentDn = ( LdapDN ) dn.clone();
            parentDn.remove( dn.size() - 1 );
            entry = proxy.lookup( parentDn, DirectoryPartitionNexusProxy.LOOKUP_BYPASS );
        }

        Attribute subentries = entry.get( TRIGGER_SUBENTRIES_ATTR );
        if ( subentries == null )
        {
            return;
        }
        for ( int ii = 0; ii < subentries.size(); ii++ )
        {
            String subentryDn = ( String ) subentries.get( ii );
            triggerSpecs.addAll( triggerSpecCache.getSubentryTriggerSpecs( subentryDn ) );
        }
    }

    /**
     * Adds the set of entryTriggers to a collection of trigger specifications.
     * The entryTrigger is parsed and tuples are generated on they fly then
     * added to the collection.
     *
     * @param triggerSpecs the collection of trigger specifications to add to
     * @param entry the target entry that is considered as the trigger source
     * @throws NamingException if there are problems accessing attribute values
     */
    private void addEntryTriggerSpecs( Collection triggerSpecs, Attributes entry ) throws NamingException
    {
        Attribute entryTrigger = entry.get( ENTRY_TRIGGER_ATTR );
        if ( entryTrigger == null )
        {
            return;
        }

        for ( int ii = 0; ii < entryTrigger.size(); ii++ )
        {
            String triggerString = ( String ) entryTrigger.get( ii );
            TriggerSpecification item;

            try
            {
                item = triggerParser.parse( triggerString );
            }
            catch ( ParseException e )
            {
                String msg = "failed to parse entryTrigger: " + triggerString;
                log.error( msg, e );
                throw new LdapNamingException( msg, ResultCodeEnum.OPERATIONSERROR );
            }

            triggerSpecs.add( item );
        }
    }
    
    public Map getActionTimeMappedTriggerSpecsForOperation( List triggerSpecs, LdapOperation ldapOperation )
    {
        List beforeTriggerSpecs = new ArrayList();
        List insteadofTriggerSpecs = new ArrayList();
        List afterTriggerSpecs = new ArrayList();
        Map triggerSpecMap = new HashMap();
        
        Iterator it = triggerSpecs.iterator();
        while ( it.hasNext() )
        {
            TriggerSpecification triggerSpec = ( TriggerSpecification ) it.next();
            if ( triggerSpec.getLdapOperation().equals( ldapOperation ) )
            {
                if ( triggerSpec.getActionTime().equals( ActionTime.BEFORE ) )
                {
                    beforeTriggerSpecs.add( triggerSpec );
                }
                else if ( triggerSpec.getActionTime().equals( ActionTime.INSTEADOF ) )
                {
                    insteadofTriggerSpecs.add( triggerSpec );
                }
                else if ( triggerSpec.getActionTime().equals( ActionTime.AFTER ) )
                {
                    afterTriggerSpecs.add( triggerSpec );
                }
                else
                {
                    // TODO
                }    
            }
        }
        
        triggerSpecMap.put( ActionTime.BEFORE, beforeTriggerSpecs );
        triggerSpecMap.put( ActionTime.INSTEADOF, insteadofTriggerSpecs );
        triggerSpecMap.put( ActionTime.AFTER, afterTriggerSpecs );
        
        return triggerSpecMap;
    }    

    /**
     * Initializes this interceptor based service by getting a handle on the nexus.
     *
     * @param dirServCfg the ContextFactory configuration for the server
     * @param intCfg the interceptor configuration
     * @throws NamingException if there are problems during initialization
     */
    public void init( DirectoryServiceConfiguration dirServCfg, InterceptorConfiguration intCfg ) throws NamingException
    {
        super.init( dirServCfg, intCfg );
        triggerSpecCache = new TriggerSpecCache( dirServCfg );
        attrRegistry = dirServCfg.getGlobalRegistries().getAttributeTypeRegistry();
        triggerParser = new TriggerSpecificationParser( new NormalizerMappingResolver()
            {
                public Map getNormalizerMapping() throws NamingException
                {
                    return attrRegistry.getNormalizerMapping();
                }
            });
        this.enabled = true; // TODO: get this from the configuration if needed
    }


    public void add( NextInterceptor next, LdapDN normName, Attributes entry ) throws NamingException
    {
        // Access the principal requesting the operation
        Invocation invocation = InvocationStack.getInstance().peek();
        LdapPrincipal principal = ( ( ServerContext ) invocation.getCaller() ).getPrincipal();
        LdapDN userName = new LdapDN( principal.getName() );
        userName.normalize();

        // Bypass trigger code if we are disabled
        if ( !enabled )
        {
            next.add( normName, entry );
            return;
        }
        
        /**
         * 
         */
        next.add( normName, entry );
        
        triggerSpecCache.subentryAdded( normName, entry );
        
    }

    public void delete( NextInterceptor next, LdapDN name ) throws NamingException
    {
        // Access the principal requesting the operation
        Invocation invocation = InvocationStack.getInstance().peek();
        DirectoryPartitionNexusProxy proxy = invocation.getProxy();
        Attributes entry = proxy.lookup( name, DirectoryPartitionNexusProxy.LOOKUP_BYPASS );
        LdapPrincipal principal = ( ( ServerContext ) invocation.getCaller() ).getPrincipal();
        LdapDN userName = new LdapDN( principal.getName() );
        userName.normalize();
        
        ServerLdapContext ctx = ( ServerLdapContext ) ( ( ServerLdapContext ) invocation.getCaller() ).getRootContext();

        // Bypass trigger code if we are disabled
        if ( !enabled )
        {
            next.delete( name );
            return;
        }

        List triggerSpecs = new ArrayList();
        addPrescriptiveTriggerSpecs( triggerSpecs, proxy, name, entry );
        addEntryTriggerSpecs( triggerSpecs, entry );
        Map triggerMap = getActionTimeMappedTriggerSpecsForOperation( triggerSpecs, LdapOperation.DELETE );
        
        DeleteStoredProcedureParameterInjector injector = new DeleteStoredProcedureParameterInjector( invocation, name );
        
        List beforeTriggerSpecs = (List) triggerMap.get( ActionTime.BEFORE );
        log.debug( "There are " + beforeTriggerSpecs.size() + " \"BEFORE delete\" triggers associated with this entry [" + name + "] being deleted:" );
        log.debug( ">>> " + beforeTriggerSpecs );
        
        List insteadofTriggerSpecs = (List) triggerMap.get( ActionTime.INSTEADOF );
        log.debug( "There are " + insteadofTriggerSpecs.size() + " \"INSTEADOF delete\" triggers associated with this entry [" + name + "] being deleted:" );
        log.debug( ">>> " + insteadofTriggerSpecs );
        
        if ( insteadofTriggerSpecs.size() == 0 )
        {
            next.delete( name );
            // we call subentryDeleted when there is really no INSTEADOF triggers for this method
            triggerSpecCache.subentryDeleted( name, entry );
        }
        else
        {
            log.debug("Delete operation has not been performed due to the INSTEADOF trigger(s).");
        }
        
        List afterTriggerSpecs = (List) triggerMap.get( ActionTime.AFTER );
        log.debug( "There are " + afterTriggerSpecs.size() + " \"AFTER delete\" triggers associated with this entry [" + name + "] being deleted:" );
        log.debug( ">>> " + afterTriggerSpecs );
        
        Iterator it = afterTriggerSpecs.iterator();
        
        while( it.hasNext() )
        {
            TriggerSpecification tsec = ( TriggerSpecification ) it.next();
            
            List arguments = new ArrayList();
            arguments.add( ctx );
            arguments.addAll( injector.getArgumentsToInject( tsec.getStoredProcedureParameters() ) );
            
            List typeList = new ArrayList();
            typeList.add( ctx.getClass() );
            typeList.addAll( getTypesFromValues( arguments ) );
            
            Class[] types = ( Class[] ) ( getTypesFromValues( arguments ).toArray( EMPTY_CLASS_ARRAY ) );
            Object[] values = arguments.toArray();
            
            executeProcedure( ctx, tsec.getStoredProcedureName(), types, values );
        }
    }
    
    private static Class[] EMPTY_CLASS_ARRAY = new Class[0];
    
    private List getTypesFromValues( List objects )
    {
        List types = new ArrayList();
        
        Iterator it = objects.iterator();
        
        while( it.hasNext() )
        {
            types.add( it.next().getClass() );
        }
        
        return types;
    }
    
    private Object executeProcedure( ServerLdapContext ctx, String procedure, Class[] types, Object[] values ) throws NamingException
    {
        int lastDot = procedure.lastIndexOf( '.' );
        String className = procedure.substring( 0, lastDot );
        String methodName = procedure.substring( lastDot + 1 );
        LdapClassLoader loader = new LdapClassLoader( ctx );
        
        try
        {
            Class clazz = loader.loadClass( className );
            Method proc = DirectoryClassUtils.getAssignmentCompatibleMethod( clazz, methodName, types );
            return proc.invoke( null, values );
        }
        catch ( Exception e )
        {
            log.debug( "Exception occured during executing stored procedure:\n" +
                e.getMessage() + "\n" + e.getStackTrace() );
            LdapNamingException lne = new LdapNamingException( ResultCodeEnum.OTHER );
            lne.setRootCause( e );
            throw lne;
        }
    }

}
