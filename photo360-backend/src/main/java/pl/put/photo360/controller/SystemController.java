package pl.put.photo360.controller;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_PHOTO_UPLOADED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import pl.put.photo360.service.PhotoService;
import pl.put.photo360.shared.dto.RequestResponseDto;

@RequestMapping( "/photo360" )
@RestController( "SystemController" )
public class SystemController
{
    private final PhotoService photoService;

    @Autowired
    public SystemController( PhotoService photoService )
    {
        this.photoService = photoService;
    }

    @PostMapping( "/uploadPhotos" )
    @ApiOperation( "Endpoint to upload user's photos." )
    public ResponseEntity< RequestResponseDto > uploadPhoto( @RequestParam( "zipFile" ) MultipartFile aFile,
        @RequestParam( "isPublic" ) Boolean isPublic, @RequestParam( "description" ) String description,
        @RequestHeader( name = HttpHeaders.AUTHORIZATION ) String authorizationToken )
    {
        photoService.savePhotos( isPublic, description, authorizationToken, aFile );
        return new ResponseEntity<>( new RequestResponseDto( STATUS_PHOTO_UPLOADED ),
            STATUS_PHOTO_UPLOADED.getStatus() );
    }

    @GetMapping( "/downloadGif/{gifId}" )
    public ResponseEntity< byte[] > downloadGif( @PathVariable Long gifId )
    {
        byte[] gifData = photoService.downloadGifById( gifId );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.IMAGE_GIF );
        headers.setContentLength( gifData.length );
        headers.setContentDispositionFormData( "attachment", "download.gif" );
        return new ResponseEntity<>( gifData, headers, HttpStatus.OK );
    }
}
