package com.example.codesandbox.user.repository;

import com.example.codesandbox.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限名称查找权限
     * 用途：权限检查时使用
     */
    Optional<Permission> findByPermissionName(String permissionName);

    /**
     * 根据资源类型查找所有权限
     * 例如：查找所有与 "code" 相关的权限
     */
    List<Permission> findByResource(String resource);

    /**
     * 根据资源和动作查找权限
     * 例如：resource="code", action="read"
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * 检查权限名称是否已存在
     */
    boolean existsByPermissionName(String permissionName);
}
