package pl.put.photo360.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.dto.ServerResponseCode;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
class AuthControllerTest
{
    @Value( value = "${local.server.port}" )
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Configuration configuration;
    private HttpHeaders requiredHttpHeaders;
    private final HttpHeaders requiredHttpHeaders_missingPublicApiKey = new HttpHeaders();

    private String registerEndpointPath;

    @BeforeEach
    void setUp()
    {
        requiredHttpHeaders = new HttpHeaders();
        requiredHttpHeaders.set( "publicApiKey", configuration.getPUBLIC_API_KEY() );

        registerEndpointPath = "http://localhost:" + port + "/photo360/authorization/register";
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
            String testEmailUser = "testEmail@gmail.com";
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testPasswordUser, testEmailUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, requiredHttpHeaders_missingPublicApiKey ),
                    RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenLoginContainsWhiteMarks()
        {
            // GIVEN
            String testLoginUser = " testLogin ";
            String testPasswordUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testEmailUser = "testEmail@gmail.com";
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_FIELD_CONTAINS_WHITESPACES );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, requiredHttpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnFieldValidation_whenPasswordContainsWhiteMarks()
        {
            // GIVEN
            String testLoginUser =
                RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
            String testPasswordUser = " testPassword ";
            String testEmailUser = "testEmail@gmail.com";
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode =
                new RequestResponseDto( ServerResponseCode.STATUS_FIELD_CONTAINS_WHITESPACES );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, requiredHttpHeaders ), RequestResponseDto.class );

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
            String testEmailUser = "testEmail@gmail.com";
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_FIELD_SIZE );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, requiredHttpHeaders ), RequestResponseDto.class );

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
            String testEmailUser = "testEmail@gmail.com";
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_FIELD_SIZE );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, requiredHttpHeaders ), RequestResponseDto.class );

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
            String testEmailUser = "testowyEmail";
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_EMAIL_WRONG_FORMAT );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, requiredHttpHeaders ), RequestResponseDto.class );

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
            String testEmailUser = "testEmail@gmail.com";
            var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_USER_CREATED );

            // WHEN
            ResponseEntity< RequestResponseDto > response =
                restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( registerRequestDto, requiredHttpHeaders ), RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }
    }

    @Nested
    class LoginUserTests
    {

    }

    @Nested
    class ChangePasswordUserTests
    {

    }
}