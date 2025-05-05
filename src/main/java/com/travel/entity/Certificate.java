package com.travel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificate")
@Getter @Setter
@NoArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Guide tourGuide;
}
