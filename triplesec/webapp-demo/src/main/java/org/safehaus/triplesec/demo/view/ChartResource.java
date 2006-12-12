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
package org.safehaus.triplesec.demo.view;

import org.jCharts.chartData.ChartDataException;
import org.jCharts.chartData.PieChartDataSet;
import org.jCharts.nonAxisChart.PieChart2D;
import org.jCharts.properties.ChartProperties;
import org.jCharts.properties.LegendProperties;
import org.jCharts.properties.PieChart2DProperties;
import org.safehaus.triplesec.demo.model.Account;
import wicket.markup.html.image.resource.BufferedDynamicImageResource;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;


public class ChartResource extends BufferedDynamicImageResource
{
    private static final long serialVersionUID = 7331088436339160727L;

    public ChartResource( final Account account )
    {
        this.setFormat("PNG");
        this.setImage( createChart( account ) );
        this.setCacheable( false );
    }

    
    private BufferedImage createChart( final Account account )
    {
        double[] data= { 
            account.getBonds(), 
            account.getForeign(), 
            account.getTBills(), 
            account.getTechStocks(),
            account.getVolatileHighYield() 
        };
        String[] labels= { "Bonds", "Foriegn", "T-Bills", "Tech Stocks", "Volatile" };
        Paint[] paints= { Color.magenta, Color.green, Color.blue, Color.red, Color.yellow };

        PieChart2DProperties pieChart2DProperties= new PieChart2DProperties();
        PieChartDataSet pieChartDataSet= null;
        
        try
        {
            pieChartDataSet = new PieChartDataSet( "Investments", data, labels, paints, pieChart2DProperties );
        }
        catch ( ChartDataException e )
        {
            e.printStackTrace();
        }

        PieChart2D pieChart2D = new PieChart2D( pieChartDataSet, new LegendProperties(), new ChartProperties(), 500, 300 );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try
        {
            org.jCharts.encoders.PNGEncoder.encode( pieChart2D, out );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
 
        ImageIcon img = new ImageIcon( out.toByteArray() );
        return toBufferedImage( img.getImage() );
    }
    
    private BufferedImage toBufferedImage( Image image )
    {
        if ( image instanceof BufferedImage )
        {
            return ( BufferedImage ) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon( image ).getImage();

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try
        {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage( image.getWidth( null ), image.getHeight( null ), transparency );
        }
        catch ( HeadlessException e )
        {
            // The system does not have a screen
            e.printStackTrace();
        }

        if ( bimage == null )
        {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage( image, 0, 0, null );
        g.dispose();

        return bimage;
    }
}
