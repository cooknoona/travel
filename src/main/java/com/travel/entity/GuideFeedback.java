package com.travel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
