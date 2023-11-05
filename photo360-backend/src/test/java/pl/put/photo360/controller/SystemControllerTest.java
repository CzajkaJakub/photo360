package pl.put.photo360.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.*;
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

import pl.put.photo360.auth.AuthService;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dto.*;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
@TestClassOrder( ClassOrderer.OrderAnnotation.class )
@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class SystemControllerTest
{
    private final String testLoginUserWithoutRole = "LOGIN_EMPTY_ROLE_ACCOUNT";
    private final String testEmailUserWithoutRole = "EMPTY_ROLE_ACCOUNT@gmail.com";
    private final String testPasswordUserWithoutRole = "PASSWORD_EMPTY_ROLE_ACCOUNT";
    private final String testLoginUser = "LOGIN_USER_ROLE_ACCOUNT";
    private final String testEmailUser = "EMPTY_USER_ACCOUNT@gmail.com";
    private final String testPasswordUser = "PASSWORD_USER_ROLE_ACCOUNT";
    private final String testLoginAdmin = "LOGIN_ADMIN_ACCOUNT";
    private final String testEmailAdmin = "ADMIN_ACCOUNT@gmail.com";
    private final String testPasswordAdmin = "PASSWORD_ADMIN_ACCOUNT";
    @Autowired
    private AuthService authService;
    @Value( value = "${local.server.port}" )
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private Configuration configuration;
    private HttpHeaders httpHeaders;
    private String removeGifEndpointPath;
    private String downloadGifEndpointPath;
    private String addGifToFavouriteEndpointPath;
    private String removeGifFromFavouriteEndpointPath;
    private String uploadPhotosEndpointPath;
    private String downloadAllGifsEndpointPath;
    private String downloadPublicGifsEndpointPath;
    private String downloadPrivateGifsEndpointPath;
    private String downloadFavouriteGifsEndpointPath;
    private String testTokenUser;
    private String testTokenAdmin;
    private String testTokenUserWithoutRole;

    @BeforeAll
    public void registerAndLogTestUser_setUp()
    {
        httpHeaders = new HttpHeaders();
        httpHeaders.set( "publicApiKey", configuration.getPUBLIC_API_KEY() );

        String loginEndpointPath = "http://localhost:" + port + "/photo360/authorization/login";
        uploadPhotosEndpointPath = "http://localhost:" + port + "/photo360/uploadPhotos";
        downloadPublicGifsEndpointPath = "http://localhost:" + port + "/photo360/downloadPublicGifs";
        downloadPrivateGifsEndpointPath = "http://localhost:" + port + "/photo360/downloadPrivateGifs";
        downloadAllGifsEndpointPath = "http://localhost:" + port + "/photo360/downloadAllGifs";
        removeGifEndpointPath = "http://localhost:" + port + "/photo360/removeGif/";
        downloadGifEndpointPath = "http://localhost:" + port + "/photo360/downloadGif/";
        addGifToFavouriteEndpointPath = "http://localhost:" + port + "/photo360/addToFavourite/";
        removeGifFromFavouriteEndpointPath = "http://localhost:" + port + "/photo360/removeFromFavourite/";
        downloadFavouriteGifsEndpointPath = "http://localhost:" + port + "/photo360/getFavourites";

        var loginUserRequestDto = new LoginRequestDto( testLoginUser, testPasswordUser );
        var loginAdminLoginRequestDto = new LoginRequestDto( testLoginAdmin, testPasswordAdmin );
        var loginUserWithoutRoleRequestDto =
            new LoginRequestDto( testLoginUserWithoutRole, testPasswordUserWithoutRole );

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

        try
        {
            authService.saveNewUser( new RegisterRequestDto( testLoginUser, testEmailUser, testPasswordUser ),
                List.of( UserRoles.USER_ROLE ) );
        }
        catch( Exception ignore )
        {
        }

        testTokenUser = Objects
            .requireNonNull( restTemplate
                .exchange( loginEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( loginUserRequestDto, httpHeaders ), LoginResponseDto.class )
                .getBody() )
            .get_token();

        testTokenAdmin = Objects
            .requireNonNull( restTemplate
                .exchange( loginEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( loginAdminLoginRequestDto, httpHeaders ), LoginResponseDto.class )
                .getBody() )
            .get_token();

        testTokenUserWithoutRole = Objects
            .requireNonNull( restTemplate
                .exchange( loginEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( loginUserWithoutRoleRequestDto, httpHeaders ), LoginResponseDto.class )
                .getBody() )
            .get_token();
    }

    @Nested
    @Order( 1 )
    @TestInstance( TestInstance.Lifecycle.PER_CLASS )
    class RequiredLoadedGifs
    {
        private final Integer amountOfPublicGifsIteration = 4;
        private final Integer amountOfPrivateGifsIteration = 5;
        private final MultiValueMap< String, Object > formData = new LinkedMultiValueMap<>();

        @BeforeAll
        public void createFormData()
        {
            var adminHeaders = new HttpHeaders();
            adminHeaders.putAll( httpHeaders );
            adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

            var userHeaders = new HttpHeaders();
            userHeaders.putAll( httpHeaders );
            userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

            FileSystemResource fileResource =
                new FileSystemResource( "src/test/java/pl/put/photo360/resources/testGif.zip" );

            formData.add( "isPublic", true );
            formData.add( "description", "testDescription" );
            formData.add( "zipFile", fileResource );

            IntStream.range( 0, amountOfPublicGifsIteration )
                .forEach( iteration -> restTemplate.exchange( uploadPhotosEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( formData, userHeaders ), RequestResponseDto.class ) );

            formData.set( "isPublic", false );
            IntStream.range( 0, amountOfPrivateGifsIteration )
                .forEach( iteration -> restTemplate.exchange( uploadPhotosEndpointPath, HttpMethod.POST,
                    new HttpEntity<>( formData, adminHeaders ), RequestResponseDto.class ) );
        }

        @Nested
        @Order( 1 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadPublicGifsTest
        {

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPublicGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );

                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPublicGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldSuccessfulDownloadPublicGifs()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

                // WHEN
                ResponseEntity< Collection< PhotoDataDto > > response =
                    restTemplate.exchange( downloadPublicGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), new ParameterizedTypeReference<>()
                        {} );

                // Then
                assertEquals( amountOfPublicGifsIteration, Objects.requireNonNull( response.getBody() )
                    .size() );
                assertTrue( response.getBody()
                    .stream()
                    .allMatch( PhotoDataDto::isPublic ) );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );

                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );
                HttpEntity< MultiValueMap< String, Object > > requestEntity =
                    new HttpEntity<>( formData, userHeaders );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    downloadPublicGifsEndpointPath, HttpMethod.GET, requestEntity, RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }

        @Nested
        @Order( 2 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadPrivateGifsTest
        {

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPrivateGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPrivateGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldSuccessfulDownloadPrivateGifs_postedByAdminAccount()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

                // WHEN
                ResponseEntity< Collection< PhotoDataDto > > response =
                    restTemplate.exchange( downloadPrivateGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), new ParameterizedTypeReference<>()
                        {} );

                // Then
                assertEquals( amountOfPrivateGifsIteration, Objects.requireNonNull( response.getBody() )
                    .size() );
                assertTrue( response.getBody()
                    .stream()
                    .noneMatch( PhotoDataDto::isPublic ) );
                assertTrue( response.getBody()
                    .stream()
                    .allMatch( pho -> pho.getUserLogin()
                        .equals( testLoginAdmin ) ) );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );

                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );
                HttpEntity< MultiValueMap< String, Object > > requestEntity =
                    new HttpEntity<>( formData, userHeaders );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadPrivateGifsEndpointPath, HttpMethod.GET, requestEntity,
                        RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }

        @Nested
        @Order( 3 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadAllGifsTest
        {
            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldUnauthorized_whenTokenIsNotAdmin()
            {
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

                // GIVEN
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldSuccessfulDownloadAllGifs_whenAdminLogged()
            {
                // GIVEN
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

                // WHEN
                ResponseEntity< Collection< PhotoDataDto > > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, adminHeaders ), new ParameterizedTypeReference<>()
                        {} );

                // Then
                assertEquals( amountOfPrivateGifsIteration + amountOfPublicGifsIteration,
                    Objects.requireNonNull( response.getBody() )
                        .size() );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }

        @Nested
        @Order( 4 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadGifByIdTest
        {
            private List< PhotoDataDto > gifs = new ArrayList<>();

            @BeforeEach
            public void extractGifsFromDb()
            {
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

                gifs = restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                    new HttpEntity<>( null, adminHeaders ),
                    new ParameterizedTypeReference< List< PhotoDataDto > >()
                    {} )
                    .getBody();
            }

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );
                PhotoDataDto gifToDownload = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    downloadGifEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.GET, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                PhotoDataDto gifToDownload = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    downloadGifEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.GET, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldUnauthorized_whenGifIsNotOwnedByUser()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> !testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_IS_NOT_PUBLIC );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    downloadGifEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.GET, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldDownload_whenGifIsOwnedByUser()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< PhotoDataDto > response = restTemplate.exchange(
                    downloadGifEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.GET, new HttpEntity<>( null, userHeaders ), PhotoDataDto.class );

                // Then
                assertEquals( gifToDownload.getGifId(), Objects.requireNonNull( response.getBody() )
                    .getGifId() );
                assertEquals( gifToDownload.getUserLogin(), response.getBody()
                    .getUserLogin() );
                assertEquals( gifToDownload.getDescription(), response.getBody()
                    .getDescription() );
                assertEquals( gifToDownload.getUploadDateTime(), response.getBody()
                    .getUploadDateTime() );
            }

            @Test
            void shouldDownload_whenGifIsOwnedToAdmin()
            {
                // GIVEN
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> !testLoginAdmin.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< PhotoDataDto > response = restTemplate.exchange(
                    downloadGifEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.GET, new HttpEntity<>( null, adminHeaders ), PhotoDataDto.class );

                // Then
                assertEquals( gifToDownload.getGifId(), response.getBody()
                    .getGifId() );
                assertEquals( gifToDownload.getUserLogin(), response.getBody()
                    .getUserLogin() );
                assertEquals( gifToDownload.getDescription(), response.getBody()
                    .getDescription() );
                assertEquals( gifToDownload.getUploadDateTime(), response.getBody()
                    .getUploadDateTime() );
            }

            @Test
            void shouldDownload_whenGifIsNotOwnedToAdmin()
            {
                // GIVEN
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> !testTokenAdmin.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< PhotoDataDto > response = restTemplate.exchange(
                    downloadGifEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.GET, new HttpEntity<>( null, adminHeaders ), PhotoDataDto.class );

                // Then
                assertEquals( gifToDownload.getGifId(), Objects.requireNonNull( response.getBody() )
                    .getGifId() );
                assertEquals( gifToDownload.getUserLogin(), response.getBody()
                    .getUserLogin() );
                assertEquals( gifToDownload.getDescription(), response.getBody()
                    .getDescription() );
                assertEquals( gifToDownload.getUploadDateTime(), response.getBody()
                    .getUploadDateTime() );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    downloadGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.GET, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }

        @Nested
        @Order( 5 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class AddToFavouriteTests
        {
            private List< PhotoDataDto > gifs = new ArrayList<>();

            @BeforeEach
            public void extractGifsFromDb()
            {
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

                gifs = restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                    new HttpEntity<>( null, adminHeaders ),
                    new ParameterizedTypeReference< List< PhotoDataDto > >()
                    {} )
                    .getBody();
            }

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );
                PhotoDataDto gifToDownload = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                PhotoDataDto gifToDownload = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongIdException_whenGifNotFoundById()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( addGifToFavouriteEndpointPath.concat( "9999" ), HttpMethod.PUT,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldSuccessfulAddPrivatePersonalToFavourite_and_shouldThrowException_whenTryAddSameGifSecondTime()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCodeFirst =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_ADDED_TO_FAVOURITE );
                var expectedResultCodeSecond =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_ALREADY_ADDED_TO_FAVOURITE );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< RequestResponseDto > firstResponse = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                ResponseEntity< RequestResponseDto > secondResponse = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );
                restTemplate.exchange( removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                    .toString() ), HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ),
                    RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCodeFirst, firstResponse.getBody() );
                assertEquals( expectedResultCodeSecond, secondResponse.getBody() );
            }

            @Test
            void shouldThrowException_whenTryAddNotPublicAndNotPersonalGif()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> !testLoginUser.equals( gif.getUserLogin() ) && !gif.isPublic() )
                    .findFirst()
                    .orElse( null );
                var expectedResultCodeFirst =
                    new RequestResponseDto( ServerResponseCode.STATUS_ADD_TO_FAVOURITE_NOT_ALLOWED );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< RequestResponseDto > firstResponse = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCodeFirst, firstResponse.getBody() );
            }

            @Test
            void shouldSuccessfulAddPublicToFavourite()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> !testLoginAdmin.equals( gif.getUserLogin() ) && gif.isPublic() )
                    .findFirst()
                    .orElse( null );
                var expectedResultCodeFirst =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_ADDED_TO_FAVOURITE );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< RequestResponseDto > firstResponse = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );
                restTemplate.exchange( removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                    .toString() ), HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ),
                    RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCodeFirst, firstResponse.getBody() );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }

        @Nested
        @Order( 6 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class RemoveFromFavouriteTests
        {
            private List< PhotoDataDto > gifs = new ArrayList<>();

            @BeforeEach
            public void extractGifsFromDb()
            {
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

                gifs = restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                    new HttpEntity<>( null, adminHeaders ),
                    new ParameterizedTypeReference< List< PhotoDataDto > >()
                    {} )
                    .getBody();
            }

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );
                PhotoDataDto gifToDownload = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                PhotoDataDto gifToDownload = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnException_whenGifNotFoundFromUserRepository()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( removeGifFromFavouriteEndpointPath.concat( "9999" ),
                        HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnException_whenGifNotAddedToFavourite()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> testLoginAdmin.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldRemoveFromFavouriteSuccessful_and_shouldThrowExceptionWhenTryRemoveSecondTime()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
                var expectedResultRemoved =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_REMOVED_FROM_FAVOURITE );

                // WHEN
                assert gifToDownload != null;
                restTemplate.exchange( addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                    .toString() ), HttpMethod.PUT, new HttpEntity<>( null, userHeaders ),
                    RequestResponseDto.class );
                ResponseEntity< RequestResponseDto > responseRemoveFirst = restTemplate.exchange(
                    removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );
                ResponseEntity< RequestResponseDto > responseSecond = restTemplate.exchange(
                    removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultRemoved, responseRemoveFirst.getBody() );
                assertEquals( expectedResultCode, responseSecond.getBody() );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifFromFavouriteEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }

        @Nested
        @Order( 7 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class DownloadFavouriteTests
        {
            private List< PhotoDataDto > gifs = new ArrayList<>();

            @BeforeEach
            public void extractGifsFromDb()
            {
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

                gifs = restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                    new HttpEntity<>( null, adminHeaders ),
                    new ParameterizedTypeReference< List< PhotoDataDto > >()
                    {} )
                    .getBody();
            }

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadFavouriteGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadFavouriteGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnFavouriteGifsSuccessful()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToDownload = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );

                var expectedResultCodeAdd =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_ADDED_TO_FAVOURITE );

                // WHEN
                assert gifToDownload != null;
                ResponseEntity< RequestResponseDto > responseAdd = restTemplate.exchange(
                    addGifToFavouriteEndpointPath.concat( gifToDownload.getGifId()
                        .toString() ),
                    HttpMethod.PUT, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                ResponseEntity< List< PhotoDataDto > > responseFavouriteGifs =
                    restTemplate.exchange( downloadFavouriteGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), new ParameterizedTypeReference<>()
                        {} );
                restTemplate.exchange( removeGifFromFavouriteEndpointPath.concat( gifToDownload.getGifId()
                    .toString() ), HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ),
                    RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCodeAdd, responseAdd.getBody() );
                assertEquals( gifToDownload.getGifId(),
                    Objects.requireNonNull( responseFavouriteGifs.getBody() )
                        .get( 0 )
                        .getGifId() );
                assertEquals( gifToDownload.getUserLogin(),
                    Objects.requireNonNull( responseFavouriteGifs.getBody() )
                        .get( 0 )
                        .getUserLogin() );
                assertEquals( gifToDownload.getDescription(),
                    Objects.requireNonNull( responseFavouriteGifs.getBody() )
                        .get( 0 )
                        .getDescription() );
                assertEquals( gifToDownload.getUploadDateTime(),
                    Objects.requireNonNull( responseFavouriteGifs.getBody() )
                        .get( 0 )
                        .getUploadDateTime() );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                // WHEN
                ResponseEntity< RequestResponseDto > response =
                    restTemplate.exchange( downloadFavouriteGifsEndpointPath, HttpMethod.GET,
                        new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }

        @Nested
        @Order( 8 )
        @TestInstance( TestInstance.Lifecycle.PER_CLASS )
        class RemoveUserGifTest
        {
            private List< PhotoDataDto > gifs = new ArrayList<>();

            @BeforeEach
            public void extractGifsFromDb()
            {
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );

                gifs = restTemplate.exchange( downloadAllGifsEndpointPath, HttpMethod.GET,
                    new HttpEntity<>( null, adminHeaders ),
                    new ParameterizedTypeReference< List< PhotoDataDto > >()
                    {} )
                    .getBody();
            }

            @Test
            void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( "publicApiKey", null );
                PhotoDataDto gifToRemove = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnWrongToken_whenTokenIsMissing()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                PhotoDataDto gifToRemove = gifs.get( 0 );

                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );

                // WHEN
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldUnauthorized_whenGifIsNotOwnedByUser()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> !testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_DELETE_NOT_ALLOWED );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldRemove_whenGifIsOwnedByUser()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_GIF_REMOVED );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnStatus_whenGifIsOwnedByUser_tryRemoveSecondTime()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );

                // WHEN
                assert gifToRemove != null;
                restTemplate.exchange( removeGifEndpointPath.concat( gifToRemove.getGifId()
                    .toString() ), HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ),
                    RequestResponseDto.class );
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldRemove_whenGifIsOwnedToAdmin()
            {
                // GIVEN
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> !testLoginAdmin.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_GIF_REMOVED );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, adminHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldRemove_whenGifIsNotOwnedToAdmin()
            {
                // GIVEN
                var adminHeaders = new HttpHeaders();
                adminHeaders.putAll( httpHeaders );
                adminHeaders.set( HttpHeaders.AUTHORIZATION, testTokenAdmin );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> testLoginUser.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_GIF_REMOVED );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, adminHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }

            @Test
            void shouldReturnUnauthorizedRole_whenEmptyRole()
            {
                // GIVEN
                var userHeaders = new HttpHeaders();
                userHeaders.putAll( httpHeaders );
                userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );
                PhotoDataDto gifToRemove = gifs.stream()
                    .filter( gif -> !testTokenAdmin.equals( gif.getUserLogin() ) )
                    .findFirst()
                    .orElse( null );
                var expectedResultCode =
                    new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );

                // WHEN
                assert gifToRemove != null;
                ResponseEntity< RequestResponseDto > response = restTemplate.exchange(
                    removeGifEndpointPath.concat( gifToRemove.getGifId()
                        .toString() ),
                    HttpMethod.DELETE, new HttpEntity<>( null, userHeaders ), RequestResponseDto.class );

                // Then
                assertEquals( expectedResultCode, response.getBody() );
            }
        }
    }

    @Nested
    @Order( 2 )
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
        @Order( 2 )
        void shouldReturnWrongPublicApiKey_whenPublicApiKeyIsMissing()
        {
            // GIVEN
            var httpHeadersMissingPublicApiKey = new HttpHeaders();
            httpHeadersMissingPublicApiKey.putAll( httpHeaders );
            httpHeadersMissingPublicApiKey.set( "publicApiKey", null );
            httpHeadersMissingPublicApiKey.set( HttpHeaders.AUTHORIZATION, testTokenUser );

            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_WRONG_PUBLIC_API_KEY );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formData, httpHeadersMissingPublicApiKey );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        @Order( 3 )
        void shouldReturnWrongToken_whenTokenIsMissing()
        {
            var userHeaders = new HttpHeaders();
            userHeaders.putAll( httpHeaders );

            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_AUTH_TOKEN_NOT_VALID );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formData, userHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        @Order( 4 )
        void shouldReturnWrongFormatStatus_whenPdfPassed()
        {
            var userHeaders = new HttpHeaders();
            userHeaders.putAll( httpHeaders );
            userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_UNSUPPORTED_FILE );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formDataWrongFormat, userHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        @Order( 5 )
        void shouldReturnWrongFormatStatus_whenPdfInZipPassed()
        {
            var userHeaders = new HttpHeaders();
            userHeaders.putAll( httpHeaders );
            userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_UNSUPPORTED_FILE );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formDataWrongInput, userHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        @Order( 6 )
        void shouldReturnUnauthorizedRole_whenEmptyRole()
        {
            var userHeaders = new HttpHeaders();
            userHeaders.putAll( httpHeaders );
            userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUserWithoutRole );

            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_UNAUTHORIZED_ROLE );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formData, userHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }

        @Test
        @Order( 7 )
        void shouldUploadPhotosSuccessful()
        {
            var userHeaders = new HttpHeaders();
            userHeaders.putAll( httpHeaders );
            userHeaders.set( HttpHeaders.AUTHORIZATION, testTokenUser );

            // GIVEN
            var expectedResultCode = new RequestResponseDto( ServerResponseCode.STATUS_PHOTO_UPLOADED );
            HttpEntity< MultiValueMap< String, Object > > requestEntity =
                new HttpEntity<>( formData, userHeaders );

            // WHEN
            ResponseEntity< RequestResponseDto > response = restTemplate.exchange( uploadPhotosEndpointPath,
                HttpMethod.POST, requestEntity, RequestResponseDto.class );

            // Then
            assertEquals( expectedResultCode, response.getBody() );
        }
    }
}
