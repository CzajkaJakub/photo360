package pl.put.photo360.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.UserRoleEntity;

/**
 * Simple dao interface to fetch data from database
 */
@Repository
public interface UserRoleDao extends JpaRepository< UserRoleEntity, Long >
{
    @Query( "select role from UserRoleEntity role where role.name = :aName" )
    UserRoleEntity findByRoleName( @Param( "aName" ) String aName );
}