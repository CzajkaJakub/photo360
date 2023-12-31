package pl.put.photo360.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table( name = "photo" )
public class PhotoEntity implements Serializable
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column( name = "photo_file_name" )
    private String photoFileName;

    @Column( name = "photo", nullable = false )
    private byte[] photo;

    public PhotoEntity( String aFileName, byte[] aPhotoData )
    {
        this.photoFileName = aFileName;
        this.photo = aPhotoData;
    }
}
