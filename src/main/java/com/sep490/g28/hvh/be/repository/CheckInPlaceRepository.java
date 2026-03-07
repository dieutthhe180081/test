package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.CheckInPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CheckInPlaceRepository extends JpaRepository<CheckInPlace, UUID> {

    @Query(
            """
            SELECT CheckInPlace
            FROM CheckInPlace CheckInPlace
            WHERE CheckInPlace.event.id = :id
            """
    )
    List<CheckInPlace> findByEventId(UUID id);
}
