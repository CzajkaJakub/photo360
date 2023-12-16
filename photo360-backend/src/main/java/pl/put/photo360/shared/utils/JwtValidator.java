package pl.put.photo360.shared.utils;

import static pl.put.photo360.dto.ServerResponseCode.STATUS_AUTH_TOKEN_EXPIRED;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_UNAUTHORIZED_ROLE;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Getter;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.UserRoles;
import pl.put.photo360.entity.RoleEntity;
import pl.put.photo360.entity.UserDataEntity;
import pl.put.photo360.exception.ExpiredTokenException;
import pl.put.photo360.exception.TokenNotValidException;
import pl.put.photo360.exception.UnauthorizedRoleException;

@Service
@Getter
public class JwtValidator
{
    private final Algorithm JWT_ALGORITHM;
    private final Configuration configuration;
    private final SecureKeyGenerator secureKeyGenerator;

    @Autowired
    public JwtValidator( SecureKeyGenerator aSecureKeyGenerator, Configuration aConfiguration )
    {
        configuration = aConfiguration;
        secureKeyGenerator = aSecureKeyGenerator;
        JWT_ALGORITHM = Algorithm.HMAC256( secureKeyGenerator.generateSecureKey() );
    }

    public void validateJwtToken( String token )
    {
        if( Objects.isNull( token ) || Objects.equals( token, StringUtils.EMPTY ) )
        {
            throw new TokenNotValidException( STATUS_AUTH_TOKEN_NOT_VALID );
        }
        try
        {
            JWT.require( JWT_ALGORITHM )
                .build()
                .verify( token );
        }
        catch( TokenExpiredException e )
        {
            throw new ExpiredTokenException( STATUS_AUTH_TOKEN_EXPIRED );
        }
        catch( Exception e )
        {
            throw new TokenNotValidException( STATUS_AUTH_TOKEN_NOT_VALID );
        }
    }

    public void validateJwtTokenWithRoles( String token, UserRoles requiredRole )
    {
        if( Objects.isNull( token ) || Objects.equals( token, StringUtils.EMPTY ) )
        {
            throw new TokenNotValidException( STATUS_AUTH_TOKEN_NOT_VALID );
        }
        try
        {
            DecodedJWT decodedJWT = JWT.require( JWT_ALGORITHM )
                .build()
                .verify( token );
            if( !getRoleFromToken( decodedJWT ).contains( requiredRole ) )
                throw new UnauthorizedRoleException( STATUS_UNAUTHORIZED_ROLE );
        }
        catch( TokenExpiredException e )
        {
            throw new ExpiredTokenException( STATUS_AUTH_TOKEN_EXPIRED );
        }
        catch( JWTVerificationException e )
        {
            throw new TokenNotValidException( STATUS_AUTH_TOKEN_NOT_VALID );
        }
    }

    public boolean isAdminRoleToken( String token )
    {
        if( Objects.isNull( token ) || Objects.equals( token, StringUtils.EMPTY ) )
        {
            return false;
        }

        DecodedJWT decodedJWT = JWT.require( JWT_ALGORITHM )
            .build()
            .verify( token );
        return getRoleFromToken( decodedJWT ).contains( UserRoles.ADMIN_ROLE );
    }

    private List< UserRoles > getRoleFromToken( DecodedJWT decodedJWT )
    {
        var userRolesIds = decodedJWT.getClaim( "roles" )
            .asList( Long.class );
        return userRolesIds.stream()
            .map( UserRoles::get )
            .toList();
    }

    public String extractLoginFromToken( String token )
    {
        try
        {
            DecodedJWT decodedJWT = JWT.require( JWT_ALGORITHM )
                .build()
                .verify( token );
            return decodedJWT.getSubject();
        }
        catch( TokenExpiredException e )
        {
            throw new ExpiredTokenException( STATUS_AUTH_TOKEN_EXPIRED );
        }
        catch( JWTVerificationException e )
        {
            throw new TokenNotValidException( STATUS_AUTH_TOKEN_NOT_VALID );
        }
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
            .sign( JWT_ALGORITHM );
    }
}
