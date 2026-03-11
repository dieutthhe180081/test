package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.entity.IdentityVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface IdentityVerificationRepository extends JpaRepository<IdentityVerification, UUID> {

    @Query("""
                SELECT iv
                FROM IdentityVerification iv
                WHERE (:status IS NULL OR iv.status = :status)
                  AND (:email IS NULL OR iv.email ILIKE CONCAT('%', CAST(:email AS string), '%'))
            """)
    Page<IdentityVerification> search(
            @Param("status") EVolunteerVerificationStatus status,
            @Param("email") String email,
            Pageable pageable
    );
}