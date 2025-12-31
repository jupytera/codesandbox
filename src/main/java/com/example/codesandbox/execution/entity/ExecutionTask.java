package com.example.codesandbox.execution.entity;

import com.example.codesandbox.snippet.entity.CodeSnippet;
import com.example.codesandbox.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ExecutionTask 实体 - 代码执行任务
 *
 * 作用：记录用户提交的代码执行请求及其执行结果
 *
 * 业务场景：
 * 1. 用户在代码编辑器中写代码，点击"运行"按钮
 * 2. 系统创建一个 ExecutionTask 记录（状态：PENDING）
 * 3. 任务被放入 RabbitMQ 消息队列
 * 4. 后台服务从队列中取出任务，在 Docker 容器中执行代码
 * 5. 执行完成后，更新任务状态和结果（COMPLETED/FAILED/TIMEOUT）
 * 6. 前端通过 WebSocket 或轮询获取执行结果
 *
 * 关键字段：
 * - code_hash: 用于缓存相同代码的执行结果（避免重复执行）
 * - container_id: 记录在哪个 Docker 容器中执行（方便排查问题）
 * - execution_time_ms: 执行耗时（用于性能统计和限流）
 * - status: 执行状态（前端根据状态显示不同的提示）
 */
@Entity
@Table(name = "execution_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的代码片段（可选）
     *
     * 两种情况：
     * 1. snippet_id != null：用户执行已保存的代码片段
     * 2. snippet_id == null：用户临时执行代码（未保存）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id")
    private CodeSnippet snippet;

    /**
     * 执行者（必须）
     *
     * 记录是谁提交的执行请求
     * 用途：
     * - 权限控制（只有执行者能查看结果）
     * - 用户行为分析（统计执行次数、偏好语言等）
     * - 限流（每个用户每分钟最多执行 10 次）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", nullable = false)
    private User executor;

    /**
     * 编程语言
     * 例如：python, java, javascript, cpp, go
     */
    @Column(nullable = false, length = 50)
    private String language;

    /**
     * 要执行的代码内容
     *
     * 为什么保存代码内容而不是只保存 snippet_id？
     * 因为：
     * 1. 用户可能执行临时代码（未保存）
     * 2. 代码片段可能被修改，需要记录执行时的确切代码
     */
    @Column(name = "code_content", columnDefinition = "LONGTEXT", nullable = false)
    private String codeContent;

    /**
     * 代码哈希值（SHA256）
     *
     * 作用：缓存优化
     * 流程：
     * 1. 计算代码的 SHA256 哈希值
     * 2. 执行前查询 Redis：是否已有该 hash 的执行结果？
     * 3. 有 → 直接返回缓存结果（节省资源）
     * 4. 无 → 执行代码，并缓存结果（key: code_hash, value: output_data）
     *
     * 例如：100 个用户都执行 print("Hello")，只需真正执行 1 次
     */
    @Column(name = "code_hash", length = 64)
    private String codeHash;

    /**
     * 输入数据
     *
     * 例如：
     * - 算法题的测试用例：[1, 5, 3, 2, 4]
     * - 函数的参数：{"n": 10}
     */
    @Column(name = "input_data", columnDefinition = "LONGTEXT")
    private String inputData;

    /**
     * 输出结果
     *
     * 例如：
     * - 标准输出：Hello World
     * - 函数返回值：55
     */
    @Column(name = "output_data", columnDefinition = "LONGTEXT")
    private String outputData;

    /**
     * 错误信息
     *
     * 当 status = FAILED 时，保存错误堆栈
     * 例如：
     * Traceback (most recent call last):
     *   File "main.py", line 2, in <module>
     *     print(x)
     * NameError: name 'x' is not defined
     */
    @Column(name = "error_message", columnDefinition = "LONGTEXT")
    private String errorMessage;

    /**
     * 执行状态
     *
     * PENDING    → 排队中（刚提交，等待执行）
     * RUNNING    → 执行中（代码正在运行）
     * COMPLETED  → 执行成功（正常结束，有输出结果）
     * FAILED     → 执行失败（代码报错或异常）
     * TIMEOUT    → 执行超时（超过限制时间，强制终止）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    /**
     * 执行耗时（毫秒）
     *
     * 用途：
     * 1. 显示给用户：执行耗时 125ms
     * 2. 性能统计：平均执行时间、最慢的代码等
     * 3. 限流依据：执行时间过长的用户降低优先级
     */
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    /**
     * 内存使用（MB）
     *
     * 记录执行时占用的内存峰值
     * 用途：
     * 1. 显示给用户
     * 2. 资源控制：超过 512MB 强制终止
     */
    @Column(name = "memory_used_mb")
    private Integer memoryUsedMb;

    /**
     * Docker 容器 ID
     *
     * 记录在哪个容器中执行的代码
     * 用途：
     * 1. 排查问题：可以查看容器日志
     * 2. 资源管理：统计容器使用情况
     */
    @Column(name = "container_id", length = 100)
    private String containerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 执行状态枚举
     */
    public enum Status {
        PENDING,    // 等待执行
        RUNNING,    // 正在执行
        COMPLETED,  // 执行成功
        FAILED,     // 执行失败
        TIMEOUT     // 执行超时
    }
}
