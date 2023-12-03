package pl.put.photo360.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.exception.AccountLockedException;
import pl.put.photo360.exception.EmailExistsInDbException;
import pl.put.photo360.exception.ExpiredTokenException;
import pl.put.photo360.exception.FieldValidationException;
import pl.put.photo360.exception.LoginExistsInDbException;
import pl.put.photo360.exception.MissingRequiredFieldsException;
import pl.put.photo360.exception.ServiceException;
import pl.put.photo360.exception.TokenNotValidException;
import pl.put.photo360.exception.UnauthorizedRoleException;
import pl.put.photo360.exception.UserNotFoundException;
import pl.put.photo360.exception.WrongCredentialsException;
import pl.put.photo360.exception.WrongPasswordException;
import pl.put.photo360.exception.WrongPublicApiKeyException;
import pl.put.photo360.fieldValidator.FieldValidator;

@RestControllerAdvice
public class AdviceController
{
    /**
     * Handler for duplicated email.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( EmailExistsInDbException.class )
    public ResponseEntity< RequestResponseDto > handleEmailExistsInDbException( EmailExistsInDbException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for duplicated login.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( LoginExistsInDbException.class )
    public ResponseEntity< RequestResponseDto > handleLoginExistsInDbException( LoginExistsInDbException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for jwt token expiration.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( ExpiredTokenException.class )
    public ResponseEntity< RequestResponseDto > handleJwtTokenExpiredException( ExpiredTokenException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for user login request, when given login was not found in database.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( UserNotFoundException.class )
    public ResponseEntity< RequestResponseDto > userNotFoundHandler( UserNotFoundException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for wrong password.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( WrongPasswordException.class )
    public ResponseEntity< RequestResponseDto > userWrongPasswordHandler( WrongPasswordException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for wrong personal token.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( TokenNotValidException.class )
    public ResponseEntity< RequestResponseDto > userTokenValidationHandler( TokenNotValidException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for wrong public api key.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( WrongPublicApiKeyException.class )
    public ResponseEntity< RequestResponseDto > userWrongPublicApiKeyHandler( WrongPublicApiKeyException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for registration form, need to pass some rules in {@link FieldValidator}.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( FieldValidationException.class )
    public ResponseEntity< RequestResponseDto > registerFormValidatorHandler( FieldValidationException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for wrong credentials.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( WrongCredentialsException.class )
    public ResponseEntity< RequestResponseDto > handleWrongCredentialsException(
        WrongCredentialsException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for unauthorized role.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( UnauthorizedRoleException.class )
    public ResponseEntity< RequestResponseDto > handleUnauthorizedRoleException(
        UnauthorizedRoleException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for MissingRequiredFieldsException.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( MissingRequiredFieldsException.class )
    public ResponseEntity< RequestResponseDto > handleMissingRequiredFieldsException(
        MissingRequiredFieldsException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for account locked.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( AccountLockedException.class )
    public ResponseEntity< RequestResponseDto > handleAccountLockedException( AccountLockedException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }

    /**
     * Handler for service exceptions.
     *
     * @param aEx
     *                Exception
     */
    @ExceptionHandler( ServiceException.class )
    public ResponseEntity< RequestResponseDto > handleServiceException( ServiceException aEx )
    {
        return new ResponseEntity<>( new RequestResponseDto( aEx.getServerResponseCode() ),
            aEx.getServerResponseCode()
                .getStatus() );
    }
}
