package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface HostRepository extends JpaRepository<Host, UUID> {

    Long countHostByOrganizationId(UUID orgId);
}
