package com.travel.entity;

import com.travel.constant.PostStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Entity for a post, only guides have an authority to post it */
@Entity
@Table(name = "guide_tour")
@Getter @Setter
@NoArgsConstructor
public class GuideTour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
