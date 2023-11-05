package pl.put.photo360.dto;

import java.time.Instant;

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
    private Instant uploadDateTime;
}