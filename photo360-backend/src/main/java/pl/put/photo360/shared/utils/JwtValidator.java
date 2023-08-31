package pl.put.photo360.shared.utils;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_AUTH_TOKEN_EXPIRED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_UNAUTHORIZED_ROLE;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Getter;
import pl.put.photo360.shared.dto.UserRoles;
import pl.put.photo360.shared.exception.ExpiredTokenException;
import pl.put.photo360.shared.exception.TokenNotValidException;
import pl.put.photo360.shared.exception.UnauthorizedRoleException;

@Service
@Getter
public class JwtValidator
{
    private final SecureKeyGenerator secureKeyGenerator;
    private final Algorithm JWT_ALGORITHM;

    @Autowired
    public JwtValidator( SecureKeyGenerator aSecureKeyGenerator )
    {
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

    private List< UserRoles > getRoleFromToken( DecodedJWT decodedJWT )
    {
        var userRolesIds = decodedJWT.getClaim( "roles" )
            .asList( Long.class );
        return userRolesIds.stream()
            .map( UserRoles::get )
            .collect( Collectors.toList() );
    }

}
