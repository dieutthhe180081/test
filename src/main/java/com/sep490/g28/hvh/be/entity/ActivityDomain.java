package com.sep490.g28.hvh.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "activity_domains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDomain {
    @Id
    @Column(columnDefinition = "smallint")
    private Short id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "earliest_start_time", nullable = false)
    private LocalTime earliestStartTime; //HH:mm:ss

    @Column(name = "latest_end_time", nullable = false)
    private LocalTime latestEndTime;

    @Column(name = "default_session_max_time", nullable = false)
    private Short defaultSessionMaxTime;

    @Column(name = "special_session_max_time")
    private Short specialSessionMaxTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
