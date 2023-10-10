package pl.put.photo360.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import pl.put.photo360.auth.AuthService;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.shared.dto.LoginRequestDto;
import pl.put.photo360.shared.dto.LoginResponseDto;
import pl.put.photo360.shared.dto.PasswordChangeRequestDto;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.dto.ServerResponseCode;
import pl.put.photo360.shared.dto.UserRoles;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
class AuthControllerTest
{
    private final String gmailSuffix = "@gmail.com";
    private final String testLoginUserWithoutRole = "LOGIN_EMPTY_ROLE_ACCOUNT";
    private final String testEmailUserWithoutRole = "EMPTY_ROLE_ACCOUNT@gmail.com";
    private final String testPasswordUserWithoutRole = "PASSWORD_EMPTY_ROLE_ACCOUNT";
    private final String testLoginAdmin = "LOGIN_ADMIN_ACCOUNT";
    private final String testEmailAdmin = "ADMIN_ACCOUNT@gmail.com";
    private final String testPasswordAdmin = "PASSWORD_ADMIN_ACCOUNT";
    @Value( value = "${local.server.port}" )
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private Configuration configuration;
    @Autowired
    private AuthService authService;
    private HttpHeaders httpHeaders;
    private String registerEndpointPath;
    private String loginEndpointPath;
    private String changePasswordEndpointPath;

    @BeforeAll
    void setUp()
    {
        httpHeaders = new HttpHeaders();
        httpHeaders.set( "publicApiKey", configuration.getPUBLIC_API_KEY() );

        registerEndpointPath = "http://localhost:" + port + "/photo360/authorization/register";
        loginEndpointPath = "http://localhost:" + port + "/photo360/authorization/login";
        changePasswordEndpointPath = "http://localhost:" + port + "/photo360/authorization/changePassword";

        try
        {
            authService.saveNewUser( new RegisterRequestDto( testLoginUserWithoutRole,
                testEmailUserWithoutRole, testPasswordUserWithoutRole ), List.of() );
        }
        catch( Exception ignore )
        {
        }

        try
        {
            authService.saveNewUser(
                new RegisterRequestDto( testLoginAdmin, testEmailAdmin, testPasswordAdmin ),
                List.of( UserRoles.USER_ROLE, UserRoles.ADMIN_ROLE ) );
        }
        catch( Exception ignore )
        {
        }
    }

