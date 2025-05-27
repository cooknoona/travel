package com.travel.entity;

import com.travel.constant.GuideStatus;
import com.travel.constant.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/** Guide entity, it's inheritance class from User, after examining a user who requested to become a guide */
@Entity
@Table(name = "guide")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Guide extends User {
    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private GuideStatus guideStatus;

    @Builder.Default
    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<Tour> tours = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<TourRequest> tourRequests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<TourReservation> tourReservations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<GuideFeedback> guideFeedbacks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "guide", cascade = CascadeType.REMOVE)
    private List<Certificate> certificates = new ArrayList<>();
}
