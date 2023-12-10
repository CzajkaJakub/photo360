package pl.put.photo360.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.FavouriteGifDataEntity;
import pl.put.photo360.entity.PhotoDataEntity;

@Repository
public interface FavouriteGifDataDao extends JpaRepository< FavouriteGifDataEntity, Long >
{
    @Query( "select fgde.photoDataId.id from FavouriteGifDataEntity fgde where fgde.userId.login = :userId" )
    List< Long > findUserFavouriteGifsIds( @Param( "userId" ) String userId );

    @Query( "select fgde from FavouriteGifDataEntity fgde where fgde.userId.id = :userId and fgde.photoDataId.id = :gifId" )
    Optional< FavouriteGifDataEntity > findUserGif( @Param( "userId" ) Integer userId,
        @Param( "gifId" ) Long gifId );
}
