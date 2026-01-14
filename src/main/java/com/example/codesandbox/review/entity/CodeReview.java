package com.example.codesandbox.review.entity;

import com.example.codesandbox.snippet.entity.CodeSnippet;
import com.example.codesandbox.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "code_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 你来添加其他字段...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id",nullable = false)
    private CodeSnippet snippet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id",nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status=ReviewStatus.PENDING;

    @Column(name = "review_content",columnDefinition = "TEXT")
    private String reviewContent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ReviewStatus {
        PENDING,// 待审核
        APPROVED,           // 通过
        CHANGES_REQUESTED,  // 需要修改
        REJECTED            // 拒绝
    }
}
