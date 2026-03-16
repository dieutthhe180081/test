package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EventApplicationRepository extends JpaRepository<EventApplication, UUID> {
    @Query(
            value = """
                    SELECT e.*
                    FROM event_applications e
                    WHERE volunteer_id = :volunteerId AND session_id = :sessionId
                    """,
            nativeQuery = true)
    Optional<EventApplication> getEventApplicationsByVolunteerIdAndSessionId(UUID volunteerId, UUID sessionId);
}
