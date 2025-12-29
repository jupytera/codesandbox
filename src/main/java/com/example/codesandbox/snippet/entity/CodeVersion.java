package com.example.codesandbox.snippet.entity;

import com.example.codesandbox.user.entity.User;  // 导入User实体
import jakarta.persistence.*;                // JPA注解
import lombok.*;                                    // Lombok注解
import java.time.LocalDateTime;                     // 时间类

@Entity
@Table(name = "code_versions")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class CodeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    private CodeSnippet snippet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "delta_content", columnDefinition = "LONGTEXT")
    private String deltaContent;

    @Column(name = "commit_message", columnDefinition = "TEXT")
    private String commitMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
