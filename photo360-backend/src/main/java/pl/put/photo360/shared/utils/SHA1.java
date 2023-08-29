package pl.put.photo360.shared.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-1 algorithm
 */
public class SHA1
{
    private static byte[] getSHA1( String input ) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest = MessageDigest.getInstance( "SHA-1" );
        return messageDigest.digest( input.getBytes( StandardCharsets.UTF_8 ) );
    }

    private static String toHexString( byte[] hash )
    {
        StringBuilder hexString = new StringBuilder();
        for( byte b : hash )
        {
            String hex = Integer.toHexString( 0xff & b );
            if( hex.length() == 1 )
                hexString.append( '0' );
            hexString.append( hex );
        }
        return hexString.toString();
    }

    public static String sha1Hash( String input ) throws NoSuchAlgorithmException
    {
        return toHexString( getSHA1( input ) );
    }
}
