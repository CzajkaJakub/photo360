package pl.put.photo360.dto;

import java.time.Instant;
import java.util.HashSet;
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

    public PhotoDataDto( Long aGifId, String aUserLogin, String aTitle, byte[] aListOfPhotos )
    {
        gifId = aGifId;
        userLogin = aUserLogin;
        title = aTitle;
        listOfPhotos = Set.of( aListOfPhotos );
    }
}