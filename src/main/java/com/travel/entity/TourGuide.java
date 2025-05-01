package com.travel.entity;

import com.travel.constant.user.Level;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tour_guide")
@Getter @Setter
@NoArgsConstructor
public class TourGuide extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime birthDate;

    @Enumerated(EnumType.STRING)
    private Level level;

    @OneToMany(mappedBy = "tour_guide", cascade = CascadeType.REMOVE)
    private List<GuidePost> guidePosts = new ArrayList<>();

    @OneToMany(mappedBy = "tour_guide", cascade = CascadeType.REMOVE)
    private List<Certificate> certificates = new ArrayList<>();
}
