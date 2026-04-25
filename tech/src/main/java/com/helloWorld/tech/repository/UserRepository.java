package com.helloWorld.tech.repository;

import com.helloWorld.tech.model.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDao, Integer> {
    Optional<UserDao> findByEmail(String email);
}
