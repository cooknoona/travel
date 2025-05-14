package com.travel.entity;

import com.travel.constant.LogDetail;
import com.travel.constant.LogStatus;
import com.travel.constant.LogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** For user event log checking, Not necessary to have any relation with other entities */
@Entity
@Table(name = "user_event_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** This will be replaced for user primary key, nullable = true */
    @Column(unique = true, length = 20)
    private Long userPk;

    @Enumerated(value = EnumType.STRING)
    private LogType logType;

    @Enumerated(value = EnumType.STRING)
    private LogStatus logStatus;

    @Enumerated(value = EnumType.STRING)
    private LogDetail logDetail;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
