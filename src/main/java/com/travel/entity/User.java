package com.travel.entity;

import com.travel.constant.Authority;
import com.travel.constant.Gender;
import com.travel.constant.Language;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** User entity to have a set of basic information of users */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String firstName;

    @Column(length = 20)
    private String middleName;

    @Column(nullable = false, length = 20)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String nickName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime birthDate;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String phone;

    private String profileImgUrl;
    private String feed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @ElementCollection(targetClass = Language.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    private Set<Language> languages = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<GuideFeedback> guideFeedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "requester", cascade = CascadeType.REMOVE)
    private List<FriendRequest> friendRequesters = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<FriendRequest> friendReceivers = new ArrayList<>();

    @OneToMany(mappedBy = "requester", cascade = CascadeType.REMOVE)
    private List<Friend> friendInitiators = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<Friend> friendAcceptors = new ArrayList<>();
}
