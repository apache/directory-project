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
package org.safehaus.triplesec.store.interceptor;


import org.apache.directory.shared.ldap.message.LockableAttributeImpl;
import org.apache.directory.shared.ldap.message.LockableAttributesImpl;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.apache.directory.shared.ldap.exception.LdapNameAlreadyBoundException;
import org.apache.directory.server.core.invocation.InvocationStack;
import org.apache.directory.server.core.partition.PartitionNexusProxy;
import org.apache.directory.server.core.schema.AttributeTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import java.util.*;


/**
 * Automatically manages ACI creation, deletion and updates when applications are
 * created, deleted, renamed and moved.
 *
 * @author Alex Karasulu
 * @version $Rev: 64 $, $Date: 2005-11-03 01:43:49 -0500 (Thu, 03 Nov 2005) $
 */
public class ApplicationAciManager
{
    private static final Logger log = LoggerFactory.getLogger( ApplicationAciManager.class );
    private static final String APP_ACITAG_SUFFIX = "Aci";
    private static final String APP_ACITAG_SUFFIX_LOWER = APP_ACITAG_SUFFIX.toLowerCase();
    private static final String APPADMIN_ACITAG_SUFFIX = "AdminAci";
    private static final String APPADMIN_GROUP_SUFFIX = "AdminGroup";
    private static final String APPADMIN_GROUP_SUFFIX_LOWWER = APPADMIN_GROUP_SUFFIX.toLowerCase();
    private static final String[] RETURN_ADMINROLE = new String[] { "administrativeRole" };

    private static final Collection ADD_BYPASS;
    private static final Collection DEL_BYPASS;
    static final Collection LOOKUP_BYPASS;

    static
    {
        Collection c = new HashSet();
        c.add( "normalizationService" );
        c.add( "authenticationService" );
        c.add( "defaultAuthorizationService" );
        c.add( "schemaService" );
        c.add( "policyProtectionService" );
        c.add( "collectiveAttributeService" );
        ADD_BYPASS = Collections.unmodifiableCollection( c );
        DEL_BYPASS = Collections.unmodifiableCollection( c );

        c = new HashSet();
        c.addAll( PartitionNexusProxy.LOOKUP_BYPASS );
        c.add( "policyProtectionService" );
        LOOKUP_BYPASS = Collections.unmodifiableCollection( c );
    }

    /** LUT of normalized DNs for existing ou=groups entries of suffixes */
    private final Set groupsLut = new HashSet();
    /** LUT of normalized suffix DNs which are already ASCAs */
    private final Set acsaLut = new HashSet();

    private final AttributeTypeRegistry registry;
    private final AttributeType administrativeRoleType;
    
    
    public ApplicationAciManager( AttributeTypeRegistry registry ) throws NamingException
    {
        this.registry = registry;
        administrativeRoleType = registry.lookup( "administrativeRole" );
    }
    

    /**
     * Creates an ACIItem in a new subentry and adds it to the topmost AAA for application's to fully
     * access their subtree.  This method should be invoked immediately after the application entry
     * is created.
     *
     * @param upDn the user provided DN string for the entry being added
     * @param appDn the normalized DN for the entry being added
     */
    public void appAdded( LdapDN appDn ) throws NamingException
    {
        // get the current invocation object's proxy to access it's nexus proxy
        PartitionNexusProxy proxy = InvocationStack.getInstance().peek().getProxy();
        addApplicationAdminGroup( proxy, appDn );
        addApplicationSubentry( proxy, appDn );
    }


    /**
     * Deletes the access control subentry added to the top most AAA for application access.  This
     * method should be invoked immediately after the application entry is removed.
     */
    public void appRemoved( LdapDN appDn ) throws NamingException
    {
        // get the current invocation object's proxy to access it's nexus proxy
        PartitionNexusProxy proxy = InvocationStack.getInstance().peek().getProxy();
        removeApplicationAdminGroup( proxy, appDn );
        removeApplicationSubentry( proxy, appDn );
    }


