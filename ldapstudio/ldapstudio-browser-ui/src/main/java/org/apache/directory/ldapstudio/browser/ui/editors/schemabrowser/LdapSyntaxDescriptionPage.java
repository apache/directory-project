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

package org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser;


import java.util.HashSet;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


public class LdapSyntaxDescriptionPage extends SchemaPage implements ISelectionChangedListener
{

    private Section section;

    private Text filterText;

    private TableViewer viewer;


    public LdapSyntaxDescriptionPage( SchemaBrowser schemaBrowser )
    {
        super( schemaBrowser );
    }


    protected void refresh()
    {
        if ( this.schemaBrowser.isShowDefaultSchema() )
        {
            this.form.setText( "Syntaxes of default schema" );
            this.viewer.setInput( Schema.DEFAULT_SCHEMA );
        }
        else if ( this.selectedConnection != null )
        {
            this.form.setText( "Syntaxes of connection '" + this.selectedConnection.getName() + "'" );
            this.viewer.setInput( this.selectedConnection.getSchema() );
        }
        else
        {
            this.form.setText( "Syntaxes" );
            this.viewer.setInput( null );
        }
        this.viewer.refresh();
        this.selectionChanged( new SelectionChangedEvent( this.viewer, this.viewer.getSelection() ) );
    }


    protected void createMaster( Composite parent )
    {

        section = toolkit.createSection( parent, Section.DESCRIPTION );
        section.marginWidth = 10;
        section.marginHeight = 12;
        section.setText( "Syntaxes" );
        section.setDescription( "Please select a syntax. Enter a filter to restrict the list." );
        toolkit.createCompositeSeparator( section );

        Composite client = toolkit.createComposite( section, SWT.WRAP );
        GridLayout layout = new GridLayout( 2, false );
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        client.setLayout( layout );
        section.setClient( client );

        toolkit.createLabel( client, "Filter:" );
        this.filterText = toolkit.createText( client, "", SWT.NONE );
        this.filterText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        this.filterText.setData( FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER );
        this.filterText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                viewer.refresh();
            }
        } );

        Table t = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.horizontalSpan = 2;
        gd.heightHint = 20;
        gd.widthHint = 100;
        t.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        viewer = new TableViewer( t );
        viewer.setContentProvider( new LSDContentProvider() );
        viewer.setLabelProvider( new LSDLabelProvider() );
        viewer.setSorter( new LSDViewerSorter() );
        viewer.addFilter( new LSDViewerFilter() );
    }


    protected void createDetail( Composite body )
    {
        this.detailsPage = new LdapSyntaxDescriptionDetailsPage( this.schemaBrowser, this.toolkit );
        this.detailsPage.createContents( this.detailForm );
        this.viewer.addSelectionChangedListener( this );
    }


    public void selectionChanged( SelectionChangedEvent event )
    {
        ISelection selection = event.getSelection();
        if ( selection.isEmpty() )
        {
            EventRegistry.fireLdapSyntaxDescriptionSelected( null, this );
        }
        else
        {
            Object obj = ( ( StructuredSelection ) selection ).getFirstElement();
            if ( obj instanceof LdapSyntaxDescription )
            {
                LdapSyntaxDescription lsd = ( LdapSyntaxDescription ) obj;
                EventRegistry.fireLdapSyntaxDescriptionSelected( lsd, this );
            }
        }
    }


    public void select( Object obj )
    {
        this.viewer.setSelection( new StructuredSelection( obj ), true );
        if ( this.viewer.getSelection().isEmpty() )
        {
            this.filterText.setText( "" );
            this.viewer.setSelection( new StructuredSelection( obj ), true );
        }
    }

    class LSDContentProvider implements IStructuredContentProvider
    {
        public Object[] getElements( Object inputElement )
        {
            if ( inputElement instanceof Schema )
            {
                Schema schema = ( Schema ) inputElement;
                if ( schema != null && schema.getLsdMapByNumericOID() != null )
                {
                    Set set = new HashSet( schema.getLsdMapByNumericOID().values() );
                    return set.toArray();
                }
            }
            return new Object[0];
        }


        public void dispose()
        {
        }


        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    class LSDLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText( Object obj, int index )
        {
            return obj.toString();
        }


        public Image getColumnImage( Object obj, int index )
        {
            return null;
        }
    }

    class LSDViewerSorter extends ViewerSorter
    {
        public int compare( Viewer viewer, Object e1, Object e2 )
        {
            return e1.toString().compareTo( e2.toString() );
        }
    }

    class LSDViewerFilter extends ViewerFilter
    {
        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            if ( element instanceof LdapSyntaxDescription )
            {
                LdapSyntaxDescription lsd = ( LdapSyntaxDescription ) element;
                boolean matched = false;

                if ( !matched )
                    matched = lsd.toString().toLowerCase().indexOf( filterText.getText().toLowerCase() ) != -1;
                if ( !matched )
                    matched = lsd.getNumericOID().toLowerCase().indexOf( filterText.getText().toLowerCase() ) != -1;

                return matched;
            }
            return false;
        }
    }

}
