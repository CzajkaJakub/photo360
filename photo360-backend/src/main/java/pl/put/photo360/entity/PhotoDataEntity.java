package pl.put.photo360.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table( name = "photo_data" )
public class PhotoDataEntity implements Serializable
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "user_id", nullable = false )
    private UserDataEntity userId;

    @Column( name = "upload_date_time", nullable = false )
    private Instant uploadDateTime;

    @Column( name = "public", nullable = false )
    private boolean isPublic;

    @Column( name = "description" )
    private String description;

    @Column( name = "converted_gif", nullable = false )
    private byte[] convertedGif;

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn( name = "photo_data_id" )
    private List< PhotoEntity > photos = new ArrayList<>();

    @OneToMany( mappedBy = "photoDataId", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private Set< FavouriteGifDataEntity > favouriteBy = new HashSet<>();

    public PhotoDataEntity( UserDataEntity user, Boolean isPublic, String aDescription )
    {
        this.userId = user;
        this.isPublic = isPublic;
        this.description = aDescription;
        this.uploadDateTime = Instant.now();
    }
}