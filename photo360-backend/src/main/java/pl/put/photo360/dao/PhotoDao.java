package pl.put.photo360.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.PhotoEntity;

@Repository
public interface PhotoDao extends JpaRepository< PhotoEntity, Long >
{
    @Query( "select pde.photo from PhotoEntity pde where pde.id = :gifId limit 1" )
    byte[] findSinglePhotoByGifId( @Param( "gifId" ) Long gifId );
}
