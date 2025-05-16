package com.travel.service;

import com.travel.constant.LogStatus;
import com.travel.constant.LogType;
import com.travel.dto.request.UserEventLogRequest;
import com.travel.entity.UserEventLog;
import com.travel.repository.UserEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** To collect important events into DB */
@Service
@RequiredArgsConstructor
public class EventTrackingService {
    private final UserEventLogRepository userEventLogRepository;

    public void loginEventCollector(Long userPk, String ipAddress, LogType type, LogStatus status) {
        UserEventLogRequest request = UserEventLogRequest.of(userPk, ipAddress);
        UserEventLog log = request.toEntity(type, status);
        userEventLogRepository.save(log);
    }
}
