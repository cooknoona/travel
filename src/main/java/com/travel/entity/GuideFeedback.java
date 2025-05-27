package com.travel.entity;

import com.travel.constant.Satisfaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/** Entity to comment on a guide reputation */
@Entity
@Table(name = "guide_feedback")
@Getter @Setter
@NoArgsConstructor
public class GuideFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(nullable = false)
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime commentedAt;

    @Enumerated(EnumType.STRING)
    private Satisfaction satisfaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
