package com.example.codesandbox.audit.entity;

// 导入需要的包
import com.example.codesandbox.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 接下来你来添加其他字段...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String action;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate(){
        createdAt=LocalDateTime.now();
    }

    @Column(name = "ip_address",nullable = false)
    private String ipAddress;

    @Column(name = "old_value",columnDefinition = "LONGTEXT")
    private String oldValue;

    @Column(name = "new_value",columnDefinition = "LONGTEXT")
    private String newValue;

    @Column(name = "user_agent")
    private String userAgent;

}
