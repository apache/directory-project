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
package org.apache.ldap.common.berlib.asn1.encoder;


import org.apache.asn1.ber.DeterminateLengthVisitor;
import org.apache.asn1.ber.TupleEncodingVisitor;
import org.apache.asn1.ber.TupleNode;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1.codec.stateful.EncoderCallback;
import org.apache.asn1.codec.stateful.EncoderMonitor;
import org.apache.asn1.codec.stateful.StatefulEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.abandon.AbandonRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.add.AddRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.add.AddResponseEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.bind.BindRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.bind.BindResponseEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.compare.CompareRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.compare.CompareResponseEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.delete.DeleteRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.delete.DeleteResponseEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.extended.ExtendedRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.extended.ExtendedResponseEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.modify.ModifyRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.modify.ModifyResponseEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.modifyDn.ModifyDnRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.modifyDn.ModifyDnResponseEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.search.SearchRequestEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.search.SearchResponseDoneEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.search.SearchResponseEntryEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.search.SearchResponseReferenceEncoder;
import org.apache.ldap.common.berlib.asn1.encoder.unbind.UnbindRequestEncoder;
import org.apache.ldap.common.message.AbandonRequest;
import org.apache.ldap.common.message.AddRequest;
import org.apache.ldap.common.message.AddResponse;
import org.apache.ldap.common.message.BindRequest;
import org.apache.ldap.common.message.BindResponse;
import org.apache.ldap.common.message.CompareRequest;
import org.apache.ldap.common.message.CompareResponse;
import org.apache.ldap.common.message.DeleteRequest;
import org.apache.ldap.common.message.DeleteResponse;
import org.apache.ldap.common.message.ExtendedRequest;
import org.apache.ldap.common.message.ExtendedResponse;
import org.apache.ldap.common.message.Message;
import org.apache.ldap.common.message.MessageTypeEnum;
import org.apache.ldap.common.message.ModifyDnRequest;
import org.apache.ldap.common.message.ModifyDnResponse;
import org.apache.ldap.common.message.ModifyRequest;
import org.apache.ldap.common.message.ModifyResponse;
import org.apache.ldap.common.message.SearchRequest;
import org.apache.ldap.common.message.SearchResponseDone;
import org.apache.ldap.common.message.SearchResponseEntry;
import org.apache.ldap.common.message.SearchResponseReference;
import org.apache.ldap.common.message.UnbindRequest;



/**
 * A Snickers based LDAP message producer.  The generated events via the callback
 * are TLV tuples.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SnickersLdapEncoder implements StatefulEncoder
{
    EncoderMonitor monitor;
    TupleEncodingVisitor encoder = new TupleEncodingVisitor();
    DeterminateLengthVisitor lengthVisitor = new DeterminateLengthVisitor();


    public void encode( Object obj ) throws EncoderException
    {
        Message msg = ( Message ) obj;
        TupleNode root = null;

        switch( msg.getType().getValue() )
        {
            case( MessageTypeEnum.ABANDONREQUEST_VAL ):
                root = AbandonRequestEncoder.INSTANCE
                        .encode( ( AbandonRequest ) msg );
                break;

            case( MessageTypeEnum.ADDREQUEST_VAL ):
                root = AddRequestEncoder.INSTANCE
                        .encode( ( AddRequest ) msg );
                break;

            case( MessageTypeEnum.ADDRESPONSE_VAL ):
                root = AddResponseEncoder.INSTANCE
                        .encode( ( AddResponse ) msg );
                break;

            case( MessageTypeEnum.BINDREQUEST_VAL ):
                root = BindRequestEncoder.INSTANCE
                        .encode( ( BindRequest ) msg );
                break;

            case( MessageTypeEnum.BINDRESPONSE_VAL ):
                root = BindResponseEncoder.INSTANCE
                        .encode( ( BindResponse ) msg );
                break;

            case( MessageTypeEnum.COMPAREREQUEST_VAL ):
                root = CompareRequestEncoder.INSTANCE
                    .encode( ( CompareRequest ) msg );

            case( MessageTypeEnum.COMPARERESPONSE_VAL ):
                root = CompareResponseEncoder.INSTANCE
                    .encode( ( CompareResponse ) msg );
                break;
            case( MessageTypeEnum.DELREQUEST_VAL ):
                root = DeleteRequestEncoder.INSTANCE
                        .encode( ( DeleteRequest ) msg );
                break;

            case( MessageTypeEnum.DELRESPONSE_VAL ):
                root = DeleteResponseEncoder.INSTANCE
                        .encode( ( DeleteResponse ) msg );
                break;

            case( MessageTypeEnum.EXTENDEDREQ_VAL ):
                root = ExtendedRequestEncoder.INSTANCE
                        .encode( ( ExtendedRequest ) msg );
                break;

            case( MessageTypeEnum.EXTENDEDRESP_VAL ):
                root = ExtendedResponseEncoder.INSTANCE
                        .encode( ( ExtendedResponse ) msg );
                break;

            case( MessageTypeEnum.MODIFYREQUEST_VAL ):
                root = ModifyRequestEncoder.INSTANCE
                        .encode( ( ModifyRequest ) msg );
                break;

            case( MessageTypeEnum.MODIFYRESPONSE_VAL ):
                root = ModifyResponseEncoder.INSTANCE
                        .encode( ( ModifyResponse ) msg );
                break;

            case( MessageTypeEnum.MODDNREQUEST_VAL ):
                root = ModifyDnRequestEncoder.INSTANCE
                        .encode( ( ModifyDnRequest ) msg );
                break;

            case( MessageTypeEnum.MODDNRESPONSE_VAL ):
                root = ModifyDnResponseEncoder.INSTANCE
                        .encode( ( ModifyDnResponse ) msg );
                break;

            case( MessageTypeEnum.SEARCHREQUEST_VAL ):
                root = SearchRequestEncoder.INSTANCE
                        .encode( ( SearchRequest ) msg );
                break;

            case( MessageTypeEnum.SEARCHRESDONE_VAL ):
                root = SearchResponseDoneEncoder.INSTANCE
                        .encode( ( SearchResponseDone ) msg );
                break;

            case( MessageTypeEnum.SEARCHRESENTRY_VAL ):
                root = SearchResponseEntryEncoder.INSTANCE
                        .encode( ( SearchResponseEntry ) msg );
                break;

            case( MessageTypeEnum.SEARCHRESREF_VAL ):
                root = SearchResponseReferenceEncoder.INSTANCE
                        .encode( ( SearchResponseReference ) msg );
                break;

            case( MessageTypeEnum.UNBINDREQUEST_VAL ):
                root = UnbindRequestEncoder.INSTANCE
                        .encode( ( UnbindRequest ) msg );
                break;

            default:
                IllegalArgumentException e = new IllegalArgumentException(
                        "Unable to encode unrecognized object: " + obj ) ;
                if ( monitor != null )
                {
                    monitor.error( this, e );
                }
                
                throw e;
        }

        // use determinate length vistor to set all tlvs to use set lengths
        root.accept( lengthVisitor );

        // now encode using the another visitor
        root.accept( encoder );

        // now make the encoder flush out the ByteBuffers with an encodeOccurred
        encoder.flush();
    }


    public void setCallback( EncoderCallback cb )
    {
        encoder.setCallback( cb );

        if ( monitor != null )
        {
            monitor.callbackSet( this, null, cb );
            return;
        }
    }


    public void setEncoderMonitor( EncoderMonitor monitor )
    {
        this.monitor = monitor;
        this.encoder.setEncoderMonitor( monitor );
    }
}
