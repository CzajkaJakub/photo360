package pl.put.photo360.camera.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

public class Utils
{
    public static BufferedImage matToBufferedImage( Mat original )
    {
        BufferedImage image;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[ width * height * channels ];
        original.get( 0, 0, sourcePixels );

        if( original.channels() > 1 )
        {
            image = new BufferedImage( width, height, BufferedImage.TYPE_3BYTE_BGR );
        }
        else
        {
            image = new BufferedImage( width, height, BufferedImage.TYPE_BYTE_GRAY );
        }

        final byte[] targetPixels = ((DataBufferByte)image.getRaster()
            .getDataBuffer()).getData();
        System.arraycopy( sourcePixels, 0, targetPixels, 0, sourcePixels.length );

        return image;
    }
}
