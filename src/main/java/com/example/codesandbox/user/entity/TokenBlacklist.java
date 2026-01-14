package com.example.codesandbox.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "token",nullable = false)
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createAt;

    @PrePersist
    private void onCreate(){
        createAt=LocalDateTime.now();
    }

}