    @Nested
    class RegisterUserTests
    {
        @Test
        void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var httpHeadersMissingPublicApiKey = new HttpHeaders();
            httpHeadersMissingPublicApiKey.putAll( httpHeaders );
            httpHeadersMissingPublicApiKey.set( "publicApiKey", null );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testPasswordUser, testEmailUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( registerEndpointPath,
                HttpMethod.POST, new HttpEntity<>( registerRequestDto, httpHeadersMissingPublicApiKey ),
                RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenLoginNull()
        {
            // GIVEN
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( null, testEmailUser, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_MISSING_REQUIRED_FIELD );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenPasswordNull()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, null );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_MISSING_REQUIRED_FIELD );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenEmailNull()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, null, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_MISSING_REQUIRED_FIELD );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenLoginContainsWhiteMarks()
        {
            // GIVEN
            String testLoginUser = " ";
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_FIELD_CONTAINS_WHITESPACES );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenPasswordContainsWhiteMarks()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser = " ";
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_FIELD_CONTAINS_WHITESPACES );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenPasswordTooShort()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMIN_REGISTER_PASSWORD_LENGTH() - 1 );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_FIELD_SIZE );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenPasswordTooLong()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() + 1 );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_FIELD_SIZE );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnEmailFormatValidation_whenEmailNotMatchFormat()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_EMAIL_WRONG_FORMAT );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldRegisterUserSuccessful()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_USER_CREATED );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnExistedLoginCode_whenTryToCreateAccountWhenFieldIsNotAvailable()
        {
            // GIVEN
            String testLoginUser1 =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser1 =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            String testEmailUser2 =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto =
                new RegisterRequestDto( testLoginUser1, testEmailUser1, testPasswordUser );
            var registerRequestDto2 =
                new RegisterRequestDto( testLoginUser1, testEmailUser2, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_LOGIN_ALREADY_EXISTS );

            // WHEN

            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto2, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnExistedEmailCode_whenTryToCreateAccountWhenFieldIsNotAvailable()
        {
            // GIVEN
            String testLoginUser1 =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser1 =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            String testLoginUser2 =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            var registerRequestDto =
                new RegisterRequestDto( testLoginUser1, testEmailUser1, testPasswordUser );
            var registerRequestDto2 =
                new RegisterRequestDto( testLoginUser2, testEmailUser1, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_EMAIL_ALREADY_EXISTS );

            // WHEN

            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto2, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }
    }

    @Nested
    class LoginUserTests
    {
        @Test
        void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var httpHeadersMissingPublicApiKey = new HttpHeaders();
            httpHeadersMissingPublicApiKey.putAll( httpHeaders );
            httpHeadersMissingPublicApiKey.set( "publicApiKey", null );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );

            var response = restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeadersMissingPublicApiKey ),
                RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldLoginUserSuccessful_checkNullLastDateLoggedWhenLogFirstTime()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var response = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );

            // Then
            assertEquals( testEmailUser, Objects.requireNonNull( response.getBody() )
                .getEmail() );
            assertNull( null, Objects.requireNonNull( response.getBody() )
                .get_lastLoggedDatetime() );
            assertNotNull( Objects.requireNonNull( response.getBody() )
                .get_token() );
            assertNotNull( Objects.requireNonNull( response.getBody() )
                .get_tokenExpirationDate() );
        }

        @Test
        void shouldLoginUserSuccessful_checkLastDateLoggedWhenLogSecondTime()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );
            var response = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );

            // Then
            assertEquals( testEmailUser, Objects.requireNonNull( response.getBody() )
                .getEmail() );
            assertNotNull( Objects.requireNonNull( response.getBody() )
                .get_lastLoggedDatetime() );
            assertNotNull( Objects.requireNonNull( response.getBody() )
                .get_token() );
            assertNotNull( Objects.requireNonNull( response.getBody() )
                .get_tokenExpirationDate() );
        }

        @Test
        void shouldReturnLoginNotFound_whenPassedWrongLogin()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String wrongTestLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( wrongTestLoginUser, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_USER_NOT_FOUND_BY_LOGIN );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var response = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnPasswordNotFound_whenPassedWrongPassword()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordWrongUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordWrongUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PASSWORD );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var response = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldLockAccount_whenPassedWrongPassword()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordWrongUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var wrongLoginRequestDto = new LoginRequestDto( testLoginUser, testPasswordWrongUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_ACCOUNT_LOCKED );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            IntStream.range( 0, configuration.getMAX_LOGIN_ATTEMPT() )
                .forEach( attempt -> restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( wrongLoginRequestDto, httpHeaders ), RequestResponseDto.class ) );

            var response = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( wrongLoginRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnMissingFieldsCode_whenPassedNullAsLogin()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( null, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_MISSING_REQUIRED_FIELD );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var response = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnWrongPassword_whenPassedNullAsPassword()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, null );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PASSWORD );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var response = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }
    }

    @Nested
    class ChangePasswordUserTests
    {
        @Test
        void shouldReturnNotAllowedStatus_whenTokenNotAddedInHeader()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String changedTestPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var changePasswordRequestDto =
                new PasswordChangeRequestDto( testPasswordUser, changedTestPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var result = restTemplate.exchange( changePasswordEndpointPath, HttpMethod.PUT,
                new HttpEntity<>( changePasswordRequestDto, httpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, result.getBody() );
        }

        @Test
        void shouldReturnNotVerifiedAccount_whenEmailNotVerified()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String changedTestPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var headerWithToken = new HttpHeaders();
            headerWithToken.putAll( httpHeaders );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );
            var changePasswordRequestDto =
                new PasswordChangeRequestDto( testPasswordUser, changedTestPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_EMAIL_NOT_CONFIRMED );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var loginResponse = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );
            var authToken = Objects.requireNonNull( loginResponse.getBody() )
                .get_token();
            headerWithToken.set( HttpHeaders.AUTHORIZATION, authToken );
            var result = restTemplate.exchange( changePasswordEndpointPath, HttpMethod.PUT,
                new HttpEntity<>( changePasswordRequestDto, headerWithToken ), RequestResponseDto.class );

            // Then
            assertNotNull( authToken );
            assertEquals( expectedResultCode, result.getBody() );
        }

        @Test
        void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String changedTestPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );

            var httpHeadersMissingPublicApiKey = new HttpHeaders();
            httpHeadersMissingPublicApiKey.putAll( httpHeaders );
            httpHeadersMissingPublicApiKey.set( "publicApiKey", null );

            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );
            var changePasswordRequestDto =
                new PasswordChangeRequestDto( testPasswordUser, changedTestPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var loginResponse = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );
            var authToken = Objects.requireNonNull( loginResponse.getBody() )
                .get_token();
            httpHeadersMissingPublicApiKey.set( HttpHeaders.AUTHORIZATION, authToken );
            var response = restTemplate.exchange( changePasswordEndpointPath, HttpMethod.PUT,
                new HttpEntity<>( changePasswordRequestDto, httpHeadersMissingPublicApiKey ),
                RequestResponseDto.class );

            // Then
            assertNotNull( authToken );
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnEmailNotVerified_whenAccountEmailNotVerified()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String changedTestPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            String wrongTestPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            var headerWithToken = new HttpHeaders();
            headerWithToken.putAll( httpHeaders );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );
            var changePasswordRequestDto =
                new PasswordChangeRequestDto( wrongTestPasswordUser, changedTestPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_EMAIL_NOT_CONFIRMED );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var loginResponse = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );
            var authToken = Objects.requireNonNull( loginResponse.getBody() )
                .get_token();
            headerWithToken.set( HttpHeaders.AUTHORIZATION, authToken );
            var result = restTemplate.exchange( changePasswordEndpointPath, HttpMethod.PUT,
                new HttpEntity<>( changePasswordRequestDto, headerWithToken ), RequestResponseDto.class );

            // Then
            assertNotNull( authToken );
            assertEquals( expectedResultCode, result.getBody() );
        }

        @Test
        void shouldReturnStatus_whenWrongNullPassedAsPassword()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String changedTestPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var headerWithToken = new HttpHeaders();
            headerWithToken.putAll( httpHeaders );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );
            var changePasswordRequestDto = new PasswordChangeRequestDto( null, changedTestPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_MISSING_REQUIRED_FIELD );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var loginResponse = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );
            var authToken = Objects.requireNonNull( loginResponse.getBody() )
                .get_token();
            headerWithToken.set( HttpHeaders.AUTHORIZATION, authToken );
            var result = restTemplate.exchange( changePasswordEndpointPath, HttpMethod.PUT,
                new HttpEntity<>( changePasswordRequestDto, headerWithToken ), RequestResponseDto.class );

            // Then
            assertNotNull( authToken );
            assertEquals( expectedResultCode, result.getBody() );
        }

        @Test
        void shouldReturnStatus_whenWrongNullPassedAsNewPassword()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                    .concat( gmailSuffix );
            var headerWithToken = new HttpHeaders();
            headerWithToken.putAll( httpHeaders );
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );
            var changePasswordRequestDto = new PasswordChangeRequestDto( testPasswordUser, null );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_MISSING_REQUIRED_FIELD );

            // WHEN
            restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                new HttpEntity<>( registerRequestDto, httpHeaders ), RequestResponseDto.class );
            var loginResponse = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );
            var authToken = Objects.requireNonNull( loginResponse.getBody() )
                .get_token();
            headerWithToken.set( HttpHeaders.AUTHORIZATION, authToken );
            var result = restTemplate.exchange( changePasswordEndpointPath, HttpMethod.PUT,
                new HttpEntity<>( changePasswordRequestDto, headerWithToken ), RequestResponseDto.class );

            // Then
            assertNotNull( authToken );
            assertEquals( expectedResultCode, result.getBody() );
        }

        @Test
        void shouldReturnStatus_whenUnauthorizedRole()
        {
            // GIVEN
            var headerWithToken = new HttpHeaders();
            headerWithToken.putAll( httpHeaders );
            var loginRequestDto =
                new LoginRequestDto( testLoginUserWithoutRole, testPasswordUserWithoutRole );

            var changePasswordRequestDto =
                new PasswordChangeRequestDto( testLoginUserWithoutRole, testPasswordUserWithoutRole );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

            // WHEN
            var loginResponse = restTemplate.exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, httpHeaders ), LoginResponseDto.class );
            var authToken = Objects.requireNonNull( loginResponse.getBody() )
                .get_token();
            headerWithToken.set( HttpHeaders.AUTHORIZATION, authToken );
            var result = restTemplate.exchange( changePasswordEndpointPath, HttpMethod.PUT,
                new HttpEntity<>( changePasswordRequestDto, headerWithToken ), RequestResponseDto.class );

            // Then
            assertNotNull( authToken );
            assertEquals( expectedResultCode, result.getBody() );
        }
    }
}