package pl.put.photo360.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.UserDataEntity;

import java.time.Instant;
import java.util.Optional;

/**
 * User data
 */
@Repository
public interface UserDataDao extends JpaRepository< UserDataEntity, Long >
{
    /**
     * Find user by given login.
     *
     * @param aLogin
     *                   User's request login.
     */
    @Query( "select ud from UserDataEntity ud where ud.login = :aLogin" )
    Optional< UserDataEntity > findByLogin( @Param( "aLogin" ) String aLogin );

    /**
     * Find user by given email.
     *
     * @param aEmail
     *                   User's request email.
     */
    @Query( "select ud from UserDataEntity ud where ud.email = :aEmail" )
    Optional< UserDataEntity > findByEmail( @Param( "aEmail" ) String aEmail );

    /**
     * Function increases number of failed log in attempts in database
     *
     * @param user
     *                 user entity
     */
    @Transactional
    default void increaseFailedAttempts( UserDataEntity user )
    {
        user.increaseFailAttempts();
        save( user );
    }

    /**
     * Function resets the number of failed log in attempts in database
     *
     * @param user
     *                 user entity
     */
    @Transactional
    default void resetFailedAttempts( UserDataEntity user )
    {
        user.setFailedAttempt( 0 );
        save( user );
    }

    /**
     * Function locks user
     *
     * @param user
     *                 user entity
     */
    @Transactional
    default void lockAccount( UserDataEntity user )
    {
        user.setLocked( true );
        user.setLockTime( Instant.now() );
        save( user );
    }
}
