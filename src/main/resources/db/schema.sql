-- ============================================================================
-- CodeSandbox Database Schema
-- Database: codesandbox
-- Created: 2025-11-09
-- ============================================================================

-- Drop existing tables (optional - use with caution)
-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE IF EXISTS token_blacklist;
-- DROP TABLE IF EXISTS login_history;
-- DROP TABLE IF EXISTS audit_logs;
-- DROP TABLE IF EXISTS execution_tasks;
-- DROP TABLE IF EXISTS code_shares;
-- DROP TABLE IF EXISTS review_comments;
-- DROP TABLE IF EXISTS code_reviews;
-- DROP TABLE IF EXISTS code_versions;
-- DROP TABLE IF EXISTS code_snippets;
-- DROP TABLE IF EXISTS user_roles;
-- DROP TABLE IF EXISTS role_permissions;
-- DROP TABLE IF EXISTS permissions;
-- DROP TABLE IF EXISTS roles;
-- DROP TABLE IF EXISTS users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- Users Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  email VARCHAR(100) UNIQUE NOT NULL COMMENT '电子邮件',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  full_name VARCHAR(100) COMMENT '全名',
  avatar_url VARCHAR(500) COMMENT '头像URL',
  email_verified BOOLEAN DEFAULT FALSE COMMENT '邮箱是否已验证',
  email_verification_token VARCHAR(255) UNIQUE COMMENT '邮箱验证令牌',
  last_login TIMESTAMP COMMENT '最后登录时间',
  account_status ENUM('ACTIVE', 'SUSPENDED', 'DELETED') DEFAULT 'ACTIVE' COMMENT '账户状态',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_email (email),
  INDEX idx_account_status (account_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================================
-- Roles Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
  description VARCHAR(255) COMMENT '角色描述',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================================================
-- Permissions Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS permissions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  permission_name VARCHAR(100) UNIQUE NOT NULL COMMENT '权限名称',
  description VARCHAR(255) COMMENT '权限描述',
  resource VARCHAR(100) COMMENT '资源类型',
  action VARCHAR(50) COMMENT '操作类型',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_permission_name (permission_name),
  INDEX idx_resource_action (resource, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ============================================================================
-- Role Permissions Junction Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS role_permissions (
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
  FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================================================
-- User Roles Junction Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ============================================================================
-- Code Snippets Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS code_snippets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '代码片段ID',
  owner_id BIGINT NOT NULL COMMENT '所有者ID',
  title VARCHAR(200) NOT NULL COMMENT '代码片段标题',
  description TEXT COMMENT '代码片段描述',
  language VARCHAR(50) NOT NULL COMMENT '编程语言',
  visibility ENUM('PRIVATE', 'SHARED', 'PUBLIC') DEFAULT 'PRIVATE' COMMENT '可见性',
  tags VARCHAR(500) COMMENT '标签（逗号分隔）',
  view_count INT DEFAULT 0 COMMENT '浏览次数',
  fork_count INT DEFAULT 0 COMMENT '分叉次数',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_owner_id (owner_id),
  INDEX idx_language (language),
  INDEX idx_visibility (visibility),
  INDEX idx_created_at (created_at),
  FULLTEXT INDEX ft_title_description (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码片段表';

-- ============================================================================
-- Code Versions Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS code_versions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '版本ID',
  snippet_id BIGINT NOT NULL COMMENT '代码片段ID',
  version_number INT NOT NULL COMMENT '版本号',
  content LONGTEXT NOT NULL COMMENT '代码内容',
  delta_content LONGTEXT COMMENT '增量编码内容（用于存储差异）',
  author_id BIGINT NOT NULL COMMENT '作者ID',
  commit_message VARCHAR(500) COMMENT '提交信息',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (snippet_id) REFERENCES code_snippets(id) ON DELETE CASCADE,
  FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE RESTRICT,
  UNIQUE KEY uq_snippet_version (snippet_id, version_number),
  INDEX idx_snippet_id (snippet_id),
  INDEX idx_author_id (author_id),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码版本表';

-- ============================================================================
-- Code Reviews Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS code_reviews (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评审ID',
  snippet_id BIGINT NOT NULL COMMENT '代码片段ID',
  reviewer_id BIGINT NOT NULL COMMENT '评审者ID',
  status ENUM('PENDING', 'APPROVED', 'CHANGES_REQUESTED', 'REJECTED') DEFAULT 'PENDING' COMMENT '评审状态',
  review_content TEXT COMMENT '评审内容',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (snippet_id) REFERENCES code_snippets(id) ON DELETE CASCADE,
  FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE RESTRICT,
  INDEX idx_snippet_id (snippet_id),
  INDEX idx_reviewer_id (reviewer_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码评审表';

-- ============================================================================
-- Review Comments Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS review_comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  review_id BIGINT NOT NULL COMMENT '评审ID',
  author_id BIGINT NOT NULL COMMENT '评论作者ID',
  content TEXT NOT NULL COMMENT '评论内容',
  line_number INT COMMENT '代码行号',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (review_id) REFERENCES code_reviews(id) ON DELETE CASCADE,
  FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE RESTRICT,
  INDEX idx_review_id (review_id),
  INDEX idx_author_id (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审评论表';

-- ============================================================================
-- Code Shares Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS code_shares (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分享ID',
  snippet_id BIGINT NOT NULL COMMENT '代码片段ID',
  shared_by BIGINT NOT NULL COMMENT '分享者ID',
  shared_with BIGINT COMMENT '分享给用户ID（null表示公开分享）',
  share_token VARCHAR(100) UNIQUE COMMENT '分享令牌',
  expiration_date TIMESTAMP COMMENT '过期时间',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (snippet_id) REFERENCES code_snippets(id) ON DELETE CASCADE,
  FOREIGN KEY (shared_by) REFERENCES users(id) ON DELETE RESTRICT,
  FOREIGN KEY (shared_with) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_snippet_id (snippet_id),
  INDEX idx_share_token (share_token),
  INDEX idx_shared_with (shared_with)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码分享表';

-- ============================================================================
-- Execution Tasks Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS execution_tasks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '执行任务ID',
  snippet_id BIGINT COMMENT '代码片段ID',
  executor_id BIGINT NOT NULL COMMENT '执行者ID',
  language VARCHAR(50) NOT NULL COMMENT '编程语言',
  code_content LONGTEXT NOT NULL COMMENT '执行的代码',
  code_hash VARCHAR(64) COMMENT '代码哈希值（SHA256），用于缓存同一段代码的执行结果',
  input_data LONGTEXT COMMENT '输入数据',
  output_data LONGTEXT COMMENT '输出结果',
  error_message LONGTEXT COMMENT '错误信息',
  status ENUM('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'TIMEOUT') DEFAULT 'PENDING' COMMENT '执行状态',
  execution_time_ms INT COMMENT '执行耗时（毫秒）',
  memory_used_mb INT COMMENT '内存使用（MB）',
  container_id VARCHAR(100) COMMENT '容器ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  completed_at TIMESTAMP COMMENT '完成时间',
  FOREIGN KEY (snippet_id) REFERENCES code_snippets(id) ON DELETE SET NULL,
  FOREIGN KEY (executor_id) REFERENCES users(id) ON DELETE RESTRICT,
  INDEX idx_snippet_id (snippet_id),
  INDEX idx_executor_id (executor_id),
  INDEX idx_code_hash (code_hash),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码执行任务表';

-- ============================================================================
-- Audit Logs Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审计日志ID',
  user_id BIGINT COMMENT '用户ID',
  action VARCHAR(100) NOT NULL COMMENT '操作类型',
  resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
  resource_id BIGINT COMMENT '资源ID',
  old_value LONGTEXT COMMENT '旧值',
  new_value LONGTEXT COMMENT '新值',
  ip_address VARCHAR(45) COMMENT 'IP地址',
  user_agent VARCHAR(500) COMMENT '用户代理',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_action (action),
  INDEX idx_resource_type (resource_type),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- ============================================================================
-- Login History Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS login_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '登录记录ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  ip_address VARCHAR(45) COMMENT 'IP地址',
  user_agent VARCHAR(500) COMMENT '用户代理',
  login_success BOOLEAN DEFAULT TRUE COMMENT '登录是否成功',
  failure_reason VARCHAR(255) COMMENT '失败原因',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_login_time (login_time),
  INDEX idx_ip_address (ip_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录历史表';

-- ============================================================================
-- Token Blacklist Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS token_blacklist (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '黑名单ID',
  token VARCHAR(500) NOT NULL UNIQUE COMMENT 'JWT令牌',
  user_id BIGINT COMMENT '用户ID',
  revoked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '撤销时间',
  expiration_time TIMESTAMP NOT NULL COMMENT '令牌过期时间',
  reason VARCHAR(255) COMMENT '撤销原因',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_expiration_time (expiration_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='令牌黑名单表';

-- ============================================================================
-- Insert Default Roles
-- ============================================================================
INSERT IGNORE INTO roles (id, role_name, description) VALUES
(1, 'ADMIN', 'Administrator with full access'),
(2, 'USER', 'Regular user with basic permissions'),
(3, 'REVIEWER', 'Code reviewer role'),
(4, 'MODERATOR', 'Content moderator role');

-- ============================================================================
-- Insert Default Permissions
-- ============================================================================
INSERT IGNORE INTO permissions (id, permission_name, description, resource, action) VALUES
-- User Management
(1, 'user_view', 'View user details', 'user', 'view'),
(2, 'user_create', 'Create new user', 'user', 'create'),
(3, 'user_update', 'Update user information', 'user', 'update'),
(4, 'user_delete', 'Delete user account', 'user', 'delete'),

-- Code Snippet Management
(5, 'snippet_create', 'Create code snippet', 'snippet', 'create'),
(6, 'snippet_view', 'View code snippet', 'snippet', 'view'),
(7, 'snippet_update', 'Update code snippet', 'snippet', 'update'),
(8, 'snippet_delete', 'Delete code snippet', 'snippet', 'delete'),
(9, 'snippet_share', 'Share code snippet', 'snippet', 'share'),

-- Code Review
(10, 'review_create', 'Create code review', 'review', 'create'),
(11, 'review_approve', 'Approve code review', 'review', 'approve'),
(12, 'review_reject', 'Reject code review', 'review', 'reject'),

-- Code Execution
(13, 'execute_code', 'Execute code snippets', 'execution', 'execute'),
(14, 'view_execution', 'View execution results', 'execution', 'view'),

-- Admin Operations
(15, 'admin_panel', 'Access admin panel', 'admin', 'access'),
(16, 'audit_logs', 'View audit logs', 'audit', 'view'),
(17, 'system_config', 'Modify system configuration', 'system', 'config');

-- ============================================================================
-- Assign Permissions to Roles
-- ============================================================================
-- ADMIN role gets all permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- USER role gets basic permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 13), (2, 14), (2, 1);

-- REVIEWER role gets review permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(3, 6), (3, 10), (3, 11), (3, 12), (3, 14), (3, 1);

-- MODERATOR role gets moderation permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(4, 6), (4, 16), (4, 1);

-- ============================================================================
-- End of Schema
-- ============================================================================
