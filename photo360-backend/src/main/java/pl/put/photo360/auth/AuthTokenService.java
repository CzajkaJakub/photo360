package pl.put.photo360.auth;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.entity.RoleEntity;
import pl.put.photo360.entity.UserDataEntity;
import pl.put.photo360.shared.utils.JwtValidator;

/**
 * AuthTokenService which handles token actions like generation, validation etc.
 */
@Component
public class AuthTokenService
{
    private final Configuration configuration;
    private final JwtValidator jwtValidator;

    @Autowired
    public AuthTokenService( Configuration aConfiguration, JwtValidator aJwtValidator )
    {
        configuration = aConfiguration;
        jwtValidator = aJwtValidator;
    }

    public String generateJwt( UserDataEntity loggedUser )
    {
        var tokenBuildTime = Instant.now();
        Map< String, Object > extraClaims = new HashMap<>()
        {
            {
                put( "typ", "JWT" );
            }
        };

        return JWT.create()
            .withSubject( loggedUser.getLogin() )
            .withIssuedAt( tokenBuildTime )
            .withExpiresAt( tokenBuildTime.plusSeconds( configuration.getTOKEN_EXPIRATION_TIME() ) )
            .withHeader( extraClaims )
            .withClaim( "roles", loggedUser.getRoles()
                .stream()
                .map( RoleEntity::getId )
                .toList() )
            .sign( jwtValidator.getJWT_ALGORITHM() );
    }
}
