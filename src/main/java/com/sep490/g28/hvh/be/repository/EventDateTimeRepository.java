package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.EventDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventDateTimeRepository extends JpaRepository<EventDateTime, UUID> {
    @Query(
            """
            SELECT EventDateTime
            FROM EventDateTime EventDateTime
            WHERE EventDateTime.event.id = :id
            """
    )
    List<EventDateTime> findByEventId(UUID id);

}
