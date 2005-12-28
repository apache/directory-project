/*
 *   Copyright 2004-2005 The Apache Software Foundation
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
package org.apache.asn1.ber ;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.asn1.ber.primitives.PrimitiveUtils;
import org.apache.asn1.ber.primitives.UniversalTag;
import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.binary.Hex;
import org.apache.asn1.codec.stateful.CallbackHistory;
import org.apache.commons.collections.IteratorUtils;


/**
 * Simple JTree view of a tuple tree.
 *
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class TupleTreeAnalyzer extends JFrame implements TreeSelectionListener
{
    private boolean doVmExit = false;
    private BorderLayout layout = new BorderLayout();
    private JLabel statusBar = new JLabel("Ready");
    private JSplitPane jSplitPane1 = new JSplitPane();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JPanel jPanel1 = new JPanel();
    private JTree jTree1 = new JTree();
    private JPanel jPanel2 = new JPanel();
    private JPanel jPanel5 = new JPanel();
    private JPanel jPanel3 = new JPanel();
    private JPanel jPanel4 = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private JLabel jLabel2 = new JLabel();
    private JScrollPane jScrollPane2 = new JScrollPane();
    private JTextArea jTextArea1 = new JTextArea();
    private JTextField jTextField1 = new JTextField();
    private JTextField jTextField3 = new JTextField();
    private JTextField jTextField2 = new JTextField();

    private DefaultMutableTupleNode root = null ;


    /** Creates new form JFrame */
    public TupleTreeAnalyzer( DefaultMutableTupleNode root )
    {
        this.root = root ;

        initGUI() ;
        pack() ;
    }


    /** Creates new form JFrame */
    public TupleTreeAnalyzer( DefaultMutableTupleNode root, boolean doVmExit )
    {
        this.root = root ;
        this.doVmExit = doVmExit;

        initGUI() ;
        pack() ;
    }


    public TupleTreeAnalyzer( byte[] encoded ) throws DecoderException
    {
        this( ByteBuffer.wrap( encoded ) );
    }


    public TupleTreeAnalyzer( ByteBuffer encoded ) throws DecoderException
    {
        TupleTreeDecoder decoder = new TupleTreeDecoder();
        CallbackHistory history = new CallbackHistory();
        decoder.setCallback( history );
        decoder.decode( encoded.duplicate() );
        root = ( DefaultMutableTupleNode ) history.getMostRecent();

        initGUI();
        pack();
    }


    public TupleTreeAnalyzer( ByteBuffer[] encoded ) throws DecoderException
    {
        TupleTreeDecoder decoder = new TupleTreeDecoder();
        CallbackHistory history = new CallbackHistory();
        decoder.setCallback( history );

        for ( int ii = 0; ii < encoded.length; ii++ )
        {
            decoder.decode( encoded[ii].duplicate() );
        }

        root = ( DefaultMutableTupleNode ) history.getMostRecent();

        initGUI();
        pack();
    }


    /** This method is called from within
     * the constructor to initialize the form. */
    private void initGUI() {

        getContentPane().setLayout(layout);
        JPanel content = new JPanel();
        content.setPreferredSize(new Dimension(300, 200));
        getContentPane().add(content, BorderLayout.CENTER);
        // set title
        setTitle("");
        // add status bar
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        // add menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');
        // create Exit menu item
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.setMnemonic('E');
        fileExit.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if ( doVmExit )
                    {
                        System.exit(0);
                    }
                }
            });
        // create About menu item
        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic('H');
        JMenuItem helpAbout = new JMenuItem("About");
        helpAbout.setMnemonic('A');
        helpAbout.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            });
        menuHelp.add(helpAbout);
        // create Open menu item
        final JFileChooser fc = new JFileChooser();
        JMenuItem openFile = new JMenuItem("Open");
        openFile.setMnemonic('O');
        openFile.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int returnVal = fc.showOpenDialog(TupleTreeAnalyzer.this);
                    if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                        //java.io.File file = fc.getSelectedFile();
                        // Write your code here what to do with selected file
                    } else {
                        // Write your code here what to do if user has canceled
                    }
                }
            });
        menuFile.add(openFile);
        // create Save menu item
        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.setMnemonic('S');
        saveFile.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int returnVal = fc.showSaveDialog(TupleTreeAnalyzer.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        //java.io.File file = fc.getSelectedFile();
                        // Write your code here what to do with selected file
                    } else {
// Write your code here what to do if user has canceled Save dialog
                    }
                }
            });
        menuFile.add(saveFile);
        // create Print menu item
        JMenuItem print = new JMenuItem("Print");
        print.setMnemonic('P');
        print.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    hexDumpTupleTree();
                }
            }); menuFile.add(print);
        menuFile.add(fileExit);
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        // sets menu bar
        setJMenuBar(menuBar);
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });


        jLabel3.setText("Type Class:");
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel5.add(jLabel3);
        jPanel5.add(jTextField3);
        jTextField3.setText("");
        jTextField3.setMinimumSize(new java.awt.Dimension(184, 25));
        jTextField3.setPreferredSize(new java.awt.Dimension(184, 25));
        jSplitPane1.setLastDividerLocation(50);
        jSplitPane1.setDividerLocation(180);
        jSplitPane1.add(jScrollPane1, javax.swing.JSplitPane.LEFT);
        jSplitPane1.add(jPanel1, javax.swing.JSplitPane.RIGHT);
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });
        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);
        jScrollPane1.getViewport().add(jTree1);
        jTree1.setBounds(new java.awt.Rectangle(95,95,85,84));
        jTree1.setShowsRootHandles(true);
        jPanel1.setLayout(new java.awt.GridBagLayout());
        jPanel1.add(jPanel2,
        new java.awt.GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                        java.awt.GridBagConstraints.WEST,
                        java.awt.GridBagConstraints.HORIZONTAL,
        new java.awt.Insets(0, 9, 0, 9), 0, 0));
        jPanel1.add(jPanel3,
        new java.awt.GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                        java.awt.GridBagConstraints.WEST,
                        java.awt.GridBagConstraints.HORIZONTAL,
        new java.awt.Insets(0, 9, 0, 9), 0, 0));
        jPanel1.add(jPanel4,
        new java.awt.GridBagConstraints(0, 3, 1, 35, 1.0, 1.0,
                        java.awt.GridBagConstraints.CENTER,
                        java.awt.GridBagConstraints.BOTH,
        new java.awt.Insets(9, 12, 9, 12), 0, 0));
        jPanel1.add(jPanel5,
        new java.awt.GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                        java.awt.GridBagConstraints.WEST,
                        java.awt.GridBagConstraints.HORIZONTAL,
        new java.awt.Insets(0, 9, 0, 9), 0, 0));
        jLabel1.setText("Tag Id:");
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel2.add(jLabel1);
        jPanel2.add(jTextField1);
        jLabel2.setText("Length:");
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel3.add(jLabel2);
        jPanel3.add(jTextField2);
        jPanel4.setLayout(new java.awt.BorderLayout());
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createLineBorder(
        new java.awt.Color(153, 153, 153), 1), "Value",
                        javax.swing.border.TitledBorder.LEADING,
                        javax.swing.border.TitledBorder.TOP,
        new java.awt.Font("Comic Sans MS", 0, 14),
                        new java.awt.Color(60, 60, 60)));
        jPanel4.add(jScrollPane2, java.awt.BorderLayout.CENTER);
        jTextArea1.setText("");
        jScrollPane2.getViewport().add(jTextArea1);
        jTextField1.setText("");
        jTextField1.setMinimumSize(new java.awt.Dimension(164, 25));
        jTextField1.setPreferredSize(new java.awt.Dimension(164, 25));
        jTextField1.setEditable(true);
        jTextField2.setText("");
        jTextField2.setPreferredSize(new java.awt.Dimension(164,25));
        jTextField2.setMinimumSize(new java.awt.Dimension(164,25));
        jTextField2.setEditable(true);
        jScrollPane2.setVerticalScrollBarPolicy(
                javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setHorizontalScrollBarPolicy(
                javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setBorder(null);

        jTree1.setModel( new DefaultTreeModel(
                new TupleTreeNodeAdapter( root ) ) );
        jTree1.getSelectionModel().addTreeSelectionListener( this );
    }

    private void hexDumpTupleTree()
    {
    }

    /** Exit the Application */
    private void exitForm(WindowEvent evt)
    {
        System.out.println( "Closed window: " + evt.getWindow().getName() );

        if ( doVmExit )
        {
            System.exit(0);
        }
    }


    public void startup()
    {
        setSize( 800, 640 ) ;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        frameSize.height = ((frameSize.height > screenSize.height)
                ? screenSize.height : frameSize.height);
        frameSize.width = ((frameSize.width > screenSize.width)
                ? screenSize.width : frameSize.width);
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        setVisible(true);
    }


    public void valueChanged( TreeSelectionEvent e )
    {
        TreePath path = e.getPath();
        TupleTreeNodeAdapter node = ( TupleTreeNodeAdapter )
                path.getLastPathComponent();
        TupleNode tn = node.getTupleNode();
        Tuple tuple = tn.getTuple();

        TypeClass type = TypeClass.getTypeClass( tuple.getRawTag() >> 24 );
        jTextField3.setText( type.getName() );


        if ( tuple.getLength() == Length.INDEFINITE )
        {
            jTextField2.setText( "INDEFINITE" );
        }
        else
        {
            jTextField2.setText( Integer.toString( tuple.getLength() ) );
        }

        if ( type.equals( TypeClass.UNIVERSAL ) )
        {
            UniversalTag tag = UniversalTag.getUniversalTag( tuple.getRawTag() );
            jTextField1.setText( tag.getName() );
        }
        else
        {
            jTextField1.setText( Integer.toString( tuple.getId() ) );
        }

        if ( tuple.isPrimitive() )
        {
            ByteBuffer buf = ( ByteBuffer ) tuple.getLastValueChunk().rewind();
            byte[] bites = new byte[buf.remaining()];
            buf.get( bites );

            jTextArea1.setText( new String( Hex
                    .encodeHex( bites ) ) );

            if ( type == TypeClass.UNIVERSAL )
            {
                UniversalTag tag = UniversalTag
                        .getUniversalTag( tuple.getRawTag() );

                if ( tag == UniversalTag.ENUMERATED ||
                        tag == UniversalTag.INTEGER )
                {
                    int ii = PrimitiveUtils.decodeInt( bites, 0, bites.length );
                    jTextArea1.setToolTipText( "Numeric: "
                            + Integer.toString( ii ) );
                }
                else if ( tag == UniversalTag.BOOLEAN )
                {
                    boolean bool = PrimitiveUtils.berDecodeBoolean( bites[0] );
                    jTextArea1.setToolTipText( "Boolean: "
                            + Boolean.toString( bool ) );
                }
                else
                {
                    jTextArea1.setToolTipText( "String: "
                            + new String( bites ) );
                }
            }
            else
            {
                if ( bites.length > 4 )
                {
                    jTextArea1.setToolTipText( "String: "
                            + new String( bites ) );
                    return;
                }

                int ii = PrimitiveUtils.decodeInt( bites, 0, bites.length );
                boolean bool = PrimitiveUtils.berDecodeBoolean( bites[0] );
                String tip =  "Numeric: " + Integer.toString( ii ) + "\n";
                tip += "Boolean: " + Boolean.toString( bool ) + "\n";
                tip += "String: " + new String( bites ) ;
                jTextArea1.setToolTipText( tip );
            }
        }
        else
        {
            jTextArea1.setText( "N/A" );
            jTextArea1.setToolTipText( null );
        }
    }


    /**
     * Gets a hexDump of a direct buffer without affecting the buffer.  Used
     * for primitive analysis especially when direct memory buffers are used
     * which cannot be easily inspected within debuggers.
     *
     * @param buf the buffer to generate a hex dump for
     * @return a hex string representing the buffer
     */
    public static String getHexDump( ByteBuffer buf )
    {
        byte[] bites = new byte[buf.remaining()];
        buf.duplicate().get( bites );
        return new String( Hex.encodeHex( bites ) );
    }


    public static void analyze( byte[] bites ) throws DecoderException
    {
        TupleTreeAnalyzer analyzer = new TupleTreeAnalyzer( bites );
        analyzer.startup();
    }


    public static void analyze( ByteBuffer bites ) throws DecoderException
    {
        TupleTreeAnalyzer analyzer = new TupleTreeAnalyzer( bites );
        analyzer.startup();
    }

    class TupleTreeNodeAdapter implements TreeNode
    {
        DefaultMutableTupleNode node;


        TupleTreeNodeAdapter( DefaultMutableTupleNode node )
        {
            this.node = node;
        }


        public int getChildCount()
        {
            return node.getChildCount();
        }

        public boolean getAllowsChildren()
        {
            return !node.getTuple().isPrimitive();
        }

        public boolean isLeaf()
        {
            return node.getChildCount() == 0;
        }

        public Enumeration children()
        {
            return IteratorUtils.asEnumeration( node.getChildren() );
        }

        public TreeNode getParent()
        {
            return new TupleTreeNodeAdapter( ( DefaultMutableTupleNode )
                    node.getParentTupleNode() );
        }

        public TreeNode getChildAt( int childIndex )
        {
            return new TupleTreeNodeAdapter( ( DefaultMutableTupleNode )
                    node.getChildTupleNodeAt( childIndex ) );
        }

        public int getIndex( TreeNode node )
        {
            DefaultMutableTupleNode tn =
                    ( ( TupleTreeNodeAdapter ) node ).getTupleNode();
            return this.node.getIndex( tn );
        }

        DefaultMutableTupleNode getTupleNode()
        {
            return node;
        }

        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            Tuple tuple = node.getTuple();
            TypeClass type =
                    TypeClass.getTypeClass( node.getTuple().getRawTag() >> 24 );
            int id = Tag.getTagId( tuple.getRawTag() );

            buf.append( "[" ).append( type.getName() ).append( "][" )
                    .append( id ).append( "]" ).append( "[" )
                    .append( tuple.getLength() ).append( "]" );

            return buf.toString();
        }

    }


    public static void main( String [] args )
    {
        JFileChooser fc = new JFileChooser( "." );
        fc.setFileFilter( new javax.swing.filechooser.FileFilter()
        {
            public boolean accept( File f )
            {
                return f.isDirectory() || f.getName().endsWith( ".ber" );
            }

            public String getDescription()
            {
                return "BER encoded data files";
            }
        }
        );
        fc.showOpenDialog( null );
        File file = fc.getSelectedFile();

        if ( file == null )
        {
            System.exit( 0 );
        }

        FileInputStream in = null;

        try
        {
            in = new FileInputStream( file );
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
            System.exit( -1 );
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            int ch = -1;
            while( ( ch = in.read() ) != -1 )
            {
                out.write( ch );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            System.exit( -1 );
        }

        TupleTreeAnalyzer analyzer;
        try
        {
            analyzer = new TupleTreeAnalyzer( out.toByteArray() );
            analyzer.startup();
        }
        catch ( DecoderException e )
        {
            e.printStackTrace();
            System.exit( -1 );
        }
    }
}
