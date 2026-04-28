package com.helloWorld.tech.repository;

import com.helloWorld.tech.model.dao.UserDao;
import com.helloWorld.tech.model.dto.UserDetailsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDao, Integer> {
    Optional<UserDao> findByEmail(String email);

    @Query(
            value = """
            SELECT u.id,
                   u.email,
                   u.name,
                   u.status,
                   ug.role
            FROM `user` u left join user_group ug on ug.id = u.user_grp_id
            WHERE (:name IS NULL OR u.name LIKE CONCAT('%', :name, '%'))
            """
            ,nativeQuery = true
    )
    List<UserDetailsDto> findAllByNameLikeOrAll(@Param("name") String name);
}
