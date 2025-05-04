package com.travel.entity;

import com.travel.constant.Level;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Guide entity, it's inheritance class from User, after examining a user who requested to become a guide */
@Entity
@Table(name = "guide")
@Getter @Setter
@NoArgsConstructor
public class Guide extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Level level;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<GuidePost> guidePosts = new ArrayList<>();

    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<GuideFeedback> guideFeedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<Certificate> certificates = new ArrayList<>();
}
