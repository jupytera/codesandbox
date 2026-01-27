package com.example.codesandbox.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "token",nullable = false,unique = true,length=500)
    private String token;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(length = 255)
    private String reason;

    @PrePersist
    private void onCreate() {
        if (revokedAt == null) {
            revokedAt = LocalDateTime.now();
        }
    }

}
