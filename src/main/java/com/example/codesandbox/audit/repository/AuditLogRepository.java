package com.example.codesandbox.audit.repository;

import com.example.codesandbox.audit.entity.AuditLog;
import com.example.codesandbox.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * 查找某个用户的所有操作日志
     * 用途：用户查看"我的操作历史"
     */
    Page<AuditLog> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 根据操作类型查找日志
     * 用途：查找所有"删除"操作、"修改"操作等
     */
    Page<AuditLog> findByAction(String action, Pageable pageable);

    /**
     * 根据资源类型查找日志
     * 用途：查看对"代码片段"的所有操作
     */
    Page<AuditLog> findByResourceType(String resourceType, Pageable pageable);

    /**
     * 查找对特定资源的所有操作
     * 用途：查看某个代码片段的完整操作历史
     */
    List<AuditLog> findByResourceTypeAndResourceIdOrderByCreatedAtDesc(
            String resourceType,
            Long resourceId
    );

    /**
     * 查找某个时间段内的所有日志
     * 用途：生成操作报告
     */
    Page<AuditLog> findByCreatedAtBetween(
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );

    /**
     * 根据IP地址查找日志
     * 用途：追踪某个IP的操作行为（安全审计）
     */
    List<AuditLog> findByIpAddress(String ipAddress);

    /**
     * 查找某个用户的特定操作
     * 用途：查看用户的"删除"操作历史
     */
    Page<AuditLog> findByUserAndAction(User user, String action, Pageable pageable);

    /**
     * 统计某个用户的操作次数
     * 用途：用户活跃度统计
     */
    long countByUser(User user);

    /**
     * 删除过期的审计日志
     * 用途：定期清理（例如保留最近6个月的日志）
     */
    @Query("DELETE FROM AuditLog al WHERE al.createdAt < :cutoffDate")
    int deleteOldLogs(LocalDateTime cutoffDate);
}