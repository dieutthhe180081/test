package com.sep490.g28.hvh.be.entity;

import com.sep490.g28.hvh.be.constant.EEducationLevel;
import com.sep490.g28.hvh.be.constant.EEmployStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "volunteers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer {
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false, columnDefinition = "uuid")
    private UUID vid; //volunteer id

    @Column(unique = true, nullable = false, length = 12)
    private String cid; //citizen id

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false, length = 10)
    private String phone;

    @Column(unique = true, length = 50)
    private String nickname;

    @Column(name = "full_name", length = 100)
    private String fullName;

    private boolean gender; //1: male, 0: female

    private LocalDate dob;

    private Short level;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 50)
    private String address;

    @Column(name = "detail_address", length = 100)
    private String detailAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "employ_status", length = 30)
    private EEmployStatus employStatus;

    @Column(name = "work_address")
    private String workAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", length = 30)
    private EEducationLevel educationLevel;

    @Column(length = 50)
    private String sid; //student id

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private SystemAdmin created_by;
}
