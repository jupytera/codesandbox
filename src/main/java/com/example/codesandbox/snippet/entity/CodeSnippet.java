package com.example.codesandbox.snippet.entity;

import com.example.codesandbox.user.entity.User;  // 导入User实体
import jakarta.persistence.*;                // JPA注解
import lombok.*;                                    // Lombok注解
import java.time.LocalDateTime;                     // 时间类

@Entity
@Table(name = "code_snippet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String language;

    public enum Visibility {
        PRIVATE,// 仅所有者可见
        SHARED,     // 分享给特定用户
        PUBLIC      // 所有人可见
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PRIVATE;

    @Column(length = 500)
    private String tags;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "fork_count")
    private Integer forkCount = 0;

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
}