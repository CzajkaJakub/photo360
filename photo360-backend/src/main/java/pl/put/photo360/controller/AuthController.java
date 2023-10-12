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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import pl.put.photo360.auth.AuthService;
import pl.put.photo360.shared.dto.LoginRequestDto;
import pl.put.photo360.shared.dto.LoginResponseDto;
import pl.put.photo360.shared.dto.PasswordChangeRequestDto;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.dto.ResetPasswordConfirmationDto;
import pl.put.photo360.shared.dto.ResetPasswordRequestDto;
import pl.put.photo360.shared.dto.UserRoles;
import pl.put.photo360.tokenValidator.annotation.RequiredRole;

@RequestMapping( "/photo360/authorization" )
@RestController( "AuthController" )
@Tag( name = "Authorization controller, each endpoint requires public api key." )
public class AuthController
{
    private final AuthService userAuthService;

    @Autowired
    public AuthController( AuthService aUserAuthService )
    {
        userAuthService = aUserAuthService;
    }

    @PostMapping( "/login" )
    @Operation( summary = "Endpoint to authenticate user, public api key is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "202", description = "Successfully logged, returns jwt token with user details." ),
        @ApiResponse( responseCode = "401", description = "Wrong password." ),
        @ApiResponse( responseCode = "404", description = "User was not found by given login." ),
        @ApiResponse( responseCode = "423", description = "User account is locked, too many unsuccessful login requests." ) } )
    public ResponseEntity< LoginResponseDto > logIn( @RequestBody LoginRequestDto aLoginRequestDto )
    {
        var loginServerResponse = userAuthService.logIntoSystemAttempt( aLoginRequestDto );
        return new ResponseEntity<>( loginServerResponse, STATUS_USER_LOGGED.getStatus() );
    }

    @PostMapping( "/register" )
    @Operation( summary = "Create new user, public api key is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "201", description = "Successfully registered." ),
        @ApiResponse( responseCode = "406", description = "Wrong field validation." ),
        @ApiResponse( responseCode = "403", description = "User was given login/email already exists." ) } )
    public ResponseEntity< RequestResponseDto > createNewUser(
        @RequestBody RegisterRequestDto aRegisterRequestDto )
    {
        userAuthService.saveNewUser( aRegisterRequestDto, List.of( UserRoles.USER_ROLE ) );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_USER_CREATED ),
            STATUS_USER_CREATED.getStatus() );
    }

    @PutMapping( "/changePassword" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint allows to change currently logged user, public api key and jwt token is required to authenticate user." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Password successfully changed." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role or passed wrong old password." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token." ),
        @ApiResponse( responseCode = "406", description = "Wrong field validation." ),
        @ApiResponse( responseCode = "409", description = "Email is not verified." ) } )
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
    @Operation( summary = "Endpoint allows to verify user email, required to pass public api key, currently logged user's jwt token and verification code." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Email successfully verified." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token." ),
        @ApiResponse( responseCode = "406", description = "Email already verified/email token not valid/email token expired." ) } )
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
    @Operation( summary = "Endpoint allows to send verification email to currently logged user, required to pass public api key and currently logged user's jwt token." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Email successfully send." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token." ),
        @ApiResponse( responseCode = "406", description = "Email already verified" ) } )
    public ResponseEntity< RequestResponseDto > sendConfirmEmailRequest(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken )
    {
        userAuthService.sendConfirmEmailRequest( authorizationToken );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_EMAIL_SEND_SUCCESSFUL ),
            STATUS_EMAIL_SEND_SUCCESSFUL.getStatus() );
    }

    @PostMapping( "/resetPasswordRequest" )
    @Operation( summary = "Endpoint allows to send email request for password request, required to pass public api key." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Email successfully send." ),
        @ApiResponse( responseCode = "409", description = "Email is not verified verified" ),
        @ApiResponse( responseCode = "423", description = "User account is locked, too many unsuccessful login requests." ) } )
    public ResponseEntity< RequestResponseDto > requestPasswordReset(
        @RequestBody ResetPasswordRequestDto request )
    {
        userAuthService.requestPasswordReset( request );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND ),
            STATUS_RESET_PASSWORD_REQUEST_EMAIL_SEND.getStatus() );
    }

    @PostMapping( "/resetPasswordConfirmation" )
    @Operation( summary = "Endpoint allows to reset user's password, required to pass public api key, new password and generated reset password token." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Password changed successful." ),
        @ApiResponse( responseCode = "401", description = "UReset token expired." ),
        @ApiResponse( responseCode = "404", description = "User was not found by given email." ),
        @ApiResponse( responseCode = "406", description = "Passed same password as old one/validation not passed." ),
        @ApiResponse( responseCode = "409", description = "Email is not verified verified" ) } )
    public ResponseEntity< RequestResponseDto > confirmPasswordReset(
        @RequestBody ResetPasswordConfirmationDto request )
    {
        userAuthService.resetPassword( request );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_PASSWORD_CHANGED ),
            STATUS_PASSWORD_CHANGED.getStatus() );
    }
}
