package com.example.codesandbox.snippet.entity;

import com.example.codesandbox.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "code_shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    private CodeSnippet snippet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by", nullable = false)
    private User sharedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with")
    private User sharedWith;

    @Column(name = "share_token", unique = true, length = 100)
    private String shareToken;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 检查分享是否已过期
     */
    public boolean isExpired() {
        if (expirationDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expirationDate);
    }
}
