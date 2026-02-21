package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.ActivitySubDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivitySubDomainRepository extends JpaRepository<ActivitySubDomain, Short> {
    boolean existsByNameIgnoreCase(String name);
}
