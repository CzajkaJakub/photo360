package pl.put.photo360.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.put.photo360.entity.PhotoDataEntity;

@Repository
public interface PhotoDataDao extends JpaRepository< PhotoDataEntity, Long >
{}
