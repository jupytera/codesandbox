package com.example.codesandbox.user.repository;

import com.example.codesandbox.user.entity.LoginHistory;
import com.example.codesandbox.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    /**
     * 查找某个用户的登录历史（按时间倒序）
     * 用途：用户查看自己的登录记录
     */
    List<LoginHistory> findByUserOrderByLoginTimeDesc(User user);

    /**
     * 查找某个用户的最近N条登录记录
     * 用途：显示最近5次登录
     */
    List<LoginHistory> findTop5ByUserOrderByLoginTimeDesc(User user);

    /**
     * 查找某个时间段内的登录记录
     * 用途：统计某段时间的登录情况
     */
    List<LoginHistory> findByLoginTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找失败的登录尝试
     * 用途：安全监控，发现异常登录
     */
    List<LoginHistory> findByUserAndLoginSuccessFalse(User user);

    /**
     * 统计某个IP地址的登录次数
     * 用途：检测暴力破解攻击
     */
    @Query("SELECT COUNT(lh) FROM LoginHistory lh WHERE lh.ipAddress = :ipAddress AND lh.loginTime > :since")
    long countByIpAddressSince(String ipAddress, LocalDateTime since);
}