    /**
     * Adjusts subtree specifications and ACI when applications are renamed or moved in the DIT.  This
     * operation is a little bit tricky.  Any name change on an application will remove it from the
     * scope of the AC subentry's subtreeSpecification.  It will also invalidate the ACIItem designed
     * to grant the application at it's old DN.  The subentry subsystem will automatically remove the
     * operational attributes for relating the entry at the old DN to this AC subentry.  At this point
     * it's just best to remove the old AC subentry with it's ACIItem and create the new one.  So this
     * boil's down to an add and a delete.
     *
     * When the number of entries under the application entry are large this will mean a big change.
     * All entries will have the old subentries operational attributes removed.  The new operational
     * entries will then be added.  The cost of this change is expensive.  However not that it is a
     * management operation that happens relatively infrequently.
     */
//    public void appNameChange( Name oldDn, String newUpDn, Name newDn ) throws NamingException
//    {
//        // get the current invocation object's proxy to access it's nexus proxy
//        DirectoryPartitionNexusProxy proxy = InvocationStack.getInstance().peek().getProxy();
//
//        // we don't need to mess around with deleting and adding the admin group (don't want to loose info either)
//        removeApplicationSubentry( proxy, oldDn );
//        addApplicationSubentry( proxy, newUpDn, newDn );
//    }


    private void removeApplicationAdminGroup( PartitionNexusProxy proxy, LdapDN appDn ) throws NamingException
    {
        // bypass all interceptors and ask for the partition suffix for this application's entry
        // use the suffix to build the normalized DN for the administrator group for the application
        LdapDN suffix = proxy.getSuffix( appDn, PartitionNexusProxy.BYPASS_ALL_COLLECTION );
        String appName = NamespaceTools.getRdnValue( appDn.get( appDn.size() - 1 ) );
        LdapDN groupDn = ( LdapDN ) suffix.clone();
        groupDn.add( "ou=groups" );
        StringBuffer buf = new StringBuffer();
        buf.setLength( 0 );
        buf.append( "cn=" );
        buf.append( appName.toLowerCase() );
        buf.append( APPADMIN_GROUP_SUFFIX_LOWWER );
        groupDn.add( buf.toString() );

        // blow away the group entry
        groupDn.normalize( registry.getNormalizerMapping() );
        proxy.delete( groupDn, DEL_BYPASS );
    }


    /**
     * Adds an administrator group specifically for the application.  The group name is inferred
     * from the name of the application.
     *
     * @param proxy the nexus proxy to perform an add operation if need be
     * @param appDn the normalized name for the application
     * @throws NamingException if add operations fail
     */
    private void addApplicationAdminGroup( PartitionNexusProxy proxy, LdapDN appDn ) throws NamingException
    {
        // bypass all interceptors and ask for the partition suffix for this application's entry
        // the suffix entry will be used as the administrative point for a ACSA starting at it
        LdapDN suffix = proxy.getSuffix( appDn, PartitionNexusProxy.BYPASS_ALL_COLLECTION );
        String appUpName = NamespaceTools.getRdnValue( appDn.getRdn().getUpName() );

        // calculate the names of the group container and create ou=groups if we have to
        LdapDN groupDn = ( LdapDN ) suffix.clone();
        groupDn.add( "ou=groups" );
        groupDn.normalize( registry.getNormalizerMapping() );
        createGroupsContainer( proxy, groupDn );

        // continue building the name for the new group entry off of ou=groups
        StringBuffer buf = new StringBuffer();
        Attribute cnAttr = new LockableAttributeImpl( "cn" );
        buf.append( appUpName );
        buf.append( APPADMIN_GROUP_SUFFIX );
        cnAttr.add( buf.toString() );
        buf.insert( 0, "cn=" );
        groupDn.add( buf.toString() );
        groupDn.normalize( registry.getNormalizerMapping() );

        // create the admin group entry
        Attributes group = new LockableAttributesImpl();
        group.put( "objectClass", "top" );
        group.get( "objectClass" ).add( "groupOfUniqueNames" );
        group.put( cnAttr );
        // not need since admin can do anything but we need one member at least
        group.put( "uniqueMember", "uid=admin,ou=system" );
        proxy.add( groupDn, group, ADD_BYPASS );
    }


