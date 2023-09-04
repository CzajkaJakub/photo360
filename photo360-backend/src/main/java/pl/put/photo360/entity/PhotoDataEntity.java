package pl.put.photo360.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table( name = "photo_data" )
public class PhotoDataEntity
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @ManyToOne
    @JoinColumn( name = "user_id", nullable = false )
    private UserDataEntity userId;

    @Column( name = "creation_date_time", nullable = false )
    private Instant creationDateTime;

    @Column( name = "public", nullable = false )
    private boolean isPublic;

    @Column( name = "description" )
    private String description;

    @Lob
    @Column( name = "photo", nullable = false )
    private byte[] photo;
}