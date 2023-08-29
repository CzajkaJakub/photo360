package pl.put.photo360.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.exception.EmailExistsInDbException;
import pl.put.photo360.shared.exception.ExpiredTokenException;
import pl.put.photo360.shared.exception.FieldValidationException;
import pl.put.photo360.shared.exception.LoginExistsInDbException;
import pl.put.photo360.shared.exception.TokenNotValidException;
import pl.put.photo360.shared.exception.UserNotFoundException;
import pl.put.photo360.shared.exception.WrongCredentialsException;
import pl.put.photo360.shared.exception.WrongPasswordException;
import pl.put.photo360.shared.exception.WrongPublicApiKeyException;
import pl.put.photo360.shared.fieldValidator.FieldValidator;

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
     * Handler for registration form, need to pass some rules in
     * {@link FieldValidator}.
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
}
