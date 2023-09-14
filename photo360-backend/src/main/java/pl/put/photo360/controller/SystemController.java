package pl.put.photo360.controller;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_REMOVED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_PHOTO_UPLOADED;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import pl.put.photo360.service.PhotoService;
import pl.put.photo360.shared.dto.PhotoDataDto;
import pl.put.photo360.shared.dto.RequestResponseDto;
import pl.put.photo360.shared.dto.UserRoles;
import pl.put.photo360.tokenValidator.annotation.RequiredRole;

@RequestMapping( "/photo360" )
@RestController( "SystemController" )
@Tag( name = "Photo360 main functions controller" )
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
    @Operation( summary = "Endpoint to upload user's photos." )
    public ResponseEntity< RequestResponseDto > uploadPhoto(
        @RequestParam( value = "zipFile" ) MultipartFile aFile,
        @RequestParam( value = "isPublic" ) Boolean isPublic,
        @RequestParam( value = "description" ) String description,
        @RequestHeader( name = HttpHeaders.AUTHORIZATION ) String authorizationToken )
    {
        photoService.savePhotos( isPublic, description, authorizationToken, aFile );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_PHOTO_UPLOADED ),
            STATUS_PHOTO_UPLOADED.getStatus() );
    }

    @GetMapping( "/downloadGif/{gifId}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to get specific public gif or private owned by logged user by id." )
    public ResponseEntity< PhotoDataDto > downloadGif(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION ) String authorizationToken,
        @PathVariable Long gifId )
    {
        var gifData = photoService.downloadGifById( authorizationToken, gifId );
        return new ResponseEntity<>( gifData, HttpStatus.OK );
    }

    @GetMapping( "/downloadPublicGifs" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to get all public gifs." )
    public ResponseEntity< Collection< PhotoDataDto > > downloadPublicGif()
    {
        var publicGifs = photoService.downloadPublicGifs();
        return new ResponseEntity<>( publicGifs, HttpStatus.OK );
    }

    @GetMapping( "/downloadPrivateGifs" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to get all private gifs, which are owned by logged user." )
    public ResponseEntity< Collection< PhotoDataDto > > downloadPrivateGif(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION ) String authorizationToken )
    {
        var privateGifs = photoService.downloadPrivateGifs( authorizationToken );
        return new ResponseEntity<>( privateGifs, HttpStatus.OK );
    }

    @GetMapping( "/downloadAllGifs" )
    @RequiredRole( role = UserRoles.ADMIN_ROLE )
    @Operation( summary = "Endpoint to get all gifs in database, allowed only for admin user." )
    public ResponseEntity< Collection< PhotoDataDto > > downloadAllGif()
    {
        var gifs = photoService.downloadAllGifs();
        return new ResponseEntity<>( gifs, HttpStatus.OK );
    }

    @DeleteMapping( "/removeUserGif/{gifId}" )
    @RequiredRole( role = UserRoles.USER_ROLE )
    @Operation( summary = "Endpoint to remove gif, which is owned by logged user, also allows to remove any gif, if user has admin role" )
    public ResponseEntity< RequestResponseDto > removeUserGif(
        @RequestHeader( name = HttpHeaders.AUTHORIZATION ) String authorizationToken,
        @PathVariable Long gifId )
    {
        photoService.removeUserGif( authorizationToken, gifId );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_GIF_REMOVED ),
            STATUS_GIF_REMOVED.getStatus() );
    }
}
