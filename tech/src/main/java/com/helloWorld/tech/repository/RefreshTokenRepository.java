package com.helloWorld.tech.repository;

import com.helloWorld.tech.model.dao.RefreshTokenDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenDao, Long> {
    Optional<RefreshTokenDao> findByTokenHash(String tokenHash);
}

