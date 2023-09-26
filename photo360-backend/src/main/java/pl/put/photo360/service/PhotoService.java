package pl.put.photo360.service;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_ADD_TO_FAVOURITE_NOT_ALLOWED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_DELETE_NOT_ALLOWED;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_ALREADY_ADDED_TO_FAVOURITE;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_IS_NOT_PUBLIC;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_UNSUPPORTED_FILE;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_FROM_TOKEN;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_FILE_FORMAT;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
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
import pl.put.photo360.shared.exception.UserNotFoundException;
import pl.put.photo360.shared.utils.JwtValidator;

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
        var user = authService.findByToken( aAuthorizationToken );
        if( user.isPresent() )
        {
            checkFileFormat( aFile.getOriginalFilename(), List.of( configuration.getSUPPORTED_FORMAT() ) );
            try (ZipInputStream zipInputStream = new ZipInputStream( aFile.getInputStream() ))
            {
                PhotoDataEntity photoDataEntity = new PhotoDataEntity( user.get(), isPublic, description );
                ZipEntry entry;
                while( (entry = zipInputStream.getNextEntry()) != null )
                {
                    if( !entry.isDirectory() )
                    {
                        String fileName = entry.getName();
                        checkFileFormat( fileName, configuration.getSUPPORTED_PHOTO_FORMATS() );
                        byte[] data = IOUtils.toByteArray( zipInputStream );
                        PhotoEntity photoEntity = new PhotoEntity( fileName, data );
                        photoDataEntity.getPhotos()
                            .add( photoEntity );
                    }
                }
                photoDataEntity.sortPhotosByIndex();
                var gifByte = gifCreator.convertImagesIntoGif( photoDataEntity.getPhotos() );
                photoDataEntity.setConvertedGif( gifByte );
                photoDataDao.save( photoDataEntity );
            }
            catch( IOException aE )
            {
                throw new ServiceException( STATUS_WRONG_FILE_FORMAT );
            }
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_FROM_TOKEN );
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
        var gif = photoDataDao.findGifById( aGifId );
        if( gif != null )
        {
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
        else
        {
            throw new ServiceException( STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
        }
    }

    public void removeUserGif( String aAuthorizationToken, Long aGifId )
    {
        var userId = jwtValidator.extractLoginFromToken( aAuthorizationToken );
        var gif = photoDataDao.findGifById( aGifId );
        if( gif != null )
        {
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
        else
        {
            throw new ServiceException( STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
        }
    }

    public void addToFavourite( String aAuthorizationToken, Long aGifId )
    {
        var user = authService.findByToken( aAuthorizationToken );
        if( user.isPresent() )
        {
            var gif = photoDataDao.findGifById( aGifId );
            if( gif != null )
            {
                if( user.get()
                    .getFavouritesGif()
                    .stream()
                    .anyMatch( favouriteGifRecord -> favouriteGifRecord.getPhotoDataId()
                        .getId()
                        .equals( aGifId ) ) )
                {
                    throw new ServiceException( STATUS_GIF_ALREADY_ADDED_TO_FAVOURITE );
                }
                if( gif.getUserId()
                    .getLogin()
                    .equals( user.get()
                        .getLogin() )
                    || gif.isPublic() )
                {
                    var favouriteGifData = new FavouriteGifDataEntity( user.get(), gif );
                    favouriteGifDataDao.save( favouriteGifData );
                }
                else
                {
                    throw new ServiceException( STATUS_ADD_TO_FAVOURITE_NOT_ALLOWED );
                }
            }
            else
            {
                throw new ServiceException( STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
            }
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_FROM_TOKEN );
        }
    }

    public void removeFromFavourite( String aAuthorizationToken, Long aGifId )
    {
        var user = authService.findByToken( aAuthorizationToken );
        if( user.isPresent() )
        {
            var favouriteGifToRemove = favouriteGifDataDao.findUserGif( user.get()
                .getId(), aGifId );
            if( favouriteGifToRemove.isPresent() )
            {
                favouriteGifDataDao.delete( favouriteGifToRemove.get() );
            }
            else
            {
                throw new ServiceException( STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
            }
        }
        else
        {
            throw new UserNotFoundException( STATUS_USER_NOT_FOUND_FROM_TOKEN );
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
