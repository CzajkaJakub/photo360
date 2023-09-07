package pl.put.photo360.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.PhotoDataEntity;

@Repository
public interface PhotoDataDao extends JpaRepository< PhotoDataEntity, Long >
{
    @Query( "select pde.convertedGif from PhotoDataEntity pde where pde.id = :gifId" )
    byte[] findGifById( @Param( "gifId" ) Long gifId );
}
