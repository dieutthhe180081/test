package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.constant.EEventStatus;
import com.sep490.g28.hvh.be.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query(value = """
            SELECT e.*
            FROM events e
            LEFT JOIN activity_sub_domains asd 
                ON e.activity_sub_domain_id = asd.id
            WHERE (:name IS NULL OR e.name ILIKE CONCAT('%', :name, '%'))
            AND (:address IS NULL OR e.address ILIKE CONCAT('%', :address, '%'))
            AND (CAST(:startDate AS DATE) IS NULL OR e.start_date >= :startDate)
            AND (CAST(:endDate AS DATE) IS NULL OR e.start_date <= :endDate)
            AND (asd.id IN (:activitySubDomainIds))
            -- #pageable
            """,
            nativeQuery = true)
    Slice<Event> search(
            @Param("name") String name,
            @Param("address") String address,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activitySubDomainIds") List<Short> activitySubDomainIds,
            Pageable pageable);

    @Query(value = """
            SELECT e.*
            FROM events e
            LEFT JOIN activity_sub_domains asd 
                ON e.activity_sub_domain_id = asd.id
            WHERE (:name IS NULL OR e.name ILIKE CONCAT('%', :name, '%'))
            AND (:address IS NULL OR e.address ILIKE CONCAT('%', :address, '%'))
            AND (:startDate IS NULL OR e.start_date >= :startDate)
            AND (:endDate IS NULL OR e.start_date <= :endDate)
            AND (asd.id IN (:activitySubDomainIds))
            AND e.created_at > :since
            -- #pageable
            """,
            nativeQuery = true)
    Slice<Event> refresh(
            @Param("name") String name,
            @Param("address") String address,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activitySubDomainIds") List<Short> activitySubDomainIds,
            OffsetDateTime since,
            Pageable pageable);

    @Query(value = """
            SELECT e.*
            FROM events e
            WHERE e.organization_id = :organizationId
            AND (e.status IN (:status))
            AND (:name IS NULL OR e.name ILIKE CONCAT('%', :name, '%'))
            -- #pageable
            """,
            nativeQuery = true)
    Page<Event> findEventsByOrganizationIdAnd(
            @Param("organizationId") UUID organizationId,
            @Param("status") List<EEventStatus> status,
            @Param("name") String name,
            Pageable pageable
    );
}