    /**
     * Creates the group container ou=groups if it does not exist.
     *
     * @param proxy the nexus proxy to perform an add operation if need be
     * @param groupDn the normalized name for ou=groups under a suffix
     * @throws NamingException if add operations fail
     */
    private void createGroupsContainer( PartitionNexusProxy proxy, LdapDN groupDn ) throws NamingException
    {
        if ( groupsLut.contains( groupDn.getNormName() ) )
        {
            return;
        }

        Attributes groups = new LockableAttributesImpl();
        groups.put( "objectClass", "top" );
        groups.get( "objectClass" ).add( "organizationalUnit" );
        groups.put( "ou", "Groups" );

        try
        {
            proxy.add( groupDn, groups, ADD_BYPASS );
        }
        catch ( LdapNameAlreadyBoundException e )
        {
            if ( log.isInfoEnabled() )
            {
                log.info( "Could not add " + groupDn + " since it exists ... adding it to exists LUT.");
            }
        }
        groupsLut.add( groupDn.getNormName() );
    }



    void removeApplicationSubentry( PartitionNexusProxy proxy, LdapDN appDn ) throws NamingException
    {
        // bypass all interceptors and ask for the partition suffix for this application's entry
        // then calculate the normalized dn of the subentry to delete for this application
        LdapDN suffix = proxy.getSuffix( appDn, PartitionNexusProxy.BYPASS_ALL_COLLECTION );
        String appName = NamespaceTools.getRdnValue( appDn.get( appDn.size() - 1 ) );
        StringBuffer buf = new StringBuffer();
        buf.append( "cn=" );
        buf.append( appName.toLowerCase() );
        buf.append( APP_ACITAG_SUFFIX_LOWER );
        LdapDN subentryDn = ( LdapDN ) suffix.clone();
        subentryDn.add( buf.toString() );

        // delete the access control subentry
        subentryDn.normalize( registry.getNormalizerMapping() );
        proxy.delete( subentryDn, DEL_BYPASS );
    }


    /**
     * Adds an accessControl subentry to the suffix of the partition containing the entry.  If the suffix
     * is not the Administrative Point for the ACSA then it is promoted to one.
     *
     * @param proxy the nexus proxy to perform an add operation if need be
     * @param appDn the normalized name for the application entry being added
     * @throws NamingException if add operations fail
     */
    void addApplicationSubentry( PartitionNexusProxy proxy, LdapDN appDn ) throws NamingException
    {
        // bypass all interceptors and ask for the partition suffix for this application's entry
        // the suffix entry will be used as the administrative point for a ACSA starting at it
        LdapDN suffix = proxy.getSuffix( appDn, PartitionNexusProxy.BYPASS_ALL_COLLECTION );
        String appUpName = NamespaceTools.getRdnValue( appDn.getRdn().getUpName() );
        String appName = NamespaceTools.getRdnValue( appDn.get( appDn.size() - 1 ) );
        createAccessControlArea( proxy, suffix );

        // calculate the application admin group name for the application
        LdapDN groupDn = ( LdapDN ) suffix.clone();
        groupDn.add( "ou=Groups" );
        StringBuffer groupRdn = new StringBuffer();
        groupRdn.append( "cn=" );
        groupRdn.append( appUpName );
        groupRdn.append( APPADMIN_GROUP_SUFFIX );
        groupDn.add( groupRdn.toString() );
        groupDn.normalize( registry.getNormalizerMapping() );

        // calculate the name for the new subentry to create
        StringBuffer buf = new StringBuffer();
        buf.append( appName );
        buf.append( APP_ACITAG_SUFFIX );
        String aciTag = buf.toString();

        // calculate subentry attributes with both app user ACI and 
        // app admin group ACI in same subentry
        Attributes subentry = new LockableAttributesImpl();
        subentry.put( "objectClass", "top" );
        subentry.get( "objectClass" ).add( "subentry" );
        subentry.get( "objectClass" ).add( "accessControlSubentry" );
        subentry.put( "cn", aciTag );
        subentry.put( "subtreeSpecification", createSubtreeSpecification( suffix, appDn ) );
        subentry.put( "prescriptiveACI", createApplicationACIItem( aciTag, appDn ) );
        
        // now add another prescriptiveACI to the same subentry to allow 
        // read/write access by the admin group
        buf.setLength( 0 );
        buf.append( appName );
        buf.append( APPADMIN_ACITAG_SUFFIX );
        subentry.get( "prescriptiveACI" ).add( createApplicationAdminACIItem( buf.toString(), groupDn ) );

        // create the subentry
        buf.setLength( 0 );
        buf.append( "cn=" );
        buf.append( appUpName );
        buf.append( APP_ACITAG_SUFFIX );
        LdapDN subentryDn = ( LdapDN ) suffix.clone();
        subentryDn.add( buf.toString() );
        subentryDn.normalize( registry.getNormalizerMapping() );
        proxy.add( subentryDn, subentry, ADD_BYPASS );
    }


