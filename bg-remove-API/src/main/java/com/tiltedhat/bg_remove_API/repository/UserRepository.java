package com.tiltedhat.bg_remove_API.repository;

import com.tiltedhat.bg_remove_API.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByClerkId(String clerkId);
    boolean existsByClerkId(String clerkId);
}
