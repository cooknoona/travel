package com.travel.dto.request;

import com.travel.constant.LogType;
import com.travel.entity.UserEventLog;

import java.time.LocalDateTime;

public record UserEventLogRequest(
        Long userPk,
        String ipAddress
) {
    public UserEventLog toEntity(LogType type) {
        return UserEventLog.builder()
                .userPk(userPk)
                .logType(type)
                .ipAddress(ipAddress)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static UserEventLogRequest of(Long userPk, String ipAddress) {
        return new UserEventLogRequest(userPk, ipAddress);
    }
}
