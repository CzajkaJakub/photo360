package pl.put.photo360.shared.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.put.photo360.entity.PhotoDataEntity;

@Getter
@Setter
@NoArgsConstructor
public class PhotoDataDto
{
    private byte[] gif;
    private Long gifId;
    private boolean isPublic;
    private String userLogin;
    private String description;
    private Instant uploadDateTime;

    public PhotoDataDto( PhotoDataEntity aPhotoDataEntity )
    {
        this.gifId = aPhotoDataEntity.getId();
        this.gif = aPhotoDataEntity.getConvertedGif();
        this.isPublic = aPhotoDataEntity.isPublic();
        this.description = aPhotoDataEntity.getDescription();
        this.userLogin = aPhotoDataEntity.getUserId()
            .getLogin();
    }
}