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


import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.DataAccessException;
import org.safehaus.triplesec.admin.TriplesecAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;


/**
 * The left tree navigation.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class LeftTreeNavigation extends JPanel implements Observer
{
    private static final long serialVersionUID = 105910555885359618L;
    private JScrollPane scrollPane = null;
    private JTree tree = null;
    private JPanel rightDetailPanel;
    
    private DefaultTreeModel model;
    private DefaultMutableTreeNode rootNode;
    private DefaultMutableTreeNode groupsNode;
    private DefaultMutableTreeNode usersNode;
    private DefaultMutableTreeNode applicationsNode;


    /**
     * This method initializes
     */
    public LeftTreeNavigation()
    {
        super();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize()
    {
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows( 1 );
        this.setLayout( gridLayout );
        this.setSize( new java.awt.Dimension( 315, 497 ) );
        this.add( getJScrollPane(), null );
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane()
    {
        if ( scrollPane == null )
        {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView( getJTree() );
        }
        return scrollPane;
    }


    /**
     * This method initializes jTree
     * 
     * @return javax.swing.JTree
     */
    private JTree getJTree()
    {
        if ( tree == null )
        {
            tree = new JTree();
            tree.setShowsRootHandles( true );
            tree.setRootVisible( true );
            tree.putClientProperty("JTree.lineStyle", "None");
            rootNode = new DefaultMutableTreeNode( "disconnected" );
            tree.setCellRenderer( new LeftTreeCellRenderer( rootNode ) );
            model = new DefaultTreeModel( rootNode );
            tree.setModel( model );
            usersNode = new DefaultMutableTreeNode( "Users" );
            model.insertNodeInto( usersNode, rootNode, 0 );
            groupsNode = new DefaultMutableTreeNode( "Groups" );
            model.insertNodeInto( groupsNode, rootNode, 0 );
            applicationsNode = new DefaultMutableTreeNode( "Applications" );
            model.insertNodeInto( applicationsNode, rootNode, 0 );
        }
        return tree;
    }

    
    public void addTreeSelectionListener( TreeSelectionListener listener )
    {
        tree.addTreeSelectionListener( listener );
    }
    
    
    public JPanel getRightDetailPanel()
    {
        return rightDetailPanel;
    }

    
    public void setRightDetailPanel( JPanel rightDetailPanel )
    {
        this.rightDetailPanel = rightDetailPanel;
    }


    String realm = null;
    TriplesecAdmin admin = null;
    private static Logger log = LoggerFactory.getLogger( LeftTreeNavigation.class );
    public void update( Observable o, Object arg )
    {
        if ( o instanceof ConnectionManager )
        {
            ConnectionManager connMan = ( ConnectionManager ) o;
            if ( connMan.isConnected() )
            {
                admin = connMan.getTriplesecAdmin();
                realm = connMan.getRealm();
                try
                {
                    load();
                }
                catch ( DataAccessException e )
                {
                    log.error( "failed on load", e );
                    JOptionPane.showMessageDialog( this, e.getMessage(), "Failed on load", JOptionPane.ERROR_MESSAGE );
                }
            }
            else
            {
                realm = null;
                admin = null;
                clear();
            }
        }
    }
    
    
    private void load() throws DataAccessException
    {
        if ( admin == null )
        {
            throw new IllegalStateException( "Admin should not be null" );
        }

        rootNode.setUserObject( realm );

        for ( Iterator ii = admin.groupIterator(); ii.hasNext(); /**/ )
        {
            model.insertNodeInto( new DefaultMutableTreeNode( ii.next() ), groupsNode, 0 );
        }
        
        for ( Iterator ii = admin.userIterator(); ii.hasNext(); /**/ )
        {
            model.insertNodeInto( new DefaultMutableTreeNode( ii.next() ), usersNode, 0 );
        }
        
        for ( Iterator ii = admin.applicationIterator(); ii.hasNext(); /**/ )
        {
            Application app = ( Application ) ii.next();
            DefaultMutableTreeNode appNode = new DefaultMutableTreeNode( app );
            model.insertNodeInto( appNode, applicationsNode, 0 );
            
            DefaultMutableTreeNode profilesNode = new DefaultMutableTreeNode( "Profiles" );
            model.insertNodeInto( profilesNode, appNode, 0 );
            for ( Iterator jj = app.profileIterator(); jj.hasNext(); /**/ )
            {
                model.insertNodeInto( new DefaultMutableTreeNode( jj.next() ), profilesNode, 0 );
            }
            
            DefaultMutableTreeNode rolesNode = new DefaultMutableTreeNode( "Roles" );
            model.insertNodeInto( rolesNode, appNode, 0 );
            for ( Iterator jj = app.roleIterator(); jj.hasNext(); /**/ )
            {
                model.insertNodeInto( new DefaultMutableTreeNode( jj.next() ), rolesNode, 0 );
            }

            DefaultMutableTreeNode permissionsNode = new DefaultMutableTreeNode( "Permissions" );
            model.insertNodeInto( permissionsNode, appNode, 0 );
            for ( Iterator jj = app.permissionIterator(); jj.hasNext(); /**/ )
            {
                model.insertNodeInto( new DefaultMutableTreeNode( jj.next() ), permissionsNode, 0 );
            }
        }
    }
    
    
    void reloadApplication( DefaultMutableTreeNode appNode ) throws DataAccessException
    {
        // -------------------------------------------------------------------
        // clear out the old application 
        // -------------------------------------------------------------------
        
        while( appNode.children().hasMoreElements() )
        {
            clear( appNode );
        }
        
        // -------------------------------------------------------------------
        // load the application again
        // -------------------------------------------------------------------
        
        Application app = ( Application ) appNode.getUserObject();
        DefaultMutableTreeNode profilesNode = new DefaultMutableTreeNode( "Profiles" );
        model.insertNodeInto( profilesNode, appNode, 0 );
        for ( Iterator jj = app.profileIterator(); jj.hasNext(); /**/ )
        {
            model.insertNodeInto( new DefaultMutableTreeNode( jj.next() ), profilesNode, 0 );
        }
        
        DefaultMutableTreeNode rolesNode = new DefaultMutableTreeNode( "Roles" );
        model.insertNodeInto( rolesNode, appNode, 0 );
        for ( Iterator jj = app.roleIterator(); jj.hasNext(); /**/ )
        {
            model.insertNodeInto( new DefaultMutableTreeNode( jj.next() ), rolesNode, 0 );
        }

        DefaultMutableTreeNode permissionsNode = new DefaultMutableTreeNode( "Permissions" );
        model.insertNodeInto( permissionsNode, appNode, 0 );
        for ( Iterator jj = app.permissionIterator(); jj.hasNext(); /**/ )
        {
            model.insertNodeInto( new DefaultMutableTreeNode( jj.next() ), permissionsNode, 0 );
        }
    }
    
    
    private void clear()
    {
        rootNode.setUserObject( "disconnected" );
        
        while ( usersNode.children().hasMoreElements() )
        {
            clear( usersNode );
        }

        while ( groupsNode.children().hasMoreElements() )
        {
            clear( groupsNode );
        }

        while ( applicationsNode.children().hasMoreElements() )
        {
            clear( applicationsNode );
        }
    }
    
    
    private void clear( DefaultMutableTreeNode node )
    {
        for ( Enumeration ii = node.children(); ii.hasMoreElements(); /**/ )
        {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode ) ii.nextElement();
            clear( child );
            model.removeNodeFromParent( child );
        }
    }


    public JTree getTree()
    {
        return tree;
    }


    public void reloadGroups() throws DataAccessException
    {
        while ( groupsNode.children().hasMoreElements() )
        {
            for ( Enumeration ii = groupsNode.children(); ii.hasMoreElements(); /**/ )
            {
                model.removeNodeFromParent( ( DefaultMutableTreeNode ) ii.nextElement() );
            }
        }
        
        for ( Iterator ii = admin.groupIterator(); ii.hasNext(); /**/ )
        {
            model.insertNodeInto( new DefaultMutableTreeNode( ii.next() ), groupsNode, 0 );
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
