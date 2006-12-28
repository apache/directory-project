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
package org.safehaus.triplesec.demo.view.panels;


import wicket.markup.html.link.Link;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.RequiredTextField;
import wicket.markup.html.form.validation.PatternValidator;
import wicket.model.CompoundPropertyModel;
import wicket.model.Model;
import wicket.util.parse.metapattern.MetaPattern;
import org.safehaus.triplesec.demo.view.pages.HomePage;
import org.safehaus.triplesec.demo.service.Registry;

import java.util.List;
import java.util.Arrays;
import java.io.Serializable;


public class WapPushPanel extends BasePanel
{
    private static final long serialVersionUID = -4462580703042997059L;

    private static final List ACCOUNTS = Arrays.asList( new String[] {
        "Apache", "Codehaus", "Citi401k", "OfficeW2K", "BankOne"
    });

    private static final List CARRIERS = Arrays.asList( new String[] {
        "T-Mobile", "AT&T", "Verizon", "Cingular", "Sprint", "Nextel"
    });

    public WapPushPanel( String id )
    {
        super( id, null, " Provision a Demo Account to Your Handset!" );

        final Input input = new Input();
        setModel( new CompoundPropertyModel( input ) );

        // create feedback panel and add to page
        final FeedbackPanel feedback = new FeedbackPanel( "feedback" );
        add( feedback );

        Form form = new Form( "wapForm" )
        {
            private static final long serialVersionUID = 1615481633756109835L;

            protected void onSubmit()
            {
                try
                {
                    info( "input: " + input );
                    Registry.smsManager().sendSmsMessage( input.mobile,
                            input.carrier, input.account );
                    info( "An SMS has been sent to your handset with " +
                            "further instructions.");
                    info( "Click the link below to return to the demo login page.");
                }
                catch (Exception e)
                {
                    error( e.getMessage() );
                }
            }
        };
        add( form );

        DropDownChoice account = new DropDownChoice( "account", ACCOUNTS );
        account.setRequired( true );
        account.setLabel( new Model( "Demo Accounts" ) );
        form.add( account );

        RequiredTextField mobile = new RequiredTextField( "mobile" );
        mobile.setLabel( new Model( "Mobile Number" ) );
        mobile.add( new PatternValidator( MetaPattern.DIGITS ) );
        form.add( mobile );

        DropDownChoice carrier = new DropDownChoice( "carrier", CARRIERS );
        carrier.setRequired( true );
        carrier.setLabel( new Model( "Mobile Carriers" ) );
        form.add( carrier );

        add( new Link( "home")
        {
            private static final long serialVersionUID = -5423656870285822281L;

            public void onClick() {
                setResponsePage( HomePage.class );
            }
        });
    }

    private static class Input implements Serializable
    {
        private static final long serialVersionUID = 1752934882880403727L;

        public String account;
        public String mobile;
        public String carrier;


        public String toString() {
            return "Input{" +
                    "account='" + account + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", carrier='" + carrier + '\'' +
                    '}';
        }
    }
}
