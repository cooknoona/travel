package com.travel.entity;

import com.travel.constant.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Requested tour entity, guide can decide whether accept tour or reject
 *  Otherwise, the ticket will be on pending */
@Entity
@Table(name = "tour_request")
@Getter
@Setter
@NoArgsConstructor
public class TourRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
