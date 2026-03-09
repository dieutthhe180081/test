package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.event.response.EditEventResponse;
import com.sep490.g28.hvh.be.dto.event.request.EditEventRequest;

public interface EventService {

    EditEventResponse draftEvent(EditEventRequest request);

    EditEventResponse submitEvent(EditEventRequest request);
}
