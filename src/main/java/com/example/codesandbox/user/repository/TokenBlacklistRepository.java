package com.example.codesandbox.user.repository;

import com.example.codesandbox.user.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;


@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    /**
     * 根据Token查找黑名单记录
     * 用途：验证Token是否在黑名单中
     */
    Optional<TokenBlacklist> findByToken(String token);

    /**
     * 检查Token是否在黑名单中
     * 用途：快速检查（不需要加载整个对象）
     */
    boolean existsByToken(String token);

    /**
     * 删除已过期的Token记录
     * 用途：定时清理过期的黑名单记录，节省存储空间
     */
    @Modifying
    @Query("DELETE FROM TokenBlacklist tb WHERE tb.expirationTime < :now")
    int deleteExpiredTokens(LocalDateTime now);

    /**
     * 查找某个用户的所有被撤销的Token
     * 用途：管理员查看用户Token状态
     */
    @Query("SELECT tb FROM TokenBlacklist tb WHERE tb.user.id = :userId")
    List<TokenBlacklist> findByUserId(Long userId);
}