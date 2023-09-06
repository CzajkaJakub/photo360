package pl.put.photo360.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.UserDataEntity;

/**
 * User data
 */
@Repository
public interface UserDataDao extends JpaRepository< UserDataEntity, Long >
{
    @Query( "select ud from UserDataEntity ud where ud.login = :aLogin" )
    UserDataEntity findByLogin( @Param( "aLogin" ) String aLogin );

    @Query( "select ud from UserDataEntity ud where ud.email = :aEmail" )
    UserDataEntity findByEmail( @Param( "aEmail" ) String aEmail );
}
