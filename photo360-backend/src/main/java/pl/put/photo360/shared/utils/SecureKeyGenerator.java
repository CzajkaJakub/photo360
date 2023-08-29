package pl.put.photo360.shared.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates secure key.
 */
public class SecureKeyGenerator
{
    private static final int KEY_LENGTH_BYTES = 64;

    public static byte[] generateSecureKey()
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[ KEY_LENGTH_BYTES ];
        secureRandom.nextBytes( key );
        return Base64.getEncoder()
            .encode( key );
    }
}