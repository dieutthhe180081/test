package com.sep490.g28.hvh.be.entity;

import com.sep490.g28.hvh.be.constant.EOrgType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "dha_registered", nullable = false)
    private boolean dhaRegistered;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_type", length = 50, nullable = false)
    private EOrgType orgType;

    @Column(name = "org_introduction", length = 500, nullable = false)
    private String orgIntroduction;

    @Column(name = "manager_full_name", nullable = false, length = 100)
    private String managerFullName;

    @Column(name = "manager_cid", nullable = false, length = 12)
    private String managerCid;

    @Column(name = "manager_phone", nullable = false, length = 10)
    private String managerPhone;

    @Column(name = "manager_email", nullable = false)
    private String managerEmail;

    @Column(name = "other_images", length = 500)
    private String otherImages;

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