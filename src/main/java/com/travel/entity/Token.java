package com.travel.entity;

import jakarta.persistence.*;
import lombok.*;

/** Token Entity - Access, Refresh Token, and  */
@Entity
@Table(name = "token")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;
}
