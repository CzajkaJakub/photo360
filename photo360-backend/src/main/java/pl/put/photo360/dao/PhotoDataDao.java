package pl.put.photo360.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.PhotoDataEntity;

@Repository
public interface PhotoDataDao extends JpaRepository< PhotoDataEntity, Long >
{
    @Query( "select pde from PhotoDataEntity pde where pde.id = :gifId" )
    Optional< PhotoDataEntity > findGifById( @Param( "gifId" ) Long gifId );

    @Query( "select pde from PhotoDataEntity pde where pde.userId.login = :userId" )
    List< PhotoDataEntity > findPrivateGifs( @Param( "userId" ) String userId );

    @Query( "select pde from PhotoDataEntity pde where pde.isPublic = true" )
    List< PhotoDataEntity > findPublicGifs();
}
