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


import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.ExternalUser;
import org.safehaus.triplesec.admin.Group;
import org.safehaus.triplesec.admin.HauskeysUser;
import org.safehaus.triplesec.admin.LocalUser;
import org.safehaus.triplesec.admin.PermissionClass;
import org.safehaus.triplesec.admin.Profile;
import org.safehaus.triplesec.admin.Role;


/**
 * The left tree navigation cell renderer.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LeftTreeCellRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = 1L;
    
    // -----------------------------------------------------------------------
    // Leaf node icons
    // -----------------------------------------------------------------------
    
    private ImageIcon rootIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/home2_16x16.png" ) );
    
    private ImageIcon localUserIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/local_user_16x16.png" ) );
    private ImageIcon externalUserIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/external_user_16x16.png" ) );
    private ImageIcon hauskeysUserIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/hauskeys_user_16x16.png" ) );
    
    private ImageIcon userContainerClosedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/user_container_closed_16x16.png" ) );
    private ImageIcon userContainerOpenedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/user_container_opened_16x16.png" ) );
    
    private ImageIcon profileIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/profile2_16x16.png" ) );
    private ImageIcon profileContainerClosedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/profile_container_closed_16x16.png" ) );
    private ImageIcon profileContainerOpenedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/profile_container_opened_16x16.png" ) );
    
    private ImageIcon permissionIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/permission2_16x16.png" ) );
    private ImageIcon permissionContainerClosedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/permission_container_closed_16x16.png" ) );
    private ImageIcon permissionContainerOpenedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/permission_container_opened_16x16.png" ) );
    
    private ImageIcon applicationIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/application_16x16.png" ) );
    private ImageIcon applicationContainerClosedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/application_container_closed_16x16.png" ) );
    private ImageIcon applicationContainerOpenedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/application_container_opened_16x16.png" ) );

    private ImageIcon roleIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/role_16x16.png" ) );
    private ImageIcon roleContainerClosedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/role_container_closed_16x16.png" ) );
    private ImageIcon roleContainerOpenedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/role_container_opened_16x16.png" ) );

    private ImageIcon groupIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/group_16x16.png" ) );
    private ImageIcon groupContainerClosedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/group_container_closed_16x16.png" ) );
    private ImageIcon groupContainerOpenedIcon = new ImageIcon( 
        getClass().getResource( "/org/safehaus/triplesec/admin/swing/group_container_opened_16x16.png" ) );

    private DefaultMutableTreeNode rootNode;

    // -----------------------------------------------------------------------
    // Branch node icons
    // -----------------------------------------------------------------------

    
    public LeftTreeCellRenderer ( DefaultMutableTreeNode rootNode )
    {
        this.rootNode = rootNode;
    }
    
    
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
        boolean leaf, int row, boolean hasFocus ) 
    {
        super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

        if ( isRoot( value ) )
        {
            setIcon( rootIcon );
        }
        else if ( isContainer( "Permissions", value ) )
        {
            if ( expanded )
            {
                setIcon( permissionContainerOpenedIcon );
            }
            else
            {
                setIcon( permissionContainerClosedIcon );
            }
        }
        else if ( isContainer( "Roles", value ) )
        {
            if ( expanded )
            {
                setIcon( roleContainerOpenedIcon );
            }
            else
            {
                setIcon( roleContainerClosedIcon );
            }
        }
        else if ( isContainer( "Profiles", value ) )
        {
            if ( expanded )
            {
                setIcon( profileContainerOpenedIcon );
            }
            else
            {
                setIcon( profileContainerClosedIcon );
            }
        }
        else if ( isContainer( "Users", value ) )
        {
            if ( expanded )
            {
                setIcon( userContainerOpenedIcon );
            }
            else
            {
                setIcon( userContainerClosedIcon );
            }
        }
        else if ( isContainer( "Groups", value ) )
        {
            if ( expanded )
            {
                setIcon( groupContainerOpenedIcon );
            }
            else
            {
                setIcon( groupContainerClosedIcon );
            }
        }
        else if ( isContainer( "Applications", value ) )
        {
            if ( expanded )
            {
                setIcon( applicationContainerOpenedIcon );
            }
            else
            {
                setIcon( applicationContainerClosedIcon );
            }
        }
        else if ( leaf && isPermissionClass( value ) )
        {
            setIcon( permissionIcon );
            PermissionClass permission = getPermission( value );
//            if ( permission.getDescription() != null )
//            {
//                setToolTipText( permission.getDescription() );
//            }
        }
        else if ( isApplication( value ) )
        {
            setIcon( applicationIcon );
            Application app = getApplication( value );
            if ( app.getDescription() != null )
            {
                setToolTipText( app.getDescription() );
            }
        }
        else if ( leaf && isRole( value ) )
        {
            setIcon( roleIcon );
            Role role = getRole( value );
            if ( role.getDescription() != null )
            {
                setToolTipText( role.getDescription() );
            }
        }
        else if ( leaf && isProfile( value ) )
        {
            setIcon( profileIcon );
            Profile profile = getProfile( value );
            if ( profile.getDescription() != null )
            {
                setToolTipText( profile.getDescription() );
            }
        }
        else if ( leaf && isGroup( value ) )
        {
            setIcon( groupIcon );
            Group group = getGroup( value );
            if ( group.getName() != null )
            {
                setToolTipText( group.getName() );
            }
        }
        else if ( leaf && isLocalUser( value ) )
        {
            setIcon( localUserIcon );
            LocalUser user = getLocalUser( value );
            setToolTipText( "Local User: " + user.getId() );
        }
        else if ( leaf && isHauskeysUser( value ) )
        {
            setIcon( hauskeysUserIcon );
            HauskeysUser user = getHauskeysUser( value );
            setToolTipText( "Hauskeys User: " + user.getId() );
        }
        else if ( leaf && isExternalUser( value ) )
        {
            setIcon( externalUserIcon );
            ExternalUser user = getExternalUser( value );
            setToolTipText( "External User: " + user.getId() + " -> " + user.getReferral() );
        }
        else 
        {
            setToolTipText( null ); //no tool tip
        } 

        return this;
    }
    

    private boolean isContainer( String name, Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        if ( node.getUserObject() instanceof String )
        {
            String nodeName = ( String ) node.getUserObject();
            if ( nodeName.equals( name ) )
            {
                return true;
            }
        }
        return false;
    }


    private boolean isRoot( Object value )
    {
        return rootNode.equals( value );
    }
    
    
    private Group getGroup( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        return ( Group ) node.getUserObject();
    }


    private boolean isGroup( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        if ( node.getUserObject() instanceof Group )
        {
            return true;
        }
        return false;
    }


    private Profile getProfile( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        return ( Profile ) node.getUserObject();
    }


    private LocalUser getLocalUser( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        return ( LocalUser ) node.getUserObject();
    }


    private ExternalUser getExternalUser( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        return ( ExternalUser ) node.getUserObject();
    }


    private HauskeysUser getHauskeysUser( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        return ( HauskeysUser ) node.getUserObject();
    }


    private boolean isProfile( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        if ( node.getUserObject() instanceof Profile )
        {
            return true;
        }
        return false;
    }


    private Role getRole( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        return ( Role ) node.getUserObject();
    }


    private boolean isRole( Object value )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) value;
        if ( node.getUserObject() instanceof Role )
        {
            return true;
        }
        return false;
    }


    private boolean isPermissionClass( Object obj )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) obj;
        if ( node.getUserObject() instanceof PermissionClass )
        {
            return true;
        }
        return false;
    }
    
    
    private boolean isLocalUser( Object obj )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) obj;
        if ( node.getUserObject() instanceof LocalUser )
        {
            return true;
        }
        return false;
    }
    
    
    private boolean isExternalUser( Object obj )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) obj;
        if ( node.getUserObject() instanceof ExternalUser )
        {
            return true;
        }
        return false;
    }
    
    
    private boolean isHauskeysUser( Object obj )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) obj;
        if ( node.getUserObject() instanceof HauskeysUser )
        {
            return true;
        }
        return false;
    }
    
    
    private PermissionClass getPermission( Object obj )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) obj;
        return ( PermissionClass ) node.getUserObject();
    }
    
    
    private boolean isApplication( Object obj )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) obj;
        if ( node.getUserObject() instanceof Application )
        {
            return true;
        }
        return false;
    }
    
    
    private Application getApplication( Object obj )
    {
        DefaultMutableTreeNode node = ( DefaultMutableTreeNode ) obj;
        return ( Application ) node.getUserObject();
    }
}
