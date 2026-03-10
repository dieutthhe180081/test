package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.EventSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventSessionRepository extends JpaRepository<EventSession, UUID> {
    @Query(
            """
            SELECT EventSession
            FROM EventSession session
            WHERE session.event.id = :id
            """
    )
    List<EventSession> findByEventId(UUID id);

}
