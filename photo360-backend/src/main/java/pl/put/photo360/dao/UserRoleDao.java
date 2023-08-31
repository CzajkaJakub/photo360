package pl.put.photo360.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.RoleEntity;

/**
 * Simple dao interface to fetch data from database
 */
@Repository
public interface UserRoleDao extends JpaRepository< RoleEntity, Long >
{
    @Query( "select role from RoleEntity role where role.name = :aName" )
    RoleEntity findByRoleName( @Param( "aName" ) String aName );
}