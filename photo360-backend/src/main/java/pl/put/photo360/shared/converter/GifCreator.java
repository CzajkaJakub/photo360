package pl.put.photo360.shared.converter;

import static pl.put.photo360.shared.dto.ServerResponseCode.STATUS_WRONG_FILE_FORMAT;

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

import pl.put.photo360.entity.PhotoEntity;
import pl.put.photo360.shared.exception.ServiceException;

@Component
public class GifCreator
{
    public byte[] convertImagesIntoGif( List< PhotoEntity > aImages ) throws IOException
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageOutputStream output = new MemoryCacheImageOutputStream( byteArrayOutputStream );
            var imageByteList = aImages.stream()
                .toList();

            BufferedImage bi = ImageIO.read( new ByteArrayInputStream( imageByteList.get( 0 )
                .getPhoto() ) );

            GifSequenceWriter writer = new GifSequenceWriter( output, bi.getType(), 50, true );

            for( PhotoEntity photoEntity : imageByteList )
            {
                InputStream inputByteStream = new ByteArrayInputStream( photoEntity.getPhoto() );
                BufferedImage bufferedImage = ImageIO.read( inputByteStream );
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
}
