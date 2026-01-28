package com.example.codesandbox.security.exception;

import lombok.Getter;

/**
 * 权限不足异常
 * 当用户没有执行某个操作的权限时抛出
 */
@Getter
public class PermissionDeniedException extends RuntimeException {

    // ========== 字段 ==========

    /**
     * 需要的权限（格式：resource:action，如 "code:delete"）
     */
    private String requiredPermission;

    /**
     * 当前用户名
     */
    private String username;

    /**
     * 资源类型（如：code, user, review）
     */
    private String resource;

    /**
     * 操作类型（如：read, write, delete）
     */
    private String action;

    // ========== 构造方法 ==========

    /**
     * 构造方法1: 只传错误消息
     *
     * 使用场景：
     * throw new PermissionDeniedException("权限不足");
     */
    public PermissionDeniedException(String message) {
        // TODO: 调用父类构造方法
        super(message);
    }

    /**
     * 构造方法2: 传消息和详细权限信息
     *
     * 使用场景：
     * throw new PermissionDeniedException("权限不足", "code", "delete");
     */
    public PermissionDeniedException(String message, String resource, String action) {
        // TODO: 调用父类构造方法
        // TODO: 设置resource
        // TODO: 设置action
        // TODO: 拼接requiredPermission（格式：resource:action）
        super(message);
        this.resource=resource;
        this.action=action;
        this.requiredPermission=resource+":"+action;
    }

    /**
     * 构造方法3: 传消息、权限信息和用户名
     *
     * 使用场景：
     * throw new PermissionDeniedException("权限不足", "code", "delete", "张三");
     */
    public PermissionDeniedException(String message, String resource, String action, String username) {
        // TODO: 调用父类构造方法
        // TODO: 设置resource
        // TODO: 设置action
        // TODO: 设置username
        // TODO: 拼接requiredPermission
        super(message);
        this.resource=resource;
        this.action=action;
        this.username=username;
        this.requiredPermission=resource+":"+action+":"+username;
    }
}
