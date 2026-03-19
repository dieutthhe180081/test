package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.ActivityDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityDomainRepository extends JpaRepository<ActivityDomain, Short> {
    @Query("""
            SELECT ad
            FROM ActivityDomain ad
            WHERE (:active IS NULL OR ad.active = :active) 
                        AND (:name IS NULL OR ad.name ILIKE CONCAT('%', CAST(:name AS string), '%'))
            """)
    Page<ActivityDomain> search(
            @Param("active") Boolean active,
            @Param("name") String name,
            Pageable pageable
    );

    boolean existsByNameIgnoreCase(String name);
}
