/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

import org.apache.ldap.common.filter.BranchNormalizedVisitor;
import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.filter.FilterParserImpl;
import org.apache.ldap.common.message.DerefAliasesEnum;
import org.apache.ldap.common.message.Message;
import org.apache.ldap.common.message.MessageEncoder;
import org.apache.ldap.common.message.ScopeEnum;
import org.apache.ldap.common.message.SearchRequestImpl;
import org.apache.ldap.common.message.spi.Provider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Properties;

/**
 * Helper class to generate pre-computed PDU using Snacc.
 * PDUs are displayed on screen for pasting in your
 * Snickers test cases.
 * <p>
 * Proceed by changing the message() method to return the message
 * to encode. The main() method will display the corresponded encoded
 * PDU byte sequence.
 */
public class PDUGenerator
{
    private final MessageEncoder m_encoder;

    public PDUGenerator( Properties env )
    {
        m_encoder = new MessageEncoder( env );
    }

    public String generatePDU( Message msg )
    {
        return toString( encode( msg ) );
    }

    private byte[] encode( Message msg )
    {
        ByteBuffer buf = m_encoder.encodeBlocking( msg );
        byte[] bites = new byte[ buf.remaining() ];
        buf.get( bites );
        return bites;
    }

    private String toString( byte[] bites )
    {
        if ( bites.length == 0 ) return "{}";

        StringBuffer sb = new StringBuffer( "{");
        for ( int i = 0; i < bites.length; i++ )
        {
            byte bite = bites[i];
            sb.append( "0x" );
            String s = Integer.toHexString( bite ).toUpperCase( );
            if (s.length() < 2) sb.append( "0" );
            sb.append( s ).append( ", ");
        }
        sb.setLength( sb.length() - 2 );
        sb.append( "}");

        return sb.toString();
    }

    public static Message hardCodedMessage() throws IOException, ParseException
    {
        SearchRequestImpl req = new SearchRequestImpl( 33 );
        req.setBase( "dc=example,dc=com" );
        req.setDerefAliases( DerefAliasesEnum.DEREFFINDINGBASEOBJ );
        req.setScope( ScopeEnum.BASEOBJECT );
        req.setSizeLimit( 2);
        req.setTimeLimit( 3 );
        req.setTypesOnly( true );

        req.addAttribute( "attr0" );
        req.addAttribute( "attr1" );
        req.addAttribute( "attr2" );

        FilterParserImpl parser = new FilterParserImpl();
        ExprNode node = null ;
        node = parser.parse(
                "( | ( ou = Human Resources ) ( l=Santa Clara ) " +
                "( uid=akarasulu ) ( cn=aok ) ( cn=aok ) ( cn = abok) )" ) ;
        new BranchNormalizedVisitor().visit( node );
        req.setFilter( node );

        return req;
    }

    public static void main( String[] args ) throws Exception
    {
        final Message msg = hardCodedMessage();
        Properties env = new Properties();
        env.setProperty( Provider.BERLIB_PROVIDER,
                "org.apache.ldap.common.berlib.snacc.SnaccProvider" );
        PDUGenerator generator = new PDUGenerator( env );

        System.out.println( generator.generatePDU( msg ) );
    }
}
