package pl.put.photo360.shared.converter;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_FILE_FORMAT;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.put.photo360.config.Configuration;
import pl.put.photo360.entity.PhotoEntity;
import pl.put.photo360.shared.exception.ServiceException;

@Component
public class GifCreator
{
    private final Configuration configuration;

    public GifCreator( Configuration configuration )
    {
        this.configuration = configuration;
    }

    public byte[] convertImagesIntoGif( List< PhotoEntity > aImages ) throws IOException
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
                BufferedImage bufferedImage = transparentBG( ImageIO.read( inputByteStream ),
                    configuration.getGIF_CREATE_TRANSPARENT_BACKGROUND() );

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

    public static BufferedImage transparentBG( BufferedImage image, Boolean transparent )
    {
        BufferedImage tmpImg =
            new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR );
        for( int w = 0; w < image.getWidth(); w++ )
        {
            for( int h = 0; h < image.getHeight(); h++ )
            {
                var pixelColor = new Color( image.getRGB( w, h ) );
                var isGreyOrWhitePixel = isGrayOrWhite( pixelColor );
                if( isGreyOrWhitePixel && transparent )
                {
                    tmpImg.setRGB( w, h, 0 );
                }
                else if( isGreyOrWhitePixel )
                {
                    tmpImg.setRGB( w, h, Color.WHITE.getRGB() );
                }
                else
                {
                    tmpImg.setRGB( w, h, image.getRGB( w, h ) );
                }
            }
        }
        return tmpImg;
    }

    private static boolean isGrayOrWhite( Color color )
    {
        int tolerance = 180;

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        return r >= tolerance && g >= tolerance && b >= tolerance;
    }
}
