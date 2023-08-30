package pl.put.photo360.auth;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.entity.UserDataEntity;
import pl.put.photo360.shared.utils.JwtValidator;

/**
 * AuthTokenService which handles token actions like generation, validation etc.
 */
@Component
public class AuthTokenService
{
    private final Configuration configuration;

    @Autowired
    public AuthTokenService( Configuration aConfiguration )
    {
        configuration = aConfiguration;
    }

    public String generateJwt( Map< String, Object > extraClaims, UserDataEntity registeredUser )
    {
        if( extraClaims == null )
        {
            extraClaims = new HashMap<>( 2 );
        }
        extraClaims.put( "typ", "JWT" );
        extraClaims.put( "role", registeredUser.getRole()
            .getId() );

        return JWT.create()
            .withSubject( registeredUser.getLogin() )
            .withIssuedAt( Instant.now() )
            .withExpiresAt( Instant.now()
                .plusSeconds( configuration.getTOKEN_EXPIRATION_TIME() ) )
            .withHeader( extraClaims )
            .sign( JwtValidator.JWT_ALGORITHM );
    }

    public String generateJwt( Map< String, Object > extraClaims, String login )
    {
        if( extraClaims == null )
        {
            extraClaims = new HashMap<>( 2 );
        }
        extraClaims.put( "typ", "JWT" );

        return JWT.create()
            .withSubject( login )
            .withIssuedAt( Instant.now() )
            .withExpiresAt( Instant.now()
                .plusSeconds( configuration.getTOKEN_EXPIRATION_TIME() ) )
            .withHeader( extraClaims )
            .sign( JwtValidator.JWT_ALGORITHM );
    }
}
