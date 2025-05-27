package com.travel.dto.request;

import com.travel.constant.LogType;
import com.travel.entity.UserEventLog;

public record UserEventLogRequest(
        Long userPk,
        String ipAddress
) {
    /** ToEntity instance method to create an entity, parameter value is changeable but values from
     *  static factory method are immutable */
    public UserEventLog toEntity(LogType type) {
        return UserEventLog.builder()
                .userPk(userPk)
                .logType(type)
                .ipAddress(ipAddress)
                .build();
    }

    /** Static factory method, these two field user pk and ip address aren't changeable */
    public static UserEventLogRequest of(Long userPk, String ipAddress) {
        return new UserEventLogRequest(userPk, ipAddress);
    }
}
