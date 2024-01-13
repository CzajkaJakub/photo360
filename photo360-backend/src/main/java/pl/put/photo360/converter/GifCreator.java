package pl.put.photo360.converter;

import static pl.put.photo360.dto.ServerResponseCode.STATUS_WRONG_FILE_FORMAT;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.entity.PhotoEntity;
import pl.put.photo360.exception.ServiceException;

@Component
public class GifCreator
{
    private final Configuration configuration;
    private final AnimatedGifEncoder animatedGifEncoder;

    @Autowired
    public GifCreator( Configuration configuration, AnimatedGifEncoder aAnimatedGifEncoder )
    {
        this.configuration = configuration;
        this.animatedGifEncoder = aAnimatedGifEncoder;
    }

    public byte[] convertImagesIntoGif( List< PhotoEntity > aImages )
    {
        try
        {
            ByteArrayOutputStream gifOutputStream = new ByteArrayOutputStream();
            animatedGifEncoder.start( gifOutputStream );
            animatedGifEncoder.setDelay( configuration.getGIF_TIME_BETWEEN_FRAME() );
            animatedGifEncoder.setDispose( 2 );
            animatedGifEncoder.setRepeat( 0 );
            animatedGifEncoder.setQuality( 10 );
            animatedGifEncoder.setTransparent( Color.BLACK );

            for( PhotoEntity photoEntity : aImages )
            {
                InputStream inputByteStream = new ByteArrayInputStream( photoEntity.getPhoto() );
                animatedGifEncoder.addFrame( ImageIO.read( inputByteStream ) );
            }

            animatedGifEncoder.finish();
            return gifOutputStream.toByteArray();
        }
        catch( IOException aE )
        {
            throw new ServiceException( STATUS_WRONG_FILE_FORMAT );
        }
    }

    public static void changeBackgroundColor( List< PhotoEntity > images, String aBackgroundColor )
    {
        Color backgroundColor;
        if( aBackgroundColor != null )
        {
            backgroundColor = Color.decode( aBackgroundColor );
            images.forEach( photo -> {

                try
                {
                    BufferedImage bufferedImage = byteArrayToBufferedImage( photo.getPhoto() );

                    BufferedImage tmpImg = new BufferedImage( bufferedImage.getWidth(),
                        bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR );

                    for( int w = 0; w < bufferedImage.getWidth(); w++ )
                    {
                        for( int h = 0; h < bufferedImage.getHeight(); h++ )
                        {
                            Color pixelColor = new Color( bufferedImage.getRGB( w, h ), true );

                            if( pixelColor.getAlpha() == 0 )
                            {
                                tmpImg.setRGB( w, h, backgroundColor.getRGB() );
                            }
                            else
                            {
                                tmpImg.setRGB( w, h, bufferedImage.getRGB( w, h ) );
                            }
                        }
                    }

                    photo.setPhoto( bufferedImageToByteArray( tmpImg ) );

                }
                catch( IOException aE )
                {
                    throw new RuntimeException( aE );
                }
            } );
        }
    }

    private static BufferedImage byteArrayToBufferedImage( byte[] imageBytes ) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream( imageBytes );
        return ImageIO.read( bais );
    }

    private static byte[] bufferedImageToByteArray( BufferedImage image ) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( image, "png", baos );
        return baos.toByteArray();
    }
}
