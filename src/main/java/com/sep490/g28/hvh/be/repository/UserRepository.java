package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
                select u.role.name
                from User u
                where u.id = :id
            """)
    Optional<ERole> findRoleByUserId(UUID id);
}
