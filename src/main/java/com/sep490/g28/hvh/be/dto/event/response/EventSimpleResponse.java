package com.sep490.g28.hvh.be.dto.event.response;

import com.sep490.g28.hvh.be.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class EventSimpleResponse {

    private String orgName;
    private String name;
    private String imageUrl;
    private String address;
    private LocalDate startDate;
    private LocalDate recruitmentEndDate;
}
