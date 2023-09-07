package pl.put.photo360.service;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_UNSUPPORTED_FILE;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_USER_NOT_FOUND_FROM_TOKEN;
import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_FILE_FORMAT;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pl.put.photo360.auth.AuthService;
import pl.put.photo360.config.Configuration;
import pl.put.photo360.dao.PhotoDataDao;
import pl.put.photo360.entity.PhotoDataEntity;
import pl.put.photo360.entity.PhotoEntity;
import pl.put.photo360.shared.converter.GifCreator;
import pl.put.photo360.shared.exception.ServiceException;
import pl.put.photo360.shared.exception.UserNotFoundException;

@Service
public class PhotoService
{
    private final PhotoDataDao photoDataDao;
    private final AuthService authService;
    private final Configuration configuration;
    private final GifCreator gifCreator;

    @Autowired
    public PhotoService( PhotoDataDao aPhotoDataDao, AuthService aAuthService, Configuration aConfiguration,
        GifCreator aGifCreator )
    {
        photoDataDao = aPhotoDataDao;
        authService = aAuthService;
        configuration = aConfiguration;
        gifCreator = aGifCreator;
    }

    public void savePhotos( Boolean isPublic, String description, String aAuthorizationToken,
        MultipartFile aFile )
    {
        var user = authService.findByToken( aAuthorizationToken );
        if( user.isPresent() )
        {
            if( !Objects.requireNonNull( aFile.getOriginalFilename() )
                .endsWith( configuration.getSUPPORTED_FORMAT() ) )
            {
                throw new ServiceException( STATUS_UNSUPPORTED_FILE );
            }

            try (ZipInputStream zipInputStream = new ZipInputStream( aFile.getInputStream() ))
            {
                PhotoDataEntity photoDataEntity = new PhotoDataEntity( user.get(), isPublic, description );
                ZipEntry entry;
                while( (entry = zipInputStream.getNextEntry()) != null )
                {
                    if( !entry.isDirectory() )
                    {
                        String fileName = entry.getName();
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

    public byte[] downloadGifById( Long aGifId )
    {
        var gif = photoDataDao.findGifById( aGifId );
        if( gif != null )
        {
            return gif;
        }
        else
        {
            throw new ServiceException( STATUS_GIF_BY_GIVEN_ID_NOT_EXISTS );
        }
    }
}
