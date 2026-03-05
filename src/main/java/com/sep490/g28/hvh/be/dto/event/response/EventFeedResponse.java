package com.sep490.g28.hvh.be.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventFeedResponse {

    private List<EventSimpleResponse> events;
    private String nextCursor;
    private boolean hasMore;
}
