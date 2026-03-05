package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.Event;
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
            SELECT e
            FROM Event e
            JOIN FETCH ActivitySubDomain asd
            WHERE (:name IS NULL OR e.name ILIKE CONCAT('%', CAST(:name AS string), '%'))
            AND (:address IS NULL OR e.address ILIKE CONCAT('%', CAST(:address AS string), '%'))
            AND (:startDate IS NULL OR e.startDate >= :startDate)
            AND (:endDate IS NULL OR e.startDate <= :endDate)
            AND (:activitySubDomains IS NULL OR e.activitySubDomain.name IN :activitySubDomains)
            -- #pageable
            """,
            nativeQuery = true)
    Slice<Event> search(
            @Param("name") String name,
            @Param("address") String address,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activitySubDomains") List<String> activitySubDomains,
            Pageable pageable);

    @Query(value = """
            SELECT e
            FROM Event e
            JOIN FETCH ActivitySubDomain asd
            WHERE (:name IS NULL OR e.name ILIKE CONCAT('%', CAST(:name AS string), '%'))
            AND (:address IS NULL OR e.address ILIKE CONCAT('%', CAST(:address AS string), '%'))
            AND (:startDate IS NULL OR e.startDate >= :startDate)
            AND (:endDate IS NULL OR e.startDate <= :endDate)
            AND (:activitySubDomains IS NULL OR e.activitySubDomain.name IN :activitySubDomains)
            AND e.createdAt > :since
            ORDER BY RAND()
            -- #pageable
            """,
            nativeQuery = true)
    Slice<Event> refresh(
            @Param("name") String name,
            @Param("address") String address,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activitySubDomains") List<String> activitySubDomains,
            OffsetDateTime since,
            Pageable pageable);
}
