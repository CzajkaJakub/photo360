package pl.put.photo360.shared.utils;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.put.photo360.config.Configuration;

/**
 * Generates secure key.
 */
@Service
public class SecureKeyGenerator
{
    private final Configuration configuration;

    @Autowired
    public SecureKeyGenerator( Configuration configuration )
    {
        this.configuration = configuration;
    }

    public byte[] generateSecureKey()
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[ configuration.getKEY_LENGTH_BYTES() ];
        secureRandom.nextBytes( key );
        return Base64.getEncoder()
            .encode( key );
    }
}