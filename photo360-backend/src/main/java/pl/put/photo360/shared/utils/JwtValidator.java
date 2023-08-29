package pl.put.photo360.shared.utils;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_AUTH_TOKEN_EXPIRED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_UNAUTHORIZED_ROLE;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import pl.put.photo360.shared.dto.UserRole;
import pl.put.photo360.shared.exception.ExpiredTokenException;
import pl.put.photo360.shared.exception.TokenNotValidException;
import pl.put.photo360.shared.exception.UnauthorizedRoleException;

public class JwtValidator
{
    public static final Algorithm JWT_ALGORITHM = Algorithm.HMAC256( SecureKeyGenerator.generateSecureKey() );

    public static void validateJwtToken( String token )
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

    public static void validateJwtToken( String token, UserRole requiredRole )
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
            if( !requiredRole.equals( getRoleFromToken( decodedJWT ) ) )
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

    private static UserRole getRoleFromToken( DecodedJWT decodedJWT )
    {
        int userRoleId = decodedJWT.getHeaderClaim( "role" )
            .asInt();
        return UserRole.get( userRoleId );
    }
}
