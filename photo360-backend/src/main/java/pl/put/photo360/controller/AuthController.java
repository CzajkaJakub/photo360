package pl.put.photo360.controller;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_SEND_SUCCESSFUL;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_EMAIL_VERIFIED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_PASSWORD_CHANGED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_CREATED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_LOGGED;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import pl.put.photo360.auth.AuthService;
import pl.put.photo360.shared.dto.LoginRequestDto;
import pl.put.photo360.shared.dto.LoginResponseDto;
import pl.put.photo360.shared.dto.PasswordChangeRequestDto;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.dto.ResetPasswordRequestDto;
import pl.put.photo360.shared.dto.UserRoles;
import pl.put.photo360.tokenValidator.annotation.RequiredRole;

@RequestMapping( "/photo360/authorization" )
@RestController( "AuthController" )
@Tag( name = "Authorization controller" )
public class AuthController
{
    private final AuthService userAuthService;

    @Autowired
    public AuthController( AuthService aUserAuthService )
    {
        userAuthService = aUserAuthService;
    }

    @PostMapping( "/login" )
    @Operation( summary = "Endpoint to authenticate user, public api key is required" )
    public ResponseEntity< LoginResponseDto > logIn( @RequestBody LoginRequestDto aLoginRequestDto )
    {
        var loginServerResponse = userAuthService.logIntoSystemAttempt( aLoginRequestDto );
        return new ResponseEntity<>( loginServerResponse, STATUS_USER_LOGGED.getStatus() );
    }

    @PostMapping( "/register" )
    @Operation( summary = "Endpoint which requires public api key to create new user" )
    public ResponseEntity< RequestResponseDto > createNewUser(
        @RequestBody RegisterRequestDto aRegisterRequestDto )
    {
        userAuthService.saveNewUser( aRegisterRequestDto, List.of( UserRoles.USER_ROLE ) );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_USER_CREATED ),
            STATUS_USER_CREATED.getStatus() );
    }

    @PutMapping( "/changePassword" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint which requires public api key to authenticate user" )
    public ResponseEntity< RequestResponseDto > changePassword(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken,
        @RequestBody PasswordChangeRequestDto aPasswordChangeRequestDto )
    {
        userAuthService.changeUserPassword( authorizationToken, aPasswordChangeRequestDto );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_PASSWORD_CHANGED ),
            STATUS_PASSWORD_CHANGED.getStatus() );
    }

    @GetMapping( "/confirmEmail/{emailVerificationCode}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    public ResponseEntity< RequestResponseDto > confirmEmail(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken,
        @PathVariable String emailVerificationCode )
    {
        userAuthService.confirmEmail( authorizationToken, emailVerificationCode );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_EMAIL_VERIFIED ),
            STATUS_EMAIL_VERIFIED.getStatus() );
    }

    @GetMapping( "/emailConfirmationRequest" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    public ResponseEntity< RequestResponseDto > sendConfirmEmailRequest(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken )
    {
        userAuthService.sendConfirmEmailRequest( authorizationToken );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_EMAIL_SEND_SUCCESSFUL ),
            STATUS_EMAIL_SEND_SUCCESSFUL.getStatus() );
    }

    @PostMapping( "/resetPasswordRequest" )
    public ResponseEntity< RequestResponseDto > requestPasswordReset(
        @RequestBody ResetPasswordRequestDto request )
    {
        userAuthService.requestPasswordReset( request );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND ),
            STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND.getStatus() );
    }

    @PostMapping( "/resetPasswordConfirmation" )
    public ResponseEntity< RequestResponseDto > confirmPasswordReset(
        @RequestBody ResetPasswordRequestDto request )
    {
        userAuthService.resetPassword( request );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND ),
            STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND.getStatus() );
    }
}
