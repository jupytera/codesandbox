package com.example.codesandbox.execution.repository;

import com.example.codesandbox.execution.entity.ExecutionTask;
import com.example.codesandbox.snippet.entity.CodeSnippet;
import com.example.codesandbox.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutionTaskRepository extends JpaRepository<ExecutionTask, Long> {

    /**
     * 查找某个用户的所有执行任务
     * 用途：查看"我的执行历史"
     */
    Page<ExecutionTask> findByExecutorOrderByCreatedAtDesc(User executor, Pageable pageable);

    /**
     * 根据状态查找任务
     * 用途：查找所有正在执行/失败/超时的任务
     */
    List<ExecutionTask> findByStatus(ExecutionTask.Status status);

    /**
     * 根据代码哈希查找最近的执行结果
     * 用途：缓存命中检查
     */
    @Query("""
            SELECT et FROM ExecutionTask et WHERE et.codeHash = :codeHash AND et.status = 'COMPLETED' 
            ORDER BY et.createdAt DESC LIMIT 1
            """)
    Optional<ExecutionTask> findLatestCompletedByCodeHash(String codeHash);

    /**
     * 查找某个代码片段的所有执行记录
     * 用途：查看这个代码被执行了多少次
     */
    List<ExecutionTask> findBySnippet(CodeSnippet snippet);

    /**
     * 统计某个用户在特定时间段内的执行次数
     * 用途：限流检查（每分钟最多10次）
     */
    @Query("SELECT COUNT(et) FROM ExecutionTask et WHERE et.executor = :executor AND et.createdAt > :since")
    long countByExecutorSince(User executor, LocalDateTime since);

    /**
     * 查找超时未完成的任务
     * 用途：清理僵尸任务
     */
    @Query("SELECT et FROM ExecutionTask et WHERE et.status IN ('PENDING', 'RUNNING') AND et.createdAt < :timeout")
    List<ExecutionTask> findStuckTasks(LocalDateTime timeout);

    /**
     * 统计某种语言的执行次数
     * 用途：统计各语言的使用情况
     */
    long countByLanguage(String language);

    /**
     * 查找某个用户的最近N次执行
     * 用途：显示最近执行记录
     */
    List<ExecutionTask> findTop10ByExecutorOrderByCreatedAtDesc(User executor);
}