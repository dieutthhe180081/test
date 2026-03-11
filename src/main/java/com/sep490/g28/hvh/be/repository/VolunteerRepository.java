package com.sep490.g28.hvh.be.repository;

import com.sep490.g28.hvh.be.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VolunteerRepository extends JpaRepository<Volunteer, UUID> {

    boolean existsByCid(String cid);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByNickname(String nickname);

}
