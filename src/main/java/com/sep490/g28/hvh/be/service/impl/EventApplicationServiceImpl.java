package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.service.EventApplicationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventApplicationServiceImpl implements EventApplicationService {
    @Override
    public void applyEventSession(UUID sessionId) {
        //check session existed?

        //check expect Vol amount

        //check auto approve


        //send notification to host



    }
}
