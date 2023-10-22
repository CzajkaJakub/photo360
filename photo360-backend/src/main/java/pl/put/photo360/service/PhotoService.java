package pl.put.photo360.service;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_ADD_TO_FAVOURITE_NOT_ALLOWED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_DELETE_NOT_ALLOWED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_ALREADY_ADDED_TO_FAVOURITE;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_IS_NOT_PUBLIC;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_UNSUPPORTED_FILE;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_FILE_FORMAT;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pl.put.photo360.auth.AuthService;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dao.FavouriteGifDataDao;
import pl.put.photo360.dao.PhotoDataDao;
import pl.put.photo360.entity.FavouriteGifDataEntity;
import pl.put.photo360.entity.PhotoDataEntity;
import pl.put.photo360.entity.PhotoEntity;
import pl.put.photo360.shared.converter.GifCreator;
import pl.put.photo360.shared.dto.PhotoDataDto;
import pl.put.photo360.shared.exception.ServiceException;
import pl.put.photo360.shared.utils.JwtValidator;
import pl.put.photo360.shared.utils.PhotoEntityComparator;

@Service
public class PhotoService
{
    private final GifCreator gifCreator;
    private final AuthService authService;
    private final JwtValidator jwtValidator;
    private final PhotoDataDao photoDataDao;
    private final Configuration configuration;
    private final FavouriteGifDataDao favouriteGifDataDao;

    @Autowired
    public PhotoService( PhotoDataDao aPhotoDataDao, AuthService aAuthService, Configuration aConfiguration,
        JwtValidator aJwtValidator, GifCreator aGifCreator, FavouriteGifDataDao aFavouriteGifDataDao )
    {
        photoDataDao = aPhotoDataDao;
        authService = aAuthService;
        configuration = aConfiguration;
        jwtValidator = aJwtValidator;
        gifCreator = aGifCreator;
        favouriteGifDataDao = aFavouriteGifDataDao;
    }

    public void savePhotos( Boolean isPublic, String description, String aAuthorizationToken,
        MultipartFile aFile )
    {
        var user = authService.findUserByAuthorizationToken( aAuthorizationToken );

        checkFileFormat( aFile.getOriginalFilename(), List.of( configuration.getSUPPORTED_FORMAT() ) );
        try (ZipInputStream zipInputStream = new ZipInputStream( aFile.getInputStream() ))
        {
            PhotoDataEntity photoDataEntity = new PhotoDataEntity( user, isPublic, description );
            List< PhotoEntity > photos = new ArrayList<>();
            ZipEntry entry;
            while( (entry = zipInputStream.getNextEntry()) != null )
            {
                if( !entry.isDirectory() )
                {
                    String fileName = entry.getName();
                    checkFileFormat( fileName, configuration.getSUPPORTED_PHOTO_FORMATS() );
                    byte[] data = IOUtils.toByteArray( zipInputStream );
                    PhotoEntity photoEntity = new PhotoEntity( fileName, data );
                    photos.add( photoEntity );
                }
            }
            photos.sort( new PhotoEntityComparator() );
            var gifByte = gifCreator.convertImagesIntoGif( photos );
            photoDataEntity.setConvertedGif( gifByte );

            if( configuration.getSAVING_GIF_PHOTOS() )
            {
                photoDataEntity.setPhotos( IntStream.range( 0, photos.size() )
                    .filter( i -> i % configuration.getGIF_PHOTOS_SAVED_STEP() == 0 )
                    .mapToObj( photos::get )
                    .collect( Collectors.toList() ) );
            }

            photoDataDao.save( photoDataEntity );
        }
        catch( IOException aE )
        {
            throw new ServiceException( STATUS_WRONG_FILE_FORMAT );
        }
    }

    public void checkFileFormat( String fileName, List< String > supportedFormats )
    {
        if( fileName != null )
        {
            String fileExtension = getFileExtension( fileName );

            boolean isSupportedFormat = supportedFormats.stream()
                .anyMatch( fileExtension::equalsIgnoreCase );

            if( !isSupportedFormat )
            {
                throw new ServiceException( STATUS_UNSUPPORTED_FILE );
            }
        }
    }

