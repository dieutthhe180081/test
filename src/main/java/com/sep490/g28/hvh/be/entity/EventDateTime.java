package com.sep490.g28.hvh.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "event_date_times",
        indexes = {
                @Index(name = "idx_event_date_times_eventId", columnList = "event_id"),
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDateTime {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    //--------------------------------------------------------
//    @Column(name = "date", nullable = false)
//    private LocalDate date;

    @Column(name = "start_date_time", nullable = false)
    private OffsetDateTime startDateTime; // check-in time

    @Column(name = "end_date_time", nullable = false)
    private OffsetDateTime endDateTime;   // check-out time

    //--------------------------------------------------------
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
