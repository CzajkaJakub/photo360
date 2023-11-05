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
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.springframework.stereotype.Component;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.entity.PhotoEntity;
import pl.put.photo360.exception.ServiceException;

@Component
public class GifCreator
{
    private final Configuration configuration;

    public GifCreator( Configuration configuration )
    {
        this.configuration = configuration;
    }

    public byte[] convertImagesIntoGif( List< PhotoEntity > aImages, String backgroundColorHex )
        throws IOException
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageOutputStream output = new MemoryCacheImageOutputStream( byteArrayOutputStream );

            GifSequenceWriter writer = new GifSequenceWriter( output, BufferedImage.TYPE_4BYTE_ABGR,
                configuration.getGIF_TIME_BETWEEN_FRAME(), configuration.getGIF_LOOP_CONTINUOUSLY() );

            for( PhotoEntity photoEntity : aImages )
            {
                InputStream inputByteStream = new ByteArrayInputStream( photoEntity.getPhoto() );
                BufferedImage bufferedImage;
                if( backgroundColorHex == null )
                {
                    bufferedImage = ImageIO.read( inputByteStream );
                }
                else
                {
                    Color backgroundColor = Color.decode( backgroundColorHex );
                    bufferedImage = changeBackgroundColor( ImageIO.read( inputByteStream ), backgroundColor );
                }

                writer.writeToSequence( bufferedImage );
            }

            writer.close();
            output.close();

            return byteArrayOutputStream.toByteArray();
        }
        catch( IOException aE )
        {
            throw new ServiceException( STATUS_WRONG_FILE_FORMAT );
        }
    }

    private BufferedImage changeBackgroundColor( BufferedImage inputImage, Color backgroundColor )
    {
        BufferedImage tmpImg =
            new BufferedImage( inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR );

        for( int w = 0; w < inputImage.getWidth(); w++ )
        {
            for( int h = 0; h < inputImage.getHeight(); h++ )
            {
                Color pixelColor = new Color( inputImage.getRGB( w, h ), true );

                if( pixelColor.getAlpha() == 0 )
                {
                    tmpImg.setRGB( w, h, backgroundColor.getRGB() );
                }
                else
                {
                    tmpImg.setRGB( w, h, inputImage.getRGB( w, h ) );
                }
            }
        }

        return tmpImg;
    }
}
