package pl.put.photo360.controller;

import static pl.put.photo360.dto.ServerResponseCode.*;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import pl.put.photo360.dto.PhotoDataDto;
import pl.put.photo360.dto.RequestResponseDto;
import pl.put.photo360.dto.UserRoles;
import pl.put.photo360.service.PhotoService;
import pl.put.photo360.tokenValidator.annotation.RequiredRole;

@RequestMapping( "/photo360" )
@RestController( "SystemController" )
@Tag( name = "Photo360 main functions controller, each endpoint requires public api key." )
public class SystemController
{
    private final PhotoService photoService;

    @Autowired
    public SystemController( PhotoService photoService )
    {
        this.photoService = photoService;
    }

    @PostMapping( "/uploadPhotos" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint allows to upload user's photos to create a gif, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Photos uploaded successful." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token." ),
        @ApiResponse( responseCode = "406", description = "Unsupported zip/file format." ) } )
    public ResponseEntity< RequestResponseDto > uploadPhoto(
        @RequestParam( value = "zipFile" ) MultipartFile aFile,
        @RequestParam( value = "isPublic" ) Boolean isPublic,
        @RequestParam( value = "description" ) String description,
        @RequestParam( value = "backgroundColor", required = false ) String backgroundColor,
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken )
    {
        photoService.savePhotos( isPublic, description, authorizationToken, aFile, backgroundColor );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_PHOTO_UPLOADED ),
            STATUS_PHOTO_UPLOADED.getStatus() );
    }

    @GetMapping( "/downloadPublicGifs" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to get all public gifs, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Returns public gifs." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ) } )
    public ResponseEntity< Collection< PhotoDataDto > > downloadPublicGif()
    {
        var publicGifs = photoService.downloadPublicGifs();
        return new ResponseEntity<>( publicGifs, HttpStatus.OK );
    }

    @GetMapping( "/downloadPrivateGifs" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to get all private gifs, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Returns private gifs." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token." ) } )
    public ResponseEntity< Collection< PhotoDataDto > > downloadPrivateGif(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken )
    {
        var privateGifs = photoService.downloadPrivateGifs( authorizationToken );
        return new ResponseEntity<>( privateGifs, HttpStatus.OK );
    }

    @GetMapping( "/downloadAllGifs" )
    @RequiredRole( role = UserRoles.ADMIN_ROLE )
    @Operation( summary = "Endpoint to get all gifs in database, allowed only for admin role, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Returns gifs." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ) } )
    public ResponseEntity< Collection< PhotoDataDto > > downloadAllGif()
    {
        var gifs = photoService.downloadAllGifs();
        return new ResponseEntity<>( gifs, HttpStatus.OK );
    }

    @DeleteMapping( "/removeGif/{gifId}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to remove gif, which is owned by currently logged user, also allows to remove any gif, if user has admin role, public api key is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Gif removed." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token." ) } )
    public ResponseEntity< RequestResponseDto > removeUserGif(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken,
        @PathVariable Long gifId )
    {
        photoService.removeUserGif( authorizationToken, gifId );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_GIF_REMOVED ),
            STATUS_GIF_REMOVED.getStatus() );
    }

    @GetMapping( "/downloadGif/{gifId}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to get specific public gif or private owned by logged user by id, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Returns gif." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token/gif with passed id not exists." ),
        @ApiResponse( responseCode = "406", description = "Gif is not public." ) } )
    public ResponseEntity< PhotoDataDto > downloadGif(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken,
        @PathVariable Long gifId )
    {
        var gifData = photoService.downloadGifById( authorizationToken, gifId );
        return new ResponseEntity<>( gifData, HttpStatus.OK );
    }

    @GetMapping( "/downloadGifFile/{gifId}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    // @Operation( summary = "Endpoint to get specific public gif or private owned by logged user by id,
    // public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Returns gif." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token/gif with passed id not exists." ),
        @ApiResponse( responseCode = "406", description = "Gif is not public." ) } )
    public ResponseEntity< byte[] > downloadGifFile(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken,
        @PathVariable Long gifId )
    {
        var gifData = photoService.downloadGifById( authorizationToken, gifId );
        return new ResponseEntity<>( gifData.getGif(), HttpStatus.OK );
    }

    @PutMapping( "/addToFavourite/{gifId}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint used to save public/personal gif to favourite, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Gif added to favourite." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token/gif with passed id not exists." ),
        @ApiResponse( responseCode = "405", description = "Gif already marked as favourite." ),
        @ApiResponse( responseCode = "406", description = "Gif is not public." ) } )
    public ResponseEntity< RequestResponseDto > addToFavourite(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken,
        @PathVariable Long gifId )
    {
        photoService.addToFavourite( authorizationToken, gifId );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_GIF_ADDED_TO_FAVOURITE ),
            STATUS_GIF_ADDED_TO_FAVOURITE.getStatus() );
    }

    @DeleteMapping( "/removeFromFavourite/{gifId}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint used to remove public/personal gif from favourite, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Removed gif from favourite." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token, gif was not marked as favourite." ) } )
    public ResponseEntity< RequestResponseDto > removeFromFavourite(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken,
        @PathVariable Long gifId )
    {
        photoService.removeFromFavourite( authorizationToken, gifId );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_GIF_REMOVED_FROM_FAVOURITE ),
            STATUS_GIF_REMOVED_FROM_FAVOURITE.getStatus() );
    }

    @GetMapping( "/getFavourites" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint which returns all user's favourites gifs, public api key and currently logged user's jwt token is required." )
    @ApiResponses( value =
    { @ApiResponse( responseCode = "200", description = "Removed gif from favourite." ),
        @ApiResponse( responseCode = "401", description = "Passed jwt token not valid/expired/unauthorized role." ),
        @ApiResponse( responseCode = "404", description = "User was not found by passed token" ) } )
    public ResponseEntity< Collection< PhotoDataDto > > getFavourites(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION, required = false ) String authorizationToken )
    {
        var gifs = photoService.getFavourites( authorizationToken );
        return new ResponseEntity<>( gifs, HttpStatus.OK );
    }
}
