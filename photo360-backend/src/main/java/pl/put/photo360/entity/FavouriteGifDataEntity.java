package pl.put.photo360.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table( name = "user_favourite_gif" )
public class FavouriteGifDataEntity implements Serializable
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "user_id", nullable = false )
    private UserDataEntity userId;

    @ManyToOne
    @JoinColumn( name = "photo_data_id", nullable = false )
    private PhotoDataEntity photoDataId;

    public FavouriteGifDataEntity( UserDataEntity user, PhotoDataEntity dataEntity )
    {
        this.userId = user;
        this.photoDataId = dataEntity;
    }
}