    /**
     * Checks cache to see if the entry at apDn is an access control specific area (ACSA), if
     * not the entry is accessed to check if it is an administrative point for an ACSA.  If
     * the entry is an ACSA AP, then the cache is updated.  If the entry is NOT an ACSA AP then
     * the entry at apDn is promoted to an ACSA.
     *
     * @param apDn
     * @throws NamingException
     */
    private void createAccessControlArea( PartitionNexusProxy proxy, LdapDN apDn ) throws NamingException
    {
        if ( acsaLut.contains( apDn.getNormName() ) )
        {
            return;
        }

        Attributes acsa = proxy.lookup( apDn, RETURN_ADMINROLE, LOOKUP_BYPASS );
        Attribute administrativeRole = AttributeUtils.getAttribute( acsa, administrativeRoleType );
        if ( administrativeRole != null )
        {
            for ( int ii = 0; ii < administrativeRole.size(); ii++ )
            {
                String role = ( String ) administrativeRole.get( ii );
                if ( role.equalsIgnoreCase( "accessControlSpecificArea" ) )
                {
                    acsaLut.add( apDn.toString() );
                    return;
                }
            }
        }

        Attributes mods = new LockableAttributesImpl();
        mods.put( "administrativeRole", "accessControlSpecificArea" );
        proxy.modify( apDn, DirContext.ADD_ATTRIBUTE, mods );
        acsaLut.add( apDn.getNormName() );
    }


    /**
     * Creates the subtreeSpecification for the accessControlSubentry for the application.
     *
     * @param adminPointDn the normalized DN of the AP (Administrative Point) for the entry
     * @param appDn the normalized DN of the application entry
     * @return the subtreeSpecification of a subtree which starts at the application entry and
     * descends to all leaf entries
     * @throws NamingException if the AP is (wrong) not an ancestor of the application entry
     */
    private String createSubtreeSpecification( LdapDN adminPointDn, LdapDN appDn ) throws NamingException
    {
        LdapDN baseRdn = ( LdapDN ) NamespaceTools.getRelativeName( adminPointDn, appDn );
        StringBuffer buf = new StringBuffer();
        buf.append( "{ base \"" );
        buf.append( baseRdn.getNormName() );
        buf.append( "\" }" );
        return buf.toString();
    }


