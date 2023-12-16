package pl.put.photo360.dto;

import java.time.Instant;
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
    private Set< byte[] > listOfPhotos;
    private byte[] headPhoto;

    public PhotoDataDto( Long aGifId, String aUserLogin, String aTitle, byte[] aHeadPhoto )
    {
        gifId = aGifId;
        userLogin = aUserLogin;
        title = aTitle;
        headPhoto = aHeadPhoto;
    }
}