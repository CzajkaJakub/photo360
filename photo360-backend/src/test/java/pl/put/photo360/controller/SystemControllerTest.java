package pl.put.photo360.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.shared.dto.LoginRequestDto;
import pl.put.photo360.shared.dto.LoginResponseDto;
import pl.put.photo360.shared.dto.PhotoDataDto;
import pl.put.photo360.shared.dto.RegisterRequestDto;
import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.dto.ServerResponseCode;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class SystemControllerTest
{
    @Value( value = "${local.server.port}" )
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private Configuration configuration;
    private HttpHeaders requiredHttpHeaders;
    private String downloadPublicGifsEndpointPath;
    private String downloadPrivateGifsEndpointPath;
    private String downloadAllGifsEndpointPath;
    private String uploadPhotosEndpointPath;
    private HttpHeaders requiredHttpHeadersWithAdminToken;
    private HttpHeaders requiredHttpHeaders_missingUserToken;
    private HttpHeaders requiredHttpHeaders_missingPublicApiKey;

    @BeforeAll
    public void registerAndLogTestUser_setUp()
    {
        requiredHttpHeaders = new HttpHeaders();
        requiredHttpHeaders.set( "publicApiKey", configuration.getPUBLIC_API_KEY() );

        requiredHttpHeaders_missingUserToken = new HttpHeaders();
        requiredHttpHeaders_missingUserToken.set( "publicApiKey", configuration.getPUBLIC_API_KEY() );

        requiredHttpHeadersWithAdminToken = new HttpHeaders();
        requiredHttpHeadersWithAdminToken.set( "publicApiKey", configuration.getPUBLIC_API_KEY() );

        requiredHttpHeaders_missingPublicApiKey = new HttpHeaders();

        String registerEndpointPath = "http://localhost:" + port + "/photo360/authorization/register";
        String loginEndpointPath = "http://localhost:" + port + "/photo360/authorization/login";
        uploadPhotosEndpointPath = "http://localhost:" + port + "/photo360/uploadPhotos";
        downloadPublicGifsEndpointPath = "http://localhost:" + port + "/photo360/downloadPublicGifs";
        downloadPrivateGifsEndpointPath = "http://localhost:" + port + "/photo360/downloadPrivateGifs";
        downloadAllGifsEndpointPath = "http://localhost:" + port + "/photo360/downloadAllGifs";

        String testLoginUser =
            RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
        String testPasswordUser =
            RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() );
        String gmailSuffix = "@gmail.com";
        String testEmailUser =
            RandomStringUtils.randomAlphabetic( configuration.getMAX_REGISTER_FIELD_LENGTH() )
                .concat( gmailSuffix );
        var loginRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );
        var registerRequestDto = new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser );
        var adminLoginRequestDto = new LoginRequestDto( "LOGIN_ADMIN_ACCOUNT", "PASSWORD_ADMIN_ACCOUNT" );

        restTemplate.exchange( registerEndpointPath, HttpMethod.POST,
            new HttpEntity<>( registerRequestDto, requiredHttpHeaders ), RequestResponseDto.class );
        LoginResponseDto loginResponseDto = restTemplate
            .exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( loginRequestDto, requiredHttpHeaders ), LoginResponseDto.class )
            .getBody();
        LoginResponseDto adminLoginResponseDto = restTemplate
            .exchange( loginEndpointPath, HttpMethod.POST,
                new HttpEntity<>( adminLoginRequestDto, requiredHttpHeaders ), LoginResponseDto.class )
            .getBody();

        assert loginResponseDto != null;
        assert adminLoginResponseDto != null;
        requiredHttpHeaders.set( HttpHeaders.AUTHORIZATION, loginResponseDto.get_token() );
        requiredHttpHeadersWithAdminToken.set( HttpHeaders.AUTHORIZATION, adminLoginResponseDto.get_token() );
        requiredHttpHeaders_missingPublicApiKey.set( HttpHeaders.AUTHORIZATION,
            loginResponseDto.get_token() );
    }

    @Nested
    @TestInstance( TestInstance.Lifecycle.PER_CLASS )
    class UploadPhotosTests
    {
        private final MultiValueMap< String, Object > formData = new LinkedMultiValueMap<>();
        private final MultiValueMap< String, Object > formDataWrongFormat = new LinkedMultiValueMap<>();
        private final MultiValueMap< String, Object > formDataWrongInput = new LinkedMultiValueMap<>();

        @BeforeAll
        public void createFormData()
        {
            FileSystemResource fileResource =
                new FileSystemResource( "src/test/java/pl/put/photo360/resources/testGif.zip" );
            FileSystemResource fileResourceWrongFormat =
                new FileSystemResource( "src/test/java/pl/put/photo360/resources/testPdf.pdf" );
            FileSystemResource fileResourcePdfInZipFile =
                new FileSystemResource( "src/test/java/pl/put/photo360/resources/pdfInZip.zip" );

            formData.add( "isPublic", true );
            formData.add( "description", "testDescription" );

            formDataWrongFormat.addAll( formData );
            formDataWrongInput.addAll( formData );

            formData.add( "zipFile", fileResource );
            formDataWrongInput.add( "zipFile", fileResourcePdfInZipFile );
            formDataWrongFormat.add( "zipFile", fileResourceWrongFormat );
        }

        @Test
        void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
        {
            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formData, requiredHttpHeaders_missingPublicApiKey );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnWrongToken_whenTokenIsMissing()
        {
            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formData, requiredHttpHeaders_missingUserToken );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnWrongFormatStatus_whenPdfPassed()
        {
            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_UNSUPPORTED_FILE );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formDataWrongFormat, requiredHttpHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldReturnWrongFormatStatus_whenPdfInZipPassed()
        {
            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_UNSUPPORTED_FILE );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formDataWrongInput, requiredHttpHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        void shouldUploadPhotosSuccessful()
        {
            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_PHOTO_UPLOADED );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formData, requiredHttpHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }
    }

    @Nested
    @TestInstance( TestInstance.Lifecycle.PER_CLASS )
    class RequiredLoadedGifs
    {
        private final Integer amountOfPublicGifsIteration = 2;
        private final Integer amountOfPrivateGifsIteration = 3;
        private final MultiValueMap< String, Object > formData = new LinkedMultiValueMap<>();

        @BeforeAll
        public void createFormData()
        {
            FileSystemResource fileResource =
                new FileSystemResource( "src/test/java/pl/put/photo360/resources/testGif.zip" );

            formData.add( "isPublic", true );
            formData.add( "description", "testDescription" );
            formData.add( "zipFile", fileResource );

            IntStream.range( 0, amountOfPublicGifsIteration )
                .forEach( iteration -> restTemplate.exchange( uploadPhotosEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( formData, requiredHttpHeaders ), RequestResponseDto.class ) );

            formData.set( "isPublic", false );
            IntStream.range( 0, amountOfPrivateGifsIteration )
                .forEach( iteration -> restTemplate.exchange( uploadPhotosEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( formData, requiredHttpHeadersWithAdminToken ),
                    RequestResponseDto.class ) );
        }

        @Nested
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadPublicGifs
        {

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPublicGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders_missingPublicApiKey ),
                        RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPublicGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders_missingUserToken ),
                        RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldSuccessfulDownloadPublicGifs()
            {
                // GIVEN
                // WHEN
                ResponseEntity< Collection< PhotoDataDto > > response =
                    restTemplate.exchange( downloadPublicGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders ), new ParameterizedTypeReference<>()
                        {} );

                // Then
                assertEquals( amountOfPublicGifsIteration, Objects.requireNonNull( response.getBody() )
                    .size() );
                assertTrue( response.getBody()
                    .stream()
                    .allMatch( PhotoDataDto::isPublic ) );
            }
        }

        @Nested
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadPrivateGifs
        {

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPrivateGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders_missingPublicApiKey ),
                        RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPrivateGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders_missingUserToken ),
                        RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldSuccessfulDownloadPrivateGifs()
            {
                // GIVEN
                // WHEN
                ResponseEntity< Collection< PhotoDataDto > > response =
                    restTemplate.exchange( downloadPrivateGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeadersWithAdminToken ),
                        new ParameterizedTypeReference<>()
                        {} );

                // Then
                assertEquals( amountOfPrivateGifsIteration, Objects.requireNonNull( response.getBody() )
                    .size() );
                assertTrue( response.getBody()
                    .stream()
                    .noneMatch( PhotoDataDto::isPublic ) );
            }
        }

        @Nested
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadAllGifs
        {

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders_missingPublicApiKey ),
                        RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders_missingUserToken ),
                        RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldUnauthorized_whenTokenIsNotAdmin()
            {
                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldSuccessfulDownloadAllGifs_whenAdminLogged()
            {
                // GIVEN
                // WHEN
                ResponseEntity< Collection< PhotoDataDto > > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, requiredHttpHeadersWithAdminToken ),
                        new ParameterizedTypeReference<>()
                        {} );

                // Then
                assertEquals( amountOfPrivateGifsIteration + amountOfPublicGifsIteration,
                    Objects.requireNonNull( response.getBody() )
                        .size() );
            }
        }
    }
}
