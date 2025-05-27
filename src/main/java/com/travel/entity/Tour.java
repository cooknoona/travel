package com.travel.entity;

import com.travel.constant.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/** Entity for a tour, only guides have an authority to post it */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tour")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime postedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