    private String getFileExtension( String fileName )
    {
        int lastDotIndex = fileName.lastIndexOf( "." );
        if( lastDotIndex != -1 )
        {
            return fileName.substring( lastDotIndex + 1 )
                .toLowerCase();
        }
        return "";
    }

    public PhotoDataDto downloadGifById( String aAuthorizationToken, Long aGifId )
    {
        var userId = jwtValidator.extractLoginFromToken( aAuthorizationToken );
        var gif = findGifById( aGifId );

        if( gif.isPublic() || gif.getUserId()
            .getLogin()
            .equals( userId ) )
        {
            return getExternalFromInternal( gif );
        }
        else
        {
            throw new ServiceException( STATUS_GIF_IS_NOT_PUBLIC );
        }
    }

    public void removeUserGif( String aAuthorizationToken, Long aGifId )
    {
        var userId = jwtValidator.extractLoginFromToken( aAuthorizationToken );
        var gif = findGifById( aGifId );

        if( gif.getUserId()
            .getLogin()
            .equals( userId ) || jwtValidator.isAdminRoleToken( aAuthorizationToken ) )
        {
            photoDataDao.delete( gif );
        }
        else
        {
            throw new ServiceException( STATUS_DELETE_NOT_ALLOWED );
        }
    }

    public void addToFavourite( String aAuthorizationToken, Long aGifId )
    {
        var user = authService.findUserByAuthorizationToken( aAuthorizationToken );
        var gif = findGifById( aGifId );

        if( user.getFavouritesGif()
            .stream()
            .anyMatch( favouriteGifRecord -> favouriteGifRecord.getPhotoDataId()
                .getId()
                .equals( aGifId ) ) )
        {
            throw new ServiceException( STATUS_GIF_ALREADY_ADDED_TO_FAVOURITE );
        }
        if( gif.getUserId()
            .getLogin()
            .equals( user.getLogin() ) || gif.isPublic() )
        {
            var favouriteGifData = new FavouriteGifDataEntity( user, gif );
            favouriteGifDataDao.save( favouriteGifData );
        }
        else
        {
            throw new ServiceException( STATUS_ADD_TO_FAVOURITE_NOT_ALLOWED );
        }
    }

    public void removeFromFavourite( String aAuthorizationToken, Long aGifId )
    {
        var user = authService.findUserByAuthorizationToken( aAuthorizationToken );

        var favouriteGifToRemove = favouriteGifDataDao.findUserGif( user.getId(), aGifId );
        if( favouriteGifToRemove.isPresent() )
        {
            favouriteGifDataDao.delete( favouriteGifToRemove.get() );
        }
        else
        {
            throw new ServiceException( STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
        }
    }

    public List< PhotoDataDto > downloadPrivateGifs( String aAuthorizationToken )
    {
        var userId = jwtValidator.extractLoginFromToken( aAuthorizationToken );
        var gifs = photoDataDao.findPrivateGifs( userId );
        return getExternalFromInternal( gifs );
    }

    public List< PhotoDataDto > getFavourites( String aAuthorizationToken )
    {
        var userId = jwtValidator.extractLoginFromToken( aAuthorizationToken );
        var gifs = favouriteGifDataDao.findUserFavouriteGifs( userId );
        return getExternalFromInternal( gifs );
    }

    public List< PhotoDataDto > downloadPublicGifs()
    {
        var gifs = photoDataDao.findPublicGifs();
        return getExternalFromInternal( gifs );
    }

    public List< PhotoDataDto > downloadAllGifs()
    {
        var gifs = photoDataDao.findAll();
        return getExternalFromInternal( gifs );
    }

    public PhotoDataEntity findGifById( Long aGifId )
    {
        var gif = photoDataDao.findGifById( aGifId );
        if( gif.isPresent() )
        {
            return gif.get();
        }
        else
        {
            throw new ServiceException( STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
        }
    }

    private PhotoDataDto getExternalFromInternal( PhotoDataEntity aPhotoDataEntity )
    {
        return new PhotoDataDto( aPhotoDataEntity );
    }

    private List< PhotoDataDto > getExternalFromInternal( List< PhotoDataEntity > listOfPhotoDataEntities )
    {
        return listOfPhotoDataEntities.stream()
            .map( PhotoDataDto::new )
            .collect( Collectors.toList() );
    }
}
