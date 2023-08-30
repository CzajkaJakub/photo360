package pl.put.photo360.shared.api;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.shared.dto.ServerResponseCode;
import pl.put.photo360.shared.exception.WrongPublicApiKeyException;

public class PublicKeyValidator implements ConstraintValidator< PublicKeyConstraint, String >
{
    private static Configuration configuration;

    @Autowired
    public PublicKeyValidator( Configuration aConfiguration )
    {
        configuration = aConfiguration;
    }

    public static void isValid( String aPublicKey )
    {
        if( !aPublicKey.equals( configuration.getPUBLIC_API_KEY() ) )
            throw new WrongPublicApiKeyException( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );
    }

    @Override
    public void initialize( PublicKeyConstraint aPublicKeyConstraint )
    {
        ConstraintValidator.super.initialize( aPublicKeyConstraint );
    }

    @Override
    public boolean isValid( String aPublicKey, ConstraintValidatorContext aConstraintValidatorContext )
    {
        return aPublicKey.equals( configuration.getPUBLIC_API_KEY() );
    }
}
