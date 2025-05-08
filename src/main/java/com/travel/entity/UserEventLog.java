package com.travel.entity;

import com.travel.constant.LogType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** For user event log checking, Not necessary to have relations with other entities */
@Entity
@Table(name = "user_event_log")
@Getter @Setter
@NoArgsConstructor
public class UserEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** This will be replaced for user primary key */
    @Column(nullable = false, unique = true, length = 20)
    private Long userPk;

    @Column(nullable = false)
    private String event;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false, length = 1000)
    private String detail;

    @Enumerated(value = EnumType.STRING)
    private LogType logType;
}
