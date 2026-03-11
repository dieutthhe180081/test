package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventImageRepository extends JpaRepository<EventImage, UUID> {
}
