package com.travel.entity;

import com.travel.constant.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entity for friend request, when requester sends a ticket to a user, it will be pending here */
@Entity
@Table(name = "friend_request")
@Getter @Setter
@NoArgsConstructor
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    private User requester;

    @ManyToOne
    private User receiver;

    @Enumerated(EnumType.STRING)
    private Status status;
}
