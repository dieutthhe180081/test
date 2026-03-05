package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.event.response.EventFeedResponse;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    EventFeedResponse getEventFeeds(int pageNumber,
                                    int pageSize,
                                    boolean refresh,
                                    String name,
                                    String address,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    List<Short> activitySubDomains);
}
