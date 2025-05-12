package com.travel.service;

import com.travel.repository.UserEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventTrackingService {
    private final UserEventLogRepository userEventLogRepository;
}
