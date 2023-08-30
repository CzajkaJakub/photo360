package pl.put.photo360.controller;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_PASSWORD_CHANGED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_CREATED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_LOGGED;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import pl.put.photo360.auth.AuthService;
import pl.put.photo360.shared.dto.LoginRequestDto;
import pl.put.photo360.shared.dto.LoginResponseDto;
import pl.put.photo360.shared.dto.PasswordChangeRequestDto;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.fieldValidator.FieldValidator;

@RequestMapping( "/photo360/authorization" )
@RestController( "AuthController" )
public class AuthController
{
    private final AuthService userAuthService;

    @Autowired
    public AuthController( AuthService aUserAuthService )
    {
        userAuthService = aUserAuthService;
    }

    @PostMapping( "/logIn" )
    @ApiOperation( "Endpoint to authenticate user, public api key is required" )
    public ResponseEntity< LoginResponseDto > logIn( @RequestBody LoginRequestDto aLoginRequestDto )
    {
        var loginServerResponse = userAuthService.logIntoSystemAttempt( aLoginRequestDto );
        return new ResponseEntity<>( loginServerResponse, STATUS_USER_LOGGED.getStatus() );
    }

    @PostMapping( "/register" )
    @ApiOperation( "Endpoint which requires public api key to create new user" )
    public ResponseEntity< RequestResponseDto > createNewUser(
        @RequestBody RegisterRequestDto aRegisterRequestDto ) throws NoSuchAlgorithmException, IOException
    {
        FieldValidator.validateRegisterForm( aRegisterRequestDto );
        userAuthService.saveNewUser( aRegisterRequestDto );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_USER_CREATED ),
            STATUS_USER_CREATED.getStatus() );
    }

    @PutMapping( "/change-password" )
    @ApiOperation( "Endpoint which requires public api key to authenticate user" )
    public ResponseEntity< RequestResponseDto > changePassword(
        @RequestBody PasswordChangeRequestDto aPasswordChangeRequestDto )
        throws NoSuchAlgorithmException, IOException
    {
        userAuthService.changeUserPassword( aPasswordChangeRequestDto );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_PASSWORD_CHANGED ),
            STATUS_PASSWORD_CHANGED.getStatus() );
    }
}