    /**
     * Creates an ACIItem for the application user to have <b>READ-ONLY</b> access to policy
     * information for itself (the application).  The application user will be granted the
     * following permissions on all entries and their attributes under the application's subtree:
     *
     * <ul>
     *   <li>Read</li>
     *   <li>ReturnDN</li>
     *   <li>Browse</li>
     *   <li>DiscloseOnError</li>
     *   <li>Compare</li>
     * </ul>
     *
     * Here's what the ACIItem looks like for application appname=abc,ou=applications,dc=example,dc=com:
     * <pre>
     *  {
     *    identificationTag "abcAci"
     *    precedence 14,
     *    authenticationLevel simple,
     *    itemOrUserFirst userFirst: {
     *    userClasses { name { "appName=abc,ou=applications,dc=example,dc=com" } },
     *    userPermissions
     *    { {
     *      protectedItems {entry, allUserAttributeTypesAndValues},
     *      grantsAndDenials { grantRead, grantReturnDN, grantBrowse, grantDiscloseOnError, grantCompare } }
     *    } }
     * }
     * </pre>
     *
     * @param aciTag the identificationTag for the ACIItem produced
     * @param appDn the normalized DN for the application
     * @return the ACIItem for the application user
     */
    private String createApplicationACIItem( String aciTag, LdapDN appDn )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "{ identificationTag \"" );
        buf.append( aciTag );
        buf.append( "\", precedence 14, authenticationLevel simple, itemOrUserFirst userFirst: { " );
        buf.append( "userClasses { name { \"" );
        buf.append( appDn.getNormName() );
        buf.append( "\" } }, userPermissions { { protectedItems {entry, allUserAttributeTypesAndValues}, ");
        buf.append("grantsAndDenials { grantRead, grantReturnDN, grantBrowse, grantDiscloseOnError, grantCompare } } } } }" );
        return buf.toString();
    }


    /**
     * Creates an ACIItem for an adminstrative group with full access to alter policy information
     * for an application.  The group will be granted the following permissions on all entries and
     * their attributes under the application subtree:
     *
     * <ul>
     *   <li>Read</li>
     *   <li>ReturnDN</li>
     *   <li>Browse</li>
     *   <li>DiscloseOnError</li>
     *   <li>Compare</li>
     *   <li>Add</li>
     *   <li>Rename</li>
     *   <li>Remove</li>
     *   <li>Modify</li>
     *   <li>Import</li>
     *   <li>Export</li>
     * </ul>
     *
     * Here's what the ACIItem looks like for application appName=abc,ou=applications,dc=example,dc=com and the
     * admin group cn=abcAdminGroup,ou=groups,dc=example,dc=com:
     * <pre>
     *  {
     *    identificationTag "abcAdminAci"
     *    precedence 14,
     *    authenticationLevel simple,
     *    itemOrUserFirst userFirst: {
     *    userClasses { userGroup { "cn=abcApplicationAdminGroup,ou=groups,dc=example,dc=com" } },
     *    userPermissions
     *    { {
     *      protectedItems {entry, allUserAttributeTypesAndValues},
     *      grantsAndDenials { grantRead, grantReturnDN, grantBrowse, grantDiscloseOnError, grantCompare,
     *                         grantAdd, grantRename, grantRemove, grantModify, grantImport, grantExport } }
     *    } }
     * }
     * </pre>
     *
     * @param aciTag the identificationTag for the ACIItem produced
     * @param adminGroupDn the normalized DN for the application's admin group
     * @return the ACIItem for the administrative user's group for an application
     */
    private String createApplicationAdminACIItem( String aciTag, LdapDN adminGroupDn )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "{ identificationTag \"" );
        buf.append( aciTag );
        buf.append( "\", precedence 14, authenticationLevel simple, itemOrUserFirst userFirst: { " );
        buf.append( "userClasses { userGroup { \"" );
        buf.append( adminGroupDn.getNormName() );
        buf.append( "\" } }, userPermissions { { protectedItems {entry, allUserAttributeTypesAndValues}, ");
        buf.append( "grantsAndDenials { grantRead, grantReturnDN, grantBrowse, grantDiscloseOnError, grantCompare, ");
        buf.append( "grantAdd, grantRename, grantRemove, grantModify, grantImport, grantExport } } } } }" );
        return buf.toString();
    }
}
