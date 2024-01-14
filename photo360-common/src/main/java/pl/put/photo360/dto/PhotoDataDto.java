package pl.put.photo360.dto;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDataDto
{
    private byte[] gif;
    private Long gifId;
    private boolean isPublic;
    private String userLogin;
    private String description;
    private String title;
    private Instant uploadDateTime;
    private List< byte[] > listOfPhotos;
    private byte[] headPhoto;

    public PhotoDataDto( Long aGifId, String aTitle, String aUserLogin, byte[] aHeadPhoto )
    {
        gifId = aGifId;
        title = aTitle;
        headPhoto = aHeadPhoto;
        userLogin = aUserLogin;
    }
}