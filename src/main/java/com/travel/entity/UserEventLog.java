package com.travel.entity;

import com.travel.constant.LogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/** For user event checking, Not necessary to have any relation with other entities */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_event_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private Long userPk;

    @Enumerated(value = EnumType.STRING)
    private LogType logType;

    @Column(nullable = false)
    private String ipAddress;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
