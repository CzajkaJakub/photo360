package pl.put.photo360.shared.utils;

import java.util.Comparator;

import pl.put.photo360.entity.PhotoEntity;

public class PhotoEntityComparator implements Comparator< PhotoEntity >
{
    @Override
    public int compare( PhotoEntity photo1, PhotoEntity photo2 )
    {
        int number1 = extractNumber( photo1.getPhotoFileName() );
        int number2 = extractNumber( photo2.getPhotoFileName() );
        return Integer.compare( number1, number2 );
    }

    private int extractNumber( String fileName )
    {
        String numberPart = fileName.replaceAll( "[^0-9]", "" );
        return Integer.parseInt( numberPart );
    }
}