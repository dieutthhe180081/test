package com.sep490.g28.hvh.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "check_in_places",
        indexes = {
                @Index(name = "idx_check_in_places_event", columnList = "event_id"),
//                @Index(name = "idx_check_in_places_user", columnList = "user_id") used when saving checkin info
                // turn on PostGIS
                // CREATE EXTENSION IF NOT EXISTS postgis;
                // GIST index for location will be created by SQL, not in JPA
                //CREATE INDEX idx_check_in_places_location
                //ON check_in_places
                //USING GIST (location);
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckInPlace {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * geography(Point, 4326)
     * Save using PostGIS
     */
    @Column(
            name = "location",
            nullable = false,
            columnDefinition = "geography(Point, 4326)"
    )
    private Point location;

    @Column(name = "accuracy_meters", nullable = false)
    private Float accuracyMeters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private Host createBy;

}
