package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.constant.EOrgRegistrationStatus;
import com.sep490.g28.hvh.be.entity.OrganizationRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRegistrationRepository extends JpaRepository<OrganizationRegistration, UUID> {

    @Query("""
            SELECT or
            FROM OrganizationRegistration or
            WHERE (:status IS NULL OR or.status = :status)
                              AND (:managerEmail IS NULL OR or.managerEmail ILIKE CONCAT('%', CAST(:managerEmail AS string), '%'))
            """)
    Page<OrganizationRegistration> search(
            @Param("status") EOrgRegistrationStatus status,
            @Param("managerEmail") String managerEmail,
            Pageable pageable
    );

    @Query("""
            SELECT or
            FROM OrganizationRegistration or
            LEFT JOIN FETCH or.reviewedBy sa
            LEFT JOIN FETCH or.organization o
            LEFT JOIN FETCH or.orgManager om
            WHERE or.id = :id
            """)
    Optional<OrganizationRegistration> findOrganizationRegistrationsByIdWithLazyLoad(UUID id);
}
