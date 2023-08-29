package pl.put.photo360.shared.api;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.put.photo360.shared.dto.ServerResponseCode;
import pl.put.photo360.shared.exception.WrongPublicApiKeyException;

public class PublicKeyValidator implements ConstraintValidator< PublicKeyConstraint, String >
{
    private static final String publicKey =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJpt3Bs6fwMc2S7h5cpIP6nkG9DsISp0MfKTpwtt31/a1ZF2+Pv8I0f64CIcBj4GPWP4PWWe9nI4WSUKkf5CdxT6sUh4toHvBemfQiSw3sCaHfgL0WBrdqhqIxYUwsedb9ZuCXRp6acmbvqttNI2r5V8rsuT0nTDYCnVTl5OgnQIDAQAB";

    public static void isValid( String aPublicKey )
    {
        if( !aPublicKey.equals( publicKey ) )
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
        return aPublicKey.equals( publicKey );
    }
}
