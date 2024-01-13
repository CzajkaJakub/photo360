package pl.put.photo360.service;

import static pl.put.photo360.converter.GifCreator.changeBackgroundColor;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_ADD_TO_FAVOURITE_NOT_ALLOWED;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_BOTH_ZIPS_EMPTY;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_DELETE_NOT_ALLOWED;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_GIF_ALREADY_ADDED_TO_FAVOURITE;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_GIF_IS_NOT_PUBLIC;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_UNSUPPORTED_FILE;
import static pl.put.photo360.dto.ServerResponseCode.STATUS_WRONG_FILE_FORMAT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Tuple;
import pl.put.photo360.auth.AuthService;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.converter.GifCreator;
import pl.put.photo360.dao.FavouriteGifDataDao;
import pl.put.photo360.dao.PhotoDataDao;
import pl.put.photo360.dto.PhotoDataDto;
import pl.put.photo360.entity.FavouriteGifDataEntity;
import pl.put.photo360.entity.PhotoDataEntity;
import pl.put.photo360.entity.PhotoEntity;
import pl.put.photo360.exception.ServiceException;
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

    public void savePhotos( MultipartFile aPhotosZipFile360, MultipartFile aPhotosZipFile, Boolean aIsPublic,
        String aTitle, String aDescription, String aAuthorizationToken, String aBackgroundColor )
    {
        var user = authService.findUserByAuthorizationToken( aAuthorizationToken );
        PhotoDataEntity photoDataEntity = new PhotoDataEntity( user, aIsPublic, aDescription, aTitle );

        if( aPhotosZipFile360 == null && aPhotosZipFile == null )
        {
            throw new ServiceException( STATUS_BOTH_ZIPS_EMPTY );
        }

        if( aPhotosZipFile360 != null && configuration.getSAVING_GIF_360() )
        {
            var extractedPhotos360 = extractPhotosFromZipAndSort( aPhotosZipFile360 );
            changeBackgroundColor( extractedPhotos360, aBackgroundColor );
            var gifByte = gifCreator.convertImagesIntoGif( extractedPhotos360 );
            photoDataEntity.setConvertedGif( gifByte );
            photoDataEntity.setFirstPhoto( extractedPhotos360.get( 0 )
                .getPhoto() );
        }

        if( aPhotosZipFile != null && configuration.getSAVING_GIF_PHOTOS() )
        {
            var extractedPhotos = extractPhotosFromZipAndSort( aPhotosZipFile );
            changeBackgroundColor( extractedPhotos, aBackgroundColor );
            photoDataEntity.setPhotos( extractedPhotos );
            photoDataEntity.setFirstPhoto( extractedPhotos.get( 0 )
                .getPhoto() );
        }

        photoDataDao.save( photoDataEntity );
    }

    private List< PhotoEntity > extractPhotosFromZipAndSort( MultipartFile aPhotosZipFile )
    {
        checkFileFormat( aPhotosZipFile.getOriginalFilename(),
            List.of( configuration.getSUPPORTED_FORMAT() ) );
        List< PhotoEntity > photos = new ArrayList<>();
        try (ZipInputStream zipInputStream = new ZipInputStream( aPhotosZipFile.getInputStream() ))
        {
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
            return photos;
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
            .equals( userId ) || jwtValidator.isAdminRoleToken( aAuthorizationToken ) )
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

    public List< PhotoDataDto > downloadPrivateGifs( String aAuthorizationToken, boolean aPreviewMode )
    {
        var userId = jwtValidator.extractLoginFromToken( aAuthorizationToken );
        var gifIds = photoDataDao.findPrivateGifIds( userId );
        return aPreviewMode
            ? getExternalFromInternalPreviewVersion( photoDataDao.findGifsByIdInPreviewMode( gifIds ) )
            : getExternalFromInternal( photoDataDao.findGifsById( gifIds ) );

    }

    public List< PhotoDataDto > getFavourites( String aAuthorizationToken, boolean aPreviewMode )
    {
        var userId = jwtValidator.extractLoginFromToken( aAuthorizationToken );
        var gifIds = favouriteGifDataDao.findUserFavouriteGifsIds( userId );
        return aPreviewMode
            ? getExternalFromInternalPreviewVersion( photoDataDao.findGifsByIdInPreviewMode( gifIds ) )
            : getExternalFromInternal( photoDataDao.findGifsById( gifIds ) );

    }

    public List< PhotoDataDto > downloadPublicGifs( boolean aPreviewMode )
    {
        var gifIds = photoDataDao.findPublicGifIds();
        return aPreviewMode
            ? getExternalFromInternalPreviewVersion( photoDataDao.findGifsByIdInPreviewMode( gifIds ) )
            : getExternalFromInternal( photoDataDao.findGifsById( gifIds ) );

    }

    public List< PhotoDataDto > downloadAllGifs( boolean aPreviewMode )
    {
        var gifIds = photoDataDao.findAllGifIds();
        return aPreviewMode
            ? getExternalFromInternalPreviewVersion( photoDataDao.findGifsByIdInPreviewMode( gifIds ) )
            : getExternalFromInternal( photoDataDao.findGifsById( gifIds ) );
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

    private List< PhotoDataDto > getExternalFromInternal( List< PhotoDataEntity > listOfPhotoDataEntities )
    {
        return listOfPhotoDataEntities.stream()
            .map( this::getExternalFromInternal )
            .toList();
    }

    private PhotoDataDto getExternalFromInternal( PhotoDataEntity aPhotoDataEntity )
    {
        aPhotoDataEntity.getPhotos()
            .sort( new PhotoEntityComparator() );
        return new PhotoDataDto( aPhotoDataEntity.getConvertedGif(), aPhotoDataEntity.getId(),
            aPhotoDataEntity.isPublic(), aPhotoDataEntity.getUserId()
                .getLogin(),
            aPhotoDataEntity.getDescription(), aPhotoDataEntity.getTitle(),
            aPhotoDataEntity.getUploadDateTime(), aPhotoDataEntity.getPhotos()
                .stream()
                .map( PhotoEntity::getPhoto )
                .collect( Collectors.toSet() ),
            aPhotoDataEntity.getFirstPhoto() );
    }

    private List< PhotoDataDto > getExternalFromInternalPreviewVersion( List< Tuple > aGifsByIdInPreviewMode )
    {
        return aGifsByIdInPreviewMode.stream()
            .map( this::getExternalFromInternalPreviewVersion )
            .toList();
    }

    private PhotoDataDto getExternalFromInternalPreviewVersion( Tuple aGifsByIdInPreviewMode )
    {
        return new PhotoDataDto( aGifsByIdInPreviewMode.get( 0, Long.class ),
            aGifsByIdInPreviewMode.get( 1, String.class ), aGifsByIdInPreviewMode.get( 2, String.class ),
            aGifsByIdInPreviewMode.get( 3, byte[].class ) );
    }
}
