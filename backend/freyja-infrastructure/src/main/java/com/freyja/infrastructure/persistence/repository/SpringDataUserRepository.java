package com.freyja.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import com.freyja.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
