package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.event.response.CreateEventRequestResponse;
import com.sep490.g28.hvh.be.dto.event.request.CreateEventRequest;

public interface EventService {

    CreateEventRequestResponse editEvent(CreateEventRequest request);
}
