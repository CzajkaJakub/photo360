package pl.put.photo360.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.put.photo360.shared.utils.PhotoEntityComparator;

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

    public PhotoDataEntity( UserDataEntity user, Boolean isPublic, String aDescription )
    {
        this.userId = user;
        this.isPublic = isPublic;
        this.description = aDescription;
        this.uploadDateTime = Instant.now();
    }

    public void sortPhotosByIndex()
    {
        photos.sort( new PhotoEntityComparator() );
    }
}